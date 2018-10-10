package dosimetry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;

import danfulea.math.Convertor;
import danfulea.math.numerical.Stats;
import danfulea.utils.FrameUtilities;
import danfulea.utils.ListUtilities;
import radQC.RadQCFrame;

/**
 * CTDI evaluation. Based on CTDI measured on central axis and at phantom periphery, the 
 * weighted and volume CTDI is computed to be compared with reference levels.
 * 
 * @author Dan Fulea, 08 May. 2015
 */
@SuppressWarnings("serial")
public class CTDIEvalFrame extends JFrame implements ActionListener{

	private static final Dimension PREFERRED_SIZE = new Dimension(950, 550);
	private static final Dimension textAreaDimension = new Dimension(500, 150);
	private DosimetryFrameCt mf;
	
	private static final Dimension sizeLst = new Dimension(253,125);
	@SuppressWarnings("rawtypes")
	protected DefaultListModel kvdlm=new DefaultListModel() ;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected JList kvmL = new JList(kvdlm);
	private int nPoints=0;
	@SuppressWarnings({ "rawtypes" })
	private Vector kvv=new Vector();
	
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private static final String SETCTDIC_COMMAND = "SETCTDIC";
	private static final String ADD_COMMAND = "ADD";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String DELETEALL_COMMAND = "DELETEALL";
	private String command = null;
	
	private JRadioButton mrRb, mradRb, mgyRb;
	private JTextField exposureTf = new JTextField(5);
	
	private JTextField lengthTf = new JTextField(5);
	//private JTextField nslicesTf = new JTextField(5);
	private JTextField sliceThicknessTf = new JTextField(5);
	private JTextField pitchTf = new JTextField(5);
	
	protected JTextArea textArea = new JTextArea();
	
	private double CTDIc=-1.0;//insucces value	
	
	/**
	 * Constructor
	 * @param mf the DosimetryFrameCt object
	 */
	public CTDIEvalFrame(DosimetryFrameCt mf){
		this.mf=mf;
		this.setTitle(mf.resources.getString("CTDIEvalFrame.NAME"));
		
		createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				mf.resources.getString("form.icon.url"), this);

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
	private JPanel createMainPanel() {
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		textArea.setText("");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText(mf.resources.getString("CT.INFO"));
	
		JPanel resultP = new JPanel(new BorderLayout());
		resultP.add(new JScrollPane(textArea), BorderLayout.CENTER);
		resultP.setPreferredSize(textAreaDimension);
		resultP.setBackground(RadQCFrame.bkgColor);
		
		pitchTf.setText(mf.pitch_toSave);
		sliceThicknessTf.setText(mf.sliceThickness_toSave);
		pitchTf.setEditable(false);
		sliceThicknessTf.setEditable(false);
		lengthTf.setText("1");//"100");
		//nslicesTf.setText("1");
				
		exposureTf.addActionListener(this);
		
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		mrRb=new JRadioButton("mR");
		mradRb=new JRadioButton("mRad");
		mgyRb=new JRadioButton("mGy");

	    ButtonGroup group = new ButtonGroup();
		group.add(mrRb);
		group.add(mradRb);
		group.add(mgyRb);	
		
		JPanel p0P=new JPanel();
        p0P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("geometry.label.ct"));
        p0P.add(label);        
        p0P.setBackground(RadQCFrame.bkgColor);
        
		JPanel p1P=new JPanel();
        p1P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("ctdiEval.exposure.label"));
        p1P.add(label);
        p1P.add(exposureTf);
        p1P.add(mrRb);
        p1P.add(mradRb);
        p1P.add(mgyRb);
        buttonName = mf.resources.getString("ctdiEval.setCTDIcB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = mf.resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, SETCTDIC_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("ctdiEval.setCTDIcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1P.add(button);
		buttonName = mf.resources.getString("ctdiEval.addB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = mf.resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, ADD_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("ctdiEval.addB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1P.add(button);
        p1P.setBackground(RadQCFrame.bkgColor);
        mgyRb.setSelected(true);
        mrRb.setBackground(RadQCFrame.bkgColor);
    	mradRb.setBackground(RadQCFrame.bkgColor);
    	mgyRb.setBackground(RadQCFrame.bkgColor);
    	
    	JPanel p2P=new JPanel();
        p2P.setLayout(new FlowLayout(FlowLayout.CENTER));
        //label=new JLabel(mf.resources.getString("ctdiEval.length.label"));
        //p2P.add(label);
        //p2P.add(lengthTf);
        label=new JLabel(mf.resources.getString("ctdiEval.calFactor.label"));
        p2P.add(label);
        p2P.add(lengthTf);//cal instead
        //p2P.add(nslicesTf);//calFactor instead
        p2P.setBackground(RadQCFrame.bkgColor);
        
        JPanel p3P=new JPanel();
        p3P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("ctdiEval.sliceThickness.label"));
        p3P.add(label);
        p3P.add(sliceThicknessTf);
        label=new JLabel(mf.resources.getString("ctdiEval.pitch.label"));
        p3P.add(label);
        p3P.add(pitchTf);
        p3P.setBackground(RadQCFrame.bkgColor);
              
       //========================================= 
        JPanel p5P=new JPanel();
        p5P.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonName = mf.resources.getString("calcB");
		buttonToolTip =  mf.resources.getString("calcB.toolTip");
		buttonIconName =  mf.resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("calcB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p5P.add(button);
        p5P.setBackground(RadQCFrame.bkgColor);
        //-------
        
        JScrollPane listSp = new JScrollPane();
	    JPanel lstmasP = new JPanel();
	    lstmasP.setLayout(new BorderLayout());
	    lstmasP.add(listSp, BorderLayout.CENTER);
		listSp.getViewport().add(kvmL, null);
	    listSp.setPreferredSize(sizeLst);
	    
	    JPanel jp1=new JPanel();
		jp1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		buttonName = mf.resources.getString("ctdiEval.deleteB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = mf.resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("ctdiEval.deleteB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp1.add(button);//ra.delkvB);
		jp1.setBackground(RadQCFrame.bkgColor);
		
		JPanel jp2=new JPanel();
		jp2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		buttonName = mf.resources.getString("ctdiEval.resetB");
		buttonToolTip = null;//resources.getString("kvp.addB.toolTip");
		buttonIconName = mf.resources.getString("img.delete.all");
		button = FrameUtilities.makeButton(buttonIconName, DELETEALL_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("ctdiEval.resetB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		jp2.add(button);//ra.resetkvB);
	    jp2.setBackground(RadQCFrame.bkgColor);

	    JPanel lstopP = new JPanel();
	    BoxLayout bl = new BoxLayout(lstopP,BoxLayout.Y_AXIS);
	    lstopP.setLayout(bl);
		lstopP.add(jp1);
		lstopP.add(jp2);
		lstopP.setBackground(RadQCFrame.bkgColor);
        
		JPanel dataRepP = new JPanel();
		dataRepP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		//ataRepP.add(masuratP, null);
	    dataRepP.add(lstmasP, null);
		dataRepP.add(lstopP, null);
	    dataRepP.setBackground(RadQCFrame.bkgColor);
	    
		JPanel northP = new JPanel();
		BoxLayout blnorthP = new BoxLayout(northP, BoxLayout.Y_AXIS);
		northP.setLayout(blnorthP);
		northP.add(p0P);
		//northP.add(p1P);
		northP.add(p2P);
		northP.add(p3P);
		
		northP.add(p1P);
		northP.add(dataRepP);
		northP.add(p5P);
		northP.setBackground(RadQCFrame.bkgColor);
		//============
		JPanel mainPanel=new JPanel(new BorderLayout());
		mainPanel.add(northP, BorderLayout.NORTH);
		mainPanel.add(resultP, BorderLayout.CENTER);
		mainPanel.setBackground(RadQCFrame.bkgColor);
		return mainPanel;
	}
	
	/**
	 * Most actions are set here
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		command = e.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {			
			compute();
		} if (e.getSource()==exposureTf || command.equals(ADD_COMMAND)) {
			addInList();
		} else if (command.equals(DELETE_COMMAND)) {
			deleteSelectedData();
		} else if (command.equals(SETCTDIC_COMMAND)) {
			setCTDIc();
		} else if (command.equals(DELETEALL_COMMAND)) {
			resetData();
		}
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
	 * Add data in list
	 */
	@SuppressWarnings("unchecked")
	private void addInList(){
		//boolean b=true;
        String s1=exposureTf.getText();
        double exposure=0.0;
        double length = 0.0;//lengthTf.getText();
        //double nSlice = 0.0;
        double sliceThickness=Convertor.stringToDouble(mf.sliceThickness_toSave);
        
        try
        {
		    exposure=Convertor.stringToDouble(s1);
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
		    
		    length=Convertor.stringToDouble(lengthTf.getText());//calFactor!!
		    //nSlice=Convertor.stringToDouble(nslicesTf.getText());	
		}
		catch(Exception e)
		{
			//b=false;
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    exposureTf.setText("");
		    exposureTf.requestFocusInWindow();
		    
		    return;
		}

		//if(!b)
		//   return;
		/*if (nSlice<=0){
			String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.negative.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    exposureTf.setText("");
		    exposureTf.requestFocusInWindow();
		    return;
		}*/
        
		double ctdip=exposure*length/sliceThickness;///nSlice;

		ListUtilities.add("CTDIp :     "+ctdip+"  mGy",kvdlm);
		ListUtilities.select(nPoints,kvmL);
		s1=Convertor.doubleToString(exposure);

		s1=Convertor.doubleToString(ctdip);
		kvv.addElement((Object)s1);
		nPoints++;

		exposureTf.setText("");
		exposureTf.requestFocusInWindow();		
	}
	
	/**
	 * Delete data from list
	 */
	private void deleteSelectedData(){
		if(nPoints!=0)
        {

        	nPoints--;

			int index=ListUtilities.getSelectedIndex(kvmL);

			ListUtilities.remove(index,kvdlm);
			ListUtilities.select(nPoints-1,kvmL);

			kvv.removeElementAt(index);

			exposureTf.setText("");
			exposureTf.requestFocusInWindow();	
		}
	}
	
	/**
	 * Reset (clear) all data from list
	 */
	private void resetData(){
		kvv.removeAllElements();
        ListUtilities.removeAll(kvdlm);
        nPoints=0;
        exposureTf.setText("");
		exposureTf.requestFocusInWindow();	
	}
	
	/**
	 * Set CTDI on central axis
	 */
	private void setCTDIc(){
		String s1=exposureTf.getText();
        double exposure=0.0;
        double length = 0.0;//lengthTf.getText();
        //double nSlice = 0.0;
        double sliceThickness=Convertor.stringToDouble(mf.sliceThickness_toSave);
        
        try
        {
		    exposure=Convertor.stringToDouble(s1);
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
		    
		    length=Convertor.stringToDouble(lengthTf.getText());//calFactor!!
		    //nSlice=Convertor.stringToDouble(nslicesTf.getText());		    
		}
		catch(Exception e)
		{			
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    exposureTf.setText("");
		    exposureTf.requestFocusInWindow();
		    return;
		}
		
		/*if (nSlice<=0){
			String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.negative.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    exposureTf.setText("");
		    exposureTf.requestFocusInWindow();
		    return;
		}*/
		
		CTDIc=exposure*length/sliceThickness;///nSlice;
		exposureTf.setText("");
		exposureTf.requestFocusInWindow();	
	}
	
	/**
	 * Perform CTDI evaluation (CTDIvol)
	 */
	private void compute(){
		if (CTDIc==-1.0){
			String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.ctdic.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    exposureTf.setText("");
		    exposureTf.requestFocusInWindow();
			return;
		}
		
		//===============================================================
		String str="";
		textArea.selectAll();
		textArea.replaceSelection("");
		//================================
		double[] ctdiparray=convertVectorToDoubleArray(kvv); 
		if (ctdiparray.length<2){
			str="Calulation without peripheral values!"+"\n";
			textArea.append(str);
		}
		
		str="CTDIc = Reading at center x cal_factor/(N x slice thickness) [mGy]: "+CTDIc+"\n";
		textArea.append(str);
		
		double ctdip=0.0;
		if (ctdiparray.length>1){
			Stats.avevar(ctdiparray, ctdiparray.length);
			double mean = Stats.ave_avevar;
			ctdip=mean;
		}
		
		str="CTDIp = Mean of readings at periphery x cal_factor/(N x slice thickness) [mGy]: "+ctdip+"\n";
		textArea.append(str);
		
		double ctdiw=CTDIc;
		if(ctdiparray.length>1)
			ctdiw=CTDIc/3.0+2.0*ctdip/3.0;
		str="CTDIw = CTDIc/3 + 2 x CTDIp/3 [mGy]: "+ctdiw+"\n";
		textArea.append(str);
		
		double pitch = Convertor.stringToDouble(mf.pitch_toSave);
		double ctdivol=ctdiw/pitch;
		str="CTDIvol = CTDIw/pitch [mGy]: "+ctdivol+"\n";
		textArea.append(str);
		
		mf.ctdiVolTf.setText(Convertor.doubleToString(ctdivol*1000.0));//uGy
	}
		

}
