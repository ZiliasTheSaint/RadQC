package mtf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import danfulea.utils.FrameUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Class for plot MTF function
 * @author Dan Fulea, 10 OCT. 2006 
*/

public class Graph  extends JFrame{
 	
	private static final long serialVersionUID = 1L;
	private static final Dimension PREFERRED_SIZE = new Dimension(820, 700);
	private ChartPanel polDirChartPanel;
	private ChartPanel polDirChartPanel1;
	private ChartPanel polDirChartPanel2;
	private ChartPanel polDirChartPanel3;

	public static int interv=200;
	public static double[] xarray;
	public static double[] yarray;

	public static double[] xarray1;
	public static double[] yarray1;
	public static int interv1=200;

	public static double[] xarray2;
	public static double[] yarray2;
	public static int interv2=200;

	public static double[] xarray3;
	public static double[] yarray3;
	public static int interv3=200;

	private String smoothS="";
	private int imode=0;

	public static double xmin=0.0;
	
	/**
	 * Constructor
	 * @param smoothS the smoothness
	 */
	public Graph(String smoothS)
    {
		 imode=0;//default 4 graphs
		 this.smoothS=smoothS;
		 createGUI();

		 FrameUtilities.centerFrameOnScreen(this);
		 setDefaultLookAndFeelDecorated(true);

	     setVisible(true);
         addWindowListener(new WindowAdapter()
		 {
		     public void windowClosing(WindowEvent e)
		     {
		          dispose();
		          //System.exit(0);
		     }
         });
	}

	/**
	 * Constructor
	 */
    public Graph()
    {
		imode=1;//1 graph
		 createGUI();

		 FrameUtilities.centerFrameOnScreen(this);
		 setDefaultLookAndFeelDecorated(true);

	     setVisible(true);
         addWindowListener(new WindowAdapter()
		 {
		     public void windowClosing(WindowEvent e)
		     {
		          dispose();
		          //System.exit(0);
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
	private void createGUI()
	{
         JPanel content = new JPanel(new BorderLayout());
         JPanel graphP=createMainGraphPanel();
         content.add(graphP, BorderLayout.CENTER);
         setContentPane(content);
		 content.setOpaque(true); //content panes must be opaque
         pack();
	}

	/**
	 * Get original chart
	 * @return the result
	 */
    private JFreeChart getPolyDirChartPanel()
    {
		XYSeries series = new XYSeries("series");

		for (int i=0;i<interv;i++)
		{
		   series.add(xarray[i],yarray[i]);
        }

		XYSeriesCollection data = new XYSeriesCollection(series);

        NumberAxis xAxis = new NumberAxis("distance [pixels]");
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis("pixel values");
        XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);//new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        //------------------------
        renderer.setSeriesPaint(0,Color.RED);
        //---------------------------------
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        //XYSeries dotseries = new XYSeries("Lead Points");
        //for (int i=0;i<PEGS4A.energ.length;i++)
        //{
        //	dotseries.add(PEGS4A.energ[i],PEGS4A.totg[i]);
        //}

        //XYSeriesCollection dotdata = new XYSeriesCollection(dotseries);
        //XYItemRenderer renderer2 = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        //renderer2.setSeriesPaint(0,Color.BLACK);
        //plot.setSecondaryDataset(0, dotdata);
        //plot.setSecondaryRenderer(0, renderer2);

        JFreeChart chart=new JFreeChart("Original", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
	    //chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.YELLOW));//green));
	    chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, new Color(255,228,181)));
        return chart;
 	}

    /**
	 * Get sine wave response chart
	 * @return the result
	 */
    private JFreeChart getPolyDirChartPanel1()
    {
		XYSeries series = new XYSeries("series");

		for (int i=0;i<interv1;i++)
		{
		   series.add(xarray1[i],yarray1[i]);
        }

		XYSeriesCollection data = new XYSeriesCollection(series);

        NumberAxis xAxis = new NumberAxis("spatial frequency [cy/mm or lp/mm]");
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis("pixel values");
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        //------------------------
        renderer.setSeriesPaint(0,Color.BLUE);
        //---------------------------------
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        //XYSeries dotseries = new XYSeries("Lead Points");
        //for (int i=0;i<PEGS4A.energ.length;i++)
        //{
        //	dotseries.add(PEGS4A.energ[i],PEGS4A.totg[i]);
        //}

        //XYSeriesCollection dotdata = new XYSeriesCollection(dotseries);
        //XYItemRenderer renderer2 = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        //renderer2.setSeriesPaint(0,Color.BLACK);
        //plot.setSecondaryDataset(0, dotdata);
        //plot.setSecondaryRenderer(0, renderer2);

        JFreeChart chart=new JFreeChart("Sine wave response", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
	    //chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.YELLOW));//green));
	    chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, new Color(255,228,181)));
        return chart;
 	}

    /**
   	 * Get MTF chart
   	 * @return the result
   	 */
    private JFreeChart getPolyDirChartPanel2()
    {
		XYSeries series = new XYSeries("series");

		for (int i=0;i<interv2;i++)
		{
		   series.add(xarray2[i],yarray2[i]);
		   //System.out.println("graph:  y "+yarray2[i]+" x "+xarray2[i]);
        }

		XYSeriesCollection data = new XYSeriesCollection(series);

        NumberAxis xAxis = new NumberAxis("spatial frequency [cy/mm or lp/mm]");
        NumberAxis yAxis = new NumberAxis("MTF");
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        //------------------------
        renderer.setSeriesPaint(0,Color.GREEN);
        //---------------------------------
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        //XYSeries dotseries = new XYSeries("Lead Points");
        //for (int i=0;i<PEGS4A.energ.length;i++)
        //{
        //	dotseries.add(PEGS4A.energ[i],PEGS4A.totg[i]);
        //}

        //XYSeriesCollection dotdata = new XYSeriesCollection(dotseries);
        //XYItemRenderer renderer2 = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        //renderer2.setSeriesPaint(0,Color.BLACK);
        //plot.setSecondaryDataset(0, dotdata);
        //plot.setSecondaryRenderer(0, renderer2);

        JFreeChart chart=new JFreeChart("MTF", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
	    //chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.YELLOW));//green));
	    chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, new Color(255,228,181)));

        xAxis.setLowerBound(xmin);
        return chart;
 	}

    /**
   	 * Get smoothed chart
   	 * @return the result
   	 */
    private JFreeChart getPolyDirChartPanel3()
    {
		XYSeries series = new XYSeries("series");

		for (int i=0;i<interv3;i++)
		{
		   series.add(xarray3[i],yarray3[i]);
        }

		XYSeriesCollection data = new XYSeriesCollection(series);

        NumberAxis xAxis = new NumberAxis("distance [pixels]");
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis("pixel values");
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        //------------------------
        renderer.setSeriesPaint(0,Color.BLACK);
        //---------------------------------
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        //XYSeries dotseries = new XYSeries("Lead Points");
        //for (int i=0;i<PEGS4A.energ.length;i++)
        //{
        //	dotseries.add(PEGS4A.energ[i],PEGS4A.totg[i]);
        //}

        //XYSeriesCollection dotdata = new XYSeriesCollection(dotseries);
        //XYItemRenderer renderer2 = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        //renderer2.setSeriesPaint(0,Color.BLACK);
        //plot.setSecondaryDataset(0, dotdata);
        //plot.setSecondaryRenderer(0, renderer2);

        JFreeChart chart=new JFreeChart("f(x)=SMOOTH (x "+smoothS+")", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
	    //chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.YELLOW));//green));
	    chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, new Color(255,228,181)));
        return chart;
 	}

    /**
     * Create main panel
     * @return the result
     */
	private JPanel createMainGraphPanel()
	{
		JPanel pd=new JPanel();
		BoxLayout bl = new BoxLayout(pd,BoxLayout.Y_AXIS);
		pd.setLayout(bl);

		if (imode==0)
		{
		polDirChartPanel=new ChartPanel(getPolyDirChartPanel());
		polDirChartPanel.setPreferredSize(new java.awt.Dimension(400, 330));
		polDirChartPanel.setMinimumSize(new java.awt.Dimension(400, 330));

		polDirChartPanel1=new ChartPanel(getPolyDirChartPanel1());
		polDirChartPanel1.setPreferredSize(new java.awt.Dimension(400, 330));
		polDirChartPanel1.setMinimumSize(new java.awt.Dimension(400, 330));

		polDirChartPanel2=new ChartPanel(getPolyDirChartPanel2());
		polDirChartPanel2.setPreferredSize(new java.awt.Dimension(400, 330));
		polDirChartPanel2.setMinimumSize(new java.awt.Dimension(400, 330));

		polDirChartPanel3=new ChartPanel(getPolyDirChartPanel3());
		polDirChartPanel3.setPreferredSize(new java.awt.Dimension(400, 330));
		polDirChartPanel3.setMinimumSize(new java.awt.Dimension(400, 330));

		JPanel p1=new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
		p1.add(polDirChartPanel);
		p1.add(polDirChartPanel3);

		JPanel p2=new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER, 2,2));
		p2.add(polDirChartPanel1);
		p2.add(polDirChartPanel2);

		pd.add(p1);
		pd.add(p2);
		pd.setBackground(Color.WHITE);
        }
        else if (imode==1)
        {
		polDirChartPanel2=new ChartPanel(getPolyDirChartPanel2());
		polDirChartPanel2.setPreferredSize(new java.awt.Dimension(400, 330));
		polDirChartPanel2.setMinimumSize(new java.awt.Dimension(400, 330));

        pd.add(polDirChartPanel2);
		pd.setBackground(Color.WHITE);
		}

		return pd;

	}

}
