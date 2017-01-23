// WindowsMenu.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.menu;

import mit.cadlab.dome3.gui.objectmodel.DomeFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.ModelComponent;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class WindowsMenu extends JMenu
{

	protected HashMap projectMenus = new HashMap(); // key=model; value=menu
	protected HashMap windowMenuItems = new HashMap(); // key=window; value=menuitem

	public WindowsMenu()
	{
		super("Windows");
		setFont(MenuUtils.FONT12);
	}

	public WindowsMenu(String title)
	{
		super(title);
		setFont(MenuUtils.FONT12);
	}

	public void addWindow(DomeFrame frame)
	{
		if (windowMenuItems.containsKey(frame)) {
			System.err.println("WindowsMenu: already added " + frame.getTitle());
			return;
		}
		Object guiObj = frame.getGuiObject();
		if (guiObj instanceof DomeObject) {
			DomeObject dObj = (DomeObject) guiObj;
			if (dObj instanceof Model) {
				addMenu(dObj);
				addMenuItem(frame);
			}
			else if (dObj instanceof ModelComponent) {
				addMenuItem(frame);
			}
			else {
				System.err.println("WindowsMenu: don't know how to add window for " + dObj.getNameIdString());
			}
		}
		else if (guiObj instanceof ModelComponent) {
			addMenuItem(frame);
		}
		else {
			System.err.println("WindowsMenu: don't know how to add window for " + guiObj);
		}
	}

	public void removeWindow(DomeFrame frame)
	{
		JMenuItem frameMenuItem = (JMenuItem) windowMenuItems.remove(frame);
		if (frameMenuItem == null) {
			return;
		}
		if (frameMenuItem instanceof DomeObjectFrameMenuItem) {
			((DomeObjectFrameMenuItem) frameMenuItem).cleanUp();
		}
		else if (frameMenuItem instanceof DomeFrameMenuItem) {
			((DomeFrameMenuItem) frameMenuItem).cleanUp();
		}
		JMenu menu = getMenu(frame);
		menu.remove(frameMenuItem);
		if (menu.getItemCount() == 0) { // last item gone
			remove(menu);
			if (menu instanceof DomeObjectMenu) {
				((DomeObjectMenu) menu).cleanUp();
			}
			Object obj = frame.getGuiObject();
			Model model = null;
			if (obj instanceof DomeObject) {
				model = (obj instanceof ModelObject) ? getRootModel((ModelObject) obj) : (Model) obj;
			}
			else if (obj instanceof ModelComponent) {
				model = ((ModelComponent) obj).getModel();
			}
			projectMenus.remove(model);
		}
	}

	protected void addMenu(DomeObject dObj)
	{
		JMenu dObjMenu = new DomeObjectMenu(dObj);
		add(dObjMenu);
		projectMenus.put(dObj, dObjMenu);
	}

	protected void addMenuItem(DomeFrame frame)
	{
		JMenu menu = getMenu(frame);
		Object guiObj = frame.getGuiObject();
		JMenuItem frameMenuItem = null;
		//Qing: here all guiObj are ModelComponent, so it always create DomeFrameMenuItem. so why there is a second choice?
		if (guiObj instanceof ModelComponent) {
			frameMenuItem = new DomeFrameMenuItem(frame);
		}
		else if (guiObj instanceof DomeObject) {
			frameMenuItem = new DomeObjectFrameMenuItem(frame);
		}
		menu.add(frameMenuItem);
		windowMenuItems.put(frame, frameMenuItem);
	}

	protected JMenu getMenu(DomeFrame frame)
	{
		Object obj = frame.getGuiObject();
		Model model = null;
		if (obj instanceof ModelComponent) {
			model = ((ModelComponent) obj).getModel();
		}
		else if (obj instanceof DomeObject) {
			model = (obj instanceof ModelObject) ? getRootModel((ModelObject) obj) : (Model) obj;
		}
		JMenu menu = (JMenu) projectMenus.get(model);
		if (menu == null)
        {
            /*
             * model could be an integration project which could belong to a AnalysisToolModelBuildPanel
             * will get the AnalysisToolModelBuild contained within the IntegrationProjectBuilder class
             * and will use that as the key to get the menu item out of the HashMap
             */
            if(model instanceof IntegrationProjectBuilder)
            {
                IntegrationProjectBuilder iProjectBuilder = (IntegrationProjectBuilder) model;
                if(iProjectBuilder.getIsToolProjectBuilder())
                {
                    JMenu toolMenu = (JMenu) projectMenus.get(iProjectBuilder.getToolModel());
                    if(toolMenu != null)
                        return toolMenu;
                }
            }
            throw new NullPointerException("unable to find menu for object's model: " + model.getNameIdString());
        }
		return menu;
	}

	protected Model getRootModel(ModelObject mObj)
	{
		Model m = mObj.getModel();
		while (m instanceof ModelObject) {
			m = ((ModelObject) m).getModel();
		}
		return m;
	}

	public static class DomeObjectMenu extends JMenu
	{
		NameListener nameListener = new DomeObjectNameListener();
		private DomeObject dObj;

		public DomeObjectMenu(DomeObject dObj)
		{
			super(dObj.getName());
			this.dObj = dObj;
			setFont(MenuUtils.FONT12);
			dObj.addPropertyChangeListener(NameListener.NAME, nameListener);
		}

		protected class DomeObjectNameListener extends NameListener
		{
			public void nameChanged(String newName)
			{
				setText(newName);
			}
		}

		public void cleanUp()
		{
			dObj.removePropertyChangeListener(NameListener.NAME, nameListener);
		}
	}

	public static class DomeFrameMenuItem extends JMenuItem
	{
		protected DomeFrame frame;
		protected FrameNameListener titleListener = new FrameNameListener();

		public DomeFrameMenuItem(DomeFrame f)
		{
			setFont(MenuUtils.FONT12);
			frame = f;
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					if (frame.getState() == Frame.ICONIFIED)
						frame.setState(Frame.NORMAL);
					frame.toFront();
				}
			});
			setText(frame.getGui().getTitle());
			frame.addPropertyChangeListener(NameListener.NAME,  titleListener);
		/*	Object guiObj = frame.getGui().getGuiObject();
		    if (guiObj instanceof DomeObject) {
			    ((DomeObject) guiObj).addPropertyChangeListener(NameListener.NAME,  titleListener);
		    }*/
		}

		protected class FrameNameListener extends NameListener
		{
			public void nameChanged(String newName)
			{
				setText(frame.getTitle());
			}
		}

		protected void cleanUp()
		{
			frame.removePropertyChangeListener(NameListener.NAME,  titleListener);
			/*Object guiObj = frame.getGui().getGuiObject();
		    if (guiObj instanceof DomeObject) {
			    ((DomeObject) guiObj).removePropertyChangeListener(NameListener.NAME,  titleListener);
		    }*/
		}
	}

	public static class DomeObjectFrameMenuItem extends DomeFrameMenuItem
	{
		protected NameListener nameListener = new DomeObjectNameListener();
		protected DomeObject dObj;
		protected DomeObjectGui gui;

		public DomeObjectFrameMenuItem(DomeFrame f)
		{
			super(f);
			gui = (DomeObjectGui) frame.getGui();
			dObj = gui.getDomeObject();
		//	dObj.addPropertyChangeListener(NameListener.NAME, nameListener);
			gui.getDomeObject().addPropertyChangeListener(NameListener.NAME, nameListener);
		}

		protected class DomeObjectNameListener extends NameListener
		{
			public void nameChanged(String newName)
			{
				setText(newName);
			}
		}

		protected void cleanUp()
		{
			dObj.removePropertyChangeListener(NameListener.NAME, nameListener);
		}
	}
}
