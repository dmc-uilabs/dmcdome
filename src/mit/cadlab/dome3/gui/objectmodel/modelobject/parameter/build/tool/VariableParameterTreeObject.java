package mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.build.tool;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.gui.guiutils.tree.DomeTreeObject;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Sep 15, 2003
 * Time: 4:13:46 PM
 * To change this template use Options | File Templates.
 */
public class VariableParameterTreeObject extends DomeTreeObject
{
    boolean isInterfaceParameter;
	TreeObjectDListListener listDataListener = new TreeObjectDListListener();
	DataObject currentDataObj;

	public VariableParameterTreeObject(Parameter p)
	{
		super(p);
		currentDataObj = p.getCurrentDataObject();
		if (p.getScope() instanceof ToolInterface)
        {
            List copies = null;
            Collection objs = null;

            if(p.getScope() instanceof OptimizationInterfaceBuild)
            {
                OptimizationInterfaceBuild qmooInterface = (OptimizationInterfaceBuild) p.getScope();
                objs = qmooInterface.getVariablesFilter().getItems();
            }
            else
            {
                ToolInterface iface = (ToolInterface) p.getScope();
                objs = iface.getModelObjects();
            }
            copies = new ArrayList(objs);
            if (copies.contains(p))
            {
                isInterfaceParameter = true;
            }
        }
	}

	protected Icon getClosedIcon()
	{
        return DomeIcons.getIcon(DomeIcons.INTERFACE_PARAMETER);
	}

	protected Icon getOpenIcon()
	{
	    return DomeIcons.getIcon(DomeIcons.INTERFACE_PARAMETER_OPEN);
	}


}
