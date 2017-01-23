package mit.cadlab.dome3.objectmodel.toolinterface;

import mit.cadlab.dome3.objectmodel.*;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeSupport;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.util.*;
import java.io.IOException;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 10:58:16 AM
 * To change this template use Options | File Templates.
 */
public abstract class AnalysisToolInterfaceBase extends AbstractAnalysisToolInterface
{


    public String contentToString() {return "";}
    public static final String FILENAME = "fileName";
    public static final String CONTEXT = "Context";

    protected ModelObjectFactory m_moFactory;

    protected String fileName = "";
    protected boolean shouldSave = true;
    protected HashMap views = new HashMap(); // keyed by view name
    protected int currentInsertionIndex = -1;
    protected List currentItems = Collections.EMPTY_LIST;
    public String currentView = ToolInterface.BUILD_VIEW;

    protected Set distinctModelObjs = new HashSet();

    //For view change in tree
    protected DArrayList viewList = new DArrayList();
    protected boolean isviewchangeOperation;
    protected boolean isviewListLoaded;

    private DArrayList _causalityList;


    public AnalysisToolInterfaceBase(Model m, Id id)
    {
        super(m, id);
    }

    public AnalysisToolInterfaceBase(ToolInterface ti, Id id)
    {
        super(ti, id);
    }

    public AnalysisToolInterfaceBase(Model m, Element xmlContent)
    {
        super(m, xmlContent);
        loadXmlElement(xmlContent);
    }

    public AnalysisToolInterfaceBase(Model m, Id id, ModelObjectScope mObjScope)
    {
        super(m, id, mObjScope);
    }

    public AnalysisToolInterfaceBase(Model m, Element xmlContent, Element xmlMappings)
    {
        super(m, xmlContent);

        loadXmlElement(xmlContent);

        Element mappings = null;

        if(xmlMappings != null)
            mappings = (Element) xmlMappings.selectSingleNode("/" + getXmlTag() + "/mappingsandintermediates/mappings/interfacemappings");

        if (mappings != null)
        {
            if (m instanceof AnalysisTool)
            {
                ((AnalysisTool) m).getMappingManager().addMappings(this, mappings);
            }
        }

        createViews();
    }

    protected abstract void createViews();

    protected CausalityManager createInternalCausalityManager()
	{
		return new ToolInterfaceInternalCausalityManager();
	}

    public abstract void loadXmlElement(Element xmlContent);

    public Set getDistinctModelObjects()
    {
        return distinctModelObjs;
    }

    public void setCurrentView(String view)
    {
        this.currentView = view;
    }

    public String getCurrentView()
    {
        return currentView;
    }


    public ModelObjectFactory getModelObjectFactory()
	{
		if (m_moFactory == null)
			m_moFactory = new ModelObjectBaseFactory();
		return m_moFactory;
	}

    public Context getBuildContext()
	{
		return (Context) getModelObjectById(BUILD_CONTEXT_ID);
	}

    public List getView()
    {
        return (List)views.get(INTERFACE_CAUSALITY_VIEW);
    }

    public Document createXmlDocument()
    {
        Document doc = DocumentFactory.getInstance().createDocument();
        Element xmlElement = toXmlElement();
        doc.add(xmlElement);
        return doc;
    }

    public String getTypeName()
    {
        return ToolInterface.TYPE_INFO.getTypeName();
    }

    protected class ToolInterfaceBuildCausalityManager
	        extends AbstractModelObjectScope.AbstractInternalCausalityManager
	{

		public ToolInterfaceBuildCausalityManager()
		{
			causalityChangeListeners = new CausalityChangeSupport(AnalysisToolInterfaceBase.this);
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

		}
	}

    public Collection addItemsToFilterListener(DListListener l)
    {
    	modelObjects.addDListListener(l);
		return Collections.unmodifiableList(modelObjects);
	}

    protected void createCausalityFilters()
    {
        super.createCausalityFilters();

        _causalityList = new DArrayList();
		_causalityManager = new ToolInterfaceBuildCausalityManager();
    }

    public CausalityStatus getNewObjectCausality()
	{
		return newObjectCausality;
	}

    public List getViewNames()
    {
        return viewNames;
    }

    protected void save(Document xmlDoc, String fileName) throws IOException
	{
		XMLUtils.writeToFile(xmlDoc, fileName);
	}

    public void removeMappingsAndConnectionsBeforeDelete()
	{
		ConnectionMappingManager mgr = ((AnalysisTool)_model).getMappingManager();
		for (Iterator i = modelObjects.iterator(); i.hasNext();)
        {
            Object obj = i.next();
            if (obj instanceof Parameter)
            {
                //this will remove all param mappings
                //there are no relations in build or causal views
                mgr.removeAllMappings((Parameter) obj);
            }
        }

	}

    public Collection getModelObjectParameters()
    {
        Collection coll = new ArrayList(modelObjects.size() - 1);
        for (Iterator i = modelObjects.iterator(); i.hasNext();)
        {
            ModelObject mobj = (ModelObject) i.next();
            if (mobj instanceof Parameter)
            {
                coll.add(mobj);
            }
        }
        return Collections.unmodifiableCollection(coll);
    }

    public String getRegistryKey()
    {
        return "";
    }

    public boolean isValidModelObjectType()
    {
        return false;
    }

    public Version getVersion()
    {
        return null;
    }

    public void validate() {}

    public boolean isValidated()
    {
        return false;
    }


    public void addDeletionListener(DeletionListener dListener) {}

    public void deleteAllModelObjects() {}


    public void removeDeletionListener(DeletionListener l){}

    public List getValidModelObjectTypes() {return null;}



    public boolean isSaved() { return false; }

    public void addViewListener(DListListener l)
    {
    }

    public void removeViewListener(DListListener l)
    {
    }


}
