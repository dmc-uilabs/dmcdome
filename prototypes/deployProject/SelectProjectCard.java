package deployProject;

import mit.cadlab.dome.gui.deploy.components.DeployProjectData;
import mit.cadlab.dome.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome.gui.swing.DomeFileChooser;
import mit.cadlab.dome.swing.Templates;

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
public class SelectProjectCard extends JPanel implements ActionListener
{
	public static final GridBagConstraints gbc = null;
	private DeployProject data;
	private DeployProjectGui deployGui;

	private JRadioButton deployNew;
	private JRadioButton redeploy;
	private ButtonGroup deployGroup = new ButtonGroup();

	private JButton browse;
	private JTextField path;
	private static JFileChooser chooser = makeFileChooser();
	private JTextField projectDescription;
	private Component parent=null;

	public ActionListener deployTypeListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!data.getLocalProjectPath().equals("")) { //don't do anything if no file chosen yet
				if (deployNew.isSelected()) {
					data.setNewDeployment(true);
					deployGui.setFarthestCompleted(1);
					deployGui.successfulCompletion();
				}
				else {
					String id = DeployUtilities.getValidRedeployProjectId(parent, data.getServerConnection(),
					        data.getProjectData(), data.getLocalProjectPath());
					if (!id.equals("")) { //redeployment is allowed
						data.setNewDeployment(false);
						data.setRedeployProjectId(id);
						deployGui.setFarthestCompleted(1);
						deployGui.successfulCompletion();
					}
					else { // cannot redeploy, so reset the gui to new deploy
						deployNew.removeActionListener(deployTypeListener);
						deployNew.setSelected(true);
						deployNew.addActionListener(deployTypeListener);
						data.setNewDeployment(true);
					}
				}
			}
		}
	};

	private static JFileChooser makeFileChooser()
	{
		DomeFileChooser f = new DomeFileChooser();
		f.setFilter(DomeFileChooser.DOME_PROJECT_FILTER);
		return f;
	}


	public void setToNewDeploy()
	{
		deployNew.setSelected(true);
		data.setNewDeployment(true);
	}

	public SelectProjectCard(DeployProject deployData, DeployProjectGui gui)
	{
		data = deployData;
		deployGui = gui;
		parent = this;
		data.setSelectProjectCard(this);

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
		projectDescription.setText("");
		projectDescription.setBackground(Color.white);
		path.setText("");
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		JLabel msg1 = Templates.makeLabel("Which iProject are you deploying?", Templates.FONT12B);
		path = Templates.makeTextField("");
		path.setEditable(false);

		browse = Templates.makeButton("browse", this);
		deployNew = Templates.makeRadioButton("deploy as new iProject", true);
		redeploy = Templates.makeRadioButton("redeploy iProject", false);
		data.setNewDeployment(true);

		deployGroup.add(deployNew);
		deployGroup.add(redeploy);

		deployNew.addActionListener(deployTypeListener);
		redeploy.addActionListener(deployTypeListener);


		JPanel fill = new JPanel();
		JLabel descriptionLabel = Templates.makeLabel("iProject description (optional):");
		projectDescription = Templates.makeDTextField("");
		projectDescription.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				data.setDescription(projectDescription.getText());
				projectDescription.setBackground(Color.white);
			}
		});

		JComponent[] comps = {msg1, path, browse, deployNew, redeploy, descriptionLabel, projectDescription, fill};
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
				DeployProjectData tempData = new DeployProjectData(tempPath);
				if (deployNew.isSelected()) {
					data.setNewDeployment(true);
				}
				else {
					String id = DeployUtilities.getValidRedeployProjectId(this, data.getServerConnection(),
					        tempData, tempPath);
					if (!id.equals("")) { //redeployment is allowed
						data.setNewDeployment(false);
						data.setRedeployProjectId(id);
					}
					else { // cannot redeploy, so reset the gui to new deploy;
						deployNew.removeActionListener(deployTypeListener);
						deployNew.setSelected(true);
						deployNew.addActionListener(deployTypeListener);
						data.setNewDeployment(true);
					}
				}
				data.setLocalProjectPath(tempPath);
				path.setText(data.getLocalProjectPath());
				data.setProjectData(tempData);
				deployGui.setFarthestCompleted(1);
				deployGui.successfulCompletion();
			}
		}
	}


	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy select project card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SelectProjectCard(null, null));
		f.show();
	}


}
