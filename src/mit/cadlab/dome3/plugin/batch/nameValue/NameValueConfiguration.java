// AbaqusConfiguration.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin.batch.nameValue;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
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
public class NameValueConfiguration extends PluginConfiguration
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Name-Value Model", "NAME_VALUE");
	public static final String[] VALID_DATA_TYPES = new String[]{DomeBoolean.TYPE_INFO.getTypeName(),
                                                                 DomeEnumeration.TYPE_INFO.getTypeName(),
                                                                 DomeFile.TYPE_INFO.getTypeName(),
	                                                             DomeInteger.TYPE_INFO.getTypeName(),
                                                                 DomeMatrix.TYPE_INFO.getTypeName(),
	                                                             DomeReal.TYPE_INFO.getTypeName(),
	                                                             DomeString.TYPE_INFO.getTypeName(),
                                                                 DomeVector.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "variable name";
	public static final int MAPPING_COLUMN_SIZE = 100;

	public static final String INPUT_FILE_LOCATION = "input file location";
	public static final String OUTPUT_FILE_LOCATION = "output file location";
	public static final String EXECUTABLE_LOCATION = "executable location";
	public static final String COMMAND_LINE_ARGUMENTS = "command line arguments";

	public static final String NAME_VALUE_SEPARATOR = "name value separator";
	public static final String COMMENT_IDENTIFIER = "comment identifier";
	public static final String OUTPUT_COMMENT_CHARACTER = "#";

	public NameValueConfiguration(PluginModel model)
	{
		super (model);

		Parameter inputFileLocation = new ConcreteParameter(model, new Id(INPUT_FILE_LOCATION), DomeFile.TYPE_INFO.getTypeName());
		inputFileLocation.setName(INPUT_FILE_LOCATION);
		addSetupParameter(inputFileLocation);

		Parameter outputFileLocation = new ConcreteParameter(model, new Id(OUTPUT_FILE_LOCATION), DomeFile.TYPE_INFO.getTypeName());
		outputFileLocation.setName(OUTPUT_FILE_LOCATION);
		addSetupParameter(outputFileLocation);

		Parameter softwareLocation = new ConcreteParameter(model, new Id(EXECUTABLE_LOCATION), DomeFile.TYPE_INFO.getTypeName());
		softwareLocation.setName(EXECUTABLE_LOCATION);
		addSetupParameter(softwareLocation);

		Parameter commandLineArgs = new ConcreteParameter(model, new Id(COMMAND_LINE_ARGUMENTS), DomeString.TYPE_INFO.getTypeName());
		commandLineArgs.setName(COMMAND_LINE_ARGUMENTS);
		addSetupParameter(commandLineArgs);

		/* to be used later
		Parameter nameValueSeparator = new ConcreteParameter(model, new Id(NAME_VALUE_SEPARATOR), DomeString.TYPE_INFO.getTypeName());
		nameValueSeparator.setName(NAME_VALUE_SEPARATOR);
		addSetupParameter(nameValueSeparator);

		Parameter commentIdentifier = new ConcreteParameter(model, new Id(COMMENT_IDENTIFIER), DomeString.TYPE_INFO.getTypeName());
		commentIdentifier.setName(COMMENT_IDENTIFIER);
		addSetupParameter(commentIdentifier);*/
	}

	public NameValueConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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
