// ModelObjectsComboBoxListModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.DListModel;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public class ModelObjectsComboBoxListModel extends DefaultComboBoxModel
        implements DListModel
{
	protected ModelObjectNameListener nameListener = new ModelObjectNameListener();

	public ModelObjectsComboBoxListModel()
	{
	}

	public ModelObjectsComboBoxListModel(Object items[])
	{
		for (int i = 0; i < items.length; i++)
			addElement(items[i]);
	}

	public ModelObjectsComboBoxListModel(Collection items)
	{
		Iterator mObjs = items.iterator();
		while (mObjs.hasNext())
			addElement(mObjs.next());
	}

	public void destroy()
	{
		int numberItems = getSize();
		for (int i = 0; i < numberItems; ++i)
			((DomeObject) getElementAt(i)).removePropertyChangeListener("name", nameListener);

	}

	public void addElement(Object obj)
	{
		if (obj instanceof DomeObject) {
			super.addElement(obj);
			((DomeObject) obj).addPropertyChangeListener("name", nameListener);
		}
	}

	public void removeElement(Object obj)
	{
		if (obj instanceof DomeObject) {
			super.removeElement(obj);
			((DomeObject) obj).removePropertyChangeListener("name", nameListener);
		}
	}

	public Icon getIcon(int index)
	{
		return null;
	}

	public String getListText(int index)
	{
		Object obj;
		if (index == -1)
			obj = getSelectedItem();
		else
			obj = getElementAt(index);
		if (obj == null) return " ";
		return ((DomeObject) obj).getName();
	}

	class ModelObjectNameListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			if (property.equals("name")) {
				int index = getIndexOf(e.getSource());
				fireContentsChanged(this, index, index);
			}
		}
	}

}