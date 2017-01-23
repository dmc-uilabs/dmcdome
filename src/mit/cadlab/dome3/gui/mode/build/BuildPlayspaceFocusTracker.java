// BuildFocusTracker.java
package mit.cadlab.dome3.gui.mode.build;

import mit.cadlab.dome3.gui.guiutils.msg.FocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;

import java.awt.Window;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class BuildPlayspaceFocusTracker
{

	protected static JComponent currentComponent = null;

	public static void notifyInFocus(JComponent comp)
	{
		currentComponent = comp;
	}

	public static void notifyFocusRemoved()
	{
		currentComponent = null;
	}

	public static JComponent getCurrentComponent()
	{
		return currentComponent;
	}

	public static Window getCurrentWindow()
	{
		JComponent comp = getCurrentComponent();
		if (comp == null) return null;
		return SwingUtilities.windowForComponent(comp);
	}

	public abstract static class BuildPlayspaceFocusTrackerAction extends AbstractAction
	{

		public BuildPlayspaceFocusTrackerAction(String name)
		{
			super(name);
		}

		public JComponent getCurrentComponent()
		{
			return BuildPlayspaceFocusTracker.getCurrentComponent();
		}

	}

}
