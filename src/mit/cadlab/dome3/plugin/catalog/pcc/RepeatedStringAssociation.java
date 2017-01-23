package mit.cadlab.dome3.plugin.catalog.pcc;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 26.
 */
public class RepeatedStringAssociation extends TokenAssociation {
    private int[] associatedColumnIndexes;
    private int id;

    /**
     * create RepeatedStringAssociation from the given set containing repeated column indexes
     *
     * if [1, 4, 5] columns are in one gap association group, we want to create RepeatedStringAssociation
     * object with associatedColumnIndexes = { 1, 4, 5 }
     *
     */
    public RepeatedStringAssociation(int id, Set repeatedColumnIndexSet) {
        this.id = id;
        associatedColumnIndexes = new int[repeatedColumnIndexSet.size()];

        int counter = 0;
        Set sortedIdxSet = new TreeSet(repeatedColumnIndexSet);
        for (Iterator i = sortedIdxSet.iterator(); i.hasNext(); counter++) {
            int columnIndex = ((Integer) i.next()).intValue();
            associatedColumnIndexes[counter] = columnIndex;
        }
    }

    public String toString() {
        String ret = "[RepeatedStrAsso: id=" + id + ", ";
        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            ret = ret + "(col=" + associatedColumnIndexes[i] + ")";
            if (i != associatedColumnIndexes.length - 1) {
                ret = ret + ", ";
            }
        }
        return ret + "]";
    }

    public int[] getAssociatedColumnIndexes() {
        return associatedColumnIndexes;
    }

    public int getId() {
        return id;
    }

    public boolean equals(Object obj) {
        if (! (obj instanceof RepeatedStringAssociation)) {
            return false;
        }
        RepeatedStringAssociation comparedAsso = (RepeatedStringAssociation) obj;

        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            if (comparedAsso.getAssociatedColumnIndexes() [i] != associatedColumnIndexes[i]) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int result = 0;
        if (associatedColumnIndexes == null) {
            return 0;
        }

        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            result = result * 9 + associatedColumnIndexes[i];
        }
        return result;
    }
}
