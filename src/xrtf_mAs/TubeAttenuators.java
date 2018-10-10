package xrtf_mAs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.text.NumberFormat;
import java.util.Locale;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.io.InputStream;

import danfulea.utils.ListUtilities;

import danfulea.utils.FrameUtilities;
import danfulea.utils.ExampleFileFilter;

import danfulea.phys.XRay;
import danfulea.phys.XRaySpectrum;
import danfulea.math.Convertor;

/**
 * Computes total filtration based on known attenuators.
 *   
 * @author Dan Fulea, 03 May 2015
 *
 */
@SuppressWarnings("serial")
public class TubeAttenuators extends JFrame implements ActionListener{

	private static final String LOAD_COMMAND = "LOAD";
	private static final String COMPUTE_COMMAND = "COMPUTE";
	private static final String ADD_COMMAND = "ADD";
	private static final String DELETE_COMMAND = "DELETE";
	private static final String VIEW_X_COMMAND = "VIEW_X";
	
	public boolean saveBoo=true;

	private MainFrame mf;
	private JFrame xrayframe;
	private JTextField matTf = new JTextField(18);
	private JTextField gmatTf = new JTextField(5);

	private JButton loadB,calcB,addB,deleteB, spectruxB;
	@SuppressWarnings("rawtypes")
	protected JComboBox uAnodCb,kvCb,ianodCb,irippleCb;
	@SuppressWarnings("rawtypes")
	protected JList puncteLst;
	protected JScrollPane listSp;
	protected JTextArea resultTa;
	@SuppressWarnings("rawtypes")
	protected DefaultListModel dlm=new DefaultListModel() ;
	private static final Dimension sizeLst = new Dimension(190,110);
	private static final Dimension sizeCb = new Dimension(60, 21);
	
	private static final Dimension PREFERRED_SIZE = new Dimension(900, 700);
	@SuppressWarnings("rawtypes")
	private Vector matv=new Vector();
	@SuppressWarnings("rawtypes")
	private Vector gmatv=new Vector();
	private int nPoints=0;

	/**
	 * Constructor
	 * @param mf the MainFrame object
	 */
	public TubeAttenuators(MainFrame mf){
		super("Total filtration calculator");
		this.mf=mf;

		createGUI();
		
		XRay.reset();
		XRay.ICALC=1;

		FrameUtilities.centerFrameOnScreen(this);
		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				mf.resources.getString("form.icon.url"),this);

		setVisible(true);
		mf.setEnabled(false);

		final MainFrame mff = mf;
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				mff.setEnabled(true);
				dispose();
			}
		});
	}

	/**
	 * Setting up the frame size.
	 */
	public Dimension getPreferredSize()
	{
		return PREFERRED_SIZE;
	}

	/**
	 * GUI creation.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createGUI(){
		puncteLst = new JList(dlm);
		listSp = new JScrollPane(puncteLst);
		listSp.setPreferredSize(sizeLst);//--!!!--for resising well
		//initializari
		matTf.setOpaque(true);gmatTf.setOpaque(true);
		
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";

		buttonName = "Load...";
		buttonToolTip = "";
		buttonIconName = mf.resources.getString("img.open.file");
		loadB = FrameUtilities.makeButton(buttonIconName, LOAD_COMMAND,
				buttonToolTip, buttonName, this, this);
		//Character mnemonic = (Character) resources.getObject("runB.mnemonic");
		Character mnemonic =new Character('L');
		loadB.setMnemonic(mnemonic.charValue());		


		buttonName = "Compute";
		buttonToolTip = "";
		buttonIconName = mf.resources.getString("img.set");
		calcB = FrameUtilities.makeButton(buttonIconName, COMPUTE_COMMAND,
				buttonToolTip, buttonName, this, this);
		//Character mnemonic = (Character) resources.getObject("runB.mnemonic");
		mnemonic =new Character('C');
		calcB.setMnemonic(mnemonic.charValue());

		buttonName = "View spectrum...";
		buttonToolTip = "";
		buttonIconName = mf.resources.getString("img.view");
		spectruxB = FrameUtilities.makeButton(buttonIconName, VIEW_X_COMMAND,
				buttonToolTip, buttonName, this, this);
		//Character mnemonic = (Character) resources.getObject("runB.mnemonic");
		mnemonic =new Character('V');
		spectruxB.setMnemonic(mnemonic.charValue());  

		buttonName = "Add";
		buttonToolTip = "";
		buttonIconName = mf.resources.getString("img.insert");
		addB = FrameUtilities.makeButton(buttonIconName, ADD_COMMAND,
				buttonToolTip, buttonName, this, this);
		//Character mnemonic = (Character) resources.getObject("runB.mnemonic");
		mnemonic =new Character('A');
		addB.setMnemonic(mnemonic.charValue()); 

		buttonName = "Delete";
		buttonToolTip = "";
		buttonIconName = mf.resources.getString("img.delete");
		deleteB = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		//Character mnemonic = (Character) resources.getObject("runB.mnemonic");
		mnemonic =new Character('D');
		deleteB.setMnemonic(mnemonic.charValue()); 

		String[] ua = new String[18];//[22];
		for(int i=6; i<=23; i++)//i<=27; i++)
			ua[i-6]=Convertor.intToString(i);
		uAnodCb=new JComboBox(ua);
		String s="17";
		uAnodCb.setSelectedItem((Object)s);
		uAnodCb.setMaximumRowCount(5);
		uAnodCb.setPreferredSize(sizeCb);

		String[] ua1 ={"W","Mo","Rh"};
		ianodCb=new JComboBox(ua1);
		//s="0";//W
		//ianodCb.setSelectedItem((Object)s);
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

		String ss="Results: \n"+
		"Waveform of type: single phase=> ripple 100% \n"+
		"Waveform of type: 3 phase,6pulsuri=> ripple ~13% \n"+
		"Waveform of type: 3 phase,12pulsuri=> ripple ~4% \n"+
		"Waveform of type: CP (potential constant)=> ripple =0% \n";//+
		resultTa=new JTextArea(ss);
		resultTa.setLineWrap(true);
		resultTa.setWrapStyleWord(true);
		resultTa.setCaretPosition(0);
		resultTa.setEditable(false);

		puncteLst.setOpaque(true);

		loadB.setOpaque(true);
		calcB.setOpaque(true);
		spectruxB.setOpaque(true);
		addB.setOpaque(true);
		deleteB.setOpaque(true);
		kvCb.setOpaque(true);
		ianodCb.setOpaque(true);
		irippleCb.setOpaque(true);
		uAnodCb.setOpaque(true);

		JPanel content = new JPanel(new BorderLayout());
		JPanel mainPanel=createMainPanel();
		content.add(mainPanel);
		setContentPane(content);
		content.setOpaque(true); //content panes must be opaque
		pack();
	}

	/**
	 * Create main panel
	 * @return the result
	 */
	private JPanel createMainPanel()
	{
		String res="Material:";
		JLabel pathL=new JLabel(res);
		JPanel pathP=new JPanel();
		pathP.setLayout(new FlowLayout(FlowLayout.CENTER,10,0));
		pathP.add(pathL);
		pathP.add(matTf);
		pathP.add(loadB);
		pathP.setBackground(MainFrame.bkgColor);

		JPanel d1=new JPanel();
		d1.setLayout(new FlowLayout(FlowLayout.CENTER,10,0));
		JLabel lbl=new JLabel("Thickness[mm] ");
		d1.add(lbl);
		d1.add(gmatTf);
		d1.setBackground(MainFrame.bkgColor);

		JPanel d2=new JPanel();
		d2.setLayout(new FlowLayout(FlowLayout.CENTER,10,0));
		d2.add(addB);
		d2.add(deleteB);
		d2.setBackground(MainFrame.bkgColor);

		JPanel expDataAllP = new JPanel();
		BoxLayout bl = new BoxLayout(expDataAllP,BoxLayout.Y_AXIS);
		expDataAllP.setLayout(bl);
		expDataAllP.add(pathP);
		expDataAllP.add(d1);
		expDataAllP.add(d2);
		expDataAllP.setBackground(MainFrame.bkgColor);

		JPanel listP=new JPanel(new BorderLayout());		
		listP.add(listSp,BorderLayout.CENTER);
		listSp.getViewport().add(puncteLst, null);
		listSp.setPreferredSize(sizeLst);

		JPanel d3=new JPanel();
		d3.setLayout(new FlowLayout(FlowLayout.CENTER,10,0));
		d3.add(expDataAllP);
		d3.add(listP);
		d3.setBackground(MainFrame.bkgColor);

		JPanel d4=new JPanel();
		d4.setLayout(new FlowLayout(FlowLayout.CENTER,10,0));
		lbl=new JLabel("kv: ");
		d4.add(lbl);
		d4.add(kvCb);
		lbl=new JLabel("anode angle: ");
		d4.add(lbl);
		d4.add(uAnodCb);
		lbl=new JLabel("anode material: ");
		d4.add(lbl);
		d4.add(ianodCb);
		lbl=new JLabel("ripple [%]: ");
		d4.add(lbl);
		d4.add(irippleCb);
				
		d4.add(calcB);
		d4.add(spectruxB);
		d4.setBackground(MainFrame.bkgColor);

		JPanel centerP = new JPanel();
		BoxLayout bl1 = new BoxLayout(centerP,BoxLayout.Y_AXIS);
		centerP.setLayout(bl1);
		centerP.add(d3);
		centerP.add(d4);
		centerP.setBackground(MainFrame.bkgColor);

		JPanel resultP=new JPanel(new BorderLayout());
		JScrollPane jspres=new JScrollPane();
		jspres.getViewport().add(resultTa, null);
		resultP.add(jspres,  BorderLayout.CENTER);
		resultP.setBackground(MainFrame.bkgColor);

		matTf.setEnabled(false);

		JPanel main = new JPanel(new BorderLayout());
		main.add(centerP,BorderLayout.NORTH);
		main.add(resultP,BorderLayout.CENTER);
		return main;
	}

	/**
	 * Most actions are set here
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String command = arg0.getActionCommand();
		
		if (command.equals(ADD_COMMAND)) {
			enterDataInList();
            matTf.setText("");
            gmatTf.setText("");
		} else if (command.equals(DELETE_COMMAND)) {
			deleteSelectedData();
            matTf.setText("");
            gmatTf.setText("");
		} else if (command.equals(LOAD_COMMAND)) {
			performLoad();
		} else if (command.equals(VIEW_X_COMMAND)) {
			performX();
		} else if (command.equals(COMPUTE_COMMAND)) {
			performEval();
		} 
		 
	}
	
	/**
	 * Load filters data
	 */
	private void performLoad()
	   {
			String ext="CSV";
			String pct=".";
			String description="CSV files";
			ExampleFileFilter jpgFilter =
				new ExampleFileFilter(ext, description);
			String datas="Data";
			String filt="FILTERS";
		    String filename="";
		    String currentDir=System.getProperty("user.dir");
		    String file_sep=System.getProperty("file.separator");
		    String opens=currentDir+file_sep+datas+file_sep+filt;
			JFileChooser chooser = new JFileChooser(opens);
			chooser.addChoosableFileFilter(jpgFilter);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			int returnVal = chooser.showOpenDialog(this);//parent=this frame
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				
				filename= chooser.getSelectedFile().getName();
				int fl=filename.length();
				String test=filename.substring(fl-4);
				String ctest=pct+ext;
				
				if (test.compareTo(ctest)==0)
					filename=filename.substring(0,fl-4);
				//without extensionName

				matTf.setText(filename);
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
		 * Given a vector v, this routine converts it into a String array.
		 * @param v v
		 * @return the result
		 */
		private String[] convertVectorToStringArray(@SuppressWarnings("rawtypes") Vector v)
		{
			String[] result = new String[v.size()];
			for(int i=0; i<v.size(); i++)
			{
				String s=(String)v.elementAt(i);
				result[i]=s;
			}
			return result;
		}

		/**
		 * Insert attenuators data in list
		 */
	    @SuppressWarnings("unchecked")
		private void enterDataInList()
	    {
		
	        boolean b=true;
	        String s1=matTf.getText();
	        String s2=gmatTf.getText();
	        
	        double d2=0.0;
	        try
	        {			  
			    d2=Convertor.stringToDouble(s2);
			    if(d2<=0){b=false;}
			}
			catch(Exception e)
			{
				b=false;			    
			}

			if(!b){
				String title ="Error...";//resources.getString("dialog.insertInListError.title");
			    String message ="Insert positive real numbers!";//resources.getString("dialog.insertInList.message");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			   return;
			}

			ListUtilities.add("mat: "+s1+" mm: " +d2,dlm);
			ListUtilities.select(nPoints,puncteLst);

			s2=Convertor.doubleToString(d2);
			matv.addElement((Object)s1);
			gmatv.addElement((Object)s2);
			nPoints++;

		}

	    /**
	     * Delete data from list
	     */
	    private void deleteSelectedData()
	    {
	        if(nPoints!=0)
	        {
	        	nPoints--;

				int index=ListUtilities.getSelectedIndex(puncteLst);
				ListUtilities.remove(index,dlm);
				ListUtilities.select(nPoints-1,puncteLst);

				matv.removeElementAt(index);
				gmatv.removeElementAt(index);
			}

	    }
	    
	    /**
	     * Initialize XRay spectrum
	     */
	    private void performX()
	    {
			validateComputationMode();
			if (!saveBoo)
			{
				return;
			}			

	        String s=(String)uAnodCb.getSelectedItem();
	        double uAnodD=Convertor.stringToDouble(s);

	       	XRay.reset();//HERE WE RESET!!!!!!!!!!!!!!!!!!!!!!
	       
	       	XRay.ICALC=1;//is;
	       	int is=ianodCb.getSelectedIndex();
	        XRay.ianod=is;
	        s=(String)irippleCb.getSelectedItem();
	        is=Convertor.stringToInt(s);
	        XRay.iripple=is;

			s=(String)kvCb.getSelectedItem();
	        double kVD=Convertor.stringToDouble(s);
	        
			String[] mats=convertVectorToStringArray(matv);
			double[] gmats=convertVectorToDoubleArray(gmatv);
			for (int i=1;i<=mats.length;i++)
			{
				XRay.readAttCoef(mats[i-1],i);
				XRay.TMM[i-1]=gmats[i-1];
			}

			XRaySpectrum buildx=new XRaySpectrum(kVD,2.5,uAnodD);//2.5 does not have any effect in this call!
		    
			xrayframe=new JFrame();
		    JPanel content = new JPanel(new BorderLayout());
		    JPanel graphP=buildx.getXRayPlotPanel();
		    content.add(graphP, BorderLayout.CENTER);
		    xrayframe.setContentPane(content);
		    content.setOpaque(true); //content panes must be opaque
		    xrayframe.pack();

		  	xrayframe.setTitle("XSpectrum");
		    FrameUtilities.centerFrameOnScreen(xrayframe);
		    JFrame.setDefaultLookAndFeelDecorated(true);
		    createImageIcon2(mf.resources.getString("form.icon.url"));
		    xrayframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    xrayframe.setVisible(true);

		}
	    
	    /**
	     * Load and set frame image icons
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
	     * Perform computation of total filtration and other data
	     */
	    private void performEval()
		{
			validateComputationMode();
			if (!saveBoo)//(is not good input)
			{
				return;
			}

	        String s=(String)uAnodCb.getSelectedItem();
	        double uAnodD=Convertor.stringToDouble(s);

	        XRay.reset();//HERE WE RESET!!!!!!!!!!!!!!!!!!!!!!
	           
	        XRay.ICALC=1;//is;
	        int is=ianodCb.getSelectedIndex();
	        XRay.ianod=is;
	        s=(String)irippleCb.getSelectedItem();
	        is=Convertor.stringToInt(s);
	        XRay.iripple=is;

		    s=(String)kvCb.getSelectedItem();
	        double kVD=Convertor.stringToDouble(s);
	        					
			String[] mats=convertVectorToStringArray(matv);
			double[] gmats=convertVectorToDoubleArray(gmatv);
			for (int i=1;i<=mats.length;i++)
			{
				XRay.readAttCoef(mats[i-1],i);
				XRay.TMM[i-1]=gmats[i-1];
			}

			XRaySpectrum buildx=
			  new XRaySpectrum(kVD,2.5,uAnodD);//initialize XRay calculation

	        resultTa.selectAll();
	        resultTa.replaceSelection("");
	        NumberFormat nf = NumberFormat.getInstance(Locale.US);
			nf.setMinimumFractionDigits(5);
	    	nf.setMaximumFractionDigits(5);
	    	nf.setGroupingUsed(false);//no 4,568.02 but 4568.02
			XRay.computeHVL1("TISS",true);//tissue equivalent
			XRay.computeHVL2("TISS",true);//tissue equivalent

			XRay.computeHVL1("AL",false);//final in mmAl
			XRay.computeHVL2("AL",false);//final in mmAl

	        resultTa.append("HVL1= "+nf.format(XRay.HVL1)+" mmAl \n");
	        resultTa.append("HVL2= "+nf.format(XRay.HVL2)+" mmAl \n");

			XRay.computeFiltrationFromHVL1(XRay.HVL1);
			XRay.computeFiltrationFromHVL2(XRay.HVL1,XRay.HVL2);
			
			resultTa.append("=============================================== \n");
	        resultTa.append("Total filtration (based on HVL)= "+nf.format(XRay.eqFiltr_HVL1)+" mmAl \n");
	        resultTa.append("----> other data:Total filtration (based on HVL2)= "+nf.format(XRay.eqFiltr_HVL2)+" mmAl \n");
			
	        double airKermaAt75cm=1.0E-03*buildx.getAirKerma();//mGy/mAs, default is uGy/mAs
	        String str="";
	        str = str + "Normalized air kerma at 75 cm [mGy/mAs] = "+Convertor.formatNumber(airKermaAt75cm,5)+"\n";
	        resultTa.append(str);
		}

	    /**
		 * Based on SRS78 database for simulating XRay spectra this routine check if all 
		 * all is good for proceeding further with calculations.		 
		 */
		public void validateComputationMode()
		{
			int itemp=0;
			saveBoo=true;

			XRay.ICALC=1;
			itemp=ianodCb.getSelectedIndex();
			XRay.ianod=itemp;
			itemp=irippleCb.getSelectedIndex();
			XRay.iripple=itemp;
			String s="";

			if (XRay.ICALC==0)
			{
				
				s=(String)uAnodCb.getSelectedItem();
				itemp=Convertor.stringToInt(s);

				if (itemp<7 || itemp>27)
					saveBoo=false;
				
				s=(String)kvCb.getSelectedItem();
				itemp=Convertor.stringToInt(s);

				if (itemp<30 || itemp>150)
					saveBoo=false;

				//======================================================
				if (XRay.ianod!=0 || XRay.iripple!=0)
					saveBoo=false;//not allowed anything but W (ianod 0) and ripple 0!!
				//=====================================================

			}
			else if (XRay.ICALC==1)
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

					
					//alloewd ripple
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
			    String title ="Error...";
			    String message = mf.resources.getString("dialog.ripple");
				//String message ="CALC=0=>NUMAI kv>=30,kv<=150;anod=W,>=7,anod<=27;ripple=0; \nCALC=1=>DACA anod=Mo or Rh>=9,anod <=23;kv>=25,kv<=32;ripple=0; \nCALC=1,anod=W >=6 anod<=22;kv>=30;kv<=150!! \n pentru ripple=>kv =55,60,65,...,90!!!!";//resources.getString("dialog.ripple");
			    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			}

		}
	    
}
