// RunWindowsMenu.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.menu;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildPlayspaceFrame;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.swing.MenuUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class PlayspaceWindowsMenu extends JMenu
{

	protected HashMap windowMenuItems = new HashMap(); // key=window; value=menuitem

	public PlayspaceWindowsMenu(String title)
	{
		super(title);
		setFont(MenuUtils.FONT12);
	}

	public void addWindow(DomeBuildPlayspaceFrame frame)
	{
		if (windowMenuItems.containsKey(frame)) {
			System.err.println("PlayspaceWindowsMenu: already added " + frame.getTitle());
			return;
		}
		addMenuItem(frame);
	}


	public void removeWindow(DomeBuildPlayspaceFrame frame)
	{
		JMenuItem frameMenuItem = (JMenuItem) windowMenuItems.remove(frame);
		if (frameMenuItem == null) {
			return;
		}

		remove(frameMenuItem);

	}


	protected void addMenuItem(DomeBuildPlayspaceFrame frame)
	{
		DomeBuildPlayspaceFrameMenuItem frameMenuItem = new DomeBuildPlayspaceFrameMenuItem(frame);
		add(frameMenuItem);
		windowMenuItems.put(frame, frameMenuItem);
	}

	public static class DomeBuildPlayspaceFrameMenuItem extends JMenuItem
	{
		protected DomeBuildPlayspaceFrame frame;
		protected BrowserNameListener titleListener = new BrowserNameListener();

		public DomeBuildPlayspaceFrameMenuItem(DomeBuildPlayspaceFrame f)
		{
			setFont(MenuUtils.FONT12);
			frame = f;
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					frame.toFront();
				}
			});
			setText("Dome Playspace: " + frame.getGui().getTitle());

			((JComponent) frame.getGui()).addPropertyChangeListener(NameListener.NAME, titleListener);
		}

		protected class BrowserNameListener extends NameListener
		{
			public void nameChanged(String newName)
			{
				setText("Dome Playspace: " + newName);
			}
		}
	}


}
