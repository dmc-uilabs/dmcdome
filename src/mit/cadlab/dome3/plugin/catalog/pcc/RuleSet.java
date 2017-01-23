package mit.cadlab.dome3.plugin.catalog.pcc;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 26.
 *
 * this class stores rules generalized from code base.
 * when we have a new rule generalized from a pair of source code fragments,
 * we can add it to this RuleSet by calling merge(newRule)
 * the method is named as 'merge' instead of 'add' because
 * a new Rule has a possibility of being merged into one of rules
 * in this rule set. when merging fails, the new rule is added to this set.
 *
 * note that supporting row indexes in a rule can be supporting other rules, too.
 * starting from this rule set, we would like to find the optimal rule set
 * where each row is used to support only one rule,
 * and rule count and unexplained row count are minimized.
 *
 * it is solved using a simple optimization technique
 * against permutation of applying rules.
 */
public class RuleSet {
    private List ruleList;

    public RuleSet() {
        ruleList = new ArrayList();
    }

    public void merge(Rule newRule) {
        if (ruleList.size() == 0) {
            ruleList.add(newRule);
        } else {
            boolean isMergedIntoExisting = false;
            for (int i = 0; i < ruleList.size(); i++) {
                Rule rule = (Rule) ruleList.get(i);
                Rule merged = mergeRulePair(rule, newRule);

                if (merged != null) {
                    ruleList.set(i, merged);
                    isMergedIntoExisting = true;
                    break;
                }
            }

            if (! isMergedIntoExisting) {
                ruleList.add(newRule);
            }
        }
    }

    /**
     * get ith rule in this rule list
     */
    public Rule getRule(int idx) {
        return (Rule) ruleList.get(idx);
    }

    public List getRules() {
        return ruleList;
    }

    public int size() {
        return ruleList.size();
    }

    public String toString() {
        return "[RuleSet: " + ruleList.toString() + "]";
    }

    /**
     * this method accept a Rule instance derived from the same TokenMatrix as this.
     * it returns if this Rule and compared Rule can be merged into one
     *
     * column idx    |      1      2      3       4       5       6       7       8
     * ------------------------------------------------------------------------------
     * row 1 value   |      1      1      3       B       B       1       2       0
     * row 2 value   |      3      3      5       B       B       1       4       2
     * row 3 value   |      3      3      5       E       E       1       7       5
     * ------------------------------------------------------------------------------
     * rule A        | GN'(1)  GN'(1)  GN'(3)  SS(B)  SS(B)    SN(1)  GN'(2)  GN'(0)
     * rule B        |  SN(3)   SN(3)   SN(5)    RS'    RS'    SN(1)  GN"(2)  GN"(0)
     * ------------------------------------------------------------------------------
     * merged rule   | GN'(0)  GN'(0)  GN'(2)    RS'    RS'    SN(1)  GN"(2)  GN"(0)
     *
     * by considering the fact that our association could have been over-generalized and thus
     * needs to be relaxed appropriately, we merge two rule into one.
     * there are 3 types of relaxation:
     *  1) SN -> GN: col 1, 2, 3
     *  2) SS -> RS: col 4, 5
     *  3) some GN's that belonged to one group forms a separate group: col 7, 8
     *
     * if we find no relaxed association that satisfies both rules, null is returned.
     * it means these two rules are not compatible
     *
     * this operation does not modify associations in this object
     *
     */
    protected static Rule mergeRulePair(Rule rule1, Rule rule2) {
        Rule ret = new Rule();

        /* if rule1 is already supported by all rows in rule2, we know rule1 will be the relaxation result without computation. the opposite case with rule1 and rule2 reversed is also true. */
        if (rule1.getSupportingRowIndexSet().containsAll(rule2.getSupportingRowIndexSet())) {
            return rule1;
        } else if (rule2.getSupportingRowIndexSet().containsAll(rule1.getSupportingRowIndexSet())) {
            return rule2;
        }

        /* if this is completely equal to compared, just add supporting rows to rule1 and return it */
        if (rule1.equals(rule2)) {
            ret = rule1;
            ret.addSupportingRowIndex(rule2.getSupportingRowIndexSet());
            return rule1;
        }

        Set mergedColumnIndexes = new HashSet();

        Set gapAsso1Set = rule1.getGappedNumberAssociations();
        Set gapAsso2Set = rule2.getGappedNumberAssociations();

        /**************************************************************/
        /* <STEP 1-A> merging for gapped number association in rule 1 */
        /**************************************************************/

        for (Iterator i = gapAsso1Set.iterator(); i.hasNext(); ) {

            GappedNumberAssociation gapAsso1 = (GappedNumberAssociation) i.next();

            if (gapAsso2Set.contains(gapAsso1)) {
                /* if there exists the same gapped number association in rule2, copy the association to the same column indexes of the returned rule object */
                Set gapAsso1IdxSet = new HashSet();
                for (int j = 0; j < gapAsso1.getAssociatedColumnIndexes().length; j++) {
                    gapAsso1IdxSet.add(new Integer(gapAsso1.getAssociatedColumnIndexes() [j]));
                }
                ret.addGappedNumberAssociation(gapAsso1IdxSet, gapAsso1);
                mergedColumnIndexes.addAll(gapAsso1IdxSet);
                /* now that rule1 and rule2 have the same gapped number association, \
                 * we are good to move on to the next gapped number association in rule 1 */
                continue;
            }

            /* this point is reached when gapAsso2Set does not contain gapAsso1 */
            int[] columnIndexes = gapAsso1.getAssociatedColumnIndexes();

            /* column indexes where the compared rule has same number association is collected here */
            Map sameNumberIdxMap = new HashMap();

            /* column indexes where the compared rule has gapped number association is collected here. its key is diff, and its value is idx integer set */
            Map gappedNumberIdxMap = new HashMap();

            for (int j = 0; j < columnIndexes.length; j++) {
                int colIdx = columnIndexes[j];
                TokenAssociation asso2 = rule2.getTokenAssociation(colIdx);
                if (asso2 instanceof SameNumberAssociation) {
                    SameNumberAssociation sameAsso2 = (SameNumberAssociation) asso2;

                    /* if the data type of the same number value is double, this column cannot be relaxed into gapped number association,
                     * and therefore we stop merging (=return null); */
                    if (sameAsso2.getValue() instanceof Double) {
                        return null;
                    }

                    /* if the data type is integer and we group the column indexes by
                     * the difference between the value and gap size into sameNumberIdxMap */
                    int curDiff = sameAsso2.getValue().intValue() - gapAsso1.getGapSize(colIdx);
                    Set sameNumberIdxSet = (Set) sameNumberIdxMap.get(new Integer(curDiff));
                    if (sameNumberIdxSet == null) {
                        sameNumberIdxSet = new HashSet();
                        sameNumberIdxMap.put(new Integer(curDiff), sameNumberIdxSet);
                    }
                    sameNumberIdxSet.add(new Integer(colIdx));
                }

                /* when compared association is gapped number association, it is the case of 1,2,3 vs 7,8
                 * the difference between gap size is used to separate associations into an appropriate group */
                if (asso2 instanceof GappedNumberAssociation) {
                    GappedNumberAssociation gapNumAsso = (GappedNumberAssociation) asso2;
                    int curDiff = gapNumAsso.getGapSize(colIdx) - gapAsso1.getGapSize(colIdx);
                    Set gappedNumberIdxSet = (Set) gappedNumberIdxMap.get(new Integer(curDiff));
                    if (gappedNumberIdxSet == null) {
                        gappedNumberIdxSet = new HashSet();
                        gappedNumberIdxMap.put(new Integer(curDiff), gappedNumberIdxSet);
                    }
                    gappedNumberIdxSet.add(new Integer(colIdx));
                }
            }

            /* iterate through values of same number idx map (we are not interested in the diff of gap size. they are useful just for grouping)
            /* note. if sameNumberIdxSet.size() is one, we have a column that cannot be relaxed. */
            for (Iterator j = sameNumberIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addGappedNumberAssociation(columnIdxSet, gapAsso1);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }

            /* iterate through values of gapped number idx map (we are not interested in the diff of gap size. they are useful just for grouping)
             * if its size is bigger than two impose gapped number association. if any set is a size of one, no relaxation is possible (=return null)*/
            for (Iterator j = gappedNumberIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addGappedNumberAssociation(columnIdxSet, gapAsso1);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }
        }

        /**************************************************************/
        /* <STEP 1-B> merging for gapped number association in rule 2 */
        /**************************************************************/

        for (Iterator i = gapAsso2Set.iterator(); i.hasNext(); ) {
            GappedNumberAssociation gapAsso2 = (GappedNumberAssociation) i.next();

            /* out of gapAsso2.getAssociatedColumnIndexes(), remove column indexes that are already merged, we don't want to repeat computation that leads to the same result. */
            Set columnIndexesSet = convertIntArrayToSet(gapAsso2.getAssociatedColumnIndexes());
            columnIndexesSet.removeAll(mergedColumnIndexes);
            int[] columnIndexes = convertSetToIntArray(columnIndexesSet);

            /* column indexes where the compared rule has same number association is collected here */
            Map sameNumberIdxMap = new HashMap();

            /* column indexes where the compared rule has gapped number association is collected here. its key is diff, and its value is idx integer set */
            Map gappedNumberIdxMap = new HashMap();

            for (int j = 0; j < columnIndexes.length; j++) {
                int colIdx = columnIndexes[j];
                TokenAssociation asso1 = rule1.getTokenAssociation(colIdx);
                if (asso1 instanceof SameNumberAssociation) {
                    SameNumberAssociation sameAsso1 = (SameNumberAssociation) asso1;

                    /* as explained above, sameNumberIdxMap is created */
                    if (sameAsso1.getValue() instanceof Double) {
                        return null;
                    }

                    int curDiff = sameAsso1.getValue().intValue() - gapAsso2.getGapSize(colIdx);
                    Set sameNumberIdxSet = (Set) sameNumberIdxMap.get(new Integer(curDiff));
                    if (sameNumberIdxSet == null) {
                        sameNumberIdxSet = new HashSet();
                        sameNumberIdxMap.put(new Integer(curDiff), sameNumberIdxSet);
                    }
                    sameNumberIdxSet.add(new Integer(colIdx));
                }

                /* as explained above, gappedNumberIdxSet is created */
                if (asso1 instanceof GappedNumberAssociation) {
                    GappedNumberAssociation gapNumAsso = (GappedNumberAssociation) asso1;
                    int curDiff = gapNumAsso.getGapSize(colIdx) - gapAsso2.getGapSize(colIdx);
                    Set gappedNumberIdxSet = (Set) gappedNumberIdxMap.get(new Integer(curDiff));
                    if (gappedNumberIdxSet == null) {
                        gappedNumberIdxSet = new HashSet();
                        gappedNumberIdxMap.put(new Integer(curDiff), gappedNumberIdxSet);
                    }
                    gappedNumberIdxSet.add(new Integer(colIdx));
                }
            }

            /* sameNumberIdxMap is used to add association to the return object */
            for (Iterator j = sameNumberIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addGappedNumberAssociation(columnIdxSet, gapAsso2);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }

            /* gappedNumberIdxSet is used to add association to the return object */
            for (Iterator j = gappedNumberIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addGappedNumberAssociation(columnIdxSet, gapAsso2);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }
        }

        /*******************************************************************/
        /* <STEP 1-C> merge same number association existing in both rules */
        /*******************************************************************/

        /* find columns with the equivalent same number assocication, add the same number association to the return object */
        Set sameNumberAsso1Set = rule1.getSameNumberAssociations();
        for (Iterator i = sameNumberAsso1Set.iterator(); i.hasNext(); ) {
            SameNumberAssociation sameAsso1 = (SameNumberAssociation) i.next();
            if (mergedColumnIndexes.contains(new Integer(sameAsso1.getAssociatedColumnIndex()))) {
                /* skip already merged columns */
                continue;
            }

            /* if rule1 and rule2 have the same number association for a column index, same number association is added to the return object  */
            TokenAssociation asso2 = rule2.getTokenAssociation(sameAsso1.getAssociatedColumnIndex());
            if (! (asso2 instanceof SameNumberAssociation)) {
                return null; // this is a column that cannot be explained using a single association
            }
            if (sameAsso1.equals(asso2)) {
                ret.addSameNumberAssociation(sameAsso1.getAssociatedColumnIndex(), sameAsso1.getValue());
                mergedColumnIndexes.add(new Integer(sameAsso1.getAssociatedColumnIndex()));
            }
        }

        Set rptAsso1Set = rule1.getRepeatedStringAssociations();
        Set rptAsso2Set = rule2.getRepeatedStringAssociations();

        /****************************************************************/
        /* <STEP 2-A> merging for repeated string association in rule 1 */
        /****************************************************************/

        for (Iterator i = rptAsso1Set.iterator(); i.hasNext(); ) {

            RepeatedStringAssociation rptAsso1 = (RepeatedStringAssociation) i.next();

            if (rptAsso2Set.contains(rptAsso1)) {
                /* if there exists the same repeated string association in rule2, copy the association to the same column indexes of the returned rule object */
                Set gapAsso1IdxSet = new HashSet();
                for (int j = 0; j < rptAsso1.getAssociatedColumnIndexes().length; j++) {
                    gapAsso1IdxSet.add(new Integer(rptAsso1.getAssociatedColumnIndexes() [j]));
                }
                ret.addRepeatedStringAssociation(gapAsso1IdxSet);
                mergedColumnIndexes.addAll(gapAsso1IdxSet);
                /* now that rule1 and rule2 have the same repeated string association, \
                 * we are good to move on to the next repeated string association in rule 1 */
                continue;
            }

            /* this point is reached when rptAsso2Set does not contain rptAsso1 */
            int[] columnIndexes = rptAsso1.getAssociatedColumnIndexes();
            /* column indexes where the compared rule has same string association is collected here */
            Map sameStringIdxMap = new HashMap();
            /* column indexes where the compared rule has repeated string association is collected here. */
            Map rptStringIdxMap = new HashMap();

            for (int j = 0; j < columnIndexes.length; j++) {
                int colIdx = columnIndexes[j];
                TokenAssociation asso2 = rule2.getTokenAssociation(colIdx);
                if (asso2 instanceof SameStringAssociation) {
                    SameStringAssociation sameAsso2 = (SameStringAssociation) asso2;

                    /* we group the column indexes by the value and store the result into sameNumberIdxMap */
                    String groupKey = sameAsso2.getValue();
                    Set sameStringIdxSet = (Set) sameStringIdxMap.get(groupKey);
                    if (sameStringIdxSet == null) {
                        sameStringIdxSet = new HashSet();
                        sameStringIdxMap.put(groupKey, sameStringIdxSet);
                    }
                    sameStringIdxSet.add(new Integer(colIdx));
                }

                /* when compared association is repeated string association, it is the string-version case of 1,2,3 vs 7,8
                 * if some columns belong to one repeated association and others belong to another repeated association, they are grouped into separate groups. */
                if (asso2 instanceof RepeatedStringAssociation) {
                    RepeatedStringAssociation groupKey = (RepeatedStringAssociation) asso2;
                    Set rptStringIdxSet = (Set) rptStringIdxMap.get(groupKey);
                    if (rptStringIdxSet == null) {
                        rptStringIdxSet = new HashSet();
                        rptStringIdxMap.put(groupKey, rptStringIdxSet);
                    }
                    rptStringIdxSet.add(new Integer(colIdx));
                }
            }

            /* sameStringIdxMap is used to add association to the return object */
            for (Iterator j = sameStringIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addRepeatedStringAssociation(columnIdxSet);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }

            /* rptStringIdxMap is used to add association to the return object */
            for (Iterator j = rptStringIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addRepeatedStringAssociation(columnIdxSet);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }
        }

        /****************************************************************/
        /* <STEP 2-B> merging for repeated string association in rule 1 */
        /****************************************************************/

        for (Iterator i = rptAsso2Set.iterator(); i.hasNext(); ) {

            RepeatedStringAssociation rptAsso2 = (RepeatedStringAssociation) i.next();

            /* out of rptAsso2.getAssociatedColumnIndexes(), remove column indexes that are already merged, we don't want to repeat computation that leads to the same result. */
            Set columnIndexesSet = convertIntArrayToSet(rptAsso2.getAssociatedColumnIndexes());
            columnIndexesSet.removeAll(mergedColumnIndexes);
            int[] columnIndexes = convertSetToIntArray(columnIndexesSet);

            /* column indexes where the compared rule has same string association is collected here */
            Map sameStringIdxMap = new HashMap();
            /* column indexes where the compared rule has repeated string association is collected here. */
            Map rptStringIdxMap = new HashMap();

            for (int j = 0; j < columnIndexes.length; j++) {
                int colIdx = columnIndexes[j];

                TokenAssociation asso1 = rule1.getTokenAssociation(colIdx);
                if (asso1 instanceof SameStringAssociation) {
                    SameStringAssociation sameAsso1 = (SameStringAssociation) asso1;

                    /* we group the column indexes by the value and store the result into sameNumberIdxMap */
                    String groupKey = sameAsso1.getValue();
                    Set sameStringIdxSet = (Set) sameStringIdxMap.get(groupKey);
                    if (sameStringIdxSet == null) {
                        sameStringIdxSet = new HashSet();
                        sameStringIdxMap.put(groupKey, sameStringIdxSet);
                    }
                    sameStringIdxSet.add(new Integer(colIdx));
                }

                /* when compared association is repeated string association, it is the string-version case of 1,2,3 vs 7,8
                 * if some columns belong to one repeated association and others belong to another repeated association, they are grouped into separate groups. */
                if (asso1 instanceof RepeatedStringAssociation) {
                    RepeatedStringAssociation groupKey = (RepeatedStringAssociation) asso1;
                    Set rptStringIdxSet = (Set) rptStringIdxMap.get(groupKey);
                    if (rptStringIdxSet == null) {
                        rptStringIdxSet = new HashSet();
                        rptStringIdxMap.put(groupKey, rptStringIdxSet);
                    }
                    rptStringIdxSet.add(new Integer(colIdx));
                }
            }

            /* sameStringIdxMap is used to add association to the return object */
            for (Iterator j = sameStringIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addRepeatedStringAssociation(columnIdxSet);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }

            /* rptStringIdxMap is used to add association to the return object */
            for (Iterator j = rptStringIdxMap.values().iterator(); j.hasNext(); ) {
                Set columnIdxSet = (Set) j.next();
                if (columnIdxSet.size() == 1) {
                    return null;
                } else {
                    ret.addRepeatedStringAssociation(columnIdxSet);
                    mergedColumnIndexes.addAll(columnIdxSet);
                }
            }
        }

        /*******************************************************************/
        /* <STEP 2-C> merge same string association existing in both rules */
        /*******************************************************************/

        /* find columns with the equivalent same string assocication, add the same string association to the return object */
        Set sameStringAsso1Set = rule1.getSameStringAssociations();
        for (Iterator i = sameStringAsso1Set.iterator(); i.hasNext(); ) {
            SameStringAssociation sameAsso1 = (SameStringAssociation) i.next();
            if (mergedColumnIndexes.contains(new Integer(sameAsso1.getAssociatedColumnIndex()))) {
                /* skip already merged columns */
                continue;
            }

            /* if rule1 and rule2 have the same string association for a column index, same string association is added to the return object  */
            TokenAssociation asso2 = rule2.getTokenAssociation(sameAsso1.getAssociatedColumnIndex());
            if (! (asso2 instanceof SameStringAssociation)) {
                return null; // this is a column that cannot be explained using a single association
            }
            if (sameAsso1.equals(asso2)) {
                ret.addSameStringAssociation(sameAsso1.getAssociatedColumnIndex(), sameAsso1.getValue());
                mergedColumnIndexes.add(new Integer(sameAsso1.getAssociatedColumnIndex()));
            }
        }

        /**************************************************************************/
        /* <STEP 3> we check if we have assigned associations to all columns.
         * if not, no relaxation was possible for these two rules. it should return null.
         * if so, add supporting row indexes to return object and finally return it */
        /**************************************************************************/
        if (! mergedColumnIndexes.equals(rule1.getAssociationColumnIndexes())) {
            return null;
        }

        ret.addSupportingRowIndex(rule1.getSupportingRowIndexSet());
        ret.addSupportingRowIndex(rule2.getSupportingRowIndexSet());
        return ret;
    }

    private static int[] convertSetToIntArray(Set intSet) {
        int[] ret = new int[intSet.size()];
        int counter = 0;
        for (Iterator i = intSet.iterator(); i.hasNext(); counter++) {
            ret[counter] = ((Integer) i.next()).intValue();
        }
        return ret;
    }

    private static Set convertIntArrayToSet(int[] intArray) {
        Set ret = new HashSet(intArray.length);
        for (int i = 0; i < intArray.length; i++) {
            ret.add(new Integer(intArray[i]));
        }
        return ret;
    }
}
