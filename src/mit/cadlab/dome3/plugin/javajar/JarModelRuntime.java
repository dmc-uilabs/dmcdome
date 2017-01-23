package mit.cadlab.dome3.plugin.javajar;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.CommonAuxFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/3/11
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class JarModelRuntime extends PluginModelRuntime{
    private JarPlugin plugin;

    public JarModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
        loadNativeModel();
    }
    protected void executeNativePlugin(List affectedOutputParams) {
        if (!plugin.isModelLoaded()) {
            plugin.loadModel();
        }
        plugin.execute(affectedOutputParams);
    }
    /**
     * Halt the native model.
     */
    public void stopModel() {
        plugin.unloadModel();
    }

    public void deleteModel() {
        if (solver.isSolving()) {
            solver.stopSolving();
            waitingToDie = Boolean.TRUE;
            return;
        }
        plugin.deleteModel();
        super.deleteModel();
    }

    @SuppressWarnings("deprecation")
	protected void loadNativeModel() {
        try {
            // get configuration parameters
            File jarFile = new File(getMainModelFileName());
           
            StringData className = (StringData) pluginConfiguration.getSetupParameter(JarConfiguration.CLASS_TO_INSTANTIATE).getCurrentDataObject();
            
            //String jarFilePath = this.workingDirectory.toString() + System.getProperty("file.separator") + jarFile.getName();
            //plugin = new JarPlugin(jarFilePath, className.getValue());

            ArrayList<CommonAuxFile> afiles = this.getAuxFiles();
            for (CommonAuxFile oaf : afiles) {
            	String nm = oaf.getFile().getName();
            	if (nm.substring(nm.length()-3).equals("jar")) {
            		//plugin.addURL(new File(this.workingDirectory.toString() + System.getProperty("file.separator") + oaf.getFile().getName()).toURI().toURL());
            		JarPlugin.SaddURL(new File(this.modelDirectory.toString() + System.getProperty("file.separator") + oaf.getFile().getName()).toURI().toURL());
            	}
            }
            
            String jarFilePath = this.modelDirectory.toString() + System.getProperty("file.separator") + jarFile.getName();
            plugin = new JarPlugin(jarFilePath, className.getValue());
            plugin.createModel();

            // create map of dome object to corresponding java object
            Iterator it = getModelObjects().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof Parameter) {
                    Parameter p = (Parameter) o;
                    Object map = getPluginMappingManager().getMappingObjectForParameter(p);
                    if (map != null) {
                        plugin.createLinkedObject(p, (String) map, getCausality(p).equals(CausalityStatus.INDEPENDENT));

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
