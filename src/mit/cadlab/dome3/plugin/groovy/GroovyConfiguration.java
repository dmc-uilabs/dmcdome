/*
 * GroovyConfiguration.java
 *
 */
package mit.cadlab.dome3.plugin.groovy;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.plugin.PluginConfiguration;
import org.dom4j.Element;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 6.
 */
public class GroovyConfiguration extends PluginConfiguration {
    public static final TypeInfo TYPE_INFO = new TypeInfo("Groovy Script Model", "GROOVY");
    public static final String[] VALID_DATA_TYPES = new String[]{DomeReal.TYPE_INFO.getTypeName(),
                                                                 DomeMatrix.TYPE_INFO.getTypeName(),
                                                                 DomeEnumeration.TYPE_INFO.getTypeName()};

    public static final String MAPPING_COLUMN_NAME = "script variable name";
    public static final int MAPPING_COLUMN_SIZE = 150;

    public static final String FILE_LOCATION = "groovy script file location";

    public GroovyConfiguration(PluginModel model) {
        super(model);
    }

    public GroovyConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement) {
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
