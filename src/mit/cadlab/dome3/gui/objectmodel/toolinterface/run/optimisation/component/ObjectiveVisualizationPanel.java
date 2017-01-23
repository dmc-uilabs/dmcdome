package mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.component;

import mit.cadlab.dome3.swing.Templates;
import org.jfree.chart.*;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYDataItem;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 5, 2003
 * Time: 6:17:02 PM
 * To change this template use Options | File Templates.
 */
public class ObjectiveVisualizationPanel extends JPanel
                                implements ChartMouseListener
{
    public static final GridBagConstraints gbc = null;
    public static final String INDIVIDUAL_CHOSEN = "individualChosen";

    private JFreeChart _paretoFront;
    private ChartPanel _paretoFrontPanel;

    private String _xAxisName = "";
    private String _yAxisName = "";

    private XYSeriesCollection _xySeriesCollection;
    private HashMap _pointMap = new HashMap();

    public ObjectiveVisualizationPanel()
    {
        createComponents();
        configurePlot();
        layoutComponents();
    }

    protected void createComponents()
    {
        XYSeries unrankedPop = new XYSeries("dominated individual");
        XYSeries paretoPop = new XYSeries("optimal individual");
        _xySeriesCollection = new XYSeriesCollection(unrankedPop);
        _xySeriesCollection.addSeries(paretoPop);
        _paretoFront = ChartFactory.createScatterPlot(null, null, null, _xySeriesCollection, PlotOrientation.VERTICAL, false, true, false);
        _paretoFrontPanel = new ChartPanel(_paretoFront);
        _paretoFrontPanel.addChartMouseListener(this);
    }

    protected void configurePlot()
    {
        _paretoFront.getXYPlot().getRangeAxis().setAutoRange(true);
        _paretoFront.getXYPlot().getDomainAxis().setAutoRange(true);

        ParetoPlotToolTipGenerator toolTip = new ParetoPlotToolTipGenerator();
        _paretoFront.getXYPlot().getRenderer().setToolTipGenerator(toolTip);

        Shape circle = new Ellipse2D.Double(0, 0, 5, 5);
        _paretoFront.getXYPlot().getRenderer().setSeriesPaint(0, Color.GRAY);
        _paretoFront.getXYPlot().getRenderer().setSeriesPaint(1, Color.RED);
        ((StandardXYItemRenderer) _paretoFront.getXYPlot().getRenderer()).setShape(circle);
    }

    protected void layoutComponents()
    {
        JComponent[] comps = {

            _paretoFrontPanel
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    public void updateXYSeries(boolean isRankOne, Double obj1, Double obj2)
    {
        if (isRankOne)
            _xySeriesCollection.getSeries(1).add(obj1, obj2);
        else
            _xySeriesCollection.getSeries(0).add(obj1, obj2);
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
        XYItemEntity point = null;
        if (event.getEntity() != null)
        {
            if (event.getEntity() instanceof XYItemEntity)
            {
                Vector v = null;
                point = (XYItemEntity) event.getEntity();
                XYDataItem item = _xySeriesCollection.getSeries(point.getSeries()).getDataItem(point.getItem());
                Number xValue = item.getX();
                Number yValue = item.getY();
                if (xValue instanceof Double && yValue instanceof Double)
                {
                    Point2D p = new Point2D.Double(xValue.doubleValue(), yValue.doubleValue());
                    v = (Vector) _pointMap.get(p);
                }
                firePropertyChange(INDIVIDUAL_CHOSEN, null, v);
            }
        }
    }

    public void chartMouseMoved(ChartMouseEvent event){}

    public void setAxesNames(String xAxisName, String yAxisName)
    {
        _xAxisName = xAxisName;
        _yAxisName = yAxisName;
    }

    class ParetoPlotToolTipGenerator implements XYToolTipGenerator
    {
        DecimalFormat _format;

        public ParetoPlotToolTipGenerator()
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

