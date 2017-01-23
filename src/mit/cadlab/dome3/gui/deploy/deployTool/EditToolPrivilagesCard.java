package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Oct 21, 2003
 * Time: 11:53:59 AM
 * To change this template use Options | File Templates.
 */
public class EditToolPrivilagesCard extends JPanel
{
    public static final GridBagConstraints gbc = null;

    private JPanel _editPrivPanel = new JPanel();

    private DeployToolGui _deployToolGui;
    private DeployAnalysisTool _data;

    public void setEditPrivPanel()
	{
		String permissionCategory = PermissionUtils.ANALYSIS_TOOL_EDIT_PRIVILEGES;
		PermissionsPanel panel = null;

		if (_data.isNewDeployment())
        {
            panel = new PermissionsPanel(_data.getServerConnection(), permissionCategory, null, _data.getToolData().getName(), false, false);
            _editPrivPanel.add("edit permissions", panel.getGui());
            ((CardLayout2) _editPrivPanel.getLayout()).last(_editPrivPanel);
            _data.setEditPermissionsPanel(panel);
        }
        else if (PermissionFunctions.sessionUserHasPermission(_data.getServerConnection(), _data.getRedeployAnalysisToolId(), PermissionUtils.PERMISSION_TO_SET_ANALYSIS_TOOL_EDIT_PRIV))
        {
            ServerConnection svr = _data.getServerConnection();
            panel = new PermissionsPanel(svr, permissionCategory, _data.getRedeployAnalysisToolId(), _data.getToolData().getName(), true, false);
            _editPrivPanel.add("edit permissions", panel.getGui());
            ((CardLayout2) _editPrivPanel.getLayout()).last(_editPrivPanel);
            _data.setEditPermissionsPanel(panel);
        }
        else
        {
            JPanel p = new JPanel();
            JLabel msg = Templates.makeLabel("You do have not been given privileges to edit this permission set.");
            JPanel fill = new JPanel();
            JComponent[] comps = {msg, fill};
            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            GridBagConstraints[] gbcs = {
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
            };
            Templates.layoutGridBag(p, comps, gbcs);
            _editPrivPanel.add("edit permissions", p);
            ((CardLayout2) _editPrivPanel.getLayout()).last(_editPrivPanel);
        }


	}
    public EditToolPrivilagesCard(DeployAnalysisTool deployData, DeployToolGui gui)
	{
		_data = deployData;
		_deployToolGui = gui;
		_data.setEditPrivilagesCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who will be able to edit the analysis tool?", Templates.FONT12B);
		_editPrivPanel.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, _editPrivPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}
}
