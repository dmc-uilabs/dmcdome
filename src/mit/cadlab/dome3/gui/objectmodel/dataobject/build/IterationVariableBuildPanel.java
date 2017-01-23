// IterationVariableBuildPanel.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.objectmodel.dataobject.RealIterationVariable;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DTextField;

import javax.swing.JComponent;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class IterationVariableBuildPanel extends RealBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("IterationVariableBuildPanel");
	public static final String XML_TAG = "iterationvariablebuildpanel";

	protected DTextField incrementField, limitField;

	public IterationVariableBuildPanel(RealIterationVariable real)
	{
		super(real);
	}

	protected RealIterationVariable getData() {
		return (RealIterationVariable)dataModel;
	}

	protected JComponent[] createComponents()
	{
		JComponent[] realComponents = super.createComponents();
		incrementField = Templates.makeDTextField(String.valueOf(getData().getIncrement()));
		limitField = Templates.makeDTextField(String.valueOf(getData().getLimit()));
		return new JComponent[]{realComponents[0], // valueTextField
		                        realComponents[1], // unitComboBox,
		                        Templates.makeLabel("increment:"),
		                        incrementField,
		                        Templates.makeLabel("limit:"),
		                        limitField,
		                        realComponents[2], //new JPanel(),
		                        realComponents[3], //constraintsButton,
		};
	}

	protected void layoutComponents(JComponent[] comps)
	{
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBagB(this, comps, gbcs);
	}

	protected void configureComponents()
	{
		super.configureComponents();
		incrementField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					getData().setIncrement(Double.parseDouble(incrementField.getText()));
					incrementField.setBackground(Color.WHITE);
				}
				catch (NumberFormatException ex) {
					System.err.println("invalid increment value");
				}
			}
		});
		limitField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					getData().setLimit(Double.parseDouble(limitField.getText()));
					limitField.setBackground(Color.WHITE);
				}
				catch (NumberFormatException ex) {
					System.err.println("invalid limit value");
				}
			}
		});
	}

}
