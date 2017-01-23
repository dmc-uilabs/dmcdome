package iproject;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 25, 2003
 * Time: 2:51:44 PM
 * To change this template use Options | File Templates.
 */

import mit.cadlab.dome3.swing.Templates;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * This class is used to browse playspaces in run mode
 */
public class DefinitionPanel extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(500,400);
	public static final GridBagConstraints gbc = null;

	private JTable availableTable = new JTable(); //Just a temporary place holder
	private JTable integrationTable = new JTable();
	private JButton integrationUpButton;
	private JButton integrationDownButton;
	private JButton availableUpButton;
	private JButton availableDownButton;
	
	private DefinitionPanel() {

		JPanel i = makeIntegrationPanel();
		JPanel a = makeAvailablePanel();
		JSplitPane p = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		p.add(i, JSplitPane.TOP);
		p.add(a, JSplitPane.BOTTOM);

		JComponent[] comps = {p};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

        Templates.layoutGridBag(this, comps, gbcs);
		p.resetToPreferredSizes();
		p.setDividerLocation(150);
	}

	private JPanel makeIntegrationPanel()
	{
		JPanel p = new JPanel();

		JLabel integrationLabel = Templates.makeLabel("integration models in project:");
		JScrollPane integrationScroll = new JScrollPane(integrationTable);
		integrationUpButton = Templates.makeListArrowButton("up");
		integrationDownButton = Templates.makeListArrowButton("down");

		JComponent[] comps = {integrationLabel, integrationScroll, integrationUpButton, integrationDownButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 2, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 2, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(2, 2, 0, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}


	private JPanel makeAvailablePanel()
	{
		JPanel p = new JPanel();

		JLabel availableLabel = Templates.makeLabel("models and iProjects available to project:");
		JScrollPane availableScroll = new JScrollPane(availableTable);
		availableUpButton = Templates.makeListArrowButton("up");
		availableDownButton = Templates.makeListArrowButton("down");

		JComponent[] comps = {availableLabel, availableScroll, availableUpButton, availableDownButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 2, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 2, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(2, 2, 0, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Shell for iProject definition Panel");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DefinitionPanel());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}

}
