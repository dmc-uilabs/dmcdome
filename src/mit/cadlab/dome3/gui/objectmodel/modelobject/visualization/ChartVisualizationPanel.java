// ChartVisualizationPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import org.jfree.chart.*;
import org.jfree.chart.renderer.*;
import org.jfree.chart.plot.*;
//import com.jrefinery.chart.demo.EmptyXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.data.CategoryDataset;


import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;


import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;

import mit.cadlab.dome3.objectmodel.modelobject.visualization.EmptyCategoryDataset;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.VisualizationUtils;

import javax.swing.*;

/**
 *
 */
public class ChartVisualizationPanel extends ChartPanel
{
	protected Visualization vis;
	protected VisualizationBuildPanel visPanel;
	//protected PropertyChangeListener propertyListener;
	protected String chartType;
	protected String chartSubType;


	public ChartVisualizationPanel(JFreeChart jFreeChart, VisualizationBuildPanel visP)
	{
		super(jFreeChart);
		visPanel = visP;
		vis = (Visualization) visPanel.getDomeObject();
		vis.addPropertyChangeListener(getPropertyListener());

		chartType = vis.getCurrentVisType();
		chartSubType = vis.getCurrentVisSubType();
		//**setModelChartTypes();          //Should be no use
	}

	/**
	 *
	 * @param jFreeChart
	 * @param b  :boolean properties
	 * @param b1 :boolean save
	 * @param b2 :boolean print
	 * @param b3 :boolean zoom
	 * @param b4 :boolean tooltips
	 */
	public ChartVisualizationPanel(JFreeChart jFreeChart, boolean b, boolean b1, boolean b2, boolean b3, boolean b4, VisualizationBuildPanel visP)
	{
		super(jFreeChart, b, b1, b2, b3, b4);
		visPanel = visP;
		vis = (Visualization) visPanel.getDomeObject();
		vis.addPropertyChangeListener(getPropertyListener());
		chartType = vis.getCurrentVisType();
		chartSubType = vis.getCurrentVisSubType();
		//**setModelChartTypes();

	}

	/**
	 * Constructs a JFreeChart panel.
	 *
	 * @param chart  the chart.
	 * @param width  the preferred width of the panel.
	 * @param height  the preferred height of the panel.
	 * @param minimumDrawWidth  the minimum drawing width.
	 * @param minimumDrawHeight  the minimum drawing height.
	 * @param maximumDrawWidth  the maximum drawing width.
	 * @param maximumDrawHeight  the maximum drawing height.
	 * @param useBuffer  a flag that indicates whether to use the off-screen
	 *                   buffer to improve performance (at the expense of memory).
	 * @param properties  a flag indicating whether or not the chart property
	 *                    editor should be available via the popup menu.
	 * @param save  a flag indicating whether or not save options should be
	 *              available via the popup menu.
	 * @param print  a flag indicating whether or not the print option
	 *               should be available via the popup menu.
	 * @param zoom  a flag indicating whether or not zoom options should be added to the
	 *              popup menu.
	 * @param tooltips  a flag indicating whether or not tooltips should be enabled for the chart.
	 */

	public ChartVisualizationPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight,
	                               boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips, VisualizationBuildPanel visP)
	{
		super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, save, print, zoom, tooltips);
		visPanel = visP;
		vis = (Visualization) visPanel.getDomeObject();
		vis.addPropertyChangeListener(getPropertyListener());
		chartType = vis.getCurrentVisType();
		chartSubType = vis.getCurrentVisSubType();

		//**setModelChartTypes();
	}


	/**
	 * this function is to read charttype is formation in the chart and set the chart type and subtype property
	 */
/*	public void setModelChartTypes()
	{
		JFreeChart jFreeChart = getChart();
		if (jFreeChart.getPlot().getPlotType().equals("XY Plot")) {
			chartType = Visualization.XYCHART;
			if (((XYPlot) jFreeChart.getPlot()).getRenderer() instanceof StandardXYItemRenderer) {
				chartSubType = Visualization.XYLINECHART;
			} else if (((XYPlot) jFreeChart.getPlot()).getRenderer() instanceof AreaXYRenderer) {
				chartSubType = Visualization.XYAREACHART;
			} else {
				chartSubType = null;
			}
		} else if (jFreeChart.getPlot().getPlotType().equals("Category Plot")) {
			chartType = Visualization.CATEGORYCHART;
			if (((CategoryPlot) jFreeChart.getPlot()).getRenderer() instanceof BarRenderer) {
				if (((CategoryPlot) jFreeChart.getPlot()).getOrientation().equals(PlotOrientation.VERTICAL))
				      chartSubType = Visualization.CATEGORYVERTICALCHART;
				else
				      chartSubType = Visualization.CATEGORYHORIZONTALCHART;
			} else if (((CategoryPlot) jFreeChart.getPlot()).getRenderer() instanceof BarRenderer3D) {
				if (((CategoryPlot) jFreeChart.getPlot()).getOrientation().equals(PlotOrientation.VERTICAL))
        			  chartSubType = Visualization.CATEGORYVERTICAL3DCHART;
				else
					  chartSubType = Visualization.CATEGORYHORIZONTAL3DCHART;
			} else {
				chartSubType = null;
			}

		} else if (jFreeChart.getPlot().getPlotType().equals("Pie Plot")) {
			chartType = Visualization.PIECHART;
		} else {
			chartType = "";
		}

		//update visualbuildPanel for changes
		if (chartType.equals(vis.getCurrentVisType()) && chartSubType.equals(vis.getCurrentVisSubType()))
			visPanel.updateTypeComboBox();
		else
			System.err.println("Error in chart type:" + chartType + vis.getCurrentVisType() + chartSubType + vis.getCurrentVisSubType());
	}
*/

	public String getChartType()
	{
		return chartType;
	}

/*	public void setChartType(String newType)
	{
		if (newType.equals(Visualization.XYCHART) || newType.equals(Visualization.PIECHART) || newType.equals(Visualization.CATEGORYCHART)) {
			if (!newType.equals(chartType)) {
				chartType = newType;
				vis.setCurrentVisType(chartType);
			}
		}

	}
*/

	/**
	 *  to work around the right click response different on MAC / Windows
	 */
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger()) {

			if (this.getPopupMenu() != null) {
				this.getPopupMenu().show(this, e.getX(), e.getY());
			}

		}
	}


	public void actionPerformed(ActionEvent event)
	{

		String command = event.getActionCommand();

		if (command.equals(PROPERTIES_ACTION_COMMAND)) {
			this.attemptEditChartProperties();
		} else {

			super.actionPerformed(event);
		}

	}

	/**
	 * Displays a dialog that allows the user to edit the properties for the current chart.
	 */
	public void attemptEditChartProperties()
	{

		PropertyEditPanel panel = new PropertyEditPanel(visPanel);
		int result = JOptionPane.showConfirmDialog(this, panel, "Chart Properties",
		                                           JOptionPane.OK_CANCEL_OPTION,
		                                           JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			panel.updateChartProperties();
		}

	}


	public void showChart()
	{
		JFreeChart newchart = VisualizationUtils.createChart(vis.getSelectedSet(), vis.getCurrentVisType() , vis.getCurrentVisSubType(),vis.isVertical());
		if (newchart == null) {
			Error("wrong new chart type");
			return;
		}
		if (vis.getSelectedSet() != null) {
			/**String name = vis.getCurrentVisType() + ":" + vis.getSelectedSet().getName();
			TextTitle t = new TextTitle(name);
			newchart.setTitle(t);
			**/
			if(vis.getSelectedSet().getJchartProperties() != null) {
                VisualizationUtils.setChartProperties(newchart,vis.getSelectedSet().getJchartProperties(),vis.getSelectedSet());
			}
		}
		setChart(newchart);
	}

	public void repaintToSetChange()
	{
/**		JFreeChart newchart = getChart();
		//to keep visual property
		if (vis.getSelectedSet() != null)
		newchart.setDataset(VisualizationUtils.portIntoChartDataset(chartType, vis.getSelectedSet()));
		//change title
		if (vis.getSelectedSet() != null) {
			newchart.setDataset(VisualizationUtils.portIntoChartDataset(chartType, vis.getSelectedSet()));
			String name = vis.getCurrentVisType() + ":" + vis.getSelectedSet().getName();
			TextTitle t = new TextTitle(name);
			ArrayList list = new ArrayList();
			list.add(t);
			newchart.setTitles(list);

		}
		// paint subtypes
		if (getChart().getPlot() instanceof XYPlot) {
			//make approperiate changes to unit change
			Axis v_axis = getChart().getPlot().getAxis(Axis.VERTICAL);
			Axis h_axis = getChart().getPlot().getAxis(Axis.HORIZONTAL);

			v_axis.setLabel("Y Axis");
			h_axis.setLabel("X Axis");
		}
**/
		JFreeChart newchart = VisualizationUtils.createChart(vis.getSelectedSet(),vis.getCurrentVisType() , vis.getCurrentVisSubType(), vis.isVertical());
		if (newchart == null) {
			Error("wrong new chart type");
			return;
		}
		if (vis.getSelectedSet() != null) {
            /**String name = vis.getCurrentVisType() + ":" + vis.getSelectedSet().getName();
            TextTitle t = new TextTitle(name);
            newchart.setTitle(t);
            **/
            if(vis.getSelectedSet().getJchartProperties() != null) {
                VisualizationUtils.setChartProperties(newchart,vis.getSelectedSet().getJchartProperties(),vis.getSelectedSet());
            }
		}
		setChart(newchart);

	}

	public void setTitle(String title)
	{
		getChart().setTitle(title);
	}

	public String getTitle()
	{

		String chartname = "";
		chartname = this.getChart().getTitle().getText();
		return chartname;

	}

	private void Error(String msg)
	{
		boolean debug = true;
		if (debug)
			System.err.println("Chart Panel: " + msg);
	}


	public void changeTitleUsingDomeName()
	{
		if (vis.getSelectedSet() == null) return;
		if (getTitle().equals(vis.getSelectedSet().getName()))
			return;
		else
			setTitle(vis.getSelectedSet().getName());
	}

	public void changeTitleNotUsingDomeName(String newName)
	{
		if (getTitle().equals(newName)) return;
		setTitle(newName);
	}


	protected PropertyChangeListener getPropertyListener()
	{
		return new ChartVisualizationPanel.ChartPanelPropertyChangeListener();
	}


	class ChartPanelPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(Visualization.NAME)) {
				//repaint chartTitle
/**				String newVisName = (String) newValue;
				String oldtitle = getTitle();
				int index = oldtitle.indexOf(":");
				String subS = oldtitle.substring(index + 1, oldtitle.length());
				setTitle(newVisName + subS);
**/
			} else if (property.equals(Visualization.SETSELECTIONCHANGED)) {
				//System.out.println("visualization setselctionchange catched");
				//showChart();
				repaintToSetChange();
			} else if (property.equals(Visualization.SETSCHANGED)) {
				//System.out.println("visualization setschange catched");
				//showChart();
				repaintToSetChange();
			} else if (property.equals(Visualization.DATACHANGED )) {
				//System.out.println("visualization setschange catched");
				//showChart();
				repaintToSetChange();
			} else if (property.equals(Visualization.CHARTTYPESELECTION)) {
				if (!chartType.equals(vis.getCurrentVisType()))
					chartType = vis.getCurrentVisType();
				if (!chartSubType.equals(vis.getCurrentVisSubType()))
					chartSubType = vis.getCurrentVisSubType();
				showChart();

			} else if (property.equals(Visualization.VERTICAL)) {
				showChart();

			} else if (property.equals(Visualization.VECTORDATACHANGED)) {
				//refresh chart
				System.out.println("gets vector data change");
				repaintToSetChange();
			}  else if (property.equals(Visualization.MATRIXDATACHANGED)) {
				//refresh chart
				System.out.println("gets matrix data change");
				repaintToSetChange();
			}

		}


	}


}


