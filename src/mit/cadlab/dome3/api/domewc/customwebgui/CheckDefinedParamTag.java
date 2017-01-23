package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Regex;

import javax.servlet.jsp.JspException;
import java.util.List;

public class CheckDefinedParamTag extends DomeWebTagSupport {
    private String params, statusattr, parammap;

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap);
            List ps = Regex.split(" ", params.trim());
            for (int i = 0; i < ps.size(); i++) {
                if (!map.containsKey(ps.get(i))) {
                    log("Parameter '" + (String) ps.get(i) + "' is still not set");
                    return SKIP_BODY;
                }
            }
            map.setStringValue(statusattr, "set");
            setSesAttribute(map.getSesName(), map);
            log(statusattr + " is set");
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setStatusattr(String statusattr) {
        this.statusattr = statusattr;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }
}
