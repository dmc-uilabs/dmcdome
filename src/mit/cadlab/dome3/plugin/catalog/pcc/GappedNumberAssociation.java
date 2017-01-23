package mit.cadlab.dome3.plugin.catalog.pcc;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 25.
 */
public class GappedNumberAssociation extends TokenAssociation {
    private int[] associatedColumnIndexes;
    private int[] gapSizes; // normalized so that the minimum gap size is zero
    private int id; // used to identify GappedNumberAssociation in a rule in text format

    /**
     * create GappedNumberAssociation from the given set containing gapped column indexes and
     * codeTokens object which references values of gapped columns
     *
     * note that GappedNumberAssociation is always about integer-datatype columns
     *
     * if [1, 4, 5] columns are in one gap association group and
     * their values are 5, 5, 8 respectively, we want to create GappedNumberAssociation
     * object with associatedColumnIndexes = { 1, 4, 5 } and gapSizes = { 0, 0, 3 }
     *
     */
    public GappedNumberAssociation(int id, Set gappedColumnIndexSet, CodeToken[] codeTokens) {
        associatedColumnIndexes = new int[gappedColumnIndexSet.size()];
        gapSizes = new int[gappedColumnIndexSet.size()];
        this.id = id;

        int counter = 0;
        int minGapSize = Integer.MAX_VALUE;
        Set sortedIdxSet = new TreeSet(gappedColumnIndexSet);
        for (Iterator i = sortedIdxSet.iterator(); i.hasNext(); counter++) {
            int columnIndex = ((Integer) i.next()).intValue();
            associatedColumnIndexes[counter] = columnIndex;
            gapSizes[counter] = codeTokens[columnIndex].getInt();
            /* track min of gapSizes. to normalize gapSizes, we  later substracted min of gap size from gapSizes array */
            if (minGapSize > gapSizes[counter]) {
                minGapSize = gapSizes[counter];
            }
        }

        for (int j = 0; j < gapSizes.length; j++) {
            gapSizes[j] = gapSizes[j] - minGapSize;
        }
    }

    public GappedNumberAssociation(int id, Set gappedColumnIndexSet, GappedNumberAssociation srcAssociation) {
        associatedColumnIndexes = new int[gappedColumnIndexSet.size()];
        gapSizes = new int[gappedColumnIndexSet.size()];
        this.id = id;

        int counter = 0;
        int minGapSize = Integer.MAX_VALUE;
        Set sortedIdxSet = new TreeSet(gappedColumnIndexSet);
        for (Iterator i = sortedIdxSet.iterator(); i.hasNext(); counter++) {
            int columnIndex = ((Integer) i.next()).intValue();
            associatedColumnIndexes[counter] = columnIndex;
            gapSizes[counter] = srcAssociation.getGapSize(columnIndex);
            /* track min of gapSizes. to normalize gapSizes, we  later substracted min of gap size from gapSizes array */
            if (minGapSize > gapSizes[counter]) {
                minGapSize = gapSizes[counter];
            }
        }

        for (int j = 0; j < gapSizes.length; j++) {
            gapSizes[j] = gapSizes[j] - minGapSize;
        }
    }

    /**
     * when the given column index is out of array bounds, it returns -1 instead of throwing an exception
     */
    public int getGapSize(int columnIndex) {
        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            if (associatedColumnIndexes[i] == columnIndex) {
                return gapSizes[i];
            }
        }
        throw new RuntimeException("Given column index " + columnIndex + " does not have gap association, so gap size not found.");
    }

    /**
     * test if the given column index is associated with this GappedNumberAssocation
     */
    public boolean isAssociatedColumnIndex(int columnIndex) {
        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            if (associatedColumnIndexes[i] == columnIndex) {
                return true;
            }
        }
        return false;
    }


    public int[] getAssociatedColumnIndexes() {
        return associatedColumnIndexes;
    }

    public int[] getGapSizes() {
        return gapSizes;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        String ret = "[GappedNumAsso: id=" + id + ", ";
        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            ret = ret + "(col=" + associatedColumnIndexes[i] + " gap=" + gapSizes[i] + ")";
            if (i != associatedColumnIndexes.length - 1) {
                ret = ret + ", ";
            }
        }
        return ret + "]";
    }

    public boolean equals(Object obj) {
        if (! (obj instanceof GappedNumberAssociation)) {
            return false;
        }
        GappedNumberAssociation comparedAsso = (GappedNumberAssociation) obj;

        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            int colIdx = this.getAssociatedColumnIndexes() [i];

            if (! comparedAsso.isAssociatedColumnIndex(colIdx)) {
                return false;
            }

            if (this.gapSizes[i] != comparedAsso.getGapSize(colIdx)) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int result = 0;
        if (associatedColumnIndexes == null || gapSizes == null) {
            return 0;
        }

        for (int i = 0; i < associatedColumnIndexes.length; i++) {
            result = result * 9 + associatedColumnIndexes[i];
        }

        for (int i = 0; i < gapSizes.length; i++) {
            result = result * 9 + gapSizes[i];
        }


        return result;
    }
}
