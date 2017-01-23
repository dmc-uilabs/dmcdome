/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Apr 27, 2003
 * Time: 1:31:11 PM
 * To change this template use Options | File Templates.
 */
package test.gui;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.data.DefaultXYDataset;
import com.jrefinery.data.XYDataset;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

public class MechanicalGlassDropCustomGui extends JPanel {
	public static final Dimension DEFAULT_SIZE = new Dimension(300, 450);
	public static final GridBagConstraints gbc = null;

	private JFreeChartPanel pane;
	private JFreeChart chart;
	private JTextField bPillarValue;
	private JLabel bPillarUnit;
	private JTextField glassRadiusValue;
	private JLabel glassRadiusUnit;
	private JTextField glassThicknessValue;
	private JLabel glassThicknessUnit;
	private JTextField headerLengthValue;
	private JLabel headerLengthUnit;
	private JTextField dragValue;
	private JLabel dragUnit;
	private JTextField velocityValue;
	private JLabel velocityUnit;
	private JTextField stallValue;
	private JLabel stallUnit;

	protected ModelInterfaceBase iface; // using the base class should allow gui to be used in both build and run

	//used for attaching custom Gui to an interface
	public MechanicalGlassDropCustomGui(ModelInterfaceBase base)
	{
		this.iface = base;
		add(makePanel());
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		JLabel inputTitle1 = Templates.makeLabel("GEOMETRIC PROPERTIES", Templates.FONT11B);

		Parameter bPillar = getParameterByName("BPillarHeight");
		JLabel bPillarLabel = Templates.makeLabel("B Pillar:");
		bPillarValue = makeParameterTextField(bPillar, true);
		bPillarUnit = Templates.makeLabel(bPillar.getCurrentDataObject().getUnit().toString());

		Parameter glassRadius = getParameterByName("GlassRadius");
		JLabel glassRadiusLabel = Templates.makeLabel("Glass Radius:");
		glassRadiusValue = makeParameterTextField(glassRadius, true);
		glassRadiusUnit = Templates.makeLabel(glassRadius.getCurrentDataObject().getUnit().toString());

		Parameter glassThickness = getParameterByName("GlassThickness");
		JLabel glassThicknessLabel = Templates.makeLabel("Glass Thickness:");
		glassThicknessValue = makeParameterTextField(glassThickness, true);
		glassThicknessUnit = Templates.makeLabel(glassThickness.getCurrentDataObject().getUnit().toString());

		Parameter headerLength = getParameterByName("HeaderLength");
		JLabel headerLengthLabel = Templates.makeLabel("Header Length:");
		headerLengthValue = makeParameterTextField(headerLength, true);
		headerLengthUnit = Templates.makeLabel(headerLength.getCurrentDataObject().getUnit().toString());

		JLabel inputTitle2 = Templates.makeLabel("SEAL PROPERTIES", Templates.FONT11B);

		Parameter drag = getParameterByName("SealDragB");
		JLabel dragLabel = Templates.makeLabel("Drag:");
		dragValue = makeParameterTextField(drag, true);
		dragUnit = Templates.makeLabel(drag.getCurrentDataObject().getUnit().toString());

		JLabel outputTitle = Templates.makeLabel("PERFORMANCE", Templates.FONT11B);

		Parameter velocity = getParameterByName("MaxVel");
		JLabel velocityLabel = Templates.makeLabel("Max. Velocity:");
		velocityValue = makeParameterTextField(velocity, false);
		velocityUnit = Templates.makeLabel(velocity.getCurrentDataObject().getUnit().toString());
		velocityValue.setEditable(false);

		Parameter stall = getParameterByName("MaxStall");
		JLabel stallLabel = Templates.makeLabel("Max. Stall:");
		stallValue = makeParameterTextField(stall, false);
		stallUnit = Templates.makeLabel(stall.getCurrentDataObject().getUnit().toString());
		stallValue.setEditable(false);

		final Parameter velocityMatrix = getParameterByName("VelocityMatrix");
		mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix vmatrix =(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix)velocityMatrix.getCurrentDataObject();
		chart = createChart(vmatrix);
		pane = new JFreeChartPanel(chart);
		vmatrix.addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent de) {
						System.out.println("in propertyChange method");
						if(de.getPropertyName().equals(DataObject.VALUE)) {
							System.out.println("inside property change processing");
							mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix mat = (mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix)velocityMatrix.getCurrentDataObject();
							JFreeChart newChart = createChart(mat);
							chart = newChart;
							pane.setChart(newChart);
							pane.repaint();
						}
					}
				});

		JComponent[] comps = {
			pane,
			inputTitle1,
			bPillarLabel, bPillarValue, bPillarUnit,
			glassRadiusLabel, glassRadiusValue, glassRadiusUnit,
			glassThicknessLabel, glassThicknessValue, glassThicknessUnit,
			headerLengthLabel, headerLengthValue, headerLengthUnit,
			inputTitle2,
			dragLabel, dragValue, dragUnit,
			outputTitle,
			velocityLabel, velocityValue, velocityUnit,
			stallLabel, stallValue, stallUnit, new JPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			//pane
			new GridBagConstraints(0, 0, 1, 11, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0),

			//inputTitle1
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),

			// bPillarLabel, bPillarValue, bPillarUnit
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//glassRadiusLabel, glassRadiusValue, glassRadiusUnit
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//glassThicknessLabel, glassThicknessValue, glassThicknessUnit
			new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),


			//headerLengthLabel, headerLengthValue, headerLengthUnit
			new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//inputTitle2
			new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15, 5, 0, 0), 0, 0),

			//dragLabel, dragValue, dragUnit
			new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//outputTitle
			new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15, 5, 0, 0), 0, 0),

			//velocityLabel, velocityValue, velocityUnit
			new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//stallLabel, stallValue, stallUnit
			new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 9, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			new GridBagConstraints(1, 10, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JFreeChart createChart(DataObject obj) {
		if(obj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix) {
			mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix vmatrix = (mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix)obj;
			Object[][][] items=new Object[1][vmatrix.getRowCount()][vmatrix.getColumnCount()];
			for(int i = 0; i < vmatrix.getRowCount(); i++){
				for(int j = 0; j<vmatrix.getColumnCount(); j++){
					items[0][i][j] = vmatrix.getItem(i,j);
				}
			}
			XYDataset dataset = new DefaultXYDataset(new String[]{"Window velocity"},items);
			String title = "";
			String xaxis = "Window postion (mm)";
			String yaxis = "Speed (cm/s)";
			JFreeChart chart = ChartFactory.createXYChart(title, xaxis, yaxis, dataset, true);
			return chart;
		}
		else return null;
	}

	/**
	 * @param paramName
	 * @return first variable found in interface with specified name
	 */
	protected Parameter getParameterByName(String paramName) {
		Iterator it = iface.getModelObjectParameters().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				if (((Parameter)o).getName().equals(paramName))
					return (Parameter)o;
			}
		}
		throw new RuntimeException("unable to find parameter "+paramName);

	}

	/**
	 * Makes textfield for parameter and registers listeners between parameter and textfield.
	 * @param p
	 * @param isEditable or in other words is it an input
	 * @return
	 */
	protected JTextField makeParameterTextField(Parameter p, boolean isEditable)
	{
		JTextField tf = new JTextField(10);
		p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
		tf.setText(getRealValue(p));
		tf.setEditable(isEditable);
		tf.addActionListener(new RealTextFieldActionListener(p, tf));
		p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new RealDataListener(tf));
		return tf;
	}

	protected class ParameterStatusChangeListener implements PropertyChangeListener {
		Parameter p;
		JComponent comp;
		public ParameterStatusChangeListener(Parameter p, JComponent comp) {
			this.p = p;
			this.comp = comp;
		}

		public void propertyChange(PropertyChangeEvent e) {
			if (e.getPropertyName().equals(Parameter.VALUE_STATUS)) {
				String valueStatus = p.getValueStatus();
				if (Parameter.VALUE_STATUS_STALE.equals(valueStatus))
					comp.setBackground(Templates.STALE_COLOR);
				else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(valueStatus))
					comp.setBackground(Templates.INCONSISTENT_COLOR);
				else if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(valueStatus))
					comp.setBackground(Templates.WAITING_VALIDATION_COLOR);
				else if (Parameter.VALUE_STATUS_CONSISTENT.equals(valueStatus))
					comp.setBackground(Templates.CONSISTENT_COLOR);
			}
		}
	}

	/**
	 * When textfield is clicked, value is sent to parameter.
	 */
	class RealTextFieldActionListener implements ActionListener {
		JTextField txtField;
		Parameter p;

		public RealTextFieldActionListener(Parameter p, JTextField txtField)
		{
			this.p = p;
			this.txtField = txtField;
		}

		public void actionPerformed(ActionEvent e)
		{
			try {
				double newValue = Double.parseDouble(txtField.getText());
				setRealValue(p, newValue);
			}
			catch (NumberFormatException ex) {
				System.err.println("invalid real value: " + txtField.getText());
			}
		}
	}

	/**
	 * When value changes in parameter, value is set in textfield.
	 */
	class RealDataListener implements PropertyChangeListener {
		JTextField txtField;

		public RealDataListener(JTextField txtField)
		{
			this.txtField = txtField;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(DataObject.VALUE)) {
				txtField.setText(evt.getNewValue().toString());
			}
		}
	}

	/**
	 * Sets value of a parameter which contains a real data object.
	 * @param p
	 * @param value
	 */
	protected void setRealValue(Parameter p, double value) {
		((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal)p.getCurrentDataObject()).setValue(value);
	}

	/**
	 * Gets value of a parameter which contains a real data object.
	 * @param p
	 * @return
	 */
	protected String getRealValue(Parameter p) {
		return Double.toString(((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal)p.getCurrentDataObject()).getValue());
	}


	private DomeMatrixData createDummyDomeMatrix(){

		DomeMatrixData fake=new DomeMatrixData(new Double(1.8),23,2);
		return fake;
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
		JFrame f = new JFrame("Mechanical Glass Drop Custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new MechanicalGlassDropCustomGui(null));
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}

