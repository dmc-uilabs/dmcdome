package mit.cadlab.dome3.gui.serverPanel;

import mit.cadlab.dome3.swing.DListModel;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;

/**
 * This ComboBoxModel works with entries of the form (name,value) where
 * name is to be displayed in the combobox and value is to be returned by
 * the method getSelectedValue().
 * Entries must be added to the model via addEntry(name,value).
 * The convential model modification methods (addElement,insertElementAt) have
 *   been overridden to throw exceptions.
 */
public class ServerPanelComboBoxModel extends DefaultComboBoxModel implements DListModel
{

	public ServerPanelComboBoxModel()
	{

	}

	// following two methods allow users to add entries to the combo box model
	// and retrieve the selected value

	public void addEntry(String name, String value)
	{
		Object obj = new ComboBoxEntry(name, value);
		super.addElement(obj);
		setSelectedItem(obj);
	}

	public String getSelectedValue()
	{
		Object obj = getSelectedItem();
		if (obj != null && obj instanceof ComboBoxEntry)
			return ((ComboBoxEntry) getSelectedItem()).getEntryValue();
		return "";
	}

	public boolean setSelectionByValue(String value)
	{
		ComboBoxEntry c;
		for (int i = 0; i < getSize(); i++) {
			c = (ComboBoxEntry) getElementAt(i);
			if (c.getEntryValue().equals(value)) {
				setSelectedItem(c);
				return true;
			}
		}
		return false;
	}
	// override the following two methods so that users can not modify the model
	// via these methods. addEntry must be used to add elements to this model.

	public void addElement(Object anObject)
	{
		throw new UnsupportedOperationException();
	}

	public void insertElementAt(Object anObject, int index)
	{
		throw new UnsupportedOperationException();
	}

	// DListModel interface used to specify custom icon/text for each entry in
	// the combo box model.

	public Icon getIcon(int index)
	{
		return null;
	}

	public String getListText(int index)
	{
		ComboBoxEntry entry;
		if (index == -1)
			entry = (ComboBoxEntry) getSelectedItem();
		else
			entry = (ComboBoxEntry) getElementAt(index);
		if (entry == null)
			return "";
		else
			return entry.getEntryName();
	}

	// represents the data in this combo box model

	public static class ComboBoxEntry
	{
		String entryName;
		String entryValue;

		public ComboBoxEntry(String entryName, String entryValue)
		{
			this.entryName = entryName;
			this.entryValue = entryValue;
		}

		public String getEntryName()
		{
			return entryName;
		}

		public String getEntryValue()
		{
			return entryValue;
		}

		public String toString()
		{
			return entryName;
		}
	}
}
