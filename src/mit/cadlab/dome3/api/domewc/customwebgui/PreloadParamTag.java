package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Converters;

import javax.servlet.jsp.JspException;

public class PreloadParamTag extends DomeWebTagSupport {
    private String value, param, type, parammap;

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap);
            if (isVector(type))
                map.setVectorMatrixValue(param, Converters.parseVector(value), type);
            else if (isMatrix(type))
                map.setVectorMatrixValue(param, Converters.parseMatrix(value), type);
            else
                map.setStringValue(param, value);
            setSesAttribute(map.getSesName(), map);

        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }
}
