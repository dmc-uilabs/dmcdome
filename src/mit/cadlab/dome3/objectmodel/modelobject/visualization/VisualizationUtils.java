// VisualizationUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.*;
import org.jfreechart.EmptyXYDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 *
 */
public class VisualizationUtils
{

	public static JFreeChart createEmptyChart(String visType, String visSubType, boolean orientation)
	{
		if (visType.equals(Visualization.XYCHART)) {
			// create a default chart based on empty data...
			String title = "XY Plot (no data)";
			String domain = "X axis";
			String range = "Y axis";
			XYDataset data = new EmptyXYDataset();

			if (visSubType.equals(Visualization.XYLINECHART)) {
				JFreeChart chart = ChartFactory.createXYLineChart(title, domain, range, data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYSCATTERCHART)) {
				JFreeChart chart = ChartFactory.createScatterPlot(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYSTEPCHART)) {
				JFreeChart chart = ChartFactory.createXYStepChart(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYAREACHART)) {
				JFreeChart chart = ChartFactory.createXYAreaChart(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYSTACKEDAREACHART)) {
				JFreeChart chart = ChartFactory.createStackedAreaXYChart(title, domain, range, new DefaultTableXYDataset(),  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYSTEPAREACHART)) {
				JFreeChart chart = ChartFactory.createXYStepAreaChart(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
		} else if (visType.equals(Visualization.PIECHART)) {
			//disable it for now
			// create a default chart based on empty data...
			//  String title = "To Be PieChart";
			//  String domain = "X axis";
			//  String range = "Y axis";
			//  XYDataset data = new EmptyXYDataset();
			//  JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

			// then customise it a little...
			// chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
			// return chart;
		} else if (visType.equals(Visualization.CATEGORYCHART)) {
			// create a default chart based on empty data...
			String title = "Category Plot";
			String domain = "Categories";
			String range = "Value";
			CategoryDataset data = new DefaultCategoryDataset();
			if (visSubType.equals(Visualization.CATEGORYBARCHART)) {
				JFreeChart chart = ChartFactory.createBarChart(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;

			}
			if (visSubType.equals(Visualization.CATEGORY3DBARCHART)) {
				JFreeChart chart = ChartFactory.createBarChart3D(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.STACKEDBARCHART )) {
				JFreeChart chart = ChartFactory.createStackedBarChart(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.STACKEDBAR3DCHART)) {
				JFreeChart chart = ChartFactory.createStackedBarChart3D(title, domain, range, data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}

		}
		return null;
	}


	public static JFreeChart createChart(DomeObjectSet set, String visType, String visSubType, boolean orientation)
	{
		if(set == null) return createEmptyChart(visType, visSubType, orientation);
		AbstractDataset data = portIntoChartDataset(visType, visSubType, set);
		if (visType.equals(Visualization.XYCHART)) {
			// create a default chart based on empty data...
			//**String title = "XY Plot (no data)";
			String title = set.getName();
			String domain = "X axis";
			String range = "Y axis";

			if (visSubType.equals(Visualization.XYLINECHART)) {
				JFreeChart chart = ChartFactory.createXYLineChart(title, domain, range, (XYDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYSCATTERCHART)) {
				JFreeChart chart = ChartFactory.createScatterPlot(title, domain, range, (XYDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYSTEPCHART)) {
				JFreeChart chart = ChartFactory.createXYStepChart(title, domain, range, (XYDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.XYAREACHART)) {
				JFreeChart chart = ChartFactory.createXYAreaChart(title, domain, range, (XYDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				chart.getPlot().setForegroundAlpha(0.5f);
				return chart;
			}
			if (visSubType.equals(Visualization.XYSTACKEDAREACHART)) {
				JFreeChart chart = ChartFactory.createStackedAreaXYChart(title, domain, range, (TableXYDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				//** it's weird that for this type of chart, it's not autorange although it should be autorange
				Axis axis;
				if (chart.getPlot() instanceof XYPlot) {
					axis = ((XYPlot) chart.getPlot()).getRangeAxis();
					if (axis instanceof NumberAxis) {
						((NumberAxis) axis).zoomRange(0, 1);
						((NumberAxis) axis).setAutoRange(true);
					}
					axis = ((XYPlot) chart.getPlot()).getDomainAxis();
					if (axis instanceof NumberAxis) {
						((NumberAxis) axis).zoomRange(0, 1);
						((NumberAxis) axis).setAutoRange(true);
					}
				} else {
					axis = ((CategoryPlot) chart.getPlot()).getRangeAxis();
					if (axis instanceof NumberAxis) {
						((NumberAxis) axis).zoomRange(0, 1);
						((NumberAxis) axis).setAutoRange(true);
					}
					axis = ((CategoryPlot) chart.getPlot()).getDomainAxis();
					if (axis instanceof NumberAxis) {
						((NumberAxis) axis).zoomRange(0, 1);
						((NumberAxis) axis).setAutoRange(true);
					}

				}
				return chart;
			}
			if (visSubType.equals(Visualization.XYSTEPAREACHART)) {
				JFreeChart chart = ChartFactory.createXYStepAreaChart(title, domain, range, (XYDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				chart.getPlot().setForegroundAlpha(0.5f);
				return chart;
			}
		} else if (visType.equals(Visualization.PIECHART)) {
			//disable it for now
			// create a default chart based on empty data...
			//  String title = "To Be PieChart";
			//  String domain = "X axis";
			//  String range = "Y axis";
			//  XYDataset data = new EmptyXYDataset();
			//  JFreeChart chart = ChartFactory.createXYChart(title, domain, range, data, true);

			// then customise it a little...
			// chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
			// return chart;
		} else if (visType.equals(Visualization.CATEGORYCHART)) {
			// create a default chart based on empty data...
			//**String title = "Category Plot";
			String title = set.getName();
			String domain = "Categories";
			String range = "Value";

			if (visSubType.equals(Visualization.CATEGORYBARCHART)) {
				JFreeChart chart = ChartFactory.createBarChart(title, domain, range, (CategoryDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;

			}
			if (visSubType.equals(Visualization.CATEGORY3DBARCHART)) {
				JFreeChart chart = ChartFactory.createBarChart3D(title, domain, range, (CategoryDataset)data, orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL ,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.STACKEDBARCHART )) {
				JFreeChart chart = ChartFactory.createStackedBarChart(title, domain, range, (CategoryDataset)data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
			if (visSubType.equals(Visualization.STACKEDBAR3DCHART)) {
				JFreeChart chart = ChartFactory.createStackedBarChart3D(title, domain, range, (CategoryDataset)data,  orientation ? PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL,true,true,false);

				// then customise it a little...
				//chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.red));
				return chart;
			}
		}

		return null;
	}

	/**
	 *  Check if this domeobjectset is okay for ported into Chart
	 * @return boolean isValid
	 */

	public static AbstractDataset portIntoChartDataset(String option, String subOption, DomeObjectSet set)
	{
		ArrayList alldata = set.getSelectedSeries();
		DomeVectorData xaxis = set.getDomeVectorDataBySeries(set.getHorizontalIndex());
        int indexinAlldata = set.getIndexinSelectedSeriesforHorizontalAxis();

		if (option.equals(Visualization.XYCHART)) {
			if (alldata.size() == 0) {
				System.out.println("empty data");
				return new EmptyXYDataset();
			}

/**			else if (alldata.length == 1) {
				System.err.println("only one item in data");
				return new EmptyXYDataset();
			}
 **/

			//DefaultXYDataset takes in String[] seriesname, and Object[serie][itemx][itemy]
			//the first item in domeobjectset will be X axis
			// <P>
			// The dimensions of the data array are [series][item][x=0, y=1]. The x-values should be Number
			// or Date objects, the y-values should be Number objects.  Any other types are interpreted as
			// zero. The data will be sorted so that the x-values are ascending.
			ArrayList seriesNameArrayList = new ArrayList();
			ArrayList yaxis = new ArrayList();
			for (int i = 0; i < alldata.size() ; i++) {
				if (i != indexinAlldata) {    //if (!alldata.get(i).equals(xaxis))
					yaxis.add(alldata.get(i));
				}
			}

			if (yaxis.size() == 0) {
				System.out.println("no y axis data");
				return new EmptyXYDataset();
			}

			for(int i = 0;i<set.getSeriesSize(); i++){
				if(i!= set.getHorizontalIndex() && set.isSelected(i)) seriesNameArrayList.add(set.getSeriesAlias(i));
			}

			AbstractDataset dataset;
			if(subOption.equals(Visualization.XYSTACKEDAREACHART)) dataset = new DefaultTableXYDataset();
			else dataset = new XYSeriesCollection();

			for (int i = 0; i < yaxis.size(); i++) {
				DomeVectorData d_y = (DomeVectorData)yaxis.get(i);
                XYSeries series;
				if(subOption.equals(Visualization.XYSTACKEDAREACHART)) series = new XYSeries((String)seriesNameArrayList.get(i),false);
				else series = new XYSeries((String)seriesNameArrayList.get(i));
				if (d_y == null) {
					System.err.println("null item in data");
					return new EmptyXYDataset();
				}
				if (xaxis != null)
					if (d_y.getSize() != xaxis.getSize()) {
						System.err.println("x-axis item doesn't match y-axis item!");
						return new EmptyXYDataset();
					}
				if (xaxis == null)
					for (int j = 0; j < d_y.getSize(); j++) {
						series.add(j, d_y.getItem(j));
					}
				else
					for (int j = 0; j < xaxis.getSize(); j++) {
						series.add(xaxis.getItem(j), d_y.getItem(j));
					}
				if(subOption.equals(Visualization.XYSTACKEDAREACHART)) ((DefaultTableXYDataset)dataset).addSeries(series);
				else ((XYSeriesCollection)dataset).addSeries(series);
			}

			return dataset;
		} else if (option.equals(Visualization.PIECHART)) {
			//disable piechart for now

		} else if (option.equals(Visualization.CATEGORYCHART)) {
			//for now, it will display "vertical bar chart"

			if (alldata.size() == 0) {
				System.out.println("empty data");
				return new DefaultCategoryDataset();
			}

			//the way to make vector into category dataset is to make each vector a category
			// Category names are generated automatically ("Category 1", "Category 2", etc).
			// public DefaultCategoryDataset(String[] seriesNames, Number[][] data) {
			// data[series][category]

			//ArrayList seriesNameArrayList = new ArrayList();
			ArrayList seriesNameArrayList = new ArrayList();
			for(int i = 0;i<set.getSeriesSize(); i++){
				if(set.isSelected(i)) seriesNameArrayList.add(set.getSeriesAlias(i));
			}

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (int i = 0; i < alldata.size() ; i++) {
				//if (isUsingDomeName())
				//	seriesNameArrayList.add(alldata[i].getName());
				//else
				//	seriesNameArrayList.add(getAlias(alldata[i]));

				for (int j = 0; j < set.getCategorySize() ; j++) {
					dataset.addValue(((DomeVectorData)alldata.get(i)).getItem(j), "Series "+(j+1), (String)seriesNameArrayList.get(i));
				}

			}
			return dataset;
		}



		//other case
		return null;
	}

	public static void setChartProperties(JFreeChart chart, ChartProperties chartProperties, DomeObjectSet set)
	{
        Axis axis;
        Plot plot = chart.getPlot();
		int[] colorInt = new int[3];
		int[] insetsInt = new int[4];
        Color newColor = null;
		Stroke newStroke = null;
		Insets newInsets = null;
        Font newFont = null;

		//for chartarea
		chart.setAntiAlias(chartProperties.isAntiAliased());
        if(chartProperties.isUseDomeSetName() == true ) chart.setTitle(set.getName());
		else chart.setTitle(chartProperties.getPlotTitle());
		chart.setBorderVisible(chartProperties.isShowBorder());
		newStroke = new BasicStroke(chartProperties.getBorderStroke());
		chart.setBorderStroke(newStroke);
		colorInt = chartProperties.getChartBackgroundColor();
		newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
		chart.setBackgroundPaint(newColor);
		colorInt = chartProperties.getBorderColor();
		newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
		chart.setBorderPaint(newColor);

		//for legend
		if (chartProperties.isShowLegend() == true) {
			StandardLegend new_legend = (StandardLegend)Legend.createInstance(chart);
            newStroke = new BasicStroke(chartProperties.getLegendOutlineStroke());
			new_legend.setOutlineStroke(newStroke);
			colorInt = chartProperties.getLegendOutlineColor();
			newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
			new_legend.setOutlinePaint(newColor);
			colorInt = chartProperties.getLegendBackgroundColor();
			newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
			new_legend.setBackgroundPaint(newColor);
			colorInt = chartProperties.getLegendLabelColor();
			newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
            new_legend.setItemPaint(newColor);
			newFont = new Font(chartProperties.getLegendFontName(), chartProperties.getLegendFontStyle(), chartProperties.getLegendFontSize());
			new_legend.setItemFont(newFont);

			chart.setLegend(new_legend);

		} else {
			chart.setLegend(null);
		}

		//for plot-appearance
		colorInt = chartProperties.getAppOutlineColor();
		newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
		plot.setOutlinePaint(newColor);
		newStroke = new BasicStroke(chartProperties.getAppOutlineStroke());
		plot.setOutlineStroke(newStroke);
		colorInt = chartProperties.getAppBackgroundColor();
		newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
		plot.setBackgroundPaint(newColor);
		insetsInt = chartProperties.getAppInsets();
		newInsets = new Insets(insetsInt[0],insetsInt[1],insetsInt[2],insetsInt[3]);
		plot.setInsets(newInsets);
		plot.setBackgroundImage(new ImageIcon(chartProperties.getAppImage()).getImage());

		//set colors of series
		ArrayList seriesArray = new ArrayList();
		String[] seriesList;

		Dataset ds = null;
		if (plot instanceof XYPlot)	ds = ((XYPlot)plot).getDataset();
		else ds = ((CategoryPlot)plot).getDataset();
		if (ds != null && ds instanceof XYDataset) {
			int count = ((SeriesDataset) ds).getSeriesCount();

			for (int i = 0; i < count; i++) {
				seriesArray.add(((XYDataset) ds).getSeriesName(i));
			}
		} else if (ds != null && ds instanceof CategoryDataset) {
			int count = ((DefaultCategoryDataset) ds).getRowCount();

			for (int i = 0; i < count; i++) {
				seriesArray.add(((DefaultCategoryDataset) ds).getRowKey(i));
			}
		} else if (ds != null && ds instanceof PieDataset) {
			//do sth else

		}

		//build series array
		seriesList = (String[]) seriesArray.toArray(new String[]{});

		if (plot instanceof XYPlot) {
			XYItemRenderer itemRenderer = ((XYPlot)plot).getRenderer();
			for (int i = 0; i < seriesList.length; i++) {
				colorInt = set.getSeriesColor(seriesList[i]);
				if (colorInt != null) {
					newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
					itemRenderer.setSeriesPaint(i, newColor);
					itemRenderer.setSeriesStroke(i,new BasicStroke(chartProperties.getAppSeriesStroke()));
				}
			}
		} else {
			CategoryItemRenderer itemRenderer = ((CategoryPlot)plot).getRenderer();
			for (int i = 0; i < seriesList.length; i++) {
                colorInt = set.getSeriesColor(i);
                if (colorInt != null) {
                    newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
                    itemRenderer.setSeriesPaint(i, newColor);
	                //**itemRenderer.setSeriesStroke(i,new BasicStroke(chartProperties.getAppSeriesStroke()));
                }
			}
		}


		//for plot-vertical axis
        if(plot instanceof XYPlot) {
            axis = ((XYPlot)plot).getRangeAxis();
            if (chartProperties.getvXYLabel() != null){
	        if (!(chartProperties.getvUnit().trim().length() == 0))
                axis.setLabel(chartProperties.getvXYLabel() + "(" + chartProperties.getvUnit() + ")");
            else
                axis.setLabel(chartProperties.getvXYLabel());
            }
        }
        else {
            axis = ((CategoryPlot)plot).getRangeAxis();
	        if (chartProperties.getvBarLabel()!= null){
            if (!(chartProperties.getvUnit().trim().length() == 0))
                axis.setLabel(chartProperties.getvBarLabel() + "(" + chartProperties.getvUnit() + ")");
            else
                axis.setLabel(chartProperties.getvBarLabel());
	        }
        }

        newFont = new Font(chartProperties.getvFontName(), chartProperties.getvFontStyle(), chartProperties.getvFontSize());
        axis.setLabelFont(newFont);
        colorInt = chartProperties.getvColor();
        newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
        axis.setLabelPaint(newColor);
        insetsInt = chartProperties.getvLabelInsets();
        newInsets = new Insets(insetsInt[0],insetsInt[1],insetsInt[2],insetsInt[3]);
        axis.setLabelInsets(newInsets);
        insetsInt = chartProperties.getvTickLabelInsets();
        newInsets = new Insets(insetsInt[0],insetsInt[1],insetsInt[2],insetsInt[3]);
        axis.setTickLabelInsets(newInsets);
        axis.setTickMarksVisible(chartProperties.isvShowTickMarks());
        axis.setTickLabelsVisible(chartProperties.isvShowTickLabel());
        newFont = new Font(chartProperties.getvTickLabelFontName() , chartProperties.getvTickLabelFontStyle() , chartProperties.getvTickLabelFontSize() );
        axis.setTickLabelFont(newFont);

        if(axis instanceof NumberAxis) {
            ((NumberAxis)axis).zoomRange(0,1);
	        ((NumberAxis)axis).setAutoRange(true);
	        ((NumberAxis)axis).setAutoRange(chartProperties.isvAutoRange());
            if (!chartProperties.isvAutoRange()) {
                ((NumberAxis)axis).setLowerBound(chartProperties.getvMinimumRange());
                ((NumberAxis)axis).setUpperBound(chartProperties.getvMaximumRange());
            }

            float[] dash = {2,2};
            if (plot instanceof XYPlot) {
	            ((XYPlot)plot).setRangeGridlinesVisible(chartProperties.isvShowGrid());
	            colorInt = chartProperties.getvGridColor();
	            newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
	            ((XYPlot)plot).setRangeGridlinePaint(newColor);
	            newStroke = new BasicStroke(chartProperties.getvGridStroke(),0,2,0,dash,0);
	            ((XYPlot)plot).setRangeGridlineStroke(newStroke);
            } else {
	            ((CategoryPlot)plot).setRangeGridlinesVisible(chartProperties.isvShowGrid());
	            colorInt = chartProperties.getvGridColor();
	            newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
	            ((CategoryPlot)plot).setRangeGridlinePaint(newColor);
	            newStroke = new BasicStroke(chartProperties.getvGridStroke(),0,2,0,dash,0);
	            ((CategoryPlot)plot).setRangeGridlineStroke(newStroke);
            }
        }

        //for plot-horizontal axis
        if(plot instanceof XYPlot) {
            axis = ((XYPlot)plot).getDomainAxis();
	        if (chartProperties.gethXYLabel() != null){
            if (!(chartProperties.gethUnit().trim().length() == 0))
                axis.setLabel(chartProperties.gethXYLabel() + "(" + chartProperties.gethUnit() + ")");
            else
                axis.setLabel(chartProperties.gethXYLabel());
	        }
        }
        else {
            axis = ((CategoryPlot)plot).getDomainAxis();
	        if (chartProperties.gethBarLabel()!= null){
            if (!(chartProperties.gethUnit().trim().length() == 0))
                axis.setLabel(chartProperties.gethBarLabel() + "(" + chartProperties.gethUnit() + ")");
            else
                axis.setLabel(chartProperties.gethBarLabel());
	        }
        }

        newFont = new Font(chartProperties.gethFontName(), chartProperties.gethFontStyle(), chartProperties.gethFontSize());
        axis.setLabelFont(newFont);
        colorInt = chartProperties.gethColor();
        newColor = new Color(colorInt[0],colorInt[1],colorInt[2]);
        axis.setLabelPaint(newColor);
        insetsInt = chartProperties.gethLabelInsets();
        newInsets = new Insets(insetsInt[0],insetsInt[1],insetsInt[2],insetsInt[3]);
        axis.setLabelInsets(newInsets);
        insetsInt = chartProperties.gethTickLabelInsets();
        newInsets = new Insets(insetsInt[0],insetsInt[1],insetsInt[2],insetsInt[3]);
        axis.setTickLabelInsets(newInsets);
        axis.setTickMarksVisible(chartProperties.ishShowTickMarks());
        axis.setTickLabelsVisible(chartProperties.ishShowTickLabel());
        newFont = new Font(chartProperties.gethTickLabelFontName() , chartProperties.gethTickLabelFontStyle() , chartProperties.gethTickLabelFontSize() );
        axis.setTickLabelFont(newFont);

        if(axis instanceof NumberAxis) {
	        ((NumberAxis)axis).zoomRange(0,1);
		    ((NumberAxis)axis).setAutoRange(true);
            ((NumberAxis)axis).setAutoRange(chartProperties.ishAutoRange());
            if (!chartProperties.ishAutoRange()) {
                ((NumberAxis)axis).setLowerBound(chartProperties.gethMinimumRange());
                ((NumberAxis)axis).setUpperBound(chartProperties.gethMaximumRange());
            }

	        if (plot instanceof XYPlot) {
		        if (chartProperties.gethXYLabel() != null) {           //in the case that XYPlot has been saved
			        ((XYPlot) plot).setDomainGridlinesVisible(chartProperties.ishShowGrid());
			        colorInt = chartProperties.gethGridColor();
			        newColor = new Color(colorInt[0], colorInt[1], colorInt[2]);
			        ((XYPlot) plot).setDomainGridlinePaint(newColor);
			        float[] dash = {2,2};
			        newStroke = new BasicStroke(chartProperties.gethGridStroke(),0,2,0,dash,0);
			        ((XYPlot) plot).setDomainGridlineStroke(newStroke);
		        }
	        }
        }

	}
}
