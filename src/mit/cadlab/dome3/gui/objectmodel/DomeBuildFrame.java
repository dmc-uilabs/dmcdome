// DomeBuildFrame.java
package mit.cadlab.dome3.gui.objectmodel;

import mit.cadlab.dome3.gui.guiutils.msg.Saveable;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.menu.WindowsMenu;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMenus;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginModelBuildPanel;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.swing.WindowTracker;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class DomeBuildFrame extends DomeFrame implements Saveable
{
	public static final Dimension DEFAULT_SIZE = new Dimension(675, 400);
	protected NameListener nameListener = null;
	protected DomeObject dObj = null;
	protected boolean frameClosed = false;

	public DomeBuildFrame(DomeGui gui)
	{
		super(gui);
		getWindowsMenu().addWindow(this);
		Object guiObj = gui.getGuiObject();
		if (guiObj instanceof DomeObject) {
			nameListener = new DomeObjectNameListener();
			((DomeObject) guiObj).addPropertyChangeListener(NameListener.NAME, nameListener);
			if (guiObj instanceof Model || guiObj instanceof ModelInterface || guiObj instanceof ToolInterface)
				this.setSize(DEFAULT_SIZE);
		}
	}

	public DomeBuildFrame(DomeGui gui, WindowTracker parent)
	{
		super(gui, parent);
		getWindowsMenu().addWindow(this);
		Object guiObj = gui.getGuiObject();
		if (guiObj instanceof DomeObject) {
			nameListener = new DomeObjectNameListener();
			((DomeObject) guiObj).addPropertyChangeListener(NameListener.NAME, nameListener);
			if (guiObj instanceof Model || guiObj instanceof ModelInterface || guiObj instanceof ToolInterface)
				this.setSize(DEFAULT_SIZE);
		}
	}

	protected WindowListener createWindowListener()
	{
		return new DomeObjectBuildFrameWindowListener();
	}

	protected WindowTracker createWindowTracker()
	{
		return BuildMode.getWindowTracker(getGuiObject());
	}

	protected Point getWindowLocation()
	{
		return BuildMode.getWindowLocation(getGuiObject());
	}

	protected void notifyNotInFocus()
	{
		MenuManager.setContext(ModeContexts.BUILD_MODE);
		BuildFocusTracker.notifyFocusRemoved();
	}

	protected class DomeObjectBuildFrameWindowListener extends DomeFrameWindowListener
	{
		public void windowClosed(WindowEvent e)
		{
			if (!frameClosed) frameClosed = true;
			getWindowsMenu().removeWindow(DomeBuildFrame.this);
			//give some time
			try {
				Thread.sleep(100);
			}
			catch (Exception ee) {
				ee.printStackTrace();
			}
			if (frameClosed) {
				notifyNotInFocus();
				BuildMode.getWindowTracker().removeChildWindow(DomeBuildFrame.this);
				//give focus to main menu bar
				MenuManager.getDomeMenuBar().grabFocus();
			}

		}

		public void windowClosing(WindowEvent e)
		{
			if (gui instanceof PluginModelBuildPanel) {
				if(!((PluginModelBuildPanel)gui).isCausalityDefined()){
							String msg = "The causality has not been defined\n" +
                                    "in this model";
							int button = TwoButton1Msg.showOption(((PluginModelBuildPanel)gui), "Option: Undefined causality",
                                    msg, "define it now","close model", new Dimension(230, 80));
							if (button == 1) return;
						}
			}
			gui.close();
			selfClose();
		}

		public void windowActivated(WindowEvent e)
		{
			gui.setMenuContext();

			if (frameClosed) {
				gui.close();
				selfClose();
			}
		}
	}

	protected WindowsMenu getWindowsMenu()
	{
		Model model = null;
		Object guiObj = gui.getGuiObject();
		if (guiObj instanceof ModelComponent)
		{
			model = ((ModelComponent) guiObj).getModel();
		}
		else if (guiObj instanceof DomeObject)
		{
			model = (guiObj instanceof ModelObject) ? getRootModel((ModelObject) guiObj) : (Model) guiObj;
		}
		if (model instanceof IntegrationProject)
        {
            IntegrationProjectBuilder iProject = (IntegrationProjectBuilder)model;
            if(iProject.getIsToolProjectBuilder())
                return BuildMenus.toolWindowsMenu;
            else
                return BuildMenus.iProjectWindowsMenu;
        }
        else if(model instanceof AnalysisTool)
			return BuildMenus.toolWindowsMenu;
		else if (model instanceof Model)
			return BuildMenus.modelWindowsMenu;
		return null;
	}

	protected Model getRootModel(ModelObject mObj)
	{
		Model m = mObj.getModel();
		while (m instanceof ModelObject) {
			m = ((ModelObject) m).getModel();
		}
		return m;
	}
	
	public void close()
	{
		/*	if (gui instanceof PluginModelBuildPanel) {
						PluginModelBuilder modelBuilder = (PluginModelBuilder) ((PluginModelBuildPanel) gui).getModel();
						if (modelBuilder.getShouldSave() && !modelBuilder.isSaved() && modelBuilder.getDependencyInfo().isEmpty()) {
							//pop up warning
							String msg = "Causality not defined for Plugin model:" + modelBuilder.getName();
							int button = TwoButton1Msg.showOption(null, "Warning: Causality not defined!", msg, "back to build",
									"continue anyway", new Dimension(230, 80));
							if (button == 1) return;
						}
			}*/
		if (dObj != null)
			dObj.removePropertyChangeListener(NameListener.NAME, nameListener);
		super.close();
	}

	public void save(boolean closeAfterSave)
	{
		if (gui instanceof Saveable)
			((Saveable) gui).save(closeAfterSave);
		else
			gui.close();
	}

	protected class DomeObjectNameListener extends NameListener
	{
		public void nameChanged(String newName)
		{
			setTitle(getGui().getTitle());
		}
	}
}
