// VisualizationBuildMenus.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization;

import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.*;

/**
 *
 */
public class VisualizationBuildMenus
{
	public static final VisualizationBuildMenus menus = new VisualizationBuildMenus();


	protected JMenuItem dataMI = MenuUtils.makeMenuItem(VisualizationBuildPanel.dataAction);
	protected JMenuItem visualPropertyMI = MenuUtils.makeMenuItem(VisualizationBuildPanel.showPropertyPanelAction);
	protected JMenu pasteCopyMI = MenuUtils.makeMenu("Paste copy");
	protected JMenuItem pasteCopyLastSelection = MenuUtils.makeMenuItem(VisualizationBuildPanel.pasteCopyLastSelectionAction);
	protected JMenuItem pasteCopyFromClipBoard = MenuUtils.makeMenuItem(VisualizationBuildPanel.pasteCopyFromClipBoardAction);
	protected JMenuItem saveChartMI = MenuUtils.makeMenuItem(VisualizationBuildPanel.saveChartAction);
	protected JMenuItem printChartMI = MenuUtils.makeMenuItem(VisualizationBuildPanel.printChartAction);
	protected JMenu editMenu = makeEditMenu();
	protected JMenu recordMenu = makeRecordMenu();
	protected JMenu viewSeriesMenu = makeViewSeriesMenu();


	public VisualizationBuildMenus()
	{

	}

	public JMenu makeEditMenu()
	{
		JMenu menu = MenuUtils.makeBoldMenu("Edit Chart");
		menu.add(pasteCopyMI);
		pasteCopyMI.add(pasteCopyLastSelection);
		pasteCopyMI.add(pasteCopyFromClipBoard);
		menu.addSeparator();

		menu.add(dataMI);
		menu.addSeparator();
		menu.add(visualPropertyMI);


		//pasteCopyLastSelection.setEnabled(false);
		return menu;
	}

	public JMenu makeRecordMenu()
	{
		JMenu menu = MenuUtils.makeBoldMenu("Record");
		menu.add(saveChartMI);
		menu.add(printChartMI);
		return menu;
	}

	public JMenu makeViewSeriesMenu()
	{
		JMenu menu = MenuUtils.makeBoldMenu("View Series");

		return menu;

	}


	public JMenu getEditMenu()
	{
		return this.editMenu;
	}

	public JMenu getRecordMenu()
	{
		return this.recordMenu;
	}

	public JMenu getViewSeriesMenu()
	{
		return this.viewSeriesMenu;
	}


}
