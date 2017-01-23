// Clipboard.java
package mit.cadlab.dome3.gui.guiutils.clipboard;

import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A Clipboard is a collection of ClipboardSelections.
 * New ClipboardSelections are added to the front of the Clipboard.
 */
public class Clipboard
{

	protected static int MAX_LIMIT = 50;
	protected static int DEFAULT_LIMIT = 10;
	protected int nextSelectionId = 1;
	protected int maxSelections;
	protected DArrayList selections;

	public Clipboard()
	{
		this(DEFAULT_LIMIT);
	}

	public Clipboard(int maxSelections)
	{
		selections = new SelectionsList();
		setMaxSelections(maxSelections);
	}

	public void addClipboardListener(DListListener l)
	{
		selections.addDListListener(l);
	}

	public void removeClipboardListener(DListListener l)
	{
		selections.removeDListListener(l);
	}

	public boolean isEmpty()
	{
		return selections.isEmpty();
	}

	public int getMaxSelections()
	{
		return maxSelections;
	}

	public void setMaxSelections(int newMaxSelections)
	{
		if (newMaxSelections < 0 || newMaxSelections > MAX_LIMIT)
			newMaxSelections = DEFAULT_LIMIT;
		maxSelections = newMaxSelections;
		if (maxSelections < selections.size()) { // trim selections
			selections.removeRange(maxSelections, selections.size() - 1); // notifies listeners
		}
	}

	protected int getNextSelectionId()
	{
		if (nextSelectionId % 1000 == 0) {
			nextSelectionId = 1;
		}
		return nextSelectionId++;
	}

	public void addSelection(List items)
	{
		if (selections.size() == maxSelections) {
			selections.remove(selections.size() - 1); // last element
		}
		selections.add(0, new ClipboardSelection(this, getNextSelectionId(), items));
	}

	public void removeSelection(ClipboardSelection sel)
	{
		selections.remove(sel);
	}

	public void removeSelections(ClipboardSelection[] sels)
	{
		selections.removeAll(sels);
	}

	public void deleteItem(ModelObject obj)
	{
		Id modelId = obj.getModel().getId();
		for (int i = selections.size() - 1; i >= 0; --i) {
			ClipboardSelection sel = (ClipboardSelection) selections.get(i);
			if (sel.isFromModel(modelId))
				sel.delete(obj);
		}
	}

	public void deleteModel(Id modelId)
	{
		for (int i = selections.size() - 1; i >= 0; --i) {
			ClipboardSelection sel = (ClipboardSelection) selections.get(i);
			if (sel.isFromModel(modelId))
				selections.remove(i);
		}
	}

	public ClipboardSelection getLastSelection()
	{
		if (selections.isEmpty()) return null;
		return (ClipboardSelection) selections.get(0);
	}

	public List getSelections()
	{
		return Collections.unmodifiableList(selections);
	}

	// support Clipboard editing
	public void empty()
	{
		selections.clear();
	}

	public String toString()
	{
		if (selections.isEmpty()) return "Empty Clipboard";
		Iterator it = selections.iterator();
		StringBuffer sb = new StringBuffer("Clipboard:\n  " + it.next().toString());
		while (it.hasNext()) {
			sb.append("\n  " + it.next().toString());
		}
		return sb.toString();
	}

	class SelectionsList extends DArrayList
	{
		protected boolean addHookBefore(Object obj)
		{
			return !((ClipboardSelection) obj).isEmpty();
		}

		protected void removeHookAfter(Object obj)
		{
			((ClipboardSelection) obj).deleteAll();
		}
	}

}
