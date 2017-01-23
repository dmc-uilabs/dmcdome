package mit.cadlab.dome3.objectmodel.toolinterface;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.ViewSupport;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractPropertyChangeFilter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.AbstractFilterFunction;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AbstractAnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.causality.*;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.util.log.Log;
import mit.cadlab.dome3.util.log.LogHandler;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 22, 2003
 * Time: 7:57:47 AM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractAnalysisToolInterface extends AbstractModelObjectScope
                                                implements ToolInterface,
                                                                Filterable
{
    protected Model _model;
	protected Version modelVersion = null;
	protected Version version = new Version(0, 0, 1);
	protected Version lastSavedVersion = new Version(0, 0, 0);
	protected String lastSavedXml = "";
	protected boolean isValidated = false;
	protected CausalityManager _internalCausalityManager;
	protected LogHandler logHandler = Log.getDefaultLog(); // default, no logging

    protected transient CausalityStatus newObjectCausality = CausalityStatus.INDEPENDENT; // default

    protected Filter _independentFilter, _intermediateFilter, _resultFilter;

    protected CausalityManager _causalityManager;

    protected Context _buildViewContext;

    protected ArrayList _customGUIList = new ArrayList();
    public static final String CUSTOMGUICHANGE = "custom gui changed";

    //when the relation parameter in the model changes its causality to intermediate, we
    //do not want to show those parameters in the interface.  So we hold those parameters
    //in the following list.  so that the parameter can be brought back in very easily.
    protected List _tempModelObjects = new ArrayList();
    //key - intermediate object id, value - intermediate object
    protected HashMap _tempModelObjectsMap = new HashMap();


	public AbstractAnalysisToolInterface(Model m, Id id)
	{
		super(id);
		setModel(m);
		_internalCausalityManager = createInternalCausalityManager();
        _buildViewContext = createBuildContext();
        modelObjects.add(_buildViewContext);
        createCausalityFilters();
    }

    public AbstractAnalysisToolInterface(ToolInterface ti, Id id)
    {
        super(id);
        setModel(ti.getModel());
        _internalCausalityManager = createInternalCausalityManager();
        _buildViewContext = createBuildContext();
        modelObjects.add(_buildViewContext);
        createCausalityFilters();
    }

	public AbstractAnalysisToolInterface(Model m, Id id, ModelObjectScope mObjScope)
	{
		this(m, id, mObjScope, false);
	}

	public AbstractAnalysisToolInterface(Model m, Id id, ModelObjectScope mObjScope, boolean copyObjects)
	{
		super(id, mObjScope, copyObjects);
		setModel(m);
		_internalCausalityManager = createInternalCausalityManager();
        _buildViewContext = createBuildContext();
        modelObjects.add(_buildViewContext);
        createCausalityFilters();
	}

	public AbstractAnalysisToolInterface(Model m, Element xmlElement)
	{
		super(xmlElement);
		setModel(m);
        parseInterfaceInfoElement((Element) xmlElement.selectSingleNode("/modelinterface/interfaceinfo"));
        createCausalityFilters();
	}

    protected Context createBuildContext()
	{
		Object[] ctrParams = new Object[]{this, BUILD_CONTEXT_ID};
		Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
		if (cxt == null)
			throw new DomeObjectException("AbstractToolIterface: createBuildContext failed");
		cxt.setName(BUILD_VIEW);
		return cxt;
	}

    protected void createCausalityFilters()
	{
        _independentFilter = new SystemCausalityFilter(CausalityStatus.INDEPENDENT);
		_intermediateFilter = new SystemCausalityFilter(CausalityStatus.INTERMEDIATE);
		_resultFilter = new SystemCausalityFilter(CausalityStatus.RESULT);
	}

	protected class SystemCausalityFilter extends AbstractPropertyChangeFilter implements ViewSupport
	{
		private CausalityStatus causality;

		//for run mode client - model is null
		public SystemCausalityFilter(Model m, CausalityStatus cs)
		{
			super(m, getNextId(), true);
			causality = cs;
			setName(cs.toString());
			//arguemnt has to be a Filterable
//			addListToFilter(AbstractAnalysisToolInterface.this);
		}

		public SystemCausalityFilter(CausalityStatus cs)
		{
			super(AbstractAnalysisToolInterface.this.getModel(),
			      AbstractAnalysisToolInterface.this.getNextId(), true);
			causality = cs;
			setName(cs.toString());
			//arguemnt has to be a Filterable
//			addListToFilter(AbstractAnalysisToolInterface.this);
		}

		protected AbstractPropertyChangeFilter.PropertyChangeFilterFunction createFilterFunction()
		{
			return new CausalityFilterFunction("Causality Filter");
		}

		protected void processCausalityChange(CausalityChangeEvent event)
		{
			CausalityStatus oldCS = event.getOldCausalityStatus();
			CausalityStatus newCS = event.getNewCausalityStatus();
			if (oldCS == newCS) return;
			if (causality.equals(newCS)) {
				filteredItems.add(event.getParameter());
			} else if (causality.equals(oldCS)) {
				filteredItems.remove(event.getParameter());
			}
		}

		protected boolean keepInFilter(Object obj)
		{
			if (getModel() != null)
            {
                if (AbstractAnalysisToolInterface.this instanceof AnalysisToolInterfaceBase)
                {
                    AnalysisToolInterfaceBase build = (AnalysisToolInterfaceBase) AbstractAnalysisToolInterface.this;
                    return true;
                }
                else
                    return false;
            }
            else
                return false;
		}

		protected class CausalityFilterFunction extends AbstractFilterFunction
		        implements AbstractPropertyChangeFilter.PropertyChangeFilterFunction
		{
			CausalityChangeListener ccListener;

			public CausalityFilterFunction(String name)
			{
				super(name);
				ccListener = new CausalityFilterCausalityChangeListener();
			}

			public boolean keepInFilter(Object obj)
			{
				return SystemCausalityFilter.this.keepInFilter(obj);
			}

			public void addListenerTo(Object obj)
			{
				if (getModel() != null)
                {
                    getModel().addCausalityChangeListener(obj, ccListener);
                }

			}

            public void removeListenerFrom(Object obj)
            {
                if (getModel() != null)
                {
                    getModel().removeCausalityChangeListener(obj, ccListener);
                }
            }

			protected class CausalityFilterCausalityChangeListener implements CausalityChangeListener
			{
				public void causalityChanged(CausalityChangeEvent event)
				{
					processCausalityChange(event);
				}
			}
		}
    }

    public Element createMappingElement()
	{
		Element topElement = DocumentHelper.createElement("mappingsandintermediates");
		Element mapElement = DocumentHelper.createElement("mappings");

		// add mappings
		ConnectionMappingManager mgr = ((AnalysisTool) _model).getMappingManager();

        Element mapSubElements = mgr.toXmlElement(this, "interfacemappings");

        if (mapSubElements != null)
        {
            mapElement.add(mapSubElements);
        }

        topElement.add(mapElement);
		return topElement;
	}

    protected abstract CausalityManager createInternalCausalityManager();

	protected abstract CausalityManager createInternalCausalityManager(Element xmlElement);

    protected abstract void copyModelObjects(ToolInterface iface);

	public CausalityManager getCausalityManager()
	{
		return new ImmutableCausalityManager(_internalCausalityManager);
	}

	public LogHandler getLogHandler()
	{
		return logHandler;
	}

	public void setLogHandler(LogHandler log)
	{
		if (log == null)
			throw new AbstractDomeObject.DomeObjectException("AbstractModelInterface: setLogHandler", "null log handler");
		this.logHandler = log;
	}

	// CausalitySupport interface

	public List getItems(CausalityStatus causality)
	{
		return getCausalityManager().getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return getCausalityManager().isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return getCausalityManager().getCausality(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(obj, l);
	}

	public boolean isValidModelObjectType(String modelObjectType)
	{
		if (_model != null) {
			return _model.isValidModelObjectType(modelObjectType);
		} else {
			return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter")
			        || modelObjectType.equals("Context");
		}
	}

	public boolean isValidModelObjectType(ModelObject modelObject)
	{
		if (_model != null) {
			return _model.isValidModelObjectType(modelObject);
		} else {
			String modelObjectType = modelObject.getTypeName();
			return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter")
			        || modelObjectType.equals("Context");
		}
	}

    protected class ToolInterfaceInternalCausalityManager
            extends AbstractModelObjectScope.AbstractInternalCausalityManager
    {
        protected MappingChangeListener mapListener;
        protected ConnectionMappingManager mappingManager;

        public ToolInterfaceInternalCausalityManager()
        {
            Model m = getModel();
            if (m instanceof AbstractAnalysisTool)
            {
                mappingManager = ((AbstractAnalysisTool) m).getMappingManager();
            }
            mapListener = new InterfaceMappingChangesListener();
            mappingManager.addMappingChangeListener(mapListener);
        }

        public ToolInterfaceInternalCausalityManager(Element xmlElement)
        {
            super(xmlElement);
            Model m = getModel();
            if (m instanceof AbstractAnalysisTool)
            {
                mappingManager = ((AbstractAnalysisTool) m).getMappingManager();
            }
        }

        protected CausalityStatus getInitialCausality(Object obj)
        {
            return getNewObjectCausality();
        }

        /**
         * listens to all mapping changes for model parameters
         */
        class InterfaceMappingChangesListener implements MappingChangeListener
        {
            public void mappingChanged(MappingChangeEvent event)
            {
                processMappingChange(event.getParameter());
            }
        }

        private void processMappingChange(Parameter p)
        {

        }
    }

    public CausalityStatus getNewObjectCausality()
	{
		return newObjectCausality;
	}

	// ModelObject interface

	public Model getModel()
	{
		return _model;
	}

	private void setModel(Model model)
	{
		if (_model != null)
			throw new AbstractDomeObject.DomeObjectException("setModel", "can not change model of ModelObject!");
		_model = model;
	}

	public TypeInfo getTypeInfo()
	{
		return ToolInterface.TYPE_INFO;
	}

	public Version getModelVersion()
	{
		return modelVersion;
	}

	public Version getVersion()
	{
		return version;
	}

	public void validate()
	{
		// TODO: implement this
	}

	public boolean isValidated()
	{
		return isValidated;
	}

	public boolean isSaved()
	{
		return hasChanged();
	}

	public boolean hasChanged()
	{
		Document xmlDoc = createXmlDocumentWithoutViews();
		return hasChanged(xmlDoc);
	}

	protected boolean hasChanged(Document xmlDoc)
	{
		String newXml = xmlDoc.asXML();
		if (lastSavedXml.equals(newXml))
			return false;
		return true;
	}

	public void updateSavedStatus()
	{
		lastSavedXml = createXmlDocumentWithoutViews().asXML();
	}

	protected Document createXmlDocumentWithoutViews()
	{
		Document doc = createXmlDocument();
		Element root = doc.getRootElement();
		Node viewsNode = root.selectSingleNode("/" + getXmlTag() + "/" + ToolInterface.VIEWS);
		root.remove(viewsNode);
		return doc;
	}

	public String getXmlTag()
	{
		return ToolInterface.XML_TAG;
	}

    protected abstract Document createXmlDocument();

	protected Element createInterfaceInfoElement()
	{
		Element xml = DocumentHelper.createElement("interfaceinfo");
		xml.add(version.toXmlElement());
		return xml;
	}

	protected void parseInterfaceInfoElement(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml interface info");
		Element versionElement = (Element) xmlElement.selectSingleNode("version");
		if (versionElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml version: " + xmlElement.asXML());
		version = new Version(versionElement);

	}

	protected void addXmlContent(Element xmlElement)
	{
		xmlElement.add(createInterfaceInfoElement());
	}

	protected String contentToString()
	{
		return "  version: " + version.toString();
	}

    protected void changeCausality(Object obj, CausalityStatus cause)
	{
		((ToolInterfaceInternalCausalityManager) _internalCausalityManager).changeCausality(
		        obj, cause);
	}

    public ArrayList getCustomGUIList()
	{
		return _customGUIList;
	}

    public void addCustomGui(CustomGuiInfo cInfo)
	{
		_customGUIList.add(cInfo);
		firePropertyChange(CUSTOMGUICHANGE, null, cInfo);
	}

    public void removeCustomGui(CustomGuiInfo cInfo)
    {
        _customGUIList.remove(cInfo);
        firePropertyChange(CUSTOMGUICHANGE, cInfo, null);
    }

    public void editCustomGui(CustomGuiInfo oldC, CustomGuiInfo newC)
    {
        oldC.setClassName(newC.getClassName());
        oldC.setJarFilePath(newC.getJarFilePath());
        oldC.setShortName(newC.getShortName());
        firePropertyChange(CUSTOMGUICHANGE, oldC, newC);
    }


}
