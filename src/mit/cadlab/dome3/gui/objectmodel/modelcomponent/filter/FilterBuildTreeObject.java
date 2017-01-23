// FilterBuildTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;


public class FilterBuildTreeObject extends FilterTreeObject
{

	public FilterBuildTreeObject(Filter f)
	{
		super(f);
	}

	protected void makeGui()
	{
		Filter filter = (Filter) getDomeObject();
		gui = new DomeBuildFrame(new FilterBuildPanel(filter));
	}

}
