package dispatchmodelgui.components;

import dispatchmodelgui.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultCategoryDataset;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 19, 2003
 * Time: 12:34:57 AM
 * To change this template use Options | File Templates.
 */
public class ModelResultsPanel extends JPanel
{
    private static final GridBagConstraints gbc = null;

    private JComboBox _seasonData;
    private ModelResultsChartPanel _cP;
    private HashMap _dataSets = new HashMap();

    public ModelResultsPanel()
    {
        super();

        configurePanel();

        JComponent[] comps = {

            makeResultsPanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        GUIConstants.layoutGridBag(this, comps, gbcs);

    }

    protected void configurePanel()
    {
        setSize(GUIConstants.DEFAULT_SIZE);
    }

    protected JPanel makeResultsPanel()
    {
        makeResultComponents();
        return layoutResultComponents();
    }

    private void makeResultComponents()
    {
        _seasonData = GUIConstants.makeComboBox(GUIConstants.comboBoxChoice);
        _seasonData.setSelectedItem(GUIConstants.PEAK_DEMAND);
        _seasonData.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox) e.getSource();
				_cP.getChart().getCategoryPlot().setDataset(createNewDataset(
                        (Number[][]) _dataSets.get(new Integer(cb.getSelectedIndex()))));
            }
        });

        _cP = ModelResultsChartPanel.createModelResultsChart();

    }

    private JPanel layoutResultComponents()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, GUIConstants.BORDER_RESULTS_PANEL,
                                                                        0, 0, GUIConstants.BOLD_FONT));

        JComponent[] comps = {

            _cP,

            GUIConstants.selectSeasonLabel,

            _seasonData

        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0)
        };

        GUIConstants.layoutGridBag(p, comps, gbcs);

        return p;
    }

    protected static class ModelResultsChartPanel extends ChartPanel
    {
        protected static ModelResultsChartPanel createModelResultsChart()
        {
            return new ModelResultsChartPanel(ChartFactory.createStackedAreaChart
                    (null, GUIConstants.X_AXIS, GUIConstants.Y_AXIS, createDefaultCategoryDataset(), PlotOrientation.VERTICAL, true, false, false));
        }

        protected ModelResultsChartPanel(JFreeChart chart)
        {
            super(chart);
            customizeChart(chart);
        }

        protected void customizeChart(JFreeChart chart)
        {
            chart.getCategoryPlot().getRangeAxis().setLabelFont(GUIConstants.REGULAR_FONT);
            chart.getCategoryPlot().getDomainAxis().setLabelFont(GUIConstants.REGULAR_FONT);
            chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(255, 0, 0));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(1, new Color(255, 204, 0));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(2, new Color(0, 0, 0));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(3, new Color(204, 204, 204));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(4, new Color(153, 102, 153));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(5, new Color(0, 153, 153));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(6, new Color(0, 102, 153));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(7, new Color(0, 153, 102));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(8, new Color(51, 204, 51));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(9, new Color(52, 120, 120));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(10, new Color(200, 200, 120));
        }

        private static DefaultCategoryDataset createDefaultCategoryDataset()
        {
            DefaultCategoryDataset a = new DefaultCategoryDataset();

            for (int i = 0; i < GUIConstants.NUMBER_OF_HOURS.intValue(); i++)
            {
                for (int j = 0; j < GUIConstants.seriesNames.length; j++)
                {
                    a.addValue(new Double(0.0), GUIConstants.seriesNames[j], new Integer(i + 1));
                }
            }
            return a;
        }
    }

    protected DefaultCategoryDataset createNewDataset(Number[][] data)
    {
        DefaultCategoryDataset a = new DefaultCategoryDataset();
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[i].length; j++)
            {
                a.addValue(data[i][j], GUIConstants.seriesNames[j], new Integer(i+1));
            }
        }
        return a;
    }

    public void swapDaySelectComboBox(int index, String newName)
    {
    	if(_seasonData.getItemCount() == 1)
		{
			_seasonData.removeAllItems();
			_seasonData.addItem(newName);
		}
		else
		{
			_seasonData.insertItemAt(newName, index);
			_seasonData.removeItemAt(index+1);
			_seasonData.setSelectedIndex(index);
		}

        _seasonData.setSelectedIndex(0);
	}

    public void swapPowerGenerationTypeNameInStackedAreaChart(int index, String newName)
    {
        for(int i = 0; i < GUIConstants.seriesNames.length; i++)
        {
            if(i == index)
            {
                GUIConstants.seriesNames[i] = newName;
            }
        }
        _seasonData.setSelectedIndex(0);
    }

    public void updateData(Number[][] value)
    {
        int check = 0;

        Number[][][] items = new Number[7][GUIConstants.NUMBER_OF_HOURS.intValue()]
                                                        [GUIConstants.seriesNames.length];
        for (int i = 0; i < value.length; i++)
        {
            if (i % 24 == 0 && i != 0)
            {
                _dataSets.put(new Integer(check), items[check]);
                check++;
            }
            for (int j = 0; j < value[i].length; j++)
            {
                items[check][i - 24 * check][j] = value[i][j];
            }
        }

        _seasonData.setSelectedIndex(0);
    }
}
