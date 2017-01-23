package mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.run;

import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.visualization.VisualizationBuildMenus;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 30, 2003
 * Time: 3:29:40 PM
 * To change this template use Options | File Templates.
 */
public class VisualizationRunMenus
{
	public static final VisualizationRunMenus menus = new VisualizationRunMenus();

	protected JMenuItem saveChartMI = MenuUtils.makeMenuItem(VisualizationRunPanel.saveRunChartAction);
	protected JMenuItem printChartMI = MenuUtils.makeMenuItem(VisualizationRunPanel.printRunChartAction);
	protected JMenu recordMenu = makeRecordMenu();

	public VisualizationRunMenus()
	{
	}

	public JMenu makeRecordMenu()
	{
		JMenu menu = MenuUtils.makeBoldMenu("Record");
		menu.add(saveChartMI);
		menu.add(printChartMI);
		return menu;
	}

}
