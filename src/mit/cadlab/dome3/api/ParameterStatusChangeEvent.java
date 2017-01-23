package mit.cadlab.dome3.api;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;

import java.beans.PropertyChangeEvent;


/**
 * User: Sangmok Han
 * Date: 2004. 12. 16.
 */
public class ParameterStatusChangeEvent extends PropertyChangeEvent {

    /* Following two events are special ParameterStatusChangeEvent used to indicate users that solving thread has finished.
       One of the two events below is sent when solving thread exits. If changes are solved successfully within time limit,
       SOLVING_SUCCESS_EVENT is sent. Otherwise, SOLVING_FAILURE_EVENT is sent. */
    public final static ParameterStatusChangeEvent SOLVING_SUCCESS_EVENT = new ParameterStatusChangeEvent("RUNNING", "SUCCESS");
    public final static ParameterStatusChangeEvent SOLVING_FAILURE_EVENT = new ParameterStatusChangeEvent("RUNNING", "FAILURE");

    public ParameterStatusChangeEvent(InterfaceParameterClient param, String oldStatus, String newStatus) {
        super(param, "runtime_status", oldStatus, newStatus);
    }

    private ParameterStatusChangeEvent(String oldStatus, String newStatus) {
        super("ParameterStatusChangeEvent for SUCCESS or FAILURE", "runtime_status", oldStatus, newStatus);
    }

    public String getOldStatus() {
        return (String) getOldValue();
    }

    public String getNewStatus() {
        return (String) getNewValue();
    }

    public InterfaceParameterClient getInterfaceParameterClient() {
        try {
            return (InterfaceParameterClient) getSource();
        } catch (ClassCastException e) {
            System.err.println("getSource() is not an instance of InterfaceParameterClient. it happens when current StatusChangeEvent is to notify the end of solving: be it successful or not."); 
            return null;
        }
    }

    public String toString() {
        if (getSource() instanceof String) {
            /* this is the case when this ParameterStatusChangeEvent is constructed by tge private constructor. Its source is String of "ParameterStatusChangeEvent for SUCCESS or FAILURE" */
            return "ParameterStatusChangeEvent[ solving thread finished. state change = " + getOldValue() + " -> " + getNewValue() + "]";
        } else {
            return "ParameterStatusChangeEvent[ param = " + ((InterfaceParameterClient) getSource()).getName() + ", id = " + ((InterfaceParameterClient) getSource()).getId() + ", change = " + getOldValue() + " -> " + getNewValue() + "]";
        }
    }
}
