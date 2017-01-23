// AbaqusConfiguration.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin.abaqus;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import org.dom4j.Element;

/**
 * Setup parameters for Abaqus plugin.
 */
public class AbaqusConfiguration extends PluginConfiguration
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Abaqus Model", "ABAQUS");
	public static final String[] VALID_DATA_TYPES = new String[]{DomeFile.TYPE_INFO.getTypeName(),
	                                                             DomeInteger.TYPE_INFO.getTypeName(),
	                                                             DomeMatrix.TYPE_INFO.getTypeName(),
	                                                             DomeReal.TYPE_INFO.getTypeName(),
	                                                             DomeString.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "variable name";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String ITERATIVE_MODEL = "iterative model";
	public static final String EXECUTABLE_COMMAND = "executable command";
	public static final String POSTPROCESSING_COMMAND = "postprocessing command";
	public static final String OUTPUT_DATA_FILENAME = "output data file name";

	public static final String MODEL_NAME_MAPPING = "model_name";
	public static final String INITIAL_MODEL_TEMPLATE = "initial model template";
	public static final String RESTART_MODEL_TEMPLATE = "restart model template";

	public static final String OUTPUT_COMMENT_CHARACTER = "#";

	public AbaqusConfiguration(PluginModel model)
	{
		super (model);

		Parameter iterativeModel = new ConcreteParameter(model, new Id(ITERATIVE_MODEL), DomeBoolean.TYPE_INFO.getTypeName());
		iterativeModel.setName(ITERATIVE_MODEL);
		addSetupParameter(iterativeModel);

		Parameter executableCommand = new ConcreteParameter(model, new Id(EXECUTABLE_COMMAND), DomeString.TYPE_INFO.getTypeName());
		executableCommand.setName(EXECUTABLE_COMMAND);
		addSetupParameter(executableCommand);

		Parameter postProcessingCommand = new ConcreteParameter(model, new Id(POSTPROCESSING_COMMAND), DomeString.TYPE_INFO.getTypeName());
		postProcessingCommand.setName(POSTPROCESSING_COMMAND);
		addSetupParameter(postProcessingCommand);

		Parameter outputDataFileName = new ConcreteParameter(model, new Id(OUTPUT_DATA_FILENAME), DomeString.TYPE_INFO.getTypeName());
		outputDataFileName.setName(OUTPUT_DATA_FILENAME);
		addSetupParameter(outputDataFileName);
	}

	public AbaqusConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
	{
		super(model, moFactory, xmlElement);
	}

	public TypeInfo getTypeInfo()
	{
		return TYPE_INFO;
	}

	public String getMappingColumnName()
	{
		return MAPPING_COLUMN_NAME;
	}

	public int getMappingColumnSize()
	{
		return MAPPING_COLUMN_SIZE;
	}

    public String[] getValidDataTypes() {
        return VALID_DATA_TYPES;
    }
}
