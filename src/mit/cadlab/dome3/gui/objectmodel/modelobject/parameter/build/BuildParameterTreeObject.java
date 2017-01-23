// BuildParameterTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.ParameterTreeObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.AbstractParameter;

public class BuildParameterTreeObject extends ParameterTreeObject
{

	public BuildParameterTreeObject(AbstractParameter p)
	{
		super(p);
	}

	protected void makeGui()
	{
		AbstractParameter parameter = (AbstractParameter) getDomeObject();
		gui = new DomeBuildFrame(new ParameterBuildPanel(parameter));
	}

}
