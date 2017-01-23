package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.serverPanel.ServerPanelSelectionListener;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.functions.DeployFilesDbFunctions;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 9, 2003
 * Time: 8:07:14 PM
 * To change this template use Options | File Templates.
 */
public class LocateCard extends JPanel
{
    public static final GridBagConstraints gbc = null;
    private JTextField _selectedLocation;
    private ServerPanel _serverPanel;
    private JPanel _serverPanelCard = new JPanel();

    private String _selectionPath = "";
    private Object _selectionId = null;

    private DeployAnalysisTool _data;
    private DeployToolGui _deployGui;

    private ServerPanelSelectionListener l = new ServerPanelSelectionListener()
	{
		public void selectionChanged(String path, Object id, ServerConnection svr)
		{
			setDataAndTextField(path, id);
		}
	};

    public LocateCard(DeployAnalysisTool deployData, DeployToolGui gui)
	{
		_data = deployData;
		_deployGui = gui;
		_data.setLocateCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};
		Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Where will the analysis tool be located on the server?", Templates.FONT12B);

		JLabel pathLabel = Templates.makeLabel("The current deployment destination is:", Templates.FONT12B);
		_selectedLocation = Templates.makeTextField("");
		_selectedLocation.setEditable(false);

		_serverPanelCard.setLayout(new CardLayout2());
		_serverPanel = new ServerPanel(_data.getServerConnection(), ServerPanel.MODEL_DEPLOY);
		_serverPanelCard.add("serverPanel", _serverPanel);
        _serverPanelCard.validate();
        initGui();

		JComponent[] comps = {msg1, _serverPanelCard, pathLabel, _selectedLocation};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(20, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

    /**
	 * used to reset data in Gui components after successful deployment
	 */
	public void initGui()
	{
		_selectedLocation.setText("");
		_selectedLocation.setBackground(Color.white);
	}

    public void setServerPanelCard()
	{
		_serverPanel = new ServerPanel(_data.getServerConnection(), ServerPanel.MODEL_DEPLOY);
		((ServerPanel) ((CardLayout2) (_serverPanelCard.getLayout())).getActiveComponent()).removeSelectionListeners(l);
		_serverPanelCard.remove(0);
		_serverPanel.addSelectionListeners(l);
		_serverPanelCard.add("serverPanel", _serverPanel);
		_serverPanelCard.validate();
		initGui();

        if (!_data.isNewDeployment())
        { //redeploy case so need to set selection to correct playspace
            String root = DeployFilesFunctions.getRootForAnalysisTool(_data.getServerConnection(), _data.getRedeployAnalysisToolId());
            if (root.startsWith(Folder.SERVER_ROOT + DeployFilesDbFunctions.slash))
                _serverPanel.setRootTo(ServerPanel.SERVER);
            else if (root.startsWith(Folder.GROUPS_ROOT + DeployFilesDbFunctions.slash))
                _serverPanel.setRootTo(ServerPanel.GROUP);
            else if (root.startsWith(Folder.USERS_ROOT + DeployFilesDbFunctions.slash))
                _serverPanel.setRootTo(ServerPanel.USER);
            else
                _serverPanel.setRootTo(ServerPanel.SELF);

            _serverPanel.setObjectSelection(_data.getRedeployAnalysisToolId(), "analysis tool"); //this also locks the table selection to this item
            _deployGui.successfulCompletion();
        }
	}

    private void setDataAndTextField(String path, Object id)
	{
		_selectionPath = path;
		_selectionId = id;

		if (_selectionId != null) {
			_selectedLocation.setText(_selectionPath);
			_data.setServerLocationPath(_selectionPath);
			_data.setLocationFolderId(_selectionId);
			_deployGui.successfulCompletion();
		} else {
			_selectedLocation.setText("");
			_deployGui.enableNextButton(false);
		}
	}
}
