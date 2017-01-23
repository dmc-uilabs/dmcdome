// DomeTreeObject.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.objectmodel.*;
import mit.cadlab.dome3.swing.tree.DefaultGuiTreeObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;

/**
 * Every DomeTreeObject must represent a DomeObject instance.
 * ViewSupport interface is used to determine if object has children.
 * Icons in a DomeTree indicate whether pop-up guis are showing or not.
 */
public abstract class DomeTreeObject extends DefaultGuiTreeObject
{

	public DomeTreeObject(DomeObject dObj)
	{
		super(dObj, (dObj instanceof ViewSupport || dObj instanceof MultiViewSupport));
		if (dObj == null)
			throw new IllegalArgumentException("DomeTreeObject - null DomeObject");
		dObj.addPropertyChangeListener(DomeObject.NAME, new DomeObjectNameListener());
		dObj.addDeletionListener(new DomeObjectDeletionListener());
	}

	public DomeObject getDomeObject()
	{
		return (DomeObject) data;
	}

	// Tree support for ObjectTreeNode
	public String getTreeValue()
	{
		return getDomeObject().getName();
	}

	public void setTreeValue(String value)
	{
		getDomeObject().setName(value);
	}

	public List getChildren()
	{
		if (data instanceof ViewSupport) {
			return ((ViewSupport) data).getView();
		}
		return Collections.EMPTY_LIST;
	}

	public List getChildren(String view)
	{
		if (data instanceof MultiViewSupport) {
			return ((MultiViewSupport) data).getView(view);
		}
		return Collections.EMPTY_LIST;
	}

	public Icon getIcon(int type)
	{
		if (gui == null)
			return getClosedIcon();
		else
			return getOpenIcon();
	}

	// implement these methods
	protected abstract Icon getClosedIcon();

	protected abstract Icon getOpenIcon();

	// repaint tree node to show different icon when
	// guis are shown or hidden
	public boolean showGui()
	{
		boolean statusChange = super.showGui();
		if (statusChange)
			fireNodeValueChanged();
		return statusChange;
	}

	public boolean hideGui()
	{
		boolean statusChange = super.hideGui();
		if (statusChange)
			fireNodeValueChanged();
		return statusChange;
	}

	protected class DomeObjectNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(DomeObject.NAME)) {
				fireNodeValueChanged();
			} else {
				System.out.println("DomeObjectTreeObject: " + property + " change to " + newValue);
			}
		}
	}

	protected class DomeObjectDeletionListener implements DeletionListener
	{
		public void objectDeleted(DeletionEvent e)
		{
			hideGui();
		}
	}

}
