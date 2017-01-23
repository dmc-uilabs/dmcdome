// CustomSimpleMathModelGui.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.gui;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Custom GUI for Math Model interface
 */
public class CustomSimpleMathModelGui extends JPanel
{

	protected ModelInterfaceBase iface; // using the base class should allow gui to be used in both build and run

	public CustomSimpleMathModelGui(ModelInterfaceBase interfaceObj)
	{
		iface = interfaceObj;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(new JLabel("inputs:"));
		add(makeParameterPanel("width",true));
		add(makeParameterPanel("height", true));
		add(new JLabel("outputs:"));
		add(makeParameterPanel("area", false));
		add(makeParameterPanel("volume", false));
		JButton submitButton = new JButton("submit changes");
		submitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (iface instanceof ModelInterfaceRuntimeClient) {
					try {
						((ModelInterfaceRuntimeClient)iface).submitChanges();
					}
					catch (Exception e1) {
						System.err.println("error running interface. perhaps you aren't connected to server?");
					}
				}
			}
		});
		add(submitButton);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	}

	/**
	 * Creates a panel with name of variable and textfield.
	 * @param paramName
	 * @param isInput
	 * @return
	 */
	protected JPanel makeParameterPanel(String paramName, boolean isInput) {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1,2));
		Parameter param = getParameterByName(paramName);
		if (param != null) {
			p.add(new JLabel(paramName));
			p.add(makeTextField(param,isInput));
		}
		return p;
	}

	// todo: verify that getModelObjectParameters actually gets all parameters in interface
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
		System.err.println("unable to find parameter "+paramName);
		return null;
	}

	/**
	 * Makes textfield for parameter and registers listeners between parameter and textfield.
	 * @param p
	 * @param isEditable
	 * @return
	 */
	protected JTextField makeTextField(Parameter p, boolean isEditable)
	{
		JTextField tf = new JTextField(10);
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

	/**
	 * test2 method loads up the interface from the filesystem for testing GUI
	 * @param args
	 */
	public static void main(String[] args)
	{
		String interfaceFileName = getInterfaceFile();
		if (interfaceFileName==null)
			System.exit(0);
		try {
			DomeInit.initializeDOME(); // needed for dome object support
			ModelInterfaceRuntimeClient ifaceClient = new ModelInterfaceRuntimeClient(XMLUtils.stringToXmlElement(FileUtils.readTextFileAsString(interfaceFileName)), false);
			JFrame f = new JFrame("Math Model");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.getContentPane().add(new CustomSimpleMathModelGui(ifaceClient));
			f.pack();
			f.show();
		}
		catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(0);
		}
	}

	public static String getInterfaceFile()
	{
		DomeFileChooser chooser = new DomeFileChooser();
		return chooser.showOpenDialog(null,DomeFileChooser.DOME_INTERFACE_FILTER);
	}
}
