package mit.cadlab.dome3.plugin.catia;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: weimao
 * Date: Apr 3, 2003
 * Time: 1:59:20 PM
 * To change this template use Options | File Templates.
 */
public class CATIAConfiguration extends PluginConfiguration
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("CATIA Model", "CATIA");

	public static final String CATIA_REAL               = "Real";
	public static final String CATIA_INT                = "Integer";
	public static final String CATIA_STRING             = "String";
	public static final String CATIA_BOOL               = "Boolean";
	public static final String CATIA_LENGTH             = "Length";
	public static final String CATIA_ANGLE              = "Angle";
	public static final String CATIA_MASS               = "Mass";
	public static final String CATIA_VOLUME             = "Volume";
	public static final String CATIA_DENSITY            = "Density";
	public static final String CATIA_AREA               = "Area";
	public static final String CATIA_VECTOR             = "Vector";
	public static final String CATIA_IGES_FILE          = "IGES neutral file";
	public static final String CATIA_STEP_FILE          = "STEP neutral file";
	public static final String CATIA_VRML_FILE          = "VRML file";
	public static final String CATIA_USER_LIBRARY       = "User library";

	public static final String[] VALID_DATA_TYPES = new String[]{
		CATIA_REAL, CATIA_INT, CATIA_STRING, CATIA_BOOL, CATIA_LENGTH, CATIA_ANGLE,
		CATIA_MASS, CATIA_VOLUME, CATIA_DENSITY, CATIA_AREA, CATIA_VECTOR,
		CATIA_IGES_FILE, CATIA_STEP_FILE, CATIA_VRML_FILE, CATIA_USER_LIBRARY};

	public static final String MAPPING_COLUMN_NAME = "Dimension Name or Property";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String FILE_LOCATION = ".CATPart File location";
	public static final String SOFTWARE_VERSION = "Software version";
	public static final String RUN_IN_FOREGROUND = "Run in foreground";

	public static final String CATIA_DLL = "CATIAPlugin";
	public static final String CATIA_VERSION = "CATIA_V5";


	public CATIAConfiguration(PluginModel model)
	{
        super (model);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(CATIA_VERSION, "");
		version.setLastSelection(0); // default is v5
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model, new Id(RUN_IN_FOREGROUND), DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
    }

	public CATIAConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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
		if (CATIA_REAL.equals(type)) {
			Parameter real = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			real.setName(CATIA_REAL);
			return new Object[]{real, "Real"};
		}

		if (CATIA_INT.equals(type)) {
			Parameter integer = new ConcreteParameter(model, id, DomeInteger.TYPE_INFO.getTypeName());
			integer.setName(CATIA_INT);
			return new Object[]{integer, "Integer"};
		}

		if (CATIA_STRING.equals(type)) {
			Parameter string = new ConcreteParameter(model, id, DomeString.TYPE_INFO.getTypeName());
			string.setName(CATIA_STRING);
			return new Object[]{string, "String"};
		}

		if (CATIA_BOOL.equals(type)) {
			Parameter bool = new ConcreteParameter(model, id, DomeBoolean.TYPE_INFO.getTypeName());
			bool.setName(CATIA_BOOL);
			return new Object[]{bool, "Boolean"};
		}

		if (CATIA_LENGTH.equals(type)) {
			Parameter length = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			length.setName(CATIA_LENGTH);
			((DomeReal) length.getCurrentDataObject()).setUnit("m");
			return new Object[]{length, "Length"};
		}

		if (CATIA_ANGLE.equals(type)) {
			Parameter angle = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			angle.setName(CATIA_ANGLE);
			return new Object[]{angle, "Angle"};
		}

		if (CATIA_MASS.equals(type)) {
			Parameter mass = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			mass.setName(CATIA_MASS);
			((DomeReal) mass.getCurrentDataObject()).setUnit("kg");
			return new Object[]{mass, "Mass"};
		}

		if (CATIA_VOLUME.equals(type)) {
			Parameter volume = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			volume.setName(CATIA_VOLUME);
			((DomeReal) volume.getCurrentDataObject()).setUnit("[c_m]");
			return new Object[]{volume, "Volume"};
		}

		if (CATIA_DENSITY.equals(type)) {
			Parameter density = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			density.setName(CATIA_DENSITY);
			((DomeReal) density.getCurrentDataObject()).setUnit("[kg_p_m3]");
			return new Object[]{density, "Density"};
		}

		if (CATIA_AREA.equals(type)) {
			Parameter area = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			area.setName(CATIA_AREA);
			((DomeReal) area.getCurrentDataObject()).setUnit("[sm]");
			return new Object[]{area, "Area"};
		}

		if (CATIA_VECTOR.equals(type)) {
			Parameter vector = new ConcreteParameter(model, id, DomeVector.TYPE_INFO.getTypeName());
			vector.setName(CATIA_VECTOR);
			return new Object[]{vector, "Vector"};
		}

		if (CATIA_IGES_FILE.equals(type)) {
			Parameter igesExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			igesExportFile.setName(CATIA_IGES_FILE);
			return new Object[]{igesExportFile, "IGES neutral file"};
		}

		if (CATIA_STEP_FILE.equals(type)) {
			Parameter stepExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			stepExportFile.setName(CATIA_STEP_FILE);
			return new Object[]{stepExportFile, "STEP neutral file"};
		}

		if (CATIA_VRML_FILE.equals(type)) {
			Parameter vrmlExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			vrmlExportFile.setName(CATIA_VRML_FILE);
			return new Object[]{vrmlExportFile, "VRML file"};
		}

		if (CATIA_USER_LIBRARY.equals(type)) {
			Parameter userLibrary = new ConcreteParameter(model, id, DomeList.TYPE_INFO.getTypeName());
			userLibrary.setName(CATIA_USER_LIBRARY);
			Parameter libName = new ConcreteParameter(model, new Id(UUIDGenerator.create()),
			                                          DomeString.TYPE_INFO.getTypeName());
			libName.setName ("Library Name");
			libName.setConstant(true);

			DomeListData list = (DomeListData) userLibrary.getCurrentDataObject();
			list.addItemReference(libName);

			return new Object[]{userLibrary, "User Library"};
		}

		return null;
	}

    public String[] getValidDataTypes() {
        return VALID_DATA_TYPES;
    }
}