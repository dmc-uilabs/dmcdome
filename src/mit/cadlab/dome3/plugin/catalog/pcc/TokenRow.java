package mit.cadlab.dome3.plugin.catalog.pcc;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 2.
 *
 * TokenRow is a unit of code completion and parameterized code editing
 * only one token row is activated for editing in an editor
 * i.e. once a user request code completion for another row.
 *      previously activated row is unactivated at that point.
 */
public class TokenRow {
    private boolean isBeingEdited;
    private CodeToken[] tokens;

    public TokenRow(CodeToken[] tokens) {
        this.tokens = tokens;
    }

    /** the token index corresponds to column index of the token matrix */
    public CodeToken get(int tokenIndex) {
        return tokens[tokenIndex];
    }

    public boolean isBeingEdited() {
        return isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[TokenRow: ");
        for (int i = 0; i < tokens.length; i++) {
            sb.append(tokens [i]);
            if (i < (tokens.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public int size() {
        return tokens.length;
    }

    public CodeToken[] getTokenArray() {
        return tokens;
    }
}
