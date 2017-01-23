package mit.cadlab.dome3.plugin.javajar.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: 200019935
 * Date: 10/21/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarString  extends JarPluginData {
    private DomeString data;

    public JarString(JarPlugin plg, Parameter p, String fName) throws Exception {
        super(plg, p, fName);
        data = (DomeString) parameter.getCurrentDataObject();
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
            data.setValue((String)value);
    }

    public String toString()
    {
        return ("int (" + fieldName + "): " + getValue(false));
    }

    protected List<Class> getSupportedDataTypes() {
        List<Class> supportedTypes = new Vector<Class>();
        supportedTypes.add(String.class);
        return supportedTypes;
    }
}
