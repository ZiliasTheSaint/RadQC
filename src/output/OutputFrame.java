package output;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import danfulea.db.DatabaseAgent;
//import jdf.db.DBConnection;
import danfulea.math.Convertor;
import danfulea.math.StatsUtil;
import danfulea.math.numerical.Stats;
import danfulea.utils.FrameUtilities;
import danfulea.utils.ListUtilities;
import radQC.RadQCFrame;

/**
 * Tube output, repeatability and mAs linearity QC tests. <br>
 * 
 * 
 * @author Dan Fulea, 01 May. 2015
 */
@SuppressWarnings("serial")
public class OutputFrame extends JFrame implements ActionListener{

	private static final Dimension PREFERRED_SIZE = new Dimension(800, 720);
	private static final Dimension textAreaDimension = new Dimension(700, 300);
	private static final Dimension sizeLst = new Dimension(253,125);
	private static final String BASE_RESOURCE_CLASS = "output.resources.OutputFrameResources";
	protected ResourceBundle resources;
	private RadQCFrame mf;
	
	protected String measurementDate_toSave="";
	protected String mainDB = "";
	protected int IDLINK=0;
	
	private static final String COMPUTEOUTPUT_COMMAND = "COMPUTEOUTPUT";
	private static final String ADDREP_COMMAND = "ADDREP";
	private static final String CALCREP_COMMAND = "CALCREP";
	private static final String DELREP_COMMAND = "DELREP";
	private static final String RESETREP_COMMAND = "RESETREP";
	
	private static final String ADDLIN_COMMAND = "ADDLIN";
	private static final String CALCLIN_COMMAND = "CALCLIN";
	private static final String DELLIN_COMMAND = "DELLIN";
	private static final String RESETLIN_COMMAND = "RESETLIN";
	
	private static final String SAVE_COMMAND = "SAVE";
	private String command = null;
	
	private JTabbedPane mainTab = new JTabbedPane();

	private JTextField estimatedMeasurementUncertaintyTf = new JTextField(3);
	
	private JTextField maxPermissibleOutputTf=new JTextField(5);	
	private JTextField mAOutputTf=new JTextField(5);
	private JTextField msOutputTf=new JTextField(5);
	private JTextField mAsOutputTf=new JTextField(5);	
	private JRadioButton mrRb, mradRb, mgyRb;
	private JTextField distanceTf = new JTextField(5);
	private JTextField exposureTf = new JTextField(5);
	protected JTextArea outputTa = new JTextArea();
	
	protected JTextArea repeatabilityTa = new JTextArea();
	private JTextField maxPermissibleRepeatabilityTf=new JTextField(5);
	private JTextField measuredRepeatabilityTf=new JTextField(5);	
	@SuppressWarnings("rawtypes")
	protected DefaultListModel repeatabilitydlm=new DefaultListModel() ;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JList repeatabilityL = new JList(repeatabilitydlm);
	
	protected JTextArea randTa = new JTextArea();
	private JTextField maxPermissibleLinearityTf=new JTextField(5);
	private JTextField setmaTf=new JTextField(5);
	private JTextField setmsTf=new JTextField(5);
	private JTextField setmasTf=new JTextField(5);
	private JTextField setmgyTf=new JTextField(5);//dose indicator
	@SuppressWarnings("rawtypes")
	protected DefaultListModel dlm=new DefaultListModel() ;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JList randList = new JList(dlm);
	//Output variable
	protected boolean isOktoSaveOutput=false;
	protected boolean resultOutput=false;
	protected double output=0.0;
	protected double maxOutput=0.0;
	
	//-----------
	private int nRepeatabilityPoints=0;
	private Vector<String> repeatabilityv=new Vector<String>();	
	protected double[] repeatabilityd=new double[0];
	protected boolean isOktoSaveOutputReapeatability=false;
	protected boolean resultRepeatability=false;
	protected double cvRepeatability=0.0;
	protected double maxcvRepeatability=0.0;
	
	//------------------------------
	private boolean masmode=true;//decide if mas is taken directly as input data!!
	private int nrandPoints=0;
	private Vector<String> masv=new Vector<String>();
	private Vector<String> expv=new Vector<String>();
	private Vector<String> randv=new Vector<String>();
	protected double[] masd=new double[0];
	protected double[] expd=new double[0];
	protected double[] randd=new double[0];
	protected double cvLinearity=0.0;
	protected double maxcvLinearity=0.0;	
	protected boolean resultLinearity=false;
	protected boolean isOktoSavemAsLinearity=false;
	
	public String outputTable="";
	public String outputRepeatabilityTable="";
	public String outputRepeatabilityTableDetail="";
	public String outputLinearityTable="";
	public String outputLinearityTableDetail="";
	
	private Connection radqcdbcon = null;
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public OutputFrame(RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("OutputFrame.NAME"));
		//DBConnection.startDerby();//just in case is closed
		//===============
		mainDB=mf.radqcDB;
				
		outputTable=mf.outputTable;
		outputRepeatabilityTable=mf.outputRepeatabilityTable;
		outputRepeatabilityTableDetail=mf.outputRepeatabilityTableDetail;
		outputLinearityTable=mf.outputLinearityTable;
		outputLinearityTableDetail=mf.outputLinearityTableDetail;
    			
    	measurementDate_toSave=mf.measurementDate;
    	IDLINK=mf.IDLINK;
    	//=============================================
    	
    	DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		
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
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        addButtons(toolBar);
                
		JTabbedPane mainPanel = createMainPanel();
		
		content.add(toolBar, BorderLayout.PAGE_START);
		content.add(mainPanel, BorderLayout.CENTER);
		//content.add(statusBar, BorderLayout.PAGE_END);

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
		
		exposureTf.requestFocusInWindow();
		measuredRepeatabilityTf.requestFocusInWindow();
	}
	
	/**
	 * Create main panel
	 * @return the result
	 */
	private JTabbedPane createMainPanel() {
		//some inits
		
		outputTa.setCaretPosition(0);
		outputTa.setEditable(false);
		outputTa.setText("");
		outputTa.setLineWrap(true);
		outputTa.setWrapStyleWord(true);
		
		repeatabilityTa.setCaretPosition(0);
		repeatabilityTa.setEditable(false);
		repeatabilityTa.setText("");
		repeatabilityTa.setLineWrap(true);
		repeatabilityTa.setWrapStyleWord(true);
		
		randTa.setCaretPosition(0);
		randTa.setEditable(false);
		randTa.setText("");
		randTa.setLineWrap(true);
		randTa.setWrapStyleWord(true);
	    
		estimatedMeasurementUncertaintyTf.setText("5");
		distanceTf.setText("100");
		maxPermissibleOutputTf.setText("80");
		if (mf.EXAMINATION_ID==1){
			distanceTf.setText("50");//mammo
			maxPermissibleOutputTf.setText("120");
		}
		
		maxPermissibleRepeatabilityTf.setText("20");
		measuredRepeatabilityTf.addActionListener(this);
		
		maxPermissibleLinearityTf.setText("20");
		setmaTf.addActionListener(this);
		setmsTf.addActionListener(this);
		setmasTf.addActionListener(this);
		setmgyTf.addActionListener(this);
		
		JLabel jlabel=new JLabel();
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		JButton button =null;
		Character mnemonic = null;
			
		mrRb=new JRadioButton("mR");
		mradRb=new JRadioButton("mRad");
		mgyRb=new JRadioButton("mGy");

	    ButtonGroup group = new ButtonGroup();
		group.add(mrRb);
		group.add(mradRb);
		group.add(mgyRb);
		//-----------------------------
		
		//-------------OUTPUT panel------------------
		JPanel puncP=new JPanel();
		puncP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        jlabel=new JLabel(resources.getString("expData.unc"));
        puncP.add(jlabel);
        puncP.add(estimatedMeasurementUncertaintyTf);        
        puncP.setBackground(RadQCFrame.bkgColor);
        
		JPanel p0P=new JPanel();
		p0P.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
		jlabel = new JLabel(resources.getString("output.permissible.label"));
		if (mf.EXAMINATION_ID==1){
			jlabel = new JLabel(resources.getString("output.permissible.label.mammo"));
		}
		p0P.add(jlabel);
        p0P.add(maxPermissibleOutputTf);
        p0P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p1P=new JPanel();
        p1P.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
        jlabel=new JLabel(resources.getString("output.exposureLabel"));
        p1P.add(jlabel);
        p1P.add(exposureTf);
        p1P.add(mrRb);
        p1P.add(mradRb);
        p1P.add(mgyRb);
        jlabel=new JLabel(resources.getString("output.distanceLabel"));
        p1P.add(jlabel);
        p1P.add(distanceTf);
        p1P.setBackground(RadQCFrame.bkgColor);
        mradRb.setSelected(true);
        mrRb.setBackground(RadQCFrame.bkgColor);
    	mradRb.setBackground(RadQCFrame.bkgColor);
    	mgyRb.setBackground(RadQCFrame.bkgColor);
    	
    	JPanel p2P=new JPanel();
        p2P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        jlabel=new JLabel(resources.getString("output.mA.label"));
        p2P.add(jlabel);
        p2P.add(mAOutputTf);
        jlabel=new JLabel(resources.getString("output.ms.label"));
        p2P.add(jlabel);
        p2P.add(msOutputTf);
        jlabel=new JLabel(resources.getString("output.OR.label"));
        p2P.add(jlabel);
        p2P.setBackground(RadQCFrame.bkgColor);
        
        JPanel p3P=new JPanel();
        p3P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        jlabel=new JLabel(resources.getString("output.mAs.label"));
        p3P.add(jlabel);
        p3P.add(mAsOutputTf);
        p3P.setBackground(RadQCFrame.bkgColor);
		
        buttonName = resources.getString("calcB");
		buttonToolTip = resources.getString("calcB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTEOUTPUT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		JPanel p4P=new JPanel();
        p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        p4P.add(button);
        p4P.setBackground(RadQCFrame.bkgColor);
        
        JPanel resultP = new JPanel(new BorderLayout());
		resultP.add(new JScrollPane(outputTa), BorderLayout.CENTER);
		resultP.setPreferredSize(textAreaDimension);
		resultP.setBackground(RadQCFrame.bkgColor);
		
    	JPanel outputP = new JPanel();
		BoxLayout blloutputP = new BoxLayout(outputP, BoxLayout.Y_AXIS);
		outputP.setLayout(blloutputP);
		outputP.add(p0P);
		outputP.add(p1P);
		outputP.add(p2P);
		outputP.add(p3P);
		
		outputP.add(puncP);
		
		outputP.add(p4P);
		
		outputP.setBackground(RadQCFrame.bkgColor);
		
		JPanel mainOutputP = new JPanel(new BorderLayout());
		mainOutputP.add(outputP, BorderLayout.NORTH);
		mainOutputP.add(resultP, BorderLayout.CENTER);
		mainOutputP.setBackground(RadQCFrame.bkgColor);
    	//----------------------------------------------------------
		
		//Repeatability panel------------------------------------------
		JPanel p00P=new JPanel();
		p00P.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
		jlabel = new JLabel(resources.getString("repeatability.maxPermissibleLabel"));
		p00P.add(jlabel);
        p00P.add(maxPermissibleRepeatabilityTf);
        p00P.setBackground(RadQCFrame.bkgColor);
        
        JPanel m1=new JPanel();
		m1.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
		jlabel = new JLabel(resources.getString("repeatability.measuredLabel"));
		m1.add(jlabel);
		m1.setBackground(RadQCFrame.bkgColor);
		
		JPanel m2 = new JPanel();
	    m2.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
	    m2.add(measuredRepeatabilityTf);
		m2.setBackground(RadQCFrame.bkgColor);
		
		JPanel m3 = new JPanel();
	    m3.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
	    buttonName = resources.getString("repeatability.addB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADDREP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.addB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    m3.add(button);
	    m3.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel m4 = new JPanel();
	    m4.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
	    buttonName = resources.getString("repeatability.calcB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, CALCREP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    m4.add(button);
	    m4.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel masuratP = new JPanel();
	    BoxLayout bl0 = new BoxLayout(masuratP,BoxLayout.Y_AXIS);
	    masuratP.setLayout(bl0);
	    masuratP.add(m1);
	    masuratP.add(m2);
	    masuratP.add(m3);
	    masuratP.add(m4);
	    
	    JScrollPane listSp = new JScrollPane();
	    JPanel lstmasP = new JPanel();
	    lstmasP.setLayout(new BorderLayout());
	    lstmasP.add(listSp, BorderLayout.CENTER);
		listSp.getViewport().add(repeatabilityL, null);
	    listSp.setPreferredSize(sizeLst);
	    
	    JPanel jp1=new JPanel();
		jp1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		buttonName = resources.getString("repeatability.delB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELREP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.delB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp1.add(button);//ra.delkvB);
		jp1.setBackground(RadQCFrame.bkgColor);
		
		JPanel jp2=new JPanel();
		jp2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		buttonName = resources.getString("repeatability.resetB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete.all");
		button = FrameUtilities.makeButton(buttonIconName, RESETREP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.resetB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp2.add(button);//ra.resetkvB);
	    jp2.setBackground(RadQCFrame.bkgColor);

	    JPanel lstopP = new JPanel();
	    BoxLayout bl = new BoxLayout(lstopP,BoxLayout.Y_AXIS);
	    lstopP.setLayout(bl);
		lstopP.add(jp1);
		lstopP.add(jp2);
		
		JPanel dataRepP = new JPanel();
		dataRepP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		dataRepP.add(masuratP, null);
	    dataRepP.add(lstmasP, null);
		dataRepP.add(lstopP, null);
	    dataRepP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel resultRepP = new JPanel();
	    resultRepP.setLayout(new BorderLayout());
	    resultRepP.add(new JScrollPane(repeatabilityTa), BorderLayout.CENTER);
		resultRepP.setPreferredSize(textAreaDimension);
		resultRepP.setBackground(RadQCFrame.bkgColor);
		
	    //jScrollPane1.getViewport().add(repeatabilityTa, null);
	    //resultKvP.add(jScrollPane1,  BorderLayout.CENTER);
	    //resultKvP.setBackground(RadQCFrame.bkgColor);
		JPanel repeatP = new JPanel();
		BoxLayout blrepeat = new BoxLayout(repeatP,BoxLayout.Y_AXIS);
		repeatP.setLayout(blrepeat);
		repeatP.add(p00P, null);
		repeatP.add(dataRepP, null);
		repeatP.add(resultRepP, null);
		repeatP.setBackground(RadQCFrame.bkgColor);
		//-----------------------------------------------------------
		//LINEARITY PANEL
		JPanel mpP=new JPanel();
		mpP.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
		jlabel = new JLabel(resources.getString("repeatability.maxPermissibleLabel"));
		mpP.add(jlabel);//the same label
		mpP.add(maxPermissibleLinearityTf);
		mpP.setBackground(RadQCFrame.bkgColor);
		//data panel
		JPanel datakP=new JPanel();
	    BoxLayout bldatak = new BoxLayout(datakP,BoxLayout.Y_AXIS);
	    datakP.setLayout(bldatak);
	    JLabel setmaL=new JLabel(resources.getString("rand.setmaL"));
	    JLabel setmsL=new JLabel(resources.getString("rand.setmsL"));
	    JLabel setmasL=new JLabel(resources.getString("rand.setmasL"));
	    JLabel setmgyL=new JLabel(resources.getString("rand.setmgyL"));
	    JLabel ORL=new JLabel(resources.getString("output.OR.label"));
	    
	    JPanel setmaP=new JPanel();
	    setmaP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    setmaP.add(setmaL);
	    setmaP.add(setmaTf);
	    setmaP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel setmsP=new JPanel();
	    setmsP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    setmsP.add(setmsL);
	    setmsP.add(setmsTf);
	    setmsP.add(ORL);
	    setmsP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel setmasP=new JPanel();
	    setmasP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    setmasP.add(setmasL);
	    setmasP.add(setmasTf);
	    setmasP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel setmgyP=new JPanel();
	    setmgyP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    setmgyP.add(setmgyL);
	    setmgyP.add(setmgyTf);
	    setmgyP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel addP=new JPanel();
	    addP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    buttonName = resources.getString("repeatability.addB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADDLIN_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.addB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    addP.add(button);
	    addP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel calcP=new JPanel();
	    calcP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    buttonName = resources.getString("repeatability.calcB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, CALCLIN_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    calcP.add(button);	    
	    calcP.setBackground(RadQCFrame.bkgColor);
	    
	    datakP.add(setmaP);
	    datakP.add(setmsP);
	    datakP.add(setmasP);
	    datakP.add(setmgyP);
	    datakP.add(addP);
	    datakP.add(calcP);
	    datakP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel lstP = new JPanel(new BorderLayout());
	    JScrollPane jsp=new JScrollPane();
		jsp.getViewport().add(randList, null);
	    jsp.setPreferredSize(sizeLst);
		lstP.add(jsp, BorderLayout.CENTER);
		JPanel delP=new JPanel();
	    delP.setLayout(new FlowLayout(FlowLayout.CENTER,5,2));
	    buttonName = resources.getString("repeatability.delB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELLIN_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.delB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    delP.add(button);
	    buttonName = resources.getString("repeatability.resetB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete.all");
		button = FrameUtilities.makeButton(buttonIconName, RESETLIN_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("repeatability.resetB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    delP.add(button);
	    delP.setBackground(RadQCFrame.bkgColor);
	    lstP.add(delP, BorderLayout.SOUTH);
	    lstP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel dataallP =new JPanel();
		dataallP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
		dataallP.add(datakP);
		dataallP.add(lstP);
		dataallP.setBackground(RadQCFrame.bkgColor);
		
		JPanel resultrandP=new JPanel(new BorderLayout());
	    JScrollPane jspres=new JScrollPane();
	    jspres.getViewport().add(randTa, null);
	    resultrandP.add(jspres,  BorderLayout.CENTER);
	    resultrandP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel mainLinP=new JPanel();
	    BoxLayout bl1 = new BoxLayout(mainLinP,BoxLayout.Y_AXIS);
	    mainLinP.setLayout(bl1);
	    mainLinP.add(mpP, null);
	    mainLinP.add(dataallP, null);
	    mainLinP.add(resultrandP, null);
	    mainLinP.setBackground(RadQCFrame.bkgColor);
		//Glue together		
    	mainTab.add(mainOutputP,  resources.getString("output.tab.title"));
    	mainTab.add(repeatP,  resources.getString("reprod.tab.title"));
    	mainTab.add(mainLinP,  resources.getString("linearity.tab.title"));
		return mainTab;
	}
	
	/**
	 * Adding some buttons to toolbar
	 * @param toolBar the toolBar
	 */
	private void addButtons(JToolBar toolBar)
	{
	    JButton button = null;
	    JPanel toolP = new JPanel();
	    toolP.setLayout(new FlowLayout(FlowLayout.LEFT,5,1));
     
	    String buttonName = resources.getString("saveB");
	    String buttonToolTip = resources.getString("saveB.toolTip");
	    String buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, SAVE_COMMAND,
					buttonToolTip, buttonName, this, this);
		Character mnemonic = (Character) resources.getObject("saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
			
	    toolP.add(button);	  
	    toolBar.add(toolP) ;
	  }
	//================================================
	/**
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource()==setmaTf)//enterul
        {
            setmsTf.requestFocusInWindow();
  	    }

		if (e.getSource()==setmsTf)
		{
			setmgyTf.requestFocusInWindow();
		}

		if (e.getSource()==setmasTf)
		{
			setmgyTf.requestFocusInWindow();
		}
		
		command = e.getActionCommand();
		if (e.getSource()==measuredRepeatabilityTf || command.equals(ADDREP_COMMAND)) {
			//(command.equals(ADDREP_COMMAND)) {
			addInListRep();
		} else if (command.equals(DELREP_COMMAND)) {
			deleteFromListRep();
		} else if (command.equals(CALCREP_COMMAND)) {
			performCalcRep();
		} else if (command.equals(RESETREP_COMMAND)) {
			resetRep();
		} else if (command.equals(SAVE_COMMAND)) {
			save();
		} else if (command.equals(COMPUTEOUTPUT_COMMAND)) {
			performCalcOutput();
		//=============================================================			
		} else if (e.getSource()==setmgyTf || command.equals(ADDLIN_COMMAND)){
			addInListLin();
		} else if (command.equals(DELLIN_COMMAND)){
			deleteFromListLin();
		} else if (command.equals(CALCLIN_COMMAND)) {
			performCalcLin();
		} else if (command.equals(RESETLIN_COMMAND)) {
			resetLin();
		}
	}
	
	/**
	 * Add data in list for mAs linearity
	 */
	private void addInListLin(){
		String s=setmaTf.getText();
		String ss=setmsTf.getText();
        double sd=0.0;
        double ssd=0.0;
        double masdbl=0.0;
        String masstr="";
        try
		{
		    sd=Convertor.stringToDouble(s);
			ssd=Convertor.stringToDouble(ss);
			masdbl=sd*ssd*1.0E-03;//ma*ms*10^-3=mAs
			if (masdbl!=0)
			{
				masmode=false;
				masstr=Convertor.doubleToString(masdbl);
				setmasTf.setText(masstr);
			}
			else
			{
			    masmode=true;
			    setmaTf.setText("");
			    setmsTf.setText("");
			}
		}
		catch (Exception ex)
		{
			masmode=true;
		    setmaTf.setText("");
		    setmsTf.setText("");
		}

        //continue
		boolean b=true;
        String s1=setmasTf.getText();
        String s2=setmgyTf.getText();
        double d1=0.0;
        double d2=0.0;
        double d3=0.0;

        try
        {
		    d1=Convertor.stringToDouble(s1);
    		d2=Convertor.stringToDouble(s2);
		}
		catch(Exception e)
		{
			b=false;
		    String title =resources.getString("dialog.insertInListError.title");
		    String message =resources.getString("dialog.insertInList.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			setmaTf.setText("");
			setmsTf.setText("");
			setmasTf.setText("");
			setmgyTf.setText("");
			if (masmode)
				setmasTf.requestFocusInWindow();
			else
			 	setmaTf.requestFocusInWindow();

		}

		if (d1<=0 || d2<=0)
		{
			b=false;
		    String title =resources.getString("dialog.insertInTableError.title");
		    String message =resources.getString("dialog.insertInTable.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			setmaTf.setText("");
			setmsTf.setText("");
			setmasTf.setText("");
			setmgyTf.setText("");
			if (masmode)
				setmasTf.requestFocusInWindow();
			else
			 	setmaTf.requestFocusInWindow();

		}
		if(!b)
		   return;

        //end test-->la succes se poate merge mai departe
        d3=d2/d1;

		ListUtilities.add("mAs :     "+d1+"   dose_indicator:   "+d2,dlm);
		ListUtilities.select(nrandPoints,randList);
		s1=Convertor.doubleToString(d1);
		s2=Convertor.doubleToString(d2);
		String s3=Convertor.doubleToString(d3);

		masv.addElement(s1);
		expv.addElement(s2);
		randv.addElement(s3);
		nrandPoints++;

		setmaTf.setText("");
		setmsTf.setText("");
		setmasTf.setText("");
		setmgyTf.setText("");

		if (masmode)
			setmasTf.requestFocusInWindow();
		else
		 	setmaTf.requestFocusInWindow();
	}
	
	/**
	 * Delete data from mAs linearity list
	 */
	private void deleteFromListLin(){
		if(nrandPoints!=0)
        {

        	nrandPoints--;

			int index=ListUtilities.getSelectedIndex(randList);

			ListUtilities.remove(index,dlm);
			ListUtilities.select(nrandPoints-1,randList);

	        masv.removeElementAt(index);
			expv.removeElementAt(index);
			randv.removeElementAt(index);

			setmaTf.setText("");
			setmsTf.setText("");
			setmasTf.setText("");
			setmgyTf.setText("");

			if (masmode)
				setmasTf.requestFocusInWindow();
			else
			 	setmaTf.requestFocusInWindow();
		}
	}
	
	/**
	 * Perform mAs linearity QC test
	 */
	private void performCalcLin(){
		isOktoSavemAsLinearity=false;
		resultLinearity=false;
		boolean b=true;

		String cvms=maxPermissibleLinearityTf.getText();
		
		try
        {
		    maxcvLinearity=Convertor.stringToDouble(cvms);
		}
		catch(Exception e)
		{
			b=false;
		    String title =resources.getString("dialog.insertInListError.title");
		    String message =resources.getString("dialog.insertInList.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}

		if(!b)
			   return;
		
		masd=convertVectorToDoubleArray(masv);
		expd=convertVectorToDoubleArray(expv);
		randd=convertVectorToDoubleArray(randv);
		
		if (masd.length==0 || expd.length==0)//if so so is randd!!
		{
		   b=false;
		   String title =resources.getString("dialog.insertInListError.title");
		   String message =resources.getString("dialog.insertInList.message");
		   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	    }

		if(!b)
		{
		    return;
	    }
		
		if (masd.length<2)//if so, so is the rest
		{
			   b=false;
			   String title =resources.getString("dialog.insertInListError.title");
			   String message =resources.getString("dialog.insertInList.message2");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

		}
		
		if(!b)
		   return;
		
		Stats.avevar(randd, randd.length);
		double mean = Stats.ave_avevar;
		double stdev = Stats.var_avevar;
		stdev = Math.sqrt(stdev);
		
		if (mean!=0.0)
			cvLinearity=100*stdev/mean;
		else
			cvLinearity=0.0;

        //------clear first
		randTa.selectAll();
		randTa.replaceSelection("");
        
		for (int i=0;i<randd.length;i++)
	    {
		    randTa.append(resources.getString("rand.rezultat.mas")+
		    		Convertor.formatNumber(masd[i],2)+
		    	";  "+resources.getString("rand.rezultat.mgy")+
		    	Convertor.formatNumber(expd[i],2)+
		    	";  "+resources.getString("rand.rezultat.rand")+
		    	Convertor.formatNumber(randd[i],2)+"  \n");

		}
		
		randTa.append(resources.getString("reprod.rezultat.cv")+
    			Convertor.formatNumber(cvLinearity,2)+"  \n");
		randTa.append(resources.getString("reprod.rezultat.cvmp")+
    			Convertor.formatNumber(maxcvLinearity,2)+"  \n");

        if (cvLinearity<=maxcvLinearity)
        	resultLinearity=true;
        else
        	resultLinearity=false;

        if (resultLinearity)
        	randTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.succes"));
        else
        	randTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.fail"));	
		//=========
		isOktoSavemAsLinearity=true;
	}
	
	/**
	 * Clear mAs linearity list
	 */
	private void resetLin(){
		masv.removeAllElements();
		expv.removeAllElements();
		randv.removeAllElements();

        ListUtilities.removeAll(dlm);
        nrandPoints=0;

		setmaTf.setText("");
		setmsTf.setText("");
		setmasTf.setText("");
		setmgyTf.setText("");

		if (masmode)
			setmasTf.requestFocusInWindow();
		else
		 	setmaTf.requestFocusInWindow();
	}
	
	/**
	 * Perform tube output QC test
	 */
	private void performCalcOutput(){
		isOktoSaveOutput=false;
		//boolean b=true;
		boolean masmode=false;
		double mas=0.0;
		double dose=0.0;
		double distance=0.0;
		maxOutput=0.0;
		
		double unc=0.0;
		//ma,mas check
		try
		{			
		    double ma= Convertor.stringToDouble(mAOutputTf.getText());
			double ms=Convertor.stringToDouble(msOutputTf.getText());
			mas=ma*ms*1.0E-03;//ma*ms*10^-3=mAs
			if (mas!=0)
			{
				masmode=false;
				mAsOutputTf.setText(Convertor.doubleToString(mas));
			}
			else
			{
			    masmode=true;
			    mAOutputTf.setText("");
			    msOutputTf.setText("");
			}
		}
		catch (Exception ex)
		{
			masmode=true;
			mAOutputTf.setText("");
		    msOutputTf.setText("");
		}
		
		try
        {
			unc=Convertor.stringToDouble(estimatedMeasurementUncertaintyTf.getText());
			distance=Convertor.stringToDouble(distanceTf.getText());
			maxOutput=Convertor.stringToDouble(maxPermissibleOutputTf.getText());
		    mas=Convertor.stringToDouble(mAsOutputTf.getText());
    		dose=Convertor.stringToDouble(exposureTf.getText());
		}
		catch(Exception e)
		{
			//b=false;
		    String title =resources.getString("dialog.insertInListError.title");
		    String message =resources.getString("dialog.insertInList.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    mAOutputTf.setText("");
		    msOutputTf.setText("");
		    mAsOutputTf.setText("");
		    exposureTf.setText("");
			if (masmode)
				mAsOutputTf.requestFocusInWindow();
			else
				mAOutputTf.requestFocusInWindow();
			return;
		}

		if (mas<=0 || dose<=0 || distance<=0.0 || maxOutput<=0.0 || unc<0.0)
		{
			//b=false;
		    String title =resources.getString("dialog.insertInTableError.title");
		    String message =resources.getString("dialog.insertInTable.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    mAOutputTf.setText("");
		    msOutputTf.setText("");
		    mAsOutputTf.setText("");
		    exposureTf.setText("");
			if (masmode)
				mAsOutputTf.requestFocusInWindow();
			else
				mAOutputTf.requestFocusInWindow();
			return;

		}
		//if(!b)
		   //return;
		
		//=======================================END CHECK
		if (mrRb.isSelected())
	    {
           //1mR=0.00876 mGy
           dose=dose*0.00876;
		}
		else if(mradRb.isSelected())
		{
			//1mrad=0.01mGy
			dose=dose*0.01;
		}
		dose = dose*1000.0;//uGy
		output=dose/mas;
		double refdistance=100.0;
		if (mf.EXAMINATION_ID==1)
			refdistance=50.0;//cm
		//output convert to refDistance for comparison
		output=output*distance*distance/(refdistance*refdistance);
		resultOutput=false;
		
		outputTa.selectAll();
		outputTa.replaceSelection("");
		outputTa.append(resources.getString("output.rezultat.output")+Convertor.formatNumber(output,2)+"  \n");
		outputTa.append(resources.getString("output.rezultat.reference")+Convertor.formatNumber(maxOutput,2)+"  \n");
		//------------------UNC-------------------
		StatsUtil.confidenceLevel = 0.95;
		double output_unc=output*unc/100.0;
		double maxOutput_unc=maxOutput*0.1/100.0;//0.1.%
		double f_degrees=StatsUtil.evaluateDegreesOfFreedom(output_unc, output);
		double f_poisson=StatsUtil.evaluateDegreesOfFreedom(maxOutput_unc, maxOutput);//10000.0;//for theor
		boolean diffB = StatsUtil.ttest_default_unc(output, maxOutput, 
				output_unc,maxOutput_unc, f_degrees,	f_poisson);
	
		if (output<=maxOutput){
			diffB=false;
		}
//System.out.println("diffB "+diffB+" ounc "+maxOutput_unc+" f "+f_poisson);			
		//----------------------------------------
        if (output<maxOutput)
        	resultOutput=true;
        else
        {
        	if (diffB)//!!!!!!!!!!!!!!!!!!!!        
        		resultOutput=false;
        	else
        		resultOutput=true;
        }
        
        if (resultOutput)
        	outputTa.append(resources.getString("reprod.rezultat2")+
        			" (95% confidence level): "+
        	resources.getString("reprod.rezultat.succes"));
        else
        	outputTa.append(resources.getString("reprod.rezultat2")+
        			" (95% confidence level): "+
        	resources.getString("reprod.rezultat.fail"));	
        
        isOktoSaveOutput=true;
	}
	
	/**
	 * Add data in list for repeatability
	 */
	private void addInListRep(){
		boolean b=true;
        String s1=measuredRepeatabilityTf.getText();
        double d1=0.0;

        try
        {
		    d1=Convertor.stringToDouble(s1);
		}
		catch(Exception e)
		{
			b=false;
		    String title =resources.getString("dialog.insertInListError.title");
		    String message =resources.getString("dialog.insertInList.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    measuredRepeatabilityTf.setText("");
		    measuredRepeatabilityTf.requestFocusInWindow();
		}

		if(!b)
		   return;

        //end test-->la succes se poate merge mai departe

		ListUtilities.add("Measured value :     "+d1+"  ",repeatabilitydlm);
		ListUtilities.select(nRepeatabilityPoints,repeatabilityL);
		s1=Convertor.doubleToString(d1);

		repeatabilityv.addElement(s1);
		nRepeatabilityPoints++;

		measuredRepeatabilityTf.setText("");
		measuredRepeatabilityTf.requestFocusInWindow();	
	}
	
	/**
	 * Delete data from repeatability list 
	 */
	private void deleteFromListRep(){
		if(nRepeatabilityPoints!=0)
        {

        	nRepeatabilityPoints--;

			int index=ListUtilities.getSelectedIndex(repeatabilityL);

			ListUtilities.remove(index,repeatabilitydlm);
			ListUtilities.select(nRepeatabilityPoints-1,repeatabilityL);

			repeatabilityv.removeElementAt(index);

			measuredRepeatabilityTf.setText("");
			measuredRepeatabilityTf.requestFocusInWindow();
		}	
	}
	
	/**
	 * Perform repeatability QC test
	 */
	private void performCalcRep(){
		isOktoSaveOutputReapeatability=false;
		resultRepeatability=false;
		boolean b=true;

		String cvms=maxPermissibleRepeatabilityTf.getText();
  		
        try
        {
		    maxcvRepeatability=Convertor.stringToDouble(cvms);
		}
		catch(Exception e)
		{
			b=false;
		    String title =resources.getString("dialog.insertInListError.title");
		    String message =resources.getString("dialog.insertInList.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}

		if(!b)
			   return;
		
		repeatabilityd=convertVectorToDoubleArray(repeatabilityv);

		if (repeatabilityd.length==0)
		{
		   b=false;
		   String title =resources.getString("dialog.insertInListError.title");
		   String message =resources.getString("dialog.insertInList.message");
		   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

	    }
		
		if(!b)
			   return;
		
		if (repeatabilityd.length<2)
		{
			   b=false;
			   String title =resources.getString("dialog.insertInListError.title");
			   String message =resources.getString("dialog.insertInList.message2");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

		}
		
		if(!b)
		   return;
		
		Stats.avevar(repeatabilityd, repeatabilityd.length);
		double mean = Stats.ave_avevar;
		double stdev = Stats.var_avevar;
		stdev = Math.sqrt(stdev);
		
		if (mean!=0.0)
			cvRepeatability=100*stdev/mean;
		else
			cvRepeatability=0.0;

        //------clear first
		repeatabilityTa.selectAll();
        repeatabilityTa.replaceSelection("");
                
    	repeatabilityTa.append(resources.getString("reprod.rezultat.cv")+
    			Convertor.formatNumber(cvRepeatability,2)+"  \n");
    	repeatabilityTa.append(resources.getString("reprod.rezultat.cvmp")+
    			Convertor.formatNumber(maxcvRepeatability,2)+"  \n");

        if (cvRepeatability<=maxcvRepeatability)
        	resultRepeatability=true;
        else
        	resultRepeatability=false;

        if (resultRepeatability)
        	repeatabilityTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.succes"));
        else
        	repeatabilityTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.fail"));	
        
        isOktoSaveOutputReapeatability=true;
	}
	
	/**
	 * Given a vector v, this routine converts it into a double array.
	 * @param v v
	 * @return the result
	 */
	private double[] convertVectorToDoubleArray(Vector<String> v)
	{
		double[] result = new double[v.size()];
		for(int i=0; i<v.size(); i++)
		{
			String s=(String)v.elementAt(i);
			result[i]=Convertor.stringToDouble(s);
		}
		return result;
	}
	
	/**
	 * Clear repeatability list
	 */
	private void resetRep(){
		repeatabilityv.removeAllElements();
        ListUtilities.removeAll(repeatabilitydlm);
        nRepeatabilityPoints=0;
        measuredRepeatabilityTf.setText("");
        measuredRepeatabilityTf.requestFocusInWindow();	
	}	
	
	/**
	 * Go to save in database
	 */
	private void save(){
		int itab = mainTab.getSelectedIndex();
		SaveViewDBFrame.ITAB=itab;//1;
		new SaveViewDBFrame(this);
	}

}
