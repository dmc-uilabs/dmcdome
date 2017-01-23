package RuleOfMixturesGUITest;

import mit.cadlab.dome.swing.Templates;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 20, 2003
 * Time: 11:20:40 PM
 * To change this template use Options | File Templates.
 */
public class CompleteInterfaceGui extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(300, 400);
	public static final GridBagConstraints gbc = null;

	private JTextField fiberYoungValue;
	private JTextField fiberPoissonValue;
	private JTextField fiberVolumeValue;
	private JTextField matrixYoungValue;
	private JTextField matrixPoissonValue;
	private JTextField modulusOneValue;
	private JTextField modulusTwoValue;
	private JTextField shearModulusValue;
	private JTextField poissonOneTwoValue;
	private JButton submitButton;

	{
		JPanel p = makePanel();

		JComponent[] comps = {p};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();
		JLabel windowTitle = Templates.makeLabel("Rule of Mixtures Model", Templates.FONT12B);
		ImageIcon graphic = Templates.makeImageIcon("carbonfiber.gif");
		JLabel graphicLabel = new JLabel(graphic, SwingConstants.LEFT);
		graphicLabel.setOpaque(false);

		JLabel fiberTitle = Templates.makeLabel("Fibre inputs", Templates.FONT11B);
		JLabel fiberYoung = Templates.makeLabel("Young's modulus:");
		fiberYoungValue = Templates.makeDTextField();
		JLabel fiberPoisson = Templates.makeLabel("Poisson ratio:");
		fiberPoissonValue = Templates.makeDTextField();
		JLabel fiberVolume = Templates.makeLabel("volume fraction:");
		fiberVolumeValue = Templates.makeDTextField();

		JLabel matrixTitle = Templates.makeLabel("Matrix inputs", Templates.FONT11B);
		JLabel matrixYoung = Templates.makeLabel("Young's modulus:");
		matrixYoungValue = Templates.makeDTextField();
		JLabel matrixPoisson = Templates.makeLabel("Poisson ratio:");
		matrixPoissonValue = Templates.makeDTextField();

		JLabel laminaTitle = Templates.makeLabel("Lamina properties", Templates.FONT11B);
		JLabel modulusOne = Templates.makeLabel("modulus 1:");
		modulusOneValue = Templates.makeDTextField("");
		modulusOneValue.setEditable(false);
		JLabel modulusTwo = Templates.makeLabel("modulus 2:");
		modulusTwoValue = Templates.makeDTextField("");
		modulusTwoValue.setEditable(false);
		JLabel shearModulus = Templates.makeLabel("shear modulus 12:");
		shearModulusValue = Templates.makeDTextField("");
		shearModulusValue.setEditable(false);
		JLabel poissonOneTwo = Templates.makeLabel("Poisson ratio 12:");
		poissonOneTwoValue = Templates.makeDTextField("");
		poissonOneTwoValue.setEditable(false);

		submitButton = Templates.makeButton("submit");

		JComponent[] comps = {windowTitle, graphicLabel,
		                      fiberTitle, fiberYoung, fiberYoungValue, fiberPoisson, fiberPoissonValue, fiberVolume, fiberVolumeValue,
		                      matrixTitle, matrixYoung, matrixYoungValue, matrixPoisson, matrixPoissonValue,
		                      laminaTitle, modulusOne, modulusOneValue, modulusTwo, modulusTwoValue, shearModulus, shearModulusValue, poissonOneTwo, poissonOneTwoValue,
		                      submitButton
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 5), 0, 0),

			new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

			new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

			new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 9, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 10, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 11, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 12, 2, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(2, 13, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Custom rule of Mixtures GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new CompleteInterfaceGui());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
