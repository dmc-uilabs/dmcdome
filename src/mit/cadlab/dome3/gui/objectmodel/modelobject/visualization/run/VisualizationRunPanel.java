package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.run;

import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.Visualization;
import mit.cadlab.dome3.objectmodel.modelobject.visualization.ConcreteVisualization;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DomeVectorRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationBuildPanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.plugin.PluginModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 20, 2003
 * Time: 3:39:22 PM
 * To change this template use Options | File Templates.
 */
public class VisualizationRunPanel extends VisualizationBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("VisualizationRunPanel");
	public static final String XML_TAG = "visualizationrunpanel";


	/*
	 * Constructors
	 */
	public VisualizationRunPanel(Visualization vis)
	{
		super(vis);
		//setDataModel_GUI(v);
		convertToNotEditable();
	}



	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("Visualization Run Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		//f.getContentPane().add(new VisualizationRunPanel(), BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
	}

	private void dispose()
	{

		SwingUtilities.windowForComponent(this).dispose();
	}

	public void setMenuContext()
	{
		/**if (((DomeModel) dataModel.getModel()).getIntegrationProject() != null) {
			//set project menu
			MenuManager.setContext(ModeContexts.BUILD_PROJECT_VISUALIZATION);
		} else if (dataModel.getModel() instanceof PluginModel)
			MenuManager.setContext(ModeContexts.BUILD_PLUGIN_VISUALIZATION);
		else
			MenuManager.setContext(ModeContexts.BUILD_VISUALIZATION);
        **/
		MenuManager.setContext(ModeContexts.RUN_VISUALIZATION);
		RunFocusTracker.notifyInFocus(this);
		this.refreshViewSetMenu();
	}

	public static abstract class RunFocusTrackerAction extends AbstractAction
	{

		public RunFocusTrackerAction(String name)
		{
			super(name);
		}

		protected final VisualizationRunPanel getVisualizationRunPanel(ActionEvent e)
		{
			if (e != null) {
				Object o = e.getSource();
				if (o instanceof VisualizationRunPanel) {
					return (VisualizationRunPanel) o;
				}
			}
			JComponent comp = RunFocusTracker.getCurrentComponent();
			if (comp instanceof VisualizationRunPanel)
				return (VisualizationRunPanel) comp;
			/**/System.err.println("No current VisualizationRunPanel");
			throw new NullPointerException("No current VisualizationRunPanel");
		}
	}

	public static final AbstractAction saveRunChartAction = new VisualizationRunPanel.RunFocusTrackerAction("Save...")
	{
		public void actionPerformed(ActionEvent e)
		{
			try {
				getVisualizationRunPanel(e).valuePanel.doSaveAs();
			} catch (IOException ee) {
				System.err.println("VisualizationRunPanel.doSaveAs: i/o exception = " + ee.getMessage());
			}
		}
	};

	public static final AbstractAction printRunChartAction = new VisualizationRunPanel.RunFocusTrackerAction("Print...")
	{
		public void actionPerformed(ActionEvent e)
		{
			getVisualizationRunPanel(e).valuePanel.createChartPrintJob();
		}
	};


}
