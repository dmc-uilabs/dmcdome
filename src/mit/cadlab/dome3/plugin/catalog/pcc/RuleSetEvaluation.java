package mit.cadlab.dome3.plugin.catalog.pcc;

/**
 * User: Sangmok Han
 * Date: 2005. 12. 28.
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * a class to store evaluation result of the permutation of a rule set
 */
public class RuleSetEvaluation implements Comparable {
    private final int WEIGHT_RULE_SIZE = 1;
    private final int WEIGHT_UNEXPLAINED_COUNT = 1;
    private List activeRuleList;
    private Set unexplainedRowIndexes;
    private List perm;
    private boolean isActiveRulesSorted = false;

    public RuleSetEvaluation(Set unexplainedRowIndexes, List activeRuleList, List perm) {
        this.unexplainedRowIndexes = unexplainedRowIndexes;
        this.activeRuleList = activeRuleList;
        this.perm = perm;
    }

    public int getUnexplainedCount() {
        return unexplainedRowIndexes.size();
    }

    public Set getUnexplainedRowIndexes() {
        return unexplainedRowIndexes;
    }

    public int getRuleCount() {
        return activeRuleList.size();
    }

    public List getActiveRuleList() {
        if (! isActiveRulesSorted) {
            Collections.sort(activeRuleList, new Comparator() {
                public int compare(Object obj1, Object obj2) {
                    Rule rule1 = (Rule) obj1;
                    Rule rule2 = (Rule) obj2;
                    return rule2.getSupportingRowIndexSet().size() - rule1.getSupportingRowIndexSet().size();
                }
            });
        }
        return activeRuleList;
    }

    public List getPermutation() {
        return perm;
    }

    /**
     * the smaller the better
     */
    public int getScore() {
        return (WEIGHT_RULE_SIZE * getRuleCount() + WEIGHT_UNEXPLAINED_COUNT * getUnexplainedCount());
    }

    /**
     * remind that the score is the smaller, the better
     */
    public int compareTo(Object comparedEval) {
        if (! (comparedEval instanceof RuleSetEvaluation)) {
            throw new RuntimeException("compareTo() cannot be called against other classes than RuleSetEvaluation: now it is called against " + comparedEval.getClass().getName());
        }
        return this.getScore() - ((RuleSetEvaluation) comparedEval).getScore();
    }

    public String toString() {
        String ret = "[RuleSetEval: ";
        ret = ret + "perm=" + perm + ", rulecount=" + getRuleCount() + ", unexplained=" + getUnexplainedCount() + ", unexplained=" + getUnexplainedRowIndexes() + "]";
        return ret;
    }
}