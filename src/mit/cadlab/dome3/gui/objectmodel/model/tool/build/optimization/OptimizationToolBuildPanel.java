//OptimizationToolBuildPanel.java

package mit.cadlab.dome3.gui.objectmodel.model.tool.build.optimization;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolBuildMenus;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolModelBuildPanel;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.tool.optimization.qmoo.QMOOConfiguration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class OptimizationToolBuildPanel extends AnalysisToolModelBuildPanel
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("OptimizationToolBuildPanel");
	public static final String XML_TAG = "qmoomodelbuildpanel";

	public static final String VARIABLES = "design variables";
	public static final String OBJECTIVES = "design objectives";
	public static final String CONFIGURATION = "configuration";

	protected OptimizationToolDefinitionBuildPanel _variablesPanel;
    protected OptimizationToolDefinitionBuildPanel _objectivesPanel;


    public OptimizationToolBuildPanel(OptimizationToolBuild modelBuilder)
	{
		super(modelBuilder);
		createComponents();

	}

	protected void createComponents()
	{
		super.createComponents();

		String[] columnNames = new String[]{"name", "value", "lower", "upper", this.modelBuilder.getToolConfiguration().getMappingColumnName()};
		int[] columnWidths = new int[]{250, 150, 100, 100, 150};
		this._variablesPanel = new OptimizationToolDefinitionBuildPanel((OptimizationToolBuild)modelBuilder, QMOOConfiguration.MODEL_VARIABLE,
                                            (DefaultContextBuilder)((OptimizationToolBuild)modelBuilder).getDesignVariableContext(), columnNames.length,
		                                        columnNames, columnWidths);
		contentTabs.addTab(VARIABLES, _variablesPanel);

        columnNames = new String[]{"name", "value", "direction", this.modelBuilder.getToolConfiguration().getMappingColumnName()};
     	columnWidths = new int[]{250, 150, 200, 150};
        this._objectivesPanel = new OptimizationToolDefinitionBuildPanel((OptimizationToolBuild)modelBuilder, QMOOConfiguration.MODEL_OBJECTIVE,
                (DefaultContextBuilder)((OptimizationToolBuild)modelBuilder).getDesignObjectiveContext(),
                columnNames.length, columnNames, columnWidths);
		contentTabs.addTab(OBJECTIVES, _objectivesPanel);

		JPanel setupPanel = makeSetupPanel();
		contentTabs.addTab(CONFIGURATION, setupPanel);

		docPanel = new DocumentationBuildPanel(modelBuilder.getDocumentation());
		contentTabs.addTab("documentation", docPanel);
		contentTabs.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
                OptimizationToolBuildPanel.this.setMenuContext();
			}
		});
		layoutComponent();
	}

	public void setMenuContext()
	{
		switch (contentTabs.getSelectedIndex()) {
            case 0: // qmoo project
                ((OptimizationToolBuild)this.modelBuilder).setView(OptimizationToolBuild.TOOL_PROJECT_VIEW);
                setToolProjectMenuContext();
                OptimizationToolBuildPanel.setCurrentView(OptimizationToolBuildPanel.TOOL_PROJECT);
                return;
			case 1: // variable definition
                ((OptimizationToolBuild)this.modelBuilder).setView(OptimizationToolBuild.VARIABLE_VIEW);
				AnalysisToolBuildMenus.menu.setEditTitle("Edit Variables");
                AnalysisToolModelBuildPanel.projectInterfaces.setEnabled(false);
                _variablesPanel.setVariableMenuContext();
                OptimizationToolBuildPanel.setCurrentView(OptimizationToolBuildPanel.VARIABLES);
				return;
            case 2: // objective definition
                ((OptimizationToolBuild)this.modelBuilder).setView(OptimizationToolBuild.OBJECTIVE_VIEW);
                AnalysisToolBuildMenus.menu.setEditTitle("Edit Objectives");
                AnalysisToolModelBuildPanel.projectInterfaces.setEnabled(false);
                _objectivesPanel.setObjectiveMenuContext();
                OptimizationToolBuildPanel.setCurrentView(OptimizationToolBuildPanel.OBJECTIVES);
                return;
			case 4: // documentation
				MenuManager.setContext(ModeContexts.BUILD_QMOOMODEL_DOCUMENTATION);
				break;
			default: // default for other tabs
				MenuManager.setContext(ModeContexts.BUILD_QMOOMODEL);
		}
		BuildFocusTracker.notifyInFocus(this, modelBuilder);
	}

}
