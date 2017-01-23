package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Converters;
import mit.cadlab.dome3.util.Regex;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *  load data from param group map (eiter in a file or a session) to param map
 */
public class InputValueGroupTag extends DomeWebTagSupport {
    private String field, name, selection, datafile, fromparamnames, asparamnames, params, parammap;

    public int doStartTag() throws JspException
    {
        try {
            if (field!=null) {  // selection may be made through a form field
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                selection = request.getParameter(field);

                log("Using selection '" + selection + "' from field '" + field + "'");
            } else if (selection == null)
                printError("No selection made for parameter group:'" + name +"'");

            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap);

            if (datafile.equalsIgnoreCase(SESSION))
                loadFromSession(map);
            else
                loadFromFile(map);
            setSesAttribute(map.getSesName(), map);
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setAsparamnames(String asparamnames) {
        this.asparamnames = asparamnames;
    }

    public void setDatafile(String datafile) {
        this.datafile = datafile;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setFromparamnames(String fromparamnames) {
        this.fromparamnames = fromparamnames;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }

    private void loadFromFile(ParameterMap map) throws JspException {
        File f = new File(pageContext.getServletContext().getRealPath("") + datafile);
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(f);
        } catch (Exception e) {
            throw new JspException(e);
        }
        Element root = document.getRootElement();

        if (!root.getName().equalsIgnoreCase(PARAM_GROUP_ROOT))
            printError("Invalid root node: " + root.getName()
                    + ".\n Please use a valid DOME Web Parameter Group file.");

        Node group = document.selectSingleNode(GROUP_XPATH + "[" + NAME_ATTRIBUTE + "='" + name + "']");
        if (group == null)
            printError("Specified parameter group: '" + name + "' does not exist in file " + datafile);

        Node select = group.selectSingleNode(SELECTION + "[" + NAME_ATTRIBUTE + "='" + selection + "']");
        if (select == null)
            printError("Specified selection: '" + selection + "' of parameter group: '" + name
                    + "' does not exist in file " + datafile);

        List list = select.selectNodes(PARAM);

        List asnames = new ArrayList();
        List fromnames = new ArrayList();
        if (asparamnames != null && fromparamnames != null) {
            asnames = Regex.split(" ", asparamnames.trim());
            fromnames = Regex.split(" ", fromparamnames.trim());
            if (asnames.size() != fromnames.size())
                printError("the numbers of names in " + fromparamnames + " and " + asparamnames + " are not the same");
        }

        List plist = new ArrayList();
        if (params != null)
            plist = Regex.split(" ", params.trim());

        for (int i = 0; i < list.size(); i++) {
            Node n = (Node) list.get(i);

            if (!plist.isEmpty()) {
                if (!plist.contains(n.valueOf(NAME_ATTRIBUTE)))
                    continue;
                else
                    plist.remove(n.valueOf(NAME_ATTRIBUTE));
            }

            String name = n.valueOf(NAME_ATTRIBUTE);

            if (!asnames.isEmpty())
                name = (String) asnames.get(fromnames.indexOf(name));

            String val = n.valueOf(VALUE_ATTRIBUTE);
            String type = n.valueOf(TYPE_ATTRIBUTE);

            if (isVector(type))
                map.setVectorMatrixValue(name, Converters.parseVector(val), type);
            else if (isMatrix(type))
                map.setVectorMatrixValue(name, Converters.parseMatrix(val), type);
            else
                map.setStringValue(name, val);
            log("load value of: " + val + " for " + name + " from " + datafile);
        }
    }

    /**
     *  loads data of selection from the param group map to the param map
     */
    private void loadFromSession(ParameterMap map) throws JspException {
        ParameterGroup pgroup = getParamGroupMap().getParameterGroup(name);
        Vector sel = new Vector();
        if (!pgroup.containsKey(selection)) {
            log("The specified selection '" + selection + "' does not exist in the parameter group.");
            return;
        } else
            sel = pgroup.getSelection(selection);

        List asnames = new ArrayList();
        List fromnames = new ArrayList();
        if (asparamnames != null && fromparamnames != null) {
            asnames = Regex.split(" ", asparamnames.trim());
            fromnames = Regex.split(" ", fromparamnames.trim());
            if (asnames.size()!=fromnames.size())
                printError("the numbers of names in " + fromparamnames + " and " + asparamnames + " are not the same");
        }

        List plist = new ArrayList();
        if (params != null)
            plist = Regex.split(" ", params.trim());

        for (int i = 0; i < sel.size(); i++) {
            Vector s = (Vector) sel.get(i);
            String p = (String)  s.get(0);
            if (!plist.isEmpty()) {
                if (!plist.contains(p))
                    continue;
                else
                    plist.remove(name);
            }
            if (!asnames.isEmpty())
                p = (String) asnames.get(fromnames.indexOf(p));

            Vector prop = (Vector) s.get(1);
            if (prop.size() > 1) {
                String type = (String) prop.get(1);
                if (isMatrix(type)) {
                    Vector old = (Vector) prop.get(0);
                    Vector new_ = new Vector();
                    for (int j = 0; j < old.size(); j++) {
                        new_.add(((Vector)old.get(j)).clone());
                    }
                    map.setVectorMatrixValue(p, new_, type);
                    log("load value of: " + new_ + " for " + p + " from " + datafile);
                } else {
                    map.setVectorMatrixValue(p, (Vector) ((Vector) prop.get(0)).clone(), type);
                    log("load value of: " + prop.get(0) + " for " + p + " from " + datafile);
                }
            } else {
                map.setStringValue(p, (String) prop.get(0));
                log("load value of: " + prop.get(0) + " for " + p + " from " + datafile);
            }
        }
    }
}
