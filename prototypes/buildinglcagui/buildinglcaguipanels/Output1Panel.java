package buildinglcagui.buildinglcaguipanels;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import buildinglcagui.BuildingLCAGUIConstants;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;
import com.jrefinery.chart.demo.DemoDatasetFactory;

/**
 * Created by IntelliJ IDEA.
 * Name: Output1Panel
 * User: jacob
 * Date: Aug 13, 2003
 * Time: 11:43:00 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class Output1Panel extends JPanel
{
	private static final GridBagConstraints gbc = null;

	private ModelInterfaceBase _iface;

	// chart components
	private ChartPanel _chartPanel;
	private JFreeChart _barChart;
	private DefaultCategoryDataset _data;

	private JComboBox _outputs;

    private Parameter _lce, _NOx, _SOx, _CO2, _cost;

	private Vector _parameters = new Vector();

	public Output1Panel(ModelInterfaceBase iface)
	{
		_iface = iface;

        initializeParameters();
		makeComponents();
		layoutComponents();
		registerListeners();

		setBorder(BorderFactory.createTitledBorder(null, BuildingLCAGUIConstants.BUILDING_LCA_RESULTS_BORDER, 0, 0, Templates.FONT11B));
	}

	protected void initializeParameters()
	{
		_lce = getParameterByName(BuildingLCAGUIConstants.LCE_PARAMETER);
		_parameters.add(_lce);
		_CO2 = getParameterByName(BuildingLCAGUIConstants.CO2_PARAMETER);
		_parameters.add(_CO2);
		_NOx = getParameterByName(BuildingLCAGUIConstants.NOX_PARAMETER);
		_parameters.add(_NOx);
		_SOx = getParameterByName(BuildingLCAGUIConstants.SOX_PARAMETER);
		_parameters.add(_SOx);
		_cost = getParameterByName(BuildingLCAGUIConstants.COST_PARAMETER);
		_parameters.add(_cost);
	}

	protected void makeComponents()
	{
		_data = new DefaultCategoryDataset();

		createDataset(_lce);
		buildChart();

		_outputs = Templates.makeComboBox(BuildingLCAGUIConstants.outputChoices);
		_outputs.addActionListener(new OutputComboBoxListener(_outputs));
	}

	protected void layoutComponents()
	{
		JComponent[] comps = {

			_chartPanel,
			_outputs

		};

		GridBagConstraints[] gbcs = {

			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 10, 10, 10), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 20, 0, 20), 0, 0)
		};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	protected void registerListeners()
	{

	}

	private void createDataset(Parameter p)
	{

		for (int i = 0; i < ((DomeMatrix) p.getCurrentDataObject()).getRowCount(); i++)
		{
			for (int j = 0; j < ((DomeMatrix) p.getCurrentDataObject()).getColumnCount(); j++)
			{
				DomeMatrix values = (DomeMatrix) p.getCurrentDataObject();
				_data.setValue(values.getItem(i, j), new Integer(j), BuildingLCAGUIConstants.resultLegends[i]);
			}
		}
	}

	private void buildChart()
	{
		_barChart = ChartFactory.createStackedBarChart("", BuildingLCAGUIConstants.VERTICAL_AXIS,
		                                               BuildingLCAGUIConstants.HORIZONTAL_AXIS, _data, PlotOrientation.HORIZONTAL, false, false, false);

		_chartPanel = new ChartPanel(_barChart);

	}

	class OutputComboBoxListener implements ActionListener
	{
        private JComboBox _comboBox;

		public OutputComboBoxListener(JComboBox comboBox)
        {
	        _comboBox = comboBox;
        }

		public void actionPerformed(ActionEvent e)
		{
			createDataset((Parameter)_parameters.get(_comboBox.getSelectedIndex()));
		}
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
