// GenericDomeTreeObject.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.swing.tree.DefaultGuiTreeObject;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * ViewSupport interface can be used to determine if object has children.
 * Icons in a DomeTree indicate whether pop-up guis are showing or not.
 */
public abstract class GenericDomeTreeObject extends DefaultGuiTreeObject
{

	public GenericDomeTreeObject(Object data, boolean allowsChildren)
	{
		super(data, allowsChildren);
	}

	public GenericDomeTreeObject(Object data, DArrayList children)
	{
		super(data, children);
	}

	public GenericDomeTreeObject(Object obj)
	{
		super(obj, (obj instanceof ViewSupport || obj instanceof MultiViewSupport));
		if (obj == null)
			throw new IllegalArgumentException("GenericDomeTreeObject - null Object");
		if (obj instanceof ViewSupport) {
			((ViewSupport) obj).addViewListener(new TreeObjectDListListener());
		}
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

	public Object getData() {
		return data;
	}
}
