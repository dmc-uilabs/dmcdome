// TwoButton2Msg.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class TwoButton2Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(250, 100);

	public static final int RIGHT_OPTION = 0;
	public static final int LEFT_OPTION = 1;

	public static int show(Component parent, String type, String title, String message, String item1,
	                       String button1, String button2, Dimension size)
	{
		TwoButton2Msg msg = new TwoButton2Msg(item1, type, message, button1, button2, size);
		JDialog d = DialogFactory.createDialog(parent, title,
		                                       msg.gui,
		                                       true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return msg.answer;
	}

	public static int showOption(Component parent, String title, String message, String item1,
	                             String button1, String button2, Dimension size)
	{
		return (show(parent, Msg.OPTION_MSG, title, message, item1, button1, button2, size));
	}

	public static int showWarning(Component parent, String title, String message, String item1,
	                              String button1, String button2, Dimension size)
	{
		return (show(parent, Msg.WARNING_MSG, title, message, item1, button1, button2, size));
	}

	public static int showError(Component parent, String title, String message, String item1,
	                            String button1, String button2, Dimension size)
	{
		return (show(parent, Msg.ERROR_MSG, title, message, item1, button1, button2, size));
	}

	public TwoButton2Msg(String subject, String msgType, String msg, String ok, String cancel, Dimension dim)
	{
		gui = makePanel(subject, msgType, msg, ok, cancel, dim);
		answer = RIGHT_OPTION; // default
	}

	private JPanel makePanel(String subject, String msgType, String msg, String ok, String cancel, Dimension size)
	{
		JLabel iconLabel = new JLabel(getMsgIcon(msgType));
		JTextArea subjectArea = Templates.makeDisplayTextArea(subject, Templates.FONT11I);
		JTextArea messageArea = Templates.makeDisplayTextArea(msg);
		JButton okButton = Templates.makeButton(ok, new MsgOptionActionListener(LEFT_OPTION));
		JButton cancelButton = Templates.makeButton(cancel, new MsgOptionActionListener(RIGHT_OPTION));

		JPanel buttonPanel = new JPanel();
		JComponent[] bComps = {okButton, cancelButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] bGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, bComps, bGbcs);

		JPanel dialogPanel = new JPanel();
		JComponent[] dComps = {iconLabel, subjectArea, messageArea, buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(1, 0, 1, 3, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(7, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(10, 10, 10, 0), 0, 0)};
		Templates.layoutGridBag(dialogPanel, dComps, dGbcs);

		Dimension prefSize = dialogPanel.getPreferredSize();
		dialogPanel.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                           Math.max(prefSize.height, size.height)));
		return dialogPanel;
	}

	public static void main(String[] args)
	{
		int answer = TwoButton2Msg.showOption(null, "title", "HAS NOT BEEN SAVED \nkeep width of dialog fixed for consistency", "XYZ",
		                                      "OK", "Cancel",
		                                      new Dimension(280, 110));
		switch (answer) {
			case TwoButton2Msg.RIGHT_OPTION:
				System.out.println("Cancel option.");
				break;
			case TwoButton2Msg.LEFT_OPTION:
				System.out.println("Ok option.");
				break;
			default:
				System.out.println("Unknown option: " + answer);
		}
		TwoButton2Msg.show(null, Msg.ERROR_MSG, "title", "HAS NOT BEEN SAVED \nkeep width of dialog fixed for consistency", "XYZ",
		                   "OK", "Cancel",
		                   new Dimension(280, 110));
		TwoButton2Msg.show(null, Msg.WARNING_MSG, "title", "HAS NOT BEEN SAVED \nkeep width of dialog fixed for consistency", "XYZ",
		                   "OK", "Cancel",
		                   new Dimension(280, 110));
		System.exit(0);
	}

}
