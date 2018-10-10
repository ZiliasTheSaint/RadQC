package aec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import danfulea.db.DatabaseAgent;
//import jdf.db.DBConnection;
import danfulea.math.Convertor;
import danfulea.math.numerical.Stats;
import danfulea.utils.FrameUtilities;
import danfulea.utils.ListUtilities;
import radQC.RadQCFrame;


/**
* QC tests for AEC (Automatic Exposure Control) devices.
* @author Dan Fulea, 03 May 2015
*/
@SuppressWarnings("serial")
public class AECFrame extends JFrame implements ActionListener{

	private static final Dimension PREFERRED_SIZE = new Dimension(800, 720);
	private static final Dimension textAreaDimension = new Dimension(700, 300);
	private static final Dimension sizeLst = new Dimension(253,125);
	private static final String BASE_RESOURCE_CLASS = "aec.resources.AECFrameResources";
	protected ResourceBundle resources;
	private RadQCFrame mf;
	
	protected String measurementDate_toSave="";
	protected String mainDB = "";
	protected int IDLINK=0;
	
	private static final String ADDREP_COMMAND = "ADDREP";
	private static final String CALCREP_COMMAND = "CALCREP";
	private static final String DELREP_COMMAND = "DELREP";
	private static final String RESETREP_COMMAND = "RESETREP";
	
	private static final String ADDLIN_COMMAND = "ADDLIN";//compensate
	private static final String CALCLIN_COMMAND = "CALCLIN";
	private static final String DELLIN_COMMAND = "DELLIN";
	private static final String RESETLIN_COMMAND = "RESETLIN";
	
	private static final String SAVE_COMMAND = "SAVE";
	private String command = null;
	
	private JTabbedPane mainTab = new JTabbedPane();
	
	protected JTextArea repeatabilityTa = new JTextArea();
	private JTextField maxPermissibleRepeatabilityTf=new JTextField(5);
	private JTextField measuredRepeatabilityTf=new JTextField(5);	
	@SuppressWarnings("rawtypes")
	protected DefaultListModel repeatabilitydlm=new DefaultListModel() ;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected JList repeatabilityL = new JList(repeatabilitydlm);
	
	private JCheckBox perCentCh;
	private JCheckBox perCentChLin;
	
	protected JTextArea randTa = new JTextArea();
	private JTextField maxPermissibleLinearityTf=new JTextField(5);
	private JTextField setmaTf=new JTextField(5);//measured
	@SuppressWarnings("rawtypes")
	protected DefaultListModel dlm=new DefaultListModel() ;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected JList randList = new JList(dlm);
	
	//Output variable
	private int nRepeatabilityPoints=0;
	private Vector<String> repeatabilityv=new Vector<String>();	
	protected double[] repeatabilityd=new double[0];
	protected boolean isOktoSaveReapeatability=false;
	protected boolean resultRepeatability=false;
	protected double cvRepeatability=0.0;
	protected double maxcvRepeatability=0.0;
	
	private int nrandPoints=0;
	private Vector<String> randv=new Vector<String>();//measured
	protected double[] randd=new double[0];
	protected double cvLinearity=0.0;
	protected double maxcvLinearity=0.0;	
	protected boolean resultLinearity=false;
	protected boolean isOktoSaveLinearity=false;
		
	public String aecRepeatabilityTable="";
	public String aecRepeatabilityTableDetail="";
	public String aecLinearityTable="";
	public String aecLinearityTableDetail="";
	
	
	//private Connection radqcdbcon = null;
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public AECFrame(RadQCFrame mf){
		this.mf=mf;
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("AECFrame.NAME"));
		//DBConnection.startDerby();//just in case is closed
		//===============
		mainDB=mf.radqcDB;
		
		aecRepeatabilityTable=mf.aecRepeatabilityTable;
		aecRepeatabilityTableDetail=mf.aecRepeatabilityTableDetail;
		aecLinearityTable=mf.aecLinearityTable;
		aecLinearityTableDetail=mf.aecLinearityTableDetail;
    			
    	measurementDate_toSave=mf.measurementDate;
    	IDLINK=mf.IDLINK;
    	//==============================================
    	DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = this.resources.getString("data.load");// "Data";
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		opens = opens + file_sep + mainDB;
    	//radqcdbcon = 
    	DatabaseAgent.getConnection(opens, "", "");
    	//=============================================
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

		measuredRepeatabilityTf.requestFocusInWindow();
	}
	
	/**
	 * Create main panel
	 * @return the result
	 */
	private JTabbedPane createMainPanel() {
		//some inits
		perCentCh = new JCheckBox("%",true);//initial selected
		perCentChLin = new JCheckBox("%",true);//initial selected
		
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
	    		
		maxPermissibleRepeatabilityTf.setText("20");//or +/- 0.3 OD!!!!!!!!!!!!! or 40%!!
		measuredRepeatabilityTf.addActionListener(this);
		
		maxPermissibleLinearityTf.setText("20");
		setmaTf.addActionListener(this);
		
		JLabel jlabel=new JLabel();
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		JButton button =null;
		Character mnemonic = null;		
		
		//Repeatability panel------------------------------------------
		JPanel p00P=new JPanel();
		p00P.setLayout(new FlowLayout(FlowLayout.CENTER,20,2));
		jlabel = new JLabel(resources.getString("repeatability.maxPermissibleLabel"));
		p00P.add(jlabel);
        p00P.add(maxPermissibleRepeatabilityTf);
        p00P.add(perCentCh);
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
		mpP.add(perCentChLin);
		mpP.setBackground(RadQCFrame.bkgColor);
		//data panel
		JPanel datakP=new JPanel();
	    BoxLayout bldatak = new BoxLayout(datakP,BoxLayout.Y_AXIS);
	    datakP.setLayout(bldatak);
	    JLabel setmaL=new JLabel(resources.getString("rand.setmaL"));
	    	    
	    JPanel setmaP=new JPanel();
	    setmaP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    setmaP.add(setmaL);
	    setmaP.add(setmaTf);
	    setmaP.setBackground(RadQCFrame.bkgColor);
	    	    
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
    	//mainTab.add(mainOutputP,  resources.getString("output.tab.title"));
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
	
	/**
	 * Most actions are set here
	 */
	public void actionPerformed(ActionEvent e) {		
		
		command = e.getActionCommand();
		if (e.getSource()==measuredRepeatabilityTf || command.equals(ADDREP_COMMAND)) {			
			addInListRep();
		} else if (command.equals(DELREP_COMMAND)) {
			deleteFromListRep();
		} else if (command.equals(CALCREP_COMMAND)) {
			performCalcRep();
		} else if (command.equals(RESETREP_COMMAND)) {
			resetRep();
		} else if (command.equals(SAVE_COMMAND)) {
			save();
		} else if (e.getSource()==setmaTf || command.equals(ADDLIN_COMMAND)){
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
	 * Add data in list. Used for AEC compensation.
	 */
	private void addInListLin(){
		String s=setmaTf.getText();
		boolean b=true;
        double sd=0.0;
                
        try
		{
		    sd=Convertor.stringToDouble(s);		
		
		}
		catch (Exception ex)
		{
			b=false;
			String title =resources.getString("dialog.insertInListError.title");
		    String message =resources.getString("dialog.insertInList.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    setmaTf.setText("");
		}
       
		if(!b)
		   return;
        
		ListUtilities.add("Measured value :     "+sd,dlm);
		ListUtilities.select(nrandPoints,randList);
		
		randv.addElement(s);
		nrandPoints++;

		setmaTf.setText("");
		setmaTf.requestFocusInWindow();
	}
	
	/**
	 * Delete data from list. Used for AEC compensation.
	 */
	private void deleteFromListLin(){
		if(nrandPoints!=0)
        {

        	nrandPoints--;

			int index=ListUtilities.getSelectedIndex(randList);

			ListUtilities.remove(index,dlm);
			ListUtilities.select(nrandPoints-1,randList);
	       
			randv.removeElementAt(index);

			setmaTf.setText("");
		 	setmaTf.requestFocusInWindow();
		}
	}
	
	/**
	 * Perform AEC compensation QC test. 
	 */
	private void performCalcLin(){
		isOktoSaveLinearity=false;
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
		
		randd=convertVectorToDoubleArray(randv);

		if (randd.length==0)
		{
		   b=false;
		   String title =resources.getString("dialog.insertInListError.title");
		   String message =resources.getString("dialog.insertInList.message");
		   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

	    }
		
		if(!b)
			   return;
		
		if (randd.length<2)
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
		
		if (!perCentChLin.isSelected())
			cvLinearity=stdev;//number not perCent

        //------clear first
		randTa.selectAll();
		randTa.replaceSelection("");
        
        if (!perCentChLin.isSelected()){
        	randTa.append(resources.getString("reprod.rezultat.cv2")+
        			Convertor.formatNumber(cvLinearity,2)+"  \n");
        	randTa.append(resources.getString("reprod.rezultat.cvmp2")+
        			Convertor.formatNumber(maxcvLinearity,2)+"  \n");
        }else{
        	randTa.append(resources.getString("reprod.rezultat.cv")+
    			Convertor.formatNumber(cvLinearity,2)+"  \n");
        	randTa.append(resources.getString("reprod.rezultat.cvmp")+
    			Convertor.formatNumber(maxcvLinearity,2)+"  \n");
        }

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
        
        isOktoSaveLinearity=true;
	}
	
	/**
	 * Reset list. Used for AEC compensation
	 */
	private void resetLin(){
		randv.removeAllElements();
        ListUtilities.removeAll(dlm);
        nrandPoints=0;
		setmaTf.setText("");		
		setmaTf.requestFocusInWindow();
	}
	
	/**
	 * Add data in list. Used for AEC repeatability.
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
	 * Delete data from list. Used for AEC repeatability.
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
	 * Perform AEC repeatability QC test.
	 */
	private void performCalcRep(){
		isOktoSaveReapeatability=false;
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
		
		if (!perCentCh.isSelected())
			cvRepeatability=stdev;//number not perCent

        //------clear first
		repeatabilityTa.selectAll();
        repeatabilityTa.replaceSelection("");
        
        if (!perCentCh.isSelected()){
        	repeatabilityTa.append(resources.getString("reprod.rezultat.cv2")+
        			Convertor.formatNumber(cvRepeatability,2)+"  \n");
        	repeatabilityTa.append(resources.getString("reprod.rezultat.cvmp2")+
        			Convertor.formatNumber(maxcvRepeatability,2)+"  \n");
        }else{
        	repeatabilityTa.append(resources.getString("reprod.rezultat.cv")+
    			Convertor.formatNumber(cvRepeatability,2)+"  \n");
        	repeatabilityTa.append(resources.getString("reprod.rezultat.cvmp")+
    			Convertor.formatNumber(maxcvRepeatability,2)+"  \n");
        }

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
        
        isOktoSaveReapeatability=true;
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
	 * Reset list. Used for AEC repeatability.
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
