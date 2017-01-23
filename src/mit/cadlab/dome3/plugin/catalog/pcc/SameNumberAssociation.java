package mit.cadlab.dome3.plugin.catalog.pcc;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 25.
 */
public class SameNumberAssociation extends TokenAssociation {
    private Number sameValue;
    private int associatedColumnIndex;

    public SameNumberAssociation(int associatedColumnIndex, Number sameValue) {
        this.sameValue = sameValue;
        this.associatedColumnIndex = associatedColumnIndex;
    }

    public String toString() {
        String ret = "[SameNumAsso: value=" + sameValue + ", col=" + associatedColumnIndex + "]";
        return ret;
    }

    public Number getValue() {
        return sameValue;
    }

    public int getAssociatedColumnIndex() {
        return associatedColumnIndex;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SameNumberAssociation)) return false;

        final SameNumberAssociation sameNumberAssociation = (SameNumberAssociation) o;

        if (associatedColumnIndex != sameNumberAssociation.associatedColumnIndex) return false;
        if (sameValue != null ? !sameValue.equals(sameNumberAssociation.sameValue) : sameNumberAssociation.sameValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (sameValue != null ? sameValue.hashCode() : 0);
        result = 29 * result + associatedColumnIndex;
        return result;
    }
}
