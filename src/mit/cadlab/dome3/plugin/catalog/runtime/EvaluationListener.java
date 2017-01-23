package mit.cadlab.dome3.plugin.catalog.runtime;

/**
 * User: Sangmok Han
 * Date: 2006. 6. 30.
 */
public interface EvaluationListener {
    public void mappingEvaluated(String qualifiedParamName);

    public void evaluationStarted();
    public void evaluationEnded(boolean isSuccess);

    public void relationStarted(String relAlias);
    public void relationEnded(String relAlias, boolean isSuccess);

    public void parameterStatusChanged(int newStatus, String qualifiedParamName);
    public void parameterValueChanged(Object newValue, String qualifiedParamName);
}
