// BooleanBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.gui.objectmodel.dataobject.BooleanBasePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooleanBuildPanel extends BooleanBasePanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("BooleanBuildPanel");
	public static final String XML_TAG = "booleanbuildpanel";

	public BooleanBuildPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean bool)
	{
		super(bool);
	}

	protected void configureComponents()
	{
		valueComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setModelData();
			}
		});
	}

}
