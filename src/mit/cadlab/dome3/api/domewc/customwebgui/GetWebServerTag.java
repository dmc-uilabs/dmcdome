package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Handler for the "getWebServer" tag
 */
public class GetWebServerTag extends TagSupport {
    public int doStartTag() throws JspException {
        try {
// Get the request object from the page context
            HttpServletRequest request =
                    (HttpServletRequest) pageContext.getRequest();
// Request information from web server
            URL url = new URL("http",
                    request.getServerName(),
                    request.getServerPort(),
                    "/");
            URLConnection con = url.openConnection();
            ((HttpURLConnection) con).setRequestMethod("OPTIONS");
            String webserver = con.getHeaderField("server");
// Write it to the output stream
            JspWriter out = pageContext.getOut();
            out.print(webserver);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
