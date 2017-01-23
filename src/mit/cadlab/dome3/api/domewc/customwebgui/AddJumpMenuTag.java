package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.jsp.JspException;

public class AddJumpMenuTag extends DomeWebTagSupport {

    public int doStartTag() throws JspException
    {
        try {
            printToPage("<script language='JavaScript' type='text/JavaScript'>");
            printToPage("function MM_jumpMenu(targ,selObj,restore){");
            StringBuffer sb = new StringBuffer();
            sb.append("  eval(targ+");
            sb.append('"');
            sb.append(".location='");
            sb.append('"');
            sb.append("+selObj.options[selObj.selectedIndex].value+");
            sb.append('"');
            sb.append("'");
            sb.append('"');
            sb.append(");");
            printToPage(sb.toString());
            printToPage("  if (restore) selObj.selectedIndex=0;}</script>");

        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
