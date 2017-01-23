// RunWindowsMenu.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.menu;

import mit.cadlab.dome3.gui.objectmodel.DomeRunFrame;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.HashMap;

public class RunWindowsMenu extends JMenu
{

	protected HashMap windowMenuItems = new HashMap(); // key=window; value=menuitem

	public RunWindowsMenu(String title)
	{
		super(title);
		setFont(MenuUtils.FONT12);
	}

	public void addWindow(DomeRunFrame frame)
	{
		if (windowMenuItems.containsKey(frame)) {
			System.err.println("RunWindowsMenu: already added " + frame.getTitle());
			return;
		}
		addMenuItem(frame);
	}


	public void removeWindow(DomeRunFrame frame)
	{
		JMenuItem frameMenuItem = (JMenuItem) windowMenuItems.remove(frame);
		if (frameMenuItem == null) {
			return;
		}

		remove(frameMenuItem);

	}


	protected void addMenuItem(DomeRunFrame frame)
	{
		DomeRunFrameMenuItem frameMenuItem = new DomeRunFrameMenuItem(frame);
		add(frameMenuItem);
		windowMenuItems.put(frame, frameMenuItem);
	}

	public static class DomeRunFrameMenuItem extends JMenuItem
	{
		protected DomeRunFrame frame;
		protected BrowserNameListener titleListener = new BrowserNameListener();

		public DomeRunFrameMenuItem(DomeRunFrame f)
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
			setText(frame.getTitle());

			((JComponent) frame.getGui()).addPropertyChangeListener(NameListener.NAME, titleListener);
		}

		protected class BrowserNameListener extends NameListener
		{
			public void nameChanged(String newName)
			{
					setText(newName);
			}
		}
	}


}
