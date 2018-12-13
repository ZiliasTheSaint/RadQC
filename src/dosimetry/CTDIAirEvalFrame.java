package dosimetry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import danfulea.math.Convertor;
import danfulea.phys.XRay;
import danfulea.phys.XRaySpectrum;
import danfulea.utils.FrameUtilities;
import radQC.RadQCFrame;

/**
 * CTDI air evaluation based on mAs per rotation.
 * 
 * @author Dan Fulea, 13 Dec. 2018
 */
public class CTDIAirEvalFrame extends JFrame implements ActionListener, Runnable {

	private static final long serialVersionUID = 3077954184275097541L;

	private static final Dimension PREFERRED_SIZE = new Dimension(800, 500);
	private static final Dimension textAreaDimension = new Dimension(500, 150);
	private DosimetryFrameCt mf;

	private static final String COMPUTE_COMMAND = "COMPUTE";
	private String command;

	private JLabel statusL = new JLabel("Waiting...");
	private volatile Thread computationTh = null;// computation thread!
	private volatile Thread statusTh = null;// status display thread!
	private int delay = 100;
	private int frameNumber = -1;
	private String statusRunS = "";
	private boolean stopAnim = true;

	private JTextField mAsTf = new JTextField(5); // mAs per rotation
	protected JTextArea textArea = new JTextArea();

	private double FCA;
	private double kVD;
	private double filtrareD;
	private double uAnodD;
	private int ripple;

	/**
	 * Constructor
	 * 
	 * @param mf
	 *            the DosimetryFrameCt object
	 */
	public CTDIAirEvalFrame(DosimetryFrameCt mf, double FCA, double kVD, double filtrareD, double uAnodD, int ripple) {
		this.mf = mf;
		this.FCA = FCA;
		this.kVD = kVD;
		this.filtrareD = filtrareD;
		this.uAnodD = uAnodD;
		this.ripple = ripple;
		this.setTitle(mf.resources.getString("CTDIAirEvalFrame.NAME"));

		createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(mf.resources.getString("form.icon.url"), this);

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

		// content.add(toolBar, BorderLayout.PAGE_START);
		content.add(mainPanel, BorderLayout.CENTER);

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
	 * Setting up the status bar.
	 * 
	 * @param toolBar
	 *            toolBar
	 */
	private void initStatusBar(JToolBar toolBar) {
		JPanel toolP = new JPanel();
		toolP.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

		toolP.add(statusL);
		toolBar.add(toolP);
		statusL.setText(mf.resources.getString("status.wait"));
	}

	private String getInitialText() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(mf.resources.getString("ctdiEval.air.text")).append("\n");
		stringBuilder.append(mf.resources.getString("ctdiEval.air.text.fca")).append(FCA).append("\n");
		stringBuilder.append(mf.resources.getString("ctdiEval.air.text.kv")).append(kVD).append("\n");
		stringBuilder.append(mf.resources.getString("ctdiEval.air.text.filtration")).append(filtrareD).append("\n");
		stringBuilder.append(mf.resources.getString("ctdiEval.air.text.uanod")).append(uAnodD).append("\n");
		stringBuilder.append(mf.resources.getString("ctdiEval.air.text.ripple")).append(ripple).append("\n");
		String str = stringBuilder.toString();
		return str;
	}

	/**
	 * Create main panel
	 * 
	 * @return the result
	 */
	private JPanel createMainPanel() {
		textArea.setCaretPosition(0);
		textArea.setEditable(false);
		textArea.setText("");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText(getInitialText());// !!!!!!!!!!!!!!!!

		JPanel resultP = new JPanel(new BorderLayout());
		resultP.add(new JScrollPane(textArea), BorderLayout.CENTER);
		resultP.setPreferredSize(textAreaDimension);
		resultP.setBackground(RadQCFrame.bkgColor);

		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";

		JPanel p0P = new JPanel();
		p0P.setLayout(new FlowLayout(FlowLayout.CENTER));
		label = new JLabel(mf.resources.getString("geometry.label.ct.mAs"));
		p0P.add(label);
		p0P.setBackground(RadQCFrame.bkgColor);

		JPanel p1P = new JPanel();
		p1P.setLayout(new FlowLayout(FlowLayout.CENTER));
		label = new JLabel(mf.resources.getString("ctdiEval.air.mas.label"));
		p1P.add(label);
		p1P.add(mAsTf);
		mAsTf.setText("125");// default

		buttonName = mf.resources.getString("ctdiEval.air.computeB");
		buttonToolTip = null;// resources.getString("kvp.addB.toolTip");
		buttonIconName = mf.resources.getString("img.set");
		button = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND, buttonToolTip, buttonName, this, this);
		mnemonic = (Character) mf.resources.getObject("ctdiEval.air.computeB.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1P.add(button);
		p1P.setBackground(RadQCFrame.bkgColor);

		JPanel northP = new JPanel();
		BoxLayout blnorthP = new BoxLayout(northP, BoxLayout.Y_AXIS);
		northP.setLayout(blnorthP);
		northP.add(p0P);
		northP.add(p1P);
		northP.setBackground(RadQCFrame.bkgColor);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(northP, BorderLayout.NORTH);
		mainPanel.add(resultP, BorderLayout.CENTER);
		mainPanel.setBackground(RadQCFrame.bkgColor);
		return mainPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		command = e.getActionCommand();
		if (command.equals(COMPUTE_COMMAND)) {
			statusRunS = mf.resources.getString("status.computing");
			startThread();
		}

	}
	
	private void compute(){
		String mAsS = mAsTf.getText();//checked at MC run
		try
	    {
			if(Convertor.stringToDouble(mAsS)<=0.0){
			   String title =mf.resources.getString("dialog.number.title");
			   String message =mf.resources.getString("dialog.mas.message");
			   JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   
			   stopThread();// kill all threads
			   statusL.setText(mf.resources.getString("status.done"));
				
			   return;
			}
			
			
		    
		}
		catch(Exception e)
		{
		    String title =mf.resources.getString("dialog.number.title");
		    String message =mf.resources.getString("dialog.mas.message");
		    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		    
		    stopThread();// kill all threads
			statusL.setText(mf.resources.getString("status.done"));
		    return;
		}
		
		double masperrotation = Convertor.stringToDouble(mAsS);
		
		XRay.ICALC=1;
    	XRay.ianod=0;//W
    	XRay.iripple=ripple;
    	
    	String filesname="AL";
    	XRay.readAttCoef(filesname,1);
    	XRay.TMM[0]=filtrareD;
    	
    	new XRaySpectrum(kVD,filtrareD,uAnodD);
    	//XRay.computeHVL1("AL",false);//kermapermasat75 is computed!
    	//computeHVL is already called when new XRaySpectrum is called
    	double CTDI = XRay.KERMAPERMASAT750MM * 75.0*75.0*masperrotation/(FCA*FCA);//uGy
    	CTDI=CTDI/1000.0;//mGy
		
    	textArea.append("CTDI [mGy]= "+CTDI+"\n");
    	mf.ctdiTf.setText(Convertor.doubleToString(CTDI*1000.0));//uGy
		//All done
		stopThread();// kill all threads
		statusL.setText(mf.resources.getString("status.done"));
	}

	/**
	 * Start computation thread.
	 */
	private void startThread() {
		stopAnim = false;
		if (computationTh == null) {
			computationTh = new Thread(this);
			computationTh.start();// Allow one simulation at time!
			setEnabled(false);
		}

		if (statusTh == null) {
			statusTh = new Thread(this);
			statusTh.start();
		}
	}

	/**
	 * Stop computation thread.
	 */
	private void stopThread() {

		stopAnim = true;
		statusTh = null;
		frameNumber = 0;

		computationTh = null;
		setEnabled(true);

	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);// both thread
		// same
		// priority

		long startTime = System.currentTimeMillis();
		Thread currentThread = Thread.currentThread();
		while (!stopAnim && currentThread == statusTh) {// if thread is status
														// display
			// Thread!!
			frameNumber++;
			if (frameNumber % 2 == 0)
				statusL.setText(statusRunS + ".....");
			else
				statusL.setText(statusRunS);

			// Delay
			try {
				startTime += delay;
				Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				break;
			}
		}

		if (currentThread == computationTh) {

			if (command.equals(COMPUTE_COMMAND)) {
				compute();
			} 
		}

	}

}
