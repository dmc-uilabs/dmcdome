package mit.cadlab.dome3.api.domewc;

import java.util.Map;

/**
 * User: Sangmok Han
 * Date: 2006. 9. 1.
 */
public abstract class AfterValueChangedTask {
    private String paramName;

    public AfterValueChangedTask(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

    public String toString() {
        return "[AfterValChgTask: paramName=" + paramName + "]";
    }

    abstract public void run(String changedParamName, Object newValue, Map paramMap);
}
