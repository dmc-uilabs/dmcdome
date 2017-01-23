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
import javax.swing.JCheckBox;
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
public class AirCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField cfm;
    private JTextField extractor;

    private JCheckBox seals;
    private JCheckBox latch;
    private JCheckBox windowDrop;

	public AirCard()
	{
        JComponent[] comps = {makeTopPanel(), makeAirPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel latchLabel = Templates.makeLabel("Air", Templates.FONT12B);
        JTextArea latchText1 = Templates.makeDTextArea("Air pressure effects are used in the ADAMS model. They are approximated from physical testing of the WIN88, DN101, and the SN95.\n\n"+
                                                "Testing results were used to define a force which is a function of the door velocity acting at the door center. " +
                                                "The equation accounts for door area, vehicle flow rate, air extractors, dual seals, latch face vents, and power window drops.");
        latchText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/air.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("air nomenclature");

        JComponent[] comps = {latchLabel, latchText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeAirPanel() {
        JPanel p = new JPanel();

        JLabel cfmLabel = Templates.makeLabel("Body air leakage rate (ft^3/min)");
        cfmLabel.setToolTipText("Leakage rate, excluding the extractor.");
        cfm = Templates.makeTextField("");

        JLabel extractorLabel = Templates.makeLabel("Extractor leakage rate (ft^3/min)");
        extractorLabel.setToolTipText("Leakage rate. If not known, set to zero and use an overall body leakage rate above");
        extractor = Templates.makeTextField("");

        seals = Templates.makeCheckBox("use complete dual seal",false);
        seals.setToolTipText("Indicates whether or not there are complete dual seals in your system.");

        latch = Templates.makeCheckBox("use latch face vent", false);
        latch.setToolTipText("Indicates whether or not there is a latch face vent in your system.");

        windowDrop = Templates.makeCheckBox("use power window drop", false);
        windowDrop.setToolTipText("Indicates whether or not there is a power window drop when the door is closing.");

        JPanel fill = new JPanel();

        JComponent[] comps = {cfmLabel, cfm,
                              extractorLabel, extractor,
                              seals,
                              latch,
                              windowDrop,
                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "CFM", cfm);
        CustomGui.connectStringOrNumberTextField(iface, "Extractor", extractor);

        CustomGui.connectBooleanCheckBox(iface,"Complete Dual Seals", seals);
        CustomGui.connectBooleanCheckBox(iface, "Latch Face Vent", latch);
        CustomGui.connectBooleanCheckBox(iface, "Power Window Drop", windowDrop);

    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Air card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new AirCard());
		f.show();
	}
}
