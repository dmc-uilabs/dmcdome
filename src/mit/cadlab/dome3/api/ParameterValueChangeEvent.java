package mit.cadlab.dome3.api;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;

import java.beans.PropertyChangeEvent;
import java.util.List;


/**
 * User: Sangmok Han
 * Date: 2004. 12. 16.
 */
public class ParameterValueChangeEvent extends PropertyChangeEvent {
    public ParameterValueChangeEvent(InterfaceParameterClient param, List oldValues, List newValues) {
        super(param, "values", oldValues, newValues);
    }

    public List getOldValues() {
        return (List) getOldValue();
    }

    public List getNewValues() {
        return (List) getNewValue();
    }

    public InterfaceParameterClient getInterfaceParameterClient() {
        return (InterfaceParameterClient) getSource();
    }

    public String toString() {
        return "ParameterValueChangeEvent[ param = " + ((InterfaceParameterClient) getSource()).getName() + ", id = " + ((InterfaceParameterClient) getSource()).getId() + ", change = " + getOldValue() + " -> " + getNewValue() + "]";
    }
}
