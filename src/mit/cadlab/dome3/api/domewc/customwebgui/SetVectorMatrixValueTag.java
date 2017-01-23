package mit.cadlab.dome3.api.domewc.customwebgui;

import mit.cadlab.dome3.util.Regex;
import mit.cadlab.dome3.util.Converters;

import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.Vector;

/**
 * examples of "range": 1 1; 4 5    for a matrix
 *                      1 5         for a vector
 */
public class SetVectorMatrixValueTag extends DomeWebTagSupport {
    private String field, param, item, copyparam, value, range, parammap;
    private boolean valIsVec;   // value is a vector only when range is used

    public int doStartTag() throws JspException
    {
        try {
            ParameterMap map = parammap == null ? getParamMap() : getParamMap(parammap);
            String val = "";
            Vector valvec = new Vector();
            if (value == null) {
                if (field == null) {
                    if (copyparam == null || !map.containsKey(copyparam)) {
                        return SKIP_BODY;
                    } else {
                        if (isVectorMatrix(map.getParamType(copyparam))) {
                            valIsVec = true;
                            if (isMatrix(map.getParamType(copyparam))) {
                                log("need to clone matrix");
                                Vector old = map.getVectorMatrixValue(copyparam);
                                valvec = new Vector();
                                for (int j = 0; j < old.size(); j++) {
                                    valvec.add(((Vector) old.get(j)).clone());
                                }
                                log("the clone is " + valvec);
                            } else {
                                log("vector");
                                valvec = (Vector) (map.getVectorMatrixValue(copyparam)).clone();
                            }
                        } else {
                            valIsVec = false;
                            val = map.getStringValue(copyparam);
                        }
                    }
                } else {
                    val = (pageContext.getRequest()).getParameter(field);
                    if (val == null)
                        printError("Invalid field name: " + field);
                }
            } else
                val = value;
            if (val.equalsIgnoreCase("") && !valIsVec) {
                return SKIP_BODY;
            }

            Vector vec = map.containsKey(param) ? map.getVectorMatrixValue(param) : new Vector();
            String type = "";
            if (range == null) { // setting only one item
                List items = Regex.split(" ", item.trim());
                if (items.size() > 2)
                    printError("Invalid item selected for setting a value");

                int x = Integer.parseInt(((String) items.get(0)));
                if (vec.size() < x)
                    vec.setSize(x);

                if (items.size()==1) { // vector
                    vec.setElementAt(new Double(val), x - 1);
                    type = map.containsKey(param) ? map.getParamType(param) : "vector";
                    log("set vector item '" + Integer.toString(x-1) + "' of '" + param + "' to '" + val);
                } else { // matrix
                    int y = Integer.parseInt(((String) items.get(1)));
                    Vector row = (Vector) vec.get(x-1);
                    if (row == null) {
                        row = new Vector();
                        row.setSize(y);
                    } else if (row.size() < y) {
                        row.setSize(y);
                    }
                    row.setElementAt(new Double(val), y - 1);

                    vec.setElementAt(row, x-1);
                    type = "matrix";
                    log("set matrix item '" + Integer.toString(x - 1) + " " + Integer.toString(y - 1) + "' of '"
                            + param + "' to '" + val);
                }
            } else { // setting a range of items
                List rr = Regex.split(";", range.trim());
                if (rr.size()==1) { // vector range
                    type = map.containsKey(param) ? map.getParamType(param) : "vector";
                    List ind = Regex.split(" ", ((String) rr.get(0)).trim());
                    int in1 = Integer.parseInt((String) ind.get(0)) - 1;
                    int in2 = Integer.parseInt((String) ind.get(1)) - 1;

                    if (!valIsVec)
                        valvec = Converters.parseVector(val);

                    if (vec.size() < in2 + 1)
                        vec.setSize(in2 + 1);

                    for (int j = in1; j <= in2; j++) {
                        vec.setElementAt(valvec.get(j - in1), j);
                    }

                } else if (rr.size()==2) { // matrix range
                    type = "matrix";
                    List inda = Regex.split(" ", ((String) rr.get(0)).trim());
                    int ina1 = Integer.parseInt((String) inda.get(0)) - 1;
                    int ina2 = Integer.parseInt((String) inda.get(1)) - 1;
                    List indb = Regex.split(" ", ((String) rr.get(1)).trim());
                    int inb1 = Integer.parseInt((String) indb.get(0)) - 1;
                    int inb2 = Integer.parseInt((String) indb.get(1)) - 1;

                    if (!valIsVec)
                        valvec = Converters.parseMatrix(val);

                    if (vec.size() < inb1 + 1)
                        vec.setSize(inb1 + 1);

                    for (int i = ina1; i <= inb1; i++ ) {
                        Vector row = (Vector) vec.get(i);
                        if (row == null) {
                            row = new Vector();
                            row.setSize(inb2 + 1);
                            vec.setElementAt(row, i);
                        } else if (row.size() < inb2 + 1) {
                            row.setSize(inb2 + 1);
                        }
                        for (int j = ina2; j <= inb2; j++) {
                            row.setElementAt(((Vector) valvec.get(i - ina1)).get(j - ina2), j);
                        }
                    }
                } else
                    printError("Invalid range given: " + range);
            }
            map.setVectorMatrixValue(param, vec, type);
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

    public void setParam(String param) {
        this.param = param;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setCopyparam(String copyparam) {
        this.copyparam = copyparam;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setParammap(String parammap) {
        this.parammap = parammap;
    }
}
