// ModelFactory.java
package mit.cadlab.dome3.gui.mode;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.optimization.OptimizationToolBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildPanel;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ModelFactory
{

	private static List modelTypes = new ArrayList();
	private static HashMap models = new HashMap();

	private ModelFactory()
	{
	}

	public static void registerModel(String modelClassName,
	                                 String modelBuildGuiClassName)
	{
		try {
			Class modelClass = Class.forName(modelClassName);
			Constructor modelCtr = modelClass.getConstructor(new Class[]{String.class});
			Class modelBuildGuiClass = Class.forName(modelBuildGuiClassName);
			Constructor modelBuildGuiCtr = modelBuildGuiClass.getConstructor
			        (new Class[]{modelClass});
			Model m = (Model) modelCtr.newInstance(new String[]{""});
			String modelType = m.getTypeName();
			if (modelTypes.contains(modelType))
				throw new UnsupportedOperationException("Duplicate model type registration: " + modelType);
			// note: check later for duplicate modelClassName!
			modelTypes.add(modelType);
			models.put(modelType, new ModelInfo(modelClassName, modelCtr, modelBuildGuiCtr));
		} catch (Exception ex) {
			handleException(ex);
		}
	}

	public static List getModelTypes()
	{
		return Collections.unmodifiableList(modelTypes);
	}

//  public static DomeObjectBuildFrame newModel(String modelType, String id) {
//    ModelInfo mInfo = (ModelInfo)models.get(modelType);
//    if (mInfo == null)
//      throw new UnsupportedOperationException("ModelFactory.newModel: unknown model type "+modelType);
//    try {
//      Object model = mInfo.modelConstructor.newInstance(new Object[]{id});
//      DomeObjectGui modelGui = (DomeObjectGui)mInfo.buildGuiConstructor.newInstance(new Object[]{model});
//      return new DomeObjectBuildFrame(modelGui);
//    } catch (Exception ex) {
//      handleException(ex);
//      return null;
//    }
//  }


	public static DomeBuildFrame newModel(String modelType, String id)
	{
        try
        {
            if (DomeModel.TYPE_INFO.getTypeName().equals(modelType))
            {
                Constructor modelConstructor = Registry.getConstructor(modelType, Registry.BASE_CLS);
                Object model = modelConstructor.newInstance(new Object[]{new Id(id)});
                Constructor buildGuiConstructor = Registry.getConstructor(getGuiClass(modelType), Registry.BUILD_GUI);
                DomeObjectGui modelGui = (DomeObjectGui) buildGuiConstructor.newInstance(new Object[]{model});
                return new DomeBuildFrame(modelGui);
            }
            else if (IntegrationProject.TYPE_INFO.getTypeName().equals(modelType))
            {
                IntegrationProjectBuilder ipbuilder = new IntegrationProjectBuilder(new Id(id));
                DomeObjectGui projectGui = new ProjectBuildPanel(ipbuilder);
                return new DomeBuildFrame(projectGui);
            }
            else if (QMOOConfiguration.TYPE_INFO.getTypeName().equals(modelType))
            {
                OptimizationToolBuild tool = new OptimizationToolBuild(id);
                DomeObjectGui toolGui = new OptimizationToolBuildPanel(tool);
                return new DomeBuildFrame(toolGui);
            }
            else
            { // Plugin model type
                PluginModelBuilder model = new PluginModelBuilder(id, modelType);
                DomeObjectGui modelGui = new PluginModelBuildPanel(model);
                return new DomeBuildFrame(modelGui);
            }

        }
        catch (Exception ex)
        {
            handleException(ex);
            return null;
        }
	}

	public static DomeBuildFrame newModel(Model model)
	{
		try {
			Constructor buildGuiConstructor = Registry.getConstructor(getGuiClass(model.getTypeName()),
			                                                          Registry.BUILD_GUI);
			DomeObjectGui modelGui = (DomeObjectGui) buildGuiConstructor.newInstance(new Object[]{model});
			return new DomeBuildFrame(modelGui);
		} catch (Exception ex) {
			handleException(ex);
			return null;
		}
	}

	// ModelFactory.newTool has been created to distinguish between the different types of tools since each tool will have
	// a different AnalysisToolModelBuildPanel.  The above class ModelFacotry.newModel does not consider this since all
	// plugin models use the same build gui class, PluginModelBuildPanel

	public static DomeBuildFrame newTool(Model model)
	{
		try
		{
			Constructor buildGuiConstructor = Registry.getConstructor(getToolGuiClass(((AnalysisToolBase)model).getToolConfiguration().getTypeName()),
			                                                                          Registry.BUILD_GUI);
			DomeObjectGui modelGui = (DomeObjectGui) buildGuiConstructor.newInstance(new Object[] {model});
			return new DomeBuildFrame(modelGui);
		}
		catch (Exception ex)
		{
			handleException(ex);
			return null;
		}
	}

	public static DomeBuildFrame duplicateModel(Model model, String id)
	{
		try {
			Constructor modelConstructor = Registry.getConstructor(model, Registry.BASE_CLS);
			Object modelCopy = modelConstructor.newInstance(new Object[]{new Id(id), model});
			Constructor buildGuiConstructor;
			buildGuiConstructor = Registry.getConstructor(getGuiClass(model.getTypeName()), Registry.BUILD_GUI);
			DomeObjectGui modelGui = (DomeObjectGui) buildGuiConstructor.newInstance(new Object[]{modelCopy});
			return new DomeBuildFrame(modelGui);
		} catch (Exception ex) {
			handleException(ex);
			return null;
		}
	}

	public static DomeBuildFrame saveAsModel(Model model)
	{
		try {
			Constructor buildGuiConstructor;
			buildGuiConstructor = Registry.getConstructor(getGuiClass(model.getTypeName()), Registry.BUILD_GUI);
			DomeObjectGui modelGui = (DomeObjectGui) buildGuiConstructor.newInstance(new Object[]{model});
			return new DomeBuildFrame(modelGui);
		} catch (Exception ex) {
			handleException(ex);
			return null;
		}
	}

	// load model..
	// run model...

	private static void handleException(Exception ex)
	{
		if (ex instanceof java.lang.reflect.InvocationTargetException) {
			System.err.println("ModelFactory error:");
			ex.printStackTrace();
		} else {
			System.err.println("ModelFactory: " + ex);
			ex.printStackTrace();
		}
	}

	private static class ModelInfo
	{
		public String modelClassName;
		public Constructor modelConstructor;
		public Constructor buildGuiConstructor;

		public ModelInfo(String modelClassName, Constructor modelConstructor,
		                 Constructor buildGuiConstructor)
		{
			this.modelClassName = modelClassName;
			this.modelConstructor = modelConstructor;
			this.buildGuiConstructor = buildGuiConstructor;
		}
	}

	private static String getGuiClass(String modelType)
	{
		if (DomeModel.TYPE_INFO.getTypeName().equals(modelType))
		{
			return "DomeModelBuildPanel";
		}
		else if (PluginModel.TYPE_INFO.getTypeName().equals(modelType))
		{
			return "PluginModelBuildPanel";
		}
		else
			return null;
	}

	private static String getToolGuiClass(String toolType)
	{
		if(QMOOConfiguration.TYPE_INFO.getTypeName().equals(toolType))
		{
			return "OptimizationToolBuildPanel";
		}
		else
			return null;
	}

}
