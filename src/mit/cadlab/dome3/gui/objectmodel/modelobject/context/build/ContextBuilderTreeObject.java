// ContextBuilderTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.context.build;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.ContextTreeObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;

public class ContextBuilderTreeObject extends ContextTreeObject
{

	public ContextBuilderTreeObject(DefaultContextBuilder c)
	{
		super(c);
	}

	protected void makeGui()
	{
		DefaultContextBuilder cBuilder = (DefaultContextBuilder) getDomeObject();
		gui = new DomeBuildFrame(new ContextBuildPanel(cBuilder));
	}

}
