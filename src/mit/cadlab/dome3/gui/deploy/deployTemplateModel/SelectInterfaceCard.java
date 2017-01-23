package mit.cadlab.dome3.gui.deploy.deployTemplateModel;

import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceSelectionTable;
import mit.cadlab.dome3.gui.deploy.components.DeployModelData;
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

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 8:35:48 PM
 * To change this template use Options | File Templates.
 */
public class SelectInterfaceCard extends JPanel
{
	public static final GridBagConstraints gbc = null;
	JPanel interfaceSelectionCard = new JPanel();
	DeployModelTemplate data;
	DeployModelGui deployGui;

	public PropertyChangeListener selectListener = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			Integer i = (Integer) evt.getNewValue();
			data.setInterfacePermissionsCreated(false); //needed so interface permissions cards will be recreated
			if (i.intValue() == 0)
				deployGui.enableNextButton(false);
			else {
				deployGui.enableNextButton(true);
				deployGui.successfulCompletion();
			}
		}
	};

	public void setSelectInterfacePanel()
	{
		CardLayout2 layout = (CardLayout2) interfaceSelectionCard.getLayout();
		/* this is the better way to do it, but it causes a swing crash (see test2 file)
		if (layout.getActiveComponent() != null)
			layout.remove(interfaceSelectionCard, "interfacePanel");
		interfaceSelectionCard.add("interfacePanel", new DeployInterfaceSelectionTable(data.getModelData()));
		layout.first(interfaceSelectionCard);
		*/
		interfaceSelectionCard.add("interfacePanel", new DeployInterfaceSelectionTable(data.getModelData()));
		layout.last(interfaceSelectionCard);
		registerInterfaceSelectionListener();
		interfaceSelectionCard.validate();
	}

	/**
	 * Used to init the panel after a successful deploy
	 */
	public void initGui()
	{
		unregisterInterfaceSelectionListener();
		//if (((CardLayout2) interfaceSelectionCard.getLayout()).getActiveComponent() != null)
		//	interfaceSelectionCard.remove(0);
	}

	public void registerInterfaceSelectionListener()
	{
		data.getModelData().addPropertyChangeListener(DeployModelData.NUM_AVAILABLE, selectListener);
	}

	public void unregisterInterfaceSelectionListener()
	{
		if (data.getModelData() != null)
			data.getModelData().removePropertyChangeListener(DeployModelData.NUM_AVAILABLE, selectListener);
	}

	public SelectInterfaceCard(DeployModelTemplate deployData, DeployModelGui gui)
	{
		data = deployData;
		deployGui = gui;
		data.setSelectInterfaceCard(this);

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("Which interfaces do you want to make available?", Templates.FONT12B);

		interfaceSelectionCard.setLayout(new CardLayout2());

		JComponent[] comps = {msg1, interfaceSelectionCard};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Deploy login card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SelectInterfaceCard(null, null));
		f.show();
	}
}
