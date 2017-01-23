package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.jsp.JspException;
import java.util.HashMap;

public class AddParameterMapTag extends DomeWebTagSupport {
    private String name, description;

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = new ParameterMap();
            map.setName(name);
            HashMap maplist = getMapList();
            maplist.put(name, description);
            setSesAttribute(MAP_NAME + "-" + name, map);
            setSesAttribute(MAP_LIST, maplist);
            log("Add parameter map '" + name + "' to session");
            log("Add parameter map list to session");
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
