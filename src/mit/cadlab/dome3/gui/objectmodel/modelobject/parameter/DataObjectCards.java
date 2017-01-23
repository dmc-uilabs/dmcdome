// DataObjectCards.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter;

import com.sun.java.CardLayout2;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DataObjectPanel;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class DataObjectCards extends JPanel
{

	protected CardLayout2 layout;
	protected int mode;

	public DataObjectCards(int mode)
	{
		layout = new CardLayout2();
		setLayout(layout);
		this.mode = mode; // assume correct mode (0,1,2)
	}

	public String getCurrentObject()
	{
		return layout.getActiveName();
	}

	public void setDataObjects(List dataObjects, String currentType)
	{
		Vector newNames = new Vector();
		for (int i = 0; i < dataObjects.size(); ++i) {
			newNames.add(((DataObject) dataObjects.get(i)).getTypeName());
		}
		// remove names not in new list
		Enumeration names = layout.names();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (!newNames.contains(name))
				layout.remove(this, name);
		}
		// add new panels
		for (int i = 0; i < dataObjects.size(); ++i) {
			if (!layout.containsName(((DataObject) dataObjects.get(i)).getTypeName()))
				addDataObject((DataObject) dataObjects.get(i));
		}

		layout.show(this, currentType);
	}

//    public void addDataObject(String typeName) {
//        if (!layout.containsName(typeName)) {
//            add(createDataObjectPanel(typeName),typeName);
//            // add listeners? return panel?
//        }
//    }

	public void addDataObject(DataObject data)
	{
		String typeName = data.getTypeName();
		if (!layout.containsName(typeName)) {
			DataObjectPanel dataGui = createDataObjectPanel(data);
			if (dataGui == null)
				return;
			add(dataGui, typeName);
			// add listeners? return panel?
		}
	}

    /**
     * sangmok: release reference to data object of each DataObjectPanel
     * DataObjectCards contains many DataObjectPanel instances which have reference to data object instance.
     * those references should be removed when Panel closes.
     * close() method of ParameterBasePanel is called when window closes.
     * this method will be invoked in that close() method
     */
    public void releaseDataObjectReferenceOfDataObjectPanel() {
//        Component[] components = getComponents();
//
//        /* iterate through components in this DataObjectCards and release the data object if it is DataObjectPanel */
//        for (int i = 0; i < components.length; i++) {
//            if (components[i] instanceof DataObjectPanel) {
//                System.out.println("removing the reference of " + components[i].getClass().getName());
//                ((DataObjectPanel) components[i]).setDataObject(null);
//            }
//        }
    }

	protected DataObjectPanel createDataObjectPanel(DataObject dObj)
	{
		Constructor ctr;
		switch (mode) {
			case 0:
				ctr = Registry.getConstructor(getBuildGUIClass(dObj), Registry.BUILD_GUI);
				break;
			default:
				ctr = Registry.getConstructor(getRunGUIClass(dObj), Registry.RUN_GUI);
		}
		if (ctr == null)
			return null;
		try {
			return (DataObjectPanel) ctr.newInstance(new Object[]{dObj});
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}


	private String getBuildGUIClass(DataObject dObj)
	{
		if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.IterationVariable) {
			return "IterationVariableBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger) {
			return "IntegerBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal) {
			return "RealBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean) {
			return "BooleanBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString) {
			return "StringBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText) {
			return "TextBuildPanel";
		} else if (dObj instanceof Documentation) {
			return "DocumentationBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile) {
			return "FileBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector) {
			return "VectorBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix) {
			return "MatrixBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomePreference) {
			return "PreferenceBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration) {
			return "EnumerationBuildPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList) {
			return "ListBuildPanel";
		} else
			return "";
	}

	private String getRunGUIClass(DataObject dObj)
	{
		if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.IterationVariable) {
			return "IterationVariableRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger) {
			return "IntegerRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal) {
			return "RealRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean) {
			return "BooleanRundPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString) {
			return "StringRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText) {
			return "TextRunPanel";
		} else if (dObj instanceof Documentation) {
			return "DocumentationRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile) {
			return "FileRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector) {
			return "VectorRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix) {
			return "MatrixRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomePreference) {
			return "PreferenceRunPanel";
		} else if (dObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration) {
			return "EnumerationRunPanel";
		} else
			return "";
	}

	public void removeType(String typeName)
	{
		layout.remove(this, typeName);
	}

	public void showType(String typeName)
	{
		layout.show(this, typeName);
	}

}
