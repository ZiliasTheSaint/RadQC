package dosimetry;

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
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;

/**
 * Database for dosimetry in mammography 
 * 
 * @author Dan Fulea, 07 May 2015
 */
@SuppressWarnings("serial")
public class SaveViewDBFrameMammo extends JFrame implements ActionListener, ItemListener{
	private final Dimension PREFERRED_SIZE = new Dimension(950, 700);
	private final Dimension tableDimension = new Dimension(800, 200);
	//public static int ITAB = 0;
	private DosimetryFrameMammo mf;
	private static final String SAVE_COMMAND = "SAVE";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String VIEW_COMMAND = "VIEW";

	private JPanel mainPanel;
	//private AdvancedSelectPanel asp = null;
	private JPanel suportSp = new JPanel(new BorderLayout());
		
	private int nOutput=0;
	private JLabel nOutputLabel=new JLabel();
		
	private int maxUniqueIdOutput=0;
	
	private Connection radqcdbcon = null;
	private DatabaseAgentSupport dbagent;
	private JComboBox<String> orderbyCb;
	private final Dimension sizeOrderCb = new Dimension(200, 21);
	
	/**
	 * Constructor
	 * @param mf the DosimetryFrameMammo object
	 */
	public SaveViewDBFrameMammo(DosimetryFrameMammo mf){
		this.mf=mf;
		this.setTitle(mf.resources.getString("SaveViewDBFrame.NAME"));
		//========================
		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
	   	String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mf.mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		dbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID",
					mf.dosimetryTable);
		dbagent.setHasValidAIColumn(false);
		//-------now dummy initialization of agents just to create combobox, label and tables!!!
		dbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
		dbagent.init();		
		//================================
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
		mainPanel = createMainPanel();
		content.add(mainPanel);
		// Create the statusbar.
		//JToolBar statusBar = new JToolBar();
		//statusBar.setFloatable(false);
		//initStatusBar(statusBar);
		//content.add(statusBar, BorderLayout.PAGE_END);

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();		
		
		//mainPanel.setSelectedIndex(ITAB);
	}
	
	/**
	 * Initialize database
	 */
	private void performQueryDB(){
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {
						
			//Rep
			String s = "select * from " + mf.dosimetryTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

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

			ListSelectionModel rowSMt = mainTable.getSelectionModel();
			rowSMt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//rowSMt.addListSelectionListener(new ListSelectionListener() {
			//	public void valueChanged(ListSelectionEvent e) {
			//		if (e.getValueIsAdjusting())
			//			return; // Don't want to handle intermediate selections

			//		updateDetailTable();//fired each time a row is selected in main table
			//	}
			//});
			
			nOutput = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutput=Convertor.stringToInt(maxUniqueIDs);								
			} 

			//detail
			//s = "select * from " + mf.aecRepeatabilityTableDetail + 
			//" where IDLINK = "+ mf.IDLINK + 
			//" and Unique_id = "+maxUniqueIdOutputRep+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			//aspRepDetail = new AdvancedSelectPanel();
			//suportSpRepDetail.add(aspRepDetail, BorderLayout.CENTER);
			
			//mainTable = aspRepDetail.getTab();
			//nOutputRepDetail = mainTable.getRowCount();
			//-----------------------
						
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
		
		orderbyCb = dbagent.getOrderByComboBox();
		orderbyCb.setMaximumRowCount(5);
		orderbyCb.setPreferredSize(sizeOrderCb);
		orderbyCb.addItemListener(this);
		JPanel orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(orderbyCb);
		orderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(dbagent.getRecordsLabel());		
		
		suportSp.setPreferredSize(tableDimension);
		
		JPanel p4P=new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4P.add(label);
		String str=Convertor.intToString(nOutput);
		nOutputLabel.setText(str);// = new JLabel(str);
		nOutputLabel.setForeground(RadQCFrame.foreColor);
		p4P.add(nOutputLabel);
		p4P.setBackground(RadQCFrame.bkgColor);
				
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
		
		JPanel outP = new JPanel();
		BoxLayout blkvRP = new BoxLayout(outP, BoxLayout.Y_AXIS);
		outP.setLayout(blkvRP);
		//outP.setBorder(FrameUtilities.getGroupBoxBorder(
			//	mf.resources.getString("output.border"),
				//RadQCFrame.foreColor));
		outP.add(suportSp);
		outP.add(orderP);//////////////////////outP.add(p4P);
		outP.add(p7P);
		outP.setBackground(RadQCFrame.bkgColor);
		
		return outP;
		
		/*JPanel outRall = new JPanel();
		BoxLayout bloutRall = new BoxLayout(outRall, BoxLayout.Y_AXIS);
		outRall.setLayout(bloutRall);
		outRall.add(outPr);
		outRall.add(kvRPdt);
		outRall.setBackground(RadQCFrame.bkgColor);
				
		JTabbedPane jtab = new JTabbedPane();
		//jtab.add(outP,  mf.resources.getString("output.tab.title"));
		jtab.add(outRall,  mf.resources.getString("reprod.tab.title"));
		jtab.add(outLall,  mf.resources.getString("linearity.tab.title"));
		return jtab;*/
	}
	
	/**
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String command = e.getActionCommand();
		if (command.equals(SAVE_COMMAND)) {
			save();
		} else if (command.equals(VIEW_COMMAND)) {
			view();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
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
	 * Sorts data from the table
	 */
	private void sort() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCb.getSelectedItem();
		dbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagent.performSelection(orderbyS);
	}
	
	/**
	 * Save results in database
	 */
	private void save(){
		if (!mf.isOkToSave){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.error.message"),
					mf.resources.getString("nosave.error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}		
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		//String resultS="";
		
		try {			
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			//-------------------------
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ mf.dosimetryTable + " values " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			int id = maxUniqueIdOutput + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, Convertor.intToString(mf.IDLINK));
			psInsert.setString(3, mf.measurementDate_toSave);			
			psInsert.setString(4, mf.ESAK_toSave);
			psInsert.setString(5, mf.AGD_toSave);
			psInsert.setString(6, mf.unc_toSave);
			psInsert.setString(7, mf.DRL_toSave);	//AGD LIMIT
			psInsert.setString(8, mf.resultTest_toSave);		
			psInsert.setString(9, mf.effectiveDose);//DOSE
			psInsert.setString(10, mf.effectiveDoseUnit);
			psInsert.setString(11, mf.risk);
			psInsert.setString(12, mf.KAP_toSave);
			psInsert.setString(13, mf.KAIR_toSave);
			psInsert.setString(14, mf.FSD_toSave);
			psInsert.setString(15, mf.breastDiameter_toSave);
			psInsert.setString(16, mf.breastThickness_toSave);			
			psInsert.setString(17, mf.phantomAge_toSave);
			psInsert.setString(18, mf.kv_toSave);
			psInsert.setString(19, mf.filtration_toSave);
			psInsert.setString(20, mf.anodeMaterial_toSave);
			psInsert.setString(21, mf.anodeAngle_toSave);
			psInsert.setString(22, mf.ripple_toSave);
			psInsert.executeUpdate();
			//------------------------
									
			selectTable();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Select data from database
	 */
	private void selectTable(){
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.dosimetryTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

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
						
			nOutput = mainTable.getRowCount();
						
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutput=Convertor.stringToInt(maxUniqueIDs);
								
			} else {
				maxUniqueIdOutput=0;//reset counter
			}
			
			//detail
			
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
		
			String str=Convertor.intToString(nOutput);
			nOutputLabel.setText(str);
			//str=Convertor.intToString(nOutputLinDetail);
			//nOutputLinDetailLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
	}
	
	/**
	 * View all data from database
	 */
	private void view(){
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.dosimetryTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagent.select(s);

			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = dbagent.getMainTable();//asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//rowSM.addListSelectionListener(new ListSelectionListener() {
			//	public void valueChanged(ListSelectionEvent e) {
			//		if (e.getValueIsAdjusting())
			//			return; // Don't want to handle intermediate selections

				//	updateDetailTableLin();//fired each time a row is selected in main table
				//}
			//});
			
			nOutput = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID				
						
			} 
			
			//detail
			//int selUID = (Integer) mainTable.getValueAt(nOutputLin-1, 0);//column 1, Unique_ID
			//int selID = (Integer) mainTable.getValueAt(nOutputLin-1, 1);//column 2, LINK_ID
			
			//s = "select * from " + mf.aecLinearityTableDetail + 
			//" where IDLINK = "+ selID + 
			//" and Unique_ID = "+ selUID +
			//" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation			
			//suportSpLinDetail.remove(aspLinDetail);//remove first
			//aspLinDetail = new AdvancedSelectPanel();
			//suportSpLinDetail.add(aspLinDetail, BorderLayout.CENTER);
			
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(nOutput);
			nOutputLabel.setText(str);
			
			//mainTable = aspLinDetail.getTab();
			//nOutputLinDetail = mainTable.getRowCount();
			//str=Convertor.intToString(nOutputLinDetail);
			//nOutputLinDetailLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection
	}

	/**
	 * Delete an entry from database
	 */
	private void delete(){
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.dosimetryTable);//deviceTable);
			//PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("Unique_ID");//("ID");
				int id2 = res.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res.deleteRow();
				} else if (id > selID && id2==selID2) {
					
				}
			}
			
			//now the detail
			/*ResultSet res1 = s.executeQuery("SELECT * FROM " + mf.aecLinearityTableDetail);//deviceTable);
			while (res1.next()) {
				int id = res1.getInt("Unique_ID");//("ID");
				int id2 = res1.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res1.deleteRow();
				} 
			}*/
			//===============================================
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
}
