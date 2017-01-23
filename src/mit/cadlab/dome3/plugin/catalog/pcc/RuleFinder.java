package mit.cadlab.dome3.plugin.catalog.pcc;

import java.util.*;

/**
 * Facade class to perform code pattern generalization tasks.
 * To find rules generalized from code fragments, use this class as follows:
 *
 * (1) create instance of RuleFinder by TokenMatrix-argument constructor.
 *      - String[] can be easily converted into TokenMatrix by contructing TokenMatrix() and then calling addCode(String) as many as needed
 * (2) retrieve the rule generalization result:
 *      - getRuleCount():int
 *      - getRule(int idx):Rule
 *      - getRuleList():List
 *      - getUnexplainedCount():int
 *      - getUnexplainedRowIndexes():Set
 *      - getTokenMatrix():TokenMatrix
 *
 * User: Sangmok Han
 * Date: 2005. 12. 28.
 */
public class RuleFinder {
    private TokenMatrix matrix;
    private RuleSet ruleSet;
    private List evalList;
    private RuleSetEvaluation bestEval;

    /**
     * create a RuleFinder instance from TokenMatrix
     */
    public RuleFinder(TokenMatrix matrix) {
        evalList = new ArrayList();
        this.matrix = matrix;
        this.mineCode();
    }

    /**
     * check if a rule is generalized from codes
     */
    public boolean isRuleFound() {
        return (bestEval != null);
    }

    /**
     * the number of rules generalized from code fragments
     * returns 0 if no rule is generalized
     */
    public int getRuleCount() {
        if (! isRuleFound()) {
            return 0;
        }
        return bestEval.getRuleCount();
    }

    /**
     * return a generalized Rule at the given index
     */
    public Rule getRule(int idx) {
        if (! isRuleFound()) {
            throw new RuntimeException("No rule could be generalized from the codes, so you cannot invoke getRule(int). You can call isRuleFound() to check if any rule is found.");
        }
        List ruleList = bestEval.getActiveRuleList();
        if (ruleList.size() > idx) {
            return (Rule) ruleList.get(idx);
        } else {
            throw new RuntimeException("Given rule index " + idx + " is out of bounds. It should be between [0 .. " + (getRuleCount() - 1) + "]");
        }
    }

    /**
     * returns a List of Rule generalized from codes
     */
    public List getRuleList() {
        if (! isRuleFound()) {
            return new ArrayList();
        }
        return bestEval.getActiveRuleList();
    }

    /**
     * returns -1 if no rule could be found from code fragments
     */
    public int getUnexplainedCount() {
        if (! isRuleFound()) {
            return -1;
        }
        return bestEval.getUnexplainedCount();
    }

    /**
     * returns a Set of Integer representing the row indexes that could not be explained the rules generalized
     * returns all rows if no rule could be found from code fragments
     */
    public Set getUnexplainedRowIndexes() {
        if (! isRuleFound()) {
            createIntegerSet(matrix.getRowSize());
        }
        return bestEval.getUnexplainedRowIndexes();
    }

    /**
     * can be invoked after setting codes
     * @return
     */
    public TokenMatrix getTokenMatrix() {
        return matrix;
    }

    /**
     * to see the details of how permutations are evaluated
     * to choose our result that can be retrieved by calling getRules()
     */
    public List getEvaluationList() {
        if (! isRuleFound()) {
            return new ArrayList();
        }
        return evalList;
    }

    /**
     * find generalization rules to explain most source codes with a minimal number of rules
     */
    private void mineCode() {
        /* init rule list token matrix */
        ruleSet = new RuleSet();
        for (int i = 0 ; i < matrix.getRowSize(); i++) {
            for (int j = i + 1 ; j < matrix.getRowSize(); j++) {
                Rule rule = generalizeRuleFromPair(i, j, matrix);

                //System.out.println("rule from pair " + i + ", " + j + ": " + rule);
                if (rule != null) {
                    ruleSet.merge(rule);
                }
            }
        }

        if (ruleSet.size() == 0) {
            bestEval = null;
            return;
        }

        /* as we permutate through all combination of applying rules, we collect the evaluataion result for each permutations in evalList */
        permutate(createIntegerList(ruleSet.size()), 0, evalList, ruleSet, matrix);

        /* by sorting based on score of evaluataion, we find a set of rules that explain all source codes using the smallest number of rules. */
        Collections.sort(evalList);

        /* bestEval holds the most preferable RuleSetEvaluation object:its score is the lowest, actually. */
        bestEval = (RuleSetEvaluation) evalList.get(0);
    }

    /**
     * evaluate a permutation of applying rule.
     */
    private static RuleSetEvaluation evaluate(List perm, RuleSet ruleList, TokenMatrix matrix) {

        Set toBeExplained = createIntegerSet(matrix.getRowSize());
        List activeRuleList = new ArrayList();

        for (Iterator i = perm.iterator(); i.hasNext(); ) {
            int ruleIdx = ((Integer) i.next()).intValue();
            Rule curRule = ruleList.getRule(ruleIdx);
            Set explainedByThisRule = curRule.getSupportingRowIndexSet();

            int howManyToBeNewlyExplained = 0;
            for (Iterator j = explainedByThisRule.iterator(); j.hasNext(); ) {
                if (toBeExplained.contains(j.next())) {
                    howManyToBeNewlyExplained++;
                }
            }

            /* if a candidate rule for adding explains one or less-than-one instance, the rule is not valid. it is because a rule need at least two supporing instances. */
            if (howManyToBeNewlyExplained > 1) {
                toBeExplained.removeAll(explainedByThisRule);
                activeRuleList.add(curRule);
            }

            if (toBeExplained.size() == 0) {
                return new RuleSetEvaluation(toBeExplained, activeRuleList, new ArrayList(perm));
            }
        }
        return new RuleSetEvaluation(toBeExplained, activeRuleList, new ArrayList(perm));
    }

    /**
     * kick it off by calling permutate([a b c d e], 0);
     * naming of argument comes from the idea that
     * we have a space, [a b c d e], which is separated into two part, head and tail,
     * and that tailIdx points the beginning element of tail part.
     *
     * three auxiliary arguments of evalList, ruleList, and matrix are not related to permutation itself,
     * but are required for evaluating permutations and storing evaluation results
     */
    private static boolean permutate(List space, int tailIdx, List evalList, RuleSet ruleList, TokenMatrix matrix) {
        if (tailIdx == space.size()) {
            RuleSetEvaluation eval = evaluate(space, ruleList, matrix);
            evalList.add(eval);

            /* terminate permutation if the optimum is found */
            if (eval.getRuleCount() == 1 && eval.getUnexplainedCount() == 0) {
                return true;
            }
        } else {
            for (int i = tailIdx; i < space.size(); i++) {
                swap(space, tailIdx, i);
                boolean stop = permutate(space, tailIdx + 1, evalList, ruleList, matrix);
                if (stop) {
                    return true;
                }
                swap(space, tailIdx, i);
            }
        }
        return false;
    }

    private static void swap(List space, int idx1, int idx2) {
        if (idx1 == idx2) {
            return;
        }
        Object temp = space.get(idx1);
        space.set(idx1, space.get(idx2));
        space.set(idx2, temp);
    }

    private static List createIntegerList(int max) {
        List ret = new ArrayList(max);
        for (int i = 0; i < max; i++) {
            ret.add(new Integer(i));
        }
        return ret;
    }

    private static Set createIntegerSet(int max) {
        Set ret = new HashSet(max);
        for (int i = 0; i < max; i++) {
            ret.add(new Integer(i));
        }
        return ret;
    }

    /**
     * generalize Rule from two rows in token matrix
     */
    protected static Rule generalizeRuleFromPair(int row1Idx, int row2Idx, TokenMatrix matrix) {
        Rule rule = new Rule();

        rule.addSupportingRowIndex(row1Idx);
        rule.addSupportingRowIndex(row2Idx);

        TokenRow row1 = matrix.getTokenRow(row1Idx);
        TokenRow row2 = matrix.getTokenRow(row2Idx);


        Set remainingNumberColumnIndexes = new HashSet();
        Set remainingStringColumnIndexes = new HashSet();

        for (int i = 0; i < row1.size(); i++) {
            CodeToken t1 = row1.get(i);
            CodeToken t2 = row2.get(i);

            if (t1.getDataType() == CodeToken.DELIM_TYPE) {
                continue;
            }

            if (t1.getTokenObject().equals(t2.getTokenObject())) {
                if (t1.getDataType() == CodeToken.STRING_TYPE) {
                    rule.addSameStringAssociation(i, t1.getString());
                } else {
                    rule.addSameNumberAssociation(i, t1.getNumber());
                }
                continue;

                /* todo: need debugging so fix not applied */
                // (2006 may fix #1) to repeated string and gapped number has priority)
                // commenting above line will make gapped number asso and repeated string asso override same number asso and same string asso.
                // because "i" will be included in either remainingNumberColumnIndexes or remainingStringColumnIndexes
            }

            /* collect remaining string columns and number columns */
            if (t1.getDataType() == CodeToken.STRING_TYPE) {
                remainingStringColumnIndexes.add(new Integer(i));
            } else {
                if (t1.getDataType() == CodeToken.INTEGER_TYPE && t2.getDataType() == CodeToken.INTEGER_TYPE) {
                    remainingNumberColumnIndexes.add(new Integer(i));
                } else {
                    /* here come double vs integer, integer vs double, or double vs double */
                    /* if a pair has different datatype for a number column, the pair cannot be generalized with one rule */
                    /* also two double datatype with different values cannot be generalized with one rule */
                    return null;
                }
            }
        }

        /*
         * make diff map, which has difference as a key and a set of column indexes with the same difference
         *
         * if following data are given
         *
         * column idx  | 1 4 5 6 7
         * row 1 value | 1 1 3 3 5
         * row 2 value | 2 2 4 4 8
         * -------------------------
         * diff value  | 1 1 1 1 3
         *
         * resulting diffMap will be as follows:
         * 1 -> {1, 4, 5, 6}
         * 3 -> {7}
         */
        Map diffMap = new HashMap();
        for (Iterator i = remainingNumberColumnIndexes.iterator(); i.hasNext(); ) {
            int numColIdx = ((Integer) i.next()).intValue();
            Integer diff = new Integer(row2.get(numColIdx).getInt() - row1.get(numColIdx).getInt());
            Set sameDiffSet = (Set) diffMap.get(diff);
            if (sameDiffSet == null) {
                sameDiffSet = new HashSet();
                diffMap.put(diff, sameDiffSet);
            }
            sameDiffSet.add(new Integer(numColIdx));
        }

        /* based on diffMap just generated, we will try to impose gap association on remaining number columns */
        for (Iterator i = diffMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            Set gappedColumnIndexSet = (Set) entry.getValue();
            if (gappedColumnIndexSet.size() > 1) {
                rule.addGappedNumberAssociation(gappedColumnIndexSet, row1.getTokenArray());
            } else {
                /* as we can see in above sample data, column idx 7 is the only column that has difference of 3.
                 * because a gap association assumes there exist at least two columns with the same difference
                 * we do not add gap association to such a column.
                 * moreover, now that we have no more association assumable to that column -- we already tried
                 * two all possible associations that were not applicable -- we can conclude that we cannot generalize
                 * any rule for this pair, hence we return null */
                return null;

                /* todo: need debugging so fix not applied */
                /* (2006 may fix #1) however, if the remaining number column indexes already has the same number association, it is okay. we can continue and return a rule */
//                for (Iterator j = remainingNumberColumnIndexes.iterator(); j.hasNext();) {
//                    Integer remainingNumColIdx = (Integer) j.next();
//                    if (rule.getTokenAssociation(remainingNumColIdx.intValue()) != null) {
//                        continue;
//                    } else {
//                        return null;
//                    }
//                }

            }
        }

        /*
         * make concat map, which has concat of two string in the same column as a key and a set of column indexes with the same concat string
         *
         * if following data are given
         *
         * column idx    |      2       3       8        9     10
         * row 1 value   |     ko     Sum      ko      Sum      B
         * row 2 value   |     ko     Sum      ko      Sum      C
         * -------------------------------------------------------
         * concat value  |  ko-ko  Sum-Sum  ko-ko  Sum-Sum    B-C
         *
         * resulting concatMap will be as follows:
         * ko-ko -> {2, 8}
         * Sum-Sum -> {3, 9}
         * B-C -> {10}
         */
        Map concatMap = new HashMap();
        for (Iterator i = remainingStringColumnIndexes.iterator(); i.hasNext(); ) {
            int numColIdx = ((Integer) i.next()).intValue();
            String concat = row2.get(numColIdx).getString() + "-" + row1.get(numColIdx).getString();
            Set sameConcatSet = (Set) concatMap.get(concat);
            if (sameConcatSet == null) {
                sameConcatSet = new HashSet();
                concatMap.put(concat, sameConcatSet);
            }
            sameConcatSet.add(new Integer(numColIdx));
        }

        /* based on concatMap just generated, we will try to impose repeated association on remaining string columns */
        for (Iterator i = concatMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            Set repeatedColumnIndexSet = (Set) entry.getValue();
            if (repeatedColumnIndexSet.size() > 1) {
                rule.addRepeatedStringAssociation(repeatedColumnIndexSet);
            } else {
                /* as we can see in above sample data, column idx 10 is the only column that has concat of B-C.
                 * because a repeated string association assumes there exist at least two columns with the same concat value
                 * we do not add repeated string association to such a column.
                 * moreover, now that we have no more association assumable to that column -- we already tried
                 * two all possible associations that were not applicable -- we can conclude that we cannot generalize */
                return null;

                /* todo: need debugging so fix not applied */
//                /* (2006 may fix #1) however, if the remaining string column indexes already has the same string association, it is okay. we can continue and return a rule */
//                for (Iterator j = remainingStringColumnIndexes.iterator(); j.hasNext();) {
//                    Integer remainingStrColIdx = (Integer) j.next();
//                    if (rule.getTokenAssociation(remainingStrColIdx.intValue()) != null) {
//                        continue;
//                    } else {
//                        return null;
//                    }
//                }

            }
        }

        return rule;
    }

//    /**
//     * update the specified row with a given srcCode string
//     * @param rowIdx
//     * @param srcCode
//     */
//    public void setCode(int rowIdx, String srcCode) {
//         this.srcCodes[rowIdx] = srcCode;
//    }

}