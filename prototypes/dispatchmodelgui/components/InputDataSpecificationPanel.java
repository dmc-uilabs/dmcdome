package dispatchmodelgui.components;

import dispatchmodelgui.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.jfree.data.XYSeriesCollection;
import org.jfree.data.XYSeries;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 19, 2003
 * Time: 12:33:17 AM
 * To change this template use Options | File Templates.
 */
public class InputDataSpecificationPanel extends JPanel
                                            implements ActionListener
{
    public static final GridBagConstraints gbc = null;

    public PropertyChangeSupport _listener;

    private JTextField _numberOfDaysTextField, _editableNameOfDayTextField, _numberOfDaysRepresentedTextField,
                            _interestRateTextField, _propertyTaxTextField;
    private JButton _addADay, _removeADay;
    private JComboBox _dayComboBox;
    private JLabel _numberOfDaysInYearAccountedForLabel;

    private Integer _numberOfDays;

    private EditableChartPanel _editableChart;

    public void actionPerformed(ActionEvent event)
    {
        if(event.getSource().equals(_numberOfDaysTextField))
            _listener.firePropertyChange(GUIConstants.CHANGE_NUMBER_OF_DAYS, null, null);
        else if(event.getSource().equals(_addADay))
        {
            Integer oldValue = _numberOfDays;
            _numberOfDays = new Integer(oldValue.intValue() + 1);
            _listener.firePropertyChange(GUIConstants.CHANGE_DAY_ADDED, oldValue, _numberOfDays);
        }
        else if(event.getSource().equals(_removeADay))
        {
            Integer oldValue = _numberOfDays;
            _numberOfDays = new Integer(oldValue.intValue() - 1);
            _listener.firePropertyChange(GUIConstants.CHANGE_DAY_REMOVED, oldValue, _numberOfDays);
        }

        else if(event.getSource().equals(_dayComboBox))
            _listener.firePropertyChange(GUIConstants.CHANGE_DAY_COMBO_BOX, null, null);
        else if(event.getSource().equals(_editableNameOfDayTextField))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_NAME_OF_DAY, null, _editableNameOfDayTextField.getText());
        }

        else if(event.getSource().equals(_numberOfDaysRepresentedTextField))
            _listener.firePropertyChange(GUIConstants.CHANGE_DAYS_REPRESENTED, null, _numberOfDaysRepresentedTextField.getText());
        else if(event.getSource().equals(_interestRateTextField))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_INTEREST_RATE, null, _interestRateTextField.getText());
        }
        else if(event.getSource().equals(_propertyTaxTextField))
        {
            _listener.firePropertyChange(GUIConstants.CHANGE_PROPERTY_TAX, null, _propertyTaxTextField.getText());
        }
        else
            return;
    }

    public InputDataSpecificationPanel()
    {
        super();

        configurePanel();

        JComponent[] comps = {

            makeInputDayDataPanel(),
            makePowerDemandInputPanel(),
            makeSocietyFactorsPanel()
        };


        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GUIConstants.gbc.NORTH, GUIConstants.gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0, GUIConstants.gbc.WEST, GUIConstants.gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GUIConstants.gbc.NORTH, GUIConstants.gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        GUIConstants.layoutGridBag(this, comps, gbcs);
    }

    protected void configurePanel()
    {
        _listener = new PropertyChangeSupport(this);
        setSize(GUIConstants.DEFAULT_SIZE);
    }

    protected JPanel makeInputDayDataPanel()
    {
        createInputDayDataComponents();
        return layoutInputDayDataComponents();
    }

    protected JPanel makeSocietyFactorsPanel()
    {
        createSocietyFactorsComponents();
        return layoutSocietyFactorsComponents();
    }

    protected JPanel makePowerDemandInputPanel()
    {
        createPowerDemandInputPanel();
        return layoutPowerDemandInputPanel();
    }

    private void createInputDayDataComponents()
    {
        _numberOfDays = new Integer(GUIConstants.DEFAULT_NUMBER_OF_DAYS);
        _numberOfDaysTextField =  GUIConstants.makeTextField();
        _numberOfDaysTextField.setEditable(false);
        _numberOfDaysTextField.addActionListener(this);
        _numberOfDaysTextField.setText(_numberOfDays.toString());

        _addADay = GUIConstants.makeButton(GUIConstants.ADD);
        _addADay.setEnabled(false);
        _addADay.addActionListener(this);
        _removeADay = GUIConstants.makeButton(GUIConstants.REMOVE);
        _removeADay.addActionListener(this);
        _removeADay.setEnabled(false);

        _dayComboBox = GUIConstants.makeComboBox(GUIConstants.dayComboBoxChoice);
        _dayComboBox.addActionListener(this);

        _editableNameOfDayTextField = GUIConstants.makeTextField();
        _editableNameOfDayTextField.addActionListener(this);

        _numberOfDaysRepresentedTextField = GUIConstants.makeTextField();
        _numberOfDaysRepresentedTextField.addActionListener(this);

        _numberOfDaysInYearAccountedForLabel = GUIConstants.makeLabel(GUIConstants.TOTAL_NUMBER_OF_DAYS_ACCOUNTED_FOR);


    }

    private void createSocietyFactorsComponents()
    {
        _interestRateTextField = GUIConstants.makeTextField();
        _interestRateTextField.addActionListener(this);
        _propertyTaxTextField = GUIConstants.makeTextField();
        _propertyTaxTextField.addActionListener(this);
    }

    private void createPowerDemandInputPanel()
    {
    }

    private JPanel layoutInputDayDataComponents()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, GUIConstants.BORDER_DAY_ATTRIBUTES, 0, 0, GUIConstants.BOLD_FONT));

        JComponent[] comps = {

            // number of days: (text field) (add) (remove)
            GUIConstants.numberOfDaysLabel, _numberOfDaysTextField, _addADay, _removeADay,

            // (combo box) - used to select specific day
            _dayComboBox,

			// day label for attributes
            GUIConstants.dayAttributesLabel,

            // name: (text field)
			GUIConstants.nameLabel, _editableNameOfDayTextField,

            //
			GUIConstants.numberOfDaysRepresentedLabel, _numberOfDaysRepresentedTextField, GUIConstants.daysLabel,

            // number of days accounted for
			_numberOfDaysInYearAccountedForLabel,
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, GUIConstants.gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 100, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),

            new GridBagConstraints(0, 1, 4, 1, 1.0, 0.0, GUIConstants.gbc.CENTER, GUIConstants.gbc.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0),

            new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(20, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 5, 4, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(5, 5, 20, 0), 0, 0)

        };

        GUIConstants.layoutGridBag(p, comps, gbcs);

        return p;

    }

    private JPanel layoutSocietyFactorsComponents()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, GUIConstants.BORDER_MARKET_ATTRIBUTES, 0, 0, GUIConstants.BOLD_FONT));

        JComponent[] comps = {

            // property tax: (text field) %
            GUIConstants.propertyTaxLabel, _propertyTaxTextField, GUIConstants.yenLabel,

            // interest rate: (text field) yen
            GUIConstants.interestRateLabel, _interestRateTextField, GUIConstants.percentInputLabel,

            // spacer
            new JPanel(),

            makeImagePanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GUIConstants.gbc.WEST, GUIConstants.gbc.BOTH, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 3, 3, 1, 1.0, 0.0, GUIConstants.gbc.SOUTH, GUIConstants.gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        GUIConstants.layoutGridBag(p, comps, gbcs);

        return p;
    }

    /**
     * Special panel that contains the image
     * And author label
     * @return
     */
    private JPanel makeImagePanel()
    {
        JPanel p = new JPanel();

        JComponent[] comps = {

            GUIConstants.globeIconLabel, GUIConstants.cadlabLabel
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GUIConstants.gbc.WEST, GUIConstants.gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GUIConstants.gbc.SOUTHWEST, GUIConstants.gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0)
        };

        GUIConstants.layoutGridBag(p, comps, gbcs);
        return p;
    }

    /**
     * EditableChartPanel
     * This class extends from ChartPanel.
     * Respnosible for all function related to the
     * Editable chart in the custom gui.
     */

    public static class EditableChartPanel extends ChartPanel
                                                implements ChartMouseListener
    {
        private InputDataSpecificationPanel _ip;

        private XYSeriesCollection _xySeriesCollection;
        private XYPlot _xyPlot;

        // point clicked on the chart
        private Point _newPoint;

        protected static EditableChartPanel createEditableChart(InputDataSpecificationPanel a)
        {

            /**
             * we first create an empty data set:
             * an array of 25 entries of (0,0.0)
             */

            // ...

            XYSeries xySeries = new XYSeries("");

            for(int i = 0; i <= GUIConstants.NUMBER_OF_HOURS.intValue(); i++)
                                                                                xySeries.add(i, 0.0);

            // ...

            EditableChartPanel cp = new EditableChartPanel(a, ChartFactory.createXYLineChart(null, GUIConstants.X_AXIS_TITLE,
                            GUIConstants.Y_AXIS_TITLE, new XYSeriesCollection(xySeries), PlotOrientation.VERTICAL, false, true, false));

            cp.setBorder(BorderFactory.createTitledBorder(null, GUIConstants.BORDER_POWER_DEMAND, 0, 0, GUIConstants.BOLD_FONT));

            return cp;
        }

        private EditableChartPanel(InputDataSpecificationPanel a, JFreeChart chart)
        {
            super(chart);
            _ip = a;
            _xyPlot = chart.getXYPlot();
            _xySeriesCollection = (XYSeriesCollection)_xyPlot.getDataset();
            _newPoint = new Point();

            customizeXYPlot();
            addChartMouseListener(this);
        }

        private void customizeXYPlot()
        {
            // customizing the x - axis of the editable chart - domain axis
            _xyPlot.getDomainAxis().setAutoRange(false);
            _xyPlot.getDomainAxis().setLowerBound(1.0);
            _xyPlot.getDomainAxis().setUpperBound(24.0);
            _xyPlot.setDomainAnchor(_xyPlot.getDomainAxis().getLowerBound(), false);
            _xyPlot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // customizing the y - axis of the editable chart - range axis
            _xyPlot.getRangeAxis().setAutoRange(false);
            _xyPlot.getRangeAxis().setUpperBound(200E06);
            _xyPlot.getRangeAxis().setLowerBound(0.0);
            _xyPlot.setRangeAnchor(_xyPlot.getRangeAxis().getLowerBound(), false);

        }

        public void chartMouseClicked(ChartMouseEvent event)
        {
            updateXYDataset(_newPoint);
            _ip.notifyChartPanelChange(_xySeriesCollection);
        }

        public void chartMouseMoved(ChartMouseEvent event)
        {
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

        private int convertDoubleToCoordinate(double xy)
        {
            int x = (int) xy;
            return (xy % x < 0.5) ? x : x + 1;
        }

        public void updateXYDataset(Point point)
        {
            _xySeriesCollection.getSeries(0).update((int)point.getX(), new Double(point.getY()));
        }
    }

    /**
     * get/set methods for class member variables
     */

    public JTextField getEditableNameOfDayTextField()
    {
        return _editableNameOfDayTextField;
    }

    public JTextField getNumberOfDaysTextField()
    {
        return _numberOfDaysTextField;
    }

    public JComboBox getDayComboBox()
    {
        return _dayComboBox;
    }

    public JTextField getDaysRepresentedTextField()
    {
        return _numberOfDaysRepresentedTextField;
    }

    public JLabel getNumberOfDaysAccountedFor()
    {
        return _numberOfDaysInYearAccountedForLabel;
    }

    public EditableChartPanel getEditableChartPanel()
    {
        return _editableChart;
    }

    public JTextField getInterestRateTextField()
    {
        return _interestRateTextField;
    }

    public JTextField getPropertyTaxTextField()
    {
        return _propertyTaxTextField;
    }

    /**
     * This method calls the static method
     * inside the nested class EditableLineXYChartPanel
     * and returns the editable chart inside a panel
     * @return
     */
    private JPanel layoutPowerDemandInputPanel()
    {
        _editableChart =  EditableChartPanel.createEditableChart(this);
        return _editableChart;
    }

    private void notifyChartPanelChange(XYSeriesCollection xy)
    {
        Double[] d = new Double[24];
        XYSeries xySeries = xy.getSeries(0);

		for(int i = 0; i < d.length; i++)
				d[i] = (Double)xySeries.getYValue(i+1);

        _listener.firePropertyChange(GUIConstants.CHANGE_EDIT_X_Y, null, d);
    }

    /**
     * This method will replace the combo box selection
     * With the new name as soon as it changes.
     */
    public void swapNameInComboBox()
	{
		int index = _dayComboBox.getSelectedIndex();
		if(_dayComboBox.getItemCount() == 1)
		{
			_dayComboBox.removeAllItems();
			_dayComboBox.addItem(_editableNameOfDayTextField.getText());
		}
		else
		{
			_dayComboBox.insertItemAt(_editableNameOfDayTextField.getText(), index);
			_dayComboBox.removeItemAt(index+1);
			_dayComboBox.setSelectedIndex(index);
		}
	}

    /**
     * PropertyChangeSupport listeners
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        _listener.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        _listener.removePropertyChangeListener(propertyChangeListener);
    }

    public void addPropertyChangeListener(String s, PropertyChangeListener propertyChangeListener)
    {
        _listener.addPropertyChangeListener(s, propertyChangeListener);
    }

    public void removePropertyChangeListener(String s, PropertyChangeListener propertyChangeListener)
    {
        _listener.removePropertyChangeListener(s, propertyChangeListener);
    }
}
