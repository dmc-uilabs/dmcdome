package mit.cadlab.dome3.plugin.catalog.pcc;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 5.
 */
public class CodeToken {
    public static final int DELIM_TYPE = 0;
    public static final int STRING_TYPE = 1;
    public static final int INTEGER_TYPE = 2;
    public static final int DOUBLE_TYPE = 3;
    public static final int UNKNOWN_TYPE = 4;

    private int startIdx;
    private int endIdx;
    private Object tokenObj;
    private boolean isDelim;
    private Object expectedObj;

    /**
     * this constructor is used for code tokens which are served as inferernce basis of code completion
     */
    public CodeToken(int startIdx, int endIdx, Object tokenObj, boolean isDelim) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.tokenObj = tokenObj;
        this.isDelim = isDelim;
        this.expectedObj = null;
    }

    /**
     * this constructor is used for code tokens which are automatically completed and thereby have expected values
     */
    public CodeToken(int startIdx, int endIdx, Object tokenObj, Object expectedObj, boolean isDelim) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.tokenObj = tokenObj;
        this.isDelim = isDelim;
        this.expectedObj = expectedObj;
    }

    public int getStartIdx() {
        return startIdx;
    }

    public int getEndIdx() {
        return endIdx;
    }

    public Object getExpectedObject() {
        return expectedObj;
    }

    public void setExpectedObject(Object expectedObj) {
        this.expectedObj = expectedObj;
    }

    public Object getTokenObject() {
        return tokenObj;
    }

    /**
     * this method also updates end indexes of this token when its value is changed to newTokenObj
     * the returned int value indicate the difference between previous end index and new end index.
     * if non-zero valu, say one, is returned, we need to change the start and end indexes of all tokens
     * that follow this token until the end of the line; moveIndex(returnedIntValue) serves that purpose.
     */
    public int setTokenObject(Object newTokenObj) {
        this.tokenObj = newTokenObj;
        int previousEndIdx = endIdx;
        this.endIdx = startIdx + tokenObj.toString().length();
        return endIdx - previousEndIdx;
    }

    /** if +1 is given as an argument, the start and end indexes of this token is increased by one. this method should be called for tokens that is */
    public void moveIndex(int shiftSize) {
        this.startIdx = startIdx + shiftSize;
        this.endIdx = endIdx + shiftSize;
    }

    public int getInt() {
        if (tokenObj instanceof Integer) {
            return ((Integer) tokenObj).intValue();
        }
        throw new RuntimeException("CodeToken object is not an instance of Integer: " + tokenObj);
    }

    public double getDouble() {
        if (tokenObj instanceof Double) {
            return ((Double) tokenObj).doubleValue();
        }
        throw new RuntimeException("CodeToken object is not an instance of Double: " + tokenObj);
    }

    /**
     * assuming tokenObj is String, cast it to String and return it.
     * when this method is called against Integer or Double tokenObj, it will invoke ClassCastException
     * @return
     */
    public String getString() {
        return (String) tokenObj;
    }

    /**
     * assuming tokenObj is either Integer or Double, cast it to Number and return it.
     * when this method is called against String tokenObj, it will invoke ClassCastException
     * @return
     */
    public Number getNumber() {
        return (Number) tokenObj;
    }

    public boolean isDelim() {
        return isDelim;
    }

    public String toString() {
        String type = "string";
        if (isDelim) {
            type = "delim";
        } else if (tokenObj instanceof String) {
            type = "string";
        } else if (tokenObj instanceof Integer) {
            type = "integer";
        } else if (tokenObj instanceof Double) {
            type = "double";
        } else {
            type = "unknown";
        }
        return "[CodeToken: index=" + startIdx + "-" + endIdx + ", " +  type + "=" + tokenObj + "]";
    }

    public static String convertColumnTypeToString(int columnType) {
        if (columnType == DELIM_TYPE) {
            return "delim";
        } else if (columnType == STRING_TYPE) {
            return "string";
        } else if (columnType == INTEGER_TYPE) {
            return "integer";
        } else if (columnType == DOUBLE_TYPE) {
            return "double";
        } else {
            return "unknown";
        }
    }

    /**
     * token has three data type
     * all tokens belong to one column have the same data type
     * so we can tell 'the column data type' just by checking the data type of a token in the first row.
     * @return
     */
    public int getDataType() {
        if (isDelim) {
            return DELIM_TYPE;
        } else if (tokenObj instanceof String) {
            return STRING_TYPE;
        } else if (tokenObj instanceof Integer) {
            return INTEGER_TYPE;
        } else if (tokenObj instanceof Double) {
            return DOUBLE_TYPE;
        } else {
            return UNKNOWN_TYPE;
        }
    }
}
