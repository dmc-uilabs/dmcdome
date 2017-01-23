// ModelInterfaceBuildTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelinterface.build;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceTreeObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;

public class ModelInterfaceBuildTreeObject extends ModelInterfaceTreeObject
{

	public ModelInterfaceBuildTreeObject(ModelInterface mi)
	{
		super(mi);
	}

	protected void makeGui()
	{
		ModelInterface mi = (ModelInterface) getDomeObject();
		gui = new DomeBuildFrame(new ModelInterfaceBuildPanel(mi));
	}

}
