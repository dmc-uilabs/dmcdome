package mit.cadlab.dome3.api.domewc.customwebgui;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.HashMap;

public class DomeWebTagSupport extends TagSupport {

    public static final String DOME_SESSION_NAME = "domeSession";
    static final String VECTOR = "vector";
    static final String STRINGVECTOR = "stringvector";
    static final String MATRIX = "matrix";
    static final String PARAM_XPATH = "//param";
    static final String GROUP_XPATH = "//group";
    static final String MAPLISTITEM_XPATH = "//parammaplist/maplistitem";
    static final String PARAMMAP_XPATH = "//parammap";
    static final String PARAM = "param";
    static final String PARAMMAP = "parammap";
    static final String PARAMMAPLIST = "parammaplist";
    static final String MAPLISTITEM = "maplistitem";
    static final String DESCRIPTION = "description";
    static final String GROUP = "group";
    static final String NAME = "name";
    static final String VALUE = "value";
    static final String PROP = "prop";
    static final String TYPE = "paramtype";
    static final String SELECTION = "selection";
    static final String NAME_ATTRIBUTE = "@name";
    static final String VALUE_ATTRIBUTE = "@value";
    static final String TYPE_ATTRIBUTE = "@paramtype";
    static final String DESCRIPTION_ATTRIBUTE = "@description";
    static final String PROGRESS_ROOT = "domewebprogress";
    static final String PARAM_GROUP_ROOT = "domewebparamgroup";
    static final String PROGRESS_EXT = ".dwp";
    static final String SESSION = "session";
    static final String UNDEFINED_COLOR = "#ff0000";
    static final String DEFINED_COLOR = "#009900";
    static final String LOADED_FILE = "loadedFile";
    static final String MAP_NAME = "paramMap";
    static final String MAP_LIST = "maplist";

    /**
     *  print a message to the application server log
     */
    protected void log(String msg) {
        pageContext.getServletContext().log(msg);
    }

    /**
     *  get the map containing parameter info from the session
     *  map:    key = (String) parameter name
     *          value = (Vector) (parameter value, parameter type)
     */
    protected ParameterMap getParamMap() {
        ParameterMap paramMap = (ParameterMap) getSesAttribute(MAP_NAME);
        return paramMap != null ? paramMap : new ParameterMap();
    }

    /**
     *  return information of parameter maps' name and description, not the real maps
     */
    protected HashMap getMapList() {
        HashMap maplist = (HashMap) getSesAttribute(MAP_LIST);
        return maplist != null ? maplist : new HashMap();
    }

    /**
     *  return a parameter map with the specified name
     *  use when there're multiple parameter maps in one session
     */
    protected ParameterMap getParamMap(String mapname) {
        ParameterMap paramMap = mapname.equals("") ?
                (ParameterMap) getSesAttribute(MAP_NAME) :
                (ParameterMap) getSesAttribute(MAP_NAME + "-" + mapname);
        return paramMap != null ? paramMap : new ParameterMap();
    }

    /**
     *  get the map containing groups of parameter from the session
     *  map:    key = (String) parameter group name
     *          value = (ParameterGroupMap)
     */
    protected ParameterGroupMap getParamGroupMap() {
        ParameterGroupMap groupMap = (ParameterGroupMap) getSesAttribute(ParameterGroupMap.MAP_NAME);
        return groupMap != null ? groupMap : new ParameterGroupMap();
    }

    /**
     *  set the specified attribute in the session
     */
    protected void setSesAttribute(String name, Object att) {
        pageContext.getSession().setAttribute(name, att);
    }

    /**
     *  get the specified attribute from the session
     */
    protected Object getSesAttribute(String name) {
        return pageContext.getSession().getAttribute(name);
    }

    /**
     *  print a message to the page
     */
    protected void printToPage(String msg) throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.print(msg);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
    }

    protected void printError(String msg) throws JspException {
        throw new JspException(msg);
    }

    protected boolean isVectorMatrix(String type) {
        return VECTOR.equalsIgnoreCase(type) || MATRIX.equalsIgnoreCase(type) || STRINGVECTOR.equalsIgnoreCase(type);
    }

    protected boolean isMatrix(String type) {
        return MATRIX.equalsIgnoreCase(type);
    }

    protected boolean isVector(String type) {
        return VECTOR.equalsIgnoreCase(type);
    }

    protected boolean isStringVector(String type) {
        return STRINGVECTOR.equalsIgnoreCase(type);
    }
}
