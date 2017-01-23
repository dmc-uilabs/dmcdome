package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceSelectionTable;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployToolInterfaceSelectionTable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Oct 27, 2003
 * Time: 6:51:38 PM
 * To change this template use Options | File Templates.
 */
public class ConfirmAnalysisToolCard extends JPanel
{
    public static final GridBagConstraints gbc = null;

    private JLabel _server;
    private JLabel _analysisTool;
    private JLabel _type;
    private JLabel _location;
    private JLabel _description;

    private JPanel _interfaceChoicesCard = new JPanel();

    private DeployAnalysisTool _data;
    private DeployToolGui _deployGui;

    public void setConfirmCard()
	{
		_server.setText(_data.getServerConnection().getServerPort());
		_analysisTool.setText(_data.getLocalAnalysisToolPath());
		_description.setText(_data.getDescription());
		if (_data.isNewDeployment())
			_type.setText("new deployment");
		else
			_type.setText("redeployment");
		_location.setText(_data.getServerLocationPath());
		if (((CardLayout2) _interfaceChoicesCard.getLayout()).getActiveComponent() != null)
			_interfaceChoicesCard.remove(0);
		DeployToolInterfaceSelectionTable t = new DeployToolInterfaceSelectionTable(_data.getToolData());
		t.setTableEditable(false);
		t.setEnabled(false);
		_interfaceChoicesCard.add("interfaceChoices", t);
	}

    public ConfirmAnalysisToolCard(DeployAnalysisTool data, DeployToolGui deployGui)
    {
        _data = data;
        _deployGui = deployGui;

        _data.setConfirmCard(this);

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
		_server = Templates.makeLabel("");
		JLabel modelLabel = Templates.makeLabel("analysis tool:");
		_analysisTool = Templates.makeLabel("");
		JLabel descriptionLabel = Templates.makeLabel("description:");
		_description = Templates.makeLabel("");

		JLabel typeLabel = Templates.makeLabel("type:");
		_type = Templates.makeLabel("");
		JLabel locationLabel = Templates.makeLabel("location:");
		_location = Templates.makeLabel("");
		JLabel deployLabel = Templates.makeLabel("interfaces being deployed:");

		_interfaceChoicesCard.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, serverLabel, _server, modelLabel, _analysisTool, descriptionLabel,
                              _description, typeLabel, _type, locationLabel, _location, deployLabel, _interfaceChoicesCard};

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
			new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(15, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 7, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(1, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
    }
}
