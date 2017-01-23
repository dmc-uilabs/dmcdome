package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.ArrayList;

public class MakeParamGroupMenuTag extends DomeWebTagSupport {
    private String group, name, firstitem;

    public int doStartTag() throws JspException
    {
        try {
            ParameterGroup g = getParamGroupMap().getParameterGroup(group);
            List selects = new ArrayList(g.keySet());

            StringBuffer sb = new StringBuffer();
            sb.append("<select name=").append('"').append(name).append('"').append(">");
            sb.append(firstitem == null ? "" : "<option selected value=''>"+ firstitem + "</option>");

            for (int i = 0; i < selects.size(); i++) {
                String s = (String) selects.get(i);
                sb.append("<option value=").append('"').append(s).append('"').append(">");
                sb.append(s).append("</option>");
            }
            sb.append("</select>");
            printToPage(sb.toString());
            log("makes a menu: " + sb.toString() + " for group '" + group + "'");

        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstitem(String firstitem) {
        this.firstitem = firstitem;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}