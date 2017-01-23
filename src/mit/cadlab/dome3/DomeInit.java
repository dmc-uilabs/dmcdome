// DomeInit.java
package mit.cadlab.dome3;

import edu.iupui.rg.ucum.units.UnitTab;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.plugin.PluginUtils;
import mit.cadlab.dome3.tool.AnalysisToolUtils;
import org.apache.xmlrpc.XmlRpc;
import org.python.util.PythonInterpreter;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DomeInit {

    public static final String unitsFileLocation = "edu/iupui/rg/ucum/common-cs.units";

    private static boolean initialized = false;

    public static void main(String[] args) {
        initializeDOME();
        System.exit(0);
    }

    public static void initializeDOME() {
	    if (initialized) {
		    System.err.println("DomeInit.initializeDOME should not be called multiple times!");
		    return;
	    }

	    XmlRpc.setKeepAlive(true);

	    if (! (DomeClientApplication.appletMode || DomeClientApplication.DOME_API_MODE)){
        // force python libraries to be loaded
        // this avoids the hit when a relation runs for the first time
        PythonInterpreter interp = new PythonInterpreter();
        interp.exec("1");
	    }

        /*boolean isJDK1_3_1 = true;
        try { // need to find a 1.3.1 class...
        // java.awt.Cursor is available only in the 1.1.x JDK
        Class cls = Class.forName("java.awt.Cursor");
        } catch (Exception e)    {
        isJDK1_3_1 = false;
        }*/
        String objectRoot = "mit.cadlab.dome3.";
        String guiRoot = "mit.cadlab.dome3.gui.";

        String version = System.getProperty("java.version");
        System.out.println("found java version: " + version);
        if (version.startsWith("1.0") || version.startsWith("1.1") ||
                version.startsWith("1.2") || version.startsWith("1.3") ||
                version.startsWith("1.4.0")) {
            System.out.println("!!!WARNING: DOME must be run" +
                    "with JDK 1.4.1 or higher version VM!!!");
            System.exit(1);
        }

        if (! DomeClientApplication.DOME_SERVER_MODE) {
            try {
                //get system laf
              String laf = System.getProperty("swing.defaultlaf");
              if(laf == null) {
                laf = UIManager.getSystemLookAndFeelClassName();
              }
              UIManager.setLookAndFeel(laf);
                //some custom ones to play with
                //UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        // register units
        try {
            loadUnits();
        } catch (RuntimeException e) {
            System.err.println("skip loading units: " + e.getMessage());
        }

        //register python datatypes
        Registry.addPythonDataType("Real");
        Registry.addPythonDataType("Integer");
        Registry.addPythonDataType("Boolean");
        Registry.addPythonDataType("Enumerated");
        Registry.addPythonDataType("String");
        Registry.addPythonDataType("Matrix");
	    Registry.addPythonDataType("Preference");
        Registry.addPythonDataType("Vector");
        Registry.addPythonDataType("Text");
        Registry.addPythonDataType("List");

        // register data types
        String[] dataTypeInterfaces = {
            objectRoot + "objectmodel.dataobject.interfaces.DomeReal",
            objectRoot + "objectmodel.dataobject.interfaces.DomeInteger",
            objectRoot + "objectmodel.dataobject.interfaces.DomeBoolean",
            objectRoot + "objectmodel.dataobject.interfaces.DomeString",
            objectRoot + "objectmodel.dataobject.interfaces.DomeText",
            objectRoot + "objectmodel.dataobject.interfaces.DomeVector",
            objectRoot + "objectmodel.dataobject.interfaces.DomeMatrix",
            objectRoot + "objectmodel.dataobject.interfaces.DomePreference",
            objectRoot + "objectmodel.dataobject.interfaces.DomeEnumeration",
            objectRoot + "objectmodel.dataobject.interfaces.DomeFile",
            objectRoot + "objectmodel.dataobject.interfaces.DomeList",
            objectRoot + "objectmodel.dataobject.interfaces.IterationVariable",
        };
        for (int i = 0; i < dataTypeInterfaces.length; ++i) {
            Registry.registerDataObject(dataTypeInterfaces[i], Registry.INTERFACE_CLS);
        }

	    String[] dataTypeBases = {
            objectRoot + "objectmodel.dataobject.RealData",
            objectRoot + "objectmodel.dataobject.IntegerData",
            objectRoot + "objectmodel.dataobject.BooleanData",
            objectRoot + "objectmodel.dataobject.StringData",
            objectRoot + "objectmodel.dataobject.TextData",
            objectRoot + "objectmodel.dataobject.DomeVectorData",
            objectRoot + "objectmodel.dataobject.DomeMatrixData",
            objectRoot + "objectmodel.dataobject.DomePreferenceData",
            objectRoot + "objectmodel.dataobject.EnumerationData",
            objectRoot + "objectmodel.dataobject.FileData",
            objectRoot + "objectmodel.dataobject.DomeListData",
            objectRoot + "objectmodel.dataobject.RealIterationVariable",
        };
        for (int i = 0; i < dataTypeBases.length; ++i) {
            Registry.registerDataObject(dataTypeBases[i], Registry.BASE_CLS);
        }

	     // register svr interface version of DomeFile
	    String svrIfaceDomeFile = objectRoot + "objectmodel.dataobject.ServerInterfaceFileData";
	    Registry.registerDataObject(svrIfaceDomeFile, Registry.SERVER_INTERFACE_CLS);

        if (! DomeClientApplication.DOME_API_MODE && ! DomeClientApplication.DOME_SERVER_MODE) {

            // register data type guis
            String[] dataTypeBuildGuis = {
                guiRoot + "objectmodel.dataobject.build.RealBuildPanel",
                guiRoot + "objectmodel.dataobject.build.IntegerBuildPanel",
                guiRoot + "objectmodel.dataobject.build.BooleanBuildPanel",
                guiRoot + "objectmodel.dataobject.build.StringBuildPanel",
                guiRoot + "objectmodel.dataobject.build.TextBuildPanel",
                guiRoot + "objectmodel.dataobject.build.DomeVectorBuildPanel",
                guiRoot + "objectmodel.dataobject.build.DomeMatrixBuildPanel",
                guiRoot + "objectmodel.dataobject.build.DomePreferenceBuildPanel",
                guiRoot + "objectmodel.dataobject.build.EnumerationBuildPanel",
                guiRoot + "objectmodel.dataobject.build.FileBuildPanel",
                guiRoot + "objectmodel.dataobject.build.DomeListBuildPanel",
                guiRoot + "objectmodel.dataobject.build.IterationVariableBuildPanel",
            };

            for (int i = 0; i < dataTypeBuildGuis.length; ++i) {
                Registry.registerDataObject(dataTypeBuildGuis[i], Registry.BUILD_GUI);
            }

            String[] dataTypeRunGuis = {
                guiRoot + "objectmodel.dataobject.run.RealRunPanel",
                guiRoot + "objectmodel.dataobject.run.IntegerRunPanel",
                guiRoot + "objectmodel.dataobject.run.BooleanRunPanel",
                guiRoot + "objectmodel.dataobject.run.StringRunPanel",
                guiRoot + "objectmodel.dataobject.run.TextRunPanel",
                guiRoot + "objectmodel.dataobject.run.DomeVectorRunPanel",
                guiRoot + "objectmodel.dataobject.run.DomeMatrixRunPanel",
                guiRoot + "objectmodel.dataobject.run.DomePreferenceRunPanel",
                guiRoot + "objectmodel.dataobject.run.EnumerationRunPanel",
                guiRoot + "objectmodel.dataobject.run.FileRunPanel",
                guiRoot + "objectmodel.dataobject.run.IterationVariableRunPanel",
            };
            for (int i = 0; i < dataTypeRunGuis.length; ++i) {
                Registry.registerDataObject(dataTypeRunGuis[i], Registry.RUN_GUI);
            }

        }

        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.parameter.Parameter",
                Registry.INTERFACE_CLS);
        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.parameter.ConcreteParameter",
                Registry.BASE_CLS);
        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.parameter.ModelParameterRuntime",
                Registry.SERVER_CLS);
        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.parameter.InterfaceParameterRuntime",
                Registry.SERVER_INTERFACE_CLS);
        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.parameter.InterfaceParameterClient",
                Registry.CLIENT_CLS);

        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.context.Context",
                Registry.INTERFACE_CLS);
        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.context.DefaultContextBuilder",
                Registry.BASE_CLS);

	    Registry.registerRelation(objectRoot + "objectmodel.modelobject.relation.procedural.ProceduralRelation",
                Registry.INTERFACE_CLS);

        Registry.registerRelation(objectRoot + "objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation",
                Registry.BASE_CLS);

        Registry.registerRelation(objectRoot + "objectmodel.modelobject.relation.equal.EqualRelation",
                Registry.INTERFACE_CLS);
        Registry.registerRelation(objectRoot + "objectmodel.modelobject.relation.equal.ConcreteEqualRelation",
                Registry.BASE_CLS);

        //Qing add for iteration relation
        Registry.registerRelation(objectRoot + "objectmodel.modelobject.relation.iteration.IterationRelation",
                Registry.INTERFACE_CLS);
        Registry.registerRelation(objectRoot + "objectmodel.modelobject.relation.iteration.ConditionIterationRelation",
                Registry.BASE_CLS);

	    Registry.registerModelObject(objectRoot + "objectmodel.modelobject.subscription.Subscription",
                Registry.INTERFACE_CLS);
        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.subscription.DefaultSubscription",
                Registry.BASE_CLS);

        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.visualization.Visualization",
                Registry.INTERFACE_CLS);
        Registry.registerModelObject(objectRoot + "objectmodel.modelobject.visualization.ConcreteVisualization",
                Registry.BASE_CLS);

	    // register types of models (before modes!)
        Registry.registerModel(objectRoot + "objectmodel.model.dome.DomeModel",
                Registry.INTERFACE_CLS);
        Registry.registerModel(objectRoot + "objectmodel.model.dome.DomeModelBuilder",
                Registry.BASE_CLS);

        if (! DomeClientApplication.DOME_API_MODE && ! DomeClientApplication.DOME_SERVER_MODE) {
            Registry.registerModel(guiRoot + "objectmodel.model.dome.DomeModelBuildPanel",
                    Registry.BUILD_GUI);
        }

        Registry.registerModel(objectRoot + "plugin.PluginModel",
                Registry.INTERFACE_CLS);

        if (! DomeClientApplication.DOME_API_MODE && ! DomeClientApplication.DOME_SERVER_MODE) {
            Registry.registerModel(guiRoot + "objectmodel.model.plugin.PluginModelBuildPanel",
                    Registry.BUILD_GUI);
        }

	    // registering tool classes

        Registry.registerModel(objectRoot + "objectmodel.model.tool.AnalysisTool",
                Registry.INTERFACE_CLS);

        Registry.registerModel(objectRoot + "objectmodel.model.tool.optimization.build.OptimizationToolBuild",
                Registry.BASE_CLS);


        if (! DomeClientApplication.DOME_API_MODE && ! DomeClientApplication.DOME_SERVER_MODE) {
            // tool build panels have to be registered independently since they are unique
            Registry.registerModel(guiRoot + "objectmodel.model.tool.build.optimization.OptimizationToolBuildPanel",
                    Registry.BUILD_GUI);
        }

	    // register model interfaces
        Registry.registerModelInterface(objectRoot + "objectmodel.modelinterface.dome.DomeModelInterface",
                Registry.INTERFACE_CLS);
        Registry.registerModelInterface(objectRoot + "objectmodel.modelinterface.ModelInterfaceBuilder",
                Registry.BASE_CLS);
        if (! DomeClientApplication.DOME_API_MODE && ! DomeClientApplication.DOME_SERVER_MODE) {
            Registry.registerModelInterface(guiRoot + "objectmodel.modelinterface.build.ModelInterfaceBuildPanel",
                    Registry.BUILD_GUI);
        }

        // register tool interfaces
        Registry.registerToolInterface(objectRoot + "objectmodel.toolinterface.ToolInterface",
                Registry.INTERFACE_CLS);
        Registry.registerToolInterface(objectRoot + "objectmodel.toolinterface.AnalysisToolInterfaceBase",
                Registry.BASE_CLS);
        if (! DomeClientApplication.DOME_API_MODE && ! DomeClientApplication.DOME_SERVER_MODE) {
            Registry.registerToolInterface(guiRoot + "objectmodel.toolinterface.build.optimisation.OptimisationInterfaceBuildPanel",
                    Registry.BUILD_GUI);
        }
        // create Dome Model File Chooser
        DomeFileChooser.registerPluginFileFilter(DomeModel.TYPE_INFO.getTypeName(), "DOME");

	    // register plugin model classes
        registerPluginModels();

	    // register tool model classes
        registerToolModels();

        if (! DomeClientApplication.DOME_API_MODE && ! DomeClientApplication.DOME_SERVER_MODE) {

            // register modes and add menu components
            Modes.exitAction = DomeClientApplication.exitAction;
            Modes.registerMode("mit.cadlab.dome3.gui.mode.build.BuildMode",
                    "mit.cadlab.dome3.gui.mode.build.BuildMenus");
            Modes.registerMode("mit.cadlab.dome3.gui.mode.deploy.DeployMode",
                    "mit.cadlab.dome3.gui.mode.deploy.DeployMenus");
            Modes.registerMode("mit.cadlab.dome3.gui.mode.run.RunMode",
                    "mit.cadlab.dome3.gui.mode.run.RunMenus");
            Modes.registerMode("mit.cadlab.dome3.gui.mode.server.ServerMode",
                    "mit.cadlab.dome3.gui.mode.server.ServerMenus");

        }

	    initialized = true;
    }

    public static void loadUnits() {
        try {
            InputStream is = new ByteArrayInputStream(edu.iupui.rg.ucum.CommonUnit.DATA.getBytes());
            UnitTab.read(is);
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading unit data");
        }


//	    if (DomeClientApplication.appletMode) {
//		    InputStream is = DomeInit.class.getResourceAsStream("/"+unitsFileLocation);
//		    if (is == null) {
//			    throw new RuntimeException("Remote file not found: " + unitsFileLocation);
//		    }
//		    try {
//			    UnitTab.read(is);
//		    }
//		    catch (IOException e) {
//			    throw new RuntimeException("Error reading remote file: " + unitsFileLocation);
//		    }
//	    } else {
//            URL unitsURL = ClassLoader.getSystemClassLoader().getResource(unitsFileLocation);
//		    if (unitsURL == null) {
//			    throw new RuntimeException("File not found: " + unitsFileLocation);
//		    }
//		    try {
//			    UnitTab.read(unitsURL.toString());
//		    } catch (IOException e) {
//			    throw new RuntimeException("Error reading file: " + unitsFileLocation);
//		    }
//	    }
    }

    public static void registerPluginModels() {
        String pluginRoot = "mit.cadlab.dome3.plugin.";
        // IMPORTANT: the plugin element in the two arrays must have the same order
        String[] pluginConfigurations = {
	        pluginRoot + "abaqus.AbaqusConfiguration",
	        pluginRoot + "adams.AdamsConfiguration",
	        pluginRoot + "catia.CATIAConfiguration",
            pluginRoot + "excel.ExcelConfiguration",
            pluginRoot + "ideas10.Ideas10Configuration",
            //pluginRoot + "ideas.IdeasConfiguration",
            pluginRoot + "mathematica.MathematicaConfiguration",
            pluginRoot + "matlab.MatlabConfiguration",
            pluginRoot + "mathcad.MathcadConfiguration",
            //pluginRoot + "maxwell.MaxwellConfiguration",
            pluginRoot + "nastran.NastranConfiguration",
            pluginRoot + "batch.nameValue.NameValueConfiguration",
            pluginRoot + "Solidworks.SolidworksConfiguration",
            pluginRoot + "ug.UgConfiguration",
            pluginRoot + "catalog.CatalogConfiguration",
            pluginRoot + "groovy.GroovyConfiguration",
            pluginRoot + "vensim.VensimConfiguration",
            pluginRoot + "extendsim.ExtendSimConfiguration",
            pluginRoot + "javajar.JarConfiguration",
        };
        String[] pluginRuntimes = {
	        pluginRoot + "abaqus.AbaqusModelRuntime",
	        pluginRoot + "adams.AdamsModelRuntime",
	        pluginRoot + "catia.CATIAModelRuntime",
            pluginRoot + "excel.ExcelModelRuntimeNew",
//            pluginRoot + "excel.ExcelModelRuntime",
            pluginRoot + "ideas10.Ideas10ModelRuntime",
            //pluginRoot + "ideas.IdeasModelRuntime",
            pluginRoot + "mathematica.MathematicaModelRuntimeNew",
            //pluginRoot + "mathematica.MathematicaModelRuntime",
            pluginRoot + "matlab.MatlabModelRuntimeNew",
            pluginRoot + "mathcad.MathcadModelRuntimeNew",
            //pluginRoot + "matlab.MatlabModelRuntime",
            //pluginRoot + "maxwell.MaxwellModelRuntime",
            pluginRoot + "nastran.NastranModelRuntime",
            pluginRoot + "batch.nameValue.NameValueModelRuntime",
            pluginRoot + "Solidworks.SolidworksModelRuntimeNew",
            //pluginRoot + "Solidworks.SolidworksModelRuntime",
            pluginRoot + "ug.UgModelRuntimeNew",
            //pluginRoot + "ug.UgModelRuntime",
            pluginRoot + "catalog.CatalogModelRuntime",
            pluginRoot + "groovy.GroovyModelRuntime",
            pluginRoot + "vensim.VensimModelRuntime",
            pluginRoot + "extendsim.ExtendSimModelRuntime",
            pluginRoot + "javajar.JarModelRuntime",
        };
        if (pluginConfigurations.length != pluginRuntimes.length) {
            System.err.println("registerPluginModels - no plugins registered due to unequal number of array elements");
            return;
        }
        for (int i = 0; i < pluginConfigurations.length; ++i) {
            PluginUtils.registerPluginModel(pluginConfigurations[i], pluginRuntimes[i]);
        }
    }

    public static void registerToolModels() {
        String toolRoot = "mit.cadlab.dome3.tool.";
        String[] toolConfigurations = {
            toolRoot + "optimization.qmoo.QMOOConfiguration"
        };
        String[] toolRuntimes = {
            "mit.cadlab.dome3.objectmodel.model.tool.optimization.run.OptimizationToolRuntime"
        };
        if (toolConfigurations.length != toolRuntimes.length) {
            System.err.println("registerToolModels - no tools registered due to unequal number of array elements");
            return;
        }
        for (int i = 0; i < toolConfigurations.length; ++i) {
            AnalysisToolUtils.registerToolModel(toolConfigurations[i], toolRuntimes[i]);
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

}
