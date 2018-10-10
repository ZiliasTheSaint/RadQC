package other;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import danfulea.db.DatabaseAgent;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;
import radQC.RadQCFrame;

/**
 * Class for generic "other" QC tests
 * 
 * @author Dan Fulea, 03 May 2015
 */
@SuppressWarnings("serial")
public class OtherFrame extends JFrame implements ActionListener, ItemListener{

	private static final Dimension PREFERRED_SIZE = new Dimension(800, 720);
	private final Dimension tableDimension = new Dimension(700, 200);
	private static final Dimension textAreaDimension = new Dimension(700, 300);	
	private static final String BASE_RESOURCE_CLASS = "other.resources.OtherFrameResources";
	protected ResourceBundle resources;
	private RadQCFrame mf;
	
	protected String measurementDate_toSave="";
	protected String mainDB = "";
	protected String otherTable = "";
	protected int IDLINK=0;
	
	private static final String DELETE_COMMAND = "DELETE";
	private static final String VIEW_COMMAND = "VIEW";
	private static final String SAVE_COMMAND = "SAVE";
	private static final String VIEWNOTES_COMMAND = "VIEWNOTES";
	//private String command = null;
	
	protected JTextArea textArea = new JTextArea();
	//protected AdvancedSelectPanel asp = null;
	protected JPanel suportSp = new JPanel(new BorderLayout());
	private int IDDEVICE = 0;
	protected JLabel iddevicelabel=null;
	private int maxUniqueID=0;
	
	private Connection radqcdbcon = null;
	private DatabaseAgentSupport dbagent;
	private JComboBox<String> orderbyCb;
	private final Dimension sizeOrderCb = new Dimension(200, 21);
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public OtherFrame(RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("OtherFrame.NAME"));
		//DBConnection.startDerby();//just in case is closed
		//===============
		mainDB=mf.radqcDB;
		otherTable=mf.otherTable;
		
    	measurementDate_toSave=mf.measurementDate;
    	IDLINK=mf.IDLINK;
    	//=============================================
    	DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		dbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID",
				otherTable);
		dbagent.setHasValidAIColumn(false);
		//-------now dummy initialization of agents just to create combobox, label and tables!!!
		dbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
		dbagent.init();	
    	//============
    	performQueryDB();
    	createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);
		//===========================
		//populateFromDb();
		//===========================
		setVisible(true);
		mf.setEnabled(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// force attemptExit to be called always!
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();

			}
		});
	}
	
	/**
	 * Exit method
	 */
	private void attemptExit() {
		try{
			if (radqcdbcon != null)
				radqcdbcon.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		mf.setEnabled(true);
		dispose();
	}

	/**
	 * Setting up the frame size.
	 */
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}
	
	/**
	 * GUI creation.
	 */
	private void createGUI() {
		JPanel content = new JPanel(new BorderLayout());
		                
		JPanel mainPanel = createMainPanel();

		content.add(mainPanel, BorderLayout.CENTER);
		//content.add(statusBar, BorderLayout.PAGE_END);

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
		
		if (mf.EXAMINATION_ID==0){
			textArea.append(resources.getString("textArea.rad.title")+"\n");
			textArea.append(resources.getString("textArea.rad")+"\n");
		} else if (mf.EXAMINATION_ID==1){
			textArea.append(resources.getString("textArea.mammo.title")+"\n");
			textArea.append(resources.getString("textArea.mammo")+"\n");
		} else if (mf.EXAMINATION_ID==2){
			textArea.append(resources.getString("textArea.fluoro.title")+"\n");
			textArea.append(resources.getString("textArea.fluoro")+"\n");
		} else if (mf.EXAMINATION_ID==3){
			textArea.append(resources.getString("textArea.ct.title")+"\n");
			textArea.append(resources.getString("textArea.ct")+"\n");
		}
	}
	
	/**
	 * Create main panel
	 * @return the result
	 */
	private JPanel createMainPanel() {
		JLabel jlabel=new JLabel();
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		JButton button =null;
		Character mnemonic = null;	
		
		orderbyCb = dbagent.getOrderByComboBox();
		orderbyCb.setMaximumRowCount(5);
		orderbyCb.setPreferredSize(sizeOrderCb);
		orderbyCb.addItemListener(this);
		JPanel orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		JLabel label = new JLabel(resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(orderbyCb);
		orderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(dbagent.getRecordsLabel());
		
		JPanel p00P=new JPanel();
		p00P.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
		jlabel = new JLabel(resources.getString("info.label"));
		p00P.add(jlabel);
        p00P.setBackground(RadQCFrame.bkgColor);
        
        textArea.setCaretPosition(0);
		textArea.setEditable(true);//we want write in!
		//textArea.setText("");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JPanel textAreaP = new JPanel(new BorderLayout());
		textAreaP.setPreferredSize(textAreaDimension);
		textAreaP.add(new JScrollPane(textArea), BorderLayout.CENTER);
		textAreaP.setBackground(RadQCFrame.bkgColor);
		
		JPanel p7P=new JPanel();
		p7P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		
		buttonName = resources.getString("save.saveB");
		buttonToolTip = resources.getString("save.saveB.toolTip");
		buttonIconName = resources.getString("img.save.database");
		button = FrameUtilities.makeButton(buttonIconName, SAVE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("save.saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7P.add(button);
		
		buttonName = resources.getString("save.deleteB");
		buttonToolTip = resources.getString("save.deleteB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("save.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7P.add(button);
		
		buttonName = resources.getString("save.viewB");
		buttonToolTip =resources.getString("save.viewB.toolTip");
		buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEW_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("save.viewB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7P.add(button);
		
		buttonName = resources.getString("save.viewBNotes");
		buttonToolTip =resources.getString("save.viewBNotes.toolTip");
		buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEWNOTES_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("save.viewBNotes.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7P.add(button);
		p7P.setBackground(RadQCFrame.bkgColor);
		
		JPanel centerP = new JPanel();
		BoxLayout blcenter = new BoxLayout(centerP, BoxLayout.Y_AXIS);
		centerP.setLayout(blcenter);
		centerP.add(p00P);
		centerP.add(textAreaP);
		centerP.setBackground(RadQCFrame.bkgColor);
		
		suportSp.setPreferredSize(tableDimension);
		//=-------------------DB panel and records
		JPanel p4P=new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		jlabel = new JLabel(resources.getString("records.label"));
		jlabel.setForeground(RadQCFrame.foreColor);
		p4P.add(jlabel);
		String str=Convertor.intToString(IDDEVICE);
		iddevicelabel = new JLabel(str);
		iddevicelabel.setForeground(RadQCFrame.foreColor);
		p4P.add(iddevicelabel);
		p4P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p5P = new JPanel();
		BoxLayout blp5P = new BoxLayout(p5P, BoxLayout.Y_AXIS);
		p5P.setLayout(blp5P);
		p5P.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("records.border"),
				RadQCFrame.foreColor));
		p5P.add(suportSp);
		p5P.add(orderP);//////////////////////////////p5P.add(p4P);
		p5P.setBackground(RadQCFrame.bkgColor);
		
		JPanel southP = new JPanel();
		BoxLayout blsouth = new BoxLayout(southP, BoxLayout.Y_AXIS);
		southP.setLayout(blsouth);
		southP.add(p5P);
		southP.add(p7P);
		southP.setBackground(RadQCFrame.bkgColor);
		//========================
		
		JPanel mainP=new JPanel(new BorderLayout());
		mainP.add(centerP, BorderLayout.CENTER);
		mainP.add(southP, BorderLayout.SOUTH);
		mainP.setBackground(RadQCFrame.bkgColor);
		return mainP;
	}

	/**
	 * Initialize database
	 */
	private void performQueryDB(){
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mainDB;
		//opens = opens + file_sep + dbName;
		
		try {
			//int dummy=1;
			String s = "select * from " + otherTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagent.select(s);

			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);
			JTable mainTable = dbagent.getMainTable();
			JScrollPane scrollPane = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp.add(scrollPane, BorderLayout.CENTER);
			//JTable mainTable = asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			IDDEVICE = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueID=Convertor.stringToInt(maxUniqueIDs);								
			}	
			
			//if (con1 != null)
				//con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String command = arg0.getActionCommand();
		if (command.equals(SAVE_COMMAND)) {
			save();
		} else if (command.equals(VIEW_COMMAND)) {
			view();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
		} else if (command.equals(VIEWNOTES_COMMAND)) {
			viewNotes();
		}		
	}
	
	/**
	 * JCombobox actions are set here
	 */
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == orderbyCb) {
			sort();
		} 
	}
	
	/**
	 * Sorts data from "other" table
	 */
	private void sort() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCb.getSelectedItem();
		dbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagent.performSelection(orderbyS);
	}
	
	/**
	 * View "encrypted" data (CLOB data) from a record
	 */
	private void viewNotes(){
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mainDB;
		//opens = opens + file_sep + dbName;	
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = dbagent.getMainTable();//asp.getTab();

			int selID = 0;// Unique
			int selID2 = 0;// IDLINK
			int selRow = aspTable.getSelectedRow();
			if (selRow != -1) {
				selID = (Integer) aspTable.getValueAt(selRow, 0);
				selID2 = (Integer) aspTable.getValueAt(selRow, 1);
			} else {
				//if (con1 != null)
					//con1.close();
				
				return;// nothing to delete
			}
			String s="select * from " + otherTable+" where IDLINK = "+
			selID2 +" and Unique_ID = "+selID;			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_FORWARD_ONLY,//con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet res = stmt.executeQuery(s);//"SELECT * FROM " + otherTable);
			res.next();
			//measurementDate_other=resultSet.getString(3);
			String noteS_other="";
			Reader reader = res.getCharacterStream(4);//4 column			
			try{					
				int i;               
				do {
				    i = reader.read();
				    char c = (char) i;
				    
				    if (i != -1)
				    	noteS_other=noteS_other+c;

				} while (i != -1);   
				
			}catch(Exception e){
				e.printStackTrace();
				return;
			}			
			
			textArea.selectAll();
			textArea.replaceSelection("");
			textArea.setText(noteS_other);
			
			if (res != null)
				res.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			
			//b=false;//if no data catch sql error
		}	
	}
	
	/**
	 * Save test results in database
	 */
	private void save(){
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mainDB;
		//opens = opens + file_sep + dbName;
		
		String notesStr = textArea.getText();
		StringReader reader = new StringReader(notesStr);
		int length=notesStr.length();
		try {			
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
						
			//int n = mf.cvkvacd.length;//mf.kvd.length;//cpsV.size();
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ otherTable + " values " + "(?, ?, ?, ?)");
			//for (int i = 0; i < n; i++) {				
				int id = maxUniqueID + 1;//Unique ID
				psInsert.setString(1, Convertor.intToString(id));
				psInsert.setString(2, Convertor.intToString(mf.IDLINK));
				psInsert.setString(3, measurementDate_toSave);
				//psInsert.setString(4, Convertor.doubleToString(mf.output));
				psInsert.setCharacterStream(4, reader, length);//CLOB data here
				psInsert.executeUpdate();				
			//}
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
						
			selectTable();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Select data from "other" table
	 */
	private void selectTable(){
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + otherTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			dbagent.select(s);
			
			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = dbagent.getMainTable();//asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			IDDEVICE = mainTable.getRowCount();
						
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueID=Convertor.stringToInt(maxUniqueIDs);
								
			} else {
				maxUniqueID=0;//reset counter
			}
						
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(IDDEVICE);
			iddevicelabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
		
	}

	/**
	 * Delete an entry from "other" table
	 */
	private void delete(){
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = dbagent.getMainTable();//asp.getTab();

			int selID = 0;// Unique
			int selID2 = 0;// IDLINK
			int selRow = aspTable.getSelectedRow();
			if (selRow != -1) {
				selID = (Integer) aspTable.getValueAt(selRow, 0);
				selID2 = (Integer) aspTable.getValueAt(selRow, 1);
			} else {
				//if (con1 != null)
					//con1.close();
				
				return;// nothing to delete
			}
			//System.out.println("Unique + "+selID+"; IDLINK= "+selID2);
			
			Statement s = radqcdbcon.createStatement(ResultSet.TYPE_FORWARD_ONLY,//con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet res = s.executeQuery("SELECT * FROM " + otherTable);//deviceTable);
			//PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("Unique_ID");//("ID");
				int id2 = res.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res.deleteRow();
				} else if (id > selID && id2==selID2) {
										
				}
			}
						
			selectTable();
			
			if (res != null)
				res.close();
			if (s != null)
				s.close();
						 
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * View all records in database
	 */
	private void view(){
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + otherTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagent.select(s);

			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = dbagent.getMainTable();//asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			IDDEVICE = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				//Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//maxUniqueID=Convertor.stringToInt(maxUniqueIDs);//not here in view all!!!								
			} //else
				//maxUniqueID=0;//reset counter
						
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(IDDEVICE);
			iddevicelabel.setText(str);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection	
	}
}
