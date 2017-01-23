package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Regex;

import javax.servlet.jsp.JspException;
import java.util.List;

public class ColorStatusTag extends DomeWebTagSupport {
    private String param, text, type, item, parammap;
    private String definedcolor = DEFINED_COLOR;
    private String undefinedcolor = UNDEFINED_COLOR;

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap);
            StringBuffer sb = new StringBuffer();
            sb.append("<span style='color:");
            if (!isVectorMatrix(type))
                sb.append(map.isStringDefined(param) ? definedcolor : undefinedcolor);
            else if (isVector(type) || isStringVector(type)) {
                if (item == null)
                    sb.append(map.isVectorDefined(param) ? definedcolor : undefinedcolor);
                else
                    sb.append(map.isVectorDefined(param, Integer.parseInt(item)-1) ? definedcolor : undefinedcolor);
            } else if (isMatrix(type)) {
                if (item == null)
                    sb.append(map.isMatrixDefined(param) ? definedcolor : undefinedcolor);
                else {
                    List ind = Regex.split(" ", item);
                    sb.append(map.isMatrixDefined(param, Integer.parseInt((String) ind.get(0))-1, Integer.parseInt((String) ind.get(1)) - 1) ? definedcolor : undefinedcolor);
                }
            }
            sb.append(";' > ").append(text).append("</span>");
            printToPage(sb.toString());
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDefinedcolor(String definedcolor) {
        this.definedcolor = definedcolor;
    }

    public void setUndefinedcolor(String undefinedcolor) {
        this.undefinedcolor = undefinedcolor;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }
}