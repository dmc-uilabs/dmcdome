package mit.cadlab.dome3.plugin.catalog.runtime;

import mit.cadlab.dome3.plugin.catalog.core.CRelation;

/**
 * This is not a typical exception class because it is not thrown.
 * Instead, it is accessed using a method of EvaluationContext after each evaluate() call.
 * No exception will be thrown during the evaluate() call.
 * We tell if the evaluate() call was successful by listening to EvaluationListener.evaluationEnded(boolean isSuccess).
 * When the call is found to be unsuccessful, to know why evaluate() finished unsuccessfully,
 * invoke getEvaluationExceptions():RelationExecutionFailure[].
 * RelationExecutionFailure contains information about where an exception occurred.
 * Note that when the evaluate() call is successful, getEvaluationExceptions() returns empty array.
 *
 * User: Sangmok Han
 * Date: 2006. 8. 16.
 */
public class RelationExecutionFailure {
    private String relAlias;
    private int trackerId;
    private EvaluationContext evalContext;
    private String message;
    private Exception exception;

    public RelationExecutionFailure(String relAlias, int relTrackerId, String message, EvaluationContext evalContext, Exception exception) {
        this.message = message;
        this.exception = exception;
        this.relAlias = relAlias;
        this.trackerId = relTrackerId;
        this.evalContext = evalContext;
    }

    public String getRelAlias() {
        return relAlias;
    }

    public CRelation getRelation() {
        return evalContext.getNamingService().getRelation(relAlias);
    }

    public int getTrackerId() {
        return trackerId;
    }

    public EvaluationContext getEvalContext() {
        return evalContext;
    }

    public String getMessage() {
        if (exception == null) {
            return message;
        } else {
            return message + " : " + exception.getMessage();
        }
    }

    public Exception getException() {
        return exception;
    }

    public String toString() {
        return "[RelExecFailure: relAlias=" + relAlias + ", trackerId=" + trackerId + ", message=" + message + ", exception=" + exception + "]";
    }
}
