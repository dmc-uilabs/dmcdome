// AbstractDomeObjectGui.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.objectmodel.DomeObject;

import javax.swing.JPanel;

/**
 * To do by subclasses:
 * public String getTitlePrefix();
 * public String getHelpContext();
 * public void setMenuContext();
 */
public abstract class AbstractDomeObjectGui extends JPanel
        implements DomeObjectGui
{

	private DomeObject domeObject;

	public AbstractDomeObjectGui(DomeObject dObj)
	{
		this.domeObject = dObj;
	}

	public DomeObject getDomeObject()
	{
		return domeObject;
	}

	public Object getGuiObject()
	{
		return getDomeObject();
	}

	public String getTitle()
	{
		return getTitlePrefix() + getDomeObject().getName();
	}

	public void close()
	{
		// default, do nothing
	}

}
