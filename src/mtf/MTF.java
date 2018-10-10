package mtf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.pdfbox.pdmodel.PDDocument;

//import radQC.RadQCFrame;

import danfulea.utils.ExampleFileFilter;
import danfulea.utils.ScanDiskLFGui;
import danfulea.math.Convertor;
import danfulea.math.Sort;

import danfulea.utils.FrameUtilities;
import danfulea.utils.ListUtilities;
import danfulea.utils.PDFRenderer;

/**
 * Computes the MTF (Modular Transfer Function) based on pixel profile of a tester (pixel profile can be 
 * acquired by other software such as imageJ). <br>
 * 
 * 
 * @author Dan Fulea, 18 Jun. 2013
 * 
 */

public class MTF extends JFrame implements ActionListener, ChangeListener//, Runnable
{
	//private RadQCFrame mf;
	private Window parent = null;
	private boolean hasParent=false; 

	private static final long serialVersionUID = 1L;
	private final Dimension PREFERRED_SIZE = new Dimension(1000, 800);
	private static final Dimension textAreaDimension = new Dimension(800, 50);
	private static final Dimension sizeCb = new Dimension(10, 21);
	
	private static final String BASE_RESOURCE_CLASS = "mtf.resources.MTFResources";
	protected ResourceBundle resources;
	
	public static Color bkgColor = new Color(230, 255, 210, 255);// Linux mint green
	public static Color foreColor = Color.black;// Color.white;
	public static Color textAreaBkgColor = Color.white;// Color.black;
	public static Color textAreaForeColor = Color.black;// Color.yellow;
	public static boolean showLAF = true;

    private static final Dimension sizeCh = new Dimension(120, 21);
    private JLabel statusL = new JLabel("Waiting...");
    protected String outFilename = null;
    
	protected JButton openB;//=new JButton();
    protected JButton calcB;//=new JButton();
    protected JRadioButton sineRb;
    protected JRadioButton slitRb;
    protected JRadioButton bargroupRb;
    protected JRadioButton linScaleRb;
    protected JRadioButton logScaleRb;
    protected JTextField fminTf=new JTextField(5);
    protected JTextField fmaxTf=new JTextField(5);
    protected JTextField whiteTf=new JTextField(5);
    protected JTextField greyThresholdTf=new JTextField(5);
    protected JTextField contrastThresholdPercentTf=new JTextField(5);
    protected JCheckBox sineCh=new JCheckBox("Sin. tester",true);//new JCheckBox("Sine pattern",true);
    protected JCheckBox sine2Ch=new JCheckBox("Sin. tester",true);
    @SuppressWarnings("rawtypes")
	protected JComboBox smoothCb;
    @SuppressWarnings("rawtypes")
	protected JComboBox smooth2Cb;
    protected JTextField mtfhalfTf=new JTextField(5);
    protected JTextField mtftenthTf=new JTextField(5);
    protected JTextField dimageTf=new JTextField(5);
    protected JTextField drealTf=new JTextField(5);
    protected JTextArea simTa= new JTextArea();

    protected JTextField fTf=new JTextField(5);
    protected JTextField nCyclesTf=new JTextField(5);
    protected JTextField wwhiteTf=new JTextField(5);
    protected JButton addB;//=new JButton();
    protected JButton delB;//=new JButton();
    protected JButton printB;
    JPanel dd1P=new JPanel();
    JPanel sineContP=new JPanel();

	@SuppressWarnings("rawtypes")
	protected DefaultListModel dlm=new DefaultListModel() ;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected JList dataL = new JList(dlm);
	private JScrollPane listSp = new JScrollPane();
	private static final Dimension sizeLst = new Dimension(350,150); 
    //===============================================================================
	double [] yneted;//smoothing array-internal
	int ndat;//array length-internal
	int nneted=1;//5;//100;//25;//10000;//100;//=ASSYER=min 5 max 25
	double greyThreshold=20.0;//gray threshold lvl=>8% of 255 (white)~20!!!
	double contrastThresholdPercent=2.0;//2 % percent 100*(Max-Min)/(Max+Min)>2!!
	double dimage=0.0;
	double dreal=0.0;
	double ppmm=0.0;//pixels per mm
	double magnification=0.0;
	int nPoints=0;
	double[] pixelValue=new double[10028];//maximum
	int npixels=0;
	
	@SuppressWarnings("rawtypes")
	Vector fvector=new Vector();
	@SuppressWarnings("rawtypes")
	Vector mtfvector=new Vector();

	private static final String EXIT_COMMAND = "EXIT";
	private static final String ABOUT_COMMAND = "ABOUT";
	private static final String PRINT_COMMAND = "PRINT";
	private static final String LOOKANDFEEL_COMMAND = "LOOKANDFEEL";
	private static final String RUN_COMMAND = "RUN";
	
	private static final String ADD_COMMAND = "ADD";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String LOAD_COMMAND = "LOAD";
	
	/**
	 * Constructor
	 * @param frame the calling program
	 */
	public MTF(Window frame){
		this();
		this.parent = frame;
		hasParent=true;
	}
	
	/**
	 * Standalone constructor
	 */
	public MTF(){
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(this.resources.getString("Application.NAME"));//("Application.name"));

		// the key to force attemptExit() method on close!!
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});

		JMenuBar menuBar = createMenuBar(resources);
		setJMenuBar(menuBar);

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
	 * Program close
	 */
	private void attemptExit() {
		//String title = resources.getString("dialog.exit.title");
		//String message = resources.getString("dialog.exit.message");

		//Object[] options = (Object[]) resources
		//		.getObject("dialog.exit.buttons");
		//int result = JOptionPane.showOptionDialog(this, message, title,
		//		JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
		//		options, options[0]);
		//if (result == JOptionPane.YES_OPTION) {
		//	dispose();
		//	System.exit(0);
		//}
		
		if (!hasParent) {
			dispose();
			System.exit(0);
		} else {
			parent.setVisible(true);
			dispose();
		}
	}
	
	/**
	 * GUI creation.
	 */
	private void createGUI() {
		JPanel content = new JPanel(new BorderLayout());
		JPanel mainPanel = createMainPanel();
		content.add(mainPanel);
		// Create the statusbar.
		JToolBar statusBar = new JToolBar();
		statusBar.setFloatable(false);
		initStatusBar(statusBar);
		content.add(statusBar, BorderLayout.PAGE_END);

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
	}
	
	/**
	 * Creates the frame main panel.
	 * 
	 * @return the main Panel
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JPanel createMainPanel() {
		simTa.setCaretPosition(0);
	    simTa.setEditable(false);
	    simTa.setText("Results:");
	    simTa.setLineWrap(true);
	    simTa.setWrapStyleWord(true);
	    simTa.setToolTipText(" Select text then press CTRL+C to copy!");

		mtfhalfTf.setEditable(false);
		mtftenthTf.setEditable(false);
		String[] sCstm = {
			"0","1","2","3","4","5","6","7","8","9","10",
			"11","12","13","14","15","16","17","18","19","20",
			"21","22","23","24","25","26","27","28","29","30"
		};
		smoothCb=new JComboBox(sCstm);
		smooth2Cb=new JComboBox(sCstm);
		String str=sCstm[1];
	    smoothCb.setSelectedItem((Object)str);
		smoothCb.setMaximumRowCount(5);
		smoothCb.setPreferredSize(sizeCb);
		smoothCb.setToolTipText("High order smoothness can alter significantly raw data!");
	    smooth2Cb.setSelectedItem((Object)str);
		smooth2Cb.setMaximumRowCount(5);
		smooth2Cb.setPreferredSize(sizeCb);
		smooth2Cb.setToolTipText("High order smoothness can alter significantly raw data!");
		//---------
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		buttonName = resources.getString("runB");
		buttonToolTip = resources.getString("runB.toolTip");
		buttonIconName = resources.getString("img.set");
		calcB = FrameUtilities.makeButton(buttonIconName, RUN_COMMAND,
				buttonToolTip, buttonName, this, this);
		Character mnemonic = (Character) resources.getObject("runB.mnemonic");
		calcB.setMnemonic(mnemonic.charValue());
		
		buttonName = resources.getString("openB");
		buttonToolTip = resources.getString("openB.toolTip");
		buttonIconName = resources.getString("img.open.file");
		openB = FrameUtilities.makeButton(buttonIconName, LOAD_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("openB.mnemonic");
		openB.setMnemonic(mnemonic.charValue());

		buttonName = resources.getString("printB");
		buttonToolTip = resources.getString("printB.toolTip");
		buttonIconName = resources.getString("img.report");
		printB = FrameUtilities.makeButton(buttonIconName, PRINT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("printB.mnemonic");
		printB.setMnemonic(mnemonic.charValue());
		
		buttonName = resources.getString("addB");
		buttonToolTip = resources.getString("addB.toolTip");
		buttonIconName = resources.getString("img.insert");
		addB = FrameUtilities.makeButton(buttonIconName, ADD_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("addB.mnemonic");
		addB.setMnemonic(mnemonic.charValue());

		buttonName = resources.getString("deleteB");
		buttonToolTip = resources.getString("deleteB.toolTip");
		buttonIconName = resources.getString("img.delete");
		delB = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("deleteB.mnemonic");
		delB.setMnemonic(mnemonic.charValue());
		
    	//===========================
		sineRb=new JRadioButton("Continuous sine/bar pattern");
    	slitRb=new JRadioButton("Slit pattern");
    	bargroupRb=new JRadioButton("Group sine/bar  pattern");
    	linScaleRb=new JRadioButton("Lin. scale");
    	logScaleRb=new JRadioButton("Log. scale");    	
    	sineRb.addActionListener(this);
   	    bargroupRb.addActionListener(this);
   	  
		ButtonGroup group = new ButtonGroup();
		group.add(sineRb);
		group.add(slitRb);
		group.add(bargroupRb);
		sineRb.setSelected(true);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(logScaleRb);
		group1.add(linScaleRb);
		logScaleRb.setSelected(true);

	    JPanel buttP = new JPanel();
		buttP.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		buttP.add(sineRb);
		buttP.add(slitRb);
		buttP.add(bargroupRb);
		sineRb.setBackground(bkgColor);
		slitRb.setBackground(bkgColor);
		bargroupRb.setBackground(bkgColor);
		buttP.setBackground(bkgColor);

		JPanel scP=new JPanel();
		scP.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		scP.add(openB);
		JLabel lbel=new JLabel("Tester image distance [mm]: ");
		scP.add(lbel);
		scP.add(dimageTf);
		dimageTf.setText("195.0");
		lbel=new JLabel("Tester real distance [mm]: ");
		scP.add(lbel);
		scP.add(drealTf);
		drealTf.setText("5.0");
		scP.setBackground(bkgColor);

//-----------------------------------------------------------------------------
		JPanel buttP1=new JPanel();
		BoxLayout bl1 = new BoxLayout(buttP1,BoxLayout.Y_AXIS);
		buttP1.setLayout(bl1);
		buttP1.add(logScaleRb);
		buttP1.add(linScaleRb);
		logScaleRb.setBackground(bkgColor);
		linScaleRb.setBackground(bkgColor);
		buttP1.setBackground(bkgColor);

		JPanel fP1=new JPanel();
		fP1.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		JLabel label=new JLabel("Tester minimum frequency [lp/mm]: ");
		fP1.add(label);
		fP1.add(fminTf);
		fminTf.setText("2.0");
		fminTf.setToolTipText("As Given in the tester (patterb) description. Related to tester real dimensions!");
		fP1.setBackground(bkgColor);

		JPanel fP11=new JPanel();
		fP11.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		label=new JLabel("Tester maximum frequency [lp/mm]: ");
		fP11.add(label);
		fP11.add(fmaxTf);
		fmaxTf.setText("200.0");
		fmaxTf.setToolTipText("As given in the tester (pattern) description. Related to tester real dimensions!");		
		fP11.setBackground(bkgColor);

		JPanel fPv=new JPanel();
		BoxLayout bl2 = new BoxLayout(fPv,BoxLayout.Y_AXIS);
		fPv.setLayout(bl2);
		fPv.add(fP1);
		fPv.add(fP11);
		fPv.setBackground(bkgColor);

		JPanel fPv0=new JPanel();
		BoxLayout bl4 = new BoxLayout(fPv0,BoxLayout.Y_AXIS);
		fPv0.setLayout(bl4);
		fPv0.add(sineCh);
		sineCh.setPreferredSize(sizeCh);
		label=new JLabel("Smooth level:");
		fPv0.add(label);
		fPv0.add(smoothCb);
		sineCh.setBackground(bkgColor);
		fPv0.setBackground(bkgColor);

		JPanel fP21=new JPanel();
		fP21.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		label=new JLabel("Maximum level  [White lvl. ]: ");
		fP21.add(label);
		fP21.add(whiteTf);
		whiteTf.setText("255.0");
		fP21.setBackground(bkgColor);

		JPanel fP22=new JPanel();
		fP22.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		label=new JLabel("Grey threshold [% from Maximum lvl.]: ");
		fP22.add(label);
		fP22.add(greyThresholdTf);
		greyThresholdTf.setText("8.0");
		fP22.setBackground(bkgColor);

		JPanel fP23=new JPanel();
		fP23.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		label=new JLabel("Contrast threshold [%]: ");
		fP23.add(label);
		fP23.add(contrastThresholdPercentTf);
		contrastThresholdPercentTf.setText("2.0");
		fP23.setBackground(bkgColor);

		JPanel fPv1=new JPanel();
		BoxLayout bl3 = new BoxLayout(fPv1,BoxLayout.Y_AXIS);
		fPv1.setLayout(bl3);
		fPv1.add(fP21);
		fPv1.add(fP22);
		fPv1.add(fP23);
		fPv1.setBackground(bkgColor);

		sineContP.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
		sineContP.add(fPv0);
		sineContP.add(fPv);
		sineContP.add(buttP1);
		sineContP.add(fPv1);
		sineContP.setBackground(bkgColor);

		sineContP.setBorder(FrameUtilities.getGroupBoxBorder(
				"Continuum tester data", foreColor));
//---------------------------------------------------------
		JPanel listP= new JPanel(new BorderLayout());
		listP.setBackground(bkgColor);
		listSp.getViewport().add(dataL, null);
	    listSp.setPreferredSize(sizeLst);
		listP.add(listSp, BorderLayout.CENTER);

		JPanel g1P=new JPanel();
		BoxLayout bl31 = new BoxLayout(g1P,BoxLayout.Y_AXIS);
		g1P.setLayout(bl31);
		g1P.add(sine2Ch);
		sine2Ch.setPreferredSize(sizeCh);
		label=new JLabel("Smooth data:");
		g1P.add(label);
		g1P.add(smooth2Cb);
		g1P.setBackground(bkgColor);
		sine2Ch.setBackground(bkgColor);
		sine2Ch.addChangeListener(this);

	    JPanel g2P=new JPanel();
	    g2P.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
	    label=new JLabel("Current group frequency [cy/mm]:");	    
	    g2P.add(label);
	    g2P.add(fTf);fTf.setText("2");
	    g2P.setBackground(bkgColor);
	    JPanel g21P=new JPanel();
	    g21P.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
	    label=new JLabel("Number of cycles (measured):");	    
	    g21P.add(label);
	    g21P.add(nCyclesTf);nCyclesTf.setText("5");
	    g21P.setBackground(bkgColor);
	    JPanel g22P=new JPanel();
	    g22P.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
	    label=new JLabel("White [max] fraction per cycle:");	    
	    g22P.add(label);
	    g22P.add(wwhiteTf);wwhiteTf.setText("0.5");
	    wwhiteTf.setToolTipText("e.g. if white is 1/2 from total white/black for one cycle (period)=>0.5");	    
	    g22P.setBackground(bkgColor);
	    wwhiteTf.setEnabled(false);
	    nCyclesTf.setEnabled(false);
	    JPanel g23P=new JPanel();
	    g23P.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
	    g23P.add(addB);
	    g23P.add(delB);
	    g23P.setBackground(bkgColor);

		JPanel g11P=new JPanel();
		BoxLayout bl32 = new BoxLayout(g11P,BoxLayout.Y_AXIS);
		g11P.setLayout(bl32);
		g11P.add(g2P);
		g11P.add(g21P);
		g11P.add(g22P);
		g11P.add(g23P);
		g11P.setBackground(bkgColor);

	    dd1P.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
	    dd1P.add(g1P);
	    dd1P.add(g11P);
	    dd1P.add(listP);
	    dd1P.setBackground(bkgColor);
	    dd1P.setBorder(FrameUtilities.getGroupBoxBorder(
				"Group tester data", foreColor));
//-----------------------------------------------------------
	    JPanel d1P=new JPanel();
	    d1P.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
	    d1P.add(calcB);
	    d1P.add(printB);
	    calcB.setToolTipText("MTF computation; For accurate results, input data must be inserted from low to high frequencies!");
	    d1P.setBackground(bkgColor);

	    JPanel d2P=new JPanel();
	    d2P.setLayout(new FlowLayout(FlowLayout.CENTER, 12,12));
	    label=new JLabel("Frequency for 1/2MTF [cy/mm]:");
	    d2P.add(label);
	    d2P.add(mtfhalfTf);
	    label=new JLabel("Frequency for 1/10MTF [cy/mm]:");
	    d2P.add(label);
	    d2P.add(mtftenthTf);
	    d2P.setBackground(bkgColor);

		JPanel dsPan=new JPanel();
		BoxLayout bl0 = new BoxLayout(dsPan,BoxLayout.Y_AXIS);
		dsPan.setLayout(bl0);
		dsPan.add(scP);
		dsPan.add(buttP);
		dsPan.add(sineContP);
		dsPan.add(dd1P);
		dsPan.add(d1P);
		dsPan.setBackground(bkgColor);

	    JPanel resultP=new JPanel(new BorderLayout());
	    JScrollPane jspres=new JScrollPane();
	    jspres.getViewport().add(simTa, null);
	    resultP.add(jspres,  BorderLayout.CENTER);
	    resultP.setBackground(bkgColor);
	    resultP.setPreferredSize(textAreaDimension);

    	linScaleRb.setEnabled(true);
    	logScaleRb.setEnabled(true);
    	fminTf.setEnabled(true);
    	fmaxTf.setEnabled(true);
    	whiteTf.setEnabled(true);
    	greyThresholdTf.setEnabled(true);
    	contrastThresholdPercentTf.setEnabled(true);
    	sineCh.setEnabled(true);
    	smoothCb.setEnabled(true);

    	smooth2Cb.setEnabled(false);
		sine2Ch.setEnabled(false);
		dataL.setEnabled(false);
		fTf.setEnabled(false);
		nCyclesTf.setEnabled(false);
		wwhiteTf.setEnabled(false);
		addB.setEnabled(false);
		delB.setEnabled(false);
			    
		JPanel mainP = new JPanel(new BorderLayout());
		mainP.add(dsPan, BorderLayout.NORTH);
		mainP.add(resultP, BorderLayout.CENTER);
		mainP.setBackground(bkgColor);
		return mainP;
	}
	
	/**
	 * Setting up the menu bar.
	 * 
	 * @param resources resources
	 * @return the menu bar
	 */
	private JMenuBar createMenuBar(ResourceBundle resources) {
		ImageIcon img;
		String imageName = "";
		// create the menus
		JMenuBar menuBar = new JMenuBar();

		String label;
		Character mnemonic;

		// first the file menu
		label = resources.getString("menu.file");
		mnemonic = (Character) resources.getObject("menu.file.mnemonic");
		JMenu fileMenu = new JMenu(label, true);
		fileMenu.setMnemonic(mnemonic.charValue());

		imageName = resources.getString("img.set");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("runB");
		mnemonic = (Character) resources.getObject("runB.mnemonic");
		JMenuItem runItem = new JMenuItem(label, mnemonic.charValue());
		runItem.setActionCommand(RUN_COMMAND);
		runItem.setIcon(img);
		runItem.addActionListener(this);
		fileMenu.add(runItem);
		
		imageName = resources.getString("img.report");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("printB");
		mnemonic = (Character) resources.getObject("printB.mnemonic");
		JMenuItem printItem = new JMenuItem(label, mnemonic.charValue());
		printItem.setActionCommand(PRINT_COMMAND);
		printItem.setIcon(img);
		printItem.addActionListener(this);
		fileMenu.add(printItem);
		fileMenu.addSeparator();
		
		imageName = resources.getString("img.close");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.exit");
		mnemonic = (Character) resources.getObject("menu.file.exit.mnemonic");
		JMenuItem exitItem = new JMenuItem(label, mnemonic.charValue());
		exitItem.setActionCommand(EXIT_COMMAND);
		exitItem.setIcon(img);
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);

		// then the help menu
		label = resources.getString("menu.help");
		mnemonic = (Character) resources.getObject("menu.help.mnemonic");
		JMenu helpMenu = new JMenu(label);
		helpMenu.setMnemonic(mnemonic.charValue());

		label = resources.getString("menu.help.LF");
		mnemonic = (Character) resources.getObject("menu.help.LF.mnemonic");
		JMenuItem lfItem = new JMenuItem(label, mnemonic.charValue());
		lfItem.setActionCommand(LOOKANDFEEL_COMMAND);
		lfItem.addActionListener(this);
		lfItem.setToolTipText(resources.getString("menu.help.LF.toolTip"));

		if (showLAF) {
			helpMenu.add(lfItem);
			helpMenu.addSeparator();
		}
		
		imageName = resources.getString("img.about");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.help.about");
		mnemonic = (Character) resources.getObject("menu.help.about.mnemonic");
		JMenuItem aboutItem = new JMenuItem(label, mnemonic.charValue());
		aboutItem.setActionCommand(ABOUT_COMMAND);
		aboutItem.setIcon(img);
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);

		// finally, glue together the menu and return it
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	/**
	 * Setting up the status bar.
	 * 
	 * @param toolBar toolBar
	 */
	private void initStatusBar(JToolBar toolBar) {
		JPanel toolP = new JPanel();
		toolP.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

		toolP.add(statusL);
		toolBar.add(toolP);
		statusL.setText(resources.getString("status.wait"));
	}
	/**
	 * Setting up actions!
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String command = arg0.getActionCommand();
		if (command.equals(ABOUT_COMMAND)) {
			about();
		} else if (command.equals(EXIT_COMMAND)) {
			attemptExit();
		} else if (command.equals(LOOKANDFEEL_COMMAND)) {
			lookAndFeel();
		} else if (command.equals(RUN_COMMAND)) {
			computeMTF();
		} else if (command.equals(PRINT_COMMAND)) {
			printReport();
		} else if (command.equals(ADD_COMMAND)) {
			addInList();
		} else if (command.equals(DELETE_COMMAND)) {
			delFromList();
		} else if (command.equals(LOAD_COMMAND)) {
			loadPixels();
		}
		
		if (arg0.getSource()==sineRb)
		{
			linScaleRb.setEnabled(true);
			logScaleRb.setEnabled(true);
			fminTf.setEnabled(true);
			fmaxTf.setEnabled(true);
			whiteTf.setEnabled(true);
			greyThresholdTf.setEnabled(true);
			contrastThresholdPercentTf.setEnabled(true);
			sineCh.setEnabled(true);
			smoothCb.setEnabled(true);

			smooth2Cb.setEnabled(false);
			sine2Ch.setEnabled(false);
			dataL.setEnabled(false);
		    fTf.setEnabled(false);
		    nCyclesTf.setEnabled(false);
		    wwhiteTf.setEnabled(false);
		    addB.setEnabled(false);
		    delB.setEnabled(false);
		}
		if (arg0.getSource()==bargroupRb)
		{
			linScaleRb.setEnabled(false);
			logScaleRb.setEnabled(false);
			fminTf.setEnabled(false);
			fmaxTf.setEnabled(false);
			whiteTf.setEnabled(false);
			greyThresholdTf.setEnabled(false);
			contrastThresholdPercentTf.setEnabled(false);
			sineCh.setEnabled(false);
			smoothCb.setEnabled(false);

			smooth2Cb.setEnabled(true);
			sine2Ch.setEnabled(true);
			dataL.setEnabled(true);
		    fTf.setEnabled(true);
		    if (sine2Ch.isSelected())
		    {
			    nCyclesTf.setEnabled(false);
			    wwhiteTf.setEnabled(false);
			}
			else
			{
			    nCyclesTf.setEnabled(true);
			    wwhiteTf.setEnabled(true);
			}
		    addB.setEnabled(true);
		    delB.setEnabled(true);
		}

		
	}
	
	/**
	 * JCheckbox actions are set here
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource()==sine2Ch)
		{
			enableds();
		}
	}
	
	/**
	 * Printing report
	 */
	private void printReport() {
		String FILESEPARATOR = System.getProperty("file.separator");
		String currentDir = System.getProperty("user.dir");
		File infile = null;

		String ext = resources.getString("file.extension");
		String pct = ".";
		String description = resources.getString("file.description");
		ExampleFileFilter eff = new ExampleFileFilter(ext, description);

		String myDir = currentDir + FILESEPARATOR;//
		// File select
		JFileChooser chooser = new JFileChooser(myDir);
		chooser.addChoosableFileFilter(eff);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = chooser.showSaveDialog(this);// parent=this frame
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			infile = chooser.getSelectedFile();
			outFilename = chooser.getSelectedFile().toString();

			int fl = outFilename.length();
			String test = outFilename.substring(fl - 4);// exstension lookup!!
			String ctest = pct + ext;
			if (test.compareTo(ctest) != 0)
				outFilename = chooser.getSelectedFile().toString() + pct + ext;

			if (infile.exists()) {
				String title = resources.getString("dialog.overwrite.title");
				String message = resources
						.getString("dialog.overwrite.message");

				Object[] options = (Object[]) resources
						.getObject("dialog.overwrite.buttons");
				int result = JOptionPane
						.showOptionDialog(this, message, title,
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				if (result != JOptionPane.YES_OPTION) {
					return;
				}

			}

			//new Report(this);
			performPrintReport();
			statusL.setText(resources.getString("status.save") + outFilename);
		} else {
			return;
		}
		// end File select		
	}
	
	/**
	 * Actual pdf renderer is called here. Called by printReport.
	 */
	public void performPrintReport(){
		PDDocument doc = new PDDocument();
		PDFRenderer renderer = new PDFRenderer(doc);
		try{
			renderer.setTitle(resources.getString("pdf.content.title"));
			renderer.setSubTitle(
					resources.getString("pdf.content.subtitle")+
					resources.getString("pdf.metadata.author")+ ", "+
							new Date());
						
			String str = " \n" + simTa.getText();
		
			//renderer.renderTextHavingNewLine(str);//works!!!!!!!!!!!!!!!!
			renderer.renderTextEnhanced(str);
			
			renderer.addPageNumber();
			renderer.close();		
			doc.save(new File(outFilename));
			doc.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Enable/Disable some controls
	 */
	private void enableds()
	{
		//==========
        fvector.removeAllElements();
        mtfvector.removeAllElements();
        ListUtilities.removeAll(dlm);
        nPoints=0;

		//========
		nCyclesTf.setEnabled(false);
		wwhiteTf.setEnabled(false);
		if (!sine2Ch.isSelected())
		{
			nCyclesTf.setEnabled(true);
			wwhiteTf.setEnabled(true);
		}
	}

	
	/**
	 * Shows the about window!
	 */
	private void about() {
		new AboutFrame(this);
	}
	
	/**
	 * Changing look and feel can be done done here. Also display some gadgets.
	 */
	private void lookAndFeel(){
		setVisible(false);
		new ScanDiskLFGui(this);
	}

	/**
	 * Load the pixel profile previously acquired by some specialized image software (e.g. imageJ)
	 */
	private void loadPixels()
	{
		String ext="txt";
		String pct=".";
		String description="TextFile";
		ExampleFileFilter jpgFilter =
			new ExampleFileFilter(ext, description);

	    String filename="";
	    String currentDir=System.getProperty("user.dir");
	    String file_sep=System.getProperty("file.separator");
	    String opens=currentDir+file_sep;
		JFileChooser chooser = new JFileChooser(opens);
		chooser.addChoosableFileFilter(jpgFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//-----------------
		char lineSep = '\n';
		int i =0;
		@SuppressWarnings("unused")
		int lnr =0;//line number
		StringBuffer desc=new StringBuffer();
		String line="";
		//--------------
		int returnVal = chooser.showOpenDialog(this);//parent=this frame
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			filename= chooser.getSelectedFile().toString();
			int fl=filename.length();
			String test=filename.substring(fl-4);//exstension lookup!!
			String ctest=pct+ext;
			String ctest1=pct+"TXT";
			if ((test.compareTo(ctest)!=0) && (test.compareTo(ctest1)!=0))
				  filename=chooser.getSelectedFile().toString()+pct+ext;

			boolean haveData=false;
			boolean xb=false;
			int ndata=0;
			int ndata2=0;
			double d=0.0;
			double d2=0.0;
			int idatarow=0;
			try
			{
				FileInputStream in = new FileInputStream(filename);
        	   	while ((i = in.read()) != -1)
        	   	{
					if (!Character.isWhitespace((char)i))
					{
			   			desc.append((char)i);
			   			haveData=true;
					}
					else
					{
						if (haveData)
						{
							haveData=false;
							lnr++;

							if (!xb)
							{
								line=desc.toString();
								//====nothing do with it=====
								idatarow++;//1
								ndata2++;
								d2=Convertor.stringToDouble(line);
								xb=true;
							}
							else
							{
								idatarow++;//2
								//data here
								xb=false;//next is x data
								line=desc.toString();
								ndata++;
								d=Convertor.stringToDouble(line);
							}
						}//have data
						desc=new StringBuffer();
						if ((char)i == lineSep)
						{

							if(idatarow==1)//single line
							{

								npixels=ndata2;
								pixelValue[ndata2-1]=d2;
								xb=false;


							}
							else if (idatarow==2)
							{
								npixels=ndata;
								pixelValue[ndata-1]=d;

							}
							//--------------------
							idatarow=0;
						}
					}
        	   	}
				in.close();
				String label = "Pixel data loaded: "+filename;
				statusL.setText(label);
			}
			catch (Exception e)
			{
				String label = "Error: Pixel data not loaded!";
				statusL.setText(label);
			}
		}


	}
	
	/**
	 * Add group tester data in list
	 */
	@SuppressWarnings("unchecked")
	private void addInList()
	{
		//========compute constants==========
		if (npixels==0)
		{
		    String title ="Error...";
		    String message ="Pixel profile data must be loaded first!";//resources.getString("dialog.double.message");
		    //String message ="Datele de profil pixeli trebuie mai intai incarcate!";//resources.getString("dialog.double.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}

        try
        {
		    dimage=Convertor.stringToDouble(dimageTf.getText());
		    dreal=Convertor.stringToDouble(drealTf.getText());
		}
		catch(Exception e)
		{
		    String title ="Error...";
		    String message ="Insert real numbers for image/real distance!";//resources.getString("dialog.double.message");
		    //String message ="Introduceti numere reale pentru distantele imagine/efective!";//resources.getString("dialog.double.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		if (dimage<=0 || dreal<=0)
		{
		    String title ="Error...";
		    String message ="Insert positive real numbers for image/real distance!";
		    //String message ="Introduceti numere pozitive pentru distantele imagine/efective!";
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		magnification=dimage/dreal;
		ppmm=npixels/(dimage);//pixels per mm
		//==============================================

		double fc=0.0;int kc=0;double wc=0.0;double mtf=0.0;
		if (sine2Ch.isSelected())
		{
	        try
	        {
			    fc=Convertor.stringToDouble(fTf.getText());
			}
			catch(Exception e)
			{
			    String title ="Error...";
			    String message ="Insert real numbers for frequency!";
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			    return;
			}
			if (fc<=0)
			{
			    String title ="Error...";
			    String message ="Insert positive real numbers for frequency!";
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return;
			}
			//=============================================
			int ndat=npixels;//xxx.length;
			if (ndat<1) return;

			double[] xxx=new double[ndat];
			double[] yyy=new double[ndat];
			for (int i=0; i<ndat;i++)
			{
				xxx[i]=i;
				yyy[i]=pixelValue[i];
			}
			double[] yneted=new double[ndat];//local!!
			//===============================SMOOTHING==========================================
			int nneted=smooth2Cb.getSelectedIndex();//0.1,...ok=======local!!
			if (nneted>0)
			{
				for(int j=1;j<=nneted;j++)
				{
					for (int i=1; i<=ndat; i++)
					{
						if(j==1)
						{
							//========1st smoothing
							yneted[i-1]=yyy[i-1];
							if((i>2)&&(i<ndat-1))
							{
								yneted[i]=yyy[i];
								yneted[i+1]=yyy[i+1];
							}
							//=======================
						}

						if((i>2)&&(i<ndat-1))
						{
							yneted[i-1]=(1.0/35.0)*(-3.0*yneted[i-3]+12.0*yneted[i-2]+17.0*yneted[i-1]+
							12.0*yneted[i]-3.0*yneted[i+1]);
						}
					}
				}
			}
			//no smooth
			if(nneted==0)
			{
				for (int i=0; i<ndat;i++)
				{
					yneted[i]=yyy[i];
				}
	    	}
			//==========end SMOOTHING=============================
			double cfmax=Sort.findValue(yneted,1);
			double cfmin=Sort.findValue(yneted,ndat);

			mtf=(cfmax-cfmin)/(cfmax+cfmin);

			NumberFormat nfe = NumberFormat.getInstance(Locale.US);
			nfe.setMinimumFractionDigits(2);//default e 2 oricum!!
			nfe.setMaximumFractionDigits(2);//default e 2 oricum!!
			nfe.setGroupingUsed(false);
			//============================================
			ListUtilities.add("image f[cy/mm]= "+fc/magnification+"; Nyquist: "+nfe.format(ppmm/2.0)+
			"; mtf= "+nfe.format(mtf),dlm);
			ListUtilities.select(nPoints,dataL);
			nPoints++;
		}
		else
		{
	        try
	        {
			    fc=Convertor.stringToDouble(fTf.getText());
			    kc=Convertor.stringToInt(nCyclesTf.getText());
			    wc=Convertor.stringToDouble(wwhiteTf.getText());
			}
			catch(Exception e)
			{
			    String title ="Error...";
			    String message ="Insert real numbers!";
			    //String message ="Introduceti numere reale!";
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			    return;
			}
			if (fc<=0 || kc<=0 || wc<=0)
			{
			    String title ="Error...";
			    String message ="Insert positive real numbers!";
			    //String message ="Introduceti numere reale pozitive!";
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				return;
			}
			//=============================================
			//=============================================
			int ndat=npixels;//xxx.length;
			if (ndat<1) return;

			double[] xxx=new double[ndat];
			double[] yyy=new double[ndat];
			for (int i=0; i<ndat;i++)
			{
				xxx[i]=i;
				yyy[i]=pixelValue[i];
			}
			double[] yneted=new double[ndat];//local!!
			//===============================SMOOTHING==========================================
			int nneted=smooth2Cb.getSelectedIndex();//0.1,...ok=======local!!
			if (nneted>0)
			{
				for(int j=1;j<=nneted;j++)//nneted x netezire
				{
					for (int i=1; i<=ndat; i++)
					{
						if(j==1)
						{
							//========1st smoothing
							yneted[i-1]=yyy[i-1];
							if((i>2)&&(i<ndat-1))
							{
								yneted[i]=yyy[i];
								yneted[i+1]=yyy[i+1];
							}
							//=======================
						}

						if((i>2)&&(i<ndat-1))//eg 3 and ndat-2
						{
							yneted[i-1]=(1.0/35.0)*(-3.0*yneted[i-3]+12.0*yneted[i-2]+17.0*yneted[i-1]+
							12.0*yneted[i]-3.0*yneted[i+1]);
						}
					}
				}
			}
			//no smooth
			if(nneted==0)
			{
				for (int i=0; i<ndat;i++)
				{
					yneted[i]=yyy[i];
				}
	    	}
			//==========end SMOOTHING=============================
			double cfmax=Sort.findValue(yneted,1);
			double cfmin=Sort.findValue(yneted,ndat);//last, length ndat!
			double da=Math.abs(cfmax-cfmin);
			
			//int ink=nHARMINICS*kc;
			//int iN=yneted.length;
			//if (ink > (iN/2))
				//ink = iN/2;
			//double[] dft=AbsDFT(yneted,ink);
			//double dft= dft[kc];//OK!!!! BOTH (imageJ and mine) methods are compatible with each other!

			double dft=AbsDFT(yneted, kc);
			
			//dBarSpacing=period=1/fc; kc=nCyclesToMeasure; magnification=dScale.
			//imageJ MTF plugin: dN= (int)(dBarSpacing*nCyclesToMeasure*dScale +0.5)//round up!
			//but the above int is in fact mm!! so to be a real integer must be multiplied to pixelpermm=>number of pixels
			//which is in fact number of data, which is correct see MTF derivation!!!!
			//double dN=magnification*kc*ppmm/fc;
			
			double dN=ndat;
			mtf=Math.abs(dft/(dN*wc*da*sinc(wc)));

			//============================================
			NumberFormat nfe = NumberFormat.getInstance(Locale.US);
			nfe.setMinimumFractionDigits(2);//default e 2 oricum!!
			nfe.setMaximumFractionDigits(2);//default e 2 oricum!!
			nfe.setGroupingUsed(false);

			ListUtilities.add("image f[cy/mm]= "+fc/magnification+"; Nyquist: "+nfe.format(ppmm/2.0)
			+"; nCycles= "+kc+"; Wwhite= "+wc+"; mtf= "+nfe.format(mtf),dlm);
			ListUtilities.select(nPoints,dataL);
			nPoints++;
		}
		String s1=Convertor.doubleToString(fc);
		String s2=Convertor.doubleToString(mtf);
		fvector.addElement((Object)s1);
		mtfvector.addElement((Object)s2);

		fTf.setText("");
		fTf.requestFocusInWindow();
	}

	/**
	 * Delete group tester data from list
	 */
	private void delFromList()
	{
		if (nPoints>0)
		{
			int index=ListUtilities.getSelectedIndex(dataL);
			nPoints--;
			ListUtilities.remove(index,dlm);
			ListUtilities.select(nPoints-1,dataL);

			fvector.removeElementAt(index);
			mtfvector.removeElementAt(index);
		}
	}

	/**
	 * Compute MTF
	 */
	private void computeMTF()
	{
		//========compute constants==========
		if (npixels==0)
		{
		    String title ="Error...";
		    String message ="Pixel profile data must be loaded first!";
		    //String message ="Datele de profil pixeli trebuie mai intai incarcate!";//resources.getString("dialog.double.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}

        try
        {
		    dimage=Convertor.stringToDouble(dimageTf.getText());
		    dreal=Convertor.stringToDouble(drealTf.getText());
		}
		catch(Exception e)
		{
		    String title ="Error...";
		    String message ="Insert real numbers for image/real distance!";//resources.getString("dialog.double.message");
		    //String message ="Introduceti numere reale pentru distantele imagine/efective!";//resources.getString("dialog.double.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		if (dimage<=0 || dreal<=0)
		{
		    String title ="Error...";
		    String message ="Insert positive real numbers for image/real distance!";
		    //String message ="Introduceti numere pozitive pentru distantele imagine/efective!";//resources.getString("dialog.double.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		magnification=dimage/dreal;
		ppmm=npixels/(dimage);
		//==============================================

		if (sineRb.isSelected())
		{
			computeSine();
		}
		else if (slitRb.isSelected())
		{
			//test();
			computeSlit();
		}
		else if (bargroupRb.isSelected())
		{
			computeGroup();
			//System.out.println("barg");
		}

	}

	/**
	 * Called by computeMTF. Handles group pattern tester
	 */
	private void computeGroup()
	{
		double[] x=convertVectorToDoubleArray(fvector);
		double[] y=convertVectorToDoubleArray(mtfvector);

		int nmtf=x.length;
		if (nmtf<2) return;
		Graph.interv2=nmtf;
		Graph.xarray2=new double[nmtf];
		Graph.yarray2=new double[nmtf];
		double[] mtfxx=new double[nmtf];
		double[] mtfyy=new double[nmtf];
		double[] mtftrunc=new double[nmtf];
		double[] mtfxxsort=new double[nmtf];

		for (int i=1; i<=nmtf;i++)
		{
			mtfxx[i-1]=x[i-1]/magnification;//IMPORTANT!!We work with images which are scaled!!
			mtfyy[i-1]=y[i-1]/y[0];//norm@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

			mtftrunc[i-1]=mtfyy[i-1];
			mtfxxsort[i-1]=mtfxx[i-1];

			Graph.xarray2[i-1]=mtfxx[i-1];
			Graph.yarray2[i-1]=mtfyy[i-1];
		}
		//==========================
		double mtfhalf=0.0;double mtftenth=0.0;double mtf_2=0.0;
		double fhalf=0.0;double ftenth=0.0;double f_2=0.0;
		int ifmin=0;int ifmax=0;
		mtfhalf=mtfyy[0]/2.0;mtftenth=mtfyy[0]/10.0;mtf_2=mtfyy[0]/50.0;

		Sort.qSort2(mtftrunc,mtfxxsort);
		//=================================================================================================
		Sort.findNearestValue(mtftrunc, mtfhalf, true);ifmin=Sort.getNearestPosition();
       	if (ifmin<mtftrunc.length-1)
           ifmax=ifmin+1;
        else
        {
			ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			fhalf=mtfxxsort[0];//first means last=>crescator
		else
			fhalf=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtfhalf);

		Sort.findNearestValue(mtftrunc, mtftenth, true);ifmin=Sort.getNearestPosition();
	   	if (ifmin<mtftrunc.length-1)
	       ifmax=ifmin+1;
	    else
	    {
			ifmin=mtftrunc.length-1;
	       ifmax=ifmin;
	       ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			ftenth=mtfxxsort[0];//first means last=>crescator
		else
			ftenth=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtftenth);

		Sort.findNearestValue(mtftrunc, mtf_2, true);ifmin=Sort.getNearestPosition();
       	if (ifmin<mtftrunc.length-1)
           ifmax=ifmin+1;
        else
        {
			ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			f_2=mtfxxsort[0];//first means last=>crescator
		else
			f_2=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtf_2);
		//====================================================================================================
		NumberFormat nfe = NumberFormat.getInstance(Locale.US);
		nfe.setMinimumFractionDigits(2);//default e 2 oricum!!
		nfe.setMaximumFractionDigits(2);//default e 2 oricum!!
		nfe.setGroupingUsed(false);
		
        simTa.selectAll();
        simTa.replaceSelection("");
		String s="";
		if (!sine2Ch.isSelected())
		{
			//s=s+" Tester grup de bare: MTF normalizat la valoarea lui maxima [corespunzatoare freventei mainime]!"+" \n";
			s=s+" Bar pattern group: Normalized MTF!"+" \n";
		}
		else
		{
			//s=s+" Tester grup de sinusoide:"+" \n";
			s=s+" Sine-Bar pattern group: Normalized MTF!"+" \n";
		}
		//s=s+" Marire: "+nfe.format(magnification)+" \n";
		s=s+" Magnification: "+nfe.format(magnification)+" \n";
		s=s+" Pixels per image mm: "+nfe.format(ppmm)+" \n";
		s=s+" Nyquist cutt-off frequency [cy/mm]: "+nfe.format(ppmm/2.0)+" \n";
		s=s+" Frequency for MTF reduction at 1/2 of MTF[minimum frequency] in [cy/mm]: "+
		nfe.format(fhalf)+" \n";
		s=s+" Frequency for MTF reduction at 1/10 of MTF[minimum frequency] in [cy/mm]: "+
		nfe.format(ftenth)+" \n";
		s=s+" Frequency for MTF reduction at 2% of MTF[minimum frequency] in [cy/mm]: "+
		nfe.format(f_2)+" \n";

		s=s+"---------------------------------------------------------"+" \n";
		s=s+" If frequency of imaged pattern is higher than Nyquist frequency( =1/[2 x sampilng interval])=> aliasing!!"+" \n";
		s=s+" In this case, results are not concludent!"+" \n";
		//s=s+" Daca frecventa testerului imagiat este mai mare decat frecventa Nyquist (=1/[2 x sampling interval])=> fenomen ALIASING!"+" \n";
		//s=s+" In acest caz rezultatele sunt NECONCLUDENTE!"+" \n";
		simTa.append(s);

		//==========================
		Graph.xmin=mtfxx[0];
		new Graph();
	}

	/**
	 * Called by computeMTF. Handles slit pattern tester
	 */
	private void computeSlit()
	{
		int ndat=npixels;
		if (ndat<1) return;

		double[] xxx=new double[ndat];
		double[] yyy=new double[ndat];
		for (int i=0; i<ndat;i++)
		{
			xxx[i]=i;
			yyy[i]=pixelValue[i];
		}
		//find maximum then divide x interval in -inf,...0,...+inf, 0=y max!!
		int imax=Sort.findPosition(yyy,1);
		double[] xx=new double[ndat];
		double[] yy=new double[ndat];
		int j=0;int jj=0;
		while(true)
		{
			if(j<imax)
			{
				xx[j]=-xxx[imax-j];
				yy[j]=yyy[j];//unchanged
				j++;
			}
			else
			{
				xx[j+jj]=xxx[imax+jj]-xxx[imax];
				yy[j+jj]=yyy[j+jj];
				jj++;
			}
			if (j+jj==ndat) break;
		}

		//for(j=1;j<=ndat;j++)
		//{
//System.out.println("x "+xx[j-1]+" y "+yy[j-1]+" -old- x "+xxx[j-1]+" y "+yyy[j-1]);
		//}


//int j=0;
/*double[] xx=
		{
-2.54,//0,
-2.22,//0.32,
-1.90,//0.64,
-1.59,//0.96,
-1.27,//1.28,
-0.95,//1.6,
-0.63,//1.92,
-0.32,//2.24,
0.0,//2.56,
0.32,//neg part here!!! symmetric versus 0!!
0.63,
0.95,
1.27,
1.59,
1.90,
2.22,
2.54,

		};

double[]yy=
{
35.0,//10240.0,
52.0,
84.0,//1764.0,
153.0,//656.0,
227.0,//183.0,
415.0,//154.0,
797.0,//139.0,
1087.0,//101.0,
13638.0,//35.0,
10240,
1764.0,
650.0,
183.0,
154.0,
139.0,
101.0,
95.0,
};*/
//WORKS
/*double[] xx=
		{
0.0,
-0.32,//0,
-0.63,//0.32,
-0.95,//0.64,
-1.27,//0.96,
-1.59,//1.28,
-1.90,//1.6,
-2.22,//1.92,
-2.54,//2.24,
0.32,//2.56,
0.63,
0.95,
1.27,
1.59,
1.90,
2.22,
2.54,
		};

double[]yy=
{
	13638,
1087.,
797.,
415.,//1764.0,
227.,//656.0,
153.0,//183.0,
84.0,//154.0,
52.0,//139.0,
35.0,//101.0,
10240.0,//35.0,
1764.0,
650.0,
183.0,
154.0,
139.0,
101.0,
95.0
};
//WORKS !!!!!!!!!!!!!!
*/

		int nmtf=0;
		int nintervals=30;
		double dx=Math.abs(xx[1]-xx[0]);//=1 pixel
		dx=dx/ppmm;//delta=1/ppmm
		double vmax=1.0/(2.0*dx);//Nyquist frequency=cy/mm=ppmm/2
		double vmin=vmax/nintervals;//0.2;
		double v=0.0;
		if (vmax<vmin) return;//!!!!!!!!!!!!!!!!!!!!!!!!!1NEVER
		nmtf=1;//1 frequency at v=0!!!
		while(true)
		{
			v=v+vmin;
			if (v>vmax)break;
			nmtf=nmtf+1;
		}

        double sumf=0.0;
        double sumcosf=0.0;
		double[] mtfxx=new double[nmtf];
		double[] mtfyy=new double[nmtf];

		Graph.interv2=nmtf;
		Graph.xarray2=new double[nmtf];
		Graph.yarray2=new double[nmtf];

		double[] mtftrunc=new double[nmtf];
		double[] mtfxxsort=new double[nmtf];

		for (int i=1; i<=nmtf; i++)
		{
			mtfxx[i-1]=(i-1)*vmin;
            sumf=0.0;
            sumcosf=0.0;
			for (j=1; j<=ndat; j++)
		    {
				sumf=sumf+yy[j-1];
				sumcosf=sumcosf+yy[j-1]*Math.cos(2.0*Math.PI*xx[j-1]*mtfxx[i-1]/ppmm);
				//here frequency converted to per pixels (data=pixels on image)=>OK!!!
			}

		    mtfyy[i-1]=sumcosf/sumf;
			Graph.xarray2[i-1]=mtfxx[i-1];
			Graph.yarray2[i-1]=mtfyy[i-1];

			mtftrunc[i-1]=mtfyy[i-1];
			mtfxxsort[i-1]=mtfxx[i-1];

  	    }

		//==========================
		double mtfhalf=0.0;double mtftenth=0.0;double mtf_2=0.0;
		double fhalf=0.0;double ftenth=0.0;double f_2=0.0;
		int ifmin=0;int ifmax=0;
		mtfhalf=mtfyy[0]/2.0;mtftenth=mtfyy[0]/10.0;mtf_2=mtfyy[0]/50.0;

		Sort.qSort2(mtftrunc,mtfxxsort);
		
		Sort.findNearestValue(mtftrunc, mtfhalf, true);ifmin=Sort.getNearestPosition();
       	if (ifmin<mtftrunc.length-1)
           ifmax=ifmin+1;
        else
        {
			ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			fhalf=mtfxxsort[0];//first means last=>crescator
		else
			fhalf=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtfhalf);

		Sort.findNearestValue(mtftrunc, mtftenth, true);ifmin=Sort.getNearestPosition();
	   	if (ifmin<mtftrunc.length-1)
	       ifmax=ifmin+1;
	    else
        {
			ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			ftenth=mtfxxsort[0];//first means last=>crescator
		else
			ftenth=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtftenth);

		Sort.findNearestValue(mtftrunc, mtf_2, true);ifmin=Sort.getNearestPosition();

       	if (ifmin<mtftrunc.length-1)
           ifmax=ifmin+1;
        else
        {
			ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			f_2=mtfxxsort[0];//first means last=>crescator
		else
			f_2=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtf_2);

		NumberFormat nfe = NumberFormat.getInstance(Locale.US);
		nfe.setMinimumFractionDigits(2);//default e 2 oricum!!
		nfe.setMaximumFractionDigits(2);//default e 2 oricum!!
		nfe.setGroupingUsed(false);

        simTa.selectAll();
        simTa.replaceSelection("");
		String s=" Slit pattern"+" \n";
		s=s+" Magnification: "+nfe.format(magnification)+" \n";
		s=s+" Pixels per image mm: "+nfe.format(ppmm)+" \n";
		s=s+" Nyquist cutt-off frequency [cy/mm]: "+nfe.format(ppmm/2.0)+" \n";
		s=s+" Frequency for MTF reduction at 1/2 of MTF[minimum frequency] in  [cy/mm]: "+
		nfe.format(fhalf)+" \n";
		s=s+" Frequency for MTF reduction at 1/10 of MTF[minimum frequency] in [cy/mm]: "+
		nfe.format(ftenth)+" \n";
		s=s+" Frequency for MTF reduction at 2% of MTF[minimum frequency] in [cy/mm]: "+
		nfe.format(f_2)+" \n";
		s=s+"---------------------------------------------------------"+" \n";
		s=s+" If frequency of imaged pattern is higher than Nyquist frequency( =1/[2 x sampilng interval])=> aliasing!!"+" \n";
		s=s+" In this case, results are not concludent!"+" \n";
		/*s=s+" Marire: "+nfe.format(magnification)+" \n";//s+" Magnification: "+nfe.format(magnification)+" \n";
		s=s+" Pixeli per imagine [pixels/mm]: "+nfe.format(ppmm)+" \n";//" Pixels per image mm: "+nfe.format(ppmm)+" \n";
		s=s+" Frecventa limita -maxima- Nyquist a testerului [cy/mm]: "+nfe.format(ppmm/2.0)+" \n";//" Nyquist cutt-off frequency [cy/mm]: "+nfe.format(ppmm/2.0)+" \n";
		s=s+" Frecventa corespunzatoare reducerii MTF la 1/2 din MTF maxim [cy/mm]: "+//" Frequency for 1/2 reduction of minimum frequency MTF  [cy/mm]: "+
		nfe.format(fhalf)+" \n";
		s=s+" Frecventa corespunzatoare reducerii MTF la 1/10 din MTF maxim [cy/mm]: "+//" Frequency for 1/10 reduction of minimum frequency MTF [cy/mm]: "+
		nfe.format(ftenth)+" \n";*/
		//s=s+" Frecventa corespunzatoare reducerii MTF la 2% din MTF maxim [cy/mm]: "+//" Frequency for 1/10 reduction of minimum frequency MTF [cy/mm]: "+
		//nfe.format(f_2)+" \n";

		//s=s+"---------------------------------------------------------"+" \n";
		//s=s+" Daca frecventa testerului imagiat este mai mare decat frecventa Nyquist (=1/[2 x sampling interval])=> fenomen ALIASING!"+" \n";
		//s=s+" In acest caz rezultatele sunt NECONCLUDENTE!"+" \n";

		simTa.append(s);

		//==========================
		Graph.xmin=mtfxx[0];
		new Graph();
	}

	/**
	 * Called by computeMTF. Handles sine wave pattern tester
	 */
	private void computeSine()
	{
		double f1=0.0;double f2=0.0;double whiteLvl=0.0;
        try
        {
		    f1=Convertor.stringToDouble(fminTf.getText());
		    f2=Convertor.stringToDouble(fmaxTf.getText());
		    contrastThresholdPercent=Convertor.stringToDouble(contrastThresholdPercentTf.getText());
		    whiteLvl=Convertor.stringToDouble(whiteTf.getText());
		    greyThreshold=Convertor.stringToDouble(greyThresholdTf.getText());
		}
		catch(Exception e)
		{
		    String title ="Error...";
		    String message ="Insert real numbers!";//resources.getString("dialog.double.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		if ((f1<0 || f2<0) || (whiteLvl<0 || greyThreshold<0)||contrastThresholdPercent<0)
		{
		    String title ="Error...";
		    String message ="Insert positive real numbers!";
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		greyThreshold=greyThreshold*whiteLvl/100.0;

		//==========1 SINE PATTERN, logarithmic scale for cycles(LP)/mm

		int ndat=npixels;
		if (ndat<1) return;

		double[] xxx=new double[ndat];
		double[] yyy=new double[ndat];
		for (int i=0; i<ndat;i++)
		{
			xxx[i]=i;
			yyy[i]=pixelValue[i];
		}

		yneted=new double[ndat];

		Graph.interv=ndat;
		Graph.xarray=new double[ndat];
		Graph.yarray=new double[ndat];
		Graph.interv1=ndat;
		Graph.xarray1=new double[ndat];
		Graph.yarray1=new double[ndat];
		Graph.interv2=ndat;
		Graph.xarray2=new double[ndat];
		Graph.yarray2=new double[ndat];
		Graph.interv3=ndat;
		Graph.xarray3=new double[ndat];
		Graph.yarray3=new double[ndat];
		
		//===============================SMOOTHING==========================================
		nneted=smoothCb.getSelectedIndex();//0.1,...ok
		if (nneted>0)
		{
			for(int j=1;j<=nneted;j++)
			{
				for (int i=1; i<=ndat; i++)
				{
					if(j==1)
					{
						//========1st smoothing
						yneted[i-1]=yyy[i-1];
						if((i>2)&&(i<ndat-1))
						{
							yneted[i]=yyy[i];
							yneted[i+1]=yyy[i+1];
						}
						//=======================
					}

					if((i>2)&&(i<ndat-1))//eg 3 and ndat-2
					{
						yneted[i-1]=(1.0/35.0)*(-3.0*yneted[i-3]+12.0*yneted[i-2]+17.0*yneted[i-1]+
								12.0*yneted[i]-3.0*yneted[i+1]);
					}
				}
			}
		}
		//no smooth
		if(nneted==0)
		{
			for (int i=0; i<ndat;i++)
			{
				yneted[i]=yyy[i];
			}
	    }
		//==========end SMOOTHING=============================
		
		double xmax=xxx[ndat-1];
		
		double[] spfreq=new double[ndat];
		double[] xnorm=new double[ndat];
		int[] idiff=new int[ndat];
		
		double[] mtfxx=new double[ndat];
		double[] mtfyy=new double[ndat];
		double[] mtfyyb=new double[ndat];
		int nmtf=0;
		double maxmtf=0.0;
		boolean isMinFirstB=false;

		int nmax=0;int nmin=0;int nn=0;
		double[] dymax=new double[ndat];
		double[] dymin=new double[ndat];
		double[] dxmax=new double[ndat];
		double[] dxmin=new double[ndat];

		for (int i=1; i<=ndat; i++)
		{
			xnorm[i-1]=xxx[i-1]/xmax;//[0]=1/N;[N-1]=N/N=1

			if (logScaleRb.isSelected())
			{
				//spfreq[i-1]=f1*Math.pow(f2/f1,xnorm[i-1]);//in fact must be: f1*(f2/f1)^(i-1/(N-1))
				//proof is similar to lin, but here logf1=a/N+b;logf2=a+b=>a=log(f2/f1)/(1-1/N)=log[(f2/f1)^(N/(N-1))]
				//b=logf1-a/N=log[f1/[(f2/f1)^(1/(N-1))]]=>logf=ai/N+b=log[(f2/f1)^(i/(N-1))*f1/[(f2/f1)^(1/(N-1))]]
				//=>f=f1(f2/f1)^((i-1)/(N-1))
				
				spfreq[i-1]=f1*Math.pow(f2/f1,(xxx[i-1]-1.0)/(xmax-1.0));
		    }
			else if (linScaleRb.isSelected())
			{
				double aa=(f2-f1)/(1.0-xnorm[0]);
				double bb=f2-aa*1.0;//Si=ai/N+b;S1=f1=a/N+b;SN=f2=a+b
				spfreq[i-1]=aa*xnorm[i-1]+bb;//ok!!!!
			}

			spfreq[i-1]=spfreq[i-1]/magnification;

			if (i>1)
			{
				double dif=yneted[i-1]-yneted[i-2];
				int idif=0;
				if (dif>=0) idif=1;//max
				else idif=-1;//pure min

				idiff[i-1]=idif;
				idiff[0]=idiff[1];

				if (idiff[i-1]==-1 && idiff[i-2]==1)//max
				{
					nmax++;
					dymax[nmax-1]=yneted[i-2];
					dxmax[nmax-1]=spfreq[i-2];
				}
				if (idiff[i-1]==1 && idiff[i-2]==-1)//min
				{
					nmin++;
					dymin[nmin-1]=yneted[i-2];
					dxmin[nmin-1]=spfreq[i-2];

					if (nmax==0)
						isMinFirstB=true;

				}

			}

			Graph.xarray[i-1]=xxx[i-1];//original
			Graph.yarray[i-1]=yyy[i-1];

			Graph.xarray3[i-1]=xxx[i-1];//smooth but in old scale
			Graph.yarray3[i-1]=yneted[i-1];

			Graph.xarray1[i-1]=spfreq[i-1];//sine wave response
			Graph.yarray1[i-1]=yneted[i-1];
		}

		nn=Math.min(nmax,nmin);
		for (int i=1; i<=nn; i++)
		{
			if(100.0*Math.abs((dymax[i-1]-dymin[i-1])/(dymax[i-1]+dymin[i-1]))>contrastThresholdPercent)//e.g.2% resoution limit
			{
				if(dymax[i-1]>greyThreshold)//threshold 20 grey level 255*8/100=~20=8% error in gray lvl.
				{
					nmtf++;//score it
					if (!isMinFirstB)
						mtfxx[nmtf-1]=dxmin[i-1];//take last allways!!
					else
						mtfxx[nmtf-1]=dxmax[i-1];
					
					mtfyy[nmtf-1]=Math.abs((dymax[i-1]-dymin[i-1])/(dymax[i-1]+dymin[i-1]));

					if (mtfyy[nmtf-1]>maxmtf)
						maxmtf=mtfyy[nmtf-1];
				}
			}
		}

		double[] mtftrunc=new double[nmtf];
		double[] mtfxxsort=new double[nmtf];

		for (int i=1; i<=nmtf; i++)
		{
			mtfyy[i-1]=mtfyy[i-1]/maxmtf;//NORMALIZATION
			if (sineCh.isSelected())
			{
				mtftrunc[i-1]=mtfyy[i-1];
				mtfxxsort[i-1]=mtfxx[i-1];

				Graph.xarray2[i-1]=mtfxx[i-1];
				Graph.yarray2[i-1]=mtfyy[i-1];
		    }
		}
		
		//=====================================================================================================
		
		if (!sineCh.isSelected())//bar=>Apply Coltman equation here
		{
			//==============may require sort here but not necessarely

			//=====================================================
			double nf=0.0;double cnf=0.0;
			int imin=0;int imax=0;
			//----------------------inner loop-------------
			for (int i=1; i<=nmtf; i++)
			{
				//=================1f================
				mtfyyb[i-1]=mtfyy[i-1];
				//-------compute mtfold [C(f)] at 3f, 5 f,....
				//===============3f===============================
				nf=3.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            	{
	           		imax=imin;
               	}

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]+cnf/3.0;
				}
				//===============5f==============================
				nf=5.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            		imax=imin;

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]-cnf/5.0;
				}
				//===============7f==============================
				nf=7.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            		imax=imin;

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]+cnf/7.0;
				}
				//===============11f==============================
				nf=11.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            		imax=imin;

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]+cnf/11.0;
				}
				//===============13f==============================
				nf=13.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            		imax=imin;

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]-cnf/13.0;
				}
				//===============15f==============================
				nf=15.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            		imax=imin;

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]-cnf/15.0;
				}
				//===============17f==============================
				nf=17.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            		imax=imin;

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]-cnf/17.0;
				}
				//===============19f==============================
				nf=19.0*mtfxx[i-1];
				//look for lower and upper values in mtfyyy arays
				Sort.findNearestValue(mtfxx, nf, true);imin=Sort.getNearestPosition();
            	if (imin<mtfxx.length-1)
            		imax=imin+1;
            	else
            		imax=imin;

				if (imin<mtfxx.length-1)//valid
				{
					cnf=linInt(mtfxx[imax],mtfyy[imax],mtfxx[imin],mtfyy[imin],nf);
					mtfyyb[i-1]=mtfyyb[i-1]+cnf/19.0;
				}
				//=================================================
				mtfyyb[i-1]=mtfyyb[i-1]*Math.PI/4.0;//Pi/4 discrepancy!!!
				mtftrunc[i-1]=mtfyyb[i-1];
				mtfxxsort[i-1]=mtfxx[i-1];

				Graph.xarray2[i-1]=mtfxx[i-1];
				Graph.yarray2[i-1]=mtfyyb[i-1];
			}
		}

		//==========================
		double mtfhalf=0.0;double mtftenth=0.0;double mtf_2=0.0;
		double fhalf=0.0;double ftenth=0.0;double f_2=0.0;
		int ifmin=0;int ifmax=0;
		if (!sineCh.isSelected())
		{
			mtfhalf=mtfyyb[0]/2.0;mtftenth=mtfyyb[0]/10.0;mtf_2=mtfyyb[0]/50.0;
		}
		else
		{
			mtfhalf=mtfyy[0]/2.0;mtftenth=mtfyy[0]/10.0;mtf_2=mtfyy[0]*1.0/50.0;
			//look for lower and upper values in mtfyyy arays
		}
		Sort.qSort2(mtftrunc,mtfxxsort);
		
		Sort.findNearestValue(mtftrunc, mtfhalf, true);ifmin=Sort.getNearestPosition();
       	if (ifmin<mtftrunc.length-1)
           ifmax=ifmin+1;
        else
        {
		   ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			fhalf=mtfxxsort[0];//first means last=>crescator
		else
			fhalf=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtfhalf);

		Sort.findNearestValue(mtftrunc, mtftenth, true);ifmin=Sort.getNearestPosition();
	   	if (ifmin<mtftrunc.length-1)
	       ifmax=ifmin+1;
	    else
        {
			ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components
	    }
		if (ifmin==mtftrunc.length-2)//not found
			ftenth=mtfxxsort[0];//first means last=>crescator
		else
			ftenth=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtftenth);

		Sort.findNearestValue(mtftrunc, mtf_2, true);ifmin=Sort.getNearestPosition();
       	if (ifmin<mtftrunc.length-1)
           ifmax=ifmin+1;
        else
        {
		   ifmin=mtftrunc.length-1;
           ifmax=ifmin;
           ifmin=ifmax-1;//force last 2 components           
	    }
		if (ifmin==mtftrunc.length-2)//not found
			f_2=mtfxxsort[0];//first means last=>crescator
		else
		    f_2=linInt(mtftrunc[ifmax],mtfxxsort[ifmax],mtftrunc[ifmin],mtfxxsort[ifmin],mtf_2);
	
		//=================================================================================
		NumberFormat nfe = NumberFormat.getInstance(Locale.US);
		nfe.setMinimumFractionDigits(2);//default e 2 oricum!!
		nfe.setMaximumFractionDigits(2);//default e 2 oricum!!
		nfe.setGroupingUsed(false);

        simTa.selectAll();
        simTa.replaceSelection("");
		String s="";
		if (!sineCh.isSelected())
			s=s+" Continuous bar pattern: "+" \n";
			//s=s+" Tester continuu de bare: "+" \n";
		else
			s=s+" Continuous sine pattern: "+" \n";
			//s=s+" Tester continuu de sinusoide: "+" \n";
		s=s+" Magnification: "+nfe.format(magnification)+" \n";
		s=s+" Pixels per image mm: "+nfe.format(ppmm)+" \n";
		s=s+" Nyquist cutt-off frequency [cy/mm]: "+nfe.format(ppmm/2.0)+" \n";
		s=s+" Frequency for MTF reduction at 1/2 of MTF[minimum frequency] in  [cy/mm]: "+
		nfe.format(fhalf)+" \n";
		s=s+" Frequency for MTF reduction at 1/10 of MTF[minimum frequency] in [cy/mm]: "+
		nfe.format(ftenth)+" \n";
		s=s+" Frequency for MTF reduction at 2% of MTF[minimum frequency] in [cy/mm]: "+
		nfe.format(f_2)+" \n";
		s=s+"---------------------------------------------------------"+" \n";
		s=s+" If frequency of imaged pattern is higher than Nyquist frequency( =1/[2 x sampilng interval])=> aliasing!!"+" \n";
		s=s+" In this case, results are not concludent!"+" \n";
		/*s=s+" Marire: "+nfe.format(magnification)+" \n";//s+" Magnification: "+nfe.format(magnification)+" \n";
		s=s+" Pixeli per imagine [pixels/mm]: "+nfe.format(ppmm)+" \n";//" Pixels per image mm: "+nfe.format(ppmm)+" \n";
		s=s+" Frecventa limita -maxima- Nyquist a testerului [cy/mm]: "+nfe.format(ppmm/2.0)+" \n";//" Nyquist cutt-off frequency [cy/mm]: "+nfe.format(ppmm/2.0)+" \n";
		s=s+" Frecventa corespunzatoare reducerii MTF la 1/2 din MTF maxim [cy/mm]: "+//" Frequency for 1/2 reduction of minimum frequency MTF  [cy/mm]: "+
		nfe.format(fhalf)+" \n";
		s=s+" Frecventa corespunzatoare reducerii MTF la 1/10 din MTF maxim [cy/mm]: "+//" Frequency for 1/10 reduction of minimum frequency MTF [cy/mm]: "+
		nfe.format(ftenth)+" \n";
		s=s+" Frecventa corespunzatoare reducerii MTF la 2% din MTF maxim [cy/mm]: "+//" Frequency for 1/10 reduction of minimum frequency MTF [cy/mm]: "+
		nfe.format(f_2)+" \n";*/

		//s=s+"---------------------------------------------------------"+" \n";
		//s=s+" Daca frecventa testerului imagiat este mai mare decat frecventa Nyquist (=1/[2 x sampling interval])=> fenomen ALIASING!"+" \n";
		//s=s+" In acest caz rezultatele sunt NECONCLUDENTE!"+" \n";
		simTa.append(s);

		//simTa.append(s);

		//==========================
		String smthS=(new Integer(nneted)).toString();
		Graph.xmin=mtfxx[0];//for MTF graph only!!
		new Graph(smthS);

	}

	/**
	 * Computes SINC function
	 * @param x x
	 * @return the result
	 */
	public static double sinc (double x)
	{
		double result=1.0;
		if (x!=0)
			result=Math.sin(Math.PI*x)/(Math.PI*x);
		//x=0=>sin(x)/x=1!!!!
		return result;
	}


	
	//------------------ABS DFT------------------------------------------------------------------------------
	
	//measure a number of periods at same spatial frequency=>e.g number of bars,or number of sine periods
	//test patterns=>group!!!
	
	//static final int nHARMONICS = 15;//if want to compute the harmonics just for fun
	//in this case=>iNK must be set to nHARMINICS*nCycles.
	
	//public static double[] AbsDFT(double[] y, int ink)
	/**
	 * Return the absolute discrete Fourier transform value
	 * @param y y
	 * @param nCycles nCycles
	 * @return the result
	 */
	public static double AbsDFT(double[] y, int nCycles)
	{
		int iN=y.length;
		
		//if (ink > iN)
			//return null;
		//double[] result=new double[iN];
		
		double result =0.0;
		if (iN<2) return result;

		double A=0.0;double B=0.0;
		
		//for (int k=0; k<=ink;k++)
		//{
			A=0.0;B=0.0;
			for (int x=0; x<iN;x++)
			{
				//A=A+y[x]*Math.cos(-2.0*Math.PI*x*k/iN);
				//B=B+y[x]*Math.sin(-2.0*Math.PI*x*k/iN);
				
				//-angle or + angle is of no importance!!e.g 7-9 = -7+9, in the end we take the square!!
				
				A=A+y[x]*Math.cos(-2.0*Math.PI*x*nCycles/iN);
				B=B+y[x]*Math.sin(-2.0*Math.PI*x*nCycles/iN);//SEE MATH, MTF derivations!				
			}
	    	//result[k]=Math.sqrt(A*A+B*B);
			result=Math.sqrt(A*A+B*B);
	    //}
			
		//Note:
		//result[number of sampled covered periods]=max DFT corresponding to
		//fundamental frequency=>see theory
		//last value is for freq=1/2delta, Nyquist. Array has 0 values till the end. OK!!!
		
		return result;
	}

	/**
	 * Linear interpolation
	 * @param x1 first point x-value
	 * @param y1 first point y-value
	 * @param x2 second point x-value
	 * @param y2 second point y-value
	 * @param x desire point x-value
	 * @return desire point y-value
	 */
	public static double linInt(double x1, double y1, double x2, double y2 ,double x)
	{
		double result=-1.0;
        double[] mn=new double[2];
        //insucces
        mn[0]=-1.0;//m
        mn[1]=-1.0;//n
        double num=x1-x2;
        if(num!=0.0)
        {
        	mn[0]=(y1-y2)/num;
        	mn[1]=(x1*y2-y1*x2)/num;
        	result=mn[0]*x+mn[1];
		}
        return result;
	}

	/**
	 * Given a vector v, this routine converts it into a double array.
	 * @param v v
	 * @return the result
	 */
	@SuppressWarnings("rawtypes")
	private double[] convertVectorToDoubleArray(Vector v)
	{
		double[] result = new double[v.size()];
		for(int i=0; i<v.size(); i++)
		{
			String s=(String)v.elementAt(i);
			result[i]=Convertor.stringToDouble(s);
		}
		return result;
    }

	
}
