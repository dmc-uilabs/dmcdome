// DeletePreferenceDialog.java
//    ver 0.1
//
//
//

package mit.cadlab.dome3.gui.guiutils;

//import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeletePreferenceDialog extends JPanel implements ActionListener
{
	// define components here

	JRadioButton rowRadioButton;
	JRadioButton columnRadioButton;
	JButton OkButton;
	JButton cancelButton;
	JPanel contentPanel;

	Font font11 = new Font("Dialog", Font.PLAIN, 11);
	Font bold = new Font("Dialog", Font.BOLD, 11);

	int[] selectedRows;
	int[] selectedColumns;

	DomePreferenceData Model;
	ButtonGroup group1;

	public static void showDialog(Component parent, DomePreferenceData model, int[] selectedcolumns, int[] selectedrows)
	{

		DeletePreferenceDialog editor = new DeletePreferenceDialog(model, selectedrows, selectedcolumns);

		JDialog d = DialogFactory.createDialog(parent, "Delete", editor, true, false);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		d.show();


	}


	public DeletePreferenceDialog(DomePreferenceData Model, int[] selectedRows, int[] selectedColumns)
	{
		super();
		this.Model = Model;
		this.selectedRows = selectedRows;
		this.selectedColumns = selectedColumns;


		// create components
		OkButton = Templates.makeButton("ok", font11, this);
		cancelButton = Templates.makeButton("cancel", font11, this);

		rowRadioButton = Templates.makeRadioButton("delete selected rows", true);
		columnRadioButton = Templates.makeRadioButton("delete selected column", false);


		// Group the radio buttons.
		group1 = new ButtonGroup();
		group1.add(rowRadioButton);
		group1.add(columnRadioButton);

		contentPanel = new JPanel();


		JComponent[] comps = {rowRadioButton,
		                      columnRadioButton,
		                      OkButton,
		                      cancelButton,
		};

		// do layout
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 10, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 10, 5), 0, 0),
		};

		GridBagLayout gridbag = new GridBagLayout();
		contentPanel.setLayout(gridbag);
		for (int i = 0; i < gbcs.length; ++i) {
			gridbag.setConstraints(comps[i], gbcs[i]);
			contentPanel.add(comps[i]);
		}

		Dimension d = new Dimension(180, 80);
		this.setLayout(new BorderLayout());
		this.add(contentPanel);
		this.setPreferredSize(d);
	}


	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == OkButton) {
			if (rowRadioButton.isSelected()) {
				Model.removeRowItems(selectedRows);
			} else if (columnRadioButton.isSelected()) {
				Model.removeColumnItems(selectedColumns);
			}
			this.dispose();

		} else if (e.getSource() == cancelButton) {
			this.dispose();

		}

	}


	private void dispose()
	{

		SwingUtilities.windowForComponent(this).dispose();
	}

	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("DeleteDialog: " + msg);
	}
}
