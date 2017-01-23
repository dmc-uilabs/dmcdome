// DeployMode.java
package mit.cadlab.dome3.gui.mode.deploy;

import mit.cadlab.dome3.gui.deploy.deployModel.DeployModel;
import mit.cadlab.dome3.gui.deploy.deployPlayspace.DeployPlayspace;
import mit.cadlab.dome3.gui.deploy.deployProject.DeployProject;
import mit.cadlab.dome3.gui.deploy.deployTool.DeployAnalysisTool;
import mit.cadlab.dome3.gui.deploy.deployProjectTemplate.DeployProjectTemplate;
import mit.cadlab.dome3.gui.deploy.deployTemplateModel.DeployModelTemplate;
import mit.cadlab.dome3.gui.mode.Mode;
import mit.cadlab.dome3.swing.DefaultWindowTracker;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DeployMode implements Mode
{

	public static final String NAME = "Deploy";
	private static DefaultWindowTracker windows = new DeployModeWindowTracker();

	private static DeployMode.DeployModeFrame deploymodelframe = null;
    private static DeployMode.DeployModeFrame deployToolFrame = null;
	private static DeployMode.DeployModeFrame deployPlayspaceframe = null;
	private static DeployMode.DeployModeFrame deployProjectframe = null;
    private static DeployMode.DeployModeFrame deployProjectTemplateframe = null;
    private static DeployMode.DeployModeFrame deployModelTemplateframe = null;


	// Mode interface
	public static String name()
	{
		return NAME;
	}

	public static void show()
	{
		windows.showAll();
	}

	public static void hide()
	{
		windows.hideAll();
	}

	public static void exit()
	{
		//closeAll();
	}

	public static void close()
	{
		windows.closeAll();
	}

	public static Point getWindowLocation()
	{
		// the following code will be called when the deployplayspace window also exist,
		// to determine the window location
		if (deploymodelframe != null && deploymodelframe.isShowing()) {
			Point p = deploymodelframe.getLocationOnScreen();
			return new Point(p.x + 25, p.y + 25);
		} else if (deployPlayspaceframe != null && deployPlayspaceframe.isShowing()) {
			Point p = deployPlayspaceframe.getLocationOnScreen();
			return new Point(p.x + 25, p.y + 25);
		} else if (deployProjectframe != null && deployProjectframe.isShowing()) {
			Point p = deployProjectframe.getLocationOnScreen();
			return new Point(p.x + 25, p.y + 25);
		}

		return new Point(0, DomeClientApplication.getBottomCoordinate());
	}

    public static void deployCatalog() {
        deployModel();
        deploymodelframe.setTitle("Deploy catalog");
    }

	public static void deployModel()
	{
		if (deploymodelframe != null) {
			if (deploymodelframe.getState() == Frame.ICONIFIED)
				deploymodelframe.setState(Frame.NORMAL);
			deploymodelframe.show();
			return;
		}
		DeployModel dm = new DeployModel();
		deploymodelframe = new DeployModeFrame("Deploy model", dm.getGui());
		deploymodelframe.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());
		deploymodelframe.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				deploymodelframe = null;
			}
		});
		deploymodelframe.addWindowListener(dm.getGui().getWindowAdapter());
		deploymodelframe.pack();
		deploymodelframe.show();

	}

    public static void deployAnalysisTool()
    {
        if (deployToolFrame != null)
        {
            if (deployToolFrame.getState() == Frame.ICONIFIED)
                    deployToolFrame.setState(Frame.NORMAL);
            deployToolFrame.show();
            return;
        }

        DeployAnalysisTool dt = new DeployAnalysisTool();
        deployToolFrame = new DeployModeFrame("Deploy Analysis Tool", dt.getGui());
        deployToolFrame.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());
        deployToolFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent e)
            {
                deployToolFrame = null;
            }
        });
        deployToolFrame.addWindowListener(dt.getGui().getWindowAdapter());
        deployToolFrame.pack();
        deployToolFrame.show();
    }

	public static void deployPlayspace()
	{
		if (deployPlayspaceframe != null) {
			if (deployPlayspaceframe.getState() == Frame.ICONIFIED)
				deployPlayspaceframe.setState(Frame.NORMAL);
			deployPlayspaceframe.show();
			return;
		}
		DeployPlayspace dm = new DeployPlayspace();
		deployPlayspaceframe = new DeployModeFrame("Deploy Playspace", dm.getGui());
		deployPlayspaceframe.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());
		deployPlayspaceframe.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				deployPlayspaceframe = null;
			}
		});
		deployPlayspaceframe.addWindowListener(dm.getGui().getWindowAdapter());
		deployPlayspaceframe.pack();
		deployPlayspaceframe.show();
	}

	public static void deployProject()
	{
		if (deployProjectframe != null) {
			if (deployProjectframe.getState() == Frame.ICONIFIED)
				deployProjectframe.setState(Frame.NORMAL);
			deployProjectframe.show();
			return;
		}
		DeployProject dm = new DeployProject();
		deployProjectframe = new DeployModeFrame("Deploy Project", dm.getGui());
		deployProjectframe.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());
		deployProjectframe.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				deployProjectframe = null;
			}
		});
		deployProjectframe.addWindowListener(dm.getGui().getWindowAdapter());
		deployProjectframe.pack();
		deployProjectframe.show();
	}

    private static void deployProjectTemplate()
    {
    		if (deployProjectTemplateframe != null) {
			if (deployProjectTemplateframe.getState() == Frame.ICONIFIED)
				deployProjectTemplateframe.setState(Frame.NORMAL);
			deployProjectTemplateframe.show();
			return;
		}
		DeployProjectTemplate dm = new DeployProjectTemplate();
		deployProjectTemplateframe = new DeployModeFrame("Deploy Project Template", dm.getGui());
		deployProjectTemplateframe.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());
		deployProjectTemplateframe.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				deployProjectTemplateframe = null;
			}
		});
		deployProjectTemplateframe.addWindowListener(dm.getGui().getWindowAdapter());
		deployProjectTemplateframe.pack();
		deployProjectTemplateframe.show();
    }

    private static void deployModelTemplate()
    {
        if (deployModelTemplateframe != null) {
		    if (deployModelTemplateframe.getState() == Frame.ICONIFIED)
		        deployModelTemplateframe.setState(Frame.NORMAL);
	        deployModelTemplateframe.show();
		    return;
		}
		DeployModelTemplate dm = new DeployModelTemplate();
		deployModelTemplateframe = new DeployModeFrame("Deploy Model Template", dm.getGui());
		deployModelTemplateframe.setIconImage(Templates.makeImageIcon(DomeIcons.WINDOW).getImage());
		deployModelTemplateframe.addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				deployModelTemplateframe = null;
			}
		});
		deployModelTemplateframe.addWindowListener(dm.getGui().getWindowAdapter());
		deployModelTemplateframe.pack();
		deployModelTemplateframe.show();
    }

	// --- actions for menus and buttons --------------------
	public static AbstractAction closeAllAction = new AbstractAction("Close all")
	{
		public void actionPerformed(ActionEvent e)
		{
			DeployMode.close();
		}
	};

	public static AbstractAction DeployModelAction = new AbstractAction("Model ...")
	{
		public void actionPerformed(ActionEvent e)
		{
			DeployMode.deployModel();
		}
	};

    public static AbstractAction DeployToolAction = new AbstractAction("Analysis Tool ...")
    {
        public void actionPerformed(ActionEvent e)
        {
            DeployMode.deployAnalysisTool();
        }
    };

	public static AbstractAction DeployCatalogAction = new AbstractAction("Catalog ...")
	{
		public void actionPerformed(ActionEvent e)
		{
			DeployMode.deployCatalog();
		}
	};


	public static AbstractAction DeployPlayspaceAction = new AbstractAction("Playspace ...")
	{
		public void actionPerformed(ActionEvent e)
		{
			DeployMode.deployPlayspace();
		}
	};

	public static AbstractAction DeployProjectAction = new AbstractAction("Project ...")
	{
		public void actionPerformed(ActionEvent e)
		{
			DeployMode.deployProject();
		}
	};

    public static AbstractAction DeployProjectTemplateAction = new AbstractAction("Project Template...")
    {
        public void actionPerformed(ActionEvent e)
        {
            DeployMode.deployProjectTemplate();
        }
    };

    public static AbstractAction DeployModelTemplateAction = new AbstractAction("Model Template...")
    {
        public void actionPerformed(ActionEvent e)
        {
            DeployMode.deployModelTemplate();
        }
    };



	private static class DeployModeWindowTracker extends DefaultWindowTracker
	{
		public void closeAll()
		{
			// traverse vector backwards since closing may be destructive to vector
			int i = children.size() - 1;
			for (; i >= 0; --i) {
				Object obj = children.get(i);
				if (obj instanceof Window)
					((Window) obj).dispose();
			}
		}
	}

	public static class DeployModeFrame extends JFrame
	{
		JComponent gui = null;

		public DeployModeFrame(String title, JComponent gui) throws HeadlessException
		{
			super(title);
			this.gui = gui;
			getContentPane().add(gui);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent e)
				{
					DeployMode.windows.removeChildWindow(DeployModeFrame.this);
				}

				public void windowActivated(WindowEvent e)
				{
					DeployMode.windows.notifyInFront(DeployModeFrame.this);
				}
			});
			setLocation(getWindowLocation());
		}

		public JComponent getContentPanel()
		{
			return gui;
		}

		protected Point getWindowLocation()
		{
			return DeployMode.getWindowLocation();
		}
	}
}
