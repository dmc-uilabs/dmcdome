// BuildFocusTracker.java
package mit.cadlab.dome3.gui.mode.build;

import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolModelBuildPanel;
import mit.cadlab.dome3.gui.guiutils.msg.FocusTracker;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.plugin.PluginModelBuilder;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Window;

public class BuildFocusTracker
{

	protected static FocusTracker tracker = new FocusTracker();

	public static void notifyInFocus(JComponent comp, Model model)
	{
		tracker.notifyInFocus(comp, model);
		synchronizeBuildModelMenus();
	}

	public static void notifyInFocus(JComponent comp, ModelComponent mComp)
	{
		tracker.notifyInFocus(comp, mComp);
		synchronizeBuildModelMenus();
	}

	private static String lastPluginName = "";
	private static String lastToolName = "";
	private static boolean lastDomeModelWasIModel = false;

	public static void synchronizeBuildModelMenus()
	{
		Model model = getCurrentModel();
		if (model instanceof PluginModelBuilder)
		{
			String pluginName = ((PluginModelBuilder) model).getPluginConfiguration().getTypeName();
			if (lastPluginName.equals(pluginName))
				return;
			lastPluginName = pluginName;
			PluginModelBuildPanel.menu.setText(pluginName);
			PluginModelBuildPanel.menu.repaint();
		}
		else if (model instanceof AnalysisTool)
		{
			String toolName = ((AnalysisToolBase)model).getToolConfiguration().getTypeName();
			if (lastToolName.equals(toolName))
				return;
			lastToolName = toolName;
			AnalysisToolModelBuildPanel.menu.setText(toolName);
			AnalysisToolModelBuildPanel.menu.repaint();
		}
		else if (model instanceof DomeModel)
		{
			if (((DomeModel) model).isIntegrationModel())
			{
				if (lastDomeModelWasIModel)
					return;
				DomeModelBuildPanel.addSubscriptionMenu();
				lastDomeModelWasIModel = true;
			}
			else
			{ // not an integration model
				if (!lastDomeModelWasIModel)
					return;
				DomeModelBuildPanel.removeSubscriptionMenu();
				lastDomeModelWasIModel = false;
			}
		}
	}

	public static void notifyFocusRemoved()
	{
		tracker.notifyFocusRemoved();
	}

	public static Model getCurrentModel()
	{
		return tracker.getCurrentModel();
	}

	public static JComponent getCurrentComponent()
	{
		return tracker.getCurrentComponent();
	}

	public static Window getCurrentWindow()
	{
		JComponent comp = getCurrentComponent();
		if (comp == null) return null;
        return SwingUtilities.windowForComponent(comp);
	}

	public abstract static class BuildFocusTrackerAction extends AbstractAction
	{

		public BuildFocusTrackerAction(String name)
		{
			super(name);
		}

		public JComponent getCurrentComponent()
		{
			return BuildFocusTracker.getCurrentComponent();
		}

	}

}
