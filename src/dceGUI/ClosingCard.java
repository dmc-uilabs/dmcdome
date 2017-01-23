package dceGUI;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 4:26:11 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card for the login step in deployment
 */
public class ClosingCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    JCheckBox useCg;

    JTextField posX;
    JTextField posY;
    JTextField posZ;

    JTextField stiffness;
    JTextField preload;

	public ClosingCard()
	{
        JComponent[] comps = {makeTopPanel(), makeLatchPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel closingLabel = Templates.makeLabel("Closing Spring", Templates.FONT12B);
        JTextArea closingText1 = Templates.makeDTextArea("The closing spring stiffness, preload, and location is defined here.\n\n" +
                                                "Use the door CG for the spring location only if you are unsure of the correct location.");
        closingText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("dceGUI/images/closingSpring.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("closing spring nomenclature");

        JComponent[] comps = {closingLabel, closingText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeLatchPanel() {
        JPanel p = new JPanel();

        JLabel xLabel = Templates.makeLabel("x", Templates.FONT12B);
        xLabel.setToolTipText("x is in the fore/aft direction (positive points towards the aft direction)");

        JLabel yLabel = Templates.makeLabel("y", Templates.FONT12B);
        yLabel.setToolTipText("y is inboard/outboard (positive points outboard)");

        JLabel zLabel = Templates.makeLabel("z", Templates.FONT12B);
        zLabel.setToolTipText("z is up/down (positive points upward)");

        JLabel posLabel = Templates.makeLabel("spring position (mm)");
        posLabel.setToolTipText("Location of the point where the closing spring attaches to the door.");
        posX = Templates.makeTextField("");
        posY = Templates.makeTextField("");
        posZ = Templates.makeTextField("");
        useCg = Templates.makeCheckBox("use door cg location", false);
        useCg.setToolTipText("select this option only if you do not know the correct spring attachment point");

        useCg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (useCg.isSelected())  {
                    posX.setEditable(false);
                    posY.setEditable(false);
                    posZ.setEditable(false);
                }
                else {
                    posX.setEditable(true);
                    posY.setEditable(true);
                    posZ.setEditable(true);
                }
            }
        });

        JLabel stiffnessLabel = Templates.makeLabel("spring stiffness (lbf/ft)");
        stiffnessLabel.setToolTipText("Stiffness of the closing spring (30.1 is typical)");
        stiffness = Templates.makeTextField("");

        JLabel preloadLabel = Templates.makeLabel("spring preload (lbf)");
        preloadLabel.setToolTipText("Preload of the spring when the door is closed (5 is typical)");
        preload = Templates.makeTextField("");

        JPanel fill = new JPanel();

        JComponent[] comps = {xLabel, yLabel, zLabel,
                              posLabel, posX, posY, posZ, useCg,
                              stiffnessLabel, stiffness,
                              preloadLabel, preload,
                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(2, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(1, 5, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectBooleanCheckBox(iface, "SameAsCG", useCg);

        CustomGui.connectStringOrNumberTextField(iface,"x_Position", posX);
        CustomGui.connectStringOrNumberTextField(iface, "y_Position", posY);
        CustomGui.connectStringOrNumberTextField(iface, "z_Position", posZ);

        CustomGui.connectStringOrNumberTextField(iface, "Closing Spring Stiffness", stiffness);
        CustomGui.connectStringOrNumberTextField(iface, "Closing Spring Preload", preload);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Closing card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ClosingCard());
		f.show();
	}
}
