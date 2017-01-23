package deployPlayspaceRev2;

import mit.cadlab.dome.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import deployPlayspace.DeployPlayspace;
import deployPlayspace.DeployPlayspaceGui;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 1, 2003
 * Time: 5:12:50 PM
 * To change this template use Options | File Templates.
 */
/**
 * This card is used in deploying models to determine what action to take next
 */
public class DecideNextCard extends JPanel
{
	private DeployPlayspace data;
	private DeployPlayspaceGui deployGui;


	public static final GridBagConstraints gbc = null;

	public DecideNextCard(DeployPlayspace deployData, DeployPlayspaceGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setDecideNextCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("What do you want to do now?", Templates.FONT12B);
		JLabel msg2 = Templates.makeLabel("Deploy another playspace on ...", Templates.FONT12B);

		JPanel fill = new JPanel();

		JComponent[] comps = {msg1, fill, msg2};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.VERTICAL, new Insets(5, 5, 5, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy playspace what's next card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DecideNextCard(null, null));
		f.show();
	}

}
