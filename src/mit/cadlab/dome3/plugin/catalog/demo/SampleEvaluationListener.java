package mit.cadlab.dome3.plugin.catalog.demo;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CLog;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationListener;

/**
 * User: Sangmok Han
 * Date: 2006. 7. 3.
 */
public class SampleEvaluationListener implements EvaluationListener {

    private EvaluationContext context;

    public SampleEvaluationListener(EvaluationContext context) {
        this.context = context;
    }

    public void mappingEvaluated(String qualifiedParamName) {
        CLog.log("mapping evaluated: outputNode=" + qualifiedParamName);
    }

    public void evaluationStarted() {
        CLog.log("evaluataion started: tracker=" + context.getEvaluationTracker());
    }

    public void evaluationEnded(boolean isSuccess) {
        CLog.log("evaluataion ended: isSuccess=" + isSuccess + ", tracker=" + context.getEvaluationTracker());
    }

    public void relationStarted(String relAlias) {
        CLog.log("relation started: relAlias=" + relAlias);
    }

    public void relationEnded(String relAlias, boolean isSuccess) {
        CLog.log("relation ended: relAlias=" + relAlias + ", isSuccess=" + isSuccess);
    }

    public void parameterStatusChanged(int newStatus, String qualifiedParamName) {
        String statusStr = "white";
        if (newStatus == CConstant.GREEN_STATUS) {
            statusStr = "green";
        } else if (newStatus == CConstant.RED_STATUS) {
            statusStr = "red";
        }

        CLog.log("param status changed: newStatus=" + statusStr + ", paramName=" + qualifiedParamName);
    }

    public void parameterValueChanged(Object newValue, String qualifiedParamName) {
        CLog.log("param value changed: newValue=" + newValue + ", paramName=" + qualifiedParamName);
    }
}
