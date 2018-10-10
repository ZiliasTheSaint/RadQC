package radQC;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import dosimetry.DosimetryFrame;
import dosimetry.DosimetryFrameCt;
import dosimetry.DosimetryFrameFluoro;
import dosimetry.DosimetryFrameMammo;

import aec.AECFrame;

import other.OtherFrame;
import output.OutputFrame;

import sensitometry.Sensitometry;

import fsrelated.FocalSpotFrame;
import xrtf_mAs.MainFrame;

import mtf.MTF;

import danfulea.utils.ScanDiskLFGui;
import danfulea.utils.TimeUtilities;
import danfulea.db.DatabaseAgent;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;
import kvt.KVTFrame;

/**
 * Main class designed for performing various quality control (QC) tests for XRay equipment used in  
 * radiography, mammography, fluoroscopy and CT. It also computes patient doses for individuals undergoing 
 * such radiological examination, and the corresponding lifetime attributable cancer risk. <br>
 * 
 * 
 * @author Dan Fulea, 21 Apr. 2015
 */
@SuppressWarnings("serial")
public class RadQCFrame extends JFrame implements ActionListener, ItemListener{
	private final Dimension PREFERRED_SIZE = new Dimension(990, 750);
	private final Dimension tableDimension = new Dimension(700, 200);
	private final Dimension textAreaDimension = new Dimension(400, 50);
	private final Dimension sizeCb_large = new Dimension(180, 21);
	private final Dimension sizeCb_large2 = new Dimension(250, 21);
	private final Dimension sizeCb = new Dimension(60, 21);
	
	public static Color bkgColor = new Color(230, 255, 210, 255);//Linux mint green alike
	public static Color foreColor = Color.black;//Color.white;
	public static Color textAreaBkgColor = Color.white;//Color.black;
	public static Color textAreaForeColor = Color.black;//Color.yellow;
	public static boolean showLAF=true;
	
	private static final String BASE_RESOURCE_CLASS = "radQC.resources.RadQCFrameResources";
	public ResourceBundle resources = ResourceBundle
			.getBundle(BASE_RESOURCE_CLASS);
	
	private static final String INTERFACE_COMMAND = "INTERFACE";
	private static final String SENSITOMETRY_COMMAND = "SENSITOMETRY";
	private static final String MTF_COMMAND = "MTF";
	private static final String FOCALSPOT_COMMAND = "FOCALSPOT";
	private static final String EXIT_COMMAND = "EXIT";
	private static final String ABOUT_COMMAND = "ABOUT";
	private static final String LOOKANDFEEL_COMMAND = "LOOKANDFEEL";
	private static final String MU_UPDATE_COMMAND = "MU_UPDATE";
	private static final String DEVICE_UPDATE_COMMAND = "DEVICE_UPDATE";
	private static final String LOCATION_UPDATE_COMMAND = "LOCATION_UPDATE";
	private static final String TODAY_COMMAND = "TODAY";
	private static final String INSERT_COMMAND = "INSERT";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String QCTEST_COMMAND = "QCTEST";
	protected static final String RADIOGRAPHY_COMMAND="RADIOGRAPHY";
	protected static final String MAMMOGRAPHY_COMMAND="MAMMOGRAPHY";
	protected static final String FLUOROSCOPY_COMMAND="FLUOROSCOPY";
	protected static final String CT_COMMAND="CT";
	private static final String DISPLAY_COMMAND="DISPLAY";
	private static final String UPDATE_COMMAND="UPDATE";
	private static final String SEARCH_COMMAND="SEARCH";
	private static final String REFRESH_COMMAND = "REFRESH";
	private String command = null;
	
	public String radqcDB;
	protected String deviceTable;
	private String muTable;
	private String deviceTypeTable;
	private String locationTable;
	
	//protected AdvancedSelectPanel asp = null;
	protected JPanel suportSp = new JPanel(new BorderLayout());
	private int IDDEVICE = 0;
	protected JLabel iddevicelabel=null;
	private int maxUniqueID=0;
	
	@SuppressWarnings("rawtypes")
	protected JComboBox muCb;//medical unit combobox
	private JTextField deptTf = new JTextField(25);
	@SuppressWarnings("rawtypes")
	protected JComboBox deviceCb;//X-ray devices combobox	
	private JTextField snTf = new JTextField(25);
	@SuppressWarnings("rawtypes")
	protected JComboBox locationCb;//location combobox
	private JTextField countyTf = new JTextField(5);
	private JTextField manufactureTf = new JTextField(5);
	private JTextField telephoneTf = new JTextField(12);
	private JTextField emailTf = new JTextField(25);
	private JTextField contactTf = new JTextField(17);
	private JTextArea textArea = new JTextArea();
	@SuppressWarnings("rawtypes")
	protected JComboBox dayCb, monthCb = null;
	protected JTextField yearTf = new JTextField(5);
	@SuppressWarnings("rawtypes")
	private JComboBox qcCb;//
	protected JRadioButton radiographyRb, mammographyRb, fluoroscopyRb, ctRb;
	protected JLabel statusL = new JLabel();
	
	protected String medicalUnitS="";
	protected String departmentS="";
	protected String xrayDeviceS="";
	protected String serialNumberS="";
	protected String manufactureDateS="";
	protected String countyS="";
	protected String locationS="";
	protected String telephoneS="";
	protected String emailS="";
	protected String contactNameS="";
	protected String noteS="";
	
	public int IDLINK=0;//Unique_ID transfer
	public String measurementDate="";
	public String hvlFiltrationLimitsTable;
	public String hvlFiltrationTable;
	public int EXAMINATION_ID=0;
	public String kvRepeatabilityTable="";
	public String kvRepeatabilityTableDetail="";
	public String tRepeatabilityTable="";
	public String tRepeatabilityTableDetail="";
	public String kvAccuracyTable="";
	public String tAccuracyTable="";	
	public String outputTable="";
	public String outputRepeatabilityTable="";
	public String outputRepeatabilityTableDetail="";
	public String outputLinearityTable="";
	public String outputLinearityTableDetail="";
	public String aecRepeatabilityTable="";
	public String aecRepeatabilityTableDetail="";
	public String aecLinearityTable="";
	public String aecLinearityTableDetail="";
	
	public String otherTable="";
	public String dosimetryTable="";
	
	private boolean kvRepeatabilityPerformed=false;
	private boolean tRepeatabilityPerformed=false;
	private boolean kvAccuracyPerformed=false;
	private boolean tAccuracyPerformed=false;
	
	private boolean outputPerformed=false;
	private boolean outputRepeatabilityPerformed=false;
	private boolean outputLinearityPerformed=false;
	
	private boolean aecRepeatabilityPerformed=false;
	private boolean aecLinearityPerformed=false;
	
	private boolean otherPerformed=false;
	private String measurementDate_other="";
	private String noteS_other="";
	
	private String measurementDate_hvl="";
	private String kvp_hvl="";
	private String hvl_hvl="";
	private String hvl_unc_hvl="";
	private String result_hvl_hvl="";
	private String filtration_hvl="";
	private String filtration_unc_hvl="";
	private String result_filtration_hvl="";
	
	private String measurementDate_kvRepeatability="";
	private String variation_kvRepeatability="";
	private String limit_kvRepeatability="";
	private String result_kvRepeatability="";
	
	private String measurementDate_tRepeatability="";
	private String variation_tRepeatability="";
	private String limit_tRepeatability="";
	private String result_tRepeatability="";
	//only last row displayed in report
	private String measurementDate_kvAccuracy="";
	private String set_kvAccuracy="";
	private String measured_kvAccuracy="";
	private String variation_kvAccuracy="";
	private String limit_kvAccuracy="";
	private String result_kvAccuracy="";
	
	private String measurementDate_tAccuracy="";
	private String set_tAccuracy="";
	private String measured_tAccuracy="";
	private String variation_tAccuracy="";
	private String limit_tAccuracy="";
	private String result_tAccuracy="";
	
	//--------------
	private String measurementDate_outputRepeatability="";
	private String variation_outputRepeatability="";
	private String limit_outputRepeatability="";
	private String result_outputRepeatability="";
	
	private String measurementDate_output="";
	private String value_output="";
	private String limit_output="";
	private String result_output="";
	
	private String measurementDate_outputLinearity="";
	private String variation_outputLinearity="";
	private String limit_outputLinearity="";
	private String result_outputLinearity="";
	
	private String measurementDate_aecRepeatability="";
	private String variation_aecRepeatability="";
	private String limit_aecRepeatability="";
	private String result_aecRepeatability="";
	
	private String measurementDate_aecLinearity="";
	private String variation_aecLinearity="";
	private String limit_aecLinearity="";
	private String result_aecLinearity="";
	
	private boolean dosimetryPerformed=false;
	//private int nDosimetryData=0;
	private Vector<String> measurementDate_dosimetry=new Vector<String>();//"";
	private Vector<String> exam_dosimetry=new Vector<String>();//"";
	private Vector<String> projection_dosimetry=new Vector<String>();//"";
	private Vector<String> ESAK_dosimetry=new Vector<String>();//"";
	private Vector<String> unc_dosimetry=new Vector<String>();//"";
	private Vector<String> DRL_dosimetry=new Vector<String>();//"";
	private Vector<String> result_dosimetry=new Vector<String>();//"";
	private Vector<String> effectiveDose_dosimetry=new Vector<String>();//"";
	private Vector<String> effectiveDoseUnit_dosimetry=new Vector<String>();//"";
	private Vector<String> risk_dosimetry=new Vector<String>();//"";
	private Vector<String> KAP_dosimetry=new Vector<String>();//"";
	private Vector<String> KAIR_dosimetry=new Vector<String>();//"";
	private Vector<String> FSD_dosimetry=new Vector<String>();//"";
	private Vector<String> sex_dosimetry=new Vector<String>();//"";
	private Vector<String> mass_dosimetry=new Vector<String>();//"";
	private Vector<String> height_dosimetry=new Vector<String>();//"";
	private Vector<String> age_dosimetry=new Vector<String>();//"";
	private Vector<String> kv_dosimetry=new Vector<String>();//"";
	private Vector<String> filtration_dosimetry=new Vector<String>();//"";
	private Vector<String> anodeAngle_dosimetry=new Vector<String>();//"";
	private Vector<String> ripple_dosimetry=new Vector<String>();//"";
	
	private boolean dosimetryPerformedMammo=false;
	private boolean dosimetryPerformedFluoro=false;
	private boolean dosimetryPerformedCt=false;
	
	private Vector<String> measurementDate_dosimetryFluoro=new Vector<String>();
	private Vector<String> doseRate_dosimetryFluoro=new Vector<String>();
	private Vector<String> maxDoseRate_dosimetryFluoro=new Vector<String>();
	private Vector<String> result_dosimetryFluoro=new Vector<String>();
	
	private Vector<String> AGD_dosimetry=new Vector<String>();
	private Vector<String> AGDLimit_dosimetry=new Vector<String>();
	private	Vector<String> breastDose_dosimetry=new Vector<String>();
	private Vector<String> breastDoseUnit_dosimetry=new Vector<String>();
	private Vector<String> breastDiameter_dosimetry=new Vector<String>();
	private Vector<String> breastThickness_dosimetry=new Vector<String>();
	private Vector<String> anodeMaterial_dosimetry=new Vector<String>();
	
	private Vector<String> CTDI_dosimetry=new Vector<String>();
	private Vector<String> CTDIvol_dosimetry=new Vector<String>();
	private Vector<String> DLP_dosimetry=new Vector<String>();
	private Vector<String> sliceThickness_dosimetry=new Vector<String>();
	private Vector<String> rotationAngleIncrement_dosimetry=new Vector<String>();
	private	Vector<String> fanBeam_dosimetry=new Vector<String>();
	private	Vector<String> pitch_dosimetry=new Vector<String>();
	
	
	private Connection radqcdbcon = null;
	protected DatabaseAgentSupport genericdbagent;
	private String orderbyS = "ID";	
	private final Dimension sizeOrderCb = new Dimension(200, 21);	
	private JComboBox<String> genericorderbyCb;private JLabel genericrecordLabel;
	private JPanel orderP;
	
	
	/**
	 * Constructor... setting up the application GUI!
	 */
	public RadQCFrame() {
		//DBConnection.startDerby();
		
		this.setTitle(resources.getString("Application.NAME"));
		
		radqcDB = resources.getString("main.db");
		deviceTable = resources.getString("main.db.deviceTable");//default rad table
		muTable = resources.getString("main.db.muTable");
		deviceTypeTable = resources.getString("main.db.deviceTypeTable");
		locationTable = resources.getString("main.db.locationTable");
		
		hvlFiltrationLimitsTable=resources.getString("main.db.hvlFiltrationLimitsTable");
		hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable");
		
		kvRepeatabilityTable=resources.getString("main.db.kv.RepeatabilityTable");
		kvRepeatabilityTableDetail=resources.getString("main.db.kv.RepeatabilityTable.detail");
		tRepeatabilityTable=resources.getString("main.db.t.RepeatabilityTable");
		tRepeatabilityTableDetail=resources.getString("main.db.t.RepeatabilityTable.detail");
		kvAccuracyTable=resources.getString("main.db.kv.AccuracyTable");
		tAccuracyTable=resources.getString("main.db.t.AccuracyTable");
		
		outputTable=resources.getString("main.db.output.Table");
		outputRepeatabilityTable=resources.getString("main.db.output.RepeatabilityTable");
		outputLinearityTable=resources.getString("main.db.output.LinearityTable");
		outputRepeatabilityTableDetail=resources.getString("main.db.output.RepeatabilityTable.detail");
		outputLinearityTableDetail=resources.getString("main.db.output.LinearityTable.detail");
		
		aecRepeatabilityTable=resources.getString("main.db.aec.RepeatabilityTable");
		aecLinearityTable=resources.getString("main.db.aec.LinearityTable");
		aecRepeatabilityTableDetail=resources.getString("main.db.aec.RepeatabilityTable.detail");
		aecLinearityTableDetail=resources.getString("main.db.aec.LinearityTable.detail");
		
		otherTable=resources.getString("main.db.other.Table");
		dosimetryTable=resources.getString("main.db.dose.Table");//rad
		// the key to force decision made by attemptExit() method on close!!
		// otherwise...regardless the above decision, the application exit!
		// notes: solved this minor glitch in latest sun java!!
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});
		
		JMenuBar menuBar = createMenuBar(resources);
		setJMenuBar(menuBar);
		//========================================================
		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = resources.getString("data.load");// "Data";
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		opens = opens + file_sep + radqcDB;
    	radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		//=======================================================

		//-------temporary DB creation...to be comment-out after!
		//createRadQC_DB();
		//-----------
		initDBComponents();//initialization of some database related comboboxes.
		performQueryDb();
		createGUI();
		
		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);

		setVisible(true);
	}
		
	/**
	 * Setting up the frame size.
	 */
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

	/**
	 * Program close!
	 */
	private void attemptExit() {
				
		//DBConnection.shutdownDerby();
		if (radqcdbcon != null){
			try {
				radqcdbcon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		DatabaseAgent.shutdownDerby();
		dispose();
		System.exit(0);
		
	}
	
	/**
	 * GUI creation.
	 */
	private void createGUI() {
		JPanel content = new JPanel(new BorderLayout());
		
		JPanel mainPanel = createMainPanel();
		content.add(mainPanel, BorderLayout.CENTER);
		
		//------------
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		JPanel toolP = new JPanel();
		toolP.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		toolP.add(statusL);
		toolBar.add(toolP);
		String string=resources.getString("status.default.label")+resources.getString("status.default.rad.label");
		statusL.setText(string);
		content.add(toolBar, BorderLayout.PAGE_END);
		//-------------

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
	}
	
	/**
	 * Setting up the menu bar.
	 * 
	 * @param resources resources
	 * @return the result
	 */
	private JMenuBar createMenuBar(ResourceBundle resources) {
		// create the menus
		JMenuBar menuBar = new JMenuBar();

		String label;
		Character mnemonic;
		ImageIcon img;
		String imageName = "";

		// the file menu
		label = resources.getString("menu.file");
		mnemonic = (Character) resources.getObject("menu.file.mnemonic");
		JMenu fileMenu = new JMenu(label, true);
		fileMenu.setMnemonic(mnemonic.charValue());
		
		imageName = resources.getString("img.insert");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("mu.insert.button");
		mnemonic = (Character) resources.getObject("mu.insert.button.mnemonic");
		JMenuItem insertItem = new JMenuItem(label, mnemonic.charValue());
		insertItem.setActionCommand(INSERT_COMMAND);
		insertItem.addActionListener(this);
		insertItem.setIcon(img);
		insertItem.setToolTipText(resources.getString("mu.insert.button.toolTip"));
		fileMenu.add(insertItem);
		
		imageName = resources.getString("img.delete");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("mu.delete.button");
		mnemonic = (Character) resources.getObject("mu.delete.button.mnemonic");
		JMenuItem deleteItem = new JMenuItem(label, mnemonic.charValue());
		deleteItem.setActionCommand(DELETE_COMMAND);
		deleteItem.addActionListener(this);
		deleteItem.setIcon(img);
		deleteItem.setToolTipText(resources.getString("mu.delete.button.toolTip"));
		fileMenu.add(deleteItem);
		
		imageName = resources.getString("img.set");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("main.update.button");
		mnemonic = (Character) resources.getObject("main.update.button.mnemonic");
		JMenuItem updateItem = new JMenuItem(label, mnemonic.charValue());
		updateItem.setActionCommand(UPDATE_COMMAND);
		updateItem.addActionListener(this);
		updateItem.setIcon(img);
		updateItem.setToolTipText(resources.getString("main.update.button.toolTip"));
		fileMenu.add(updateItem);
		
		fileMenu.addSeparator();
		
		imageName = resources.getString("img.about");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("main.display.button");
		mnemonic = (Character) resources.getObject("main.display.button.mnemonic");
		JMenuItem displayItem = new JMenuItem(label, mnemonic.charValue());
		displayItem.setActionCommand(DISPLAY_COMMAND);
		displayItem.addActionListener(this);
		displayItem.setIcon(img);
		displayItem.setToolTipText(resources.getString("main.display.button.toolTip"));
		fileMenu.add(displayItem);
		
		fileMenu.addSeparator();
		
		imageName = resources.getString("img.view");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("main.search.button");
		mnemonic = (Character) resources.getObject("main.search.button.mnemonic");
		JMenuItem searchItem = new JMenuItem(label, mnemonic.charValue());
		searchItem.setActionCommand(SEARCH_COMMAND);
		searchItem.addActionListener(this);
		searchItem.setIcon(img);
		searchItem.setToolTipText(resources.getString("main.search.button.toolTip"));
		fileMenu.add(searchItem);
		
		imageName = resources.getString("img.pan.refresh");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("main.refresh.button");
		mnemonic = (Character) resources.getObject("main.refresh.button.mnemonic");
		JMenuItem refreshItem = new JMenuItem(label, mnemonic.charValue());
		refreshItem.setActionCommand(REFRESH_COMMAND);
		refreshItem.addActionListener(this);
		refreshItem.setIcon(img);
		refreshItem.setToolTipText(resources.getString("main.refresh.button.toolTip"));
		fileMenu.add(refreshItem);
		
		fileMenu.addSeparator();
		
		imageName = resources.getString("img.set");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("main.qc.button");
		mnemonic = (Character) resources.getObject("main.qc.button.mnemonic");
		JMenuItem qcItem = new JMenuItem(label, mnemonic.charValue());
		qcItem.setActionCommand(QCTEST_COMMAND);
		qcItem.addActionListener(this);
		qcItem.setIcon(img);
		qcItem.setToolTipText(resources.getString("main.qc.button.toolTip"));
		fileMenu.add(qcItem);
		
		fileMenu.addSeparator();

		imageName = resources.getString("img.close");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.exit");
		mnemonic = (Character) resources.getObject("menu.file.exit.mnemonic");
		JMenuItem exitItem = new JMenuItem(label, mnemonic.charValue());
		exitItem.setActionCommand(EXIT_COMMAND);
		exitItem.addActionListener(this);
		exitItem.setIcon(img);
		exitItem.setToolTipText(resources.getString("menu.file.exit.toolTip"));
		fileMenu.add(exitItem);
		
		//tools menu
		label = resources.getString("menu.tools");
		mnemonic = (Character) resources.getObject("menu.tools.mnemonic");
		JMenu toolMenu = new JMenu(label, true);
		toolMenu.setMnemonic(mnemonic.charValue());

		imageName = resources.getString("img.set");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.mtf");
		mnemonic = (Character) resources.getObject("menu.file.mtf.mnemonic");
		JMenuItem mtfItem = new JMenuItem(label, mnemonic.charValue());
		mtfItem.setActionCommand(MTF_COMMAND);
		mtfItem.addActionListener(this);
		//mtfItem.setIcon(img);
		mtfItem.setToolTipText(resources.getString("menu.file.mtf.toolTip"));
		toolMenu.add(mtfItem);
		
		imageName = resources.getString("img.set");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.fs");
		mnemonic = (Character) resources.getObject("menu.file.fs.mnemonic");
		JMenuItem fsItem = new JMenuItem(label, mnemonic.charValue());
		fsItem.setActionCommand(FOCALSPOT_COMMAND);
		fsItem.addActionListener(this);
		//mtfItem.setIcon(img);
		fsItem.setToolTipText(resources.getString("menu.file.fs.toolTip"));
		toolMenu.add(fsItem);

		imageName = resources.getString("img.set");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.tools.sensitometry");
		mnemonic = (Character) resources.getObject("menu.tools.sensitometry.mnemonic");
		JMenuItem sensitometryItem = new JMenuItem(label, mnemonic.charValue());
		sensitometryItem.setActionCommand(SENSITOMETRY_COMMAND);
		sensitometryItem.addActionListener(this);
		//mtfItem.setIcon(img);
		sensitometryItem.setToolTipText(resources.getString("menu.tools.sensitometry.toolTip"));
		toolMenu.add(sensitometryItem);
		
		toolMenu.addSeparator();
		
		imageName = resources.getString("img.set");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.tools.interface");
		mnemonic = (Character) resources.getObject("menu.tools.interface.mnemonic");
		JMenuItem interfItem = new JMenuItem(label, mnemonic.charValue());
		interfItem.setActionCommand(INTERFACE_COMMAND);
		interfItem.addActionListener(this);
		//mtfItem.setIcon(img);
		interfItem.setToolTipText(resources.getString("menu.tools.interface.toolTip"));
		toolMenu.add(interfItem);

		//=========================================================
		
		// the help menu
		label = resources.getString("menu.help");
		mnemonic = (Character) resources.getObject("menu.help.mnemonic");
		JMenu helpMenu = new JMenu(label);
		helpMenu.setMnemonic(mnemonic.charValue());

		imageName = resources.getString("img.about");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.help.about");
		mnemonic = (Character) resources.getObject("menu.help.about.mnemonic");
		JMenuItem aboutItem = new JMenuItem(label, mnemonic.charValue());
		aboutItem.setActionCommand(ABOUT_COMMAND);
		aboutItem.addActionListener(this);
		aboutItem.setIcon(img);
		aboutItem
				.setToolTipText(resources.getString("menu.help.about.toolTip"));

		

		label = resources.getString("menu.help.LF");
		mnemonic = (Character) resources.getObject("menu.help.LF.mnemonic");
		JMenuItem lfItem = new JMenuItem(label, mnemonic.charValue());
		lfItem.setActionCommand(LOOKANDFEEL_COMMAND);
		lfItem.addActionListener(this);
		lfItem.setToolTipText(resources.getString("menu.help.LF.toolTip"));
		
		if(showLAF){
			helpMenu.add(lfItem);
			helpMenu.addSeparator();
		}

		

		helpMenu.add(aboutItem);


		// finally, glue together the menu and return it
		menuBar.add(fileMenu);
		menuBar.add(Box.createHorizontalStrut( 10 ) );//add pixel space
		menuBar.add(toolMenu);
		menuBar.add(Box.createHorizontalStrut( 10 ) );//add pixel space
		menuBar.add(helpMenu);

		return menuBar;
	}
	
	/**
	 * Create main panel
	 * @return the result
	 */
	private JPanel createMainPanel() {
		//=================
		textArea.setCaretPosition(0);
		textArea.setEditable(true);//we want write in!
		//textArea.setText("");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JPanel textAreaP = new JPanel(new BorderLayout());
		textAreaP.setPreferredSize(textAreaDimension);
		textAreaP.add(new JScrollPane(textArea), BorderLayout.CENTER);
		textAreaP.setBackground(bkgColor);
		//=======================
		radiographyRb = new JRadioButton(resources.getString("main.radiography.rb"));
		radiographyRb.setToolTipText(resources.getString("main.radiography.rb.toolTip"));
		radiographyRb.setBackground(bkgColor);
		radiographyRb.setForeground(foreColor);
		
		mammographyRb = new JRadioButton(resources.getString("main.mammography.rb"));
		mammographyRb.setToolTipText(resources.getString("main.mammography.rb.toolTip"));
		mammographyRb.setBackground(bkgColor);
		mammographyRb.setForeground(foreColor);
		
		fluoroscopyRb = new JRadioButton(resources.getString("main.fluoroscopy.rb"));
		fluoroscopyRb.setToolTipText(resources.getString("main.fluoroscopy.rb.toolTip"));
		fluoroscopyRb.setBackground(bkgColor);
		fluoroscopyRb.setForeground(foreColor);
		
		ctRb = new JRadioButton(resources.getString("main.ct.rb"));
		ctRb.setToolTipText(resources.getString("main.ct.rb.toolTip"));
		ctRb.setBackground(bkgColor);
		ctRb.setForeground(foreColor);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radiographyRb);
		group.add(mammographyRb);
		group.add(fluoroscopyRb);
		group.add(ctRb);
		radiographyRb.setSelected(true);
		
		JPanel rbP = new JPanel();
		rbP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		rbP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("main.rb.border"), foreColor));
		rbP.add(radiographyRb);
		rbP.add(mammographyRb);
		rbP.add(fluoroscopyRb);
		rbP.add(ctRb);
		rbP.setBackground(bkgColor);
		
		radiographyRb.setActionCommand(RADIOGRAPHY_COMMAND);
		radiographyRb.addActionListener(this);
		mammographyRb.setActionCommand(MAMMOGRAPHY_COMMAND);
		mammographyRb.addActionListener(this);
		fluoroscopyRb.setActionCommand(FLUOROSCOPY_COMMAND);
		fluoroscopyRb.addActionListener(this);
		ctRb.setActionCommand(CT_COMMAND);
		ctRb.addActionListener(this);
		//=====================
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		//=================
		genericorderbyCb = genericdbagent.getOrderByComboBox();
		genericorderbyCb.addItemListener(this);
		genericorderbyCb.setMaximumRowCount(5);
		genericorderbyCb.setPreferredSize(sizeOrderCb);
		genericrecordLabel = genericdbagent.getRecordsLabel();
		orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(foreColor);
		orderP.add(label);
		orderP.add(genericorderbyCb);
		orderP.setBackground(bkgColor);
		label = new JLabel(resources.getString("records.count"));//"Records count: ");
		label.setForeground(foreColor);
		orderP.add(label);
		orderP.add(genericrecordLabel);
		//============================================
		JPanel dateP = new JPanel();
		dateP.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		dateP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("main.date.border"), foreColor));
		label = new JLabel(resources.getString("main.date.day"));
		label.setForeground(foreColor);
		dateP.add(label);
		dateP.add(dayCb);
		label = new JLabel(resources.getString("main.date.month"));
		label.setForeground(foreColor);
		dateP.add(label);
		dateP.add(monthCb);
		label = new JLabel(resources.getString("main.date.year"));
		label.setForeground(foreColor);
		dateP.add(label);
		dateP.add(yearTf);
		buttonName = resources.getString("main.button.today");
		buttonToolTip = resources.getString("main.button.today.toolTip");
		buttonIconName = resources.getString("img.today");
		button = FrameUtilities.makeButton(buttonIconName, TODAY_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.button.today.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		dateP.add(button);
		dateP.setBackground(bkgColor);
		label.setForeground(foreColor);
		//=======================
		JPanel p21P = new JPanel();
		p21P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		
		buttonName = resources.getString("main.display.button");
		buttonToolTip = resources.getString("main.display.button.toolTip");
		buttonIconName = resources.getString("img.about");
		button = FrameUtilities.makeButton(buttonIconName, DISPLAY_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.display.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p21P.add(button);
		
		buttonName = resources.getString("mu.delete.button");
		buttonToolTip = resources.getString("mu.delete.button.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("mu.delete.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());		
		p21P.add(button);
		
		buttonName = resources.getString("main.search.button");
		buttonToolTip = resources.getString("main.search.button.toolTip");
		buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, SEARCH_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.search.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p21P.add(button);

		buttonName = resources.getString("main.refresh.button");
		buttonToolTip = resources.getString("main.refresh.button.toolTip");
		buttonIconName = resources.getString("img.pan.refresh");
		button = FrameUtilities.makeButton(buttonIconName, REFRESH_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.refresh.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p21P.add(button);

		p21P.setBackground(RadQCFrame.bkgColor);

		JPanel p22P = new JPanel();
		p22P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		buttonName = resources.getString("mu.insert.button");
		buttonToolTip = resources.getString("mu.insert.button.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, INSERT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("mu.insert.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p22P.add(button);
		buttonName = resources.getString("main.update.button");
		buttonToolTip = resources.getString("main.update.button.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, UPDATE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.update.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p22P.add(button);
		p22P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p25P = new JPanel();
		p25P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p25P.add(dateP);
		label = new JLabel(resources.getString("main.qc.label"));
		label.setForeground(RadQCFrame.foreColor);
		p25P.add(label);
		p25P.add(qcCb);
		buttonName = resources.getString("main.qc.button");
		buttonToolTip = resources.getString("main.qc.button.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, QCTEST_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.qc.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p25P.add(button);
		p25P.setBackground(RadQCFrame.bkgColor);
		//=============
		JPanel p1P = new JPanel();
		p1P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("main.MU.label"));
		label.setForeground(RadQCFrame.foreColor);
		p1P.add(label);
		p1P.add(muCb);
		buttonName = resources.getString("main.MU.button");
		buttonToolTip = resources.getString("main.MU.button.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, MU_UPDATE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.MU.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1P.add(button);
		label = new JLabel(resources.getString("main.dept.label"));
		label.setForeground(RadQCFrame.foreColor);
		p1P.add(label);
		p1P.add(deptTf);
		p1P.setBackground(RadQCFrame.bkgColor);
		//----------------------
		JPanel p2P = new JPanel();
		p2P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("main.device.label"));
		label.setForeground(RadQCFrame.foreColor);
		p2P.add(label);
		p2P.add(deviceCb);
		buttonName = resources.getString("main.device.button");
		buttonToolTip = resources.getString("main.device.button.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, DEVICE_UPDATE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.device.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p2P.add(button);
		label = new JLabel(resources.getString("main.sn.label"));
		label.setForeground(RadQCFrame.foreColor);
		p2P.add(label);
		p2P.add(snTf);
		p2P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p3P = new JPanel();
		p3P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("main.location.label"));
		label.setForeground(RadQCFrame.foreColor);
		p3P.add(label);
		p3P.add(locationCb);
		buttonName = resources.getString("main.location.button");
		buttonToolTip = resources.getString("main.location.button.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, LOCATION_UPDATE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("main.location.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p3P.add(button);
		label = new JLabel(resources.getString("main.county.label"));
		label.setForeground(RadQCFrame.foreColor);
		p3P.add(label);
		p3P.add(countyTf);
		label = new JLabel(resources.getString("main.manufacture.label"));
		label.setForeground(RadQCFrame.foreColor);
		p3P.add(label);
		p3P.add(manufactureTf);
		p3P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p31P = new JPanel();
		p31P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("main.telephone.label"));
		label.setForeground(RadQCFrame.foreColor);
		p31P.add(label);
		p31P.add(telephoneTf);
		label = new JLabel(resources.getString("main.email.label"));
		label.setForeground(RadQCFrame.foreColor);
		p31P.add(label);
		p31P.add(emailTf);
		label = new JLabel(resources.getString("main.contact.label"));
		label.setForeground(RadQCFrame.foreColor);
		p31P.add(label);
		p31P.add(contactTf);
		p31P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p32P = new JPanel();
		p32P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("main.notes.label"));
		label.setForeground(RadQCFrame.foreColor);
		//p32P.add(dateP);
		p32P.add(label);
		p32P.setBackground(RadQCFrame.bkgColor);
		//=-------------------DB panel and records
		JPanel p4P=new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4P.add(label);
		String str=Convertor.intToString(IDDEVICE);
		iddevicelabel = new JLabel(str);
		iddevicelabel.setForeground(RadQCFrame.foreColor);
		p4P.add(iddevicelabel);
		p4P.setBackground(RadQCFrame.bkgColor);
		
		suportSp.setPreferredSize(tableDimension);

		JPanel p5P = new JPanel();
		BoxLayout blp5P = new BoxLayout(p5P, BoxLayout.Y_AXIS);
		p5P.setLayout(blp5P);
		p5P.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("records.border"),
				RadQCFrame.foreColor));
		p5P.add(suportSp);
		p5P.add(orderP);//p5P.add(p4P);
		p5P.setBackground(RadQCFrame.bkgColor);
		//-------------------
		JPanel p6P = new JPanel();
		BoxLayout blp6P = new BoxLayout(p6P, BoxLayout.Y_AXIS);
		p6P.setLayout(blp6P);
		p6P.add(rbP);
		p6P.add(p1P);
		p6P.add(p2P);
		p6P.add(p3P);
		p6P.add(p31P);
		p6P.add(p32P);
		p6P.add(textAreaP);
		p6P.add(p22P);
		p6P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p7P = new JPanel();
		BoxLayout blp7P = new BoxLayout(p7P, BoxLayout.Y_AXIS);
		p7P.setLayout(blp7P);
		p7P.add(p21P);
		p7P.add(p25P);
		p7P.setBackground(RadQCFrame.bkgColor);
		
		JPanel mainP = new JPanel(new BorderLayout());
		mainP.add(p6P, BorderLayout.NORTH);
		mainP.add(p5P, BorderLayout.CENTER);
		mainP.add(p7P, BorderLayout.SOUTH);
		mainP.setBackground(RadQCFrame.bkgColor);
		return mainP;
		
	}
	
	/**
	 * Initialize some variables from database
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	private void initDBComponents() {
		//=======
		String[] sarray = new String[31];
		for (int i = 1; i <= 31; i++) {
			if (i < 10)
				sarray[i - 1] = "0" + i;
			else
				sarray[i - 1] = Convertor.intToString(i);
		}
		dayCb = new JComboBox(sarray);
		dayCb.setMaximumRowCount(5);
		dayCb.setPreferredSize(sizeCb);

		sarray = new String[12];
		for (int i = 1; i <= 12; i++) {
			if (i < 10)
				sarray[i - 1] = "0" + i;
			else
				sarray[i - 1] = Convertor.intToString(i);
		}
		monthCb = new JComboBox(sarray);
		monthCb.setMaximumRowCount(5);
		monthCb.setPreferredSize(sizeCb);
		// ...
		today();
		// ...
		//========
		qcCb = new JComboBox();
		qcCb.setMaximumRowCount(15);
		qcCb.setPreferredSize(sizeCb_large2);
		String [] qatest = new String[6];
		qatest[0]=resources.getString("main.gctest.rad.hvl");
		qatest[1]=resources.getString("main.gctest.rad.accuracy");
		qatest[2]=resources.getString("main.gctest.rad.output");
		qatest[3]=resources.getString("main.gctest.rad.aec");
		//qatest[4]=resources.getString("main.gctest.rad.highcontrast");
		qatest[4]=resources.getString("main.gctest.rad.dose");
		//qatest[5]=resources.getString("main.gctest.rad.lowcontrast");
		qatest[5]=resources.getString("main.gctest.rad.other");
		
		qcCb.removeAllItems();
		for (int i=0; i< qatest.length; i++)
		{
			qcCb.addItem(qatest[i]);
		}
		//==========
		
		muCb = new JComboBox();
		muCb.setMaximumRowCount(15);
		muCb.setPreferredSize(sizeCb_large);
		Vector<String> muV = new Vector<String>();
		
		deviceCb = new JComboBox();
		deviceCb.setMaximumRowCount(15);
		deviceCb.setPreferredSize(sizeCb_large);
		Vector<String> deviceTypeV = new Vector<String>();
		
		locationCb = new JComboBox();
		locationCb.setMaximumRowCount(15);
		locationCb.setPreferredSize(sizeCb_large);
		Vector<String> locationV = new Vector<String>();
		
		//Connection conn = null;

		Statement s = null;
		ResultSet rs = null;
		try {
			//String datas = resources.getString("data.load");// Data
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = radqcDB;//icrpDB;// "ICRP38"; // the name of the database
			//opens = opens + file_sep + dbName;
			
			//conn = DBConnection.getDerbyConnection(opens, "", "");

			//conn.setAutoCommit(false);

			s = radqcdbcon.createStatement();//conn.createStatement();
			rs = s.executeQuery("SELECT * FROM " + muTable);

			if (rs != null)
				while (rs.next()) {
					String ss = rs.getString(2);//2nd column??					
					muV.addElement(ss);//muCb.addItem(ss);
				}
			
			//sort alphabetically
			//String[] muS=new String[muV.size()];
			//for(int i=0;i<muV.size();i++){
			//	muS[i]=muV.elementAt(i);
			//}
			//Arrays.sort(muS,String.CASE_INSENSITIVE_ORDER);
			
			/*Collections.sort(muV, new Comparator<String>(){
				public int compare(String s1, String s2){
					return s1.compareTo(s2);
				}
			});*/
			
			Collections.sort(muV,String.CASE_INSENSITIVE_ORDER);
			for(int i=0;i<muV.size();i++){
				//muCb.addItem(muS[i]);//System.out.println(muS[i]);
				muCb.addItem(muV.elementAt(i));
			}
			//==================
			
			 rs = s.executeQuery("SELECT * FROM " + deviceTypeTable);

			if (rs != null)
				while (rs.next()) {
					String ss = rs.getString(2);//2nd column??					
					deviceTypeV.addElement(ss);//muCb.addItem(ss);
				}
			Collections.sort(deviceTypeV,String.CASE_INSENSITIVE_ORDER);
			for(int i=0;i<deviceTypeV.size();i++){
				deviceCb.addItem(deviceTypeV.elementAt(i));
			}
			
			rs = s.executeQuery("SELECT * FROM " + locationTable);

			if (rs != null)
				while (rs.next()) {
					String ss = rs.getString(2);//2nd column??					
					locationV.addElement(ss);//muCb.addItem(ss);
				}
			Collections.sort(locationV,String.CASE_INSENSITIVE_ORDER);
			for(int i=0;i<locationV.size();i++){
				locationCb.addItem(locationV.elementAt(i));
			}
			//============
			//conn.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}

			// Connection
			try {
				//if (conn != null) {
					//conn.close();
					//conn = null;
				//}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}
		}		
	}
	
	/**
	 * Initialize database
	 */
	private void performQueryDb() {
		//====================
		genericdbagent = new DatabaseAgentSupport(radqcdbcon, 
				"UNIQUE_ID", deviceTable);		
		genericdbagent.setHasValidAIColumn(false);
		genericdbagent.init();		
		orderbyS = "UNIQUE_ID";//"Unique_id";
		
		JTable genericmainTable = genericdbagent.getMainTable();
		ListSelectionModel genericLSM = genericmainTable.getSelectionModel();
		genericLSM.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		IDDEVICE = genericmainTable.getRowCount();
		
		medicalUnitS="";
		departmentS="";
		xrayDeviceS="";
		serialNumberS="";
		manufactureDateS="";
		countyS="";
		locationS="";
		telephoneS="";
		emailS="";
		contactNameS="";
		noteS="";	
		if (genericmainTable.getRowCount() > 0){
			//select last row!
			genericmainTable.setRowSelectionInterval(genericmainTable.getRowCount() - 1,
					genericmainTable.getRowCount() - 1); // last ID
			//populate some field
			Integer intg=(Integer)genericmainTable.getValueAt(genericmainTable.getRowCount() - 1, 0);
			String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
			maxUniqueID=Convertor.stringToInt(maxUniqueIDs);
			
			retrieveInformation();
		}else {
			maxUniqueID=0;//reset counter
		}
		
		JScrollPane scrollPane = new JScrollPane(genericmainTable);
		suportSp.add(scrollPane, BorderLayout.CENTER);
		//=======================
		/*try {
			String datas = resources.getString("data.load");
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = radqcDB;
			opens = opens + file_sep + dbName;

			//int dummy=1;
			String s = "select * from " + deviceTable+//" where Unique_ID = "+dummy;
			" order by Unique_ID";

			Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			DBOperation.select(s, con1);

			asp = new AdvancedSelectPanel();
			suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			//rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			
			IDDEVICE = mainTable.getRowCount();// last ID
			
			medicalUnitS="";
			departmentS="";
			xrayDeviceS="";
			serialNumberS="";
			manufactureDateS="";
			countyS="";
			locationS="";
			telephoneS="";
			emailS="";
			contactNameS="";
			noteS="";			
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueID=Convertor.stringToInt(maxUniqueIDs);

				retrieveInformation();				
			} 

			if (con1 != null)
				con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	/**
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

		command = arg0.getActionCommand();
		if (command.equals(ABOUT_COMMAND)) {
			about();
		} else if (command.equals(EXIT_COMMAND)) {
			attemptExit();
		} else if (command.equals(LOOKANDFEEL_COMMAND)) {
			lookAndFeel();
		} 
		else if (command.equals(MU_UPDATE_COMMAND)) {
			muUpdate();
		} else if (command.equals(DEVICE_UPDATE_COMMAND)) {
			deviceUpdate();
		} else if (command.equals(LOCATION_UPDATE_COMMAND)) {
			locationUpdate();
		} else if (command.equals(TODAY_COMMAND)) {
			today();
		}  else if (command.equals(INSERT_COMMAND)) {
			insert();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
		} else if (command.equals(QCTEST_COMMAND)) {
			qctest();
		} else if (
				(command.equals(RADIOGRAPHY_COMMAND))||
				(command.equals(MAMMOGRAPHY_COMMAND))||
				(command.equals(FLUOROSCOPY_COMMAND))||
				(command.equals(CT_COMMAND))
				  ) 
		{
			selectTable(command);
		} else if (command.equals(DISPLAY_COMMAND)) {
			display();
		} else if (command.equals(UPDATE_COMMAND)) {
			update();
		} else if (command.equals(SEARCH_COMMAND)) {
			search();
		} else if (command.equals(REFRESH_COMMAND)) {
			refresh();
		} else if (command.equals(MTF_COMMAND)) {
			mtf();
		} else if (command.equals(FOCALSPOT_COMMAND)) {
			focalSpot();
		} else if (command.equals(SENSITOMETRY_COMMAND)) {
			sensitometry();
		} else if (command.equals(INTERFACE_COMMAND)) {
			showInterface();
		}
	}
	
	/**
	 * JCombobox related actions are set here
	 */
	public void itemStateChanged(ItemEvent ie) {
		if (ie.getSource() == genericorderbyCb) {
			sort();
		} 		
	}
	
	/**
	 * Sorts data from main table
	 */
	private void sort() {				
		orderbyS = (String) genericorderbyCb.getSelectedItem();
		genericdbagent.performSelection(orderbyS);
	}
	
	/**
	 * Go to Interface computations
	 */
	private void showInterface(){
		new InterfaceFrame(this);
	}
	
	/**
	 * Go to Sensitometry computations
	 */
	private void sensitometry(){
		new Sensitometry(this);
	}
	
	/**
	 * Go to Focal spot computations
	 */
	private void focalSpot(){
		new FocalSpotFrame(this);
	}
	
	/**
	 * Setup available QC tests variables and GO perform that QC test
	 */
	private void qctest(){
		if (radiographyRb.isSelected()){
			EXAMINATION_ID=0;//default
			hvlFiltrationLimitsTable=resources.getString("main.db.hvlFiltrationLimitsTable");
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable");
			
			kvRepeatabilityTable=resources.getString("main.db.kv.RepeatabilityTable");
			kvRepeatabilityTableDetail=resources.getString("main.db.kv.RepeatabilityTable.detail");
			tRepeatabilityTable=resources.getString("main.db.t.RepeatabilityTable");
			tRepeatabilityTableDetail=resources.getString("main.db.t.RepeatabilityTable.detail");
			kvAccuracyTable=resources.getString("main.db.kv.AccuracyTable");
			tAccuracyTable=resources.getString("main.db.t.AccuracyTable");
			
			outputTable=resources.getString("main.db.output.Table");
			outputRepeatabilityTable=resources.getString("main.db.output.RepeatabilityTable");
			outputLinearityTable=resources.getString("main.db.output.LinearityTable");
			outputRepeatabilityTableDetail=resources.getString("main.db.output.RepeatabilityTable.detail");
			outputLinearityTableDetail=resources.getString("main.db.output.LinearityTable.detail");
			
			aecRepeatabilityTable=resources.getString("main.db.aec.RepeatabilityTable");
			aecLinearityTable=resources.getString("main.db.aec.LinearityTable");
			aecRepeatabilityTableDetail=resources.getString("main.db.aec.RepeatabilityTable.detail");
			aecLinearityTableDetail=resources.getString("main.db.aec.LinearityTable.detail");
			
			otherTable=resources.getString("main.db.other.Table");
			dosimetryTable=resources.getString("main.db.dose.Table");
		}
		else if (mammographyRb.isSelected()){
			EXAMINATION_ID=1;
			hvlFiltrationLimitsTable=resources.getString("main.db.hvlFiltrationLimitsTable.mammo");
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable.mammo");
			
			kvRepeatabilityTable=resources.getString("main.db.kv.RepeatabilityTable.mammo");
			kvRepeatabilityTableDetail=resources.getString("main.db.kv.RepeatabilityTable.detail.mammo");
			tRepeatabilityTable=resources.getString("main.db.t.RepeatabilityTable.mammo");
			tRepeatabilityTableDetail=resources.getString("main.db.t.RepeatabilityTable.detail.mammo");
			kvAccuracyTable=resources.getString("main.db.kv.AccuracyTable.mammo");
			tAccuracyTable=resources.getString("main.db.t.AccuracyTable.mammo");
			
			outputTable=resources.getString("main.db.output.Table.mammo");
			outputRepeatabilityTable=resources.getString("main.db.output.RepeatabilityTable.mammo");
			outputLinearityTable=resources.getString("main.db.output.LinearityTable.mammo");
			outputRepeatabilityTableDetail=resources.getString("main.db.output.RepeatabilityTable.detail.mammo");
			outputLinearityTableDetail=resources.getString("main.db.output.LinearityTable.detail.mammo");
			
			aecRepeatabilityTable=resources.getString("main.db.aec.RepeatabilityTable.mammo");
			aecLinearityTable=resources.getString("main.db.aec.LinearityTable.mammo");
			aecRepeatabilityTableDetail=resources.getString("main.db.aec.RepeatabilityTable.detail.mammo");
			aecLinearityTableDetail=resources.getString("main.db.aec.LinearityTable.detail.mammo");
			
			otherTable=resources.getString("main.db.other.Table.mammo");
			dosimetryTable=resources.getString("main.db.dose.Table.mammo");
		}
		else if (fluoroscopyRb.isSelected()){
			EXAMINATION_ID=2;
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable.fluoro");
			
			otherTable=resources.getString("main.db.other.Table.fluoro");
			dosimetryTable=resources.getString("main.db.dose.Table.fluoro");
		}
		else if (ctRb.isSelected()){
			EXAMINATION_ID=3;
			hvlFiltrationLimitsTable=resources.getString("main.db.hvlFiltrationLimitsTable");//same table
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable.ct");
			
			otherTable=resources.getString("main.db.other.Table.ct");
			dosimetryTable=resources.getString("main.db.dose.Table.ct");
		}
		
		//get IDLINK
		JTable aspTable = genericdbagent.getMainTable();//asp.getTab();
		//JTable tab=asp.getTab();
		int[] rowsSelected=aspTable.getSelectedRows();
		if(rowsSelected.length==0 || rowsSelected.length>1)
		{
			String title = resources.getString("dialog.selectNul.title");
			String message = resources.getString("dialog.selectNul.message");
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
	    }

		int selID = 0;// NO ZERO ID
		int selRow = aspTable.getSelectedRow();
		if (selRow != -1) {
			selID = (Integer) aspTable.getValueAt(selRow, 0);
			IDLINK=selID;
		} else {
			JOptionPane.showMessageDialog(this,
					resources.getString("main.nolink.error.message"),
					resources.getString("main.nolink.error.title"), JOptionPane.ERROR_MESSAGE);		
			return;// nothing to test
		}
		
		//get measurement date
		boolean nulneg = false;
		int year = 0;
		int day =0;
		int month =0;
		
		try {
			day = Convertor.stringToInt((String) dayCb.getSelectedItem());
			month = Convertor.stringToInt((String) monthCb.getSelectedItem());
			year = Convertor.stringToInt(yearTf.getText());
			if (year < 0 || month < 0 || day < 0)
				nulneg = true;
		} catch (Exception e) {
			String title = resources.getString("number.error.title");
			String message = resources.getString("number.error");
			JOptionPane.showMessageDialog(null, message, title,
					JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
			return;
		}
		
		if (nulneg) {
			String title = resources.getString("number.error.title");
			String message = resources.getString("number.error");
			JOptionPane.showMessageDialog(null, message, title,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//TimeUtilities.setDate(day, month, year);
		//measurementDate=TimeUtilities.formatDate();
		TimeUtilities tu = new TimeUtilities(day, month, year);
		measurementDate = tu.formatDate();
		//System.out.println("date = "+measurementDate);
		
		//Get selected QC test
		String s = (String)qcCb.getSelectedItem();
		if (s.equals(resources.getString("main.gctest.rad.hvl"))||
				s.equals(resources.getString("main.gctest.mammo.hvl")) ||
				s.equals(resources.getString("main.gctest.ct.hvl"))){
			this.setVisible(false);
			MainFrame.showLAF=false;
			new MainFrame(this);//unfortunate name but we can live with it!
		} else if (s.equals(resources.getString("main.gctest.rad.alignment"))){//never, removed from here=>Tools menu
			//this.setVisible(false);
			//new Sensitometry(this);
		} else if (s.equals(resources.getString("main.gctest.rad.accuracy"))||
				s.equals(resources.getString("main.gctest.mammo.accuracy"))){			
			new KVTFrame(this);
		} else if (s.equals(resources.getString("main.gctest.rad.output"))||
				s.equals(resources.getString("main.gctest.mammo.output"))){			
			new OutputFrame(this);
		} else if (s.equals(resources.getString("main.gctest.rad.aec"))||
				s.equals(resources.getString("main.gctest.mammo.aec"))){			
			new AECFrame(this);
		} else if (s.equals(resources.getString("main.gctest.rad.other"))||
				s.equals(resources.getString("main.gctest.mammo.other"))||
				s.equals(resources.getString("main.gctest.fluoro.other"))||
				s.equals(resources.getString("main.gctest.ct.other"))
			){			
			new OtherFrame(this);
		} else if (s.equals(resources.getString("main.gctest.rad.dose"))
			){
			new DosimetryFrame(this);
		} else if (s.equals(resources.getString("main.gctest.fluoro.dose"))
			){
			new DosimetryFrameFluoro(this);
		} else if (s.equals(resources.getString("main.gctest.mammo.agd"))
			){
			new DosimetryFrameMammo(this);
		} else if (s.equals(resources.getString("main.gctest.ct.output"))
			){
			new DosimetryFrameCt(this);
		}
	}
	
	/**
	 * Go to MTF computations
	 */
	private void mtf(){
		this.setVisible(false);
		MTF.showLAF=false;
		new MTF(this);
	}

	/**
	 * Go to Search specific data
	 */
	private void search(){
		new SearchFrame(this);
	}
	
	/**
	 * Refresh the database
	 */
	private void refresh(){
		String commandString="";
		if (radiographyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable");
			commandString=RADIOGRAPHY_COMMAND;
		}
		else if (mammographyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.mammo");
			commandString=MAMMOGRAPHY_COMMAND;
		}
		else if (fluoroscopyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.fluoro");
			commandString=FLUOROSCOPY_COMMAND;
		}
		else if (ctRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.ct");
			commandString=CT_COMMAND;
		}
		
		selectTable(commandString);
	}
	
	/**
	 * Update database
	 */
	private void update(){
		if (!attemptUpdate()){
			return;
		}
		
		
		JTable tab=genericdbagent.getMainTable();//asp.getTab();
		int[] rowsSelected=tab.getSelectedRows();
		if(rowsSelected.length==0 || rowsSelected.length>1)
		{
			String title = resources.getString("dialog.selectNul.title");
			String message = resources.getString("dialog.selectNul.message");
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
	    }
		   
		@SuppressWarnings("unused")
		String commandString="";
		if (radiographyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable");
			commandString=RADIOGRAPHY_COMMAND;
		}
		else if (mammographyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.mammo");
			commandString=MAMMOGRAPHY_COMMAND;
		}
		else if (fluoroscopyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.fluoro");
			commandString=FLUOROSCOPY_COMMAND;
		}
		else if (ctRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.ct");
			commandString=CT_COMMAND;
		}
		
		//============
		String muStr = (String) muCb.getSelectedItem();
		String depStr = deptTf.getText();
		String deviceStr = (String) deviceCb.getSelectedItem();
		String snStr = snTf.getText();
		String manStr = manufactureTf.getText();
		String countyStr = countyTf.getText();
		String locationStr = (String) locationCb.getSelectedItem();
		String telStr = telephoneTf.getText();
		String emailStr = emailTf.getText();
		String contactStr = contactTf.getText();
		String notesStr = textArea.getText();
				
		StringReader reader = new StringReader(notesStr);
		int length=notesStr.length();
		
		try {
			
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = radqcDB;
			//opens = opens + file_sep + dbName;
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			
			JTable aspTable = genericdbagent.getMainTable();//asp.getTab();

			int selID = 0;// NO ZERO ID
			int selRow = aspTable.getSelectedRow();
			if (selRow != -1) {
				selID = (Integer) aspTable.getValueAt(selRow, 0);
			} else {
				//if (con1 != null)
					//con1.close();
				
				return;// nothing to update
			}
//
			PreparedStatement psUpdate = null;
			/*
			 psUpdate = con1.prepareStatement("update " + muTable
							+ " set ID=? where ID=?");

					psUpdate.setInt(1, id - 1);
					psUpdate.setInt(2, id);

					psUpdate.executeUpdate();
					psUpdate.close();
					
			str = "create table "
					+ resources.getString("main.db.deviceTable")
					+ " ( Unique_ID integer, "
					+ "Medical_unit VARCHAR(200), Department VARCHAR(200), "
					+ "XRay_device VARCHAR(200), "
					+ "Serial_number VARCHAR(200), "
					+ "Manufacture_date VARCHAR(200), " +
					//"Test_date VARCHAR(50), " +
					"County VARCHAR(200), " +
					"Location VARCHAR(200)"+
					", Telephone VARCHAR(200), "+
					"Email VARCHAR(200), "+
					"Contact_name VARCHAR(200), " +
					"Notes CLOB" +
					")"					
			 */
			//-------------------------
			psUpdate = radqcdbcon.prepareStatement("update "//con1.prepareStatement("update "
					+ deviceTable + " set Medical_unit=?," +
							"Department=?," +
							"XRay_device=?," +
							"Serial_number=?," +
							"Manufacture_date=?," +
							"County=?," +
							"Location=?," +
							"Telephone=?," +
							"Email=?," +
							"Contact_name=?," +
							"Notes=? where Unique_ID=?");

			psUpdate.setString(1, muStr);
			psUpdate.setString(2, depStr);
			psUpdate.setString(3, deviceStr);
			psUpdate.setString(4, snStr);
			psUpdate.setString(5, manStr);
			psUpdate.setString(6, countyStr);
			psUpdate.setString(7, locationStr);
			psUpdate.setString(8, telStr);
			psUpdate.setString(9, emailStr);
			psUpdate.setString(10, contactStr);					
			psUpdate.setCharacterStream(11, reader, length);//CLOB data here						
			psUpdate.setInt(12, selID);
			psUpdate.executeUpdate();
			
			//---------
			if (psUpdate != null)
				psUpdate.close();
			//if (con1 != null)
				//con1.close();
						
			genericdbagent.performSelection(orderbyS);//selectTable(commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Retrieve HVL test information
	 * @return true on success
	 */
	private boolean retrieveHVLTestInfo(){
		boolean b =false;
		//----------------
		String s="select * from " + hvlFiltrationTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = radqcDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//PreparedStatement stmt = con1.prepareStatement(s);
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			//ResultSet resultSet = stmt.executeQuery();
			ResultSet resultSet = stmt.executeQuery(s);
			//resultSet.next();
			resultSet.last();//always last row, last date!!
			
			measurementDate_hvl=resultSet.getString(3);
			kvp_hvl=resultSet.getString(4);
			hvl_hvl=resultSet.getString(5);
			hvl_unc_hvl=resultSet.getString(6);
			result_hvl_hvl=resultSet.getString(7);
			filtration_hvl=resultSet.getString(8);
			filtration_unc_hvl=resultSet.getString(9);
			result_filtration_hvl=resultSet.getString(10);
			
			b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			b=false;//if no data catch sql error
		}
		//---------------
		return b;
	}
	
	/**
	 * Retrieve high voltage and exposure time test information
	 */
	private void retrieveKVTTestInfo(){
		// prepare db query data
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
		
		kvRepeatabilityPerformed=false;
		//----------------
		String s="select * from " + kvRepeatabilityTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_kvRepeatability=resultSet.getString(3);
			variation_kvRepeatability=resultSet.getString(4);
			limit_kvRepeatability=resultSet.getString(5);
			result_kvRepeatability=resultSet.getString(6);
						
			kvRepeatabilityPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			kvRepeatabilityPerformed=false;
			//b=false;//if no data catch sql error
		}
		//---------------
		tRepeatabilityPerformed=false;
		//----------------
		s="select * from " + tRepeatabilityTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_tRepeatability=resultSet.getString(3);
			variation_tRepeatability=resultSet.getString(4);
			limit_tRepeatability=resultSet.getString(5);
			result_tRepeatability=resultSet.getString(6);
						
			tRepeatabilityPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			tRepeatabilityPerformed=false;
			//b=false;//if no data catch sql error
		}
		
		kvAccuracyPerformed=false;
		//----------------
		s="select * from " + kvAccuracyTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_kvAccuracy=resultSet.getString(3);
			set_kvAccuracy=resultSet.getString(4);
			measured_kvAccuracy=resultSet.getString(5);
			variation_kvAccuracy=resultSet.getString(6);
			limit_kvAccuracy=resultSet.getString(7);
			result_kvAccuracy=resultSet.getString(8);
						
			kvAccuracyPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			kvAccuracyPerformed=false;
			//b=false;//if no data catch sql error
		}
		
		tAccuracyPerformed=false;
		//----------------
		s="select * from " + tAccuracyTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_tAccuracy=resultSet.getString(3);
			set_tAccuracy=resultSet.getString(4);
			measured_tAccuracy=resultSet.getString(5);
			variation_tAccuracy=resultSet.getString(6);
			limit_tAccuracy=resultSet.getString(7);
			result_tAccuracy=resultSet.getString(8);
						
			tAccuracyPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			tAccuracyPerformed=false;
			//b=false;//if no data catch sql error
		}
	}
	
	/**
	 * Retrieve tube output, linearity and repeatability test information
	 */
	private void retrieveOutputTestInfo(){
		// prepare db query data
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
		
		outputPerformed=false;
		//----------------
		String s="select * from " + outputTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_output=resultSet.getString(3);
			value_output=resultSet.getString(4);
			limit_output=resultSet.getString(5);
			result_output=resultSet.getString(6);
						
			outputPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			outputPerformed=false;
			//b=false;//if no data catch sql error
		}
		
		outputRepeatabilityPerformed=false;
		//----------------
		s="select * from " + outputRepeatabilityTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_outputRepeatability=resultSet.getString(3);
			variation_outputRepeatability=resultSet.getString(4);
			limit_outputRepeatability=resultSet.getString(5);
			result_outputRepeatability=resultSet.getString(6);
						
			outputRepeatabilityPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			outputRepeatabilityPerformed=false;
			//b=false;//if no data catch sql error
		}
				
		outputLinearityPerformed=false;
		//----------------
		s="select * from " + outputLinearityTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_outputLinearity=resultSet.getString(3);
			variation_outputLinearity=resultSet.getString(4);
			limit_outputLinearity=resultSet.getString(5);
			result_outputLinearity=resultSet.getString(6);
						
			outputLinearityPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			outputLinearityPerformed=false;
			//b=false;//if no data catch sql error
		}
				
	}
	
	/**
	 * Retrieve AEC test information
	 */
	private void retrieveAECTestInfo(){
		// prepare db query data
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
				
		aecRepeatabilityPerformed=false;
		//----------------
		String s="select * from " + aecRepeatabilityTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_aecRepeatability=resultSet.getString(3);
			variation_aecRepeatability=resultSet.getString(4);
			limit_aecRepeatability=resultSet.getString(5);
			result_aecRepeatability=resultSet.getString(6);
						
			aecRepeatabilityPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			aecRepeatabilityPerformed=false;
			//b=false;//if no data catch sql error
		}
				
		aecLinearityPerformed=false;
		//----------------
		s="select * from " + aecLinearityTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_aecLinearity=resultSet.getString(3);
			variation_aecLinearity=resultSet.getString(4);
			limit_aecLinearity=resultSet.getString(5);
			result_aecLinearity=resultSet.getString(6);
						
			aecLinearityPerformed=true;//b=true;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			aecLinearityPerformed=false;
			//b=false;//if no data catch sql error
		}
				
	}
	
	/**
	 * Retrieve additional (other) test information
	 */
	private void retrieveOtherTestInfo(){
		// prepare db query data
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
				
		otherPerformed=false;
		//----------------
		String s="select * from " + otherTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			resultSet.last();//always last row, last date!!
			
			measurementDate_other=resultSet.getString(3);
			noteS_other="";
			Reader reader = resultSet.getCharacterStream(4);//4 column			
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
			
			otherPerformed=true;//if here, succes;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			otherPerformed=false;
			//b=false;//if no data catch sql error
		}				
	}
	
	/**
	 * Retrieve dosimetry test information for radiography
	 */
	private void retrieveDosimetryTestInfo(){
		// prepare db query data
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
				
		dosimetryPerformed=false;
		//reset vectors
		measurementDate_dosimetry=new Vector<String>();//"";
		exam_dosimetry=new Vector<String>();//"";
		projection_dosimetry=new Vector<String>();//"";
		ESAK_dosimetry=new Vector<String>();//"";
		unc_dosimetry=new Vector<String>();//"";
		DRL_dosimetry=new Vector<String>();//"";
		result_dosimetry=new Vector<String>();//"";
		effectiveDose_dosimetry=new Vector<String>();//"";
		effectiveDoseUnit_dosimetry=new Vector<String>();//"";
		risk_dosimetry=new Vector<String>();//"";
		KAP_dosimetry=new Vector<String>();//"";
		KAIR_dosimetry=new Vector<String>();//"";
		FSD_dosimetry=new Vector<String>();//"";
		sex_dosimetry=new Vector<String>();//"";
		mass_dosimetry=new Vector<String>();//"";
		height_dosimetry=new Vector<String>();//"";
		age_dosimetry=new Vector<String>();//"";
		kv_dosimetry=new Vector<String>();//"";
		filtration_dosimetry=new Vector<String>();//"";
		anodeAngle_dosimetry=new Vector<String>();//"";
		ripple_dosimetry=new Vector<String>();//"";		
		//----------------
		String s="select * from " + dosimetryTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			//resultSet.last();//always last row, last date!!TO BE CHANGED TO DISPLAY ALLL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!while (res.next()) {
			while (resultSet.next()) {
			measurementDate_dosimetry.addElement(resultSet.getString(3));
			exam_dosimetry.addElement(resultSet.getString(4));
			projection_dosimetry.addElement(resultSet.getString(5));
			ESAK_dosimetry.addElement(resultSet.getString(6));
			unc_dosimetry.addElement(resultSet.getString(7));
			DRL_dosimetry.addElement(resultSet.getString(8));
			result_dosimetry.addElement(resultSet.getString(9));
			effectiveDose_dosimetry.addElement(resultSet.getString(10));
			effectiveDoseUnit_dosimetry.addElement(resultSet.getString(11));
			risk_dosimetry.addElement(resultSet.getString(12));
			KAP_dosimetry.addElement(resultSet.getString(13));
			KAIR_dosimetry.addElement(resultSet.getString(14));
			FSD_dosimetry.addElement(resultSet.getString(15));
			sex_dosimetry.addElement(resultSet.getString(16));
			mass_dosimetry.addElement(resultSet.getString(17));
			height_dosimetry.addElement(resultSet.getString(18));
			age_dosimetry.addElement(resultSet.getString(19));
			kv_dosimetry.addElement(resultSet.getString(20));
			filtration_dosimetry.addElement(resultSet.getString(21));
			anodeAngle_dosimetry.addElement(resultSet.getString(22));
			ripple_dosimetry.addElement(resultSet.getString(23));
			
			//nDosimetryData++;
			}
			
			dosimetryPerformed=true;//if here, succes;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			dosimetryPerformed=false;
			//b=false;//if no data catch sql error
		}				
	}
	
	/**
	 * Retrieve dosimetry test information for mammography
	 */
	private void retrieveDosimetryTestInfoMammo(){
		dosimetryPerformedMammo=false;
		
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
		
		//reset vectors
		measurementDate_dosimetry=new Vector<String>();//"";		
		ESAK_dosimetry=new Vector<String>();//"";
		AGD_dosimetry=new Vector<String>();
		unc_dosimetry=new Vector<String>();//"";
		AGDLimit_dosimetry=new Vector<String>();
		breastDose_dosimetry=new Vector<String>();
		breastDoseUnit_dosimetry=new Vector<String>();
		result_dosimetry=new Vector<String>();//"";
		
		breastDiameter_dosimetry=new Vector<String>();
		breastThickness_dosimetry=new Vector<String>();
		anodeMaterial_dosimetry=new Vector<String>();
		
		risk_dosimetry=new Vector<String>();//"";
		KAP_dosimetry=new Vector<String>();//"";
		KAIR_dosimetry=new Vector<String>();//"";
		FSD_dosimetry=new Vector<String>();//"";
		sex_dosimetry=new Vector<String>();//"";
		
		age_dosimetry=new Vector<String>();//"";
		kv_dosimetry=new Vector<String>();//"";
		filtration_dosimetry=new Vector<String>();//"";
		anodeAngle_dosimetry=new Vector<String>();//"";
		ripple_dosimetry=new Vector<String>();//"";		
		//----------------
		String s="select * from " + dosimetryTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			//resultSet.last();//always last row, last date!!TO BE CHANGED TO DISPLAY ALLL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!while (res.next()) {
			while (resultSet.next()) {
			measurementDate_dosimetry.addElement(resultSet.getString(3));			
			ESAK_dosimetry.addElement(resultSet.getString(4));
			AGD_dosimetry.addElement(resultSet.getString(5));
			unc_dosimetry.addElement(resultSet.getString(6));
			AGDLimit_dosimetry.addElement(resultSet.getString(7));
			result_dosimetry.addElement(resultSet.getString(8));
			breastDose_dosimetry.addElement(resultSet.getString(9));
			breastDoseUnit_dosimetry.addElement(resultSet.getString(10));
			risk_dosimetry.addElement(resultSet.getString(11));
			KAP_dosimetry.addElement(resultSet.getString(12));
			KAIR_dosimetry.addElement(resultSet.getString(13));
			FSD_dosimetry.addElement(resultSet.getString(14));
			breastDiameter_dosimetry.addElement(resultSet.getString(15));
			breastThickness_dosimetry.addElement(resultSet.getString(16));						
			age_dosimetry.addElement(resultSet.getString(17));
			kv_dosimetry.addElement(resultSet.getString(18));
			filtration_dosimetry.addElement(resultSet.getString(19));
			anodeMaterial_dosimetry.addElement(resultSet.getString(20));
			anodeAngle_dosimetry.addElement(resultSet.getString(21));
			ripple_dosimetry.addElement(resultSet.getString(22));
			
			//nDosimetryData++;
			}
			
			dosimetryPerformedMammo=true;//if here, succes;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			dosimetryPerformedMammo=false;
			//b=false;//if no data catch sql error
		}
	}
	
	/**
	 * Retrieve dosimetry test information for fluoroscopy
	 */
	private void retrieveDosimetryTestInfoFluoro(){
		dosimetryPerformedFluoro=false;
		
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
		
		measurementDate_dosimetryFluoro=new Vector<String>();
		doseRate_dosimetryFluoro=new Vector<String>();
		maxDoseRate_dosimetryFluoro=new Vector<String>();
		result_dosimetryFluoro=new Vector<String>();
		
		String s="select * from " + dosimetryTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);

			while (resultSet.next()) {
				measurementDate_dosimetryFluoro.addElement(resultSet.getString(3));
				doseRate_dosimetryFluoro.addElement(resultSet.getString(4));
				maxDoseRate_dosimetryFluoro.addElement(resultSet.getString(5));
				result_dosimetryFluoro.addElement(resultSet.getString(6));			
			}
			
			dosimetryPerformedFluoro=true;//if here, succes;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			dosimetryPerformedFluoro=false;
			//b=false;//if no data catch sql error
		}			
	}
	
	/**
	 * Retrieve dosimetry test information for CT
	 */
	private void retrieveDosimetryTestInfoCt(){
		dosimetryPerformedCt=false;
		// prepare db query data
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
		
		CTDI_dosimetry=new Vector<String>();
		CTDIvol_dosimetry=new Vector<String>();
		DLP_dosimetry=new Vector<String>();
		sliceThickness_dosimetry=new Vector<String>();
		rotationAngleIncrement_dosimetry=new Vector<String>();
		fanBeam_dosimetry=new Vector<String>();
		
		pitch_dosimetry=new Vector<String>();
		
		measurementDate_dosimetry=new Vector<String>();//"";
		exam_dosimetry=new Vector<String>();//"";
		
		unc_dosimetry=new Vector<String>();//"";
		DRL_dosimetry=new Vector<String>();//"";
		result_dosimetry=new Vector<String>();//"";
		effectiveDose_dosimetry=new Vector<String>();//"";
		effectiveDoseUnit_dosimetry=new Vector<String>();//"";
		risk_dosimetry=new Vector<String>();//"";
		
		FSD_dosimetry=new Vector<String>();//"";
		sex_dosimetry=new Vector<String>();//"";
		mass_dosimetry=new Vector<String>();//"";
		height_dosimetry=new Vector<String>();//"";
		age_dosimetry=new Vector<String>();//"";
		kv_dosimetry=new Vector<String>();//"";
		filtration_dosimetry=new Vector<String>();//"";
		anodeAngle_dosimetry=new Vector<String>();//"";
		ripple_dosimetry=new Vector<String>();//"";		
		//----------------
		String s="select * from " + dosimetryTable+" where IDLINK = "+IDLINK +" order by Unique_ID";
		
		try {			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");			
			Statement stmt = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);			
			ResultSet resultSet = stmt.executeQuery(s);
			//resultSet.last();//always last row, last date!!TO BE CHANGED TO DISPLAY ALLL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!while (res.next()) {
			while (resultSet.next()) {
			measurementDate_dosimetry.addElement(resultSet.getString(3));
			exam_dosimetry.addElement(resultSet.getString(4));
			CTDI_dosimetry.addElement(resultSet.getString(5));
			CTDIvol_dosimetry.addElement(resultSet.getString(6));
			unc_dosimetry.addElement(resultSet.getString(7));
			DRL_dosimetry.addElement(resultSet.getString(8));
			result_dosimetry.addElement(resultSet.getString(9));
			effectiveDose_dosimetry.addElement(resultSet.getString(10));
			effectiveDoseUnit_dosimetry.addElement(resultSet.getString(11));
			risk_dosimetry.addElement(resultSet.getString(12));
			DLP_dosimetry.addElement(resultSet.getString(13));			
			FSD_dosimetry.addElement(resultSet.getString(14));
			
			sliceThickness_dosimetry.addElement(resultSet.getString(15));
			pitch_dosimetry.addElement(resultSet.getString(16));
			rotationAngleIncrement_dosimetry.addElement(resultSet.getString(17));
			fanBeam_dosimetry.addElement(resultSet.getString(18));
			
			sex_dosimetry.addElement(resultSet.getString(19));
			mass_dosimetry.addElement(resultSet.getString(20));
			height_dosimetry.addElement(resultSet.getString(21));
			age_dosimetry.addElement(resultSet.getString(22));
			kv_dosimetry.addElement(resultSet.getString(23));
			filtration_dosimetry.addElement(resultSet.getString(24));
			anodeAngle_dosimetry.addElement(resultSet.getString(25));
			ripple_dosimetry.addElement(resultSet.getString(26));
			
			//nDosimetryData++;
			}
			
			dosimetryPerformedCt=true;//if here, succes;
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
			dosimetryPerformedCt=false;
			//b=false;//if no data catch sql error
		}	
	}
	
	/**
	 * Display all available information associated with the selected record.
	 */
	private void display(){
		
		//get IDLINK
		JTable aspTable = genericdbagent.getMainTable();//asp.getTab();
		//JTable tab=asp.getTab();
		int[] rowsSelected=aspTable.getSelectedRows();
		if(rowsSelected.length==0 || rowsSelected.length>1)
		{
			String title = resources.getString("dialog.selectNul.title");
			String message = resources.getString("dialog.selectNul.message");
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
	    }
		
		int selID = 0;// NO ZERO ID
		int selRow = aspTable.getSelectedRow();
		if (selRow != -1) {
			selID = (Integer) aspTable.getValueAt(selRow, 0);
			IDLINK=selID;
		} else {
			JOptionPane.showMessageDialog(this,
					resources.getString("main.nolink.error2.message"),
					resources.getString("main.nolink.error.title"), JOptionPane.ERROR_MESSAGE);		
			return;// nothing to display
		}
		
		retrieveInformation();
		
		boolean hvlB =false; 
		String examS="";
		if (radiographyRb.isSelected()){
			examS=resources.getString("status.default.rad.label");
			
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable");
			hvlB = retrieveHVLTestInfo();
			
			kvRepeatabilityTable=resources.getString("main.db.kv.RepeatabilityTable");
			kvRepeatabilityTableDetail=resources.getString("main.db.kv.RepeatabilityTable.detail");
			tRepeatabilityTable=resources.getString("main.db.t.RepeatabilityTable");
			tRepeatabilityTableDetail=resources.getString("main.db.t.RepeatabilityTable.detail");
			kvAccuracyTable=resources.getString("main.db.kv.AccuracyTable");
			tAccuracyTable=resources.getString("main.db.t.AccuracyTable");
			
			retrieveKVTTestInfo();
			
			outputTable=resources.getString("main.db.output.Table");
			outputRepeatabilityTable=resources.getString("main.db.output.RepeatabilityTable");
			outputLinearityTable=resources.getString("main.db.output.LinearityTable");
			outputRepeatabilityTableDetail=resources.getString("main.db.output.RepeatabilityTable.detail");
			outputLinearityTableDetail=resources.getString("main.db.output.LinearityTable.detail");
			
			retrieveOutputTestInfo();
			
			aecRepeatabilityTable=resources.getString("main.db.aec.RepeatabilityTable");
			aecLinearityTable=resources.getString("main.db.aec.LinearityTable");
			aecRepeatabilityTableDetail=resources.getString("main.db.aec.RepeatabilityTable.detail");
			aecLinearityTableDetail=resources.getString("main.db.aec.LinearityTable.detail");
			
			retrieveAECTestInfo();
			
			otherTable=resources.getString("main.db.other.Table");
			
			retrieveOtherTestInfo();
			
			dosimetryTable=resources.getString("main.db.dose.Table");
			
			retrieveDosimetryTestInfo();
		}
		else if (mammographyRb.isSelected()){
			examS=resources.getString("status.default.mammo.label");
			
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable.mammo");
			hvlB = retrieveHVLTestInfo();
			
			kvRepeatabilityTable=resources.getString("main.db.kv.RepeatabilityTable.mammo");
			kvRepeatabilityTableDetail=resources.getString("main.db.kv.RepeatabilityTable.detail.mammo");
			tRepeatabilityTable=resources.getString("main.db.t.RepeatabilityTable.mammo");
			tRepeatabilityTableDetail=resources.getString("main.db.t.RepeatabilityTable.detail.mammo");
			kvAccuracyTable=resources.getString("main.db.kv.AccuracyTable.mammo");
			tAccuracyTable=resources.getString("main.db.t.AccuracyTable.mammo");
			
			retrieveKVTTestInfo();
			
			outputTable=resources.getString("main.db.output.Table.mammo");
			outputRepeatabilityTable=resources.getString("main.db.output.RepeatabilityTable.mammo");
			outputLinearityTable=resources.getString("main.db.output.LinearityTable.mammo");
			outputRepeatabilityTableDetail=resources.getString("main.db.output.RepeatabilityTable.detail.mammo");
			outputLinearityTableDetail=resources.getString("main.db.output.LinearityTable.detail.mammo");
			
			retrieveOutputTestInfo();
			
			aecRepeatabilityTable=resources.getString("main.db.aec.RepeatabilityTable.mammo");
			aecLinearityTable=resources.getString("main.db.aec.LinearityTable.mammo");
			aecRepeatabilityTableDetail=resources.getString("main.db.aec.RepeatabilityTable.detail.mammo");
			aecLinearityTableDetail=resources.getString("main.db.aec.LinearityTable.detail.mammo");
			
			retrieveAECTestInfo();
			
			otherTable=resources.getString("main.db.other.Table.mammo");
			
			retrieveOtherTestInfo();
			
			dosimetryTable=resources.getString("main.db.dose.Table.mammo");
			
			retrieveDosimetryTestInfoMammo();
		}
		else if (fluoroscopyRb.isSelected()){
			examS=resources.getString("status.default.fluoro.label");
			
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable.fluoro");
			//hvlB = retrieveHVLTestInfo();//NA
			
			otherTable=resources.getString("main.db.other.Table.fluoro");
			
			retrieveOtherTestInfo();
			
			dosimetryTable=resources.getString("main.db.dose.Table.fluoro");
			
			retrieveDosimetryTestInfoFluoro();
		}
		else if (ctRb.isSelected()){
			examS=resources.getString("status.default.ct.label");
			
			otherTable=resources.getString("main.db.other.Table.ct");
			
			retrieveOtherTestInfo();
			
			dosimetryTable=resources.getString("main.db.dose.Table.ct");
			
			retrieveDosimetryTestInfoCt();
		}
		
		String displayS="";
		displayS=displayS+resources.getString("main.display.mu")+medicalUnitS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.dept")+departmentS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.device")+xrayDeviceS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.sn")+serialNumberS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.examination")+examS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.manufacture")+manufactureDateS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.county")+countyS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.location")+locationS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.tel")+telephoneS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.email")+emailS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.contact")+contactNameS;
		displayS=displayS+"\n";
		displayS=displayS+resources.getString("main.display.notes")+noteS;
		if (hvlB){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_hvl;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.hvl")+
			Convertor.formatNumber(Convertor.stringToDouble(hvl_hvl),2)+" +/- "+
			Convertor.formatNumber(Convertor.stringToDouble(hvl_unc_hvl),2)+
			resources.getString("main.display.hvl.kv")+kvp_hvl;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.hvl.test")+result_hvl_hvl;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.filtration")+
			Convertor.formatNumber(Convertor.stringToDouble(filtration_hvl),2)+" +/- "+
			Convertor.formatNumber(Convertor.stringToDouble(filtration_unc_hvl),2);
			//displayS=displayS+"\n";
			if (!examS.equals(resources.getString("status.default.mammo.label"))){
				displayS=displayS+"\n";
				displayS=displayS+resources.getString("main.display.filtration.test")+result_filtration_hvl;
				//displayS=displayS+"\n";
			}
		}
		
		if (kvRepeatabilityPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_kvRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_kvRepeatability),2)+";  "+
			resources.getString("main.display.limit")+limit_kvRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.kvRepeatability.test")+result_kvRepeatability;
			
			kvRepeatabilityPerformed=false;
		}
		
		if (tRepeatabilityPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_tRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_tRepeatability),2)+";  "+
			resources.getString("main.display.limit")+limit_tRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.tRepeatability.test")+result_tRepeatability;
			
			tRepeatabilityPerformed=false;
		}
		
		if (kvAccuracyPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_kvAccuracy;
			displayS=displayS+"\n";
			displayS=displayS+
			resources.getString("main.display.set")+set_kvAccuracy+";  "+
			resources.getString("main.display.measured")+measured_kvAccuracy+";  "+
			resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_kvAccuracy),2)+";  "+
			resources.getString("main.display.limit")+limit_kvAccuracy;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.kvAccuracy.test")+result_kvAccuracy;
		
			kvAccuracyPerformed=false;
		}
		
		if (tAccuracyPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_tAccuracy;
			displayS=displayS+"\n";
			displayS=displayS+
			resources.getString("main.display.set")+set_tAccuracy+";  "+
			resources.getString("main.display.measured")+measured_tAccuracy+";  "+
			resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_tAccuracy),2)+";  "+
			resources.getString("main.display.limit")+limit_tAccuracy;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.tAccuracy.test")+result_tAccuracy;
			
			tAccuracyPerformed=false;
		}
		
		//----------------
		if (outputPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_output;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.output")+
			Convertor.formatNumber(Convertor.stringToDouble(value_output),2)+";  "+
			resources.getString("main.display.output.limit")+limit_output;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.output.test")+result_output;
		
			outputPerformed=false;
		}
		
		if (outputRepeatabilityPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_outputRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_outputRepeatability),2)+";  "+
			resources.getString("main.display.limit")+limit_outputRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.outputRepeatability.test")+result_outputRepeatability;
		
			outputRepeatabilityPerformed=false;
		}
		
		if (outputLinearityPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_outputLinearity;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_outputLinearity),2)+";  "+
			resources.getString("main.display.limit")+limit_outputLinearity;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.outputLinearity.test")+result_outputLinearity;
		
			outputLinearityPerformed=false;
		}
		//=============
		if (aecRepeatabilityPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_aecRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_aecRepeatability),2)+";  "+
			resources.getString("main.display.limit")+limit_aecRepeatability;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.aecRepeatability.test")+result_aecRepeatability;
		
			aecRepeatabilityPerformed=false;
		}
		
		if (aecLinearityPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_aecLinearity;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.variation")+
			Convertor.formatNumber(Convertor.stringToDouble(variation_aecLinearity),2)+";  "+
			resources.getString("main.display.limit")+limit_aecLinearity;
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("main.display.aecLinearity.test")+result_aecLinearity;
		
			aecLinearityPerformed=false;
		}
		
		if (otherPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_other;
			displayS=displayS+"\n";
			displayS=displayS+noteS_other;
			//displayS=displayS+"\n";
		
			otherPerformed=false;
		}
		
		if (dosimetryPerformed){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("display.dosimetry.records")+"\n"+"\n";
			
			for (int i=0;i<measurementDate_dosimetry.size();i++){
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_dosimetry.elementAt(i)
			+"; "+resources.getString("display.dosimetry.exam")+exam_dosimetry.elementAt(i)+" "+projection_dosimetry.elementAt(i)
			+"; "+resources.getString("display.dosimetry.patient")+sex_dosimetry.elementAt(i)+"; "+mass_dosimetry.elementAt(i)
			+" kg; "+height_dosimetry.elementAt(i)+" cm; "+age_dosimetry.elementAt(i)+" yrs";
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.tube")+kv_dosimetry.elementAt(i)+" kV; "+
			resources.getString("display.dosimetry.filtration")+Convertor.formatNumber(Convertor.stringToDouble(filtration_dosimetry.elementAt(i)),2)+" mmAl; "+
			resources.getString("display.dosimetry.anodeMaterial")+" W; "+
			resources.getString("display.dosimetry.anodeAngle")+anodeAngle_dosimetry.elementAt(i)+" deg; "+
			resources.getString("display.dosimetry.ripple")+ripple_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.unc")+unc_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.ESAK")+Convertor.formatNumber(Convertor.stringToDouble(ESAK_dosimetry.elementAt(i)),2);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.DRL")+DRL_dosimetry.elementAt(i)+//Convertor.formatNumber(Convertor.stringToDouble(DRL_dosimetry.elementAt(i)),2)+
			" <=> "+resources.getString("display.dosimetry.test")+result_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.KAIR")+Convertor.formatNumber(Convertor.stringToDouble(KAIR_dosimetry.elementAt(i)),2);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.FSD")+FSD_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.KAP")+Convertor.formatNumber(Convertor.stringToDouble(KAP_dosimetry.elementAt(i)),2);
			displayS=displayS+"\n";
			if (effectiveDose_dosimetry.elementAt(i).equals("")){
				displayS=displayS+resources.getString("display.dosimetry.effectiveDose")+
				"NA";
				displayS=displayS+"\n";
				displayS=displayS+resources.getString("display.dosimetry.risk")+"NA";
				displayS=displayS+"\n";
			}else{
			displayS=displayS+resources.getString("display.dosimetry.effectiveDose")+
			Convertor.formatNumber(Convertor.stringToDouble(effectiveDose_dosimetry.elementAt(i)),2)+" "+effectiveDoseUnit_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.risk")+risk_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			}
			displayS=displayS+"\n";
			}
			
			dosimetryPerformed=false;
		}
		
		if (dosimetryPerformedMammo){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("display.dosimetry.records")+"\n"+"\n";
			
			for (int i=0;i<measurementDate_dosimetry.size();i++){
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_dosimetry.elementAt(i)
			+"; "+resources.getString("display.dosimetry.patient")+
			resources.getString("display.dosimetry.breastDiameter")+breastDiameter_dosimetry.elementAt(i)+" cm"
			+"; "+resources.getString("display.dosimetry.breastThickness")+breastThickness_dosimetry.elementAt(i)+" cm"
			+"; "+age_dosimetry.elementAt(i)+" yrs";
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.tube")+kv_dosimetry.elementAt(i)+" kV; "+
			resources.getString("display.dosimetry.filtration")+Convertor.formatNumber(Convertor.stringToDouble(filtration_dosimetry.elementAt(i)),2)+" mmAl; "+
			resources.getString("display.dosimetry.anodeMaterial")+anodeMaterial_dosimetry.elementAt(i)+"; "+
			resources.getString("display.dosimetry.anodeAngle")+anodeAngle_dosimetry.elementAt(i)+" deg; "+
			resources.getString("display.dosimetry.ripple")+ripple_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.unc")+unc_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.ESAK")+Convertor.formatNumberScientific(Convertor.stringToDouble(ESAK_dosimetry.elementAt(i)));
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.AGD")+Convertor.formatNumberScientific(Convertor.stringToDouble(AGD_dosimetry.elementAt(i)));
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.AGD.limit")+AGDLimit_dosimetry.elementAt(i)+
			" <=> "+resources.getString("display.dosimetry.test")+result_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.KAIR")+Convertor.formatNumberScientific(Convertor.stringToDouble(KAIR_dosimetry.elementAt(i)));
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.FSD")+FSD_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.KAP")+Convertor.formatNumberScientific(Convertor.stringToDouble(KAP_dosimetry.elementAt(i)));
			displayS=displayS+"\n";
			if (breastDose_dosimetry.elementAt(i).equals("")){
				displayS=displayS+resources.getString("display.dosimetry.breastDose")+
				"NA";
				displayS=displayS+"\n";
				displayS=displayS+resources.getString("display.dosimetry.risk")+"NA";
				displayS=displayS+"\n";
			}else{
			displayS=displayS+resources.getString("display.dosimetry.breastDose")+
			Convertor.formatNumber(Convertor.stringToDouble(breastDose_dosimetry.elementAt(i)),2)+" "+breastDoseUnit_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.risk")+risk_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			}
			displayS=displayS+"\n";
			}
			
			dosimetryPerformedMammo=false;
		}

		if (dosimetryPerformedFluoro){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("display.dosimetry.records")+"\n"+"\n";
			
			for (int i=0;i<measurementDate_dosimetryFluoro.size();i++)
			{
				displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_dosimetryFluoro.elementAt(i);
				displayS=displayS+"\n";			
				displayS=displayS+resources.getString("display.dosimetry.fluoro.doseRate")+
					Convertor.formatNumber(Convertor.stringToDouble(doseRate_dosimetryFluoro.elementAt(i)),2);
				displayS=displayS+"\n";
				displayS=displayS+resources.getString("display.dosimetry.fluoro.maxDoseRate")+
					maxDoseRate_dosimetryFluoro.elementAt(i)+
					" <=> "+resources.getString("display.dosimetry.fluoro.test")+result_dosimetryFluoro.elementAt(i);
			
				displayS=displayS+"\n";
			
				displayS=displayS+"\n";
			}
			
			dosimetryPerformedFluoro=false;
		}

		if (dosimetryPerformedCt){
			displayS=displayS+"\n------------------------------------------\n";
			displayS=displayS+resources.getString("display.dosimetry.records")+"\n"+"\n";
			
			for (int i=0;i<measurementDate_dosimetry.size();i++){
			displayS=displayS+resources.getString("main.display.measurementDate")+measurementDate_dosimetry.elementAt(i)
			+"; "+resources.getString("display.dosimetry.exam")+exam_dosimetry.elementAt(i)+" "//+projection_dosimetry.elementAt(i)
			+"; "+resources.getString("display.dosimetry.patient")+sex_dosimetry.elementAt(i)+"; "+mass_dosimetry.elementAt(i)
			+" kg; "+height_dosimetry.elementAt(i)+" cm; "+age_dosimetry.elementAt(i)+" yrs";
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.tube")+kv_dosimetry.elementAt(i)+" kV; "+
			resources.getString("display.dosimetry.filtration")+Convertor.formatNumber(Convertor.stringToDouble(filtration_dosimetry.elementAt(i)),2)+" mmAl; "+
			resources.getString("display.dosimetry.anodeMaterial")+" W; "+
			resources.getString("display.dosimetry.anodeAngle")+anodeAngle_dosimetry.elementAt(i)+" deg; "+
			resources.getString("display.dosimetry.ripple")+ripple_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.unc")+unc_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.CTDI")+Convertor.formatNumber(Convertor.stringToDouble(CTDI_dosimetry.elementAt(i)),2);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.CTDIvol")+Convertor.formatNumber(Convertor.stringToDouble(CTDIvol_dosimetry.elementAt(i)),2);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.DRL")+DRL_dosimetry.elementAt(i)+//Convertor.formatNumber(Convertor.stringToDouble(DRL_dosimetry.elementAt(i)),2)+
			" <=> "+resources.getString("display.dosimetry.test")+result_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			
			displayS=displayS+resources.getString("display.dosimetry.FSD.ct")+FSD_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.DLP")+Convertor.formatNumber(Convertor.stringToDouble(DLP_dosimetry.elementAt(i)),2);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.DLP")+Convertor.formatNumber(Convertor.stringToDouble(DLP_dosimetry.elementAt(i)),2);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.sliceT")+sliceThickness_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.pitch")+pitch_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.rotAngleInc")+rotationAngleIncrement_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.fanBeam")+fanBeam_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			if (effectiveDose_dosimetry.elementAt(i).equals("")){
				displayS=displayS+resources.getString("display.dosimetry.effectiveDose")+
				"NA";
				displayS=displayS+"\n";
				displayS=displayS+resources.getString("display.dosimetry.risk")+"NA";
				displayS=displayS+"\n";
			}else{
			displayS=displayS+resources.getString("display.dosimetry.effectiveDose")+
			Convertor.formatNumber(Convertor.stringToDouble(effectiveDose_dosimetry.elementAt(i)),2)+" "+effectiveDoseUnit_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			displayS=displayS+resources.getString("display.dosimetry.risk")+risk_dosimetry.elementAt(i);
			displayS=displayS+"\n";
			}
			displayS=displayS+"\n";
			}
			
			dosimetryPerformedCt=false;
		}
		//==========
		new DisplayInformationFrame(this,displayS);
		//============
	}
	
	/**
	 * View data from database
	 * @param commandString commandString
	 */
	private void selectTable(String commandString){
		if (commandString.equals(RADIOGRAPHY_COMMAND))
		{
			deviceTable=resources.getString("main.db.deviceTable");
		} else if (commandString.equals(MAMMOGRAPHY_COMMAND))
		{
			deviceTable=resources.getString("main.db.deviceTable.mammo");
		} else if (commandString.equals(FLUOROSCOPY_COMMAND))
		{
			deviceTable=resources.getString("main.db.deviceTable.fluoro");
		} else if (commandString.equals(CT_COMMAND))
		{
			deviceTable=resources.getString("main.db.deviceTable.ct");
		}
		updateQCTest(commandString);
		//=============
		genericdbagent = new DatabaseAgentSupport(radqcdbcon, 
				"UNIQUE_ID", deviceTable);
		genericdbagent.setHasValidAIColumn(false);
		genericdbagent.init();
		orderbyS = "UNIQUE_ID";//"Unique_ID";
		
		JTable genericmainTable = genericdbagent.getMainTable();
		ListSelectionModel genericLSM = genericmainTable.getSelectionModel();
		genericLSM.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		IDDEVICE = genericmainTable.getRowCount();
		
		medicalUnitS="";
		departmentS="";
		xrayDeviceS="";
		serialNumberS="";
		manufactureDateS="";
		countyS="";
		locationS="";
		telephoneS="";
		emailS="";
		contactNameS="";
		noteS="";	
		if (genericmainTable.getRowCount() > 0){
			//select last row!
			genericmainTable.setRowSelectionInterval(genericmainTable.getRowCount() - 1,
					genericmainTable.getRowCount() - 1); // last ID
			//populate some field
			Integer intg=(Integer)genericmainTable.getValueAt(genericmainTable.getRowCount() - 1, 0);
			String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
			maxUniqueID=Convertor.stringToInt(maxUniqueIDs);
			
			retrieveInformation();
		}else {
			maxUniqueID=0;//reset counter
		}
		//now status========================
		String string="";
		if (commandString.equals(RADIOGRAPHY_COMMAND)){
			string=resources.getString("status.default.label")+
				resources.getString("status.default.rad.label");
		} else if (commandString.equals(MAMMOGRAPHY_COMMAND)){
			string=resources.getString("status.default.label")+
				resources.getString("status.default.mammo.label");
		} else if (commandString.equals(FLUOROSCOPY_COMMAND)){
			string=resources.getString("status.default.label")+
				resources.getString("status.default.fluoro.label");
		} else if (commandString.equals(CT_COMMAND)){
			string=resources.getString("status.default.label")+
				resources.getString("status.default.ct.label");
		}
		statusL.setText(string);
		//=====================================
		
		genericrecordLabel = genericdbagent.getRecordsLabel();
		genericorderbyCb = genericdbagent.getOrderByComboBox();
		genericorderbyCb.addItemListener(this);
		genericorderbyCb.setMaximumRowCount(5);
		genericorderbyCb.setPreferredSize(sizeOrderCb);
		
		orderP.removeAll();
		JLabel label = new JLabel(resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(foreColor);
		orderP.add(label);
		orderP.add(genericorderbyCb);
		orderP.setBackground(bkgColor);
		label = new JLabel(resources.getString("records.count"));//"Records count: ");
		label.setForeground(foreColor);
		orderP.add(label);
		orderP.add(genericrecordLabel);
		
		suportSp.removeAll();
		JScrollPane scrollPane = new JScrollPane(genericmainTable);
		suportSp.add(scrollPane, BorderLayout.CENTER);
		//================
		/*try {
						
			String datas = resources.getString("data.load");
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = radqcDB;
			opens = opens + file_sep + dbName;

			String s = "select * from " + deviceTable+" order by Unique_ID";

			Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			con1.setAutoCommit(false);
			
			DBOperation.select(s, con1);
			suportSp.remove(asp);//remove first
			asp = new AdvancedSelectPanel();
			suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			//rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			IDDEVICE = mainTable.getRowCount();
			
			medicalUnitS="";
			departmentS="";
			xrayDeviceS="";
			serialNumberS="";
			manufactureDateS="";
			countyS="";
			locationS="";
			telephoneS="";
			emailS="";
			contactNameS="";
			noteS="";	
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				maxUniqueID=Convertor.stringToInt(maxUniqueIDs);
				
				retrieveInformation();
				
			} else {
				maxUniqueID=0;//reset counter
			}
			
			//now status========================
			String string="";
			if (commandString.equals(RADIOGRAPHY_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.rad.label");
			} else if (commandString.equals(MAMMOGRAPHY_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.mammo.label");
			} else if (commandString.equals(FLUOROSCOPY_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.fluoro.label");
			} else if (commandString.equals(CT_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.ct.label");
			}
			statusL.setText(string);
			//=====================================
			con1.commit();
			
			if (con1 != null)
				con1.close();
			
			String str=Convertor.intToString(IDDEVICE);
			iddevicelabel.setText(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		validate();*/
		
	}	
	
	/**
	 * Insert an entry to database
	 */
	private void insert(){
		@SuppressWarnings("unused")
		String commandString="";
		if (radiographyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable");
			commandString=RADIOGRAPHY_COMMAND;
		}
		else if (mammographyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.mammo");
			commandString=MAMMOGRAPHY_COMMAND;
		}
		else if (fluoroscopyRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.fluoro");
			commandString=FLUOROSCOPY_COMMAND;
		}
		else if (ctRb.isSelected()){
			//deviceTable=resources.getString("main.db.deviceTable.ct");
			commandString=CT_COMMAND;
		}
			
		
		//============
		String muStr = (String) muCb.getSelectedItem();
		String depStr = deptTf.getText();
		String deviceStr = (String) deviceCb.getSelectedItem();
		String snStr = snTf.getText();
		String manStr = manufactureTf.getText();
		String countyStr = countyTf.getText();
		String locationStr = (String) locationCb.getSelectedItem();
		String telStr = telephoneTf.getText();
		String emailStr = emailTf.getText();
		String contactStr = contactTf.getText();
		String notesStr = textArea.getText();
				
		StringReader reader = new StringReader(notesStr);
		int length=notesStr.length();
		
		try {
			
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = radqcDB;
			//opens = opens + file_sep + dbName;
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			//-------------------------
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ deviceTable + " values " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			int id = maxUniqueID + 1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, muStr);
			psInsert.setString(3, depStr);
			psInsert.setString(4, deviceStr);
			psInsert.setString(5, snStr);
			psInsert.setString(6, manStr);
			psInsert.setString(7, countyStr);
			psInsert.setString(8, locationStr);
			psInsert.setString(9, telStr);
			psInsert.setString(10, emailStr);
			psInsert.setString(11, contactStr);					
			psInsert.setCharacterStream(12, reader, length);//CLOB data here						
			psInsert.executeUpdate();
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
						
			genericdbagent.performSelection(orderbyS);//selectTable(commandString);
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Retrieve basic information for a record in database
	 */
	protected void retrieveInformation(){
		try {
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = radqcDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = genericdbagent.getMainTable();//asp.getTab();

			int selID = 0;// NO ZERO ID
			int selRow = aspTable.getSelectedRow();
			if (selRow != -1) {
				selID = (Integer) aspTable.getValueAt(selRow, 0);
			} else {
				//if (con1 != null)
					//con1.close();
				
				return;// nothing to prepare
			}
			
			String s="select * from " + deviceTable+" where Unique_ID = "+selID;
			PreparedStatement stmt = radqcdbcon.prepareStatement(s);//con1.prepareStatement(s);
			ResultSet resultSet = stmt.executeQuery();
			resultSet.next();
			medicalUnitS=resultSet.getString(2);
			departmentS=resultSet.getString(3);
			xrayDeviceS=resultSet.getString(4);
			serialNumberS=resultSet.getString(5);
			manufactureDateS=resultSet.getString(6);
			countyS=resultSet.getString(7);
			locationS=resultSet.getString(8);
			telephoneS=resultSet.getString(9);
			emailS=resultSet.getString(10);
			contactNameS=resultSet.getString(11);
			
			noteS="";				
			Reader reader = resultSet.getCharacterStream(12);//12 column			
			try{					
				int i;               
				do {
				    i = reader.read();
				    char c = (char) i;
				    
				    if (i != -1)
				    	noteS=noteS+c;

				} while (i != -1);   
				
			}catch(Exception e){
				e.printStackTrace();
			}
						
			
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Attempt to delete an entry from database
	 * @return true if approved
	 */
	private boolean attemptDelete()
	{
	   boolean confirmDelete=false;

	   String title = resources.getString("dialog.delete.title");
	   String message = resources.getString("dialog.delete.message");

	   Object[] options = (Object[])resources.getObject("dialog.exit.buttons");
	   int result = JOptionPane.showOptionDialog(this,message, title,
	                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
	  		        null, options, options[0]);
	   if (result == JOptionPane.YES_OPTION)
	   {
		  confirmDelete=true;
	   }
	   
	   return confirmDelete;
    }
	
	/**
	 * Attempt to update an entry from database
	 * @return true if approved
	 */
	private boolean attemptUpdate()
	{
	   boolean confirmDelete=false;

	   String title = resources.getString("dialog.update.title");
	   String message = resources.getString("dialog.update.message");

	   Object[] options = (Object[])resources.getObject("dialog.exit.buttons");
	   int result = JOptionPane.showOptionDialog(this,message, title,
	                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
	  		        null, options, options[0]);
	   if (result == JOptionPane.YES_OPTION)
	   {
		  confirmDelete=true;
	   }
	   
	   return confirmDelete;
    }
	/*
	  //ex: am id-urile in tabela:
       //1,2,3,4,5,6,7,8,9,10
       //cu selectiile de randuri
       //3,4,6
       //se creaza sirul
       //M,M,4,M,6,7,8,9--------[0]
       //3,4,3,6,4,5,6,7--------[1]//+1 pentru trecere la id!!
       //3,4,5,6,7,8,9,10--------[2]//vechiul id!!
	  private String[][] createTableSortableIds(int[] rows)//, String mainTableSufix)
   {
	   int length=0;
	   //if (mainTableSufix.compareTo(resources.getString("maintab.rad.name"))==0)
	   //{
	       length=mainRadTableRecordCount-rows[0];
	   //}
	   length=length+1;//de la selrowsclone[0] la tableRecordCount inclusive!!!
	   String[][] stsi = new String[length][3];//simulez tabela
	   int k =0;
	   for (int i=0; i<stsi.length; i++)
	      for (int j=0; j<rows.length;j++)
	      {
	          if(i+rows[0]==rows[j])
	          {
	            stsi[i][0]="M";//de la Muie==delete
	            stsi[i][1]=Convertor.intToString(rows[j]);
	            stsi[i][2]=Convertor.intToString(rows[j]);
	            k++;//inregistreaza saltul!!!
	            break;
			  }
			  //nu s-a gasit:
			  if(j==rows.length-1)
			  {
			     stsi[i][0]=Convertor.intToString(i+rows[0]);
			     stsi[i][1]=Convertor.intToString(i+rows[0]-k);//se va updata
			     stsi[i][2]=Convertor.intToString(i+rows[0]);//se va updata
			  }
		  }

	  return stsi;
   }
	 */
	
	/**
	 * Delete an entry from database
	 */
	private void delete(){
		if (!attemptDelete()){
			return;
		}
		
		@SuppressWarnings("unused")
		String commandString="";
		if (radiographyRb.isSelected()){
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable");//entries here must be deleted as well
			kvRepeatabilityTable=resources.getString("main.db.kv.RepeatabilityTable");
			kvRepeatabilityTableDetail=resources.getString("main.db.kv.RepeatabilityTable.detail");
			tRepeatabilityTable=resources.getString("main.db.t.RepeatabilityTable");
			tRepeatabilityTableDetail=resources.getString("main.db.t.RepeatabilityTable.detail");
			kvAccuracyTable=resources.getString("main.db.kv.AccuracyTable");
			tAccuracyTable=resources.getString("main.db.t.AccuracyTable");
			
			outputTable=resources.getString("main.db.output.Table");
			outputRepeatabilityTable=resources.getString("main.db.output.RepeatabilityTable");
			outputLinearityTable=resources.getString("main.db.output.LinearityTable");
			outputRepeatabilityTableDetail=resources.getString("main.db.output.RepeatabilityTable.detail");
			outputLinearityTableDetail=resources.getString("main.db.output.LinearityTable.detail");
			
			aecRepeatabilityTable=resources.getString("main.db.aec.RepeatabilityTable");
			aecLinearityTable=resources.getString("main.db.aec.LinearityTable");
			aecRepeatabilityTableDetail=resources.getString("main.db.aec.RepeatabilityTable.detail");
			aecLinearityTableDetail=resources.getString("main.db.aec.LinearityTable.detail");
			
			otherTable=resources.getString("main.db.other.Table");
			
			dosimetryTable=resources.getString("main.db.dose.Table");
			
			commandString=RADIOGRAPHY_COMMAND;
		}
		else if (mammographyRb.isSelected()){
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable.mammo");//entries here must be deleted as well
			kvRepeatabilityTable=resources.getString("main.db.kv.RepeatabilityTable.mammo");
			kvRepeatabilityTableDetail=resources.getString("main.db.kv.RepeatabilityTable.detail.mammo");
			tRepeatabilityTable=resources.getString("main.db.t.RepeatabilityTable.mammo");
			tRepeatabilityTableDetail=resources.getString("main.db.t.RepeatabilityTable.detail.mammo");
			kvAccuracyTable=resources.getString("main.db.kv.AccuracyTable.mammo");
			tAccuracyTable=resources.getString("main.db.t.AccuracyTable.mammo");
			
			outputTable=resources.getString("main.db.output.Table.mammo");
			outputRepeatabilityTable=resources.getString("main.db.output.RepeatabilityTable.mammo");
			outputLinearityTable=resources.getString("main.db.output.LinearityTable.mammo");
			outputRepeatabilityTableDetail=resources.getString("main.db.output.RepeatabilityTable.detail.mammo");
			outputLinearityTableDetail=resources.getString("main.db.output.LinearityTable.detail.mammo");
			
			aecRepeatabilityTable=resources.getString("main.db.aec.RepeatabilityTable.mammo");
			aecLinearityTable=resources.getString("main.db.aec.LinearityTable.mammo");
			aecRepeatabilityTableDetail=resources.getString("main.db.aec.RepeatabilityTable.detail.mammo");
			aecLinearityTableDetail=resources.getString("main.db.aec.LinearityTable.detail.mammo");
			
			otherTable=resources.getString("main.db.other.Table.mammo");
			
			dosimetryTable=resources.getString("main.db.dose.Table.mammo");
			
			commandString=MAMMOGRAPHY_COMMAND;
		}
		else if (fluoroscopyRb.isSelected()){
			hvlFiltrationTable=resources.getString("main.db.hvlFiltrationTable.fluoro");//entries here must be deleted as well
			kvRepeatabilityTable="";//NA
			kvRepeatabilityTableDetail="";//NA
			tRepeatabilityTable="";//NA
			tRepeatabilityTableDetail="";//NA
			kvAccuracyTable="";//NA
			tAccuracyTable="";//NA
			
			outputTable="";//NA
			outputRepeatabilityTable="";//NA
			outputLinearityTable="";//NA
			outputRepeatabilityTableDetail="";//NA
			outputLinearityTableDetail="";//NA
			
			aecRepeatabilityTable="";
			aecLinearityTable="";
			aecRepeatabilityTableDetail="";
			aecLinearityTableDetail="";
			
			otherTable=resources.getString("main.db.other.Table.fluoro");
			
			dosimetryTable=resources.getString("main.db.dose.Table.fluoro");
			
			commandString=FLUOROSCOPY_COMMAND;
		}
		else if (ctRb.isSelected()){
			hvlFiltrationTable="";//NA
			kvRepeatabilityTable="";//NA
			kvRepeatabilityTableDetail="";//NA
			tRepeatabilityTable="";//NA
			tRepeatabilityTableDetail="";//NA
			kvAccuracyTable="";//NA
			tAccuracyTable="";//NA
			
			outputTable="";//NA
			outputRepeatabilityTable="";//NA
			outputLinearityTable="";//NA
			outputRepeatabilityTableDetail="";//NA
			outputLinearityTableDetail="";//NA
			
			aecRepeatabilityTable="";
			aecLinearityTable="";
			aecRepeatabilityTableDetail="";
			aecLinearityTableDetail="";
			
			otherTable=resources.getString("main.db.other.Table.ct");
			
			dosimetryTable=resources.getString("main.db.dose.Table.ct");
			
			commandString=CT_COMMAND;
		}
		
		//int IDLINK1=0;//@@
		int[] selIDs= new int[0];
		
		//String datas = resources.getString("data.load");
		//String currentDir = System.getProperty("user.dir");
		//String file_sep = System.getProperty("file.separator");
		//String opens = currentDir + file_sep + datas;
		//String dbName = radqcDB;
		//opens = opens + file_sep + dbName;
		
		try {
			// prepare db query data			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			//----------------------------------
			JTable aspTable = genericdbagent.getMainTable();//asp.getTab();

			//int selID = 0;// NO ZERO ID
			//int selRow = aspTable.getSelectedRow();
			int[] selRows = aspTable.getSelectedRows();
			if (selRows.length==0){
				//System.out.println("FIRE!!!!!!!!!");
				
				//if (con1 != null)
					//	con1.close();

				return;//nothing to delete
			}
			//if (selRow != -1) {
			//	selID = (Integer) aspTable.getValueAt(selRow, 0);
			//	IDLINK1=selID;//@@
			//} else {
			//	if (con1 != null)
			//		con1.close();
				
			//	return;// nothing to delete
			//}
			//---------------------------------
			selIDs=new int[selRows.length];
			for (int i=0;i<selRows.length; i++){
				selIDs[i] = (Integer) aspTable.getValueAt(selRows[i], 0);
			}
			//-----------------
			Statement s=null;
			ResultSet res=null;
			for (int i=0;i<selRows.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_FORWARD_ONLY,//con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + deviceTable);
			//PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("Unique_ID");//("ID");
				//if (id == selID) {
				if (id == selIDs[i]) {
					res.deleteRow();
				} //else if (id > selID) {
					
					/*psUpdate = con1.prepareStatement("update " + muTable
							+ " set ID=? where ID=?");

					psUpdate.setInt(1, id - 1);
					psUpdate.setInt(2, id);

					psUpdate.executeUpdate();
					psUpdate.close();*/
				//}
			}
			}//==================
			//selectTable(commandString);
			genericdbagent.performSelection(orderbyS);
			
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
			return;
		}
		
		//===========Now other tables:
		if(!hvlFiltrationTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + hvlFiltrationTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!kvRepeatabilityTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + kvRepeatabilityTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!kvRepeatabilityTableDetail.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + kvRepeatabilityTableDetail+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!tRepeatabilityTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + tRepeatabilityTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!tRepeatabilityTableDetail.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + tRepeatabilityTableDetail+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!kvAccuracyTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + kvAccuracyTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!tAccuracyTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + tAccuracyTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!outputTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + outputTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!outputRepeatabilityTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + outputRepeatabilityTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!outputLinearityTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + outputLinearityTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!outputRepeatabilityTableDetail.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + outputRepeatabilityTableDetail+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!outputLinearityTableDetail.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + outputLinearityTableDetail+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		//===========
		if(!aecRepeatabilityTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + aecRepeatabilityTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!aecLinearityTable.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + aecLinearityTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!aecRepeatabilityTableDetail.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + aecRepeatabilityTableDetail+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!aecLinearityTableDetail.equals("")){
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + aecLinearityTableDetail+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!otherTable.equals("")){//always
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + otherTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
		
		if(!dosimetryTable.equals("")){//always
			try {
				Statement s=null;
				ResultSet res=null;
					
			// prepare db query data
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//Statement s = con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
			
			for (int i=0;i<selIDs.length; i++){
			s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = s.executeQuery("SELECT * FROM " + dosimetryTable+" where IDLINK = "+selIDs[i]);//IDLINK1);//little optimization
			
			while (res.next()) {
				int rowIndex = res.getRow();//0 if no row
				if (rowIndex!=0){
					int id2 = res.getInt("IDLINK");
					if ( id2==selIDs[i]){//IDLINK1) {
						res.deleteRow();
					} else {
					//nothing					
					}
				}
			}
			}	
			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();
			} catch (Exception e) {
			e.printStackTrace();
			return;
			}
		}
	}
	
	/**
	 * Set date as today.
	 */
	private void today() {

		String s = null;
		//TimeUtilities.today();
		TimeUtilities todayTu = new TimeUtilities();
		s = Convertor.intToString(todayTu.getDay());//TimeUtilities.iday);
		if (todayTu.getDay() < 10)//TimeUtilities.iday < 10)
			s = "0" + s;
		dayCb.setSelectedItem((Object) s);
		s = Convertor.intToString(todayTu.getMonth());//TimeUtilities.imonth);
		if (todayTu.getMonth() < 10)//TimeUtilities.imonth < 10)
			s = "0" + s;
		monthCb.setSelectedItem((Object) s);
		s = Convertor.intToString(todayTu.getYear());//TimeUtilities.iyear);
		yearTf.setText(s);
	}
	
	/**
	 * Go to location database
	 */
	private void locationUpdate(){
		new LocationFrame(this);
	}
	
	/**
	 * Go to XRay device database
	 */
	private void deviceUpdate(){
		new DeviceTypeFrame(this);
	}
	
	/**
	 * Go to medical unit database
	 */
	private void muUpdate(){
		new MUFrame(this);
	}
	
	/**
	 * Changing the look and feel can be done here. Also display some gadgets.
	 */
	private void lookAndFeel() {
		setVisible(false);
		new ScanDiskLFGui(this);
	}
	
	/**
	 * Shows the about window!
	 */
	private void about() {
		new AboutFrame(this);
	}
	
	//////////////////////////////DBCreation
	/**
	 * Database creation
	 */
	@SuppressWarnings("unused")
	private void createRadQC_DB() {
		Connection conng = null;

		String datas = resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = resources.getString("main.db");// "radqc";
		opens = opens + file_sep + dbName;
		String protocol = "jdbc:derby:";

		Statement s = null;

		try {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			// disable log file!
			System.setProperty("derby.stream.error.method",
					"jdf.db.DBConnection.disableDerbyLogFile");

			Class.forName(driver).newInstance();

			//first time creation:
			//conng = DriverManager.getConnection(protocol + opens
				//	+ ";create=true", "", "");

			//for creation of more tables into an existed DB:
			conng = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");
			
			String str = "";
			// ------------------
			conng.setAutoCommit(false);
			s = conng.createStatement();

			// delete the table
			//s.execute("drop table " + resources.getString("main.db.deviceTable"));
			//s.execute("drop table " + resources.getString("main.db.deviceTable.mammo"));
			//s.execute("drop table " + resources.getString("main.db.deviceTable.fluoro"));
			//s.execute("drop table " + resources.getString("main.db.deviceTable.ct"));
			
			 //s.execute("drop table " + resources.getString("main.db.muTable"));
			 //s.execute("drop table " + resources.getString("main.db.deviceTypeTable"));
			
			//s.execute("drop table " + resources.getString("main.db.hvlFiltrationLimitsTable"));
			//s.execute("drop table " + resources.getString("main.db.hvlFiltrationTable"));
			//s.execute("drop table " + resources.getString("main.db.hvlFiltrationTable.mammo"));
			//s.execute("drop table " + resources.getString("main.db.hvlFiltrationTable.fluoro"));
			/*
			 mf.measurementDate = (String) DBOperation.getValueAt(0, 3);
			TimeUtilities.unformatDate(mf.measurementDate);
			mf.dayCb.setSelectedItem((Object) TimeUtilities.idayS);
			mf.monthCb.setSelectedItem((Object) TimeUtilities.imonthS);
			mf.yearTf.setText(TimeUtilities.iyearS);
			 */
			str = "create table "
					+ resources.getString("main.db.deviceTable")
					+ " ( Unique_ID integer, "
					+ "Medical_unit VARCHAR(200), Department VARCHAR(200), "
					+ "XRay_device VARCHAR(200), "
					+ "Serial_number VARCHAR(200), "
					+ "Manufacture_date VARCHAR(200), " +
					//"Test_date VARCHAR(50), " +
					"County VARCHAR(200), " +
					"Location VARCHAR(200)"+
					", Telephone VARCHAR(200), "+
					"Email VARCHAR(200), "+
					"Contact_name VARCHAR(200), " +
					"Notes CLOB" +
					")"
					;
			//s.execute(str);
			
			str = "create table "
				+ resources.getString("main.db.deviceTable.mammo")
				+ " ( Unique_ID integer, "
				+ "Medical_unit VARCHAR(200), Department VARCHAR(200), "
				+ "XRay_device VARCHAR(200), "
				+ "Serial_number VARCHAR(200), "
				+ "Manufacture_date VARCHAR(200), " +
				//"Test_date VARCHAR(50), " +
				"County VARCHAR(200), " +
				"Location VARCHAR(200)"+
				", Telephone VARCHAR(200), "+
				"Email VARCHAR(200), "+
				"Contact_name VARCHAR(200), " +
				"Notes CLOB" +
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.deviceTable.fluoro")
				+ " ( Unique_ID integer, "
				+ "Medical_unit VARCHAR(200), Department VARCHAR(200), "
				+ "XRay_device VARCHAR(200), "
				+ "Serial_number VARCHAR(200), "
				+ "Manufacture_date VARCHAR(200), " +
				//"Test_date VARCHAR(50), " +
				"County VARCHAR(200), " +
				"Location VARCHAR(200)"+
				", Telephone VARCHAR(200), "+
				"Email VARCHAR(200), "+
				"Contact_name VARCHAR(200), " +
				"Notes CLOB" +
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.deviceTable.ct")
				+ " ( Unique_ID integer, "
				+ "Medical_unit VARCHAR(200), Department VARCHAR(200), "
				+ "XRay_device VARCHAR(200), "
				+ "Serial_number VARCHAR(200), "
				+ "Manufacture_date VARCHAR(200), " +
				//"Test_date VARCHAR(50), " +
				"County VARCHAR(200), " +
				"Location VARCHAR(200)"+
				", Telephone VARCHAR(200), "+
				"Email VARCHAR(200), "+
				"Contact_name VARCHAR(200), " +
				"Notes CLOB" +
				")"
				;
		   //s.execute(str);

			str = "create table "
				+ resources.getString("main.db.muTable")
				+ " ( ID integer, "
				+ "Medical_unit VARCHAR(200)" +
				")"
				;
		    //s.execute(str);
					
			str = "create table "
				+ resources.getString("main.db.deviceTypeTable")
				+ " ( ID integer, "
				+ "XRay_device VARCHAR(200)" +
				")"
				;
		   //s.execute(str);
			
		   str = "create table "
				+ resources.getString("main.db.locationTable")
				+ " ( ID integer, "
				+ "Location VARCHAR(200)" +
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.hvlFiltrationLimitsTable")
				+ " ( ID integer, "
				+ "HVL_min DOUBLE PRECISION, " +
				"Filtration_min DOUBLE PRECISION"+
				")"
				;
		   //s.execute(str);
		   PreparedStatement psInsert = null;
			//-------------------------
			/*psInsert = conng.prepareStatement("insert into "
					+ resources.getString("main.db.hvlFiltrationLimitsTable") + " values " + "(?, ?, ?)");
			int id =  1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, "2.9");
			psInsert.setString(3, "2.5");
			psInsert.executeUpdate();*/
			
		   str = "create table "
				+ resources.getString("main.db.hvlFiltrationLimitsTable.mammo")
				+ " ( ID integer, "
				+ "HVL_min DOUBLE PRECISION, " +
				"Filtration_min DOUBLE PRECISION"+
				")"
				;
		   //s.execute(str);
		   /*psInsert = conng.prepareStatement("insert into "
					+ resources.getString("main.db.hvlFiltrationLimitsTable.mammo") + " values " + "(?, ?, ?)");
			int id =  1;//Unique ID
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, "0.28");//at 28 kV for Mo/Mo or kv/100 in rest
			psInsert.setString(3, "0.01");//N.A.
			psInsert.executeUpdate();*/
			
		   str = "create table "
				+ resources.getString("main.db.hvlFiltrationTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"KVP DOUBLE PRECISION, "+
				"HVL DOUBLE PRECISION, "+
				"HVL_UNC DOUBLE PRECISION, "+
				"Result_HVL VARCHAR(50), "+
				"Filtration DOUBLE PRECISION, "+
				"Filtration_unc DOUBLE PRECISION, "+
				"Result_filtration VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.hvlFiltrationTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"KVP DOUBLE PRECISION, "+
				"HVL DOUBLE PRECISION, "+
				"HVL_UNC DOUBLE PRECISION, "+
				"Result_HVL VARCHAR(50), "+
				"Filtration DOUBLE PRECISION, "+
				"Filtration_unc DOUBLE PRECISION, "+
				"Result_filtration VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.hvlFiltrationTable.fluoro")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"KVP DOUBLE PRECISION, "+
				"HVL DOUBLE PRECISION, "+
				"HVL_UNC DOUBLE PRECISION, "+
				"Result_HVL VARCHAR(50), "+
				"Filtration DOUBLE PRECISION, "+
				"Filtration_unc DOUBLE PRECISION, "+
				"Result_filtration VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		
		   str = "create table "
				+ resources.getString("main.db.hvlFiltrationTable.ct")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"KVP DOUBLE PRECISION, "+
				"HVL DOUBLE PRECISION, "+
				"HVL_UNC DOUBLE PRECISION, "+
				"Result_HVL VARCHAR(50), "+
				"Filtration DOUBLE PRECISION, "+
				"Filtration_unc DOUBLE PRECISION, "+
				"Result_filtration VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   //==========kvt===============
		   //DO NOT USE SET or OUTPUT as column name, is a SQL Keyword=>changed to Set_value
		   //s.execute("drop table " + resources.getString("main.db.kv.RepeatabilityTable.detail"));
		   //s.execute("drop table " + resources.getString("main.db.kv.RepeatabilityTable.detail.mammo"));
		   //s.execute("drop table " + resources.getString("main.db.t.RepeatabilityTable.detail"));
		   //s.execute("drop table " + resources.getString("main.db.t.RepeatabilityTable.detail.mammo"));
		   str = "create table "
				+ resources.getString("main.db.kv.RepeatabilityTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);		   
		   
		   str = "create table "
				+ resources.getString("main.db.kv.RepeatabilityTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.kv.RepeatabilityTable.detail")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.kv.RepeatabilityTable.detail.mammo")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.t.RepeatabilityTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.t.RepeatabilityTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.t.RepeatabilityTable.detail")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.t.RepeatabilityTable.detail.mammo")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.kv.AccuracyTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION, "+
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		 //  s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.kv.AccuracyTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION, "+
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.t.AccuracyTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION, "+
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.t.AccuracyTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Set_value DOUBLE PRECISION, "+
				"Measured_value DOUBLE PRECISION, "+
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   //=========end kvt
		   
		   //output================
		   //s.execute("drop table " + resources.getString("main.db.output.Table"));
		   //s.execute("drop table " + resources.getString("main.db.output.RepeatabilityTable"));
		   //s.execute("drop table " + resources.getString("main.db.output.RepeatabilityTable.detail"));
		   //s.execute("drop table " + resources.getString("main.db.output.LinearityTable"));
		 //DO NOT USE SET or OUTPUT as column name, is a SQL Keyword=>changed to Output_value
		   //also in table name!!!!!!!!!!!!!
		   str = "create table "
				+ resources.getString("main.db.output.Table")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Output_value DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.RepeatabilityTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.RepeatabilityTable.detail")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+				
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.LinearityTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.LinearityTable.detail")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"mAs DOUBLE PRECISION, "+
				"Dose_indicator DOUBLE PRECISION, "+
				"Output_value DOUBLE PRECISION"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.Table.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Output_value DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.RepeatabilityTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.RepeatabilityTable.detail.mammo")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+				
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.LinearityTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.output.LinearityTable.detail.mammo")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"mAs DOUBLE PRECISION, "+
				"Dose_indicator DOUBLE PRECISION, "+
				"Output_value DOUBLE PRECISION"+
				")"
				;
		   //s.execute(str);
		   
		   //end output
		   //AEC
		   str = "create table "
				+ resources.getString("main.db.aec.RepeatabilityTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.aec.RepeatabilityTable.detail")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+				
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.aec.LinearityTable")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.aec.LinearityTable.detail")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"Measured_value DOUBLE PRECISION"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.aec.RepeatabilityTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.aec.RepeatabilityTable.detail.mammo")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+				
				"Measured_value DOUBLE PRECISION"+				
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.aec.LinearityTable.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"Variation DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.aec.LinearityTable.detail.mammo")
				+ " ( ID integer, "+
				"Unique_ID integer, "+
				"IDLINK integer, "+ 
				"Measured_value DOUBLE PRECISION"+
				")"
				;
		   //s.execute(str);
		   //==================
		  // s.execute("drop table " + resources.getString("main.db.other.Table"));
		   //s.execute("drop table " + resources.getString("main.db.other.Table.mammo"));
		   //s.execute("drop table " + resources.getString("main.db.other.Table.fluoro"));
		   //s.execute("drop table " + resources.getString("main.db.other.Table.ct"));
		   str = "create table "
				+ resources.getString("main.db.other.Table")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+"Measurement_date VARCHAR(50), " 
				+ "Notes CLOB"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.other.Table.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+"Measurement_date VARCHAR(50), "
				+ "Notes CLOB"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.other.Table.fluoro")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+"Measurement_date VARCHAR(50), "
				+ "Notes CLOB"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.other.Table.ct")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+"Measurement_date VARCHAR(50), "
				+ "Notes CLOB"+
				")"
				;
		   //s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.dose.Table")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+"Measurement_date VARCHAR(50), "
				+ "Exam VARCHAR(50), "+
				"Projection VARCHAR(50), "+				
				"ESAK VARCHAR(50), "+
				"Err_perCent VARCHAR(50), "+
				"DRL VARCHAR(50), "+
				"Result VARCHAR(50), "+
				"Effective_dose VARCHAR(50), "+
				"Units VARCHAR(50), "+
				"RISK_perMillion VARCHAR(50), "+
				"KAP VARCHAR(50), "+
				"Kerma_air VARCHAR(50), "+
				"FSD VARCHAR(50), "+
				"sex VARCHAR(50), "+
				"mass VARCHAR(50), "+
				"height VARCHAR(50), "+
				"age VARCHAR(50), "+
				"kVp VARCHAR(50), "+
				"filtration VARCHAR(50), "+
				"anodeAngle VARCHAR(50), "+
				"ripple VARCHAR(50)"+
				")"
				;
		   //s.execute(str);//23
		   
		   str = "create table "
				+ resources.getString("main.db.dose.Table.fluoro")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+ "Measurement_date VARCHAR(50), " +
				"DOSE_RATE DOUBLE PRECISION, "+
				"Limit DOUBLE PRECISION, "+				
				"Result VARCHAR(50)"+
				")"
				;
		  // s.execute(str);
		   
		   str = "create table "
				+ resources.getString("main.db.dose.Table.mammo")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+"Measurement_date VARCHAR(50), "
				+ 							
				"ESAK VARCHAR(50), "+
				"AGD VARCHAR(50), "+
				"Err_perCent VARCHAR(50), "+
				"AGD_LIMIT VARCHAR(50), "+
				"Result VARCHAR(50), "+
				"BREAST_DOSE VARCHAR(50), "+
				"Units VARCHAR(50), "+
				"RISK_perMillion VARCHAR(50), "+
				"KAP VARCHAR(50), "+
				"Kerma_air VARCHAR(50), "+
				"FSD VARCHAR(50), "+				
				"breast_diameter VARCHAR(50), "+
				"breast_thickness VARCHAR(50), "+
				"age VARCHAR(50), "+
				"kVp VARCHAR(50), "+
				"filtration VARCHAR(50), "+
				"anodeMaterial VARCHAR(50), "+
				"anodeAngle VARCHAR(50), "+
				"ripple VARCHAR(50)"+
				")"
				;
		   //s.execute(str);//22
		   //s.execute("drop table " + resources.getString("main.db.dose.Table.ct"));
		   str = "create table "
				+ resources.getString("main.db.dose.Table.ct")
				+ " ( Unique_ID integer, "+
				"IDLINK integer, "
				+"Measurement_date VARCHAR(50), "
				+ "Exam VARCHAR(50), "+								
				"CTDIfree VARCHAR(50), "+
				"CTDIvol VARCHAR(50), "+
				"Err_perCent VARCHAR(50), "+
				"CTDIvol_LIMIT VARCHAR(50), "+
				"Result VARCHAR(50), "+
				"Effective_dose VARCHAR(50), "+
				"Units VARCHAR(50), "+
				"RISK_perMillion VARCHAR(50), "+
				"DLP VARCHAR(50), "+
				"FCA VARCHAR(50), "+
				"sliceT VARCHAR(50), "+
				"pitch VARCHAR(50), "+
				"rotInc VARCHAR(50), "+
				"fanBeam VARCHAR(50), "+
				"sex VARCHAR(50), "+
				"mass VARCHAR(50), "+
				"height VARCHAR(50), "+
				"age VARCHAR(50), "+
				"kVp VARCHAR(50), "+
				"filtration VARCHAR(50), "+
				"anodeAngle VARCHAR(50), "+
				"ripple VARCHAR(50)"+
				")"
				;
		   //s.execute(str);//25
		   
		   conng.commit();//if here then FIRE EXECUTE ALL COMMANDS!!
		   if (psInsert != null)
				psInsert.close();
			// /////
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			// Connection
			try {
				if (conng != null) {
					conng.close();
					conng = null;
				}
			} catch (Exception sqle) {
				sqle.printStackTrace();
			}
		}
	}

	/**
	 * Updates available QC tests
	 * @param commandString commandString
	 */
	@SuppressWarnings("unchecked")
	private void updateQCTest(String commandString){
		if (commandString.equals(RADIOGRAPHY_COMMAND))
		{
			String [] qatest = new String[6];
			qatest[0]=resources.getString("main.gctest.rad.hvl");
			qatest[1]=resources.getString("main.gctest.rad.accuracy");
			qatest[2]=resources.getString("main.gctest.rad.output");
			qatest[3]=resources.getString("main.gctest.rad.aec");
			//qatest[4]=resources.getString("main.gctest.rad.highcontrast");
			//qatest[5]=resources.getString("main.gctest.rad.lowcontrast");
			qatest[4]=resources.getString("main.gctest.rad.dose");
			qatest[5]=resources.getString("main.gctest.rad.other");
			
			qcCb.removeAllItems();
			for (int i=0; i< qatest.length; i++)
			{
				qcCb.addItem(qatest[i]);
			}
			
		} else if (commandString.equals(MAMMOGRAPHY_COMMAND))
		{
			String [] qatest = new String[6];
			qatest[0]=resources.getString("main.gctest.mammo.hvl");
			qatest[1]=resources.getString("main.gctest.mammo.accuracy");
			qatest[2]=resources.getString("main.gctest.mammo.output");
			qatest[3]=resources.getString("main.gctest.mammo.aec");
			//qatest[4]=resources.getString("main.gctest.mammo.highcontrast");
			//qatest[5]=resources.getString("main.gctest.mammo.lowcontrast");
			qatest[4]=resources.getString("main.gctest.mammo.agd");
			qatest[5]=resources.getString("main.gctest.mammo.other");
			
			qcCb.removeAllItems();
			for (int i=0; i< qatest.length; i++)
			{
				qcCb.addItem(qatest[i]);
			}

		} else if (commandString.equals(FLUOROSCOPY_COMMAND))
		{
			String [] qatest = new String[2];
			//qatest[0]=resources.getString("main.gctest.fluoro.hvl");
		//	qatest[1]=resources.getString("main.gctest.fluoro.output");
			//qatest[2]=resources.getString("main.gctest.fluoro.highcontrast");
			//qatest[3]=resources.getString("main.gctest.fluoro.lowcontrast");
			qatest[0]=resources.getString("main.gctest.fluoro.dose");
			qatest[1]=resources.getString("main.gctest.fluoro.other");
						
			qcCb.removeAllItems();
			for (int i=0; i< qatest.length; i++)
			{
				qcCb.addItem(qatest[i]);
			}
		} else if (commandString.equals(CT_COMMAND))
		{
			String [] qatest = new String[3];
			qatest[0]=resources.getString("main.gctest.ct.hvl");
			qatest[1]=resources.getString("main.gctest.ct.output");
			qatest[2]=resources.getString("main.gctest.ct.other");
									
			qcCb.removeAllItems();
			for (int i=0; i< qatest.length; i++)
			{
				qcCb.addItem(qatest[i]);
			}
		}
	}
}
