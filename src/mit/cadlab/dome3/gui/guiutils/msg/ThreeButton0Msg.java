// ThreeButton0Msg.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

public class ThreeButton0Msg extends Msg
{

	public static final int CANCEL_OPTION = 0;
	public static final int TOP_OPTION = 1;
	public static final int MIDDLE_OPTION = 2;
	public static final int BOTTOM_OPTION = 3;

	public static final Dimension DEFAULT_SIZE = new Dimension(250, 105);

	public static int show(Component parent, String type, String title, String top, String middle, String bottom, Dimension size,
	                       int defaultCloseOperation)
	{
		ThreeButton0Msg m = new ThreeButton0Msg(type, top, middle, bottom, size);
		JDialog d = DialogFactory.createDialog(parent, title, m.gui, true, false);
		d.setDefaultCloseOperation(defaultCloseOperation);
		d.show();
		return m.answer;
	}

	public static int show(Component parent, String type, String title, String top, String middle, String bottom, Dimension size)
	{
		return show(parent, type, title, top, middle, bottom, size, WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static int showOption(Component parent, String title, String top, String middle, String bottom, Dimension size)
	{
		return show(parent, Msg.OPTION_MSG, title, top, middle, bottom, size);
	}

	public static int showWarning(Component parent, String title, String top, String middle, String bottom, Dimension size)
	{
		return show(parent, Msg.WARNING_MSG, title, top, middle, bottom, size);
	}

	public static int showError(Component parent, String title, String top, String middle, String bottom, Dimension size)
	{
		return show(parent, Msg.ERROR_MSG, title, top, middle, bottom, size);
	}

	private ThreeButton0Msg(String type, String top, String middle, String bottom, Dimension size)
	{
		gui = makePanel(type, top, middle, bottom, size);
		answer = CANCEL_OPTION; // default
	}

	private JPanel makePanel(String type, String top, String middle, String bottom, Dimension size)
	{

		ImageIcon icon = getMsgIcon(type);
		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);

		JButton topButton = Templates.makeButton(top, new MsgOptionActionListener(TOP_OPTION));
		JButton middleButton = Templates.makeButton(middle, new MsgOptionActionListener(MIDDLE_OPTION));
		JButton bottomButton = Templates.makeButton(bottom, new MsgOptionActionListener(BOTTOM_OPTION));
		topButton.setHorizontalAlignment(SwingConstants.LEFT);
		middleButton.setHorizontalAlignment(SwingConstants.LEFT);
		bottomButton.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel buttonPanel = new JPanel();
		JComponent[] bComps = {topButton, middleButton, bottomButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] bGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(3, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(3, 0, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, bComps, bGbcs);

		JPanel dialogPanel = new JPanel();
		JComponent[] dComps = {iconLabel, buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTH, gbc.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0)};
		Templates.layoutGridBag(dialogPanel, dComps, dGbcs);

		Dimension prefSize = dialogPanel.getPreferredSize();
		dialogPanel.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                           Math.max(prefSize.height, size.height)));
		return dialogPanel;
	}

	public static void main(String[] args)
	{
		int answer = ThreeButton0Msg.showOption(null, "title", "top message", "middle message", "bottom msg", DEFAULT_SIZE);
		switch (answer) {
			case ThreeButton0Msg.CANCEL_OPTION:
				System.out.println("RIGHT_OPTION");
				break;
			case ThreeButton0Msg.TOP_OPTION:
				System.out.println("TOP_OPTION");
				break;
			case ThreeButton0Msg.MIDDLE_OPTION:
				System.out.println("MIDDLE_OPTION");
				break;
			case ThreeButton0Msg.BOTTOM_OPTION:
				System.out.println("BOTTOM_OPTION");
				break;
			default:
				System.out.println("UNKNOWN_OPTION: " + answer);
		}
		System.exit(0);
	}

}
