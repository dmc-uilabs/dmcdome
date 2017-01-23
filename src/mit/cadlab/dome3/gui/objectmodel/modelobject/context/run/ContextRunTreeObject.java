//ContextRunTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.run;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.ContextTreeObject;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContext;
import mit.cadlab.dome3.swing.WindowTracker;

public class ContextRunTreeObject extends ContextTreeObject
{

	public ContextRunTreeObject(DefaultContext c)
	{
		super(c);
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		DefaultContext context = (DefaultContext) getDomeObject();
		gui = new DomeRunFrame(new ContextRunPanel(context), parent);
	}
}