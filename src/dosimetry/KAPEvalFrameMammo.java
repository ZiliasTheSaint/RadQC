package dosimetry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import radQC.RadQCFrame;

import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;

/**
 * Evaluation of Kerma-Area-Product (KAP) for mammography.
 * 
 * @author Dan Fulea, 07 May 2015
 */
@SuppressWarnings("serial")
public class KAPEvalFrameMammo extends JFrame implements ActionListener{
	
	private static final Dimension PREFERRED_SIZE = new Dimension(900, 550);
	private static final Dimension textAreaDimension = new Dimension(500, 150);
	private DosimetryFrameMammo mf;
	
	private static final String COMPUTE_COMMAND = "COMPUTE";	
	private String command = null;
	
	private JRadioButton mrRb, mradRb, mgyRb;
	private JTextField exposureTf = new JTextField(5);
	private JTextField detectorThicknessTf = new JTextField(5);
	private JTextField focusToTableTf = new JTextField(5);
	private JTextField focusToPatientTf = new JTextField(5);
	private JTextField fieldXTf = new JTextField(15);
	private JTextField fieldYTf = new JTextField(15);
	private JTextField bsfTf = new JTextField(5);
	protected JTextArea textArea = new JTextArea();
	protected JCheckBox autoupdateCh;
	
	/**
	 * Constructor
	 * @param mf the DosimetryFrameMammo object
	 */
	public KAPEvalFrameMammo(DosimetryFrameMammo mf){
		this.mf=mf;
		this.setTitle(mf.resources.getString("KAPEvalFrame.NAME"));
		
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
		
		updateFields();//init
		
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
		textArea.setText(mf.resources.getString("KAP.INFO"));
		
		autoupdateCh=new JCheckBox(mf.resources.getString("autoupdate.ch"),true);
		
		JPanel resultP = new JPanel(new BorderLayout());
		resultP.add(new JScrollPane(textArea), BorderLayout.CENTER);
		resultP.setPreferredSize(textAreaDimension);
		resultP.setBackground(RadQCFrame.bkgColor);
		
		detectorThicknessTf.setText("6.4");
		focusToTableTf.setText("50");
		focusToPatientTf.setText("50");
		fieldXTf.setText("39");
		fieldYTf.setText("39");
		bsfTf.setText("1.1");
		bsfTf.setToolTipText(mf.resources.getString("BSF.tooltip"));
		
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
        label=new JLabel(mf.resources.getString("geometry.label"));
        p0P.add(label);        
        p0P.setBackground(RadQCFrame.bkgColor);
        
		JPanel p1P=new JPanel();
        p1P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("expData2.exposureLabel"));
        p1P.add(label);
        p1P.add(exposureTf);
        p1P.add(mrRb);
        p1P.add(mradRb);
        p1P.add(mgyRb);
        p1P.setBackground(RadQCFrame.bkgColor);
        mradRb.setSelected(true);
        mrRb.setBackground(RadQCFrame.bkgColor);
    	mradRb.setBackground(RadQCFrame.bkgColor);
    	mgyRb.setBackground(RadQCFrame.bkgColor);
    	
    	JPanel p2P=new JPanel();
        p2P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("detector.thickness.label"));
        p2P.add(label);
        p2P.add(detectorThicknessTf);
        p2P.setBackground(RadQCFrame.bkgColor);
        
        JPanel p3P=new JPanel();
        p3P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("focus.table.label"));
        p3P.add(label);
        p3P.add(focusToTableTf);
        p3P.setBackground(RadQCFrame.bkgColor);
        
        JPanel p31P=new JPanel();
        p31P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("focus.patient.label"));
        p31P.add(label);
        p31P.add(focusToPatientTf);p31P.add(autoupdateCh);
        p31P.setBackground(RadQCFrame.bkgColor);
        
        JPanel p4P=new JPanel();
        p4P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("field.label"));
        p4P.add(label);
        p4P.add(fieldXTf);
        label=new JLabel(" X ");
        p4P.add(label);
        p4P.add(fieldYTf);
        p4P.setBackground(RadQCFrame.bkgColor);
        
        JPanel p41P=new JPanel();
        p41P.setLayout(new FlowLayout(FlowLayout.CENTER));
        label=new JLabel(mf.resources.getString("BSF.label"));
        p41P.add(label);
        p41P.add(bsfTf);
        p41P.setBackground(RadQCFrame.bkgColor);
        
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
        JPanel northP = new JPanel();
		BoxLayout blnorthP = new BoxLayout(northP, BoxLayout.Y_AXIS);
		northP.setLayout(blnorthP);
		northP.add(p0P);
		northP.add(p1P);
		northP.add(p2P);
		northP.add(p3P);northP.add(p31P);
		northP.add(p4P);northP.add(p41P);
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
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		command = e.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {			
			compute();
		}
	}
	
	/**
	 * Perform computation
	 */
	private void compute(){
		
		if (autoupdateCh.isSelected())
			if (!updateFields()){
				return;
			}
		
		//System.out.println("pass update");
		
		double exposure = 0.0;
		double detectorThickness = 0.0;
		double focusToTable = 0.0;
		double focusToTablePhantom = 0.0;//is just the focus to breast entrance
		double fieldX = 0.0;
		double fieldY = 0.0;
		double bsf=0.0;
		//boolean b=true;
		try
	    {
		    exposure=Convertor.stringToDouble(exposureTf.getText());
		    detectorThickness = Convertor.stringToDouble(detectorThicknessTf.getText());
		    focusToTable= Convertor.stringToDouble(focusToTableTf.getText());
		    focusToTablePhantom= Convertor.stringToDouble(focusToPatientTf.getText());
		    fieldX=Convertor.stringToDouble(fieldXTf.getText());
		    fieldY=Convertor.stringToDouble(fieldYTf.getText());
		    bsf=Convertor.stringToDouble(bsfTf.getText());
		}
		catch(Exception e)
		{
			//b=false;
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		if (exposure<0 || fieldX<=0.0 || fieldY<=0 || detectorThickness<0.0 || focusToTable<=0.0 || focusToTablePhantom<=0.0){
			//b=false;
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.negative.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		if(focusToTable<detectorThickness){
			//b=false;
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.geometry.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		//if(!b)
		   //return;
		//===============================================================
		textArea.selectAll();
		textArea.replaceSelection("");
		
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
		
		double doseAtTableLevel=exposure*(focusToTable-detectorThickness)*
		(focusToTable-detectorThickness)/focusToTable/focusToTable;
		
		textArea.append(mf.resources.getString("Kair.table.mgy")+
				Convertor.formatNumber(doseAtTableLevel,2)+"\n");
		
		//======================convert dose to patient distance
		double doseAtTableLevelPhantom = doseAtTableLevel*focusToTable*focusToTable/focusToTablePhantom/focusToTablePhantom;
		textArea.append(mf.resources.getString("Kair.table.phantom.mgy")+
				Convertor.formatNumber(doseAtTableLevelPhantom,2)+"\n");
//System.out.println("doseTabledet = "+doseAtTableLevel+"; doseattablelvlPHANTOM= "+doseAtTableLevelPhantom+" distanceTableDet= "+focusToTable+"distanceTablePHANTOM= "+focusToTablePhantom);		
		//====================================
		double KAP=doseAtTableLevelPhantom*fieldX*fieldY;//mGy*cm2
		
		textArea.append(mf.resources.getString("kap.KAP.mgycm2")+
				Convertor.formatNumber(KAP,2)+"\n");
		
		KAP=KAP*1000.0*10.0*10.0;//uGymm2
		
		textArea.append(mf.resources.getString("kap.KAP.ugymm2")+
				Convertor.formatNumber(KAP,2)+"\n");
		//====================
		double focusToBreastEntrance=focusToTablePhantom-mf.breastThickness;		
		double doseAtPatientEntrance=doseAtTableLevelPhantom*focusToTablePhantom*focusToTablePhantom/focusToBreastEntrance/focusToBreastEntrance;//mGy
	
		textArea.append(mf.resources.getString("Kair.mgy")+
				Convertor.formatNumber(doseAtPatientEntrance,2)+"\n");
		
		textArea.append(mf.resources.getString("ESAK.mgy")+
				Convertor.formatNumber(doseAtPatientEntrance*bsf,2)+"\n");
		
		mf.kapTf.setText(Convertor.doubleToString(KAP));
		mf.focusmidplaneTf.setText(Convertor.doubleToString(focusToBreastEntrance));
	}
	
	/**
	 * Internally used. Updates controls based on examination region
	 * @return true on success
	 */
	private boolean updateFields(){
		double focusToTablePhantom = 0.0;
		try
	    {		    
		    focusToTablePhantom= Convertor.stringToDouble(focusToPatientTf.getText());
		    
		}
		catch(Exception e)
		{
			//b=false;
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		if (focusToTablePhantom<=0.0){
			//b=false;
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.number.negative.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    return false;
		}
		
		double focusToBreastEntrance=focusToTablePhantom-mf.breastThickness;//mf.focusToBreastEntrance;	
		double fieldXAtBreastEntrance=mf.breastDiameter;
		double fieldYAtBreastEntrance=mf.breastDiameter;
		
		double fieldX_table_patientGeometry =  fieldXAtBreastEntrance*focusToTablePhantom/focusToBreastEntrance;//Thales
		double fieldY_table_patientGeometry =  fieldYAtBreastEntrance*focusToTablePhantom/focusToBreastEntrance;//Thales
		
		fieldXTf.setText(Convertor.doubleToString(fieldX_table_patientGeometry));
		fieldYTf.setText(Convertor.doubleToString(fieldY_table_patientGeometry));
		validate();//!!!!!!!!!!!
		//System.out.println("x "+fieldX_table_patientGeometry+" y "+fieldY_table_patientGeometry);
		//} else if (mf.EXAMINATION_ID==1){//MAMMO
			
		//}
		
		return true;
	}

}
