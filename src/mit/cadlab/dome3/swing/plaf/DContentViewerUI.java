// DContentViewerUI.java
package mit.cadlab.dome3.swing.plaf;

import edu.stanford.ejalbert.BrowserLauncher;

import java.io.IOException;
import java.net.URL;
import javax.help.JHelpContentViewer;
import javax.help.plaf.basic.BasicContentViewerUI;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class DContentViewerUI extends BasicContentViewerUI
{
	public static boolean debug = false;

	public static ComponentUI createUI(JComponent x)
	{
		return new DContentViewerUI((JHelpContentViewer) x);
	}

	public DContentViewerUI(JHelpContentViewer b)
	{
		super(b);
	}

	/**
	 * Modified to open external URLs using system web browser.
	 */
	protected void linkActivated(URL u)
	{
		if (debug)
			System.out.println(u.getProtocol() + "\t" + u);
		if (u.getProtocol().equals("http"))
			try {
				BrowserLauncher.openURL(u.toString());
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}
		else
			super.linkActivated(u);
	}

}
