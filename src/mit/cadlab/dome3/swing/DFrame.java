// DFrame.java
package mit.cadlab.dome3.swing;

import java.awt.Image;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JFrame;

public class DFrame extends JFrame implements IconImageWindowTracker, Closeable
{

	protected WindowTracker parent = null, children;
	protected Image iconImage = null;
	protected boolean isIconPropagated = true;

	public DFrame()
	{
		super();
		_configure(null);
	}

	public DFrame(String title)
	{
		super(title);
		_configure(null);
	}

	public DFrame(WindowTracker parent)
	{
		super();
		_configure(parent);
	}

	public DFrame(String title, WindowTracker parent)
	{
		super(title);
		_configure(parent);
	}

	private void _configure(WindowTracker parent)
	{
		setWindowTracker(parent);
		children = createChildWindowTracker();
		addWindowListener(new WindowAdapter()
		{
			public void windowActivated(WindowEvent e)
			{
				if (DFrame.this.parent != null)
					DFrame.this.parent.notifyInFront(DFrame.this);
			}

			public void windowClosed(WindowEvent e)
			{

			}

			public void windowClosing(WindowEvent e)
			{

			}

			public void windowDeactivated(WindowEvent e)
			{

			}

			public void windowDeiconified(WindowEvent e)
			{
				showAll();
			}

			public void windowIconified(WindowEvent e)
			{
				hideAll();
			}

			public void windowOpened(WindowEvent e)
			{

			}
		});
	}

	protected WindowTracker createChildWindowTracker()
	{
		return new DefaultWindowTracker();
	}

	protected void setWindowTracker(WindowTracker parent)
	{
		if (this.parent != null)
			throw new UnsupportedOperationException("DFrame.setWindowTracker - parent already set!");
		if (parent == null) return;
		this.parent = parent;
		if (parent instanceof IconImageWindowTracker) {
			iconImage = ((IconImageWindowTracker) parent).getChildIconImage();
			setIconImage(iconImage);
		}
	}

	public void setIconPropagated(boolean isIconPropagated)
	{
		this.isIconPropagated = isIconPropagated;
	}

	public void removeNotify()
	{
		super.removeNotify();
		if (parent != null)
			parent.removeChildWindow(this);
	}

	public void hide()
	{
		hideAll();
		super.hide();
	}

	public void show()
	{
		super.show();
		showAll();
	}

	public void close()
	{
		closeAll();
		try
        {
            dispose();
        }
        catch(Exception e)
        {
            System.err.println(e);
        }
	}

	public void toFront()
	{
		super.toFront();
		if (DFrame.this.parent != null)
			DFrame.this.parent.notifyInFront(this);
	}

	// IconImageWindowTracker
	public Image getChildIconImage()
	{
		if (isIconPropagated) {
			return iconImage;
		} else {
			if (parent instanceof IconImageWindowTracker) {
				return ((IconImageWindowTracker) parent).getChildIconImage();
			} else {
				return null;
			}
		}
	}

	// WindowTracker shadowing
	public List getChildren()
	{
		return children.getChildren();
	}

	public void notifyInFront(Window w)
	{
		children.notifyInFront(w);
	}

	public void removeChildWindow(Window w)
	{
		children.removeChildWindow(w);
	}

	public void hideAll()
	{
		children.hideAll();
	}

	public void showAll()
	{
		children.showAll();
	}

	public void closeAll()
	{
		children.closeAll();
	}

}
