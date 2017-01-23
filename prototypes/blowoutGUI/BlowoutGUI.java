package blowoutGUI;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

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
public class BlowoutGUI extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField gauge;
    private JTextField x;
    private JTextField y;
    private JTextField z;

    private JTextField theta_x;
    private JTextField theta_y;
    private JTextField theta_z;

    public BlowoutGUI(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

	public BlowoutGUI()
	{
        JComponent[] comps = {makePanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makePanel() {
        JPanel p = new JPanel();

        JPanel leftCol = makeLeftCol();

        JLabel blowoutLabel = Templates.makeLabel("Dynamic Door Deflection", Templates.FONT12B);
        JTextArea blowoutText = Templates.makeDTextArea("This CAE simulation uses CFD analysis to obtain high-speed aero-loads on the door and then applies this load to partial vehicle model (door, seals, some parts of the body structure) to measure maximum deflection (which usually occurs at upper b-pillar corner). This analysis is for the F150 geometry.");
        blowoutText.setOpaque(false);

        JLabel gaugeLabel = Templates.makeLabel("B pillar reinforcement gauge (mm)", Templates.FONT11B);
        gaugeLabel.setToolTipText("typical values 0.5-1.5 mm");
        gauge = Templates.makeTextField("");


        JLabel displacementLabel = Templates.makeLabel("Displacements at point P", Templates.FONT11B);

        JLabel xLabel = Templates.makeLabel("x (mm)");
        xLabel.setToolTipText("displacement in the x direction");
        x = Templates.makeTextField("");
        x.setEditable(false);

        JLabel theta_xLabel = Templates.makeLabel("angle x (degrees)");
        theta_xLabel.setToolTipText("rotation about the x axis");
        theta_x = Templates.makeTextField("");

        theta_x.setEditable(false);

        JLabel yLabel = Templates.makeLabel("y (mm)");
        yLabel.setToolTipText("displacement in the y direction");
        y = Templates.makeTextField("");
        y.setEditable(false);

        JLabel theta_yLabel = Templates.makeLabel("angle y (degrees)");
        theta_yLabel.setToolTipText("rotation about the y axis");
        theta_y = Templates.makeTextField("");
        theta_y.setEditable(false);

        JLabel zLabel = Templates.makeLabel("z (mm)");
        zLabel.setToolTipText("displacement in the z direction");
        z = Templates.makeTextField("");
        z.setEditable(false);

        JLabel theta_zLabel = Templates.makeLabel("angle z (degrees)");
        theta_zLabel.setToolTipText("rotation about the z axis");
        theta_z = Templates.makeTextField("");
        theta_z.setEditable(false);

        ImageIcon axis = Templates.makeImageIcon("blowoutGUI/coord.gif");
        JLabel axisLabel = new JLabel(axis);
        axisLabel.setToolTipText("analysis coordinate system");

        JComponent[] comps = {  leftCol,

                                blowoutLabel,
                                blowoutText,

                                gaugeLabel, displacementLabel,
                                gauge,  xLabel, x, theta_xLabel, theta_x,
                                axisLabel,
                                yLabel, y, theta_yLabel, theta_y,
                                zLabel, z, theta_zLabel, theta_z
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 7, 0.0, 1.0, gbc.SOUTHWEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),

            new GridBagConstraints(1, 0, 6, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 1, 6, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),

            //gauge and displacement labels
            new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(20, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 2, 4, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(20, 10, 0, 10), 0, 0),

            // first row of data
            new GridBagConstraints(1, 3, 1, 1, 0.1, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(4, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(6, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),

            new GridBagConstraints(1, 4, 1, 3, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),

            //second row of data
            new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(4, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(5, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(6, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),

            //  third row of data
            new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(4, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(5, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0),
            new GridBagConstraints(6, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeLeftCol() {
        JPanel p = new JPanel();
        p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        ImageIcon image1 = Templates.makeImageIcon("blowoutGUI/fea_nastran1.gif");
        JLabel image1Label = new JLabel(image1);
        image1Label.setToolTipText("door mesh visualization");

        ImageIcon image2 = Templates.makeImageIcon("blowoutGUI/fea_nastran4.gif");
        JLabel image2Label = new JLabel(image2);
        image2Label.setToolTipText("typical door displacement (not results for this analysis)");

        ImageIcon image = Templates.makeImageIcon("blowoutGUI/logo.gif");
        JLabel iconLabel = new JLabel(image);

        JPanel fill = new JPanel();
        fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JComponent[] comps = {image1Label, image2Label, fill,iconLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            //new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "Gage thickness", gauge);
        CustomGui.connectStringOrNumberTextField(iface, "Linear displacement x", x);
        CustomGui.connectStringOrNumberTextField(iface, "Linear displacement y", y);
        CustomGui.connectStringOrNumberTextField(iface, "Linear displacement z", z);

        CustomGui.connectStringOrNumberTextField(iface, "Angular displacement x", theta_x);
        CustomGui.connectStringOrNumberTextField(iface, "Angular displacement y", theta_y);
        CustomGui.connectStringOrNumberTextField(iface, "Angular displacement z", theta_z);
    }


	public static void main(String[] args)
	{
		JFrame f = new JFrame("Blowout custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new BlowoutGUI());
		f.show();
	}
}
