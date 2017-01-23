// DataPanel_changeSeriesNamePanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.*;

/**
 *
 */
public class DataPanel_changeSeriesNamePanel extends JPanel implements ActionListener
{
	JTextField valueTextField;

	JButton OkButton;
	JButton cancelButton;


	static String answer = null;

	public static String showValueInput(Component parent, String initValue)
	{

		DataPanel_changeSeriesNamePanel editor = new DataPanel_changeSeriesNamePanel(initValue);

		JDialog d = DialogFactory.createDialog(parent, "change serie name:", editor, true, false);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		d.show();

		return answer;

	}

	public DataPanel_changeSeriesNamePanel(String initValue)
	{
		super();

		Font font11 = new Font("Dialog", Font.PLAIN, 11);
		Font bold = new Font("Dialog", Font.BOLD, 11);
		// create components
		OkButton = Templates.makeButton("OK", font11, this);
		cancelButton = Templates.makeButton("cancel", font11, this);
		JLabel textLabel = Templates.makeLabel("serie name:");
		valueTextField = Templates.makeTextField(initValue.toString());

		valueTextField.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String value;
					try {

						value = valueTextField.getText();

					} catch (Exception ee) {
						return;//just return
					}
					answer = value;
					dispose();
				}
			}
		});


		JComponent[] comps = {OkButton, cancelButton, textLabel, valueTextField};

		// do layout
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.SOUTHEAST, java.awt.GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.SOUTHEAST, java.awt.GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0),
			new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0),
		};


		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		for (int i = 0; i < gbcs.length; ++i) {
			gridbag.setConstraints(comps[i], gbcs[i]);
			this.add(comps[i]);
		}

		Dimension d = new Dimension(195, 70);

		this.setPreferredSize(d);
	}


	public void actionPerformed(ActionEvent e)
	{

		if (e.getSource() == OkButton) {
			String value;
			try {

				value = valueTextField.getText();

			} catch (Exception ee) {
				return;//just return
			}
			this.answer = value;
			this.dispose();
		} else if (e.getSource() == cancelButton) {
			this.dispose();

		}

	}


	private void dispose()
	{

		SwingUtilities.windowForComponent(this).dispose();
	}


}
