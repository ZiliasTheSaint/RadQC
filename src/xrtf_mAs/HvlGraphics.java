package xrtf_mAs;

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
import danfulea.utils.FrameUtilities;

/**
 * Class for plotting graphs (HVL)
 * @author Dan Fulea, 11 Jun. 2013
 *
 */
@SuppressWarnings("serial")
public class HvlGraphics extends JFrame{
	private static final Dimension PREFERRED_SIZE = new Dimension(700, 550);
	private MainFrame hf;
	private final Dimension chartDimension = new Dimension(600, 450);
    private ChartPanel splineRevChartPanel;
    
    /**
     * Constructor
     * @param hf the MainFrame object
     */
    public HvlGraphics(MainFrame hf)
    {
    	this.hf=hf;
    	//performGraphicsCalculations();
		createGUI();
		
		setDefaultLookAndFeelDecorated(true);
		FrameUtilities.createImageIcon(
				hf.resources.getString("form.icon.url"), this);

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
		
		content.setBackground(MainFrame.bkgColor);
		
		setContentPane(new JScrollPane(content));
		content.setOpaque(true);
		
		pack();
    }
    
    /**
     * Create main graphic panel
     */
    private void createMainGraphPanel() {
		JFreeChart gammaChart = getChartPanel();
		splineRevChartPanel = new ChartPanel(gammaChart, false, true, true, false, true);
		splineRevChartPanel.setMouseWheelEnabled(true);// mouse wheel zooming!
		splineRevChartPanel.setPreferredSize(chartDimension);
		
	}
    
    /**
     * Create the HVL chart
     * @return the result
     */
    private JFreeChart getChartPanel() {

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
		chart.setBackgroundPaint(MainFrame.bkgColor);
		// chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000,
		// 0, Color.green));
		return chart;
	}
}
