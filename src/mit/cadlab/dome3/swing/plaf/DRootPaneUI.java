// DRootPaneUI.java
package mit.cadlab.dome3.swing.plaf;

import mit.cadlab.dome3.help.DHelp;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * Enables context-sensitive help for JFrames and JDialogs.
 * Code copied from parent class since methods not available
 * to subclasses.
 */
public class DRootPaneUI extends BasicRootPaneUI
{

	protected void installKeyboardActions(JRootPane root)
	{
		InputMap inputMap = new ComponentInputMapUIResource(root);
		String cshActionName = "csHelp";
		inputMap.put(DHelp.CSH_KEYSTROKE, cshActionName);
		SwingUtilities.replaceUIInputMap(root, JComponent.WHEN_IN_FOCUSED_WINDOW,
		                                 inputMap);
		ActionMap actionMap = new ActionMapUIResource();
		actionMap.put("press", new DefaultAction(root, true));
		actionMap.put("release", new DefaultAction(root, false));
		SwingUtilities.replaceUIActionMap(root, actionMap);

		// update default button bindings
		if (root.getDefaultButton() != null) {
			Object[] bindings = (Object[]) UIManager.get
			        ("RootPane.defaultButtonWindowKeyBindings");
			if (bindings != null) {
				LookAndFeel.loadKeyBindings(inputMap, bindings);
			}
		}
	}

	// from BasicRootPaneUI
	protected static class DefaultAction extends AbstractAction
	{
		JRootPane root;
		boolean press;

		DefaultAction(JRootPane root, boolean press)
		{
			this.root = root;
			this.press = press;
		}

		public void actionPerformed(ActionEvent e)
		{
			JButton owner = root.getDefaultButton();
			if (owner != null && SwingUtilities.getRootPane(owner) == root) {
				ButtonModel model = owner.getModel();
				if (press) {
					model.setArmed(true);
					model.setPressed(true);
				} else {
					model.setPressed(false);
				}
			}
		}

		public boolean isEnabled()
		{
			JButton owner = root.getDefaultButton();
			return (owner != null && owner.getModel().isEnabled());
		}
	}

}
