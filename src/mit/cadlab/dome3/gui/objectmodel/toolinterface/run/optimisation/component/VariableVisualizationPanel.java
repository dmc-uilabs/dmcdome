package mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.component;

import org.jfree.chart.*;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.data.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.HashMap;

import mit.cadlab.dome3.swing.Templates;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jun 2, 2004
 * Time: 2:33:17 AM
 * To change this template use Options | File Templates.
 */
public class VariableVisualizationPanel extends JPanel
                                implements ChartMouseListener
{
    public static final GridBagConstraints gbc = null;

    public static final String INDIVIDUAL_CHOSEN = "individual chosen";

    private String _xAxisName = "";
    private String _yAxisName = "";

    private JFreeChart _designSpacePlot;
    private ChartPanel _designSpacePanel;

    private XYSeriesCollection _xySeriesCollection;
    private HashMap _pointMap = new HashMap();


    public VariableVisualizationPanel()
    {
        createComponents();
        configurePlot();
        layoutComponents();
    }

    protected void createComponents()
    {
        XYSeries unrankedPopulation = new XYSeries("dominated individual");
        XYSeries rankedPopulation = new XYSeries("optimal individual");

        _xySeriesCollection = new XYSeriesCollection(unrankedPopulation);
        _xySeriesCollection.addSeries(rankedPopulation);

        _designSpacePlot = ChartFactory.createScatterPlot(null, null, null, _xySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        _designSpacePanel = new ChartPanel(_designSpacePlot);

        _designSpacePanel.addChartMouseListener(this);
    }

    protected void configurePlot()
    {
        _designSpacePlot.getXYPlot().getRangeAxis().setAutoRange(true);
        _designSpacePlot.getXYPlot().getDomainAxis().setAutoRange(true);

        DesignSpacePlotToolTipGenerator toolTip = new DesignSpacePlotToolTipGenerator();
        _designSpacePlot.getXYPlot().getRenderer().setToolTipGenerator(toolTip);

        Shape circle = new Ellipse2D.Double(0, 0, 5, 5);
        _designSpacePlot.getXYPlot().getRenderer().setSeriesPaint(0, Color.GRAY);
        _designSpacePlot.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        ((StandardXYItemRenderer) _designSpacePlot.getXYPlot().getRenderer()).setShape(circle);
    }

    protected void layoutComponents()
    {
        JComponent[] comps = {

            _designSpacePanel
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    public void updateXYSeries(boolean isRankOne, Double var1, Double var2)
    {
        if (isRankOne)
            _xySeriesCollection.getSeries(1).add(var1, var2);
        else
            _xySeriesCollection.getSeries(0).add(var1, var2);
    }

    public void storePointInMap(Double x, Double y, Vector v)
    {
        Point2D p = new Point2D.Double(x.doubleValue(), y.doubleValue());
        _pointMap.put(p, v);
    }

    public void resetPlot()
    {
        _pointMap.clear();
        _xySeriesCollection.getSeries(0).clear();
        _xySeriesCollection.getSeries(1).clear();
    }

    public void chartMouseClicked(ChartMouseEvent event)
    {
    }

    public void chartMouseMoved(ChartMouseEvent event)
    {
    }

    public void setAxesNames(String xAxisName, String yAxisName)
    {
        _xAxisName = xAxisName;
        _yAxisName = yAxisName;
    }

    class DesignSpacePlotToolTipGenerator implements XYToolTipGenerator
    {
        DecimalFormat _format;

        public DesignSpacePlotToolTipGenerator()
        {
            _format = new DecimalFormat();
            _format.setMaximumFractionDigits(4);
        }

        public String generateToolTip(XYDataset xyDataset, int seriesIndex, int item)
        {
            String msg = null;
            XYSeriesCollection xy = (XYSeriesCollection) xyDataset;
            Number x = null, y = null;
            if (xy.getSeries(seriesIndex).getItemCount() > item)
            {
                x = xy.getSeries(seriesIndex).getXValue(item);
                y = xy.getSeries(seriesIndex).getYValue(item);
            }

            String xValue = "", yValue = "";
            if (x instanceof Integer)
            {

            }
            else if (x instanceof Double)
            {
                xValue = _format.format(x.doubleValue());
                yValue = _format.format(y.doubleValue());
            }

            msg = xy.getSeries(seriesIndex).getName() + "  " + _xAxisName + ": " + xValue + "   " + _yAxisName + ": " + yValue;

            return msg;
        }

        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("ParetoPlotToolTipGenerator.clone not implemented");
        }
    }
}
