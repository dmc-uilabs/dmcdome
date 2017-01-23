// MainMenuFrame.java
package mit.cadlab.dome3.gui.menu;

import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

// JFrame, JMenuBar

public class MainMenuFrame extends JFrame
{

	protected int bottomCoordinate = 0;

	public MainMenuFrame(String title, JMenuBar menubar)
	{
		super(title);
		setJMenuBar(menubar);
		setResizable(false);
		addComponentListener(new MainMenuFrameComponentListener());
	}

	// fix size of application
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}

	public Dimension getPreferredSize()
	{
		// Methods used to get width of the screen
        // Toolkit tk = getToolkit();
		// Dimension screen = tk.getScreenSize();

        // Method used to get width of model GUIs
        Dimension model = DomeBuildFrame.DEFAULT_SIZE;

		Dimension pref = super.getPreferredSize();

        // full screen width main menu bar
		// return new Dimension(screen.width, pref.height - 2);

        // model width main menu bar
        return new Dimension(model.width, pref.height - 2);
	}

	public int getBottomCoordinate()
	{
		if (bottomCoordinate == 0)
			setBottomCoordinate();
		return bottomCoordinate;
	}

	private void setBottomCoordinate()
	{
		try {
			Point p = getLocationOnScreen();
			Dimension d = getSize();
			bottomCoordinate = p.y + d.height;
		} catch (Exception ex) {
		}
	}

	public static void main(String[] args)
	{
		MainMenuFrame f1 = new MainMenuFrame("MainMenuFrame", new JMenuBar());
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f1.pack();
		f1.setVisible(true);
	}

	protected class MainMenuFrameComponentListener implements ComponentListener
	{
		public void componentHidden(ComponentEvent e)
		{
		}

		public void componentMoved(ComponentEvent e)
		{
			setBottomCoordinate();
		}

		public void componentResized(ComponentEvent e)
		{
			setBottomCoordinate();
		}

		public void componentShown(ComponentEvent e)
		{
		}
	}

}
