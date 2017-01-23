package mit.cadlab.dome3.plugin.javajar.dataobject;


import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.javajar.JarPlugin;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/12/11
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarMatrix extends JarPluginData {

    private DomeMatrix data;
    private DataObject d;

    public JarMatrix(JarPlugin plg, Parameter p, String fName) throws Exception {
        super(plg, p, fName);

        // link data to parameter
        if (parameter == null)
            data = new DomeMatrixData();
        else
            data = (DomeMatrix) parameter.getCurrentDataObject();
    }

    public Object getValue(boolean isNativeCall) {
        if (isNativeCall)
            return plugin.getFieldValue(fieldName);
        else
            return convertToObject(data.getValues(), fieldType, new int[] {data.getRowCount(), data.getColumnCount()});

    }

    public void setValue(Object value, boolean isNativeCall) {
        if (isNativeCall)
            plugin.setFieldValue(fieldName, value);
        else
            data.setValues(convertToList(value));
    }

    public String toString() {
        return "matrix (" + fieldName + "): " + getValue(false);
    }

    @Override
    protected List<Class> getSupportedDataTypes() {
        List<Class> supportedTypes = new Vector<Class>();
        supportedTypes.add(double[][].class);
        supportedTypes.add(Double[][].class);
        supportedTypes.add(int[][].class);
        supportedTypes.add(Integer[][].class);
        return supportedTypes;
    }
}
