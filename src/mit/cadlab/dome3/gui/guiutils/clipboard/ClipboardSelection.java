// ClipboardSelection.java
package mit.cadlab.dome3.gui.guiutils.clipboard;

import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;

import java.util.Collections;
import java.util.List;

/**
 * A ClipboardSelection is assumed to only contain ModelObjects
 * from the same Model.
 * This is an immutable class. It can not be changed after creation.
 * Objects remain in selection until deleted from model.
 * Duplicate objects are not permitted.
 * Empty ClipboardSelections are automatically removed from Clipboard.
 */
public class ClipboardSelection
{

	protected String name = "";
	protected SelectionsList items;
	protected Model model;
	protected Clipboard clipboard;
	protected DeletionListener deletionListener =
	        new DeletionListener()
	        {
		        public void objectDeleted(DeletionEvent e)
		        {
			        delete((ModelObject) e.getSource());
		        }
	        };

	public ClipboardSelection(Clipboard cb, int id, List items)
	{
		clipboard = cb;
		setName(Integer.toString(id));
		setItems(items);
	}

	public void addClipboardSelectionListener(DListListener l)
	{
		items.addDListListener(l);
	}

	public void removeClipboardSelectionListener(DListListener l)
	{
		items.removeDListListener(l);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (name == null) return;
		this.name = name;
	}

	public List getItems()
	{
		return Collections.unmodifiableList(items);
	}

	protected void setItems(List objs)
	{
		if (items != null) {
			throw new RuntimeException("ClipboardSelection.setItems can not be called twice!");
		}
		items = new SelectionsList();
		items.addAll(objs);
		if (objs.size() > 0)  {
			if(objs.get(0) instanceof ModelObject)
				model = ((ModelObject) objs.get(0)).getModel(); // items all from same model
		}
	}

	public Id getModelId()
	{
		if(model != null)
			return model.getId();
		else
			return null;
	}

	public boolean isFromModel(Id modelId)
	{
		Id id = getModelId();
		if(id != null)
			return id.equals(modelId);
		else return false;
	}

	public boolean contains(ModelObject obj)
	{
		return items.contains(obj);
	}

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	public void delete(ModelObject obj)
	{
		items.remove(obj);
		if (items.isEmpty()) {
			clipboard.removeSelection(this);
		}
	}

	public void deleteAll()
	{ // to be used by Clipboard *only*
		items.clear();
	}

	public String toString()
	{
		return items.toString();
	}

	class SelectionsList extends DArrayList
	{
		protected boolean addHookBefore(Object obj)
		{
			if (!(obj instanceof ModelObject) &&
			    !(obj instanceof ProjectResourceInfo) &&
			    !(obj instanceof ProjectIntegrationModelInfo) &&
			    !(obj instanceof BrowseInterface) &&
			    !(obj instanceof ModelInterface))
				return false;
			return !this.contains(obj);
		}

		protected void addHookAfter(Object obj)
		{
			if(obj instanceof ModelObject)
				((ModelObject) obj).addDeletionListener(deletionListener);
		}

		protected void removeHookAfter(Object obj)
		{
			if (obj instanceof ModelObject)
				((ModelObject) obj).removeDeletionListener(deletionListener);
		}

	}

}
