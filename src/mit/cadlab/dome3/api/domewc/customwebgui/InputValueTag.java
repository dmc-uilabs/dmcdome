package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Regex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

public class InputValueTag extends DomeWebTagSupport {
    private String field, param, paramtype, parammap;
    private int matrixRows = 0, matrixCols = 0;
    private int vectorLength = 0;

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            if (VECTOR.equalsIgnoreCase(paramtype)) {
                if (vectorLength > 0) {
                    doStartVectorWithLength(request);
                } else {
                    doStartVector(request);
                }
            } else if (MATRIX.equalsIgnoreCase(paramtype)) {
                if (matrixRows > 0 && matrixCols > 0) {
                    doStartMatrixWithRowsAndCols(request);
                } else {
                    doStartMatrix(request);
                }
            } else {
                doStartNormal(request);
            }
        } catch (Exception e) {
            log("ERROR: " + e);
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public void setSize(String vectorOrMatrixSizeStr) {
        if (vectorOrMatrixSizeStr.indexOf(",") != -1 || vectorOrMatrixSizeStr.indexOf("x") != -1) {
            String[] rowsAndCols = vectorOrMatrixSizeStr.split(",|x");
            this.matrixRows = Integer.parseInt(rowsAndCols[0]);
            this.matrixCols = Integer.parseInt(rowsAndCols[1]);
        } else {
            this.vectorLength = Integer.parseInt(vectorOrMatrixSizeStr);
        }
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setParamtype(String paramtype) {
        this.paramtype = paramtype;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }

    private void doStartNormal(HttpServletRequest request) throws JspException {
        String stringval = request.getParameter(field);
        if (stringval == null)
            printError("The value of the specified field '" + field
                    + "' (associated with parameter '" + param + "') is not given.");
        if (stringval.equalsIgnoreCase("")) {
            log("The value of the specified field '" + field
                    + "' (associated with parameter '" + param + "') is empty.");
            return;
        }
        ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap); // get map from session
        map.setStringValue(param, stringval);

        setSesAttribute(map.getSesName(), map);
        log("stores value of: " + stringval + " for " + param + " into " + parammap);
    }

    /**
     * need to process a series of inputs from vector fields
     */
    private void doStartVector(HttpServletRequest request) throws JspException {
        List fields = Regex.split(" ", field.trim());

        Vector vals = new Vector();
        for (int i = 0; i < fields.size(); i++) {
            String f = (String) fields.get(i);
            String strval = request.getParameter(f);
            if (strval == null)
                printError("The value of the specified field '" + f
                        + "' (associated with vector parameter '" + param + "') is not given.");
            vals.add(new Double(strval));
        }
        ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap); // get map from session
        map.setVectorMatrixValue(param, vals, VECTOR);

        setSesAttribute(map.getSesName(), map);
        log("stores value of: " + vals + " for " + param + " into " + parammap);
    }

    private void doStartVectorWithLength(HttpServletRequest request) throws JspException {
        List fields = new ArrayList();
        for (int i = 0; i < vectorLength; i++) {
            // naming convention for vector parameters
            // (ex) for <domeweb:inputvalue vectorLength="3" field="name" param="nameParam"/>
            //      parameter 'names' will be populated from field 'name:' + index. Therefore
            //      <input type="text" name="name:1" value="john">
            //      <input type="text" name="name:2" value="bob">
            //      <input type="text" name="name:3" value="kim">
            //      will create nameParam = { "john", "bob", "kim" };
            fields.add(field.trim() + ":" + (i + 1));
        }

        Vector vals = new Vector();
        for (int i = 0; i < fields.size(); i++) {
            String f = (String) fields.get(i);
            String strval = request.getParameter(f);
            if (strval == null)
                printError("The value of the specified field '" + f
                        + "' (associated with vector parameter '" + param + "') is not given.");
            vals.add(new Double(strval));
        }
        ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap); // get map from session
        map.setVectorMatrixValue(param, vals, VECTOR);

        setSesAttribute(map.getSesName(), map);
        log("stores value of: " + vals + " for " + param + " into " + parammap);
    }

    /**
     * need to process a series of inputs from matrix fields
     */
    private void doStartMatrix(HttpServletRequest request) throws JspException {


        Vector mat = new Vector();
        List rows = Regex.split(";", field.trim());
        for (int i = 0; i < rows.size(); i++) {
            String r = (String) rows.get(i);
            Vector row = new Vector();
            List fields = Regex.split(" ", r.trim());
            for (int j = 0; j < fields.size(); j++) {
                String f = (String) fields.get(j);
                String strval = request.getParameter(f);
                if (strval == null)
                    printError("The value of the specified field '" + f
                            + "' (associated with matrix parameter '" + param + "') is not given.");
                row.add(new Double(strval));
            }
            mat.add(row);
        }
        ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap); // get map from session
        map.setVectorMatrixValue(param, mat, MATRIX);

        setSesAttribute(map.getSesName(), map);
        log("stores value of: " + mat + " for " + param + " into " + parammap);
    }

    /**
     * need to process a series of inputs from matrix fields
     */
    private void doStartMatrixWithRowsAndCols(HttpServletRequest request) throws JspException {
        Vector mat = new Vector();
        for (int i = 0; i < matrixRows; i++) {
            // naming convention for matrix parameters
            // (ex) for <domeweb:inputvalue size="2x3" field="mat" param="matrixParam"/>
            //      parameter 'names' will be populated from field 'name:' + index. Therefore
            //      <input type="text" name="mat:1:1" value="a">
            //      <input type="text" name="mat:1:2" value="b">
            //      <input type="text" name="mat:1:3" value="c">
            //      <input type="text" name="mat:2:1" value="d">
            //      <input type="text" name="mat:2:2" value="e">
            //      <input type="text" name="mat:2:3" value="f">
            //
            //      will create matrixParam = [ "a", "b", "c" ]
            //                                [ "d", "e", "f" ]
            Vector row = new Vector();
            for (int j = 0; j < matrixCols; j++) {
                String fieldName = field.trim() + ":" + (i + 1) + ":" + (j + 1);
                String strval = request.getParameter(fieldName);
                if (strval == null)
                    printError("The value of the specified field '" + fieldName
                            + "' (associated with matrix parameter '" + param + "') is not given.");
                row.add(new Double(strval));
            }
            mat.add(row);
        }
        ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap); // get map from session
        map.setVectorMatrixValue(param, mat, MATRIX);

        setSesAttribute(map.getSesName(), map);
        log("stores value of: " + mat + " for " + param + " into " + parammap);
    }
}
