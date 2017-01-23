package mit.cadlab.dome3.plugin.catalog.ui;

import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.CEnumeration;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.runtime.RelationExecutionFailure;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Iterator;

/**
 *
 * Utility class for static methods for implementing evaluation mode in the UI
 *
 * User: Sangmok Han
 * Date: Sep 18, 2006
 */
public class EvaluationMode {

    /**
     * initialize each of editor panes
     * this method called when a user navigate to this implementation or when the evaluation mode starts
     */
    public static void initEditorPanes(ComponentReference compRef) {
        compRef.getCurrentEvaluationContext().initializeDataObjectAndParameterStatusBasedOnInterfaceDefinition();
        setAllModifiedParam(compRef);
        updateValueOfAllValueEditorPanes(compRef);
        updateStatusOfAllValueEditorPanes(compRef);
    }

    /**
     * the only difference from initEditorPanes(ComponentReference) is that calling this method will keep the current value in the editor
     */
    public static void resetEditorPanes(ComponentReference compRef) {
        compRef.getCurrentEvaluationContext().initializeDataObjectAndParameterStatusBasedOnInterfaceDefinition();

//        /* keep current editor values */
//        for (Iterator i = compRef.getInterfaceInputCells().iterator(); i.hasNext(); ) {
//            InterfaceInputCell iic = (InterfaceInputCell) i.next();
//            iic.getValueEditorPane().commitEditorValue();
//        }

        setAllModifiedParam(compRef);

//        System.out.println("get ready : dataObjMap = " + compRef.getCurrentEvaluationContext().getDataObjectMap());
//        System.out.println("getModifiedParams(): " + getModifiedParamSet(compRef));

        //updateValueOfAllValueEditorPanes(compRef);
        updateStatusOfAllValueEditorPanes(compRef);
    }

    /**
     * this method will update the value and status of all parameters based on the current CModel and EvaluationContext
     * this method is called when a new implementation is displayed.
     */
    public static void updateEditorPanes(ComponentReference compRef) {
//        System.out.println("calling update.................");

//        /* keep current editor values */
//        for (Iterator i = compRef.getInterfaceInputCells().iterator(); i.hasNext(); ) {
//            InterfaceInputCell iic = (InterfaceInputCell) i.next();
//            iic.getValueEditorPane().commitEditorValue();
//        }

        updateValueOfAllValueEditorPanes(compRef);
        updateStatusOfAllValueEditorPanes(compRef);
    }

    public static void run(ComponentReference compRef) {
        Timer execTimer = new Timer(true);

        class ExecTimerTask extends TimerTask {
            ComponentReference compRef;

            ExecTimerTask(ComponentReference compRef) {
                this.compRef = compRef;
            }

            public void run() {
                compRef.getModelEditor().setStopClickedBefore(false);
                // pause mapping script reference checker
                compRef.getModelEditor().getDynamicComponent(ModelEditor.MAPPING_SCRIPT_REFERENCE_CHECKER).setEnabled(false);

                compRef.getImplementationEditor().clearFailureReport();
                //todo: uncomment below if evaluation progress window needs to be shown
                //compRef.getImplementationEditor().showEvaluationProgressWindow();
                ModelEditorKit.RunEvaluationAction.setEnabled(false);
                Set modifiedParamNameSet = getModifiedParamSet(compRef);
                for (Iterator i = compRef.getInterfaceInputCells().iterator(); i.hasNext(); ) {
                    InterfaceInputCell iic = (InterfaceInputCell) i.next();
                    iic.getValueEditorPane().commitEditorValue();
                }
                compRef.getCurrentEvaluationContext().evaluate(modifiedParamNameSet);
                ModelEditorKit.RunEvaluationAction.setEnabled(true);

                // resume mapping script reference checker
                compRef.getModelEditor().getDynamicComponent(ModelEditor.MAPPING_SCRIPT_REFERENCE_CHECKER).setEnabled(true);

                RelationExecutionFailure[] failures = compRef.getCurrentEvaluationContext().getRelationExecutionFailures();

                if (failures.length > 0) {
                    StringBuffer sb = new StringBuffer();

                    sb.append("Some error occurred. There were " + failures.length + " error(s) during the execution:\n");
                    for (int i = 0; i < failures.length; i++) {
                        sb.append(" [" + (i + 1) + "] " + failures [i].getMessage() + " @ relAlias=" + failures [i].getRelAlias());
                        if (i != failures.length - 1) {
                            sb.append("\n");
                        }
                    }
                    CLog.info(sb.toString());
                    compRef.getImplementationEditor().setFailureReport(sb.toString());
                } else {
                    CLog.info("Executed successfully. There was no error during the execution.");
                    compRef.getImplementationEditor().hideEvaluationProgressWindow();
                    clearModifiedParamSet(compRef);
                }
            }
        };
        ExecTimerTask timerTask = new ExecTimerTask(compRef);
        execTimer.schedule(timerTask, 100);
    }



    private static void slowDown() {
        try {
            CLog.println("[SLOW DOWN] --------------- [SLOW DOWN]");
            IdlingThread ht = new IdlingThread(1000, new PrintWriter(System.out), new String[] { "Step1", "Step2", "Step3", "Step1", "Step2","Step1", "Step2","Step1", "Step2","Step1", "Step2","Step1", "Step2","Step1", "Step2","Step1", "Step2" });

            ht.start();

            try {
                ht.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void stop(ComponentReference compRef) {
        EvaluationMode.desaturateRelationBarColor(compRef);
        compRef.getImplementationEditor().hideEvaluationProgressWindow();
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        compRef.getCurrentCImplementation().initializeParameterStatus();
        evalContext.refresh();
        evalContext.close(); // this will close connections
        EvaluationMode.resetEditorPanes(compRef);
        if (compRef.getModelEditor().isStopClickedBefore()) {
            EvaluationMode.initEditorPanes(compRef);
        } else {
            compRef.getModelEditor().setStopClickedBefore(true);
        }
    }

    public static void desaturateRelationBarColor(ComponentReference compRef) {
        compRef.getInterfaceBar().getCenterPanel().setBackground(UIUtil.REL_GREY_BG_COLOR);
        for (Iterator i = compRef.getRelationBars().iterator(); i.hasNext(); ) {
            RelationBar relBar = (RelationBar) i.next();
            relBar.getCenterPanel().setBackground(UIUtil.REL_GREY_BG_COLOR);
        }
    }

    public static void saturateRelationBarColor(ComponentReference compRef) {
        compRef.getInterfaceBar().getCenterPanel().setBackground(UIUtil.REL_BG_COLORS [0]);
        for (Iterator i = compRef.getRelationBars().iterator(); i.hasNext(); ) {
            RelationBar relBar = (RelationBar) i.next();
            int colorIdx = compRef.getColorIndexForRelAlias(relBar.getRelAlias(), false);
            relBar.getCenterPanel().setBackground(UIUtil.REL_BG_COLORS[colorIdx]);
        }
    }

    public static void stallParameter(String qualifiedParamName, ComponentReference compRef) {
        compRef.getCurrentCNamingService().getInterfaceInputParameter(qualifiedParamName).setStatus(CConstant.YELLOW_STATUS);
        compRef.getCurrentCNamingService().getInterfaceInputParameter(qualifiedParamName).copyCurrentStatusToMappingNodes();
    }

    public static void unstallParameter(String qualifiedParamName, ComponentReference compRef) {
        if (compRef.getCurrentCNamingService().getInterfaceInputParameter(qualifiedParamName).getStatus() == CConstant.YELLOW_STATUS) {
            compRef.getCurrentCNamingService().getInterfaceInputParameter(qualifiedParamName).setStatus(CConstant.GREEN_STATUS);
            compRef.getCurrentCNamingService().getInterfaceInputParameter(qualifiedParamName).copyCurrentStatusToMappingNodes();
        }
    }

    public static void updateStatusOfParameters(ComponentReference compRef) {
        EvaluationContext evalContext = compRef.getCurrentEvaluationContext();
        CNamingService namingService = compRef.getCurrentCNamingService();

        /* when it is not a first time evaluation, all parameters should be consistent, white status */
        if (evalContext.isAfterFirstEvaluation()) {
            Set unmodifiedParamNames = new HashSet(); // first collect all interface input param names
            for (Iterator i = namingService.getInterfaceInputParameters().iterator(); i.hasNext(); ) {
                CInterfaceInputParameter param = (CInterfaceInputParameter) i.next();
                unmodifiedParamNames.add(param.getName());
            }
            Set modifiedParamNames = getModifiedParamSet(compRef);
            unmodifiedParamNames.removeAll(modifiedParamNames); // second remove all modified input param names

            for (Iterator i = unmodifiedParamNames.iterator(); i.hasNext(); ) {
                String paramName = (String) i.next();
                CInterfaceInputParameter param = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + paramName);
                Set drivenParamNames = param.getDrivensBy(false);
                for (Iterator j = drivenParamNames.iterator(); j.hasNext();) {
                    String drivenParamName = (String) j.next();
                    namingService.getParameter(drivenParamName).setStatus(CConstant.WHITE_STATUS);
                    namingService.getParameter(drivenParamName).copyCurrentStatusToMappingNodes();
                }
            }
        }

        Set modifiedParamNames = getModifiedParamSet(compRef);
        for (Iterator i = modifiedParamNames.iterator(); i.hasNext(); ) {
            String paramName = (String) i.next();
            CInterfaceInputParameter param = namingService.getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + paramName);
            Set drivenParamNames = param.getDrivensBy(false);
            for (Iterator j = drivenParamNames.iterator(); j.hasNext();) {
                String drivenParamName = (String) j.next();
                namingService.getParameter(drivenParamName).setStatus(CConstant.RED_STATUS);
                namingService.getParameter(drivenParamName).copyCurrentStatusToMappingNodes();
            }
        }

//        System.out.println("getModifiedParamSet(compRef) = " + getModifiedParamSet(compRef));

    }

    /** update the value based on CDataObject value of all parameters */
    public static void updateValueOfAllValueEditorPanes(ComponentReference compRef) {
        List cells = compRef.getAllCells();
        for (Iterator i = cells.iterator(); i.hasNext();) {
            BaseCell cell = (BaseCell) i.next();

            if (cell.getValueEditorPane() instanceof RealValueEditorPane) {
                String text = ((RealValueEditorPane) cell.getValueEditorPane()).getTextField().getText();
            }
            cell.getValueEditorPane().updateEditorValue();
        }
    }

    /** update the value based on CDataObject value */
    public static void updateValueOfValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        BaseCell cell = compRef.getCell(qualifiedParamName);
        cell.getValueEditorPane().updateEditorValue();
    }

    /** update the value based on CDataObject value of all parameters */
    public static void updateStatusOfAllValueEditorPanes(ComponentReference compRef) {
        CNamingService namingService = compRef.getCurrentCNamingService();
        Set paramSet = namingService.getParameters();
        for (Iterator i = paramSet.iterator(); i.hasNext();) {
            CParameter param = (CParameter) i.next();
            BaseCell cell = compRef.getCell(param.getQualifiedName());
            if (param.getStatus() == CConstant.GREEN_STATUS) {
                cell.getValueEditorPane().setBackground(UIUtil.GREEN_STATUS_BG);
            } else if (param.getStatus() == CConstant.RED_STATUS || param.getStatus() == CConstant.UNASSIGNED_STATUS) {
                cell.getValueEditorPane().setBackground(UIUtil.RED_STATUS_BG);
            } else if (param.getStatus() == CConstant.WHITE_STATUS) {
                cell.getValueEditorPane().setBackground(UIUtil.WHITE_STATUS_BG);
            } else if (param.getStatus() == CConstant.YELLOW_STATUS) {
                cell.getValueEditorPane().setBackground(UIUtil.YELLOW_STATUS_BG);
            }
        }
    }

    /** update the status based on CParameter status */
    public static void updateStatusOfValueEditorPane(String qualifiedParamName, ComponentReference compRef) {
        CNamingService namingService = compRef.getCurrentCNamingService();
        CParameter param = namingService.getParameter(qualifiedParamName);
        BaseCell cell = compRef.getCell(qualifiedParamName);
        if (param.getStatus() == CConstant.GREEN_STATUS) {
            cell.getValueEditorPane().setBackground(UIUtil.GREEN_STATUS_BG);
        } else if (param.getStatus() == CConstant.RED_STATUS) {
            cell.getValueEditorPane().setBackground(UIUtil.RED_STATUS_BG);
        } else if (param.getStatus() == CConstant.WHITE_STATUS) {
            cell.getValueEditorPane().setBackground(UIUtil.WHITE_STATUS_BG);
        } else if (param.getStatus() == CConstant.YELLOW_STATUS) {
            cell.getValueEditorPane().setBackground(UIUtil.YELLOW_STATUS_BG);
        }
    }

    public static String formatDouble(double value) {
        NumberFormat numFormat = NumberFormat.getInstance();
        numFormat.setMaximumFractionDigits(6);
        numFormat.setGroupingUsed(true);
        DecimalFormat decFormat = new DecimalFormat("0.0000E0");
        if ((1000000000 > Math.abs(value) && Math.abs(value) > 0.0001) || value == 0) {
            return numFormat.format(value);
        } else {
            return decFormat.format(value);
        }
    }

    public static Number parseIntOrDouble(String doubleStr) throws ParseException {
        if (doubleStr.indexOf("E") == -1) {
            NumberFormat numFormat = NumberFormat.getInstance();
            numFormat.setGroupingUsed(true);
            return numFormat.parse(doubleStr);
        } else {
            DecimalFormat decFormat = new DecimalFormat("0.000000E0");
            return decFormat.parse(doubleStr);
        }
    }

    public static String formatInt(int value) {
        NumberFormat numFormat = NumberFormat.getInstance();
        numFormat.setMaximumFractionDigits(6);
        numFormat.setGroupingUsed(true);
        DecimalFormat decFormat = new DecimalFormat("0.0000E0");
        if (1000000000 > Math.abs(value) || value == 0) {
            return numFormat.format(value);
        } else {
            return decFormat.format(value);
        }
    }

    public static String toValueDisplayString(CDataObject dataObj) {
        String ret;

        if (dataObj instanceof CReal) {
            double value = ((CReal) dataObj).getDoubleValue();
            ret = formatDouble(value);
        } else if (dataObj instanceof CInteger) {
            ret = formatInt(((CInteger) dataObj).getIntegerValue());
        } else if (dataObj instanceof CString) {
            ret = ((CString) dataObj).getStringValue();
        } else if (dataObj instanceof CBoolean) {
            ret = Boolean.toString(((CBoolean) dataObj).getBooleanValue());
        } else if (dataObj instanceof CEnumeration) {
        	CEnumeration enm = (CEnumeration) dataObj;
            //ret = enum.getSelectedEnumName() + "(" + enum.getSelectedEnumValue() + ")";
            ret = enm.getSelectedEnumName();
        } else if (dataObj instanceof CFile) {
            CFile file = (CFile) dataObj;
            //ret = new File(file.getFilePath()).getName();
            ret = new String((byte[]) file.getValue());
        } else if (dataObj instanceof CMatrix) {
            CMatrix matrix = (CMatrix) dataObj;
            ret = "[" + matrix.getRowSize() + " x " + matrix.getColumnSize() + "]";
        } else if (dataObj instanceof CVector) {
            CVector vector = (CVector) dataObj;
            ret = "[" + vector.size() + " " + (vector.size() > 1 ? "elements" : "element") + "]";
        } else {
            throw new RuntimeException("unsupported data object class: " + dataObj.getClass().getName());
        }
        return ret;
    }

    /** set all interface input parameters to be modified. modifiedParamMap is a member variable of ModelEditor. this method is used to manipulate the variable. */
    public static void setAllModifiedParam(ComponentReference compRef) {
        clearModifiedParamSet(compRef);
        Set params = compRef.getCurrentCNamingService().getInterfaceInputParameters();
        for (Iterator i = params.iterator(); i.hasNext(); ) {
            CParameter param = (CParameter) i.next();
            addModifiedParam(param.getName(), compRef);
        }
    }

    /** modifiedParamMap is a member variable of ModelEditor. this method is used to manipulate the variable. */
    public static void addModifiedParam(String localParamName, ComponentReference compRef) {
        getModifiedParamSet(compRef).add(localParamName);
    }

    /** modifiedParamMap is a member variable of ModelEditor. this method is used to manipulate the variable. */
    public static void removeModifiedParam(String localParamName, ComponentReference compRef) {
        getModifiedParamSet(compRef).remove(localParamName);
    }

    /** modifiedParamMap is a member variable of ModelEditor. this method is used to manipulate the variable. */
    public static void clearModifiedParamSet(ComponentReference compRef) {
        getModifiedParamSet(compRef).clear();
    }

    /** modifiedParamMap is a member variable of ModelEditor. this method is used to access the variable. */
    public static Set getModifiedParamSet(ComponentReference compRef) {
        String itfName = compRef.getImplementationEditor().getInterfaceName();
        String implName = compRef.getImplementationEditor().getImplementationName();
        String key = itfName + "|" + implName;
        Set paramNameSet = (Set) compRef.getModelEditor().getModifiedParamSetMap().get(key);
        if (paramNameSet == null) {
            paramNameSet = new HashSet();
            compRef.getModelEditor().getModifiedParamSetMap().put(key, paramNameSet);
        }
        return paramNameSet;
    }
}

