package sensitometry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.pdfbox.pdmodel.PDDocument;

import danfulea.utils.ExampleFileFilter;
import danfulea.math.Convertor;
import danfulea.math.Sort;
import danfulea.math.StatsUtil;
import danfulea.math.numerical.Function;
import danfulea.math.numerical.ModelingData;
import danfulea.utils.FrameUtilities;
import danfulea.utils.PDFRenderer;
import radQC.RadQCFrame;

/**
 * Film sensitometry QC test. It involves computation of: base + fog, the contrast, film speed, latitude 
 * and the sensitometric HD curve.
 * 
 * @author Dan Fulea, 28 Apr. 2015
 */
@SuppressWarnings("serial")
public class Sensitometry extends JFrame implements ActionListener, Function{
	private static final String BASE_RESOURCE_CLASS = "sensitometry.resources.SensitometryResources";
	protected ResourceBundle resources;
	private final Dimension PREFERRED_SIZE = new Dimension(800, 750);
	private static final Dimension sizeTab = new Dimension(180,370);
	//private static final Dimension textAreaDimension = new Dimension(700, 400);
	private RadQCFrame mf;
	protected String outFilename = null;
	
	private static final String ADDROW_COMMAND = "ADDROW";
	private static final String DELETEROW_COMMAND = "DELETEROW";
	//private static final String PLOT_COMMAND = "PLOT";
	private static final String SAVE_COMMAND = "SAVE";//changed to print instead!!!
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private String command = null;
	
	private String[] names;
	private Vector<String> colNames = new Vector<String>();
	
	protected SensiSortableTablePanel sensiPan;
	protected JPanel suportsensiPan = new JPanel();
	protected JTextArea sensiTa = new JTextArea();
	protected JTextField dobfmpTf = new JTextField(5);//DO base+fog max. permitted
	protected JTextField contrastminTf = new JTextField(5);//contrast-gamma-min.permitted
	protected JTextField contrastmaxTf = new JTextField(5);//contrast-gamma-max.permitted
	protected JTextField setmidTf = new JTextField(5);//mid DO minimum
	protected JTextField setlowTf = new JTextField(5);//low DO minimum
	protected JTextField sethighTf = new JTextField(5);//high DO minimum
	protected JCheckBox fitCh;
	
	private JTextField estimatedMeasurementUncertaintyTf = new JTextField(3);
	
	//==========
	@SuppressWarnings({ "rawtypes" })
	private Vector stepv=new Vector();//sensitometric step wedge vector
	@SuppressWarnings({ "rawtypes" })
	private Vector logev=new Vector();//logE vector
	@SuppressWarnings({ "rawtypes" })
	private Vector dov=new Vector();//DO vector
	protected double[] stepd=new double[0];
	protected double[] loged=new double[0];
	protected double[] dod=new double[0];
	protected double midd=0.0;//DOmid minim
	protected double lowd=0.0;//DOlow minim
	protected double highd=0.0;//DOhigh minim
	protected double dobfmaxd=0.0;////DO base+fog max. permitted
	protected boolean dobfb=false;//for testresult
	protected double contrastmind=0.0;////contrast min. permitted
	protected double contrastmaxd=0.0;////contrast max. permitted
	protected boolean contrastb=false;//for testresult
	protected double contrast=0.0;
	protected double averageGradient=0.0;
	protected double latitudine=0.0;
	protected double speed=0.0;
	protected double basefog=0.0;
	@SuppressWarnings("unused")
	private int nsensiPoints=0;//just in case!!
	//pentru grafic
	protected double lwg=0.0;
	protected int lwpozg=0;
	protected double mdg=0.0;
	protected int mdpozg=0;
	protected double hghg=0.0;
	protected int hghpozg=0;
	//==========
	public double c1=0.0;
	public double c2=0.0;
	public double x0=0.0;
	public double dx=0.0;
	protected boolean fitB=false;
	protected boolean showB=false;
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public Sensitometry(RadQCFrame mf){
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("Sensitometry.NAME"));
		this.mf=mf;
		
		//----------some initializations----------
		names = (String[])resources.getObject("tab.columns");
		for (int i=0; i<names.length; i++)
	    	colNames.addElement(names[i]);
		
		final Object[][] data =
	    {
		    {new Integer(1),new Double(0.15),new Double(0.18)},
		    {new Integer(2),new Double(0.3),new Double(0.18)},
		    {new Integer(3),new Double(0.45),new Double(0.18)},
		    {new Integer(4),new Double(0.6),new Double(0.18)},
		    {new Integer(5),new Double(0.75),new Double(0.19)},
		    {new Integer(6),new Double(0.9),new Double(0.19)},
		    {new Integer(7),new Double(1.05),new Double(0.19)},
		    {new Integer(8),new Double(1.2),new Double(0.21)},
		    {new Integer(9),new Double(1.35),new Double(0.23)},
		    {new Integer(10),new Double(1.5),new Double(0.3)},
		    {new Integer(11),new Double(1.65),new Double(0.39)},
		    {new Integer(12),new Double(1.8),new Double(0.57)},
		    {new Integer(13),new Double(1.95),new Double(0.9)},
		    {new Integer(14),new Double(2.1),new Double(1.36)},
		    {new Integer(15),new Double(2.25),new Double(1.86)},
		    {new Integer(16),new Double(2.4),new Double(2.33)},
		    {new Integer(17),new Double(2.55),new Double(2.55)},
		    {new Integer(18),new Double(2.7),new Double(2.7)},
		    {new Integer(19),new Double(2.85),new Double(2.77)},
		    {new Integer(20),new Double(3.0),new Double(2.8)},
		    {new Integer(21),new Double(3.15),new Double(2.81)},
	    };
		sensiPan=new SensiSortableTablePanel(data,names);
		
		createGUI();
		//populateFromDb();
		
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
		
		//selection in tables
        JTable tab = sensiPan.getTab();
		//int rowTableCount=tab.getRowCount();
		tab.setRowSelectionInterval(0,0);
		dobfmpTf.setText("0.2");
	  	contrastminTf.setText("2.8");
	  	contrastmaxTf.setText("3.2");
	  	setmidTf.setText("1.2");
	  	setlowTf.setText("0.45");
	  	sethighTf.setText("2.2");
	}
	
	/**
	 * Create main panel
	 * @return the result
	 */
	private JPanel createMainPanel() {
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		fitCh=new JCheckBox(resources.getString("sensi.fitCh"),false);
		fitCh.setToolTipText(resources.getString("sensi.fitCh.tooltip"));
		
		estimatedMeasurementUncertaintyTf.setText("5");
	    JPanel puncP=new JPanel();
		puncP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
        label=new JLabel(resources.getString("expData.unc"));
        puncP.add(label);
        puncP.add(estimatedMeasurementUncertaintyTf);        
        puncP.setBackground(RadQCFrame.bkgColor);
		
		sensiTa.setCaretPosition(0);
	    sensiTa.setEditable(false);
	    sensiTa.setText(resources.getString("rezultat"));
	    sensiTa.setLineWrap(true);
	    sensiTa.setWrapStyleWord(true);
		//==============
	    JPanel bfP=new JPanel();
	    bfP.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    JLabel bfL=new JLabel(resources.getString("sensi.bfL"));
	    bfP.add(bfL);
	    bfP.add(dobfmpTf);
	    bfP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel contrastP=new JPanel();
	    contrastP.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    JLabel cminL=new JLabel(resources.getString("sensi.contrast.minL"));
	    JLabel cmaxL=new JLabel(resources.getString("sensi.contrast.maxL"));
	    contrastP.add(cminL);
	    contrastP.add(contrastminTf);
	    contrastP.add(cmaxL);
	    contrastP.add(contrastmaxTf);
	    contrastP.setBackground(RadQCFrame.bkgColor);
	    JPanel lowP=new JPanel();
	    lowP.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    JLabel lowL=new JLabel(resources.getString("sensi.lowL"));
	    lowP.add(lowL);
	    lowP.add(setlowTf);
	    lowP.setBackground(RadQCFrame.bkgColor);
	    JPanel midP=new JPanel();
	    midP.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    JLabel midL=new JLabel(resources.getString("sensi.midL"));
	    midP.add(midL);
	    midP.add(setmidTf);
	    midP.setBackground(RadQCFrame.bkgColor);
	    JPanel highP=new JPanel();
	    highP.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    JLabel highL=new JLabel(resources.getString("sensi.highL"));
	    highP.add(highL);
	    highP.add(sethighTf);
	    highP.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel pP=new JPanel();
	    pP.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
	    label = new JLabel(resources.getString("tab.related.Label"));
	    pP.add(label);
	    pP.setBackground(RadQCFrame.bkgColor);
	    
	    suportsensiPan.setLayout(new BorderLayout());
	    suportsensiPan.add(sensiPan, BorderLayout.CENTER);
	    suportsensiPan.setPreferredSize(sizeTab);
	    JPanel butP=new JPanel();
	    butP.setLayout(new FlowLayout(FlowLayout.CENTER, 20,2));
	    buttonName = resources.getString("sensi.addrB");
		buttonToolTip = null;//resources.getString("computeB.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, ADDROW_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("sensi.delrB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    butP.add(button);
	    buttonName = resources.getString("sensi.delrB");
		buttonToolTip = null;//resources.getString("computeB.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETEROW_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("sensi.delrB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    butP.add(button);
	    //butP.add(sa.graphB);
	    butP.add(fitCh);
	    buttonName = resources.getString("sensi.calcB");
		buttonToolTip = null;//resources.getString("computeB.toolTip");
		buttonIconName = resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("sensi.calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    butP.add(button);
	    buttonName = resources.getString("saveB");
		buttonToolTip = resources.getString("saveB.toolTip");
		buttonIconName = resources.getString("img.report");//view");
		button = FrameUtilities.makeButton(buttonIconName, SAVE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("saveB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		butP.add(button);
		butP.setBackground(RadQCFrame.bkgColor);
	    //North sensipanel
	    JPanel northP=new JPanel();
	    BoxLayout bl = new BoxLayout(northP,BoxLayout.Y_AXIS);
	    northP.setLayout(bl);
	    northP.add(bfP);
	    northP.add(contrastP);
	    northP.add(lowP);
	    northP.add(midP);
	    northP.add(highP);northP.add(puncP);puncP.add(pP);
	    //northP.add(pP);
	    northP.add(suportsensiPan);
	    northP.add(butP);
	    //result panel=south panel
	    JPanel resultsensiP=new JPanel(new BorderLayout());
	    JScrollPane jspres=new JScrollPane();
	    jspres.getViewport().add(sensiTa, null);
	    resultsensiP.add(jspres,  BorderLayout.CENTER);
	    resultsensiP.setBackground(RadQCFrame.bkgColor);
	    //resultsensiP.setPreferredSize(textAreaDimension);
	    //main=sensi
	    JPanel mainP=new JPanel(new BorderLayout());
	    mainP.add(northP, BorderLayout.NORTH);
	    mainP.add(resultsensiP, BorderLayout.CENTER);
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
		} else if (command.equals(ADDROW_COMMAND)) {
			addRow();
		} else if (command.equals(DELETEROW_COMMAND)) {
			deleteRow();
		} //else if (command.equals(PLOT_COMMAND)) {
			//plot();
		//} 
		else if (command.equals(SAVE_COMMAND)) {
			save();
		}		
	}
	
	/**
	 * Add a row in table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addRow(){
		JTable tab=sensiPan.getTab();
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
			if (j==0)
			{
				rowV.addElement(new Integer(rowTableCount+1));
			}
			else
				rowV.addElement(null);
		}
	    dataV.addElement(rowV);
		//end
	    //constructing the new table
	    suportsensiPan.remove(sensiPan);
	    sensiPan=new SensiSortableTablePanel(dataV,colNames);
		suportsensiPan.add(sensiPan,BorderLayout.CENTER);
		tab=sensiPan.getTab();
	    rowTableCount=tab.getRowCount();
	    tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);
		validate();
	}
	
	/**
	 * Delete a row from table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void deleteRow(){
		JTable tab=sensiPan.getTab();
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
				dataV.removeElementAt(selRows[i]-j);
				j++;
	    	}
	        //refreshing the step
	        for(int i=0; i<rowTableCount-selRows.length;i++)
			{
				rowV=(Vector)dataV.elementAt(i);
	            rowV.set(0,new Integer(i+1));
	            dataV.set(i,rowV);
	        }
		    //constructing the new table
		    suportsensiPan.remove(sensiPan);
		    sensiPan=new SensiSortableTablePanel(dataV,colNames);
			suportsensiPan.add(sensiPan,BorderLayout.CENTER);

			tab=sensiPan.getTab();
			rowTableCount=tab.getRowCount();
			if (rowTableCount>0)
				tab.setRowSelectionInterval(rowTableCount-1,rowTableCount-1);
			validate();
		}
	}
	
	//private void plot(){
		
	//}
	/**
	 * Print report
	 */
	private void save(){
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
						
			String str = " \n" + sensiTa.getText();
		
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
	 * Perform film sensitometry QC tests.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void compute(){
		showB=false;
	  	fitB=false;
	    //---zero initialization
	 	stepv=new Vector();//sensitometric step wedge vector
	 	logev=new Vector();//logE vector
	 	dov=new Vector();//DO vector
	    stepd=new double[0];
	    loged=new double[0];
	    dod=new double[0];
	    midd=0.0;//DOmid
	    lowd=0.0;//DOlow
	    highd=0.0;//DOhigh
	    dobfmaxd=0.0;////DO base+fog max. permitted
	    dobfb=false;//for testresult
	    contrastmind=0.0;////contrast min. permitted
	    contrastmaxd=0.0;////contrast max. permitted
	    contrastb=false;//for testresult
	  	contrast=0.0;
	  	averageGradient=0.0;
	  	latitudine=0.0;
	  	speed=0.0;
	  	basefog=0.0;

	  	double unc=0.0;
	  	
		boolean b=true;//local

	    double d1=0.0;//for not null testing in table
	    double d2=0.0;//for not null testing in table
	    
	    try
	    {
		    dobfmaxd=Convertor.stringToDouble(dobfmpTf.getText());
		    contrastmind=Convertor.stringToDouble(contrastminTf.getText());
		    contrastmaxd=Convertor.stringToDouble(contrastmaxTf.getText());
		    lowd=Convertor.stringToDouble(setlowTf.getText());
		    midd=Convertor.stringToDouble(setmidTf.getText());
		    highd=Convertor.stringToDouble(sethighTf.getText());
		    
		    unc=Convertor.stringToDouble(estimatedMeasurementUncertaintyTf.getText());
		}
		catch(Exception e)
		{
			b=false;
		    String title =resources.getString("number.error.title");
		    String message =resources.getString("number.error.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}

		if(!b)
		{
		    return;
	    }
		
		//next-calculation
		JTable tab=sensiPan.getTab();
		int rowTableCount=tab.getRowCount();
		
		if (rowTableCount<3){
			String title =resources.getString("number.error.title");
		    String message =resources.getString("number.error.message2");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		for(int i=0; i<rowTableCount; i++)
		{
			try
			{
				d1=((Double)tab.getValueAt(i,1)).doubleValue();
				d2=((Double)tab.getValueAt(i,2)).doubleValue();
			}
			catch(Exception e)
			{
				b=false;
			    String title =resources.getString("number.error.title");
			    String message =resources.getString("number.error.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			    break;
			}

			if(d1<=0.0 || d2<=0.0)//explicit 0.0 error
			{
				b=false;
			    String title =resources.getString("number.error.title");
			    String message =resources.getString("number.error.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			    break;
			}
            //--now we sure that vectors contain only double and positive objects!!
	        for (int j=0; j<logev.size(); j++)
			if(((Double)logev.elementAt(j)).doubleValue()>=d1)
			{
				b=false;
	            String title =resources.getString("number.error.title");
		        String message =resources.getString("number.noDuplicateAscend.message");
		        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		        break;
			}
		//--now we sure that arrays contain only double,no duplicate and positive objects!!
			stepv.addElement(tab.getValueAt(i,0));
			logev.addElement(tab.getValueAt(i,1));
		    dov.addElement(tab.getValueAt(i,2));
		}
		if(!b)
		{
		    return;
	    }

        //finally :
		//loged=convertVectorToDoubleArray(logev);
        //dod=convertVectorToDoubleArray(dov);
        //finally optimisation:
        convertAllVectorsToDoubleArray(stepv,logev,dov);

		basefog=dod[0];
		
		//unc
		StatsUtil.confidenceLevel = 0.95;
		double basefog_unc=basefog*unc/100.0;
		double dobfmaxd_unc=dobfmaxd*0.1/100.0;//0.1.%
		double f_degrees=StatsUtil.evaluateDegreesOfFreedom(basefog_unc, basefog);
		double f_poisson=StatsUtil.evaluateDegreesOfFreedom(dobfmaxd_unc, dobfmaxd);
		boolean diffB = StatsUtil.ttest_default_unc(basefog, dobfmaxd, 
				basefog_unc,dobfmaxd_unc, f_degrees,	f_poisson);
		//========
		
		if(basefog<=dobfmaxd)
			dobfb=true;
		else{
			//ffdtestb=false;
			if (diffB)//!!!!!!!!!!!!!!!!!!!!        
				dobfb=false;
        	else
        		dobfb=true;
		}
			//dobfb=false;
    	double lw=Sort.findNearestValue(dod, lowd, false);lwg=lw;
    	int lwpoz=Sort.getNearestPosition();lwpozg=lwpoz;
        double md=Sort.findNearestValue(dod, midd, false);mdg=md;
        int mdpoz=Sort.getNearestPosition();mdpozg=mdpoz;
        double hgh=Sort.findNearestValue(dod, highd, false);hghg=hgh;
        int hghpoz=Sort.getNearestPosition();hghpozg=hghpoz;

        double latitudeDO=0.0;
        double speed2=0.0;
        
		if (fitCh.isSelected())
		{
			//double fogLevel=loged[0];
			double fogLevelDO=dod[0];
			
			ModelingData.func=this;
			fitB=true;
			int ndat=dod.length;
			double[] ssig=new double[ndat];
			for (int i=1;i<=ndat;i++)
			{
				ssig[i-1]=1.0;
			}

			int mma=4;
			double[] acof=new double[mma];
			acof[0]=0.0;//fogLevelDO;//0.0;//first guess
			acof[1]=dod[ndat-1];//maximum dose first guess//loged[ndat-1]+1.0;
			//middle of stepwedge plus offset of 2 stepsizes where median is more likely to occur.
			acof[2]=(loged[ndat-1]+loged[0])/2.0+2.0*(loged[1]-loged[0]);//(acof[0]+acof[1])/2.0;
			acof[3]=loged[1]-loged[0];//stepsize

			int[] iia={1,1,1,1};
			double[][] ccovar=new double[mma][mma];
			double[][] alph=new double[mma][mma];

			ModelingData.mrqmin(loged, dod, ssig, ndat, acof,iia, mma, ccovar, alph,-1.0);
			if (ModelingData.failB)
			{
				return;
			}
			@SuppressWarnings("unused")
			int iter=0;
			for (int i=1;i<=100;i++)
			{
				iter++;
				ModelingData.mrqmin(loged, dod, ssig, ndat, acof,iia, mma, ccovar, alph,ModelingData.alamda_mrqmin);
				if (ModelingData.failB)
				{
					return;
				}

				if (ModelingData.convB)
					break;
			}
			ModelingData.mrqmin(loged, dod, ssig, ndat, acof,iia, mma, ccovar, alph,0.0);
			if (ModelingData.failB)
			{
				return;
			}
			//System.out.println(" acof0 "+acof[0]);//ok near base+fog
			//gamma is contrast which is the tangent (slope) of straight line portion of the graph
			double dooo=1.0+fogLevelDO;
			speed=acof[2]+acof[3]*Math.log((acof[0]-dooo)/(dooo-acof[1]));;//1.0+fogLevelDO;//in DO
			speed2=loged[mdpoz];//md;
			double do1=0.25+fogLevelDO;
			double do2=2.0+fogLevelDO;
			double loge1=acof[2]+acof[3]*Math.log((acof[0]-do1)/(do1-acof[1]));
			double loge2=acof[2]+acof[3]*Math.log((acof[0]-do2)/(do2-acof[1]));			
			double avegama=(do2-do1)/(loge2-loge1);//1/(loge2-loge1);
			averageGradient=avegama;//not average gradient here. ave gradient is for double do1=0.25+fogLevelDO; and do2=2.0+fogLevelDO;
			//=========
			do1=lw;//lw//1.0+fogLevelDO;
			do2=hgh;//2.0+fogLevelDO;
			loge1=acof[2]+acof[3]*Math.log((acof[0]-do1)/(do1-acof[1]));
			loge2=acof[2]+acof[3]*Math.log((acof[0]-do2)/(do2-acof[1]));
			double gamma=(do2-do1)/(loge2-loge1);
			contrast=gamma;
			//=============
			double latDOmin=lw;
			double logemin=acof[2]+acof[3]*Math.log((acof[0]-latDOmin)/(latDOmin-acof[1]));
			double latloge=loge2-logemin;
			latitudine=latloge;//in LOGE
			latitudeDO=hgh-lw;
			showB=true;
			c1=acof[0];
			c2=acof[1];
			x0=acof[2];
			dx=acof[3];
		}
		else
		{
			fitB=false;
			speed=loged[mdpoz];//md;//in DO but median
			double fogLevelDO=dod[0];
			speed2=1.0+fogLevelDO;
			latitudine=hgh-lw;//in DO
			double num=loged[hghpoz]-loged[lwpoz];
			latitudeDO=num;//here is in LOGE units
			if (num!=0)
				contrast=latitudine/num;
			else
				contrast=0.0;
        }
		
		//unc============
		double contrast_unc=contrast*unc/100.0;
		double contrastmind_unc=contrastmind*0.1/100.0;//0.1.%
		f_degrees=StatsUtil.evaluateDegreesOfFreedom(contrast_unc, contrast);
		f_poisson=StatsUtil.evaluateDegreesOfFreedom(contrastmind_unc, contrastmind);
		boolean diffBmin = StatsUtil.ttest_default_unc(contrast, contrastmind, 
				contrast,contrastmind_unc, f_degrees,	f_poisson);
		
//System.out.println("fs "+fsd+" +/- "+fsd_unc+" deg "+f_degrees);
//System.out.println("fsnommind "+fsnommind+" +/- "+fsnommind_unc+" deg "+f_poisson);
//System.out.println("diff "+diffBmin);
		
		double contrastmaxd_unc=contrastmaxd*0.1/100.0;//0.1.%
		//f_degrees=StatsUtil.evaluateDegreesOfFreedom(fsd_unc, fsd);
		f_poisson=StatsUtil.evaluateDegreesOfFreedom(contrastmaxd_unc, contrastmaxd);
		boolean diffBmax = StatsUtil.ttest_default_unc(contrast, contrastmaxd, 
				contrast_unc,contrastmaxd_unc, f_degrees,	f_poisson);
		//==================
		if(contrast<=contrastmaxd && contrast>=contrastmind)
			contrastb=true;
		else{
			//contrastb=false;
			boolean minb=false;
			//fstestb=false;
			if (diffBmin && contrastmind>contrast)
			{
				minb=false;//fail
				contrastb=false;
			}
        	else
        		minb=true;
			
			if (minb)
				if (diffBmax && contrastmaxd<contrast)//and also outside interval if here        
					contrastb=false;//fail
	        	else
	        		contrastb=true;
		}

		//show the result
		//	------clear first
		sensiTa.selectAll();
		sensiTa.replaceSelection("");
		//----------------
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumFractionDigits(2);//default e 2 oricum!!
    	nf.setMaximumFractionDigits(2);//default e 2 oricum!!
    	nf.setGroupingUsed(false);//no 4,568.02 but 4568.02

	    sensiTa.append(
				resources.getString("sensi.rezultat.bfmp")+nf.format(dobfmaxd)+";  "+
				resources.getString("sensi.rezultat.bf")+nf.format(basefog)+"  \n");

        if (dobfb)
	        	sensiTa.append(resources.getString("rezultat")+
	        	resources.getString("rezultat.succes")+" (95% confidence level)"+"  \n");
        else
	        	sensiTa.append(resources.getString("rezultat")+
	        	resources.getString("rezultat.fail")+" (95% confidence level)"+"  \n");
	    sensiTa.append(
				resources.getString("sensi.rezultat.contrast.min")+nf.format(contrastmind)+";  "+
				resources.getString("sensi.rezultat.contrast.max")+nf.format(contrastmaxd)+";  "+
				resources.getString("sensi.rezultat.contrast")+nf.format(contrast)+"  \n");
	    if (fitB){
	    	sensiTa.append(resources.getString("sensi.rezultat.avegradient")+nf.format(averageGradient)+"  \n");
	    }
        
        if (contrastb)
	        	sensiTa.append(resources.getString("rezultat")+
	        	resources.getString("rezultat.succes")+" (95% confidence level)"+"  \n");
        else
	        	sensiTa.append(resources.getString("rezultat")+
	        	resources.getString("rezultat.fail")+" (95% confidence level)"+"  \n");
        
        if (fitB){
        	sensiTa.append(
    				resources.getString("sensi.rezultat.latitudine")+nf.format(latitudeDO)+"  \n");
        	sensiTa.append(
				resources.getString("sensi.rezultat.latitudine.loge")+nf.format(latitudine)+"  \n");
        }else{
        	sensiTa.append(
    				resources.getString("sensi.rezultat.latitudine")+nf.format(latitudine)+"  \n");
        	sensiTa.append(
    				resources.getString("sensi.rezultat.latitudine.loge")+nf.format(latitudeDO)+"  \n");
        }
        
        if (fitB){
	    sensiTa.append(
				resources.getString("sensi.rezultat.speed2")+nf.format(speed)+"  \n");
	    sensiTa.append(
				resources.getString("sensi.rezultat.speed")+nf.format(speed2));
        }else{
        	sensiTa.append(
    				resources.getString("sensi.rezultat.speed")+nf.format(speed));//+"  \n");
        	//sensiTa.append(
    				//resources.getString("sensi.rezultat.speed2")+nf.format(speed2));
        }
	    
	    //now the graph
	    new SensiGraph(this);
	}

	/**
	 * Convert vectors to arrays
	 * @param v v
	 * @param v1 v1
	 * @param v2 v2
	 */
	@SuppressWarnings("rawtypes")
	private void convertAllVectorsToDoubleArray(Vector v,Vector v1,Vector v2)
	  {
	        stepd=new double[v.size()];
	        loged=new double[v.size()];
	        dod=new double[v.size()];
			//all 3 vectors=same size
			for(int i=0; i<v.size(); i++)
			{
				double s=((Integer)v.elementAt(i)).doubleValue();//step
				double s1=((Double)v1.elementAt(i)).doubleValue();//loge
				double s2=((Double)v2.elementAt(i)).doubleValue();//do
				stepd[i]=s;
				loged[i]=s1;
				dod[i]=s2;
			}
	  }
	//==========================INTERFACE================
	/**
	 * Interface specific methods
	 */
	@Override
	//derivatives for F function
	//F=a1+(a0-a1)/(1+exp[(x-a2)/a3]) or F=c2+(c1-c2)/(1+exp[(x-x0)/dx]) 
	//this is the fitting function for H-D curve where fitting parameters represents:
	//a0=c1=OD for base plus fog, or OD for step 1 in stepwedge bar
	//a1=c2=maximum OD or OD for last step in stepwedge bar
	//a2=x0=median log of expoure, the median stepwedge value in logE rel.unit.
	//a3=dx=stepsize, loge1-log0 for instance
	public double fdf(double x, double[] a, double[] dyda, int na) {
		// TODO Auto-generated method stub
		double y=0.0;
		double expterm=Math.exp((x-a[2])/a[3]);
		double num=1.0+expterm;
		y=a[1]+(a[0]-a[1])/num;
		dyda[0]=1/num;
		dyda[1]=1-1/num;
		dyda[2]=-(a[0]-a[1])*expterm*(-1.0/a[3])/(num*num);
		dyda[3]=-(a[0]-a[1])*expterm*(-(x-a[2])/(a[3]*a[3]))/(num*num);
		return y;
	}
	
	@Override
	public double F(double x) {
		// TODO Auto-generated method stub
		double result = c2+(c1-c2)/(1+Math.exp((x-x0)/dx));
		return result;
	}
	
	@Override
	public void printSequence(String s) {
		// TODO Auto-generated method stub
		System.out.println(s);
	}	

	@Override
	public double[] FD(double x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double MF(double[] x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] DMF(double[] x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double F3D(double x, double y, double z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double yy1(double x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double yy2(double x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double z1(double x, double y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double z2(double x, double y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] vecfunc(int n, double[] x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] aF(double x, int ma) {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public double[] derivF(double x, double[] y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(double x1, double[] v, double[] y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load1(double x1, double[] v, double[] y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load2(double x2, double[] v, int nn2, double[] y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void score(double x2, double[] y, double[] f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void difeq(int k, int k1, int k2, int jsf, int is1, int isf,
			int[] indexv, int ne, double[][] s, double[][] y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double g(double t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double ak(double t, double s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double g(int k, double t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double ak(int k, int l, double t, double s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void kermom(double[] w, double y, int m) {
		// TODO Auto-generated method stub
		
	}
}
