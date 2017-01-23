// ThreeButton2Msg.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

public class FourButton2Msg extends Msg
{

	public static final int TOP_OPTION = 0;
	public static final int UPPER_MIDDLE_OPTION = 1;
	public static final int LOWER_MIDDLE_OPTION = 2;
	public static final int BOTTOM_OPTION = 3;

	public static final Dimension DEFAULT_SIZE = new Dimension(565, 130);

	public static int show(Component parent, String type, String title, String msg, String item1,
	                       String button1, String button2, String button3, String button4,
	                       Dimension size)
	{
		FourButton2Msg m = new FourButton2Msg(type, item1, msg,
		                                      button1, button2, button3, button4, size);
		JDialog d = DialogFactory.createDialog(parent, title, m.gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return m.answer;
	}

	public static int showOption(Component parent, String title, String msg, String item1,
	                             String button1, String button2, String button3, String button4,
	                             Dimension size)
	{
		return show(parent, Msg.OPTION_MSG, title, msg, item1, button1, button2, button3, button4, size);
	}

	public static int showWarning(Component parent, String title, String msg, String item1,
	                              String button1, String button2, String button3, String button4,
	                              Dimension size)
	{
		return show(parent, Msg.WARNING_MSG, title, msg, item1, button1, button2, button3, button4, size);
	}

	public static int showError(Component parent, String title, String msg, String item1,
	                            String button1, String button2, String button3, String button4,
	                            Dimension size)
	{
		return show(parent, Msg.ERROR_MSG, title, msg, item1, button1, button2, button3, button4, size);
	}


	private FourButton2Msg(String type, String subject, String msg, String option1, String option2,
	                       String option3, String option4, Dimension size)
	{
		gui = makePanel(type, subject, msg, option1, option2, option3, option4, size);
		answer = BOTTOM_OPTION; // default
	}

	private JPanel makePanel(String type, String subject, String msg, String option1, String option2,
	                         String option3, String option4, Dimension size)
	{

		ImageIcon icon = getMsgIcon(type);
		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);

		JTextArea subjectArea = Templates.makeDisplayTextArea(subject, Templates.FONT11I);
		JTextArea messageArea = Templates.makeDisplayTextArea(msg);

		JButton option1Button = Templates.makeButton(option1, new MsgOptionActionListener(TOP_OPTION));
		JButton option2Button = Templates.makeButton(option2, new MsgOptionActionListener(UPPER_MIDDLE_OPTION));
		JButton option3Button = Templates.makeButton(option3, new MsgOptionActionListener(LOWER_MIDDLE_OPTION));
		JButton option4Button = Templates.makeButton(option4, new MsgOptionActionListener(BOTTOM_OPTION));

		JPanel buttonPanel = new JPanel();
		JComponent[] bComps = {option1Button, option2Button, option3Button, option4Button};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] bGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, bComps, bGbcs);

		JPanel dialogPanel = new JPanel();
		JComponent[] dComps = {iconLabel, subjectArea, messageArea, buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(2, 0, 1, 2, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(7, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 10, 0), 0, 0)};
		Templates.layoutGridBag(dialogPanel, dComps, dGbcs);

		Dimension prefSize = dialogPanel.getPreferredSize();
		dialogPanel.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                           Math.max(prefSize.height, size.height)));
		return dialogPanel;
	}

	public static void main(String[] args)
	{
		int answer = FourButton2Msg.showOption(null, "Options: remove context ", "is no longer referenced elsewhere", "myName",
		                                       "keep context object and its contents", "delete context, keep its contents",
		                                       "delete context and contents not used elsewhere", "delete context and all its contents", DEFAULT_SIZE);
		System.exit(0);
	}

}
