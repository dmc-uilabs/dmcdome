// DefaultGuiTreeObject.java
package mit.cadlab.dome3.swing.tree;

import mit.cadlab.dome3.util.DArrayList;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DefaultGuiTreeObject extends DefaultTreeObject
        implements GuiTreeObject
{
	// Default implementation supports pop-up context guis
	// which are windows (either JFrame or JDialog).
	protected Window gui = null;

	public DefaultGuiTreeObject(Object data)
	{ // leaf
		super(data);
	}

	public DefaultGuiTreeObject(Object data, boolean allowsChildren)
	{
		super(data, allowsChildren);
	}

	public DefaultGuiTreeObject(Object data, DArrayList children)
	{
		super(data, children);
	}

	// override this method
	protected void makeGui()
	{
		// assign gui variable here
	}

	// Support for pop-up context guis
	public boolean showGui()
	{
		if (gui == null) { // gui does not exist
			makeGui();
			if (gui != null) {
				gui.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						hideGui();
					}
				});
				gui.show();
				return true;
			} else { // no gui for object
				return false;
			}
		} else { // gui exists and is showing
			// bring into focus & open up?
			if (gui instanceof Frame && ((Frame) gui).getState() == Frame.ICONIFIED) {
				((Frame) gui).setState(Frame.NORMAL);
			} else {
				gui.toFront(); // make sure it is showing
			}
			return false;
		}
	}

	public boolean hideGui()
	{
		if (gui == null) { // gui does not exist
			return false;
		} else { // gui exists and is showing
			gui.hide();
			gui.dispose();
			gui = null;
			return true;
		}
	}

}
