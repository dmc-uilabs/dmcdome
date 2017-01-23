package mit.cadlab.dome3.plugin.javajar;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
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
 * User: jmekler
 * Date: 10/3/11
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarConfiguration extends PluginConfiguration {
    public static final TypeInfo TYPE_INFO = new TypeInfo("Java JAR Model", "JAR");
    public static final String[] VALID_DATA_TYPES = new String[]{
            DomeBoolean.TYPE_INFO.getTypeName(),
            DomeInteger.TYPE_INFO.getTypeName(),
            DomeReal.TYPE_INFO.getTypeName(),
            DomeString.TYPE_INFO.getTypeName(),
            DomeFile.TYPE_INFO.getTypeName(),
            DomeVector.TYPE_INFO.getTypeName(),
            DomeMatrix.TYPE_INFO.getTypeName(),
            DomeEnumeration.TYPE_INFO.getTypeName()
    };

    public static final String MAPPING_COLUMN_NAME = "Instance Field Name";
    public static final int MAPPING_COLUMN_SIZE = 150;

    public static final String FILE_LOCATION = "JAR File Location";
    public static final String CLASS_TO_INSTANTIATE = "Class to Instantiate";

    public JarConfiguration(PluginModel model) {
        super(model);

        // Create "Class to Instantiate" field
        Parameter className = new ConcreteParameter(model, new Id(CLASS_TO_INSTANTIATE), DomeString.TYPE_INFO.getTypeName());
        className.setName(CLASS_TO_INSTANTIATE);
		addSetupParameter(className);
    }

    public JarConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement) {
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
