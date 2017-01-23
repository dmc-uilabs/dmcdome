package mit.cadlab.dome3.api.domewc.customwebgui;

import java.util.HashMap;

/* key = (String) group name -- e.g. city, car
 * value = (ParameterGroup)
 */
public class ParameterGroupMap extends HashMap {

    public static final String MAP_NAME = "paramGroupMap";

    public void setParameterGroup(String name, ParameterGroup paramgroup) {
        put(name, paramgroup); // overwrite all param-value pairs
    }

    public ParameterGroup getParameterGroup(String name) {
        if (!containsKey(name)) {
            ParameterGroup pgroup = new ParameterGroup();
            pgroup.setGroupName(name);
            setParameterGroup(name, pgroup);
        }
            return (ParameterGroup) get(name);
    }
}
