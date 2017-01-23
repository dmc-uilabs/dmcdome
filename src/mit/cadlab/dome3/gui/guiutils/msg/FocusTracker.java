// FocusTracker.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.util.log.Log;

import javax.swing.JComponent;

/**
 * This interface is for components to register that they
 * are the active instance of their type
 * java.awt.Window has internal window tracking which is
 * limited to package-only access.
 */
public class FocusTracker
{

	protected JComponent currentComponent;
	protected Model currentModel;

	public void notifyInFocus(JComponent comp, Model model)
	{
		if (comp == null || model == null)
			throw new NullPointerException("FocusTracker.notifyInFocus");
		currentComponent = comp;
		if (model != null && !model.equals(currentModel))
			Log.setCurrentLog(model.getLogHandler());
		currentModel = model;
	}

	public void notifyInFocus(JComponent comp, ModelComponent mComp)
	{
		if (comp == null || mComp == null)
			throw new NullPointerException("FocusTracker.notifyInFocus");
		currentComponent = comp;
		Model newModel = mComp.getModel();
		if (newModel != null && !newModel.equals(currentModel))
			Log.setCurrentLog(newModel.getLogHandler());
		else if (mComp instanceof ModelInterface) {
			Log.setCurrentLog(((ModelInterface) mComp).getLogHandler());
		}
		currentModel = newModel;
	}

	public void notifyFocusRemoved()
	{
		currentComponent = null;
		currentModel = null;
	}

	public Model getCurrentModel()
	{
		return currentModel;
	}

	public JComponent getCurrentComponent()
	{
		return currentComponent;
	}

}
