package mit.cadlab.dome3.plugin.javajar.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/4/11
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class JarReal extends JarPluginData
{
    private DomeReal data;

    public JarReal(JarPlugin plg, Parameter p, String fName) throws Exception {
        super(plg, p, fName);

        // link data to parameter
        if (parameter == null)
            data = new RealData();
        else
            data = (DomeReal) parameter.getCurrentDataObject();
    }

    // get value
    public Object getValue(boolean isNativeCall)
    {
        if (isNativeCall)
            return plugin.getFieldValue(fieldName);
        else
            return data.getRealValue();
    }

    public void setValue(Object value, boolean isNativeCall) {
        if (isNativeCall)
            plugin.setFieldValue(fieldName, value);
        else
            data.setRealValue((Double) value);
    }

    public String toString()
    {
        return ("JarReal: " + fieldName + " " + getValue(false));
    }

    @Override
    protected List<Class> getSupportedDataTypes() {
        List<Class> supportedTypes = new Vector<Class>();
        supportedTypes.add(double.class);
        supportedTypes.add(Double.class);
        supportedTypes.add(float.class);
        supportedTypes.add(Float.class);
        return supportedTypes;
    }
}
