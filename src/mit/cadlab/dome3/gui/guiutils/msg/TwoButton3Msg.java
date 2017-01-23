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

public class TwoButton3Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(250, 130);

	public static final int RIGHT = 0;  //default
	public static final int LEFT = 1;

	protected static final String DUMMY_OPTION = "Options";
	protected static final String DUMMY_WARNING = "Warning";
	protected static final String DUMMY_ERROR = "Error";

	protected static final String DUMMY_MSG = "message goes here";
	protected static final String DUMMY_ITEM1 = "XXX";
	protected static final String DUMMY_ITEM2 = "YYY";
	protected static final String DUMMY_LEFT = "OK";
	protected static final String DUMMY_RIGHT = "cancel";
	protected static final Dimension DUMMY_SIZE = new Dimension(330, 120);


	public static int show(Component parent, String type, String title, String message, String item1, String item2,
	                       String button1, String button2, Dimension size)
	{
		TwoButton3Msg msg = new TwoButton3Msg(item1, item2, type, message, button1, button2, size);
		JDialog d = DialogFactory.createDialog(parent, title,
		                                       msg.gui,
		                                       true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return msg.answer;
	}

	public static int showOption(Component parent, String title, String message, String item1, String item2,
	                             String button1, String button2, Dimension size)
	{
		return (show(parent, Msg.OPTION_MSG, title, message, item1, item2, button1, button2, size));
	}

	public static int showWarning(Component parent, String title, String message, String item1, String item2,
	                              String button1, String button2, Dimension size)
	{
		return (show(parent, Msg.WARNING_MSG, title, message, item1, item2, button1, button2, size));
	}

	public static int showError(Component parent, String title, String message, String item1, String item2,
	                            String button1, String button2, Dimension size)
	{
		return (show(parent, Msg.ERROR_MSG, title, message, item1, item2, button1, button2, size));
	}

	public TwoButton3Msg(String item1, String item2, String msgType, String msg, String left, String right, Dimension dim)
	{
		gui = makePanel(item1, item2, msgType, msg, left, right, dim);
		answer = RIGHT; // default
	}

	private JPanel makePanel(String item1, String item2, String msgType, String msg, String left, String right, Dimension size)
	{

		JLabel iconLabel = new JLabel(getMsgIcon(msgType));
		JTextArea item1Area = Templates.makeDisplayTextArea(item1, Templates.FONT11I);
		JTextArea messageArea = Templates.makeDisplayTextArea(msg);
		JTextArea item2Area = Templates.makeDisplayTextArea(item2, Templates.FONT11I);
		JButton leftButton = Templates.makeButton(left, new MsgOptionActionListener(LEFT));
		JButton rightButton = Templates.makeButton(right, new MsgOptionActionListener(RIGHT));

		JPanel buttonPanel = new JPanel();
		JComponent[] bComps = {leftButton, rightButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] bGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, bComps, bGbcs);

		JPanel dialogPanel = new JPanel();
		JComponent[] dComps = {iconLabel, item1Area, messageArea, item2Area, buttonPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(1, 0, 1, 4, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(7, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(7, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(10, 10, 10, 0), 0, 0)};
		Templates.layoutGridBag(dialogPanel, dComps, dGbcs);

		Dimension prefSize = dialogPanel.getPreferredSize();
		dialogPanel.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                           Math.max(prefSize.height, size.height)));
		return dialogPanel;
	}

	public static void main(String[] args)
	{
		TwoButton3Msg.showError(null, DUMMY_ERROR, DUMMY_MSG, DUMMY_ITEM1, DUMMY_ITEM2, DUMMY_LEFT, DUMMY_RIGHT, DUMMY_SIZE);
		TwoButton3Msg.showError(null, DUMMY_WARNING, DUMMY_MSG, DUMMY_ITEM1, DUMMY_ITEM2, DUMMY_LEFT, DUMMY_RIGHT, DUMMY_SIZE);
		TwoButton3Msg.showError(null, DUMMY_OPTION, DUMMY_MSG, DUMMY_ITEM1, DUMMY_ITEM2, DUMMY_LEFT, DUMMY_RIGHT, DUMMY_SIZE);
		System.exit(0);
	}

}
