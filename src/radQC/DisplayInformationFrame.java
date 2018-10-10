package radQC;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.pdfbox.pdmodel.PDDocument;

import danfulea.utils.ExampleFileFilter;
import danfulea.utils.FrameUtilities;
import danfulea.utils.PDFRenderer;

/**
 * Class for displaying information about QC tests and dosimetry results for a specific 
 * medical unit.
 * 
 * 
 * @author Dan Fulea, 25 Apr. 2015
 */
@SuppressWarnings("serial")
public class DisplayInformationFrame extends JFrame implements ActionListener{
	private final Dimension PREFERRED_SIZE = new Dimension(850, 700);
	private static final String BASE_RESOURCE_CLASS = "radQC.resources.RadQCFrameResources";
	protected ResourceBundle resources;
	private JScrollPane jScrollPane1 = new JScrollPane();
	protected JTextArea textArea = new JTextArea();
	private String displayS="";
	private RadQCFrame mf;	
	
	protected String outFilename = null;
	private static final String REPORT_COMMAND="REPORT";
	private String command = null;
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 * @param displayS the display string
	 */
	public DisplayInformationFrame(RadQCFrame mf, String displayS){
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		this.setTitle(resources.getString("DisplayInformationFrame.NAME"));
		
		this.mf = mf;
		this.displayS=displayS;
		
		textArea.setBackground(RadQCFrame.textAreaBkgColor);
		textArea.setForeground(RadQCFrame.textAreaForeColor);
		createGUI();

		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				this.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);

		setVisible(true);
		mf.setEnabled(false);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//not necessary, exit normal!
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				attemptExit();
			}
		});
	}

	/**
	 * Setting up the frame size.
	 */
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

	/**
	 * Exit method
	 */

	private void attemptExit() {
		mf.setEnabled(true);
		dispose();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void createGUI() {		

		jScrollPane1.setBorder(new javax.swing.border.TitledBorder(
				new javax.swing.border.LineBorder(
						new java.awt.Color(0, 51, 255), 1, true),
						this.resources.getString("DisplayInformation.border"),
				javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.TOP));
		jScrollPane1
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane1
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setAutoscrolls(true);
		textArea.setColumns(1);
		textArea.setEditable(false);

		textArea.setLineWrap(true);
		textArea.setRows(10);
		textArea.setText(displayS);//this.resources.getString("HowTo"));
		textArea.setWrapStyleWord(true);
		jScrollPane1.setViewportView(textArea);
		
		Character mnemonic = null;
		JButton button = null;
		//JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		JPanel p4P = new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		buttonName = resources.getString("display.report");
		buttonToolTip = resources.getString("display.report.toolTip");
		buttonIconName = resources.getString("img.report");
		button = FrameUtilities.makeButton(buttonIconName, REPORT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("display.report.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p4P.add(button);
		p4P.setBackground(RadQCFrame.bkgColor);
		
		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new java.awt.BorderLayout());
		jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);
		jPanel2.add(p4P, java.awt.BorderLayout.SOUTH);
		jPanel2.setBackground(RadQCFrame.bkgColor);

		getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);
		pack();
	}
	
	/**
	 * Most actions are set here
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		command = arg0.getActionCommand();
		if (command.equals(REPORT_COMMAND)) {
			report();
		}
	}
	
	/**
	 * Print report
	 */
	private void report(){
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

			//new DeviceReport(this);
			performPrintReport();
			//statusL.setText(resources.getString("status.save") + outFilename);
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
}
