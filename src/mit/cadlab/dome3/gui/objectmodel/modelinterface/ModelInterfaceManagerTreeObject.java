//ModelInterfaceManagerTreeObject.java

package mit.cadlab.dome3.gui.objectmodel.modelinterface;

import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class ModelInterfaceManagerTreeObject extends DefaultTreeObject
{
	protected ModelInterfaceManager mi;

	public ModelInterfaceManagerTreeObject(ModelInterfaceManager mi)
	{
		super(mi, true);
		this.mi = mi;
		mi.addInterfacesListener(new TreeObjectDListListener());
	}

	public List getChildren()
	{
		return mi.getView();
	}

}
