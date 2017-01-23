package mit.cadlab.dome3.objectmodel.toolinterface.manager;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.AbstractOptimizationToolInterfaceRuntime;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.network.CompoundId;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 10:50:01 AM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolInterfaceManager extends AbstractDomeObject
                                        implements ModelComponent, ViewSupport
{
    public static final TypeInfo TYPE_INFO = new TypeInfo("AnalysisToolInterfaceManager");
    public static final String XML_TAG = "toolinterfaces";
    protected static final String slash = System.getProperty("file.separator");

    protected Model model;
    protected DArrayList _interfaces;
    protected HashMap interfacesById; // key=id; value=interface
    protected String _toolFileName = null;
    protected HashMap interfaceMappingFileMap;

    public AnalysisToolInterfaceManager(Id id, Model model)
    {
        super(id);
        this.model = model;
        _interfaces = new InterfacesList();
        interfacesById = new HashMap();
    }

    public Collection getInterfaces()
    {
        return Collections.unmodifiableCollection(_interfaces);
    }

    public boolean isEmpty()
    {
        return _interfaces.isEmpty();
    }

    public int countInterfaces()
    {
        return _interfaces.size();
    }

    public ToolInterface getById(String id)
    {
        return (ToolInterface) interfacesById.get(id);
    }

    public ToolInterface getInterface(int index)
    {
        return (ToolInterface) _interfaces.get(index);
    }

    public Model getModel()
    {
        return model;
    }

    public void addInterfacesListener(DListListener l)
    {
        _interfaces.addDListListener(l);
    }

    public void removeInterfacesListener(DListListener l)
    {
        _interfaces.removeDListListener(l);
    }

    class InterfacesList extends DArrayList
    {
        protected boolean addHookBefore(Object obj)
        {
            return !contains(obj) && (obj instanceof ToolInterface);
        }

        protected void addHookAfter(Object obj)
        {
            interfacesById.put(((ToolInterface) obj).getId().getIdString(), obj);
	        if (obj instanceof mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.AbstractOptimizationToolInterfaceRuntime) {
		        CompoundId cId = ((mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.AbstractOptimizationToolInterfaceRuntime) obj).getRuntimeId();
		        interfacesById.put(cId.getInterfaceRuntimeId(), obj);
		        String staticId = cId.getInterfaceStaticId();
		        if (staticId == null)
			        System.err.println("AnalysisToolInterfaceManager warning - no static id for " + Names.getName(obj));
		        else
			        interfacesById.put(staticId, obj);
	        }
        }

        protected void removeHookAfter(Object obj)
        {
            interfacesById.remove(((ToolInterface) obj).getId().getIdString());
	        if (obj instanceof mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.AbstractOptimizationToolInterfaceRuntime) {
		        CompoundId cId = ((mit.cadlab.dome3.objectmodel.toolinterface.optimization.run.AbstractOptimizationToolInterfaceRuntime) obj).getRuntimeId();
		        interfacesById.remove(cId.getInterfaceRuntimeId());
		        interfacesById.remove(cId.getInterfaceStaticId());
	        }
        }
    }

    // ViewSupport interface
    public List getView()
    {
        return Collections.unmodifiableList(_interfaces);
    }

    public void addViewListener(DListListener l)
    {
        _interfaces.addDListListener(l);
    }

    public void removeViewListener(DListListener l)
    {
        _interfaces.removeDListListener(l);
    }

    public boolean hasChanged()
    {
        for (Iterator iter = _interfaces.iterator(); iter.hasNext();)
        {
//            AnalysisToolInterfaceBuild iface = (AnalysisToolInterfaceBuild) iter.next();
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
