package mit.cadlab.dome3.plugin.adams;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Oct 8, 2003
 * Time: 11:29:02 AM
 * To change this template use Options | File Templates.
 */
public class AdamsConfiguration extends PluginConfiguration
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Adams Model", "ADAMS");
	public static final String[] VALID_DATA_TYPES = new String[]{DomeInteger.TYPE_INFO.getTypeName(),
	                                                             DomeReal.TYPE_INFO.getTypeName(),
	                                                             DomeString.TYPE_INFO.getTypeName(),
																 DomeFile.TYPE_INFO.getTypeName(),
																 DomeVector.TYPE_INFO.getTypeName(),
																 DomeMatrix.TYPE_INFO.getTypeName(),
	                                                             DomeEnumeration.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "variable name";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String SOFTWARE_LOCATION = "Adams executable and command line options";
	public static final String OUTPUT_FILENAME = "Adams output file name(s)";
	public static final String OUTPUT_POSTPROCESSOR_COMMAND = "Output postprocessor command";
	public static final String IS_OUTPUT_POSTPROCESSED = "is output post-processed?";

	public AdamsConfiguration(PluginModel model)
	{
		super (model);

		Parameter softwareLocation = new ConcreteParameter(model, new Id(SOFTWARE_LOCATION), DomeString.TYPE_INFO.getTypeName());
		softwareLocation.setName(SOFTWARE_LOCATION);
		addSetupParameter(softwareLocation);

		Parameter outputFilename = new ConcreteParameter(model, new Id(OUTPUT_FILENAME), DomeString.TYPE_INFO.getTypeName());
		outputFilename.setName(OUTPUT_FILENAME);
		addSetupParameter(outputFilename);

		Parameter outputPostProcCommand = new ConcreteParameter(model, new Id(OUTPUT_FILENAME), DomeString.TYPE_INFO.getTypeName());
		outputPostProcCommand.setName(OUTPUT_POSTPROCESSOR_COMMAND);
		addSetupParameter(outputPostProcCommand);

		Parameter isProcessedOutput = new ConcreteParameter(model, new Id(OUTPUT_FILENAME), DomeBoolean.TYPE_INFO.getTypeName());
		isProcessedOutput.setName(IS_OUTPUT_POSTPROCESSED);
		addSetupParameter(isProcessedOutput);
	}

	public AdamsConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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

