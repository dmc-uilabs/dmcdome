package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Regex;

import javax.servlet.jsp.JspException;
import java.util.List;

public class IncludePagesTag extends DomeWebTagSupport {
    private String relativepath, count, incrementvar, fixedvars, fixedvalues;

    public int doStartTag() throws JspException
    {
        try {
            StringBuffer varclause = new StringBuffer();
            if (fixedvars != null) {
                List svars = Regex.split(" ", fixedvars);
                List svals = Regex.split(" ", fixedvalues);
                if (svars.size() != svals.size())
                    printError("Cannot include page '" + relativepath + "'.\n " +
                            "The number of given fixed variables and values are not the same.");

                for (int j = 0; j < svars.size(); j++) {
                    varclause.append(j == 0 && incrementvar == null ? "?" : "&");
                    varclause.append((String) svars.get(j)).append("=").append((String) svals.get(j));
                }
            }

            for (int i = 0; i < Integer.parseInt(count); i++) {
                StringBuffer sb = new StringBuffer();
                sb.append(relativepath);

                if (incrementvar!=null)
                    sb.append("?").append(incrementvar).append("=").append(i+1);

                sb.append(varclause);

                pageContext.include(sb.toString());
                log("includes page: " + sb.toString());
            }
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setRelativepath(String relativepath)
    {
        this.relativepath = relativepath;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setIncrementvar(String incrementvar) {
        this.incrementvar = incrementvar;
    }

    public void setFixedvars(String fixedvars) {
        this.fixedvars = fixedvars;
    }

    public void setFixedvalues(String fixedvalues) {
        this.fixedvalues = fixedvalues;
    }
}
