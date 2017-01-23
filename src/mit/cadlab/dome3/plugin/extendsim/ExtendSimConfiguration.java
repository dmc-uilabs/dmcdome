package mit.cadlab.dome3.plugin.extendsim;

import mit.cadlab.dome3.plugin.PluginConfiguration;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: himosqui
 * Date: Aug 10, 2007
 * Time: 3:25:52 AM
 * To change this template use Options | File Templates.
 */
public class ExtendSimConfiguration extends PluginConfiguration {
    public static final TypeInfo TYPE_INFO = new TypeInfo("ExtendSim Model", "EXTENDSIM");
    public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName(), DomeInteger.TYPE_INFO.getTypeName(), DomeMatrix.TYPE_INFO.getTypeName()};
//                                                                 DomeVector.TYPE_INFO.getTypeName(),
//                                                                 DomeMatrix.TYPE_INFO.getTypeName()};

    public static final String MAPPING_COLUMN_NAME = "variable reference";
    public static final int MAPPING_COLUMN_SIZE = 150;

	public static final String SOFTWARE_VERSION = "software version";
	public static final String RUN_IN_FOREGROUND = "run in foreground";

	public static final String EXTENDSIM_6 = "ExtendSim 6";

    public static final String FILE_LOCATION = "ExtendSim model file (.mox) location";

    public ExtendSimConfiguration(PluginModel model) {
        super(model);

		Parameter softwareVersion = new ConcreteParameter(model, new Id(SOFTWARE_VERSION), DomeEnumeration.TYPE_INFO.getTypeName());
		softwareVersion.setName(SOFTWARE_VERSION);
		EnumerationData version = (EnumerationData) softwareVersion.getCurrentDataObject();
		version.addElement(EXTENDSIM_6, "");
		version.setLastSelection(0);
		addSetupParameter(softwareVersion);

		Parameter runInForeground = new ConcreteParameter(model, new Id(RUN_IN_FOREGROUND), DomeBoolean.TYPE_INFO.getTypeName());
		runInForeground.setName(RUN_IN_FOREGROUND);
		addSetupParameter(runInForeground);
    }

    public ExtendSimConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement) {
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
