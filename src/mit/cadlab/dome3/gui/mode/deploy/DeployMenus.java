// DeployMenus.java
package mit.cadlab.dome3.gui.mode.deploy;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeMenus;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.help.DHelp;
import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.*;

public class DeployMenus extends ModeMenus
{

	public static final JMenu windowsMenu = makeWindowsMenu();
	private static boolean initialized = false;

	protected static JMenu makeWindowsMenu()
	{
		JMenu menu = MenuUtils.makeMenu("Windows");
		menu.add(MenuUtils.makeMenuItem("Deploy Windows"));
		return menu;
	}

	public static void initialize(int deployModeId)
	{
		if (initialized) {
			System.err.println("DeployMenus already initialized");
			return;
		}
		//MenuManager.addModeContext(DEPLOY_MODE, new JMenu[]{makeDeployMenu(),windowsMenu});
		MenuManager.addModeContext(DEPLOY_MODE, new JMenu[]{makeDeployMenu(), null});

		_Context[] deployContexts = {
			//new _Context(DEPLOY_DOMEMODEL,makeDeployDomeModelMenus()),
		};

		addContextMenusToMode(deployContexts, deployModeId);
		initialized = true;
	}

	private static JMenu makeDeployMenu()
	{
		JMenu m = MenuUtils.makeMenu("Deploy");
		DHelp.enableHelp(m, DHelp.DEPLOY_MODELS);
		m.add(MenuUtils.makeMenuItem(DeployMode.DeployModelAction));
        JMenuItem item = MenuUtils.makeMenuItem(DeployMode.DeployPlayspaceAction);
		m.add(item);
		m.add(MenuUtils.makeMenuItem(DeployMode.DeployProjectAction));
        m.add(MenuUtils.makeMenuItem(DeployMode.DeployCatalogAction));
		m.add(MenuUtils.makeMenuItem(DeployMode.DeployToolAction));
        m.add(MenuUtils.makeMenuItem(DeployMode.DeployProjectTemplateAction));
        m.add(MenuUtils.makeMenuItem(DeployMode.DeployModelTemplateAction));
        m.addSeparator();
		m.add(MenuUtils.makeMenuItem(DeployMode.closeAllAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(Modes.exitAction));
		return m;
	}

}
