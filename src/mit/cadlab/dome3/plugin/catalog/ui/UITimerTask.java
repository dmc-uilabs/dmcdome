package mit.cadlab.dome3.plugin.catalog.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 11.
 */
public class UITimerTask extends TimerTask {
    ModelEditor modelEditor;

    public UITimerTask(ModelEditor modelEditor) {
        this.modelEditor = modelEditor;
    }

    public void run() {
        if (! modelEditor.isVisible()) {
            return;
        }

        for (Iterator i = modelEditor.getDynamicComponentMap().entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            if (((DynamicComponent) entry.getValue()).isEnabled()) {
                ((DynamicComponent) entry.getValue()).update();
                // System.out.println("DynamicComponent " + entry.getKey() + " is updated");
            }
        }
    }
}
