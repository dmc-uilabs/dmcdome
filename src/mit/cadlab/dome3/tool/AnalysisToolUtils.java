// AnalysisToolUtils.java

package mit.cadlab.dome3.tool;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolBuildMenus;
import mit.cadlab.dome3.network.CompoundId;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.dom4j.Element;

public class AnalysisToolUtils
{
    public static final String INTERFACE_RESULTS_FILE = "interface results";
    public static final String RESULTS = "RESULTS";

    // todo: use registry instead of this class

    private static List _toolNames = new ArrayList();

    private static HashMap _configCtrs = new HashMap();      // keyed by type name
    private static HashMap _configXmlCtrs = new HashMap();   // keyed by xml type
    private static HashMap _runtimeCtrs = new HashMap();     // keyed by xml type/dbConstants

    public static void registerToolModel(String toolConfigurationClassName,
                                           String toolRuntimeClassName)
    {
        String typeName = null;
        String xmlType = null;
        Constructor ctr = null;
        try {
            Class configClass = null;
            try {
                configClass = Class.forName(toolConfigurationClassName);
            } catch (ClassNotFoundException e) {
                throw e;
            }

            TypeInfo typeInfo = null;
            try {
                Field field = configClass.getField("TYPE_INFO");
                typeInfo = (TypeInfo) field.get(null);
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException(toolConfigurationClassName + " missing public static TYPE_INFO field.");
            } catch (ClassCastException e) {
                throw new ClassCastException(toolConfigurationClassName + " invalid TYPE_INFO field. Must be TypeInfo.");
            }
            typeName = typeInfo.getTypeName();
            xmlType = typeInfo.getXmlType();

            if (AnalysisToolUtils._toolNames.contains(typeName)) {
                System.err.println("duplicate tool registration ignored: " + typeName + "\t" + toolConfigurationClassName);
                return;
            }
            AnalysisToolUtils._toolNames.add(typeName);

            String[] validDataTypes = null;
            try {
                Field field = configClass.getField("VALID_DATA_TYPES");
                validDataTypes = (String[]) field.get(null);
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException(toolConfigurationClassName + " missing public static VALID_DATA_TYPES field.");
            } catch (ClassCastException e) {
                throw new ClassCastException(toolConfigurationClassName + " invalid VALID_DATA_TYPES field. Must be String[].");
            }

            DomeFileChooser.registerToolFileFilter(typeName, typeInfo.getXmlType());
            DomeFileChooser.registerAnalysisToolInterfaceResultsFileFilter(INTERFACE_RESULTS_FILE, RESULTS);

            if (! DomeClientApplication.DOME_SERVER_MODE)
	            AnalysisToolBuildMenus.registerDataTypesForTool(typeName, validDataTypes);

            try
            {
                ctr = configClass.getConstructor(new Class[]{AnalysisTool.class});
                AnalysisToolUtils._configCtrs.put(typeName, ctr);
            }
            catch (NoSuchMethodException ex)
            {
                throw new NoSuchMethodException(toolConfigurationClassName + "(AnalysisTool model) constructor not found.");
            }

            try
            {
                ctr = configClass.getConstructor(new Class[]{AnalysisTool.class, Element.class});
                AnalysisToolUtils._configXmlCtrs.put(xmlType, ctr);
            }
            catch (NoSuchMethodException ex)
            {
                throw new NoSuchMethodException(toolConfigurationClassName + "(AnalysisTool model, Element xmlElement) constructor not found.");
            }
        }
        catch (Exception ex)
        {
            System.err.println("Error registering tool " + toolConfigurationClassName + ":\n\t" + ex);
        }

        if (toolRuntimeClassName == null)
        {
            System.out.println("Warning: no runtime class registered for " + toolConfigurationClassName);
            return;
        }
        try
        {
            Class runtimeClass = null;
            try
            {
                runtimeClass = Class.forName(toolRuntimeClassName);
            }
            catch (ClassNotFoundException e)
            {
                throw e;
            }

            try
            {
                ctr = runtimeClass.getConstructor(new Class[]{CompoundId.class, Element.class});
                AnalysisToolUtils._runtimeCtrs.put(xmlType, ctr);
            }
            catch (NoSuchMethodException ex)
            {
                throw new NoSuchMethodException(toolRuntimeClassName + "(CompoundId id, Element xmlElement) constructor not found.");
            }
        }
        catch (Exception ex)
        {
            System.err.println("Error registering tool " + toolRuntimeClassName + ":\n\t" + ex);
        }
    }

    public static List getToolNames()
    {
        return Collections.unmodifiableList(AnalysisToolUtils._toolNames);
    }

    public static AnalysisToolConfiguration createToolConfiguration(String toolTypeName, AnalysisTool model)
    {
        Constructor ctr = (Constructor) AnalysisToolUtils._configCtrs.get(toolTypeName);
        if (ctr == null)
            throw new IllegalArgumentException("AnalysisToolUtils.createToolConfiguration - invalid ToolTypeName: " + toolTypeName);
        try {
            return (AnalysisToolConfiguration) ctr.newInstance(new Object[]{model});
        } catch (Exception e) {
            System.err.println("AnalysisToolUtils.createToolConfiguration error: " + toolTypeName + "\t" + model.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static AnalysisToolConfiguration createToolConfiguration(String toolXmlType, AnalysisTool model, ModelObjectFactory moFactory, Element xmlElement)
    {
        Constructor ctr = (Constructor) AnalysisToolUtils._configXmlCtrs.get(toolXmlType);
        if (ctr == null)
            throw new IllegalArgumentException("AnalysisToolUtils.createToolConfiguration - invalid toolXmlType: " + toolXmlType);
        try {
            return (AnalysisToolConfiguration) ctr.newInstance(new Object[]{model, moFactory, xmlElement});
        } catch (Exception e) {
            System.err.println("AnalysisToolUtils.createToolConfiguration error: " + toolXmlType + "\t" + model.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static ModelRuntime getAnalysisToolRuntime(String analysisToolDbType, CompoundId id, Element xmlElement)
    {
        Constructor ctr = (Constructor) AnalysisToolUtils._runtimeCtrs.get(analysisToolDbType);
        if (ctr == null)
            throw new IllegalArgumentException("AnalysisToolUtils.getToolRuntime - invalid toolDbType: " + analysisToolDbType);
        try
        {
            return (ModelRuntime) ctr.newInstance(new Object[]{id, xmlElement});
        }
        catch (Exception e)
        {
            System.err.println("AnalysisToolUtils.getToolRuntime error: " + analysisToolDbType + "\t" + id);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
