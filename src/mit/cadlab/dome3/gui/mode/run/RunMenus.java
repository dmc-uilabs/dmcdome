// RunMenus.java
package mit.cadlab.dome3.gui.mode.run;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.menu.RunWindowsMenu;
import mit.cadlab.dome3.gui.mode.ModeMenus;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.gui.objectmodel.model.tool.run.AnalysisToolRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.ModelInterfaceTreePanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.run.VisualizationRunMenus;
import mit.cadlab.dome3.gui.objectmodel.project.run.ProjectRunPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.AnalysisToolInterfaceTreePanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimisation.OptimizationInterfaceResultsPanel;
import mit.cadlab.dome3.gui.playspace.run.PlayspaceRunPanel;
import mit.cadlab.dome3.help.DHelp;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.*;

public class RunMenus extends ModeMenus
{

	public static final RunWindowsMenu browser_windowsMenu = makeBrowserWindowsMenu();
//	public static final WindowsMenu domeObj_windowsMenu = makeDomeObjWindowsMenu();
    public static final JMenuItem addBookMarkMenu = MenuUtils.makeMenuItem(RunMode.addBookmarkAction);
    public static final JMenuItem BookMarkServerMenu = MenuUtils.makeMenuItem(RunMode.BookMarkServerAction);
    public static JMenu BookMarksMenu = MenuUtils.makeMenu("Bookmarks");
    public static final JMenuItem organizeBookMarkMenu = MenuUtils.makeMenuItem(RunMode.organizeBookmarkAction);


	public static final JMenu bookmarksMenu = makeBookmarksMenu();
	private static boolean initialized = false;

	public static final JMenuItem BuildMenu = MenuUtils.makeMenuItem(RunMode.viewBuildAction);
	public static final JMenuItem InterfaceCausalityMenu = MenuUtils.makeMenuItem(RunMode.viewInterfaceCausalityAction);
	public static final JMenuItem SystemCausalityMenu = MenuUtils.makeMenuItem(RunMode.viewSystemCausalityAction);

	public static final JMenuItem BuildMenu2 = MenuUtils.makeMenuItem(RunMode.viewBuildAction);
	public static final JMenuItem InterfaceCausalityMenu2 = MenuUtils.makeMenuItem(RunMode.viewInterfaceCausalityAction);
	public static final JMenuItem SystemCausalityMenu2 = MenuUtils.makeMenuItem(RunMode.viewSystemCausalityAction);

	public static final JMenuItem pBuildMenu = MenuUtils.makeMenuItem(PlayspaceRunPanel.viewBuildAction);
	public static final JMenuItem pInterfaceCausalityMenu = MenuUtils.makeMenuItem(PlayspaceRunPanel.viewInterfaceCausalityAction);
	public static final JMenuItem pSystemCausalityMenu = MenuUtils.makeMenuItem(PlayspaceRunPanel.viewSystemCausalityAction);

	public static final JMenuItem prBuildMenu = MenuUtils.makeMenuItem(ProjectRunPanel.viewBuildAction);
	public static final JMenuItem prInterfaceCausalityMenu = MenuUtils.makeMenuItem(ProjectRunPanel.viewInterfaceCausalityAction);
	public static final JMenuItem prSystemCausalityMenu = MenuUtils.makeMenuItem(ProjectRunPanel.viewSystemCausalityAction);

    public static final JMenuItem atBuildMenu = MenuUtils.makeMenuItem(AnalysisToolRunPanel.viewBuildAction);
    public static final JMenuItem atInterfaceCausalityMenu = MenuUtils.makeMenuItem(AnalysisToolRunPanel.viewInterfaceCausalityAction);
    public static final JMenuItem viewResultsMenuItem = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.viewResultsAction);
    public static final JMenuItem viewDesignSpaceMenuItem = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.viewDesignSpaceAction);


	protected static RunWindowsMenu makeBrowserWindowsMenu()
	{
		RunWindowsMenu menu = new RunWindowsMenu("Browser windows");
		return menu;
	}

	/*protected static WindowsMenu makeDomeObjWindowsMenu()
	{
		WindowsMenu menu = new WindowsMenu();
		menu.setText("Dome model windows");
		return menu;
	}*/

	protected static JMenu makeBookmarksMenu()
	{
		JMenu menu = MenuUtils.makeMenu("Bookmarks");
        menu.add(addBookMarkMenu);
        addBookMarkMenu.setEnabled(false);
        menu.add(BookMarkServerMenu);
        //BookMarkServerMenu.setEnabled(false);
        menu.add(organizeBookMarkMenu);
        menu.addSeparator();
        menu.add(BookMarksMenu);
		return menu;
	}

	protected static JMenu makeWindowMenu()
	{
		JMenu menu = MenuUtils.makeMenu("Windows");
		menu.add(browser_windowsMenu);
		//	menu.addSeparator();
		//	menu.add(domeObj_windowsMenu);
		return menu;
	}

	public static void initialize(int runModeId)
	{
		if (initialized) {
			System.err.println("RunMenus already initialized");
			return;
		}
		MenuManager.addModeContext(RUN_MODE, new JMenu[]{makeRunMenu(), bookmarksMenu, makeWindowMenu()});

		_Context[] runContexts = {
			new _Context(RUN_BROWSER, makeRunBroswerMenus()),
			new _Context(RUN_BROWSER_MODEL_VIEW, makeRunBroswerModelViewMenus()),
			new _Context(RUN_BROWSER_PLAYSPACE_VIEW, makeRunBroswerPlayspaceViewMenus()),
			new _Context(RUN_DOMEMODEL_INTERFACE, makeRunInterfaceMenus()),
			new _Context(RUN_DOMEMODEL_INTERFACE_CAUSALVIEW, makeRunInterfaceMenus()),
			new _Context(RUN_DOMEMODEL_INTERFACE_BUILDVIEW, makeRunInterfaceMenus()),
			new _Context(RUN_DOMEMODEL_INTERFACE_MODELVIEW, makeRunInterfaceMenus()),
			new _Context(RUN_PLAYSPACE, makeRunPlayspaceMenus()),
			new _Context(RUN_PROJECT, makeRunProjectMenus()),
            new _Context(RUN_ANALYSIS_TOOL, makeRunAnalysisToolMenus()),
			new _Context(RUN_VISUALIZATION, makeRunVisualizationMenus()),
            new _Context(RUN_ANALYSIS_TOOL_INTERFACE, makeRunAnalysisToolInterfaceMenus()),
            new _Context(RUN_ANALYSIS_TOOL_INTERFACE_RESULTS_VIEW, makeRunAnalysisToolInterfaceResultsViewMenus())
		};

		addContextMenusToMode(runContexts, runModeId);
		initialized = true;
	}

	public static ImageIcon icon = DomeIcons.getIcon(DomeIcons.CHECKED);
	public static ImageIcon icon2 = DomeIcons.getIcon(DomeIcons.UNCHECKED);

	private static JMenu[] makeRunBroswerMenus()
	{
		JMenu BrowserMenu = MenuUtils.makeBoldMenu("Browser");
		JMenuItem closeMenu = MenuUtils.makeMenuItem(RunMode.closeBroswerAction);

		BrowserMenu.addSeparator();
		BrowserMenu.add(closeMenu);

		return new JMenu[]{BrowserMenu};
	}

	private static JMenu[] makeRunBroswerModelViewMenus()
	{
		JMenu BrowserMenu = MenuUtils.makeBoldMenu("Browser");
		JMenuItem openMenu = MenuUtils.makeMenuItem(RunMode.openInBroswerAction);
		JMenuItem closeMenu = MenuUtils.makeMenuItem(RunMode.closeBroswerAction);

		BrowserMenu.add(openMenu);
		BrowserMenu.addSeparator();
		BrowserMenu.add(closeMenu);

		JMenu ViewMenu = MenuUtils.makeBoldMenu("View");
		BuildMenu.setIcon(icon2);
		InterfaceCausalityMenu.setIcon(icon2);
		SystemCausalityMenu.setIcon(icon2);

		ViewMenu.add(BuildMenu);
		ViewMenu.add(InterfaceCausalityMenu);
		ViewMenu.add(SystemCausalityMenu);
		return new JMenu[]{BrowserMenu, ViewMenu};
	}

	private static JMenu[] makeRunBroswerPlayspaceViewMenus()
	{
		JMenu BrowserMenu = MenuUtils.makeBoldMenu("Browser");
		JMenuItem openMenu = MenuUtils.makeMenuItem(RunMode.openInBroswerAction);
		JMenuItem open_in_playspace_Menu = MenuUtils.makeMenuItem(RunMode.openInNewPlayspaceAction);
        open_in_playspace_Menu.setEnabled(false);
        JMenuItem closeMenu = MenuUtils.makeMenuItem(RunMode.closeBroswerAction);

		BrowserMenu.add(openMenu);
		BrowserMenu.add(open_in_playspace_Menu);
		BrowserMenu.addSeparator();
		BrowserMenu.add(closeMenu);

		JMenu ViewMenu = MenuUtils.makeBoldMenu("View");
		BuildMenu2.setIcon(icon2);
		InterfaceCausalityMenu2.setIcon(icon2);
		SystemCausalityMenu2.setIcon(icon2);

		ViewMenu.add(BuildMenu2);
		ViewMenu.add(InterfaceCausalityMenu2);
		ViewMenu.add(SystemCausalityMenu2);
		return new JMenu[]{BrowserMenu, ViewMenu};
	}

	//qing add for open playspace in run broswer
	private static JMenu[] makeRunPlayspaceMenus()
	{
		JMenu interfaceMenu = MenuUtils.makeBoldMenu("Playspace");
		JMenuItem openMenu = MenuUtils.makeMenuItem(PlayspaceRunPanel.openAction);
		JMenuItem submitMenu = MenuUtils.makeMenuItem(PlayspaceRunPanel.submitAction);
		JMenuItem closeMenu = MenuUtils.makeMenuItem(PlayspaceRunPanel.closeAction);

		interfaceMenu.add(openMenu);
		interfaceMenu.add(submitMenu);

		interfaceMenu.addSeparator();

		interfaceMenu.add(closeMenu);

		JMenu ViewMenu = MenuUtils.makeBoldMenu("View");
		pBuildMenu.setIcon(icon2);
		pInterfaceCausalityMenu.setIcon(icon2);
		pSystemCausalityMenu.setIcon(icon2);

		ViewMenu.add(pBuildMenu);
		ViewMenu.add(pInterfaceCausalityMenu);
		ViewMenu.add(pSystemCausalityMenu);

		return new JMenu[]{interfaceMenu, ViewMenu};
	}

	private static JMenu[] makeRunProjectMenus()
	{
		JMenu interfaceMenu = MenuUtils.makeBoldMenu("Project");
		JMenuItem openMenu = MenuUtils.makeMenuItem(ProjectRunPanel.openAction);
		JMenuItem submitMenu = MenuUtils.makeMenuItem(ProjectRunPanel.submitAction);
		JMenuItem closeMenu = MenuUtils.makeMenuItem(ProjectRunPanel.closeAction);

		interfaceMenu.add(openMenu);
		interfaceMenu.add(submitMenu);

		interfaceMenu.addSeparator();

		interfaceMenu.add(closeMenu);

		JMenu ViewMenu = MenuUtils.makeBoldMenu("View");
		prBuildMenu.setIcon(icon2);
		prInterfaceCausalityMenu.setIcon(icon2);
		prSystemCausalityMenu.setIcon(icon2);

		ViewMenu.add(prBuildMenu);
		ViewMenu.add(prInterfaceCausalityMenu);
		ViewMenu.add(prSystemCausalityMenu);

		return new JMenu[]{interfaceMenu, ViewMenu};
	}

    private static JMenu[] makeRunAnalysisToolMenus()
    {
        JMenu interfaceMenu = MenuUtils.makeBoldMenu("Analysis Tool");
        JMenuItem openMenu = MenuUtils.makeMenuItem(AnalysisToolRunPanel.openAction);
        JMenuItem submitMenu = MenuUtils.makeMenuItem(AnalysisToolRunPanel.submitAction);
        JMenuItem closeMenu = MenuUtils.makeMenuItem(AnalysisToolRunPanel.closeAction);

        interfaceMenu.add(openMenu);
        interfaceMenu.add(submitMenu);

        interfaceMenu.addSeparator();

        interfaceMenu.add(closeMenu);

        JMenu viewMenu = MenuUtils.makeBoldMenu("View");
        atBuildMenu.setIcon(icon2);
        atInterfaceCausalityMenu.setIcon(icon2);

        viewMenu.add(atBuildMenu);
        viewMenu.add(atInterfaceCausalityMenu);

        return new JMenu[] {interfaceMenu, viewMenu};
    }

    private static JMenu[] makeRunAnalysisToolInterfaceMenus()
    {
        JMenu interfaceMenu = MenuUtils.makeBoldMenu("Interface");
        JMenu resultsMenu = MenuUtils.makeBoldMenu("Results");
        JMenuItem submitMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.submitAction);
        JMenuItem pauseResumeMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.pauseResumeAction);
        JMenuItem saveVerMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.saveVerAction);
        JMenuItem saveMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.saveAction);
        JMenuItem loadVerMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.loadVerAction);
        JMenuItem saveResultsMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.saveResultsAction);
        JMenuItem loadResultsMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.loadResultsAction);
        JMenuItem closeMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.closeAction);
        JMenuItem killMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.killAction);

        submitMenu.setEnabled(false);
        pauseResumeMenu.setEnabled(false);
        saveVerMenu.setEnabled(false);
        saveMenu.setEnabled(false);
        loadVerMenu.setEnabled(false);
        killMenu.setEnabled(false);

        interfaceMenu.add(submitMenu);
        interfaceMenu.add(pauseResumeMenu);

        interfaceMenu.addSeparator();

        interfaceMenu.add(saveVerMenu);
		interfaceMenu.add(saveMenu);
		interfaceMenu.add(loadVerMenu);

		interfaceMenu.addSeparator();

        interfaceMenu.add(closeMenu);
		interfaceMenu.add(killMenu);

        resultsMenu.add(saveResultsMenu);
        resultsMenu.add(loadResultsMenu);

        JMenu viewMenu = MenuUtils.makeBoldMenu("View");

        viewResultsMenuItem.setIcon(icon2);
        viewDesignSpaceMenuItem.setIcon(icon2);

        viewMenu.add(viewResultsMenuItem);
        viewMenu.add(viewDesignSpaceMenuItem);

        return new JMenu[]{interfaceMenu, resultsMenu, viewMenu};
    }

    private static JMenu[] makeRunAnalysisToolInterfaceResultsViewMenus()
    {
        JMenu interfaceMenu = MenuUtils.makeBoldMenu("Interface");
        JMenu resultsMenu = MenuUtils.makeBoldMenu("Results");
        JMenuItem submitMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.submitAction);
        JMenuItem pauseResumeMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.pauseResumeAction);
        JMenuItem saveVerMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.saveVerAction);
        JMenuItem saveMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.saveAction);
        JMenuItem loadVerMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.loadVerAction);
        JMenuItem saveResultsMenu = MenuUtils.makeMenuItem(OptimizationInterfaceResultsPanel.saveResultsAction);
        JMenuItem loadResultsMenu = MenuUtils.makeMenuItem(OptimizationInterfaceResultsPanel.loadResultsAction);
        JMenuItem closeMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.closeAction);
        JMenuItem killMenu = MenuUtils.makeMenuItem(AnalysisToolInterfaceTreePanel.killAction);

        submitMenu.setEnabled(false);
        pauseResumeMenu.setEnabled(false);
        saveVerMenu.setEnabled(false);
        saveMenu.setEnabled(false);
        loadVerMenu.setEnabled(false);
        killMenu.setEnabled(false);

        interfaceMenu.add(submitMenu);
        interfaceMenu.add(pauseResumeMenu);

        interfaceMenu.addSeparator();

        interfaceMenu.add(saveVerMenu);
        interfaceMenu.add(saveMenu);
        interfaceMenu.add(loadVerMenu);

        interfaceMenu.addSeparator();

        interfaceMenu.add(closeMenu);
        interfaceMenu.add(killMenu);

        resultsMenu.add(saveResultsMenu);
        resultsMenu.add(loadResultsMenu);

        return new JMenu[]{interfaceMenu, resultsMenu};
    }

	private static JMenu[] makeRunInterfaceMenus()
	{
		JMenu interfaceMenu = MenuUtils.makeBoldMenu("Interface");
		JMenuItem submitMenu = MenuUtils.makeMenuItem(ModelInterfaceTreePanel.submitAction);
		JMenuItem pauseResumeMenu = MenuUtils.makeMenuItem(ModelInterfaceTreePanel.pauseResumeAction);

		JMenuItem saveVerMenu = MenuUtils.makeMenuItem(ModelInterfaceTreePanel.saveVerAction);
		JMenuItem saveMenu = MenuUtils.makeMenuItem(ModelInterfaceTreePanel.saveAction);
		JMenuItem loadVerMenu = MenuUtils.makeMenuItem(ModelInterfaceTreePanel.loadVerAction);
        saveVerMenu.setEnabled(false);
        saveMenu.setEnabled(false);
        loadVerMenu.setEnabled(false);
        //todo enable the save and load options when implemented

		JMenuItem closeMenu = MenuUtils.makeMenuItem(ModelInterfaceTreePanel.closeAction);

		interfaceMenu.add(submitMenu);
		interfaceMenu.add(pauseResumeMenu);

		interfaceMenu.addSeparator();

		interfaceMenu.add(saveVerMenu);
		interfaceMenu.add(saveMenu);
		interfaceMenu.add(loadVerMenu);

		interfaceMenu.addSeparator();

		interfaceMenu.add(closeMenu);

		return new JMenu[]{interfaceMenu};
	}


	public static JMenu makeRunMenu()
	{
		JMenu m = MenuUtils.makeMenu("Run");
		DHelp.enableHelp(m, DHelp.RUN_MODELS);
		JMenuItem newWindow = MenuUtils.makeMenuItem(RunMode.newWidnowAction);
		m.add(newWindow);
		JMenuItem find = MenuUtils.makeMenuItem("Find...");
		find.setEnabled(false);
		m.add(find);
		m.add(MenuUtils.makeMenuItem(RunMode.closeAllAction));
		m.addSeparator();
		m.add(MenuUtils.makeMenuItem(Modes.exitAction));
		return m;
	}


	public static void checkViewMenu(String option, String filter)
	{
		if (filter.equalsIgnoreCase("model")) {
			if (option.equals(RunMode.INTERFACECAUSALITYVIEW)) {
				BuildMenu.setIcon(icon2);
				InterfaceCausalityMenu.setIcon(icon);
				SystemCausalityMenu.setIcon(icon2);
				return;
			} else if (option.equals(RunMode.BUILDVIEW)) {
				BuildMenu.setIcon(icon);
				InterfaceCausalityMenu.setIcon(icon2);
				SystemCausalityMenu.setIcon(icon2);
				return;
			} else if (option.equals(RunMode.SYSTEMCAUSALITYVIEW)) {
				BuildMenu.setIcon(icon2);
				InterfaceCausalityMenu.setIcon(icon2);
				SystemCausalityMenu.setIcon(icon);
				return;
			} else {
				BuildMenu.setIcon(icon2);
				InterfaceCausalityMenu.setIcon(icon2);
				SystemCausalityMenu.setIcon(icon2);
				return;
			}
		} else if (filter.equalsIgnoreCase("playspace")) {
			if (option.equals(RunMode.INTERFACECAUSALITYVIEW)) {
				BuildMenu2.setIcon(icon2);
				InterfaceCausalityMenu2.setIcon(icon);
				SystemCausalityMenu2.setIcon(icon2);
				return;
			} else if (option.equals(RunMode.BUILDVIEW)) {
				BuildMenu2.setIcon(icon);
				InterfaceCausalityMenu2.setIcon(icon2);
				SystemCausalityMenu2.setIcon(icon2);
				return;
			} else if (option.equals(RunMode.SYSTEMCAUSALITYVIEW)) {
				BuildMenu2.setIcon(icon2);
				InterfaceCausalityMenu2.setIcon(icon2);
				SystemCausalityMenu2.setIcon(icon);
				return;
			} else {
				BuildMenu2.setIcon(icon2);
				InterfaceCausalityMenu2.setIcon(icon2);
				SystemCausalityMenu2.setIcon(icon2);
				return;
			}
		}
	}

	public static void checkPlayspaceViewMenu(String option)
	{
		if (option.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
			pBuildMenu.setIcon(icon2);
			pInterfaceCausalityMenu.setIcon(icon);
			pSystemCausalityMenu.setIcon(icon2);
			return;
		} else if (option.equals(DomeModelInterface.BUILD_VIEW)) {
			pBuildMenu.setIcon(icon);
			pInterfaceCausalityMenu.setIcon(icon2);
			pSystemCausalityMenu.setIcon(icon2);
			return;
		} else if (option.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
			pBuildMenu.setIcon(icon2);
			pInterfaceCausalityMenu.setIcon(icon2);
			pSystemCausalityMenu.setIcon(icon);
			return;
		} else {
			pBuildMenu.setIcon(icon2);
			pInterfaceCausalityMenu.setIcon(icon2);
			pSystemCausalityMenu.setIcon(icon2);
			return;
		}

	}

	public static void checkProjectViewMenu(String option)
	{
		if (option.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
			prBuildMenu.setIcon(icon2);
			prInterfaceCausalityMenu.setIcon(icon);
			prSystemCausalityMenu.setIcon(icon2);
			return;
		} else if (option.equals(DomeModelInterface.BUILD_VIEW)) {
			prBuildMenu.setIcon(icon);
			prInterfaceCausalityMenu.setIcon(icon2);
			prSystemCausalityMenu.setIcon(icon2);
			return;
		} else if (option.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
			prBuildMenu.setIcon(icon2);
			prInterfaceCausalityMenu.setIcon(icon2);
			prSystemCausalityMenu.setIcon(icon);
			return;
		} else {
			prBuildMenu.setIcon(icon2);
			prInterfaceCausalityMenu.setIcon(icon2);
			prSystemCausalityMenu.setIcon(icon2);
			return;
		}

	}

    public static void checkAnalysisToolViewMenu(String option)
	{
		if (option.equals(ToolInterface.INTERFACE_CAUSALITY_VIEW))
        {
            atBuildMenu.setIcon(icon2);
            atInterfaceCausalityMenu.setIcon(icon);
            return;
        }
        else if (option.equals(ToolInterface.BUILD_VIEW))
        {
            atBuildMenu.setIcon(icon);
            atInterfaceCausalityMenu.setIcon(icon2);
            return;
        }
        else if (option.equals(ToolInterface.RESULTS_DISPLAYED))
        {
            viewResultsMenuItem.setIcon(icon);
        }
        else if (option.equals(ToolInterface.RESULTS_CLOSED))
        {
            viewResultsMenuItem.setIcon(icon2);
        }
        else if (option.equals(ToolInterface.DESIGN_SPACE_DISPLAYED))
        {
            viewDesignSpaceMenuItem.setIcon(icon);
        }
        else if (option.equals(ToolInterface.DESIGN_SPACE_CLOSED))
        {
            viewDesignSpaceMenuItem.setIcon(icon2);
        }
        else
        {
            atBuildMenu.setIcon(icon2);
            atInterfaceCausalityMenu.setIcon(icon2);
            return;
        }

	}

	public static JMenu[] makeRunVisualizationMenus() {
	    return new JMenu[]{VisualizationBuildMenus.menus.getViewSeriesMenu(),
	                       VisualizationRunMenus.menus.makeRecordMenu()};
	}


}
