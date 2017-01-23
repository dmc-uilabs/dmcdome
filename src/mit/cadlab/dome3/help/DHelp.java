// DHelp.java
package mit.cadlab.dome3.help;

import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * This class contains static functions for starting
 * DomeHelp at the command line and for associating DomeHelp
 * with Swing GUI components.
 * Use these functions to get DOME-specific behavior.
 * While this class can not be subclassed, similar functionality
 * can be obtained for other HelpSystems by copying and modifying
 * this class.
 */
public final class DHelp implements DHelpTopics
{

	public static final KeyStroke CSH_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK);
	public static final AbstractAction CSH_ACTION = new ContextSensitiveHelpAction("csHelp");

	static
	{
		DHelpUtils.setDContentViewerUI(); // open external URLs in web browser
	}

	public static final String HELPSET = "DomeHelp"; // .hs extension optional

	// The initial width and height of the JHelp
	private static Dimension HELP_SIZE = new Dimension(800, 600);
	private static HelpBroker domeHelpBroker;
	private static ActionListener displayHelpAfterTracking;

	/**
	 * Associates DomeHelp help topic with component.
	 */
	public static void enableHelp(Component comp, String id)
	{
		getHelpBroker().enableHelp(comp, id, null);
	}

	/**
	 * Opens DomeHelp and shows help topic specified when button clicked.
	 */
	public static void enableHelpOnButton(Component comp, String id)
	{
		getHelpBroker().enableHelpOnButton(comp, id, null);
	}

	/**
	 * Opens DomeHelp and shows help view specified when button clicked.
	 */
	public static void enableViewOnButton(Component comp, String viewName)
	{
		if (!(comp instanceof AbstractButton) && !(comp instanceof Button)) {
			throw new IllegalArgumentException("Invalid Component");
		}
		if (comp instanceof AbstractButton) {
			((AbstractButton) comp).addActionListener(new DisplayViewOnButton(viewName));
		} else if (comp instanceof Button) {
			((Button) comp).addActionListener(new DisplayViewOnButton(viewName));
		}
	}

	/**
	 * Activates context sensitive DomeHelp when button clicked.
	 */
	public static void enableContextSensitiveHelpOnButton(Component comp)
	{
		if (!(comp instanceof AbstractButton) && !(comp instanceof Button)) {
			throw new IllegalArgumentException("Invalid Component");
		}
		if (comp instanceof AbstractButton) {
			((AbstractButton) comp).addActionListener(getDisplayHelpAfterTracking());
		} else if (comp instanceof Button) {
			((Button) comp).addActionListener(getDisplayHelpAfterTracking());
		}
	}

	protected static ActionListener getDisplayHelpAfterTracking()
	{
		if (displayHelpAfterTracking == null) {
			displayHelpAfterTracking = new CSH.DisplayHelpAfterTracking(getHelpBroker());
		}
		return displayHelpAfterTracking;
	}

	public static class ContextSensitiveHelpAction extends AbstractAction
	{
		public ContextSensitiveHelpAction(String name)
		{
			super(name);
		}

		public void actionPerformed(ActionEvent event)
		{
			getDisplayHelpAfterTracking().actionPerformed(event);
		}
	}

	/**
	 * @return a HelpBroker with DomeHelp helpset.
	 */
	public static HelpBroker getHelpBroker()
	{
		if (domeHelpBroker == null) {
			domeHelpBroker = DHelpUtils.getHelpBroker(HELPSET);
			if (domeHelpBroker != null)
				domeHelpBroker.setSize(HELP_SIZE);
		}
		return domeHelpBroker;
	}

	/**
	 * @return a HelpFrame with DomeHelp in it; null if errors.
	 */
	public static DHelpUtils.HelpFrame getHelpFrame()
	{
		DHelpUtils.HelpFrame hf = DHelpUtils.getHelpFrame(HELPSET);
		if (hf != null) {
			hf.getJHelp().setPreferredSize(HELP_SIZE);
			hf.pack();
		}
		return hf;
	}

	/**
	 * Show DomeHelp.
	 */
	public static void main(String args[])
	{
		String version = System.getProperty("java.version");
		if (version.startsWith("1.0")) {
			System.out.println("!!!WARNING: JavaHelp & Swing must be run" +
			                   "with JDK 1.1.2 or higher version VM!!!");
			System.exit(1);
		}

		try { // use system look and feel
			String laf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(laf);
		} catch (Exception ex) {
			ex.getMessage();
		}

		DHelpUtils.HelpFrame helpFrame = getHelpFrame();
		if (helpFrame == null) System.exit(1);
		helpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		helpFrame.setVisible(true);
	}

	private static class DisplayViewOnButton extends DHelpUtils.DisplayView
	{
		public DisplayViewOnButton(String view)
		{
			super(getHelpBroker(), view);
		}
	}

}
