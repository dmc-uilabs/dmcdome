package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Regex;

import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.Vector;

public class ShowSelectedValueTag extends DomeWebTagSupport {
    private String param, value, item, parammap;

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap);
            if (map.containsKey(param)) {
                if (item == null) {
                    if (map.getStringValue(param).equalsIgnoreCase(value))
                        printToPage("selected");
                    else
                        return SKIP_BODY;
                } else {
                    String type = map.getParamType(param);
                    if (isVector(type)) {
                        if (((Double) map.getVectorMatrixValue(param).get(Integer.parseInt(item)-1)).doubleValue()
                                == Double.parseDouble(value))
                            printToPage("selected");
                        else
                            return SKIP_BODY;
                    } else if (isMatrix(type)) {
                        List ind = Regex.split(" ", item);
                        if (((Double) ((Vector) map.getVectorMatrixValue(param).get(Integer.parseInt((String) ind.get(0))-1)).
                                get(Integer.parseInt((String) ind.get(1))-1)).doubleValue() == Double.parseDouble(value))
                            printToPage("selected");
                        else
                            return SKIP_BODY;
                    } else {
                        throw new JspException("item can only be applied to a vector or matrix parameter");
                    }
                }
            } else
                return SKIP_BODY;
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setValue(String text) {
        this.value = text;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }
}
