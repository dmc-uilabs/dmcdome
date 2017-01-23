package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.jsp.JspException;
import java.util.Vector;

public class MakeJumpMenuTag extends DomeWebTagSupport {
    private String count, countparam, textprefix, linkprefix, linkvar, name, firstitem, forcenew, namelist, parammap;
    private ParameterMap map;
    private Vector nlist;

    public int doStartTag() throws JspException
    {
        try {
            map = parammap == null ? getParamMap() : getParamMap(parammap);
            if (map.containsKey(name) && !"true".equalsIgnoreCase(forcenew)) {
                log("Loads an existing jump menu: " + name);
                printToPage(map.getStringValue(name));
            } else {
                log("Creates a new jump menu: " + name);
                if (namelist != null)
                    nlist = map.getVectorMatrixValue(namelist);
                makeNewMenu();
            }
            setSesAttribute(map.getSesName(), map);

        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setCountparam(String countparam) {
        this.countparam = countparam;
    }

    public void setTextprefix(String textprefix) {
        this.textprefix = textprefix;
    }

    public void setLinkprefix(String linkprefix) {
        this.linkprefix = linkprefix;
    }

    public void setLinkvar(String linkvar) {
        this.linkvar = linkvar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstitem(String firstitem) {
        this.firstitem = firstitem;
    }

    public void setForcenew(String forcenew) {
        this.forcenew = forcenew;
    }

    public void setNamelist(String namelist) {
        this.namelist = namelist;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }

    private void makeNewMenu() throws JspException {
        StringBuffer sb = new StringBuffer();
        sb.append("<select name=").append('"').append(name).append('"');
        sb.append(" onChange=").append('"').append("MM_jumpMenu('parent',this,0)").append('"').append(">");

        int c = 0;
        if (countparam == null) {
            if (count == null)
                printError("The number of items is unspecified for the menu: " + name);
            else
                c = Integer.parseInt(count);
        } else
            c = Integer.parseInt(map.getStringValue(countparam));

        sb.append("<option selected  value='#'>").append(firstitem == null ? " " : firstitem).append("</option>");
        for (int i = 1; i < c + 1; i++) {
            sb.append("<option value=").append('"').append(linkprefix);
            sb.append("?").append(linkvar.trim()).append("=").append(i);
            sb.append('"').append(">").append(textprefix).append(" ").append(i);
            if (nlist != null && i<=nlist.size() && nlist.get(i - 1) != null)
                sb.append(" - ").append(nlist.get(i-1)).append(" ");
            sb.append("</option>");
        }
        sb.append("</select>");
        printToPage(sb.toString());

        map.setStringValue(name, sb.toString());
        log("stores value of: " + sb.toString() + " for " + name);
    }
}
