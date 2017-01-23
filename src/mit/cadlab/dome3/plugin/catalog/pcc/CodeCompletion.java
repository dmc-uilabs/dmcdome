package mit.cadlab.dome3.plugin.catalog.pcc;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 30.
 */
public class CodeCompletion {

    private CodeToken[] cueTokens;
    private Rule activeRule;
    private TokenMatrix activeMatrix;

    private List candidateRows;
    private int selectedRowIndex;

    /**
     * we have cue tokens and codebase prepared to create code completion.
     * in constructor, we query code base with cue signature to find
     * a set of code rows having the same start signature. index is organized
     * such that once we pick a code token signature, we have code rows sharing
     * the code token signature. therefore, if there were multiple code token signature having
     * the same beginning signature as the cue signature, multiple token matrixes
     * -- which is just another data representation of a set of code rows --
     * corresponding to each of matched signatures could be generated.
     */
    public CodeCompletion(String cueStr) {
        candidateRows = new ArrayList();
        this.cueTokens = TokenMatrix.createCodeTokenArray(cueStr);
        CodeBase codeBase = CodeBase.getInstance();
        Set signatureSet = codeBase.findSignatures(cueStr);
        for (Iterator i = signatureSet.iterator(); i.hasNext(); ) {
            String signature = (String) i.next();
            TokenMatrix matrix = codeBase.getTokenMatrix(signature);
            RuleFinder ruleFinder = new RuleFinder(matrix);

            for (int j = 0; j < ruleFinder.getRuleCount(); j++) {
                Rule rule = ruleFinder.getRule(j);
                candidateRows.add(new CandidateRow(cueTokens, rule, matrix));
            }
        }
        Collections.sort(candidateRows);
        this.selectedRowIndex = -1;
    }

    public boolean isActivated() {
        return (selectedRowIndex != -1);
    }

    public void setActiveRow(int candidateIndex) {
        CandidateRow cRow = getCandidateRow(candidateIndex);
        activeRule = cRow.getRule();
        activeMatrix = cRow.getMatrix();
    }

    public CandidateRow getCandidateRow(int candidateIndex) {
        return (CandidateRow) candidateRows.get(candidateIndex);
    }

    public int getCandidateCount() {
        return candidateRows.size();
    }

    public Rule getActiveRule() {
        return activeRule;
    }

    public TokenMatrix getActiveMatrix() {
        return activeMatrix;
    }

    public static InconsistancyList checkConsistancy(CodeToken[] cueTokens, Rule rule) {
        InconsistancyList ret = new InconsistancyList();
        /* iterate cueTokens as we check if each token is consistant
           with rules associated with the tokens */
        for (int i = 0; i < cueTokens.length; i++) {

            if (! ret.isConsistant(i)) {
                /* for repeated or gapped association, inconsistancy is discovered
                 * when the first token of an association is tested,
                 * and all related error column indexes are added to InconsistancyList
                 * at this point. therefore we can skip below testing for those pre-tested column indexes. */
                continue;
            }

            Object tokenObj = cueTokens [i].getTokenObject();
            TokenAssociation asso = rule.getTokenAssociation(i);
            if (asso == null) {
                /* delim token */
                continue;
            }

            if (asso instanceof SameStringAssociation) {
                SameStringAssociation sameStrAsso = (SameStringAssociation) asso;
                if (! tokenObj.equals(sameStrAsso.getValue())) {
                    ret.add(i, Inconsistancy.INCONSISTANTLY_DIFFERENT_STRING, sameStrAsso.getValue());
                }
            }

            if (asso instanceof SameNumberAssociation) {
                SameNumberAssociation sameNumAsso = (SameNumberAssociation) asso;
                if (! tokenObj.equals(sameNumAsso.getValue())) {
                    ret.add(i, Inconsistancy.INCONSISTANTLY_DIFFERENT_NUMBER, sameNumAsso.getValue());
                }
            }

            if (asso instanceof RepeatedStringAssociation) {
                RepeatedStringAssociation rptStrAsso = (RepeatedStringAssociation) asso;
                int[] rptColIdx = rptStrAsso.getAssociatedColumnIndexes();
                String valueToBeRepeated = (String) tokenObj;
                for (int j = 0; j < rptColIdx.length; j++) {
                    if (rptColIdx[j] < cueTokens.length) {
                        /* if cueTokens has some value at column index of rptColIdx[j], we need to check if it is the same as 'valueToBeRepeated' */
                        if (! valueToBeRepeated.equals(cueTokens[rptColIdx[j]].getTokenObject())) {
                            ret.add(i, Inconsistancy.INCONSISTANTLY_REPEATED_STRING, valueToBeRepeated);
                        }
                    }
                }
            }

            if (asso instanceof GappedNumberAssociation) {
                GappedNumberAssociation gappedNumAsso = (GappedNumberAssociation) asso;
                int[] gappedColIdx = gappedNumAsso.getAssociatedColumnIndexes();
                int valueToBeGappedFrom = ((Integer) tokenObj).intValue();
                for (int j = 0; j < gappedColIdx.length; j++) {
                    if (gappedColIdx[j] < cueTokens.length) {
                        /* if cueTokens has some value at column index of gappedColIdx[j], we need to check if it is the expected gap with 'valueToBeGappedFrom' */
                        /* expected gap is calculated from gappedNumAsso.getGapSize(i) - gappedNumAsso.getGapSize(gappedColIdx[j]) */
                        int expectedGap = gappedNumAsso.getGapSize(gappedColIdx[j]) - gappedNumAsso.getGapSize(i);
                        int actualGap = cueTokens[gappedColIdx[j]].getInt() - valueToBeGappedFrom;
                        if (actualGap != expectedGap) {
                            ret.add(i, Inconsistancy.INCONSISTANTLY_GAPPED_NUMBER, new Integer(valueToBeGappedFrom + expectedGap));
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * completed code string
     */
    public static String getCodeString(CodeToken[] cueTokens, Rule rule, TokenMatrix matrix) {
        StringBuffer sb = new StringBuffer();

        int tokenSize = matrix.getColumnSize();
        for (int i = 0; i < tokenSize; i++) {
            Object tokenObj = getTokenObject(i, cueTokens, rule, matrix);
            if (tokenObj != null) {
                /* insert space based on the original tokens' spacing */
                if (i > 0) {
                    CodeToken[] originTokens = matrix.getTokenRow(0).getTokenArray();
                    int spaceCount = originTokens[i].getStartIdx() - originTokens[i - 1].getEndIdx();
                    for (int j =0; j < spaceCount; j++) {
                        sb.append(' ');
                    }
                }
                sb.append(tokenObj.toString());
            } else {
                /* insert space based on the original tokens' spacing */
                if (i > 0) {
                    CodeToken[] originTokens = matrix.getTokenRow(0).getTokenArray();
                    int spaceCount = originTokens[i].getStartIdx() - originTokens[i - 1].getEndIdx();
                    for (int j =0; j < spaceCount; j++) {
                        sb.append(' ');
                    }
                }
                sb.append(getUndeterminedTokenExpression(rule, i));
            }
        }
        return sb.toString();
    }

    /**
     * column index is between 0 and N - 1(=getTokenSize() - 1)
     * we can construct code completed string by iterating those column indexes and merging getTokenObject()
     * for columnIndexes where cues are already exist, values of the cues are returned. they may not be consistant with rules.
     * inconsistancy information could be retrieved using getInconsistancyList()
     * this method can be called against any column type.
     *  - when it is called against DELIM_TYPE, delim String returned
     *  - when it is called against INTEGER_TYPE, Integer returned
     *  - when it is called against STRING_TYPE, String returned
     *  - when it is called against DOUBLE_TYPE, Double returned
     * note. null is returned when the cue does not determine the value
     * @param columnIdx
     * @return
     */
    public static Object getTokenObject(int columnIdx, CodeToken[] cueTokens, Rule rule, TokenMatrix matrix) {
        /* if cueTokens specifies what is token object for a column index, we just take it as it is. */
        if (columnIdx < cueTokens.length) {
            return cueTokens[columnIdx].getTokenObject();
        }

        TokenAssociation asso = rule.getTokenAssociation(columnIdx);

        if (asso == null) {
            return matrix.getDelim(columnIdx);
        }

        if (asso instanceof SameStringAssociation) {
            return ((SameStringAssociation) asso).getValue();
        }

        if (asso instanceof SameNumberAssociation) {
            return ((SameNumberAssociation) asso).getValue();
        }

        if (asso instanceof RepeatedStringAssociation) {
            RepeatedStringAssociation rptStrAsso = (RepeatedStringAssociation) asso;
            int[] rptColIdx = rptStrAsso.getAssociatedColumnIndexes();
            String valueToBeRepeated = null;
            if (rptColIdx [0] < cueTokens.length) {
                /* size of rptColIdx is always larger than one */
                /* if cueTokens specifies the repeated value set it to valueToBeRepeated. sometimes it may not be specified, and valueToBeRepeated left as null*/
                valueToBeRepeated = cueTokens[rptColIdx [0]].getString();
            }
            return valueToBeRepeated;
        }

        if (asso instanceof GappedNumberAssociation) {
            GappedNumberAssociation gappedNumAsso = (GappedNumberAssociation) asso;
            int[] gappedColIdx = gappedNumAsso.getAssociatedColumnIndexes();
                if (gappedColIdx[0] < cueTokens.length) {
                    /* size of rptColIdx is always larger than one */
                    int valueToBeGappedFrom = cueTokens[gappedColIdx[0]].getInt();

                    /* if cueTokens has some value at column index of gappedColIdx[j], we need to check if it is the expected gap with 'valueToBeGappedFrom' */
                    /* expected gap is calculated from gappedNumAsso.getGapSize(i) - gappedNumAsso.getGapSize(gappedColIdx[j]) */
                    int expectedValue = valueToBeGappedFrom - gappedNumAsso.getGapSize(gappedColIdx[0]) + gappedNumAsso.getGapSize(columnIdx);
                    return new Integer(expectedValue);
                }
        }

        return null;
    }

    /**
     * this method applies to columnIndex with RepeatedStringAssociation or GappedNumberAssociation
     */
    public static String getUndeterminedTokenExpression(Rule rule, int columnIdx) {

        TokenAssociation asso = rule.getTokenAssociation(columnIdx);
        if (asso instanceof GappedNumberAssociation) {
            GappedNumberAssociation gappedNumAsso = (GappedNumberAssociation) asso;
            char idChar = (char) (gappedNumAsso.getId() + 'a');
            return "${" + Character.toString(idChar) + "+" + gappedNumAsso.getGapSize(columnIdx) + "}" ;
        }

        if (asso instanceof RepeatedStringAssociation) {
            RepeatedStringAssociation repeatedStrAsso = (RepeatedStringAssociation) asso;
            char idChar = (char) (repeatedStrAsso.getId() + 'A');
            return "${" + Character.toString(idChar) + "}" ;
        }

        throw new RuntimeException("given column index " + columnIdx + " should have either RepeatedStringAssociation or GappedNumberAssociation, but current association is " + asso);
    }

    static class InconsistancyList {

        /* Integer -> Object[2]<Integer, Object>
         */
        Map colIdxToError;

        InconsistancyList() {
            this.colIdxToError = new TreeMap();
        }

        void add(int columnIdx, int errorCode, Object correctValue) {
            colIdxToError.put(new Integer(columnIdx), new Inconsistancy(errorCode, correctValue));
        }

        boolean isConsistant(int columnIdx) {
            return ! colIdxToError.containsKey(new Integer(columnIdx));
        }

        public int size() {
            return colIdxToError.size();
        }

        public Inconsistancy get(int i) {
            return (Inconsistancy) colIdxToError.get(new Integer(i));
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[ErrorList: ");
            for (Iterator i = colIdxToError.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry entry = (Map.Entry) i.next();
                sb.append("\r\n   column " + entry.getKey() + " -> " + entry.getValue());
                if (! i.hasNext()) {
                    sb.append("\r\n");
                }
            }
            if (colIdxToError.size() == 0) {
                sb.append("no error");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    static class Inconsistancy {
        public static final int INCONSISTANTLY_DIFFERENT_STRING = 1;
        public static final int INCONSISTANTLY_DIFFERENT_NUMBER = 2;
        public static final int INCONSISTANTLY_REPEATED_STRING = 3;
        public static final int INCONSISTANTLY_GAPPED_NUMBER = 4;

        private int errorCode;
        private Object correctValue;

        /*
         * possible combinations of error code and correct value object are as follows:
         * INCONSISTANTLY_DIFFERENT_STRING - String
         * INCONSISTANTLY_DIFFERENT_NUMBER - Real or Integer
         * INCONSISTANTLY_REPEATED_STRING - String
         * INCONSISTANTLY_GAPPED_NUMBER - Integer
         */
        public Inconsistancy(int errorCode, Object correctValue) {
            this.errorCode = errorCode;
            this.correctValue = correctValue;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public Object getCorrectValue() {
            return correctValue;
        }

        String convertErrorCodeToString(int errorCode) {
            if (Inconsistancy.INCONSISTANTLY_DIFFERENT_STRING == errorCode) return "same string with other rows expected";
            if (Inconsistancy.INCONSISTANTLY_DIFFERENT_NUMBER == errorCode) return "same number with other rows expected";
            if (Inconsistancy.INCONSISTANTLY_REPEATED_STRING == errorCode) return "repeated string in other columns expected";
            if (Inconsistancy.INCONSISTANTLY_GAPPED_NUMBER == errorCode) return "consistantly gapped number with other columns expected";
            return "INVALID_ERROR_CODE";
        }

        public String toString() {
            return "[Error: " + convertErrorCodeToString(errorCode) + ", expected: " + correctValue + "]";
        }
    }

    public class CandidateRow implements Comparable {
        public static final int SUPPORTING_COUNT_WEIGHT = 1;
        public static final int INCONSISTANCY_COUNT_WEIGHT = 2;

        private CodeToken[] cueTokens;
        private Rule rule;
        private TokenMatrix matrix;
        private CodeCompletion.InconsistancyList inconsistList;

        CandidateRow(CodeToken[] cueTokens, Rule rule, TokenMatrix matrix) {
            this.cueTokens = cueTokens;
            this.rule = rule;
            this.matrix = matrix;
            this.inconsistList = checkConsistancy(cueTokens, rule);
        }

        public Rule getRule() {
            return rule;
        }

        public TokenMatrix getMatrix() {
            return matrix;
        }

        public int getInconsistancyCount() {
            return inconsistList.size();
        }

        public int getScore() {
            return rule.getSupportingRowIndexSet().size() * SUPPORTING_COUNT_WEIGHT - getInconsistancyCount() * INCONSISTANCY_COUNT_WEIGHT ;
        }

        public String getScoreExplanation() {
            String ret = "support count: " + rule.getSupportingRowIndexSet().size()+ ", inconsist count: " + getInconsistancyCount();
            if (getInconsistancyCount() > 0) {
                ret = ret + " ~ " + inconsistList;
            }
            return ret;
        }

        public int compareTo(Object obj) {
            if (! (obj instanceof CodeCompletion.CandidateRow)) {
                throw new RuntimeException("CandidateRow instance is only comparable with CandidateRow instances. It has been compared with " + obj);
            }
            return this.getScore() - ((CodeCompletion.CandidateRow) obj).getScore();
        }

        public String getCodeString() {
            return CodeCompletion.getCodeString(cueTokens, rule, matrix);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[Candidate: score=" + getScore() + ", detail=" + getScoreExplanation() + ", code=").append(this.getCodeString()).append("]");
            return sb.toString();
        }

//        public String getDetailString() {
//            StringBuffer sb = new StringBuffer();
//            sb.append("[Candidate: score=" + getScore() + ", \r\n  rule=");
//            sb.append(rule).append(", \r\n  matrix=").append(matrix).append("]");
//            return sb.toString();
//        }
    }
}
