package mit.cadlab.dome3.plugin.catalog.pcc;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 25.
 */
public class Rule {
    private Map columnToAssociationMap;
    private Set supportingRowIndexSet;

    /* in order to assign indentification no for each GappedNumberAssociation in a rule.
     * we need to count how many different gapped number associations have been added
     * the id number is used to express the association in a text format such as ${A+0}, ${A+1}, ${A+2} */
    private int gappedNumAssoCounter = 0;
    private int repeatedStrAssoCounter = 0;

    public Rule() {
        columnToAssociationMap = new TreeMap();
        supportingRowIndexSet = new TreeSet();
    }

    public Map getColumnToAssociationMap() {
        return columnToAssociationMap;
    }

    public Set getSupportingRowIndexSet() {
        return supportingRowIndexSet;
    }

    public void addSameStringAssociation(int columnIndex, String strValue) {
        columnToAssociationMap.put(new Integer(columnIndex), new SameStringAssociation(columnIndex, strValue));
    }

    public void addSameNumberAssociation(int columnIndex, Number numValue) {
        columnToAssociationMap.put(new Integer(columnIndex), new SameNumberAssociation(columnIndex, numValue));
    }

    public void addGappedNumberAssociation(Set gappedColumnIndexSet, CodeToken[] codeTokens) {
        GappedNumberAssociation gapAsso = new GappedNumberAssociation(gappedNumAssoCounter++, gappedColumnIndexSet, codeTokens);
        for (Iterator i = gappedColumnIndexSet.iterator(); i.hasNext(); ) {
            columnToAssociationMap.put(i.next(), gapAsso);
        }
    }

    /**
     * sometimes we need to generate gapped number association using a subset of a gapped number association (=srcAssociation)
     * @param gappedColumnIndexSet
     * @param srcAssociation
     */
    public void addGappedNumberAssociation(Set gappedColumnIndexSet, GappedNumberAssociation srcAssociation) {
        GappedNumberAssociation gapAsso = new GappedNumberAssociation(gappedNumAssoCounter++, gappedColumnIndexSet, srcAssociation);
        for (Iterator i = gappedColumnIndexSet.iterator(); i.hasNext(); ) {
            columnToAssociationMap.put(i.next(), gapAsso);
        }
    }

    public void addRepeatedStringAssociation(Set gappedColumnIndexSet) {
        RepeatedStringAssociation repAsso = new RepeatedStringAssociation(repeatedStrAssoCounter++, gappedColumnIndexSet);
        for (Iterator i = gappedColumnIndexSet.iterator(); i.hasNext();) {
            columnToAssociationMap.put(i.next(), repAsso);
        }
    }

    /** note. it returns null if the given columnIndex points a delim column. there is no TokenAssociation for delim columns. */
    public TokenAssociation getTokenAssociation(int columnIndex) {
        return getTokenAssociation(new Integer(columnIndex));
    }

    /** note. it returns null if the given columnIndex points a delim column. there is no TokenAssociation for delim columns. */
    public TokenAssociation getTokenAssociation(Integer columnIndex) {
        return (TokenAssociation) columnToAssociationMap.get(columnIndex);
    }

//    /**
//     * in order to assign indentification no for each GappedNumberAssociation in a rule.
//     * we need to count how many different gapped number associations have been added
//     * the id number is used to express the association in a text format such as ${A+0}, ${A+1}, ${A+2}
//     */
//    public int countGappedNumberAssociation() {
//        int counter = 0;
//        for (Iterator i = columnToAssociationMap.values().iterator(); i.hasNext(); ) {
//            if (i.next() instanceof GappedNumberAssociation) {
//                counter++;
//            }
//        }
//        return counter;
//    }
//
//    public int countRepeatedStringAssociation() {
//        int counter = 0;
//        for (Iterator i = columnToAssociationMap.values().iterator(); i.hasNext(); ) {
//            if (i.next() instanceof RepeatedStringAssociation) {
//                counter++;
//            }
//        }
//        return counter;
//    }

    public void addSupportingRowIndex(int rowIdx) {
        supportingRowIndexSet.add(new Integer(rowIdx));
    }

    protected void addSupportingRowIndex(Set rowIndexSet) {
        supportingRowIndexSet.addAll(rowIndexSet);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[Rule: support=" + supportingRowIndexSet + ", mapping=");
        for (Iterator i = columnToAssociationMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            sb.append("\r\n   column " + entry.getKey() + " -> " + entry.getValue());
        }
        sb.append("]");
        return sb.toString();
    }

    /** retrieve all gapped number association in this rule it is used in merging this rule with the other */
    protected Set getGappedNumberAssociations() {
        Set ret = new HashSet();
        for (Iterator i = columnToAssociationMap.values().iterator(); i.hasNext(); ) {
            TokenAssociation asso = (TokenAssociation) i.next();
            if (asso instanceof GappedNumberAssociation) {
                ret.add(asso);
            }
        }
        return ret;
    }

    /** retrieve all same number association in this rule it is used in merging this rule with the other */
    protected Set getSameNumberAssociations() {
        Set ret = new HashSet();
        for (Iterator i = columnToAssociationMap.values().iterator(); i.hasNext(); ) {
            TokenAssociation asso = (TokenAssociation) i.next();
            if (asso instanceof SameNumberAssociation) {
                ret.add(asso);
            }
        }
        return ret;
    }


    /** retrieve all repeated string association in this rule it is used in merging this rule with the other */
    protected Set getRepeatedStringAssociations() {
        Set ret = new HashSet();
        for (Iterator i = columnToAssociationMap.values().iterator(); i.hasNext(); ) {
            TokenAssociation asso = (TokenAssociation) i.next();
            if (asso instanceof RepeatedStringAssociation) {
                ret.add(asso);
            }
        }
        return ret;
    }

    /** retrieve all same string association in this rule it is used in merging this rule with the other */
    protected Set getSameStringAssociations() {
        Set ret = new HashSet();
        for (Iterator i = columnToAssociationMap.values().iterator(); i.hasNext(); ) {
            TokenAssociation asso = (TokenAssociation) i.next();
            if (asso instanceof SameStringAssociation) {
                ret.add(asso);
            }
        }
        return ret;
    }

    /**
     * return a column index set at which this rule has associations.
     * we are good to modify the Set object returned by this method.
     * note. it is because some column indexes can have a delimiter and do not have an association
     */
    public Set getAssociationColumnIndexes() {
        return new HashSet(columnToAssociationMap.keySet());
    }

    /**
     * return the number of associations in this rule
     * note. it is different from TokenMatrix.getColumnSize() because some column indexes can have a delimiter and do not have an association
     */
    public int getAssociationSize() {
        return columnToAssociationMap.keySet().size();
    }

    /**
     * tell if given rule object is the same as this rule object
     * by comparing all associations in them
     */
    public boolean equals(Object obj) {
        if (! (obj instanceof Rule)) {
            return false;
        }

        Rule compared = (Rule) obj;

        for (Iterator i = columnToAssociationMap.keySet().iterator(); i.hasNext(); ) {
            int columnIdx = ((Integer) i.next()).intValue();
            TokenAssociation comparedAsso = compared.getTokenAssociation(columnIdx);
            TokenAssociation thisAsso = this.getTokenAssociation(columnIdx);
            if (! thisAsso.equals(comparedAsso)) {
                return false;
            }
        }
        return true;
    }

}
