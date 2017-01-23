package mit.cadlab.dome3.gui.deploy.deployProject;

import mit.cadlab.dome3.gui.deploy.components.DeployProjectData;
import mit.cadlab.dome3.gui.deploy.components.DeployProjectInterfaceSelectionTable;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Apr 2, 2003
 * Time: 11:04:39 AM
 * To change this template use Options | File Templates.
 */
public class SelectProjectInterfacesCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

	private DeployProject data;
	private DeployProjectGui deployGui;
	private JPanel interfaceSelectionCard = new JPanel();


	public PropertyChangeListener selectListener = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (deployGui.getFarthestCompleted() > deployGui.SELECT_PROJECT_INTERFACE_CARD + 1) {
				data.setAvailableProjectInterfacesChanged(true); //needed so interface permissions cards will be recreated
			}
		}
	};

	public void setSelectProjectInterfacesCard()
	{
		List interfaces = data.getProjectData().getInterfaces();

		if (interfaces == null || interfaces.isEmpty()) {
			JPanel temp = new JPanel();
			JLabel msg = Templates.makeLabel("\nThis iProject does not have any interfaces.");
			JPanel fill = new JPanel();
			JComponent[] comps = {msg, fill};
			// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
			GridBagConstraints[] gbcs = {
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
				new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
			};
			Templates.layoutGridBag(temp, comps, gbcs);
			interfaceSelectionCard.add("tempPanel", temp);
		} else { // create the table and put it in the card
			interfaceSelectionCard.add("projectInterfacePanel", new DeployProjectInterfaceSelectionTable(data.getProjectData()));
		}
		CardLayout2 layout = (CardLayout2) interfaceSelectionCard.getLayout();
		layout.last(interfaceSelectionCard);
		interfaceSelectionCard.validate();
		registerInterfaceSelectionListener();
		deployGui.successfulCompletion(); // no input is required for this stage.
	}

	public void registerInterfaceSelectionListener()
	{
		data.getProjectData().addPropertyChangeListener(DeployProjectData.NUM_PROJECT_INT_AVAILABLE, selectListener);
	}

	public SelectProjectInterfacesCard(DeployProject deployData, DeployProjectGui gui)
	{
		data = deployData;
		deployGui = gui;
		if (data != null) data.setSelectProjectInterfaces(this);
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Which project interfaces do you want to make available?", Templates.FONT12B);

		interfaceSelectionCard.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, interfaceSelectionCard};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(10, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy select project interfaces card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SelectProjectInterfacesCard(null, null));
		f.show();
	}
}
