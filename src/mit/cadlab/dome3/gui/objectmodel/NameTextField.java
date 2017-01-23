// NameTextField.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.Templates;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class NameTextField extends DTextField
{

	protected static final String NAME_PROPERTY = "name";
	protected DomeObject dObj;
	protected PropertyChangeListener nameListener;

	public NameTextField()
	{
		super();
		_nameFieldConfigure();
	}

	public NameTextField(int columns)
	{
		super(columns);
		_nameFieldConfigure();
	}

	private void _nameFieldConfigure()
	{
		nameListener = new NameListener()
		{
			public void nameChanged(String newName)
			{
				setText(newName);
				setCurrent();
			}
		};

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// name change
				dObj.setName(getText());
			}
		});
		Templates.formatTextField(this);
	}

	public void setDomeObject(DomeObject domeObject)
	{
		if (dObj != null)
			dObj.removePropertyChangeListener(NameListener.NAME, nameListener);
		dObj = domeObject;
		dObj.addPropertyChangeListener(NameListener.NAME, nameListener);
		setText(dObj.getName());
		setCurrent();
	}

}
