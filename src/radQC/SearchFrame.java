package radQC;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import danfulea.db.DatabaseAgent;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;

/**
 * Search for a specific entry in database
 * 
 * @author Dan Fulea, 25 Apr. 2015
 */
@SuppressWarnings("serial")
public class SearchFrame extends JFrame implements ActionListener{

	private RadQCFrame mf;
	private static final String BASE_RESOURCE_CLASS = "radQC.resources.RadQCFrameResources";
	private ResourceBundle resources;
	private final Dimension PREFERRED_SIZE = new Dimension(600, 400);
	
	private static final String SEARCH_COMMAND = "SEARCH";
	private String command = null;
	
	private String mainDB = "";
	private String dbTable="";
	
	private JCheckBox localitateCh;//comun
    private JCheckBox uMedCh;//comun
    private JCheckBox tipXInstalCh;//comun
    private JTextField localitateTf=new JTextField(20);
    private JTextField uMedTf=new JTextField(20);
    private JTextField tipXInstalTf=new JTextField(20);
    
    private int nSearch=0;
    
    private Connection radqcdbcon = null;
    //private DatabaseAgentSupport dbagent;
    
    /**
     * Constructor
     * @param mf the RadQCFrame object
     */
	public SearchFrame(RadQCFrame mf){
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		//DBConnection.startDerby();//just in case is closed
		
		this.setTitle(resources.getString("SearchFrame.NAME"));		
		mainDB = mf.radqcDB;//resources.getString("main.db");
		dbTable = mf.deviceTable;//resources.getString("main.db.muTable");
		
		this.mf = mf;

		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		//dbagent = new DatabaseAgentSupport(radqcdbcon,"UNIQUE_ID",
			//	dbTable);
		//dbagent.setHasValidAIColumn(false);
		//-------now dummy initialization of agents just to create combobox, label and tables!!!
		//dbagent.init();	
		
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
		
		JPanel mainPanel = createMainPanel();
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
		localitateTf.setEnabled(false);
		uMedTf.setEnabled(false);
		tipXInstalTf.setEnabled(false);
		
		Character mnemonic = null;
		JButton button = null;
		//JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		localitateCh=new JCheckBox(resources.getString("main.location.ch"));
		uMedCh=new JCheckBox(resources.getString("main.medicalUnit.ch"));
		tipXInstalCh=new JCheckBox(resources.getString("main.device.ch"));
		localitateCh.addActionListener(this);
		uMedCh.addActionListener(this);
		tipXInstalCh.addActionListener(this);
		
		JPanel p1=new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p1.add(localitateCh);
	    p1.setBackground(RadQCFrame.bkgColor);

	    JPanel p2=new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p2.add(localitateTf);
	    p2.setBackground(RadQCFrame.bkgColor);

	    JPanel p3=new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p3.add(uMedCh);
	    p3.setBackground(RadQCFrame.bkgColor);

	    JPanel p4=new JPanel();
		p4.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p4.add(uMedTf);
	    p4.setBackground(RadQCFrame.bkgColor);

	    JPanel p5=new JPanel();
		p5.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p5.add(tipXInstalCh);
	    p5.setBackground(RadQCFrame.bkgColor);

	    JPanel p6=new JPanel();
		p6.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		p6.add(tipXInstalTf);
	    p6.setBackground(RadQCFrame.bkgColor);	       

	    JPanel qaRadCautare1 = new JPanel();
	    BoxLayout bl = new BoxLayout(qaRadCautare1,BoxLayout.Y_AXIS);
		qaRadCautare1.setLayout(bl);
	    qaRadCautare1.add(p1);
	    qaRadCautare1.add(p3);
	    qaRadCautare1.add(p5);
	       //qaRadCautare1.setBorder(getGroupBoxBorder(resources.getString("qaRad.qaRadCautareChei.border")));
	    qaRadCautare1.setBorder(FrameUtilities.getGroupBoxBorder(
					resources.getString("main.key.border"), RadQCFrame.foreColor));
	    qaRadCautare1.setBackground(RadQCFrame.bkgColor);
	    
	    JPanel qaRadCautare2 = new JPanel();
	    bl = new BoxLayout(qaRadCautare2,BoxLayout.Y_AXIS);
		qaRadCautare2.setLayout(bl);
	    qaRadCautare2.add(p2);
	    qaRadCautare2.add(p4);
	    qaRadCautare2.add(p6);
	       //qaRadCautare2.setBorder(getGroupBoxBorder(mf.resources.getString("qaRad.qaRadCautareParametrii.border")));
	    qaRadCautare2.setBorder(FrameUtilities.getGroupBoxBorder(
					resources.getString("main.param.border"), RadQCFrame.foreColor));
	    qaRadCautare2.setBackground(RadQCFrame.bkgColor);

	    JPanel qaRadCautare3 = new JPanel();
	    qaRadCautare3.setLayout(new FlowLayout(FlowLayout.CENTER));
	    buttonName = resources.getString("search.search.button");
		buttonToolTip = resources.getString("search.search.button.toolTip");
		buttonIconName = resources.getString("img.view");
		button = FrameUtilities.makeButton(buttonIconName, SEARCH_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("search.search.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
	    qaRadCautare3.add(button);
	    qaRadCautare3.setBackground(RadQCFrame.bkgColor);

	    Insets ins = new Insets(5,5,5,5);
	    GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints cts = new GridBagConstraints();
	    JPanel qaRadCautare = new JPanel(gbl);

	    cts.anchor = GridBagConstraints.WEST;//default
	    cts.fill = GridBagConstraints.BOTH;//umplerea extraspatiului atat pe orizontala cat si pe verticala

	    cts.insets = ins;
	    cts.ipadx = 5;
	    cts.ipady = 5;
	    cts.weightx = 1.0; //ponderi egale in maximizare toate pe orizontala
	    cts.gridwidth = GridBagConstraints.RELATIVE; //end row
		gbl.setConstraints(qaRadCautare1,cts);
	    qaRadCautare.add(qaRadCautare1);

	    cts.gridwidth = GridBagConstraints.REMAINDER; //end row
	 	cts.weighty = 1.0;//maximizare pe verticala--atrage dupa sine tot restul!!
	    gbl.setConstraints(qaRadCautare2,cts);
	    qaRadCautare.add(qaRadCautare2);

	    gbl.setConstraints(qaRadCautare3,cts);
	    qaRadCautare.add(qaRadCautare3);
	    qaRadCautare.setBackground(RadQCFrame.bkgColor);
	    return qaRadCautare;
	//end panel cu cautari
		
		//JPanel mainP = new JPanel(new BorderLayout());
		//mainP.setBackground(RadQCFrame.bkgColor);
		//return mainP;
	}
	
	/**
	 * Most actions are set here
	 */
	public void actionPerformed(ActionEvent arg0) {

		command = arg0.getActionCommand();
		if (command.equals(SEARCH_COMMAND)) {
			search();
		} 
		
		if (arg0.getSource()==localitateCh)
			   if (localitateCh.isSelected())
			   {
				   localitateTf.setEnabled(true);
				   localitateTf.requestFocusInWindow();
                nSearch++;
                //nSearchContinium++;
			   }
			   else
			   {
				   localitateTf.setEnabled(false);
				   nSearch--;
				   //nSearchContinium--;
			   }
		
		if (arg0.getSource()==uMedCh)
			   if (uMedCh.isSelected())
			   {
				   uMedTf.setEnabled(true);
				   uMedTf.requestFocusInWindow();
				   nSearch++;
				   //nSearchContinium++;
			   }
			   else
			   {
				   uMedTf.setEnabled(false);
				   nSearch--;
				   //nSearchContinium--;
			   }

	    if (arg0.getSource()==tipXInstalCh)
			   if (tipXInstalCh.isSelected())
			   {
				   tipXInstalTf.setEnabled(true);
				   tipXInstalTf.requestFocusInWindow();
				   nSearch++;
				   //nSearchContinium++;
			   }
			   else
			   {
				   tipXInstalTf.setEnabled(false);
                   nSearch--;
                	//nSearchContinium--;
			   }
	}
	
	/**
	 * Perform search in database
	 */
	private void search(){
		int i=0;
        String [][] searchMatrix = new String[nSearch][2];
        if (localitateCh.isSelected())
	    {
		    searchMatrix[i][0]=resources.getString("db.location.columnName");
            searchMatrix[i][1]=localitateTf.getText();
            i++;
		}

        if (uMedCh.isSelected())
	    {
		    searchMatrix[i][0]=resources.getString("db.medicalUnit.columnName");
            searchMatrix[i][1]=uMedTf.getText();
            i++;
		}

        if (tipXInstalCh.isSelected())
	    {
		    searchMatrix[i][0]=resources.getString("db.device.columnName");
            searchMatrix[i][1]=tipXInstalTf.getText();
            i++;
		}
        
        String s="select * from "+dbTable;//+" where ";
        //i=nSearchContinium-1;
        
        if(i!=0)
        	s=s+" where ";
        
        int j=0;
        for (int k=j; k<=nSearch-1+j; k++)
		{
			String x=null;
			x="%" + searchMatrix[k-j][1] +"%";
		    s=s+searchMatrix[k-j][0]+" like '"+x+"'";
		    if (k-j<nSearch-1)
		      s=s+" and ";
	    }
        
        s=s+" order by Unique_ID";
        //System.out.println(s);
        //now select
        String commandString="";
		if (mf.radiographyRb.isSelected()){
			commandString=RadQCFrame.RADIOGRAPHY_COMMAND;
		}
		else if (mf.mammographyRb.isSelected()){
			commandString=RadQCFrame.MAMMOGRAPHY_COMMAND;
		}
		else if (mf.fluoroscopyRb.isSelected()){
			commandString=RadQCFrame.FLUOROSCOPY_COMMAND;
		}
		else if (mf.ctRb.isSelected()){
			commandString=RadQCFrame.CT_COMMAND;
		}
		try {
			
			//String datas = resources.getString("data.load");
			///String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;

			//String s = "select * from " + deviceTable+" order by Unique_ID";
			//we already have string s

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			mf.genericdbagent.select(s);
			
			//mf.suportSp.remove(mf.asp);//remove first
			//mf.asp = new AdvancedSelectPanel();
			//mf.suportSp.add(mf.asp, BorderLayout.CENTER);

			JTable mainTable = mf.genericdbagent.getMainTable();//mf.asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			int IDDEVICE = mainTable.getRowCount();
			
			mf.medicalUnitS="";
			mf.departmentS="";
			mf.xrayDeviceS="";
			mf.serialNumberS="";
			mf.manufactureDateS="";
			mf.countyS="";
			mf.locationS="";
			mf.telephoneS="";
			mf.emailS="";
			mf.contactNameS="";
			mf.noteS="";	
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some field
				//Integer intg=(Integer)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//String maxUniqueIDs=intg.toString();//(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 0);
				//maxUniqueID=Convertor.stringToInt(maxUniqueIDs);
				
				mf.retrieveInformation();
				
			} else {
				//maxUniqueID=0;//reset counter
			}
			
			//now status========================
			String string="";
			if (commandString.equals(RadQCFrame.RADIOGRAPHY_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.rad.label");
			} else if (commandString.equals(RadQCFrame.MAMMOGRAPHY_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.mammo.label");
			} else if (commandString.equals(RadQCFrame.FLUOROSCOPY_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.fluoro.label");
			} else if (commandString.equals(RadQCFrame.CT_COMMAND)){
				string=resources.getString("status.default.label")+
					resources.getString("status.default.ct.label");
			}
			mf.statusL.setText(string);
			//=====================================
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(IDDEVICE);
			mf.iddevicelabel.setText(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//mf.validate();
	}
}
