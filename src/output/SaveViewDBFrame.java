package output;

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

import radQC.RadQCFrame;
import danfulea.db.DatabaseAgent;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;

/**
 * View/Save in database tube output related QC tests.
 * 
 * @author Dan Fulea, 02 May 2015
 */
@SuppressWarnings("serial")
public class SaveViewDBFrame extends JFrame implements ActionListener, ItemListener{
	
	private final Dimension PREFERRED_SIZE = new Dimension(900, 700);
	private final Dimension tableDimension = new Dimension(800, 200);
	public static int ITAB = 0;
	private OutputFrame mf;
	
	private static final String SAVE_COMMAND = "SAVE";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String VIEW_COMMAND = "VIEW";

	private static final String SAVEREP_COMMAND = "SAVEREP";
	private static final String DELETEREP_COMMAND = "DELETEREP";
	private static final String VIEWREP_COMMAND = "VIEWREP";

	private static final String SAVELIN_COMMAND = "SAVELIN";
	private static final String DELETELIN_COMMAND = "DELETELIN";
	private static final String VIEWLIN_COMMAND = "VIEWLIN";

	private JTabbedPane mainPanel;
	//private AdvancedSelectPanel asp = null;
	private JPanel suportSp = new JPanel(new BorderLayout());
	//private AdvancedSelectPanel aspRep = null;
	private JPanel suportSpRep = new JPanel(new BorderLayout());
	//private AdvancedSelectPanel aspRepDetail = null;
	private JPanel suportSpRepDetail = new JPanel(new BorderLayout());
	//private AdvancedSelectPanel aspLin = null;
	private JPanel suportSpLin = new JPanel(new BorderLayout());
	//private AdvancedSelectPanel aspLinDetail = null;
	private JPanel suportSpLinDetail = new JPanel(new BorderLayout());
	
	private int nOutput=0;
	private int nOutputRep=0;
	private int nOutputLin=0;
	private int nOutputRepDetail=0;
	private int nOutputLinDetail=0;
	private JLabel nOutputLabel=new JLabel();
	private JLabel nOutputRepLabel=new JLabel();
	private JLabel nOutputLinLabel=new JLabel();
	private JLabel nOutputRepDetailLabel=new JLabel();
	private JLabel nOutputLinDetailLabel=new JLabel();
	
	private int maxUniqueIdOutput=0;
	private int maxUniqueIdOutputRep=0;
	private int maxUniqueIdOutputLin=0;
	
	private Connection radqcdbcon = null;
	private DatabaseAgentSupport dbagent;
	private DatabaseAgentSupport repdbagent;	
	private DatabaseAgentSupport repdetaildbagent;
	private DatabaseAgentSupport lindbagent;
	private DatabaseAgentSupport lindetaildbagent;
	private JComboBox<String> reporderbyCb;
	private JComboBox<String> orderbyCb;
	private JComboBox<String> linorderbyCb;
	private final Dimension sizeOrderCb = new Dimension(200, 21);

	/**
	 * Constructor
	 * @param mf the OutputFrame object
	 */
	public SaveViewDBFrame(OutputFrame mf){
		this.mf=mf;
		this.setTitle(mf.resources.getString("SaveViewDBFrame.NAME"));
		//==========================================
		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = mf.resources.getString("data.load");// "Data";
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		opens = opens + file_sep + mf.mainDB;
    	radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
    	
    	dbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID", mf.outputTable);
    	repdbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID", mf.outputRepeatabilityTable);
    	repdetaildbagent = new DatabaseAgentSupport(radqcdbcon,"ID", mf.outputRepeatabilityTableDetail);
    	lindbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID", mf.outputLinearityTable);
    	lindetaildbagent = new DatabaseAgentSupport(radqcdbcon,"ID", mf.outputLinearityTableDetail);
    	dbagent.setHasValidAIColumn(false);
    	repdbagent.setHasValidAIColumn(false);
    	repdetaildbagent.setHasValidAIColumn(false);
    	lindbagent.setHasValidAIColumn(false);
    	lindetaildbagent.setHasValidAIColumn(false);
    	//-------now dummy initialization of agents just to create combobox, label and tables!!!
    	dbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
    	dbagent.init();
    	repdbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
    	repdbagent.init();
    	repdetaildbagent.setLinks("ID", Convertor.intToString(1));
    	repdetaildbagent.init();
    	lindbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
    	lindbagent.init();
    	lindetaildbagent.setLinks("ID", Convertor.intToString(1));
    	lindetaildbagent.init();
		//==============================================
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
			String s = "select * from " + mf.outputTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			dbagent.select(s);

			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);
			JTable mainTable = dbagent.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp.add(scrollPane, BorderLayout.CENTER);

			///JTable mainTable = asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			nOutput = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutput=Convertor.stringToInt(maxUniqueIDs);								
			}		
			
			//Rep
			s = "select * from " + mf.outputRepeatabilityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			repdbagent.select(s);

			//aspRep = new AdvancedSelectPanel();
			//suportSpRep.add(aspRep, BorderLayout.CENTER);
			mainTable = repdbagent.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane1 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSpRep.add(scrollPane1, BorderLayout.CENTER);

			//mainTable = aspRep.getTab();

			ListSelectionModel rowSMt = mainTable.getSelectionModel();
			rowSMt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSMt.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();//fired each time a row is selected in main table
				}
			});
			
			nOutputRep = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutputRep=Convertor.stringToInt(maxUniqueIDs);								
			} 

			//detail
			s = "select * from " + mf.outputRepeatabilityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueIdOutputRep+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			repdetaildbagent.select(s);
			//aspRepDetail = new AdvancedSelectPanel();
			//suportSpRepDetail.add(aspRepDetail, BorderLayout.CENTER);
			mainTable = repdetaildbagent.getMainTable();
			JScrollPane scrollPane2 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSpRepDetail.add(scrollPane2, BorderLayout.CENTER);
			//mainTable = aspRepDetail.getTab();
			nOutputRepDetail = mainTable.getRowCount();
			//-----------------------
			//Lin
			s = "select * from " + mf.outputLinearityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			lindbagent.select(s);

			//aspLin = new AdvancedSelectPanel();
			//suportSpLin.add(aspLin, BorderLayout.CENTER);
			mainTable = lindbagent.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane3 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSpLin.add(scrollPane3, BorderLayout.CENTER);

			//mainTable = aspLin.getTab();

			ListSelectionModel rowSMlin = mainTable.getSelectionModel();
			rowSMlin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSMlin.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTableLin();//fired each time a row is selected in main table
				}
			});
			
			nOutputLin = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutputLin=Convertor.stringToInt(maxUniqueIDs);								
			} 

			//detail
			s = "select * from " + mf.outputLinearityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueIdOutputLin+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			lindetaildbagent.select(s);
			
			//aspLinDetail = new AdvancedSelectPanel();
			//suportSpLinDetail.add(aspLinDetail, BorderLayout.CENTER);
			mainTable = lindetaildbagent.getMainTable();//aspRep.getTab();
			JScrollPane scrollPane4 = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSpLinDetail.add(scrollPane4, BorderLayout.CENTER);
			
			//mainTable = aspLinDetail.getTab();
			nOutputLinDetail = mainTable.getRowCount();
			
			//if (con1 != null)
				//con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update nested table (output repeatability)
	 */
	private void updateDetailTable() {
	//System.out.println("update kv detail fired");//nope
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		JTable aspTable = repdbagent.getMainTable();//aspRep.getTab();
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
			String s = "select * from " + mf.outputRepeatabilityTable+ " where IDLINK = "+selID +" order by Unique_ID";
			//nothing here
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			//=====================
			// now the job:
			s = "select * from " + mf.outputRepeatabilityTableDetail + 
			" where IDLINK = "+ selID + 
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";
			// IF press header=>selRow=-1=>ID=0=>NO ZERO ID DATA=>
			// so display an empty table!
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			repdetaildbagent.select(s);

			//if (aspRepDetail != null)
				//suportSpRepDetail.remove(aspRepDetail);
			//aspRepDetail = new AdvancedSelectPanel();
			//suportSpRepDetail.add(aspRepDetail, BorderLayout.CENTER);

			//if (aspRepDetail != null){				
				JTable mainTab=repdetaildbagent.getMainTable();//aspRepDetail.getTab();
				nOutputRepDetail = mainTab.getRowCount();
				String str=Convertor.intToString(nOutputRepDetail);
				nOutputRepDetailLabel.setText(str);
			//}
			
			//if (con1 != null)
				//con1.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//validate();
	}
	
	/**
	 * Update nested table (mAs linearity)
	 */
	private void updateDetailTableLin() {
		//System.out.println("enter from start?");//nope
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		JTable aspTable = lindbagent.getMainTable();//aspLin.getTab();//TIME
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
			String s = "select * from " + mf.outputLinearityTable+ " where IDLINK = "+selID +" order by Unique_ID";
			//NOTHING HERE
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			//=====================
			// now the job:
			s = "select * from " + mf.outputLinearityTableDetail + 
			" where IDLINK = "+ selID + 
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";
			// IF press header=>selRow=-1=>ID=0=>NO ZERO ID DATA=>
			// so display an empty table!
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			lindetaildbagent.select(s);

			//if (aspLinDetail != null)
				//suportSpLinDetail.remove(aspLinDetail);
			//aspLinDetail = new AdvancedSelectPanel();
			//suportSpLinDetail.add(aspLinDetail, BorderLayout.CENTER);

			//if (aspLinDetail != null){				
				JTable mainTab=lindetaildbagent.getMainTable();//aspLinDetail.getTab();
				nOutputLinDetail = mainTab.getRowCount();
				String str=Convertor.intToString(nOutputLinDetail);
				nOutputLinDetailLabel.setText(str);
			//}

			//if (con1 != null)
				//con1.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		validate();
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
		//===========================
		orderbyCb = dbagent.getOrderByComboBox();
		orderbyCb.setMaximumRowCount(5);
		orderbyCb.setPreferredSize(sizeOrderCb);
		orderbyCb.addItemListener(this);
		JPanel orderP0 = new JPanel();
		orderP0.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP0.add(label);
		orderP0.add(orderbyCb);
		orderP0.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP0.add(label);
		orderP0.add(dbagent.getRecordsLabel());
		
		reporderbyCb = repdbagent.getOrderByComboBox();
		reporderbyCb.setMaximumRowCount(5);
		reporderbyCb.setPreferredSize(sizeOrderCb);
		reporderbyCb.addItemListener(this);
		JPanel orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(reporderbyCb);
		orderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(repdbagent.getRecordsLabel());
				
		JPanel dorderP = new JPanel();
		dorderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderP.add(label);
		dorderP.add(repdetaildbagent.getRecordsLabel());
		
		linorderbyCb = lindbagent.getOrderByComboBox();
		linorderbyCb.setMaximumRowCount(5);
		linorderbyCb.setPreferredSize(sizeOrderCb);
		linorderbyCb.addItemListener(this);
		JPanel orderP2 = new JPanel();
		orderP2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP2.add(label);
		orderP2.add(linorderbyCb);
		orderP2.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP2.add(label);
		orderP2.add(lindbagent.getRecordsLabel());
				
		JPanel dorderP2 = new JPanel();
		dorderP2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderP2.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		dorderP2.add(label);
		dorderP2.add(lindetaildbagent.getRecordsLabel());
		//============================
		
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
		outP.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("output.border"),
				RadQCFrame.foreColor));
		outP.add(suportSp);
		outP.add(orderP0);//outP.add(p4P);
		outP.add(p7P);
		outP.setBackground(RadQCFrame.bkgColor);
		
		//Rep
		suportSpRep.setPreferredSize(tableDimension);
		
		JPanel p4Pr=new JPanel();
		p4Pr.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4Pr.add(label);
		str=Convertor.intToString(nOutputRep);
		nOutputRepLabel.setText(str);// = new JLabel(str);
		nOutputRepLabel.setForeground(RadQCFrame.foreColor);
		p4Pr.add(nOutputRepLabel);
		p4Pr.setBackground(RadQCFrame.bkgColor);
		
		JPanel p7Pr=new JPanel();
		p7Pr.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));		
		buttonName = mf.resources.getString("save.saveB");
		buttonToolTip = mf.resources.getString("save.saveB.toolTip");
		buttonIconName = mf.resources.getString("img.save.database");
		button = FrameUtilities.makeButton(buttonIconName, SAVEREP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pr.add(button);		
		buttonName = mf.resources.getString("save.deleteB");
		buttonToolTip = mf.resources.getString("save.deleteB.toolTip");
		buttonIconName = mf.resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETEREP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pr.add(button);		
		buttonName = mf.resources.getString("save.viewB");
		buttonToolTip = mf.resources.getString("save.viewB.toolTip");
		buttonIconName = mf.resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEWREP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.viewB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pr.add(button);
		p7Pr.setBackground(RadQCFrame.bkgColor);
		
		JPanel outPr = new JPanel();
		BoxLayout blkvRPr = new BoxLayout(outPr, BoxLayout.Y_AXIS);
		outPr.setLayout(blkvRPr);
		outPr.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("rep.border"),
				RadQCFrame.foreColor));
		outPr.add(suportSpRep);
		outPr.add(orderP);//////////////////////////outPr.add(p4Pr);
		outPr.add(p7Pr);
		outPr.setBackground(RadQCFrame.bkgColor);
		
		//detail
		suportSpRepDetail.setPreferredSize(tableDimension);
		
		JPanel p4Prd=new JPanel();
		p4Prd.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4Prd.add(label);
		str=Convertor.intToString(nOutputRepDetail);
		nOutputRepDetailLabel.setText(str);// = new JLabel(str);
		nOutputRepDetailLabel.setForeground(RadQCFrame.foreColor);
		p4Prd.add(nOutputRepDetailLabel);
		p4Prd.setBackground(RadQCFrame.bkgColor);
		
		JPanel kvRPdt = new JPanel();
		BoxLayout blkvRPdt = new BoxLayout(kvRPdt, BoxLayout.Y_AXIS);
		kvRPdt.setLayout(blkvRPdt);
		kvRPdt.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("details.border"),
				RadQCFrame.foreColor));
		kvRPdt.add(suportSpRepDetail);
		kvRPdt.add(dorderP);	/////////////////kvRPdt.add(p4Prd);		
		kvRPdt.setBackground(RadQCFrame.bkgColor);
		//============all rep.
		JPanel outRall = new JPanel();
		BoxLayout bloutRall = new BoxLayout(outRall, BoxLayout.Y_AXIS);
		outRall.setLayout(bloutRall);
		outRall.add(outPr);
		outRall.add(kvRPdt);
		outRall.setBackground(RadQCFrame.bkgColor);
		
		//LIN
		suportSpLin.setPreferredSize(tableDimension);
		
		JPanel p4Pl=new JPanel();
		p4Pl.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4Pl.add(label);
		str=Convertor.intToString(nOutputLin);
		nOutputLinLabel.setText(str);// = new JLabel(str);
		nOutputLinLabel.setForeground(RadQCFrame.foreColor);
		p4Pl.add(nOutputLinLabel);
		p4Pl.setBackground(RadQCFrame.bkgColor);
		
		JPanel p7Pl=new JPanel();
		p7Pl.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));		
		buttonName = mf.resources.getString("save.saveB");
		buttonToolTip = mf.resources.getString("save.saveB.toolTip");
		buttonIconName = mf.resources.getString("img.save.database");
		button = FrameUtilities.makeButton(buttonIconName, SAVELIN_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pl.add(button);		
		buttonName = mf.resources.getString("save.deleteB");
		buttonToolTip = mf.resources.getString("save.deleteB.toolTip");
		buttonIconName = mf.resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETELIN_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pl.add(button);		
		buttonName = mf.resources.getString("save.viewB");
		buttonToolTip = mf.resources.getString("save.viewB.toolTip");
		buttonIconName = mf.resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEWLIN_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("save.viewB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p7Pl.add(button);
		p7Pl.setBackground(RadQCFrame.bkgColor);
		
		JPanel outPl = new JPanel();
		BoxLayout bloutPl = new BoxLayout(outPl, BoxLayout.Y_AXIS);
		outPl.setLayout(bloutPl);
		outPl.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("lin.border"),
				RadQCFrame.foreColor));
		outPl.add(suportSpLin);
		outPl.add(orderP2);//////////////////outPl.add(p4Pl);
		outPl.add(p7Pl);
		outPl.setBackground(RadQCFrame.bkgColor);
		
		//detail
		suportSpLinDetail.setPreferredSize(tableDimension);
		
		JPanel p4Pld=new JPanel();
		p4Pld.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(mf.resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4Pld.add(label);
		str=Convertor.intToString(nOutputLinDetail);
		nOutputLinDetailLabel.setText(str);// = new JLabel(str);
		nOutputLinDetailLabel.setForeground(RadQCFrame.foreColor);
		p4Pld.add(nOutputLinDetailLabel);
		p4Pld.setBackground(RadQCFrame.bkgColor);
		
		JPanel outLd = new JPanel();
		BoxLayout bloutLd = new BoxLayout(outLd, BoxLayout.Y_AXIS);
		outLd.setLayout(bloutLd);
		outLd.setBorder(FrameUtilities.getGroupBoxBorder(
				mf.resources.getString("details.border"),
				RadQCFrame.foreColor));
		outLd.add(suportSpLinDetail);
		outLd.add(dorderP2);////////////////////outLd.add(p4Pld);		
		outLd.setBackground(RadQCFrame.bkgColor);
		//============all rep.
		JPanel outLall = new JPanel();
		BoxLayout bloutLall = new BoxLayout(outLall, BoxLayout.Y_AXIS);
		outLall.setLayout(bloutLall);
		outLall.add(outPl);
		outLall.add(outLd);
		outLall.setBackground(RadQCFrame.bkgColor);
		
		JTabbedPane jtab = new JTabbedPane();
		jtab.add(outP,  mf.resources.getString("output.tab.title"));
		jtab.add(outRall,  mf.resources.getString("reprod.tab.title"));
		jtab.add(outLall,  mf.resources.getString("linearity.tab.title"));
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
		} else if (command.equals(VIEW_COMMAND)) {
			view();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
		} else if (command.equals(SAVEREP_COMMAND)) {
			saveRep();
		} else if (command.equals(VIEWREP_COMMAND)) {
			viewRep();
		} else if (command.equals(DELETEREP_COMMAND)) {
			deleteRep();
		} else if (command.equals(SAVELIN_COMMAND)) {
			saveLin();
		} else if (command.equals(VIEWLIN_COMMAND)) {
			viewLin();
		} else if (command.equals(DELETELIN_COMMAND)) {
			deleteLin();
		} 
	}
	
	/**
	 * JCombobox actions are set here
	 */
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == reporderbyCb) {
			repsort();
		} else if (e.getSource() == orderbyCb) {
			sort();
		} else if (e.getSource() == linorderbyCb) {
			linsort();
		} 
	}
	
	/**
	 * Sorts data from output table
	 */
	private void sort() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCb.getSelectedItem();
		dbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagent.performSelection(orderbyS);
	}
	
	/**
	 * Sorts data from repeatability table
	 */
	private void repsort() {
		//System.out.println("sort");
		String orderbyS = (String) reporderbyCb.getSelectedItem();
		repdbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		repdbagent.performSelection(orderbyS);
	}
	
	/**
	 * Sorts data from mAs linearity table
	 */
	private void linsort() {
		String orderbyS = (String) linorderbyCb.getSelectedItem();
		lindbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		lindbagent.performSelection(orderbyS);
	}
	
	/**
	 * Save tube output QC test results
	 */
	private void save(){
		if (!mf.isOktoSaveOutput){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.output.error.message"),
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
						
			//int n = mf.cvkvacd.length;//mf.kvd.length;//cpsV.size();
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ mf.outputTable + " values " + "(?, ?, ?, ?, ?, ?)");
			//for (int i = 0; i < n; i++) {				
				int id = maxUniqueIdOutput + 1;//Unique ID
				psInsert.setString(1, Convertor.intToString(id));
				psInsert.setString(2, Convertor.intToString(mf.IDLINK));
				psInsert.setString(3, mf.measurementDate_toSave);
				psInsert.setString(4, Convertor.doubleToString(mf.output));
				psInsert.setString(5, Convertor.doubleToString(mf.maxOutput));
				if (mf.resultOutput)
	            	resultS=mf.resources.getString("reprod.rezultat.succes");
	            else
					resultS=mf.resources.getString("reprod.rezultat.fail");			
				psInsert.setString(6, resultS);									
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
	 * Select data from output table
	 */
	private void selectTable(){
		
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.outputTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

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
						
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(nOutput);
			nOutputLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
		
	}
	
	/**
	 * View all data from output table
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
			String s = "select * from " + mf.outputTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
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
				
				//Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//maxUniqueID=Convertor.stringToInt(maxUniqueIDs);//not here in view all!!!								
			} //else
				//maxUniqueID=0;//reset counter
						
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(nOutput);
			nOutputLabel.setText(str);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection	
	}

	/**
	 * Delete an entry from output table
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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.outputTable);//deviceTable);
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
	 * Save repeatability QC test results
	 */
	private void saveRep(){
		
		if (!mf.isOktoSaveOutputReapeatability){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.rep.error.message"),
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
					+ mf.outputRepeatabilityTable + " values " + "(?, ?, ?, ?, ?, ?)");
			int id = maxUniqueIdOutputRep + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, Convertor.intToString(mf.IDLINK));
			psInsert.setString(3, mf.measurementDate_toSave);
			psInsert.setString(4, Convertor.doubleToString(mf.cvRepeatability));
			psInsert.setString(5, Convertor.doubleToString(mf.maxcvRepeatability));
			if (mf.resultRepeatability)
            	resultS=mf.resources.getString("reprod.rezultat.succes");
            else
				resultS=mf.resources.getString("reprod.rezultat.fail");			
			psInsert.setString(6, resultS);									
			psInsert.executeUpdate();
			//------------------------
			
			int n = mf.repeatabilityd.length;
			psInsert = radqcdbcon.prepareStatement("insert into " + mf.outputRepeatabilityTableDetail//con1.prepareStatement("insert into " + mf.outputRepeatabilityTableDetail
					+ " values " + "(?, ?, ?, ?)");
			for (int i = 0; i < n; i++) {
				int nrcrt = i + 1;
				psInsert.setString(1, Convertor.intToString(nrcrt));
				psInsert.setString(2, Convertor.intToString(id));///NOT IDLINK
				psInsert.setString(3, Convertor.intToString(mf.IDLINK));				
				psInsert.setString(4,
						Convertor.doubleToString(mf.repeatabilityd[i]));
				
				psInsert.executeUpdate();
			}
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
						
			selectTableRep();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}	
		 
	}
	
	/**
	 * Select data from repeatability table
	 */
	private void selectTableRep(){
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.outputRepeatabilityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			repdbagent.select(s);
			
			//suportSpRep.remove(aspRep);//remove first
			//aspRep = new AdvancedSelectPanel();
			//suportSpRep.add(aspRep, BorderLayout.CENTER);

			JTable mainTable = repdbagent.getMainTable();//aspRep.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();//fired each time a row is selected in main table
				}
			});
			
			nOutputRep = mainTable.getRowCount();
						
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutputRep=Convertor.stringToInt(maxUniqueIDs);
								
			} else {
				maxUniqueIdOutputRep=0;//reset counter
			}
			
			//detail
			s = "select * from " + mf.outputRepeatabilityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueIdOutputRep+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			repdetaildbagent.select(s);
			
			//suportSpRepDetail.remove(aspRepDetail);//remove first
			//aspRepDetail = new AdvancedSelectPanel();
			//suportSpRepDetail.add(aspRepDetail, BorderLayout.CENTER);
			
			mainTable = repdetaildbagent.getMainTable();//aspRepDetail.getTab();
			nOutputRepDetail = mainTable.getRowCount();
			
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(nOutputRep);
			nOutputRepLabel.setText(str);
			str=Convertor.intToString(nOutputRepDetail);
			nOutputRepDetailLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
	}
	
	/**
	 * View all data from repeatability table
	 */
	private void viewRep(){
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.outputRepeatabilityTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			repdbagent.select(s);

			//suportSpRep.remove(aspRep);//remove first
			//aspRep = new AdvancedSelectPanel();
			//suportSpRep.add(aspRep, BorderLayout.CENTER);

			JTable mainTable = repdbagent.getMainTable();//aspRep.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();//fired each time a row is selected in main table
				}
			});
			
			nOutputRep = mainTable.getRowCount();
								
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
			int selUID = (Integer) mainTable.getValueAt(nOutputRep-1, 0);//column 1, Unique_ID
			int selID = (Integer) mainTable.getValueAt(nOutputRep-1, 1);//column 2, LINK_ID
			
			s = "select * from " + mf.outputRepeatabilityTableDetail + 
			" where IDLINK = "+ selID + 
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			repdetaildbagent.select(s);
			
			//suportSpRepDetail.remove(aspRepDetail);//remove first
			//aspRepDetail = new AdvancedSelectPanel();
			//suportSpRepDetail.add(aspRepDetail, BorderLayout.CENTER);
			
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(nOutputRep);
			nOutputRepLabel.setText(str);
			
			mainTable = repdetaildbagent.getMainTable();//aspRepDetail.getTab();
			nOutputRepDetail = mainTable.getRowCount();
			str=Convertor.intToString(nOutputRepDetail);
			nOutputRepDetailLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection
	}

	/**
	 * Delete an entry from repeatability table
	 */
	private void deleteRep(){
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = repdbagent.getMainTable();//aspRep.getTab();

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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.outputRepeatabilityTable);//deviceTable);
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
			ResultSet res1 = s.executeQuery("SELECT * FROM " + mf.outputRepeatabilityTableDetail);//deviceTable);
			while (res1.next()) {
				int id = res1.getInt("Unique_ID");//("ID");
				int id2 = res1.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res1.deleteRow();
				} 
			}
			//===============================================
			selectTableRep();
			
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
	 * Save mAs linearity (or output linearity) QC test results
	 */
	private void saveLin(){
		if (!mf.isOktoSavemAsLinearity){
			JOptionPane.showMessageDialog(this,
					mf.resources.getString("nosave.lin.error.message"),
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
					+ mf.outputLinearityTable + " values " + "(?, ?, ?, ?, ?, ?)");
			int id = maxUniqueIdOutputLin + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, Convertor.intToString(mf.IDLINK));
			psInsert.setString(3, mf.measurementDate_toSave);
			psInsert.setString(4, Convertor.doubleToString(mf.cvLinearity));
			psInsert.setString(5, Convertor.doubleToString(mf.maxcvLinearity));
			if (mf.resultLinearity)
            	resultS=mf.resources.getString("reprod.rezultat.succes");
            else
				resultS=mf.resources.getString("reprod.rezultat.fail");			
			psInsert.setString(6, resultS);									
			psInsert.executeUpdate();
			//------------------------
			
			int n = mf.randd.length;//cpsV.size();
			psInsert = radqcdbcon.prepareStatement("insert into " + mf.outputLinearityTableDetail//con1.prepareStatement("insert into " + mf.outputLinearityTableDetail
					+ " values " + "(?, ?, ?, ?, ?, ?)");
			for (int i = 0; i < n; i++) {
				int nrcrt = i + 1;
				psInsert.setString(1, Convertor.intToString(nrcrt));
				psInsert.setString(2, Convertor.intToString(id));///NOT IDLINK
				psInsert.setString(3, Convertor.intToString(mf.IDLINK));
				psInsert.setString(4,
						Convertor.doubleToString(mf.masd[i]));
				psInsert.setString(5,
						Convertor.doubleToString(mf.expd[i]));
				psInsert.setString(6,
						Convertor.doubleToString(mf.randd[i]));
				psInsert.executeUpdate();
			}
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
						
			selectTableLin();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Select data from linearity table
	 */
	private void selectTableLin(){
		//String datas = mf.resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mf.mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + mf.outputLinearityTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			lindbagent.select(s);
			
			//suportSpLin.remove(aspLin);//remove first
			//aspLin = new AdvancedSelectPanel();
			//suportSpLin.add(aspLin, BorderLayout.CENTER);

			JTable mainTable = lindbagent.getMainTable();//aspLin.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTableLin();//fired each time a row is selected in main table
				}
			});
			
			nOutputLin = mainTable.getRowCount();
						
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutputLin=Convertor.stringToInt(maxUniqueIDs);
								
			} else {
				maxUniqueIdOutputLin=0;//reset counter
			}
			
			//detail
			s = "select * from " + mf.outputLinearityTableDetail + 
			" where IDLINK = "+ mf.IDLINK + 
			" and Unique_id = "+maxUniqueIdOutputLin+" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			lindetaildbagent.select(s);
			
			//suportSpLinDetail.remove(aspLinDetail);//remove first
			//aspLinDetail = new AdvancedSelectPanel();
			//suportSpLinDetail.add(aspLinDetail, BorderLayout.CENTER);
			
			mainTable = lindetaildbagent.getMainTable();//aspLinDetail.getTab();
			nOutputLinDetail = mainTable.getRowCount();
			
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(nOutputLin);
			nOutputLinLabel.setText(str);
			str=Convertor.intToString(nOutputLinDetail);
			nOutputLinDetailLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
	}
	
	/**
	 * View all data from linearity table
	 */
	private void viewLin(){
		try {
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + mf.outputLinearityTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			
			lindbagent.select(s);

			//suportSpLin.remove(aspLin);//remove first
			//aspLin = new AdvancedSelectPanel();
			//suportSpLin.add(aspLin, BorderLayout.CENTER);

			JTable mainTable = lindbagent.getMainTable();//aspLin.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTableLin();//fired each time a row is selected in main table
				}
			});
			
			nOutputLin = mainTable.getRowCount();
								
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
			int selUID = (Integer) mainTable.getValueAt(nOutputLin-1, 0);//column 1, Unique_ID
			int selID = (Integer) mainTable.getValueAt(nOutputLin-1, 1);//column 2, LINK_ID
			
			s = "select * from " + mf.outputLinearityTableDetail + 
			" where IDLINK = "+ selID + 
			" and Unique_ID = "+ selUID +
			" ORDER BY ID";			
			//con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);//here is the link to asp creation
			
			lindetaildbagent.select(s);
			
			//suportSpLinDetail.remove(aspLinDetail);//remove first
			//aspLinDetail = new AdvancedSelectPanel();
			//suportSpLinDetail.add(aspLinDetail, BorderLayout.CENTER);
			
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(nOutputLin);
			nOutputLinLabel.setText(str);
			
			mainTable = lindetaildbagent.getMainTable();//aspLinDetail.getTab();
			nOutputLinDetail = mainTable.getRowCount();
			str=Convertor.intToString(nOutputLinDetail);
			nOutputLinDetailLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection
	}

	/**
	 * Delete an entry from linearity table
	 */
	private void deleteLin(){
		try {
			// prepare db query data
			//String datas = mf.resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mf.mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = lindbagent.getMainTable();//aspLin.getTab();

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
			ResultSet res = s.executeQuery("SELECT * FROM " + mf.outputLinearityTable);//deviceTable);
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
			ResultSet res1 = s.executeQuery("SELECT * FROM " + mf.outputLinearityTableDetail);//deviceTable);
			while (res1.next()) {
				int id = res1.getInt("Unique_ID");//("ID");
				int id2 = res1.getInt("IDLINK");//("ID");
				if (id == selID && id2==selID2) {
					res1.deleteRow();
				} 
			}
			//===============================================
			selectTableLin();
			
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
