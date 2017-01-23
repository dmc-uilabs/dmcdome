package mit.cadlab.dome3.plugin.vensim;

import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import org.dom4j.Element;

import java.util.List;
import java.util.Iterator;
import java.io.File;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 28.
 */
public class VensimModelRuntime extends PluginModelRuntime {
    protected VensimPlugin plg;

    public VensimModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
        loadNativeModel();
    }

    protected void executeNativePlugin(List affectedOutputParams) {
        if (!plg.isModelLoaded()) {
            plg.loadModel();
        }
        plg.execute(affectedOutputParams);
    }

    /**
     * Halt the native model.
     */
    public void stopModel() {
        plg.unloadModel();
    }

    public void deleteModel() {
        if (solver.isSolving()) {
            solver.stopSolving();
            waitingToDie = Boolean.TRUE;
            return;
        }
        plg.deleteModel();
        super.deleteModel();
    }

    protected void loadNativeModel() {
        String fileName = getMainModelFileName();

        if (fileName == null) {
            throw new UnsupportedOperationException("cannot start vensim model - no filename");
        }

        /* extract fileName part from the full path */
        if (fileName.indexOf("\\") != -1) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        } else if (fileName.indexOf("/") != -1) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }

        plg = new VensimPlugin(getWorkingDirectory() + File.separator + fileName, this);
        plg.createModel();

        /* register all parameters of VensimModelRuntime at VensimPlugin */
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) {
                    plg.addParameter(p);
                }
            }
        }

        Debug.trace(Debug.ALL, "has loaded vensim model");
    }

    /** returns a vensim variable name mapped to a given parameter. it is invoked in VensimPlugin.execute(). */
    public String getMappedVariableName(Parameter p) {
        return (String) getPluginMappingManager().getMappingObjectForParameter(p);
    }
}
