package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import mit.cadlab.dome3.plugin.catalog.core.CLog;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationListener;

import java.util.List;
import java.util.Iterator;

/**
 * User: Sangmok Han
 * Date: Sep 21, 2006
 */
public class EvaluationModeListener implements EvaluationListener {
    private EvaluationContext context;
    private ComponentReference compRef;

    public EvaluationModeListener(EvaluationContext context, ComponentReference compRef) {
        this.context = context;
        this.compRef = compRef;
    }

    public void mappingEvaluated(String qualifiedParamName) {
        //speedup Clog.debug("mapping evaluated: outputNode=" + qualifiedParamName);
    }

    public void evaluationStarted() {
        EvaluationMode.desaturateRelationBarColor(compRef);
        compRef.getInterfaceBar().getCenterPanel().setBackground(UIUtil.REL_ACTIVE_BG_COLOR);
        //speedup Clog.debug("evaluataion started: tracker=" + context.getEvaluationTracker());
    }

    public void evaluationEnded(boolean isSuccess) {
        compRef.getInterfaceBar().getCenterPanel().setBackground(UIUtil.REL_GREY_BG_COLOR);

        if (! isSuccess) {
            compRef.getImplementationEditor().showEvaluationProgressWindow();
        }
        //speedup Clog.debug("evaluataion ended: isSuccess=" + isSuccess + ", tracker=" + context.getEvaluationTracker());
    }

    public void relationStarted(String relAlias) {
        compRef.getRelationBar(relAlias).getCenterPanel().setBackground(UIUtil.REL_ACTIVE_BG_COLOR);
        //speedup Clog.debug("relation started: relAlias=" + relAlias);
    }

    public void relationEnded(String relAlias, boolean isSuccess) {
        compRef.getRelationBar(relAlias).getCenterPanel().setBackground(UIUtil.REL_GREY_BG_COLOR);
        //speedup Clog.debug("relation ended: relAlias=" + relAlias + ", isSuccess=" + isSuccess);
    }

    public void parameterStatusChanged(int newStatus, String qualifiedParamName) {
        String statusStr = "white";
        if (newStatus == CConstant.GREEN_STATUS) {
            statusStr = "green";
            compRef.getCell(qualifiedParamName).getValueEditorPane().setBackground(UIUtil.GREEN_STATUS_BG);
        } else if (newStatus == CConstant.RED_STATUS) {
            statusStr = "red";
            compRef.getCell(qualifiedParamName).getValueEditorPane().setBackground(UIUtil.RED_STATUS_BG);
        } else if (newStatus == CConstant.WHITE_STATUS) {
            statusStr = "white";
            compRef.getCell(qualifiedParamName).getValueEditorPane().setBackground(UIUtil.WHITE_STATUS_BG);
        }

        //speedup Clog.debug("param status changed: newStatus=" + statusStr + ", paramName=" + qualifiedParamName);
    }

    public void parameterValueChanged(Object newValue, String qualifiedParamName) {
        compRef.getCell(qualifiedParamName).getValueEditorPane().updateEditorValue();
        //speedup Clog.debug("param value changed: newValue=" + newValue + ", paramName=" + qualifiedParamName);
    }
}
