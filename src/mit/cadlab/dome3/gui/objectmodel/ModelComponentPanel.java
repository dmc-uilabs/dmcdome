// DomeObjectBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;

import javax.swing.JPanel;

public abstract class ModelComponentPanel extends JPanel
        implements ModelComponentGui
{

	protected ModelComponent mComp;
	protected String title;

	public ModelComponentPanel(ModelComponent mComp, String title)
	{
		this.mComp = mComp;
		this.title = title;
	}

	public ModelComponent getModelComponent()
	{
		return mComp;
	}

	public Object getGuiObject()
	{
		return getModelComponent();
	}

	public String getTitle()
	{
		return title;
	}

	public void close()
	{
		// default, do nothing
	}

}
