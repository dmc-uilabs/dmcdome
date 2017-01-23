package mit.cadlab.dome3.plugin.vensim;

import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import org.dom4j.Element;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 28.
 */
public class VensimConfiguration extends PluginConfiguration {
    public static final TypeInfo TYPE_INFO = new TypeInfo("Vensim Model", "VENSIM");
    public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName(),
                                                                 DomeVector.TYPE_INFO.getTypeName()};

    public static final String MAPPING_COLUMN_NAME = "variable name";
    public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String SOFTWARE_VERSION = "software version";
	public static final String RUN_IN_FOREGROUND = "run in foreground";

	public static final String VENSIM_55D = "Vensim 5.5D";

    public static final String FILE_LOCATION = "vensim MDL file location";

    public VensimConfiguration(PluginModel model) {
        super(model);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(VENSIM_55D, "");
		version.setLastSelection(0);
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model, new Id(RUN_IN_FOREGROUND), DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
    }

    public VensimConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement) {
        super(model, moFactory, xmlElement);
    }

    public TypeInfo getTypeInfo() {
        return TYPE_INFO;
    }

    public String getMappingColumnName() {
        return MAPPING_COLUMN_NAME;
    }

    public int getMappingColumnSize() {
        return MAPPING_COLUMN_SIZE;
    }

    public String[] getValidDataTypes() {
        return VALID_DATA_TYPES;
    }
}
