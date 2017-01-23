package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.deploy.deployProject.*;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 8, 2003
 * Time: 10:58:09 PM
 * To change this template use Options | File Templates.
 */
public class DeployToolGui extends DeployProjectGui
{
    public static final Dimension DEFAULT_SIZE = new Dimension(DomeBuildFrame.DEFAULT_SIZE.width, 500);
	public static final int SEL_LOCATION = 2;
	public static final int MODEL_PRIV = 3;
	public static final int INTERFACE_SEL = 4;
	public static final int INTERFACE_PRIV = 5;
	public static final int CONFIRM = 6;
	public static final GridBagConstraints gbc = null;

    public static final int SELECT_TOOL_INTERFACE_CARD = 4;

    private JRadioButton _login, _selModel, _selLocation, _setEditPrivileges,
                            _selInterfaces, _setInterfacePrivileges, _confirm,
                                _selProject, _setEditProjectPriv, _setProjectVisibility,
                                    _selectProjectInterfaces, _setProjectInterfaceUsePriv,
                                        _setIntModelPriv, _confirmAndDeploy;
    private DeployAnalysisTool _data;

    public DeployToolGui(DeployAnalysisTool deployTool)
	{
        super(deployTool.getDeployProject(), true);
		_data = deployTool;

		JComponent[] comps = {makeRadioPanel(), makeButtonPanel(), makeCardPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
		initializeDeploy();

		setPreferredSize(DEFAULT_SIZE);
	}

    public void initializeDeploySequence()
	{
		sel = 0;
		//allows the user to skip relogin if they want to deploy on the same server
		if (_data.getServerConnection() == null) {
			farthestCompleted = sel;
			nextButton.setEnabled(false);
		} else {
			farthestCompleted = sel + 1;
			nextButton.setEnabled(true);
		}

		buttons[sel].setEnabled(true);
		buttons[sel].setSelected(true);
		for (int i = sel + 1; i < buttons.length; i++) {
			buttons[i].setEnabled(false);
			buttons[i].setSelected(false);
		}
		((CardLayout) cardPanel.getLayout()).show(cardPanel, "_login");

        _data.getSelectToolCard().initGui();
        _data.getLocateCard().initGui();
        _data.getDeployProject().getSelectProjectCard().initGui();
		_data.initDeployData();
	}

    private void setControlButtons()
	{ // nice and convoluted!
        if (backDisabled)
        {
            backButton.setEnabled(true);
            backDisabled = false;
        }
        if (nextSetToDeploy)
        {
            nextButton.setText("next");
            nextSetToDeploy = false;
        }
        if (sel == 0)
        {
            backButton.setEnabled(false);
            backDisabled = true;
            return;
        }
        if (sel == (buttons.length - 1))
        {
            if (_data.isNewDeployment())
                nextButton.setText("deploy");
            else
                nextButton.setText("redeploy");
            nextSetToDeploy = true;
            return;
        }
	}

    private JPanel makeRadioPanel()
	{
		JPanel p = new JPanel();

		_login = Templates.makeRadioButton("Login", false);
		_login.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		_selModel = Templates.makeRadioButton("Select analysis tool", false);
		_selModel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		_selLocation = Templates.makeRadioButton("Select location on server", false);
		_selLocation.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _setEditPrivileges = Templates.makeRadioButton("Set analysis tool editing privileges", false);
        _setEditPrivileges.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _selInterfaces = Templates.makeRadioButton("Select interfaces to deploy", false);
		_selInterfaces.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		_setInterfacePrivileges = Templates.makeRadioButton("Set interface use privileges", false);
		_setInterfacePrivileges.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		_confirm = Templates.makeRadioButton("Confirm analysis tool information", false);
		_confirm.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _selProject = Templates.makeRadioButton("Confirm iProject location");
        _selProject.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _setEditProjectPriv = Templates.makeRadioButton("Set iProject editing privileges", false);
        _setEditProjectPriv.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _selectProjectInterfaces = Templates.makeRadioButton("Select iProject interfaces ", false);
        _selectProjectInterfaces.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _setProjectInterfaceUsePriv = Templates.makeRadioButton("Select iProject interface use privileges ", false);
        _setProjectInterfaceUsePriv.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _setProjectVisibility = Templates.makeRadioButton("Set iProject content visibility", false);
        _setProjectVisibility.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _setIntModelPriv = Templates.makeRadioButton("Set integration model privileges ", false);
        _setIntModelPriv.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        _confirmAndDeploy = Templates.makeRadioButton("Confirm and deploy", false);
        _confirmAndDeploy.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JPanel fill = new JPanel();
		fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {

            _login,
            _selModel,
            _selLocation,
            _setEditPrivileges,
            _selInterfaces,
            _setInterfacePrivileges,
            _confirm,
            _selProject,
            _setEditProjectPriv,
            _selectProjectInterfaces,
            _setProjectInterfaceUsePriv,
            _setProjectVisibility,
            _setIntModelPriv,
            _confirmAndDeploy,
            fill
        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 14, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0)
        };

		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JRadioButton[] buttonsTemp = new JRadioButton[]{
            _login,
            _selModel,
            _selLocation,
            _setEditPrivileges,
            _selInterfaces,
            _setInterfacePrivileges,
            _confirm,
            _selProject,
            _setEditProjectPriv,
            _selectProjectInterfaces,
            _setProjectInterfaceUsePriv,
            _setProjectVisibility,
            _setIntModelPriv,
            _confirmAndDeploy
        };

        buttons = buttonsTemp;
		return p;
	}

    private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();

		backButton = Templates.makeButton("back", this);
		nextButton = Templates.makeButton("next", this);
	    cancelButton = Templates.makeButton("cancel", this);

		JComponent[] comps = {

            backButton,
            nextButton,
            cancelButton
        };
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 10, 0, 0), 0, 0)
		};

        Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

    private JPanel makeCardPanel()
	{
		cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout());
		cardPanel.add("_login", new LoginCard(_data, this));
	    cardPanel.add("_selModel", new SelectAnalysisToolCard(_data, this));
		cardPanel.add("_selLocation", new LocateCard(_data, this));
        cardPanel.add("_selEditPrivileges", new EditToolPrivilagesCard(_data, this));
		cardPanel.add("_selInterfaces", new SelectToolInterfacesCard(_data, this));
        cardPanel.add("_setInterfacePrivileges", new SetToolInterfacePrivilegesCard(_data, this));
		cardPanel.add("_confirmAnalysisTool", new ConfirmAnalysisToolCard(_data, this));
        cardPanel.add("_selProject", new SelectProjectCard(_data.getDeployProject(), this));
        cardPanel.add("_selEditProjectPriv", new EditProjectPrivCard(_data.getDeployProject(), this));
        cardPanel.add("_selectProjectInterfaces", new SelectProjectInterfacesCard(_data.getDeployProject(), this));
        cardPanel.add("_selectProjectInterfaceUsePriv", new SetProjectInterfacePrivCard(_data.getDeployProject(), this));
        cardPanel.add("_setProjectVisibility", new SetProjectVisibility(_data.getDeployProject(), this));
        cardPanel.add("_setIntModelPriv", new SetIntegrationModelPrivCard(_data.getDeployProject(), this));
        cardPanel.add("_confirm", new ConfirmCard(_data.getDeployProject(), this));

        return cardPanel;
	}

    public void actionPerformed(ActionEvent event)
	{
		Object object = event.getSource();
		if (object == cancelButton) {
			logoutFromServer();
			dispose();
		}
		if (object == backButton) {
			WaitCursorUtils.showWaitCursor(true, this);
            setPrevCard();
            WaitCursorUtils.showWaitCursor(false, this);
			return;
		}
		if (object == nextButton) {
			//set wait cursor here
            WaitCursorUtils.showWaitCursor(true, this);
            setNextCard();
            WaitCursorUtils.showWaitCursor(false, this);
			return;
		}
	}

    private void logoutFromServer()
	{
		ServerConnection svr = _data.getServerConnection();
		if (svr != null) svr.logout();
	}

    private void setNextCard()
	{
		if (sel != buttons.length - 1)
        {
            buttons[sel].setEnabled(false);
            buttons[sel].setSelected(false);
            sel++;
            createDataForNextPanel();
            buttons[sel].setEnabled(true);
            buttons[sel].setSelected(true);
            ((CardLayout) cardPanel.getLayout()).next(cardPanel);
            setControlButtons();
            if ((sel != buttons.length - 1) && (sel >= farthestCompleted)) nextButton.setEnabled(false);
        }
        else
        { // case of selecting deploy on a new server for the what's next card
            _data.deployData(); // this must happen before initializing the deploy
            initializeDeploy();

        }
	}

    private void createDataForNextPanel()
	{
		switch (sel)
        {
            case 1: // analysis tool selection
                if (farthestCompleted == (sel))
                {
                    _data.getSelectToolCard().initGui();
                    _data.setNewDeployment(true);
                }
                break;
            case 2: // locate model on server, setup the server panel
                if (farthestCompleted == (sel))
                {
                    _data.getLocateCard().setServerPanelCard();
                }
                break;
            case 3: //edit analysis tool privs, need to setup the edit permission table
                if (farthestCompleted == (sel))
                {
                    _data.getEditPrivilagesCard().setEditPrivPanel();
                    successfulCompletion(); // no input required for this step
                }
                break;
			case 4:
                if (farthestCompleted == (sel))
                {
                    _data.getSelectToolInterfaceCard().setSelectToolInterfacesCard();
                    successfulCompletion();
                }
                break;
            case 5:
                if (farthestCompleted == (sel))
                {
                    _data.getToolInterfaceUsePrivilegesCard().setUsePrivilegesPanel();
                    successfulCompletion();
                    _data.setAvailableToolInterfacesChanged(false);
                }
                break;
            case 6:
                if (farthestCompleted == sel)
                {
                    _data.getConfirmCard().setConfirmCard();
                    successfulCompletion();// no input required for this step
                }
				break;
            case 7:                               // confirms location of project inside analysis tool
                if (farthestCompleted == sel)
                {
                    _data.getDeployProject().getSelectProjectCard().initAnalysisToolDeploy(_data);
                    successfulCompletion();
                }
            case 8:
                if (farthestCompleted == sel)
                {
                    _data.getDeployProject().getEditPrivCard().setEditPrivPanel();
                    successfulCompletion();
                }
				break;
			case 9: //select project interfaces
				if (farthestCompleted == (sel)) {
					_data.getDeployProject().getSelectProjectInterfaces().setSelectProjectInterfacesCard();
					successfulCompletion(); // no input required for this step
				}
				break;
			case 10: //need to setup the project interface use permission table
				if (farthestCompleted == (sel) || _data.getDeployProject().isAvailableProjectInterfacesChanged())
                {
					_data.getDeployProject().getInterfaceUsePrivCard().setUsePrivPanel();
					successfulCompletion(); // no input required for this step
					_data.getDeployProject().setAvailableProjectInterfacesChanged(false);
				}
				break;
			case 11: //project visibility, need to setup visibility permissions table
				if (farthestCompleted == (sel)) {
					_data.getDeployProject().getProjectVisCard().setProjectVisCard();
					successfulCompletion(); // no input required for this step
				}
				break;
			case 12: //set the integration model interface priviledges
				if (farthestCompleted == (sel)) {
					_data.getDeployProject().getIntegrationModelPrivCard().setIntegrationModelPrivCard();
					successfulCompletion(); // no input required for this step
				}
				break;
			case 13: //need to setup the confirm card
				_data.getDeployProject().getConfirmCard().setConfirmCard();
				successfulCompletion();// no input required for this step
				break;
        }
	}

    public void dispose()
    {
        SwingUtilities.windowForComponent(DeployToolGui.this).dispose();
    }

    public void enableNextButton(boolean b)
	{
		nextButton.setEnabled(b);
	}

    /**
	 * This method MUST be called by any deploy card after its data has been filled sucessfully
	 */

}
