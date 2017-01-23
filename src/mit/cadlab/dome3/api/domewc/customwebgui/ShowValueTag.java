package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Regex;

import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.Vector;

public class ShowValueTag extends DomeWebTagSupport {
    private String param, item, defaultvalue, parammap;

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap); // get map from session
            String stringval;
            if (map.containsKey(param)) {
                String type = map.getParamType(param);
                if (isVectorMatrix(type)) {
                    if (item==null)
                        stringval = map.getVectorMatrixValue(param).toString();
                    else if (isMatrix(type)) {
                        List co = Regex.split(" ", item.trim());
                        stringval = ((Vector) map.getVectorMatrixValue(param).
                                get(Integer.parseInt((String)co.get(0)) - 1)).
                                get(Integer.parseInt((String) co.get(1)) - 1).toString();
                    } else
                        stringval = map.getVectorMatrixValue(param).get(Integer.parseInt(item)-1).toString();
                } else
                    stringval = map.getStringValue(param);
                stringval = stringval.equalsIgnoreCase("null")?"":stringval;
            } else {
                stringval = defaultvalue==null?"":defaultvalue;
            }
            printToPage(stringval);
            log("shows value of " + stringval + " for " + param + " (default value is " + defaultvalue);
            if (item!=null)
                log("   for item " + item);
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }
}
