package sensitometry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import danfulea.utils.table.SortButtonRenderer;
import danfulea.utils.table.SortableTableModel;

/**
 * Create a sortable table panel 
 * 
 * @author Dan Fulea, 28 Apr. 2015
 */
@SuppressWarnings("serial")
public class SensiSortableTablePanel extends JPanel{
	private SortableTableModel dm;
	private JTable table;
	@SuppressWarnings("unused")
	private Object[][] dataObj;
	@SuppressWarnings("unused")
	private String[] headerStr;
	@SuppressWarnings({ "rawtypes", "unused" })
	private Vector dataVector = new Vector();
	@SuppressWarnings({ "unused", "rawtypes" })
	private Vector headerVector = new Vector();
	
	/**
	 * Constructor
	 * @param dataObj data object
	 * @param headerStr the header array of Strings
	 */
	public SensiSortableTablePanel(Object[][] dataObj, String[] headerStr)
	  {
		this.dataObj=dataObj;
		this.headerStr=headerStr;

	    setLayout(new BorderLayout());
	    dm = new SortableTableModel()
	    {
		  //--append and overrides something in model
	      @SuppressWarnings({ "rawtypes", "unchecked" })
		  public Class getColumnClass(int col)
	      {
	        switch (col)
	        {
	          case  0: return Integer.class;//step-wedge-->1 to 21 steps
	          case  1: return Double.class;//-->logE
	          case  2: return Double.class;//-->DO
	          default: return Object.class;
	        }
	      }

	      public boolean isCellEditable(int row, int col)
	      {
	        switch (col)
	        {
	          case  0: return false;
	          default: return true;
	        }
	      }

	      public void setValueAt(Object obj, int row, int col)
	      {
	        switch (col)
		    {
		      case  0: super.setValueAt(new Integer(obj.toString()), row, col); return;
		      case  1: super.setValueAt(new Double(obj.toString()), row, col); return;
		      case  2: super.setValueAt(new Double(obj.toString()), row, col); return;
		      default: super.setValueAt(obj, row, col); return;
		    }
	      }
	      //-------------end
	    };

	    dm.setDataVector(dataObj,headerStr);

	    table = new JTable(dm);
	    table.setShowVerticalLines(true);
	    SortButtonRenderer renderer = new SortButtonRenderer();

	    TableColumnModel model = table.getColumnModel();
	    int n = headerStr.length;
	    for (int i=0;i<n;i++)
	    {
	      model.getColumn(i).setHeaderRenderer(renderer);
	    }
		//----------------------------------------------------
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setForeground(Color.BLUE);
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		model.getColumn(1).setCellRenderer(dtcr);//logE
		dtcr = new DefaultTableCellRenderer();
		dtcr.setForeground(Color.RED);
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		model.getColumn(2).setCellRenderer(dtcr);//OD
		//----------------------------------------------------
	    JTableHeader header = table.getTableHeader();
	    header.addMouseListener(new HeaderListener(header,renderer));

	    JScrollPane pane = new JScrollPane(table);
	    add(pane, BorderLayout.CENTER);
	  }

	/**
	 * Constructor
	 * @param dataVector data vector
	 * @param headerVector header vector
	 */
	  public SensiSortableTablePanel(@SuppressWarnings("rawtypes") Vector dataVector, @SuppressWarnings("rawtypes") Vector headerVector)
	  {
		this.dataVector=dataVector;
		this.headerVector=headerVector;

	    setLayout(new BorderLayout());
	    dm = new SortableTableModel()
	    {
		  //--append and overrides something in model
	      @SuppressWarnings({ "rawtypes", "unchecked" })
		  public Class getColumnClass(int col)
	      {
	        switch (col)
	        {
	          case  0: return Integer.class;//step-wedge-->1 to 21 steps
	          case  1: return Double.class;//-->logE
	          case  2: return Double.class;//-->DO
	          default: return Object.class;
	        }
	      }

	      public boolean isCellEditable(int row, int col)
	      {
	        switch (col)
	        {
	          case  0: return false;
	          default: return true;
	        }
	      }

	      public void setValueAt(Object obj, int row, int col)
	      {
	        switch (col)
	        {
	          case  0: super.setValueAt(new Integer(obj.toString()), row, col); return;
	          case  1: super.setValueAt(new Double(obj.toString()), row, col); return;
	          case  2: super.setValueAt(new Double(obj.toString()), row, col); return;
	          default: super.setValueAt(obj, row, col); return;
	        }
	      }
	      //-------------end
	    };

	    dm.setDataVector(dataVector,headerVector);
	    table = new JTable(dm);

	    table.setShowVerticalLines(true);
	    SortButtonRenderer renderer = new SortButtonRenderer();

	    TableColumnModel model = table.getColumnModel();
	    int n = headerVector.size();
	    for (int i=0;i<n;i++)
	    {
	      model.getColumn(i).setHeaderRenderer(renderer);
	    }
		//----------------------------------------------------
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setForeground(Color.BLUE);
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		model.getColumn(1).setCellRenderer(dtcr);//logE
		dtcr = new DefaultTableCellRenderer();
		dtcr.setForeground(Color.RED);
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		model.getColumn(2).setCellRenderer(dtcr);//OD
		//----------------------------------------------------

	    JTableHeader header = table.getTableHeader();
	    header.addMouseListener(new HeaderListener(header,renderer));

	    JScrollPane pane = new JScrollPane(table);
	    add(pane, BorderLayout.CENTER);
	  }


	  /**
		 * Return the table model
		 * @return the result
		 */
	  public SortableTableModel getTabModel()
	  {
		  return this.dm;
	  }

	  /**
	   * Return the table
	   * @return the result
	   */
	  public JTable getTab()
	  {
		  return this.table;
	  }

	  private static DateFormat dateFormat =
	    DateFormat.getDateInstance(DateFormat.SHORT, Locale.JAPAN);//-------------!!??

	  /**
	   * Get Date from a String.
	   * @param dateString dateString
	   * @return the result
	   */
	  @SuppressWarnings("unused")
	  private static Date getDate(String dateString)
	  {
	    Date date = null;
	    try
	    {
	      date = dateFormat.parse(dateString);
	    }
	    catch(ParseException ex)
	    {
	      date = new Date();
	    }
	    return date;
	  }


	  /**
	   * The header listener inner class
	   * @author Dan Fulea
	   *
	   */
	  class HeaderListener extends MouseAdapter
	  {
	    JTableHeader   header;
	    SortButtonRenderer renderer;

	    HeaderListener(JTableHeader header,SortButtonRenderer renderer)
	    {
	      this.header   = header;
	      this.renderer = renderer;
	    }

	    public void mousePressed(MouseEvent e)
	    {
	      int col = header.columnAtPoint(e.getPoint());
	      int sortCol = header.getTable().convertColumnIndexToModel(col);
	      renderer.setPressedColumn(col);
	      renderer.setSelectedColumn(col);
	      header.repaint();

	      if (header.getTable().isEditing())
	      {
	        header.getTable().getCellEditor().stopCellEditing();
	      }

	      boolean isAscent;
	      if (SortButtonRenderer.DOWN == renderer.getState(col))
	      {
	        isAscent = true;
	      }
	      else
	      {
	        isAscent = false;
	      }

	      ((SortableTableModel)header.getTable().getModel()).sortByColumn(sortCol, isAscent);
	    }

	    public void mouseReleased(MouseEvent e)
	    {
	      @SuppressWarnings("unused")
		  int col = header.columnAtPoint(e.getPoint());
	      renderer.setPressedColumn(-1);                // clear
	      header.repaint();
	    }
	  }
}
