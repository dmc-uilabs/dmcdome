package mit.cadlab.dome3.plugin.javajar.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/13/11
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarVector extends JarPluginData {
    private DomeVector data;

    public JarVector(JarPlugin plg, Parameter p, String fName) throws Exception {
        super(plg, p, fName);
        data = (DomeVector) p.getCurrentDataObject();
    }

    protected Object getValue(boolean isNativeCall) {
        if (isNativeCall)
            return plugin.getFieldValue(fieldName);
        else
            return convertToObject(data.getValues(), fieldType, new int[] {data.getSize()});
    }

    protected void setValue(Object value, boolean isNativeCall) {
        if (isNativeCall)
            plugin.setFieldValue(fieldName, value);
        else
            data.setValues(convertToList(value));
    }

    public String toString() {
        return "vector (" + fieldName + "): " + getValue(false);
    }

    protected List<Class> getSupportedDataTypes() {
        List<Class> supportedTypes = new Vector<Class>();
        supportedTypes.add(double[].class);
        supportedTypes.add(int[].class);
        supportedTypes.add(Double[].class);
        supportedTypes.add(Integer[].class);

        return supportedTypes;
    }
}
