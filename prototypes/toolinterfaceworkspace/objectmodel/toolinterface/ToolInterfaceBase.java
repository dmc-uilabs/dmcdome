package toolinterfaceworkspace.objectmodel.toolinterface;

import mit.cadlab.dome3.objectmodel.*;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.model.Model;
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
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.id.UUIDGenerator;
import mit.cadlab.dome3.util.DArrayList;

import java.util.*;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 10:58:16 AM
 * To change this template use Options | File Templates.
 */
public abstract class ToolInterfaceBase extends AbstractToolInterface
{


    public String contentToString() {return "";}
    public static final String FILENAME = "fileName";
    public static final String CONTEXT = "Context";

    protected DArrayList modelObjects = new DArrayList();

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

    protected CausalityManager internalCausalityManager;

    public ToolInterfaceBase(Model m, Id id)
    {
        super(m, id);
        createViews();
    }

    protected void createViews()
    {
        // create build view
        views.put(BUILD_VIEW, Collections.unmodifiableList(getBuildContext().getModelObjectReferences()));

        // create run view
        views.put(RUN_VIEW, Collections.EMPTY_LIST);

        // create tool causality view
        List toolCausalityView = new ArrayList();
        toolCausalityView.add(independentFilter);
        toolCausalityView.add(intermediateFilter);
        toolCausalityView.add(resultFilter);
        views.put(CAUSALITY_VIEW, Collections.unmodifiableList(toolCausalityView));
    }

    private void setModel(Model model)
	{
		if (this.model != null)
			throw new AbstractDomeObject.DomeObjectException("setModel", "can not change model of ModelObject!");
		this.model = model;
	}

    protected CausalityManager createInternalCausalityManager()
	{
		return new ToolInterfaceInternalCausalityManager();
	}



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

    public List getView(String viewName)
    {
        if (viewNames.contains(viewName))
        {
            List view = (List) views.get(viewName);
            return (view == null) ? Collections.EMPTY_LIST : view;
        }
        return Collections.EMPTY_LIST;
    }


    public Context getBuildContext()
	{
		return (Context) getModelObjectById(BUILD_CONTEXT_ID);
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

    public void delete(DeletionListener dListener) {}

    public void addDeletionListener(DeletionListener dListener) {}

    public void deleteModelObjects(Collection c)
    {

    }

    public void deleteAllModelObjects() {}

    public DomeObject getDomeObject()
    {
        return null;
    }

    public Id getId()
    {
        return new Id("id");
    }

    public Collection newModelObjects(Collection c) {return null;}

    public Collection newModelObjects(Collection c, boolean flag) { return null;}

    public void removeDeletionListener(DeletionListener l){}

    public ModelObject newModelObject(String model) { return null; }

    public List getValidModelObjectTypes() {return null;}

    public String getTypeName() {return "";}

    public boolean isSaved() { return false; }



}
