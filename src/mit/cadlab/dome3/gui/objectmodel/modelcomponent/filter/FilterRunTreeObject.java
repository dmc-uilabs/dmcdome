// FilterRunTreeObject.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 11, 2003
 * Time: 2:41:06 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.swing.WindowTracker;

public class FilterRunTreeObject extends FilterTreeObject
{

	public FilterRunTreeObject(Filter f)
	{
		super(f);
	}

	protected void makeGui()
	{
		WindowTracker parent = RunMode.getCurrentWindowTracker();
		Filter filter = (Filter) getDomeObject();
		gui = new DomeRunFrame(new FilterRunPanel(filter), parent);
	}

}
