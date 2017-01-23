package visualvectorsinputgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.gui.guiutils.customGui.EditableChart;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Feb 25, 2004
 * Time: 3:40:01 PM
 * To change this template use Options | File Templates.
 */
public class VisualVectorsInputGUI extends JPanel {

    protected static String TIME_VECTOR_PARAM_NAME = "time vector";
    protected static String LOAD_VECTOR_PARAM_NAME = "load vector";
    protected static String MAX_TIME_PARAM_NAME = "chart maximum time";

    protected int numIntervals = 24;

    public VisualVectorsInputGUI(ModelInterfaceBase iface) {
        XYSeries xySeries = new XYSeries("");
        for (int i = 0; i <= numIntervals; i++)
            xySeries.add(i, 0.0);

        EditableChartPanel chart = new EditableChartPanel(ChartFactory.createXYLineChart(null, "", "", new XYSeriesCollection(xySeries),
                PlotOrientation.VERTICAL, false, true, false));
        CustomGui.connectTwoVectorsEditableChart(iface, TIME_VECTOR_PARAM_NAME, LOAD_VECTOR_PARAM_NAME,
                MAX_TIME_PARAM_NAME, chart, true);
        this.add(chart);
    }

    /**
     * EditableChartPanel
     * This class extends from ChartPanel.
     * Respnosible for all function related to the
     * Editable chart in the custom gui.
     */
    public class EditableChartPanel extends ChartPanel
            implements ChartMouseListener, EditableChart {

        private XYSeriesCollection _xySeriesCollection;
        private XYPlot _xyPlot;
        private Point _newPoint; // point clicked on the chart

        private int maxLoad = 100;
        private int maxTime = 24;
        protected DomeVectorData timeVec;
        protected DomeVectorData loadVec;

        private EditableChartPanel(JFreeChart chart) {
            super(chart);
            _xyPlot = chart.getXYPlot();
            _xySeriesCollection = (XYSeriesCollection) _xyPlot.getDataset();
            _newPoint = new Point();

            customizeXYPlot();
            addChartMouseListener(this);
        }

        public void setXAxisLabel(String x) {
            _xyPlot.getDomainAxis().setLabel("time (" + x + ")");
        }

        public void setYAxisLabel(String y) {
            _xyPlot.getRangeAxis().setLabel("load (" + y + ")");
        }

        private void customizeXYPlot() {
            // customizing the x - axis of the editable chart - domain axis
            _xyPlot.getDomainAxis().setAutoRange(false);
            _xyPlot.getDomainAxis().setLowerBound(0.0);
            _xyPlot.getDomainAxis().setUpperBound(maxTime);
            _xyPlot.setDomainAnchor(_xyPlot.getDomainAxis().getLowerBound(), false);
            _xyPlot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // customizing the y - axis of the editable chart - range axis
            _xyPlot.getRangeAxis().setAutoRange(true);
            _xyPlot.getRangeAxis().setUpperBound(maxLoad);
            _xyPlot.getRangeAxis().setLowerBound(0.0);
            _xyPlot.setRangeAnchor(_xyPlot.getRangeAxis().getLowerBound(), false);
        }

        public void chartMouseClicked(ChartMouseEvent event) {
            updateXYDataset(_newPoint);
        }

        public void chartMouseMoved(ChartMouseEvent event) {
            Point2D p2 = translateScreenToJava2D(event.getTrigger().getPoint());
            Insets chartInsets = getInsets();
            Rectangle2D scaledArea = getScaledDataArea();
            XYPlot xyPlot = getChart().getXYPlot();

            /**
             *                          --------------  important -----------------
             * the ChartPanel in the JFreeChart class has issues re-painting charts when the window is too large or small.
             * Therefore, the size of the window and not allow users to set it's size to compensate for this problem.
             */
            int x = convertDoubleToCoordinate(xyPlot.getDomainAxis().translateJava2DToValue(p2.getX() + chartInsets.left, scaledArea, _xyPlot.getDomainAxisEdge()));
            double y = xyPlot.getRangeAxis().translateJava2DToValue(p2.getY() + chartInsets.top, scaledArea, _xyPlot.getRangeAxisEdge());
            _newPoint.setLocation(x, y);
        }

        private int convertDoubleToCoordinate(double xy) {
            if (xy < 0.5 && xy > -0.5)
                return 0;
            int x = (int) xy;
            return (xy % x < 0.5) ? x : x + 1;
        }

        public void updateXYDataset(Point point) {
            _xySeriesCollection.getSeries(0).update((int) point.getX(), new Double(point.getY()));
            printData((int) point.getX());
            updateVectorData((int) point.getX());
        }

        public void loadDataset(DomeVectorData x, DomeVectorData y) {
            setXVec(x);
            setYVec(y);
            for (int i = 0; i < x.getSize(); i++) {
                _xySeriesCollection.getSeries(0).update(x.getItem(i).intValue(), y.getItem(i));
            }
        }

        protected void printData(int item) {
            System.out.println("item:" + item);
            System.out.println("    x: " + _xySeriesCollection.getXValue(0, item));
            System.out.println("    y: " + _xySeriesCollection.getYValue(0, item));
        }

        protected void updateVectorData(int item) {
            Number x = _xySeriesCollection.getXValue(0, item);
            int index = timeVec.getFirstIndexForValue(x);
            loadVec.setItem(index, _xySeriesCollection.getYValue(0, item));
        }

        public void setMaxXChartBound(int num) {
            _xyPlot.getDomainAxis().setUpperBound(num);
        }

        public void updateXChartBound(int num) {
            int oldItemsCount = _xySeriesCollection.getSeries(0).getItemCount();
            if (num > oldItemsCount) {
                for (int i = oldItemsCount; i <= num; i++)
                    _xySeriesCollection.getSeries(0).add(i, 0.0);
            } else if (num < oldItemsCount) {
                for (int i = num+1; i < oldItemsCount; i++)
                    _xySeriesCollection.getSeries(0).update(i, new Double(0.0));
 ;
            }
            _xyPlot.getDomainAxis().setUpperBound(num);
        }

        public void setMaxYChartBound(double num) {
            _xyPlot.getRangeAxis().setUpperBound(num);
        }

        public void setMinYChartBound(double min) {
        }

        public void setXVec(DomeVectorData vec) {
            timeVec = vec;
        }

        public void setYVec(DomeVectorData vec) {
            loadVec = vec;
        }
    }
}
