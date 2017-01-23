package deployPlayspaceRev2;

import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import deployPlayspace.DeployPlayspace;
import deployPlayspace.LoginCard;
import deployPlayspace.LocateCard;
import deployPlayspace.EditCard;
import deployPlayspace.EditPrivCard;
import deployPlayspace.UsePrivCard;
import deployPlayspace.ConfirmCard;
import deployPlayspace.DecideNextCard;


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
	public static final Dimension DEFAULT_SIZE = new Dimension(650, 450);

	public static final GridBagConstraints gbc = null;

	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;

	private JRadioButton[] buttons;
	private int sel;

	private JRadioButton login;
	private JRadioButton locatePlayspace;
	private JRadioButton editPlayspace;
	private JRadioButton setEditPriv;
	private JRadioButton setUsePriv;
	private JRadioButton confirm;
	private JRadioButton whatNext;
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

	public void enableNextButton(boolean b){
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
	}

	private JPanel makeRadioPanel()
	{
		JPanel p = new JPanel();

		login = Templates.makeRadioButton("Login", false);
		login.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		locatePlayspace = Templates.makeRadioButton("Locate playspace", false);
		locatePlayspace.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		editPlayspace = Templates.makeRadioButton("Edit contents", false);
		editPlayspace.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		setEditPriv = Templates.makeRadioButton("Set editing privileges", false);
		setEditPriv.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		setUsePriv = Templates.makeRadioButton("Set use privileges", false);
		setUsePriv.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		confirm = Templates.makeRadioButton("Confirm and deploy", false);
		confirm.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		whatNext = Templates.makeRadioButton("Decide what to do next", false);
		whatNext.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JPanel fill = new JPanel();
		fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {login, locatePlayspace, editPlayspace, setEditPriv, setUsePriv,
		                      confirm, whatNext, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 7, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(5, 5, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JRadioButton[] buttonsTemp = new JRadioButton[]{login, locatePlayspace, editPlayspace, setEditPriv,
		                                                setUsePriv, confirm, whatNext};
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
		cardPanel.add("login", new LoginCard(data,this));
		cardPanel.add("locatePlayspace", new LocateCard(data, this));
		cardPanel.add("editPlayspace", new EditCard(data, this ));
		cardPanel.add("setEditPriv", new EditPrivCard(data, this));
		cardPanel.add("setUsePriv", new UsePrivCard(data, this));
		cardPanel.add("confirm", new ConfirmCard(data, this));
		cardPanel.add("whatNext", new DecideNextCard(data, this));
		return cardPanel;
	}

	private void initializeDeploySequence()
	{
		sel = 0;
		farthestCompleted = sel;
		buttons[sel].setEnabled(true);
		buttons[sel].setSelected(true);
		for (int i = sel + 1; i < buttons.length; i++) {
			buttons[i].setEnabled(false);
			buttons[i].setSelected(false);
		}
		((CardLayout) cardPanel.getLayout()).show(cardPanel, "login");
		nextButton.setEnabled(false);
		//todo here should reset all of the data in the DeployPlayspace

	}

	private static boolean backDisabled = false;
	private static boolean nextSetToDeploy = false;
	private static boolean nextSetToAnotherServer = false;

	private void setControlButtons()
	{ // nice and convoluted!

		if (backDisabled) { // have left the first card
			backButton.setEnabled(true);
			backDisabled = false;
		}
		if (nextSetToDeploy) { // have left the confirm card
			nextButton.setText("next");
			nextSetToDeploy = false;
		}
		if (nextSetToAnotherServer) { //have left the what's next card
			nextButton.setText("next");
			backButton.setText("back");
			nextSetToAnotherServer = false;
		}

		if (sel == 0) { // on the first login card
			backButton.setEnabled(false);
			backDisabled = true;
			return;
		}
		if (sel == (buttons.length - 2)) { // the confirm and deploy card
			nextButton.setText("deploy");
			nextSetToDeploy = true;
			return;
		}
		if (sel == (buttons.length -1)) { // the what next card
			nextButton.setText("another server");
			backButton.setText("same server");
			nextSetToAnotherServer = true;
			return;
		}
	}

	private void setNextCard()
	{
		if (sel != buttons.length - 1) {
			if (sel != buttons.length - 2) moveDataToServer();
			buttons[sel].setEnabled(false);
			buttons[sel].setSelected(false);
			sel++;
			createDataForNextPanel();
			buttons[sel].setEnabled(true);
			buttons[sel].setSelected(true);
			((CardLayout) cardPanel.getLayout()).next(cardPanel);
			setControlButtons();
			if (sel >= farthestCompleted) nextButton.setEnabled(false);
		}
		else { // case of selecting deploy on a new server for the what's next card
			initializeDeploy();
		}
	}

	private void setPrevCard()
	{
		if (sel != buttons.length - 1) {
			buttons[sel].setEnabled(false);
			buttons[sel].setSelected(false);
			sel--;
			buttons[sel].setEnabled(true);
			buttons[sel].setSelected(true);
			((CardLayout) cardPanel.getLayout()).previous(cardPanel);
			setControlButtons();
			nextButton.setEnabled(true);
		}
		else { // case of deploying on same server (whatnext card)
			//cache the login information
			ServerConnection con = data.getServerConnection();
			initializeDeploySequence();
			buttons[sel].setEnabled(false);
			buttons[sel].setSelected(false);
			//complete the first login step data transfer
			data.setServerConnection(con);
			farthestCompleted++;//indicate that the login step is complete
			// move to the next step
			sel++;
			buttons[sel].setEnabled(true);
			buttons[sel].setSelected(true);
			((CardLayout) cardPanel.getLayout()).next(cardPanel);
			setControlButtons();
		}

	}

	private void createDataForNextPanel(){
		switch (sel) {
			case 2: //need to stuff the edit playspace table
				if (farthestCompleted==(sel)) {
				data.getEditCard().setEditTable();
				successfulCompletion(); // no input required for this step
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
					//todo call the methods to deploy the stuff
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
		if (object == cancelButton)
			System.exit(0);
		if (object == backButton) {
			setPrevCard();
			return;
		}
		if (object == nextButton) {
			setNextCard();
			return;
		}
	}

	private void moveDataToServer()
	{
		//todo write the code to move the data to the server
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy Playspace");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DeployPlayspaceGui(null));
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
