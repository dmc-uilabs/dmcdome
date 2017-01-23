package fullWindNoiseToolGUI;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JComboBox;
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
public class LatchCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField posX;
    private JTextField posY;
    private JTextField posZ;

    private JTextField offset;

    private JComboBox latchType;

	public LatchCard()
	{
        JComponent[] comps = {makeTopPanel(), makeLatchPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makeTopPanel()
	{
		JPanel p = new JPanel();
        JLabel latchLabel = Templates.makeLabel("Door Latch", Templates.FONT12B);
        JTextArea latchText1 = Templates.makeDTextArea("The latch affects the door closing effort simulations. It is a simplified force versus deflection curve that was generated from a dynamic model of the latch. " +
                               "The force and overslam distance are adjusted in according to the type of latch. Energy to overcome increased friction due to striker offset is added to the latch force.\n\n" +
                               "In a production tool, the geometric inputs would probably be driven by a CAD model. ");
        latchText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/latch.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("latch nomenclature");

        JComponent[] comps = {latchLabel, latchText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(3, 5, 0, 5), 0, 0),
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

        JLabel posLabel = Templates.makeLabel("latch position (mm)");
        posLabel.setToolTipText("Position of the latch. See the reference image for the numbering convention.");
        posX = Templates.makeTextField("");
        posY = Templates.makeTextField("");
        posZ = Templates.makeTextField("");

        JLabel typeLabel = Templates.makeLabel("latch type");
        typeLabel.setToolTipText("Standard latches.Ê Each latch is defined in the model by a three-dimensional force-deflection curve.");

        latchType = Templates.makeComboBox(new String[] {"D5","D21","mini"});
        latchType.setToolTipText("Standard latches.Ê Each latch is defined in the model by a three-dimensional force-deflection curve");

        JLabel offsetLabel = Templates.makeLabel("offset (mm)");
        offsetLabel.setToolTipText("Offset of the striker with respect to the nominal position.Ê A positive value indicates that the door must move upward to align with the striker.");
        offset = Templates.makeTextField("");

        JPanel fill = new JPanel();

        JComponent[] comps = {xLabel, yLabel, zLabel,
                              posLabel, posX, posY, posZ,
                              typeLabel, latchType,
                              offsetLabel, offset,
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

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(1, 4, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "x_Latch", posX);
        CustomGui.connectStringOrNumberTextField(iface, "y_Latch", posY);
        CustomGui.connectStringOrNumberTextField(iface, "z_Latch", posZ);

        CustomGui.connectStringOrNumberTextField(iface, "Striker Offset", offset);

        CustomGui.connectEnumerationComboBox(iface, "Latch Type", latchType);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Latch card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new LatchCard());
		f.show();
	}
}
