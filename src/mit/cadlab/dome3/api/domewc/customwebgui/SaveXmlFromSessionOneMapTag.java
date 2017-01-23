package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Converters;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SaveXmlFromSessionOneMapTag extends DomeWebTagSupport {
    private String filename = "save" + PROGRESS_EXT;
    private String mapname;

    public int doStartTag() throws JspException {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement(PROGRESS_ROOT);

            Element mapEl = root.addElement(PARAMMAP).addAttribute(NAME, mapname);
            saveFromParamMap(getParamMap(mapname), mapEl);

            File f = new File(filename);
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setIndentSize(4);
            XMLWriter writer = new XMLWriter(new FileWriter(f), format);
            writer.write(document);
            String abPath = f.getAbsolutePath();
            log("writes " + abPath);
            writer.close();

            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);

            // Send the file.
            OutputStream out = response.getOutputStream();
            returnFile(filename, out);
            out.close();

        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setFilename(String filename) {
        this.filename = filename + PROGRESS_EXT;
    }

    public void setMapname(String mapname) {
        this.mapname = mapname;
    }

    public static void returnFile(String filename, OutputStream out)
            throws FileNotFoundException, IOException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filename));
            byte[] buf = new byte[4 * 1024];  // 4K buffer
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
        } finally {
            if (in != null) in.close();
        }
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
            log("write parameter to " + filename + ": " + NAME + "='" + name + "', " + PROP + "='" + prop + "' from map " + map.getSesName());
        }
    }
}