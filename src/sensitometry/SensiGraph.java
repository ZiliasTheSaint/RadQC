package sensitometry;

import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;

import radQC.RadQCFrame;
import danfulea.utils.FrameUtilities;

/**
 * Class for plotting the sensitometric curve.
 * @author Dan Fulea, 28 Apr. 2015
 *
 */
@SuppressWarnings("serial")
public class SensiGraph extends JFrame{
	private static final Dimension PREFERRED_SIZE = new Dimension(700, 550);
	private Sensitometry sa;
	private final Dimension chartDimension = new Dimension(600, 450);
    private ChartPanel splineRevChartPanel;
    
    public static int interv=200;
	public static double[] xarray;
	public static double[] yarray;
	public double c1=0.0;
	public double c2=0.0;
	public double x0=0.0;
    public double dx=0.0;
   
    /**
     * Constructor
     * @param sa the Sensitometry object
     */
    public SensiGraph(Sensitometry sa)
    {
    	this.sa=sa;
    	this.c1=sa.c1;
		this.c2=sa.c2;
		this.x0=sa.x0;
		this.dx=sa.dx;
    	performGraphicsCalculations();
		createGUI();
		
		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				sa.resources.getString("form.icon.url"), this);

		FrameUtilities.centerFrameOnScreen(this);

		setVisible(true);
		//hf.setEnabled(false);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);// not necessary,
																// exit normal!
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
		//hf.setEnabled(true);
		dispose();
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
    private void createGUI(){
    	
    	JPanel content = new JPanel();//new BorderLayout());
		BoxLayout blc = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(blc);
		//content.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		//JPanel graphP = createMainGraphPanel();
		createMainGraphPanel();
		//content.add(graphP, BorderLayout.CENTER);
		//graphP.setBackground(NuclideExposure.bkgColor);
		content.add(splineRevChartPanel);
		
		content.setBackground(RadQCFrame.bkgColor);
		
		setContentPane(new JScrollPane(content));
		content.setOpaque(true);
		
		pack();
    }
    
    /**
     * Computes plot main series (HD curve)
     */
    private void performGraphicsCalculations()
    {		 
		 if (sa.showB)
		 {
			 double high=sa.loged[sa.loged.length-1]+//1 stepsize!!
			 sa.loged[sa.loged.length-1]-sa.loged[sa.loged.length-2];
			 double low=0.0;//from zero
			 double w=(high-low)/interv;
			 xarray=new double[interv];
			 yarray=new double[interv];
		
			 for (int i=0; i<interv; i++)
			 {
				 xarray[i]=low+i*w;//do not care if exceed UP
				 double da=getY(xarray[i]);
				 yarray[i]=da;
			 }
		 }
  	}
    
    /**
     * Called by performGraphicsCalculations. For given x return the y value.
     * @param x x
     * @return the result
     */
    public double getY(double x)
	{		
		double result = c2+(c1-c2)/(1+Math.exp((x-x0)/dx));//+x*x;
		return result;
	}
    
    /**
     * Create main plot panel
     */
    private void createMainGraphPanel() {
		JFreeChart gammaChart = getChartPanel();
		splineRevChartPanel = new ChartPanel(gammaChart, false, true, true, false, true);
		splineRevChartPanel.setMouseWheelEnabled(true);// mouse wheel zooming!
		splineRevChartPanel.setPreferredSize(chartDimension);
		
	}
    
    /**
     * Create the chart
     * @return the chart
     */
    private JFreeChart getChartPanel() {
/*
		XYSeries serie = new XYSeries("data");//[hf.xs.length];
		XYSeries series1 = new XYSeries("HVL1");
		XYSeries series2 = new XYSeries("HVL2");
		for (int i = 0; i < hf.xs.length; i++) {
			serie.add(hf.xs[i],hf.ys[i]);
			series1.add(hf.xs[0]/2,hf.ys[i]);
			series2.add(hf.xs[0]/4,hf.ys[i]);
		}

		XYSeriesCollection data = new XYSeriesCollection(serie);
		data.addSeries(series1);
        data.addSeries(series2);
        
		NumberAxis xAxis = new NumberAxis("fmmAl");
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis("mmAl");
		XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);
		// ------------------------
		renderer.setSeriesPaint(0, Color.GREEN);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

		XYPlot plot = new XYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// allow chart movement by pressing CTRL and drag with mouse!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		// 1st axis
		plot.setDomainAxis(0, xAxis);// the axis index;axis
		plot.setRangeAxis(0, yAxis);

		int idataset = 0;
		plot.setDataset(idataset, data);// idataset=0!
		plot.setRenderer(idataset, renderer);// idataset=0!

		
			XYSeries dotseries = new XYSeries(
					"Leading points"
					);
			for (int i = 0; i < hf.x.length; i++) {
				dotseries.add(hf.y[i], hf.x[i]);// leading points
			}

			XYSeriesCollection dotdata = new XYSeriesCollection(dotseries);
			XYItemRenderer renderer2 = new StandardXYItemRenderer(
					StandardXYItemRenderer.SHAPES);
			renderer2.setSeriesPaint(0, Color.BLACK);

			idataset = 1;
			plot.setDataset(idataset, dotdata);// idataset=0!
			plot.setRenderer(idataset, renderer2);// idataset=0!
			

		JFreeChart chart = new JFreeChart(
				"HVL",//" Chain decay",
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.setBackgroundPaint(RadQCFrame.bkgColor);
		// chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000,
		// 0, Color.green));
		return chart;
	*/
    	
    	XYSeries series = new XYSeries(sa.resources.getString("graphics.XYSeries"));
		XYSeries series1 = new XYSeries(sa.resources.getString("graphics.XYSeries1"));
		XYSeries series2 = new XYSeries(sa.resources.getString("graphics.XYSeries2"));
		XYSeries series3 = new XYSeries(sa.resources.getString("graphics.XYSeries3"));
		XYSeries series4 = new XYSeries(sa.resources.getString("graphics.XYSeries4"));
	    XYSeries series5 = new XYSeries(sa.resources.getString("graphics.XYSeries5"));
		XYSeries series6 = new XYSeries(sa.resources.getString("graphics.XYSeries6"));

		if(sa.showB)
		for (int i=0;i<interv;i++)
		{
			series.add(xarray[i],yarray[i]);
		}

		for (int i=0;i<sa.loged.length;i++)
		{
			if(!sa.showB)
		     series.add(sa.loged[i],sa.dod[i]);
		   
			series1.add(sa.loged[i],sa.lwg);
		    series2.add(sa.loged[i],sa.mdg);
		    series3.add(sa.loged[i],sa.hghg);
		    series4.add(sa.loged[sa.lwpozg],sa.dod[i]);
		    series5.add(sa.loged[sa.mdpozg],sa.dod[i]);
		    series6.add(sa.loged[sa.hghpozg],sa.dod[i]);
		    
		    //System.out.println(sa.loged[sa.lwpozg]+" ; "+sa.dod[i]);
		    //System.out.println(sa.loged[i]+" ; "+sa.lwg);
        }

		XYSeriesCollection data = new XYSeriesCollection(series);
        data.addSeries(series1);
        data.addSeries(series2);
        data.addSeries(series3);
        data.addSeries(series4);
        data.addSeries(series5);
        data.addSeries(series6);

        NumberAxis xAxis = new NumberAxis(sa.resources.getString("graphics.axes.1"));
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(sa.resources.getString("graphics.axes.2"));
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        //------------------------
        renderer.setSeriesPaint(0,Color.RED);
        renderer.setSeriesPaint(1,Color.ORANGE);
        renderer.setSeriesPaint(2,Color.GREEN);
        renderer.setSeriesPaint(3,Color.BLUE);
        renderer.setSeriesPaint(4,Color.ORANGE);
        renderer.setSeriesPaint(5,Color.GREEN);
        renderer.setSeriesPaint(6,Color.BLUE);
        //---------------------------------
        //renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        
        XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        //----------------------------------------------------------ADDED--------
        plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// allow chart movement by pressing CTRL and drag with mouse!
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		// 1st axis
		plot.setDomainAxis(0, xAxis);// the axis index;axis
		plot.setRangeAxis(0, yAxis);
		//-----------------------------------------------------------------------
		int idataset = 0;
		plot.setDataset(idataset, data);// idataset=0!
		plot.setRenderer(idataset, renderer);// idataset=0!
		///=======
        XYSeries dotseries = new XYSeries(sa.resources.getString("graphics.Points"));
        for (int i=0;i<sa.loged.length;i++)
        {
        	dotseries.add(sa.loged[i],sa.dod[i]);
        }

        XYSeriesCollection dotdata = new XYSeriesCollection(dotseries);
        XYItemRenderer renderer2 = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        renderer2.setSeriesPaint(0,Color.BLACK);
        //plot.setSecondaryDataset(0, dotdata);
        //plot.setSecondaryRenderer(0, renderer2);
        idataset = 1;
		plot.setDataset(idataset, dotdata);// idataset=0!
		plot.setRenderer(idataset, renderer2);// idataset=0!
		
        JFreeChart chart=new JFreeChart(sa.resources.getString("graphics.Title"), JFreeChart.DEFAULT_TITLE_FONT, plot, true);
	    chart.setBackgroundPaint(RadQCFrame.bkgColor);//new GradientPaint(0, 0, Color.white, 1000, 0, Color.green));
        return chart;
	}
}
