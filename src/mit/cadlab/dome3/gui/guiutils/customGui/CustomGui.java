// CustomGui.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.customGui;

import edu.iupui.rg.ucum.units.UnitAtom;
import mit.cadlab.dome3.objectmodel.dataobject.*;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.FileUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 * This class has convenience methods for connecting DOME parameters to
 * Swing GUI components.
 */
public class CustomGui
{
    /**
     *
     * @param iface
     * @param paramName       name of the real, integer or string parameter in the DOME interface
     * @param tf              the textfield in the custom GUI that the parameter's value will be connected to
     */
    public static void connectStringOrNumberTextField(ModelInterfaceBase iface, String paramName, JTextField tf) {
        Parameter p = getParameterByName(iface, paramName);
        if (p!=null) {
            if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
               p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
               tf.setText((getRealValue(p)));
               setBackgroundColour(p, tf, tf.getBackground()); //set the inital state to match the model
               tf.addActionListener(new RealTextFieldActionListener(p, tf));
               p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealIntStringDataListener(tf));
            } else if (p.getCurrentType().equals(DomeInteger.TYPE_INFO.getTypeName())) {
                p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
                tf.setText((getIntValue(p)));
                setBackgroundColour(p, tf, tf.getBackground()); //set the inital state to match the model
                tf.addActionListener(new IntTextFieldActionListener(p, tf));
                p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealIntStringDataListener(tf));
            } else if (p.getCurrentType().equals(DomeString.TYPE_INFO.getTypeName())) {
                p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
                tf.setText((getStringValue(p)));
                setBackgroundColour(p, tf, tf.getBackground()); //set the inital state to match the model
                tf.addActionListener(new StringTextFieldActionListener(p, tf));
                p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealIntStringDataListener(tf));
            }else if (p.getCurrentType().equals(IterationVariable.TYPE_INFO.getTypeName())) {
                p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
                tf.setText((getRealValue(p)));
                setBackgroundColour(p, tf, tf.getBackground()); //set the inital state to match the model
                tf.addActionListener(new RealTextFieldActionListener(p, tf));
                p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealIntStringDataListener(tf));
            } else
                System.out.println("ConnectStringOrNumberTextField Error: Parameter " + paramName + " is type " + p.getCurrentType());
         }
    }

    /**
     *
     * @param iface
     * @param paramName     name of the Enumeration parameter in the DOME interface
     * @param cb            the combobox in the custom GUI that the parameter's value will be connected to
     */
    public static void connectEnumerationComboBox(ModelInterfaceBase iface, String paramName, JComboBox cb) {
        Parameter p = getParameterByName(iface, paramName);
        if (p != null) {
            if (p.getCurrentType().equals(DomeEnumeration.TYPE_INFO.getTypeName())) {
                p.addPropertyChangeListener(new ParameterStatusChangeListener(p, cb));

                //set model of combo box to match options in Enumeration
                DefaultComboBoxModel cbModel = new DefaultComboBoxModel(((EnumerationData) p.getCurrentDataObject()).getNames());
                cb.setModel(cbModel);
                cb.setSelectedIndex(getEnumerationSelectionIndex(p));
                setBackgroundColour(p, cb, cb.getBackground()); //set the inital state to match the model

                cb.addActionListener(new EnumerationComboBoxActionListener(p,cb));
                p.getCurrentDataObject().addPropertyChangeListener(EnumerationData.LASTSELECTION, new EnumerationDataListener(cb));
            } else
                System.out.println("ConnectEnumerationComboBox Error: Parameter " + paramName + " is type " + p.getCurrentType());
        }
    }

    /**
     *
     * @param iface
     * @param paramName     name of the boolean parameter in the DOME interface
     * @param cb            the checkbox in the custom GUI that the parameter's value will be connected to
     */
    public static void connectBooleanCheckBox(ModelInterfaceBase iface, String paramName, JCheckBox cb) {
        Parameter p = getParameterByName(iface, paramName);
        if (p != null) {
            if (p.getCurrentType().equals(DomeBoolean.TYPE_INFO.getTypeName())) {
                //p.addPropertyChangeListener(new ParameterStatusChangeListener(p, cb));
                //todo need to thing of reasonable way to add status information to a combination box
                cb.setSelected(getBooleanValue(p));
                cb.addActionListener(new BooleanCheckBoxActionListener(p, cb));
                p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new BooleanDataListener(cb));
            } else
                System.out.println("ConnectBooleanCheckBox Error: Parameter " + paramName + " is type " + p.getCurrentType());
        }
    }

    /**
     *
     * @param iface
     * @param paramName    name of the matrix parameter in the DOME interface
     * @param row          row of the value to be coneected to the custom GUI
     * @param col          column of the value to be connected to the customGUI
     * @param tf           the text field in the custom GUI that the parameters will be connected to
     */
    public static void connectMatrixElementTextField(ModelInterfaceBase iface, String paramName, int row, int col, JTextField tf) {
        Parameter p = getParameterByName(iface, paramName);
        if (p != null) {
            if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
                if (((DomeMatrixData) p.getCurrentDataObject()).getColumnCount() < col &&
                    ((DomeMatrixData) p.getCurrentDataObject()).getRowCount() < row) {
                    p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
                    tf.setText((getMatrixElementValue(p, row, col)));
                    setBackgroundColour(p, tf, tf.getBackground()); //set the inital state to match the model
                    tf.addActionListener(new MatrixElementTextFieldActionListener(p, row, col, tf));
                    p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new MatrixElementDataListener(p, row, col, tf));
                }
                else {
                    System.out.println("ConnectmatrixElementTextField Error, out of bounds element: Parameter " + paramName + " is "
                            + ((DomeMatrixData) p.getCurrentDataObject()).getRowCount() + "x"
                            + ((DomeMatrixData) p.getCurrentDataObject()).getColumnCount()
                            + ", trying to connect to element " + row + ", " + col);
                }
            } else
                System.out.println("ConnectmatrixElementTextField Error: Parameter " + paramName + " is type " + p.getCurrentType());
        }
    }

    public static void connectTwoVectorsEditableChart(ModelInterfaceBase iface,
                                                      String xVecParamName, String yVecParamName,
                                                      String xBoundParamName, EditableChart editChart,
                                                      boolean isInput) {
        Parameter xVecParam = getParameterByName(iface, xVecParamName);
        Parameter yVecParam = getParameterByName(iface, yVecParamName);
        Parameter xBoundParam = getParameterByName(iface, xBoundParamName);
        DomeVectorData xVec = (DomeVectorData) xVecParam.getCurrentDataObject();
        DomeVectorData yVec = (DomeVectorData) yVecParam.getCurrentDataObject();
        IntegerData xBound = (IntegerData) xBoundParam.getCurrentDataObject();

        // labels
        editChart.setXAxisLabel(UnitAtom.getUnitDescription(xVec.getUnit().toString()));
        editChart.setYAxisLabel(UnitAtom.getUnitDescription(yVec.getUnit().toString()));
        xVec.addPropertyChangeListener(NumericQuantity.UNIT, new ChartXAxisUnitChangeListener(xVecParam, editChart));
        yVec.addPropertyChangeListener(NumericQuantity.UNIT, new ChartYAxisUnitChangeListener(yVecParam, editChart));

        // bounds
        editChart.setMaxXChartBound(xBound.getValue());
        xBound.addPropertyChangeListener(DataObject.VALUE, new ChartXAxisBoundChangeListener(editChart, xVec, yVec, isInput));

        if (isInput) {
            xVec.setSize(xBound.getValue() + 1);
            yVec.setSize(xBound.getValue() + 1);

        // vectors
            editChart.loadDataset(xVec, yVec);
        } else {
            VectorDataListener vectorListenner = new VectorDataListener(xVecParam, yVecParam, editChart);
            xVec.addPropertyChangeListener(DataObject.VALUE, vectorListenner);
            yVec.addPropertyChangeListener(DataObject.VALUE, vectorListenner);
        }

        // status
        JFreeChart chart = ((ChartPanel) editChart).getChart();
        yVecParam.addPropertyChangeListener(new ChartStatusChangeListener(yVecParam, chart));
        setChartBackgroundColour(yVecParam, chart, (Color) (chart.getBackgroundPaint()));
    }

    /**
     *
     * @param iface
     * @param paramName    the matrix in the DOME interface that you want to plot. Currently assumes the matrix
     *                     data are in either one or two rows and will be plotted in a single series.
     * @param chart        the JFreeChart XYFreeChart in your custom GUI
     */
    public static void connectMatrixElementXYFreeChart(ModelInterfaceBase iface, String paramName, JFreeChart chart) {
    //todo chart currently only reflects changes in matrix, does not support changes in chart. See replotMatrixData for simple assumptions
        if (chart.getPlot() instanceof XYPlot) {
            Parameter p = getParameterByName(iface, paramName);
            if (p != null) {
              if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
                 //set chart to match the Matrix data
                 replotMatrixData(p, chart);
                 p.addPropertyChangeListener(new ChartStatusChangeListener(p, chart));
                 setChartBackgroundColour(p, chart, (Color)(chart.getBackgroundPaint()));
                 p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new MatrixDataListener(p, chart));
            } else
                System.out.println("ConnectMatrixJFreeChart Error: Parameter " + paramName + " is type " + p.getCurrentType());
            }
        }  else
            System.out.println("ConnectMatrixJFreeChart Error: chart is not XY plot");
    }

    /**
     * This method is used to connect a custom GUI check box to the 'show' option of a file object.
     * @param iface
     * @param paramName
     * @param check
     */
    public static void connectFileOpenCheckBox(ModelInterfaceBase iface, String paramName, JCheckBox check) {
        Parameter p = getParameterByName(iface, paramName);
        if (p != null) {
            if (p.getCurrentType().equals(DomeFile.TYPE_INFO.getTypeName())) {
                check.addActionListener(new OpenFileActionListener(p, check));
            } else
                System.out.println("ConnectFileOpenButton Error: Parameter " + paramName + " is type " + p.getCurrentType());
        }
    }

    public static class OpenFileActionListener implements ActionListener {
        Parameter p;
        FileChangePropertyListener l;
        JCheckBox c;

        public OpenFileActionListener(Parameter p, JCheckBox c) {
            this.p = p;
            this.l = new FileChangePropertyListener();
            this.c = c;
        }

        public void actionPerformed(ActionEvent e) {
            FileData f = (FileData) p.getCurrentDataObject();
            if (c.isSelected())  {
                FileUtils.showFile(f);
                f.addPropertyChangeListener(FileData.VALUE, l);
            }
            else
                f.removePropertyChangeListener(FileData.VALUE, l);
        }

        class FileChangePropertyListener implements PropertyChangeListener  {
            public void propertyChange(PropertyChangeEvent evt) {
                FileUtils.showFile((FileData) p.getCurrentDataObject());
            }
        }
    }


    /* General methods used by all connect methods*/
	public static Parameter getParameterByName(ModelInterfaceBase iface, String paramName)
	{
		Iterator it = iface.getModelObjectParameters().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				if (((Parameter) o).getName().equals(paramName))
					return (Parameter) o;
			}
		}
		System.out.println("unable to find parameter " + paramName + " in interface " + iface.getName());
        //throw new RuntimeException("unable to find parameter " + paramName + " in interface "+iface.getName());
        return null;
	}


    protected static class ChartStatusChangeListener implements PropertyChangeListener {
        Parameter p;
        JFreeChart chart;
        Color c;

        public ChartStatusChangeListener(Parameter p, JFreeChart chart) {
            this.p = p;
            this.chart = chart;
            this.c = (Color)(chart.getBackgroundPaint());
        }

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(Parameter.VALUE_STATUS)) {
                setChartBackgroundColour(p, chart, c);
            }
        }
    }

    private static void setChartBackgroundColour(Parameter p, JFreeChart chart, Color originalBackground) {
        String valueStatus = p.getValueStatus();
        if (Parameter.VALUE_STATUS_STALE.equals(valueStatus))
            chart.setBackgroundPaint(Templates.STALE_COLOR);
        else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(valueStatus))
            chart.setBackgroundPaint(Templates.INCONSISTENT_COLOR);
        else if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(valueStatus))
            chart.setBackgroundPaint(Templates.WAITING_VALIDATION_COLOR);
        else if (Parameter.VALUE_STATUS_CONSISTENT.equals(valueStatus)) {
            chart.setBackgroundPaint(originalBackground);
        }
    }

    protected static class ParameterStatusChangeListener implements PropertyChangeListener {
        Parameter p;
        JComponent comp;
        Color c;

        public ParameterStatusChangeListener(Parameter p, JComponent comp) {
            this.p = p;
            this.comp = comp;
            this.c = comp.getBackground();
        }

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(Parameter.VALUE_STATUS)) {
                setBackgroundColour(p, comp, c);
            }
        }
    }

    private static void setBackgroundColour(Parameter p, JComponent comp, Color originalNotEditableBackground){
        String valueStatus = p.getValueStatus();
        if (Parameter.VALUE_STATUS_STALE.equals(valueStatus))
            comp.setBackground(Templates.STALE_COLOR);
        else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(valueStatus))
            comp.setBackground(Templates.INCONSISTENT_COLOR);
        else if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(valueStatus))
            comp.setBackground(Templates.WAITING_VALIDATION_COLOR);
        else if (Parameter.VALUE_STATUS_CONSISTENT.equals(valueStatus)) {
            if ((comp instanceof JTextField) && !((JTextField) comp).isEditable())
                comp.setBackground(originalNotEditableBackground);
            else
                comp.setBackground(Templates.CONSISTENT_COLOR);
        }
    }

    /********** Methods used for connecting real values to text fields*/
    /**
     * When textfield is clicked, value is sent to parameter.
     */
    protected static class RealTextFieldActionListener implements ActionListener {
        JTextField txtField;
        Parameter p;

        public RealTextFieldActionListener(Parameter p, JTextField txtField) {
            this.p = p;
            this.txtField = txtField;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                double newValue = Double.parseDouble(txtField.getText());
                setRealValue(p, newValue);
            } catch (NumberFormatException ex) {
                System.err.println("invalid real value: " + txtField.getText());
                txtField.setText(getRealValue(p));
            }
        }
    }

    /**

    /**
     * When value changes in parameter, value is set in textfield.
     */
    protected static class RealIntStringDataListener implements PropertyChangeListener {
        JTextField txtField;

        public RealIntStringDataListener(JTextField txtField) {
            this.txtField = txtField;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            txtField.setText(evt.getNewValue().toString());
        }
    }

    /**
     * Gets value of a parameter which contains a real data object.
     * @param p
     * @return
     */
    protected static String getRealValue(Parameter p) {
        return Double.toString(((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal) p.getCurrentDataObject()).getValue());
    }

    protected static void setRealValue(Parameter p, double value) {
        ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal) p.getCurrentDataObject()).setValue(value);
    }

    //********** Methods for connecting integers to textfield

    protected static class IntTextFieldActionListener implements ActionListener {
        JTextField txtField;
        Parameter p;

        public IntTextFieldActionListener(Parameter p, JTextField txtField) {
            this.p = p;
            this.txtField = txtField;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                int newValue = Integer.parseInt(txtField.getText());
                setIntValue(p, newValue);
            } catch (NumberFormatException ex) {
                System.err.println("invalid integer value: " + txtField.getText());
                txtField.setText(getIntValue(p));
            }
        }
    }

    protected static String getIntValue(Parameter p) {
        return Integer.toString(((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger) p.getCurrentDataObject()).getValue());
    }

    protected static void setIntValue(Parameter p, int value) {
        ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger) p.getCurrentDataObject()).setValue(value);
    }

    //**********  Methods for connecting strings to textfields

    protected static class StringTextFieldActionListener implements ActionListener {
        JTextField txtField;
        Parameter p;

        public StringTextFieldActionListener(Parameter p, JTextField txtField) {
            this.p = p;
            this.txtField = txtField;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                setStringValue(p, txtField.getText());
            } catch (NumberFormatException ex) {
                System.err.println("invalid string value: " + txtField.getText());
                txtField.setText(getStringValue(p));
            }
        }
    }

    protected static String getStringValue(Parameter p) {
        return ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString) p.getCurrentDataObject()).getValue();
    }

    protected static void setStringValue(Parameter p, String value) {
        ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString) p.getCurrentDataObject()).setValue(value);
    }

    /*********** Methods for connecting boolean to checkbox*/
    /**
     * Gets value of a parameter which contains a boolean data object.
     * @param p
     * @return
     */
    protected static boolean getBooleanValue(Parameter p) {
         if (((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean) p.getCurrentDataObject()).getBooleanValue().booleanValue())
             return true;
        else
             return false;
    }

    protected static void setBooleanValue(Parameter p, boolean value) {
        ((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean) p.getCurrentDataObject()).setValue(value);
    }

    protected static class BooleanCheckBoxActionListener implements ActionListener {
        JCheckBox cb;
        Parameter p;

        public BooleanCheckBoxActionListener(Parameter p, JCheckBox cb) {
            this.p = p;
            this.cb = cb;
        }

        public void actionPerformed(ActionEvent e) {
                setBooleanValue(p, cb.isSelected());
        }
    }

    /**
     * When value changes in parameter, value is set in checkbox.
     */
    protected static class BooleanDataListener implements PropertyChangeListener {
        JCheckBox cb;

        public BooleanDataListener(JCheckBox cb) {
            this.cb = cb;
        }

        public void propertyChange(PropertyChangeEvent evt) {
                cb.setSelected(((Boolean)(evt.getNewValue())).booleanValue());
        }
    }

    /*********** Methods for connecting enumeration to comboBox */

    protected static int getEnumerationSelectionIndex(Parameter p) {
        return ((mit.cadlab.dome3.objectmodel.dataobject.EnumerationData) p.getCurrentDataObject()).getLastSelection();
    }

    protected static void setEnumerationSelectionByIndex(Parameter p, int index) {
        ((mit.cadlab.dome3.objectmodel.dataobject.EnumerationData) p.getCurrentDataObject()).setLastSelection(index);
    }

    protected static class EnumerationComboBoxActionListener implements ActionListener {
        JComboBox cb;
        Parameter p;

        public EnumerationComboBoxActionListener(Parameter p, JComboBox cb) {
            this.p = p;
            this.cb = cb;
        }

        public void actionPerformed(ActionEvent e) {
            setEnumerationSelectionByIndex(p, cb.getSelectedIndex());
        }
    }

    /**
     * When value changes in parameter, selection is set in checkbox.
     */
    protected static class EnumerationDataListener implements PropertyChangeListener {
        JComboBox cb;

        public EnumerationDataListener(JComboBox cb) {
            this.cb = cb;
        }

        public void propertyChange(PropertyChangeEvent evt) {
                cb.setSelectedIndex(((Integer)(evt.getNewValue())).intValue());
        }
    }

    /********** Methods for connecting DOME matrix to JFreechart XY chart*/

    private static void replotMatrixData(Parameter p, JFreeChart chart){
        //todo generalize this so that it can be configured for multiple series in rows or columns.
        // currently limited: assumes that there will be only one series ploted,  and data are in rows

        Number[][] values = ((DomeMatrixData) p.getCurrentDataObject()).getNumberArrayData();

        XYSeries series = new XYSeries("");

        if (values.length != 0) {
            if (values.length == 1) { // case for a one row matrix, so use index as x axis
                for (int i = 0; i < values[0].length; i++)
                    series.add(i, values[0][i]);
            }
            else if (values.length == 2) { // case for a two row matrix, so use first row as x axis
                for (int i = 0; i < values[0].length; i++)
                    series.add(values[0][i], values[1][i]);
            }
            else
                System.out.println("replotMatrixData: matrix " + p.getName() + "dimensions are not supported in CustomGui");
        }
        XYSeriesCollection collection = new XYSeriesCollection(series);
        chart.getXYPlot().setDataset(collection);
    }

    //todo this method is here as example of how to unpdate as single point in the chart
    private void updateXYDataset(Point point, XYSeriesCollection s) {
        s.getSeries(0).update((int) point.getX(), new Double(point.getY()));
    }

    protected static class MatrixDataListener implements PropertyChangeListener {
        JFreeChart chart;
        Parameter p;

        public MatrixDataListener(Parameter p, JFreeChart chart) {
            this.chart = chart;
            this.p = p;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            replotMatrixData(p,chart);
        }
    }

    // Methods used for connecting matrix element to text field
    protected static String getMatrixElementValue(Parameter p, int r, int c) {
        try {
            return (((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix)
                    p.getCurrentDataObject()).getItem(r, c).toString());
        }
        catch (Exception e) {
            System.out.println(e); //in case the matrix changes size dynamically at run time and element does not exist
            return "";
        }
    }

    protected static class MatrixElementTextFieldActionListener implements ActionListener {
        JTextField txtField;
        Parameter p;
        int r, c;

        public MatrixElementTextFieldActionListener(Parameter p, int r, int c, JTextField txtField) {
            this.p = p;
            this.r = r;
            this.c = c;
            this.txtField = txtField;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                double newValue = Double.parseDouble(txtField.getText());
                ((mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData)
                        p.getCurrentDataObject()).setItem(r,c, new Double(newValue));
            } catch (NumberFormatException ex) {
                System.err.println("invalid matrix element value in text field: " + txtField.getText());
                txtField.setText(getMatrixElementValue(p, r, c));
            }
        }
    }

    protected static class MatrixElementDataListener implements PropertyChangeListener {
        JTextField txtField;
        int r, c;
        Parameter p;

        public MatrixElementDataListener(Parameter p, int r, int c, JTextField txtField) {
            this.txtField = txtField;
            this.r = r;
            this.c = c;
            this.p = p;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            txtField.setText(getMatrixElementValue(p, r, c));
        }
    }

    //methods for editable chart
    protected static class ChartXAxisUnitChangeListener implements PropertyChangeListener {
        Parameter p;
        EditableChart chart;

        public ChartXAxisUnitChangeListener(Parameter p, EditableChart chart) {
            this.p = p;
            this.chart = chart;
        }

        public void propertyChange(PropertyChangeEvent e) {
            chart.setXAxisLabel(UnitAtom.getUnitDescription(p.getCurrentDataObject().getUnit().toString()));
        }
    }

    protected static class ChartYAxisUnitChangeListener implements PropertyChangeListener {
        Parameter p;
        EditableChart chart;

        public ChartYAxisUnitChangeListener(Parameter p, EditableChart chart) {
            this.p = p;
            this.chart = chart;
        }

        public void propertyChange(PropertyChangeEvent e) {
            chart.setYAxisLabel(UnitAtom.getUnitDescription(p.getCurrentDataObject().getUnit().toString()));
        }
    }

    protected static class ChartXAxisBoundChangeListener implements PropertyChangeListener {
        EditableChart chart;
        DomeVectorData xVec;
        DomeVectorData yVec;
        boolean isInput;

        public ChartXAxisBoundChangeListener(EditableChart chart, DomeVectorData xVec, DomeVectorData yVec, boolean isInput) {
            this.chart = chart;
            this.xVec = xVec;
            this.yVec = yVec;
            this.isInput = isInput;
        }

        public void propertyChange(PropertyChangeEvent e) {
            int bound = ((Integer) e.getNewValue()).intValue();
            int oldSize = xVec.getSize();
            if (isInput) {
                xVec.setSize(bound + 1);
                yVec.setSize(bound + 1);
                for (int i = oldSize; i <= bound; i++)
                    xVec.setItem(i, new Double(i));
            }
            chart.updateXChartBound(bound);
        }
    }

    protected static class VectorDataListener implements PropertyChangeListener {
        EditableChart chart;
        Parameter xVec, yVec;

        public VectorDataListener(Parameter xVec, Parameter yVec, EditableChart chart) {
            this.chart = chart;
            this.xVec = xVec;
            this.yVec = yVec;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            DomeVectorData x = (DomeVectorData) xVec.getCurrentDataObject();
            DomeVectorData y = (DomeVectorData) yVec.getCurrentDataObject();
            if (x.getSize() <= y.getSize()) // when both axis
                chart.plotOutput(x, y);
        }
    }
}
