package mit.cadlab.dome3.gui.deploy.deployProjectTemplate;

import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.deploy.components.DeployProjectInterfaceSelectionTable;
import mit.cadlab.dome3.gui.deploy.components.DeployIntegrationModelInterfaceSelectionTable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;


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
	private JLabel project;
	private JLabel type;
	private JLabel location;
	private JLabel description;

	private JPanel projectInterfaceChoicesCard = new JPanel();
	private JPanel iModelInterfaceChoicesCard = new JPanel();

	private DeployProjectTemplate data;
	private DeployProjectGui deployGui;


	public void setConfirmCard()
	{
		//server.setText(data.getServerConnection().getServerPort());
		if (data.isNewDeployment())
			type.setText("new deployment");
		else
			type.setText("redeployment");
		//location.setText(data.getLocationPath());
		project.setText(data.getLocalProjectPath());
		description.setText(data.getDescription());

		DeployProjectInterfaceSelectionTable pt = new DeployProjectInterfaceSelectionTable(data.getProjectData());
		pt.setTableEditable(false);
		pt.setEnabled(false);
		projectInterfaceChoicesCard.add("projectInterfaces", pt);
		((CardLayout2) (projectInterfaceChoicesCard.getLayout())).last(projectInterfaceChoicesCard);

		/*DeployIntegrationModelInterfaceSelectionTable mt = new DeployIntegrationModelInterfaceSelectionTable(data.getProjectData());
		mt.setTableEditable(false);
		mt.setEnabled(false);
		iModelInterfaceChoicesCard.add("modelInterfaces", mt);
		((CardLayout2) (iModelInterfaceChoicesCard.getLayout())).last(iModelInterfaceChoicesCard);   */
	}

	public ConfirmCard(DeployProjectTemplate deployData, DeployProjectGui gui)
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

		//JLabel serverLabel = Templates.makeLabel("server:");
		//server = Templates.makeLabel("");
		JLabel playspaceLabel = Templates.makeLabel("iProject:");
		project = Templates.makeLabel("");

		JLabel descriptionLabel = Templates.makeLabel("description:");
		description = Templates.makeLabel("");

		JLabel typeLabel = Templates.makeLabel("type:");
		type = Templates.makeLabel("");
		//JLabel locationLabel = Templates.makeLabel("location:");
		//location = Templates.makeLabel("server/path");

		JPanel projectPanel = makeProjectPanel();
		JPanel iModelPanel = makeModelPanel();
        JPanel fill = new JPanel();

		JComponent[] comps = {msg1, /*serverLabel, server, */playspaceLabel, project, descriptionLabel, description,
		                      typeLabel, type, /*locationLabel, location,*/ projectPanel, /*iModelPanel,*/ fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			//new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//new GridBagConstraints(1, 5, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 6, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),
			new GridBagConstraints(0, 7, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	private JPanel makeProjectPanel()
	{
		JPanel p = new JPanel();
		JLabel tableLabel = Templates.makeLabel("iProject interfaces being deployed:");
		projectInterfaceChoicesCard.setLayout(new CardLayout2());

		JComponent[] comps = {tableLabel, projectInterfaceChoicesCard};
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	private JPanel makeModelPanel()
	{
		JPanel p = new JPanel();
		JLabel tableLabel = Templates.makeLabel("iModel interfaces being deployed:");
		iModelInterfaceChoicesCard.setLayout(new CardLayout2());

		JComponent[] comps = {tableLabel, iModelInterfaceChoicesCard};
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0)
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
