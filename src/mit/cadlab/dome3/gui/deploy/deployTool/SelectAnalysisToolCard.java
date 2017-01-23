package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 9, 2003
 * Time: 12:51:06 PM
 * To change this template use Options | File Templates.
 */
public class SelectAnalysisToolCard extends JPanel implements ActionListener
{
    public static final GridBagConstraints gbc = null;

    private Component parent = null;
    private JTextField _path, _toolDescription;
    private JButton _browse;
    private JRadioButton _deployNew, _redeploy;
    private ButtonGroup _deployGroup = new ButtonGroup();

    private static JFileChooser chooser = makeFileChooser();

    private DeployAnalysisTool _data;
    private DeployToolGui _deployGui;

    public ActionListener _deployTypeListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!_data.getLocalAnalysisToolPath().equals(""))
            { //don't do anything if no file chosen yet
                if (_deployNew.isSelected())
                {
                    _data.setNewDeployment(true);
                    _deployGui.setFarthestCompleted(1);
                    _deployGui.successfulCompletion();
                }
                else
                {
                    String id = DeployUtilities.getValidRedeployAnalysisToolId(parent, _data.getServerConnection(),
					                                                      _data.getToolData(), _data.getLocalAnalysisToolPath());
                    if (!id.equals(""))
                    { //redeployment is allowed
                        _data.setNewDeployment(false);
                        _data.setRedeployAnalysisToolId(id);
                        synchronizeDataWithServer();
                        _deployGui.setFarthestCompleted(1);
                        _deployGui.successfulCompletion();
                    }
                    else
                    { // cannot redeploy, so reset the gui to new deploy
                        _deployNew.removeActionListener(_deployTypeListener);
                        _deployNew.setSelected(true);
                        _deployNew.addActionListener(_deployTypeListener);
                        _data.setNewDeployment(true);
                    }
                }
            }
		}
	};

    public SelectAnalysisToolCard(DeployAnalysisTool deployData, DeployToolGui gui)
	{
		_data = deployData;
		_deployGui = gui;
		parent = this;
		_data.setSelectToolCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makePanel()
	{
		JPanel p = new JPanel();

		JLabel msg1 = Templates.makeLabel("which analysis tool are you deploying?", Templates.FONT12B);
		_path = Templates.makeTextField("");
		_path.setEditable(false);

		_browse = Templates.makeButton("browse", this);
		_deployNew = Templates.makeRadioButton("deploy as new analysis tool", true);
		_redeploy = Templates.makeRadioButton("redeploy analysis tool", false);
		_data.setNewDeployment(true);

		_deployGroup.add(_deployNew);
		_deployGroup.add(_redeploy);

		_deployNew.addActionListener(_deployTypeListener);
		_redeploy.addActionListener(_deployTypeListener);


		JPanel fill = new JPanel();
		JLabel descriptionLabel = Templates.makeLabel("analysis tool description (optional):");
		_toolDescription = Templates.makeDTextField("");
		_toolDescription.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				_data.setDescription(_toolDescription.getText());
				if (_data.getToolData() != null)
					_data.getToolData().setDescription(_toolDescription.getText());
				_toolDescription.setBackground(Color.white);
			}
		});

		JComponent[] comps = {msg1, _path, _browse, _deployNew, _redeploy, descriptionLabel, _toolDescription, fill};
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

    /**
	 * used to reset the GUI after successful deployment
	 */
	public void initGui()
	{
		_deployNew.removeActionListener(_deployTypeListener);
		_deployNew.setSelected(true);
		_deployNew.addActionListener(_deployTypeListener);
		_toolDescription.setText("");
		_toolDescription.setBackground(Color.white);
		_path.setText("");
	}

    private static JFileChooser makeFileChooser()
	{
		DomeFileChooser f = new DomeFileChooser();
		f.setFilter(DomeFileChooser.DOME_ANALYSIS_TOOL_FILTER);
		return f;
	}

    public void actionPerformed(ActionEvent event)
	{
		Object object = event.getSource();
        if (object == _browse)
        {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = chooser.getSelectedFile();
                String tempPath = file.getAbsolutePath();
                DeployAnalysisToolData tempData = new DeployAnalysisToolData(tempPath);

                _data.setLocalToolPath(tempPath);
                _path.setText(_data.getLocalAnalysisToolPath());
                _data.setToolData(tempData);

                if (_deployNew.isSelected())
                {
                    _data.setNewDeployment(true);
                }
                else
                {
                    String id = DeployUtilities.getValidRedeployAnalysisToolId(this, _data.getServerConnection(),
                            tempData, tempPath);
                    if (!id.equals(""))
                    { //redeployment is allowed
                        _data.setNewDeployment(false);
                        _data.setRedeployAnalysisToolId(id);
                        synchronizeDataWithServer();
                    }
                    else
                    { // cannot redeploy, so reset the gui to new deploy;
                        _deployNew.removeActionListener(_deployTypeListener);
                        _deployNew.setSelected(true);
                        _deployNew.addActionListener(_deployTypeListener);
                        _data.setNewDeployment(true);
                    }
                }
                _deployGui.setFarthestCompleted(1);
                _deployGui.successfulCompletion();
            }
        }
	}

    private void synchronizeDataWithServer()
	{
		DeployUtilities.synchronizeAnalysisToolDataWithServer(_data.getServerConnection(), _data.getRedeployAnalysisToolId(),
		                                                 _data.getToolData());
		_data.setDescription(_data.getToolData().getDescription());
		_toolDescription.setText(_data.getDescription());
		_toolDescription.setBackground(Color.white);
	}
}
