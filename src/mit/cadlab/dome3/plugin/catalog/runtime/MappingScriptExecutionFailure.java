package mit.cadlab.dome3.plugin.catalog.runtime;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 17.
 */
public class MappingScriptExecutionFailure {
    private String paramName; // qualified paramName whose mapping script was executed.
    private String mappingScript;
    private EvaluationContext evalContext;
    private String message;
    private Exception exception;

    public MappingScriptExecutionFailure(String paramName, String mappingScript, EvaluationContext evalContext, String message, Exception exception) {
        this.paramName = paramName;
        this.mappingScript = mappingScript;
        this.evalContext = evalContext;
        this.message = message;
        this.exception = exception;
    }

    public String getParamName() {
        return paramName;
    }

    public String getMappingScript() {
        return mappingScript;
    }

    public EvaluationContext getEvalContext() {
        return evalContext;
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }

    public String toString() {
        return "[MapExecFailure: paramName=" + paramName + ", mappingScript=" + mappingScript + ", message=" + message + ", exception=" + exception + "]";
    }
}
