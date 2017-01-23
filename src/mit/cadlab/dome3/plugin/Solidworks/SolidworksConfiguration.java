package mit.cadlab.dome3.plugin.Solidworks;

import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import org.dom4j.Element;

import java.util.List;
import java.util.Arrays;

import edu.iupui.rg.ucum.units.Unit;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 3, 2003
 * Time: 1:59:20 PM
 * To change this template use Options | File Templates.
 */
public class SolidworksConfiguration extends PluginConfiguration
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("SolidWorks Model", "SLDWKS");

	public static final String SOLIDWORKS_MASS = "Mass";
	public static final String SOLIDWORKS_ANGLEUNIT = "Angle unit";
	public static final String SOLIDWORKS_COLOR = "Color";
	public static final String SOLIDWORKS_LENGTHUNIT = "Length unit";
	public static final String SOLIDWORKS_SURFACEAREA = "Surface area";
	public static final String SOLIDWORKS_VOLUME = "Volume";
	public static final String SOLIDWORKS_DIMENSION = "Dimension";
	public static final String IGES_NEUTRAL_FILE = "IGES neutral file";
	public static final String STEP_NEUTRAL_FILE = "STEP neutral file";
	public static final String SLDWKS_VRML_FILE = "VRML file";

	public static final String[] VALID_DATA_TYPES = new String[]{SOLIDWORKS_DIMENSION,
	                                                             SOLIDWORKS_MASS,
	                                                             SOLIDWORKS_SURFACEAREA,
	                                                             SOLIDWORKS_VOLUME,
	                                                             IGES_NEUTRAL_FILE,
	                                                             STEP_NEUTRAL_FILE,
	                                                             SLDWKS_VRML_FILE
	};
	/*
	SOLIDWORKS_ANGLEUNIT,
	SOLIDWORKS_COLOR,
	SOLIDWORKS_LENGTHUNIT,
	DomeReal.TYPE_INFO.getTypeName(),
																DomeMatrix.TYPE_INFO.getTypeName(),
	  DomeVectorData.TYPE_INFO.getTypeName(),
	DomeString.TYPE_INFO.getTypeName()};
    */

	public static final String MAPPING_COLUMN_NAME = "Dimension Name or Property";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String FILE_LOCATION = ".SLDPRT File location";
	public static final String SOFTWARE_VERSION = "Software version";
	public static final String RUN_IN_FOREGROUND = "Run in foreground";

	public static final String SOLIDWORKS_2000 = "Solidworks 2000";
    public static final String SOLIDWORKS_2001 = "Solidworks 2001";
	public static final String SOLIDWORKS_2002 = "Solidworks 2002";
	public static final String SOLIDWORKS_2003 = "Solidworks 2003";
	public static final String SOLIDWORKS_2004 = "Solidworks 2004";

    public static final String SLDWKS_2K_DLL = "SolidWorksPlugin2K";
    public static final String SLDWKS_2K1_DLL = "SolidWorksPlugin2K1";
    public static final String SLDWKS_2K2_DLL = "SolidWorksPlugin2K2";
	public static final String SLDWKS_2K3_DLL = "SolidWorksPlugin2K3";
	public static final String SLDWKS_2K4_DLL = "SolidWorksPlugin2K4";

	public SolidworksConfiguration(PluginModel model)
	{
		super (model);

		//Parameter fileLocation = new ConcreteParameter(model, new Id(FILE_LOCATION), mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile.TYPE_INFO.getTypeName());
		//fileLocation.setName(FILE_LOCATION);
		//addSetupParameter(fileLocation);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(SOLIDWORKS_2000, "");
        version.addElement(SOLIDWORKS_2001, "");
		version.addElement(SOLIDWORKS_2002, "");
		version.addElement(SOLIDWORKS_2003, "");
		version.addElement(SOLIDWORKS_2004, "");
		// todo: add more solidworks versions when they are available
		version.setLastSelection(0); // default is solidworks 2001
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model, new Id(RUN_IN_FOREGROUND), mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
	}

	public SolidworksConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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

	public boolean useCustomDatatype()
	{
		return true;
	}

	public Object[] createParameter(PluginModel model, Id id, String type)
	{
		if (SOLIDWORKS_MASS.equals(type)) {
			Parameter mass = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			mass.setName(SOLIDWORKS_MASS);
			((DomeReal) mass.getCurrentDataObject()).setUnit("kg");
			return new Object[]{mass, "Mass"};
		}
		if (SOLIDWORKS_ANGLEUNIT.equals(type)) {
			Parameter angleUnit = new ConcreteParameter(model, id, DomeString.TYPE_INFO.getTypeName());
			angleUnit.setName(SOLIDWORKS_ANGLEUNIT);
			return new Object[]{angleUnit, "Angle unit"};
		}
		if (SOLIDWORKS_COLOR.equals(type)) {
			Parameter color = new ConcreteParameter(model, id, DomeVectorData.TYPE_INFO.getTypeName());
			color.setName(SOLIDWORKS_COLOR);
			return new Object[]{color, "Color"};
		}
		if (SOLIDWORKS_LENGTHUNIT.equals(type)) {
			Parameter lengthUnit = new ConcreteParameter(model, id, DomeString.TYPE_INFO.getTypeName());
			lengthUnit.setName(SOLIDWORKS_LENGTHUNIT);
			return new Object[]{lengthUnit, "Length unit"};
		}
		if (SOLIDWORKS_SURFACEAREA.equals(type)) {
			Parameter surfaceArea = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			surfaceArea.setName(SOLIDWORKS_SURFACEAREA);
			((DomeReal) surfaceArea.getCurrentDataObject()).setUnit("[sm]");
			return new Object[]{surfaceArea, "Surface area"};
		}
		if (SOLIDWORKS_VOLUME.equals(type)) {
			Parameter volume = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			volume.setName(SOLIDWORKS_VOLUME);
			((DomeReal) volume.getCurrentDataObject()).setUnit("[c_m]");
			return new Object[]{volume, "Volume"};
		}
		if (SOLIDWORKS_DIMENSION.equals(type)) {
			Parameter dimension = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			dimension.setName(SOLIDWORKS_DIMENSION);
			((DomeReal) dimension.getCurrentDataObject()).setUnit("mm");
			return new Object[]{dimension};
		}
		if (IGES_NEUTRAL_FILE.equals(type)) {
			Parameter igesExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			igesExportFile.setName(IGES_NEUTRAL_FILE);
			return new Object[]{igesExportFile, "IGES neutral file"};
		}
		if (STEP_NEUTRAL_FILE.equals(type)) {
			Parameter stepExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			stepExportFile.setName(STEP_NEUTRAL_FILE);
			return new Object[]{stepExportFile, "STEP neutral file"};
		}
		if (SLDWKS_VRML_FILE.equals(type)) {
			Parameter vrmlExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			vrmlExportFile.setName(SLDWKS_VRML_FILE);
			return new Object[]{vrmlExportFile, "VRML file"};
		}

		return null;
	}

    public String[] getValidDataTypes() {
        return VALID_DATA_TYPES;
    }
}
