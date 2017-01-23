package interferenceGUIwithGeometry;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
public class ConfigCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField sectionSpacing;
    private JTextField xOffset;
    private JTextField yOffset;
    private JTextField zOffset;

	public ConfigCard()
	{
        JComponent[] comps = {makeTopPanel(), makeConfigPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel analysisLabel = Templates.makeLabel("Interference Analysis", Templates.FONT12B);
        JTextArea analysisText1 = Templates.makeDTextArea("The interference analysis measures the area of interference between the weatherstrip bulb and the sealing surfaces.");
        analysisText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("interferenceGUIwithGeometry/images/sealInterferenceArea.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("illustration of seal interference area at a section plane");

        JComponent[] comps = {analysisLabel, analysisText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeConfigPanel() {
        JPanel p = new JPanel();

        JLabel sectionSpacingLabel = Templates.makeLabel("Cross section spacing (1 to 60 mm): ");
        sectionSpacingLabel.setToolTipText("Spacing of the section planes along the section line. Sections are perpendicular to the line");
        sectionSpacing = Templates.makeTextField("");

        JLabel offsetLabel = Templates.makeLabel("Offset from nominal position", Templates.FONT12B);
        offsetLabel.setToolTipText("Position of door from nominal as designed location. Only global translations are supported");

        JLabel xOffsetLabel = Templates.makeLabel("X direction offset (mm): ");
        xOffsetLabel.setToolTipText("X is in the fore/aft direction (positive points towards the aft direction");
        xOffset = Templates.makeTextField("");

        JLabel yOffsetLabel = Templates.makeLabel("Y direction offset (mm): ");
        yOffsetLabel.setToolTipText("Y is inboard/outboard (positive points outboard)");
        yOffset = Templates.makeTextField("");

        JLabel zOffsetLabel = Templates.makeLabel("Z direction offset (mm): ");
        zOffsetLabel.setToolTipText("Z is up/down (positive points upward)");
        zOffset = Templates.makeTextField("");

        JPanel fill = new JPanel();

        JComponent[] comps = {sectionSpacingLabel, sectionSpacing,
                              offsetLabel,
                              xOffsetLabel, xOffset,
                              yOffsetLabel, yOffset,
                              zOffsetLabel, zOffset,
                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "X offset", xOffset);
        CustomGui.connectStringOrNumberTextField(iface, "Y offset", yOffset);
        CustomGui.connectStringOrNumberTextField(iface, "Z offset", zOffset);
        CustomGui.connectStringOrNumberTextField(iface, "cross section distance", sectionSpacing);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ConfigCard());
		f.show();
	}
}
