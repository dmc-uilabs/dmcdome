package toolinterfaceworkspace.objectmodel.toolinterface.manager;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.tool.ToolModel;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;

import java.util.*;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 10:50:01 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceManager extends AbstractDomeObject
                                        implements ModelComponent, ViewSupport
{
    public static final TypeInfo TYPE_INFO = new TypeInfo("ToolInterfaceManager");
    public static final String XML_TAG = "toolinterfaces";
    protected static final String slash = System.getProperty("file.separator");

    protected Model model;
    protected DArrayList interfaces;
    protected HashMap interfacesById; // key=id; value=interface
    protected String toolFileName = null;
    protected HashMap interfaceMappingFileMap;

    public ToolInterfaceManager(Id id, ToolModel model)
    {
        super(id);
        this.model = model;
        interfaces = new InterfacesList();
        interfacesById = new HashMap();
    }

    public Collection getInterfaces()
    {
        return Collections.unmodifiableCollection(interfaces);
    }

    public boolean isEmpty()
    {
        return interfaces.isEmpty();
    }

    public int countInterfaces()
    {
        return interfaces.size();
    }

    public ToolInterface getById(Id id)
    {
        return (ToolInterface) interfacesById.get(id);
    }

    public ToolInterface getInterface(int index)
    {
        return (ToolInterface) interfaces.get(index);
    }

    public Model getModel()
    {
        return model;
    }

    public void addInterfacesListener(DListListener l)
    {
        interfaces.addDListListener(l);
    }

    public void removeInterfacesListener(DListListener l)
    {
        interfaces.removeDListListener(l);
    }

    class InterfacesList extends DArrayList
    {
        protected boolean addHookBefore(Object obj)
        {
            return !contains(obj) && (obj instanceof ToolInterface);
        }

        protected void addHookAfter(Object obj)
        {
            interfacesById.put(((ToolInterface) obj).getId(), obj);
        }

        protected void removeHookAfter(Object obj)
        {
            interfacesById.remove(((ToolInterface) obj).getId());
        }
    }

    // ViewSupport interface
    public List getView()
    {
        return Collections.unmodifiableList(interfaces);
    }

    public void addViewListener(DListListener l)
    {
        interfaces.addDListListener(l);
    }

    public void removeViewListener(DListListener l)
    {
        interfaces.removeDListListener(l);
    }

    public boolean hasChanged()
    {
        for (Iterator iter = interfaces.iterator(); iter.hasNext();)
        {
//            ToolInterfaceBuilder iface = (ToolInterfaceBuilder) iter.next();
//            if (iface.hasChanged())
                return true;
        }
        return false;
    }

    public TypeInfo getTypeInfo()
    {
        return TYPE_INFO;
    }

    public String getXmlTag()
    {
        return XML_TAG;
    }

}
