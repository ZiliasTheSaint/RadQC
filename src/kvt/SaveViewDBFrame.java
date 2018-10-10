package kvt;

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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import danfulea.db.DatabaseAgent;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;
import radQC.RadQCFrame;

/**
 * View/Save in database for KvP and exposure time QC test! <br>
 * 
 * 
 * @author Dan Fulea, 27 Apr. 2015
 */
@SuppressWarnings("serial")
public class SaveViewDBFrame extends JFrame implements ActionListener, ItemListener{
	
	private final Dimension PREFERRED_SIZE = new Dimension(900, 700);
	private final Dimension tableDimension = new Dimension(800, 200);
	private KVTFrame mf;
	private static final String SAVE_COMMAND = "SAVE";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String VIEW_COMMAND = "VIEW";
	
	private static final String SAVET_COMMAND = "SAVET";
	private static final String DELETET_COMMAND = "DELETET";
	private static final String VIEWT_COMMAND = "VIEWT";
	
	private static final String SAVEAC_COMMAND = "SAVEAC";
	private static final String DELETEAC_COMMAND = "DELETEAC";
	private static final String VIEWAC_COMMAND = "VIEWAC";
	
	private static final String SAVEACT_COMMAND = "SAVEACT";
	private static final String DELETEACT_COMMAND = "DELETEACT";
	private static final String VIEWACT_COMMAND = "VIEWACT";
	//rep
	private int IDDEVICE=0;
	private int IDDEVICE_detail=0;
	private JLabel iddevicelabel=new JLabel();
	private JLabel iddevicelabeldetail=new JLabel();
	private int maxUniqueID=0;
	
	private int IDDEVICEt=0;
	private int IDDEVICE_detailt=0;
	private JLabel iddevicelabelt=new JLabel();
	private JLabel iddevicelabeldetailt=new JLabel();
	private int maxUniqueIDt=0;
	//end rep
	private int IDDEVICEAc=0;
	private JLabel iddevicelabelAc=new JLabel();
	private int maxUniqueIDAc=0;
	
	private int IDDEVICEAct=0;
	private JLabel iddevicelabelAct=new JLabel();
	private int maxUniqueIDAct=0;
	
	//private String hvlFiltrationTable="";
	//private AdvancedSelectPanel asp = null;
	private JPanel suportSp = new JPanel(new BorderLayout());
	
	//private AdvancedSelectPanel asp_detail = null;
	private JPanel suportSp_detail = new JPanel(new BorderLayout());
	
	//private AdvancedSelectPanel aspt = null;
	private JPanel suportSpt = new JPanel(new BorderLayout());
	
	//private AdvancedSelectPanel asp_detailt = null;
	private JPanel suportSp_detailt = new JPanel(new BorderLayout());
	
	//private AdvancedSelectPanel aspAc = null;
	private JPanel suportSpAc = new JPanel(new BorderLayout());
	//private AdvancedSelectPanel aspAct = null;
	private JPanel suportSpAct = new JPanel(new BorderLayout());
	
	private JTabbedPane mainPanel;
	public static int ITAB=0;
	
	private Connection radqcdbcon = null;
	private DatabaseAgentSupport dbagent;
	private DatabaseAgentSupport dbagent_detail;
	
	private DatabaseAgentSupport dbagentt;
	private DatabaseAgentSupport dbagent_detailt;
	
	private DatabaseAgentSupport dbagentAc;
	private DatabaseAgentSupport dbagentAct;
	
	private JComboBox<String> orderbyCb;
	private JComboBox<String> orderbyCbt;
	private JComboBox<String> orderbyCbAc;
	private JComboBox<String> orderbyCbAct;
	private final Dimension sizeOrderCb = new Dimension(200, 21);
	
	/**
	 * Constructor
	 * @param mf the KVTFrame object
	 */
	public SaveViewDBFrame(KVTFrame mf){
		this.mf=mf;
		this.setTitle(mf.resources.getString("SaveViewDBFrame.NAME"));
		
		//=============================================================
		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = mf.resources.getString("data.load");// "Data";
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		opens = opens + file_sep + mf.mainDB;
    	radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
    	
    	dbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID", mf.kvRepeatabilityTable);
    	dbagent_detail = new DatabaseAgentSupport(radqcdbcon,"ID", mf.kvRepeatabilityTableDetail);
    	dbagentt = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID", mf.tRepeatabilityTable);
    	dbagent_detailt = new DatabaseAgentSupport(radqcdbcon,"ID", mf.tRepeatabilityTableDetail);
    	dbagentAc = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID", mf.kvAccuracyTable);
    	dbagentAct = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID", mf.tAccuracyTable);
    	dbagent.setHasValidAIColumn(false);
    	dbagent_detail.setHasValidAIColumn(false);
    	dbagentt.setHasValidAIColumn(false);
    	dbagent_detailt.setHasValidAIColumn(false);
    	dbagentAc.setHasValidAIColumn(false);
    	dbagentAct.setHasValidAIColumn(false);
    	//-------now dummy initialization of agents just to create combobox, label and tables!!!
    	dbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
    	dbagent.init();
    	dbagent_detail.setLinks("ID", Convertor.intToString(1));
    	dbagent_detail.init();
    	dbagentt.setLinks("UNIQUE_ID", Convertor.intToString(1));
    	dbagentt.init();
    	dbagent_detailt.setLinks("ID", Convertor.intToString(1));
    	dbagent_detailt.init();
    	dbagentAc.setLinks("UNIQUE_ID", Convertor.intToString(1));
    	dbagentAc.init();
    	dbagentAct.setLinks("UNIQUE_ID", Convertor.intToString(1));
    	dbagentAct.init();
		
		//==================================
		
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
		
		mainPanel.setSelectedIndex(ITAB);
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
			//int dummy=1;
			String s = "select * from " + mf.kvRepeatabilityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);

			dbagent.select(s);
			
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);
			JTable mainTable = dbagent.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp.add(scrollPane, BorderLayout.CENTER);
			//JTable mainTable = asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();//fired each time a row is selected in main table
				}
			});
			
			IDDEVICE = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueID=Convertor.stringToInt(maxUniqueIDs);								
			} 

			//detail
			s = "select * from " + mf.kvRepeatabilityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueID+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detail.select(s);
			
			//asp_detail = new AdvancedSelectPanel();
			//suportSp_detail.add(asp_detail, BorderLayout.CENTER);
			mainTable = dbagent_detail.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane1 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp_detail.add(scrollPane1, BorderLayout.CENTER);
			
			//mainTable = asp_detail.getTab();
			IDDEVICE_detail = mainTable.getRowCount();
			
			//same for time
			s = "select * from " + mf.tRepeatabilityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagentt.select(s);

			//aspt = new AdvancedSelectPanel();
			//suportSpt.add(aspt, BorderLayout.CENTER);
			mainTable = dbagentt.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane2 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSpt.add(scrollPane2, BorderLayout.CENTER);
			//mainTable = aspt.getTab();

			ListSelectionModel rowSMt = mainTable.getSelectionModel();
			rowSMt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSMt.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTablet();//fired each time a row is selected in main table
				}
			});
			
			IDDEVICEt = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIDt=Convertor.stringToInt(maxUniqueIDs);								
			} 

			//detail
			s = "select * from " + mf.tRepeatabilityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueIDt+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detailt.select(s);
			
			//asp_detailt = new AdvancedSelectPanel();
			//suportSp_detailt.add(asp_detailt, BorderLayout.CENTER);
			mainTable = dbagent_detailt.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane3 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp_detailt.add(scrollPane3, BorderLayout.CENTER);
			
			//mainTable = asp_detailt.getTab();
			IDDEVICE_detailt = mainTable.getRowCount();
			
			//ACCURACY
			s = "select * from " + mf.kvAccuracyTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagentAc.select(s);

			//aspAc = new AdvancedSelectPanel();
			//suportSpAc.add(aspAc, BorderLayout.CENTER);
			mainTable = dbagentAc.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane4 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSpAc.add(scrollPane4, BorderLayout.CENTER);

			//mainTable = aspAc.getTab();

			ListSelectionModel rowSMAc = mainTable.getSelectionModel();
			rowSMAc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			IDDEVICEAc = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIDAc=Convertor.stringToInt(maxUniqueIDs);								
			} 
			
			s = "select * from " + mf.tAccuracyTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagentAct.select(s);

			//aspAct = new AdvancedSelectPanel();
			//suportSpAct.add(aspAct, BorderLayout.CENTER);
			mainTable = dbagentAct.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane5 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSpAct.add(scrollPane5, BorderLayout.CENTER);
			//mainTable = aspAct.getTab();

			ListSelectionModel rowSMAct = mainTable.getSelectionModel();
			rowSMAct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			IDDEVICEAct = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIDAct=Convertor.stringToInt(maxUniqueIDs);								
			} 
			
			//if (con1 != null)
				//con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update nested table (KvP repeatability)
	 */
	private void updateDetailTable() {
	//System.out.println("update kv detail fired");//nope
		String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mf.mainDB;
		opens = opens + file_sep + dbName;
		
		JTable aspTable = dbagent.getMainTable();//asp.getTab();
		int selID = 0;// NO ZERO ID
		int selUID = 0;
		int selRow = aspTable.getSelectedRow();
		if (selRow != -1) {
			selUID = (Integer) aspTable.getValueAt(selRow, 0);
			selID = (Integer) aspTable.getValueAt(selRow, 1);//0);//Unique_ID is 0 and IDLINK is 1
		} else {
			return;
		}

		try {
			//=============populate some fields from main table
			String s = "select * from " + mf.kvRepeatabilityTable+ " where IDLINK = "+selID +" order by Unique_ID";
			//NOTHING HERE
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);

			//=====================
			// now the job:
			s = "select * from " + mf.kvRepeatabilityTableDetail + 
			" where IDLINK = "+ selID + 
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";
			// IF press header=>selRow=-1=>ID=0=>NO ZERO ID DATA=>
			// so display an empty table!
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detail.select(s);

			//if (asp_detail != null)
				//suportSp_detail.remove(asp_detail);
			//asp_detail = new AdvancedSelectPanel();
			//suportSp_detail.add(asp_detail, BorderLayout.CENTER);

			//if (asp_detail != null){				
				JTable mainTab=dbagent_detail.getMainTable();//asp_detail.getTab();
				IDDEVICE_detail = mainTab.getRowCount();
				String str=Convertor.intToString(IDDEVICE_detail);
				iddevicelabeldetail.setText(str);
			//}
			
			//if (con1 != null)
				//con1.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//validate();
	}
	
	/**
	 * Update nested table (exposure time repeatability)
	 */
	private void updateDetailTablet() {
		//System.out.println("enter from start?");//nope
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		JTable aspTable = dbagentt.getMainTable();//aspt.getTab();//TIME
		int selID = 0;// NO ZERO ID
		int selUID=0;
		int selRow = aspTable.getSelectedRow();
		if (selRow != -1) {
			selUID = (Integer) aspTable.getValueAt(selRow, 0);
			selID = (Integer) aspTable.getValueAt(selRow, 1);//0);//Unique_ID is 0 and IDLINK is 1
		} else {
			return;
		}

		try {
			//=============populate some fields from main table
			String s = "select * from " + mf.tRepeatabilityTable+ " where IDLINK = "+selID +" order by Unique_ID";
			//NOTHING HERE!!!!!
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			//=====================
			// now the job:
			s = "select * from " + mf.tRepeatabilityTableDetail + 
			" where IDLINK = "+ selID + 
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";
			// IF press header=>selRow=-1=>ID=0=>NO ZERO ID DATA=>
			// so display an empty table!
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detailt.select(s);

			//if (asp_detailt != null)
				//suportSp_detailt.remove(asp_detailt);
			//asp_detailt = new AdvancedSelectPanel();
			//suportSp_detailt.add(asp_detailt, BorderLayout.CENTER);

			//if (asp_detailt != null){				
				JTable mainTab=dbagent_detailt.getMainTable();//asp_detailt.getTab();
				IDDEVICE_detailt = mainTab.getRowCount();
				String str=Convertor.intToString(IDDEVICE_detailt);
				iddevicelabeldetailt.setText(str);
			//}

			//if (con1 != null)
				//con1.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//validate();
	}
	
	/**
	 * Create main panel
	 * @return the result
	 */
	private JTabbedPane createMainPanel() {
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		//===================================================================
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
		
		JPanel dorderP = new JPanel();
		dorderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderP.add(label);
		dorderP.add(dbagent_detail.getRecordsLabel());
		
		orderbyCbt = dbagentt.getOrderByComboBox();
		orderbyCbt.setMaximumRowCount(5);
		orderbyCbt.setPreferredSize(sizeOrderCb);
		orderbyCbt.addItemListener(this);
		JPanel orderPt = new JPanel();
		orderPt.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderPt.add(label);
		orderPt.add(orderbyCbt);
		orderPt.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderPt.add(label);
		orderPt.add(dbagentt.getRecordsLabel());
		
		JPanel dorderPt = new JPanel();
		dorderPt.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderPt.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderPt.add(label);
		dorderPt.add(dbagent_detailt.getRecordsLabel());
		
		orderbyCbAc = dbagentAc.getOrderByComboBox();
		orderbyCbAc.setMaximumRowCount(5);
		orderbyCbAc.setPreferredSize(sizeOrderCb);
		orderbyCbAc.addItemListener(this);
		JPanel orderPAc = new JPanel();
		orderPAc.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderPAc.add(label);
		orderPAc.add(orderbyCbAc);
		orderPAc.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderPAc.add(label);
		orderPAc.add(dbagentAc.getRecordsLabel());
		
		orderbyCbAct = dbagentAct.getOrderByComboBox();
		orderbyCbAct.setMaximumRowCount(5);
		orderbyCbAct.setPreferredSize(sizeOrderCb);
		orderbyCbAct.addItemListener(this);
		JPanel orderPAct = new JPanel();
		orderPAct.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderPAct.add(label);
		orderPAct.add(orderbyCbAct);
		orderPAct.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderPAct.add(label);
		orderPAct.add(dbagentAct.getRecordsLabel());		
		//==================================================================
		suportSp.setPreferredSize(tableDimension);
		
		JPanel p4P=new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4P.add(label);
		String str=Convertor.intToString(IDDEVICE);
		iddevicelabel.setText(str);// = new JLabel(str);
		iddevicelabel.setForeground(RadQCFrame.foreColor);
		p4P.add(iddevicelabel);
		p4P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p5P = new JPanel();
		BoxLayout blp5P = new BoxLayout(p5P, BoxLayout.Y_AXIS);
		p5P.setLayout(blp5P);
		//p5P.setBorder(FrameUtilities.getGroupBoxBorder(
		//		mf.resources.getString("records.border"),
		//		RadQCFrame.foreColor));
		p5P.add(suportSp);
		p5P.add(orderP);//////////////////////////p5P.add(p4P);
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
		
		JPanel kvRP = new JPanel();
		BoxLayout blkvRP = new BoxLayout(kvRP, BoxLayout.Y_AXIS);
		kvRP.setLayout(blkvRP);
		kvRP.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("kvR.border"),
				RadQCFrame.foreColor));
		kvRP.add(suportSp);
		kvRP.add(orderP);/////////////////////////////////////kvRP.add(p4P);
		kvRP.add(p7P);
		kvRP.setBackground(RadQCFrame.bkgColor);
		//=====================detail
		suportSp_detail.setPreferredSize(tableDimension);
		
		JPanel p4Pd=new JPanel();
		p4Pd.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4Pd.add(label);
		str=Convertor.intToString(IDDEVICE_detail);
		iddevicelabeldetail.setText(str);// = new JLabel(str);
		iddevicelabeldetail.setForeground(RadQCFrame.foreColor);
		p4Pd.add(iddevicelabeldetail);
		p4Pd.setBackground(RadQCFrame.bkgColor);
			
		JPanel kvRPd = new JPanel();
		BoxLayout blkvRPd = new BoxLayout(kvRPd, BoxLayout.Y_AXIS);
		kvRPd.setLayout(blkvRPd);
		kvRPd.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("details.border"),
				RadQCFrame.foreColor));
		kvRPd.add(suportSp_detail);
		kvRPd.add(dorderP);//////////////////////////kvRPd.add(p4Pd);		
		kvRPd.setBackground(RadQCFrame.bkgColor);
		//============all KV rep.
		JPanel kvRall = new JPanel();
		BoxLayout blkvRall = new BoxLayout(kvRall, BoxLayout.Y_AXIS);
		kvRall.setLayout(blkvRall);
		kvRall.add(kvRP);
		kvRall.add(kvRPd);
		kvRall.setBackground(RadQCFrame.bkgColor);
		//rep time:
		suportSpt.setPreferredSize(tableDimension);
		JPanel p4Pt=new JPanel();
		p4Pt.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4Pt.add(label);
		str=Convertor.intToString(IDDEVICEt);
		iddevicelabelt.setText(str);// = new JLabel(str);
		iddevicelabelt.setForeground(RadQCFrame.foreColor);
		p4Pt.add(iddevicelabelt);
		p4Pt.setBackground(RadQCFrame.bkgColor);
		
		JPanel p7Pt=new JPanel();
		p7Pt.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		
		buttonName = mf.resources.getString("save.saveB");
		buttonToolTip = mf.resources.getString("save.saveB.toolTip");
		buttonIconName = mf.resources.getString("img.save.database");
		button = FrameUtilities.makeButton(buttonIconName, SAVET_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pt.add(button);
		
		buttonName = mf.resources.getString("save.deleteB");
		buttonToolTip = mf.resources.getString("save.deleteB.toolTip");
		buttonIconName = mf.resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETET_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pt.add(button);
		
		buttonName = mf.resources.getString("save.viewB");
		buttonToolTip = mf.resources.getString("save.viewB.toolTip");
		buttonIconName = mf.resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEWT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.viewB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pt.add(button);
		p7Pt.setBackground(RadQCFrame.bkgColor);
		
		JPanel kvRPt = new JPanel();
		BoxLayout blkvRPt = new BoxLayout(kvRPt, BoxLayout.Y_AXIS);
		kvRPt.setLayout(blkvRPt);
		kvRPt.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("tR.border"),
				RadQCFrame.foreColor));
		kvRPt.add(suportSpt);
		kvRPt.add(orderPt);/////////////////////kvRPt.add(p4Pt);
		kvRPt.add(p7Pt);
		kvRPt.setBackground(RadQCFrame.bkgColor);
		//detail
		suportSp_detailt.setPreferredSize(tableDimension);
		
		JPanel p4Pdt=new JPanel();
		p4Pdt.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4Pdt.add(label);
		str=Convertor.intToString(IDDEVICE_detailt);
		iddevicelabeldetailt.setText(str);// = new JLabel(str);
		iddevicelabeldetailt.setForeground(RadQCFrame.foreColor);
		p4Pdt.add(iddevicelabeldetailt);
		p4Pdt.setBackground(RadQCFrame.bkgColor);
			
		JPanel kvRPdt = new JPanel();
		BoxLayout blkvRPdt = new BoxLayout(kvRPdt, BoxLayout.Y_AXIS);
		kvRPdt.setLayout(blkvRPdt);
		kvRPdt.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("details.border"),
				RadQCFrame.foreColor));
		kvRPdt.add(suportSp_detailt);
		kvRPdt.add(dorderPt);////////////////////////////kvRPdt.add(p4Pdt);		
		kvRPdt.setBackground(RadQCFrame.bkgColor);
		//============all KV rep.
		JPanel kvRallt = new JPanel();
		BoxLayout blkvRallt = new BoxLayout(kvRallt, BoxLayout.Y_AXIS);
		kvRallt.setLayout(blkvRallt);
		kvRallt.add(kvRPt);
		kvRallt.add(kvRPdt);
		kvRallt.setBackground(RadQCFrame.bkgColor);
		//=================
		
		//ACC
		suportSpAc.setPreferredSize(tableDimension);
		
		JPanel p4PAc=new JPanel();
		p4PAc.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4PAc.add(label);
		str=Convertor.intToString(IDDEVICEAc);
		iddevicelabelAc.setText(str);// = new JLabel(str);
		iddevicelabelAc.setForeground(RadQCFrame.foreColor);
		p4PAc.add(iddevicelabelAc);
		p4PAc.setBackground(RadQCFrame.bkgColor);
				
		JPanel p7PAc=new JPanel();
		p7PAc.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		
		buttonName = mf.resources.getString("save.saveB");
		buttonToolTip = mf.resources.getString("save.saveB.toolTip");
		buttonIconName = mf.resources.getString("img.save.database");
		button = FrameUtilities.makeButton(buttonIconName, SAVEAC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7PAc.add(button);
		
		buttonName = mf.resources.getString("save.deleteB");
		buttonToolTip = mf.resources.getString("save.deleteB.toolTip");
		buttonIconName = mf.resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETEAC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7PAc.add(button);
		
		buttonName = mf.resources.getString("save.viewB");
		buttonToolTip = mf.resources.getString("save.viewB.toolTip");
		buttonIconName = mf.resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEWAC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.viewB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7PAc.add(button);
		p7PAc.setBackground(RadQCFrame.bkgColor);
		
		JPanel kvRPAc = new JPanel();
		BoxLayout blkvRPAc = new BoxLayout(kvRPAc, BoxLayout.Y_AXIS);
		kvRPAc.setLayout(blkvRPAc);
		kvRPAc.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("kvAc.border"),
				RadQCFrame.foreColor));
		kvRPAc.add(suportSpAc);
		kvRPAc.add(orderPAc);////////////////////////////kvRPAc.add(p4PAc);
		kvRPAc.add(p7PAc);
		kvRPAc.setBackground(RadQCFrame.bkgColor);
		//----------
		suportSpAct.setPreferredSize(tableDimension);
		
		JPanel p4PAct=new JPanel();
		p4PAct.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4PAct.add(label);
		str=Convertor.intToString(IDDEVICEAct);
		iddevicelabelAct.setText(str);// = new JLabel(str);
		iddevicelabelAct.setForeground(RadQCFrame.foreColor);
		p4PAct.add(iddevicelabelAct);
		p4PAct.setBackground(RadQCFrame.bkgColor);
				
		JPanel p7PAct=new JPanel();
		p7PAct.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		
		buttonName = mf.resources.getString("save.saveB");
		buttonToolTip = mf.resources.getString("save.saveB.toolTip");
		buttonIconName = mf.resources.getString("img.save.database");
		button = FrameUtilities.makeButton(buttonIconName, SAVEACT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7PAct.add(button);
		
		buttonName = mf.resources.getString("save.deleteB");
		buttonToolTip = mf.resources.getString("save.deleteB.toolTip");
		buttonIconName = mf.resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETEACT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7PAct.add(button);
		
		buttonName = mf.resources.getString("save.viewB");
		buttonToolTip = mf.resources.getString("save.viewB.toolTip");
		buttonIconName = mf.resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEWACT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.viewB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7PAct.add(button);
		p7PAct.setBackground(RadQCFrame.bkgColor);
		
		JPanel kvRPAct = new JPanel();
		BoxLayout blkvRPAct = new BoxLayout(kvRPAct, BoxLayout.Y_AXIS);
		kvRPAct.setLayout(blkvRPAct);
		kvRPAct.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("tAc.border"),
				RadQCFrame.foreColor));
		kvRPAct.add(suportSpAct);
		kvRPAct.add(orderPAct);////////////////////////kvRPAct.add(p4PAct);
		kvRPAct.add(p7PAct);
		kvRPAct.setBackground(RadQCFrame.bkgColor);
		
		JTabbedPane jtab = new JTabbedPane();
		jtab.add(kvRall,  mf.resources.getString("reprod.tab.title"));
		jtab.add(kvRallt,  mf.resources.getString("reprod.tab.title1"));
		jtab.add(kvRPAc,  mf.resources.getString("ac.tab.title"));
		jtab.add(kvRPAct,  mf.resources.getString("ac.tab.title1"));
		//==============
		//JPanel mainP = new JPanel(new BorderLayout());
		//mainP.add(kvRall, BorderLayout.CENTER);//p5P, BorderLayout.CENTER);
		//mainP.add(p7P, BorderLayout.SOUTH);
		//jtab.setBackground(RadQCFrame.bkgColor);
		return jtab;
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
		} else if (command.equals(SAVET_COMMAND)) {
			savet();
		} else if (command.equals(DELETET_COMMAND)) {
			deletet();
		} else if (command.equals(VIEWT_COMMAND)) {
			viewt();
		} else if (command.equals(SAVEAC_COMMAND)) {
			saveAc();
		} else if (command.equals(DELETEAC_COMMAND)) {
			deleteAc();
		} else if (command.equals(VIEWAC_COMMAND)) {
			viewAc();
		} else if (command.equals(SAVEACT_COMMAND)) {
			saveAct();
		} else if (command.equals(DELETEACT_COMMAND)) {
			deleteAct();
		} else if (command.equals(VIEWACT_COMMAND)) {
			viewAct();
		}
	}
	
	/**
	 * JCombobox actions are set here
	 */
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == orderbyCb) {
			sort();
		} else if (e.getSource() == orderbyCbt) {
			sortt();
		} else if (e.getSource() == orderbyCbAc) {
			sortAc();
		} else if (e.getSource() == orderbyCbAct) {
			sortAct();
		} 
	}
	
	/**
	 * Sorts data from KvP repeatability table
	 */
	private void sort() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCb.getSelectedItem();
		dbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagent.performSelection(orderbyS);
	}
	
	/**
	 * Sorts data from exposure time repeatability table
	 */
	private void sortt() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCbt.getSelectedItem();
		dbagentt.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagentt.performSelection(orderbyS);
	}
	
	/**
	 * Sorts data from KvP accuracy table
	 */
	private void sortAc() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCbAc.getSelectedItem();
		dbagentAc.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagentAc.performSelection(orderbyS);
	}
	
	/**
	 * Sorts data from exposure time accuracy table
	 */
	private void sortAct() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCbAct.getSelectedItem();
		dbagentAct.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagentAct.performSelection(orderbyS);
	}
	
	/**
	 * Save KvP accuracy QC test results
	 */
	private void saveAc(){
		//System.out.println("SAVE AC");
		if (!mf.isOktoSaveKvAccuracy){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.kv.acc.error.message"),
					mf.resources.getString("nosave.error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		String resultS="";
		
		try {			
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
						
			int n = mf.cvkvacd.length;//mf.kvd.length;//cpsV.size();
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ mf.kvAccuracyTable + " values " + "(?, ?, ?, ?, ?, ?, ?, ?)");
			for (int i = 0; i < n; i++) {				
				int id = maxUniqueIDAc + i + 1;//Unique ID
				psInsert.setString(1, Convertor.intToString(id));
				psInsert.setString(2, Convertor.intToString(mf.IDLINK));
				psInsert.setString(3, mf.measurementDate_toSave);
				psInsert.setString(4, Convertor.doubleToString(mf.kvsacd[i]));//set
				psInsert.setString(5, Convertor.doubleToString(mf.kvmacd[i]));//measured
				psInsert.setString(6, Convertor.doubleToString(mf.cvkvacd[i]));//diff
				psInsert.setString(7, Convertor.doubleToString(mf.kvaccvm));//max dif
				if (mf.kvaccvb[i])
	            	resultS=mf.resources.getString("reprod.rezultat.succes");
	            else
					resultS=mf.resources.getString("reprod.rezultat.fail");			
				psInsert.setString(8, resultS);									
				psInsert.executeUpdate();				
			}
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
						
			selectTableAc();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Select data from KvP accuracy table
	 */
	private void selectTableAc(){
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.kvAccuracyTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			dbagentAc.select(s);
			
			//suportSpAc.remove(aspAc);//remove first
			//aspAc = new AdvancedSelectPanel();
			//suportSpAc.add(aspAc, BorderLayout.CENTER);

			JTable mainTable = dbagentAc.getMainTable();//aspAc.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			IDDEVICEAc = mainTable.getRowCount();
						
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIDAc=Convertor.stringToInt(maxUniqueIDs);
								
			} else {
				maxUniqueIDAc=0;//reset counter
			}
						
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(IDDEVICEAc);
			iddevicelabelAc.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
		
	}

	/**
	 * Delete an entry from KvP accuracy table
	 */
	private void deleteAc(){
		//System.out.println("DEL AC");
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = dbagentAc.getMainTable();//aspAc.getTab();

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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.kvAccuracyTable);//deviceTable);
			//PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("Unique_ID");//("ID");
				int id2 = res.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res.deleteRow();
				} else if (id > selID && id2==selID2) {
					
					//psUpdate = con1.prepareStatement("update " + muTable
						//	+ " set ID=? where ID=?");

					//psUpdate.setInt(1, id - 1);
					//psUpdate.setInt(2, id);

					//psUpdate.executeUpdate();
					//psUpdate.close();
				}
			}
						
			selectTableAc();
			
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
	 * View all data from KvP accuracy table
	 */
	private void viewAc(){
		//System.out.println("VIEW AC");
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.kvAccuracyTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagentAc.select(s);

			//suportSpAc.remove(aspAc);//remove first
			//aspAc = new AdvancedSelectPanel();
			//suportSpAc.add(aspAc, BorderLayout.CENTER);

			JTable mainTable = dbagentAc.getMainTable();//aspAc.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			IDDEVICEAc = mainTable.getRowCount();
								
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

			String str=Convertor.intToString(IDDEVICEAc);
			iddevicelabelAc.setText(str);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection			
	}
	
	/**
	 * Save exposure time accuracy QC test results
	 */
	private void saveAct(){
		//System.out.println("SAVE AC time");
		if (!mf.isOktoSaveTexpAccuracy){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.t.acc.error.message"),
					mf.resources.getString("nosave.error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}	
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		String resultS="";
		
		try {			
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
						
			int n = mf.cvtexpacd.length;//mf.kvd.length;//cpsV.size();
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ mf.tAccuracyTable + " values " + "(?, ?, ?, ?, ?, ?, ?, ?)");
			for (int i = 0; i < n; i++) {				
				int id = maxUniqueIDAct + i + 1;//Unique ID
				psInsert.setString(1, Convertor.intToString(id));
				psInsert.setString(2, Convertor.intToString(mf.IDLINK));
				psInsert.setString(3, mf.measurementDate_toSave);
				psInsert.setString(4, Convertor.doubleToString(mf.texpsacd[i]));//set
				psInsert.setString(5, Convertor.doubleToString(mf.texpmacd[i]));//measured
				psInsert.setString(6, Convertor.doubleToString(mf.cvtexpacd[i]));//diff
				psInsert.setString(7, Convertor.doubleToString(mf.texpaccvm));//max dif
				if (mf.texpaccvb[i])
	            	resultS=mf.resources.getString("reprod.rezultat.succes");
	            else
					resultS=mf.resources.getString("reprod.rezultat.fail");			
				psInsert.setString(8, resultS);									
				psInsert.executeUpdate();				
			}
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
						
			selectTableAct();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}		
	}
	
	/**
	 * Select data from exposure time accuracy table
	 */
	private void selectTableAct(){
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.tAccuracyTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			dbagentAct.select(s);
			
			//suportSpAct.remove(aspAct);//remove first
			//aspAct = new AdvancedSelectPanel();
			//suportSpAct.add(aspAct, BorderLayout.CENTER);

			JTable mainTable = dbagentAct.getMainTable();//aspAct.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			IDDEVICEAct = mainTable.getRowCount();
						
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIDAct=Convertor.stringToInt(maxUniqueIDs);
								
			} else {
				maxUniqueIDAct=0;//reset counter
			}
						
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(IDDEVICEAct);
			iddevicelabelAct.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
		
	}

	/**
	 * Delete an entry from exposure time accuracy table
	 */
	private void deleteAct(){
		//System.out.println("DEL AC time");
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = dbagentAct.getMainTable();//aspAct.getTab();

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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.tAccuracyTable);//deviceTable);
			//PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("Unique_ID");//("ID");
				int id2 = res.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res.deleteRow();
				} else if (id > selID && id2==selID2) {
					
					//psUpdate = con1.prepareStatement("update " + muTable
						//	+ " set ID=? where ID=?");

					//psUpdate.setInt(1, id - 1);
					//psUpdate.setInt(2, id);

					//psUpdate.executeUpdate();
					//psUpdate.close();
				}
			}
						
			selectTableAct();
			
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
	 * View all data from exposure time accuracy table
	 */
	private void viewAct(){
		//System.out.println("VIEW AC time");
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.tAccuracyTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagentAct.select(s);

			//suportSpAct.remove(aspAct);//remove first
			///aspAct = new AdvancedSelectPanel();
			//suportSpAct.add(aspAct, BorderLayout.CENTER);

			JTable mainTable = dbagentAct.getMainTable();//aspAct.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			IDDEVICEAct = mainTable.getRowCount();
								
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

			String str=Convertor.intToString(IDDEVICEAct);
			iddevicelabelAct.setText(str);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection		
	}
	
	/**
	 * Save exposure time repeatability QC test results
	 */
	private void savet(){
		//System.out.println("SAVE REP time");
		if (!mf.isOktoSaveTexpRepeatability){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.t.rep.error.message"),
					mf.resources.getString("nosave.error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}		
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		String resultS="";
		
		try {			
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			//-------------------------
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ mf.tRepeatabilityTable + " values " + "(?, ?, ?, ?, ?, ?)");
			int id = maxUniqueIDt + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, Convertor.intToString(mf.IDLINK));
			psInsert.setString(3, mf.measurementDate_toSave);
			psInsert.setString(4, Convertor.doubleToString(mf.texpcv));
			psInsert.setString(5, Convertor.doubleToString(mf.texpcvm));
			if (mf.texpcvb)
            	resultS=mf.resources.getString("reprod.rezultat.succes");
            else
				resultS=mf.resources.getString("reprod.rezultat.fail");			
			psInsert.setString(6, resultS);									
			psInsert.executeUpdate();
			//------------------------
			
			int n = mf.texpd.length;//cpsV.size();
			psInsert = radqcdbcon.prepareStatement("insert into " + mf.tRepeatabilityTableDetail//con1.prepareStatement("insert into " + mf.tRepeatabilityTableDetail
					+ " values " + "(?, ?, ?, ?, ?)");
			for (int i = 0; i < n; i++) {
				int nrcrt = i + 1;
				psInsert.setString(1, Convertor.intToString(nrcrt));
				psInsert.setString(2, Convertor.intToString(id));///NOT IDLINK
				psInsert.setString(3, Convertor.intToString(mf.IDLINK));
				psInsert.setString(4,
						Convertor.doubleToString(mf.texpset));
				psInsert.setString(5,
						Convertor.doubleToString(mf.texpd[i]));
				
				psInsert.executeUpdate();
			}
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
						
			selectTablet();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}		
	}
	
	/**
	 * Select data from exposure time repeatability table
	 */
	private void selectTablet(){
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.tRepeatabilityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			dbagentt.select(s);			
			
			//suportSpt.remove(aspt);//remove first
			//aspt = new AdvancedSelectPanel();
			//suportSpt.add(aspt, BorderLayout.CENTER);

			JTable mainTable = dbagentt.getMainTable();//aspt.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTablet();//fired each time a row is selected in main table
				}
			});
			
			IDDEVICEt = mainTable.getRowCount();
						
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIDt=Convertor.stringToInt(maxUniqueIDs);
								
			} else {
				maxUniqueIDt=0;//reset counter
			}
			
			//detail
			s = "select * from " + mf.tRepeatabilityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueIDt+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detailt.select(s);
			
			//suportSp_detailt.remove(asp_detailt);//remove first
			//asp_detailt = new AdvancedSelectPanel();
			//suportSp_detailt.add(asp_detailt, BorderLayout.CENTER);
			
			mainTable = dbagent_detailt.getMainTable();//asp_detailt.getTab();
			IDDEVICE_detailt = mainTable.getRowCount();
			
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(IDDEVICEt);
			iddevicelabelt.setText(str);
			str=Convertor.intToString(IDDEVICE_detailt);
			iddevicelabeldetailt.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
		
	}

	/**
	 * Delete an entry from exposure time repeatability table
	 */
	private void deletet(){
		//System.out.println("DEL REP time");
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = dbagentt.getMainTable();//aspt.getTab();

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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.tRepeatabilityTable);//deviceTable);
			//PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("Unique_ID");//("ID");
				int id2 = res.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res.deleteRow();
				} else if (id > selID && id2==selID2) {
					
					//psUpdate = con1.prepareStatement("update " + muTable
						//	+ " set ID=? where ID=?");

					//psUpdate.setInt(1, id - 1);
					//psUpdate.setInt(2, id);

					//psUpdate.executeUpdate();
					//psUpdate.close();
				}
			}
			
			//now the detail
			ResultSet res1 = s.executeQuery("SELECT * FROM " + mf.tRepeatabilityTableDetail);//deviceTable);
			while (res1.next()) {
				int id = res1.getInt("Unique_ID");//("ID");
				int id2 = res1.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res1.deleteRow();
				} 
			}
			//===============================================
			selectTablet();
			
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
	 * View all data from exposure time repeatability table
	 */
	private void viewt(){
		//System.out.println("VIEW REP time");
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.tRepeatabilityTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagentt.select(s);

			//suportSpt.remove(aspt);//remove first
			//aspt = new AdvancedSelectPanel();
			//suportSpt.add(aspt, BorderLayout.CENTER);

			JTable mainTable = dbagentt.getMainTable();//aspt.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTablet();//fired each time a row is selected in main table
				}
			});
			
			IDDEVICEt = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				//Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//maxUniqueID=Convertor.stringToInt(maxUniqueIDs);//not here in view all!!!								
			} //else
				//maxUniqueID=0;//reset counter
			
			//detail
			int selUID = (Integer) mainTable.getValueAt(IDDEVICEt-1, 0);//column 1, Unique_ID
			int selID = (Integer) mainTable.getValueAt(IDDEVICEt-1, 1);//column 2, LINK_ID
			
			s = "select * from " + mf.tRepeatabilityTableDetail + 
			" where IDLINK = "+ selID + 
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detailt.select(s);			
			
			//suportSp_detailt.remove(asp_detailt);//remove first
			//asp_detailt = new AdvancedSelectPanel();
			//suportSp_detailt.add(asp_detailt, BorderLayout.CENTER);
			
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(IDDEVICEt);
			iddevicelabelt.setText(str);
			
			mainTable = dbagent_detailt.getMainTable();//asp_detailt.getTab();
			IDDEVICE_detailt = mainTable.getRowCount();
			str=Convertor.intToString(IDDEVICE_detailt);
			iddevicelabeldetailt.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection
	}
	
	/**
	 * Save KvP repeatability QC test results
	 */
	private void save(){
		//System.out.println("SAVE REP kv");
		if (!mf.isOktoSaveKvRepeatability){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.kv.rep.error.message"),
					mf.resources.getString("nosave.error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		String resultS="";
		
		try {			
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			//-------------------------
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ mf.kvRepeatabilityTable + " values " + "(?, ?, ?, ?, ?, ?)");
			int id = maxUniqueID + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, Convertor.intToString(mf.IDLINK));
			psInsert.setString(3, mf.measurementDate_toSave);
			psInsert.setString(4, Convertor.doubleToString(mf.kvcv));
			psInsert.setString(5, Convertor.doubleToString(mf.kvcvm));
			if (mf.kvcvb)
            	resultS=mf.resources.getString("reprod.rezultat.succes");
            else
				resultS=mf.resources.getString("reprod.rezultat.fail");			
			psInsert.setString(6, resultS);									
			psInsert.executeUpdate();
			//------------------------
			
			int n = mf.kvd.length;//cpsV.size();
			psInsert = radqcdbcon.prepareStatement("insert into " + mf.kvRepeatabilityTableDetail//con1.prepareStatement("insert into " + mf.kvRepeatabilityTableDetail
					+ " values " + "(?, ?, ?, ?, ?)");
			for (int i = 0; i < n; i++) {
				int nrcrt = i + 1;
				psInsert.setString(1, Convertor.intToString(nrcrt));
				psInsert.setString(2, Convertor.intToString(id));///NOT IDLINK
				psInsert.setString(3, Convertor.intToString(mf.IDLINK));
				psInsert.setString(4,
						Convertor.doubleToString(mf.kvset));
				psInsert.setString(5,
						Convertor.doubleToString(mf.kvd[i]));
				
				psInsert.executeUpdate();
			}
			
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
	 * Select data from KvP repeatability table
	 */
	private void selectTable(){
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.kvRepeatabilityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

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
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();//fired each time a row is selected in main table
				}
			});
			
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
			
			//detail
			s = "select * from " + mf.kvRepeatabilityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueID+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detail.select(s);
			
			//suportSp_detail.remove(asp_detail);//remove first
			//asp_detail = new AdvancedSelectPanel();
			//suportSp_detail.add(asp_detail, BorderLayout.CENTER);
			
			mainTable = dbagent_detail.getMainTable();//asp_detail.getTab();
			IDDEVICE_detail = mainTable.getRowCount();
			
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(IDDEVICE);
			iddevicelabel.setText(str);
			str=Convertor.intToString(IDDEVICE_detail);
			iddevicelabeldetail.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
		
	}
	
	/**
	 * Delete an entry from KvP repeatability table
	 */
	private void delete(){
		//System.out.println("DEL REP kv");
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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.kvRepeatabilityTable);//deviceTable);
			//PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("Unique_ID");//("ID");
				int id2 = res.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res.deleteRow();
				} else if (id > selID && id2==selID2) {
					
					//psUpdate = con1.prepareStatement("update " + muTable
						//	+ " set ID=? where ID=?");

					//psUpdate.setInt(1, id - 1);
					//psUpdate.setInt(2, id);

					//psUpdate.executeUpdate();
					//psUpdate.close();
				}
			}
			
			//now the detail
			ResultSet res1 = s.executeQuery("SELECT * FROM " + mf.kvRepeatabilityTableDetail);//deviceTable);
			while (res1.next()) {
				int id = res1.getInt("Unique_ID");//("ID");
				int id2 = res1.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res1.deleteRow();
				} 
			}
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
	
	/**
	 * View all data from KvP repeatability table
	 */
	private void view(){
		//System.out.println("VIEW REP kv");
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.kvRepeatabilityTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagent.select(s);

			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = dbagent.getMainTable();//asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();//fired each time a row is selected in main table
				}
			});
			
			IDDEVICE = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				//Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//maxUniqueID=Convertor.stringToInt(maxUniqueIDs);//not here in view all!!!								
			} else {
				//maxUniqueID=0;//reset counter
			
		    }
			//detail
			
			int selUID = (Integer) mainTable.getValueAt(IDDEVICE-1, 0);//column 1, Unique_ID
			int selID = (Integer) mainTable.getValueAt(IDDEVICE-1, 1);//column 2, LINK_ID
			s = "select * from " + mf.kvRepeatabilityTableDetail + 
			" where IDLINK = "+ selID +
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			dbagent_detail.select(s);
			
			//suportSp_detail.remove(asp_detail);//remove first	
			//asp_detail = new AdvancedSelectPanel();
			//suportSp_detail.add(asp_detail, BorderLayout.CENTER);
				
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(IDDEVICE);
			iddevicelabel.setText(str);
			
			mainTable = dbagent_detail.getMainTable();//asp_detail.getTab();
			IDDEVICE_detail = mainTable.getRowCount();
			str=Convertor.intToString(IDDEVICE_detail);
			iddevicelabeldetail.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection
	}
}
