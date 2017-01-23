package mit.cadlab.dome3.objectmodel.model.tool.optimization.build;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.OptimizationToolBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerBuild;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.ObjectiveParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.tool.optimization.VariableParameter;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.build.AnalysisToolInterfaceManagerBuild;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jacob
 * Date: Jun 6, 2003
 * Time: 2:02:58 PM
 * To change this template use Options | File Templates.
 */
public class OptimizationToolBuild
            extends OptimizationToolBase
{
    public static final String VARIABLE_VIEW = "Design Variable View";
    public static final String OBJECTIVE_VIEW = "Design Objective View";

	public static final Id VARIABLE_CONTEXT_ID = new Id("Design Variable Context Id");
	public static final Id OBJECTIVE_CONTEXT_ID = new Id("Design Objective Context Id");

    public static final List _qmooViewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{TOOL_PROJECT_VIEW, VARIABLE_VIEW, OBJECTIVE_VIEW}));

    private String _currentView = OptimizationToolBuild.TOOL_PROJECT_VIEW;

	public OptimizationToolBuild(Id id)
    {
        super(id);
    }

    public OptimizationToolBuild(Id id, AnalysisTool model)
	{
		super(id, model);
    }

    public OptimizationToolBuild(String id)
	{
		super(id);
	    createModelContext();
    }

    public OptimizationToolBuild(String file, Element xml)
    {
        super(file, xml);
        loadInterfaces(file);
    }

    public OptimizationToolBuild(Element xml)
    {
        super(xml);
    }

    public void setView(String viewType)
    {
        this._currentView = viewType;
    }

    public Context getDesignVariableContext()
    {
        return _designVariableObjects;
    }

    public Context getDesignObjectiveContext()
    {
        return _designObjectiveObjects;
    }

    protected void createModelContext()
	{
		this._designVariableObjects = new DefaultContextBuilder(this, VARIABLE_CONTEXT_ID);
		this._designObjectiveObjects = new DefaultContextBuilder(this, OBJECTIVE_CONTEXT_ID);
	}

    protected ConnectionMappingManager createConnectionMappingManager()
    {
        return new ConnectionMappingManagerBuild(this);
    }

	protected Element createModelInfoElement()
	{
		Element xml = super.createModelInfoElement();
		if(this._toolProject == null)
			xml.addElement("project").addText("");
		else
			xml.addElement("project").addText(((IntegrationProjectBuilder)this._toolProject).getFileName());
		return xml;
	}

	protected void parseModelInfoElement(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml model info");
		XMLUtils.makeRootElement(xmlElement);
		Element versionXml = (Element) xmlElement.selectSingleNode("/modelinfo/version");
		if (versionXml == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml version: " + xmlElement.asXML());
		version = new Version(versionXml);
		Element projectXml = (Element) xmlElement.selectSingleNode("/modelinfo/project");
		if (projectXml == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml project: " + xmlElement.asXML());
        _toolProject = BuildMode.importAnalysisToolProject(projectXml.getText());
	}

	public ModelObject newModelObject(String modelObjectType)
	{
		Parameter o = null;
		Object[] oArray = this._toolConfiguration.createParameter(this, new Id(UUIDGenerator.create()), modelObjectType);
		if (oArray != null && oArray.length > 0)
		{
			if(oArray[0] instanceof VariableParameter)
				o = ((VariableParameter) oArray[0]).getParameter();
			else
				o = ((ObjectiveParameter) oArray[0]).getParameter();
			this.modelObjects.add(o);
			String typeOfParameter = (String) oArray[1];
			if(typeOfParameter.equals(QMOOConfiguration.VARIABLE))
                this._variableParameters.put(o,oArray[0]);
			else
				this._objectiveParameters.put(o,oArray[0]);
			this.getToolMappingManager().addMapping(o, typeOfParameter);
			return o;
		}
		return super.newModelObject(modelObjectType);
	}

	protected void loadXml(Element xmlElement)
    {
        super.loadXml(xmlElement);

        loadMappings(xmlElement);

        storeXml();
    }

	public Element toXmlElement()
	{
		// creates the model element
		Element xml = this.headerToXmlElement();
		xml.addAttribute("toolType", getToolXmlType());

		// creates the modelinfo element
		xml.add(createModelInfoElement());


		// creates the model configuration element
		xml.add(_toolConfiguration.createConfigurationXml());

		Element variableElement = xml.addElement("variables");
		Element objectiveElement = xml.addElement("objectives");
		Element contextElement = xml.addElement("contexts");

		// add parameters and contexts
		for (Iterator iter = modelObjects.listIterator(); iter.hasNext();)
		{
			Object obj = iter.next();
			if (obj instanceof Parameter)
			{
				Element parameter = null;
				if (this.getOptimizationToolVariableParameterMap().containsKey(obj))
				{
					parameter = ((VariableParameter)this.getOptimizationToolVariableParameterMap().get(obj)).toXmlElement();
					variableElement.add(parameter);
				}
				else
				{
					parameter = ((ObjectiveParameter) this.getOptimizationToolObjectiveParameterMap().get(obj)).toXmlElement();
					objectiveElement.add(parameter);
				}
			}
			else if (obj instanceof Context)
			{
				Element context = ((Context) obj).toXmlElement();
				contextElement.add(context);
			}
		}

		contextElement.add(this._designVariableObjects.toXmlElement());
		contextElement.add(this._designObjectiveObjects.toXmlElement());

		// creates mappings element
		Element mapElement = DocumentHelper.createElement("mappings");
		xml.add(mapElement);

		// add mappings
		Element mapSubElements = mappingManager.toXmlElement(this, "modelMappings");
		if (mapSubElements != null)
			mapElement.add(mapSubElements);

		// add documentation
		if (!doc.isEmpty())
			xml.add(doc.toXmlElement());

		return xml;
	}

	/*
	 * method below is used for the "Add and Map", "Paste copy" functions
	 */
    public Collection newModelObject(Collection modelObjects)
	{
		return newModelObjects(modelObjects, false);
	}

	public Collection newModelObjects(Collection modelObjects, boolean deepCopy)
	{
		Iterator iterator = modelObjects.iterator();
		List newObjects = new ArrayList();
		while(iterator.hasNext())
		{
			Object obj = iterator.next();
			if(obj instanceof Parameter)
			{
				Parameter originalObject = (Parameter) obj;
				ModelObject newObject = null;
				if(_currentView.equals(OptimizationToolBuild.VARIABLE_VIEW))
				{
					VariableParameter variableParameter = new VariableParameter(this, getNextId(), originalObject);
					if(variableParameter != null)
					{
						this.modelObjects.add(variableParameter.getParameter());
						_variableParameters.put(variableParameter.getParameter(), variableParameter);
						newObject = variableParameter.getParameter();
					}
			    }
				else if(_currentView.equals(OptimizationToolBuild.OBJECTIVE_VIEW))
				{
                	ObjectiveParameter objectiveParameter = new ObjectiveParameter(this, getNextId(), originalObject);
					if(objectiveParameter != null)
					{
						this.modelObjects.add(objectiveParameter.getParameter());
						_objectiveParameters.put(objectiveParameter.getParameter(), objectiveParameter);
						newObject = objectiveParameter.getParameter();
					}
				}
				else
					OneButton1Msg.showWarning(null, "Warning: Add and Map", "You can not add and map objects in this view.","Ok", new Dimension(150, 75));

				if(newObject != null)
						newObjects.add(newObject);
			}
		}

		return newObjects;
	}

    public void loadInterfaces(String modelFileName)
	{
        ((AnalysisToolInterfaceManagerBuild)_interfaces).loadInterfaces(modelFileName);
	}

    protected AnalysisToolInterfaceManager createAnalysisToolInterfacesManager()
    {
        return new AnalysisToolInterfaceManagerBuild(this);
    }

    public void deleteModelObjects(Collection mObjs)
    {
        List objectsToBeDeleted = new ArrayList(mObjs);
        ListIterator iterator = objectsToBeDeleted.listIterator();

        while (iterator.hasNext())
        {
            Object obj = iterator.next();
            if (obj instanceof Parameter)
            {
                if (_variableParameters.containsKey(obj))
                    _variableParameters.remove(obj);
                else
                    _objectiveParameters.remove(obj);
            }
        }

        super.deleteModelObjects(mObjs);
    }

    public String getCurrentView()
    {
        return _currentView;
    }
}

