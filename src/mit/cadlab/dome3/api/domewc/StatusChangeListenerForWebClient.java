package mit.cadlab.dome3.api.domewc;

import mit.cadlab.dome3.api.ParameterStatusChangeListener;
import mit.cadlab.dome3.api.ParameterStatusChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;

import java.io.PrintWriter;
import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 19.
 */
public class StatusChangeListenerForWebClient implements ParameterStatusChangeListener {
    PrintWriter writer = null;
    private boolean debug = false;
	private String START_OF_SCRIPT = "<script language='JavaScript'>";
	private String END_OF_SCRIPT= "</script>";
    private Map afterStatChgIdSetMap;

    public StatusChangeListenerForWebClient(PrintWriter writer, boolean debug) {
        this.writer = writer;
        this.debug = debug;
        this.afterStatChgIdSetMap = new HashMap();
    }

    public void statusChanged(ParameterStatusChangeEvent event) {
        if (ParameterStatusChangeEvent.SOLVING_SUCCESS_EVENT.equals(event)) {
            writeScript("window.parent.finishSolving('success');");
            return;
        } else if (ParameterStatusChangeEvent.SOLVING_FAILURE_EVENT.equals(event)) {
            writeScript("window.parent.finishSolving('failure');");
            return;
        }

        String paramName = event.getInterfaceParameterClient().getName();
        if (paramName.indexOf("'") != -1 || paramName.indexOf("\"") != -1) {
            System.out.println("Invalid parameter name: it contains ' or \": " + paramName);
        }

        if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(event.getNewStatus())) {
            writeScript("window.parent.notifyGreen('" + paramName + "');");
            doStatusNotification(paramName, "notifyGreen");
        } else if (Parameter.VALUE_STATUS_CONSISTENT.equals(event.getNewStatus())) {
            writeScript("window.parent.notifyWhite('" + paramName + "');");
            doStatusNotification(paramName, "notifyWhite");
        } else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(event.getNewStatus())) {
            writeScript("window.parent.notifyRed('" + paramName + "');");
            doStatusNotification(paramName, "notifyRed");
        } else if (Parameter.VALUE_STATUS_STALE.equals(event.getNewStatus())) {
            // unreachable condition. do nothing.
        }
        writer.flush();
    }

    /** add a new id to be notified when the given paramName changes its status */
    public void addStatusNotification(String paramName, String idToBeNotifiedTogether) {
        Set idSet = (Set) afterStatChgIdSetMap.get(paramName);
        if (idSet == null) {
            idSet = new HashSet();
            afterStatChgIdSetMap.put(paramName, idSet);
        }
        idSet.add(idToBeNotifiedTogether);
    }

    public String[] getStatusNotification(String paramName) {
        Set idSet = (Set) afterStatChgIdSetMap.get(paramName);
        if (idSet == null) {
            return new String[0];
        } else {
            return (String[]) idSet.toArray(new String[idSet.size()]);
        }
    }

    private void doStatusNotification(String paramName, String funcName) {
        Set idSet = (Set) afterStatChgIdSetMap.get(paramName);
        if (idSet != null) {
            for (Iterator i = idSet.iterator(); i.hasNext();) {
                String id = (String) i.next();
                writeScript("window.parent." + funcName + "('" + id + "');");
            }
        }
    }

    private void writeScript(String contents) {
        String scriptStr =(START_OF_SCRIPT + contents + END_OF_SCRIPT);
        writer.println(scriptStr);
        if (debug) { System.out.println(scriptStr); }
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
}