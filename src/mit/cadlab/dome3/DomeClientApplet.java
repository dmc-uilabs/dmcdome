// DomeClientApplet.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3;

import mit.cadlab.dome3.gui.bookmark.BookmarkFolder;
import mit.cadlab.dome3.gui.bookmark.BookmarkInfo;
import mit.cadlab.dome3.gui.bookmark.BookmarkCache;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiUtils;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.swing.LayeredCenterLayout;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.server.Debug;
import org.dom4j.Element;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DomeClientApplet extends JApplet
{
	public static final GridBagConstraints gbc = null;

	public static final Color TEXT_PRESSED_COLOR = Color.RED;
	public static final Color TEXT_COLOR = Color.BLUE;
	public static final Color BACKGROUND_MOUSE_OVER = Color.WHITE;

	public static final Dimension DEFAULT_SIZE = new Dimension(600, 480);

	private BookmarkLabelListener bookmarkListener = new BookmarkLabelListener();

	private String title = "DOME Bookmarks", bookmarkFile = "bookmarks.xml", svr_url = "";
	private BookmarkInfo[] bookmarks = {};

	private JTextField usernameField;
	private JPasswordField passwordField;

	private JButton startClientButton;
	private boolean loggedIn = false;

	private Boolean initCalled = Boolean.FALSE;
	private Boolean domeInitialized = Boolean.FALSE;

	public static boolean runAsApplet = false; // set to true when this is run as an applet

	public DomeClientApplet() throws HeadlessException
	{
		// for applet
	}

	public DomeClientApplet(String placeholder) throws HeadlessException
	{
		// for application
		loadBookmarks();
		new Thread(new DomeInitializer()).start();
	}

	//Called when this applet is loaded into the browser.
	public void init()
	{
		// Some browsers call init multiple times.
		synchronized (initCalled) {
			if (initCalled.booleanValue())
				return;
			initCalled = Boolean.TRUE;
		}
		DomeClientApplication.appletMode = true;
		BookmarkCache.useLocalBookmarkFile = false;
		DomeClientApplet.runAsApplet = true;
		loadAppletParameters();
		//Execute a job on the event-dispatching thread: creating this applet's GUI.

		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					loadBookmarks();
					showBookmarks();
					BookmarkCache.useLocalBookmarkFile = true; // for build mode
				}
			});
		}
		catch (Exception e) {
			System.err.println("error in init: " + e);
		}
		new Thread(new DomeInitializer()).start();
	}

	//Called to start the applet's execution.
	public void start()
	{
	}

	class DomeInitializer implements Runnable {
		public void run()
		{
			synchronized (domeInitialized) {
				DomeInit.initializeDOME();
				domeInitialized = Boolean.TRUE;
			}
		}

	}

	class BookmarkOpener implements Runnable
	{
		BookmarkInfo bookmark;

		public BookmarkOpener(BookmarkInfo bookmark)
		{
			this.bookmark = bookmark;
		}

		public void run()
		{
			bookmark.goBookmark();
		}
	}

	//Called to stop (temporarily or permanently) the applet's execution.
	public void stop()
	{
	}

	public String getAppletInfo()
	{
		return "Title: DOME Applet, 26 April 2004\n"
		        + "MIT CADLAB\n"
		        + "Web client for accessing DOME bookmarks.";
	}

	public String[][] getParameterInfo()
	{
		String[][] info = {
			{"title", "string", "title to go before bookmarks (default 'DOME Bookmarks')"},
			{"noCCL", "", "do not use custom class loader (default uses custom class loader)"},
			{"bookmark_file", "String", "bookmark file name (default is bookmarks.xml"},
		};
		return info;
	}

	protected void loadAppletParameters()
	{
		String title = getParameter("title");
		if (title != null)
			this.title = title;
		String noCCL = getParameter("noCCL");
		if (noCCL != null)
			CustomGuiUtils.USE_CUSTOM_JAR_LOADER = false;
		String bookmarkFile = getParameter("bookmark_file");
		if (bookmarkFile != null)
			this.bookmarkFile = bookmarkFile;
        String svr_url = getParameter("svr_url");
		if (svr_url != null)
			this.svr_url = svr_url;
	}

	private void loadBookmarks() {
		String xmlString = null;
		if (DomeClientApplication.appletMode) {
			try {
				InputStream in = getClass().getResourceAsStream("/" + bookmarkFile);
				if (in == null) {
					System.err.println("bookmarks file not found: " + bookmarkFile);
					bookmarks = new BookmarkInfo[]{};
					return;
				}

				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				xmlString = new String(buffer);
			}
			catch (java.io.IOException e) {
				System.err.println("Error reading bookmarks file: " + e); // should direct to client
			}
		} else {
			File bf = new File(bookmarkFile);
			try {
				xmlString = FileUtils.readTextFileAsString(bf);
			}
			catch (FileNotFoundException e) {
				System.err.println("Error reading bookmarks file: " + e); // should direct to client
			}
		}

		//Element xml = BookmarkCache.getBookmarkXml(bookmarkFile);
		if (xmlString == null)
			this.bookmarks = new BookmarkInfo[]{};
		else {
			Element xml = XMLUtils.stringToXmlElement(xmlString);
			ArrayList bookmarkList = new ArrayList();
			List foldersXml = xml.selectNodes("/bookmarkFolders/" + BookmarkFolder.XML_TAG);
			Element folderXml;
			for (int i = 0; i < foldersXml.size(); i++) {
				folderXml = (Element) foldersXml.get(i);
				bookmarkList.addAll(new BookmarkFolder(folderXml, false).getBookmarks());
			}
			this.bookmarks = (BookmarkInfo[]) bookmarkList.toArray(new BookmarkInfo[bookmarkList.size()]);
		}
	}

	public void goToBookmark(BookmarkInfo bookmark) {
		if (!isDomeInitialized()) {
			System.err.println("Can not open bookmark - DOME not initialized!");
			return;
		}
		if (!loggedIn) {
			if (!loginUser(bookmark.getServerURL()))
				return;
		}
		bookmark.goBookmark();
	}

	private synchronized boolean loginUser(String serverPort) { // just process one login at a time
		if (loggedIn)
			return true;
		int sepIndex = serverPort.indexOf("$");
		if (sepIndex != -1)
			serverPort = serverPort.substring(0,sepIndex);
		String userName = usernameField.getText();
		char[] pwdChars = passwordField.getPassword();
		String password = new String(pwdChars);
		// clear password char array
		for (int i = 0; i < pwdChars.length; ++i)
			pwdChars[i] = '0';
		byte[] encryptedPwd = LoginUtils.encryptPassword(password);
		try {
			LoginUtils.login(LoginUtils.USER, userName, svr_url.equals("") ? RunMode.getClientUrl() : svr_url,
                    serverPort, encryptedPwd);
			loggedIn = true;
			return true;
		}
		catch (Exception e) {
			System.err.println(e);
			LoginPrompt.handleLoginError(e, null);
			return false;
		}
	}

	private void showBookmarks() {
		JPanel mainPanel = makeMainPanel();
		getContentPane().add(mainPanel); // add login and bookmark screen
	}

	private JPanel makeMainPanel()
	{
		JPanel p = new JPanel();
		JComponent[] comps = {makeLoginPanel(), makeBookmarksPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setPreferredSize(DEFAULT_SIZE);
		return p;
	}

	protected JPanel makeLoginPanel()
	{
		JPanel p = new JPanel();
		usernameField = Templates.makeTextField("");
		passwordField = new JPasswordField(25);

		JComponent[] comps = {Templates.makeLabel("Login to DOME", Templates.FONT12B),
		                      Templates.makeLabel("user name:"),
		                      usernameField,
		                      Templates.makeLabel("password:"),
		                      passwordField,
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {// configured with 5 pixel borders around
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 10, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);
		return p;
	}

	private JLayeredPane makeBookmarksPanel()
	{
		JPanel backgroundPanel = new JPanel();
		ImageIcon icon = Templates.makeImageIcon("mit/cadlab/dome3/icons/copyrightBackground.gif");
		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);
		JComponent[] bcomps = {iconLabel};
		GridBagConstraints[] bgbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 2), 0, 0)};
		Templates.layoutGridBag(backgroundPanel, bcomps, bgbcs);

		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);

		ArrayList compList = new ArrayList();
		ArrayList constraints = new ArrayList();

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady

		// add title
		compList.add(Templates.makeLabel(title, Templates.FONT12B));
		constraints.add(new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, -2, 0), 0, 0));

		// add bookmarks
		int row = 1;
		for (int i = 0; i < bookmarks.length; i++) {
			compList.add(new BookmarkLabel(bookmarks[i]));
			compList.add(makeCommentLabel(bookmarks[i]));
			constraints.add(new GridBagConstraints(0, row++, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 8));
			constraints.add(new GridBagConstraints(0, row++, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		}

		// add button for starting DOME client
		startClientButton = Templates.makeButton("start DOME client");
		startClientButton.setForeground(TEXT_COLOR);
		startClientButton.setOpaque(false);
		startClientButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!isDomeInitialized()) {
					System.err.println("Can not start client - DOME not initialized!");
					return;
				}
				DomeClientApplication.startForApplet();
			}
		});
		compList.add(startClientButton);
		compList.add(Templates.makeLabel("Use the DOME client application"));
		constraints.add(new GridBagConstraints(0, row, 1, 1, 0.0, 1.0, gbc.SOUTHWEST, gbc.HORIZONTAL, new Insets(10, 0, 5, 5), 0, 0));
		constraints.add(new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(10, 0, 9, 5), 0, 0));

		JComponent[] comps = (JComponent[])compList.toArray(new JComponent[compList.size()]);
		GridBagConstraints[] gbcs = (GridBagConstraints[]) constraints.toArray(new GridBagConstraints[constraints.size()]);
		Templates.layoutGridBagB(contentPanel, comps, gbcs);

		JLayeredPane p = new JLayeredPane();
		p.setLayout(new LayeredCenterLayout()); // supports overlaying two panels
		p.add(contentPanel); // first panel is transparent
		p.add(backgroundPanel); // second panel is not transparent
		return p;
	}

	private JLabel makeCommentLabel(BookmarkInfo bookmark)
	{
		return Templates.makeLabel(bookmark.getComment());
	}

	class BookmarkLabel extends JLabel {
		private BookmarkInfo bookmark;

		public BookmarkLabel(BookmarkInfo bookmark) {
			super(bookmark.getAliasname());
			this.bookmark = bookmark;
			setFont(Templates.FONT12);
			setForeground(TEXT_COLOR);
			setBackground(BACKGROUND_MOUSE_OVER);
			addMouseListener(bookmarkListener);
		}
	}

	private boolean isDomeInitialized() {
		synchronized (domeInitialized) {
			return domeInitialized.booleanValue();
		}
	}
	class BookmarkLabelListener implements MouseListener
	{
		public void mouseClicked(MouseEvent event)
		{
			BookmarkLabel bl = (BookmarkLabel) event.getSource();
			DomeClientApplet.this.goToBookmark(bl.bookmark);
		}

		public void mousePressed(MouseEvent event)
		{
			JLabel l = (JLabel) event.getSource();
			l.setForeground(TEXT_PRESSED_COLOR);
		}

		public void mouseReleased(MouseEvent event)
		{
			JLabel l = (JLabel) event.getSource();
			l.setForeground(TEXT_COLOR);
		}

		public void mouseEntered(MouseEvent event)
		{
			JLabel l = (JLabel) event.getSource();
			l.setOpaque(true);
			l.repaint();
		}

		public void mouseExited(MouseEvent event)
		{
			JLabel l = (JLabel) event.getSource();
			l.setOpaque(false);
			l.repaint();
		}
	}

	public static void main(String[] args)
	{
		Debug.setDebugLevel(Debug.ALL);
		DomeClientApplet app = new DomeClientApplet("bookmarks.xml"); // loads bookmarks
		JFrame f = new JFrame(app.title);
		JPanel p = app.makeMainPanel();
		f.getContentPane().add(p);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				DomeClientApplication.exit();
				System.exit(0);
			}
		});
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
