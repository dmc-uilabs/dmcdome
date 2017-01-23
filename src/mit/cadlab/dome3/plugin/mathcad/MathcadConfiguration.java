package mit.cadlab.dome3.plugin.mathcad;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
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
 * Date: Mar 11, 2003
 * Time: 6:21:13 PM
 * To change this template use Options | File Templates.
 */
public class MathcadConfiguration extends PluginConfiguration
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Mathcad Model", "MATHCAD");
	public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName(),
	                                                             DomeMatrix.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "Mathcad Variable Name";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String FILE_LOCATION = ".xmcd file location";
	public static final String SOFTWARE_VERSION = "software version";
	public static final String RUN_IN_FOREGROUND = "run in foreground";

	public static final String MATHCAD_13 = "Mathcad 13";
	//public static final String MATHCAD_5 = "Mathcad 5";

	public static final String MATHCAD_R13_DLL = "MathcadPluginR13";

	public MathcadConfiguration(PluginModel model)
	{
		super (model);

		//Parameter fileLocation = new ConcreteParameter(model, new Id(FILE_LOCATION), mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile.TYPE_INFO.getTypeName());
		//fileLocation.setName(FILE_LOCATION);
		//addSetupParameter(fileLocation);

		Parameter softwareVersion = new ConcreteParameter(model,
		                                                  new Id(SOFTWARE_VERSION),
		                                                  DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		//version.addElement(MATHCAD_5,""); // todo: add more mathcad versions when they are available
		version.addElement(MATHCAD_13, "");
		version.setLastSelection(0); // default is mathcad 13
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model,
		                                                  new Id(RUN_IN_FOREGROUND),
		                                                  DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
	}

	public MathcadConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
	{
		super(model, moFactory, xmlElement);
		// add the additional mathcad version to old models that did not have it
		Parameter softwareVersion = getSetupParameter(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		if (version.getSize()==1) { // only had mathcad 6
			//version.addElement(MATHCAD_6p5, "");
		}
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
