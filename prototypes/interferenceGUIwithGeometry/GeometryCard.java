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
public class GeometryCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField sealFlat;
    private JTextField cornerRadius;
    private JTextField sealWidth;
    private JTextField sealHeight;
    private JTextField nominalGap;

	public GeometryCard()
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
        JLabel geometryLabel = Templates.makeLabel("Parametric 3D body/door seal design and interference tool", Templates.FONT12B);
        JTextArea geometryText1 = Templates.makeDTextArea("This tool illustrates the combination of 3D parametric I-deas geometry with the existing Ford I-deas interference tool that is already in use. \n\n"+
                                                "A parametric version of the body/door/seal geometry was not available, so a single straight parametric extrusion of the goemetry was implemented for demonstration purposes. " +
                                                "A small subset of the body and door seal design parameters may be changed. The door geometry has been fixed in this application.");
        geometryText1.setOpaque(false);

        //ImageIcon sealImage = Templates.makeImageIcon("interferenceGUIwithGeometry/images/sealSmall.gif");
        //JLabel sealImageLabel = new JLabel(sealImage);
        //sealImageLabel.setToolTipText("Door seal geometry nomenclature");

        //ImageIcon bodyImage = Templates.makeImageIcon("interferenceGUIwithGeometry/images/bodySmall.gif");
        //JLabel bodyImageLabel = new JLabel(bodyImage);
        //bodyImageLabel.setToolTipText("Body seal geometry nomenclature");

        ImageIcon image = Templates.makeImageIcon("interferenceGUIwithGeometry/images/sealInterferenceArea.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("illustration of seal interference area at a section plane");

        JComponent[] comps = {geometryLabel, geometryText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
            //new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            //new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeConfigPanel() {
        JPanel p = new JPanel();

        JLabel bodyLabel = Templates.makeLabel("Body", Templates.FONT12B);
        bodyLabel.setToolTipText("Input parameters for the body sheet metal");

        JLabel sealFlatLabel = Templates.makeLabel("Body flat (<13 to >17 mm): ");
        sealFlatLabel.setToolTipText("Allowable body seal flat is <13 to >17mm");
        sealFlat = Templates.makeTextField("");

        JLabel cornerRadiusLabel = Templates.makeLabel("Corner radius (>3 to <6 mm): ");
        cornerRadiusLabel.setToolTipText("Allowable corner radius is >3 to <6mm");
        cornerRadius = Templates.makeTextField("");


        JLabel sealLabel = Templates.makeLabel("Primary door seal", Templates.FONT12B);
        sealLabel.setToolTipText("Input parameters for the door seal");

        JLabel sealWidthLabel = Templates.makeLabel("Seal width (>19 to <23 mm): ");
        sealWidthLabel.setToolTipText("Allowable seal width is >19 to <23mm");
        sealWidth = Templates.makeTextField("");

        JLabel sealHeightLabel = Templates.makeLabel("Seal height (>10 to <14 mm): ");
        sealHeightLabel.setToolTipText("Allowabale seal height is >10 to <14mm");
        sealHeight = Templates.makeTextField("");

        JLabel nominalGapLabel = Templates.makeLabel("Seal gap (>7 to <10 mm): ");
        nominalGapLabel.setToolTipText("Allowable seal gap range is >7 to <10 mm");
        nominalGap = Templates.makeTextField("");

        ImageIcon image = Templates.makeImageIcon("interferenceGUIwithGeometry/images/interferenceAnnotated.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("Geometry nomenclature. Dots denote anchor points from which relative dimension changes are made");



        JPanel fill = new JPanel();

        JComponent[] comps = {bodyLabel,
                              sealFlatLabel, sealFlat,
                              cornerRadiusLabel, cornerRadius,

                              sealLabel,
                              sealWidthLabel, sealWidth,
                              sealHeightLabel, sealHeight,
                              nominalGapLabel, nominalGap,

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

            new GridBagConstraints(0, 0, 1, 7, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),

            new GridBagConstraints(1, 7, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {

        CustomGui.connectStringOrNumberTextField(iface, "body seal-flat surface width", sealFlat);
        CustomGui.connectStringOrNumberTextField(iface, "body surface corner radius", cornerRadius);
        CustomGui.connectStringOrNumberTextField(iface, "seal width",sealWidth);
        CustomGui.connectStringOrNumberTextField(iface, "seal height", sealHeight);
        CustomGui.connectStringOrNumberTextField(iface, "seal gap", nominalGap);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new GeometryCard());
		f.show();
	}
}
