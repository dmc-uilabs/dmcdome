package pvsystemgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.gui.guiutils.customGui.EditableChart;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Feb 25, 2004
 * Time: 3:40:01 PM
 * To change this template use Options | File Templates.
 */
public class ChartCard extends JPanel {

    protected String xVecParamName;
    protected String yVecParamName;
    protected String maxXParamName;

    protected int numIntervals = 24;
    public static final GridBagConstraints gbc = null;
    private JTextField xBound;
    private JTextField yBound;
    private JTextField yMinBound;
    private EditableChartPanel chart;
    protected double yMin;
    protected double yMax;

    public ChartCard(String panelTitle, String xLabel, String yLabel, boolean isInput) {
        XYSeries xySeries = new XYSeries("");

        if (isInput) {
            for (int i = 0; i <= numIntervals; i++)
                xySeries.add(i, 0.0);

            chart = new EditableChartPanel(ChartFactory.createXYLineChart(null, "", "", new XYSeriesCollection(xySeries),
                    PlotOrientation.VERTICAL, false, true, false), xLabel, yLabel, isInput);
        }
        else
            chart = new EditableChartPanel(ChartFactory.createScatterPlot(null, "", "", new XYSeriesCollection(xySeries),
                    PlotOrientation.VERTICAL, false, true, false), xLabel, yLabel, isInput);

        JComponent[] comps = {chart, makeBoundPanel(xLabel, yLabel, isInput)};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
        this.setBorder(BorderFactory.createTitledBorder(null, panelTitle));
    }

    public void setParamNames(String xVecParamName, String yVecParamName,
                              String maxXParamName) {
        this.xVecParamName = xVecParamName;
        this.yVecParamName = yVecParamName;
        this.maxXParamName = maxXParamName;
    }

    public void setNumIntervals(int numIntervals) {
        this.numIntervals = numIntervals;
    }

    public void setYMin(double yMin) {
        this.yMin = yMin;
        yMinBound.setText((new Double(yMin)).toString());
        chart.setMinYChartBound(yMin);
    }

    public void setYMax(double yMax) {
        this.yMax = yMax;
        yBound.setText((new Double(yMax)).toString());
        chart.setMaxYChartBound(yMax);
    }

    private JPanel makeBoundPanel(String xLabel, String yLabel, boolean isInput) {
        JPanel p = new JPanel();

        JLabel boundText = Templates.makeLabel("Bounds of the profile chart");

        JLabel xBoundLabel = Templates.makeLabel("Maximum " + xLabel + ": ");
        xBoundLabel.setToolTipText("Maximum bound of the " + xLabel + " axis");
        xBound = Templates.makeTextField("");

        JLabel yBoundLabel = Templates.makeLabel("Maximum " + yLabel + ": ");
        yBoundLabel.setToolTipText("Maximum bound of the " + yLabel + " axis");
        yBound = Templates.makeTextField("");
        yBound.addActionListener(new MaxYTextFieldActionListener(yBound, chart));

        if (isInput) {
            JComponent[] comps = {boundText,
                                  xBoundLabel, xBound,
                                  yBoundLabel, yBound
            };
            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            GridBagConstraints[] gbcs = {

                new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
                new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
                new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
            };
            Templates.layoutGridBag(p, comps, gbcs);
        } else {
            JLabel yMinBoundLabel = Templates.makeLabel("Minimum " + yLabel + ": ");
            yMinBoundLabel.setToolTipText("Minimum bound of the " + yLabel + " axis");
            yMinBound = Templates.makeTextField("");
            yMinBound.addActionListener(new MinYTextFieldActionListener(yBound, chart));

            JComponent[] comps = {boundText,
                                  xBoundLabel, xBound,
                                  yMinBoundLabel, yMinBound,
                                  yBoundLabel, yBound
            };
            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            GridBagConstraints[] gbcs = {

                new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
                new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
                new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
                new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
                new GridBagConstraints(5, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
            };
            Templates.layoutGridBag(p, comps, gbcs);
        }
        return p;
    }

    public void setInterface(ModelInterfaceBase iface, boolean isInput) {
        CustomGui.connectTwoVectorsEditableChart(iface, xVecParamName, yVecParamName,
                maxXParamName, chart, isInput);
        CustomGui.connectStringOrNumberTextField(iface, maxXParamName, xBound);
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
        protected boolean isInput;
        protected String xLabel, yLabel;

        private EditableChartPanel(JFreeChart chart, String xLabel, String yLabel, boolean isInput) {
            super(chart);
            _xyPlot = chart.getXYPlot();
            _xySeriesCollection = (XYSeriesCollection) _xyPlot.getDataset();
            _newPoint = new Point();
            this.xLabel = xLabel;
            this.yLabel = yLabel;
            this.isInput = isInput;

            customizeXYPlot();
            addChartMouseListener(this);
        }

        public void setXAxisLabel(String x) {
            _xyPlot.getDomainAxis().setLabel(xLabel + " (" + x + ")");
        }

        public void setYAxisLabel(String y) {
            _xyPlot.getRangeAxis().setLabel(yLabel + " (" + y + ")");
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
            if (isInput)
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
            int currentCount = _xySeriesCollection.getSeries(0).getItemCount();
            int x = (int) point.getX();
            if (x > currentCount)
                updateXChartBound(x);
            _xySeriesCollection.getSeries(0).update(x, new Double(point.getY()));
            printData((int) point.getX());
            updateVectorData((int) point.getX());
        }

        public void loadDataset(DomeVectorData x, DomeVectorData y) {
            setXVec(x);
            setYVec(y);
            XYSeries series = _xySeriesCollection.getSeries(0);
            for (int i = 0; i < x.getSize(); i++) {
                series.update(x.getItem(i).intValue(), y.getItem(i));
            }
        }

        public void plotOutput(DomeVectorData x, DomeVectorData y) {
            XYSeries series = _xySeriesCollection.getSeries(0);
            series.clear();
            for (int i = 0; i < x.getSize(); i++) {
                series.add(x.getItem(i).doubleValue(), y.getItem(i));
            }
            _xyPlot.getRangeAxis().setAutoRange(true);
        }

        protected void printData(int item) {
            System.out.println("item:" + item);
            System.out.println("    x: " + _xySeriesCollection.getXValue(0, item));
            System.out.println("    y: " + _xySeriesCollection.getYValue(0, item));
        }

        protected void updateVectorData(int item) {
            Number x = _xySeriesCollection.getXValue(0, item);
            int index = timeVec.getFirstIndexForValue(x);
            if (index == -1) {
                timeVec.populateIncrData();
                index = timeVec.getFirstIndexForValue(x);
            }
            loadVec.setItem(index, _xySeriesCollection.getYValue(0, item));
        }

        public void setMaxXChartBound(int num) {
            _xyPlot.getDomainAxis().setUpperBound(num);
            updateXChartBound(num);
        }

        public void updateXChartBound(int num) {
            int oldItemsCount = _xySeriesCollection.getSeries(0).getItemCount();
            if (num > oldItemsCount) {
                for (int i = oldItemsCount; i <= num; i++)
                    _xySeriesCollection.getSeries(0).add(i, 0.0);
            } else if (num < oldItemsCount) {
                for (int i = num+1; i < oldItemsCount; i++)
                    _xySeriesCollection.getSeries(0).update(i, new Double(0.0));
            }
            _xyPlot.getDomainAxis().setUpperBound(num);
        }

        public void setMaxYChartBound(double num) {
            _xyPlot.getRangeAxis().setUpperBound(num);
        }

        public void setMinYChartBound(double num) {
            _xyPlot.getRangeAxis().setLowerBound(num);
        }

        public void setXVec(DomeVectorData vec) {
            timeVec = vec;
        }

        public void setYVec(DomeVectorData vec) {
            loadVec = vec;
        }
    }

    protected class MaxYTextFieldActionListener implements ActionListener {
        JTextField txtField;
        EditableChart chart;

        public MaxYTextFieldActionListener(JTextField txtField, EditableChart chart) {
            this.chart = chart;
            this.txtField = txtField;
        }

        public void actionPerformed(ActionEvent e) {
            double newValue = (new Double(txtField.getText())).doubleValue();
            chart.setMaxYChartBound(newValue);
        }
    }

    protected class MinYTextFieldActionListener implements ActionListener {
        JTextField txtField;
        EditableChart chart;

        public MinYTextFieldActionListener(JTextField txtField, EditableChart chart) {
            this.chart = chart;
            this.txtField = txtField;
        }

        public void actionPerformed(ActionEvent e) {
            double newValue = (new Double(txtField.getText())).doubleValue();
            chart.setMinYChartBound(newValue);
        }
    }
}
