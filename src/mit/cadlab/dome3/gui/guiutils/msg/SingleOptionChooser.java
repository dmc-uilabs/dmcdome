// SingleOptionChooser.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.Templates;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SingleOptionChooser extends JDialog
{

	public static String showDialog(JComponent comp,
	                                String title,
	                                String choice,
	                                java.util.List stringChoices)
	{
		return showDialog(comp, title, choice, (String[]) stringChoices.toArray(new String[]{}));
	}

	public static String showDialog(JComponent comp,
	                                String title,
	                                String choice,
	                                String[] choices)
	{
		SingleOptionChooser chooser = new SingleOptionChooser(comp, title, choice, choices);
		chooser.show();
		return chooser.selection;
	}

	protected static GridBagConstraints gbc;
	protected JList optionList;
	protected JButton okButton,cancelButton;
	protected String selection;

	public SingleOptionChooser(JComponent comp, String title,
	                           String choice, String[] choices)
	{
		super(JOptionPane.getFrameForComponent(comp), title, true); // modal
		getContentPane().add(makeChooser(choice, choices));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		// make sure title fits!
		JLabel l = new JLabel(title);
		Dimension labelD = l.getPreferredSize();
		Dimension d = getSize();
		setSize(Math.max(labelD.width + 65, d.width), d.height);
		setLocationRelativeTo(comp);
	}

	private JPanel makeChooser(String choice, String[] choices)
	{
		JPanel p = new JPanel();
		// component array for GridBagLayout
		JComponent[] comps = {Templates.makeLabel("options:"),
		                      makeOptionList(choice, choices),
		                      makeButtonPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(4, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)};
		Templates.layoutGridBagB(p, comps, gbcs);
		return p;
	}

	public JScrollPane makeOptionList(String choice, String[] choices)
	{
		optionList = Templates.makeList(choices);
		optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionList.setSelectedValue(choice, true);
		JScrollPane scrollPane = new JScrollPane(optionList);
		return scrollPane;
	}

	public JPanel makeButtonPanel()
	{
		ActionListener buttonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JButton b = (JButton) e.getSource();
				if (b == okButton)
					selection = optionList.getSelectedValue().toString();
				dispose();
			}
		};
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createHorizontalGlue());
		okButton = Templates.makeButton("ok");
		okButton.addActionListener(buttonListener);
		p.add(okButton);
		p.add(Box.createRigidArea(new Dimension(5, 0))); // separated by spacer
		cancelButton = Templates.makeButton("cancel");
		cancelButton.addActionListener(buttonListener);
		p.add(cancelButton);
		return p;
	}

	public static void main(String[] args)
	{
		String[] labels = {"one", "two", "three"};
		String answer = showDialog(null,
		                           "Options Dialog",
		                           "two", labels);
		if (answer == null)
			System.out.println("cancelled");
		else
			System.out.println("picked: " + answer);
		System.exit(0);
	}

}
