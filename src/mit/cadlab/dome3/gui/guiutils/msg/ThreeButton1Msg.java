// ThreeButton2Msg.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

public class ThreeButton1Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(330, 105);

	public static final int OPTION1 = 0;
	public static final int OPTION2 = 1;
	public static final int OPTION3 = 2;

	public static final String OPTION = "Option";
	public static final String WARNING = "Warning";
	public static final String ERROR = "Error";

	public static final String MSG = "message goes here";
	public static final Dimension SIZE = new Dimension(350, 100);

	public static final String BUTTON1 = "maybe";
	public static final String BUTTON2 = "OK";
	public static final String BUTTON3 = "cancel";

	public static int show(Component parent, String type, String title, String msg,
	                       String button1, String button2, String button3,
	                       Dimension size)
	{
		ThreeButton1Msg m = new ThreeButton1Msg(type, msg,
		                                        button1, button2, button3, size);
		JDialog d = DialogFactory.createDialog(parent, title, m.gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return m.answer;
	}

	public static int showOption(Component parent, String title, String msg,
	                             String button1, String button2, String button3,
	                             Dimension size)
	{
		return show(parent, Msg.OPTION_MSG, title, msg, button1, button2, button3, size);
	}

	public static int showWarning(Component parent, String title, String msg,
	                              String button1, String button2, String button3,
	                              Dimension size)
	{
		return show(parent, Msg.WARNING_MSG, title, msg, button1, button2, button3, size);
	}

	public static int showError(Component parent, String title, String msg,
	                            String button1, String button2, String button3,
	                            Dimension size)
	{
		return show(parent, Msg.ERROR_MSG, title, msg, button1, button2, button3, size);
	}


	private ThreeButton1Msg(String type, String msg, String option1, String option2,
	                        String option3, Dimension size)
	{
		gui = makePanel(type, msg, option1, option2, option3, size);
		answer = OPTION3; // default
	}

	private JPanel makePanel(String type, String msg, String option1, String option2,
	                         String option3, Dimension size)
	{

		ImageIcon icon = getMsgIcon(type);
		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);

		JTextArea messageArea = Templates.makeDisplayTextArea(msg);

		JButton option1Button = Templates.makeButton(option1, new MsgOptionActionListener(OPTION1));
		JButton option2Button = Templates.makeButton(option2, new MsgOptionActionListener(OPTION2));
		JButton option3Button = Templates.makeButton(option3, new MsgOptionActionListener(OPTION3));

		JPanel buttonPanel = new JPanel();
		JComponent[] bComps = {option1Button, option2Button, option3Button};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] bGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.SOUTHEAST, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, bComps, bGbcs);

		JPanel dialogPanel = new JPanel();
		JComponent[] dComps = {iconLabel, messageArea, buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(2, 0, 1, 2, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 10, 0), 0, 0)};
		Templates.layoutGridBag(dialogPanel, dComps, dGbcs);

		Dimension prefSize = dialogPanel.getPreferredSize();
		dialogPanel.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                           Math.max(prefSize.height, size.height)));
		return dialogPanel;
	}

	public static void main(String[] args)
	{
		ThreeButton1Msg.showOption(null, OPTION, MSG, BUTTON1, BUTTON2, BUTTON3, SIZE);
		ThreeButton1Msg.showWarning(null, WARNING, MSG, BUTTON1, BUTTON2, BUTTON3, SIZE);
		ThreeButton1Msg.showError(null, ERROR, MSG, BUTTON1, BUTTON2, BUTTON3, SIZE);
		System.exit(0);
	}

}
