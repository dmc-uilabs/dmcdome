// DataTypeComboBoxModel.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter;

import javax.swing.DefaultComboBoxModel;

public class DataTypeComboBoxModel extends DefaultComboBoxModel
{

	public static final String CHANGE_LIST = "change type...";
	protected boolean isBuildMode = false;

	public DataTypeComboBoxModel(String type, boolean isBuildMode)
	{
		this(new String[]{type}, isBuildMode);
	}

	public DataTypeComboBoxModel(String[] types, boolean isBuildMode)
	{
		this(types, types[0], isBuildMode);
	}

	public DataTypeComboBoxModel(String[] types, String selectedDataType, boolean isBuildMode)
	{
		super(types);
		this.isBuildMode = isBuildMode;
		if (isBuildMode)
			addElement(CHANGE_LIST);
		setSelectedItem(selectedDataType);
	}

}
