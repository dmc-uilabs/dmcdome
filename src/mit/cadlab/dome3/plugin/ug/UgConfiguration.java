package mit.cadlab.dome3.plugin.ug;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import org.dom4j.Element;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: May 15, 2003
 * Time: 1:59:55 PM
 * To change this template use Options | File Templates.
 */
public class UgConfiguration extends PluginConfiguration
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("Unigraphics Model", "UNIGRAPHICS");

	public static final String PART = "part";
	public static final String ASSEMBLY = "assembly";
	public static final String MASS = "mass";
	public static final String VOLUME = "volume";
	public static final String SURFACE_AREA = "surface area";
	public static final String DIMENSION = "dimension";
	public static final String IGES_NEUTRAL_FILE = "iges neutral file";
	public static final String STEP_NEUTRAL_FILE = "step neutral file";
	public static final String VRML_FILE = "vrml file";

	// for mappings in UgModelRuntime
	public static final String IGES = "iges";
	public static final String STEP = "step";
	public static final String VRML = "vrml";

	public static final String[] VALID_DATA_TYPES = new String[]{PART,
	                                                             ASSEMBLY,
	                                                             MASS,
	                                                             VOLUME,
	                                                             SURFACE_AREA,
	                                                             DIMENSION,
	                                                             IGES_NEUTRAL_FILE,
	                                                             STEP_NEUTRAL_FILE,
	                                                             VRML_FILE};
	public static final String FILE_LOCATION = ".prt file location: ";
	public static final String SOFTWARE_VERSION = "software version: ";

	public static final String UG17 = "Unigraphics v17";
	public static final String UG18 = "Unigraphics v18";
	public static final String UGNX2 = "Unigraphics NX2";

	public static final String UGDLL = "UgPlugin";

	public static final String PART_NAME = "part name";
	public static final String ASSEMBLY_NAME = "assembly name";

	public static final String MAPPING_COLUMN_NAME = "model object";
	public static final int MAPPING_COLUMN_SIZE = 150;

	public UgConfiguration(PluginModel model)
	{
		super (model);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(UgConfiguration.UG17, "");
		version.addElement(UgConfiguration.UG18, ""); // todo: add more Unigraphics versions when they are available
		version.addElement(UgConfiguration.UGNX2, "");
		version.setLastSelection(2); // default is unigraphics 17
		this.addSetupParameter(softwareVersion);
	}

	public UgConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
	{
		super(model, moFactory, xmlElement);
	}

	public Object[] createParameter(PluginModel model, Id id, String type)
	{
		//todo the units right now are hardcoded ....this is garbage
		//todo in the future they must be set in the model build mode

		if (UgConfiguration.PART.equals(type))
		{
			Parameter ugPart = new ConcreteParameter(model, id, DomeList.TYPE_INFO.getTypeName());
			DomeListData partList = (DomeListData) ugPart.getCurrentDataObject();
			ugPart.setName(UgConfiguration.PART);
			Parameter ugPartName = partList.addItem(DomeString.TYPE_INFO.getTypeName());
			ugPartName.setName(PART_NAME);
			return new Object[]{ugPart, PART};
		}
		if (UgConfiguration.ASSEMBLY.equals(type))
		{
			Parameter ugAssembly = new ConcreteParameter(model, id, DomeList.TYPE_INFO.getTypeName());
			DomeListData assemblyList = (DomeListData) ugAssembly.getCurrentDataObject();
			ugAssembly.setName(UgConfiguration.ASSEMBLY);
			Parameter ugAssemblyName = assemblyList.addItem(DomeString.TYPE_INFO.getTypeName());
			ugAssemblyName.setName(ASSEMBLY_NAME);
			return new Object[]{ugAssembly, ASSEMBLY};
		}
		if (UgConfiguration.MASS.equals(type))
		{
			Parameter ugMassProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ugMassProperty.setName(MASS);
			((DomeReal) ugMassProperty.getCurrentDataObject()).setUnit("g");
			return new Object[]{ugMassProperty, MASS};
		}
		if (UgConfiguration.VOLUME.equals(type))
		{
			Parameter ugVolumeProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ugVolumeProperty.setName(VOLUME);
			((DomeReal) ugVolumeProperty.getCurrentDataObject()).setUnit("[c_mm]");
			return new Object[]{ugVolumeProperty, VOLUME};
		}
		if (UgConfiguration.SURFACE_AREA.equals(type))
		{
			Parameter ugSurfaceAreaProperty = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ugSurfaceAreaProperty.setName(SURFACE_AREA);
			((DomeReal) ugSurfaceAreaProperty.getCurrentDataObject()).setUnit("[smm]");
			return new Object[]{ugSurfaceAreaProperty, SURFACE_AREA};
		}
		if (UgConfiguration.DIMENSION.equals(type))
		{
			Parameter ugDimension = new ConcreteParameter(model, id, DomeReal.TYPE_INFO.getTypeName());
			ugDimension.setName(UgConfiguration.DIMENSION);
			((DomeReal) ugDimension.getCurrentDataObject()).setUnit("mm");
			return new Object[]{ugDimension, DIMENSION};
		}
		if (IGES_NEUTRAL_FILE.equals(type))
		{
			Parameter igesExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			igesExportFile.setName(IGES_NEUTRAL_FILE);
			return new Object[]{igesExportFile, IGES};
		}
		if (STEP_NEUTRAL_FILE.equals(type))
		{
			Parameter stepExportFile = new ConcreteParameter(model, id, DomeFile.TYPE_INFO.getTypeName());
			stepExportFile.setName(STEP_NEUTRAL_FILE);
			return new Object[]{stepExportFile, STEP};
		}
		if (VRML_FILE.equals(type))
		{
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
