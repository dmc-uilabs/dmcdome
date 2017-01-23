// RunParameterTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.run;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.run.ParameterRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.ParameterTreeObject;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter;
import mit.cadlab.dome3.swing.WindowTracker;

public class RunParameterTreeObject extends ParameterTreeObject
{

	public RunParameterTreeObject(AbstractParameter p)
	{
		super(p);
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		AbstractParameter parameter = (AbstractParameter) getDomeObject();
		gui = new DomeRunFrame(new ParameterRunPanel(parameter), parent);
	}

}
