// DomeModelDefinitionBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.model.tool.build.optimization;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.model.tool.build.AnalysisToolBuildMenus;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;

import javax.swing.*;
import java.awt.*;

public class OptimizationToolDefinitionBuildPanel extends JPanel
{

	protected static GridBagConstraints gbc;
	protected static ImageIcon comboArrow = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrow.gif");
	protected static ImageIcon comboArrowOver = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrowOver.gif");
	protected static Color notEditableColor = new Color(105, 105, 105);

	protected OptimizationToolBuild modelBuilder;
	protected CardLayout2 modelViewsCards;
	protected JPanel modelViewsPanel;
	protected ContextTreeBuilderPanel buildViewPanel;

	public OptimizationToolDefinitionBuildPanel(OptimizationToolBuild modelBuilder, String qmooParameterType, DefaultContextBuilder context,
	                                       int noCols, String[] colNames, int[] colWidths)
	{
		this.modelBuilder = modelBuilder;
		setBackground(Templates.DARKER_BACKGROUND_COLOR);
		createComponents(context, qmooParameterType, noCols, colNames, colWidths);
	}
	protected void createComponents(DefaultContextBuilder context, String qmooParameterType, int noCols, String[] colNames, int[] colWidths)
	{
		modelViewsCards = new CardLayout2();
		modelViewsPanel = new JPanel();
		modelViewsPanel.setLayout(modelViewsCards);
		buildViewPanel = new ContextTreeBuilderPanel(context, qmooParameterType, noCols, colNames, colWidths);
		buildViewPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		buildViewPanel.setUpDownBackgroundColour(Templates.DARKER_BACKGROUND_COLOR);
		modelViewsPanel.add(DomeModel.BUILD_VIEW, buildViewPanel);
		layoutComponent();
	}
	protected void layoutComponent()
	{
		JComponent[] comps = {modelViewsPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// 25 inset
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBagB(this, comps, gbcs);
	}



    public void setVariableMenuContext()
	{
        AnalysisToolBuildMenus.menu.setToolDataTypes(modelBuilder.getToolTypeName());
		MenuManager.setContext(ModeContexts.BUILD_QMOOMODEL_VARIABLE_DEFINITION);
        JComponent comp = (JComponent) modelViewsCards.getActiveComponent();
		BuildFocusTracker.notifyInFocus(comp, modelBuilder);
		if (comp.equals(buildViewPanel))
			buildViewPanel.setQMOOModelEditMenusForSelection();
	}

    public void setObjectiveMenuContext()
    {
        AnalysisToolBuildMenus.menu.setToolDataTypes(modelBuilder.getToolTypeName());
        MenuManager.setContext(ModeContexts.BUILD_QMOOMODEL_OBJECTIVE_DEFINITION);
        JComponent comp = (JComponent) modelViewsCards.getActiveComponent();
        BuildFocusTracker.notifyInFocus(comp, modelBuilder);
        if(comp.equals(buildViewPanel))
            buildViewPanel.setQMOOModelEditMenusForSelection();
    }
}

