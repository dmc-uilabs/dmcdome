package org.goof.qmoo.monitor;

import org.goof.qmoo.Individual;
import org.goof.qmoo.Monitor;
import org.goof.qmoo.PopIterator;
import org.goof.qmoo.Population;
import org.goof.qmoo.gui.QMOOGuiConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYDataItem;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 8, 2004
 * Time: 12:09:36 PM
 * To change this template use Options | File Templates.
 */
public class ParetoMonitor
                    implements ChartMouseListener
{
    public static final String PARETO_FRONT = "pareto front";
    public static final String OPTIMIZATION_RESULTS = "optimization results";

    private final Shape circle = new Ellipse2D.Double(0, 0, 5, 5);

    private XYSeriesCollection _individuals;
    private JFreeChart _paretoFront;
    private ChartPanel _panel;

    private int nobjs = 2;

    private double _uR = 5.0, _lR = -5.0;
    private double _uD = 5.0, _lD = -5.0;

    private int _iteration = 0;

    public ParetoMonitor()
    {
        createComponents();
        paintComponents();
        addListener();
    }

    protected void createComponents()
    {

        XYSeries unrankedPop = new XYSeries("");
        XYSeries paretoPop = new XYSeries("");

        _individuals = new XYSeriesCollection(unrankedPop);
        _individuals.addSeries(paretoPop);

        _paretoFront = ChartFactory.createScatterPlot(null, null, null, _individuals, PlotOrientation.VERTICAL, false, true, false);

        _paretoFront.getXYPlot().getRangeAxis().setAutoRange(false);
        _paretoFront.getXYPlot().getRangeAxis().setRange(_lR, _uR);

        _paretoFront.getXYPlot().getDomainAxis().setAutoRange(false);
        _paretoFront.getXYPlot().getDomainAxis().setRange(_lD, _uD);

        ParetoPlotToolTipGenerator toolTip = new ParetoPlotToolTipGenerator();

        Shape circle = new Ellipse2D.Double(0, 0, 4.5, 4.5);
        _paretoFront.getXYPlot().getRenderer().setSeriesPaint(0, Color.GRAY);
        _paretoFront.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        ((StandardXYItemRenderer) _paretoFront.getXYPlot().getRenderer()).setShape(circle);
        ((StandardXYItemRenderer) _paretoFront.getXYPlot().getRenderer()).setToolTipGenerator(toolTip);
    }

    protected void paintComponents()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        _panel = new ChartPanel(_paretoFront);
        JTabbedPane p = QMOOGuiConstants.makeTabbedPane();
        p.add(PARETO_FRONT, _panel);

        f.getContentPane().add(p);

        f.setTitle(OPTIMIZATION_RESULTS);
        f.setFont(QMOOGuiConstants.FONT12B);

        f.show();
        f.pack();
    }

    protected void addListener()
    {
        _panel.addChartMouseListener(this);
    }

    public void go(Monitor m)
    {
        _individuals.getSeries(0).clear();
        _individuals.getSeries(1).clear();

        Population pop = m.getPopulation();
        PopIterator it = pop.newIterator();

        while (it.valid())
        {
            Individual z = it.dereference();
            if (z.getObjective(0) > _uR)
                _uR = z.getObjective(0);
            if (z.getObjective(0) < _lR)
                _lR = z.getObjective(0);
            if (z.getObjective(1) > _uD)
                _uD = z.getObjective(1);
            if (z.getObjective(1) < _lD)
                _lD = z.getObjective(1);

            _paretoFront.getXYPlot().getRangeAxis().setRange(_lR, _uR);
            _paretoFront.getXYPlot().getDomainAxis().setRange(_lD, _uD);

            System.out.println("individual: " + z.getObjective(0) + "   " + z.getObjective(1));

            if (z.getState() == Individual.ALIVE && z.getRank() == 1)
            {
                _individuals.getSeries(1).add(z.getObjective(0), z.getObjective(1));
            }
            else
                _individuals.getSeries(0).add(z.getObjective(0), z.getObjective(1));
            it.next();
        }
    }

    public void chartMouseClicked(ChartMouseEvent event)
    {
        XYItemEntity point = null;
        if (event.getEntity() != null)
        {
            if (event.getEntity() instanceof XYItemEntity)
            {
                point = (XYItemEntity) event.getEntity();
                XYDataItem item = _individuals.getSeries(point.getSeries()).getDataItem(point.getItem());
                System.out.println("x: " + item.getX().toString() + "  y: " + item.getY().toString());
            }
        }
    }

    public void chartMouseMoved(ChartMouseEvent event) {}

    class ParetoPlotToolTipGenerator implements XYToolTipGenerator
    {
        public static final String MSG = "Hello Mr. Engineer, I am point ";

        DecimalFormat _format;

        public ParetoPlotToolTipGenerator()
        {
            _format = new DecimalFormat();
            _format.setMaximumFractionDigits(4);
        }

        public String generateToolTip(XYDataset xyDataset, int series, int item)
        {
            String msg = null;
            XYSeriesCollection xy = (XYSeriesCollection) xyDataset;
            Number x = xy.getSeries(series).getXValue(item);
            Number y = xy.getSeries(series).getYValue(item);

            String xValue = null, yValue = null;
            if (x instanceof Integer)
            {

            }
            else if (x instanceof Double)
            {
                xValue = _format.format(x.doubleValue());
                yValue = _format.format(y.doubleValue());
            }
            msg = MSG + "\n     x: " + xValue + "        y: " + yValue;

            return msg;
        }

        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("ParetoPlotToolTipGenerator.clone not implemented");
        }
    }
}
