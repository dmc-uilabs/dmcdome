package feaGUI;

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

    private JTextField sealFlat;
    private JTextField cornerRadius;
    private JTextField sealWidth;
    private JTextField sealHeight;
    private JTextField nominalGap;

    private JTextField C10;
    private JTextField C01;

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
        JLabel analysisLabel = Templates.makeLabel("Parametric 2D body/door seal CLD analysis tool", Templates.FONT12B);
        JTextArea analysisText1 = Templates.makeDTextArea("This tool illustrates the combination of 2D parametric CATIA geometry with non-linear FEA seal analysis in ABAQUS. The door seal FEA model was implemented at MIT for demonstration purposes. "+
                                                "A small subset of the body and door seal design parameters may be changed. The door geometry has been fixed in this application.\n\n" +
                                                "The Mooney-Rivlin constants are set to values for a typical rubber-like material. They are included to indicate the possibility of changing materials, but we do not expect most users to be familiar with these constants. "+
                                                 "In a production tool, where actual material options are known, a combination box of material choices would be provided instead of the material constants.");
        analysisText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("feaGUI/sealDeformed.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("Illustration of a typical seal in its deformed state.");

        JComponent[] comps = {analysisLabel, analysisText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeConfigPanel() {
        JPanel p = new JPanel();

        JLabel bodyLabel = Templates.makeLabel("Body", Templates.FONT12B);
        bodyLabel.setToolTipText("Input parameters for the body sheet metal");

        JLabel sealFlatLabel = Templates.makeLabel("Body flat (13 to <17 mm): ");
        sealFlatLabel.setToolTipText("Allowable body seal flat range is 13 to <17 mm");
        sealFlat = Templates.makeTextField("");

        JLabel cornerRadiusLabel = Templates.makeLabel("Corner radius (3 to <6 mm): ");
        cornerRadiusLabel.setToolTipText("Allowable corner radius range is 3 to <6 mm");
        cornerRadius = Templates.makeTextField("");

        JLabel sealLabel = Templates.makeLabel("Primary door seal", Templates.FONT12B);
        sealLabel.setToolTipText("Input parameters for the door seal");

        JLabel sealWidthLabel = Templates.makeLabel("Seal width (19 to <22 mm): ");
        sealWidthLabel.setToolTipText("Allowable seal width range is 19 to <22 mm");
        sealWidth = Templates.makeTextField("");

        JLabel sealHeightLabel = Templates.makeLabel("Seal height (12 to <14 mm): ");
        sealHeightLabel.setToolTipText("Allowable seal height range is 12 to <14 mm");
        sealHeight = Templates.makeTextField("");

        JLabel nominalGapLabel = Templates.makeLabel("Seal gap (8 to <10 mm): ");
        nominalGapLabel.setToolTipText("Allowable seal gap range is 8 to <10 mm");
        nominalGap = Templates.makeTextField("");

        ImageIcon image = Templates.makeImageIcon("feaGUI/feaAnnotated.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("Geometry nomenclature. Dots denote anchor points from which relative dimension changes are made.");


        JPanel fill = new JPanel();

        JComponent[] comps = {bodyLabel,
                              sealFlatLabel, sealFlat,
                              cornerRadiusLabel, cornerRadius,

                              sealLabel,
                              sealWidthLabel, sealWidth,
                              sealHeightLabel, sealHeight,
                              nominalGapLabel, nominalGap,

                              makeMaterialPanel(),

                              imageLabel,

                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),

            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),

            new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 0, 1, 7, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(11, 5, 0, 0), 0, 0),

            new GridBagConstraints(1, 8, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeMaterialPanel() {
        JPanel p = new JPanel();
        JLabel materialLabel = Templates.makeLabel("Hyper elastic material properties", Templates.FONT12B);
        materialLabel.setToolTipText("Constants for the Mooney-Rivlin equation");

        JLabel C10Label = Templates.makeLabel("Strain energy coefficient C10 (0 to 100): ");
        C10Label.setToolTipText("C10 constant in Mooney-Rivlin equation");
        C10 = Templates.makeTextField("");

        JLabel C01Label = Templates.makeLabel("Strain energy coefficient C01 (0 to 100): ");
        C01Label.setToolTipText("C01 constant in Mooney-Rivlin equation");
        C01 = Templates.makeTextField("");

        JComponent[] comps = {
                               materialLabel,
                              C10Label, C10,
                              C01Label, C01
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "Door seal width", sealWidth);
        CustomGui.connectStringOrNumberTextField(iface, "Door seal height", sealHeight);
        CustomGui.connectStringOrNumberTextField(iface, "Seal gap", nominalGap);
        CustomGui.connectStringOrNumberTextField(iface, "Body corner radius", cornerRadius);
        CustomGui.connectStringOrNumberTextField(iface, "Body seal flat", sealFlat);

        C01.setEditable(false);
        C10.setEditable(false);
        CustomGui.connectStringOrNumberTextField(iface, "Mooney Riven Coefficient C10", C10);
        CustomGui.connectStringOrNumberTextField(iface, "Mooney Riven Coefficient C01", C01);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ConfigCard());
		f.show();
	}
}
