package fordWebClient;

import mit.cadlab.dome3.swing.LayeredCenterLayout;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.ImageIcon;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 4:26:11 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card for the login step in deployment
 */
public class FordWebClient extends JPanel
{

	public static final GridBagConstraints gbc = null;

	public static final Color TEXT_PRESSED_COLOR = Color.RED;
	public static final Color TEXT_COLOR = Color.BLUE;
	public static final Color BACKGROUND_MOUSE_OVER = Color.WHITE;

	public static final Dimension DEFAULT_SIZE = new Dimension(600, 480);

    private JTextField usernameField;
    private JTextField passwordField;

    private JLabel inhouseBookmark;
	private JLabel supplierBookmark;
	private JLabel closingEffortBookmark;
	private JLabel sealDampingBookmark;
	private JLabel sealInterferenceBookmark;
	private JLabel bPillarBookmark;

	private JButton startClientButton;


	public FordWebClient()
	{
        JComponent[] comps = {makeLoginPanel(), makeBookmarksPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
		this.setPreferredSize(DEFAULT_SIZE);
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

	MouseListener m = new MouseListener()
	{
		public void mouseClicked(MouseEvent event)
		{
			System.out.println("clicked on label");
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
	};

	private void formatLabel(JLabel l){
		l.setForeground(TEXT_COLOR);
		l.setBackground(BACKGROUND_MOUSE_OVER);
		l.addMouseListener(m);
	}

	private JLayeredPane makeBookmarksPanel()
	{

		inhouseBookmark = Templates.makeLabel("Effort/Sealing tradeoff for inhouse seal design/FEA process", Templates.FONT12);
		formatLabel(inhouseBookmark);

		supplierBookmark = Templates.makeLabel("Effort/Sealing tradeoff for supplier seal design process", Templates.FONT12);
		formatLabel(supplierBookmark);

		closingEffortBookmark = Templates.makeLabel("Door closing effort tool",  Templates.FONT12);
		formatLabel(closingEffortBookmark);

		sealDampingBookmark = Templates.makeLabel("Seal damping coefficient calculator",  Templates.FONT12);
		formatLabel(sealDampingBookmark);

		sealInterferenceBookmark = Templates.makeLabel("Seal/body interference analysis tool", Templates.FONT12);
		formatLabel(sealInterferenceBookmark);

		bPillarBookmark = Templates.makeLabel("B pillar dynamic door deflection tool",  Templates.FONT12);
		formatLabel(bPillarBookmark);

		startClientButton = Templates.makeButton("start dome client");
		startClientButton.setForeground(TEXT_COLOR);
		startClientButton.setOpaque(false);

		JPanel t = new JPanel();
		JPanel i = new JPanel();

		ImageIcon icon = Templates.makeImageIcon("fordWebClient/octopus.gif");

		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);
		JComponent[] bcomps = {iconLabel};
		GridBagConstraints[] bgbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 2), 0, 0)};
		Templates.layoutGridBag(i, bcomps, bgbcs);
		//

		JComponent[] comps = {Templates.makeLabel("Windnoise bookmarks", Templates.FONT12B),
		                      inhouseBookmark,
		                      Templates.makeLabel("This tool takes 10-15 minutes to run the first time you submit changes. Subsequent runs take approximately 6-8 minutes."),
		                      supplierBookmark,
		                      Templates.makeLabel("This tool takes 5-8 minutes to run the first time you submit changes. Subsequent runs take approximately 2-3 minutes."),
		                      closingEffortBookmark,
		                      Templates.makeLabel("This tool takes 5-8 minutes to run the first time you submit changes. Subsequent runs take approximately 2-3 minutes."),
		                      sealDampingBookmark,
		                      Templates.makeLabel("This tool takes only a few seconds to run."),
		                      sealInterferenceBookmark,
		                      Templates.makeLabel("This tool takes 2-4 minutes to run the first time you submit changes. Subsequent run times depend on section spacing."),
		                      bPillarBookmark,
		                      Templates.makeLabel("This tool takes approximately 20-30 minutes to run."),
		                      Templates.makeLabel("Use the DOME client application"),
		                      startClientButton
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 8),
			new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 8),
			new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 8),
			new GridBagConstraints(0, 6, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 8),
			new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 9, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 8),
			new GridBagConstraints(0, 10, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			new GridBagConstraints(0, 11, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 8),
			new GridBagConstraints(0, 12, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

			new GridBagConstraints(1, 13, 1, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(10, 0, 9, 5), 0, 0),
			new GridBagConstraints(0, 13, 1, 1, 0.0, 1.0, gbc.SOUTHWEST, gbc.HORIZONTAL, new Insets(10, 0, 5, 5), 0, 0),
		};
		Templates.layoutGridBagB(t, comps, gbcs);

		// enable border to set size, then disable
		//copyrightTextArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
		//useTextArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
		t.setOpaque(false);

		JLayeredPane p = new JLayeredPane(); //here add the two panels i and t (t must be transparent)
		p.setLayout(new LayeredCenterLayout());
		p.add(t);
		p.add(i);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Ford Web Client Applet");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new FordWebClient());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
