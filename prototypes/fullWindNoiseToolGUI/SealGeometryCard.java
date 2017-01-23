package fullWindNoiseToolGUI;

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
import javax.swing.JCheckBox;

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
public class SealGeometryCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField sealFlat;
    private JTextField cornerRadius;
    private JTextField sealWidth;
    private JTextField sealHeight;
    private JTextField nominalGap;

    private JCheckBox showGeometry;

	public SealGeometryCard()
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
        JLabel analysisLabel = Templates.makeLabel("Parametric body/seal CAD geometry", Templates.FONT12B);
        JTextArea analysisText1 = Templates.makeDTextArea("This panel is used to configure the door (primary) seal and outer body geometry. " +
                "A small subset of the body and seal parameters may be changed. " +
                "These parameters affect a 3D parametric CATIA model of the body/seal/door segment, the parametric I-deas CAD model that is used for seal " +
                "interference analysis, and the 2D CATIA parametric geometry used by the non-linear seal FEA analysis in Abaqus.\n\n" +
                "You may view the CATIA body/seal/door segment by checking the 'show live geometry' box, provided that you have a default program to display VRML/WRL files on your computer. " +
                "The visualization will update after each run, but is not available to you until after your first analysis. " +
                "Immediately after the first run you will be prompted to select where the WRL file should be saved on your computer.\n\n" +
                "Note: the 'show live geometry' checkbox is disabled as we are waiting for a VRML generation bug fix from Dassault.");

        analysisText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/3DCatiaGeometry.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("Illustration of a typical body/seal/door segment.");

        showGeometry = Templates.makeCheckBox("show live geometry", false, true);
        showGeometry.setToolTipText("If checked, a 3D CATIA geometry visualization will open and update in a VRML viewer");

        JPanel ip = new JPanel();
        JComponent[] iPcomps = {imageLabel, showGeometry};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] iPgbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(ip, iPcomps, iPgbcs);

        JComponent[] comps = {analysisLabel, analysisText1, ip};
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

//        showGeometry = Templates.makeCheckBox("show live geometry", false, true);
//        showGeometry.setToolTipText("If checked, a 3D CATIA geometry visualization will open and update in a VRML viewer");

        JLabel bodyLabel = Templates.makeLabel("Body", Templates.FONT12B);
        bodyLabel.setToolTipText("Input parameters for the body sheet metal");

        JLabel sealFlatLabel = Templates.makeLabel("Body flat (>13 to <17 mm): ");
        sealFlatLabel.setToolTipText("Allowable body seal flat range is >13 to <17 mm");
        sealFlat = Templates.makeTextField("");

        JLabel cornerRadiusLabel = Templates.makeLabel("Corner radius (>3 to <6 mm): ");
        cornerRadiusLabel.setToolTipText("Allowable corner radius range is >3 to <6 mm");
        cornerRadius = Templates.makeTextField("");

        JLabel sealLabel = Templates.makeLabel("Primary door seal", Templates.FONT12B);
        sealLabel.setToolTipText("Input parameters for the door seal");

        JLabel sealWidthLabel = Templates.makeLabel("Seal width (>19 to <22 mm): ");
        sealWidthLabel.setToolTipText("Allowable seal width range is >19 to <22 mm");
        sealWidth = Templates.makeTextField("");

        JLabel sealHeightLabel = Templates.makeLabel("Seal height (12 to <14 mm): ");
        sealHeightLabel.setToolTipText("Allowable seal height range is 12 to <14 mm");
        sealHeight = Templates.makeTextField("");

        JLabel nominalGapLabel = Templates.makeLabel("Seal gap (8 to <10 mm): ");
        nominalGapLabel.setToolTipText("Allowable seal gap range is 8 to <10 mm");
        nominalGap = Templates.makeTextField("");

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/bodySealAnnotated.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("Geometry nomenclature. Dots denote anchor points from which relative dimension changes are made.");


        JPanel fill = new JPanel();

        JComponent[] comps = {
                              //showGeometry,
                              bodyLabel,
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
            //new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

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

            new GridBagConstraints(0, 0, 1, 7, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(11, 5, 0, 0), 0, 0),

            new GridBagConstraints(1, 8, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
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

        showGeometry.setEnabled(false);
        //todo connect check box to the CATIA vrml
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SealGeometryCard());
		f.show();
	}
}
