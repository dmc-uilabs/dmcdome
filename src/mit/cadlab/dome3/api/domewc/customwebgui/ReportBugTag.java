package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Converters;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.jsp.JspException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportBugTag extends DomeWebTagSupport {
    private String note, reporter;

    public int doStartTag() throws JspException {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement(PROGRESS_ROOT);

            HashMap maplist = getMapList();

            // -- save data from param map
            // -- also save parammap list
            if (maplist.isEmpty()) { // only the default parameter map exists
                saveFromParamMap(getParamMap(),root);
                root.addElement(PARAMMAPLIST);  // just an empty element
            } else {
                List mapnames = new ArrayList(maplist.keySet());
                Element maplistEle = root.addElement(PARAMMAPLIST);
                for (int i = 0; i < mapnames.size(); i++) {
                    String mapname = (String) mapnames.get(i);
                    String description = (String) maplist.get(mapname);
                    Element mapEl = root.addElement(PARAMMAP).addAttribute(NAME, mapname)
                            .addAttribute(DESCRIPTION, description);
                    saveFromParamMap(getParamMap(mapname), mapEl);
                    maplistEle.addElement(MAPLISTITEM).addAttribute(NAME, mapname)
                            .addAttribute(DESCRIPTION, description);
                }
            }

            // -- save data from param group map
            ParameterGroupMap gmap = getParamGroupMap();
            List groups = new ArrayList(gmap.keySet());
            for (int i = 0; i < groups.size(); i++) {
                String gname = (String) groups.get(i);
                ParameterGroup group = gmap.getParameterGroup(gname);
                Element gEl = root.addElement(GROUP).addAttribute(NAME, gname);
                List selects = new ArrayList(group.keySet());
                for (int j = 0; j < selects.size(); j++) {
                    String sname = (String) selects.get(j);
                    Element sEl = gEl.addElement(SELECTION).addAttribute(NAME, sname);
                    Vector select = group.getSelection(sname);
                    for (int k = 0; k < select.size(); k++) {
                        Vector pair = (Vector) select.get(k);
                        String name = (String) pair.get(0);
                        Vector prop = (Vector) pair.get(1);
                        addParamNode(sEl, name, prop);
                    }
                }
                log("write parameter group to BUG-REPORT: " + NAME + "='" + gname + "'");
            }

            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            String time = formatter.format(new Date());

            log("BUG-REPORT begins");
            log("Time: " + time);
            log("Note: " + note);
            log("Reporter: " + reporter);
            log("Data: " + XMLUtils.toPrettyString(document));
            log("BUG-REPORT ends");

        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    private Element addParamNode(Element parent, String name, Vector prop) {
        String type, val;
        if (prop.size() > 1 && prop.get(1) != null) {
            type = (String) prop.get(1);
            if (isVector(type))
                val = Converters.vectorToString((Vector) prop.get(0));
            else if (isStringVector(type))
                val = Converters.stringVectorToString((Vector) prop.get(0));
            else if (isMatrix(type))
                val = Converters.matrixToString((Vector) prop.get(0));
            else
                val = (String) prop.get(0);
        } else {
            val = (String) prop.get(0);
            type = "";
        }
        Element p = parent.addElement(PARAM).addAttribute(NAME, name).addAttribute(VALUE, val);
        if (isVectorMatrix(type)) {
            p.addAttribute(TYPE, type);
        }
        return p;
    }

    private void saveFromParamMap(ParameterMap map, Element parent) {
        List names = new ArrayList(map.keySet());
        for (int i = 0; i < names.size(); i++) {
            String name = (String) names.get(i);
            Vector prop = (Vector) map.get(name);
            addParamNode(parent, name, prop);
            log("write parameter to BUG-REPORT: " + NAME + "='" + name + "', " + PROP + "='" + prop + "' from map " + map.getSesName());
        }
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }
}