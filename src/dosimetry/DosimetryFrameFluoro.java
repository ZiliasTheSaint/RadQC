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
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import danfulea.db.DatabaseAgent;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.math.StatsUtil;
import danfulea.utils.FrameUtilities;
import radQC.RadQCFrame;

/**
 * Dosimetry for fluoroscopy. Due to irregular examination procedure, there is no 
 * Monte Carlo implementation for this. The doses here are measurable free in air. There is 
 * however a possibility to perform Monte Carlo computation of absorbed dose, effective dose 
 * and cancer risk by transforming fluoroscopy examination to a VIRTUAL series of successive 
 * radioghraphic examinations and then cumulate the results.   
 * 
 * @author Dan Fulea, 06 May 2015
 */
@SuppressWarnings("serial")
public class DosimetryFrameFluoro extends JFrame implements ActionListener, ItemListener{
	private static final Dimension PREFERRED_SIZE = new Dimension(700, 720);
	//private static final Dimension textAreaDimension = new Dimension(700, 100);
	private final Dimension tableDimension = new Dimension(700, 200);
	private static final String BASE_RESOURCE_CLASS = "dosimetry.resources.DosimetryFrameResources";
	protected ResourceBundle resources;
	private RadQCFrame mf;
	
	protected String dosimetryTable="";
	protected String measurementDate_toSave="";
	protected String mainDB = "";
	protected int IDLINK=0;
	
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private static final String SAVE_COMMAND = "SAVE";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String VIEW_COMMAND = "VIEW";
	private String command = null;
	
	//private AdvancedSelectPanel asp = null;
	private JPanel suportSp = new JPanel(new BorderLayout());
	private int nOutput=0;
	private JLabel nOutputLabel=new JLabel();
	
	private JRadioButton mrRb, mradRb, mgyRb, secRb, minRb, oraRb;
	private JTextField doseRateTf=new JTextField(10);
	private JTextField maxPermissibleDoseRateTf=new JTextField(5);
	private JTextField dosimeterreadTf=new JTextField(5);
	private JTextField estimatedMeasurementUncertaintyTf=new JTextField(5);
	
	private int maxUniqueIdOutput=0;
	
	protected boolean isOkToSave=false;
	
	private Connection radqcdbcon = null;
	private DatabaseAgentSupport fluorodbagent;
	private JComboBox<String> fluoroorderbyCb;
	private final Dimension sizeOrderCb = new Dimension(200, 21);
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public DosimetryFrameFluoro (RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("DosimetryFrameFluoro.NAME"));
		//DBConnection.startDerby();//just in case is closed
		//===============
		mainDB=mf.radqcDB;
		dosimetryTable=mf.dosimetryTable;		
    	measurementDate_toSave=mf.measurementDate;
    	IDLINK=mf.IDLINK;
    	//=========================
    	DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		fluorodbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID",
				dosimetryTable);
		fluorodbagent.setHasValidAIColumn(false);
		//-------now dummy initialization of agents just to create combobox, label and tables!!!
		fluorodbagent.setLinks("UNIQUE_ID", Convertor.intToString(1));
		fluorodbagent.init();
    	//============================
    	performQueryDB();//save is here!!!
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
		
		//Create the toolbar.
        //JToolBar toolBar = new JToolBar();
        //toolBar.setFloatable(false);
        //addButtons(toolBar);
                
		JPanel mainPanel = createMainPanel();
		
		//content.add(toolBar, BorderLayout.PAGE_START);
		content.add(mainPanel, BorderLayout.CENTER);
		//content.add(statusBar, BorderLayout.PAGE_END);

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
		
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
		
		fluoroorderbyCb = fluorodbagent.getOrderByComboBox();
		fluoroorderbyCb.setMaximumRowCount(5);
		fluoroorderbyCb.setPreferredSize(sizeOrderCb);
		fluoroorderbyCb.addItemListener(this);
		JPanel orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(mf.resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(fluoroorderbyCb);
		orderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(mf.resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(fluorodbagent.getRecordsLabel());
		
		estimatedMeasurementUncertaintyTf.setText("5");
	    JPanel puncP=new JPanel();
		puncP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("expData.unc"));
        puncP.add(label);
        puncP.add(estimatedMeasurementUncertaintyTf);        
        puncP.setBackground(RadQCFrame.bkgColor);
        
		mrRb=new JRadioButton("mR");
		mradRb=new JRadioButton("mRad");
		mgyRb=new JRadioButton("mGy");
		secRb=new JRadioButton("sec");
		minRb=new JRadioButton("min");
		oraRb=new JRadioButton("h");
		
		ButtonGroup group = new ButtonGroup();
		group.add(mrRb);
		group.add(mradRb);
		group.add(mgyRb);
	    ButtonGroup group1 = new ButtonGroup();
		group1.add(secRb);
		group1.add(minRb);
		group1.add(oraRb);
		
		JPanel d0P= new JPanel();
		d0P.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    label=new JLabel(resources.getString("fluoro.dosimeter.read.label"));
	    label.setForeground(RadQCFrame.foreColor);
	    d0P.add(label);
	    d0P.add(dosimeterreadTf);
		d0P.setBackground(RadQCFrame.bkgColor);
		
		JPanel buttP = new JPanel();
		buttP.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
		buttP.add(mrRb);
		buttP.add(mradRb);
		buttP.add(mgyRb);
		mrRb.setBackground(RadQCFrame.bkgColor);
		mradRb.setBackground(RadQCFrame.bkgColor);
		mgyRb.setBackground(RadQCFrame.bkgColor);
		buttP.setBackground(RadQCFrame.bkgColor);
		mradRb.setSelected(true);
		
		JPanel butt1P = new JPanel();
		butt1P.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
		butt1P.add(secRb);
		butt1P.add(minRb);
		butt1P.add(oraRb);
		secRb.setBackground(RadQCFrame.bkgColor);
		minRb.setBackground(RadQCFrame.bkgColor);
		oraRb.setBackground(RadQCFrame.bkgColor);
		butt1P.setBackground(RadQCFrame.bkgColor);
		minRb.setSelected(true);
		
		JPanel d3P=new JPanel();
	    d3P.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    buttonName = resources.getString("calcB");
		buttonToolTip = resources.getString("calcB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		d3P.add(button);
		label = new JLabel(resources.getString("fluoro.doseRate.label"));
		label.setForeground(RadQCFrame.foreColor);
		d3P.add(label);
		d3P.add(doseRateTf);
	    d3P.setBackground(RadQCFrame.bkgColor);
	    
		maxPermissibleDoseRateTf.setText("100");
		
		/*JPanel p1P=new JPanel();
		p1P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(resources.getString("fluoro.doseRate.label"));
		label.setForeground(RadQCFrame.foreColor);
		p1P.add(label);
		p1P.add(doseRateTf);		
		p1P.setBackground(RadQCFrame.bkgColor);*/
		
		JPanel p2P=new JPanel();
		p2P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(resources.getString("fluoro.max.doseRate.label"));
		label.setForeground(RadQCFrame.foreColor);
		p2P.add(label);
		p2P.add(maxPermissibleDoseRateTf);		
		p2P.setBackground(RadQCFrame.bkgColor);
		
		JPanel northP=new JPanel();
	    BoxLayout bl11 = new BoxLayout(northP,BoxLayout.Y_AXIS);
	    northP.setLayout(bl11);
	    northP.add(d0P, null);
	    northP.add(buttP, null);
	    northP.add(butt1P, null);
	    northP.add(d3P, null);
	    northP.add(p2P, null);
	    northP.add(puncP, null);
		//============================================
		suportSp.setPreferredSize(tableDimension);
		
		JPanel p4P=new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4P.add(label);
		String str=Convertor.intToString(nOutput);
		nOutputLabel.setText(str);// = new JLabel(str);
		nOutputLabel.setForeground(RadQCFrame.foreColor);
		p4P.add(nOutputLabel);
		p4P.setBackground(RadQCFrame.bkgColor);
				
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
		buttonToolTip = resources.getString("save.viewB.toolTip");
		buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, VIEW_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("save.viewB.mnemonic");
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
		outP.add(orderP);////////////////////////////////outP.add(p4P);
		outP.add(p7P);
		outP.setBackground(RadQCFrame.bkgColor);
		
		JPanel mainPanel=new JPanel(new BorderLayout());
		mainPanel.add(northP, BorderLayout.NORTH);//CENTER);
		//mainPanel.add(resultP, BorderLayout.CENTER);
		mainPanel.add(outP, BorderLayout.CENTER);
		mainPanel.setBackground(RadQCFrame.bkgColor);
		return mainPanel;
	}
	
	/**
	 * Initialize database
	 */
	private void performQueryDB(){
		String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		
		try {
						
			//Rep
			String s = "select * from " + dosimetryTable+" where IDLINK = "+IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			fluorodbagent.select(s);
			//asp = new AdvancedSelectPanel();
			JTable mainTable = fluorodbagent.getMainTable();
			JScrollPane scrollPane = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp.add(scrollPane, BorderLayout.CENTER);
			//suportSp.add(asp, BorderLayout.CENTER);

			//JTable mainTable = asp.getTab();

			ListSelectionModel rowSMt = mainTable.getSelectionModel();
			rowSMt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			nOutput = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueIdOutput=Convertor.stringToInt(maxUniqueIDs);								
			} 
			
			//if (con1 != null)
				//con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Most actions are performed here
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		command = e.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {			
			compute();
		} else if (command.equals(SAVE_COMMAND)){
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
		if (e.getSource() == fluoroorderbyCb) {
			fluorosort();
		} 
	}
	
	/**
	 * Sorts data from fluoro table
	 */
	private void fluorosort() {
		//System.out.println("sort");
		String orderbyS = (String) fluoroorderbyCb.getSelectedItem();
		fluorodbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		fluorodbagent.performSelection(orderbyS);
	}
	
	/**
	 * Perform computation
	 */
	private void compute(){
		double doseRate=0.0;
		try{
			doseRate=Convertor.stringToDouble(dosimeterreadTf.getText());			
		} catch (Exception e){
			//e.printStackTrace();
			String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (mrRb.isSelected())
	    {
           //1mR=0.00876 mGy
			doseRate=doseRate*0.00876;          
		}
		else if(mradRb.isSelected())
		{
			//1mrad=0.01mGy
			doseRate=doseRate*0.01;			
		}

	    if (secRb.isSelected())
	    {
           //1s=1/60 min
	    	doseRate=doseRate*60.0;           
		}
		else if(oraRb.isSelected())
		{
			//1ora=60min
			doseRate=doseRate/60.0;			
		}
	    
	    //doseRateTf.setText(Convertor.doubleToString(doseRate));
	    doseRateTf.setText(Convertor.formatNumber(doseRate,5));
	}
	
	/**
	 * Save results in database
	 */
	private void save(){
		double doseRate=0.0;
		double maxDoseRate=0.0;
		double unc=0.0;
		try{
			doseRate=Convertor.stringToDouble(doseRateTf.getText());
			maxDoseRate=Convertor.stringToDouble(maxPermissibleDoseRateTf.getText());
			unc=Convertor.stringToDouble(estimatedMeasurementUncertaintyTf.getText());
		} catch (Exception e){
			//e.printStackTrace();
			String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		isOkToSave=true;
		//error
		double error = Math.abs(unc*doseRate/100.0);
		StatsUtil.confidenceLevel = 0.95;
		double DRL_unc=maxDoseRate*0.1/100.0;
		double f_degrees=StatsUtil.evaluateDegreesOfFreedom(error, doseRate);
		double f_poisson=StatsUtil.evaluateDegreesOfFreedom(DRL_unc, maxDoseRate);
		boolean diffB = StatsUtil.ttest_default_unc(doseRate, maxDoseRate, 
				error,DRL_unc, f_degrees,	f_poisson);
		
		String resultS=resources.getString("rezultat.fail");
		if (doseRate<=maxDoseRate){
			resultS=resources.getString("rezultat.succes");
		}else{
			if (!diffB)
				resultS=resources.getString("rezultat.succes");
		}
		
		if (!isOkToSave){
			JOptionPane.showMessageDialog(this,
					resources.getString("nosave.error.message"),
					resources.getString("nosave.error.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}		
		
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mainDB;
		//opens = opens + file_sep + dbName;
				
		
		try {			
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			//-------------------------
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ dosimetryTable + " values " + "(?, ?, ?, ?, ?, ?)");
			int id = maxUniqueIdOutput + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, Convertor.intToString(IDLINK));
			psInsert.setString(3, measurementDate_toSave);
			psInsert.setString(4, Convertor.doubleToString(doseRate));
			psInsert.setString(5, Convertor.doubleToString(maxDoseRate));
			psInsert.setString(6, resultS);
			psInsert.executeUpdate();
			//------------------------
									
			selectTable();//commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		
	}
	
	/**
	 * Select last record from database
	 */
	private void selectTable(){
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = mainDB;
		//opens = opens + file_sep + dbName;
		
		try {			

			String s = "select * from " + dosimetryTable+" where IDLINK = "+IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			fluorodbagent.select(s);
			
			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = fluorodbagent.getMainTable();//asp.getTab();

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
	 * Delete an entry from database
	 */
	private void delete(){
		try {
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = fluorodbagent.getMainTable();//asp.getTab();

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
			
			Statement s = radqcdbcon.createStatement(ResultSet.TYPE_FORWARD_ONLY,//con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet res = s.executeQuery("SELECT * FROM " + dosimetryTable);//deviceTable);

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
	 * View all records from database
	 */
	private void view(){
		try {
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + dosimetryTable;//+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);
			fluorodbagent.select(s);

			//suportSp.remove(asp);//remove first
			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = fluorodbagent.getMainTable();//asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			nOutput = mainTable.getRowCount();
								
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID				
						
			} 
						
			//if (con1 != null)
				//con1.close();

			String str=Convertor.intToString(nOutput);
			nOutputLabel.setText(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//validate();//enable selection
	}
}
