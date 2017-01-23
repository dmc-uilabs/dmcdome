package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 3:50:44 PM
 * To change this template use Options | File Templates.
 */
public class DeployInterfaceSelectionTableTest
{

	public static void main(String[] args)
	{

		DomeInit.initializeDOME();
		DomeFileChooser fileChooser = new DomeFileChooser();
		String localFileName = fileChooser.showOpenDialog(null, DomeModel.TYPE_INFO.getTypeName());
		if (localFileName == null)
			return; // cancelled
		ServerConnection conn = LoginPrompt.showDialog(null);

		DeployModelData model = DeployUtilities.loadModelForDeploy(localFileName);
		String parentId = model.getDeployId();  // todo: change this to deploy id of model
		model.addPropertyChangeListener(DeployModelData.NUM_AVAILABLE, new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				Integer i = (Integer) evt.getNewValue();
				System.out.println(i.intValue());
			}
		});

		DeployInterfaceSelectionTable tableInterface = new DeployInterfaceSelectionTable(model);
		JFrame f = new JFrame("Model Browser");
		Container cc = f.getContentPane();
		cc.setLayout(new BorderLayout());
		cc.add(tableInterface, BorderLayout.CENTER);
		f.pack();
		f.show();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DeployUtilities.synchronizeModelDataWithServer(conn, parentId, model);

		f.repaint();

	}
}


