package toolinterfaceworkspace.objectmodel.toolinterface;

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
import mit.cadlab.dome3.objectmodel.model.dome.AbstractDomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeListener;
import mit.cadlab.dome3.objectmodel.util.mapping.MappingChangeEvent;
import mit.cadlab.dome3.objectmodel.util.causality.*;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.id.UUIDGenerator;
import mit.cadlab.dome3.util.log.Log;
import mit.cadlab.dome3.util.log.LogHandler;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 22, 2003
 * Time: 7:57:47 AM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractToolInterface extends AbstractModelObjectScope
                                                implements ToolInterface,
                                                                Filterable
{
    protected Model model;
	protected Version modelVersion = null;
	protected Version version = new Version(0, 0, 1);
	protected Version lastSavedVersion = new Version(0, 0, 0);
	protected String lastSavedXml = "";
	protected boolean isValidated = false;
	protected CausalityManager internalCausalityManager;
	protected LogHandler logHandler = Log.getDefaultLog(); // default, no logging

    protected transient CausalityStatus newObjectCausality = CausalityStatus.INDEPENDENT; // default

    protected Filter independentFilter, intermediateFilter, resultFilter;

    protected CausalityManager _causalityManager;

    protected Context _buildViewContext;

    protected InterfaceToolView _toolView;

	public AbstractToolInterface(Model m, Id id)
	{
		super(id);
		setModel(m);
		internalCausalityManager = createInternalCausalityManager();
        _buildViewContext = createBuildContext();
        modelObjects.add(_buildViewContext);
        createCausalityFilters();
        Id toolviewId = new Id(UUIDGenerator.create());
        _toolView = new InterfaceToolView(this, toolviewId);

    }

	public AbstractToolInterface(Model m, Id id, ModelObjectScope mObjScope)
	{
		this(m, id, mObjScope, true);
	}

	public AbstractToolInterface(Model m, Id id, ModelObjectScope mObjScope, boolean copyObjects)
	{
		super(id, mObjScope, copyObjects);
		setModel(m);
		// objects and mappings copied if same model
		// objects only if different model (currently not allowed by gui)
		internalCausalityManager = createInternalCausalityManager();
	}

	public AbstractToolInterface(Model m, Element xmlElement)
	{
		super(xmlElement);
		setModel(m);
		// create causality manager
		Element causalityElement = (Element) xmlElement.selectSingleNode("causality");
		if (causalityElement != null)
			internalCausalityManager = createInternalCausalityManager(xmlElement);
		else
			internalCausalityManager = createInternalCausalityManager();

		// init
		parseInterfaceInfoElement((Element) xmlElement.selectSingleNode("/modelinterface/interfaceinfo"));
	}

    protected Context createBuildContext()
	{
		Object[] ctrParams = new Object[]{this, BUILD_CONTEXT_ID};
		Context cxt = (Context) getModelObjectFactory().newInstance("Context", ctrParams);
		if (cxt == null)
			throw new DomeObjectException("AbstractDomeModelIterface: createBuildContext failed");
		cxt.setName(BUILD_VIEW);
		return cxt;
	}

    protected void createCausalityFilters()
	{
        independentFilter = new SystemCausalityFilter(CausalityStatus.INDEPENDENT);
		intermediateFilter = new SystemCausalityFilter(CausalityStatus.INTERMEDIATE);
		resultFilter = new SystemCausalityFilter(CausalityStatus.RESULT);
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
			addListToFilter(AbstractToolInterface.this);
		}

		public SystemCausalityFilter(CausalityStatus cs)
		{
			super(AbstractToolInterface.this.getModel(),
			      AbstractToolInterface.this.getNextId(), true);
			causality = cs;
			setName(cs.toString());
			//arguemnt has to be a Filterable
			addListToFilter(AbstractToolInterface.this);
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
			if (getModel() != null) {
                if (AbstractToolInterface.this instanceof ToolInterfaceBuilder) {
                    ToolInterfaceBuilder build = (ToolInterfaceBuilder) AbstractToolInterface.this;
                    return true;//!filteredItems.contains(obj) && build.isItemOfSystemCausality(obj, causality);
                    //return !filteredItems.contains(obj) && getModel().isItemOfCausality(obj, causality);
                } else
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

    protected abstract CausalityManager createInternalCausalityManager();

	protected abstract CausalityManager createInternalCausalityManager(Element xmlElement);

	public CausalityManager getCausalityManager()
	{
		return new ImmutableCausalityManager(internalCausalityManager);
	}

	// ModelInterface interface
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
		if (model != null) {
			return model.isValidModelObjectType(modelObjectType);
		} else {
			return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter")
			        || modelObjectType.equals("Context") || modelObjectType.equals("Procedural Relation");
		}
	}

	public boolean isValidModelObjectType(ModelObject modelObject)
	{
		if (model != null) {
			return model.isValidModelObjectType(modelObject);
		} else {
			String modelObjectType = modelObject.getTypeName();
			return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter")
			        || modelObjectType.equals("Context") || modelObjectType.equals("Procedural Relation");
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
            if (m instanceof AbstractDomeModel)
            {
                mappingManager = ((AbstractDomeModel) m).getMappingManager();
            }
            mapListener = new InterfaceMappingChangesListener();
            mappingManager.addMappingChangeListener(mapListener);
        }

        public ToolInterfaceInternalCausalityManager(Element xmlElement)
        {
            super(xmlElement);
            Model m = getModel();
            if (m instanceof AbstractDomeModel)
            {
                mappingManager = ((AbstractDomeModel) m).getMappingManager();
            }
            mapListener = new InterfaceMappingChangesListener();
            mappingManager.addMappingChangeListener(mapListener);
        }

        protected CausalityStatus getInitialCausality(Object obj)
        {
//            return getNewObjectCausality();
            return null;
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
            if (!(p.getScope() instanceof ToolInterface))
                return;
            CausalityStatus newCS = CausalityStatus.INDETERMINATE;
            Collection mappedParams = mappingManager.getMappingsForParameter(p);
            if (mappedParams.isEmpty())
            {
                newCS = CausalityStatus.INDEPENDENT;
            }
            else if (mappedParams.size() == 1)
            {
                Parameter modelParam = (Parameter) mappedParams.iterator().next();
                ModelObjectScope scope = modelParam.getScope();
                CausalityStatus mpCS = null;
                if (scope instanceof Model)
                {
                    Model m = (Model) scope;
                    mpCS = m.getCausality(modelParam);
                }
                else
                {
                    Model m = scope.getModel();
                    mpCS = m.getCausality(modelParam);
                }
                if (CausalityStatus.INDEPENDENT.equals(mpCS))
                {
                    newCS = CausalityStatus.INDEPENDENT;
                }
                else if (CausalityStatus.RESULT.equals(mpCS))
                {
                    newCS = CausalityStatus.RESULT;
                }
                else if (CausalityStatus.INTERMEDIATE.equals(mpCS))
                {
                    newCS = CausalityStatus.INTERMEDIATE;
                }
                else
                {
                    newCS = CausalityStatus.INDETERMINATE; // what to do here?
                }
            }
            else
            { // more than one mappedParams
                boolean drivenByOutput = false;
                boolean drivingInput = false;
                Iterator it = mappedParams.iterator();
                while (it.hasNext())
                {
                    Parameter modelParam = (Parameter) it.next();
                    Model mod = (Model) modelParam.getScope();
                    CausalityStatus mpCS = mod.getCausality(modelParam);
                    if (CausalityStatus.INDEPENDENT.equals(mpCS))
                    {
                        drivingInput = true;
                    }
                    else if (CausalityStatus.RESULT.equals(mpCS) || CausalityStatus.INTERMEDIATE.equals(mpCS))
                    {
                        drivenByOutput = true;
                    }
                }
                if (drivingInput && drivenByOutput)
                    newCS = CausalityStatus.INTERMEDIATE;
                else if (drivingInput)
                    newCS = CausalityStatus.INDEPENDENT;
                else if (drivenByOutput)
                { // impossible to be driven by more than one item!
                    throw new IllegalStateException("found parameter driven by more than one item: " + Names.getNameId(p) +
                            " --> " + Names.getNameIds(mappedParams));
                }
                else
                { // mapped but not driven or driving
                    newCS = CausalityStatus.INDEPENDENT;
                }
            }
            changeCausality(p, newCS); // should we do this if it's null?
        }
    }

	// ModelObject interface

	public Model getModel()
	{
		return model;
	}

	private void setModel(Model model)
	{
		if (this.model != null)
			throw new AbstractDomeObject.DomeObjectException("setModel", "can not change model of ModelObject!");
		this.model = model;
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
}
