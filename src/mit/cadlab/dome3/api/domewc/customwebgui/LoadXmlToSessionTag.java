package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.util.Converters;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.InputStream;
import java.util.*;

public class LoadXmlToSessionTag extends DomeWebTagSupport {

    public int doStartTag() throws JspException {
        try {
            DiskFileUpload fu = new DiskFileUpload();
            // If file size exceeds, a FileUploadException will be thrown
            fu.setSizeMax(1000000);

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            List items = fu.parseRequest(request);

            ParameterGroupMap gmap = getParamGroupMap();

            //todo remove all attributes from sessions?

            String names = "";
            // Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {
                    InputStream uploadedStream = item.getInputStream();
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(uploadedStream);
                    names = names + item.getName() + " ";

                    // get the parammap list
                    List maplistnodes = document.selectNodes(MAPLISTITEM_XPATH);
                    HashMap maplist = new HashMap();
                    for (int i = 0; i < maplistnodes.size(); i++) {
                        Node n = (Node) maplistnodes.get(i);
                        maplist.put(n.valueOf(NAME_ATTRIBUTE), n.valueOf(DESCRIPTION_ATTRIBUTE));
                    }
                    setSesAttribute(MAP_LIST, maplist);

                    // load data to param map
                    if (maplist.isEmpty()) { // only the default parameter map exists
                        ParameterMap map = getParamMap();
                        List list = document.selectNodes(PARAM_XPATH);
                        for (Iterator it = list.iterator(); it.hasNext();) {
                            Node n = (Node) it.next();
                            String name = n.valueOf(NAME_ATTRIBUTE);
                            String val = n.valueOf(VALUE_ATTRIBUTE);
                            String type = n.valueOf(TYPE_ATTRIBUTE);
                            if (isVector(type))
                                map.setVectorMatrixValue(name, Converters.parseVector(val), type);
                            else if (isStringVector(type))
                                map.setVectorMatrixValue(name, Converters.parseStringVector(val), type);
                            else if (isMatrix(type))
                                map.setVectorMatrixValue(name, Converters.parseMatrix(val), type);
                            else
                                map.setStringValue(name, val);
                            setSesAttribute(map.getSesName(), map);
                            log("load value of: " + val + " for " + name + " from " + item.getName());
                        }
                    } else {
                        List mapnames = new ArrayList(maplist.keySet());
                        for (int i = 0; i < mapnames.size(); i++) {
                            String mapname = (String) mapnames.get(i);
                            log("mapname: " + mapname);
                            ParameterMap map = new ParameterMap();
                            map.setName(mapname);
                            Node mapnode = document.selectSingleNode(PARAMMAP_XPATH + "[@name='"+mapname+"']");
                            List list = mapnode.selectNodes(PARAM);
                            for (Iterator it = list.iterator(); it.hasNext();) {
                                Node n = (Node) it.next();
                                String name = n.valueOf(NAME_ATTRIBUTE);
                                String val = n.valueOf(VALUE_ATTRIBUTE);
                                String type = n.valueOf(TYPE_ATTRIBUTE);
                                if (isVector(type))
                                    map.setVectorMatrixValue(name, Converters.parseVector(val), type);
                                else if (isStringVector(type))
                                    map.setVectorMatrixValue(name, Converters.parseStringVector(val), type);
                                else if (isMatrix(type))
                                    map.setVectorMatrixValue(name, Converters.parseMatrix(val), type);
                                else
                                    map.setStringValue(name, val);
                                setSesAttribute(map.getSesName(), map);
                                log("load value of: " + val + " for " + name + " from " + item.getName() + " to map " + mapname);
                            }
                        }
                    }

                    // load data to param group map
                    List glist = document.selectNodes(GROUP_XPATH);
                    for (Iterator it = glist.iterator(); it.hasNext();) {
                        Node g = (Node) it.next();
                        ParameterGroup pgroup = gmap.getParameterGroup(g.valueOf(NAME_ATTRIBUTE));

                        List slist = g.selectNodes(SELECTION);
                        for (int i = 0; i < slist.size(); i++) {
                            Node s = (Node) slist.get(i);
                            List pairs = s.selectNodes(PARAM);
                            Vector select = new Vector();
                            for (int j = 0; j < pairs.size(); j++) {
                                Node p = (Node) pairs.get(j);
                                String type = p.valueOf(TYPE_ATTRIBUTE);
                                Vector prop;
                                if (isVector(type))
                                    prop = Vectors.create(Converters.parseVector(p.valueOf(VALUE_ATTRIBUTE)), type);
                                else if (isStringVector(type))
                                    prop = Vectors.create(Converters.parseStringVector(p.valueOf(VALUE_ATTRIBUTE)), type);
                                else if (isMatrix(type))
                                    prop = Vectors.create(Converters.parseMatrix(p.valueOf(VALUE_ATTRIBUTE)), type);
                                else
                                    prop = Vectors.create(p.valueOf(VALUE_ATTRIBUTE));
                                select.add(Vectors.create(p.valueOf(NAME_ATTRIBUTE), prop));
                            }
                            pgroup.setSelection(s.valueOf(NAME_ATTRIBUTE), select);
                            log("load value group of: " + select + " for " + s.valueOf(NAME_ATTRIBUTE)
                                    + " into group " + g.valueOf(NAME_ATTRIBUTE) + " from " + item.getName());
                        }
                    }

                    uploadedStream.close();
                    pageContext.getSession().setAttribute(LOADED_FILE, names);
                } else {
                    // do what is needed
                    // String name = item.getFieldName();
                    // String value = item.getString();
                }
            }
            setSesAttribute(ParameterGroupMap.MAP_NAME, gmap);

        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
