package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

public class GoToPageTag extends DomeWebTagSupport {
    private String url;

    public int doStartTag() throws JspException
    {
        try {
            HttpServletResponse response =
                    (HttpServletResponse) pageContext.getResponse();

            response.sendRedirect(url);
            log("redirects to page: " + url);
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

}
