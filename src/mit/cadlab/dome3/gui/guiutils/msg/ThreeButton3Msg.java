// ThreeButton2Msg.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

public class ThreeButton3Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(330, 125);

	public static final int OPTION1 = 0;
	public static final int OPTION2 = 1;
	public static final int OPTION3 = 2;

	public static final String OPTION = "Option";
	public static final String WARNING = "Warning";
	public static final String ERROR = "Error";

	public static final String BUTTON1 = "maybe";
	public static final String BUTTON2 = "OK";
	public static final String BUTTON3 = "cancel";

	public static final String ITEM1 = "XXX";
	public static final String ITEM2 = "YYY";
	public static final String MSG = "message goes here";

	public static final Dimension SIZE = new Dimension(400, 100);

	public static int show(Component parent, String type, String title, String msg, String item1, String item2,
	                       String button1, String button2, String button3,
	                       Dimension size)
	{
		ThreeButton3Msg m = new ThreeButton3Msg(type, item1, item2, msg,
		                                        button1, button2, button3, size);
		JDialog d = DialogFactory.createDialog(parent, title, m.gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return m.answer;
	}

	public static int showOption(Component parent, String title, String msg, String item1, String item2,
	                             String button1, String button2, String button3,
	                             Dimension size)
	{
		return show(parent, Msg.OPTION_MSG, title, msg, item1, item2, button1, button2, button3, size);
	}

	public static int showWarning(Component parent, String title, String msg, String item1, String item2,
	                              String button1, String button2, String button3,
	                              Dimension size)
	{
		return show(parent, Msg.WARNING_MSG, title, msg, item1, item2, button1, button2, button3, size);
	}

	public static int showError(Component parent, String title, String msg, String item1, String item2,
	                            String button1, String button2, String button3,
	                            Dimension size)
	{
		return show(parent, Msg.ERROR_MSG, title, msg, item1, item2, button1, button2, button3, size);
	}


	private ThreeButton3Msg(String type, String item1, String item2, String msg, String option1, String option2,
	                        String option3, Dimension size)
	{
		gui = makePanel(type, item1, item2, msg, option1, option2, option3, size);
		answer = OPTION3; // default
	}

	private JPanel makePanel(String type, String item1, String item2, String msg, String option1, String option2,
	                         String option3, Dimension size)
	{

		ImageIcon icon = getMsgIcon(type);
		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);

		JTextArea item1Area = Templates.makeDisplayTextArea(item1, Templates.FONT11I);
		JTextArea messageArea = Templates.makeDisplayTextArea(msg);
		JTextArea item2Area = Templates.makeDisplayTextArea(item2, Templates.FONT11I);

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


		JPanel messagePanel = new JPanel();
		JComponent[] mComps = {item1Area, messageArea, item2Area};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] mGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(7, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(7, 0, 0, 0), 0, 0), };
		Templates.layoutGridBag(messagePanel, mComps, mGbcs);

		JPanel dialogPanel = new JPanel();
		JComponent[] dComps = {iconLabel, messagePanel, buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 10, 0), 0, 0)};
		Templates.layoutGridBag(dialogPanel, dComps, dGbcs);

		Dimension prefSize = dialogPanel.getPreferredSize();
		dialogPanel.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                           Math.max(prefSize.height, size.height)));
		return dialogPanel;
	}

	public static void main(String[] args)
	{
		ThreeButton3Msg.showOption(null, OPTION, MSG, ITEM1, ITEM2, BUTTON1, BUTTON2, BUTTON3, SIZE);
		ThreeButton3Msg.showWarning(null, WARNING, MSG, ITEM1, ITEM2, BUTTON1, BUTTON2, BUTTON3, SIZE);
		ThreeButton3Msg.showError(null, ERROR, MSG, ITEM1, ITEM2, BUTTON1, BUTTON2, BUTTON3, SIZE);
		System.exit(0);
	}

}
