/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Mar 21, 2003
 * Time: 3:20:07 AM
 * To change this template use Options | File Templates.
 */
package test.gui;

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

public class CompleteInterfaceCustomGui extends JPanel {
	public static final Dimension DEFAULT_SIZE = new Dimension(300, 450);
	public static final GridBagConstraints gbc = null;

	private JTextField fiberYoungValue;
	private JTextField fiberPoissonValue;
	private JTextField fiberVolumeValue;
	private JTextField matrixYoungValue;
	private JTextField matrixPoissonValue;
	private JTextField modulusOneValue;
	private JTextField modulusTwoValue;
	private JTextField shearModulusValue;
	private JTextField poissonOneTwoValue;

	protected ModelInterfaceBase iface; // using the base class should allow gui to be used in both build and run

	//only for testing Gui layout from main function
	public CompleteInterfaceCustomGui()
	{
       layoutComponents();
	}

	//used for attaching custom Gui to an interface
	public CompleteInterfaceCustomGui(ModelInterfaceBase base)
	{
	   this.iface = base;
       layoutComponents();
	}

	private void layoutComponents() {
		JPanel p = makePanel();

		JComponent[] comps = {p};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel windowTitle = Templates.makeLabel("Rule of Mixtures Model", Templates.FONT12B);
		ImageIcon graphic = Templates.makeImageIcon("mit/cadlab/dome3/icons/carbonfiber.gif");
		JLabel graphicLabel = new JLabel(graphic, SwingConstants.LEFT);
		graphicLabel.setOpaque(false);

		JLabel fiberTitle = Templates.makeLabel("Fibre inputs", Templates.FONT11B);

		Parameter fiberYoungParam = getParameterByName("Ef");
		JLabel fiberYoung = Templates.makeLabel("Young's modulus:");
		fiberYoungValue = makeParameterTextField(fiberYoungParam, true);

		Parameter fiberPoissonParam = getParameterByName("Nuf");
		JLabel fiberPoisson = Templates.makeLabel("Poisson ratio:");
		fiberPoissonValue = makeParameterTextField(fiberPoissonParam, true);

		Parameter fiberVolumeParam = getParameterByName("vf");
		JLabel fiberVolume = Templates.makeLabel("volume fraction:");
		fiberVolumeValue = makeParameterTextField(fiberVolumeParam, true);

		JLabel matrixTitle = Templates.makeLabel("Matrix inputs", Templates.FONT11B);

		Parameter matrixYoungParam = getParameterByName("Em");
		JLabel matrixYoung = Templates.makeLabel("Young's modulus:");
		matrixYoungValue = makeParameterTextField(matrixYoungParam, true);

		Parameter matrixPoissonParam = getParameterByName("Num");
		JLabel matrixPoisson = Templates.makeLabel("Poisson ratio:");
		matrixPoissonValue = makeParameterTextField(matrixPoissonParam, true);

		JLabel laminaTitle = Templates.makeLabel("Lamina properties", Templates.FONT11B);

		Parameter modulusOneParam = getParameterByName("E1");
		JLabel modulusOne = Templates.makeLabel("modulus 1:");
		modulusOneValue = makeParameterTextField(modulusOneParam, false);
		modulusOneValue.setEditable(false);

		Parameter modulusTwoParam = getParameterByName("E2");
		JLabel modulusTwo = Templates.makeLabel("modulus 2:");
		modulusTwoValue = makeParameterTextField(modulusTwoParam, false);
		modulusTwoValue.setEditable(false);

		Parameter shearModulusParam = getParameterByName("G12");
		JLabel shearModulus = Templates.makeLabel("shear modulus 12:");
		shearModulusValue = makeParameterTextField(shearModulusParam, false);
		shearModulusValue.setEditable(false);

		Parameter poissonOneTwoParam = getParameterByName("nu12");
		JLabel poissonOneTwo = Templates.makeLabel("Poisson ratio 12:");
		poissonOneTwoValue = makeParameterTextField(poissonOneTwoParam, false);
		poissonOneTwoValue.setEditable(false);

		JComponent[] comps = {windowTitle, graphicLabel,
							  fiberTitle, fiberYoung, fiberYoungValue, fiberPoisson, fiberPoissonValue, fiberVolume, fiberVolumeValue,
							  matrixTitle, matrixYoung, matrixYoungValue, matrixPoisson, matrixPoissonValue,
							  laminaTitle, modulusOne, modulusOneValue, modulusTwo, modulusTwoValue, shearModulus, shearModulusValue, poissonOneTwo, poissonOneTwoValue,
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),

			new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

			new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

			new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 9, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 10, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 11, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 12, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
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



	public static void main(String[] args)
	{
		JFrame f = new JFrame("Custom rule of Mixtures GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new CompleteInterfaceCustomGui());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
