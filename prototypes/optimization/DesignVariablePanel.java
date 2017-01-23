package optimization;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 25, 2003
 * Time: 2:51:44 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class DesignVariablePanel extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(500,400);
	public static final GridBagConstraints gbc = null;

	private JTable designVariableTable = new JTable(); //Just a temporary place holder
	private JButton upButton;
	private JButton downButton;
	
	private DesignVariablePanel() {

		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
        Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		JScrollPane variableScroll = new JScrollPane(designVariableTable);
		upButton = Templates.makeListArrowButton("up");
		downButton = Templates.makeListArrowButton("down");

		JComponent[] comps = {variableScroll, upButton, downButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 2, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(2, 2, 0, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("optimization design variable panel");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DesignVariablePanel());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
