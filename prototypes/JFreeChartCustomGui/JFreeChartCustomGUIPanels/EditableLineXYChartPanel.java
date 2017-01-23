package JFreeChartCustomGui.JFreeChartCustomGUIPanels;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;


/**
 * Created by IntelliJ IDEA.
 * Name: EditableLineXYChartPanel
 * User: jacob
 * Date: Jul 19, 2003
 * Time: 5:10:43 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class EditableLineXYChartPanel extends JPanel
{
	public static final String TABLE_DATA = "power demand for ";
	public static final int NUMBER_HOURS = 24;

	public static final String XY_DATA_CHANGED = "x-y data has changed";

	public static final String X_AXIS_TITLE = "hour of the day ";
	public static final String Y_AXIS_TITLE = "power demand (Wh)";

	public PropertyChangeSupport _listeners;

	private ChartPanel _chartPanel;
	private JFreeChart _lineXYChart;
	private XYSeriesCollection _xySeriesCollection;
	private XYPlot _xyPlot;

    private CustomPoint _activePoint = new CustomPoint();

	public EditableLineXYChartPanel(XYSeriesCollection xySeries)
	{
		this._xySeriesCollection = xySeries;
		this.createJFreeChartDataObjects();
		if (this._xyPlot != null)
			this.customizeXYPlot();
		this.createChartPanel();

		this._listeners = new PropertyChangeSupport(this);

	}

	private void createJFreeChartDataObjects()
	{
		this._lineXYChart = ChartFactory.createLineXYChart("", X_AXIS_TITLE, Y_AXIS_TITLE, this._xySeriesCollection, false, true, false);
		this._xyPlot = this._lineXYChart.getXYPlot();
	}

	private void customizeXYPlot()
	{
		// customizing the x - axis of the editable chart - domain axis
        this._xyPlot.getDomainAxis().setAutoRange(false);
		this._xyPlot.getDomainAxis().setMinimumAxisValue(1.0);
		this._xyPlot.getDomainAxis().setMaximumAxisValue(24.0);
		this._xyPlot.getDomainAxis().setAnchorValue(1.0);
        this._xyPlot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// customizing the y - axis of the editable chart - range axis
		this._xyPlot.getRangeAxis().setAutoRange(false);
		this._xyPlot.getRangeAxis().setAnchorValue(0.0);
		this._xyPlot.getRangeAxis().setMaximumAxisValue(200E06);
		this._xyPlot.getRangeAxis().setMinimumAxisValue(0.0);

	}

	private void createChartPanel()
	{
		this._chartPanel = new ChartPanel(this._lineXYChart);

		this.add(this._chartPanel);

		this._chartPanel.addChartMouseListener(new ChartMouseListener()
		{
			private int _x;
			private double _y;
			private ChartPanel _panel;

            public void chartMouseClicked(ChartMouseEvent e)
            {
	            EditableLineXYChartPanel.this.updateXYDataset(this._x, this._y);

            }

			public void chartMouseMoved(ChartMouseEvent e)
			{
				this._panel = EditableLineXYChartPanel.this._chartPanel;
				Point2D p2 = this._panel.translateScreenToJava2D(e.getTrigger().getPoint());
				Insets chartInsets = this._panel.getInsets();
				Rectangle2D scaledArea = this._panel.getScaledDataArea();
				XYPlot xyPlot = this._panel.getChart().getXYPlot();

				//                         --------------  important -----------------

				// the ChartPanel in the JFreeChart class has issues re-painting charts when the window is too large or small.
				// Therefore, the size of the window and not allow users to set it's size to compensate for this problem.


				this._x = this.convertDoubleToCoordinate(xyPlot.getDomainAxis().translateJava2DtoValue((float)(e.getTrigger().getPoint().getX() - chartInsets.left),
																										scaledArea,
																										xyPlot.getDomainAxisLocation()));
				this._y = xyPlot.getRangeAxis().getMaximumAxisValue() + ((xyPlot.getRangeAxis().getMaximumAxisValue()-xyPlot.getRangeAxis().getMinimumAxisValue())
				                                                                            *(chartInsets.top-p2.getY()))/scaledArea.getHeight();
			}

			public int convertDoubleToCoordinate(double xy)
			{
				int x = (int) xy;
				return  (xy % x < 0.5) ? x : x + 1;
			}

		});
	}

	private void updateXYDataset(int x, double y)
	{
		this._xySeriesCollection.getSeries(0).update(x, new Double(y));
		_activePoint.setPoint(x, y);
		this.updatedXYSeriesCollection();
	}

	public XYSeriesCollection getXYSeriesCollection()
	{
		return this._xySeriesCollection;
	}

	public void setXYSeriesCollection(XYSeriesCollection newXYSeriesCollection)
	{
		this._xySeriesCollection = newXYSeriesCollection;
	}

    public void updatedXYSeriesCollection()
    {
	    _listeners.firePropertyChange(XY_DATA_CHANGED, null, this._xySeriesCollection);
    }

	public JFreeChart getLineXYChart()
	{
		return this._lineXYChart;
	}

	public ChartPanel getChartPanel()
	{
		return this._chartPanel;
	}

	public int getActivePointX()
	{
		return _activePoint.getX();
	}

	public double getActivePointY()
	{
		return _activePoint.getY();
	}

	public void addPropertyChangeListener(
					PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(
					String propertyName,
					PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(
					PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(
					String propertyName,
					PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(propertyName, listener);
	}

	class CustomPoint
	{
		int _x;
		double _y;

		public CustomPoint(){}

		public void setPoint(int x, double y)
		{
			_x = x;
			_y = y;
		}

		public int getX()
		{
			return _x;
		}

		public double getY()
		{
			return _y;
		}
	}
}
