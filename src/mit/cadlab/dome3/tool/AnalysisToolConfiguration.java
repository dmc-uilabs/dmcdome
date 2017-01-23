package mit.cadlab.dome3.tool;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.tool.ToolMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.tool.DefaultToolMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: jacob
 * Date: Jun 6, 2003
 * Time: 3:07:01 PM
 * To change this template use Options | File Templates.
 */

/*
 * this is the base tool configuration class, for tools such as
 * optimization, decision analysis support, etc.
 */

public abstract class AnalysisToolConfiguration
{
    protected List _setupParameters = new ArrayList();
	protected HashMap _paramsByName = new HashMap();

    protected List _setupContextFolders = new ArrayList();
    protected HashMap _contextFolderByName = new HashMap();

    protected AnalysisToolConfiguration()
	{
	}

    protected AnalysisToolConfiguration(AnalysisTool model, ModelObjectFactory moFactory, Element xmlElement)
    {
    }

    public abstract TypeInfo getTypeInfo();

	public abstract String getMappingColumnName();

	public abstract int getMappingColumnSize();

    public String getTypeName()
	{
		return getTypeInfo().getTypeName();
	}

    public String getXmlType()
	{
		return getTypeInfo().getXmlType();
	}

    public List getSetupParameters()
	{
		return Collections.unmodifiableList(_setupParameters);
	}

	public Parameter getSetupParameter(String name)
	{
		return (Parameter) _paramsByName.get(name);
	}

    protected void addSetupParameter(Parameter param)
	{
		this._setupParameters.add(param);
		this._paramsByName.put(param.getName(), param);
	}

    public ToolMappingManager createToolMappingManager(AnalysisTool m)
	{
		return new DefaultToolMappingManager(m);
	}

    protected void addSetupContextFolder(Context context)
    {
        this._setupContextFolders.add(context);
        this._contextFolderByName.put(context.getName(), context);
    }

    public List getSetupContextFolders()
    {
        return Collections.unmodifiableList(_setupContextFolders);
    }

    public boolean useCustomDatatype()
	{
		return false;
	}

    public Object[] createParameter(AnalysisTool model, Id id, String type)
	{
		return null;
	}

    public abstract Element createConfigurationXml();
}
