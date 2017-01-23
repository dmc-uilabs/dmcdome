package mit.cadlab.dome3.plugin.ideas;

import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import org.dom4j.Element;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 10, 2003
 * Time: 3:53:43 PM
 * To change this template use Options | File Templates.
 */
public class IdeasConfiguration extends PluginConfiguration
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("Ideas8 Model", "IDEAS8");

	public static final String PART = "part";
	public static final String ASSEMBLY = "assembly";
	public static final String MASS = "mass";
	public static final String VOLUME = "volume";
	public static final String SURFACE_AREA = "surface area";
	public static final String DIMENSION = "dimension";
	public static final String IGES_NEUTRAL_FILE = "iges neutral file";
	public static final String STEP_NEUTRAL_FILE = "step neutral file";
	public static final String VRML_FILE = "vrml file";

	public static final String[] VALID_DATA_TYPES = new String[]{PART,
	                                                             ASSEMBLY,
	                                                             MASS,
	                                                             VOLUME,
	                                                             SURFACE_AREA,
	                                                             DIMENSION,
	                                                             IGES_NEUTRAL_FILE,
	                                                             STEP_NEUTRAL_FILE,
	                                                             VRML_FILE};

	public static final String PROJECT_NAME = "name of ideas project: ";
	public static final String SOFTWARE_VERSION = "software version: ";
    public static final String FILE_LOCATION = ".mf1 file location: ";

	public static final String IDEAS8 = "Ideas 8";
	public static final String IDEAS9 = "Ideas 9";
    public static final String IDEAS10 = "Ideas 10";

    public static final String IDEAS_8_DLL = "IdeasPlugin8";
    public static final String IDEAS_9_DLL = "IdeasPlugin9";
    public static final String IDEAS_10_DLL = "IdeasPlugin10";

	public static final String PART_BIN = "part bin";
	public static final String PART_NAME = "part name";

	public static final String ASSEMBLY_BIN = "assembly bin";
	public static final String ASSEMBLY_NAME = "assembly name";

	public static final String IGES = "iges";
	public static final String STEP = "step";
	public static final String VRML = "vrml";

	public static final String MAPPING_COLUMN_NAME = "mapping to model object";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public IdeasConfiguration(PluginModel model)
	{
		super (model);

		Parameter projectName = new ConcreteParameter(model, new Id(IdeasConfiguration.PROJECT_NAME), DomeString.TYPE_INFO.getTypeName());
		projectName.setName(PROJECT_NAME);
		this.addSetupParameter(projectName);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(IDEAS8, "");
		version.addElement(IDEAS9, ""); // todo: add more Ideas versions when they are available
		version.setLastSelection(0); // default is I-DEAS v8
		this.addSetupParameter(softwareVersion);
	}

	public IdeasConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
	{
		super(model, moFactory, xmlElement);
	}

	public Object[] createParameter(PluginModel model, Id id, String type)
	{
		if (PART.equals(type)) {
			Parameter ideasPart = new ConcreteParameter(model, id, DomeList.TYPE_INFO.getTypeName());
			DomeListData partList = (DomeListData) ideasPart.getCurrentDataObject();
			ideasPart.setName(PART);
			Parameter ideasBinName = partList.addItem(DomeString.TYPE_INFO.getTypeName());
			Parameter ideasPartName = partList.addItem(DomeString.TYPE_INFO.getTypeName());
			ideasBinName.setName(PART_BIN);
			ideasPartName.setName(PART_NAME);
			return new Object[]{ideasPart, PART};
		}
		if (ASSEMBLY.equals(type)) {
			Parameter ideasAssembly = new ConcreteParameter(model, id, DomeList.TYPE_INFO.getTypeName());
			DomeListData assemblyList = (DomeListData) ideasAssembly.getCurrentDataObject();
			ideasAssembly.setName(ASSEMBLY);
			Parameter ideasBinName = assemblyList.addItem(DomeString.TYPE_INFO.getTypeName());
			Parameter ideasAssemblyName = assemblyList.addItem(DomeString.TYPE_INFO.getTypeName());
			ideasBinName.setName(ASSEMBLY_BIN);
			ideasAssemblyName.setName(ASSEMBLY_NAME);
			return new Object[]{ideasAssembly, ASSEMBLY};
		}
		if (MASS.equals(type)) {
			Parameter ideasMassProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ideasMassProperty.setName(MASS);
			((DomeReal) ideasMassProperty.getCurrentDataObject()).setUnit("g");
			return new Object[]{ideasMassProperty, MASS};
		}
		if (VOLUME.equals(type)) {
			Parameter ideasVolumeProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ideasVolumeProperty.setName(VOLUME);
			((DomeReal) ideasVolumeProperty.getCurrentDataObject()).setUnit("[c_mm]");
			return new Object[]{ideasVolumeProperty, VOLUME};
		}
		if (SURFACE_AREA.equals(type)) {
			Parameter ideasSurfaceAreaProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ideasSurfaceAreaProperty.setName(SURFACE_AREA);
			((DomeReal) ideasSurfaceAreaProperty.getCurrentDataObject()).setUnit("[smm]");
			return new Object[]{ideasSurfaceAreaProperty, SURFACE_AREA};
		}
		if (DIMENSION.equals(type)) {
			Parameter ideasDimension = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ideasDimension.setName(DIMENSION);
			((DomeReal) ideasDimension.getCurrentDataObject()).setUnit("mm");
			return new Object[]{ideasDimension, DIMENSION};
		}
		if (IGES_NEUTRAL_FILE.equals(type)) {
			Parameter igesExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			igesExportFile.setName(IGES_NEUTRAL_FILE);
			return new Object[]{igesExportFile, IGES};
		}
		if (STEP_NEUTRAL_FILE.equals(type)) {
			Parameter stepExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			stepExportFile.setName(STEP_NEUTRAL_FILE);
			return new Object[]{stepExportFile, STEP};
		}
		if (VRML_FILE.equals(type)) {
			Parameter vrmlExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			vrmlExportFile.setName(VRML_FILE);
			return new Object[]{vrmlExportFile, VRML};
		}
		return null;
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

    public String[] getValidDataTypes() {
        return VALID_DATA_TYPES;
    }
}
