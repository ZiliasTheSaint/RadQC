package kvt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import danfulea.db.DatabaseAgent;
//import jdf.db.DBConnection;
import danfulea.math.Convertor;
import danfulea.math.numerical.Stats;
import danfulea.utils.FrameUtilities;
import danfulea.misc.BoxLayout2;
import danfulea.misc.VerticalFlowLayout;
import danfulea.utils.ListUtilities;
import radQC.RadQCFrame;

/**
 * Peak kilovoltage (KVP) and exposure TIME QC tests! <br>
 * 
 * 
 * @author Dan Fulea, 29 Apr. 2015
 */
@SuppressWarnings("serial")
public class KVTFrame extends JFrame implements ActionListener{
	private static final Color c =Color.BLACK;
	protected static final Border LINE_BORDER = BorderFactory.createLineBorder(c,2);
	protected static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(2,2,2,2);
	
	private RadQCFrame mf;
	private static final String BASE_RESOURCE_CLASS = "kvt.resources.KVTFrameResources";
	protected ResourceBundle resources;
	private static final Dimension PREFERRED_SIZE = new Dimension(800, 720);
	private static final Dimension sizeTab = new Dimension(250,150);
	private static final Dimension sizeLst = new Dimension(253,125);
	  
	private static final String ADDKV_COMMAND = "ADDKV";
	private static final String CALCKV_COMMAND = "CALCKV";
	private static final String DELKV_COMMAND = "DELKV";
	private static final String RESETKV_COMMAND = "RESETKV";
	private static final String ADDTEXP_COMMAND = "ADDTEXP";
	private static final String CALCTEXP_COMMAND = "CALCTEXP";
	private static final String DELTEXP_COMMAND = "DELTEXP";
	private static final String RESETTEXP_COMMAND = "RESETTEXP";
	
	private static final String ADDROWTEXP_COMMAND = "ADDROWTEXP";
	private static final String DELROWTEXP_COMMAND = "DELROWTEXP";
	private static final String CALCTEXPAC_COMMAND = "CALCTEXPAC";
	
	private static final String ADDROWKV_COMMAND = "ADDROWKV";
	private static final String DELROWKV_COMMAND = "DELROWKV";
	private static final String CALCKVAC_COMMAND = "CALCKVAC";
	
	private static final String SAVE_COMMAND = "SAVE";
	private String command = null;
	//-------------------
	protected boolean isOktoSaveKvRepeatability=false;
	protected boolean isOktoSaveTexpRepeatability=false;
	protected boolean isOktoSaveKvAccuracy=false;
	protected boolean isOktoSaveTexpAccuracy=false;
	//---------------
	protected String measurementDate_toSave="";
	protected String mainDB = "";
	protected String kvRepeatabilityTable="";
	protected String kvRepeatabilityTableDetail="";
	protected String tRepeatabilityTable="";
	protected String tRepeatabilityTableDetail="";
	protected String kvAccuracyTable="";
	protected String tAccuracyTable="";
    protected int IDLINK=0;
	//--------------
	protected JTextArea kvrepTa = new JTextArea();
	protected JTextArea texprepTa = new JTextArea();
	protected JTextArea kvacTa = new JTextArea();
	protected JTextArea texpacTa = new JTextArea();

	@SuppressWarnings("rawtypes")
	protected DefaultListModel kvdlm=new DefaultListModel() ;
	@SuppressWarnings("rawtypes")
	protected DefaultListModel texpdlm=new DefaultListModel() ;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JList kvmL = new JList(kvdlm);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected JList texpL = new JList(texpdlm);
	
	protected SortableTablePanel kvacPan;
	protected SortableTablePanel1 texpacPan;
	protected JPanel suportkvacPan = new JPanel();
	protected JPanel suporttexpacPan = new JPanel();
	private String[] names;
	@SuppressWarnings({ })
	private Vector<String> colNames = new Vector<String>();

	protected JTextField setkvTf = new JTextField(5);
	protected JTextField cvmpTf = new JTextField(5);
	protected JTextField maskvTf = new JTextField(6);
	protected JTextField settexpTf = new JTextField(5);
	protected JTextField cvmpTf1 = new JTextField(5);
	protected JTextField mastexpTf = new JTextField(6);
	protected JTextField kvacTf = new JTextField(5);
	protected JTextField texpacTf = new JTextField(5);

	 //---reprod main variables
	private int nKvPoints=0;
    private int nTexpPoints=0;
	@SuppressWarnings({ "rawtypes" })
	private Vector kvv=new Vector();
	@SuppressWarnings({ "rawtypes" })
	private Vector texpv=new Vector();
	protected double[] kvd=new double[0];
	protected double[] texpd=new double[0];
	protected double texpcv=0.0;
	protected double kvcv=0.0;
	protected double texpcvm=0.0;
	protected double kvcvm=0.0;
	protected boolean texpcvb=false;
	protected boolean kvcvb=false;
	protected double kvset=0.0;
	protected double texpset=0.0;
	  //---acuracy main variables
	protected boolean[] kvaccvb=new boolean[0];//false;//boolean for acceptance
	protected boolean[] texpaccvb=new boolean[0];//false;
	protected double kvaccvm=0.0;//maximum permitted
	protected double texpaccvm=0.0;
	@SuppressWarnings({ "rawtypes" })
	private Vector kvsacv=new Vector();
	@SuppressWarnings({ "rawtypes" })
	private Vector texpsacv=new Vector();
	@SuppressWarnings({ "rawtypes" })
	private Vector kvmacv=new Vector();
	@SuppressWarnings({ "rawtypes" })
	private Vector texpmacv=new Vector();
	@SuppressWarnings({ "rawtypes" })
	private Vector cvkvacv=new Vector();//for acceptance series
	@SuppressWarnings({ "rawtypes" })
	private Vector cvtexpacv=new Vector();//for acceptance series
	protected double[] kvsacd=new double[0];
	protected double[] texpsacd=new double[0];
	protected double[] kvmacd=new double[0];
	protected double[] texpmacd=new double[0];
	protected double[] cvkvacd=new double[0];//coresponding double->raport
	protected double[] cvtexpacd=new double[0];//coresponding double->raport

	//GUI_Layout
	private JTabbedPane jTabbedPane1 = new JTabbedPane();
	private FlowLayout flowLayout2 = new FlowLayout();
	private FlowLayout flowLayout3 = new FlowLayout();
	private FlowLayout flowLayout4 = new FlowLayout();
	private FlowLayout flowLayout5 = new FlowLayout();
	private FlowLayout flowLayout6 = new FlowLayout();
	private FlowLayout flowLayout7 = new FlowLayout();
	private FlowLayout flowLayout8 = new FlowLayout();
	private FlowLayout flowLayout9 = new FlowLayout();
	private BoxLayout2 boxLayout23 = new BoxLayout2();
	private BoxLayout2 boxLayout22 = new BoxLayout2();
	private BoxLayout2 boxLayout21 = new BoxLayout2();
	private BoxLayout2 boxLayout25 = new BoxLayout2();
	private BoxLayout2 boxLayout24 = new BoxLayout2();
	private BoxLayout2 boxLayout26 = new BoxLayout2();
	private BorderLayout borderLayout2 = new BorderLayout();
	private BorderLayout borderLayout4 = new BorderLayout();
	private BorderLayout borderLayout6 = new BorderLayout();
	private BorderLayout borderLayout7 = new BorderLayout();
	private BorderLayout borderLayout8 = new BorderLayout();
	private BorderLayout borderLayout9 = new BorderLayout();
	private BorderLayout borderLayout10 = new BorderLayout();
	private BorderLayout borderLayout11 = new BorderLayout();
	private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
	private VerticalFlowLayout verticalFlowLayout2 = new VerticalFlowLayout();
	private TitledBorder titledBorder1;
	private TitledBorder titledBorder2;
	//-----------------------------------------------------------------------
	private JPanel lstopP = new JPanel();
	private JPanel texpP = new JPanel();
	private JPanel mainP = new JPanel();
	private JPanel lstmasP = new JPanel();
	private JPanel resultKvP = new JPanel();
	private JPanel kvP = new JPanel();
	private JPanel setKvP = new JPanel();
	private JPanel dataKvP = new JPanel();
	private JPanel masuratP = new JPanel();
	private JPanel mainAcP = new JPanel();
	private JPanel jPanel1 = new JPanel();
	private JPanel texpacP = new JPanel();
	private JPanel resulttexpP = new JPanel();
	private JPanel setTexpP = new JPanel();
	private JPanel lstmasP1 = new JPanel();
	private JPanel dataTexpP = new JPanel();
	private JPanel lstopP1 = new JPanel();
	private JPanel masuratP1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	//private JPanel jPanel4 = new JPanel();
	private JPanel jPanel5 = new JPanel();
	private JPanel jPanel6 = new JPanel();
	private JPanel jPanel7 = new JPanel();
	private JPanel jPanel8 = new JPanel();
	//private JPanel jPanel9 = new JPanel();
	private JPanel jPanel10 = new JPanel();
	private JPanel jPanel11 = new JPanel();
	//-------------------------------------------------------------------------
	private JLabel cvmpL = new JLabel();
	private JLabel setKvL = new JLabel();
	private JLabel maskvL = new JLabel();
	private JLabel setTexpL = new JLabel();
	private JLabel cvmpL1 = new JLabel();
	private JLabel masTexpL = new JLabel();
	private JLabel jLabel1 = new JLabel();
	private JLabel jLabel2 = new JLabel();
	//-------------------------------------------------------------
	private JScrollPane listSp = new JScrollPane();
	private JScrollPane listSp1 = new JScrollPane();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JScrollPane jScrollPane2 = new JScrollPane();
	private JScrollPane jScrollPane3 = new JScrollPane();
	//private JScrollPane jScrollPane4 = new JScrollPane();
	//private JScrollPane jScrollPane5 = new JScrollPane();
	private JScrollPane jScrollPane6 = new JScrollPane();
	//============end GUI layout
	private Connection radqcdbcon = null;
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public KVTFrame(RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("KVTFrame.NAME"));
		//DBConnection.startDerby();//just in case is closed
		//===============
		mainDB=mf.radqcDB;
		kvRepeatabilityTable=mf.kvRepeatabilityTable;
		kvRepeatabilityTableDetail=mf.kvRepeatabilityTableDetail;
		tRepeatabilityTable=mf.tRepeatabilityTable;
		tRepeatabilityTableDetail=mf.tRepeatabilityTableDetail;
		kvAccuracyTable=mf.kvAccuracyTable;
		tAccuracyTable=mf.tAccuracyTable;
    	measurementDate_toSave=mf.measurementDate;
    	IDLINK=mf.IDLINK;
		//=====================================
    	DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		//some init tables
	    names = (String[])resources.getObject("tab.columns");
	    for (int i=0; i<names.length; i++)
	    	colNames.addElement(names[i]);

	    Object[][] datakv =
	    {
		    {"60"," "},
		    {"70"," "},
		    {"80"," "},
		    {"100"," "},
		    {"120"," "},
	    };
	    
	    Object[][] datakvMammo=null;
	    if (mf.EXAMINATION_ID==1){
	    	datakvMammo = new Object[5][2];
	    	datakvMammo[0][0]="25";datakvMammo[0][1]=" ";
	    	datakvMammo[1][0]="27";datakvMammo[1][1]=" ";
	    	datakvMammo[2][0]="29";datakvMammo[2][1]=" ";
	    	datakvMammo[3][0]="30";datakvMammo[3][1]=" ";
	    	datakvMammo[4][0]="32";datakvMammo[4][1]=" ";
		    //{
			  //  {"60"," "},
			   // {"70"," "},
			    //{"80"," "},
			    //{"100"," "},
			    //{"120"," "},
		    //};
	    }
	    final Object[][] datatexp =
	    {
		    {" "," "},
		    {" "," "},
		    {" "," "},
		    {" "," "},
		    {" "," "},
	    };

	    if (mf.EXAMINATION_ID==1){
	    	kvacPan=new SortableTablePanel(datakvMammo,names);
	    }else
		 kvacPan=new SortableTablePanel(datakv,names);
		
	    texpacPan=new SortableTablePanel1(datatexp,names);
	    //--end init tables
		//performQueryDb();		
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
        //toolBar.setBackground(RadQCFrame.bkgColor);
        
		JTabbedPane mainPanel = createMainPanel();
		
		content.add(toolBar, BorderLayout.PAGE_START);
		content.add(mainPanel, BorderLayout.CENTER);
		//content.add(statusBar, BorderLayout.PAGE_END);

		//setContentPane(content);//new JScrollPane(content));
		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
		
		//selection in tables
        JTable tab = kvacPan.getTab();
		int rowTableCount=tab.getRowCount();
		tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);

		tab = texpacPan.getTab();
		rowTableCount=tab.getRowCount();
		tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);
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

	    //String buttonName=resources.getString("toolBar.saveBD");
	    //String buttonToolTip=resources.getString("toolBar.saveBD.toolTip");
	    //String buttonIconName=resources.getString("toolBar.saveBD.iconName.url");
	    //button = makeNavigationButton(buttonIconName,SAVEBD,buttonToolTip,buttonName);
	    //Character mnemonic=(Character) resources.getObject("toolBar.saveBD.mnemonic");
	    //button.setMnemonic(mnemonic.charValue());
	     
	    String buttonName = resources.getString("saveB");
	    String buttonToolTip = resources.getString("saveB.toolTip");
	    String buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, SAVE_COMMAND,
					buttonToolTip, buttonName, this, this);
		Character mnemonic = (Character) resources.getObject("saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
			
	    toolP.add(button);
	    //toolP.setBackground(RadQCFrame.bkgColor); 
	     //if(mode==0)
	     //  button.setEnabled(false);
/*
	        buttonName=resources.getString("toolBar.bd");
	        buttonToolTip=resources.getString("toolBar.bd.toolTip");
	        buttonIconName=resources.getString("toolBar.bd.iconName.url");
	        button = makeNavigationButton(buttonIconName,VIEWBD,buttonToolTip,buttonName);
	        mnemonic=(Character) resources.getObject("toolBar.bd.mnemonic");
			button.setMnemonic(mnemonic.charValue());
	        toolP.add(button);
	        if(mode==0)
	            button.setEnabled(false);
	        JSeparator sep=new JSeparator();
	        toolP.add(sep);
	        JSeparator sep2=new JSeparator();
	        toolP.add(sep2);
	        JSeparator sep3=new JSeparator();
	        toolP.add(sep3);

	        buttonName=resources.getString("toolBar.raport");
	        buttonToolTip=resources.getString("toolBar.raport.toolTip");
	        buttonIconName=resources.getString("toolBar.raport.iconName.url");
	        button = makeNavigationButton(buttonIconName,RAPORT,buttonToolTip,buttonName);
	        mnemonic=(Character) resources.getObject("toolBar.raport.mnemonic");
			button.setMnemonic(mnemonic.charValue());
	        toolP.add(button);

	        buttonName=resources.getString("toolBar.exit");
	        buttonToolTip=resources.getString("toolBar.exit.toolTip");
	        buttonIconName=resources.getString("toolBar.exit.iconName.url");
	        button = makeNavigationButton(buttonIconName,EXIT,buttonToolTip,buttonName);
	        mnemonic=(Character) resources.getObject("toolBar.exit.mnemonic");
			button.setMnemonic(mnemonic.charValue());
	        toolP.add(button);
	        //toolP.setBorder(getGroupBoxBorder(""));
*/
	     //toolP.setBorder(getGroupBoxBorder(""));
	     toolBar.add(toolP) ;
	  }
	
	/**
	 * Create main panel
	 * @return the result
	 */
	private JTabbedPane createMainPanel() {
		titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(Color.black,2),
                resources.getString("kvp.border2"));
		titledBorder2 = new TitledBorder(BorderFactory.createLineBorder(Color.black,2),
                resources.getString("texp.border2"));
		kvP.setBorder(BorderFactory.createTitledBorder(LINE_BORDER,
				resources.getString("kvp.border")));
		setKvP.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
		setKvL.setText(resources.getString("kvp.setKvL"));
		cvmpL.setText(resources.getString("kvp.cvmpL"));
		dataKvP.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
	    maskvL.setText(resources.getString("kvp.maskvL"));

	    kvrepTa.setCaretPosition(0);
	    kvrepTa.setEditable(false);
	    kvrepTa.setText(resources.getString("reprod.rezultat"));
	    kvrepTa.setLineWrap(true);
	    kvrepTa.setWrapStyleWord(true);
	    resultKvP.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
	    texpP.setBorder(BorderFactory.createTitledBorder(LINE_BORDER,
	        resources.getString("texp.border")));
	    setTexpP.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));

	    setTexpL.setText(resources.getString("texp.setTexpL"));
	    cvmpL1.setText(resources.getString("texp.cvmpL"));

	    masTexpL.setText(resources.getString("texp.mastexpL"));
	    
	    texprepTa.setWrapStyleWord(true);
	    texprepTa.setLineWrap(true);
	    texprepTa.setText(resources.getString("reprod.rezultat"));
	    texprepTa.setEditable(false);
	    texprepTa.setCaretPosition(0);
	    /*
	    mnemonic=(Character) ra.resources.getObject("kvac.calcB.mnemonic");
	    ra.kvaccalcB.setMnemonic(mnemonic.charValue());
	    ra.kvaccalcB.setText(ra.resources.getString("kvac.calcB"));
	    mnemonic=(Character) ra.resources.getObject("kvac.delrB.mnemonic");
	    ra.kvacdelrB.setMnemonic(mnemonic.charValue());
	    ra.kvacdelrB.setText(ra.resources.getString("kvac.delrB"));
	    mnemonic=(Character) ra.resources.getObject("kvac.addrB.mnemonic");
	    ra.kvacaddrB.setMnemonic(mnemonic.charValue());
	    ra.kvacaddrB.setText(ra.resources.getString("kvac.addrB"));*/
	    jLabel1.setText(resources.getString("ac.label"));
	    
	    suportkvacPan.setPreferredSize(sizeTab);
	    suporttexpacPan.setPreferredSize(sizeTab);  
	    
	    jLabel2.setText(resources.getString("ac.label"));
	    	    
	    jPanel1.setBorder(titledBorder1);
	    texpacP.setBorder(titledBorder2);
	    jPanel2.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
	    jPanel3.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
	    jPanel10.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
	    jPanel11.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
	    kvacTa.setWrapStyleWord(true);
	    kvacTa.setLineWrap(true);
	    kvacTa.setText(resources.getString("reprod.rezultat"));
	    kvacTa.setEditable(false);
	    kvacTa.setCaretPosition(0);
	    texpacTa.setWrapStyleWord(true);
		texpacTa.setLineWrap(true);
		texpacTa.setText(resources.getString("reprod.rezultat"));
		texpacTa.setEditable(false);
	    texpacTa.setCaretPosition(0);

	  	cvmpTf.setText("10");
	  	cvmpTf1.setText("10");
	  	kvacTf.setText("10");
	  	texpacTf.setText("20");
	  	setkvTf.setText("90");
	  	if (mf.EXAMINATION_ID==1)
	  		setkvTf.setText("28");
	  	settexpTf.setText("100");		
	  	maskvTf.addActionListener(this);
	  	mastexpTf.addActionListener(this);
	  	
	  	Character mnemonic = null;
		JButton button = null;
		//JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		boxLayout21.setAxis(BoxLayout.Y_AXIS);
	    mainP.setLayout(boxLayout21);
	    
	  //kvppanel
	    boxLayout22.setAxis(BoxLayout.Y_AXIS);
	    kvP.setLayout(boxLayout22);
	    //setkvppanel
	    setKvP.setLayout(flowLayout2);
	    setKvP.add(setKvL, null);
	    setKvP.add(setkvTf, null);
	    setKvP.add(cvmpL, null);
	    setKvP.add(cvmpTf, null);
	    setKvP.setBackground(RadQCFrame.bkgColor);
	    //data kvppanel
	    dataKvP.setLayout(flowLayout3);
	    //==========================
	    JPanel labelPan = new JPanel();
	    labelPan.setLayout(new FlowLayout(FlowLayout.CENTER));
	    JLabel labTabInfo = new JLabel(resources.getString("tab.related.Label"));
	    labelPan.add(labTabInfo);
	    labelPan.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel labelPan1 = new JPanel();
	    labelPan1.setLayout(new FlowLayout(FlowLayout.CENTER));
	    JLabel labTabInfo1 = new JLabel(resources.getString("tab.related.Label"));
	    labelPan1.add(labTabInfo1);
	    labelPan1.setBackground(RadQCFrame.bkgColor);
	    //============================
	    JPanel m1 = new JPanel();
	    m1.setLayout(new FlowLayout(FlowLayout.CENTER));
	    m1.add(maskvL);
	    JPanel m2 = new JPanel();
	    m2.setLayout(new FlowLayout(FlowLayout.CENTER));
	    m2.add(maskvTf);
	    JPanel m3 = new JPanel();
	    m3.setLayout(new FlowLayout(FlowLayout.CENTER));
	    buttonName = resources.getString("kvp.addB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADDKV_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("kvp.addB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    m3.add(button);//addkvB);
	    JPanel m4 = new JPanel();
	    m4.setLayout(new FlowLayout(FlowLayout.CENTER));
	    buttonName = resources.getString("kvp.calcB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, CALCKV_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("kvp.calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    m4.add(button);//calckvB);
	    m1.setBackground(RadQCFrame.bkgColor);
	    m2.setBackground(RadQCFrame.bkgColor);
	    m3.setBackground(RadQCFrame.bkgColor);
	    m4.setBackground(RadQCFrame.bkgColor);
	    BoxLayout bl0 = new BoxLayout(masuratP,BoxLayout.Y_AXIS);
	    masuratP.setLayout(bl0);
	    masuratP.add(m1);
	    masuratP.add(m2);
	    masuratP.add(m3);
	    masuratP.add(m4);
	    //---------------------------------
	    lstmasP.setLayout(borderLayout2);
		lstmasP.add(listSp, BorderLayout.CENTER);
		listSp.getViewport().add(kvmL, null);
	    listSp.setPreferredSize(sizeLst);
	    
	    JPanel jp1=new JPanel();
		jp1.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonName = resources.getString("kvp.delB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELKV_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("kvp.delB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp1.add(button);//ra.delkvB);
		JPanel jp2=new JPanel();
		jp2.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonName = resources.getString("kvp.resetB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete.all");
		button = FrameUtilities.makeButton(buttonIconName, RESETKV_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("kvp.resetB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp2.add(button);//ra.resetkvB);
		jp1.setBackground(RadQCFrame.bkgColor);
	    jp2.setBackground(RadQCFrame.bkgColor);
	    BoxLayout bl = new BoxLayout(lstopP,BoxLayout.Y_AXIS);
	    lstopP.setLayout(bl);
		lstopP.add(jp1);
		lstopP.add(jp2);

	    dataKvP.add(masuratP, null);
	    dataKvP.add(lstmasP, null);
		dataKvP.add(lstopP, null);
	    dataKvP.setBackground(RadQCFrame.bkgColor);
	    // result kvppanel
	    resultKvP.setLayout(borderLayout4);
	    jScrollPane1.getViewport().add(kvrepTa, null);
	    resultKvP.add(jScrollPane1,  BorderLayout.CENTER);
	    resultKvP.setBackground(RadQCFrame.bkgColor);
	    //////////////////
	    kvP.add(setKvP, null);
	    kvP.add(dataKvP, null);
	    kvP.add(resultKvP, null);
	     //texpPanel
	    boxLayout23.setAxis(BoxLayout.Y_AXIS);
	    texpP.setLayout(boxLayout23);
	    
	  //settexppanel
	    setTexpP.setLayout(flowLayout4);
	    setTexpP.add(setTexpL, null);
	    setTexpP.add(settexpTf, null);
	    setTexpP.add(cvmpL1, null);
	    setTexpP.add(cvmpTf1, null);
	    setTexpP.setBackground(RadQCFrame.bkgColor);
	    //data texppanel
	    JPanel m11 = new JPanel();
	    m11.setLayout(new FlowLayout(FlowLayout.CENTER));
	    m11.add(masTexpL);
	    JPanel m21 = new JPanel();
	    m21.setLayout(new FlowLayout(FlowLayout.CENTER));
	    m21.add(mastexpTf);
	    JPanel m31 = new JPanel();
	    m31.setLayout(new FlowLayout(FlowLayout.CENTER));//texp.addB
	    buttonName = resources.getString("texp.addB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADDTEXP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("texp.addB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    m31.add(button);//addTexpB);
	    JPanel m41 = new JPanel();
	    m41.setLayout(new FlowLayout(FlowLayout.CENTER));//texp.calcB
	    buttonName = resources.getString("texp.calcB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, CALCTEXP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("texp.calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    m41.add(button);//calcTexpB);
	    m11.setBackground(RadQCFrame.bkgColor);
	    m21.setBackground(RadQCFrame.bkgColor);
	    m31.setBackground(RadQCFrame.bkgColor);
	    m41.setBackground(RadQCFrame.bkgColor);
	    BoxLayout bl01 = new BoxLayout(masuratP1,BoxLayout.Y_AXIS);
	    masuratP1.setLayout(bl01);
	    masuratP1.add(m11);
	    masuratP1.add(m21);
	    masuratP1.add(m31);
	    masuratP1.add(m41);

		lstmasP1.setLayout(borderLayout6);
	    lstmasP1.add(listSp1, BorderLayout.CENTER);
	    listSp1.getViewport().add(texpL, null);
		listSp1.setPreferredSize(sizeLst);
		
		JPanel jp11=new JPanel();
		jp11.setLayout(new FlowLayout(FlowLayout.CENTER));//texp.delB
		buttonName = resources.getString("texp.delB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELTEXP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("texp.delB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp11.add(button);//ra.deltexpB);
		JPanel jp21=new JPanel();
		jp21.setLayout(new FlowLayout(FlowLayout.CENTER));//texp.resetB
		buttonName = resources.getString("texp.resetB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete.all");
		button = FrameUtilities.makeButton(buttonIconName, RESETTEXP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("texp.resetB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp21.add(button);//ra.resetTexpB);
		jp11.setBackground(RadQCFrame.bkgColor);
	    jp21.setBackground(RadQCFrame.bkgColor);
	    BoxLayout bl1 = new BoxLayout(lstopP1,BoxLayout.Y_AXIS);
	    lstopP1.setLayout(bl1);
		lstopP1.add(jp11);
		lstopP1.add(jp21);

	    dataTexpP.setLayout(flowLayout5);
		dataTexpP.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
		dataTexpP.add(masuratP1, null);
		dataTexpP.add(lstmasP1, null);
		dataTexpP.add(lstopP1, null);
	    dataTexpP.setBackground(RadQCFrame.bkgColor);
	    //resulttexppanel
	    resulttexpP.setLayout(borderLayout7);
	    resulttexpP.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER,LINE_BORDER));
	    jScrollPane2.getViewport().add(texprepTa, null);
	    resulttexpP.add(jScrollPane2, BorderLayout.CENTER);
	    resulttexpP.setBackground(RadQCFrame.bkgColor);
	    ////////////////////////////////////////
	    texpP.add(setTexpP, null);
	    texpP.add(dataTexpP, null);
	    texpP.add(resulttexpP, null);
	    //end texpP
	    //mainP.add(kvP, null);
	    //mainP.add(texpP, null);
		
	  //accP
	    boxLayout25.setAxis(BoxLayout.Y_AXIS);
	    mainAcP.setLayout(boxLayout25);
	    jPanel1.setLayout(boxLayout24);
	    boxLayout24.setAxis(BoxLayout.Y_AXIS);
	    jPanel3.setLayout(borderLayout8);
	    jPanel2.setLayout(flowLayout6);
	    jPanel5.setLayout(verticalFlowLayout1);
	    //ra.jPanel4.setLayout(borderLayout9);//new BorderLayout();
	    suportkvacPan.setLayout(borderLayout9);
	    jPanel6.setLayout(flowLayout7);
	    texpacP.setLayout(boxLayout26);
	    boxLayout26.setAxis(BoxLayout.Y_AXIS);
	    jPanel7.setLayout(flowLayout8);
	    jPanel8.setLayout(verticalFlowLayout2);
	    //ra.jPanel9.setLayout(borderLayout10);//new BorderLayout();
	    suporttexpacPan.setLayout(borderLayout10);//new BorderLayout();
	    jPanel10.setLayout(flowLayout9);
	    jPanel11.setLayout(borderLayout11);
	    //-----------------------------
	    jPanel7.add(jLabel2, null);
	    jPanel7.add(texpacTf, null);//texpac.addrB
	    buttonName = resources.getString("texpac.addrB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADDROWTEXP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("texpac.addrB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    jPanel8.add(button);//texpacaddrB, null);
	    buttonName = resources.getString("texpac.delrB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELROWTEXP_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("texpac.delrB.mnemonic");
		button.setMnemonic(mnemonic.charValue());//texpac.delrB
	    jPanel8.add(button);//texpacdelrB, null);
	    buttonName = resources.getString("texpac.calcB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, CALCTEXPAC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("texpac.calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    jPanel8.add(button);//texpaccalcB, null);//texpac.calcB

	    JPanel jPanel81=new JPanel(new BorderLayout());
	    jPanel81.add(jPanel8,BorderLayout.CENTER);
	    jPanel81.add(jPanel7,BorderLayout.SOUTH);
	    
	    suporttexpacPan.add(texpacPan, BorderLayout.CENTER);
	    jPanel10.add(suporttexpacPan, null);
	    jPanel10.add(jPanel81, null);

	    jPanel6.add(jLabel1, null);
	    jPanel6.add(kvacTf, null);
	    buttonName = resources.getString("kvac.addrB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADDROWKV_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("kvac.addrB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    jPanel5.add(button);//kvacaddrB, null);
	    buttonName = resources.getString("kvac.delrB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELROWKV_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("kvac.delrB.mnemonic");
		button.setMnemonic(mnemonic.charValue());//texpac.delrB
	    jPanel5.add(button);//kvacdelrB, null);
	    buttonName = resources.getString("kvac.calcB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, CALCKVAC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("kvac.calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    jPanel5.add(button);//kvaccalcB, null);

	    JPanel jPanel51=new JPanel(new BorderLayout());
	    jPanel51.add(jPanel5,BorderLayout.CENTER);
	    jPanel51.add(jPanel6,BorderLayout.SOUTH);

		suportkvacPan.add(kvacPan, BorderLayout.CENTER);
	    
	    jPanel2.setBackground(RadQCFrame.bkgColor);
	    jPanel10.setBackground(RadQCFrame.bkgColor);
	    jPanel5.setBackground(RadQCFrame.bkgColor);
	    jPanel6.setBackground(RadQCFrame.bkgColor);
	    jPanel7.setBackground(RadQCFrame.bkgColor);
	    jPanel8.setBackground(RadQCFrame.bkgColor);
	    jPanel2.add(suportkvacPan, null);
	    jPanel2.add(jPanel51, null);

	    //==============
	    JPanel kvNorthP = new JPanel();
		BoxLayout bllkvNorthP = new BoxLayout(kvNorthP, BoxLayout.Y_AXIS);
		kvNorthP.setLayout(bllkvNorthP);
		kvNorthP.add(labelPan);
		kvNorthP.add(jPanel2);
		kvNorthP.setBackground(RadQCFrame.bkgColor);
		
		//==============
	    JPanel tNorthP = new JPanel();
		BoxLayout blltNorthP = new BoxLayout(tNorthP, BoxLayout.Y_AXIS);
		tNorthP.setLayout(blltNorthP);
		tNorthP.add(labelPan1);
		tNorthP.add(jPanel10);
		tNorthP.setBackground(RadQCFrame.bkgColor);
	    //================
	    
	    jScrollPane3.getViewport().add(kvacTa, null);
	    jPanel3.add(jScrollPane3,  BorderLayout.CENTER);
	    jScrollPane6.getViewport().add(texpacTa, null);
	    jPanel11.add(jScrollPane6, BorderLayout.CENTER);
	    jPanel1.add(kvNorthP,null);//jPanel2, null);
	    jPanel1.add(jPanel3, null);///////////////////
	    texpacP.add(tNorthP,null);//jPanel10, null);
	    texpacP.add(jPanel11, null);
	    //mainAcP.add(jPanel1, null);
	    //mainAcP.add(texpacP, null);//////////////////
		jPanel3.setBackground(RadQCFrame.bkgColor);
	    jPanel11.setBackground(RadQCFrame.bkgColor);

	    //mainP.setBackground(RadQCFrame.bkgColor);
	    //mainAcP.setBackground(RadQCFrame.bkgColor);
	    
	    //jTabbedPane1.add(mainP,  resources.getString("reprod.tab.title"));	    
	    jTabbedPane1.add(kvP,  resources.getString("reprod.tab.title"));
	    //jTabbedPane1.add(mainAcP,  resources.getString("ac.tab.title"));
	    jTabbedPane1.add(texpP,  resources.getString("reprod.tab.title1"));
	    
	    jTabbedPane1.add(jPanel1,  resources.getString("ac.tab.title"));
	    jTabbedPane1.add(texpacP,  resources.getString("ac.tab.title1"));
	    return jTabbedPane1;		
	}
	
	/**
	 * Create a panel border with a title
	 * @param title the title
	 * @return the result
	 */
	@SuppressWarnings("unused")
	private TitledBorder getGroupBoxBorder(String title)
	{
	     return	BorderFactory.createTitledBorder(LINE_BORDER,title);
	}
	
	/**
	 * Setting up actions!
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		command = arg0.getActionCommand();
		if (arg0.getSource()==maskvTf || command.equals(ADDKV_COMMAND)) {
			addKvInList();
		} else if (command.equals(DELKV_COMMAND)) {
			deleteKvSelectedData();
		} else if (command.equals(CALCKV_COMMAND)) {
			performKvRepCalc();
		} else if (command.equals(RESETKV_COMMAND)) {
			resetKvData();
		} else if (arg0.getSource()==mastexpTf || command.equals(ADDTEXP_COMMAND)) {
			addTexpInList();
		} else if (command.equals(CALCTEXP_COMMAND)) {
			performTexpRepCalc();
		} else if (command.equals(DELTEXP_COMMAND)) {
			deleteTexpSelectedData();
		} else if (command.equals(RESETTEXP_COMMAND)) {
			resetTexpData();
		} else if (command.equals(ADDROWTEXP_COMMAND)) {
			addEmptyRowInTexpTable();
		} else if (command.equals(DELROWTEXP_COMMAND)) {
			delRowInTexpTable();
		} else if (command.equals(CALCTEXPAC_COMMAND)) {
			performTexpAcCalc();
		} else if (command.equals(ADDROWKV_COMMAND)) {
			addEmptyRowInKvTable();
		} else if (command.equals(DELROWKV_COMMAND)) {
			delRowInKvTable();
		} else if (command.equals(CALCKVAC_COMMAND)) {
			performKvAcCalc();
		} else if (command.equals(SAVE_COMMAND)) {
			save();
		} 
	}
	
	/**
	 * Go to save in database
	 */
	private void save(){
		int itab = jTabbedPane1.getSelectedIndex();
		SaveViewDBFrame.ITAB=itab;//1;
		new SaveViewDBFrame(this);
	}
	
	/**
	 * Perform KvP accuracy QC test
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void performKvAcCalc()
	{
		isOktoSaveKvAccuracy=false;
		kvaccvb=new boolean[0];//boolean for acceptance
		kvsacv=new Vector();
		kvmacv=new Vector();
		cvkvacv=new Vector();//for acceptance series
		kvsacd=new double[0];
		kvmacd=new double[0];
		cvkvacd=new double[0];//coresponding double->raport

		boolean b=true;
		String cvms=kvacTf.getText();
		String cvms1=" ";
		double dbl=0.0;
		double dbl1=0.0;
		double diff=0.0;
		String diffs=" ";
		kvaccvm=0.0;
	    try
	    {
		    kvaccvm=Convertor.stringToDouble(cvms);
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

		JTable tab=kvacPan.getTab();
		int rowTableCount=tab.getRowCount();

		if (rowTableCount>0)
		{
			for(int i=0; i<rowTableCount; i++)
			{
				cvms=(String)tab.getValueAt(i,0);
				cvms1=(String)tab.getValueAt(i,1);
				try
				{
					dbl=Convertor.stringToDouble(cvms);
					dbl1=Convertor.stringToDouble(cvms1);
				}
				catch(Exception e)
				{
					b=false;
				    String title =resources.getString("dialog.insertInListError.title");
				    String message =resources.getString("dialog.insertInList.message");
				    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				    break;
				}

				if(dbl<=0.0 || dbl1<=0.0)
				{
					b=false;
				    String title =resources.getString("dialog.insertInTableError.title");
				    String message =resources.getString("dialog.insertInTable.message");
				    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				    break;
				}
				else
				{
					diff=100*Math.abs(dbl1-dbl)/dbl1;
					diffs=Convertor.doubleToString(diff);
				}
	            //--now we sure that vectors contain only double and positive objects!!
			    kvsacv.addElement(tab.getValueAt(i,0));//setat
			    kvmacv.addElement(tab.getValueAt(i,1));//masurat
			    cvkvacv.addElement(diffs);//diff

			}

			if(!b)
			{
			    return;
		    }

			//--display results and construct arrays
			//------clear first
			kvacTa.selectAll();
			kvacTa.replaceSelection("");
			//----------------
	        NumberFormat nf = NumberFormat.getInstance(Locale.US);
			nf.setMinimumFractionDigits(2);//default e 2 oricum!!
	    	nf.setMaximumFractionDigits(2);//default e 2 oricum!!
	    	nf.setGroupingUsed(false);//no 4,568.02 but 4568.02

			kvsacd = new double[kvsacv.size()];
			kvmacd = new double[kvmacv.size()];//same size
			cvkvacd = new double[cvkvacv.size()];//same size
			kvaccvb = new boolean[cvkvacv.size()];
			for(int i=0; i<kvsacv.size(); i++)
			{
				String s=(String)kvsacv.elementAt(i);
				String s1=(String)kvmacv.elementAt(i);
				String s2=(String)cvkvacv.elementAt(i);
				kvsacd[i]=Convertor.stringToDouble(s);
				kvmacd[i]=Convertor.stringToDouble(s1);
				cvkvacd[i]=Convertor.stringToDouble(s2);

		        if (cvkvacd[i]<=kvaccvm)
		           kvaccvb[i]=true;
		        else
		           kvaccvb[i]=false;

		        kvacTa.append(
					resources.getString("ac.rezultat.set")+nf.format(kvsacd[i])+"  "+
					resources.getString("ac.rezultat.mas")+nf.format(kvmacd[i])+"  "+
					resources.getString("ac.rezultat.cv")+nf.format(cvkvacd[i])+"  "+
					resources.getString("ac.rezultat.cvmp")+nf.format(kvaccvm)+"  \n");

		        if (kvaccvb[i])
		        	kvacTa.append(resources.getString("reprod.rezultat")+
		        	resources.getString("reprod.rezultat.succes")+"  \n");
		        else
		        	kvacTa.append(resources.getString("reprod.rezultat")+
		        	resources.getString("reprod.rezultat.fail")+"  \n");

				kvacTa.append("---------------------------------"+"  \n");
			}
			isOktoSaveKvAccuracy=true;
		}		
	}
	
	/**
	 * Perform exposure time accuracy QC test
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void performTexpAcCalc(){
		isOktoSaveTexpAccuracy=false;
		texpaccvb=new boolean[0];//boolean for acceptance
		texpsacv=new Vector();
		texpmacv=new Vector();
		cvtexpacv=new Vector();//for acceptance series
		texpsacd=new double[0];
		texpmacd=new double[0];
		cvtexpacd=new double[0];//coresponding double->raport

		boolean b=true;
		String cvms=texpacTf.getText();
		String cvms1=" ";
		double dbl=0.0;
		double dbl1=0.0;
		double diff=0.0;
		String diffs=" ";
		texpaccvm=0.0;
	    try
	    {
		    texpaccvm=Convertor.stringToDouble(cvms);
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

		JTable tab=texpacPan.getTab();
		int rowTableCount=tab.getRowCount();

		if (rowTableCount>0)
		{
			for(int i=0; i<rowTableCount; i++)
			{
				cvms=(String)tab.getValueAt(i,0);
				cvms1=(String)tab.getValueAt(i,1);
				try
				{
					dbl=Convertor.stringToDouble(cvms);
					dbl1=Convertor.stringToDouble(cvms1);
				}
				catch(Exception e)
				{
					b=false;
				    String title =resources.getString("dialog.insertInListError.title");
				    String message =resources.getString("dialog.insertInList.message");
				    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				    break;
				}

				if(dbl<=0.0 || dbl1<=0.0)
				{
					b=false;
				    String title =resources.getString("dialog.insertInTableError.title");
				    String message =resources.getString("dialog.insertInTable.message");
				    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				    break;
				}
				else
				{
					diff=100*Math.abs(dbl1-dbl)/dbl1;
					diffs=Convertor.doubleToString(diff);
				}
	            //--now we sure that vectors contain only double and positive objects!!
			    texpsacv.addElement(tab.getValueAt(i,0));//setat
			    texpmacv.addElement(tab.getValueAt(i,1));//masurat
			    cvtexpacv.addElement(diffs);//diff

			}

			if(!b)
			{
			    return;
		    }

			//--display results and construct arrays
			//------clear first
			texpacTa.selectAll();
			texpacTa.replaceSelection("");
			//----------------
	        NumberFormat nf = NumberFormat.getInstance(Locale.US);
			nf.setMinimumFractionDigits(2);//default e 2 oricum!!
	    	nf.setMaximumFractionDigits(2);//default e 2 oricum!!
	    	nf.setGroupingUsed(false);//no 4,568.02 but 4568.02

			texpsacd = new double[texpsacv.size()];
			texpmacd = new double[texpmacv.size()];//same size
			cvtexpacd = new double[cvtexpacv.size()];//same size
			texpaccvb = new boolean[cvtexpacv.size()];
			for(int i=0; i<texpsacv.size(); i++)
			{
				String s=(String)texpsacv.elementAt(i);
				String s1=(String)texpmacv.elementAt(i);
				String s2=(String)cvtexpacv.elementAt(i);
				texpsacd[i]=Convertor.stringToDouble(s);
				texpmacd[i]=Convertor.stringToDouble(s1);
				cvtexpacd[i]=Convertor.stringToDouble(s2);

		        if (cvtexpacd[i]<=texpaccvm)
		           texpaccvb[i]=true;
		        else
		           texpaccvb[i]=false;

		        texpacTa.append(
					resources.getString("ac.rezultat.set")+nf.format(texpsacd[i])+"  "+
					resources.getString("ac.rezultat.mas")+nf.format(texpmacd[i])+"  "+
					resources.getString("ac.rezultat.cv")+nf.format(cvtexpacd[i])+"  "+
					resources.getString("ac.rezultat.cvmp")+nf.format(texpaccvm)+"  \n");

		        if (texpaccvb[i])
		        	texpacTa.append(resources.getString("reprod.rezultat")+
		        	resources.getString("reprod.rezultat.succes")+"  \n");
		        else
		        	texpacTa.append(resources.getString("reprod.rezultat")+
		        	resources.getString("reprod.rezultat.fail")+"  \n");

				texpacTa.append("---------------------------------"+"  \n");
			}
			isOktoSaveTexpAccuracy=true;
		}		
	}
	
	/**
	 * Delete an entry from accuracy kv table (not database)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void delRowInKvTable(){
		JTable tab=kvacPan.getTab();
		int rowTableCount=tab.getRowCount();
		int colTableCount=tab.getColumnCount();

		Vector rowV=new Vector();
		Vector dataV=new Vector();

		if (rowTableCount>0)
		{
			int[] selRows = tab.getSelectedRows();
			//---read initial data
			for(int i=0; i<rowTableCount; i++)
			{
				rowV=new Vector();
				for(int j=0; j<colTableCount; j++)
				{
					rowV.addElement(tab.getValueAt(i,j));
				}
				dataV.addElement(rowV);
			}
	        //--end
			int j=0;//count rows that we've just got rid of it!!!
			for(int i=0; i<selRows.length;i++)
			{
				//System.out.println(""+selRows[i]+" value: "+tab.getValueAt(selRows[i],0));
				dataV.removeElementAt(selRows[i]-j);
				//TableUtilities.removeRow(selRows[i]-j,dataModelkv);//DO NOT WORK ON SORTABLE TABLE!!!
				j++;
	    	}
		    //constructing the new table
		    suportkvacPan.remove(kvacPan);
		    kvacPan=new SortableTablePanel(dataV,colNames);
			suportkvacPan.add(kvacPan,BorderLayout.CENTER);

			tab=kvacPan.getTab();
			rowTableCount=tab.getRowCount();
			if (rowTableCount>0)
				tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);
			validate();
		}		
	}
	
	/**
	 * Delete an entry from accuracy exposure time table (not database)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void delRowInTexpTable(){
		JTable tab=texpacPan.getTab();
		int rowTableCount=tab.getRowCount();
		int colTableCount=tab.getColumnCount();

		Vector rowV=new Vector();
		Vector dataV=new Vector();

		if (rowTableCount>0)
		{
			int[] selRows = tab.getSelectedRows();
			//---read initial data
			for(int i=0; i<rowTableCount; i++)
			{
				rowV=new Vector();
				for(int j=0; j<colTableCount; j++)
				{
					rowV.addElement(tab.getValueAt(i,j));
				}
				dataV.addElement(rowV);
			}
	        //--end
			int j=0;//count rows that we've just got rid of it!!!
			for(int i=0; i<selRows.length;i++)
			{
				//System.out.println(""+selRows[i]+" value: "+tab.getValueAt(selRows[i],0));
				dataV.removeElementAt(selRows[i]-j);
				//TableUtilities.removeRow(selRows[i]-j,dataModelkv);//DO NOT WORK ON SORTABLE TABLE!!!
				j++;
	    	}
		    //constructing the new table
		    suporttexpacPan.remove(texpacPan);
		    texpacPan=new SortableTablePanel1(dataV,colNames);
			suporttexpacPan.add(texpacPan,BorderLayout.CENTER);

			tab=texpacPan.getTab();
			rowTableCount=tab.getRowCount();
			if (rowTableCount>0)
				tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);
			validate();
		}		
	}
	
	/**
	 * Add an empty row to accuracy kv table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addEmptyRowInKvTable(){
		JTable tab=kvacPan.getTab();
	    Vector rowV=new Vector();
	    Vector dataV=new Vector();
	    int rowTableCount=tab.getRowCount();
		int colTableCount=tab.getColumnCount();

		for(int i=0; i<rowTableCount; i++)
		{
			rowV=new Vector();
			for(int j=0; j<colTableCount; j++)
			{
				rowV.addElement(tab.getValueAt(i,j));
			}
			dataV.addElement(rowV);
		}
	    //end
		//add empty row
	    rowV=new Vector();
		for(int j=0; j<colTableCount; j++)
		{
			rowV.addElement(" ");
		}
	    dataV.addElement(rowV);
		//end
	    //constructing the new table
	    suportkvacPan.remove(kvacPan);
	    kvacPan=new SortableTablePanel(dataV,colNames);
		suportkvacPan.add(kvacPan,BorderLayout.CENTER);
		tab=kvacPan.getTab();
	    rowTableCount=tab.getRowCount();
	    tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);
		validate();
	}
	
	/**
	 * Add an empty row to accuracy exposure time table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addEmptyRowInTexpTable(){
		JTable tab=texpacPan.getTab();
	    Vector rowV=new Vector();
	    Vector dataV=new Vector();
	    int rowTableCount=tab.getRowCount();
		int colTableCount=tab.getColumnCount();

		for(int i=0; i<rowTableCount; i++)
		{
			rowV=new Vector();
			for(int j=0; j<colTableCount; j++)
			{
				rowV.addElement(tab.getValueAt(i,j));
			}
			dataV.addElement(rowV);
		}
	    //end
		//add empty row
	    rowV=new Vector();
		for(int j=0; j<colTableCount; j++)
		{
			rowV.addElement(" ");
		}
	    dataV.addElement(rowV);
		//end
	    //constructing the new table
	    suporttexpacPan.remove(texpacPan);
	    texpacPan=new SortableTablePanel1(dataV,colNames);
		suporttexpacPan.add(texpacPan,BorderLayout.CENTER);
		tab=texpacPan.getTab();
	    rowTableCount=tab.getRowCount();
	    tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);
		validate();		
	}
	
	/**
	 * Given a vector v, this routine converts it into a double array.
	 * @param v v
	 * @return the result
	 */
	@SuppressWarnings({ "rawtypes" })
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
	
	/**
	 * Perform KvP repeatability QC test
	 */
	private void performKvRepCalc(){
		isOktoSaveKvRepeatability=false;
		kvcvb=false;
		boolean b=true;
  		String cvms=cvmpTf.getText();
  		String kvs=setkvTf.getText();
  		kvcvm=0.0;
		kvcv=0.0;

        try
        {
		    kvcvm=Convertor.stringToDouble(cvms);
		    kvset=Convertor.stringToDouble(kvs);
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
		
		kvd=convertVectorToDoubleArray(kvv);

		if (kvd.length==0)
		{
		   b=false;
		   String title =resources.getString("dialog.insertInListError.title");
		   String message =resources.getString("dialog.insertInList.message");
		   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

	    }
		
		if(!b)
			   return;
		
		if (kvd.length<2)
		{
			   b=false;
			   String title =resources.getString("dialog.insertInListError.title");
			   String message =resources.getString("dialog.insertInList.message2");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

		}
		
		if(!b)
		   return;

		//double stdev=Stat.stDev(kvd);
		//double mean=Stat.mean(kvd);
		Stats.avevar(kvd, kvd.length);
		double mean = Stats.ave_avevar;
		double stdev = Stats.var_avevar;
		stdev = Math.sqrt(stdev);
		
		if (mean!=0.0)
			kvcv=100*stdev/mean;
		else
		    kvcv=0.0;

        //------clear first
        kvrepTa.selectAll();
        kvrepTa.replaceSelection("");
        //----------------

        NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumFractionDigits(2);//default e 2 oricum!!
    	nf.setMaximumFractionDigits(2);//default e 2 oricum!!
    	nf.setGroupingUsed(false);//no 4,568.02 but 4568.02
        kvrepTa.append(resources.getString("reprod.rezultat.cv")+nf.format(kvcv)+"  \n");
        kvrepTa.append(resources.getString("reprod.rezultat.cvmp")+nf.format(kvcvm)+"  \n");

        if (kvcv<=kvcvm)
           kvcvb=true;
        else
           kvcvb=false;

        if (kvcvb)
        	kvrepTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.succes"));
        else
        	kvrepTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.fail"));	
        
        isOktoSaveKvRepeatability=true;
	}
	
	/**
	 * Perform exposure time repeatability QC test
	 */
	private void performTexpRepCalc(){
		isOktoSaveTexpRepeatability=false;
		texpcvb=false;
		boolean b=true;
  		String cvms=cvmpTf1.getText();
  		String texps=settexpTf.getText();
  		texpcvm=0.0;
  		texpcv=0.0;

        try
        {
		    texpcvm=Convertor.stringToDouble(cvms);
		    texpset=Convertor.stringToDouble(texps);
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
		
		texpd=convertVectorToDoubleArray(texpv);

		if (texpd.length==0)
		{
		   b=false;
		   String title =resources.getString("dialog.insertInListError.title");
		   String message =resources.getString("dialog.insertInList.message");
		   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

	    }

		if(!b)
			   return;
		
		if (texpd.length<2)
		{
			   b=false;
			   String title =resources.getString("dialog.insertInListError.title");
			   String message =resources.getString("dialog.insertInList.message2");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

		}
		
		if(!b)
		   return;
		
		//double stdev=Stat.stDev(texpd);
		//double mean=Stat.mean(texpd);
		Stats.avevar(texpd, texpd.length);
		double mean = Stats.ave_avevar;
		double stdev = Stats.var_avevar;
		stdev = Math.sqrt(stdev);
		
		if(mean!=0.0)
			texpcv=100*stdev/mean;
		else
			texpcv=0.0;
        //------------------clear first--
        texprepTa.selectAll();
        texprepTa.replaceSelection("");
        //-------------------------------

        NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumFractionDigits(2);//default e 2 oricum!!
    	nf.setMaximumFractionDigits(2);//default e 2 oricum!!
    	nf.setGroupingUsed(false);//no 4,568.02 but 4568.02
        texprepTa.append(resources.getString("reprod.rezultat.cv")+nf.format(texpcv)+"  \n");
        texprepTa.append(resources.getString("reprod.rezultat.cvmp")+nf.format(texpcvm)+"  \n");

        if (texpcv<=texpcvm)
           texpcvb=true;
        else
           texpcvb=false;

        if (texpcvb)
        	texprepTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.succes"));
        else
        	texprepTa.append(resources.getString("reprod.rezultat")+
        	resources.getString("reprod.rezultat.fail"));	
        isOktoSaveTexpRepeatability=true;
	}
	
	/**
	 * Delete KvP data from list
	 */
	private void deleteKvSelectedData(){
		if(nKvPoints!=0)
        {

        	nKvPoints--;

			int index=ListUtilities.getSelectedIndex(kvmL);

			ListUtilities.remove(index,kvdlm);
			ListUtilities.select(nKvPoints-1,kvmL);

			kvv.removeElementAt(index);

			maskvTf.setText("");
			maskvTf.requestFocusInWindow();
		}		
	}
	
	/**
	 * Delete exposure time data from list
	 */
	private void deleteTexpSelectedData(){
		if(nTexpPoints!=0)
        {

        	nTexpPoints--;

			int index=ListUtilities.getSelectedIndex(texpL);

			ListUtilities.remove(index,texpdlm);
			ListUtilities.select(nTexpPoints-1,texpL);

			texpv.removeElementAt(index);

			mastexpTf.setText("");
			mastexpTf.requestFocusInWindow();
		}		
	}
	
	/**
	 * Clear KvP list
	 */
	private void resetKvData(){
		kvv.removeAllElements();
        ListUtilities.removeAll(kvdlm);
        nKvPoints=0;
		maskvTf.setText("");
		maskvTf.requestFocusInWindow();		
	}
	
	/**
	 * Clear exposure time list
	 */
	private void resetTexpData(){
	   texpv.removeAllElements();
       ListUtilities.removeAll(texpdlm);
       nTexpPoints=0;
       mastexpTf.setText("");
	   mastexpTf.requestFocusInWindow();	   
	}
	
	/**
	 * Add KvP data in list
	 */
	@SuppressWarnings("unchecked")
	private void addKvInList(){
		boolean b=true;
        String s1=maskvTf.getText();
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
			maskvTf.setText("");
			maskvTf.requestFocusInWindow();
		}

		if(!b)
		   return;

        //end test-->la succes se poate merge mai departe

		ListUtilities.add("Measured kilovoltage :     "+d1+"  kV",kvdlm);
		ListUtilities.select(nKvPoints,kvmL);
		s1=Convertor.doubleToString(d1);

		kvv.addElement((Object)s1);
		nKvPoints++;

		maskvTf.setText("");
		maskvTf.requestFocusInWindow();		
	}
	
	/**
	 * Add exposure time data in list
	 */
	@SuppressWarnings("unchecked")
	private void addTexpInList(){
		boolean b=true;
        String s1=mastexpTf.getText();
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
			mastexpTf.setText("");
			mastexpTf.requestFocusInWindow();
		}

		if(!b)
		   return;

        //end test-->la succes se poate merge mai departe

		ListUtilities.add("timp exp. masurat:     "+d1+"  s sau ms",texpdlm);
		ListUtilities.select(nTexpPoints,texpL);
		s1=Convertor.doubleToString(d1);

		texpv.addElement((Object)s1);
		nTexpPoints++;

		mastexpTf.setText("");
		mastexpTf.requestFocusInWindow();		
	}
}
