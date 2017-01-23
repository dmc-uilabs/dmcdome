package mit.cadlab.dome3.api.domewc;

import mit.cadlab.dome3.api.ParameterStatusChangeEvent;
import mit.cadlab.dome3.api.ParameterStatusChangeListener;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.io.PrintWriter;

/**
 * This listener is a variation of StatusChangeListenerForWebClient. The difference here is that it assumes that vectors
 * and matrices are displayed in a web page as text, e.g. "Matrix (3x3 elements)", instead of as an array of text fields 
 */
public class StatusChangeListenerForWebClient2 implements ParameterStatusChangeListener {
    PrintWriter writer = null;
    private boolean debug = false;
	private String START_OF_SCRIPT = "<script language='JavaScript' type='text/JavaScript'>";
	private String END_OF_SCRIPT= "</script>";
    private String WHITE_BG = "#FFFFFF";
    private String GREEN_BG = "#B1F8A3";
    private String RED_BG = "#FFBFBF";
    private String YELLOW_BG = "#FFFF86";

    public StatusChangeListenerForWebClient2(PrintWriter writer, boolean debug) {
        this.writer = writer;
        this.debug = debug;
    }

    public void statusChanged(ParameterStatusChangeEvent event) {
        /* end event doesn't have to be handled */
        if (ParameterStatusChangeEvent.SOLVING_SUCCESS_EVENT.equals(event)) {
            System.out.println("My status listener : Solving succeed");
            writer.println(START_OF_SCRIPT + "window.parent.finishSolving('success');" + END_OF_SCRIPT);
            return;
        } else if (ParameterStatusChangeEvent.SOLVING_FAILURE_EVENT.equals(event)) {
            System.out.println("My status listener : Solving failed");
            writer.println(START_OF_SCRIPT + "window.parent.finishSolving('failure');" + END_OF_SCRIPT);
            return;
        }

        String paramName = event.getInterfaceParameterClient().getName();

        if (paramName.indexOf("'") != -1 || paramName.indexOf("\"") != -1) {
            System.out.println("Parameter Name contains ' or \". This could result in JavaScript error.");
        }

        String newColor = WHITE_BG;
        if (Parameter.VALUE_STATUS_CONSISTENT.equals(event.getNewStatus())) {
            newColor = WHITE_BG;
        } else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(event.getNewStatus())) {
            newColor = RED_BG;
        } else if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(event.getNewStatus())) {
            newColor = GREEN_BG;
        } else if (Parameter.VALUE_STATUS_STALE.equals(event.getNewStatus())) {
            newColor = YELLOW_BG;
        }

        // a vector is displayed on the page in a <span> as, e.g., "Vector (3 elements)"
        // a matrix is displayed on the page in a <span> as, e.g., "Matrix (3x3 elements)"
        newColor = newColor==WHITE_BG ? "" : newColor;
        String scriptStr = (START_OF_SCRIPT + "window.parent.document.getElementById('" + paramName + "').style.backgroundColor='" + newColor + "';" + END_OF_SCRIPT);
        writer.println(scriptStr);
        if (debug) { System.out.println(scriptStr); }
        writer.flush();
    }
}