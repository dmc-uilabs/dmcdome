package mit.cadlab.dome3.plugin.javajar.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/4/11
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarInteger extends JarPluginData {
    private DomeInteger data;

    public JarInteger(JarPlugin plg, Parameter p, String fName) throws Exception {
        super(plg, p, fName);
        data = (DomeInteger) parameter.getCurrentDataObject();
    }

    // get value
    public Object getValue(boolean isNativeCall)
    {
        if (isNativeCall)
            return plugin.getFieldValue(fieldName);
        else
            return data.getValue();
    }

    public void setValue(Object value, boolean isNativeCall) {
        if (isNativeCall)
            plugin.setFieldValue(fieldName, value);
        else
            data.setValue((Integer)value);
    }

    public String toString()
    {
        return ("int (" + fieldName + "): " + getValue(false));
    }

    protected List<Class> getSupportedDataTypes() {
        List<Class> supportedTypes = new Vector<Class>();
        supportedTypes.add(int.class);
        supportedTypes.add(Integer.class);
        return supportedTypes;
    }
}
