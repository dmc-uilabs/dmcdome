package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.jsp.JspException;

public class AddToSessionTag extends DomeWebTagSupport {
    private String param, parammap;

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = parammap==null ? getParamMap() : getParamMap(parammap);
            if (map.containsKey(param)) {
                setSesAttribute(param, map.getRawValue(param));
                log("adds parameter '" + param + "' to the session as an attribute.");
            } else {
                log("could not add parameter '" + param + "' to the session as an attribute.");
                return SKIP_BODY;
            }
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }
}
