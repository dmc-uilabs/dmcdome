// DComboBox.java
package mit.cadlab.dome3.swing;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * Subclass of <code>JComboBox</code>
 * fix bug 4203915 (setModel resets selected index to 0)
 *  which is fixed in Java 1.4+
 * added method setSelectedObject
 *  which sets selected item without triggering action event
 * Customized ListCellRenderer:
 *   support DListModel; set icon and text based on object
 */
public class DComboBox extends JComboBox
{

	protected boolean settingModel = false;
	protected boolean shouldFireActionEvent = true;

	public DComboBox()
	{
		super();
		setRenderer(new DListCellRenderer());
	}

	public DComboBox(ComboBoxModel model)
	{
		super(model);
		setRenderer(new DListCellRenderer());
	}

	public DComboBox(Object[] items)
	{
		super(items);
		setRenderer(new DListCellRenderer());
	}

	public DComboBox(Vector items)
	{
		super(items);
		setRenderer(new DListCellRenderer());
	}

	public synchronized void setModel(ComboBoxModel model)
	{
		settingModel = true;
		super.setModel(model);
		settingModel = false;
	}

	public synchronized void setSelectedObject(Object obj)
	{
		shouldFireActionEvent = false;
		super.setSelectedItem(obj);
		shouldFireActionEvent = true;
	}

	public synchronized void setSelectedItem(Object obj)
	{
		if (!settingModel)
			super.setSelectedItem(obj);
	}

	protected void fireActionEvent()
	{
		if (shouldFireActionEvent)
			super.fireActionEvent();
	}

}
