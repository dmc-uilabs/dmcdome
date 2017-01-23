package mit.cadlab.dome3.gui.objectmodel.project.build;

import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Apr 10, 2003
 * Time: 2:12:05 PM
 * To change this template use Options | File Templates.
 */
public class ProjectBuildMenus
{
	public static ProjectBuildMenus menus = new ProjectBuildMenus();
	public static ImageIcon icon = DomeIcons.getIcon(DomeIcons.CHECKED);
	public static ImageIcon icon2 = DomeIcons.getIcon(DomeIcons.UNCHECKED);

	public JMenu editResourceMenu;
	public JMenu editIModelMenu;
	public JMenu addMenu;
	public JMenu pasteMenu;
	public JMenu pasteIMenu;
	JMenuItem addAndSubScribeMenuItem;
	protected boolean isClipboardEmpty;

	protected JMenuItem buildMenuItem;
	protected JMenuItem interfaceCausalityMenuItem;
	protected JMenuItem systemCausalityMenuItem;
	public JMenu viewMenu;

	public ProjectBuildMenus()
	{
		editResourceMenu = MenuUtils.makeBoldMenu("Edit resources");
		editResourceMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.copyResourceAction));
		ProjectBuildListPanel.copyResourceAction.setEnabled(false);
		pasteMenu = MenuUtils.makeMenu("Paste");
		pasteMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.pasteLastSelectionAction));
		pasteMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.pasteFromClipboardAction));
		editResourceMenu.add(pasteMenu);
		editResourceMenu.addSeparator();
		editResourceMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.resourceClearSelectionAction));
		editResourceMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.resourceSelectAllAction));
        editResourceMenu.addSeparator();
        editResourceMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.removeResourceAction));
        ProjectBuildListPanel.removeResourceAction.setEnabled(false);
		editResourceMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.relocateResourceAction));
		ProjectBuildListPanel.relocateResourceAction.setEnabled(false);
		editResourceMenu.setEnabled(false);

		editIModelMenu = MenuUtils.makeBoldMenu("Edit iModels");
		editIModelMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.openIModelAction));
		ProjectBuildListPanel.openIModelAction.setEnabled(false);
		editIModelMenu.addSeparator();
		editIModelMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.copyIModelAction));
		ProjectBuildListPanel.copyIModelAction.setEnabled(false);
		pasteIMenu = MenuUtils.makeMenu("Paste");
		pasteIMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.pasteLastISelectionAction));
		pasteIMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.pasteFromIClipboardAction));
		editIModelMenu.add(pasteIMenu);
		editIModelMenu.addSeparator();
		editIModelMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.iModelClearSelectionAction));
		editIModelMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.iModelSelectAllAction));
        editIModelMenu.addSeparator();
        editIModelMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.deleteIModelAction));
        ProjectBuildListPanel.deleteIModelAction.setEnabled(false);
		editIModelMenu.setEnabled(false);

		addMenu = MenuUtils.makeBoldMenu("Add");
		addMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.addResourceAction));
		addMenu.add(MenuUtils.makeMenuItem(ProjectBuildListPanel.addIModelAction));
		addMenu.addSeparator();
		addAndSubScribeMenuItem = MenuUtils.makeMenuItem(ProjectBuildListPanel.addAndSubScribeAction);
		addMenu.add(addAndSubScribeMenuItem);
		addAndSubScribeMenuItem.setEnabled(false);
		BuildMode.clipboard.addClipboardListener(new ClipboardListener());
		updateClipboardStatus();

		buildMenuItem = MenuUtils.makeMenuItem(ProjectBuildListPanel.viewBuildAction);
		interfaceCausalityMenuItem = MenuUtils.makeMenuItem(ProjectBuildListPanel.viewInterfaceCausalityAction);
		systemCausalityMenuItem = MenuUtils.makeMenuItem(ProjectBuildListPanel.viewSystemCausalityAction);
		viewMenu = MenuUtils.makeBoldMenu("View");
		buildMenuItem.setIcon(icon2);
		interfaceCausalityMenuItem.setIcon(icon2);
		systemCausalityMenuItem.setIcon(icon2);
		viewMenu.add(buildMenuItem);
		viewMenu.add(interfaceCausalityMenuItem);
		viewMenu.add(systemCausalityMenuItem);
        viewMenu.setEnabled(false);
	}

	protected void updateClipboardStatus()
	{
		if (isClipboardEmpty == BuildMode.clipboard.isEmpty()) return; // already consistent
		isClipboardEmpty = BuildMode.clipboard.isEmpty();
		if (isClipboardEmpty) { // disable paste options
        	pasteMenu.setEnabled(false);
			pasteIMenu.setEnabled(false);
			addAndSubScribeMenuItem.setEnabled(false);
		}
		else { // enable paste options
			pasteMenu.setEnabled(true);
			pasteIMenu.setEnabled(true);
			addAndSubScribeMenuItem.setEnabled(true);
		}
	}

	class ClipboardListener implements DListListener
	{
		public void intervalChanged(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void intervalAdded(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void intervalRemoved(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void itemsRemoved(DListEvent e)
		{
			updateClipboardStatus();
		}

		public void itemsReplaced(DListEvent e)
		{
			updateClipboardStatus();
		}
	}

	public void setView(String option)
	{
		if (option.equals(DomeModelInterface.INTERFACE_CAUSALITY_VIEW)) {
			buildMenuItem.setIcon(icon2);
			interfaceCausalityMenuItem.setIcon(icon);
			systemCausalityMenuItem.setIcon(icon2);
			return;
		} else if (option.equals(DomeModelInterface.BUILD_VIEW)) {
			buildMenuItem.setIcon(icon);
			interfaceCausalityMenuItem.setIcon(icon2);
			systemCausalityMenuItem.setIcon(icon2);
			return;
		} else if (option.equals(DomeModelInterface.SYSTEM_CAUSALITY_VIEW)) {
			buildMenuItem.setIcon(icon2);
			interfaceCausalityMenuItem.setIcon(icon2);
			systemCausalityMenuItem.setIcon(icon);
			return;
		} else {
			buildMenuItem.setIcon(icon2);
			interfaceCausalityMenuItem.setIcon(icon2);
			systemCausalityMenuItem.setIcon(icon2);
			return;
		}
	}
}
