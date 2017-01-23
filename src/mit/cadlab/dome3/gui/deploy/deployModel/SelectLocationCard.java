package mit.cadlab.dome3.gui.deploy.deployModel;

import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelSelectionListener;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 5:56:22 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card for selecting where the model will be deployed to on the server
 */
public class SelectLocationCard extends JPanel
{
	public static final GridBagConstraints gbc = null;


	private JPanel serverPanelCard;

	private DeployModel data;
	private DeployModelGui deployGui; //used later to call the sucessfulCompletion method
	private JTextField selectionField;

	private String selectionPath = "";
	private Object selectionId = null;

	private ServerPanelSelectionListener l = new ServerPanelSelectionListener()
	{
		public void selectionChanged(String path, Object id, ServerConnection svr)
		{
			setDataAndTextField(path, id);
		}
	};

	/**
	 * 
	 * @param serverPanel
	 */
	public void setServerPanelCard(ServerPanel serverPanel)
	{
		((ServerPanel) ((CardLayout2) (serverPanelCard.getLayout())).getActiveComponent()).removeSelectionListeners(l);
		serverPanelCard.remove(0);
		serverPanel.addSelectionListeners(l);
		serverPanelCard.add("serverPanel", serverPanel);
		initGui();
	}

	public ServerPanel getServerPanel()
	{
		return (ServerPanel) ((CardLayout2) (serverPanelCard.getLayout())).getActiveComponent();
	}

	/**
	 * used to reset data in Gui components after successful deployment
	 */
	public void initGui()
	{
		selectionField.setText("");
		selectionField.setBackground(Color.white);
	}

	/**
	 * Constructor for locating where model deployment will occur on the server
	 * @param deployData underlying DeployModel data
	 * @param gui main DeployModelGUI used to inform when valid selection is made.
	 */
	public SelectLocationCard(DeployModel deployData, DeployModelGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setSelectLocationCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Where will you put the model on the server?", Templates.FONT12B);

		serverPanelCard = new JPanel();
		serverPanelCard.setLayout(new CardLayout2());
		serverPanelCard.add("mit.cadlab.dome3.gui.serverPanel", new ServerPanel(data.getServerConnection(), ServerPanel.MODEL_DEPLOY));

		JLabel selectionLabel = Templates.makeLabel("The current deployment destination is:", Templates.FONT12B);
		selectionField = Templates.makeTextField("");
		selectionField.setEditable(false);

		JComponent[] comps = {msg1, serverPanelCard, selectionLabel, selectionField};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(2, 0, 5, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	private void setDataAndTextField(String path, Object id)
	{
		selectionPath = path;
		selectionId = id;

		if (selectionId != null) {
			selectionField.setText(selectionPath);
			data.setServerLocationPath(selectionPath);
			data.setServerLocationId(selectionId);
			deployGui.successfulCompletion();
		} else {
			selectionField.setText("");
			deployGui.enableNextButton(false);
		}
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy select location on server");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SelectLocationCard(null, null));
		f.show();
	}
}
