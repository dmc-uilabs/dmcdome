package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.util.Regex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

/**
 *  put data into the param group map
 */
public class SaveValueGroupTag extends DomeWebTagSupport {
    private String name, selection, params, selectionfield, asparamnames, parammap;

    public int doStartTag() throws JspException
    {
        try {
            String sel = selection;
            if (selection==null) {
                if (selectionfield==null) {
                    log("The selection name is not provided; therefore the data is not saved to the parameter group '"
                            + name + "'");
                    return SKIP_BODY;
                } else {
                    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                    sel = request.getParameter(selectionfield).trim();
                    if (sel.equalsIgnoreCase("")) {
                        log("The selection name is not provided; therefore the data is not saved to the parameter group '"
                                + name + "'");
                        return SKIP_BODY;
                    }
                }
            }

            ParameterGroupMap pgmap = getParamGroupMap();
            ParameterGroup pgroup = pgmap.getParameterGroup(name);

            Vector select = new Vector();
            ParameterMap pmap = parammap == null ? getParamMap() : getParamMap(parammap);
            List ps = Regex.split(" ", params.trim());
            List asnames = new ArrayList();
            if (asparamnames!=null)
                asnames = Regex.split(" ", asparamnames.trim());

            // select's structure = < <param name, <val, type>>,
            //                        <param name, <val, type>>,
            //                        <param name, <val, type>> >
            for (int i = 0; i < ps.size(); i++) {
                String p = (String) ps.get(i);
                String type = pmap.getParamType(p);
                if (isVectorMatrix(type)) {
                    Vector val;
                    if (isMatrix(type)) {
                        Vector old = pmap.getVectorMatrixValue(p);
                        val = new Vector();
                        for (int j = 0; j < old.size(); j++) {
                            val.add(((Vector) old.get(j)).clone());
                        }
                    } else
                        val = (Vector) pmap.getVectorMatrixValue(p).clone();
                    if (val == null)
                        printError("Cannot save this parameter group. The value of the specified parameter '" + p
                                + "' is not yet set (to the parameter map).");
                    select.add(Vectors.create(asparamnames == null ? p : (String) asnames.get(i), Vectors.create(val, type)));
                } else {
                    String val = pmap.getStringValue(p);
                    if (val == null)
                        printError("Cannot save this parameter group. The value of the specified parameter '" + p
                                + "' is not yet set (to the parameter map).");
                    select.add(Vectors.create(asparamnames == null ? p : (String) asnames.get(i), Vectors.create(val)));
                }
            }
            pgroup.setSelection(sel, select);
            log("adds selection '" + sel + "' with data=" + select + " to the group '" + name + "'");

            setSesAttribute(ParameterGroupMap.MAP_NAME, pgmap);
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setSelectionfield(String selectionfield) {
        this.selectionfield = selectionfield;
    }

    public void setAsparamnames(String asparamnames) {
        this.asparamnames = asparamnames;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }

}
