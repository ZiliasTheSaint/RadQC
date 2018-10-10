package radQC;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import danfulea.db.DatabaseAgent;
import danfulea.db.DatabaseAgentSupport;
//import jdf.db.AdvancedSelectPanel;
//import jdf.db.DBConnection;
//import jdf.db.DBOperation;
import danfulea.math.Convertor;
import danfulea.utils.FrameUtilities;

/**
 * Class for saving medical unit location.
 * 
 * @author Dan Fulea, 22 Apr. 2015
 */
@SuppressWarnings("serial")
public class LocationFrame extends JFrame implements ActionListener, ItemListener{
	private RadQCFrame mf;
	private static final String BASE_RESOURCE_CLASS = "radQC.resources.RadQCFrameResources";
	private ResourceBundle resources;
	private final Dimension PREFERRED_SIZE = new Dimension(600, 400);
	private final Dimension tableDimension = new Dimension(400, 200);
	
	private static final String INSERT_COMMAND = "INSERT";
	private static final String DELETE_COMMAND = "DELETE";
	private String command = null;
	
	private String mainDB = "";
	private String locationTable="";
	//private AdvancedSelectPanel asp = null;
	private JPanel suportSp = new JPanel(new BorderLayout());
	private int IDLOCATION = 0;
	private JLabel idlocationlabel=null;
	
	private JTextField locationTf = new JTextField(25);
	
	private Connection radqcdbcon = null;
	private DatabaseAgentSupport dbagent;
	private JComboBox<String> orderbyCb;
	private final Dimension sizeOrderCb = new Dimension(200, 21);
	
	/**
	 * Constructor
	 * @param mf the RadQCFrame object
	 */
	public LocationFrame(RadQCFrame mf){
		resources = ResourceBundle.getBundle(BASE_RESOURCE_CLASS);
		//DBConnection.startDerby();//just in case is closed
		
		this.setTitle(resources.getString("LocationFrame.NAME"));		
		mainDB = resources.getString("main.db");
		locationTable = resources.getString("main.db.locationTable");
		
		this.mf = mf;

		DatabaseAgent.ID_CONNECTION = DatabaseAgent.DERBY_CONNECTION;
    	String datas = mf.resources.getString("data.load");
		String currentDir = System.getProperty("user.dir");
		String file_sep = System.getProperty("file.separator");
		String opens = currentDir + file_sep + datas;
		String dbName = mainDB;
		opens = opens + file_sep + dbName;
		radqcdbcon = DatabaseAgent.getConnection(opens, "", "");
		dbagent = new DatabaseAgentSupport(radqcdbcon,"ID",
				locationTable);
		dbagent.setHasValidAIColumn(false);
		//-------now dummy initialization of agents just to create combobox, label and tables!!!
		dbagent.init();			
		
		performQueryDb();
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
		Character mnemonic = null;
		JButton button = null;
		JLabel label = null;
		String buttonName = "";
		String buttonToolTip = "";
		String buttonIconName = "";
		
		orderbyCb = dbagent.getOrderByComboBox();
		orderbyCb.setMaximumRowCount(5);
		orderbyCb.setPreferredSize(sizeOrderCb);
		orderbyCb.addItemListener(this);
		JPanel orderP = new JPanel();
		orderP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("sort.by"));//"Sort by: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(orderbyCb);
		orderP.setBackground(RadQCFrame.bkgColor);
		label = new JLabel(resources.getString("records.count"));//"Records count: ");
		label.setForeground(RadQCFrame.foreColor);
		orderP.add(label);
		orderP.add(dbagent.getRecordsLabel());
		
		JPanel p1P = new JPanel();
		p1P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		label = new JLabel(resources.getString("main.location.label"));
		label.setForeground(RadQCFrame.foreColor);
		p1P.add(label);
		p1P.add(locationTf);locationTf.addActionListener(this);
		buttonName = resources.getString("mu.insert.button");
		buttonToolTip = resources.getString("mu.insert.button.toolTip");
		buttonIconName = resources.getString("img.insert");
		button = FrameUtilities.makeButton(buttonIconName, INSERT_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("mu.insert.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p1P.add(button);
		p1P.setBackground(RadQCFrame.bkgColor);
		
		JPanel p2P = new JPanel();
		p2P.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		buttonName = resources.getString("mu.delete.button");
		buttonToolTip = resources.getString("mu.delete.button.toolTip");
		buttonIconName = resources.getString("img.delete");
		button = FrameUtilities.makeButton(buttonIconName, DELETE_COMMAND,
				buttonToolTip, buttonName, this, this);
		mnemonic = (Character) resources.getObject("mu.delete.button.mnemonic");
		button.setMnemonic(mnemonic.charValue());
		p2P.add(button);
		p2P.setBackground(RadQCFrame.bkgColor);
		
		//=-------------------DB panel and records
		JPanel p4P=new JPanel();
		p4P.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 4));
		label = new JLabel(resources.getString("records.label"));
		label.setForeground(RadQCFrame.foreColor);
		p4P.add(label);
		String str=Convertor.intToString(IDLOCATION);
		idlocationlabel = new JLabel(str);
		idlocationlabel.setForeground(RadQCFrame.foreColor);
		p4P.add(idlocationlabel);
		p4P.setBackground(RadQCFrame.bkgColor);
		
		suportSp.setPreferredSize(tableDimension);

		JPanel p5P = new JPanel();
		BoxLayout blp5P = new BoxLayout(p5P, BoxLayout.Y_AXIS);
		p5P.setLayout(blp5P);
		p5P.setBorder(FrameUtilities.getGroupBoxBorder(
				resources.getString("records.border"),
				RadQCFrame.foreColor));
		p5P.add(suportSp);
		p5P.add(orderP);/////////////////p5P.add(p4P);
		p5P.setBackground(RadQCFrame.bkgColor);
		//-------------------
		JPanel p6P = new JPanel();
		BoxLayout blp6P = new BoxLayout(p6P, BoxLayout.Y_AXIS);
		p6P.setLayout(blp6P);
		p6P.add(p1P);				
		p6P.setBackground(RadQCFrame.bkgColor);
		//-------------
				
		JPanel mainP = new JPanel(new BorderLayout());
		mainP.add(p6P, BorderLayout.NORTH);
		mainP.add(p5P, BorderLayout.CENTER);
		mainP.add(p2P, BorderLayout.SOUTH);
		mainP.setBackground(RadQCFrame.bkgColor);
		return mainP;
		
	}
	
	/**
	 * Initialize database
	 */
	private void performQueryDb() {
		try {
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;

			//String s = "select * from " + locationTable;

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//DBOperation.select(s, con1);

			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);
			JTable mainTable = dbagent.getMainTable();//already selected
			JScrollPane scrollPane = new JScrollPane(mainTable);
			mainTable.setFillsViewportHeight(true);
			suportSp.add(scrollPane, BorderLayout.CENTER);
			//JTable mainTable = asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			/*rowSM.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return; // Don't want to handle intermediate selections

					updateDetailTable();
				}
			});*/

			IDLOCATION = mainTable.getRowCount();// last ID

			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				//populate some fields:
				//descriptionS=(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 1);
				//descriptionTf.setText(descriptionS);
				//measurementDate=(String)mainTable.getValueAt(mainTable.getRowCount() - 1, 2);
				//TimeUtilities.unformatDate(measurementDate);
				//dayCb.setSelectedItem((Object) TimeUtilities.idayS);
				//monthCb.setSelectedItem((Object) TimeUtilities.imonthS);
				//yearTf.setText(TimeUtilities.iyearS);
			} //else {
				//IDDEVICE = 0;				
			//}

			//if (con1 != null)
				//con1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Setting up actions!
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		command = arg0.getActionCommand();
		if (command.equals(INSERT_COMMAND)) {
			insert();
		} else if (command.equals(DELETE_COMMAND)) {
			delete();
		} else if (arg0.getSource() == locationTf) {// press enter!
			insert();
		}
	}

	/**
	 * JCombobox actions are set here
	 */
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == orderbyCb) {
			sort();
		} 
	}
	
	/**
	 * Sorts data from the table
	 */
	private void sort() {
		//System.out.println("sort");
		String orderbyS = (String) orderbyCb.getSelectedItem();
		//dbagent.setLinks("IDLINK", Convertor.intToString(mf.IDLINK));
		dbagent.performSelection(orderbyS);
	}
	
	/**
	 * Insert data in database table
	 */
	private void insert(){
		//String nucs = (String) nucCb.getSelectedItem();// initialization
		
		String muStr=locationTf.getText();
		
		JTable mainTable = dbagent.getMainTable();//asp.getTab();
		int recordCount = mainTable.getRowCount();
		
		try {
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;
			// make a connection
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			PreparedStatement psInsert = null;
			//-------------------------
			psInsert = radqcdbcon.prepareStatement("insert into "//con1.prepareStatement("insert into "
					+ locationTable + " values " + "(?, ?)");
			int id = recordCount + 1;
			psInsert.setString(1, Convertor.intToString(id));
			psInsert.setString(2, muStr);
			//psInsert.setString(5, Convertor.doubleToString(source_activity));
			psInsert.executeUpdate();
			
			//---------
			if (psInsert != null)
				psInsert.close();
			//if (con1 != null)
				//con1.close();
			
			IDLOCATION=IDLOCATION+1;
			updateControls();
			
			locationTf.setText("");
			locationTf.requestFocusInWindow();
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Update database
	 */
	@SuppressWarnings("unchecked")
	private void updateControls(){
		//suportSp.remove(asp);
		mf.locationCb.removeAllItems();
		
		try {
			Statement st = null;
			ResultSet rs = null;
			
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;

			String s = "select * from " +locationTable;

			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");
			//con1.setAutoCommit(false);
			
			//DBOperation.select(s, con1);
			
			dbagent.select(s);

			//asp = new AdvancedSelectPanel();
			//suportSp.add(asp, BorderLayout.CENTER);

			JTable mainTable = dbagent.getMainTable();//asp.getTab();

			ListSelectionModel rowSM = mainTable.getSelectionModel();
			rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			if (mainTable.getRowCount() > 0) {
				// always display last row!
				mainTable.setRowSelectionInterval(mainTable.getRowCount() - 1,
						mainTable.getRowCount() - 1); // last ID
				
			} else {
				//---------------
			}
			
			//now combobox========================
			Vector<String> muV = new Vector<String>();
			st = radqcdbcon.createStatement();//con1.createStatement();
			rs = st.executeQuery("SELECT * FROM " + locationTable);

			if (rs != null)
				while (rs.next()) {
					String ss = rs.getString(2);					
					muV.addElement(ss);//mf.muCb.addItem(ss);
				}
			//String[] muS=new String[muV.size()];
			//for(int i=0;i<muV.size();i++){
			//	muS[i]=muV.elementAt(i);
			//}
			//Arrays.sort(muS);
			
			Collections.sort(muV,String.CASE_INSENSITIVE_ORDER);//sort alphabetically
			for(int i=0;i<muV.size();i++){
				//mf.muCb.addItem(muS[i]);//System.out.println(muS[i]);
				mf.locationCb.addItem(muV.elementAt(i));
			}
			//=====================================
			//con1.commit();
			
			//if (con1 != null)
				//con1.close();
			
			String str=Convertor.intToString(IDLOCATION);
			idlocationlabel.setText(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//===========
		//validate();
		//mf.validate();redundant
	}
	
	/**
	 * Delete an entry from database
	 */
	private void delete(){
		try {
			// prepare db query data
			//String datas = resources.getString("data.load");
			//String currentDir = System.getProperty("user.dir");
			//String file_sep = System.getProperty("file.separator");
			//String opens = currentDir + file_sep + datas;
			//String dbName = mainDB;
			//opens = opens + file_sep + dbName;
			
			//Connection con1 = DBConnection.getDerbyConnection(opens, "", "");

			JTable aspTable = dbagent.getMainTable();//asp.getTab();
			//int rowTableCount = aspTable.getRowCount();// =MAX ID!!

			int selID = 0;// NO ZERO ID
			int selRow = aspTable.getSelectedRow();
			if (selRow != -1) {
				selID = (Integer) aspTable.getValueAt(selRow, 0);
			} else {
				//if (con1 != null)
					//con1.close();
				
				return;// nothing to delete
			}
			
			Statement s = radqcdbcon.createStatement(ResultSet.TYPE_FORWARD_ONLY,//con1.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet res = s.executeQuery("SELECT * FROM " + locationTable);
			PreparedStatement psUpdate = null;
			while (res.next()) {
				int id = res.getInt("ID");
				if (id == selID) {
					res.deleteRow();
				} else if (id > selID) {
					// since in this table ID is UNIQUE and ASCENDING, we can
					// make
					// on-the fly update
					psUpdate = radqcdbcon.prepareStatement("update " + locationTable//con1.prepareStatement("update " + locationTable
							+ " set ID=? where ID=?");

					psUpdate.setInt(1, id - 1);
					psUpdate.setInt(2, id);

					psUpdate.executeUpdate();
					psUpdate.close();
				}
			}
			IDLOCATION=IDLOCATION-1;						
			//update visual controls!
			updateControls();
			// do not shutdown derby..it will be closed at frame exit!

			if (res != null)
				res.close();
			if (s != null)
				s.close();
			if (psUpdate != null)
				psUpdate.close();
			//if (con1 != null)
				//con1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
