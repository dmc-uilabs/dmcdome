package mit.cadlab.dome3.plugin.mathematica;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 4, 2003
 * Time: 2:48:45 PM
 * To change this template use Options | File Templates.
 */
public class MathematicaConfiguration extends PluginConfiguration
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Mathematica Model", "MATHEMATICA");
	public static final String[] VALID_DATA_TYPES = new String[]{mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal.TYPE_INFO.getTypeName(),
	                                                             mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger.TYPE_INFO.getTypeName(),
	                                                             DomeVectorData.TYPE_INFO.getTypeName(),
	                                                             mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "Mathematica Variable Name";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String FILE_LOCATION = ".m file location";
	public static final String SOFTWARE_VERSION = "software version";
	public static final String RUN_IN_FOREGROUND = "run in foreground";

	public static final String MATHEMATICA_4 = "Mathematica 4";


	public MathematicaConfiguration(PluginModel model)
	{
		super (model);

		//Parameter fileLocation = new ConcreteParameter(model, new Id(FILE_LOCATION), mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile.TYPE_INFO.getTypeName());
		//fileLocation.setName(FILE_LOCATION);
		//addSetupParameter(fileLocation);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(MATHEMATICA_4, "");
		//version.addElement(MATLAB_5,""); // todo: add more mathematica versions when they are available
		version.setLastSelection(0); // default is matlab 6
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model, new Id(RUN_IN_FOREGROUND), mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
	}

	public MathematicaConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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

