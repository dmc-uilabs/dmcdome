package toolinterfaceworkspace.gui.objectmodel.toolinterface;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import toolinterfaceworkspace.objectmodel.toolinterface.manager.ToolInterfaceManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 24, 2003
 * Time: 3:50:55 PM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceManagerTreeObject extends DefaultTreeObject
{
    protected ToolInterfaceManager mi;

        public ToolInterfaceManagerTreeObject(ToolInterfaceManager mi)
        {
            super(mi, true);
            this.mi = mi;
            mi.addInterfacesListener(new TreeObjectDListListener());
        }

        public List getChildren()
        {
            return mi.getView();
        }

}
