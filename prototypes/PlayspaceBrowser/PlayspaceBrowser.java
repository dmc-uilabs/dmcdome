package PlayspaceBrowser;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 25, 2003
 * Time: 2:51:44 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome.gui.playspace.PlayspaceRuntimeTable;
import mit.cadlab.dome.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is used to browse playspaces in run mode
 */
public class PlayspaceBrowser extends JPanel implements ActionListener
{
	public static final Dimension DEFAULT_SIZE = new Dimension(500, 400);
	public static final GridBagConstraints gbc = null;

	JButton closeButton;
	JButton submitButton;
	JToggleButton stopButton;
	JToggleButton pauseButton;
	JToggleButton resumeButton;

	private PlayspaceBrowser()
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
		closeButton = Templates.makeButton("close", this);
		submitButton = Templates.makeButton("submit");

		ImageIcon stopIcon = Templates.makeImageIcon("mit/cadlab/dome/icons/run/stop.gif");
		stopButton = Templates.makeImageToggleButton(stopIcon);

		ImageIcon pauseIcon = Templates.makeImageIcon("mit/cadlab/dome/icons/run/pause.gif");
		pauseButton = Templates.makeImageToggleButton(pauseIcon);

		ImageIcon resumeIcon = Templates.makeImageIcon("mit/cadlab/dome/icons/run/running.gif");
		resumeButton = Templates.makeImageToggleButton(resumeIcon);

		PlayspaceRuntimeTable table = new PlayspaceRuntimeTable();
		JScrollPane scrollPane = new JScrollPane(table.createPlayspaceTable());

		ButtonGroup runControls = new ButtonGroup();
		runControls.add(pauseButton);
		runControls.add(resumeButton);
		runControls.add(stopButton);

		JComponent[] comps = {scrollPane, submitButton, pauseButton, resumeButton, stopButton, closeButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 5, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 10, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 10, 0), 0, 0),
			new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 2, 10, 0), 0, 0),
			new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 10, 0), 0, 0),
			new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 10, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public void actionPerformed(ActionEvent event)
	{
		Object object = event.getSource();
		if (object == closeButton)
			System.exit(0);
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Playspace1 on dummyServer");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new PlayspaceBrowser());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}

}
