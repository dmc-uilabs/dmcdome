package mit.cadlab.dome3.gui.deploy.components;

import com.sun.java.CardLayout2;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 13, 2003
 * Time: 3:50:44 PM
 * To change this template use Options | File Templates.
 */
public class DeployIntegrationModelInterfaceSelectionTableTest
{
	private static JFrame f = new JFrame();
	public static final GridBagConstraints gbc = null;

	public static PropertyChangeListener c = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			Integer i = (Integer) evt.getNewValue();
			System.out.println(i.intValue());
		}
	};


	public static ListSelectionListener lsl = new ListSelectionListener()
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting()) return;

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (lsm.isSelectionEmpty()) {
			} else {
				int selectedRow = lsm.getMinSelectionIndex();
				//System.out.println("selectedRow = " + selectedRow);
			}
		}
	};

	private static JPanel card = new JPanel();
	static boolean flag = true; //for "O" card
	static DeployIntegrationModelInterfaceSelectionTable table;
	static DeployIntegrationModelInterfaceSelectionTable table2;

	public static void main(String[] args)
	{

		DomeInit.initializeDOME();

		DomeFileChooser fc = new DomeFileChooser();
		String localFileName = fc.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);
		if (localFileName == null)
			return; // cancelled
		DeployProjectData project = new DeployProjectData(localFileName);
		if (project == null) {
			System.err.println("unable to load project at " + localFileName);
			return;
		}
		//DeployProjectData project = new DeployProjectData("~//DomeModelFilesForTesting//project1.dpj");//DeployUtilities.("C://dome//dome//DomeModelFilesForTesting//ruleOfMixtures//ruleOfMixturesII-DOME.dml");
		table = new DeployIntegrationModelInterfaceSelectionTable(project);
		project.addPropertyChangeListener(DeployModelData.NUM_AVAILABLE, c);

		ListSelectionModel rowSM = table._table.getSelectionModel();
		rowSM.addListSelectionListener(lsl);

		//DeployModelData model2 = DeployUtilities.loadModelForDeploy("C://dome//dome//DomeModelFilesForTesting//solvingCases//elaineTest-DOME.dml");
		//table2 = new DeployIntegrationModelInterfaceSelectionTable(model2);

		JPanel p = new JPanel();
		card.setLayout(new CardLayout2());
		card.add("name", table);

		JButton nextButton = Templates.makeButton("next", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				CardLayout2 layout = (CardLayout2) (card.getLayout());
				layout.getActiveComponent().removePropertyChangeListener(c);
				layout.remove(card, "name");
				if (flag) {
					System.out.println("setting false");
					flag = false;
					card.add("name", table2);
					layout.first(card);
				} else {
					System.out.println("setting true");
					flag = true;
					card.add("name", table);
					layout.first(card);
				}
				card.validate();
				layout.getActiveComponent().addPropertyChangeListener(c);
			}
		});

		JComponent[] comps = {card, nextButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);

		f = new JFrame("Integration Model Browser");

		f.getContentPane().add(p);

		f.pack();
		f.show();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}


