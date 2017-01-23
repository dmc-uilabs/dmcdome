// ServerLoginError.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.exceptions.BuildMappingValuePropagationException;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

public class OneButton2Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(250, 100);

	protected static final String ERRORTITLE = "Login Error";
	protected static final String WARNINGTITLE = "Warning";
	protected static final String OPTIONTITLE = "Option";
	protected static final String MAPPING_VALUE_ERROR_TITLE =  "Mapping value propagation error";
	protected static final String MAPPING_VALUE_ERROR_ITEM = "You will need to fix the error manually.";

	protected static final Dimension DUMMYSIZE = new Dimension(250, 100);

	protected static final String DUMMYTEXT = "Message goes here";
	protected static final String DUMMYITEM = "Item 1";

	protected static final String BUTTONTEXT = "OK";

	public static void showBuildMappingValuePropagationError(BuildMappingValuePropagationException ex) {
		JDialog d = DialogFactory.createDialog(BuildFocusTracker.getCurrentComponent(),
		                                       MAPPING_VALUE_ERROR_TITLE,
		                                       new OneButton2Msg(DUMMYSIZE, ex.getMessage(),
		                                                         MAPPING_VALUE_ERROR_ITEM, BUTTONTEXT,
		                                                         Msg.ERROR_MSG).gui, false, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void show(Component parent, String type, String title, String msg, String item1, String button, Dimension size)
	{
		JDialog d = DialogFactory.createDialog(parent, title, new OneButton2Msg(size, msg, item1, button, type).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void showError(Component parent, String title, String msg, String item1, String button, Dimension size)
	{
		show(parent, Msg.ERROR_MSG, title, msg, item1, button, size);
	}

	public static void showWarning(Component parent, String title, String msg, String item1, String button, Dimension size)
	{
		show(parent, Msg.WARNING_MSG, title, msg, item1, button, size);
	}

	public static void showOption(Component parent, String title, String msg, String item1, String button, Dimension size)
	{
		show(parent, Msg.OPTION_MSG, title, msg, item1, button, size);
	}

	private OneButton2Msg(Dimension size, String msg, String item1, String buttonText, String type)
	{
		gui = makePanel(size, msg, item1, buttonText, type);
	}

	private JPanel makePanel(Dimension size, String msg, String item1, String buttonText, String type)
	{
		JPanel p = new JPanel();
		JTextArea itemArea = Templates.makeDisplayTextArea(item1, Templates.FONT11I);
		JTextArea messageArea = Templates.makeDisplayTextArea(msg);

		ImageIcon labelIcon = getMsgIcon(type);
		JLabel iconLabel = new JLabel(labelIcon, SwingConstants.RIGHT);
		JButton dismissButton = Templates.makeButton(buttonText, new CloseMsgActionListener());

		JComponent[] comps = {iconLabel, itemArea, messageArea, dismissButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(1, 0, 1, 3, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(7, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(10, 10, 10, 0), 0, 0)};
		Templates.layoutGridBag(p, comps, gbcs);
		Dimension prefSize = p.getPreferredSize();
		p.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                 Math.max(prefSize.height, size.height)));
		return p;
	}

	public static void main(String[] args)
	{
		OneButton2Msg.showError(null, ERRORTITLE, DUMMYTEXT, DUMMYITEM, BUTTONTEXT, DUMMYSIZE);
		OneButton2Msg.showOption(null, OPTIONTITLE, DUMMYTEXT, DUMMYITEM, BUTTONTEXT, DUMMYSIZE);
		OneButton2Msg.showWarning(null, WARNINGTITLE, DUMMYTEXT, DUMMYITEM, BUTTONTEXT, DUMMYSIZE);
		System.exit(0);
	}

}
