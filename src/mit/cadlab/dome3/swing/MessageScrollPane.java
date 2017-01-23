// MessageScrollPane.java
// Copyright (c) 2002 Massachsetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * This ScrollPane provides hooks to control whether or not
 * autoscrolling occurs. It is assumed that the default behavior is
 * to autoscroll the component.
 * In order to do this efficiently, the scrollpane must have a
 * handle on the current window. We do this when the component
 * is instantiated with a native peer.
 */
public class MessageScrollPane extends JScrollPane
{

	protected boolean isWindowShowing = false;
	protected boolean isAutoScrollOn = true;
	protected boolean needsUpdating = false;

	public MessageScrollPane(MessageArea view)
	{
		super(view);
	}

	public MessageScrollPane(MessageArea view, int vsbPolicy, int hsbPolicy)
	{
		super(view, vsbPolicy, hsbPolicy);
	}

	public void setAutoScrollOn(boolean isAutoScrollOn)
	{
		this.isAutoScrollOn = isAutoScrollOn;
		((MessageArea) getViewport().getView()).freezeCaret(!isAutoScrollOn);
		if (isAutoScrollOn && needsUpdating)
			((MessageAreaViewport) getViewport()).scrollToEnd();
	}

	public boolean isAutoScrollOn()
	{
		return isAutoScrollOn;
	}

	public void addNotify()
	{
		super.addNotify();
		Window window = SwingUtilities.windowForComponent(this);
		window.addWindowListener(new ScrollPaneWindowListener());
		isWindowShowing = window.isShowing();
	}

	protected JViewport createViewport()
	{
		return new MessageAreaViewport();
	}

	protected class MessageAreaViewport extends JViewport
	{
		public MessageAreaViewport()
		{
			super();
		}

		protected void scrollToEnd()
		{
			Dimension viewSize = getViewSize();
			int totalHeight = viewSize.height;
			int screenHeight = getExtentSize().height;
			if (totalHeight > screenHeight) {
				Rectangle endOfDocument = new Rectangle(0, totalHeight - screenHeight - 1,
				                                        viewSize.width, screenHeight);
				scrollRectToVisible(endOfDocument);
			}
		}

		protected ViewListener createViewListener()
		{
			return new MessageAreaViewListener();
		}

		protected class MessageAreaViewListener extends JViewport.ViewListener
		{
			public void componentResized(ComponentEvent e)
			{
				if (isWindowShowing && isAutoScrollOn) {
					//super.componentResized(e);
					needsUpdating = false;
				} else {
					needsUpdating = true;
				}
			}
		}
	}

	protected class ScrollPaneWindowListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			isWindowShowing = false;
		}

		public void windowClosed(WindowEvent e)
		{
			isWindowShowing = false;
		}

		public void windowIconified(WindowEvent e)
		{
			isWindowShowing = false;
		}

		public void windowActivated(WindowEvent e)
		{
			if (!isWindowShowing) {
				isWindowShowing = true;
				if (isAutoScrollOn && needsUpdating)
					((MessageAreaViewport) getViewport()).scrollToEnd();
			}
		}
	}

}

