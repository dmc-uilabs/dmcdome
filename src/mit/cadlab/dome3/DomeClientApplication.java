// DomeClientApplication.java
// eventually split out to DomeClientBase.java and DomeClientApplet.java
package mit.cadlab.dome3;

import mit.cadlab.dome3.gui.bookmark.BookmarkCache;
import mit.cadlab.dome3.gui.guiutils.msg.Copyright;
import mit.cadlab.dome3.gui.guiutils.waitcursor.TransparantWindow;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiUtils;
import mit.cadlab.dome3.gui.menu.ApplicationMenuBar;
import mit.cadlab.dome3.gui.menu.MainMenuFrame;
import mit.cadlab.dome3.gui.mode.Modes;
import mit.cadlab.dome3.help.DHelp;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.FileUtils;

import javax.help.CSH;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.util.Date;
import java.io.File;


public class DomeClientApplication
{
	private static final boolean ALLOWED_TO_SKIP_COPYRIGHT = false;
	private static final String DOME_LICENSE_ACCEPTANCE_FILE = "DOME_license_acceptance.txt";

	private static final String appTitle = "DOME";
	private static DomeClientApplication app; // only one application per JVM
	private static int initialMode = 0;

	private static int clientPort = -1;
	public static boolean appletMode = false;
    public static boolean DOME_API_MODE = false;
	private static boolean initializedOnce = false;
    public static boolean DOME_API_BUILD_MODE = false;
    public static boolean DOME_SERVER_MODE = false;

    private static boolean _isRunningThroughDomeClient = false;  // needed to determine if the client is running or the models are being exectured through the API

	public static void start()
	{
		if (app == null) {
			if (!initializedOnce && shouldShowAcceptanceDialog()) {
				switch (Copyright.showAskAcceptanceDialog(null)) {
					case Copyright.ACCEPT_OPTION:
						if (ALLOWED_TO_SKIP_COPYRIGHT)
							writeAcceptanceRecord();
						break;
					default:
						if (appletMode)
							return;
						else
							System.exit(0);
				}
			}

            TransparantWindow splash= new TransparantWindow("mit/cadlab/dome3/icons/installerSplash.gif","loading...");
            splash.showStatus("Initializing DOME ........");

			if (!initializedOnce && !appletMode)
				DomeInit.initializeDOME();
            splash.showStatus("Creating Main Menubar......");
			app = new DomeClientApplication();
            splash.close();
            _isRunningThroughDomeClient = true;

		} else { // application exists
			app.getFocus();
		}
    }

	public static void startForApplet() {
		if (app == null) {
			appletMode = true;
			initialMode = 2;
			DomeClientApplication.start();
		}
		else {
			app.getFocus();
		}
	}

	public static boolean shouldShowAcceptanceDialog()
	{
		if (ALLOWED_TO_SKIP_COPYRIGHT) {
			File f = new File(".", DOME_LICENSE_ACCEPTANCE_FILE);
			if (f.exists() && f.isFile()) {
				return false; // todo: maybe check content of file?
			}
			else {
				return true;
			}
		}
		return true;
	}

	public static void writeAcceptanceRecord()
	{
		File f = new File(".", DOME_LICENSE_ACCEPTANCE_FILE);
		StringBuffer sb = new StringBuffer("");
		sb.append(new Date().toString());
		sb.append(FileUtils.ENDL);
		sb.append("DOME Terms of Use accepted");
		FileUtils.writeStringToFile(sb.toString(), f);
	}

	public static void setTitle(String title)
	{
		if (app != null)
			app.appFrame.setTitle(title);
	}

	public static void exit()
	{
		if (app != null) {
			BookmarkCache.saveBookmarks();
			app.close();
		}
		if (!appletMode)
			System.exit(0);
	}

	public static int getBottomCoordinate()
	{
		if (app == null)
			return 0;
		else
			return app.appFrame.getBottomCoordinate();
	}

	public static JFrame getMainFrame()
	{
		return app.appFrame;
	}

	// --- DomeClientApplication --------------------------------------------------
	private MainMenuFrame appFrame;
	private ApplicationMenuBar appMenuBar;

	public static int getMode()
	{
        if(app == null)
        {
            if (_isRunningThroughDomeClient)
                return Modes.API_MODE;
            else
                return Modes.SERVER;
        }
        else
		    return app.appMenuBar.getMode();
	}

	private DomeClientApplication()
	{ // must be created via method above
		// initialize servers?
		appMenuBar = new ApplicationMenuBar(makeHelpMenu());
		if (initialMode != appMenuBar.getMode()) // not default
			appMenuBar.setMode(initialMode);
		createMainMenuFrame();
		initializedOnce = true;
    }


	private void createMainMenuFrame()
	{
        //dealing with bookmark should be only called at client side
        appFrame = new MainMenuFrame(appTitle, appMenuBar);
		DHelp.enableHelp(appFrame.getRootPane(), DHelp.USING_DOME);
        BookmarkCache.loadBookmarks();

		appFrame.addWindowListener(new WindowAdapter()
		{
			public void windowDeiconified(WindowEvent e)
			{
				appMenuBar.showMode();
				appFrame.requestFocus();
			}

			public void windowIconified(WindowEvent e)
			{
				appMenuBar.hideMode();
			}

			public void windowClosing(WindowEvent e)
			{
				DomeClientApplication.exit();
       		}
		});

        //make a glasspane
        JPanel glassPane=new JPanel();
        glassPane.setOpaque(false);
        glassPane.setVisible(false);
        glassPane.setBounds(0,0,600,600);
        glassPane.addMouseListener(new MouseAdapter(){});

        appFrame.getRootPane().setGlassPane(glassPane);

		appFrame.setIconImage(Templates.makeImageIcon("mit/cadlab/dome3/icons/domeWindow.gif").getImage());
		appFrame.pack();
		appFrame.setVisible(true);
	}


	private JMenu makeHelpMenu()
	{
		JMenu m = MenuUtils.makeMenu("Help", DHelp.HELP_FEATURES);
		DHelp.enableHelp(m, DHelp.HELP_FEATURES);
		JMenuItem contextHelp = MenuUtils.makeMenuItem(new DHelp.ContextSensitiveHelpAction("point and click"));
		contextHelp.setAccelerator(DHelp.CSH_KEYSTROKE);

		m.add(MenuUtils.makeHelpMenuItem("DOME Help", DHelp.DOME_HELP));
		m.add(MenuUtils.makeHelpMenuItem("Tutorials", DHelp.TUTORIALS));
		m.add(contextHelp);

		m.addSeparator();
		if (modeHelp == null)
			modeHelp = new ModeHelpMenuItem();
		m.add(modeHelp);

		m.addSeparator();
		m.add(MenuUtils.makeHelpMenuItem("About DOME", DHelp.DOME_PROJECT));
		m.add(MenuUtils.makeHelpMenuItem("What's New", DHelp.WHATS_NEW));
		m.add(MenuUtils.makeMenuItem(new Copyright.ShowCopyrightAction("Copyright & Credits", null)));
		return m;
	}

	private void getFocus()
	{
		if (appFrame.getState() == Frame.ICONIFIED)
			appFrame.setState(Frame.NORMAL);
		appFrame.setVisible(true); // needed!
		appFrame.toFront();
		appFrame.requestFocus();
	}

	private void close()
	{
		// close all modes, destroy all windows and mainmenubar
		Modes.exitAllModes();
		if (!appletMode) {
			appFrame.dispose();
			app = null;
		}
	}

	// --- actions for menus and buttons --------------------
	public static final AbstractAction exitAction = new ExitAction("Exit");
	public static ModeHelpMenuItem modeHelp = null;

	public static class ExitAction extends AbstractAction
	{
		public ExitAction(String name)
		{
			super(name);
		}

		public void actionPerformed(ActionEvent e)
		{
			DomeClientApplication.exit();
		}
	}

	public static void main(String[] args)
	{
		int parsedArgs = 0;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-debug:")) {
				String debugLevelString = arg.substring(7);
				try {
					int debugLevel = Integer.parseInt(debugLevelString);
					Debug.setDebugLevel(debugLevel);
				}
				catch (NumberFormatException e) {
					System.err.println("Invalid debug level: " + debugLevelString);
				}
				parsedArgs++;
			}
			else if (arg.startsWith("-clientPort:")) {
				int clntPort = new Integer(arg.substring(12)).intValue();
				if (clntPort == 8080 || clntPort == 9001) {
					System.out.println("DomeClientApplication: invalid client port specified");
				}
				else {
					clientPort = clntPort;
				}
				parsedArgs++;
			}
			else if (arg.equalsIgnoreCase("-noCCL")) {
				CustomGuiUtils.USE_CUSTOM_JAR_LOADER = false;
			}
		}
		if ((args.length - parsedArgs) > 0) { // still args left to parse
			try {
				initialMode = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e) {
				System.out.println("usage: DomeClientApplication [modeId] [-clientPort:portNumber] [-noCCL] [-debug:level]\n" +
				                   "      build(0), deploy(1), run(2), server(3)\n" +
				                   "      default is build mode" +
				                   "      standard debug levels are 0,10,20,30,40,50 - default is 0 \n" +
				                   "      debug level must be last argument");
				System.exit(0);
			}
		}
		/*GregorianCalendar expirationDate = new GregorianCalendar(2002,11,4);
		System.out.println("This version of DOME expires on April 11, 2002");
		GregorianCalendar today = new GregorianCalendar();
		if (today.after(expirationDate)) {
		JOptionPane.showMessageDialog(null,
		"DOME version has expired. Please get new copy.");
		System.exit(0);
		}*/
		DomeClientApplication.start();
	}

	// mode help item
	public static class ModeHelpMenuItem extends JMenuItem
	{

		public ModeHelpMenuItem()
		{
			setFont(MenuUtils.FONT12);
			DHelp.enableHelpOnButton(this, DHelp.BUILD_MODE);
			setMode(0);
		}

		public void setMode(int mode)
		{
			switch (mode) {
				case 1:
					CSH.setHelpIDString(this, DHelp.DEPLOY_MODE);
					setText("Using Deploy Mode");
					break;
				case 2:
					CSH.setHelpIDString(this, DHelp.RUN_MODE);
					setText("Using Run Mode");
					break;
				case 3:
					CSH.setHelpIDString(this, DHelp.SERVER_MODE);
					setText("Using Server Mode");
					break;
				default:
					CSH.setHelpIDString(this, DHelp.BUILD_MODE);
					setText("Using Build Mode");
			}
		}
	}

	public static int getClientPort()
	{
		return clientPort != -1 ? clientPort : 9002 ;
	}

    public static boolean getIsRunningThroughDomeClient()
    {
        return _isRunningThroughDomeClient;
    }

    public static void setIsRunningThroughDomeClient(boolean isRunningThroughDomeClient)
    {
        _isRunningThroughDomeClient = isRunningThroughDomeClient;
    }

}
