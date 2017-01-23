// AddDialog.java
//package mit.cadlab.dome3.gui.components;

package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AddDialogBasePanel extends JPanel
{

	GridBagConstraints gbc;

	// define components here
	protected DTextField sizeTextField;
	protected JTextField valueTextField;
	protected JRadioButton rowRadioButton;
	protected JRadioButton columnRadioButton;
	protected JRadioButton startRadioButton;
	protected JRadioButton endRadioButton;
	protected JRadioButton beforeRadioButton;

	protected JButton OkButton;
	protected JButton cancelButton;

	public AddDialogBasePanel()
	{
		layoutComponents(createComponents(null));
		setPreferredSize(new Dimension(200, 200));

	}

	public AddDialogBasePanel(Class initialDataClass)
	{
		layoutComponents(createComponents(initialDataClass));
		setPreferredSize(new Dimension(200, 200));

	}

	private JComponent[] createComponents(Class initialDataClass)
	{
		Font font11 = new Font("Dialog", Font.PLAIN, 11);
		Font bold = new Font("Dialog", Font.BOLD, 11);

		OkButton = Templates.makeButton("OK", font11);
		cancelButton = Templates.makeButton("cancel", font11);
		JLabel addLabel = Templates.makeLabel("add", bold);
		JLabel valueLabel = Templates.makeLabel("initial value:");

		sizeTextField = Templates.makeDTextField("1");
		if (initialDataClass == null)
			valueTextField = Templates.makeDTextField("", 5);
		else {
			if (initialDataClass == Double.class)
				valueTextField = Templates.makeDRealField();
			else
				valueTextField = Templates.makeDIntegerField();
		}


		rowRadioButton = Templates.makeRadioButton("row(s)");
		columnRadioButton = Templates.makeRadioButton("column(s)");
		// Group the radio buttons.
		ButtonGroup group1 = new ButtonGroup();
		group1.add(rowRadioButton);
		group1.add(columnRadioButton);

		startRadioButton = Templates.makeRadioButton("at start");
		endRadioButton = Templates.makeRadioButton("at end", true);
		beforeRadioButton = Templates.makeRadioButton("before selection");
		// Group the radio buttons.
		ButtonGroup group2 = new ButtonGroup();
		group2.add(startRadioButton);
		group2.add(endRadioButton);
		group2.add(beforeRadioButton);


		JPanel rowColButtonGroupPanel = new JPanel();
		GridLayout grid = new GridLayout(2, 0, 0, 0);
		rowColButtonGroupPanel.setLayout(grid);
		rowColButtonGroupPanel.add(rowRadioButton);
		rowColButtonGroupPanel.add(columnRadioButton);

		JPanel addPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		addPanel.setLayout(gridbag);
		addPanel.add(addLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0));
		addPanel.add(sizeTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 20, 0));
		addPanel.add(rowColButtonGroupPanel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0));


		JPanel OkPanel = new JPanel();
		gridbag = new GridBagLayout();
		OkPanel.setLayout(gridbag);
		OkPanel.add(OkButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0));
		OkPanel.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0));

		JPanel locationButtonGroupPanel = new JPanel();
		gridbag = new GridBagLayout();
		locationButtonGroupPanel.setLayout(gridbag);
		locationButtonGroupPanel.add(startRadioButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		locationButtonGroupPanel.add(endRadioButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		locationButtonGroupPanel.add(beforeRadioButton, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


		JPanel valuePanel = new JPanel();
		gridbag = new GridBagLayout();
		valuePanel.setLayout(gridbag);
		valuePanel.add(valueLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0));
		valuePanel.add(valueTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


		return new JComponent[]{addPanel, locationButtonGroupPanel, valuePanel, OkPanel};
	}


	protected void layoutComponents(JComponent[] comps)
	{
		// do layout
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
		};


		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		for (int i = 0; i < gbcs.length; ++i) {
			gridbag.setConstraints(comps[i], gbcs[i]);
			this.add(comps[i]);
		}


	}


	public void setRowColumnMode(boolean isRow, boolean isColumn)
	{
		rowRadioButton.setSelected(isRow);
		rowRadioButton.setEnabled(isRow);
		columnRadioButton.setSelected(isColumn);
		columnRadioButton.setEnabled(isColumn);
	}


	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("Add Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		f.getContentPane().add(new AddDialogBasePanel(), BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
	}

	protected int min(int[] indices)
	{
		Arrays.sort(indices);
		if (indices.length > 0)
			return indices[0];
		else //not selected
			return -1;
	}

	protected void dispose()
	{

		SwingUtilities.windowForComponent(this).dispose();
	}

	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("AddDialogBasePanel: " + msg);
	}

}
