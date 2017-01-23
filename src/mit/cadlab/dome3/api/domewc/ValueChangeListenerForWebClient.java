package mit.cadlab.dome3.api.domewc;

import mit.cadlab.dome3.api.ParameterValueChangeEvent;
import mit.cadlab.dome3.api.ParameterValueChangeListener;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;

import java.text.NumberFormat;
import java.io.PrintWriter;
import java.io.File;
import java.io.Writer;
import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 19.
 */
public class ValueChangeListenerForWebClient implements ParameterValueChangeListener {
    PrintWriter writer = null;
    private boolean debug = false;
    public static final String AFTER_VALUE_CHANGED_NOTIFICATION_FUNCTION_NAME = "afterValueChangedTaskFinished";
	String START_OF_SCRIPT = "<script language='JavaScript'>";
	String END_OF_SCRIPT= "</script>";
    NumberFormat numFormat;
    Map paramMap;
    Map afterValChgTaskSetMap;

    public ValueChangeListenerForWebClient(PrintWriter writer, Map paramMap, boolean debug) {
        this.writer = writer;
        this.debug = debug;
        this.paramMap = paramMap;
        numFormat = NumberFormat.getInstance();
        setMaximumFractionDigits(4);
        numFormat.setGroupingUsed(true);
        afterValChgTaskSetMap = new HashMap();
    }

    public void setMaximumFractionDigits(int digits) {
        numFormat.setMaximumFractionDigits(digits);
    }

    public void addAfterValueChangedTask(AfterValueChangedTask task) {
        Set taskSet = (Set) afterValChgTaskSetMap.get(task.getParamName());
        if (taskSet == null) {
            taskSet = new HashSet();
            afterValChgTaskSetMap.put(task.getParamName(), taskSet);
        }
        taskSet.add(task);
    }

    private void writeScript(String contents) {
        String scriptStr =(START_OF_SCRIPT + contents + END_OF_SCRIPT);
        writer.println(scriptStr);
        if (debug) { System.out.println(scriptStr); }
    }

    public void valueChanged(ParameterValueChangeEvent event) {
        String paramName = event.getInterfaceParameterClient().getName();
        if (paramName.indexOf("'") != -1 || paramName.indexOf("\"") != -1) {
            System.out.println("Parameter Name contains ' or \". This could result in JavaScript error.");
        }

        if (DomeMatrix.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            DomeMatrix matrix = (DomeMatrix) event.getInterfaceParameterClient().getCurrentDataObject();
            paramMap.put(paramName, matrix.getData());
            writeScript("window.parent.updateMatrixValue('" + paramName + "', " + matrix.getRowCount() + ", " + matrix.getColumnCount() + ");");
        } else if (DomeVector.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            DomeVector vector = (DomeVector) event.getInterfaceParameterClient().getCurrentDataObject();
            paramMap.put(paramName, vector.getData());
            writeScript("window.parent.updateVectorValue('" + paramName + "', " + vector.getSize() + ");");
        } else if (DomeEnumeration.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            DomeEnumeration enm = (DomeEnumeration) event.getInterfaceParameterClient().getCurrentDataObject();
            paramMap.put(paramName, new Integer(enm.getLastSelection()));
            int lastSelection = enm.getLastSelection();
            writeScript("window.parent.updateEnumerationValue('" + paramName + "', " + enm.getLastSelection() + ", '" + enm.getElementName(lastSelection) + "', '" + enm.getElementValue(lastSelection) + "');");
        } else if (FileData.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            FileData fileData = (FileData) event.getInterfaceParameterClient().getCurrentDataObject();
            File file = new File(fileData.getFilePath());
            paramMap.put(paramName, file.getName());
            writeScript("window.parent.updateFileValue('" + paramName + "', '" + file.getName() + "', '" + fileData.getFilePath() + "');");
            if (debug) { System.out.println("file param '" + paramName + "' has been downloaded to " + fileData.getFilePath()); }
        } else if (DomeReal.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            paramMap.put(paramName, (Double) event.getNewValues().get(0));
            String formattedDouble = numFormat.format(((Double) event.getNewValues().get(0)).doubleValue());
            writeScript("window.parent.updateNumberValue('" + paramName + "', '" + formattedDouble + "');");
        } else if (DomeInteger.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            paramMap.put(paramName, (Integer) event.getNewValues().get(0));
            String formattedDouble = numFormat.format(((Integer) event.getNewValues().get(0)).intValue());
            writeScript("window.parent.updateNumberValue('" + paramName + "', '" + formattedDouble + "');");
        } else if (DomeBoolean.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            paramMap.put(paramName, (Boolean) event.getNewValues().get(0));
            String formattedBoolean = ((Boolean) event.getNewValues().get(0)).toString();
            writeScript("window.parent.updateBooleanValue('" + paramName + "', '" + formattedBoolean + "');");
        } else {
            /* string or text */
            paramMap.put(paramName, event.getNewValues().get(0));
            String valueStr = event.getNewValues().get(0).toString();
            writeScript("window.parent.updateTextValue('" + paramName + "', '" + valueStr + "');");
        }

        Set taskSet = (Set) afterValChgTaskSetMap.get(paramName);
        if (taskSet != null) {
            for (Iterator i = taskSet.iterator(); i.hasNext();) {
                AfterValueChangedTask task = (AfterValueChangedTask) i.next();
                task.run(paramName, paramMap.get(paramName), paramMap);
                writeScript("window.parent." + AFTER_VALUE_CHANGED_NOTIFICATION_FUNCTION_NAME + "('" + paramName + "');");
            }
        }

        writer.flush();
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
}