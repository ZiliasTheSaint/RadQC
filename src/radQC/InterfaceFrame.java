package radQC;

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

import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;
import danfulea.utils.SystemInfo;

/**
 * BIPM (Bureau International des Poids et Mesures) can perform calibration of exposure 
 * (X) for BIPM standard ionization chamber using various X-ray tube settings or various 
 * standard gamma sources such 137Cs or 60Co. Exposure X=DQ/Dm and all electrons are 
 * stopped in Dm (definition of exposure) hence EEC is met (Electron Equilibrium 
 * Condition). From X, KERMA K is simply: K=(W/e) X /(1-g). W/e in air is known to be 
 * 33.07J/C. The radiative correction factor (fraction of electron energy loss by 
 * bremsstrahlung ) g is knwon for various sources (e.g. MC simulations). 
 * Of course, for BIPM standard, the collision kerma K(1-g) is equal with the absorbed 
 * dose in standard ionization chamber.<br>

 * All other dosimeters, regardless of their dimensions can be calibrated versus 
 * primary BIPM standard in terms of kerma in air using a calibration factor 
 * (kerma  = reading x cal_factor). It doesn't matter if electrons are stopped or not 
 * in the dosimeter chamber which is CONSISTENT WITH KERMA DEFINITION VERSUS EXPOSURE. 
 * That's why KERMA is a powerful concept. Of course, under EEC and when radiative loss 
 * of electron energy is neglected, KERMA equals the dose absorbed in cavity 
 * (dosimeter) material. Dosimeters should be used in exposure conditions closed to 
 * calibration ones (closed enough to perform some 'light' corrections such as 
 * pressure, temperature, HVL).<br>

 * Regardless of calibration condition, the dosimeter reading is always proportional 
 * with dose absorbed in dosimeter active volume. When using dosimeter in exposure 
 * condition far from calibration condition, such as close to interface of media, 
 * we must compute corection factors using MC tehniques for absorbed dose in cavity 
 * and surrounding region (esspecially when we cannot apply cavity theory). This class 
 * is designed to compute cavity(dosimeter)/interface correction for dosimeter reading 
 * when we want dose in medium (not in cavity/dosimeter). It calls an external C++ 
 * program which runs GEANT4 Monte-Carlo engine.<br>
 * 
 * @author Dan Fulea, 12 May. 2015
 */
@SuppressWarnings("serial")
public class InterfaceFrame extends JFrame implements ActionListener, Runnable{

private volatile Thread simTh;	
	
	private static String filenameMC = "MonteCarloPath.txt";
	private static String doseExeName= "Interface.exe";//"DHPRE.exe";
	private static String detExeNameLinux = "Interface";
	protected static String doseExeFileName= "Interface";
	protected String doseFolderURL;
	protected String macroFilename;
	protected File macroFile;
	
	private static final Dimension PREFERRED_SIZE = new Dimension(950, 700);
	private static final Dimension sizeCb = new Dimension(100, 21);
	private static final Dimension sizeCb2 = new Dimension(150, 21);
	private static final Dimension textAreaDimension = new Dimension(950, 100);
	private static final String BASE_RESOURCE_CLASS = "radQC.resources.RadQCFrameResources";
	protected ResourceBundle resources;
	private RadQCFrame mf;
	
	protected JCheckBox graphicSceneAutoRefreshCh;
	protected JCheckBox spectrumCh;
	
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private String command = null;
	
	@SuppressWarnings("rawtypes")
	private JComboBox media1Cb, kvCb, anodeAngleCb, rippleCb, media2Cb;//media 1: Water; media 2: chamber media: Air!!
	private JTextField filtrationTf = new JTextField(5);
	private JTextField energyTf = new JTextField(5);
	private JTextField chamberThicknessTf = new JTextField(5);//box approximation
	protected JTextArea textArea = new JTextArea();
	private JTextField runsTf = new JTextField(5);
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public InterfaceFrame(RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("InterfaceFrame.NAME"));
		
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
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		textArea.setText(resources.getString("interf.INFO"));//("");
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
		spectrumCh=
			new JCheckBox(resources.getString("spectrum.ch"),true);
		
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		filtrationTf.setText("2.5");
		chamberThicknessTf.setText("2.0");
		runsTf.setText("1000");
		energyTf.setText("0.662");
        String[] media1Items = (String[])resources.getObject("interf.media1.cb");
		media1Cb=new JComboBox(media1Items);
		media1Cb.setMaximumRowCount(5);
		media1Cb.setPreferredSize(sizeCb2);
		media1Cb.setSelectedItem((String)"G4_PLEXIGLASS");
		
		String[] media2Items = (String[])resources.getObject("interf.cavity.cb");
		media2Cb=new JComboBox(media2Items);
		media2Cb.setMaximumRowCount(5);
		media2Cb.setPreferredSize(sizeCb2);
		media2Cb.setSelectedItem((String)"Air");
		
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
		kvCb.setSelectedItem((String)"80");
		
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
			
		//Phantom selection
		JPanel mediaP=new JPanel();
		mediaP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("interf.media1.label"));
        mediaP.add(label);
        mediaP.add(media1Cb); 
        label=new JLabel(resources.getString("interf.media2.label"));
        mediaP.add(label);
        mediaP.add(media2Cb); 
        mediaP.setBackground(RadQCFrame.bkgColor);
        
        JPanel cavP=new JPanel();
        cavP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("interf.cavity.label"));
        cavP.add(label);
        cavP.add(chamberThicknessTf);         
        cavP.setBackground(RadQCFrame.bkgColor);
        
        JPanel enP=new JPanel();
        enP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        enP.add(spectrumCh);
        label=new JLabel(resources.getString("interf.energy.label"));
        enP.add(label);
        enP.add(energyTf);         
        enP.setBackground(RadQCFrame.bkgColor);
        //-------------
        JPanel phantomP = new JPanel();
		BoxLayout blphantomP = new BoxLayout(phantomP, BoxLayout.Y_AXIS);
		phantomP.setLayout(blphantomP);				
		phantomP.add(mediaP);
		phantomP.add(cavP);
		phantomP.add(enP);
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
     
        //-----------
        kvP.add(label);
        kvP.add(anodeAngleCb);
        //----------------
     
        label=new JLabel(resources.getString("ripple.label"));
     
        //-------------
        kvP.add(label);
        kvP.add(rippleCb);
                
        JPanel filtrationP=new JPanel();
        filtrationP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("filtration.label"));
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
		//tubeP.add(anodeAngleP);	
		//tubeP.add(rippleP);	
		//tubeP.add(mAsP);	
		tubeP.setBackground(RadQCFrame.bkgColor);
		//------------------------------
		        
		JPanel runsP=new JPanel();
        runsP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("runs.label"));
        runsP.add(label);
        runsP.add(runsTf);runsP.add(graphicSceneAutoRefreshCh);
		buttonName = resources.getString("calcB");
		buttonToolTip = resources.getString("calcB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		runsP.add(button);			
		runsP.setBackground(RadQCFrame.bkgColor);
		
		JPanel northP = new JPanel();
		BoxLayout blnorthP = new BoxLayout(northP, BoxLayout.Y_AXIS);
		northP.setLayout(blnorthP);
		northP.add(phantomP);
		northP.add(tubeP);			
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
	private void compute(){
		textArea.selectAll();
		textArea.replaceSelection("");
		//startMC();
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
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		command = e.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {			
			compute();
		}
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
		str = str + "/N03/gun/rndm on" + "\n";
		String media1 = (String)media1Cb.getSelectedItem();
		str = str + "/N03/det/setAbsMat" + " " + media1 + "\n";
		
		String media2 = (String)media2Cb.getSelectedItem();
		str = str + "/N03/det/setGapMat" + " " + media2 + "\n";
		
		String thick = chamberThicknessTf.getText();
		str = str + "/N03/det/setAbsThick" + " " + thick+" cm" + "\n";
		str = str + "/N03/det/setGapThick" + " " + thick+" cm" + "\n";
		str = str + "/N03/det/setNbOfLayers 10" + "\n";
		
		//test:
		double ddd=0.0;
		try
	    {
			ddd=Convertor.stringToDouble(thick);
			if(ddd<0.0){
			   String title =resources.getString("dialog.number.title");
			   String message =resources.getString("dialog.cavity.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return false;
			}
		    
		}
		catch(Exception e)
		{
		    String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.cavity.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		str = str + "/N03/det/setSizeYZ"+" "+ddd*10+" cm" + "\n";
				
		str = str + "/run/initialize" + "\n";
				
		if (graphicSceneAutoRefreshCh.isSelected()){//changed in vis-novis!!
			str=str+"/control/execute visInterf.mac"+"\n";					
			str=str+"/vis/viewer/set/autoRefresh false"+"\n";
		}
		//===================================================================
		String spectrum = "yes";//request.getParameter("spectrum");
		if (!spectrumCh.isSelected())
			spectrum = "no";
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
		
		
		//str=str+"/event/printModulo 1000"+"\n";//internal default
				
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
		
		
					
		String particleType = "gamma";//request.getParameter("particleType");
		str=str+"/gun/particle"+" "+particleType+"\n";
		
		String particleKineticEnergy = energyTf.getText();		
		try{
			if (Convertor.stringToDouble(particleKineticEnergy)<=0){
				String title =resources.getString("dialog.number.title");
			    String message =resources.getString("dialog.energy.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (Exception e){
			e.printStackTrace();
			String title =resources.getString("dialog.number.title");
		    String message =resources.getString("dialog.energy.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//String particleKineticEnergy = "0.662";//request.getParameter("incidentKineticEnergy");
		str=str+"/gun/energy"+" "+particleKineticEnergy+" MeV"+"\n";
		//---------------
		str=str+"/run/beamOn"+" "+nRuns+"\n";
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
				String str = " Dose_in_medium = Dosimeter_Reading";
				int len = str.length();
				int newLineLen=newLine.length();
				if (newLineLen>len)
				{
					String cstr=newLine.substring(0, len);
					if (str.equals(cstr)){
						//exit if on LINUX
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
}
