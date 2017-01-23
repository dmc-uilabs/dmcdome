package mit.cadlab.dome3.plugin.groovy;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyReal;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyEnumeration;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyMatrix;
import mit.cadlab.dome3.plugin.groovy.dataobject.GroovyDataObject;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 6.
 */
public class GroovyModelRuntime extends PluginModelRuntime {
    protected GroovyPlugin plg;

    public GroovyModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
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
            throw new UnsupportedOperationException("can not start groovy model - no filename");
        }

        plg = new GroovyPlugin(fileName, this);
        plg.createModel();

        /* create groovy objects that are mapped to dome object */
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                if (map != null) {
                    createCatalogDataObject(p);
                }
            }
        }

        Debug.trace(Debug.ALL, "has loaded groovy model");
    }

    /** returns a script variable name mapped to a given parameter. it is invoked in GroovyPlugin.execute(). GroovyDataObject is binded to the returned variable name */
    public String getMappedScriptVariableName(Parameter p) {
        return (String) getPluginMappingManager().getMappingObjectForParameter(p);
    }

    /**
     * create GroovyDataObject which has a reference to a given parameter
     * the reference is used to data exchange between a plugin data object and a java data object
     * when creating catalog data object, its reference is added to a vector called data in CatalogPlugin
     */
    protected GroovyDataObject createCatalogDataObject(Parameter p) {
        GroovyDataObject catDataObj = null;
        boolean isInput = getCausality(p).equals(CausalityStatus.INDEPENDENT);
        if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createReal(p);
            if (!isInput)
                ((GroovyReal) catDataObj).setIsResult(true);
        } else if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createMatrix(p);
            if (!isInput)
                ((GroovyMatrix) catDataObj).setIsResult(true);
        } else if (p.getCurrentType().equals(DomeEnumeration.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createEnumeration(p);
            if (!isInput)
                ((GroovyEnumeration) catDataObj).setIsResult(true);
        }
        return catDataObj;
    }
}
