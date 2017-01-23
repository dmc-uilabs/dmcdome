// BuildMenus.java
package mit.cadlab.dome3.gui.mode.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.menu.PlayspaceWindowsMenu;
import mit.cadlab.dome3.gui.menu.WindowsMenu;
import mit.cadlab.dome3.gui.mode.ModeMenus;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.dome.DomeModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.dome.StandardViewBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelcomponent.filter.FilterBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextBuilderMenus;
import mit.cadlab.dome3.gui.objectmodel.model.plugin.PluginBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build.IterationRelationBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.tools.MappingsBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildMenus;
import mit.cadlab.dome3.gui.playspace.build.PlayspaceBuildPanel;
import mit.cadlab.dome3.help.DHelp;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.plugin.PluginUtils;
import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.*;
import java.util.Iterator;

import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolModelBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.optimization.OptimizationToolBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.AnalysisToolInterfaceBuildMenus;
import mit.cadlab.dome3.tool.AnalysisToolUtils;


public class BuildMenus extends ModeMenus {

    public static final WindowsMenu windowsMenu = makeWindowsMenu();
    public static final PlayspaceWindowsMenu playspace_windowsMenu = makePlayspaceWindowsMenu();
    public static final WindowsMenu modelWindowsMenu = makeModelWindowsMenu();
    public static final WindowsMenu iProjectWindowsMenu = makeiProjectWindowsMenu();
    public static final WindowsMenu toolWindowsMenu = makeToolWindowsMenu();


    private static boolean initialized = false;

    protected static WindowsMenu makeWindowsMenu() {
        WindowsMenu menu = new WindowsMenu();
        menu.add(MenuUtils.makeMenuItem(BuildMode.clipboardAction));
        menu.addSeparator();
        return menu;
    }

    protected static PlayspaceWindowsMenu makePlayspaceWindowsMenu() {
        PlayspaceWindowsMenu menu = new PlayspaceWindowsMenu("Playspaces");
        return menu;
    }

    protected static WindowsMenu makeModelWindowsMenu() {
        WindowsMenu menu = new WindowsMenu("Models");
        return menu;
    }

    protected static WindowsMenu makeiProjectWindowsMenu() {
        WindowsMenu menu = new WindowsMenu("iProjects");
        return menu;
    }

    protected static WindowsMenu makeToolWindowsMenu() {
        WindowsMenu menu = new WindowsMenu("Tools");
        return menu;
    }


    protected static JMenu makeBuildWindowMenu() {
        windowsMenu.addSeparator();
        windowsMenu.add(modelWindowsMenu);
        //windowsMenu.addSeparator();
        windowsMenu.add(playspace_windowsMenu);
        windowsMenu.add(iProjectWindowsMenu);
        return windowsMenu;
    }

    public static void initialize(int buildModeId) {
        if (initialized) {
            System.err.println("BuildMenus already initialized");
            return;
        }
        // must register with MenuManager in same order as with Modes
        MenuManager.addModeContext(BUILD_MODE, new JMenu[]{makeBuildMenu(), makeBuildWindowMenu()});

        _Context[] buildContexts = {
            new _Context(BUILD_DOMEMODEL, makeBuildDomeModelMenus()),
            new _Context(BUILD_PLUGINMODEL, makeBuildPluginModelMenus()),
            new _Context(BUILD_TOOLMODEL, makeBuildToolModelMenus()),
            new _Context(BUILD_DOMEMODEL_DEFINITION, makeBuildDomeModelDefinitionMenus()),
            new _Context(BUILD_MODELCAUSALITY_VIEW, makeBuildDomeModelCausalityMenus()),
            new _Context(BUILD_PROJECTCAUSALITY_VIEW, makeBuildDomeProjectCausalityMenus()),
            new _Context(BUILD_PLUGINMODEL_DEFINITION, makeBuildPluginModelDefinitionMenus()),
            new _Context(BUILD_DOMEMODEL_SETUP, makeBuildDomeModelSetupMenus()),
            new _Context(BUILD_QMOOMODEL, makeBuildQMOOModelMenus()),
            new _Context(BUILD_QMOOMODEL_VARIABLE_DEFINITION, makeBuildQMOOModelVariableDefinitionMenus()),
            new _Context(BUILD_QMOOMODEL_OBJECTIVE_DEFINITION, makeBuildQMOOModelObjectiveDefinitionMenus()),
            new _Context(BUILD_DOMEMODEL_DOCUMENTATION, makeBuildDomeModelDocumentationMenus()),
            new _Context(BUILD_PLUGINMODEL_DOCUMENTATION, makeBuildPluginModelDocumentationMenus()),
            new _Context(BUILD_QMOOMODEL_DOCUMENTATION, makeBuildAnalysisToolDocumentationMenus()),
            new _Context(BUILD_FILTER, makeBuildFilterMenus()),
            new _Context(BUILD_STANDARD_VIEW, makeBuildStandardViewMenus()),
            new _Context(BUILD_PROJECT_STANDARD_VIEW, makeBuildProjectStandardViewMenus()),
            new _Context(BUILD_PLUGIN_STANDARD_VIEW, makePluginBuildStandardViewMenus()),
            new _Context(BUILD_PROCEDURALRELATION_DEFINITION, makeBuildProceduralRelationDefinitionMenus()),
            new _Context(BUILD_MAPPING_TOOL, makeBuildMappingToolMenus()),
            new _Context(BUILD_PLUGIN_MAPPING_TOOL, makeBuildPluginMappingToolMenus()),
            new _Context(BUILD_PROJECT_MAPPING_TOOL, makeBuildProjectMappingToolMenus()),
            new _Context(BUILD_ANALYSIS_TOOL_MAPPING_TOOL, makeBuildAnalysisToolMappingToolMenus()),
            new _Context(BUILD_DOMEMODEL_INTERFACES, makeBuildDomeModelInterfacesMenus()),
            new _Context(BUILD_PROJECT_INTERFACES, makeBuildProjectInterfacesMenus()),
            new _Context(BUILD_TOOL_PROJECT_INTERFACES, makeBuildToolProjectInterfacesMenus()),
            new _Context(BUILD_PLUGINMODEL_INTERFACES, makeBuildPluginModelInterfacesMenus()),
            new _Context(BUILD_TOOLMODEL_INTERFACES, makeBuildToolModelInterfacesMenus()),
            new _Context(BUILD_PROJECTMODEL_INTERFACES, makeBuildProjectModelInterfacesMenus()),
            new _Context(BUILD_DOMEMODEL_INTERFACE_BUILDVIEW, makeBuildInterfaceMenus()),
            new _Context(BUILD_DOMEMODEL_INTERFACE_MODELVIEW, makeBuildInterfaceMenus()),
            new _Context(BUILD_DOMEMODEL_INTERFACE_CAUSALVIEW, makeBuildInterfaceMenus()),
            new _Context(BUILD_PLUGINMODEL_INTERFACE_BUILDVIEW, makeBuildPluginInterfaceMenus()),
            new _Context(BUILD_PLUGINMODEL_INTERFACE_MODELVIEW, makeBuildPluginInterfaceMenus()),
            new _Context(BUILD_PLUGINMODEL_INTERFACE_CAUSALVIEW, makeBuildPluginInterfaceMenus()),
            new _Context(BUILD_PROJECT_INTERFACE_BUILDVIEW, makeBuildProjectInterfaceMenus()),
            new _Context(BUILD_PROJECT_INTERFACE_MODELVIEW, makeBuildProjectInterfaceMenus()),
            new _Context(BUILD_PROJECT_INTERFACE_CAUSALVIEW, makeBuildProjectInterfaceMenus()),
            new _Context(BUILD_PROJECT_INTERFACE, makeBuildProjInterfaceMenus()),
            new _Context(BUILD_ANALYSIS_TOOL_PROJECT_INTERFACE, makeBuildAnalysisToolProjectInterfaceMenus()),
            new _Context(BUILD_TOOLMODEL_INTERFACE_BUILDVIEW, makeBuildToolInterfaceMenus()),

            new _Context(BUILD_PLUGIN_VISUALIZATION, makeBuildPluginVisualizationMenus()),
            new _Context(BUILD_VISUALIZATION, makeBuildVisualizationMenus()),
            new _Context(BUILD_PROJECT_VISUALIZATION, makeBuildProjectVisualizationMenus()),
            new _Context(BUILD_EQUALRELATION_DEFINITION, makeBuildEqualRelationDefinitionMenus()),
            new _Context(BUILD_PLAYSPACE, makePlayspaceMenus()),
            new _Context(BUILD_PROJECT, makeBuildProjectMenus()),
            new _Context(BUILD_PROJECT_DEFINITION, makeBuildProjectDefinitionMenus()),
            new _Context(BUILD_PROJECT_DOCUMENTATION, makeBuildProjectDocumentationMenus()),
            new _Context(BUILD_DOMEMODEL_OTHERVISUALIZATION_VIEW, makeBuildDomeModelOtherVisdViewMenus()),
            new _Context(BUILD_PLUGINMODEL_OTHERVISUALIZATION_VIEW, makeBuildPluginModelOtherVisdViewMenus()),
            new _Context(BUILD_PROJECT_DOMEMODEL_OTHERVISUALIZATION_VIEW, makeBuildProjectDomeModelOtherVisdViewMenus()),
            new _Context(BUILD_PROJECT_DOMEMODEL_DEFINITION, makeBuildProjectDomeModelDefinitionMenus()),
            new _Context(BUILD_TOOL_PROJECT_DOMEMODEL_DEFINITION, makeBuildToolProjectDomeModelDefinitionMenus()),
            new _Context(BUILD_TOOL_PROJECT_STANDARD_VIEW, makeBuildToolProjectStandardViewMenus()),
            new _Context(BUILD_TOOL_PROJECT_OTHERVISUALIZATION_VIEW, makeBuildToolProjectDomeModelOtherVisualizationViewMenus()),
            new _Context(BUILD_PROJECT_PROCEDURALRELATION_DEFINITION, makeBuildProjectProceduralRelationDefinitionMenus()),
            new _Context(BUILD_PROJECT_EQUALRELATION_DEFINITION, makeBuildProjectEqualRelationDefinitionMenus()),

            new _Context(BUILD_ITERATIONRELATION_DEFINITION, makeBuildIterationRelationDefinitionMenus()),
            new _Context(BUILD_PROJECT_ITERATIONRELATION_DEFINITION, makeBuildProjectIterationRelationDefinitionMenus()),
            new _Context(BUILD_TOOL_PROJECT_DEFINITION, makeBuildToolModelProjectDefinitionMenus()),
        };

        addContextMenusToMode(buildContexts, buildModeId);
        initialized = true;
    }

    public static JMenu makeBuildMenu() {
        JMenu m = MenuUtils.makeMenu("Build");
        DHelp.enableHelp(m, DHelp.BUILD_MODELS);
        JMenu newMenu = MenuUtils.makeMenu("New model");
        newMenu.add(MenuUtils.makeMenuItem(new BuildMode.NewModelAction(DomeModel.TYPE_INFO.getTypeName())));
        m.add(newMenu);
        JMenu openMenu = MenuUtils.makeMenu("Open model...");
        openMenu.add(MenuUtils.makeMenuItem(new BuildMode.OpenModelAction(DomeModel.TYPE_INFO.getTypeName())));

        // add new and open menus for plugin models
        Iterator plugins = PluginUtils.getPluginNames().iterator();
        while (plugins.hasNext()) {
            String pluginTypeName = (String) plugins.next();
            newMenu.add(MenuUtils.makeMenuItem(new BuildMode.NewModelAction(pluginTypeName)));
            openMenu.add(MenuUtils.makeMenuItem(new BuildMode.OpenModelAction(pluginTypeName)));
        }
        m.add(openMenu);
        m.add(MenuUtils.makeMenuItem(BuildMode.checkoutModelAction));

        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(new BuildMode.NewModelAction("New integration project", "Project")));
        m.add(MenuUtils.makeMenuItem(new BuildMode.OpenProjectAction("Open integration project...")));
        m.add(MenuUtils.makeMenuItem(BuildMode.checkoutProjectAction));

        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(new BuildMode.LaunchCatalogBuilderAction("Launch catalog builder", "Catalog")));

        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(BuildMode.newPlayspaceAction));//new playspace
        m.add(MenuUtils.makeMenuItem(BuildMode.openPlayspaceAction));//open playspace
        m.add(MenuUtils.makeMenuItem(BuildMode.checkoutPlayspaceAction));

        m.addSeparator();
        JMenu newToolsMenu = MenuUtils.makeMenu("New analysis tool...");
        JMenu openToolsMenu = MenuUtils.makeMenu("Open analysis tool...");

        Iterator tools = AnalysisToolUtils.getToolNames().iterator();
        while (tools.hasNext()) {
            String toolTypeName = (String) tools.next();
            newToolsMenu.add(MenuUtils.makeMenuItem(new BuildMode.NewToolAction(toolTypeName)));
            openToolsMenu.add(MenuUtils.makeMenuItem(new BuildMode.OpenToolAction(toolTypeName)));
        }

        m.add(newToolsMenu);
        m.add(openToolsMenu);
        m.add(MenuUtils.makeMenuItem(BuildMode.checkoutToolAction));

        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(BuildMode.closeAllAction));
        m.add(MenuUtils.makeMenuItem(BuildMode.saveAllAction));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(Modes.exitAction));
        return m;
    }

    public static JMenu[] makeBuildDomeModelMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu};
    }

    public static JMenu[] makeBuildPluginModelMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           PluginModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildToolModelMenus() {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           AnalysisToolModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildQMOOModelMenus() {
        return new JMenu[]{OptimizationToolBuildPanel.menu,
                           AnalysisToolModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildDomeModelDefinitionMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu, ContextBuilderMenus.menus.getEditMenu(),
                           ContextBuilderMenus.menus.getAddMenu(),
                           DomeModelBuildPanel.toolsMenu};
    }

	public static JMenu[] makeBuildDomeModelCausalityMenus()
	{
		return new JMenu[]{DomeModelBuildPanel.menu,
		                   DomeModelBuildPanel.modelCausalityAddMenu,
		                   DomeModelBuildPanel.toolsMenu};
	}

	public static JMenu[] makeBuildDomeProjectCausalityMenus()
	{
		return new JMenu[]{ProjectBuildPanel.menu,
		                   StandardViewBuildPanel.menu,
		                   DomeModelBuildPanel.modelCausalityAddMenu,
		                   DomeModelBuildPanel.iModelToolsMenu
		};
	}

    public static JMenu[] makeBuildPluginModelDefinitionMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           PluginBuildMenus.menus.getEditMenu(),
                           PluginBuildMenus.menus.getAddMenu(),
                           PluginModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildToolModelProjectDefinitionMenus() {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           ProjectBuildMenus.menus.editResourceMenu,
                           ProjectBuildMenus.menus.editIModelMenu,
                           AnalysisToolBuildMenus.menu.getProjectAddMenu(),
                           AnalysisToolModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildQMOOModelVariableDefinitionMenus() {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           AnalysisToolBuildMenus.menu.getEditMenu(),
                           AnalysisToolBuildMenus.menu.getAddMenu(),
                           AnalysisToolModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildQMOOModelObjectiveDefinitionMenus() {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           AnalysisToolBuildMenus.menu.getEditMenu(),
                           AnalysisToolBuildMenus.menu.getAddMenu(),
                           AnalysisToolModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildProjectDomeModelDefinitionMenus() {
        return new JMenu[]{ProjectBuildPanel.menu, ContextBuilderMenus.menus.getEditMenu(),
                           ContextBuilderMenus.menus.getAddMenu(),
                           DomeModelBuildPanel.iModelToolsMenu,
                           DomeModelBuildPanel.iModelSubscriptionViewMenu};
    }

    public static JMenu[] makeBuildToolProjectDomeModelDefinitionMenus() {
        return new JMenu[]{

            AnalysisToolModelBuildPanel.projectMenu,
            ContextBuilderMenus.menus.getEditMenu(),
            ContextBuilderMenus.menus.getAddMenu(),
            AnalysisToolModelBuildPanel.toolsiModelMenu,
            DomeModelBuildPanel.iModelSubscriptionViewMenu
        };
    }

	public static JMenu[] makeBuildDomeModelSetupMenus()
	{
		return new JMenu[]{DomeModelBuildPanel.menu,
		                   DomeModelBuildPanel.fileAddMenu,
		                   DomeModelBuildPanel.toolsMenu};
	}

    public static JMenu[] makeBuildDomeModelDocumentationMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           DocumentationBuildPanel.menu,
                           DomeModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildPluginModelDocumentationMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           DocumentationBuildPanel.menu,
                           PluginModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildAnalysisToolDocumentationMenus() {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           DocumentationBuildPanel.menu};
    }

    public static JMenu[] makeBuildFilterMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           FilterBuildPanel.menu};
    }

    public static JMenu[] makeBuildStandardViewMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           StandardViewBuildPanel.menu,
                           DomeModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildProjectStandardViewMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           StandardViewBuildPanel.menu,
                           DomeModelBuildPanel.iModelToolsMenu
        };
    }

    public static JMenu[] makeBuildToolProjectStandardViewMenus() {
        return new JMenu[]{

            AnalysisToolModelBuildPanel.projectMenu,
            StandardViewBuildPanel.menu,
            DomeModelBuildPanel.iModelToolsMenu
        };
    }

    public static JMenu[] makePluginBuildStandardViewMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           StandardViewBuildPanel.menu,
                           PluginModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildProceduralRelationDefinitionMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           ProceduralRelationBuildMenus.menus.getEditMenu(),
                           ProceduralRelationBuildMenus.menus.getAddMenu()};
    }

    public static JMenu[] makeBuildProjectProceduralRelationDefinitionMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           ProceduralRelationBuildMenus.menus.getEditMenu(),
                           ProceduralRelationBuildMenus.menus.getAddMenu()};
    }

    public static JMenu[] makeBuildMappingToolMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           MappingsBuildPanel.menu};
    }

    public static JMenu[] makeBuildPluginMappingToolMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           MappingsBuildPanel.menu};
    }

    public static JMenu[] makeBuildProjectMappingToolMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           MappingsBuildPanel.menu};
    }

    public static JMenu[] makeBuildAnalysisToolMappingToolMenus()
    {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           MappingsBuildPanel.menu};
    }

    public static JMenu[] makeBuildDomeModelInterfacesMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           ModelInterfaceManagerBuildPanel.menu};
    }

    public static JMenu[] makeBuildProjectInterfacesMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           ModelInterfaceManagerBuildPanel.menu};
    }

    public static JMenu[] makeBuildToolProjectInterfacesMenus() // menu for project interface manager
    {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           ModelInterfaceManagerBuildPanel.menu};
    }

    public static JMenu[] makeBuildPluginModelInterfacesMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           ModelInterfaceManagerBuildPanel.menu};
    }


    public static JMenu[] makeBuildToolModelInterfacesMenus() // menu for tool interface manager
    {
        return new JMenu[]{

            AnalysisToolModelBuildPanel.menu,
            ToolInterfaceManagerBuildPanel.menu
        };
    }


    public static JMenu[] makeBuildProjectModelInterfacesMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           ModelInterfaceManagerBuildPanel.menu};
    }

    public static JMenu[] makeBuildInterfaceMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           ModelInterfaceBuildMenus.menus.getEditMenu(),
                           ModelInterfaceBuildMenus.menus.getAddMenu(),
                           DomeModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildProjectInterfaceMenus()    //for imodel interface?
    {
        return new JMenu[]{ProjectBuildPanel.menu,
                           ModelInterfaceBuildMenus.menus.getEditMenu(),
                           ModelInterfaceBuildMenus.menus.getAddMenu(),
                           DomeModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildProjInterfaceMenus()  //for project interface
    {
        return new JMenu[]{ProjectBuildPanel.menu,
                           ModelInterfaceBuildMenus.menus.getEditMenu(),
                           ModelInterfaceBuildMenus.menus.getAddMenu(),
                           ProjectBuildPanel.projectInterfaceToolsMenu};
    }

    public static JMenu[] makeBuildAnalysisToolProjectInterfaceMenus()
    {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           ModelInterfaceBuildMenus.menus.getEditMenu(),
                           ModelInterfaceBuildMenus.menus.getAddMenu(),
                           AnalysisToolModelBuildPanel.analysisToolProjectInterfaceToolsMenu};
    }

    public static JMenu[] makeBuildToolInterfaceMenus()  //for project interface
    {
        return new JMenu[]{AnalysisToolModelBuildPanel.menu,
                           AnalysisToolInterfaceBuildMenus.menus.getEditMenu(),
                           AnalysisToolInterfaceBuildMenus.menus.getAddMenu(),
                           AnalysisToolModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildPluginInterfaceMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           ModelInterfaceBuildMenus.menus.getEditMenu(),
                           ModelInterfaceBuildMenus.menus.getAddMenu(),
                           PluginModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildVisualizationMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           VisualizationBuildMenus.menus.getEditMenu(),
                           VisualizationBuildMenus.menus.getRecordMenu(),
                           VisualizationBuildMenus.menus.getViewSeriesMenu()};
    }

    public static JMenu[] makeBuildProjectVisualizationMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           VisualizationBuildMenus.menus.getEditMenu(),
                           VisualizationBuildMenus.menus.getRecordMenu(),
                           VisualizationBuildMenus.menus.getViewSeriesMenu()};
    }

    public static JMenu[] makeBuildPluginVisualizationMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           VisualizationBuildMenus.menus.getEditMenu(),
                           VisualizationBuildMenus.menus.getRecordMenu(),
                           VisualizationBuildMenus.menus.getViewSeriesMenu()};
    }

    //Add For Equal Relation --  Qing Feb14
    public static JMenu[] makeBuildEqualRelationDefinitionMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           EqualRelationBuildMenus.menus.getEditMenu(),
                           EqualRelationBuildMenus.menus.getAddMenu()};
    }

    public static JMenu[] makeBuildProjectEqualRelationDefinitionMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           EqualRelationBuildMenus.menus.getEditMenu(),
                           EqualRelationBuildMenus.menus.getAddMenu()};
    }

    //Add for Iteration Relation -- Qing Sep 17th
    public static JMenu[] makeBuildIterationRelationDefinitionMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           IterationRelationBuildMenus.menus.getEditMenu(),
                           IterationRelationBuildMenus.menus.getAddMenu()};
    }

    public static JMenu[] makeBuildProjectIterationRelationDefinitionMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           IterationRelationBuildMenus.menus.getEditMenu(),
                           IterationRelationBuildMenus.menus.getAddMenu()};
    }


    /**
     * should have the following items: New model -> (dome model, excel model sub menus); Open model ...; separator; New playspace;
     *  Open playspace...; separator; Close all; Save all; separator; Exit
     * *Add For playspace --  Qing Mar18
     * @return
     */
    public static JMenu[] makePlayspaceMenus() {
        JMenu m = MenuUtils.makeBoldMenu("Dome Playspace");
        m.add(MenuUtils.makeMenuItem(BuildMode.newPlayspaceAction));
        m.add(MenuUtils.makeMenuItem(BuildMode.openPlayspaceAction));
        m.addSeparator();
        m.add(MenuUtils.makeMenuItem(PlayspaceBuildPanel.saveAction));
        m.add(MenuUtils.makeMenuItem(PlayspaceBuildPanel.saveAsAction));
        m.add(MenuUtils.makeMenuItem(PlayspaceBuildPanel.closeAction));

        return new JMenu[]{m};
    }

    public static JMenu[] makeBuildProjectMenus() {
        return new JMenu[]{ProjectBuildPanel.menu};
    }

    public static JMenu[] makeBuildProjectDefinitionMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           ProjectBuildMenus.menus.editResourceMenu,
                           ProjectBuildMenus.menus.editIModelMenu,
                           ProjectBuildMenus.menus.addMenu,
                           ProjectBuildPanel.toolsMenu,
        };
    }

    public static JMenu[] makeBuildProjectDocumentationMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           DocumentationBuildPanel.menu};
    }

    public static JMenu[] makeBuildDomeModelOtherVisdViewMenus() {
        return new JMenu[]{DomeModelBuildPanel.menu,
                           DomeModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildPluginModelOtherVisdViewMenus() {
        return new JMenu[]{PluginModelBuildPanel.menu,
                           PluginModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildProjectDomeModelOtherVisdViewMenus() {
        return new JMenu[]{ProjectBuildPanel.menu,
                           DomeModelBuildPanel.toolsMenu};
    }

    public static JMenu[] makeBuildToolProjectDomeModelOtherVisualizationViewMenus() {
        return new JMenu[]{

            AnalysisToolModelBuildPanel.projectMenu,
            DomeModelBuildPanel.toolsMenu
        };
    }
}
