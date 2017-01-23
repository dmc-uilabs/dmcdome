// ServerLoginError.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

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

public class TwoButton1Msg extends Msg
{

	public static final Dimension DEFAULT_SIZE = new Dimension(250, 80);

	public static final int RIGHT_OPTION = 0;
	public static final int LEFT_OPTION = 1;

	protected static final String ERRORTITLE = "Error";
	protected static final String WARNINGTITLE = "Warning";
	protected static final String OPTIONTITLE = "Option";


	protected static final Dimension SIZE = new Dimension(240, 80);

	protected static final String DUMMYTEXT = "Message goes here";

	protected static final String BUTTON1TEXT = "OK";
	protected static final String BUTTON2TEXT = "cancel";

	public static int show(Component parent, String type, String title, String msg, String button1, String button2, Dimension size)
	{

		TwoButton1Msg m = new TwoButton1Msg(size, msg, button1, button2, type);
		JDialog d = DialogFactory.createDialog(parent, title, m.gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		return m.answer;
	}

	public static int showError(Component parent, String title, String msg, String button1, String button2, Dimension size)
	{
		return show(parent, Msg.ERROR_MSG, title, msg, button1, button2, size);
	}

	public static int showWarning(Component parent, String title, String msg, String button1, String button2, Dimension size)
	{
		return show(parent, Msg.WARNING_MSG, title, msg, button1, button2, size);
	}

	public static int showOption(Component parent, String title, String msg, String button1, String button2, Dimension size)
	{
		return show(parent, Msg.OPTION_MSG, title, msg, button1, button2, size);
	}

	private TwoButton1Msg(Dimension size, String msg, String button1, String button2, String type)
	{
		gui = makeErrorPanel(size, msg, button1, button2, type);
		answer = RIGHT_OPTION; // default
	}

	private JPanel makeErrorPanel(Dimension size, String msg, String button1, String button2, String type)
	{
		JPanel p = new JPanel();
		JLabel errorMessageLabel = Templates.makeLabel(msg);
		ImageIcon errorIcon = getMsgIcon(type);
		JLabel errorIconLabel = new JLabel(errorIcon, SwingConstants.RIGHT);

		JButton leftButton = Templates.makeButton(button1, new MsgOptionActionListener(LEFT_OPTION));
		JButton rightButton = Templates.makeButton(button2, new CloseMsgActionListener());

		JPanel buttonPanel = new JPanel();
		JComponent[] bComps = {leftButton, rightButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		// in the order of the comps array
		GridBagConstraints[] bGbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)};
		Templates.layoutGridBag(buttonPanel, bComps, bGbcs);


		JComponent[] comps = {errorMessageLabel, errorIconLabel, buttonPanel};
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
		TwoButton1Msg.showError(null, ERRORTITLE, DUMMYTEXT, BUTTON1TEXT, BUTTON2TEXT, SIZE);
		TwoButton1Msg.showOption(null, OPTIONTITLE, DUMMYTEXT, BUTTON1TEXT, BUTTON2TEXT, SIZE);
		TwoButton1Msg.showWarning(null, WARNINGTITLE, DUMMYTEXT, BUTTON1TEXT, BUTTON2TEXT, SIZE);
		System.exit(0);
	}

}
