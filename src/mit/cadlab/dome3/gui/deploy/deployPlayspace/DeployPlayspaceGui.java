package mit.cadlab.dome3.gui.deploy.deployPlayspace;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.deploy.deployModel.DeployModelGui;
import mit.cadlab.dome3.gui.guiutils.waitcursor.WaitCursorUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 25, 2003
 * Time: 10:36:47 PM
 * To change this template use Options | File Templates.
 */

/**
 * The main class for the playspace deploy wizard
 */
public class DeployPlayspaceGui extends JPanel implements ActionListener
{
	public static final GridBagConstraints gbc = null;

	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;

	private JRadioButton[] buttons;
	private int sel;

	private JRadioButton login;
	private JRadioButton locatePlayspace;
	private JRadioButton selectPlayspace;
	private JRadioButton setEditPriv;
	private JRadioButton setUsePriv;
	private JRadioButton confirm;
	private DeployPlayspace data;

	private JPanel cardPanel;
	private int farthestCompleted;

	/**
	 * Used to reset the process if the user steps back certain points in the process
	 * @param i the step in the process that want to reset the process to
	 */
	public void setFarthestCompleted(int i)
	{
		farthestCompleted = i;
	}

	public void enableNextButton(boolean b)
	{
		nextButton.setEnabled(b);
	}

	/**
	 * This method MUST be called by any deploy card after its data has been filled sucessfully
	 */
	public void successfulCompletion()
	{
		if (farthestCompleted == sel)
			farthestCompleted++;
		nextButton.setEnabled(true);
	}

	/**
	 * Constructor for the main Deploy playspace wizard
	 */
	public DeployPlayspaceGui(DeployPlayspace deployPlayspace)
	{
		data = deployPlayspace;

		JComponent[] comps = {makeRadioPanel(), makeButtonPanel(), makeCardPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
		initializeDeploy();

		setPreferredSize(DeployModelGui.DEFAULT_SIZE);
	}

	private JPanel makeRadioPanel()
	{
		JPanel p = new JPanel();

		login = Templates.makeRadioButton("Login", false);
		login.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		selectPlayspace = Templates.makeRadioButton("Select the playspace", false);
		selectPlayspace.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		locatePlayspace = Templates.makeRadioButton("Locate playspace", false);
		locatePlayspace.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		setEditPriv = Templates.makeRadioButton("Set editing privileges", false);
		setEditPriv.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		setUsePriv = Templates.makeRadioButton("Set use privileges", false);
		setUsePriv.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		confirm = Templates.makeRadioButton("Confirm and deploy", false);
		confirm.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JPanel fill = new JPanel();
		fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {login, selectPlayspace, locatePlayspace, setEditPriv, setUsePriv,
		                      confirm, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 7, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(5, 5, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JRadioButton[] buttonsTemp = new JRadioButton[]{login, selectPlayspace, locatePlayspace, setEditPriv,
		                                                setUsePriv, confirm};
		buttons = buttonsTemp;
		return p;
	}

	private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();

		backButton = Templates.makeButton("back", this);
		nextButton = Templates.makeButton("next", this);
		cancelButton = Templates.makeButton("cancel", this);

		JComponent[] comps = {backButton, nextButton, cancelButton};
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
		cardPanel.add("login", new LoginCard(data, this));
		cardPanel.add("selectPlayspace", new SelectPlayspaceCard(data, this));
		cardPanel.add("locatePlayspace", new LocateCard(data, this));
		cardPanel.add("setEditPriv", new EditPrivCard(data, this));
		cardPanel.add("setUsePriv", new UsePrivCard(data, this));
		cardPanel.add("confirm", new ConfirmCard(data, this));
		return cardPanel;
	}

	private void initializeDeploySequence()
	{
		sel = 0;
		//allows the user to skip relogin if they want to deploy on the same server
		if (data.getServerConnection() == null) {
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
		((CardLayout) cardPanel.getLayout()).show(cardPanel, "login");

		//data.getLoginCard().initGui(); do not need this line as allows additional cycles without new login
		data.getSelectPlayspaceCard().initGui();
		data.getLocateCard().initGui();
		//reset all of the data in the DeployModel
		data.initDeployData();

	}

	private static boolean backDisabled = false;
	private static boolean nextSetToDeploy = false;

	private void setControlButtons()
	{ // nice and convoluted!

		if (backDisabled) {
			backButton.setEnabled(true);
			backDisabled = false;
		}
		if (nextSetToDeploy) {
			nextButton.setText("next");
			nextSetToDeploy = false;
		}
		if (sel == 0) {
			backButton.setEnabled(false);
			backDisabled = true;
			return;
		}
		if (sel == (buttons.length - 1)) {
			if (data.isNewDeployment())
				nextButton.setText("deploy");
			else
				nextButton.setText("redeploy");
			nextSetToDeploy = true;
			return;
		}
	}

	private void setNextCard()
	{
		if (sel != buttons.length - 1) {
			buttons[sel].setEnabled(false);
			buttons[sel].setSelected(false);
			sel++;
			createDataForNextPanel();
			buttons[sel].setEnabled(true);
			buttons[sel].setSelected(true);
			((CardLayout) cardPanel.getLayout()).next(cardPanel);
			setControlButtons();
			if ((sel != buttons.length - 1) && (sel >= farthestCompleted)) nextButton.setEnabled(false);
		} else { // case of selecting deploy on a new server for the what's next card
			data.deployData(); // this musyt happen before initializing the deploy
			initializeDeploy();

		}
	}

	private void setPrevCard()
	{
		buttons[sel].setEnabled(false);
		buttons[sel].setSelected(false);
		sel--;
		buttons[sel].setEnabled(true);
		buttons[sel].setSelected(true);
		((CardLayout) cardPanel.getLayout()).previous(cardPanel);
		setControlButtons();
		nextButton.setEnabled(true);
	}

	private void createDataForNextPanel()
	{
		switch (sel) {
			case 1: // new model selection
				if (farthestCompleted == (sel)) {
					data.getSelectPlayspaceCard().initGui();
					data.setNewDeployment(true);
				}
				break;
			case 2: // need to setup the server panel
				if (farthestCompleted == (sel)) {
					data.getLocateCard().setServerPanelCard();
				}
				break;
			case 3: //need to setup the edit permission table
				if (farthestCompleted == (sel)) {
					data.getEditPrivCard().setEditPrivPanel();
					successfulCompletion(); // no input required for this step
				}
				break;
			case 4: //need to setup the use permission table
				if (farthestCompleted == (sel)) {
					data.getUsePrivCard().setUsePrivPanel();
					successfulCompletion(); // no input required for this step
				}
				break;
			case 5: //need to setup the use permission table
				data.getConfirmCard().setConfirmCard();
				successfulCompletion();
				break;
		}
	}

	private void initializeDeploy()
	{
		initializeDeploySequence();
		setControlButtons();
	}

	public void actionPerformed(ActionEvent event)
	{
		Object object = event.getSource();
		if (object == cancelButton) {
			logoutFromServer();
			dispose();
		}
		if (object == backButton) {
            //set wait cursor here
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
		ServerConnection svr = data.getServerConnection();
		if (svr != null) svr.logout();
	}

	public void dispose()
	{
		SwingUtilities.windowForComponent(DeployPlayspaceGui.this).dispose();
	}

	public WindowAdapter getWindowAdapter()
	{
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		};
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy Playspace");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DeployPlayspaceGui(null));
		f.setSize(DeployModelGui.DEFAULT_SIZE);
		f.show();
	}
}
