package mit.cadlab.dome3.plugin.catalog.pcc;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 16.
 */
public class CodeLine implements Comparable {

    private String codeStr;
    private CodeReference codeRef;

    public CodeLine(String codeStr, CodeReference codeRef) {
        this.codeStr = codeStr;
        this.codeRef = codeRef;
    }

    public CodeReference getCodeReference() {
        return codeRef;
    }

    public String getCodeString() {
        return codeStr;
    }

    public int compareTo(Object obj) {
        if (! (obj instanceof CodeLine)) {
            throw new RuntimeException("CodeLine instance is only comparable with CodeLine instances. It has been compared with " + obj);
        }

        CodeLine compared = (CodeLine) obj;
        //return (this.getCodeReference().getSourceId().hashCode() * 10000 + this.getCodeReference().getLineNo() * 100 + this.getCodeString().hashCode()) - (compared.getCodeReference().getSourceId().hashCode() * 10000 + compared.getCodeReference().getLineNo() * 100 + compared.getCodeString().hashCode());
        return this.getCodeReference().hashCode() - compared.getCodeReference().hashCode();
    }

    public boolean equals(Object obj) {
        if (! (obj instanceof CodeLine)) {
            return false;
        }
        System.out.println("first:" + codeRef.equals(((CodeLine) obj).getCodeReference()));
        System.out.println("second:" + this.getCodeString().equals(((CodeLine) obj).getCodeString()));
        //return codeRef.equals(((CodeLine) obj).getCodeReference()) && (this.getCodeString().equals(((CodeLine) obj).getCodeString()));
        return codeRef.equals(((CodeLine) obj).getCodeReference());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[CodeLine: srcid=" + getCodeReference().getSourceId()).append(", lineno=" + getCodeReference().getLineNo()).append(", code=").append(getCodeString()).append("]");
        return sb.toString();
    }
}
