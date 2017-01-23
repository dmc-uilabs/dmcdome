package mit.cadlab.dome3.gui.deploy.deployModel;

import mit.cadlab.dome3.gui.deploy.components.DeployModelData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.serverPanel.ServerPanel;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.server.functions.DeployFilesDbFunctions;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 5:28:17 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card for selecting what model will be deployed on a server
 */
public class SelectModelCard extends JPanel implements ActionListener
{
	public static final String MODEL = "model";
	public static final GridBagConstraints gbc = null;
	private DeployModel data;
	private DeployModelGui deployGui;
	private Component parent = null;

	private JRadioButton deployNew;
	private JRadioButton redeploy;
	private ButtonGroup deployGroup = new ButtonGroup();

	private JButton browse;
	private JTextField path;
	private static JFileChooser chooser = makeFileChooser();
	private JTextField modelDescription;

	private static JFileChooser makeFileChooser()
	{
		DomeFileChooser f = new DomeFileChooser();
		f.setFilter(DomeFileChooser.DOME_MODELS_FILTER);
		return f;
	}

	public void setToNewDeploy()
	{
		deployNew.setSelected(true);
	}

	public SelectModelCard(DeployModel deployData, DeployModelGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setSelectModelCard(this);
		parent = this;

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	/**
	 * used to reset the GUI after successful deployment
	 */
	public void initGui()
	{
		deployNew.removeActionListener(deployTypeListener);
		deployNew.setSelected(true);
		deployNew.addActionListener(deployTypeListener);
		modelDescription.setText("");
		modelDescription.setBackground(Color.white);
		path.setText("");
	}

	ActionListener deployTypeListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!data.getLocalModelPath().equals("")) { // don't do anything is there is no file picked yet
				if (!deployNew.isSelected()) { //if a redeploy is being attempted
					String modelId = DeployUtilities.getValidRedeployModelId(parent, data.getServerConnection(), data.getModelData(), data.getLocalModelPath());
					if (!modelId.equals("")) {//found valid redeployId
						resetData();//need to reset process to this point if we backed up and selected a new file to deploy
						data.setRedeployModelId(modelId); //this line must be called before setData
						setData(data.getLocalModelPath(), data.getModelData());
						configureSelectLocationForRedeploy(modelId);
						deployGui.successfulCompletion();
						DeployUtilities.synchronizeModelDataWithServer(data.getServerConnection(),
						                                               modelId, data.getModelData());
						data.setModelDescription(data.getModelData().getModelDescription());
						modelDescription.setText(data.getModelDescription());
						modelDescription.setBackground(Color.white);
					} else { //cannot redeploy so force newdeploy case
						deployNew.removeActionListener(deployTypeListener);
						deployNew.setSelected(true);
						deployNew.addActionListener(deployTypeListener);
					}
					return;
				}
				resetData();//need to reset process to this point if we backed up and selected a new file to deploy
				setData(data.getLocalModelPath(), data.getModelData());
				//todo line below is overkill, done because cannot seem to make folder selectable again after toggling from redeploy
				(data.getSelectLocationCard()).setServerPanelCard(new ServerPanel(data.getServerConnection(), ServerPanel.MODEL_DEPLOY));
				//just need to make folders selectable again, but does not seem to work.
				//(data.getSelectLocationCard().getServerPanel()).setEnabled(true);
				//(data.getSelectLocationCard().getServerPanel()).setSelectionModel(new FileSystemFilters.FoldersFilterTreeSelectionModel());
				deployGui.successfulCompletion();
			}
		}
	};

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		JLabel msg1 = Templates.makeLabel("Which model are you deploying?", Templates.FONT12B);
		path = Templates.makeTextField("");
		path.setEditable(false);

		browse = Templates.makeButton("browse", this);
		deployNew = Templates.makeRadioButton("deploy as new model", true);
		redeploy = Templates.makeRadioButton("redeploy model", false);

		deployGroup.add(deployNew);
		deployGroup.add(redeploy);

		deployNew.addActionListener(deployTypeListener);
		redeploy.addActionListener(deployTypeListener);

		JPanel fill = new JPanel();
		JLabel descriptionLabel = Templates.makeLabel("model description (optional):");
		modelDescription = Templates.makeDTextField("");
		modelDescription.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				data.setModelDescription(modelDescription.getText());
				if (data.getModelData() != null)
					data.getModelData().setModelDescription(modelDescription.getText());
				modelDescription.setBackground(Color.white);
			}
		});

		JComponent[] comps = {msg1, path, browse, deployNew, redeploy, descriptionLabel, modelDescription, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public void actionPerformed(ActionEvent event)
	{
		Object object = event.getSource();
		if (object == browse) {
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				String tempPath = file.getAbsolutePath();
				DeployModelData tempModelData = DeployUtilities.loadModelForDeploy(tempPath);
				if (!deployNew.isSelected()) { // redeploy case
					String modelId = DeployUtilities.getValidRedeployModelId(parent, data.getServerConnection(), tempModelData, tempPath);
					if (!modelId.equals("")) {//found valid redeployId
						resetData();//need to reset process to this point if we backed up and selected a new file to deploy
						data.setRedeployModelId(modelId); //this line must be called before setData
						setData(tempPath, tempModelData);
						configureSelectLocationForRedeploy(modelId);
						deployGui.successfulCompletion();
						DeployUtilities.synchronizeModelDataWithServer(data.getServerConnection(),
						                                               modelId, data.getModelData());
						data.setModelDescription(data.getModelData().getModelDescription());
						modelDescription.setText(data.getModelDescription());
						modelDescription.setBackground(Color.white);
						return;
					} else { //cannot redeploy so force newdeploy case
						deployNew.removeActionListener(deployTypeListener);
						deployNew.setSelected(true);
						deployNew.addActionListener(deployTypeListener);
						// now continue as if new deploy case
					}
				}
				// only get to here is if is a new deploy or a failed redeploy attempt, so load as new
				if ((tempModelData.getModelInterfaces()).size() == 0) {
					OneButton1Msg.show(this, "warning", "Deploy warning",
					                   "This model has no interfaces so it cannot be deployed", "ok", new Dimension(250, 80));
					return;
				} else {
					resetData();//need to reset process to this point if we backed up and selected a new file to deploy
					setData(tempPath, tempModelData);
					deployGui.successfulCompletion();
				}
			}
		}
	}

	private void configureSelectLocationForRedeploy(String modelId)
	{
		ServerPanel panel = data.getSelectLocationCard().getServerPanel();

		String root = DeployFilesFunctions.getRootForModel(data.getServerConnection(), modelId);
		if (root.startsWith(Folder.SERVER_ROOT + DeployFilesDbFunctions.slash))
			panel.setRootTo(ServerPanel.SERVER);
		else if (root.startsWith(Folder.GROUPS_ROOT + DeployFilesDbFunctions.slash))
			panel.setRootTo(ServerPanel.GROUP);
		else if (root.startsWith(Folder.USERS_ROOT + DeployFilesDbFunctions.slash))
			panel.setRootTo(ServerPanel.USER);
		else
			panel.setRootTo(ServerPanel.SELF);

		panel.setObjectSelection(modelId, SelectModelCard.MODEL); //this also locks the table selection to this item
	}


	private void resetData()
	{
		deployGui.setFarthestCompleted(1);
		//rest the permissions panel
		data.setModelPermPanel(null);
		//reset the interface priviledges card
		data.getSetInterfacePrivCard().resetInterfacePriv(); // this is set later and depends on whether redeploy
		data.getSelectInterfaceCard().initGui();
	}

	private void setData(String tempPath, DeployModelData tempModelData)
	{
		//this must be first
		deployGui.setFarthestCompleted(1);
		if (deployNew.isSelected() == true)
			data.setNewDeployment(true);
		else
			data.setNewDeployment(false);

		//these must be second
		//update the path selection
		path.setText(tempPath);
		//update the underlying data model because everything is OK
		data.setLocalModelPath(tempPath);
		//update the modelData and interface cards
		data.setModelData(tempModelData);

		// finally, these must be last
		//create a new model permissions panel
		data.getSetModelPrivCard().setModelPermPanel();
		(data.getSelectInterfaceCard()).setSelectInterfacePanel();
	}


	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy select model card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SelectModelCard(null, null));
		f.show();
	}


}
