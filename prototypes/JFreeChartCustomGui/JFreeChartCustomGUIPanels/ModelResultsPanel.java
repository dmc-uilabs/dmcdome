package JFreeChartCustomGui.JFreeChartCustomGUIPanels;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;

import javax.swing.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultCategoryDataset;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * Name: ModelResultsPanel
 * User: jacob
 * Date: Aug 5, 2003
 * Time: 10:19:20 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class ModelResultsPanel extends JPanel
{
	public static final GridBagConstraints gbc = null;

	public static final String SELECT_A_SEASON = "selected season: ";
	public static final String CHART_TITLE = "Tokyo City power demand delivered: ";
	public static final String TITLE = "Power Demand Delivery Curves";

	public static final JLabel selectSeasonLabel = Templates.makeLabel(SELECT_A_SEASON);

	public static final String X_AXIS = "Hour of the Day";
	public static final String Y_AXIS = "KW/H";

	// parameter names
	public static final String GENERATED_POWER_SUPPLY = "generated power supply";

	// times of year data was taken
	public static final String AVERAGE_SPRING_FALL_WEEKDAY = "average spring/fall weekday";
	public static final String AVERAGE_SPRING_FALL_WEEKEND = "average spring/fall weekend";
	public static final String AVERAGE_SUMMER_WEEKDAY = "average summer weekday";
	public static final String AVERAGE_SUMMER_WEEKEND = "average summer weekend";
	public static final String AVERAGE_WINTER_WEEKDAY = "average winter weekday";
	public static final String AVERAGE_WINTER_WEEKEND = "average winter weekend";
	public static final String PEAK_DEMAND = "peak power demand";

	// season combo box choices
	public static final String[] comboBoxChoice =
	        {
		        PEAK_DEMAND,
		        AVERAGE_SUMMER_WEEKDAY,
		        AVERAGE_SUMMER_WEEKEND,
		        AVERAGE_SPRING_FALL_WEEKDAY,
		        AVERAGE_SPRING_FALL_WEEKEND,
		        AVERAGE_WINTER_WEEKDAY,
		        AVERAGE_WINTER_WEEKEND
	        };

	public static final String STORAGE = "storage";
	public static final String NUCLEAR = "nuclear";
	public static final String COAL = "coal";
	public static final String LNG = "LNG";
	public static final String OIL = "oil";
	public static final String GCC = "GCC";
	public static final String SOFC = "SOFC";
	public static final String PUMP = "pump";
	public static final String XXX = "XXX";
	public static final String YYY = "YYY";
	public static final String HYDRO = "hydro";


	// type of power generation processes
	public static final String[] seriesNames =
	        {
		        STORAGE,
		        NUCLEAR,
		        COAL,
		        LNG,
		        OIL,
		        GCC,
		        SOFC,
		        PUMP,
		        HYDRO,
		        XXX,
		        YYY
	        };


	public static int NUMBER_HOURS = 24;

	private JComboBox _seasonData;

	protected ModelInterfaceBase _iface;

	private JFreeChart _chart;
	private HashMap _dataSets;
	private ChartPanel _chartPanel;

	private Parameter _generatedPowerSupply;

	public ModelResultsPanel(ModelInterfaceBase iface)
	{
		_iface = iface;

		_dataSets = new HashMap();  // hashMap that will hold the parsed information for the power demand satisfaction for each day

		initializeParameters();
		makeResultsPanel();
	}

	protected void initializeParameters()
	{
		_generatedPowerSupply = getParameterByName(GENERATED_POWER_SUPPLY);
	}

	protected void makeResultsPanel()
	{
		this.setBorder(BorderFactory.createTitledBorder(null, CHART_TITLE, 0, 0, Templates.FONT11B));

		this._seasonData = Templates.makeComboBox(comboBoxChoice);
		this._seasonData.setSelectedItem(PEAK_DEMAND);

		DomeMatrix dataMatrix = (DomeMatrix) _generatedPowerSupply.getCurrentDataObject();
		_chart = createChart(PEAK_DEMAND, dataMatrix);

		CategoryPlot plot = this._chart.getCategoryPlot();

		plot.getRenderer().setSeriesPaint(0, new Color(255, 0, 0));
		plot.getRenderer().setSeriesPaint(1, new Color(255, 204, 0));
		plot.getRenderer().setSeriesPaint(2, new Color(0, 0, 0));
		plot.getRenderer().setSeriesPaint(3, new Color(204, 204, 204));
		plot.getRenderer().setSeriesPaint(4, new Color(153, 102, 153));
		plot.getRenderer().setSeriesPaint(5, new Color(0, 153, 153));
		plot.getRenderer().setSeriesPaint(6, new Color(0, 102, 153));
		plot.getRenderer().setSeriesPaint(7, new Color(0, 153, 102));
		plot.getRenderer().setSeriesPaint(8, new Color(51, 204, 51));
		plot.getRenderer().setSeriesPaint(9, new Color(52, 120, 120));
		plot.getRenderer().setSeriesPaint(10, new Color(200, 200, 120));

		this._chartPanel = new ChartPanel(this._chart);
		this._chartPanel.setBorder(BorderFactory.createTitledBorder(null, TITLE, 0, 0, Templates.FONT11B));

		dataMatrix.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				if (e.getPropertyName().equals(DataObject.VALUE))
				{
					ModelResultsPanel.this._seasonData.setSelectedItem(ModelResultsPanel.PEAK_DEMAND);
					DomeMatrix matrix = (DomeMatrix) ModelResultsPanel.this._generatedPowerSupply.getCurrentDataObject();
					JFreeChart newChart = createChart(ModelResultsPanel.PEAK_DEMAND, matrix);
					ModelResultsPanel.this._chart = newChart;
					ModelResultsPanel.this._chartPanel.setChart(newChart);
					ModelResultsPanel.this._chartPanel.repaint();
				}
			}
		});

		this._seasonData.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox cb = (JComboBox) e.getSource();
				ModelResultsPanel.this._chart.getCategoryPlot().
				        setDataset(ModelResultsPanel.this.
				                   createDefaultCategoryDataset((Number[][]) ModelResultsPanel.this._dataSets.get((String) cb.getSelectedItem())));
			}
		});

		JComponent[] comps =
		        {
			        this._chartPanel,
			        selectSeasonLabel, this._seasonData,
		        };

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {

			//this._chartPanel
			new GridBagConstraints(0, 2, 3, 3, 1.0, 1.0, gbc.SOUTH, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),

			// this._seasonDataLabel, this._seasonData
			new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

		};

		Templates.layoutGridBag(this, comps, gbcs);

	}

	private JFreeChart createChart(String key, DataObject obj)
	{
		if (obj instanceof DomeMatrix)
		{
			int check = 0;

//			TODO
//			TODO: in the future change the number of day samples during the year
//			TODO: at this point it is hardcoded to be 7, since data is taken for 7 data samples
//			TODO
//
			DomeMatrix vmatrix = (DomeMatrix) obj;
			Number[][][] items = new Number[7][NUMBER_HOURS][seriesNames.length];
			for (int i = 0; i < vmatrix.getRowCount(); i++)
			{
				if (i % 24 == 0 && i != 0)
				{
					this._dataSets.put(comboBoxChoice[check], items[check]);
					check++;
				}
				for (int j = 0; j < vmatrix.getColumnCount(); j++)
				{
					items[check][i - 24 * check][j] = vmatrix.getItem(i, j);
				}
			}

//			 put the last set of data in the HashMap
			this._dataSets.put(ModelResultsPanel.comboBoxChoice[check], items[check]);

			return ChartFactory.createStackedAreaChart("", X_AXIS, Y_AXIS, createDefaultCategoryDataset((Number[][]) _dataSets.get(key)), PlotOrientation.VERTICAL, true, false, false);
		}
		else
			return null;
	}

	private DefaultCategoryDataset createDefaultCategoryDataset(Number[][] temp)
	{
		DefaultCategoryDataset a = new DefaultCategoryDataset();
		for (int i = 0; i < temp.length; i++)
		{
			for (int j = 0; j < temp[i].length; j++)
			{
				a.addValue(temp[i][j], seriesNames[j], new Integer(i + 1));
			}
		}
		return a;
	}

	protected Parameter getParameterByName(String paramName)
	{
		Iterator it = this._iface.getModelObjectParameters().iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof Parameter)
			{
				if (((Parameter) o).getName().equals(paramName))
					return (Parameter) o;
			}
		}
		throw new RuntimeException("unable to find parameter " + paramName);
	}

}
