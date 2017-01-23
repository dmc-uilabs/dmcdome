package deployPlayspaceRev2;

import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.swing.CardLayout2;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import editPlayspace.EditPlayspaceTable;
import deployPlayspace.DeployPlayspace;
import deployPlayspace.DeployPlayspaceGui;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 9:11:00 PM
 * To change this template use Options | File Templates.
 */
/**
 * Card for confirming deployment settings
 */
public class ConfirmCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

	private JLabel server;
	private JLabel playspace;
	private JLabel type;
	private JLabel location;

	JPanel playspaceCard = new JPanel();

	private DeployPlayspace data;
	private DeployPlayspaceGui deployGui;


	public void setConfirmCard()
	{
		if (!(data.getPlayspaceTable()==null)) {
			playspaceCard.add("temp", data.getPlayspaceTable());
			((CardLayout2) playspaceCard.getLayout()).last(playspaceCard);
		}
		server.setText(data.getServerConnection().getServerPort());
		playspace.setText(data.getPlayspaceTable().getName());
		if (data.isNewDeployment())
			type.setText("new deployment");
		else
			type.setText("redeployment");
		location.setText(data.getLocationPath());
	}


	public ConfirmCard(DeployPlayspace deployData, DeployPlayspaceGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setConfirmCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Please review your deployment choices.", Templates.FONT12B);

		JLabel serverLabel = Templates.makeLabel("server:");
		server = Templates.makeLabel("dummyServer");
		JLabel modelLabel = Templates.makeLabel("playspace:");
		playspace = Templates.makeLabel("ps1");
		JLabel typeLabel = Templates.makeLabel("type:");
		type = Templates.makeLabel("create new playspace");
		JLabel locationLabel = Templates.makeLabel("location:");
		location = Templates.makeLabel("server/path");
		JLabel deployLabel = Templates.makeLabel("models and iProjects in playspace:");

		playspaceCard.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, serverLabel, server, modelLabel, playspace, typeLabel, type,
		                      locationLabel, location, deployLabel, playspaceCard};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(15, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(1, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy confirm card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ConfirmCard(null, null));
		f.show();
	}

}
