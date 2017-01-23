package mit.cadlab.dome3.plugin.catalog.pcc;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 16.
 */
public class CodeReference {

    /* NULL_REFERENCE is used for debuging and testing of TokenMatrix */
    public final static CodeReference NULL_REFERENCE = new CodeReference("", 0);
    private String srcId;
    private int lineNo;

    public CodeReference(String srcId, int lineNo) {
        this.srcId = srcId;
        this.lineNo = lineNo;
    }

    public String getSourceId() {
        return srcId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeReference)) return false;
        return this.getSourceId().equals(((CodeReference) o).getSourceId()) && (this.getLineNo() == ((CodeReference) o).getLineNo());
    }

    public int hashCode() {
        int result;
        result = srcId.hashCode();
        result = 29 * result + lineNo;
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[CodeRef: srcid=" + srcId + ", lineno=" + lineNo + "]");
        return sb.toString();
    }
}
