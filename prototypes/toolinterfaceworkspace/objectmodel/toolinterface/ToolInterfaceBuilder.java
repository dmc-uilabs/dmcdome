package toolinterfaceworkspace.objectmodel.toolinterface;

import mit.cadlab.dome3.tool.ToolModel;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeSupport;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.ModelObjectBaseFactory;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DArrayList;
import org.dom4j.Element;
import org.dom4j.Document;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 10:57:18 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceBuilder extends ToolInterfaceBase
{
    public static final String TOOL_INTERFACE = "tool interface";

    private DArrayList _causalityList;

    public String getXmlTag()
    {
        return "";
    }

    protected Document createXmlDocument()
    {
        return null;
    }

    public void addXmlContent(Element xml) {}

    public TypeInfo getTypeInfo() {return null;}
    // just for now

    public void addViewListener(String viewName, DListListener l)
    {
    }

    public void removeViewListener(String viewName, DListListener l)
    {
    }

    protected CausalityManager createSystemCausalityManager(Element xmlElement)
    {
        return null;
    }

    protected CausalityManager createInternalCausalityManager(Element xmlElement)
    {
        return null;
    }

    public ToolInterfaceBuilder(Model m, Id id)
	{
		super(m, id);
        setName(TOOL_INTERFACE);

	}

    protected void createCausalityFilters()
    {
        _causalityList = new DArrayList();
		_causalityManager = new ToolInterfaceBuildCausalityManager();
		independentFilter = new SystemCausalityFilter(CausalityStatus.INDEPENDENT);
		intermediateFilter = new SystemCausalityFilter(CausalityStatus.INTERMEDIATE);
		resultFilter = new SystemCausalityFilter(CausalityStatus.RESULT);
    }

    protected class ToolInterfaceBuildCausalityManager
	        extends AbstractModelObjectScope.AbstractInternalCausalityManager
	{

		public ToolInterfaceBuildCausalityManager()
		{
			causalityChangeListeners = new CausalityChangeSupport(ToolInterfaceBuilder.this);
		}

		public ToolInterfaceBuildCausalityManager(Element xmlElement)
		{
			super(xmlElement);
		}

		protected CausalityStatus getInitialCausality(Object obj)
		{
			return getNewObjectCausality();
		}

		public void setData(Hashtable causal)
		{
			causalityLists.put(CausalityStatus.INDEPENDENT, new ArrayList()); // key = CausalityStatus, value=list of objects
			causalityLists.put(CausalityStatus.INTERMEDIATE, new ArrayList());
			causalityLists.put(CausalityStatus.RESULT, new ArrayList());
			causalityLists.put(CausalityStatus.INDETERMINATE, new ArrayList());
			for (Iterator iterator = causal.keySet().iterator(); iterator.hasNext();) {
				Parameter param = (Parameter) iterator.next();
				if ((causal.get(param)).equals(CausalityStatus.INDEPENDENT.toString())) {
					objectCausality.put(param, CausalityStatus.INDEPENDENT); // key=object; value=CausalityStatus
					((List) causalityLists.get(CausalityStatus.INDEPENDENT)).add(param);
				}
				else if ((causal.get(param)).equals(CausalityStatus.INTERMEDIATE.toString())) {
					objectCausality.put(param, CausalityStatus.INTERMEDIATE);
					((List) causalityLists.get(CausalityStatus.INTERMEDIATE)).add(param);
				}
				else if ((causal.get(param)).equals(CausalityStatus.RESULT.toString())) {
					objectCausality.put(param, CausalityStatus.RESULT);
					((List) causalityLists.get(CausalityStatus.RESULT)).add(param);
				}
				else if ((causal.get(param)).equals(CausalityStatus.INDETERMINATE.toString())) {
					objectCausality.put(param, CausalityStatus.INDETERMINATE);
					((List) causalityLists.get(CausalityStatus.INDETERMINATE)).add(param);
				}
			}
		}
	}

    public CausalityStatus getNewObjectCausality()
	{
		return newObjectCausality;
	}



    public void removeMappingsAndConnectionsBeforeDelete()
	{

	}

    public Collection removeItemsToFilterListener(DListListener l)
    {
        return null;
    }

    public Collection addItemsToFilterListener(DListListener l)
    {
    	_causalityList.addDListListener(l);
		return Collections.unmodifiableList(_causalityList);
	}

    public List getViewNames()
    {
        return viewNames;
    }
}
