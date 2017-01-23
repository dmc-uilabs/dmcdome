package mit.cadlab.dome3.gui.deploy.deployModel;

import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelComboBoxModel;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 8:40:18 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card used to set interface priviledes during deployment
 */
public class SetInterfacePrivCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

	private ServerPanelComboBoxModel interfaceModel;
	private JComboBox interfaceCombo;
	private JPanel interfacePrivCard = new JPanel();
	DeployModel data;
	DeployModelGui deployGui;

	public void resetInterfacePriv()
	{
		if (!((data.getInterfacePermissions() == null) || (data.getInterfacePermissions().isEmpty()))) {
			data.getInterfacePermissions().clear();
			data.setInterfacePermissionsCreated(false);
			int size = interfaceCombo.getItemCount();

			//empty the card layout
			CardLayout2 layout = (CardLayout2) interfacePrivCard.getLayout();
			for (int i = 0; i < size; i++) {
				interfaceCombo.setSelectedIndex(i);
				layout.remove(interfacePrivCard, interfaceModel.getSelectedValue());
			}
			interfaceModel.removeAllElements();
		}
	}

	/**
	 * called after a successful deployment before starting the next round of deployment
	 */
	public void initGui()
	{
		resetInterfacePriv();
	}

	public void setInterfacePermPanel()
	{
		String permissionCategory = PermissionUtils.MODEL_IPROJECT_INTERFACE_USE_PRIVILEGES;
		ServerConnection svr = data.getServerConnection();

		DeployInterfaceData[] intData =
		        (DeployInterfaceData[]) (data.getModelData().getModelInterfaces().toArray(new DeployInterfaceData[]{}));

		if (!data.isInterfacePermissionsCreated()) {
			PermissionsPanel p = null;
			interfacePrivCard.removeAll();
			interfaceModel.removeAllElements();
			if (data.getNewDeployment()) {
				for (int i = 0; i < intData.length; i++) {
					p = new PermissionsPanel(svr, permissionCategory, null, intData[i].getName(), false, false);
					data.getInterfacePermissions().put(Integer.toString(i), p);
					// only add interface to combo box if they are avaiable
					if ((intData[i]).getIsAvailable().booleanValue()) {
						interfacePrivCard.add(Integer.toString(i), p.getGui());
						interfaceModel.addEntry(intData[i].getName(), Integer.toString(i));
					}
				}
				if (interfaceCombo.getItemCount() == 0) {
					OneButton1Msg.show(this, "warning", "Deploy warning", "At least one interface must be available after deployment.\n" +
					                                                      "Please revisit the previous select interfaces step", "ok", new Dimension(230, 100));
					return;
				} else {
					interfaceCombo.setSelectedIndex(0);
					((CardLayout2) interfacePrivCard.getLayout()).first(interfacePrivCard);
					deployGui.successfulCompletion();
					data.setInterfacePermissionsCreated(true);
					return;
				}
			} else if (PermissionFunctions.sessionUserHasPermission(svr, data.getRedeployModelId(), PermissionUtils.PERMISSION_TO_SET_INTERFACE_USE_PRIV)) {
				for (int i = 0; i < intData.length; i++) {
					String deployedId = (intData[i]).getDeployId();
					if (deployedId != null)
						p = new PermissionsPanel(svr, permissionCategory, deployedId, intData[i].getName(), true, false);
					else {
						//this should never, ever happen
						System.out.println("error in SetInterfacePrivCard: deployed Id for interface " + intData[i].getName() + " is null");
						p = new PermissionsPanel(svr, permissionCategory, null, intData[i].getName(), false, false); //stuffing a new panel in so that things can go on
					}
					data.getInterfacePermissions().put(Integer.toString(i), p);
					//only add the available ones to the combo box
					if ((intData[i]).getIsAvailable().booleanValue()) {
						interfacePrivCard.add(Integer.toString(i), p.getGui());
						interfaceModel.addEntry(intData[i].getName(), Integer.toString(i));
					}
				}
				interfaceCombo.setSelectedIndex(0);
				((CardLayout2) interfacePrivCard.getLayout()).first(interfacePrivCard);
			} else {
				JPanel temp = new JPanel();
				JLabel msg = Templates.makeLabel("You do have not been given privileges to edit this permission set.");
				JPanel fill = new JPanel();
				JComponent[] comps = {msg, fill};
				// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
				GridBagConstraints[] gbcs = {
					new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
					new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
				};
				Templates.layoutGridBag(temp, comps, gbcs);
				interfacePrivCard.add("tempPanel", temp);
			}
			deployGui.successfulCompletion();
			data.setInterfacePermissionsCreated(true);

		}
	}

	public SetInterfacePrivCard(DeployModel deployData, DeployModelGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setSetInterfacePrivCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who may use the interfaces?", Templates.FONT12B);

		interfacePrivCard.setLayout(new CardLayout2());
		interfaceModel = new ServerPanelComboBoxModel();
		interfaceCombo = Templates.makeDComboBox(interfaceModel);
		interfaceCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					((CardLayout2) interfacePrivCard.getLayout()).show(interfacePrivCard, interfaceModel.getSelectedValue());
				} catch (Exception interfaceCombo) {
					System.out.println("invalid key in combo box");
				}
			}
		});
		Templates.setFixedSize(interfaceCombo, new Dimension(230, 20));

		JComponent[] comps = {msg1, interfaceCombo, interfacePrivCard};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Set interface privileges");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SetInterfacePrivCard(null, null));
		f.show();
	}
}
