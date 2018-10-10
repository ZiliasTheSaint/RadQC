package xrtf_mAs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import radQC.RadQCFrame;
import danfulea.db.DatabaseAgent;
//import javax.swing.JToolBar;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;

/**
 * View/Save in database for HVL/filtration QC tests <br>
 * 
 * @author Dan Fulea, 27 Apr. 2015
 */
@SuppressWarnings("serial")
public class SaveViewDBFrame extends JFrame implements ActionListener, ItemListener{
	
	private final Dimension PREFERRED_SIZE = new Dimension(900, 400);
	private final Dimension tableDimension = new Dimension(800, 200);
	private MainFrame mf;
	private static final String SAVE_COMMAND = "SAVE";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String VIEW_COMMAND = "VIEW";
	
	private int IDDEVICE=0;
	private JLabel iddevicelabel;
	private int maxUniqueID=0;
	//private String hvlFiltrationTable="";
	//private AdvancedSelectPanel asp = null;
	private JPanel suportSp = new JPanel(new BorderLayout());
	
	private Connection radqcdbcon = null;
	private DatabaseAgentSupport hvldbagent;
	private JComboBox<String> hvlorderbyCb;
	private final Dimension sizeOrderCb = new Dimension(200, 21);
	
	/**
	 * Constructor
	 * @param mf the MainFrame object
	 */
	public SaveViewDBFrame(MainFrame mf){
		this.mf=mf;
		this.setTitle(mf.resources.getString("SaveViewDBFrame.NAME"));
		
		//==========================================
		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mf.mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		hvldbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID",
				mf.hvlFiltrationTable);
		hvldbagent.setHasValidAIColumn(false);
		//-------now dummy initialization of agents just to create combobox, label and tables!!!
		hvldbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
		hvldbagent.init();		
		//========================================
		
		performQueryDB();
		createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				mf.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);

		setVisible(true);
		mf.setEnabled(false);
		// the key to force attemptExit() method on close!!
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});
	}

	/**
	 * Setting up the frame size.
	 */
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}
	
	/**
	 * Exit method
	 */
	private void attemptExit() {
		///System.out.println("Exiting------------");
			//final RadQCFrame mff = mf;
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
	 * Create GUI
	 */
	private void createGUI() {
		JPanel content = new JPanel(new BorderLayout());
		JPanel mainPanel = createMainPanel();
		content.add(mainPanel);
		// Create the statusbar.
		//JToolBar statusBar = new JToolBar();
		//statusBar.setFloatable(false);
		//initStatusBar(statusBar);
		//content.add(statusBar, BorderLayout.PAGE_END);

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();		
	}
	
	/**
	 * Initialize database
	 */
	private void performQueryDB(){
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.hvlFiltrationTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			hvldbagent.select(s);
			
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);
			JTable mainTable = hvldbagent.getMainTable();
			JScrollPane scrollPane = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp.add(scrollPane, BorderLayout.CENTER);
			//JTable mainTable = asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			/*rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();
				}
			});*/

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
	 * Create main panel
	 * @return the result
	 */
	private JPanel createMainPanel() {
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		hvlorderbyCb = hvldbagent.getOrderByComboBox();
		hvlorderbyCb.setMaximumRowCount(5);
		hvlorderbyCb.setPreferredSize(sizeOrderCb);
		hvlorderbyCb.addItemListener(this);
		JPanel orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(hvlorderbyCb);
		orderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(hvldbagent.getRecordsLabel());
		
		suportSp.setPreferredSize(tableDimension);
		
		JPanel p4P=new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4P.add(label);
		String str=Convertor.intToString(IDDEVICE);
		iddevicelabel = new JLabel(str);
		iddevicelabel.setForeground(RadQCFrame.foreColor);
		p4P.add(iddevicelabel);
		p4P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p5P = new JPanel();
		BoxLayout blp5P = new BoxLayout(p5P, BoxLayout.Y_AXIS);
		p5P.setLayout(blp5P);
		p5P.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("records.border"),
				RadQCFrame.foreColor));
		p5P.add(suportSp);
		p5P.add(orderP);///////////////////p5P.add(p4P);
		p5P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p7P=new JPanel();
		p7P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		
		buttonName = mf.resources.getString("save.saveB");
		buttonToolTip = mf.resources.getString("save.saveB.toolTip");
		buttonIconName = mf.resources.getString("img.save.database");
		button = FrameUtilities.makeButton(buttonIconName, SAVE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7P.add(button);
		
		buttonName = mf.resources.getString("save.deleteB");
		buttonToolTip = mf.resources.getString("save.deleteB.toolTip");
		buttonIconName = mf.resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7P.add(button);
		
		buttonName = mf.resources.getString("save.viewB");
		buttonToolTip = mf.resources.getString("save.viewB.toolTip");
		buttonIconName = mf.resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEW_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.viewB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7P.add(button);
		p7P.setBackground(RadQCFrame.bkgColor);
		
		JPanel mainP = new JPanel(new BorderLayout());
		mainP.add(p5P, BorderLayout.CENTER);
		mainP.add(p7P, BorderLayout.SOUTH);
		mainP.setBackground(RadQCFrame.bkgColor);
		return mainP;
	}
	
	/**
	 * Most actions are set here
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String command = arg0.getActionCommand();
		if (command.equals(SAVE_COMMAND)) {
			save();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
		} else if (command.equals(VIEW_COMMAND)) {
			view();
		}
	}
	
	/**
	 * JCombobox actions are set here
	 */
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == hvlorderbyCb) {
			hvlsort();
		} 
	}
	
	/**
	 * Sorts data from HVL table
	 */
	private void hvlsort() {
		//System.out.println("sort");
		String orderbyS = (String) hvlorderbyCb.getSelectedItem();
		hvldbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		hvldbagent.performSelection(orderbyS);
	}
	
	/**
	 * Save HVL/filtration QC test results
	 */
	private void save(){
		//System.out.println("save fired!");
		if(mf.KV_toSave==-1.0){
			//nothing to save
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.error.message"),
					mf.resources.getString("nosave.error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			//-------------------------
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ mf.hvlFiltrationTable + " values " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			int id = maxUniqueID + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, Convertor.intToString(mf.IDLINK));
			psInsert.setString(3, mf.measurementDate_toSave);
			psInsert.setString(4, Convertor.doubleToString(mf.KV_toSave));
			psInsert.setString(5, Convertor.doubleToString(mf.HVL_toSave));
			psInsert.setString(6, Convertor.doubleToString(mf.HVL_toSave_unc));
			psInsert.setString(7, mf.HVL_resultS);
			psInsert.setString(8, Convertor.doubleToString(mf.filtration_toSave));
			psInsert.setString(9, Convertor.doubleToString(mf.filtration_toSave_unc));
			psInsert.setString(10, mf.filtration_resultS);						
			psInsert.executeUpdate();
			
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
	 * Select data from HVL table
	 */
	private void selectTable(){
		try {
			
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			String s = "select * from " + mf.hvlFiltrationTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			hvldbagent.select(s);
			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = hvldbagent.getMainTable();//asp.getTab();

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
	 * Delete an entry from HVL table
	 */
	private void delete(){
		//System.out.println("delete fired!");
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = hvldbagent.getMainTable();//asp.getTab();

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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.hvlFiltrationTable);//deviceTable);
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
			//if (psUpdate != null)
			//	psUpdate.close();
			 
			 
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * View all data from HVL table
	 */
	private void view(){
		//System.out.println("View all fired!");
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.hvlFiltrationTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			hvldbagent.select(s);

			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = hvldbagent.getMainTable();//asp.getTab();

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
