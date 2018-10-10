package fsrelated;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.pdfbox.pdmodel.PDDocument;

import danfulea.utils.ExampleFileFilter;
import danfulea.math.Convertor;
import danfulea.math.StatsUtil;
import danfulea.utils.FrameUtilities;
import danfulea.utils.PDFRenderer;
import radQC.RadQCFrame;

/**
 * Class for focal spot evaluation using a custom tester (RMI 112b).
 * 
 * @author Dan Fulea, 28 Apr. 2015
 */
@SuppressWarnings("serial")
public class FocalSpotFrame extends JFrame implements ActionListener, ItemListener{
	private static final String BASE_RESOURCE_CLASS = "fsrelated.resources.FocalSpotFrameResources";
	protected ResourceBundle resources;
	private final Dimension PREFERRED_SIZE = new Dimension(650, 600);
	private static final Dimension sizeFsCb = new Dimension(50, 21);
	private RadQCFrame mf;
	
	protected String outFilename = null;
	private static final String PRINT_COMMAND = "PRINT";
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private String command = null;
	
	private JTextField estimatedMeasurementUncertaintyTf = new JTextField(3);
	protected JTabbedPane fsjtb=new JTabbedPane();
	@SuppressWarnings("rawtypes")
	protected JComboBox fsgiCb;
	protected JCheckBox fsgiCh;
	protected JTextArea fsTa = new JTextArea();
	protected JTextField htesterTf = new JTextField(5);
	protected JTextField hspatiatorTf = new JTextField(5);
	protected JTextField ffdTf = new JTextField(5);
	protected JTextField doriftesterTf = new JTextField(5);
	protected JTextField doriffilmTf = new JTextField(5);
	protected JTextField lpmmTf = new JTextField(5);
	protected JTextField nominalfsTf = new JTextField(5);
	protected JTextField ffdmpTf = new JTextField(5);
	
	//---focal spot main variables
	protected String fstester="";
	protected double htesterd=0.0;//h tester
	protected double hspatiatord=0.0;//h spatiator
	protected double doriftesterd=0.0;//dist orif tester
	protected double doriffilmd=0.0;//dist orif film
	protected double fsgi=0.0;//focal spot g(i)!!inca vizibil-lp/mm
	protected double ffdmd=0.0;//ffd masurat
	protected double ffdcd=0.0;//ffd calculat
	protected double mmd=0.0;//M masurat
	protected double mcd=0.0;//M calculat
	protected double mdiffd=0.0;//M diff %
	protected double ffddiffd=0.0;//ffd diff %
	protected double ffddiffmpd=0.0;//ffd diff max permitted %
	protected boolean ffdtestb=false;//for testresult
	protected double fsd=0.0;//focalspot
	protected double fsnomd=0.0;//focalspot nom dat
	protected double fsnommind=0.0;//focalspot nom minim
	protected double fsnommaxd=0.0;//focalspot nom max
	protected boolean fstestb=false;//for testresult
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame
	 */
	public FocalSpotFrame(RadQCFrame mf){
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("FocalSpotFrame.NAME"));	
		this.mf=mf;
		
		createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);

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
		
		JPanel mainPanel = createMainPanel();
		content.add(mainPanel, BorderLayout.CENTER);
		//content.add(statusBar, BorderLayout.PAGE_END);

		setContentPane(new JScrollPane(content));
		content.setOpaque(true); // content panes must be opaque
		pack();
		
		//some init
        fsgiCb.setSelectedItem((String)"6");
        setfsgi();
        fsjtb.setSelectedIndex(0);//initial pointez la RMI112B!!!!
	}
	
	/**
	 * Create main panel
	 * @return the result
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel createMainPanel() {
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		String[] fsgindex = (String[])resources.getObject("fs.fsgi.index");
		fsgiCb=new JComboBox(fsgindex);
		fsgiCb.setMaximumRowCount(5);
		fsgiCb.setPreferredSize(sizeFsCb);
		fsgiCb.addItemListener(this);
		
		fsgiCh = new JCheckBox(resources.getString("fs.lpmmL.checkbox"));
				
		fsTa.setCaretPosition(0);
	    fsTa.setEditable(false);
	    fsTa.setText(resources.getString("rezultat"));
	    fsTa.setLineWrap(true);
	    fsTa.setWrapStyleWord(true);
	    
	    estimatedMeasurementUncertaintyTf.setText("5");
	    JPanel puncP=new JPanel();
		puncP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("expData.unc"));
        puncP.add(label);
        puncP.add(estimatedMeasurementUncertaintyTf);        
        puncP.setBackground(RadQCFrame.bkgColor);
	    
	    nominalfsTf.setText("0.8");
	  	htesterTf.setText("15.2");
	  	hspatiatorTf.setText("0");
	  	ffdTf.setText("100");
	  	ffdmpTf.setText("10");
	  	doriftesterTf.setText("6");
		
	  	label=new JLabel(resources.getString("fs.htesterL"));
	    JPanel htstP=new JPanel();
	    htstP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    htstP.add(label);
	    htstP.add(htesterTf);
	    htstP.setBackground(RadQCFrame.bkgColor);

	    label=new JLabel(resources.getString("fs.hspatiatorL"));
	    JPanel hsptP=new JPanel();
	    hsptP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    hsptP.add(label);
	    hsptP.add(hspatiatorTf);
	    hsptP.setBackground(RadQCFrame.bkgColor);

	    label=new JLabel(resources.getString("fs.ffdL"));
	    JPanel ffdP=new JPanel();
	    ffdP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    ffdP.add(label);
	    ffdP.add(ffdTf);
	    ffdP.setBackground(RadQCFrame.bkgColor);

	    label=new JLabel(resources.getString("fs.ffdmpL"));
	    JPanel ffdmpP=new JPanel();
	    ffdmpP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    ffdmpP.add(label);
	    ffdmpP.add(ffdmpTf);
	    ffdmpP.setBackground(RadQCFrame.bkgColor);

	    label=new JLabel(resources.getString("fs.doriftesterL"));
	    JPanel doriftstP=new JPanel();
	    doriftstP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    doriftstP.add(label);
	    doriftstP.add(doriftesterTf);
	    doriftstP.setBackground(RadQCFrame.bkgColor);

	    label=new JLabel(resources.getString("fs.doriffilmL"));
	    JPanel dorifflmP=new JPanel();
	    dorifflmP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    dorifflmP.add(label);
	    dorifflmP.add(doriffilmTf);
	    dorifflmP.setBackground(RadQCFrame.bkgColor);

	    label=new JLabel(resources.getString("fs.grup.lpmmL"));
	    JPanel grupP=new JPanel();
	    grupP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    grupP.add(label);
	    grupP.add(fsgiCb);
	    label=new JLabel(resources.getString("fs.lpmmL"));
	    grupP.add(label);
	    grupP.add(lpmmTf);
	    grupP.add(fsgiCh);
	    grupP.setBackground(RadQCFrame.bkgColor);

	    label=new JLabel(resources.getString("fs.nominal"));
	    JPanel nomP=new JPanel();
	    nomP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    nomP.add(label);
	    nomP.add(nominalfsTf);
	    nomP.setBackground(RadQCFrame.bkgColor);

	    JPanel calcfsP=new JPanel();
	    calcfsP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    buttonName = resources.getString("computeB");
		buttonToolTip = resources.getString("computeB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("computeB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    calcfsP.add(button);
	    calcfsP.setBackground(RadQCFrame.bkgColor);

	    JPanel resultfsP=new JPanel(new BorderLayout());
	    JScrollPane jspres1=new JScrollPane();
	    jspres1.getViewport().add(fsTa, null);
	    resultfsP.add(jspres1,  BorderLayout.CENTER);
	    resultfsP.setBackground(RadQCFrame.bkgColor);
	    //===============
	    JPanel printP=new JPanel();
	    printP.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
	    buttonName = resources.getString("print.report");
		buttonToolTip = resources.getString("print.report.toolTip");
		buttonIconName = resources.getString("img.report");
		button = FrameUtilities.makeButton(buttonIconName, PRINT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("print.report.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		printP.add(button);
		printP.setBackground(RadQCFrame.bkgColor);
	    //========================
	    JPanel mainFsP = new JPanel();
	    BoxLayout bl2 = new BoxLayout(mainFsP,BoxLayout.Y_AXIS);
	    mainFsP.setLayout(bl2);
	    mainFsP.add(htstP, null);
	    mainFsP.add(hsptP, null);
	    mainFsP.add(ffdP, null);
	    mainFsP.add(ffdmpP, null);
	    mainFsP.add(doriftstP, null);
	    mainFsP.add(dorifflmP, null);
	    mainFsP.add(grupP, null);
	    mainFsP.add(nomP, null);mainFsP.add(puncP, null);
	    mainFsP.add(calcfsP, null);
	    mainFsP.add(resultfsP, null);
	    mainFsP.add(printP, null);
	    
	    fsjtb.add(mainFsP,resources.getString("fs.tab.tab1.title"));
	    //fsjtb.setSelectedIndex(0);//pata focala cu rmi112b!!!
		//=======================================================				
		JPanel mainP = new JPanel(new BorderLayout());
		//mainP.add(p6P, BorderLayout.NORTH);
		mainP.add(fsjtb, BorderLayout.CENTER);
		//mainP.add(p2P, BorderLayout.SOUTH);
		mainP.setBackground(RadQCFrame.bkgColor);
		return mainP;
		
	}
	
	/**
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		command = arg0.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {
			compute();
		}else if (command.equals(PRINT_COMMAND)) {
			printReport();
		} 
	}
	
	/**
	 * JCombobox specific actions are set here
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource()==fsgiCb)
	    {
		   setfsgi();
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
						
			String str = " \n" + fsTa.getText();
		
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
	 * Set the lpmm (line per mm) tester object
	 */
	private void setfsgi()
	{
		String s=(String)fsgiCb.getSelectedItem();
		int i=Convertor.stringToInt(s);
		String [] lp=(String[])this.resources.getObject("fs.fsgi.lpmm");
		lpmmTf.setText(lp[i-1]);
	}

	/**
	 * Compute focal spot specific parameters
	 */
	private void compute(){
		fstester=resources.getString("fs.tester.rmi112b");
		htesterd=0.0;//h tester
		hspatiatord=0.0;//h spatiator
	    doriftesterd=0.0;//dist orif tester
	    doriffilmd=0.0;//dist orif film
	    fsgi=0.0;//focal spot g(i)!!inca vizibil-lp/mm
	  	ffdmd=0.0;//ffd masurat
	  	ffdcd=0.0;//ffd calculat
	  	mmd=0.0;//M masurat real
	  	mcd=0.0;//M calculat
	  	mdiffd=0.0;//M diff %
	  	ffddiffd=0.0;//ffd diff %
	  	ffddiffmpd=0.0;//ffd diff max permitted %
	  	fsd=0.0;//focalspot
	  	fsnomd=0.0;//focalspot nom dat
	  	fsnommind=0.0;//focalspot nom minim
	  	fsnommaxd=0.0;//focalspot nom max
	  	String [] fsnomnema=(String[])this.resources.getObject("fs.NEMAstd.nom");//0.8,1.2
	  	String [] fseffmaxnema=(String[])this.resources.getObject("fs.NEMAstd.effmax");//1.6,2.4
	  	String [] fseffminnema=(String[])this.resources.getObject("fs.NEMAstd.effmin");//1.2,1.7
	  	ffdtestb=false;//for testresult
	  	fstestb=false;//for testresult

	  	double unc=0.0;
	  	
		boolean b=true;
		//----------calc
		String htsts=htesterTf.getText();
		String hspts=hspatiatorTf.getText();
		String ffds=ffdTf.getText();
		String doriftsts=doriftesterTf.getText();
		String dorifflms=doriffilmTf.getText();
		String fsgis=lpmmTf.getText();
		String fsnoms=nominalfsTf.getText();
		String ffddiffmps=ffdmpTf.getText();
		try
		{
		   htesterd=Convertor.stringToDouble(htsts);
		   hspatiatord=Convertor.stringToDouble(hspts);
		   ffdmd=Convertor.stringToDouble(ffds);
		   doriftesterd=Convertor.stringToDouble(doriftsts);
		   doriffilmd=Convertor.stringToDouble(dorifflms);
		   fsgi=Convertor.stringToDouble(fsgis);
		   fsnomd=Convertor.stringToDouble(fsnoms);
		   ffddiffmpd=Convertor.stringToDouble(ffddiffmps);
		   
		   unc=Convertor.stringToDouble(estimatedMeasurementUncertaintyTf.getText());
		}
		catch(Exception e)
		{
			b=false;
		    String title =resources.getString("number.error.title");
		    String message =resources.getString("number.error.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}

	    //---------------
		if(!b)
		   return;
		
	    //main calculation
	    double fsgi_1 = getPrecedentGroupLpPerMm();
	    fsgi_1 = getNextGroupLpPerMm();//unsharpness related to blur happens somewhere between these groups (last visible and next one)
	//System.out.println("precedent "+fsgi_1);
	    double flmtst=htesterd+hspatiatord;//film-tester distance-->fixed!!
	    double focustst=ffdmd-flmtst;//focus-tester distance-->based on measured FFD!!
		if ((focustst==0 || doriftesterd==0) || (doriffilmd==0 || flmtst==0))
		{
			b=false;
		    String title =resources.getString("number.error.title");
		    String message =resources.getString("dialog.fscalc.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}
		if(!b)
		   return;
		mmd=doriffilmd/doriftesterd;//not null
	    mcd=(flmtst+focustst)/focustst;//"computed" M=ffd/focustst!!
	    mdiffd=100*Math.abs(mmd-mcd)/mmd;
		if (mmd==1)
		{
			b=false;
		    String title =resources.getString("number.error.title");
		    String message =resources.getString("dialog.fscalc.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}
		if(!b)
		   return;
		/*
		 M = Image size/Object size or equivalently (Thales theorem):
		 M=SID/SOD (source to image distance/source to object distance); SID = SOD+OID (object to image distance, tester to film)
		 M=SID/(SID-OID)=>MxSID-MxOID=SID=>SID(M-1)=M x OID=> SID=OID x M/(M-1)
		 */
		ffdcd=flmtst*mmd/(mmd-1);//not null
		//------------------UNC-------------------
		StatsUtil.confidenceLevel = 0.95;
		double ffdcd_unc=ffdcd*unc/100.0;
		double ffdmd_unc=ffdmd*0.1/100.0;//0.1.%
		double f_degrees=StatsUtil.evaluateDegreesOfFreedom(ffdcd_unc, ffdcd);
		double f_poisson=StatsUtil.evaluateDegreesOfFreedom(ffdmd_unc, ffdmd);
		boolean diffB = StatsUtil.ttest_default_unc(ffdcd, ffdmd, 
				ffdcd_unc,ffdmd_unc, f_degrees,	f_poisson);
	
		//if (ffdcd<=ffdmd){//here, either way is bad
		//	diffB=false;
		//}
		//-----------
		ffddiffd=100*Math.abs(ffdcd-ffdmd)/ffdcd;
		if (ffddiffd<=ffddiffmpd)
			ffdtestb=true;
		else
		{
			//ffdtestb=false;
			if (diffB)//!!!!!!!!!!!!!!!!!!!!        
				ffdtestb=false;
        	else
        		ffdtestb=true;
		}
		//--------------------------------------------------------------------------
		double fsdei=mmd/(fsgi*(mmd-1));
		double fsdei_1=mmd/(fsgi_1*(mmd-1));
		fsd=(fsdei+fsdei_1)/2;

		if (fsgiCh.isSelected())
		{
			fsd=fsdei;//pt utilizare si cu alte testere..NU MEDIE=>only using last group!!
		}

		double min2=0.0;
		double min1=0.0;
		double nom2=0.0;
		double nom1=0.0;
		double max2=0.0;
		double max1=0.0;
		//==================
		if(fsnomd<=Convertor.stringToDouble(fsnomnema[1]))
		{
			min1=Convertor.stringToDouble(fseffminnema[0]);
			min2=Convertor.stringToDouble(fseffminnema[1]);
			nom1=Convertor.stringToDouble(fsnomnema[0]);
			nom2=Convertor.stringToDouble(fsnomnema[1]);
			max1=Convertor.stringToDouble(fseffmaxnema[0]);
			max2=Convertor.stringToDouble(fseffmaxnema[1]);
		}
		else if(fsnomd<=Convertor.stringToDouble(fsnomnema[2]))
		{
			min1=Convertor.stringToDouble(fseffminnema[1]);
			min2=Convertor.stringToDouble(fseffminnema[2]);
			nom1=Convertor.stringToDouble(fsnomnema[1]);
			nom2=Convertor.stringToDouble(fsnomnema[2]);
			max1=Convertor.stringToDouble(fseffmaxnema[1]);
			max2=Convertor.stringToDouble(fseffmaxnema[2]);
		}
		else //if(fsnomd<=Convertor.stringToDouble(fsnomnema[3]))
		{
			min1=Convertor.stringToDouble(fseffminnema[2]);
			min2=Convertor.stringToDouble(fseffminnema[3]);
			nom1=Convertor.stringToDouble(fsnomnema[2]);
			nom2=Convertor.stringToDouble(fsnomnema[3]);
			max1=Convertor.stringToDouble(fseffmaxnema[2]);
			max2=Convertor.stringToDouble(fseffmaxnema[3]);
		}
//		else
//		{
	//
//		}

		//==============
		fsnommind=fsnomd*(min2-min1)/(nom2-nom1)+(nom2*min1-nom1*min2)/(nom2-nom1);
		fsnommaxd=fsnomd*(max2-max1)/(nom2-nom1)+(nom2*max1-nom1*max2)/(nom2-nom1);
		
		//unc
		double fsd_unc=fsd*unc/100.0;
		double fsnommind_unc=fsnommind*0.1/100.0;//0.1.%
		f_degrees=StatsUtil.evaluateDegreesOfFreedom(fsd_unc, fsd);
		f_poisson=StatsUtil.evaluateDegreesOfFreedom(fsnommind_unc, fsnommind);
		boolean diffBmin = StatsUtil.ttest_default_unc(fsd, fsnommind, 
				fsd_unc,fsnommind_unc, f_degrees,	f_poisson);
		
//System.out.println("fs "+fsd+" +/- "+fsd_unc+" deg "+f_degrees);
//System.out.println("fsnommind "+fsnommind+" +/- "+fsnommind_unc+" deg "+f_poisson);
//System.out.println("diff "+diffBmin);
		
		double fsnommaxd_unc=fsnommaxd*0.1/100.0;//0.1.%
		//f_degrees=StatsUtil.evaluateDegreesOfFreedom(fsd_unc, fsd);
		f_poisson=StatsUtil.evaluateDegreesOfFreedom(fsnommaxd_unc, fsnommaxd);
		boolean diffBmax = StatsUtil.ttest_default_unc(fsd, fsnommaxd, 
				fsd_unc,fsnommaxd_unc, f_degrees,	f_poisson);
		
//System.out.println("fs "+fsd+" +/- "+fsd_unc+" deg "+f_degrees);
//System.out.println("fsnommaxd "+fsnommaxd+" +/- "+fsnommaxd_unc+" deg "+f_poisson);
//System.out.println("diff "+diffBmax);
		//--------------------------
		if(fsnommind<=fsd && fsd<=fsnommaxd)
			fstestb=true;//true pass
		else
		{
			boolean minb=false;
			//fstestb=false;
			if (diffBmin && fsnommind>fsd)
			{
				minb=false;//true fail
				fstestb=false;
			}
        	else
        		minb=true;//next check
			
			if (minb)
				if (diffBmax && fsnommaxd<fsd)       
					fstestb=false;//true fail
	        	else
	        		fstestb=true;
		}
		//------clear first
		fsTa.selectAll();
		fsTa.replaceSelection("");
		//----------------
	    NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumFractionDigits(2);//default e 2 oricum!!
	    nf.setMaximumFractionDigits(2);//default e 2 oricum!!
	    nf.setGroupingUsed(false);//no 4,568.02 but 4568.02

		fsTa.append(
					resources.getString("fs.rezultat.mag")+nf.format(mmd)+"  \n"+
					resources.getString("fs.rezultat.ffdm")+nf.format(ffdmd)+"  \n"+
					resources.getString("fs.rezultat.ffdc")+nf.format(ffdcd)+"  \n"+
					resources.getString("fs.rezultat.ffddiff")+nf.format(ffddiffd)+"  \n"+
					resources.getString("fs.rezultat.ffddiffmp")+nf.format(ffddiffmpd)+"  \n");
		if (ffdtestb)
		   	fsTa.append(resources.getString("rezultat.ffd")+
				   	resources.getString("rezultat.succes")+" (95% confidence level)"+"  \n");
		else
		   	fsTa.append(resources.getString("rezultat.ffd")+
		       	resources.getString("rezultat.fail")+" (95% confidence level)"+"  \n");
		fsTa.append("------------------------\n");
		fsTa.append(
					resources.getString("fs.rezultat.fs.nom")+nf.format(fsnomd)+"  \n"+
					resources.getString("fs.rezultat.fs.min")+nf.format(fsnommind)+"  \n"+
					resources.getString("fs.rezultat.fs.max")+nf.format(fsnommaxd)+"  \n"+
					resources.getString("fs.rezultat.fs.calc")+nf.format(fsd)+"  \n");

		if (fstestb)
		   	fsTa.append(resources.getString("rezultat.fs")+
		       	resources.getString("rezultat.succes")+" (95% confidence level)"+"  \n");
		else
		   	fsTa.append(resources.getString("rezultat.fs")+
		       	resources.getString("rezultat.fail")+" (95% confidence level)"+"  \n");
	}

	/**
	 * Internally used. Get precedent lpmm tester group
	 * @return the result
	 */
	private double getPrecedentGroupLpPerMm()
	  {
		  double result=0.0;
		  /*
		   String s=(String)fsgiCb.getSelectedItem();
		int i=Convertor.stringToInt(s);
		String [] lp=(String[])this.resources.getObject("fs.fsgi.lpmm");
		lpmmTf.setText(lp[i-1]);
		   */
		  //-------------------
	      String s="";
	      String [] lpms=(String[])this.resources.getObject("fs.fsgi.lpmm");	      		      
	      int index=fsgiCb.getSelectedIndex();
	      //int index=Convertor.stringToInt((String)fsgiCb.getSelectedItem());
	      if (index!=0)
	      	s=lpms[index-1];//precedent -1
	      else
	      	s=lpms[0];
	      result=Convertor.stringToDouble(s);
		  //-----------------
		  return result;
	  }
	
	/**
	 * Internally used. Get next lpmm tester group
	 * @return the result
	 */
	private double getNextGroupLpPerMm()
	  {
		  double result=0.0;
		  //-------------------
	      String s="";
	      String [] lpms=(String[])this.resources.getObject("fs.fsgi.lpmm");
	      int index=fsgiCb.getSelectedIndex();
	      if (index!=lpms.length-1)
	      	s=lpms[index+1];//next
	      else
	      	s=lpms[lpms.length-1];
	      result=Convertor.stringToDouble(s);
		  //-----------------
		  return result;
	  }
}
