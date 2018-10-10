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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormatSymbols;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import danfulea.db.DatabaseAgent;
//import jdf.db.DBConnection;
import danfulea.math.Convertor;
import danfulea.math.StatsUtil;
import danfulea.utils.FrameUtilities;
import danfulea.utils.SystemInfo;
import radQC.RadQCFrame;

/**
 * Computes organ doses, effective dose and lifetime attributable cancer risk for 
 * patients undergoing CT examinations. In the past, this class was used for evaluation of patient doses 
 * using KERMA approximation (IradMed program). Nowadays, this class prepares an input file for a more complex 
 * Monte Carlo engine which is based on GEANT4 simulation toolkit and then runs the corresponding C++ program. Inside current 
 * folder there is a file (MonteCarloPath.txt) having a single line which points to the C++ program folder (e.g. D:\\dhpre_exe_web). 
 * Hence, this works out of the box for Windows users having both C++ program and this Java program (available on sourceforge). On Linux or Mac, you have to 
 * compile and generate executable from the C++ source code, then modify this class to talk with the C++ program.
 * 
 * @author Dan Fulea, 07 May 2015
 */
@SuppressWarnings("serial")
public class DosimetryFrameCt extends JFrame implements ActionListener, ItemListener, Runnable{
	private volatile Thread simTh;	
	
	private static String filenameMC = "MonteCarloPath.txt";
	private static String doseExeName= "DHPRE.exe";
	private static String detExeNameLinux = "DHPRE";
	protected static String doseExeFileName= "DHPRE";
	protected String doseFolderURL;
	protected String macroFilename;
	protected File macroFile;
	
	private static final Dimension PREFERRED_SIZE = new Dimension(990, 720);
	private static final Dimension sizeCb = new Dimension(100, 21);
	private static final Dimension sizeCb2 = new Dimension(150, 21);
	private static final Dimension textAreaDimension = new Dimension(900, 100);
	private static final String BASE_RESOURCE_CLASS = "dosimetry.resources.DosimetryFrameResources";
	protected ResourceBundle resources;
	private RadQCFrame mf;
	
	protected String dosimetryTable="";
	protected String measurementDate_toSave="";
	protected String mainDB = "";
	protected int IDLINK=0;
	protected int EXAMINATION_ID=0;
	
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private static final String FILTRATION_COMMAND = "FILTRATION";
	private static final String KAP_COMMAND = "KAP";
	private static final String PHANTOMDEFAULTS_COMMAND="PHANTOMDEFAULTS";
	private static final String SAVE_COMMAND = "SAVE";
	private String command = null;
	
	@SuppressWarnings("rawtypes")
	private JComboBox phantomSexCb, kvCb, anodeAngleCb, rippleCb, examinationCb, fanBeamCb;//, usemasCb;//, projectionCb;
	private JRadioButton newbornRb,y1Rb,y5Rb,y10Rb,y15Rb,adultRb;
	private JTextField phantomAgeTf = new JTextField(5);
	private JTextField phantomHeightTf = new JTextField(5);
	private JTextField phantomWeightTf = new JTextField(5);
	private JTextField filtrationTf = new JTextField(15);
	
	private JTextField sliceThicknessTf = new JTextField(5);
	private JTextField rotationAngleIncrementTf = new JTextField(5);
	private JTextField pitchFactorTf = new JTextField(5);
	private JTextField ctdiTf = new JTextField(5);
	protected JTextField ctdiVolTf = new JTextField(15);
	
	//private JTextField mAsTf = new JTextField(5);
	//protected JTextField kapTf = new JTextField(15);
	protected JTextField focusmidplaneTf = new JTextField(5);
	private JTextField runsTf = new JTextField(5);
	protected String ageGroup = "";
	protected int ageGroupIndex=0;
	//protected double projectionAngle = 0.0;
	private String totalFiltrationStr="";
	private JTextField bsfTf = new JTextField(5);
	protected JTextArea textArea = new JTextArea();
	private JTextField estimatedMeasurementUncertaintyTf=new JTextField(5);
	protected JCheckBox graphicSceneAutoRefreshCh, helicalScanCh, halfFieldCh, dentalPanoramicCh, autoTCh;
	
	private double actualFemalePhantomMass=0.0;
	private double actualMalePhantomMass=0.0;
	private double actualPhantomHeight=0.0;
	protected double scaleXY=0.0;
	protected double scaleZ=0.0;
	
	//=================
	protected boolean isOkToSave=false;
	protected String effectiveDose="";//0.0;888
	protected String effectiveDoseUnit="";//8888
	protected String risk="";//0.0;8888
	
	protected String DLP_toSave="";
	
	//protected String KAP_toSave="";//444
	protected String FSD_toSave="";//444
	//protected String KAIR_toSave="";//444
	//protected String ESAK_toSave="";//555
	protected String DRL_toSave="";//555
	
	protected String exam_toSave="";//55555
	//protected String projection_toSave="";//555555	
	protected String phantomSex_toSave="";//333
	protected String filtration_toSave="";//555
	protected String phantomAge_toSave="";//333
	protected String phantomMass_toSave="";//333
	protected String phantomHeight_toSave="";//333
	protected String kv_toSave="";//5555
	protected String anodeAngle_toSave="";//
	protected String ripple_toSave="";//
	protected String resultTest_toSave="";//555
	protected String unc_toSave="";//555
	
	protected String sliceThickness_toSave="";
	protected String pitch_toSave="";
	protected String CTDI_toSave="";
	protected String CTDIvol_toSave="";
	protected String fanBeam_toSave="";
	protected String rotationAngleIncrement_toSave="";
	
	private Connection radqcdbcon = null;
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame frame
	 */
	public DosimetryFrameCt(RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("DosimetryFrameCt.NAME"));
		//DBConnection.startDerby();//just in case is closed
		//===============
		mainDB=mf.radqcDB;
		dosimetryTable=mf.dosimetryTable;		
    	measurementDate_toSave=mf.measurementDate;
    	IDLINK=mf.IDLINK;
    	EXAMINATION_ID=mf.EXAMINATION_ID;
    	//=========================
    	DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
    	//=================================
    	performQueryDB();//total filtration here
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JPanel createMainPanel() {
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		textArea.setText("");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		//to make textArea to auto scroll as it is filled:
		DefaultCaret caret = (DefaultCaret) textArea.getCaret(); 
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//=================================================
		
		JPanel resultP = new JPanel(new BorderLayout());
		resultP.add(new JScrollPane(textArea), BorderLayout.CENTER);
		resultP.setPreferredSize(textAreaDimension);
		resultP.setBackground(RadQCFrame.bkgColor);
		
		graphicSceneAutoRefreshCh=
			new JCheckBox(resources.getString("autorefresh.ch"),true);
		helicalScanCh=
			new JCheckBox(resources.getString("helicalscan.ch"),true);
		
		halfFieldCh=
			new JCheckBox(resources.getString("halffield.ch"),false);
		dentalPanoramicCh=
			new JCheckBox(resources.getString("dentalpanoramic.ch"),false);
		autoTCh=
			new JCheckBox(resources.getString("autoT.ch"),true);
		
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		estimatedMeasurementUncertaintyTf.setText("5");
		sliceThicknessTf.setText("1.0");
		rotationAngleIncrementTf.setText("1.0");
		pitchFactorTf.setText("1.0");
		ctdiTf.setText("20000");//uGy
		ctdiVolTf.setText("20000");//uGy
		
	    JPanel puncP=new JPanel();
		puncP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("expData.unc"));
        puncP.add(label);
        puncP.add(estimatedMeasurementUncertaintyTf);        
        puncP.setBackground(RadQCFrame.bkgColor);
        
		//String[] projItems = (String[])resources.getObject("projection.cb");
		//projectionCb=new JComboBox(projItems);
		//projectionCb.setMaximumRowCount(5);
		//projectionCb.setPreferredSize(sizeCb);
		//projectionCb.setSelectedItem((String)"AP");
		//projectionAngle=180;
        String[] fanItems = (String[])resources.getObject("fanBeam.cb");
		fanBeamCb=new JComboBox(fanItems);
		fanBeamCb.setMaximumRowCount(5);
		fanBeamCb.setPreferredSize(sizeCb);
		fanBeamCb.setSelectedItem((String)"on");
		
		String[] examItems = (String[])resources.getObject("examination.cb");
		examinationCb=new JComboBox(examItems);
		examinationCb.setMaximumRowCount(5);
		examinationCb.setPreferredSize(sizeCb2);
		examinationCb.setSelectedItem((String)"Head");
		
		//String[] yesnoItems = (String[])resources.getObject("usemas.cb");
		//usemasCb=new JComboBox(yesnoItems);
		//usemasCb.setMaximumRowCount(5);
		//usemasCb.setPreferredSize(sizeCb);
		//usemasCb.setSelectedIndex(1);//no
		
		String[] comboItems = (String[])resources.getObject("phantomSex.cb");
		phantomSexCb=new JComboBox(comboItems);
		phantomSexCb.setMaximumRowCount(5);
		phantomSexCb.setPreferredSize(sizeCb);
		//phantomSexCb.addItemListener(this);//after Rb are defined!!!!!		
		phantomSexCb.setSelectedIndex(1);//Female
		
		newbornRb = new JRadioButton(resources.getString("ageGroup.newborn.rb"));		
		newbornRb.setBackground(RadQCFrame.bkgColor);
		newbornRb.setForeground(RadQCFrame.foreColor);
		
		y1Rb = new JRadioButton(resources.getString("ageGroup.1y.rb"));		
		y1Rb.setBackground(RadQCFrame.bkgColor);
		y1Rb.setForeground(RadQCFrame.foreColor);
		
		y5Rb = new JRadioButton(resources.getString("ageGroup.5y.rb"));		
		y5Rb.setBackground(RadQCFrame.bkgColor);
		y5Rb.setForeground(RadQCFrame.foreColor);
		
		y10Rb = new JRadioButton(resources.getString("ageGroup.10y.rb"));		
		y10Rb.setBackground(RadQCFrame.bkgColor);
		y10Rb.setForeground(RadQCFrame.foreColor);
		
		y15Rb = new JRadioButton(resources.getString("ageGroup.15y.rb"));		
		y15Rb.setBackground(RadQCFrame.bkgColor);
		y15Rb.setForeground(RadQCFrame.foreColor);
		
		adultRb = new JRadioButton(resources.getString("ageGroup.adult.rb"));		
		adultRb.setBackground(RadQCFrame.bkgColor);
		adultRb.setForeground(RadQCFrame.foreColor);
		
		ButtonGroup group = new ButtonGroup();
		group.add(newbornRb);
		group.add(y1Rb);
		group.add(y5Rb);
		group.add(y10Rb);
		group.add(y15Rb);
		group.add(adultRb);
		adultRb.setSelected(true);
		ageGroup="adult";
		ageGroupIndex=5;
		actualFemalePhantomMass = 71.4614;//73.3201*kg;
		actualMalePhantomMass = 69.8885;//71.7475*kg;
		actualPhantomHeight = 178.60;
		
		newbornRb.setActionCommand(PHANTOMDEFAULTS_COMMAND);
		newbornRb.addActionListener(this);
		y1Rb.setActionCommand(PHANTOMDEFAULTS_COMMAND);
		y1Rb.addActionListener(this);
		y5Rb.setActionCommand(PHANTOMDEFAULTS_COMMAND);
		y5Rb.addActionListener(this);
		y10Rb.setActionCommand(PHANTOMDEFAULTS_COMMAND);
		y10Rb.addActionListener(this);
		y15Rb.setActionCommand(PHANTOMDEFAULTS_COMMAND);
		y15Rb.addActionListener(this);
		adultRb.setActionCommand(PHANTOMDEFAULTS_COMMAND);
		adultRb.addActionListener(this);
		phantomSexCb.addItemListener(this);//here!

		//kvCb
		int indx = 0;
		int sup=150;
		int inf =30;
		int len = sup-inf+1;//(last-first):step + 1
		String [] kvItems = new String[len];
		for (int j = inf; j<=sup; j++){
			String is = Convertor.intToString(j);
			indx = j-inf;
			kvItems[indx]=is;
		}
		kvCb=new JComboBox(kvItems);
		kvCb.setMaximumRowCount(5);
		kvCb.setPreferredSize(sizeCb);
		kvCb.setSelectedItem((String)"120");
		
		//anodeAngleCb
		sup=22;
		inf =6;
		len = sup-inf+1;//(last-first):step + 1
		String [] anodeAngleItems = new String[len];
		for (int j = inf; j<=sup; j++){
			String is = Convertor.intToString(j);
			indx = j-inf;
			anodeAngleItems[indx]=is;
		}
		anodeAngleCb=new JComboBox(anodeAngleItems);
		anodeAngleCb.setMaximumRowCount(5);
		anodeAngleCb.setPreferredSize(sizeCb);
		anodeAngleCb.setSelectedItem((String)"17");
		//waveform
		sup=30;
		inf =0;
		len = (sup-inf)/5+1;//(last-first):step + 1
		String [] rippleItems = new String[len];
		for (int j = inf; j<=sup; j=j+5){
			String is = Convertor.intToString(j);
			indx = (j-inf)/5;
			rippleItems[indx]=is;
			//System.out.println("indx= "+indx+"; value= "+is);			
		}
		rippleCb=new JComboBox(rippleItems);
		rippleCb.setMaximumRowCount(5);
		rippleCb.setPreferredSize(sizeCb);
		rippleCb.setSelectedItem((String)"0");
		
		//other init
		phantomAgeTf.setText("40.0");
		phantomHeightTf.setText("178.6");
		phantomWeightTf.setText("71.4614");
		filtrationTf.setText(totalFiltrationStr);
		focusmidplaneTf.setText("36");
		runsTf.setText("100000");
		bsfTf.setText("1.3");
		bsfTf.setToolTipText(resources.getString("BSF.tooltip"));
		
		//Phantom selection
		JPanel sexP=new JPanel();
		sexP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("phantomSex.label"));
        sexP.add(label);
        sexP.add(phantomSexCb);        
        sexP.setBackground(RadQCFrame.bkgColor);
        
        JPanel ageGroupP = new JPanel();
        ageGroupP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));        
        label=new JLabel(resources.getString("ageGroup.label"));
        ageGroupP.add(label);
        ageGroupP.add(newbornRb);
        ageGroupP.add(y1Rb);
        ageGroupP.add(y5Rb);
        ageGroupP.add(y10Rb);
        ageGroupP.add(y15Rb);
        ageGroupP.add(adultRb);
        ageGroupP.setBackground(RadQCFrame.bkgColor);
        
        JPanel phantomAgeP = new JPanel();
        phantomAgeP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        label=new JLabel(resources.getString("phantomAge.label"));
        phantomAgeP.add(label);
        phantomAgeP.add(phantomAgeTf);
        phantomAgeP.setBackground(RadQCFrame.bkgColor);
        
        //JPanel phantomHeightP = new JPanel();
        //phantomHeightP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        label=new JLabel(resources.getString("phantomHeight.label"));
        //phantomHeightP.add(label);
        //phantomHeightP.add(phantomHeightTf);
        //phantomHeightP.setBackground(RadQCFrame.bkgColor);
        //-----------
        phantomAgeP.add(label);
        phantomAgeP.add(phantomHeightTf);
        //-------------
        //JPanel phantomWeightP = new JPanel();
        //phantomWeightP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        label=new JLabel(resources.getString("phantomWeight.label"));
        //phantomWeightP.add(label);
        //phantomWeightP.add(phantomWeightTf);
        //phantomWeightP.setBackground(RadQCFrame.bkgColor);
        //--------------------
        phantomAgeP.add(label);
        phantomAgeP.add(phantomWeightTf);
        //-------------
        JPanel phantomP = new JPanel();
		BoxLayout blphantomP = new BoxLayout(phantomP, BoxLayout.Y_AXIS);
		phantomP.setLayout(blphantomP);
		phantomP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("phantom.border"),
				RadQCFrame.foreColor));		
		phantomP.add(sexP);
		phantomP.add(ageGroupP);
		phantomP.add(phantomAgeP);
		//phantomP.add(phantomHeightP);
		//phantomP.add(phantomWeightP);
		phantomP.setBackground(RadQCFrame.bkgColor);
		//=============
		//Tube settings
		JPanel kvP=new JPanel();
		kvP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("kv.label"));
        kvP.add(label);
        kvP.add(kvCb);        
        kvP.setBackground(RadQCFrame.bkgColor);
        		
		//JPanel anodeAngleP=new JPanel();
		//anodeAngleP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("anodeAngle.label"));
        //anodeAngleP.add(label);
        //anodeAngleP.add(anodeAngleCb);        
        //anodeAngleP.setBackground(RadQCFrame.bkgColor);
        //-----------
        kvP.add(label);
        kvP.add(anodeAngleCb);
        //----------------
        //JPanel rippleP=new JPanel();
        //rippleP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("ripple.label"));
        //rippleP.add(label);
        //rippleP.add(rippleCb);        
        //rippleP.setBackground(RadQCFrame.bkgColor);
        //-------------
        kvP.add(label);
        kvP.add(rippleCb);
        //-------------
        //JPanel mAsP=new JPanel();
        //mAsP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        //label=new JLabel(resources.getString("mAs.label"));
        //mAsP.add(label);
        //mAsP.add(mAsTf);        
        //mAsP.setBackground(RadQCFrame.bkgColor);
        
        JPanel filtrationP=new JPanel();
        filtrationP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("filtration.label"));
        filtrationP.add(label);
        filtrationP.add(filtrationTf);	
		buttonName = resources.getString("filtrationB");
		buttonToolTip = resources.getString("filtrationB.toolTip");
		buttonIconName = "";//resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, FILTRATION_COMMAND,//INITIAL TAKE IT FROM FROM DB!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("filtrationB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		//filtrationP.add(button);	
		filtrationP.setBackground(RadQCFrame.bkgColor);
		
        JPanel tubeP = new JPanel();
		BoxLayout bltubeP = new BoxLayout(tubeP, BoxLayout.Y_AXIS);
		tubeP.setLayout(bltubeP);
		tubeP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("tube.border"),
				RadQCFrame.foreColor));		
		tubeP.add(kvP);
		tubeP.add(filtrationP);	
		//tubeP.add(anodeAngleP);	
		//tubeP.add(rippleP);	
		//tubeP.add(mAsP);	
		tubeP.setBackground(RadQCFrame.bkgColor);
		//------------------------------
		//KAP and Examination settings
		//JPanel kapP=new JPanel();
		//kapP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        //label=new JLabel(resources.getString("kap.label"));
        //kapP.add(label);
        //kapP.add(kapTf);
        //buttonName = resources.getString("KAPB");
		//buttonToolTip = resources.getString("KAPB.toolTip");
		//buttonIconName = "";//resources.getString("img.set");
		//button = FrameUtilities.makeButton(buttonIconName, KAP_COMMAND,
		//		buttonToolTip, buttonName, this, this);
		//mnemonic = (Character) resources.getObject("KAPB.mnemonic");
		//button.setMnemonic(mnemonic.charValue());
        //kapP.add(button);
        //kapP.setBackground(RadQCFrame.bkgColor);
        
        //JPanel usemasP=new JPanel();
		//usemasP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        //label=new JLabel(resources.getString("usemas.label"));
        //usemasP.add(label);
        //usemasP.add(usemasCb);
        //usemasP.setBackground(RadQCFrame.bkgColor);
        
        JPanel distanceP=new JPanel();
        distanceP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("distance.label.ct"));
        distanceP.add(label);
        distanceP.add(focusmidplaneTf);
        distanceP.setBackground(RadQCFrame.bkgColor);
        
        JPanel sliceP=new JPanel();
        sliceP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("sliceThickness.label"));
        sliceP.add(label);
        sliceP.add(sliceThicknessTf);
        sliceP.setBackground(RadQCFrame.bkgColor);
        
        JPanel examinationP=new JPanel();
        examinationP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("examination.label"));
        examinationP.add(label);
        examinationP.add(examinationCb);
        
        examinationP.add(halfFieldCh);
        examinationP.add(dentalPanoramicCh);
        //label=new JLabel(resources.getString("projection.label"));
        //examinationP.add(label);
        //examinationP.add(projectionCb);
        examinationP.setBackground(RadQCFrame.bkgColor);
        //==============
        //autoTCh
        JPanel autoTP=new JPanel();
        autoTP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        autoTP.add(autoTCh);
        autoTP.setBackground(RadQCFrame.bkgColor);
        //================
        JPanel angleP=new JPanel();
        angleP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("rotationAngleIncrement.label"));
        angleP.add(label);
        angleP.add(rotationAngleIncrementTf);
        angleP.add(helicalScanCh);
        angleP.setBackground(RadQCFrame.bkgColor);
        
        JPanel pitchP=new JPanel();
        pitchP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("pitchFactor.label"));
        pitchP.add(label);
        pitchP.add(pitchFactorTf);
        label=new JLabel(resources.getString("fanBeam.label"));
        pitchP.add(label);
        pitchP.add(fanBeamCb);
        pitchP.setBackground(RadQCFrame.bkgColor);
        
        JPanel ctdiP=new JPanel();
        ctdiP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("ctdi.label"));
        ctdiP.add(label);
        ctdiP.add(ctdiTf);
        ctdiP.setBackground(RadQCFrame.bkgColor);
        
        JPanel ctdiVolP=new JPanel();
        ctdiVolP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("ctdiVol.label"));
        ctdiVolP.add(label);
        ctdiVolP.add(ctdiVolTf);
        buttonName = resources.getString("CTDIB");
		buttonToolTip = resources.getString("CTDIB.toolTip");
		buttonIconName = "";//resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, KAP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("CTDIB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
        ctdiVolP.add(button);
        ctdiVolP.setBackground(RadQCFrame.bkgColor);
        //============================
        JPanel examP = new JPanel();
		BoxLayout blexamP = new BoxLayout(examP, BoxLayout.Y_AXIS);
		examP.setLayout(blexamP);
		examP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("exam.ct.border"),
				RadQCFrame.foreColor));		
		examP.add(examinationP);
		examP.add(sliceP);//(kapP);
		examP.add(autoTP);//!!!!!!!!!!!!!!!!!!!!!!
		examP.add(angleP);//usemasP);
		examP.add(pitchP);
		examP.add(ctdiP);examP.add(ctdiVolP);
		examP.add(distanceP);	
		//examP.add(examinationP);	
		examP.setBackground(RadQCFrame.bkgColor);
		//===============
		
		//JPanel p41P=new JPanel();
        //p41P.setLayout(new FlowLayout(FlowLayout.CENTER));
        //label=new JLabel(resources.getString("BSF.label"));
        //p41P.add(label);
        //p41P.add(bsfTf);
        //p41P.setBackground(RadQCFrame.bkgColor);
        
		JPanel runsP=new JPanel();
        runsP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("runs.label"));
        runsP.add(label);
        runsP.add(runsTf);runsP.add(graphicSceneAutoRefreshCh);//////////////
		buttonName = resources.getString("calcB");
		buttonToolTip = resources.getString("calcB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		runsP.add(button);	
		buttonName = resources.getString("saveB");
		buttonToolTip = resources.getString("saveB.toolTip");
		buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, SAVE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		runsP.add(button);	
		runsP.setBackground(RadQCFrame.bkgColor);
		
		JPanel northP = new JPanel();
		BoxLayout blnorthP = new BoxLayout(northP, BoxLayout.Y_AXIS);
		northP.setLayout(blnorthP);
		northP.add(phantomP);
		northP.add(tubeP);	
		northP.add(examP);	
		//northP.add(p41P);
		northP.add(puncP);	
		northP.add(runsP);	
		northP.setBackground(RadQCFrame.bkgColor);
		//=======================
		JPanel mainPanel=new JPanel(new BorderLayout());
		mainPanel.add(northP, BorderLayout.NORTH);//CENTER);
		mainPanel.add(resultP, BorderLayout.CENTER);
		mainPanel.setBackground(RadQCFrame.bkgColor);
		return mainPanel;
	}
	
	/**
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		command = e.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {			
			compute();
		} else if (command.equals(FILTRATION_COMMAND)) {
			filtration();
		} else if (command.equals(KAP_COMMAND)) {
			kapEvaluation();
		} else if (command.equals(PHANTOMDEFAULTS_COMMAND)){
			updateDefaults();
		} else if (command.equals(SAVE_COMMAND)){
			save();
		} 
	}

	/**
	 * JCombobox specific actions are set here
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource()==phantomSexCb)
	    {
			changeSex();
			//System.out.println("mamama");
	    }
	}
	
	/**
	 * Go to save in database
	 */
	private void save(){
		new SaveViewDBFrameCt(this);
	}
	
	/**
	 * Initialize database. Retrieve tube total filtration (if available).
	 */
	private void performQueryDB(){
		totalFiltrationStr="";
		try {
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;

			//int dummy=1;
			String str = "select * from " + mf.hvlFiltrationTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			
			Statement s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet res = s.executeQuery(str);
			res.last();//last record
			
			totalFiltrationStr=res.getString(8);

			if (res != null)
				res.close();
			if (s != null)
				s.close();
			
			//if (con1 != null)
				//con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (totalFiltrationStr.equals("")){
			totalFiltrationStr="2.5";
		}
	}
	
	/**
	 * Go to CTDI evaluation
	 */
	private void kapEvaluation(){
		String sliceThickness = sliceThicknessTf.getText();//request.getParameter("sliceThickness");	
		sliceThickness_toSave=sliceThickness;//for reference
		try {
			if (Convertor.stringToDouble(sliceThickness) <= 0.0) {
				 String title =resources.getString("dialog.number.title");
				 String message =resources.getString("dialog.sliceThickness.message");
				 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
			 String message =resources.getString("dialog.sliceThickness.message");
			 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String pitch = pitchFactorTf.getText();//request.getParameter("pitch");	
		pitch_toSave=pitch;
		try {
			if (Convertor.stringToDouble(pitch) <= 0.0) {
				String title =resources.getString("dialog.number.title");
				 String message =resources.getString("dialog.pitchFactor.message");
				 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
			 String message =resources.getString("dialog.pitchFactor.message");
			 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		new CTDIEvalFrame(this);
	}
	
	/**
	 * Go to filtration calculation based on attenuators. This is not longer required 
	 * since filtration is taken from database (if HVL QC test was performed).
	 */
	private void filtration(){
		//new TubeAttenuators(this);
	}
	
	/**
	 * Update default phantom variables for various age groups.
	 */
	private void updateDefaults(){
		String[] comboItems = (String[])resources.getObject("phantomSex.cb");		
		String comboValue=(String)phantomSexCb.getSelectedItem();
		double mass=0.0;
		double age=0.0;
		double height=0.0;
		
		if (comboValue.equals(comboItems[0])){//Male
			if (newbornRb.isSelected()){
				mass=3.43447;height=50.96;age=0.0;ageGroup="newborn";ageGroupIndex=0;
				actualFemalePhantomMass = 3.50105;
				actualMalePhantomMass = 3.43447;
				actualPhantomHeight = 50.96;				
			}
			else if (y1Rb.isSelected()){
				mass=9.11146;height=74.41;age=1.0;ageGroup="age_1";ageGroupIndex=1;
				actualFemalePhantomMass = 9.28103;
				actualMalePhantomMass = 9.11146;
				actualPhantomHeight = 74.41;
			}
			else if (y5Rb.isSelected()){
				mass=18.7723;height=109.11;age=5.0;ageGroup="age_5";ageGroupIndex=2;
				actualFemalePhantomMass = 19.1091;
				actualMalePhantomMass = 18.7723;
				actualPhantomHeight = 109.11;
			}
			else if (y10Rb.isSelected()){
				mass=31.3504;height=139.77;age=10.0;ageGroup="age_10";ageGroupIndex=3;
				actualFemalePhantomMass = 31.9078;
				actualMalePhantomMass = 31.3504;
				actualPhantomHeight = 139.77;
			}
			else if (y15Rb.isSelected()){
				mass=53.726;height=168.07;age=15.0;ageGroup="age_15";ageGroupIndex=4;
				actualFemalePhantomMass = 55.0503;
				actualMalePhantomMass = 53.726;
				actualPhantomHeight = 168.07;
			}
			else if (adultRb.isSelected()){
				mass=69.8885;height=178.6;age=40.0;ageGroup="adult";ageGroupIndex=5;
				actualFemalePhantomMass = 71.4614;//73.3201*kg;
				actualMalePhantomMass = 69.8885;//71.7475*kg;
				actualPhantomHeight = 178.60;
			}
		} else {
			if (newbornRb.isSelected()){
				mass=3.50105;height=50.96;age=0.0;ageGroup="newborn";ageGroupIndex=0;
				actualFemalePhantomMass = 3.50105;
				actualMalePhantomMass = 3.43447;
				actualPhantomHeight = 50.96;
			}
			else if (y1Rb.isSelected()){
				mass=9.28103;height=74.41;age=1.0;ageGroup="age_1";ageGroupIndex=1;
				actualFemalePhantomMass = 9.28103;
				actualMalePhantomMass = 9.11146;
				actualPhantomHeight = 74.41;
			}
			else if (y5Rb.isSelected()){
				mass=19.1091;height=109.11;age=5.0;ageGroup="age_5";ageGroupIndex=2;
				actualFemalePhantomMass = 19.1091;
				actualMalePhantomMass = 18.7723;
				actualPhantomHeight = 109.11;
			}
			else if (y10Rb.isSelected()){
				mass=31.9078;height=139.77;age=10.0;ageGroup="age_10";ageGroupIndex=3;
				actualFemalePhantomMass = 31.9078;
				actualMalePhantomMass = 31.3504;
				actualPhantomHeight = 139.77;
			}
			else if (y15Rb.isSelected()){
				mass=55.0503;height=168.07;age=15.0;ageGroup="age_15";ageGroupIndex=4;
				actualFemalePhantomMass = 55.0503;
				actualMalePhantomMass = 53.726;
				actualPhantomHeight = 168.07;
			}
			else if (adultRb.isSelected()){
				mass=71.4614;height=178.6;age=40.0;ageGroup="adult";ageGroupIndex=5;
				actualFemalePhantomMass = 71.4614;//73.3201*kg;
				actualMalePhantomMass = 69.8885;//71.7475*kg;
				actualPhantomHeight = 178.60;
			}
		}
		
		phantomWeightTf.setText(Convertor.doubleToString(mass));
		phantomAgeTf.setText(Convertor.doubleToString(age));
		phantomHeightTf.setText(Convertor.doubleToString(height));
	}
	
	/**
	 * Update phantom variables based on patient sex
	 */
	private void changeSex(){
		String[] comboItems = (String[])resources.getObject("phantomSex.cb");		
		String comboValue=(String)phantomSexCb.getSelectedItem();
		double mass=0.0;		
		if (comboValue.equals(comboItems[0])){//Male
			if (newbornRb.isSelected())
				mass=3.43447;
			else if (y1Rb.isSelected())
				mass=9.11146;
			else if (y5Rb.isSelected())
				mass=18.7723;
			else if (y10Rb.isSelected())
				mass=31.3504;
			else if (y15Rb.isSelected())
				mass=53.726;
			else if (adultRb.isSelected())
				mass=69.8885;
		} else {
			if (newbornRb.isSelected())
				mass=3.50105;
			else if (y1Rb.isSelected())
				mass=9.28103;
			else if (y5Rb.isSelected())
				mass=19.1091;
			else if (y10Rb.isSelected())
				mass=31.9078;
			else if (y15Rb.isSelected())
				mass=55.0503;
			else if (adultRb.isSelected())
				mass=71.4614;
		}
		
		phantomWeightTf.setText(Convertor.doubleToString(mass));
	}
	
	//protected double calculateScaleXY(String phantomSex, double phantomMass, double phantomHeight){
	/**
	 * Computes scale parameter on XY axis (used by patient organs) based on patient mass and height
	 * @param phantomMass phantomMass 
	 * @param phantomHeight phantomHeight
	 * @return the result
	 */
	protected double calculateScaleXY(double phantomMass, double phantomHeight){
		String[] comboItems = (String[])resources.getObject("phantomSex.cb");		
		String comboValue=(String)phantomSexCb.getSelectedItem();
		
		//String maleS=resources.getString("phantomSex.male");
		double scale=0.0;		
		if (comboValue.equals(comboItems[0])){//Male
			scale = Math.sqrt(actualPhantomHeight*phantomMass/(phantomHeight*actualMalePhantomMass));
		}else{
			scale =Math.sqrt(actualPhantomHeight*phantomMass/(phantomHeight*actualFemalePhantomMass));
		}
		return scale;
	}
	
	/**
	 * Computes scale parameter on Z axis (used by patient organs) based on patient height
	 * @param phantomHeight phantomHeight
	 * @return the result
	 */
	protected double calculateScaleZ(double phantomHeight){
		double scale=phantomHeight/actualPhantomHeight;
		return scale;
	}
	
	/**
	 * Get XY scale parameter
	 * @return the result
	 */
	protected double getScaleXY(){
		return scaleXY;
	}
	
	/**
	 * Get Z scale parameter
	 * @return the result
	 */
	protected double getScaleZ(){
		return scaleZ;
	}
	
	/**
	 * Get patient age group
	 * @return the result
	 */
	protected String getAgeGroup(){
		return ageGroup;
	}
	
	/**
	 * Get patient age group index
	 * @return the result
	 */
	protected int getAgeGroupIndex(){
		return ageGroupIndex;
	}
		
	//get Focus to phantom central axis distance from focus to table distance
	//also get focus to patient entrance.
	/**
	 * Return the focus to phantom central axis distance and focus to patient entrance from given focus to table distance.
	 * @param SID SID, source to image distance meaning source to table distance here
	 * @param ageGroup ageGroup
	 * @param projection projection
	 * @param scaleXY scaleXY
	 * @return the result
	 */
	protected double[] getFCAandFSDfromSID(double SID, String ageGroup, double projection, double scaleXY){
		double FCA=0.0;
		double FSD=0.0;
		double[] result = new double[2];
		
		if (ageGroup.compareTo("adult")==0){
			if (projection==0 || projection==180){//AP.PA
				FCA=SID-10.0*scaleXY;
				FSD=FCA-10.0*scaleXY;
			} else {
				FCA=SID-20.0*scaleXY;
				FSD=FCA-20.0*scaleXY;
			}
		} else if (ageGroup.compareTo("age_15")==0){
			if (projection==0 || projection==180){//AP.PA
				FCA=SID-9.8*scaleXY;
				FSD=FCA-9.8*scaleXY;
			} else {
				FCA=SID-17.25*scaleXY;
				FSD=FCA-17.25*scaleXY;
			}
		} else if (ageGroup.compareTo("age_10")==0){
			if (projection==0 || projection==180){//AP.PA
				FCA=SID-8.4*scaleXY;
				FSD=FCA-8.4*scaleXY;
			} else {
				FCA=SID-13.9*scaleXY;
				FSD=FCA-13.9*scaleXY;
			}
		} else if (ageGroup.compareTo("age_5")==0){
			if (projection==0 || projection==180){//AP.PA
				FCA=SID-7.5*scaleXY;
				FSD=FCA-7.5*scaleXY;
			} else {
				FCA=SID-11.45*scaleXY;
				FSD=FCA-11.45*scaleXY;
			}
		} else if (ageGroup.compareTo("age_1")==0){
			if (projection==0 || projection==180){//AP.PA
				FCA=SID-6.5*scaleXY;
				FSD=FCA-6.5*scaleXY;
			} else {
				FCA=SID-8.8*scaleXY;
				FSD=FCA-8.8*scaleXY;
			}
		} else if (ageGroup.compareTo("newborn")==0){
			if (projection==0 || projection==180){//AP.PA
				FCA=SID-4.9*scaleXY;
				FSD=FCA-4.9*scaleXY;
			} else {
				FCA=SID-6.35*scaleXY;
				FSD=FCA-6.35*scaleXY;
			}
		}
		
		result[0]=FCA;
		result[1]=FSD;
		return result;
	}
	
	/**
	 * Get FSD (focus to skin distance or focus to patient entrance) from focus to phantom central axis distance.
	 * @param FCA FCA
	 * @param ageGroup ageGroup
	 * @param projection projection
	 * @param scaleXY scaleXY
	 * @return the result
	 */
	protected double getFSDfromFCA(double FCA, String ageGroup, double projection, double scaleXY){
		
		double FSD=0.0;
				
		if (ageGroup.compareTo("adult")==0){
			if (projection==0 || projection==180){//AP.PA
				FSD=FCA-10.0*scaleXY;
			} else {
				FSD=FCA-20.0*scaleXY;
			}
		} else if (ageGroup.compareTo("age_15")==0){
			if (projection==0 || projection==180){//AP.PA
				FSD=FCA-9.8*scaleXY;
			} else {
				FSD=FCA-17.25*scaleXY;
			}
		} else if (ageGroup.compareTo("age_10")==0){
			if (projection==0 || projection==180){//AP.PA
				FSD=FCA-8.4*scaleXY;
			} else {
				FSD=FCA-13.9*scaleXY;
			}
		} else if (ageGroup.compareTo("age_5")==0){
			if (projection==0 || projection==180){//AP.PA
				FSD=FCA-7.5*scaleXY;
			} else {
				FSD=FCA-11.45*scaleXY;
			}
		} else if (ageGroup.compareTo("age_1")==0){
			if (projection==0 || projection==180){//AP.PA
				FSD=FCA-6.5*scaleXY;
			} else {
				FSD=FCA-8.8*scaleXY;
			}
		} else if (ageGroup.compareTo("newborn")==0){
			if (projection==0 || projection==180){//AP.PA
				FSD=FCA-4.9*scaleXY;
			} else {
				FSD=FCA-6.35*scaleXY;
			}
		}
				
		return FSD;
	}
	
	/**
	 * For given examination and age group, return the field parameters (XRay field center - Y, 
	 * XRay field width, XRay field height, XRay field center - X). Y is the coordinate of XRay center relative to phantom center on "height" direction. 
	 * X is the coordinate of XRay center relative to phantom center on "width" direction.
	 * @param exam the examination
	 * @param ageGroup the age group
	 * @return the result
	 */
	private String[] getFieldsParameters(String exam, String ageGroup){
		String[] result=new String[4];
		if (ageGroup.compareTo("adult")==0){
		if (exam.compareTo("Head")==0){
			result[0]="85.0 cm";//centerY="80.0 cm";
			result[1]="19.0 cm";//width="20.0 cm";
			result[2]="27.0 cm";//height="27.0 cm";
			result[3]="0.0 cm";//centerX
		} else if (exam.compareTo("Abdomen")==0){
			result[0]="25.0 cm";//centerY="25.0 cm";
			result[1]="35.0 cm";//width="35.0 cm";
			result[2]="35.0 cm";//height="35.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("CervicalSpine")==0){
			result[0]="75.0 cm";//centerY="70.0 cm";
			result[1]="13.0 cm";//width="20.0 cm";
			result[2]="20.0 cm";//height="20.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("Chest")==0){
			result[0]="52.0 cm";//centerY="52.0 cm";
			result[1]="35.0 cm";//width="35.0 cm";
			result[2]="32.0 cm";//height="32.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("DentalPanoramic")==0){
			result[0]="82.0 cm";//centerY="80.0 cm";
			result[1]="17.0 cm";//width="17.0 cm";
			result[2]="8.0 cm";//height="8.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("Femur")==0){
			result[0]="-18.0 cm";//centerY="18.0 cm";
			result[1]="20.0 cm";//width="20.0 cm";
			result[2]="25.0 cm";//height="25.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("FullSpine")==0){
			result[0]="53.0 cm";//centerY="50.0 cm";
			result[1]="13.0 cm";//width="20.0 cm";
			result[2]="65.0 cm";//height="65.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("ThoracicSpine")==0){
			result[0]="54.0 cm";//centerY="52.0 cm";
			result[1]="13.0 cm";//width="20.0 cm";
			result[2]="25.0 cm";//height="35.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("LumbarSpine")==0){
			result[0]="38.0 cm";//centerY="32.0 cm";
			result[1]="13.0 cm";//width="20.0 cm";
			result[2]="31.0 cm";//height="30.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("LumboSacralJunction")==0){
			result[0]="27.0 cm";//centerY="27.0 cm";
			result[1]="13.0 cm";//width="20.0 cm";
			result[2]="20.0 cm";//height="20.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("Shoulder")==0){
			result[0]="68.0 cm";//centerY="68.0 cm";
			result[1]="20.0 cm";//width="20.0 cm";
			result[2]="17.0 cm";//height="17.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("HipJoint")==0){
			result[0]="0.0 cm";//centerY="10.0 cm";
			result[1]="20.0 cm";//width="20.0 cm";
			result[2]="20.0 cm";//height="20.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("Knee")==0){
			result[0]="-27.0 cm";//centerY="27.0 cm";
			result[1]="20.0 cm";//width="20.0 cm";
			result[2]="20.0 cm";//height="20.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("Pelvis")==0){
			result[0]="21.0 cm";//centerY="16.0 cm";
			result[1]="32.0 cm";//width="35.0 cm";
			result[2]="34.0 cm";//height="32.0 cm";
			result[3]="0.0 cm";//centerX
		}else if (exam.compareTo("WholeBody")==0){
			result[0]="0.0 cm";//centerY="16.0 cm";
			result[1]="40.0 cm";//width="35.0 cm";
			result[2]="178.6 cm";//height="32.0 cm";
			result[3]="0.0 cm";//centerX
		}
		}
		else if (ageGroup.compareTo("age_15")==0){
			if (exam.compareTo("Head")==0){
				result[0]="79.0 cm";//centerY="80.0 cm";
				result[1]="19.0 cm";//width="20.0 cm";
				result[2]="23.0 cm";//height="27.0 cm";
				result[3]="0.0 cm";//centerX
			} else if (exam.compareTo("Abdomen")==0){
				result[0]="23.0 cm";//centerY="25.0 cm";
				result[1]="33.0 cm";//width="35.0 cm";
				result[2]="33.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("CervicalSpine")==0){
				result[0]="70.0 cm";//centerY="70.0 cm";
				result[1]="11.0 cm";//width="20.0 cm";
				result[2]="17.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Chest")==0){
				result[0]="47.0 cm";//centerY="52.0 cm";
				result[1]="30.0 cm";//width="35.0 cm";
				result[2]="29.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("DentalPanoramic")==0){
				result[0]="74.0 cm";//centerY="80.0 cm";
				result[1]="15.0 cm";//width="17.0 cm";
				result[2]="7.0 cm";//height="8.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Femur")==0){
				result[0]="-16.0 cm";//centerY="18.0 cm";
				result[1]="19.0 cm";//width="20.0 cm";
				result[2]="21.0 cm";//height="25.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("FullSpine")==0){
				result[0]="48.0 cm";//centerY="50.0 cm";
				result[1]="11.0 cm";//width="20.0 cm";
				result[2]="58.0 cm";//height="65.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("ThoracicSpine")==0){
				result[0]="50.0 cm";//centerY="52.0 cm";
				result[1]="11.0 cm";//width="20.0 cm";
				result[2]="22.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumbarSpine")==0){
				result[0]="35.0 cm";//centerY="32.0 cm";
				result[1]="11.0 cm";//width="20.0 cm";
				result[2]="30.0 cm";//height="30.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumboSacralJunction")==0){
				result[0]="22.0 cm";//centerY="27.0 cm";
				result[1]="11.0 cm";//width="20.0 cm";
				result[2]="18.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Shoulder")==0){
				result[0]="60.0 cm";//centerY="68.0 cm";
				result[1]="18.0 cm";//width="20.0 cm";
				result[2]="16.0 cm";//height="17.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("HipJoint")==0){
				result[0]="0.0 cm";//centerY="10.0 cm";
				result[1]="18.0 cm";//width="20.0 cm";
				result[2]="18.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Knee")==0){
				result[0]="-26.0 cm";//centerY="27.0 cm";
				result[1]="18.0 cm";//width="20.0 cm";
				result[2]="18.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Pelvis")==0){
				result[0]="19.0 cm";//centerY="16.0 cm";
				result[1]="28.0 cm";//width="35.0 cm";
				result[2]="31.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("WholeBody")==0){
				result[0]="0.0 cm";//centerY="16.0 cm";
				result[1]="34.5 cm";//width="35.0 cm";
				result[2]="168.06 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}
			}
		else if (ageGroup.compareTo("age_10")==0){
			if (exam.compareTo("Head")==0){
				result[0]="63.0 cm";//centerY="80.0 cm";
				result[1]="17.0 cm";//width="20.0 cm";
				result[2]="22.0 cm";//height="27.0 cm";
				result[3]="0.0 cm";//centerX
			} else if (exam.compareTo("Abdomen")==0){
				result[0]="18.0 cm";//centerY="25.0 cm";
				result[1]="27.0 cm";//width="35.0 cm";
				result[2]="27.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("CervicalSpine")==0){
				result[0]="57.0 cm";//centerY="70.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="14.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Chest")==0){
				result[0]="39.0 cm";//centerY="52.0 cm";
				result[1]="25.0 cm";//width="35.0 cm";
				result[2]="22.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("DentalPanoramic")==0){
				result[0]="58.0 cm";//centerY="80.0 cm";
				result[1]="14.0 cm";//width="17.0 cm";
				result[2]="6.0 cm";//height="8.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Femur")==0){
				result[0]="-14.0 cm";//centerY="18.0 cm";
				result[1]="16.0 cm";//width="20.0 cm";
				result[2]="20.0 cm";//height="25.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("FullSpine")==0){
				result[0]="39.0 cm";//centerY="50.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="45.0 cm";//height="65.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("ThoracicSpine")==0){
				result[0]="40.0 cm";//centerY="52.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="18.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumbarSpine")==0){
				result[0]="27.0 cm";//centerY="32.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="24.0 cm";//height="30.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumboSacralJunction")==0){
				result[0]="16.0 cm";//centerY="27.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="14.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Shoulder")==0){
				result[0]="50.0 cm";//centerY="68.0 cm";
				result[1]="16.0 cm";//width="20.0 cm";
				result[2]="12.0 cm";//height="17.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("HipJoint")==0){
				result[0]="0.0 cm";//centerY="10.0 cm";
				result[1]="16.0 cm";//width="20.0 cm";
				result[2]="16.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Knee")==0){
				result[0]="-24.0 cm";//centerY="27.0 cm";
				result[1]="16.0 cm";//width="20.0 cm";
				result[2]="16.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Pelvis")==0){
				result[0]="15.0 cm";//centerY="16.0 cm";
				result[1]="23.0 cm";//width="35.0 cm";
				result[2]="25.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("WholeBody")==0){
				result[0]="0.0 cm";//centerY="16.0 cm";
				result[1]="27.8 cm";//width="35.0 cm";
				result[2]="139.77 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}
			}
		else if (ageGroup.compareTo("age_5")==0){
			if (exam.compareTo("Head")==0){
				result[0]="52.0 cm";//centerY="80.0 cm";
				result[1]="17.0 cm";//width="20.0 cm";
				result[2]="20.0 cm";//height="27.0 cm";
				result[3]="0.0 cm";//centerX
			} else if (exam.compareTo("Abdomen")==0){
				result[0]="14.0 cm";//centerY="25.0 cm";
				result[1]="21.0 cm";//width="35.0 cm";
				result[2]="21.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("CervicalSpine")==0){
				result[0]="43.0 cm";//centerY="70.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="12.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Chest")==0){
				result[0]="31.0 cm";//centerY="52.0 cm";
				result[1]="19.0 cm";//width="35.0 cm";
				result[2]="18.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("DentalPanoramic")==0){
				result[0]="47.0 cm";//centerY="80.0 cm";
				result[1]="13.0 cm";//width="17.0 cm";
				result[2]="5.0 cm";//height="8.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Femur")==0){
				result[0]="-12.0 cm";//centerY="18.0 cm";
				result[1]="14.0 cm";//width="20.0 cm";
				result[2]="17.0 cm";//height="25.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("FullSpine")==0){
				result[0]="32.0 cm";//centerY="50.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="38.0 cm";//height="65.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("ThoracicSpine")==0){
				result[0]="32.0 cm";//centerY="52.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="14.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumbarSpine")==0){
				result[0]="22.0 cm";//centerY="32.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="18.0 cm";//height="30.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumboSacralJunction")==0){
				result[0]="14.0 cm";//centerY="27.0 cm";
				result[1]="9.0 cm";//width="20.0 cm";
				result[2]="8.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Shoulder")==0){
				result[0]="40.0 cm";//centerY="68.0 cm";
				result[1]="14.0 cm";//width="20.0 cm";
				result[2]="9.0 cm";//height="17.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("HipJoint")==0){
				result[0]="0.0 cm";//centerY="10.0 cm";
				result[1]="10.0 cm";//width="20.0 cm";
				result[2]="10.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Knee")==0){
				result[0]="-18.0 cm";//centerY="27.0 cm";
				result[1]="13.0 cm";//width="20.0 cm";
				result[2]="13.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Pelvis")==0){
				result[0]="12.0 cm";//centerY="16.0 cm";
				result[1]="18.0 cm";//width="35.0 cm";
				result[2]="21.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("WholeBody")==0){
				result[0]="0.0 cm";//centerY="16.0 cm";
				result[1]="22.9 cm";//width="35.0 cm";
				result[2]="109.11 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}
			}	
		else if (ageGroup.compareTo("age_1")==0){
			if (exam.compareTo("Head")==0){
				result[0]="40.0 cm";//centerY="80.0 cm";
				result[1]="14.0 cm";//width="20.0 cm";
				result[2]="17.0 cm";//height="27.0 cm";
				result[3]="0.0 cm";//centerX
			} else if (exam.compareTo("Abdomen")==0){
				result[0]="11.0 cm";//centerY="25.0 cm";
				result[1]="17.0 cm";//width="35.0 cm";
				result[2]="17.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("CervicalSpine")==0){
				result[0]="34.0 cm";//centerY="70.0 cm";
				result[1]="8.0 cm";//width="20.0 cm";
				result[2]="9.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Chest")==0){
				result[0]="23.0 cm";//centerY="52.0 cm";
				result[1]="15.0 cm";//width="35.0 cm";
				result[2]="14.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("DentalPanoramic")==0){
				result[0]="35.0 cm";//centerY="80.0 cm";
				result[1]="11.5 cm";//width="17.0 cm";
				result[2]="4.0 cm";//height="8.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Femur")==0){
				result[0]="-8.0 cm";//centerY="18.0 cm";
				result[1]="10.0 cm";//width="20.0 cm";
				result[2]="13.0 cm";//height="25.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("FullSpine")==0){
				result[0]="24.5 cm";//centerY="50.0 cm";
				result[1]="8.0 cm";//width="20.0 cm";
				result[2]="31.0 cm";//height="65.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("ThoracicSpine")==0){
				result[0]="24.5 cm";//centerY="52.0 cm";
				result[1]="8.0 cm";//width="20.0 cm";
				result[2]="11.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumbarSpine")==0){
				result[0]="17.0 cm";//centerY="32.0 cm";
				result[1]="8.0 cm";//width="20.0 cm";
				result[2]="16.0 cm";//height="30.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumboSacralJunction")==0){
				result[0]="11.0 cm";//centerY="27.0 cm";
				result[1]="8.0 cm";//width="20.0 cm";
				result[2]="7.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Shoulder")==0){
				result[0]="29.0 cm";//centerY="68.0 cm";
				result[1]="10.0 cm";//width="20.0 cm";
				result[2]="6.0 cm";//height="17.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("HipJoint")==0){
				result[0]="0.0 cm";//centerY="10.0 cm";
				result[1]="8.0 cm";//width="20.0 cm";
				result[2]="8.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Knee")==0){
				result[0]="-11.0 cm";//centerY="27.0 cm";
				result[1]="7.0 cm";//width="20.0 cm";
				result[2]="7.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Pelvis")==0){
				result[0]="9.0 cm";//centerY="16.0 cm";
				result[1]="14.0 cm";//width="35.0 cm";
				result[2]="16.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("WholeBody")==0){
				result[0]="0.0 cm";//centerY="16.0 cm";
				result[1]="17.6 cm";//width="35.0 cm";
				result[2]="74.41 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}
			}	
		else if (ageGroup.compareTo("newborn")==0){
			if (exam.compareTo("Head")==0){
				result[0]="28.5 cm";//centerY="80.0 cm";
				result[1]="10.0 cm";//width="20.0 cm";
				result[2]="12.5 cm";//height="27.0 cm";
				result[3]="0.0 cm";//centerX
			} else if (exam.compareTo("Abdomen")==0){
				result[0]="7.5 cm";//centerY="25.0 cm";
				result[1]="12.0 cm";//width="35.0 cm";
				result[2]="12.0 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("CervicalSpine")==0){
				result[0]="25.0 cm";//centerY="70.0 cm";
				result[1]="6.0 cm";//width="20.0 cm";
				result[2]="7.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Chest")==0){
				result[0]="16.0 cm";//centerY="52.0 cm";
				result[1]="11.0 cm";//width="35.0 cm";
				result[2]="10.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("DentalPanoramic")==0){
				result[0]="25.0 cm";//centerY="80.0 cm";
				result[1]="9.0 cm";//width="17.0 cm";
				result[2]="3.5 cm";//height="8.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Femur")==0){
				result[0]="-5.0 cm";//centerY="18.0 cm";
				result[1]="7.0 cm";//width="20.0 cm";
				result[2]="8.0 cm";//height="25.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("FullSpine")==0){
				result[0]="18.0 cm";//centerY="50.0 cm";
				result[1]="6.0 cm";//width="20.0 cm";
				result[2]="22.0 cm";//height="65.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("ThoracicSpine")==0){
				result[0]="17.0 cm";//centerY="52.0 cm";
				result[1]="6.0 cm";//width="20.0 cm";
				result[2]="7.5 cm";//height="35.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumbarSpine")==0){
				result[0]="12.0 cm";//centerY="32.0 cm";
				result[1]="6.0 cm";//width="20.0 cm";
				result[2]="11.0 cm";//height="30.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("LumboSacralJunction")==0){
				result[0]="8.0 cm";//centerY="27.0 cm";
				result[1]="6.0 cm";//width="20.0 cm";
				result[2]="6.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Shoulder")==0){
				result[0]="20.0 cm";//centerY="68.0 cm";
				result[1]="8.0 cm";//width="20.0 cm";
				result[2]="4.0 cm";//height="17.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("HipJoint")==0){
				result[0]="0.0 cm";//centerY="10.0 cm";
				result[1]="5.0 cm";//width="20.0 cm";
				result[2]="5.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Knee")==0){
				result[0]="-8.0 cm";//centerY="27.0 cm";
				result[1]="5.0 cm";//width="20.0 cm";
				result[2]="5.0 cm";//height="20.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("Pelvis")==0){
				result[0]="6.5 cm";//centerY="16.0 cm";
				result[1]="10.0 cm";//width="35.0 cm";
				result[2]="11.0 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}else if (exam.compareTo("WholeBody")==0){
				result[0]="0.0 cm";//centerY="16.0 cm";
				result[1]="12.7 cm";//width="35.0 cm";
				result[2]="50.96 cm";//height="32.0 cm";
				result[3]="0.0 cm";//centerX
			}
			}			
		return result;
	}
	
	/**
	 * Return the dose reference level (if available).
	 * @return the result
	 */
	private double getReferenceLevel(){
		double dbl=0.0;
		String exam=(String)examinationCb.getSelectedItem();
		int ageGroupIndex=this.getAgeGroupIndex();
		if (exam.compareTo("Abdomen")==0){
			String[] data = (String[])resources.getObject("abdomen.limit.ct");
			dbl=Convertor.stringToDouble(data[ageGroupIndex]);
			//System.out.println("dddd "+dbl+" indx "+ageGroupIndex);			
		} else if (exam.compareTo("Head")==0){
			String[] data = (String[])resources.getObject("skull.limit.ct");
			dbl=Convertor.stringToDouble(data[ageGroupIndex]);
		} 
		return dbl;
	}
	
	/**
	 * Retrieve numeric data from a string like "20 cm".
	 * @param str the string
	 * @return the numeric data
	 */
	@SuppressWarnings("unused")
	private double unformatFields(String str){
		String[] result=str.split(" ");
		String valueS =result[0];
		return Convertor.stringToDouble(valueS);
	}
	
	/**
	 * Perform calculation
	 */
	private void compute(){
		isOkToSave=false;
				
		sliceThickness_toSave=sliceThicknessTf.getText();//checked on MC run
		pitch_toSave=pitchFactorTf.getText();//checked on MC run
		rotationAngleIncrement_toSave=rotationAngleIncrementTf.getText();//checked on MC run
		fanBeam_toSave=(String)fanBeamCb.getSelectedItem();//checked on MC run
		
		//CTDI_toSave=ctdiTf.getText();
		//CTDIvol_toSave=ctdiVolTf.getText();
		
		FSD_toSave=focusmidplaneTf.getText();//checked at MC run
		
		phantomSex_toSave = (String)phantomSexCb.getSelectedItem();
		phantomMass_toSave=phantomWeightTf.getText();//checked on MC run
		phantomHeight_toSave=phantomHeightTf.getText();//checked on MC run
		kv_toSave = (String)kvCb.getSelectedItem();		//checked on MC run
		filtration_toSave = filtrationTf.getText();//checked on MC run
		anodeAngle_toSave=(String)anodeAngleCb.getSelectedItem();//checked on MC run
		ripple_toSave=(String)rippleCb.getSelectedItem();//checked on MC run
		String exam=(String)examinationCb.getSelectedItem();
		exam_toSave=exam;
		
		phantomAge_toSave=phantomAgeTf.getText();//ageGroup;//checked on MC run
		unc_toSave=estimatedMeasurementUncertaintyTf.getText();
				
		double unc=0.0;
		double ctdivol=0.0;
		double ctdi=0.0;
		
		try
	    {
			ctdi=Convertor.stringToDouble(ctdiTf.getText());
			ctdivol=Convertor.stringToDouble(ctdiVolTf.getText());
		    unc=Convertor.stringToDouble(estimatedMeasurementUncertaintyTf.getText());		
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}				

		textArea.selectAll();
		textArea.replaceSelection("");
		
		ctdivol=ctdivol/1000.0;//uGy->mGy
		ctdi=ctdi/1000.0;//uGy->mGy
		
		CTDI_toSave=Convertor.doubleToString(ctdi);//mGy
		CTDIvol_toSave=Convertor.doubleToString(ctdivol);//mGy
		textArea.append("CTDI [mGy]= "+CTDI_toSave+" +/- "+unc+" %"+"\n");
		textArea.append("CTDIvol [mGy]= "+CTDIvol_toSave+" +/- "+unc+" %"+"\n");
		
		double DRL = getReferenceLevel();
		if (DRL!=0){
			textArea.append(resources.getString("DRL.ct")+
				Convertor.formatNumber(DRL,2)+"\n");
			DRL_toSave=Convertor.doubleToString(DRL);
		}
		else{
			textArea.append(resources.getString("DRL.ct")+"N.A."+"\n");
			DRL_toSave="NA";//db=>only strings!!!!!!
		}
		
		boolean testSuccess=true;
		resultTest_toSave="NA";
		if (DRL!=0){
			StatsUtil.confidenceLevel = 0.95;
			double ESAK_unc=ctdivol*unc/100.0;
			double DRL_unc=DRL*0.1/100.0;//0.1.%
			double f_degrees=StatsUtil.evaluateDegreesOfFreedom(ESAK_unc, ctdivol);
			double f_poisson=StatsUtil.evaluateDegreesOfFreedom(DRL_unc, DRL);
			boolean diffB = StatsUtil.ttest_default_unc(ctdivol, DRL, 
				ESAK_unc,DRL_unc, f_degrees,	f_poisson);
		
		
			if (ctdivol<=DRL)
				testSuccess=true;
			else
			{
				if (diffB)        
					testSuccess=false;
				else
					testSuccess=true;
			}
		
			if (testSuccess){
				textArea.append(resources.getString("rezultat.CTDI")+
				   	resources.getString("rezultat.succes")+" (95% confidence level)"+"  \n");
				resultTest_toSave="PASSED";
			}
			else{
				textArea.append(resources.getString("rezultat.CTDI")+
						resources.getString("rezultat.fail")+" (95% confidence level)"+"  \n");
				resultTest_toSave="NOT PASSED";
			}
		}//if (DRL!=0){
		
		isOkToSave=true;
		//NOW, EFFECTIVE DOSE!!!!!!!!!!!!!!!!!!
		startMC();
	}
	
	/**
	 * Converts ASCII int value to a String.
	 * 
	 * @param i
	 *            the ASCII integer
	 * @return the string representation
	 */
	@SuppressWarnings("unused")
	private static String asciiToStr(int i) {
		char a[] = new char[1];
		a[0] = (char) i;
		return (new String(a)); // char to string
	}
	
	//FOR LINUX==========================ENVVAR
		private String G4LEDATA="";
		private String G4LEVELGAMMADATA="";
		private String G4NEUTRONHPDATA="";
		private String G4NEUTRONXSDATA="";
		private String G4PIIDATA="";
		private String G4RADIOACTIVEDATA="";
		private String G4REALSURFACEDATA="";
		private String G4SAIDXSDATA="";
		protected String detFolderURL2="";
		//===========================================
	/**
	 * Read the path to Monte Carlo program, prepare its input file and start computation thread
	 */
	private void startMC(){
		String fileSeparator = System.getProperty("file.separator");
		String curentDir = System.getProperty("user.dir");
		String filename1 = curentDir + fileSeparator + filenameMC;
		
		G4LEDATA="";
		G4LEVELGAMMADATA="";
		G4NEUTRONHPDATA="";
		G4NEUTRONXSDATA="";
		G4PIIDATA="";
		G4RADIOACTIVEDATA="";
		G4REALSURFACEDATA="";
		G4SAIDXSDATA="";
		
		File f = new File(filename1);
		int i = 0;
		String pathMC = "";

		int countLine=0;//@@@@@@@@@@@@@@@@@@@@@@@@@
		StringBuffer desc = new StringBuffer();
		boolean haveData = false;
		
		if (f.exists()) {
			try {
				FileReader fr = new FileReader(f);
				while ((i = fr.read()) != -1) {
					//String s1 = new String();
					//s1 = asciiToStr(i);
					//pathMC = pathMC + s1;
					if (!Character.isWhitespace((char) i)) {
						desc.append((char) i);
						haveData = true;
					} else {
						if (haveData)// we have data
						{
							haveData = false;// reset
							if (countLine==0){//@@@@@@@@@@@@@@
								pathMC = pathMC + desc.toString();
							} else if (countLine==1){
								G4LEDATA=G4LEDATA+desc.toString();
							}else if (countLine==2){
								G4LEVELGAMMADATA=G4LEVELGAMMADATA+desc.toString();
							}else if (countLine==3){
								G4NEUTRONHPDATA=G4NEUTRONHPDATA+desc.toString();
							}else if (countLine==4){
								G4NEUTRONXSDATA=G4NEUTRONXSDATA+desc.toString();
							}else if (countLine==5){
								G4PIIDATA=G4PIIDATA+desc.toString();
							}else if (countLine==6){
								G4RADIOACTIVEDATA=G4RADIOACTIVEDATA+desc.toString();
							}else if (countLine==7){
								G4REALSURFACEDATA=G4REALSURFACEDATA+desc.toString();
							}else if (countLine==8){
								G4SAIDXSDATA=G4SAIDXSDATA+desc.toString();
							}
							
							countLine++;
						}
						desc = new StringBuffer();
					}
				}
				fr.close();
				
				pathMC.trim();G4LEDATA.trim();
				G4LEVELGAMMADATA.trim();G4NEUTRONHPDATA.trim();G4NEUTRONXSDATA.trim();
				G4PIIDATA.trim();G4RADIOACTIVEDATA.trim();G4REALSURFACEDATA.trim();G4SAIDXSDATA.trim();
				
				doseFolderURL=pathMC+ fileSeparator;
				
				detFolderURL2=pathMC;
				if (SystemInfo.isLinux())
					doseExeName= detExeNameLinux;//"detector";
				
				String filename = pathMC+ fileSeparator + doseExeName;
				File ff = new File(filename);
				if (ff.exists()){
					//textArea.append(resources.getString("MC.notAvailable")+"\n");
					if(!prepareMacroFile())
						return;
					//===========run
					//runMC();
					startComputation();
				}else{
					textArea.append(resources.getString("MC.notAvailable")+"\n");
					return;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//defaultLookAndFeel();
		}
	}
	
	/**
	 * Run Monte-Carlo program and capture its output.
	 */
	private void runMC(){
		String file_sep = System.getProperty("file.separator");
		
		String workDir =doseFolderURL;//(String)session.getAttribute("dhpreFolderURL");
		File directory = new File(workDir);
		
		String command =workDir+file_sep+doseExeFileName;//(String)session.getAttribute("dhpreExeName");
		String argument = macroFilename;//timeS;
		//===============LINUX===================
				if (SystemInfo.isLinux()){
					//Creating a running Script because we want to set environmental variable
					//there is no global variable in Linux so we are forced to use scripts!!!
					String currentDir = System.getProperty("user.dir");
					//String file_sep = System.getProperty("file.separator");
					String filename = currentDir + file_sep + "runScript";
					File scriptFile = new File(filename);
					String str = "#!/bin/bash"+"\n";

					str= str+G4LEDATA+"\n";
					str= str+G4LEVELGAMMADATA+"\n";
					str= str+G4NEUTRONHPDATA+"\n";
					str= str+G4NEUTRONXSDATA+"\n";
					str= str+G4PIIDATA+"\n";
					str= str+G4RADIOACTIVEDATA+"\n";
					str= str+G4REALSURFACEDATA+"\n";
					str= str+G4SAIDXSDATA+"\n";
					str = str+"cd "+detFolderURL2+"; ./"+doseExeFileName+" ./"+macroFilename;
					
					//System.out.println(str);
					//return;
					//boolean succesWriteFile=true;
					try {
						FileWriter sigfos = new FileWriter(scriptFile);
						sigfos.write(str);
						sigfos.close();			
							
					} catch (Exception e) {
						e.printStackTrace();
						//succesWriteFile=false;
					}
					//============END SCRIPT CREATION
					//==========now setting up permission===============	
					String cmd_arg = "chmod 755 runScript";
					ProcessBuilder pbuilder = new ProcessBuilder("bash", "-c", cmd_arg);
					try {
						Process p = pbuilder.start();
						p.destroy();
					}catch (Exception e){
						e.printStackTrace();
					}
					//===============END PERMISSION====================			
					command = "./runScript";			
				}
				//================================
		ProcessBuilder builder = new ProcessBuilder(command, argument);
		builder.directory(directory);
		
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@		
				if (SystemInfo.isLinux()){
					String currentDir = System.getProperty("user.dir");
					builder = new ProcessBuilder(command);
					builder.directory(new File(currentDir));
				}
				//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				
		try {
			final Process process = builder.start();
			
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			//out.println("<br>Simulation starts!");
			textArea.append("\n"+"Simulation starts!"+"\n");

			while ((line = br.readLine()) != null) {
				String newLine=line.replaceAll("<", "[");
				newLine=newLine.replaceAll(">", "]");
				//out.println("<br>" + line);
				//out.println("<br>" + newLine);
				textArea.append(newLine+"\n");//System.out.println(newLine);
				
				//EXTRACTIONS==========================
				String str = "Effective dose in body";
				int len = str.length();
				int newLineLen=newLine.length();
				if (newLineLen>len)
				{
					String cstr=newLine.substring(0, len);
					if (str.equals(cstr)){
						//System.out.println(newLine);
						String[] effunit = extractValueAndUnit(newLine);
						effectiveDose=effunit[0];//Convertor.stringToDouble(effunit[0]);
						effectiveDoseUnit=effunit[1];
						//System.out.println("Eff= "+effectiveDose+" unit:"+effectiveDoseUnit);
						
						//THIS IS NEEDED SINCE SCRIPT LIKE SHELL WAITS FOR EXIT!!!!
						if (SystemInfo.isLinux())
							   break;
					}
				}
				
				str = "----- Lifetime fatal cancer risk [cases/1 million";
				len = str.length();
				newLineLen=newLine.length();
				if (newLineLen>len)
				{
					String cstr=newLine.substring(0, len);
					if (str.equals(cstr)){
						String newlineSkiped1=newLine.substring(len);
						//System.out.println(newlineSkiped1);
						String[] riskS = extractValueAndUnit(newlineSkiped1);
						risk=riskS[0];//Convertor.stringToDouble(riskS[0]);
						//System.out.println("Risk= "+risk);
					}
				}
				
				str="Minimum number of events required";
				len = str.length();
				newLineLen=newLine.length();
				if (newLineLen>len)
				{
					String cstr=newLine.substring(0, len);
					if (str.equals(cstr)){
						String title =resources.getString("dialog.number.title");
					    String message =newLine;
					    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
					}
				}
				
				str = "DLP, Dose Length Product index";
				len = str.length();
				newLineLen=newLine.length();
				if (newLineLen>len)
				{
					String cstr=newLine.substring(0, len);
					if (str.equals(cstr)){
						String newlineSkiped1=newLine.substring(len);
						//System.out.println(newlineSkiped1);
						String[] dlpS = extractValueAndUnit(newlineSkiped1);
						DLP_toSave=dlpS[0];//Convertor.stringToDouble(riskS[0]);
						//System.out.println("DLP [mgy x cm] = "+DLP_toSave);
					}
				}
				//================================
			}
			isr.close();
			is.close();
			process.destroy();

			//out.println("<br>Simulation ends successfully!");
			textArea.append("Simulation ends successfully!"+"\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//now delete macrofile
		try {
			macroFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//the end:
		stopComputation();
	}
	
	/**
	 * Based on SRS78 database for simulating XRay spectra, return true if all is good for Monte Carlo computation.
	 * @param ianod ianod
	 * @param iripple iripple
	 * @param uanod uanod
	 * @param kv kv
	 * @return the result
	 */
	public boolean validateComputationMode(int ianod, double iripple,
			double uanod, double kv) {
		boolean saveBoo = true;

		if (ianod == 0)// W
		{
			if (uanod < 6 || uanod > 22)
				saveBoo = false;

			double icav = kv;
			if (kv < 30 || kv > 150)
				saveBoo = false;

			// allowed ripple
			if (iripple != 0) {
				if (icav != 55 && icav != 60 && icav != 65 && icav != 70
						&& icav != 75 && icav != 80 && icav != 85 && icav != 90) {
					saveBoo = false;
				}

			}

		} else// Mo,Rh
		{
			if (uanod < 9 || uanod > 23)
				saveBoo = false;

			if (kv < 25 || kv > 32)
				saveBoo = false;

			// ======================================================
			if (iripple != 0)
				saveBoo = false;// not allowed anything but ripple 0!!
			// =====================================================
		}

		return saveBoo;
	}
	
	/**
	 * Prepare the input file for external Monte-Carlo program
	 * @return true on success
	 */
	private boolean prepareMacroFile(){
		String str = "";
		str = str + "/phantom/setPhantomModel MIRD" + "\n";
		String sex = (String)phantomSexCb.getSelectedItem();
		str = str + "/phantom/setPhantomSex" + " " + sex + "\n";
		String ageGroup = getAgeGroup();
		str = str + "/phantom/setAgeGroup" + " " + ageGroup + "\n";
		String age = phantomAgeTf.getText();
		str = str + "/phantom/setPhantomAge" + " " + age + "\n";
		//test:
		try
	    {
			if(Convertor.stringToDouble(age)<0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.age.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.age.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		
		String phantomHeight=phantomHeightTf.getText();
		str = str + "/phantom/phantom_height" + " " + phantomHeight +" cm"+ "\n";
		try
	    {
			if(Convertor.stringToDouble(phantomHeight)<=0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.height.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.height.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		String phantomMass = phantomWeightTf.getText();//request.getParameter("phantomMass");
		str = str + "/phantom/phantom_mass" + " " + phantomMass +" kg"+ "\n";
		try
	    {
			if(Convertor.stringToDouble(phantomMass)<=0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.mass.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.mass.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		str = str + "/phantom/buildNewPhantom" + "\n";
		str = str + "/run/initialize" + "\n";
		
		//VIZUALIZE:///vis/viewer/set/autoRefresh false
		///control/execute vis.mac
		//String truefalse="false";
		//if (graphicSceneAutoRefreshCh.isSelected())
		//	truefalse="true";
		if (graphicSceneAutoRefreshCh.isSelected()){//changed in vis-novis!!
			str=str+"/control/execute vis.mac"+"\n";					
			str=str+"/vis/viewer/set/autoRefresh false"+"\n";
		}
		//===================================================================
		String spectrum = "yes";//request.getParameter("spectrum");
		str = str + "/xfield/isSpectrum?" + " " + spectrum + "\n";
		
		String kv = (String)kvCb.getSelectedItem();//request.getParameter("kv");
		str = str + "/xfield/kVp" + " " + kv + "\n";
		
		String filtration = filtrationTf.getText();//request.getParameter("mmAl");
		str = str + "/xfield/filtration" + " " + filtration + "\n";
		try
	    {
			if(Convertor.stringToDouble(filtration)<0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.filtration.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.filtration.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		String manod = "W";//request.getParameter("manode");
		str = str + "/xfield/anodMaterial" + " " + manod + "\n";
		
		int ianod = 0;
		if (manod.compareTo("W") == 0)
			ianod = 0;
		else if (manod.compareTo("MO") == 0)
			ianod = 1;
		else if (manod.compareTo("RH") == 0)
			ianod = 2;
		
		String uanod = (String)anodeAngleCb.getSelectedItem();//request.getParameter("uanod");
		str = str + "/xfield/anodAngle" + " " + uanod + "\n";

		String ripple = (String)rippleCb.getSelectedItem();//request.getParameter("ripple");
		str = str + "/xfield/ripple" + " " + ripple + "\n";
		
		if (!validateComputationMode(ianod, Convertor.stringToDouble(ripple),	
				Convertor.stringToDouble(uanod),Convertor.stringToDouble(kv))) {
			//out.println("<br>XRay Spectrum allowed inputs: For Mo,Rh material: 9'<='anodAngle'<='23; 25'<='kv'<='32; ripple=0 !");
			//out.println("<br>XRay For W material: 6'<='anodAngle'<='22; 30'<='kv'<='150; ripple=0! For not null ripple, kv must be only 55,60,65,...,90!!");
			String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.ripple");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//String useMAs = (String)usemasCb.getSelectedItem();//request.getParameter("mAsForDAP");
		//str = str + "/xfield/use_mAs_for_DAP_calculation?" + " " + useMAs + "\n";
		
		//String mAs = mAsTf.getText();//request.getParameter("mAs");
		//if(useMAs.equals("no"))
		//	mAs="100.0";//just fill in some not null value
		//str = str + "/xfield/mAs" + " " + mAs + "\n";
		//try
	    //{
		//	if(Convertor.stringToDouble(mAs)<=0.0){
		//	   String title =resources.getString("dialog.number.title");
		//	   String message =resources.getString("dialog.mas.message");
		//	   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		//	   return false;
		//	}
		    
		//}
		//catch(Exception e)
		//{
		 //   String title =resources.getString("dialog.number.title");
		  //  String message =resources.getString("dialog.mas.message");
		   // JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    //return false;
		//}
		
		//String polarAngle = "0.0";//request.getParameter("polarAngle");
		//str = str + "/xfield/polarAngle" + " " + polarAngle +" deg"+ "\n";
		
		//String azimuthalAngle = Convertor.doubleToString(getProjectionAngle());//request.getParameter("azimuthalAngle");
		//str = str + "/xfield/azimuthalAngle" + " " + azimuthalAngle +" deg"+ "\n";
		
		String distance = focusmidplaneTf.getText();//request.getParameter("distance");
		str = str + "/xfield/focusToPhantomSymmetryAxisDistance" + " " + distance +" cm"+ "\n";
		try
	    {
			if(Convertor.stringToDouble(distance)<=0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.distance.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.distance.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		//String DAP = kapTf.getText();//request.getParameter("DAP");
		//if(useMAs.equals("yes"))
		//	DAP="250.0";//just fill in some not null value
		//str = str + "/xfield/DAP[uGymm2]" + " " + DAP + "\n";
		//try
	    //{
		//	if(Convertor.stringToDouble(DAP)<=0.0){
		//	   String title =resources.getString("dialog.number.title");
		//	   String message =resources.getString("dialog.kap.message");
		//	   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		//	   return false;
		//	}
		    
		//}
		//catch(Exception e)
		//{
		 //   String title =resources.getString("dialog.number.title");
		  //  String message =resources.getString("dialog.kap.message");
		   // JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    //return false;
		//}
		
		String sliceThickness = sliceThicknessTf.getText();//request.getParameter("sliceThickness");
		//str = str + "/xfield/sliceThickness" + " " + sliceThickness +" mm"+ "\n";
		//moved after exam!!!
		try {
			if (Convertor.stringToDouble(sliceThickness) <= 0.0) {
				 String title =resources.getString("dialog.number.title");
				 String message =resources.getString("dialog.sliceThickness.message");
				 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
			 String message =resources.getString("dialog.sliceThickness.message");
			 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		String pitch = pitchFactorTf.getText();//request.getParameter("pitch");
		str = str + "/xfield/pitch" + " " + pitch+ "\n";
		try {
			if (Convertor.stringToDouble(pitch) <= 0.0) {
				String title =resources.getString("dialog.number.title");
				 String message =resources.getString("dialog.pitchFactor.message");
				 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
			 String message =resources.getString("dialog.pitchFactor.message");
			 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		String angleIncrement = rotationAngleIncrementTf.getText();//request.getParameter("angleIncrement");
		str = str + "/xfield/angleIncrement" + " " + angleIncrement +" deg"+ "\n";
		try {
			if (Convertor.stringToDouble(angleIncrement) <= 0.0) {
				String title =resources.getString("dialog.number.title");
				 String message =resources.getString("dialog.angleIncrement.message");
				 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
			 String message =resources.getString("dialog.angleIncrement.message");
			 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		String fanBeam = (String)fanBeamCb.getSelectedItem();//request.getParameter("fanBeam");
		str = str + "/xfield/SetFanBeam" + " " + fanBeam + "\n";		
		
		if (helicalScanCh.isSelected()){
			str = str + "/xfield/SetHelicalScan" + " on"+"\n";
		} else {
			str = str + "/xfield/SetHelicalScan" + " off"+"\n";
		}
		
		if (halfFieldCh.isSelected()){
			str = str + "/xfield/SetHalfField" + " on"+"\n";
		} else {
			str = str + "/xfield/SetHalfField" + " off"+"\n";
		}
		
		if (dentalPanoramicCh.isSelected()){
			str = str + "/xfield/SetDentalPanoramic" + " on"+"\n";
		} else {
			str = str + "/xfield/SetDentalPanoramic" + " off"+"\n";
		}
		
		String CTDI = ctdiTf.getText();//request.getParameter("CTDI");
		str = str + "/xfield/CTDI[uGy]" + " " + CTDI + "\n";
		try {
			if (Convertor.stringToDouble(CTDI) <= 0.0) {
				String title =resources.getString("dialog.number.title");
				 String message =resources.getString("dialog.ctdi.message");
				 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
			 String message =resources.getString("dialog.ctdi.message");
			 JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		String exam = (String)examinationCb.getSelectedItem();;//request.getParameter("exam");
		String[] exams = getFieldsParameters(exam, ageGroup);
		str = str + "/xfield/centerY" + " " + exams[0] + "\n";
		str = str + "/xfield/centerX" + " " + exams[3] + "\n";
		str = str + "/xfield/width" + " " + exams[1] + "\n";
		str = str + "/xfield/height" + " " + exams[2] + "\n";
		
		str=str+"/event/printModulo 1000"+"\n";//internal default
		
		if (dentalPanoramicCh.isSelected()&& (autoTCh.isSelected())){
			str = str + "/xfield/sliceThickness" + " " + exams[2]+ "\n";//in cm is ok
		}else{
			str = str + "/xfield/sliceThickness" + " " + sliceThickness +" mm"+ "\n";
		}
				
		String beam = "CTScan";//request.getParameter("beam");
		str = str + "/gun/setBeam" + " " + beam + "\n";
		
		String nRuns = runsTf.getText();//request.getParameter("nRuns");		
		try{
			if (Convertor.stringToInt(nRuns)<=0){
				String title =resources.getString("dialog.number.title");
			    String message =resources.getString("dialog.run.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (Exception e){
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.run.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//if(Convertor.stringToInt(nRuns)>2000000)
			//nRuns="2000000";//this is for web
		
		str=str+"/run/beamOn"+" "+nRuns+"\n";
		
		//other uninportant params...just fill in some values:
		//String sliceThickness = "1.0";//request.getParameter("sliceThickness");
		//str = str + "/xfield/sliceThickness" + " " + sliceThickness +" mm"+ "\n";
		
		//String pitch = "1.0";//request.getParameter("pitch");
		//str = str + "/xfield/pitch" + " " + pitch+ "\n";
		
		//String angleIncrement = "1.0";//request.getParameter("angleIncrement");
		//str = str + "/xfield/angleIncrement" + " " + angleIncrement +" deg"+ "\n";
		
		//String fanBeam = "on";//request.getParameter("fanBeam");
		//str = str + "/xfield/SetFanBeam" + " " + fanBeam + "\n";
		
		//String CTDI = "250.0";//request.getParameter("CTDI");
		//str = str + "/xfield/CTDI[uGy]" + " " + CTDI + "\n";
		
		String particleType = "gamma";//request.getParameter("particleType");
		str=str+"/gun/particle"+" "+particleType+"\n";
		
		String particleKineticEnergy = "0.662";//request.getParameter("incidentKineticEnergy");
		str=str+"/gun/energy"+" "+particleKineticEnergy+" MeV"+"\n";
		//---------------
		
		//======================END STRING.
		//System.out.println(str);
		
		//SAVE MACRO FILE
		long time = System.currentTimeMillis();
		String timeS = "run" + time + ".mac";
		String file_sep = System.getProperty("file.separator");
		String filename =doseFolderURL//(String)session.getAttribute("dhpreFolderURL")
				+ file_sep + timeS; 
			//"D:" + file_sep + "dhpre_exe_web" + file_sep + timeS;
		macroFile = new File(filename);
		macroFilename=timeS;
		boolean succesWriteFile = true;
		try {
			FileWriter sigfos = new FileWriter(macroFile);
			sigfos.write(str);
			sigfos.close();

		} catch (Exception e) {
			e.printStackTrace();
			succesWriteFile = false;
		}
		if (!succesWriteFile) {
			//out.println("<br>Unexpected error has occurred when trying to process input data!");
			String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.input.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Start computation thread
	 */
	private void startComputation() {
		if (simTh == null) {
			simTh = new Thread(this);
			simTh.start();// Allow one simulation at time!
		}
		// Do nothing if simulation is in progress and run button is hit again!
	}
	
	/**
	 * Stop computation thread
	 */
	private void stopComputation() {
		
		if (simTh == null) {
			//stopAppend = false;// press kill button but simulation never
								// started!
			return;
		}
		simTh = null;
		//if (stopAppend) {// kill button was pressed!
			//Alpha_MC.STOPSIMULATION = true;// tell to stop simulation loop immediatly!
		//	textArea.append(resources.getString("text.simulation.stop") + "\n");
		//	stopAppend = false;
		//	String label = resources.getString("status.done");
		//	statusL.setText(label);
		//}
		
	}
	
	/**
	 * Thread specific run method
	 */
	public void run() {
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		simTh.setPriority(Thread.NORM_PRIORITY);
		runMC();//performCalculation();
	}
	
	/**
	 * Extract value and units from a string.
	 * @param src the string
	 * @return the result
	 */
	private String[] extractValueAndUnit(String src) {
		String[] result = new String[2];
		
		DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
		char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();
		
		boolean stop=false;
		boolean enter=false;
		int index =0; 
		
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < src.length(); i++) {
	        char c = src.charAt(i);	        
	        if (Character.isDigit(c)) {	        	
	        	stop=true;
	        	enter=true;
	            builder.append(c);
	        } else {
	        	if (enter && c == localeDecimalSeparator){
	        		stop=true;//just continue
	        		builder.append(c);
	        	}
	        	else
	        		stop=false;
	        }
	        
	        if (!stop && enter){
	        	index=i;
	        	break;	        	
	        }	        
	    }
	    String valueS = builder.toString();
	    
	    //we have index of next character non number which is SPACE
	    String str= src.substring(index+1);//skip the blank 
	    //System.out.println(str);
	    String[] splitSpace=str.split(" ");
		String unitS =splitSpace[0];
		
		unitS=unitS.replaceAll("Gy", "Sv");//Sieverts
		//System.out.println("Units="+unitS);
	    //return builder.toString();
		result[0]=valueS;
		result[1]=unitS;
		
		return result;
	}

}
