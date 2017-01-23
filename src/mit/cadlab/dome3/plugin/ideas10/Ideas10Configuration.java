package mit.cadlab.dome3.plugin.ideas10;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import org.dom4j.Element;

public class Ideas10Configuration extends PluginConfiguration
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("Ideas Model", "IDEAS");

	public static final String PART = "part";
	public static final String ASSEMBLY = "assembly";
	public static final String MASS = "mass";
	public static final String VOLUME = "volume";
	public static final String OPEN_SURFACE_AREA = "open surface area";
    public static final String SOLID_SURFACE_AREA = "solid surface area";
	public static final String DIMENSION = "dimension";
	//public static final String IGES_NEUTRAL_FILE = "iges neutral file";
	//public static final String STEP_NEUTRAL_FILE = "step neutral file";
	public static final String VRML_FILE = "vrml file";
    public static final String INTF = "interference analysis";

	public static final String[] VALID_DATA_TYPES = new String[]{PART,
	                                                             ASSEMBLY,
	                                                             MASS,
	                                                             VOLUME,
                                                                 OPEN_SURFACE_AREA,
                                                                 SOLID_SURFACE_AREA,
	                                                             DIMENSION,
	                                                             VRML_FILE,
                                                                 INTF};

	public static final String PROJECT_NAME = "name of ideas project: ";
	public static final String SOFTWARE_VERSION = "software version: ";
    public static final String USING_ON_DEMAND_SERVER = "use ondemand server: ";

    public static final String IDEAS10 = "Ideas 10";
	public static final String PART_BIN = "part bin";
	public static final String PART_NAME = "part name";
	public static final String ASSEMBLY_BIN = "assembly bin";
	public static final String ASSEMBLY_NAME = "assembly name";

    public static final String DOOR_NAME = "door surface part name";
    public static final String BODY_NAME = "body surfaces part name";
    public static final String BULB_NAME = "seal bulb part name";
    public static final String X_OFF = "X offset";
    public static final String Y_OFF = "Y offset";
    public static final String Z_OFF = "Z offset";
    public static final String XSEC_DIST = "cross section distance";
    public static final String INTF_DATA = "interference area data";

	//public static final String IGES = "iges";
	//public static final String STEP = "step";
	public static final String VRML = "vrml";

	public static final String MAPPING_COLUMN_NAME = "mapping to model object";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public Ideas10Configuration(PluginModel model)
	{
		super (model);

		Parameter projectName = new ConcreteParameter(model, new Id(Ideas10Configuration.PROJECT_NAME), DomeString.TYPE_INFO.getTypeName());
		projectName.setName(PROJECT_NAME);
		this.addSetupParameter(projectName);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(IDEAS10, ""); // todo: add more Ideas versions when they are available
		version.setLastSelection(0); // default is I-DEAS v8
		this.addSetupParameter(softwareVersion);

        Parameter onDemandSvr = new ConcreteParameter(model, new Id(USING_ON_DEMAND_SERVER), DomeBoolean.TYPE_INFO.getTypeName());
        onDemandSvr.setName(USING_ON_DEMAND_SERVER);
        BooleanData ondemand = (BooleanData) onDemandSvr.getCurrentDataObject();
        ondemand.setValue(false);
        this.addSetupParameter(onDemandSvr);
	}

	public Ideas10Configuration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
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
			((DomeReal) ideasMassProperty.getCurrentDataObject()).setUnit("kg");
			return new Object[]{ideasMassProperty, MASS};
		}
		if (VOLUME.equals(type)) {
			Parameter ideasVolumeProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ideasVolumeProperty.setName(VOLUME);
			((DomeReal) ideasVolumeProperty.getCurrentDataObject()).setUnit("[c_m]");
			return new Object[]{ideasVolumeProperty, VOLUME};
		}
		if (OPEN_SURFACE_AREA.equals(type)) {
			Parameter ideasSurfaceAreaProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ideasSurfaceAreaProperty.setName(OPEN_SURFACE_AREA);
			((DomeReal) ideasSurfaceAreaProperty.getCurrentDataObject()).setUnit("[sm]");
			return new Object[]{ideasSurfaceAreaProperty, OPEN_SURFACE_AREA};
		}
        if (SOLID_SURFACE_AREA.equals(type)) {
            Parameter ideasSurfaceAreaProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
            ideasSurfaceAreaProperty.setName(SOLID_SURFACE_AREA);
            ((DomeReal) ideasSurfaceAreaProperty.getCurrentDataObject()).setUnit("[sm]");
            return new Object[]{ideasSurfaceAreaProperty, SOLID_SURFACE_AREA};
        }
		if (DIMENSION.equals(type)) {
			Parameter ideasDimension = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ideasDimension.setName(DIMENSION);
			((DomeReal) ideasDimension.getCurrentDataObject()).setUnit("m");
			return new Object[]{ideasDimension, DIMENSION};
		}
/*		if (IGES_NEUTRAL_FILE.equals(type)) {
			Parameter igesExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			igesExportFile.setName(IGES_NEUTRAL_FILE);
			return new Object[]{igesExportFile, IGES};
		}
		if (STEP_NEUTRAL_FILE.equals(type)) {
			Parameter stepExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			stepExportFile.setName(STEP_NEUTRAL_FILE);
			return new Object[]{stepExportFile, STEP};
		}*/
		if (VRML_FILE.equals(type)) {
			Parameter vrmlExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			vrmlExportFile.setName(VRML_FILE);
			return new Object[]{vrmlExportFile, VRML};
		}
        if (INTF.equals(type)) {
            Parameter intf = new ConcreteParameter(model, id, DomeList.TYPE_INFO.getTypeName());
            DomeListData intfList = (DomeListData) intf.getCurrentDataObject();
            intf.setName(INTF);
            Parameter doorName = intfList.addItem(DomeString.TYPE_INFO.getTypeName());
            Parameter bodyName = intfList.addItem(DomeString.TYPE_INFO.getTypeName());
            Parameter bulbName = intfList.addItem(DomeString.TYPE_INFO.getTypeName());
            doorName.setName(DOOR_NAME);
            bodyName.setName(BODY_NAME);
            bulbName.setName(BULB_NAME);
            Parameter x = intfList.addItem(DomeReal.TYPE_INFO.getTypeName());
            Parameter y = intfList.addItem(DomeReal.TYPE_INFO.getTypeName());
            Parameter z = intfList.addItem(DomeReal.TYPE_INFO.getTypeName());
            Parameter xsecDist = intfList.addItem(DomeReal.TYPE_INFO.getTypeName());
            x.setName(X_OFF);
            y.setName(Y_OFF);
            z.setName(Z_OFF);
            xsecDist.setName(XSEC_DIST);
            Parameter result = intfList.addItem(DomeMatrix.TYPE_INFO.getTypeName());
            result.setName(INTF_DATA);
            return new Object[]{intf, INTF};
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
