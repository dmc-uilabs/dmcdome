package mgsCadCustomGui;

import mit.cadlab.dome.swing.Templates;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 20, 2003
 * Time: 11:20:40 PM
 * To change this template use Options | File Templates.
 */
public class MgsCadCustomGui extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(300, 400);
	public static final GridBagConstraints gbc = null;

	private JTextField bPillarHeight;
	private JLabel bPillarUnit;
	private JTextField glassRadius;
	private JLabel glassRadiusUnit;

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
		JLabel windowTitle = Templates.makeLabel("MGS glass model", Templates.FONT12B);

		JLabel inputLabel = Templates.makeLabel("Geometry inputs:", Templates.FONT11B);
        JLabel bPillarLabel = Templates.makeLabel("bPillar:");
		bPillarHeight = Templates.makeTextField("");
        bPillarUnit = Templates.makeLabel("unit");

        JLabel outputLabel = Templates.makeLabel("Geometry outputs:", Templates.FONT11B);
        JLabel glassRadiusLabel = Templates.makeLabel("glass radius:");
        glassRadius = Templates.makeTextField("");
        glassRadiusUnit = Templates.makeLabel("unit");

        JScrollPane vrmlPane = new JScrollPane();
		JComponent[] comps = {windowTitle,
		                      inputLabel,
                              bPillarLabel, bPillarHeight, bPillarUnit,
                              outputLabel,
                              glassRadiusLabel, glassRadius, glassRadiusUnit,
                              new JPanel(),
                              vrmlPane
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

			new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 100, 0),
            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 100, 0),
            new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 5, 3, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),

            new GridBagConstraints(3, 0, 1, 6, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(5, 10, 5, 5), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Custom MGS CAD GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new MgsCadCustomGui());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
