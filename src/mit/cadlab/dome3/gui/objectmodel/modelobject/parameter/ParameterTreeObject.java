// ParameterTreeObject.java
package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter;

import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.util.DArrayList;

import javax.swing.Icon;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParameterTreeObject extends DomeTreeObject
{

	boolean isRelationParameter;
	boolean isInterfaceParameter;
	boolean isSubscriptionParameter;
	TreeObjectDListListener listDataListener = new TreeObjectDListListener();


	public ParameterTreeObject(Parameter p)
	{
		super(p);
		isRelationParameter = (p.getScope() instanceof Relation);
		isSubscriptionParameter = (p.getScope() instanceof Subscription);
		if (p.getScope() instanceof ModelInterface)
        {
            ModelInterface iface = (ModelInterface) p.getScope();
            Collection objs = iface.getModelObjects();
            List copies = new ArrayList(objs);
            if (copies.contains(p))
            {
                isInterfaceParameter = true;
            }
        }
        if(p.getScope() instanceof ToolInterface)
        {
            ToolInterface tFace = (ToolInterface) p.getScope();
            Collection objs = tFace.getModelObjects();
            List copies = new ArrayList(objs);
            if(copies.contains(p))
            {
                isInterfaceParameter = true;
            }
        }
		if (isListDataParameter()) {
			DomeList dataObj = (DomeList) ((Parameter) data).getDataObjectForType("List");
			((DArrayList) dataObj.getValues()).addDListListener(listDataListener);
		}
		p.addPropertyChangeListener(Parameter.CURRENT_TYPE, new typeChangeListener());
	}

	public List getChildren()
	{
		if (isListDataParameter())
			return ((ViewSupport) (((Parameter) data).getDataObjectForType("List"))).getView();
		return super.getChildren();
	}

	// Tree support for ObjectTreeNode
	public boolean allowsChildren()
	{
		return isListDataParameter();
	}

	protected Icon getClosedIcon()
	{
		if (isRelationParameter)
			return DomeIcons.getIcon(DomeIcons.RELATION_PARAMETER);
		if (isSubscriptionParameter)
			return DomeIcons.getIcon(DomeIcons.SUBSCRIBE_PARAMETER);
		if (isInterfaceParameter)
			return DomeIcons.getIcon(DomeIcons.INTERFACE_PARAMETER);
		return DomeIcons.getIcon(DomeIcons.PARAMETER);
	}

	protected Icon getOpenIcon()
	{
		if (isRelationParameter)
			return DomeIcons.getIcon(DomeIcons.RELATION_PARAMETER_OPEN);
		if (isSubscriptionParameter)
			return DomeIcons.getIcon(DomeIcons.SUBSCRIBE_PARAMETER);
		if (isInterfaceParameter)
			return DomeIcons.getIcon(DomeIcons.INTERFACE_PARAMETER_OPEN);
		return DomeIcons.getIcon(DomeIcons.PARAMETER_OPEN);
	}


	public boolean isListDataParameter()
	{
		return (data instanceof Parameter) && (((Parameter) data).getDataObjectForType("List") != null);
	}

	protected class typeChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			DataObject currentDataObj = ((Parameter) getDomeObject()).getCurrentDataObject();
            if (currentDataObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList) {
				(currentDataObj.getValues()).clear();
				((DArrayList) currentDataObj.getValues()).removeDListListener(listDataListener);

			}
			currentDataObj = ((Parameter) data).getCurrentDataObject();
			if (currentDataObj instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList) {
				((DArrayList) currentDataObj.getValues()).addDListListener(listDataListener);
			}
			ParameterTreeObject.this.fireNodeStructureChanged();
		}
	}
}
