// UnitComboBoxModel.java
package mit.cadlab.dome3.gui.guiutils.customGui;

import mit.cadlab.dome3.swing.DListModel;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;

public class CustomGUIComboBoxModel extends DefaultComboBoxModel implements DListModel
{

	public static final String DEFAULT = "Dome Default";
	public static final String CHANGE_ADD = "add...";
	public static final String CHANGE_DEL = "remove...";
	public static final String CHANGE_EDIT = "edit...";
	protected boolean isBuildMode = false;
	ModelInterface mInterface;
    ToolInterface _tInterface;

	public CustomGUIComboBoxModel(ModelInterface _mInterface, boolean isBuildMode)
	{
		super(_mInterface.getCustomGUIList().toArray());
		this.mInterface = _mInterface;
		insertElementAt(DEFAULT, 0);
		this.isBuildMode = isBuildMode;
		if (isBuildMode) {
			addElement(CHANGE_ADD);
			addElement(CHANGE_EDIT);
//			addElement(CHANGE_DEL);
		}
		this.setSelectedItem(DEFAULT);
	}

    public CustomGUIComboBoxModel(ToolInterface tInterface, boolean isBuildMode)
    {
        super(tInterface.getCustomGUIList().toArray());
        _tInterface = tInterface;
        insertElementAt(DEFAULT, 0);
        this.isBuildMode = isBuildMode;
        if (isBuildMode)
        {
            addElement(CHANGE_ADD);
            addElement(CHANGE_EDIT);
        }

        setSelectedItem(DEFAULT);
    }

	public Icon getIcon(int index)
	{
		return null;
	}

	public String getListText(int index)
	{
		Object obj = getElementAt(index);
		if (index == -1) // get selected item
			obj = getSelectedItem();
		if (obj == null) return " ";
//		if (obj == CHANGE_ADD || obj == CHANGE_DEL ||obj == CHANGE_EDIT ||obj == DEFAULT)
				if (obj == CHANGE_ADD ||obj == CHANGE_EDIT ||obj == DEFAULT)
			return obj.toString();
		CustomGuiInfo fileInfo = (CustomGuiInfo) obj;
		return fileInfo.getShortName();
	}

}
