// ServerLoginError.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.exceptions.RelationExecutionException;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JTextArea;

public class OneButton1Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(250, 80);

	protected static final String LOGINTITLE = "Login Error";
	protected static final String PASSWORDTITLE = "Login Error";
	protected static final String WARNINGTITLE = "Warning";
	protected static final String OPTIONTITLE = "Option";


	protected static final Dimension LOGINSIZE = new Dimension(240, 80);
	protected static final Dimension PASSWORDSIZE = new Dimension(250, 80);
	protected static final Dimension DUMMYSIZE = new Dimension(250, 80);

	protected static final String LOGINTEXT = "Cannot find the specified server!";
	protected static final String PASSWORDTEXT = "Incorrect username or password!";
	protected static final String DUMMYTEXT = "Message goes here";

	protected static final String BUTTONTEXT = "OK";

	public static void showRelationExecutionError(Component parent, RelationExecutionException ex) {
		show(parent, Msg.ERROR_MSG, "Relation \""+ex.getRelationName()+"\"", ex.getMessageNoRelationName(),
		     BUTTONTEXT, DEFAULT_SIZE);
	}

	public static void show(Component parent, String type, String title, String msg, String button, Dimension size)
	{
		JDialog d = DialogFactory.createDialog(parent, title, new OneButton1Msg(size, msg, button, type).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void show(Component parent, String type, String title, String msg, String button, Dimension size, boolean modal)
	{
		JDialog d = DialogFactory.createDialog(parent, title, new OneButton1Msg(size, msg, button, type).gui, modal, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	public static void showError(Component parent, String title, String msg, String button, Dimension size)
	{
		show(parent, Msg.ERROR_MSG, title, msg, button, size);
	}

	public static void showError(Component parent, String title, String msg, String button, Dimension size, boolean modal)
	{
		show(parent, Msg.ERROR_MSG, title, msg, button, size, modal);
	}

	public static void showWarning(Component parent, String title, String msg, String button, Dimension size)
	{
		show(parent, Msg.WARNING_MSG, title, msg, button, size);
	}

	public static void showOption(Component parent, String title, String msg, String button, Dimension size)
	{
		show(parent, Msg.OPTION_MSG, title, msg, button, size);
	}

	private OneButton1Msg(Dimension size, String msg, String buttonText, String type)
	{
		if (size == null)
			size = new Dimension();
		gui = makePanel(size, msg, buttonText, type);
	}

	private JPanel makePanel(Dimension size, String msg, String buttonText, String type)
	{
		JPanel p = new JPanel();
		JTextArea messageLabel = Templates.makeDisplayTextArea(msg);
		ImageIcon labelIcon = getMsgIcon(type);
		JLabel iconLabel = new JLabel(labelIcon, SwingConstants.RIGHT);
		JButton dismissButton = Templates.makeButton(buttonText, new CloseMsgActionListener());

		JComponent[] comps = {messageLabel, iconLabel, dismissButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.NONE, new Insets(10, 10, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 2, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 10, 0), 0, 0)};
		Templates.layoutGridBag(p, comps, gbcs);

		Dimension prefSize = p.getPreferredSize();
		p.setPreferredSize(new Dimension(Math.max(prefSize.width, size.width),
		                                 Math.max(prefSize.height, size.height)));
		return p;
	}

	public static void main(String[] args)
	{
		OneButton1Msg.showError(null, LOGINTITLE, LOGINTEXT, BUTTONTEXT, LOGINSIZE);
		OneButton1Msg.showError(null, PASSWORDTITLE, PASSWORDTEXT, BUTTONTEXT, PASSWORDSIZE);
		OneButton1Msg.showOption(null, OPTIONTITLE, DUMMYTEXT, BUTTONTEXT, DUMMYSIZE);
		OneButton1Msg.showWarning(null, WARNINGTITLE, DUMMYTEXT, BUTTONTEXT, DUMMYSIZE);
		System.exit(0);
	}

}
