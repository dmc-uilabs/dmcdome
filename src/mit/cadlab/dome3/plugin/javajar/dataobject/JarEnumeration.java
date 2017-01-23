package mit.cadlab.dome3.plugin.javajar.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/13/11
 * Time: 6:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarEnumeration extends JarPluginData {
    private DomeEnumeration data;

    public JarEnumeration(JarPlugin plg, Parameter p, String fName) throws Exception {
        super(plg, p, fName);
        data = (DomeEnumeration) parameter.getCurrentDataObject();
    }


    protected Object getValue(boolean isNativeCall) {
        if (isNativeCall)
            return plugin.getFieldValue(fieldName);
        else
            return data.getLastSelection();
    }


    protected void setValue(Object value, boolean isNativeCall) {
        if (isNativeCall)
            plugin.setFieldValue(fieldName, value);
        else
            data.setLastSelection((Integer) value);

    }

    public String toString() {
        return "Enumeration (" + fieldName + "): #" + getValue(false);
    }

    protected List<Class> getSupportedDataTypes() {
        List<Class> supportedTypes = new Vector<Class>();
        supportedTypes.add(int.class);
        return supportedTypes;
    }
}
