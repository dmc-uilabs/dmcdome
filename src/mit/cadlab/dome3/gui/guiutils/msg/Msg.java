// Msg.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.Templates;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;

/*
 * Framework for msg classes supporting information msgs and option msgs.
 * For both kinds of msgs, initialize gui in constructor.
 * Msgs should attach CloseMsgActionListener to close button.
 * Option msgs should attach MsgOptionActionListener to option buttons
 *   and set default value for answer.
 * Create static methods to show the dialogs. Use DialogFactory.
 *
 * Example information dialog:
 * public static void showInformation(Component parent) {
 *   JDialog d = DialogFactory.createDialog(parent,TITLE,new MsgInfo().gui,isModal,isResizable);
 *   d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
 *   d.show();
 * }
 *
 * Example option dialog:
 * public static int showOptions(Component parent) {
 *   MsgOptions msgOptions = new MsgOptions();
 *   JDialog d = DialogFactory.createDialog(parent,TITLE,msgOptions.gui,isModal,isResizable);
 *   d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
 *   d.show();
 *   return msgOptions.answer;
 * }
 */

public abstract class Msg
{

	public static final String OPTION_MSG = "options";
	public static final String WARNING_MSG = "warning";
	public static final String ERROR_MSG = "error";

	protected static final ImageIcon OPTION_ICON = Templates.makeImageIcon("mit/cadlab/dome3/icons/msg/options.gif");
	protected static final ImageIcon WARNING_ICON = Templates.makeImageIcon("mit/cadlab/dome3/icons/msg/warning.gif");
	protected static final ImageIcon ERROR_ICON = Templates.makeImageIcon("mit/cadlab/dome3/icons/msg/serverError.gif");

	public static ImageIcon getMsgIcon(String msgType)
	{
		if (msgType.equals(WARNING_MSG))
			return WARNING_ICON;
		else if (msgType.equals(ERROR_MSG))
			return ERROR_ICON;
		else
			return OPTION_ICON;
	}

	protected static GridBagConstraints gbc;
	public JComponent gui;
	public int answer;

	protected void dispose()
	{
		SwingUtilities.windowForComponent(gui).dispose();
	}

	protected class MsgOptionActionListener implements ActionListener
	{
		protected int option;

		public MsgOptionActionListener(int option)
		{
			this.option = option;
		}

		public void actionPerformed(ActionEvent event)
		{
			answer = option;
			dispose();
		}
	}

	protected class CloseMsgActionListener implements ActionListener
	{
		public CloseMsgActionListener()
		{
		}

		public void actionPerformed(ActionEvent event)
		{
			dispose();
		}
	}

}
