package mit.cadlab.dome3.gui.deploy.deployModel;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 8:28:43 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.permission.PermissionsPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Card used to set editing priveleges on the model during deployment
 */
public class SetModelPrivCard extends JPanel
{
	public static final GridBagConstraints gbc = null;
//	private JButton setButton;
	private DeployModel data;
	private PermissionsPanel modelPermPanel;
	private JPanel modelPermCard = new JPanel();

	public SetModelPrivCard(DeployModel deployData, DeployModelGui gui)
	{
		data = deployData;
		data.setSetModelPrivCard(this);
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	public void setModelPermPanel()
	{
		String permissionCategory = PermissionUtils.MODEL_IPROJECT_EDIT_PRIVILEGES;
		ServerConnection svr = data.getServerConnection();
		String name = data.getLocalModelPath();

		if ((data.getModelPermPanel() == null)) {
			if (data.getNewDeployment() == true) {
				modelPermPanel = new PermissionsPanel(svr, permissionCategory, null, name, false, false);
				data.setModelPermPanel(modelPermPanel);
				if (((CardLayout2) modelPermCard.getLayout()).getActiveComponent() != null)
					modelPermCard.remove(0);
				modelPermCard.add("permPanel", modelPermPanel.getGui());
			} else if (PermissionFunctions.sessionUserHasPermission(svr, data.getRedeployModelId(), PermissionUtils.PERMISSION_TO_SET_MODEL_EDIT_PRIV)) {
				modelPermPanel = new PermissionsPanel(svr, permissionCategory, data.getRedeployModelId(), data.getModelData().getName(), true, false);
				data.setModelPermPanel(modelPermPanel);
				if (((CardLayout2) modelPermCard.getLayout()).getActiveComponent() != null)
					modelPermCard.remove(0);
				modelPermCard.add("permPanel", modelPermPanel.getGui());
			} else {
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
				if (((CardLayout2) modelPermCard.getLayout()).getActiveComponent() != null)
					modelPermCard.remove(0);
				modelPermCard.add("permPanel", p);
			}
		}
	}


	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Who will be able to edit the model?", Templates.FONT12B);

		modelPermCard.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, modelPermCard};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy set model edit privildges");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SetModelPrivCard(null, null));
		f.show();
	}

}
