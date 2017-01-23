// OneButton3Msg.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

public class OneButton3Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(250, 130);

	protected static final String TITLE = "Warning: illegal operation";
	protected static final Dimension SIZE = new Dimension(320, 130);
	protected static final String OK_TEXT = "OK";

	protected static final String MODEL_ADD = "is an object type that is not supported by model";
	protected static final String MODEL_PASTE = "is an object that cannot be pasted into model";
	protected static final String RELATION_ADD = "is an object type that is not supported by relation";
	protected static final String RELATION_PASTE = "is an object that cannot be pasted into relation";

	public static void show(Component parent, String type, String title, String msg, String item1, String item2, String button, Dimension size)
	{
		JDialog d = DialogFactory.createDialog(parent, title,
		                                       new OneButton3Msg(type, msg, size, item1,
		                                                         item2, button).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void showOption(Component parent, String title, String msg, String item1, String item2, String button, Dimension size)
	{
		show(parent, Msg.OPTION_MSG, title, msg, item1, item2, button, size);
	}

	public static void showWarning(Component parent, String title, String msg, String item1, String item2, String button, Dimension size)
	{
		show(parent, Msg.WARNING_MSG, title, msg, item1, item2, button, size);

	}

	public static void showError(Component parent, String title, String msg, String item1, String item2, String button, Dimension size)
	{
		show(parent, Msg.ERROR_MSG, title, msg, item1, item2, button, size);

	}


	public static void showModelAddWarning(Component parent, String modelObjectType, String modelName)
	{
		JDialog d = DialogFactory.createDialog(parent, TITLE,
		                                       new OneButton3Msg(Msg.WARNING_MSG, MODEL_ADD, SIZE,
		                                                         modelObjectType, modelName, OK_TEXT).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void showModelPasteWarning(Component parent, String modelObjectName, String modelName)
	{
		JDialog d = DialogFactory.createDialog(parent, TITLE,
		                                       new OneButton3Msg(Msg.WARNING_MSG, MODEL_PASTE, SIZE,
		                                                         modelObjectName, modelName, OK_TEXT).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void showRelationAddWarning(Component parent, String modelObjectType, String relationName)
	{
		JDialog d = DialogFactory.createDialog(parent, TITLE,
		                                       new OneButton3Msg(Msg.WARNING_MSG, RELATION_ADD, SIZE,
		                                                         modelObjectType, relationName, OK_TEXT).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void showRelationPasteWarning(Component parent, String modelObjectName, String relationName)
	{
		JDialog d = DialogFactory.createDialog(parent, TITLE,
		                                       new OneButton3Msg(Msg.WARNING_MSG, RELATION_PASTE, SIZE,
		                                                         modelObjectName, relationName, OK_TEXT).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public OneButton3Msg(String type, String msg, Dimension dim, String startMsg, String endMsg, String button)
	{
		gui = makePanel(type, msg, dim, startMsg, endMsg, button);
	}

	private JPanel makePanel(String type, String msg, Dimension size, String startMsg, String endMsg, String button)
	{

		ImageIcon icon = getMsgIcon(type);
		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);

		JTextArea messageArea = Templates.makeDisplayTextArea(msg);
		JButton okButton = Templates.makeButton(button, new CloseMsgActionListener());

		JTextArea start = Templates.makeDisplayTextArea(startMsg, Templates.FONT11I);
		JTextArea end = Templates.makeDisplayTextArea(endMsg, Templates.FONT11I);

		JPanel dialogPanel = new JPanel();
		JComponent[] dComps = {iconLabel, start, messageArea, end, okButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] dGbcs = {
			new GridBagConstraints(1, 0, 1, 4, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTHWEST, gbc.HORIZONTAL, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.HORIZONTAL, new Insets(7, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(7, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(10, 10, 10, 0), 0, 0)
		};
		Templates.layoutGridBag(dialogPanel, dComps, dGbcs);

		Dimension prefSize = dialogPanel.getPreferredSize();
		dialogPanel.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                           Math.max(prefSize.height, size.height)));
		return dialogPanel;
	}

	public static void main(String[] args)
	{
		showRelationPasteWarning(null, "ABC", "DEF");
		System.exit(0);
	}

}
