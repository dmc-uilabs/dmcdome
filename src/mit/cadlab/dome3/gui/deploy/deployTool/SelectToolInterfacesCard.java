package mit.cadlab.dome3.gui.deploy.deployTool;

import mit.cadlab.dome3.swing.Templates;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployToolInterfaceSelectionTable;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Oct 17, 2003
 * Time: 3:21:48 PM
 * To change this template use Options | File Templates.
 */
public class SelectToolInterfacesCard extends JPanel
{
    public static final GridBagConstraints gbc = null;

	private DeployAnalysisTool _data;
	private DeployToolGui _deployGui;
	private JPanel interfaceSelectionCard = new JPanel();

    public PropertyChangeListener selectListener = new PropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (_deployGui.getFarthestCompleted() > DeployToolGui.SELECT_TOOL_INTERFACE_CARD + 1)
            {
                _data.setAvailableToolInterfacesChanged(true); //needed so interface permissions cards will be recreated
            }
        }
    };

    public SelectToolInterfacesCard(DeployAnalysisTool data, DeployToolGui deployGui)
    {
        _data = data;
        _deployGui = deployGui;
        if (data != null) _data.setSelectToolInterfaceCard(this);
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};

		Templates.layoutGridBag(this, comps, gbcs);
    }

    private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel msg1 = Templates.makeLabel("which tool interfaces do you want to make available?", Templates.FONT12B);

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

    public void setSelectToolInterfacesCard()
	{
		List interfaces = _data.getToolData().getToolInterfaces();

		if (interfaces == null || interfaces.isEmpty())
        {
            JPanel temp = new JPanel();
            JLabel msg = Templates.makeLabel("\nthis analysis tool does not have any interfaces.");
            JPanel fill = new JPanel();
            JComponent[] comps = {msg, fill};
            // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
            GridBagConstraints[] gbcs = {
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
            };
            Templates.layoutGridBag(temp, comps, gbcs);
            interfaceSelectionCard.add("tempPanel", temp);
        }
        else
        { // create the table and put it in the card
            interfaceSelectionCard.add("toolInterfacePanel", new DeployToolInterfaceSelectionTable(_data.getToolData()));
        }
		CardLayout2 layout = (CardLayout2) interfaceSelectionCard.getLayout();
		layout.last(interfaceSelectionCard);
		interfaceSelectionCard.validate();
		registerInterfaceSelectionListener();
		_deployGui.successfulCompletion(); // no input is required for this stage.
	}

    public void registerInterfaceSelectionListener()
	{
		_data.getToolData().addPropertyChangeListener(DeployAnalysisToolData.NUMBER_TOOL_INTERFACES_AVAILABLE, selectListener);
	}


}
