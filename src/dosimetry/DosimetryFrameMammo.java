package dosimetry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import danfulea.db.DatabaseAgent;
//import jdf.db.DBConnection;
import danfulea.math.Convertor;
import danfulea.math.Interpolation;
import danfulea.math.Sort;
import danfulea.math.StatsUtil;
import danfulea.phys.XRay;
import danfulea.phys.XRaySpectrum;
import danfulea.utils.FrameUtilities;
import danfulea.utils.SystemInfo;
import radQC.RadQCFrame;

/**
 * Computes breast doses, evaluates AGD (Average Galandular Dose) and lifetime attributable cancer risk for 
 * patients undergoing mammographic examinations. In the past, this class was used for evaluation of patient doses 
 * using KERMA approximation (IradMed program). Nowadays, this class prepares an input file for a more complex 
 * Monte Carlo engine which is based on GEANT4 simulation toolkit and then runs the corresponding C++ program. Inside current 
 * folder there is a file (MonteCarloPath.txt) having a single line which points to the C++ program folder (e.g. D:\\dhpre_exe_web). 
 * Hence, this works out of the box for Windows users having both C++ program and this Java program (available on sourceforge). On Linux or Mac, you have to 
 * compile and generate executable from the C++ source code, then modify this class to talk with the C++ program.
 * 
 * @author Dan Fulea, 07 May 2015
 */
@SuppressWarnings("serial")
public class DosimetryFrameMammo extends JFrame implements ActionListener, Runnable{

	private volatile Thread simTh;
	
	private static String filenameMC = "MonteCarloPath.txt";
	private static String doseExeName= "DHPRE.exe";
	private static String detExeNameLinux = "DHPRE";
	protected static String doseExeFileName= "DHPRE";
	protected String doseFolderURL;
	protected String macroFilename;
	protected File macroFile;
	
	private static final Dimension PREFERRED_SIZE = new Dimension(950, 720);
	private static final Dimension sizeCb = new Dimension(70, 21);
	//private static final Dimension sizeCb2 = new Dimension(150, 21);
	private static final Dimension textAreaDimension = new Dimension(900, 100);
	private static final String BASE_RESOURCE_CLASS = "dosimetry.resources.DosimetryFrameResources";
	protected ResourceBundle resources;
	private RadQCFrame mf;
	
	protected String dosimetryTable="";
	protected String measurementDate_toSave="";
	protected String mainDB = "";
	protected int IDLINK=0;
	protected int EXAMINATION_ID=1;
	
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private static final String KAP_COMMAND = "KAP";
	private static final String SAVE_COMMAND = "SAVE";
	private String command = null;
	
	@SuppressWarnings("rawtypes")
	private JComboBox kvCb, anodeAngleCb, rippleCb, usemasCb, anodeMatCb;	
	private JTextField phantomAgeTf = new JTextField(5);
	
	private JTextField filtrationTf = new JTextField(15);
	private JTextField mAsTf = new JTextField(5);
	protected JTextField kapTf = new JTextField(15);
	protected JTextField focusmidplaneTf = new JTextField(15);//here it is focus to breast entrance
	private JTextField runsTf = new JTextField(5);
	
	private String totalFiltrationStr="";
	private double HVL=0.0;
	
	private JTextField bsfTf = new JTextField(5);
	protected JTextArea textArea = new JTextArea();
	private JTextField estimatedMeasurementUncertaintyTf=new JTextField(5);
	protected JCheckBox graphicSceneAutoRefreshCh;//changed to view/not view graphic simulation
	
	private JTextField breastDiameterTf = new JTextField(5);
	private JTextField breastThicknessTf = new JTextField(5);
	//=================
	protected double breastThickness=0.0;
	protected double breastDiameter=0.0;
	//protected double focusToBreastEntrance=0.0;
	//=================
	protected boolean isOkToSave=false;
	protected String effectiveDose="";//0.0;888//here is MC DOSE~AGD!!!!!!!!!!!!!!!!!!!!!!!!
	protected String effectiveDoseUnit="";//8888
	protected String risk="";//0.0;8888
	//==============================
	protected String phantomAge_toSave="";
	protected String KAP_toSave="";//444
	protected String FSD_toSave="";//444
	protected String KAIR_toSave="";//444
	protected String ESAK_toSave="";//555
	protected String AGD_toSave="";
	protected String DRL_toSave="";//AGD here
	
	protected String filtration_toSave="";
	protected String kv_toSave="";//5555
	protected String anodeAngle_toSave="";//
	protected String ripple_toSave="";//
	protected String resultTest_toSave="";//555
	protected String unc_toSave="";//555
	protected String anodeMaterial_toSave="";
	protected String breastDiameter_toSave="";
	protected String breastThickness_toSave="";
	
	private Connection radqcdbcon = null;
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public DosimetryFrameMammo(RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("DosimetryFrameMammo.NAME"));
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
    	//======================
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel createMainPanel() {
		String[] anodeMatItems = (String[])resources.getObject("anodeMaterial.cb");
		anodeMatCb=new JComboBox(anodeMatItems);
		anodeMatCb.setMaximumRowCount(5);
		anodeMatCb.setPreferredSize(sizeCb);
		anodeMatCb.setSelectedItem((String)"MO");
		
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
		
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		estimatedMeasurementUncertaintyTf.setText("5");
	    JPanel puncP=new JPanel();
		puncP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("expData.unc"));
        puncP.add(label);
        puncP.add(estimatedMeasurementUncertaintyTf);        
        puncP.setBackground(RadQCFrame.bkgColor);
        		
		String[] yesnoItems = (String[])resources.getObject("usemas.cb");
		usemasCb=new JComboBox(yesnoItems);
		usemasCb.setMaximumRowCount(5);
		usemasCb.setPreferredSize(sizeCb);
		usemasCb.setSelectedIndex(1);//no
		
		//kvCb
		int indx = 0;
		int sup=32;
		int inf =25;
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
		kvCb.setSelectedItem((String)"28");
		
		//anodeAngleCb
		sup=23;
		inf =9;
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
		sup=0;
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
		breastDiameterTf.setText("14");
		breastThicknessTf.setText("5");
		filtrationTf.setText(totalFiltrationStr);
		focusmidplaneTf.setText("45");
		runsTf.setText("20000");
		bsfTf.setText("1.1");
		bsfTf.setToolTipText(resources.getString("BSF.tooltip"));
		
		//Phantom selection
		        
        JPanel phantomAgeP = new JPanel();
        phantomAgeP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
        label=new JLabel(resources.getString("phantomAge.label"));
        phantomAgeP.add(label);
        phantomAgeP.add(phantomAgeTf);
        label=new JLabel(resources.getString("phantom.breast.diameter.label"));
        phantomAgeP.add(label);
        phantomAgeP.add(breastDiameterTf);
        label=new JLabel(resources.getString("phantom.breast.thickness.label"));
        phantomAgeP.add(label);
        phantomAgeP.add(breastThicknessTf);
        phantomAgeP.setBackground(RadQCFrame.bkgColor);
                
        //-------------
        JPanel phantomP = new JPanel();
		BoxLayout blphantomP = new BoxLayout(phantomP, BoxLayout.Y_AXIS);
		phantomP.setLayout(blphantomP);
		phantomP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("phantom.border"),
				RadQCFrame.foreColor));		
		
		phantomP.add(phantomAgeP);		
		phantomP.setBackground(RadQCFrame.bkgColor);
		//=============
		//Tube settings
		JPanel kvP=new JPanel();
		kvP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("kv.label"));
        kvP.add(label);
        kvP.add(kvCb);        
        kvP.setBackground(RadQCFrame.bkgColor);
        label=new JLabel(resources.getString("anodeAngle.label"));       
        kvP.add(label);
        kvP.add(anodeAngleCb);        
        label=new JLabel(resources.getString("ripple.label"));        
        kvP.add(label);
        kvP.add(rippleCb);
        //========================================================
        label=new JLabel(resources.getString("anodeMaterial.label"));        
        kvP.add(label);
        kvP.add(anodeMatCb);
        //-------------
        JPanel mAsP=new JPanel();
        mAsP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("mAs.label"));
        mAsP.add(label);
        mAsP.add(mAsTf);        
        mAsP.setBackground(RadQCFrame.bkgColor);
        
        JPanel filtrationP=new JPanel();
        filtrationP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("filtration.label.mammo"));
        filtrationP.add(label);
        filtrationP.add(filtrationTf);	
		filtrationP.setBackground(RadQCFrame.bkgColor);
		
        JPanel tubeP = new JPanel();
		BoxLayout bltubeP = new BoxLayout(tubeP, BoxLayout.Y_AXIS);
		tubeP.setLayout(bltubeP);
		tubeP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("tube.border"),
				RadQCFrame.foreColor));		
		tubeP.add(kvP);
		tubeP.add(filtrationP);			
		tubeP.add(mAsP);	
		tubeP.setBackground(RadQCFrame.bkgColor);
		//------------------------------
		//KAP and Examination settings
		JPanel kapP=new JPanel();
		kapP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("kap.label"));
        kapP.add(label);
        kapP.add(kapTf);
        buttonName = resources.getString("KAPB");
		buttonToolTip = resources.getString("KAPB.toolTip");
		buttonIconName = "";//resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, KAP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("KAPB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
        kapP.add(button);
        kapP.setBackground(RadQCFrame.bkgColor);
        
        JPanel usemasP=new JPanel();
		usemasP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("usemas.label"));
        usemasP.add(label);
        usemasP.add(usemasCb);
        usemasP.setBackground(RadQCFrame.bkgColor);
        
        JPanel distanceP=new JPanel();
        distanceP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("distance.label.mammo"));
        distanceP.add(label);
        distanceP.add(focusmidplaneTf);
        distanceP.setBackground(RadQCFrame.bkgColor);
                
        JPanel examP = new JPanel();
		BoxLayout blexamP = new BoxLayout(examP, BoxLayout.Y_AXIS);
		examP.setLayout(blexamP);
		examP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("exam.border"),
				RadQCFrame.foreColor));		
		examP.add(kapP);
		examP.add(usemasP);
		examP.add(distanceP);			
		examP.setBackground(RadQCFrame.bkgColor);
		//===============
		
		JPanel p41P=new JPanel();
        p41P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(resources.getString("BSF.label"));
        p41P.add(label);
        p41P.add(bsfTf);
        p41P.setBackground(RadQCFrame.bkgColor);
        
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
		northP.add(p41P);northP.add(puncP);	
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
	 * Initialize database. Retrieve tube total filtration (if available).
	 */
	private void performQueryDB(){
		totalFiltrationStr="";
		HVL=0.0;
		try {
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			///opens = opens + file_sep + dbName;

			//int dummy=1;
			String str = "select * from " + mf.hvlFiltrationTable+" where IDLINK = "+mf.IDLINK +" order by Unique_ID";
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			
			Statement s = radqcdbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);//con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet res = s.executeQuery(str);
			res.last();//last record
			
			totalFiltrationStr=res.getString(8);
			HVL=Convertor.stringToDouble(res.getString(5));

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
			totalFiltrationStr="0.5";
			HVL=0.36;
		}
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
		}  else if (command.equals(KAP_COMMAND)) {
			kapEvaluation();
		} else if (command.equals(SAVE_COMMAND)){
			save();
		} 
	}
	
	/**
	 * Perform calculation
	 */
	private void compute(){
		isOkToSave=false;
		double kap=0.0;
		double FCA=0.0;
		double bsf=0.0;
		double unc=0.0;
				
		kv_toSave = (String)kvCb.getSelectedItem();		
		filtration_toSave = filtrationTf.getText();
		anodeAngle_toSave=(String)anodeAngleCb.getSelectedItem();
		ripple_toSave=(String)rippleCb.getSelectedItem();
		phantomAge_toSave=phantomAgeTf.getText();//ageGroup;
		unc_toSave=estimatedMeasurementUncertaintyTf.getText();
		breastDiameter_toSave=breastDiameterTf.getText();
		breastThickness_toSave=breastThicknessTf.getText();
		anodeMaterial_toSave=(String)anodeMatCb.getSelectedItem();
		double filtration = 0.0;//filtrationTf.getText();
		double kv=0.0;
		String useMAs = (String)usemasCb.getSelectedItem();		
		double mas=0.0;
		
		try
	    {
			if(useMAs.equals("no")){
				kap=Convertor.stringToDouble(kapTf.getText());
				//System.out.println("enter "+ kap);
			}
		    FCA=Convertor.stringToDouble(focusmidplaneTf.getText());//FSD
		    bsf=Convertor.stringToDouble(bsfTf.getText());
		    unc=Convertor.stringToDouble(estimatedMeasurementUncertaintyTf.getText());
		    breastDiameter=Convertor.stringToDouble(breastDiameterTf.getText());
		    breastThickness=Convertor.stringToDouble(breastThicknessTf.getText());
		    kv=Convertor.stringToDouble(kv_toSave);
		    filtration=Convertor.stringToDouble(filtrationTf.getText());
		}
		catch(Exception e)
		{
			//b=false;
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		if(useMAs.equals("no")){
			if (kap<0 || FCA<=0.0 || breastDiameter<=0.0 || breastThickness<=0.0){
				//b=false;
			    String title =resources.getString("dialog.number.title");
			    String message =resources.getString("dialog.number.negative.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			    return;
			}
		} else 
			if (FCA<=0.0 || breastDiameter<=0.0 || breastThickness<=0.0){
				//b=false;
					String title =resources.getString("dialog.number.title");
					String message =resources.getString("dialog.number.negative.message");
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
					return;			
			}
		
		//area default
		double pi = 3.14159265359;
		double area=breastDiameter*breastDiameter;//pi*breastDiameter*breastDiameter/4.0;//breastDiameter*breastDiameter;//cm2
		area=area*10.0*10.0;//mm2
		//
		if(useMAs.equals("yes")){
			String mAs = mAsTf.getText();
			try{
				if (Convertor.stringToDouble(mAs)<=0){
					String title =resources.getString("dialog.number.title");
				    String message =resources.getString("dialog.mas.message");
				    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				    return;
				}
			}catch (Exception e){
				String title =resources.getString("dialog.number.title");
			    String message =resources.getString("dialog.mas.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			    return;
			}
			
			mas= Convertor.stringToDouble(mAs);
			
			if(filtration<=0){
				String title =resources.getString("dialog.number.title");
			    String message =resources.getString("dialog.filtration.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			    return;
			}
			
			String manod = (String)anodeMatCb.getSelectedItem();//"W";//request.getParameter("manode");
			
			int ianod = 0;
			if (manod.compareTo("W") == 0)
				ianod = 0;
			else if (manod.compareTo("MO") == 0)
				ianod = 1;
			else if (manod.compareTo("RH") == 0)
				ianod = 2;
			
			String uanod = (String)anodeAngleCb.getSelectedItem();//request.getParameter("uanod");			
			String ripple = (String)rippleCb.getSelectedItem();//request.getParameter("ripple");			
			if (!validateComputationMode(ianod, Convertor.stringToDouble(ripple),	
					Convertor.stringToDouble(uanod),kv)) {
				//out.println("<br>XRay Spectrum allowed inputs: For Mo,Rh material: 9'<='anodAngle'<='23; 25'<='kv'<='32; ripple=0 !");
				//out.println("<br>XRay For W material: 6'<='anodAngle'<='22; 30'<='kv'<='150; ripple=0! For not null ripple, kv must be only 55,60,65,...,90!!");
				String title =resources.getString("dialog.number.title");
			    String message =resources.getString("dialog.ripple");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return;
			}
			//evaluate KAP from mAs
			XRay.reset();//HERE WE RESET!!!!!!!!!!!!!!!!!!!!!!
	       	XRay.ICALC=1;
	       	//int is=anodeAngleCb.getSelectedIndex();
	       	XRay.ianod=ianod;//is;//0W-Wolfram//0 means 1 which is Mo. 
	       	//s=(String)irippleCb.getSelectedItem();
	       	//is=Convertor.stringToInt(s);
	       	XRay.iripple=Convertor.stringToInt(ripple);//is;	
	       	String filesname="AL";//allways================>EXTERNAL???
			XRay.readAttCoef(filesname,1);//allways================>EXTERNAL???
			XRay.TMM[0]=filtration;//allways================>EXTERNAL???
			
			double uAnodD=Convertor.stringToDouble(uanod);
			new XRaySpectrum(kv,filtration,uAnodD);
			double kermapermas75=XRay.KERMAPERMASAT750MM;//uGy/mAs
			double kerma75=kermapermas75*mas;//uGy
			//System.out.println("k/mas75 = "+kermapermas75);
			double kermaFCA=kerma75*75.0*75.0/FCA/FCA;//at breastEntrance
			
			//recalculate area to match C!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			area=pi*breastDiameter*breastDiameter/4.0;//breastDiameter*breastDiameter;//cm2
			area=area*10.0*10.0;//mm2
			
			kap=kermaFCA*area;//area in mm2; kap=>uGymm2
		}
		
		//System.out.println("yep "+kap);
		
		textArea.selectAll();
		textArea.replaceSelection("");
		
		double error = 0.0;
		KAP_toSave=Convertor.doubleToString(kap);//uGymm2
		double kspmgycm2=kap/1000.0/10.0/10.0;//uGymm2=>mGycm2
		error = Math.abs(unc*kspmgycm2/100.0);//for ESAK
		textArea.append(resources.getString("kap.KAP.mgycm2")+
				Convertor.formatNumberScientific(kspmgycm2)+
				" +/- "+Convertor.formatNumberScientific(error)+"\n");
		
		double kermaAtFCA=kap/area;//uGy//////////////////////////////////////////WITH ROUND AREA!!!!!!!!!!!!!!
		kermaAtFCA=kermaAtFCA/1000.0;//mGy
		
		FSD_toSave=Convertor.doubleToString(FCA);
		double kermaAtFSD=kermaAtFCA;
		KAIR_toSave=Convertor.doubleToString(kermaAtFSD);
		error = Math.abs(unc*kermaAtFSD/100.0);//for ESAK		
		textArea.append(resources.getString("Kair.mgy")+
				Convertor.formatNumberScientific(kermaAtFSD)+
				" +/- "+Convertor.formatNumberScientific(error)+"\n");
		
		double ESAK=kermaAtFSD*bsf;
		ESAK_toSave=Convertor.doubleToString(ESAK);
		error = Math.abs(unc*ESAK/100.0);//for ESAK
		textArea.append(resources.getString("ESAK.mgy")+
				Convertor.formatNumberScientific(ESAK)+
				" +/- "+Convertor.formatNumberScientific(error)+"\n");
		
		//$$$$$$$$$$$$$$$$$$
		//System.out.println("HVL= "+HVL);
		//HVL=3.2;breastThickness=9.0;kv=35;
		double [] HVLArray = (double[])resources.getObject("HVL.array.AGD");
		double [] kvArray = (double[])resources.getObject("kv.array.AGD");
		double [] breastThicknessArray = (double[])resources.getObject("breastThickness.array.AGD");
		
		Sort.findNearestValue(kvArray, kv, true);
		int indexKv_low=Sort.getNearestPosition();
		//int indexKv_high=indexKv_low+1;
		if (indexKv_low==kvArray.length-1)//out of range
		{
			if (kv<kvArray[0])
			{
				indexKv_low=0;
				//indexKv_high=1;
			}
			else//in the right outside!!
			{
				indexKv_low=kvArray.length-2;
				//indexKv_high=kvArray.length-1;
			}
		}
		
		Sort.findNearestValue(HVLArray, HVL, true);
		int indexHVL_low=Sort.getNearestPosition();
		int indexHVL_high=indexHVL_low+1;
		if (indexHVL_low==HVLArray.length-1)//out of range
		{
			if (HVL<HVLArray[0])
			{
				indexHVL_low=0;
				indexHVL_high=1;
			}
			else//in the right outside!!
			{
				indexHVL_low=HVLArray.length-2;
				indexHVL_high=HVLArray.length-1;
			}
		}
		
		Sort.findNearestValue(breastThicknessArray, breastThickness, true);
		int indexThickness_low=Sort.getNearestPosition();
		int indexThickness_high=indexThickness_low+1;
		if (indexThickness_low==breastThicknessArray.length-1)//out of range
		{
			if (breastThickness<breastThicknessArray[0])
			{
				indexThickness_low=0;
				indexThickness_high=1;
			}
			else//in the right outside!!
			{
				indexThickness_low=breastThicknessArray.length-2;
				indexThickness_high=breastThicknessArray.length-1;
			}
		}
		
		int gRow_low=3*indexKv_low+indexHVL_low;
		int gRow_high=gRow_low+1;
		if (gRow_low==8)//out of range
		{			
			gRow_low=7;
			gRow_high=8;		
		}
		//System.out.println("kv low "+indexKv_low);
		//System.out.println("HVL low "+indexHVL_low);
		//System.out.println("thick low "+indexThickness_low);
		//System.out.println("gRow low "+gRow_low);
		
		double [][] gArray = (double[][])resources.getObject("g.array.AGD");
		double glow_low=gArray[gRow_low][indexThickness_low];
		double glow_high=gArray[gRow_low][indexThickness_high];
		double ghigh_low=gArray[gRow_high][indexThickness_low];
		double ghigh_high=gArray[gRow_high][indexThickness_high];
		
		//System.out.println("ll "+glow_low+"  lh "+glow_high);
		//System.out.println("hl "+ghigh_low+"  hh "+ghigh_high);
		
		//CROSS INTERPOLATION
		double vLL = 
			Interpolation.linInt(breastThicknessArray[indexThickness_low], glow_low, 
					breastThicknessArray[indexThickness_high], glow_high, breastThickness);
		double vLH = 
			Interpolation.linInt(breastThicknessArray[indexThickness_low], ghigh_low, 
					breastThicknessArray[indexThickness_high], ghigh_high, breastThickness);
		
		double gAGD=Interpolation.linInt(HVLArray[indexHVL_low], vLL, 
				HVLArray[indexHVL_high], vLH, HVL);	

		//1mR=0.00876 mGy=>1R=1000 mR=8.76mGy
		double ESAKR=ESAK*(1.0/8.76);//in R
		double AGD=ESAKR*gAGD;//mRAd
		//1mGy=100mRad
		AGD=AGD*(1.0/100.0);//mGy
		error = Math.abs(unc*AGD/100.0);//for ESAK
		textArea.append(resources.getString("AGD.mgy")+
				Convertor.formatNumberScientific(AGD)+
				" +/- "+Convertor.formatNumberScientific(error)+"\n");
		AGD_toSave=Convertor.doubleToString(AGD);
		//=======================DRL==============
		double [] breastThicknessArrayDRL = (double[])resources.getObject("breastThickness.array.AGD.DRL");
		double [] DRLArray = (double[])resources.getObject("breastThickness.array.DRL");
		Sort.findNearestValue(breastThicknessArrayDRL, breastThickness, true);
		int index_low=Sort.getNearestPosition();
		int index_high=index_low+1;
		if (index_low==breastThicknessArrayDRL.length-1)//out of range
		{
			if (breastThickness<breastThicknessArrayDRL[0])
			{
				index_low=0;
				index_high=1;
			}
			else//in the right outside!!
			{
				index_low=breastThicknessArrayDRL.length-2;
				index_high=breastThicknessArrayDRL.length-1;
			}
		}
		//System.out.println("ll "+index_low+"  lh "+index_high);
		double DRL = 
			Interpolation.linInt(breastThicknessArrayDRL[index_low], DRLArray[index_low], 
					breastThicknessArrayDRL[index_high], DRLArray[index_high], breastThickness);
		textArea.append(resources.getString("DRL.mgy")+
				Convertor.formatNumber(DRL,2)+
				"\n");
		//double DRL = 1.5;
		//$$$$$$$$$$$$$$$$$$$$$
		DRL_toSave=Convertor.doubleToString(DRL);
		boolean testSuccess=true;
		StatsUtil.confidenceLevel = 0.95;
		double ESAK_unc=AGD*unc/100.0;//AGD HERE!!!!!!!!!!!!!!!
		double DRL_unc=DRL*0.1/100.0;//0.1.%
		double f_degrees=StatsUtil.evaluateDegreesOfFreedom(ESAK_unc, AGD);
		double f_poisson=StatsUtil.evaluateDegreesOfFreedom(DRL_unc, DRL);
		boolean diffB = StatsUtil.ttest_default_unc(AGD, DRL, 
				ESAK_unc,DRL_unc, f_degrees,	f_poisson);
		
		
		if (AGD<=DRL)
			testSuccess=true;
		else
		{
			if (diffB)        
				testSuccess=false;
        	else
        		testSuccess=true;
		}
		if (testSuccess){
			textArea.append(resources.getString("rezultat.AGD")+
				   	resources.getString("rezultat.succes")+" (95% confidence level)"+"  \n");
			resultTest_toSave="PASSED";
		}
		else{
			textArea.append(resources.getString("rezultat.AGD")+
		       	resources.getString("rezultat.fail")+" (95% confidence level)"+"  \n");
			resultTest_toSave="NOT PASSED";
		}
		//===========================
		isOkToSave=true;
		//NOW, AGD for good
		startMC();
	}
	
	/**
	 * Go to save in database
	 */
	private void save(){
		new SaveViewDBFrameMammo(this);
	}

	/**
	 * Go to KAP evaluation (Kerma-Area-Product)
	 */
	private void kapEvaluation(){
		//focusToBreastEntrance=0.0;//focus breast
		
		try
	    {		   
		    //unc=Convertor.stringToDouble(estimatedMeasurementUncertaintyTf.getText());
			//focusToBreastEntrance=Convertor.stringToDouble(focusmidplaneTf.getText());
		    breastDiameter=Convertor.stringToDouble(breastDiameterTf.getText());
		    breastThickness=Convertor.stringToDouble(breastThicknessTf.getText());
		}
		catch(Exception e)
		{			
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		if (breastDiameter<=0.0 ||  breastThickness<=0.0){
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.number.negative.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		new KAPEvalFrameMammo(this);
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
		private String LD_LIBRARY_PATH="";
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
		LD_LIBRARY_PATH="";
		
		File f = new File(filename1);
		//int i = 0;
		String pathMC = "";

		int countLine=0;//@@@@@@@@@@@@@@@@@@@@@@@@@
		//StringBuffer desc = new StringBuffer();
		//boolean haveData = false;
		String line = "";
		if (f.exists()) {
			try {
				//FileReader fr = new FileReader(f);
				BufferedReader reader = new BufferedReader(new FileReader(filename1));
				line=reader.readLine();
				while (line!=null) {//while ((i = fr.read()) != -1) {
					//if (!Character.isWhitespace((char) i)) {
					//	desc.append((char) i);
					//	haveData = true;
					//} else {
					//	if (haveData)// we have data
					//	{
					//		haveData = false;// reset
					if (countLine==0){//@@@@@@@@@@@@@@
						pathMC = line;//pathMC + desc.toString();//System.out.println(pathMC);
					} else if (countLine==1){
						G4LEDATA=line;//G4LEDATA+desc.toString();
					}else if (countLine==2){
						G4LEVELGAMMADATA=line;//G4LEVELGAMMADATA+desc.toString();
					}else if (countLine==3){
						G4NEUTRONHPDATA=line;//G4NEUTRONHPDATA+desc.toString();
					}else if (countLine==4){
						G4NEUTRONXSDATA=line;//G4NEUTRONXSDATA+desc.toString();
					}else if (countLine==5){
						G4PIIDATA=line;//G4PIIDATA+desc.toString();
					}else if (countLine==6){
						G4RADIOACTIVEDATA=line;//G4RADIOACTIVEDATA+desc.toString();
					}else if (countLine==7){
						G4REALSURFACEDATA=line;//G4REALSURFACEDATA+desc.toString();
					}else if (countLine==8){
						G4SAIDXSDATA=line;//G4SAIDXSDATA+desc.toString();
					} else if (countLine==9){
						LD_LIBRARY_PATH=line;//LD_LIBRARY_PATH+desc.toString();
					}
							countLine++;
							line=reader.readLine();
						//}
						//desc = new StringBuffer();
					//}
				}
				reader.close();//fr.close();
				
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
					str = str+LD_LIBRARY_PATH+"\n";
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
				
				//EXTRACTIONS==========================HERE NOT EFFECTIVE DOSE, IN C=>MATERIAL: ADIPOSE_GLANDULAR! OK!!!
				String str = " Dose in Breast";
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
						
						//THIS IS NEEDED SINCE SCRIPT LIKE SHELL WAITS FOR EXIT!!!!
						if (SystemInfo.isLinux())
							   break;
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
		str = str + "/phantom/setPhantomModel MAMMO" + "\n";
		//String sex = (String)phantomSexCb.getSelectedItem();
		//str = str + "/phantom/setPhantomSex" + " " + sex + "\n";
		//String ageGroup = getAgeGroup();
		//str = str + "/phantom/setAgeGroup" + " " + ageGroup + "\n";
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
		
		String distance = focusmidplaneTf.getText();//request.getParameter("distance");
		str = str + "/phantom/focusToBreastDistance" + " " + distance +" cm"+ "\n";
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
		
		String phantomDiameter=breastDiameterTf.getText();
		str = str + "/phantom/breast_total_diameter" + " " + phantomDiameter +" cm"+ "\n";
		try
	    {
			if(Convertor.stringToDouble(phantomDiameter)<=0.0){
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
		
		String phantomHeight = breastThicknessTf.getText();//request.getParameter("phantomMass");
		str = str + "/phantom/breast_total_height" + " " + phantomHeight +" cm"+ "\n";
		try
	    {
			if(Convertor.stringToDouble(phantomHeight)<=0.0){
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
				
		if (graphicSceneAutoRefreshCh.isSelected()){//changed in vis-novis!!
			str=str+"/control/execute visMammo.mac"+"\n";					
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
		
		String manod = (String)anodeMatCb.getSelectedItem();//"W";//request.getParameter("manode");
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
		
		String useMAs = (String)usemasCb.getSelectedItem();//request.getParameter("mAsForDAP");
		str = str + "/xfield/use_mAs_for_DAP_calculation?" + " " + useMAs + "\n";
		
		String mAs = mAsTf.getText();//request.getParameter("mAs");
		if(useMAs.equals("no"))
			mAs="100.0";//just fill in some not null value
		str = str + "/xfield/mAs" + " " + mAs + "\n";
		try
	    {
			if(Convertor.stringToDouble(mAs)<=0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.mas.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.mas.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		//String polarAngle = "0.0";//request.getParameter("polarAngle");
		//str = str + "/xfield/polarAngle" + " " + polarAngle +" deg"+ "\n";
		
		//String azimuthalAngle = Convertor.doubleToString(getProjectionAngle());//request.getParameter("azimuthalAngle");
		//str = str + "/xfield/azimuthalAngle" + " " + azimuthalAngle +" deg"+ "\n";
			
		String DAP = kapTf.getText();//request.getParameter("DAP");
		if(useMAs.equals("yes"))
			DAP="250.0";//just fill in some not null value
		str = str + "/xfield/DAP[uGymm2]" + " " + DAP + "\n";
		try
	    {
			if(Convertor.stringToDouble(DAP)<=0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.kap.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.kap.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		//String exam = (String)examinationCb.getSelectedItem();;//request.getParameter("exam");
		//String[] exams = getFieldsParameters(exam, ageGroup);
		//str = str + "/xfield/centerY" + " " + exams[0] + "\n";
		//str = str + "/xfield/centerX" + " " + exams[3] + "\n";
		//str = str + "/xfield/width" + " " + exams[1] + "\n";
		//str = str + "/xfield/height" + " " + exams[2] + "\n";
		
		str=str+"/event/printModulo 1000"+"\n";//internal default
		
		/*
		<option value="beamAlongX">Uniform parallel beam along x-axis</option>
			<option value="beamAlongY">Uniform parallel beam along y-axis</option>
			<option value="beamAlongZ">Uniform parallel beam along z-axis</option>
			<option value="isotropicFlux">Isotropic flux</option>
			<option value="rectangleField" selected="selected">Radiographic rectangle field</option>
			<option value="CTScan">CTScan geometry</option>	
		 */
		//String beam = "rectangleField";//request.getParameter("beam");//not important for mammo
		//str = str + "/gun/setBeam" + " " + beam + "\n";
		
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
		String sliceThickness = "1.0";//request.getParameter("sliceThickness");
		str = str + "/xfield/sliceThickness" + " " + sliceThickness +" mm"+ "\n";
		
		String pitch = "1.0";//request.getParameter("pitch");
		str = str + "/xfield/pitch" + " " + pitch+ "\n";
		
		String angleIncrement = "1.0";//request.getParameter("angleIncrement");
		str = str + "/xfield/angleIncrement" + " " + angleIncrement +" deg"+ "\n";
		
		String fanBeam = "on";//request.getParameter("fanBeam");
		str = str + "/xfield/SetFanBeam" + " " + fanBeam + "\n";
		
		String CTDI = "250.0";//request.getParameter("CTDI");
		str = str + "/xfield/CTDI[uGy]" + " " + CTDI + "\n";
		
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
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
		
		//unitS=unitS.replaceAll("Gy", "Sv");//HERE IS AGD!!!!!!!!!!!
		
		result[0]=valueS;
		result[1]=unitS;
		
		return result;
	}
}
