package mit.cadlab.dome3.plugin.matlab;

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
public class MatlabConfiguration extends PluginConfiguration
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Matlab Model", "MATLAB");
	public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName(),
	                                                             DomeMatrix.TYPE_INFO.getTypeName(),
                                                                 DomeEnumeration.TYPE_INFO.getTypeName(),
                                                                 DomeFile.TYPE_INFO.getTypeName()};

	public static final String MAPPING_COLUMN_NAME = "Matlab Variable Name";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String FILE_LOCATION = ".m file location";
	public static final String SOFTWARE_VERSION = "software version";
	public static final String RUN_IN_FOREGROUND = "run in foreground";

	public static final String MATLAB_6p5 = "Matlab 6.5";
	public static final String MATLAB_6 = "Matlab 6";
	public static final String MATLAB_7 = "Matlab 7";
	public static final String MATLAB_8p3 = "Matlab 8.3";

	//public static final String MATLAB_5 = "Matlab 5";

	public static final String MATLAB_R14_DLL = "MatlabPluginR14";
	public static final String MATLAB_R13_DLL = "MatlabPluginR13";
	public static final String MATLAB_R12_DLL = "MatlabPluginR12";
	// Going forward lets keep the Matlab release version in the string
	// WE are going to compile using lib and includes for  Matlab 2014 compiler so call it MATLAB_R2014_DLL
	public static final String MATLAB_R2014_DLL = "MatlabPluginR2014";

	public MatlabConfiguration(PluginModel model)
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
		//version.addElement(MATLAB_5,""); // todo: add more matlab versions when they are available
		version.addElement(MATLAB_6, "");
		version.addElement(MATLAB_6p5, "");
		version.addElement(MATLAB_7, "");
		version.addElement(MATLAB_8p3, "");
		// Note: We are testing in R2014A release for which the version is 
		// 8.3 and will keep that as default
		// Previous default was Matlab version 6.5
		// Matlab has Release names as R2014, R2015 ..
		// But keeps the Version numbers as Matlab 8.3, 8.5
		// See the Version Number and Release names at https://en.wikipedia.org/wiki/MATLAB
		//
		version.setLastSelection(1); // default is matlab 8.3
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model,
		                                                  new Id(RUN_IN_FOREGROUND),
		                                                  DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
	}

	public MatlabConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
	{
		super(model, moFactory, xmlElement);
		// add the additional matlab version to old models that did not have it
		Parameter softwareVersion = getSetupParameter(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		if (version.getSize()==1) { // Default to 
			version.addElement(MATLAB_8p3, "");
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
