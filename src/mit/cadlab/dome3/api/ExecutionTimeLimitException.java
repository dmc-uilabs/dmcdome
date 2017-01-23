package mit.cadlab.dome3.api;

/**
 * RuntimeException thrown from submit() method when submit() takes longer than the execution time limit.
 * User: Sangmok Han
 * Date: 2005. 1. 19.
 */
public class ExecutionTimeLimitException extends RuntimeException {
    public ExecutionTimeLimitException(long executionTimeLimit) {
        super("The parametric changes could not be solved in the execution time limit (=" + executionTimeLimit + " milliseconds). The values in parameters might not be consistent.");
    }
}
