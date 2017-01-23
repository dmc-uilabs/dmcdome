package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.network.client.functions.Vectors;

import java.util.HashMap;
import java.util.Vector;

/* key = (String) parameter name
 * value = (Vector) <parameterValue, parameterType (optional)>
 */
public class ParameterMap extends HashMap {

    //public static final String MAP_NAME = "paramMap";
    private String name;

    /**
     *  sets string representation of the given value to the specified parameter
     */
    public void setStringValue(String param, String val) {
        if (containsKey(param))
            ((Vector) get(param)).setElementAt(val, 0);
        else
            put(param, Vectors.create(val));
    }

    /**
     * get the String value of a specified parameter
     */
    public String getStringValue(String param) {
        if (containsKey(param))
            return (String) ((Vector) get(param)).elementAt(0);
        else
            throw new RuntimeException("The specified parameter '"
                    + param + "' does not exist in the map.");
    }

    public Vector getVectorMatrixValue(String param) {
        if (containsKey(param))
            return (Vector) ((Vector) get(param)).elementAt(0);
        else
            throw new RuntimeException("The specified parameter '"
                    + param + "' does not exist in the map.");
    }

    public Object getRawValue(String param) {
        if (containsKey(param))
            return ((Vector) get(param)).elementAt(0);
        else
            throw new RuntimeException("The specified parameter '"
                    + param + "' does not exist in the map.");
    }

    /**
     *  set the type of a specified parameter. needed for vector and metrix
     */
    public void setVectorMatrixValue(String param, Vector val, String type) {
        if (containsKey(param)) {
            Vector prop = ((Vector) get(param));
            if (prop.size()<2)
                prop.setSize(2);
            prop.setElementAt(val, 0);
            prop.setElementAt(type, 1);
        } else {
            put(param, Vectors.create(val, type));
        }
    }

    /**
     * NOTE: parameter type is optinal and only needs to be set for vector and matrix
     * @param param
     * @return
     */
    public String getParamType(String param) {
        if (containsKey(param)) {
            Vector prop = ((Vector) get(param));
            if (prop.size() < 2)
                return "";
            else
                return (String) ((Vector) get(param)).elementAt(1);
        } else
            throw new RuntimeException("The specified parameter '"
                    + param + "' does not exist in the map.");
    }

    public boolean isStringDefined(String param) {
        return containsKey(param) && !getStringValue(param).equals("");
    }

    public boolean isVectorDefined(String param) {
        return containsKey(param) ? !getVectorMatrixValue(param).isEmpty() : false;
    }

    public boolean isVectorDefined(String param, int index) {
        if (!containsKey(param))
            return false;
        else if (getVectorMatrixValue(param).get(index) == null)
            return false;
        else if (getVectorMatrixValue(param).get(index).getClass() == String.class)
            return !"".equals(((String) getVectorMatrixValue(param).get(index)).trim());
        else
            return true;
    }

    public boolean isMatrixDefined(String param) {
        if (containsKey(param)) {
            Vector val = getVectorMatrixValue(param);
            return val.isEmpty() ? false : !((Vector) val.get(0)).isEmpty();
        } else
            return false;
    }

    public boolean isMatrixDefined(String param, int row, int col) {
        if (containsKey(param)) {
            Vector val = getVectorMatrixValue(param);
            return val.isEmpty() ? false : ((Vector) val.get(row)).get(col) != null;
        } else
            return false;
    }

    public String getSesName() {
        return name==null ? "paramMap" : "paramMap-"+name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }
}