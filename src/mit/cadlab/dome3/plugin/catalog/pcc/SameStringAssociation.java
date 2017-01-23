package mit.cadlab.dome3.plugin.catalog.pcc;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 25.
 */
public class SameStringAssociation extends TokenAssociation {
    private String sameValue;
    private int associatedColumnIndex;

    public SameStringAssociation(int associatedColumnIndex, String sameValue) {
        this.sameValue = sameValue;
        this.associatedColumnIndex = associatedColumnIndex;
    }

    public String toString() {
        String ret = "[SameStrAsso: value=" + sameValue + ", col=" + associatedColumnIndex + "]";
        return ret;
    }

    public String getValue() {
        return sameValue;
    }

    public int getAssociatedColumnIndex() {
        return associatedColumnIndex;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SameStringAssociation)) return false;

        final SameStringAssociation sameStringAssociation = (SameStringAssociation) o;

        if (associatedColumnIndex != sameStringAssociation.associatedColumnIndex) return false;
        if (sameValue != null ? !sameValue.equals(sameStringAssociation.sameValue) : sameStringAssociation.sameValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (sameValue != null ? sameValue.hashCode() : 0);
        result = 29 * result + associatedColumnIndex;
        return result;
    }
}
