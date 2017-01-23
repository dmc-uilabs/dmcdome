package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelComboBoxModel;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolInterfaceData;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Oct 26, 2003
 * Time: 4:10:13 PM
 * To change this template use Options | File Templates.
 */
public class SetToolInterfacePrivilegesCard extends JPanel
{
    public static final GridBagConstraints gbc = null;

    public static final String INTERFACES_AVAILABLE = "yes";
    public static final String NO_INTERFACES_AVAILABLE = "no";
    public static final String NO_INTERFACES_PERMISSONS = "permissions";
    public static final String NOT_YET_IMPLEMENTED = "not yet implemented";

    private DeployAnalysisTool _data;
    private DeployToolGui _deployGui;

    private JPanel _interfaceUsePrivilegesPanel = new JPanel(); //the main panel that has cards with combo box for selecting interaces

    //or the no interfaces available card
    private JPanel _interfacesCardPanel = new JPanel(); //the card panel used to store the permissions panels for the different intefaces

    private ServerPanelComboBoxModel _interfaceModel;
    private JComboBox _interfaceCombo;

    public void setUsePrivilegesPanel()
    {
        if (_data.getToolData().getNumAvailable() > 0)
        {

            String permissionCategory = PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES;
            ServerConnection svr = _data.getServerConnection();
            PermissionsPanel p = null;

            DeployAnalysisToolInterfaceData[] intData =
                    (DeployAnalysisToolInterfaceData[]) (_data.getToolData().
                    getToolInterfaces().toArray(new DeployAnalysisToolInterfaceData[]{}));

            ((CardLayout2) _interfaceUsePrivilegesPanel.getLayout()).show(_interfaceUsePrivilegesPanel, INTERFACES_AVAILABLE);
            resetInterfacePriv();

            if (_data.isNewDeployment())
            {
                for (int i = 0; i < intData.length; i++)
                {
                    p = new PermissionsPanel(svr, permissionCategory, null, intData[i].getName(), false, false);
                    _data.getAnalysisToolInterfacePermissions().put(Integer.toString(i), p);

//					 only add interface to combo box if they are avaiable
                    if ((intData[i]).getIsAvailable().booleanValue())
                    {
                        _interfacesCardPanel.add(Integer.toString(i), p.getGui());
                        _interfaceModel.addEntry(intData[i].getName(), Integer.toString(i));
                    }
                }
                _interfaceCombo.setSelectedIndex(0);
                ((CardLayout2) _interfacesCardPanel.getLayout()).first(_interfacesCardPanel);
                _deployGui.successfulCompletion();
                return;
            }
            else if (PermissionFunctions.sessionUserHasPermission(svr, _data.getRedeployAnalysisToolId(), PermissionUtils.PERMISSION_TO_SET_INTERFACE_USE_PRIV))
            {
                for (int i = 0; i < intData.length; i++)
                {
                    String deployedId = (intData[i]).getDeployId();
                    if (deployedId != null)
                        p = new PermissionsPanel(svr, permissionCategory, deployedId, intData[i].getName(), true, false);
                    else
                    {
                        //this should never, ever happen
                        System.out.println("error in SetInterfacePrivCard: deployed Id for interface " + intData[i].getName() + " is null");
                        p = new PermissionsPanel(svr, permissionCategory, null, intData[i].getName(), false, false); //stuffing a new panel in so that things can go on
                    }
                    _data.getAnalysisToolInterfacePermissions().put(Integer.toString(i), p);
                    //only add the available ones to the combo box
                    if ((intData[i]).getIsAvailable().booleanValue())
                    {
                        _interfacesCardPanel.add(Integer.toString(i), p.getGui());
                        _interfaceModel.addEntry(intData[i].getName(), Integer.toString(i));
                    }
                }
                _interfaceCombo.setSelectedIndex(0);
                ((CardLayout2) _interfacesCardPanel.getLayout()).first(_interfacesCardPanel);
            }
            else
            { //do not have permission to set interface use privs.
                ((CardLayout2) _interfaceUsePrivilegesPanel.getLayout()).show(_interfaceUsePrivilegesPanel, NO_INTERFACES_PERMISSONS);
            }
        }
        else
        {
            ((CardLayout2) _interfaceUsePrivilegesPanel.getLayout()).show(_interfaceUsePrivilegesPanel, NO_INTERFACES_AVAILABLE);
        }
    }

    public void resetInterfacePriv()
	{
		_interfacesCardPanel.removeAll();
		_interfaceModel.removeAllElements();
		if (!((_data.getAnalysisToolInterfacePermissions() == null) || (_data.getAnalysisToolInterfacePermissions().isEmpty()))) {
			_data.getAnalysisToolInterfacePermissions().clear();
		}

	}

    public SetToolInterfacePrivilegesCard(DeployAnalysisTool data, DeployToolGui deployGui)
    {
        _data = data;
        _deployGui = deployGui;

        _data.setToolInterfaceUsePrivilegesCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
    }

    private JPanel makePanel()
    {
        JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who may use the analysis tool interfaces?", Templates.FONT12B);

		_interfaceUsePrivilegesPanel.setLayout(new CardLayout2());
		_interfaceUsePrivilegesPanel.add(INTERFACES_AVAILABLE, makeInterfacesPanel());
		_interfaceUsePrivilegesPanel.add(NO_INTERFACES_AVAILABLE, makeNoInterfacesPanel());
		_interfaceUsePrivilegesPanel.add(NO_INTERFACES_PERMISSONS, makeNoPermissionsPanel());

		JComponent[] comps = {msg1, _interfaceUsePrivilegesPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
    }

    private JPanel makeInterfacesPanel()
	{
		JPanel p = new JPanel();
		_interfacesCardPanel.setLayout(new CardLayout2());
		_interfaceModel = new ServerPanelComboBoxModel();
		_interfaceCombo = Templates.makeDComboBox(_interfaceModel);
		_interfaceCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
                {
                    ((CardLayout2) _interfacesCardPanel.getLayout()).show(_interfacesCardPanel, _interfaceModel.getSelectedValue());
                }
                catch (Exception interfaceCombo)
                {
                    System.out.println("invalid key in combo box");
                }
			}
		});

		Templates.setFixedSize(_interfaceCombo, new Dimension(230, 20));

		JComponent[] comps = {_interfaceCombo, _interfacesCardPanel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

    private JPanel makeNoInterfacesPanel()
    {
        JPanel p = new JPanel();
		JLabel msg = Templates.makeLabel("No analysis tool interfaces are available.");
		JPanel fill = new JPanel();

		JComponent[] comps = {msg, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
    }

    private JPanel makeNoPermissionsPanel()
    {
        JPanel p = new JPanel();
		JLabel msg = Templates.makeLabel("You have not been given privileges to edit this permission set.");
		JPanel fill = new JPanel();

		JComponent[] comps = {msg, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
    }
}
