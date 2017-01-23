package mit.cadlab.dome3.plugin.javajar.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/13/11
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarFile extends JarPluginData {
    private DomeFile data;

    public JarFile(JarPlugin plg, Parameter p, String fName) throws Exception {
        super(plg, p, fName);
        data = (DomeFile) parameter.getCurrentDataObject();
    }

    protected Object getValue(boolean isNativeCall) {
    	Object ret = null;
    	
        if (isNativeCall)
            ret = plugin.getFieldValue(fieldName);
        else
            ret = data.getFilePath();
        
        return ret;
    }

    protected void setValue(Object value, boolean isNativeCall) {
        if (isNativeCall)
            plugin.setFieldValue(fieldName, data);
        else
            data.setFilePath(value.toString());
    }

    public String toString() {
        return "file (" + fieldName + "): " + getValue(false);
    }

    @Override
    protected List<Class> getSupportedDataTypes() {
        List<Class> supportedTypes = new Vector<Class>();
        supportedTypes.add(DomeFile.class);
        return supportedTypes;
    }
}
