package mit.cadlab.dome3.plugin.javaexample;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import org.dom4j.Element;

public class JavaExampleConfiguration extends PluginConfiguration
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("Division Model", "DIVISION");

    public static final String NUMERATOR = "numerator";
    public static final String DENOMINATOR = "denominator";
    public static final String RESULT = "result";

	public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "mapping to model object";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public JavaExampleConfiguration(PluginModel model)
	{
		super (model);
		// no configuration options needed for this custom code
	}

	public JavaExampleConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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