package xrtf_mAs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.Date;
import java.util.ResourceBundle;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.apache.pdfbox.pdmodel.PDDocument;

import javax.swing.JList;
import javax.swing.DefaultListModel;

import radQC.RadQCFrame;
import danfulea.db.DatabaseAgent;
import danfulea.math.Convertor;
import danfulea.math.StatsUtil;
import danfulea.utils.MessageRetriever;
import danfulea.utils.PDFRenderer;
import danfulea.utils.FrameUtilities;
import danfulea.utils.ListUtilities;
import danfulea.utils.ExampleFileFilter;
import danfulea.utils.ScanDiskLFGui;

//import jdf.db.DBConnection;
import danfulea.phys.XRay;
import danfulea.phys.XRaySpectrum;
import danfulea.math.Sort;
import danfulea.math.numerical.Interpolator;

/**
 * Class designed to perform HVL QC test, i.e. to compute HVL and total tube filtration since they are related. <br>
 * It also computes HVL2, RHO and corresponding filtration as well as theoretical values using SRS78 database for XRay spectrum simulation. <br>
 * The equivalent filtration (to be considered as total tube filtration) is computed as weighted mean from filtrations derived from HVL1 and HVL2. 
 * In theory, filtrations computed from HVL1 and HVL2 should match so this is not quite necessary. If they don't match it will raise the 
 * question which one is more appropriate to be used as total tube filtration and/or how to establish the weights for a mean value? One 
 * empirical way is to consider radiation absorbed in soft tissue and based of tissue thickness make a comparison with 
 * HVL (radiation intensity drop at 1/2) and QVL (radiation intensity drop at 1/4). HVL1 is simply HVL and HVL2 is QVL-HVL so they 
 * are related. So, if filtrations don't match it will be perform a fine tunning based on assumption the radiation will reach the human body! As stated above, 
 * the filtrations should match regardless if they are computed from HVL, QVL or TVL (radiation drop at 1/10) so be sure your experimental setup 
 * for HVL and QVL measurements is appropriate, there are no voltage variations etc. If however you still get different results for 
 * filtrations, then the equivalent filtration is used to estimate the total tube filtration for further calculations such as Monte Carlo simulations for dose assessment but 
 * be advise, there is something wrong with the tested XRay tube.  
 * @author Dan Fulea, 11 Jun. 2013
 *
 */
public class MainFrame extends JFrame implements MessageRetriever, Runnable, ActionListener{
	
	protected RadQCFrame mf;
	protected boolean hasParent=false;
	
	private static final long serialVersionUID = 1L;
	private final Dimension PREFERRED_SIZE = new Dimension(900, 730);
	private static final String BASE_RESOURCE_CLASS = "xrtf_mAs.resources.MainFrameResources";
	protected ResourceBundle resources;
	
	public static Color bkgColor = new Color(230, 255, 210, 255);// Linux mint green
	public static Color foreColor = Color.black;// Color.white;
	public static Color textAreaBkgColor = Color.white;// Color.black;
	public static Color textAreaForeColor = Color.black;// Color.yellow;
	public static boolean showLAF = true;
	
	private JLabel statusL = new JLabel("Waiting...");
	private static final String EXIT_COMMAND = "EXIT";
	private static final String ABOUT_COMMAND = "ABOUT";
	private static final String RUN_COMMAND = "RUN";
	//private static final String KILL_COMMAND = "KILL";
	private static final String PRINT_COMMAND = "PRINT";
	private static final String LOOKANDFEEL_COMMAND = "LOOKANDFEEL";
	
	private static final String ADD_COMMAND = "ADD";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String RESET_COMMAND = "RESET";
	private static final String FILTRATION_COMMAND = "FILTRATION";
	private static final String SETLIMITS_COMMAND = "SETLIMITS";
	private static final String SAVE_COMMAND = "SAVE";
	
	private JFrame xrayframe,xrayframe2;
	private PleaseWait pw;
	private volatile Thread simTh;
	private JButton runB, printB, addB, deleteB, resetB, filtrationB, setLimitsB, saveB;
	protected JTextArea textArea = new JTextArea();
	private static final Dimension textAreaDimension = new Dimension(800, 100);
	private JTextField filtrareTf = new JTextField(5);
	private JTextField hvlTf = new JTextField(5);
	private JTextField hvlErrorTf = new JTextField(3);

	private JTextField mmAlTf = new JTextField(5);
	private JTextField fmmAlTf = new JTextField(5);
	private JTextField exposureTf = new JTextField(5);
	private JTextField distanceTf = new JTextField(5);
	private static final Dimension sizeCb = new Dimension(60, 21);
	@SuppressWarnings("rawtypes")
	private JComboBox uAnodCb, ianodCb, irippleCb, kvCb;
	private JRadioButton mrRb, mradRb, mgyRb;
	@SuppressWarnings("rawtypes")
	protected JList puncteLst;
	protected JScrollPane listSp;
    @SuppressWarnings("rawtypes")
	protected DefaultListModel dlm=new DefaultListModel() ;
    private static final Dimension sizeLst = new Dimension(170,110);
    
	private boolean stopAppend = false;
	protected String outFilename = null;
	private boolean saveBoo=true;
	
	private int nPoints=0;
	@SuppressWarnings("rawtypes")
	private Vector xv=new Vector();
    @SuppressWarnings("rawtypes")
	private Vector yv=new Vector();
    protected double[] xs=new double[0];//graph
    protected double[] ys=new double[0];
    protected double[] x=new double[0];//graph
    protected double[] y=new double[0];
    private double uAnodD=0.0;
    private double kVD=0.0;
    private double filtrareD=0.0;
    private double distance=0.0;
    private double exposure=0.0;
    private double unc=0.0;
    private String str="";
    XRaySpectrum buildx;
    
    private JTextField hvlminTf = new JTextField(5);
    private JTextField filtrationminTf = new JTextField(5);
    private double minHVL=0.0;
    private double minFiltration=0.0;
    protected double KV_toSave=-1;//-1 also signals nothing to save
    protected String measurementDate_toSave="";
    protected double HVL_toSave=0.0;
    protected double HVL_toSave_unc=0.0;
    protected double filtration_toSave=0.0;
    protected double filtration_toSave_unc=0.0;
	protected String HVL_resultS="PASSED";
	protected String filtration_resultS="PASSED";
    
    protected String mainDB = "";
    private JLabel minHVLlabel=new JLabel();
    private JLabel minFiltrationlabel=new JLabel();    
    
    protected String hvlFiltrationTable="";
    protected int IDLINK=0;
    
	/**
	 * Constructor.
	 * @param mf the RadQCFrame object
	 */
	
    public MainFrame(RadQCFrame mf){    	
    	this();
    	this.mf=mf;
    	hasParent=true;
    	mainDB=mf.radqcDB;
    	hvlFiltrationTable=mf.hvlFiltrationTable;
    	measurementDate_toSave=mf.measurementDate;
    	IDLINK=mf.IDLINK;
    	performQueryDb();
    }
    
    /**
     * Standalone constructor
     */
    public MainFrame() {
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
		
		//------------
		hvlminTf.setText("2.95");
		filtrationminTf.setText("2.55");
		//------------
		//performQueryDb();
		createGUI();
		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);
		setVisible(true);		
	}
    
    /**
     * Initialize database for retrieving the HVL and filtration limits.
     */
    private void performQueryDb(){
    	try {
    		if (mf.EXAMINATION_ID==1){
    			minHVLlabel.setText(resources.getString("hvl.permitted.min.mammo"));
    			minFiltrationlabel.setText(resources.getString("filtration.permitted.min.mammo"));
    		}
			String datas = mf.resources.getString("data.load");
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = mainDB;
			opens = opens + file_sep + dbName;
			//System.out.println("opens = "+opens);
			DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
			Connection con1 = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");
			int dummy=1;
			String s = "select * from " + mf.hvlFiltrationLimitsTable+" where ID = "+dummy;
			PreparedStatement stmt = con1.prepareStatement(s);
			ResultSet resultSet = stmt.executeQuery();
			resultSet.next();
			
			hvlminTf.setText(resultSet.getString(2));
			filtrationminTf.setText(resultSet.getString(3));
			
			resultSet.next();

			if (con1 != null)
				con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
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
			//System.out.println("Exiting wrong------------");
			dispose();
			System.exit(0);
		} else {
			//System.out.println("Exiting------------");
			//final RadQCFrame mff = mf;
			mf.setVisible(true);
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
		mmAlTf.requestFocusInWindow();
	}
	
	/**
	 * Creates the frame main panel.
	 * 
	 * @return the main Panel
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JPanel createMainPanel() {
		
		puncteLst = new JList(dlm);
	   	listSp = new JScrollPane(puncteLst);
		listSp.setPreferredSize(sizeLst);
		
		mrRb=new JRadioButton("mR");
		mradRb=new JRadioButton("mRad");
		mgyRb=new JRadioButton("mGy");

	    ButtonGroup group = new ButtonGroup();
		group.add(mrRb);
		group.add(mradRb);
		group.add(mgyRb);		
		
		String[] ua = new String[18];//22];
	    for(int i=6; i<=23; i++)
	        ua[i-6]=Convertor.intToString(i);
	    uAnodCb=new JComboBox(ua);
	    String s="17";
	    uAnodCb.setSelectedItem((Object)s);
	    uAnodCb.setMaximumRowCount(5);
	    uAnodCb.setPreferredSize(sizeCb);
	    
	    String[] ua1 ={"W","Mo","Rh"};
	    ianodCb=new JComboBox(ua1);
	    ianodCb.setSelectedIndex(0);//W
	    ianodCb.setMaximumRowCount(5);
	    ianodCb.setPreferredSize(sizeCb);
	    
	    String[] ua2 = {"0","5","10","15","20","25","30"};//ripple
	    irippleCb=new JComboBox(ua2);
	    s="0";//CP
	    irippleCb.setSelectedItem((Object)s);
	    irippleCb.setMaximumRowCount(5);
	    irippleCb.setPreferredSize(sizeCb);
	    
	    String[] ua4 = new String[126];
        for(int i=25; i<=150; i++)
	         ua4[i-25]=Convertor.intToString(i);
	    kvCb=new JComboBox(ua4);
	    s="80";
	    kvCb.setSelectedItem((Object)s);
	    kvCb.setMaximumRowCount(5);
	    kvCb.setPreferredSize(sizeCb);
	    
	    //filtrareTf.setText("2.5");
	    distanceTf.setText("100");
		hvlErrorTf.setText("5");
	    
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		buttonName = resources.getString("runB");
		buttonToolTip = resources.getString("runB.toolTip");
		buttonIconName = resources.getString("img.set");
		runB = FrameUtilities.makeButton(buttonIconName, RUN_COMMAND,
				buttonToolTip, buttonName, this, this);
		Character mnemonic = (Character) resources.getObject("runB.mnemonic");
		runB.setMnemonic(mnemonic.charValue());

		buttonName = resources.getString("printB");
		buttonToolTip = resources.getString("printB.toolTip");
		buttonIconName = resources.getString("img.report");
		printB = FrameUtilities.makeButton(buttonIconName, PRINT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("printB.mnemonic");
		printB.setMnemonic(mnemonic.charValue());
		
		buttonName = resources.getString("filtrationB");
		buttonToolTip = resources.getString("filtrationB.toolTip");
		buttonIconName = "";//resources.getString("img.set");
		filtrationB = FrameUtilities.makeButton(buttonIconName, FILTRATION_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("filtrationB.mnemonic");
		filtrationB.setMnemonic(mnemonic.charValue());
		
		buttonName = resources.getString("setLimitsB");
		buttonToolTip = resources.getString("setLimitsB.toolTip");
		buttonIconName = "";//resources.getString("img.set");
		setLimitsB = FrameUtilities.makeButton(buttonIconName, SETLIMITS_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("setLimitsB.mnemonic");
		setLimitsB.setMnemonic(mnemonic.charValue());
		
		buttonName = resources.getString("saveB");
		buttonToolTip = resources.getString("saveB.toolTip");
		buttonIconName = resources.getString("img.view");
		saveB = FrameUtilities.makeButton(buttonIconName, SAVE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("saveB.mnemonic");
		saveB.setMnemonic(mnemonic.charValue());
		
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
		deleteB = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("deleteB.mnemonic");
		deleteB.setMnemonic(mnemonic.charValue());

		buttonName = resources.getString("resetB");
		buttonToolTip = resources.getString("resetB.toolTip");
		buttonIconName = resources.getString("img.delete.all");
		resetB = FrameUtilities.makeButton(buttonIconName, RESET_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("resetB.mnemonic");
		resetB.setMnemonic(mnemonic.charValue());
		
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		textArea.setText("");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		JPanel resultP = new JPanel(new BorderLayout());
		resultP.add(new JScrollPane(textArea), BorderLayout.CENTER);
		resultP.setPreferredSize(textAreaDimension);
		resultP.setBackground(bkgColor);
		//=================================================
		JPanel mmAlP=new JPanel();
        mmAlP.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel mmAlL=new JLabel(resources.getString("expData.mmAlLabel"));
        mmAlP.add(mmAlL);
        mmAlP.add(mmAlTf);
        mmAlP.setBackground(bkgColor);
        
        JPanel fmmAlP=new JPanel();
        fmmAlP.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel fmmAlL=new JLabel(resources.getString("expData.fmmAlLabel"));
        fmmAlP.add(fmmAlL);
        fmmAlP.add(fmmAlTf);
        fmmAlP.setBackground(bkgColor);
        
        JPanel aP=new JPanel();
        aP.setLayout(new FlowLayout(FlowLayout.CENTER));
        aP.add(addB);
        aP.setBackground(bkgColor);
        
        JPanel listP=new JPanel(new BorderLayout());
        listP.add(listSp,BorderLayout.CENTER);
        
        JPanel lbP=new JPanel();
        lbP.setLayout(new FlowLayout(FlowLayout.CENTER));
        lbP.add(deleteB);
        lbP.add(resetB);
        lbP.setBackground(bkgColor);
        
        JPanel pa = new JPanel();
		BoxLayout blpa = new BoxLayout(pa, BoxLayout.Y_AXIS);
		pa.setLayout(blpa);
		pa.add(mmAlP, null);
		pa.add(fmmAlP, null);
		pa.add(aP, null);
		pa.setBackground(bkgColor);
		
		JPanel pb = new JPanel();
		BoxLayout blpb = new BoxLayout(pb, BoxLayout.Y_AXIS);
		pb.setLayout(blpb);
		pb.add(listP, null);
		pb.add(lbP, null);
		pb.setBackground(bkgColor);
		
		JPanel pc = new JPanel();
		pc.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		pc.add(pa);
		pc.add(pb);
		pc.setBackground(bkgColor);
		pc.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("expData.border"), foreColor));
		//==================================
		JPanel kvP=new JPanel();
        kvP.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel label=new JLabel(resources.getString("tubeData.kvLabel"));
        kvP.add(label);
        kvP.add(kvCb);
        label =new JLabel(resources.getString("tubeData.rippleLabel"));
        kvP.add(label);
        kvP.add(irippleCb);
        kvP.setBackground(bkgColor);
        
        JPanel anodP=new JPanel();
        anodP.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(resources.getString("tubeData.anodeLabel"));
        anodP.add(label);
        anodP.add(ianodCb);
        label =new JLabel(resources.getString("tubeData.anodeAngleLabel"));
        anodP.add(label);
        anodP.add(uAnodCb);
        anodP.setBackground(bkgColor);
        
        JPanel filtP=new JPanel();
        filtP.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(resources.getString("tubeData.filtrationLabel"));
        filtP.add(label);
        filtP.add(filtrareTf);
        filtP.setBackground(bkgColor);
        
        JPanel hvP=new JPanel();
        hvP.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(resources.getString("tubeData.hvlLabel"));
        hvP.add(label);
        hvP.add(hvlTf);
        hvP.setBackground(bkgColor);
        
        JPanel filtbP=new JPanel();
        filtbP.setLayout(new FlowLayout(FlowLayout.CENTER));
        filtbP.add(filtrationB);
        filtbP.setBackground(bkgColor);
        
        JPanel pd = new JPanel();
		BoxLayout blpd = new BoxLayout(pd, BoxLayout.Y_AXIS);
		pd.setLayout(blpd);
		pd.add(kvP, null);
		pd.add(anodP, null);
		pd.add(filtbP, null);
		pd.add(filtP, null);
		pd.add(hvP, null);
		//pd.add(filtbP, null);
		pd.setBackground(bkgColor);
		pd.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("tubeData.border"), foreColor));
		
		JPanel northP=new JPanel();
		northP.setLayout(new FlowLayout(FlowLayout.CENTER));
		northP.add(pc);
        northP.add(pd);
        northP.setBackground(bkgColor);
        
        //=======================
        JPanel p1P=new JPanel();
        p1P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(resources.getString("expData2.exposureLabel"));
        p1P.add(label);
        p1P.add(exposureTf);
        p1P.add(mrRb);
        p1P.add(mradRb);
        p1P.add(mgyRb);
        p1P.setBackground(bkgColor);
        mradRb.setSelected(true);
        mrRb.setBackground(bkgColor);
    	mradRb.setBackground(bkgColor);
    	mgyRb.setBackground(bkgColor);
    	
    	JPanel p2P=new JPanel();
        p2P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(resources.getString("expData2.distanceLabel"));
        p2P.add(label);
        p2P.add(distanceTf);
        p2P.setBackground(bkgColor);
        
        JPanel pe = new JPanel();
		BoxLayout blpe = new BoxLayout(pe, BoxLayout.Y_AXIS);
		pe.setLayout(blpe);
		pe.add(p1P, null);
		pe.add(p2P, null);
		pe.setBackground(bkgColor);
		pe.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("expData2.border"), foreColor));
		
		//-------------

		JPanel p30P=new JPanel();
        p30P.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        //if (mf.EXAMINATION_ID==1)
        //	label=new JLabel(resources.getString("hvl.permitted.min.mammo"));
        //else
        //	label=new JLabel(resources.getString("hvl.permitted.min"));
        minHVLlabel.setText(resources.getString("hvl.permitted.min"));
        p30P.add(minHVLlabel);//label);
        p30P.add(hvlminTf);
        p30P.setBackground(bkgColor);
        
        JPanel p31P=new JPanel();
        p31P.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        //if (mf.EXAMINATION_ID==1)
        //	label=new JLabel(resources.getString("filtration.permitted.min.mammo"));
        //else
        //	label=new JLabel(resources.getString("filtration.permitted.min"));
        minFiltrationlabel.setText(resources.getString("filtration.permitted.min"));
        p31P.add(minFiltrationlabel);//label);
        p31P.add(filtrationminTf);
        p31P.setBackground(bkgColor);

        JPanel p32P=new JPanel();
        p32P.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        p32P.add(setLimitsB);
        p32P.setBackground(bkgColor);
        
        JPanel minP = new JPanel();
		BoxLayout blminP = new BoxLayout(minP, BoxLayout.Y_AXIS);
		minP.setLayout(blminP);
		minP.add(p30P, null);
		minP.add(p31P, null);
		minP.add(p32P, null);
		minP.setBackground(bkgColor);
		minP.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("setLimits.border"), foreColor));
		//------------
		JPanel p3P=new JPanel();
        p3P.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("expData.unc"));
        p3P.add(label);
        p3P.add(hvlErrorTf);
        p3P.add(runB);
        p3P.add(printB);
        p3P.add(saveB);
        p3P.setBackground(bkgColor);
                
		JPanel north2P = new JPanel();
		BoxLayout blnorth2P = new BoxLayout(north2P, BoxLayout.Y_AXIS);
		north2P.setLayout(blnorth2P);
		north2P.add(northP, null);
		north2P.add(pe, null);
		north2P.add(minP, null);
		north2P.add(p3P, null);
		north2P.setBackground(bkgColor);
		
		mmAlTf.addActionListener(this);
		fmmAlTf.addActionListener(this);
		
		JPanel mainP = new JPanel(new BorderLayout());
		mainP.add(north2P, BorderLayout.NORTH);
		mainP.add(resultP, BorderLayout.CENTER);
		mainP.setBackground(bkgColor);
		return mainP;
	}
	/**
	 * Setting up the menu bar.
	 * 
	 * @param resources resources
	 * @return the result
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
		
		imageName = resources.getString("img.view");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("saveB");
		mnemonic = (Character) resources.getObject("saveB.mnemonic");
		JMenuItem saveItem = new JMenuItem(label, mnemonic.charValue());
		saveItem.setActionCommand(SAVE_COMMAND);
		saveItem.setIcon(img);
		saveItem.addActionListener(this);
		fileMenu.add(saveItem);
		
		fileMenu.addSeparator();

		imageName = resources.getString("img.view");
		img = FrameUtilities.getImageIcon(imageName, this);
		label = resources.getString("menu.file.filtration");
		mnemonic = (Character) resources.getObject("menu.file.filtration.mnemonic");
		JMenuItem filtrationItem = new JMenuItem(label, mnemonic.charValue());
		filtrationItem.setActionCommand(FILTRATION_COMMAND);
		//filtrationItem.setIcon(img);
		filtrationItem.addActionListener(this);
		filtrationItem.setToolTipText(resources.getString("menu.file.filtration.toolTip"));
		fileMenu.add(filtrationItem);
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
		} else if (command.equals(RUN_COMMAND)) {
			startComputation();
		//} else if (command.equals(KILL_COMMAND)) {
			//stopAppend = true;
			//stopSimulation();
		} else if (command.equals(PRINT_COMMAND)) {
			printReport();
		} else if (command.equals(LOOKANDFEEL_COMMAND)) {
			lookAndFeel();
		} else if (command.equals(FILTRATION_COMMAND)) {
			filtration();
		}else if (arg0.getSource()==mmAlTf)//enterul
        {
            fmmAlTf.requestFocusInWindow();
		}else if (arg0.getSource()==fmmAlTf||command.equals(ADD_COMMAND)) {
			add();
			mmAlTf.setText("");
            fmmAlTf.setText("");
            mmAlTf.requestFocusInWindow();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
			mmAlTf.setText("");
			fmmAlTf.setText("");
            mmAlTf.requestFocusInWindow();
		} else if (command.equals(RESET_COMMAND)) {
			reset();
			mmAlTf.setText("");
			fmmAlTf.setText("");
            mmAlTf.requestFocusInWindow();
		}  else if (command.equals(SETLIMITS_COMMAND)) {
			setLimits();
		} else if (command.equals(SAVE_COMMAND)) {
			save();
		}
		
	}
	
	/**
	 * Go to HVL/filtration database
	 */
	private void save(){
		new SaveViewDBFrame(this);
	}
	
	/**
	 * Initialize total filtration calculation based on tube attenuators.
	 */
	private void filtration(){
		new TubeAttenuators(this);
	}
	
	/**
	 * Shows the about window!
	 */
	private void about() {
		new AboutFrame(this);
	}
	
	/**
	 * Changing the look and feel can be done here. Also display some gadgets.
	 */
	private void lookAndFeel(){
		setVisible(false);
		new ScanDiskLFGui(this);
	}
	
	/**
	 * Start computation thread.
	 */
	private void startComputation() {
		if (simTh == null) {
			simTh = new Thread(this);
			simTh.start();// Allow one simulation at time!
		}
		// Do nothing if simulation is in progress and run button is hit again!
	}
	
	/**
	 * Stop computation thread.
	 */
	private void stopComputation() {
		
		if (simTh == null) {
			stopAppend = false;// press kill button but simulation never
								// started!
			return;
		}
		simTh = null;
		if (stopAppend) {// kill button was pressed!
			//Alpha_MC.STOPSIMULATION = true;// tell to stop simulation loop immediatly!
			textArea.append(resources.getString("text.simulation.stop") + "\n");
			stopAppend = false;
			String label = resources.getString("status.done");
			statusL.setText(label);
		}
		if (pw != null) {
			pw.stopAnimation();
			pw = null;
		}
	}
	
	/**
	 * Thread specific run method.
	 */
	public void run() {
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		simTh.setPriority(Thread.NORM_PRIORITY);
		performCalculation();
	}
	
	/**
	 * Printing messages via interface!
	 */
	public void printSequence(String s) {
		textArea.append(s + "\n");
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
						
			String str = " \n" + textArea.getText();
		
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
	 * Given a vector v, this routine converts it into a double array.
	 * @param v v
	 * @return the result
	 */
	private double[] convertVectorToDoubleArray(@SuppressWarnings("rawtypes") Vector v)
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
	 * Add experimental data in list
	 */
	@SuppressWarnings("unchecked")
	private void add(){
		textArea.selectAll();
		textArea.replaceSelection("");
		//test perechi de numere reale
        String s1=mmAlTf.getText();
        String s2=fmmAlTf.getText();
        double d1=0.0;
        double d2=0.0;
        try
        {
		    d1=Convertor.stringToDouble(s1);
		    d2=Convertor.stringToDouble(s2);
		}
		catch(Exception e)
		{
			String s = resources.getString("number.error");
			textArea.append(s);
			return;
		}

		//fara duplicate dupa x si y ele-s puncte de retea
		//obs e necesar si pentru y pentru metoda de revert-caz specific aici hvl!!!!
        if (nPoints>0)
        {
            double[] xi=convertVectorToDoubleArray(xv);
            double[] yi=convertVectorToDoubleArray(yv);
            for(int i=0; i<nPoints; i++)
            {
				if(xi[i]==d1 || yi[i]==d2)
				{
					String s = resources.getString("number.duplicate.error");
					textArea.append(s);					
		            return;
				}
			}
            
            if (d2>yi[0]){
				String s = resources.getString("number.y.error");
				textArea.append(s);					
	            return;            	            	
            }
		}

        //end test-->la succes se poate merge mai departe
		ListUtilities.add("mmAl: "+d1+" f(mmAl): " +d2,dlm);
		ListUtilities.select(nPoints,puncteLst);
		s1=Convertor.doubleToString(d1);
		s2=Convertor.doubleToString(d2);
		xv.addElement((Object)s1);
		yv.addElement((Object)s2);
		nPoints++;		
		
		statusL.setText("NoPoints: "+nPoints);
		//validate();
	}
	
	/**
	 * Delete data from list
	 */
	private void delete(){
		if(nPoints!=0)
        {        	
        	nPoints--;
//System.out.println("STERGE!");
			int index=ListUtilities.getSelectedIndex(puncteLst);
			ListUtilities.remove(index,dlm);
			ListUtilities.select(nPoints-1,puncteLst);

			xv.removeElementAt(index);
			yv.removeElementAt(index);
			
			statusL.setText("NoPoints: "+nPoints);
		}
		//validate();
	}

	/**
	 * Clear list and reset all variables
	 */
	private void reset(){
		xv.removeAllElements();
        yv.removeAllElements();
        ListUtilities.removeAll(dlm);
        nPoints=0;
        statusL.setText("NoPoints: "+nPoints);
        //validate();
	}

	/**
	 * Based on SRS78 data, check if we have all we need to perform further spectrum calculation.
	 */
	public void validateComputationMode()
	{
		int itemp=0;//double d=0.0;
		saveBoo=true;

		XRay.ICALC=1;
		itemp=ianodCb.getSelectedIndex();
		XRay.ianod=itemp;
		itemp=irippleCb.getSelectedIndex();
		XRay.iripple=itemp;
		String s="";

		if (XRay.ICALC==1)
		{
			if (XRay.ianod==0)//W
			{
				s=(String)uAnodCb.getSelectedItem();
				itemp=Convertor.stringToInt(s);

				if (itemp<6 || itemp>22)
					saveBoo=false;

				s=(String)kvCb.getSelectedItem();
				itemp=Convertor.stringToInt(s);
				int icav=itemp;
				if (itemp<30 || itemp>150)
					saveBoo=false;				

				//allowed ripple
				if (XRay.iripple!=0)
				{
					if (icav!=55 && icav!=60 && icav!=65 && icav!=70 && icav!=75
					&& icav!=80 && icav!=85 && icav!=90)
					{
						saveBoo=false;
					}					

				}

			}
			else//Mo,Rh
			{				
				s=(String)uAnodCb.getSelectedItem();
				itemp=Convertor.stringToInt(s);

				if (itemp<9 || itemp>23)
					saveBoo=false;

				s=(String)kvCb.getSelectedItem();
				itemp=Convertor.stringToInt(s);

				if (itemp<25 || itemp>32)
					saveBoo=false;

				//======================================================
				if (XRay.iripple!=0)
					saveBoo=false;//not allowed anything but ripple 0!!
				//=====================================================
			}
		}

		if (!saveBoo)
		{
		    //String title =resources.getString("dialog.invalidNrPError.title");
			//String message =resources.getString("dialog.ripple");
		    //JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    s = resources.getString("dialog.ripple");
			textArea.append(s);		    
		}

	}
	
	/**
	 * Load and set icon image on frame.
	 * @param URLstr URLstr
	 */
	 private void createImageIcon2(String URLstr)
	  {
			byte[] tmp= new byte[102400];
			int size = 0;
			try
			{
				InputStream is = getClass().getResourceAsStream(URLstr);

				while (is.available() > 0)
				{
					is.read(tmp, size, 1);
					size++;
				}
				is.close();
				byte[] data = new byte[size];
				System.arraycopy(tmp, 0, data, 0, size);
				ImageIcon icon = new ImageIcon(data);
				xrayframe.setIconImage(icon.getImage());
			}
			catch (Exception exc)
			{

			}
	  }
	 
	 /**
	  * Perform HVL tests
	  */
	@SuppressWarnings({ })
	private void performCalculation(){
		KV_toSave=-1;//reset
		textArea.selectAll();
		textArea.replaceSelection("");
		str="";//reset the output string
		boolean neg = false;
		String s="";
		
		//double exposure=1.0;
		//double distance=1.0;
		double knownFiltration=0.0;
		double knownHVL=0.0;
		//double unc =0.0;
		try {
			exposure = Convertor
					.stringToDouble(exposureTf.getText());
			if (exposure < 0)
				neg = true;

			distance = Convertor
			.stringToDouble(distanceTf.getText());
			if (distance < 0)
				neg = true;
			
			unc = Convertor
			.stringToDouble(hvlErrorTf.getText());
			if (unc < 0)
				neg = true;

			minHVL = Convertor
			.stringToDouble(hvlminTf.getText());
			if (minHVL < 0)
				neg = true;
	
			minFiltration = Convertor
			.stringToDouble(filtrationminTf.getText());
			if (minFiltration < 0)
				neg = true;
		} catch (Exception exc) {
			s = resources.getString("number.error");
			textArea.append(s);
			stopComputation();
			return;
		}
		if (neg) {
			s = resources.getString("number.error");
			textArea.append(s);
			stopComputation();
			return;
		}
		
		String knownFiltrationS=filtrareTf.getText();
        if (knownFiltrationS!="")
        {
        	try {
        		knownFiltration = Convertor
    					.stringToDouble(knownFiltrationS);
    			if (knownFiltration < 0)
    				neg = true;

    		} catch (Exception exc) {
    			s = resources.getString("number.error");
    			textArea.append(s);
    			stopComputation();
    			return;
    		}
    		if (neg) {
    			s = resources.getString("number.error");
    			textArea.append(s);
    			stopComputation();
    			return;
    		}     	
        }
        String knownHVLS=hvlTf.getText();
        if (knownHVLS!="")
        {
        	try {
        		knownHVL = Convertor
    					.stringToDouble(knownHVLS);
    			if (knownHVL < 0)
    				neg = true;

    		} catch (Exception exc) {
    			s = resources.getString("number.error");
    			textArea.append(s);
    			stopComputation();
    			return;
    		}
    		if (neg) {
    			s = resources.getString("number.error");
    			textArea.append(s);
    			stopComputation();
    			return;
    		}     	
        }
        
		validateComputationMode();
		if (!saveBoo)//(!isGoodInput)
		{
			stopComputation();
			return;
		}		
        //===========================================================================================================
        s=(String)uAnodCb.getSelectedItem();
        uAnodD=Convertor.stringToDouble(s);
        
        XRay.reset();//HERE WE RESET!!!!!!!!!!!!!!!!!!!!!!
       	XRay.ICALC=1;
       	int is=ianodCb.getSelectedIndex();
       	XRay.ianod=is;
       	s=(String)irippleCb.getSelectedItem();
       	is=Convertor.stringToInt(s);
       	XRay.iripple=is;	
				
		if (mrRb.isSelected())
	    {
           //1mR=0.00876 mGy
           exposure=exposure*0.00876;
		}
		else if(mradRb.isSelected())
		{
			//1mrad=0.01mGy
			exposure=exposure*0.01;
		}
		String label = resources.getString("pleaseWait.label");
		pw = new PleaseWait(label);
		pw.startAnimation();

		label = resources.getString("status.computing");
		statusL.setText(label);
		
		//-------------------------------------------------------------BEGIN COMPUTATION---------------------------------
		//String str ="";
		str = str + "-------- XRay tube settings -------------" + "\n";
		
		//1.Tube settings		
		
		s=(String)kvCb.getSelectedItem();
        kVD=Convertor.stringToDouble(s);
        //System.out.println(""+kVD);
        filtrareD=knownFiltration;
		//=========================
		String filesname="AL";//allways================>EXTERNAL???
		XRay.readAttCoef(filesname,1);//allways================>EXTERNAL???
		XRay.TMM[0]=filtrareD;//allways================>EXTERNAL???
        //===============================================

		str = str + "Anode material: " +ianodCb.getSelectedItem()+
		"; Anode angle [deg.]: "+uAnodD+ "\n"+
		"kV: "+kVD+"; waveform ripple: "+XRay.iripple+"\n";
					
		if(!knownFiltrationS.isEmpty()){
			str = str + "Preset filtration [mmAl]: "+filtrareD+"\n";			
			str=str+"----------------------------------------------------"+"\n";
			
			filtration_toSave=filtrareD;
			filtration_toSave_unc=0.0;

			buildx=new XRaySpectrum(kVD,filtrareD,uAnodD);
			//-------view spectrum---
			String title2=" kv: "+kVD+" mmAl: "+filtrareD+" deg: "+uAnodD;
  	  		xrayframe=new JFrame();
  	  		JPanel content = new JPanel(new BorderLayout());
  	  		JPanel graphP=buildx.getXRayPlotPanel();
  	  		content.add(graphP, BorderLayout.CENTER);
  	  		xrayframe.setContentPane(content);
  	  		content.setOpaque(true); //content panes must be opaque
  	  		xrayframe.pack();

  	  		String title="XRaySpectrum - photons/mAs/mm2 vs. keV";
  	  		xrayframe.setTitle(title+title2);
  	  		FrameUtilities.centerFrameOnScreen(xrayframe);
  	  		JFrame.setDefaultLookAndFeelDecorated(true);
  	  		createImageIcon2(this.resources.getString("form.icon.url"));
  	  		xrayframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  	  		xrayframe.setVisible(true);
  	  		//======================================================
  	  		XRay.computeHVL1("AL",false);//final of interest in mmAl
  	  		XRay.computeHVL2("AL",false);//final of interest in mmAl
  	  		double hvl1t=XRay.HVL1;
  	  		double hvl2t=XRay.HVL2;
			double u1=hvl1t*unc/100.0;
			double u2=hvl2t*unc/100.0;
			str = str + "HVL theor.= "+Convertor.formatNumber(hvl1t,5)+" +/- "+
				Convertor.formatNumber(u1,5)+"\n";
			str = str + "----> other data: HVL2 theor.= "+Convertor.formatNumber(hvl2t,5)+" +/- "+
				Convertor.formatNumber(u2,5)+"\n";
			double u=0.0;
			if(hvl2t!=0.0)
         	  u=Math.sqrt(u1*u1/(hvl2t*hvl2t)+u2*u2*hvl1t*hvl1t/(hvl2t*hvl2t*hvl2t*hvl2t));
			str = str + "----> other data: RHO theor.= "+Convertor.formatNumber(hvl1t/hvl2t,3)+
				" +/- "+Convertor.formatNumber(u,3)+"\n";
			
			HVL_toSave=hvl1t;
			HVL_toSave_unc=u1;
			//==============================================
			str=str+"----------------------------------------------------"+"\n";
			if(!knownHVLS.isEmpty()){
				str = str + "Warning: mAs estimation (if possible) is based on preset HVL. Total filtration is given by the user."+"\n";
				str = str + "Hence, a theoretical HVL was computed. Theoretical HVL may differ from the preset HVL!"+"\n";
				
				double totalFiltration = evaluateTotalFiltrationFromHVL(knownHVL);
				str=str+"----------------------------------------------------"+"\n";
				str = str + "Tube total filtration based on preset HVL [mmAl]= "+Convertor.formatNumber(totalFiltration,5)
				+" +/- "+Convertor.formatNumber(totalFiltration*unc/100.0,5)+"\n";
				str=str+"----------------------------------------------------"+"\n";
				
				//------------
				HVL_toSave=knownHVL;
				HVL_toSave_unc=knownHVL*unc/100.0;;
				//--------------
				filtration_toSave=totalFiltration;
				filtration_toSave_unc=totalFiltration*unc/100.0;
				buildNewSpectrum(totalFiltration);
				str=str+"----------------------------------------------------"+"\n";
			} //else{			
				evaluatemAs();
				
				str=str+"----------------------------------------------------"+"\n";
				KV_toSave=kVD;
				double f_HVL=StatsUtil.evaluateDegreesOfFreedom(HVL_toSave_unc, HVL_toSave);
				double f_filtration=StatsUtil.evaluateDegreesOfFreedom(filtration_toSave_unc, filtration_toSave);
				double compared_unc=minHVL*0.1/100.0;//0.1%
				double f_poisson=StatsUtil.evaluateDegreesOfFreedom(compared_unc,minHVL);
				//double f_poisson=10000.0;//for theor
				boolean diffB = StatsUtil.ttest_default_unc(
						//netCPS, detectionLimit, netCPS_GaussUnc,
						HVL_toSave, minHVL, HVL_toSave_unc,
						//detectionLimitUnc, f_net_gauss,	f_bkg_gauss);
						compared_unc, f_HVL,	f_poisson);
				if (HVL_toSave>=minHVL){
					diffB=false;
				}
				
				String pass="PASSED";
				if (HVL_toSave<minHVL && diffB)
					pass="NOT PASSED";
				str=str+"Final HVL = "+Convertor.formatNumber(HVL_toSave,5)
				+" +/- "+Convertor.formatNumber(HVL_toSave_unc,5)
				+"; Minimum permissible HVL = "+Convertor.formatNumber(minHVL,5)
				+"; Test result (95% confidence level): "+pass
				+"\n";
				HVL_resultS=pass;
				
				compared_unc=minFiltration*0.1/100.0;//0.1%
				f_poisson=StatsUtil.evaluateDegreesOfFreedom(compared_unc,minFiltration);
				diffB = StatsUtil.ttest_default_unc(
						//netCPS, detectionLimit, netCPS_GaussUnc,
						filtration_toSave, minFiltration, filtration_toSave_unc,
						//detectionLimitUnc, f_net_gauss,	f_bkg_gauss);
						compared_unc, f_filtration,	f_poisson);
				if (filtration_toSave>=minFiltration){
					diffB=false;
				}
				
				pass="PASSED";
				if (filtration_toSave<minFiltration && diffB)
					pass="NOT PASSED";
				str=str+"Final filtration = "+Convertor.formatNumber(filtration_toSave,5)
				+" +/- "+Convertor.formatNumber(filtration_toSave_unc,5)
				+"; Minimum permissible filtration = "+Convertor.formatNumber(minFiltration,5)
				+"; Test result (95% confidence level): "+pass
				+"\n";
				filtration_resultS=pass;
				
				textArea.append(str);
				stopComputation();
				label = resources.getString("status.done");
				statusL.setText(label);	
				return;
			//}
		}
		
		str=str+"----------------------------------------------------"+"\n";
		
		if(!knownHVLS.isEmpty()){
			str = str + "NOTE: mAs estimation (if possible) is based on preset HVL."+"\n";
			str = str + "Experimental HVL (if any) is not computed. If experimental HVL is required, then leave the preset HVL field blank!"+"\n";
			
			double totalFiltration = evaluateTotalFiltrationFromHVL(knownHVL);
			str=str+"----------------------------------------------------"+"\n";
			str = str + "Tube total filtration based on preset HVL [mmAl]= "+Convertor.formatNumber(totalFiltration,5)
			+" +/- "+Convertor.formatNumber(totalFiltration*unc/100.0,5)+"\n";
			str=str+"----------------------------------------------------"+"\n";
			
			//------------
			HVL_toSave=knownHVL;
			HVL_toSave_unc=knownHVL*unc/100.0;;
			//--------------
			filtration_toSave=totalFiltration;
			filtration_toSave_unc=totalFiltration*unc/100.0;
			buildNewSpectrum(totalFiltration);
			str=str+"----------------------------------------------------"+"\n";
			
			evaluatemAs();

			str=str+"----------------------------------------------------"+"\n";
			KV_toSave=kVD;
			double f_HVL=StatsUtil.evaluateDegreesOfFreedom(HVL_toSave_unc, HVL_toSave);
			double f_filtration=StatsUtil.evaluateDegreesOfFreedom(filtration_toSave_unc, filtration_toSave);
			double compared_unc=minHVL*0.1/100.0;//0.1%
			double f_poisson=StatsUtil.evaluateDegreesOfFreedom(compared_unc,minHVL);
			//double f_poisson=10000.0;//for theor
			boolean diffB = StatsUtil.ttest_default_unc(
					//netCPS, detectionLimit, netCPS_GaussUnc,
					HVL_toSave, minHVL, HVL_toSave_unc,
					//detectionLimitUnc, f_net_gauss,	f_bkg_gauss);
					compared_unc, f_HVL,	f_poisson);
			if (HVL_toSave>=minHVL){
				diffB=false;
			}
			
			String pass="PASSED";
			if (HVL_toSave<minHVL && diffB)
				pass="NOT PASSED";
			str=str+"Final HVL = "+Convertor.formatNumber(HVL_toSave,5)
			+" +/- "+Convertor.formatNumber(HVL_toSave_unc,5)
			+"; Minimum permissible HVL = "+Convertor.formatNumber(minHVL,5)
			+"; Test result (95% confidence level): "+pass
			+"\n";
			HVL_resultS=pass;
			
			compared_unc=minFiltration*0.1/100.0;//0.1%
			f_poisson=StatsUtil.evaluateDegreesOfFreedom(compared_unc,minFiltration);
			diffB = StatsUtil.ttest_default_unc(
					//netCPS, detectionLimit, netCPS_GaussUnc,
					filtration_toSave, minFiltration, filtration_toSave_unc,
					//detectionLimitUnc, f_net_gauss,	f_bkg_gauss);
					compared_unc, f_filtration,	f_poisson);
			if (filtration_toSave>=minFiltration){
				diffB=false;
			}
			
			pass="PASSED";
			if (filtration_toSave<minFiltration && diffB)
				pass="NOT PASSED";
			str=str+"Final filtration = "+Convertor.formatNumber(filtration_toSave,5)
			+" +/- "+Convertor.formatNumber(filtration_toSave_unc,5)
			+"; Minimum permissible filtration = "+Convertor.formatNumber(minFiltration,5)
			+"; Test result (95% confidence level): "+pass
			+"\n";
			filtration_resultS=pass;
			
			textArea.append(str);
			stopComputation();
			label = resources.getString("status.done");
			statusL.setText(label);	
			return;
		} 			
			
		//2.HVL experimental
		
		x=convertVectorToDoubleArray(xv);//mmAl absorber for HVL measurements 
		y=convertVectorToDoubleArray(yv);//function of mmAl such as dose or exposure
        int ndata=x.length;        
        if (ndata<3)
        {		    
		    s = resources.getString("dialog.invalidNrP.message");
			textArea.append(s);
			stopComputation();
			return;
		}       
        
        //first sort array up by x series!
        Sort.qSort2(x,y);
        double ymax = Sort.findValue(y,1);
        //========================
		double[] a=new double[ndata];
		double[] b=new double[ndata];
		double[] c=new double[ndata];
		double[] d=new double[ndata];
		Interpolator.set_spline(y, x, a, b, c, d, ndata);//spline revert
		double hvl1e=Interpolator.spline(ymax/2.0,y,a,b,c,d,ndata);
		double hvl2e=Interpolator.spline(ymax/4.0,y,a,b,c,d,ndata);
		//-------------
		if(ymax/4.0<y[y.length-1]){
			//not sufficient data but try the best it can
			double q=ymax/4.0;//a,b,c,d=interval=>real data n-2!!
			hvl2e = a[y.length-2] + q
			* (b[y.length-2] + q * (c[y.length-2] + q * d[y.length-2]));
		}		
		//----------------------
		hvl2e = hvl2e - hvl1e;// QVL-HVL1!!!
		double u1=hvl1e*unc/100.0;
		double u2=hvl2e*unc/100.0;
		double u=0.0;
		if(hvl2e!=0.0)
            u=Math.sqrt(u1*u1/(hvl2e*hvl2e)+u2*u2*hvl1e*hvl1e/(hvl2e*hvl2e*hvl2e*hvl2e));

		str=str+"----------------Measured HVL---------------------"+"\n";
		str = str + "HVL meas.= "+Convertor.formatNumber(hvl1e,5)
		 +" +/- "+Convertor.formatNumber(u1,5)+"\n";
		str = str + "----> other data: HVL2 meas.= "+Convertor.formatNumber(hvl2e,5)
		 +" +/- "+Convertor.formatNumber(u2,5)+"\n";
		str = str + "----> other data: RHO meas.= "+Convertor.formatNumber(hvl1e/hvl2e,3)
		 +" +/- "+Convertor.formatNumber(u,3)+"\n";
		str=str+"----------------------------------------------------"+"\n";
		
		HVL_toSave=hvl1e;
		HVL_toSave_unc=u1;
		//3. Compute new spectrum based on HVL measurement		
		
		double totalFiltration = evaluateTotalFiltrationFromHVL(hvl1e);
		str=str+"----------------------------------------------------"+"\n";
		str = str + "Tube total filtration based on HVL measurement [mmAl]= "+Convertor.formatNumber(totalFiltration,5)
		+" +/- "+Convertor.formatNumber(totalFiltration*unc/100.0,5)+"\n";
		str=str+"----------------------------------------------------"+"\n";
		
		filtration_toSave=totalFiltration;
		filtration_toSave_unc=totalFiltration*unc/100.0;
		//4. Build and view new spectrum having total filtration computed above	
		
		buildNewSpectrum(totalFiltration);
		str=str+"----------------------------------------------------"+"\n";
		//5. Evaluate mAs
		
		evaluatemAs();	
		
		//=========compare
		str=str+"----------------------------------------------------"+"\n";
		KV_toSave=kVD;
		double f_HVL=StatsUtil.evaluateDegreesOfFreedom(HVL_toSave_unc, HVL_toSave);
		double f_filtration=StatsUtil.evaluateDegreesOfFreedom(filtration_toSave_unc, filtration_toSave);
		double compared_unc=minHVL*0.1/100.0;//0.1%
		double f_poisson=StatsUtil.evaluateDegreesOfFreedom(compared_unc,minHVL);
		//double f_poisson=10000.0;//for theor
		boolean diffB = StatsUtil.ttest_default_unc(
				//netCPS, detectionLimit, netCPS_GaussUnc,
				HVL_toSave, minHVL, HVL_toSave_unc,
				//detectionLimitUnc, f_net_gauss,	f_bkg_gauss);
				compared_unc, f_HVL,	f_poisson);
		if (HVL_toSave>=minHVL){
			diffB=false;
		}
		
		String pass="PASSED";
		if (HVL_toSave<minHVL && diffB)
			pass="NOT PASSED";
		str=str+"Final HVL = "+Convertor.formatNumber(HVL_toSave,5)
		+" +/- "+Convertor.formatNumber(HVL_toSave_unc,5)
		+"; Minimum permissible HVL = "+Convertor.formatNumber(minHVL,5)
		+"; Test result (95% confidence level): "+pass
		+"\n";
		HVL_resultS=pass;
		
		compared_unc=minFiltration*0.1/100.0;//0.1%
		f_poisson=StatsUtil.evaluateDegreesOfFreedom(compared_unc,minFiltration);
		diffB = StatsUtil.ttest_default_unc(
				//netCPS, detectionLimit, netCPS_GaussUnc,
				filtration_toSave, minFiltration, filtration_toSave_unc,
				//detectionLimitUnc, f_net_gauss,	f_bkg_gauss);
				compared_unc, f_filtration,	f_poisson);
		if (filtration_toSave>=minFiltration){
			diffB=false;
		}
		
		pass="PASSED";
		if (filtration_toSave<minFiltration && diffB)
			pass="NOT PASSED";
		str=str+"Final filtration = "+Convertor.formatNumber(filtration_toSave,5)
		+" +/- "+Convertor.formatNumber(filtration_toSave_unc,5)
		+"; Minimum permissible filtration = "+Convertor.formatNumber(minFiltration,5)
		+"; Test result (95% confidence level): "+pass
		+"\n";
		filtration_resultS=pass;
		//====================
		textArea.append(str);
		
		//6. View HVL graphics
		
		int ni=ndata;
		double[] pas = new double[ni-1];
		for (int i=0; i<=ni-2; i++)
		    pas[i]=Math.abs((y[i+1]-y[i])/5.0);
		//pasul cel mai mic:
		double pasul=Sort.findValue(pas,ni-1);
		//necesita sortare crescatoare->de cate ori incape acest pas
		double dbl =Math.abs((y[y.length-1]-y[0]))/pasul;
		long j = Math.round(dbl);
		int n = 0;
		//partea intreaga a unui numar zecimal!!!
		if (j>dbl)
		   n=(int)j-1;
	    else
	       n=(int)j;//exemplu=10<->10*pasul acopera tot intervalul

	    xs=new double[n+1];
		ys=new double[n+1];
		xs[0]=y[0];
		ys[0]=x[0];
		
		for(int i=1; i<=n; i++){
		   xs[i]=y[0]-i*pasul;//fmmal
		   ys[i]=Interpolator.spline(xs[i],y,a,b,c,d,ndata);//mmAl
		}
		
		//=========================================
		new HvlGraphics(this);
		
		// 6. The end
		stopComputation();
		label = resources.getString("status.done");
		statusL.setText(label);	
	}
	
	/**
	 * Estimate mAs
	 */
	private void evaluatemAs(){
		double dist75cm=75.0;
		double airKermaAt75cm=1.0E-03*XRay.KERMAPERMASAT750MM;//buildx.getAirKerma();//mGy/mAs, default is uGy/mAs
		
		str = str + "Normalized air kerma at 75 cm [mGy/mAs] = "+Convertor.formatNumber(airKermaAt75cm,5)+"\n";
		str = str + "Exposure at distance "+distance+" cm in [mGy] = "+Convertor.formatNumber(exposure,5)+"\n";
		
		double airKermaAtDistance=0.0;//exposure at distance is already converted in mGy
		double mAs=0.0;
		if (distance!=0.0){
			airKermaAtDistance=airKermaAt75cm*dist75cm*dist75cm/(distance*distance);//mGy/mAs
			
			str = str + "Normalized air kerma at distance "+distance+" cm in [mGy/mAs] = "+Convertor.formatNumber(airKermaAtDistance,5)+"\n";
			
			mAs=exposure/airKermaAtDistance;//a mgy/mAs = b mgy/ x mAs => x=b/a
			
			str=str+"----------------------------------------------------"+"\n";
			str = str + "Estimated mAs = "+Convertor.formatNumber(mAs,5)
			+" +/- "+Convertor.formatNumber(mAs*unc/100.0,5)+"\n";
			str=str+"----------------------------------------------------"+"\n";
			
			str = str + "Note: If exposure can not be measured and mAs is well known, one can compute exposure at any distance "+"\n";
			str = str + "by multiplying mAs with normalized air kerma at that distance. More, if x-ray field dimensions (widh and height) "+"\n";
			str = str + "are known, one can calculate dose area product (DAP) by muliplying field dimensions with exposure!"+"\n";
		}		
	}
	
	/**
	 * Evaluate total filtration from HVL
	 * @param hvl1e HVL
	 * @return the result
	 */
	private double evaluateTotalFiltrationFromHVL(double hvl1e){
		
		filtrareD=0.0;//No filtration..it will be determined.
		
		XRay.reset();//HERE WE RESET!Necessary if preset filtration is present and already have a spectrum
       	
		XRay.ICALC=1;
       	
       	int is=ianodCb.getSelectedIndex();
       	XRay.ianod=is;
       	String s=(String)irippleCb.getSelectedItem();
       	is=Convertor.stringToInt(s);
       	XRay.iripple=is;
       	
		String filename="AL";//allways
		XRay.readAttCoef(filename,1);//allways
		XRay.TMM[0]=filtrareD;//allways
       	
       	int kvint=(new Double(kVD)).intValue();//make sure is an int
    	String ikv = Convertor.intToString(kvint);
    	String kvS = "";
    	if (kVD < 100.0)
    		kvS = "0" + ikv;
    	else
    		kvS = ikv;
    	
		int ianod_file = (new Double(uAnodD)).intValue();
		String uanodS = "";
		if (uAnodD >= 10) {
			if (XRay.ianod == 0)
				uanodS = ianod_file + "0";
			else if (XRay.ianod == 1)
				uanodS = ianod_file + "1";
			else if (XRay.ianod == 2)
				uanodS = ianod_file + "2";
		} else {
			if (XRay.ianod == 0)
				uanodS = "0" + ianod_file + "0";
			else if (XRay.ianod == 1)
				uanodS = "0" + ianod_file + "1";
			else if (XRay.ianod == 2)
				uanodS = "0" + ianod_file + "2";
		}
		
		XRay.KVP = kVD;
		XRay.ianod_file = ianod_file;
		
		filename = kvS + uanodS;	
		XRay.readSpectrum(filename);//here ripple is taken into account
		
		filename = "KERMAIR";// allways
		XRay.readKerma(filename);	
       	
		XRay.buildSpectra();
		
		XRay.computeHVL1("AL", false);//in order to compute kerma in air required for total filtration
		
		double totalFiltration = XRay.computeFiltrationFromHVL1(hvl1e);
		return totalFiltration;
	}
	
	/**
	 * Build a new XRay spectrum
	 * @param totalFiltration totalFiltration
	 */
	private void buildNewSpectrum(double totalFiltration){
		filtrareD=totalFiltration;
		XRay.reset();
		
		XRay.ICALC=1;
       	
       	int is=ianodCb.getSelectedIndex();
       	XRay.ianod=is;
       	String s=(String)irippleCb.getSelectedItem();
       	is=Convertor.stringToInt(s);
       	XRay.iripple=is;
       	
		String filename="AL";//allways
		XRay.readAttCoef(filename,1);//allways
		XRay.TMM[0]=filtrareD;//allways
		
		str = str + "New spectrum based on computed tube total filtration"+"\n";
		//XRaySpectrum buildx=new XRaySpectrum(kVD,filtrareD,uAnodD);
		buildx=new XRaySpectrum(kVD,filtrareD,uAnodD);
		//-------view spectrum---
		String title2=" kv: "+kVD+" mmAl: "+filtrareD+" deg: "+uAnodD;
	  	xrayframe2=new JFrame();
	  	JPanel content = new JPanel(new BorderLayout());
	  	JPanel graphP=buildx.getXRayPlotPanel();
	  	content.add(graphP, BorderLayout.CENTER);
	  	xrayframe2.setContentPane(content);
	  	content.setOpaque(true); //content panes must be opaque
	  	xrayframe2.pack();

	  	String title="XRaySpectrum - photons/mAs/mm2 vs. keV";
	  	xrayframe2.setTitle(title+title2);
	  	FrameUtilities.centerFrameOnScreen(xrayframe2);
	  	JFrame.setDefaultLookAndFeelDecorated(true);
	  	createImageIcon2(this.resources.getString("form.icon.url"));
	  	xrayframe2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	  	xrayframe2.setVisible(true);
	  	//======================================================
	  	XRay.computeHVL1("AL",false);//final of interest in mmAl
	  	XRay.computeHVL2("AL",false);//final of interest in mmAl
	  	double hvl1t=XRay.HVL1;
	  	double hvl2t=XRay.HVL2;
		double u1=hvl1t*unc/100.0;
		double u2=hvl2t*unc/100.0;
		str = str + "HVL theor. for new spectrum= "+Convertor.formatNumber(hvl1t,5)+" +/- "+
			Convertor.formatNumber(u1,5)+"\n";
		str = str + "----> other data: HVL2 theor. for new spectrum= "+Convertor.formatNumber(hvl2t,5)+" +/- "+
			Convertor.formatNumber(u2,5)+"\n";
		double u=0.0;
		if(hvl2t!=0.0)
     	  u=Math.sqrt(u1*u1/(hvl2t*hvl2t)+u2*u2*hvl1t*hvl1t/(hvl2t*hvl2t*hvl2t*hvl2t));
		str = str + "----> other data: RHO theor. for new spectrum= "+Convertor.formatNumber(hvl1t/hvl2t,3)+
			" +/- "+Convertor.formatNumber(u,3)+"\n";
		
		HVL_toSave=hvl1t;
		HVL_toSave_unc=u1;
	}
	
	/**
	 * Set limits for HVL tests.
	 */
	private void setLimits(){
		boolean neg = false;
		String s="";
		try {
			
			minHVL = Convertor
			.stringToDouble(hvlminTf.getText());
			if (minHVL < 0)
				neg = true;
	
			minFiltration = Convertor
			.stringToDouble(filtrationminTf.getText());
			if (minFiltration < 0)
				neg = true;
		} catch (Exception exc) {
			s = resources.getString("number.error");
			textArea.append(s);
			stopComputation();
			return;
		}
		if (neg) {
			s = resources.getString("number.error");
			textArea.append(s);
			stopComputation();
			return;
		}
		
		try {
			
			// prepare db query data
			String datas = mf.resources.getString("data.load");
			String currentDir = System.getProperty("user.dir");
			String file_sep = System.getProperty("file.separator");
			String opens = currentDir + file_sep + datas;
			String dbName = mainDB;
			opens = opens + file_sep + dbName;
			// make a connection
			DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
			Connection con1 = DatabaseAgent.getConnection(opens, "", "");//DBConnection.getDerbyConnection(opens, "", "");
						
			PreparedStatement psUpdate = null;
			psUpdate = con1.prepareStatement("update "
					+ mf.hvlFiltrationLimitsTable + " set HVL_min=?," +
							"Filtration_min=?" +
							" where ID=?");

			psUpdate.setString(1, Convertor.doubleToString(minHVL));
			psUpdate.setString(2, Convertor.doubleToString(minFiltration));									
			psUpdate.setInt(3, 1);
			psUpdate.executeUpdate();
			
			//---------
			if (psUpdate != null)
				psUpdate.close();
			if (con1 != null)
				con1.close();
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
}
