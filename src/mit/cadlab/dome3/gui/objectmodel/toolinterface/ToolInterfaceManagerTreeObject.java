package mit.cadlab.dome3.gui.objectmodel.toolinterface;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;

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
    protected AnalysisToolInterfaceManager mi;

        public ToolInterfaceManagerTreeObject(AnalysisToolInterfaceManager mi)
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
