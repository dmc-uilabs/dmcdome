package mit.cadlab.dome3.api.domewc.customwebgui;

import java.util.HashMap;
import java.util.Vector;

/* Parameter Group is essentially a Selection Map
 * key = (String) selection name  -- e.g. boston (for "city" group), prius (for "car" group)
 * value = (vector of vectors) selection, which is < <parameterName, <parameterValue, parameterType (optional)>>,
 *                                                   <parameterName, <parameterValue, parameterType (optional)>>,
 *                                                   <parameterName, <parameterValue, parameterType (optional)>> >
 */
public class ParameterGroup extends HashMap {

    public static final String MAP_NAME = "paramGroupMap";
    protected String groupName; // e.g. city, car

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     *  sets a set of values to the specified selection
     *  all param-value pairs are overwritten
     */
    public void setSelection(String selectName, Vector selection) {
        put(selectName, selection); // overwrite all param-value pairs
    }

    /**
     * get the vector containing param-value pairs of the selection
     * return: <paramname, <value, type>>
     */
    public Vector getSelection(String selectName) {
        if (containsKey(selectName))
            return (Vector) get(selectName);
        else
            throw new RuntimeException("The specified selection '"
                    + selectName + "' does not exist in the parameter group.");
    }
}
