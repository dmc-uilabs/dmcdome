package PlayspaceBrowser;

import mit.cadlab.dome.swing.Templates;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Apr 1, 2003
 * Time: 11:40:24 AM
 * To change this template use Options | File Templates.
 */
public class TestButton extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(100, 100);
	public static final GridBagConstraints gbc = null;

	private JButton playPauseButton;
	private ImageIcon runIcon = Templates.makeImageIcon("mit/cadlab/dome/icons/run/running.gif");
	private ImageIcon pauseIcon = Templates.makeImageIcon("mit/cadlab/dome/icons/run/pause.gif");
	private ImageIcon runDownIcon = Templates.makeImageIcon("mit/cadlab/dome/icons/run/runningDown.gif");
	private ImageIcon pauseDownIcon = Templates.makeImageIcon("mit/cadlab/dome/icons/run/pauseDown.gif");
	private boolean runIconShowing = true;

	public TestButton()
	{
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		playPauseButton = Templates.makeImageButton(runIcon);
		playPauseButton.setPressedIcon(runDownIcon);
		playPauseButton.setOpaque(false);
		playPauseButton.setBorderPainted(false);
		playPauseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (runIconShowing) {
					runIconShowing = false;
					playPauseButton.setIcon(pauseIcon);
					playPauseButton.setPressedIcon(pauseDownIcon);
				}
				else {
					runIconShowing = true;
					playPauseButton.setIcon(runIcon);
					playPauseButton.setPressedIcon(runDownIcon);
				}
			}
		});

		JComponent[] comps = {playPauseButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Playspace1 on dummyServer");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new TestButton());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
