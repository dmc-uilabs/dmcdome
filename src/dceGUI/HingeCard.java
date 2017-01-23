package dceGUI;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
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
public class HingeCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField hingePositionX;
    private JTextField hingePositionY;
    private JTextField hingePositionZ;

    private JTextField fv;
    private JTextField sv;

    private JTextField[] angle;
    private JTextField[] torque;
    private static final int NUM_ANGLES = 16;
    //private JTextField maxAngle;

	public HingeCard()
	{
        JComponent[] comps = {makeTopPanel(), makeHingePanel(), makeAnglePanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(5, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel hingeLabel = Templates.makeLabel("Hinge", Templates.FONT12B);
        JTextArea hingeText1 = Templates.makeDTextArea("The hinge defines the axis of pivot for the door."+
                                                        " The axis is adjusted by the hinge angle relative to a vertical line. The lower hinge position also serves as the reference point for all geometry in the ADAMS model. Because the ADAMS model uses ideal joints, you are required only to enter the lower hinge position of the door.");
        hingeText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("dceGUI/images/hinge.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("hinge nomenclature");

        JComponent[] comps = {hingeLabel, hingeText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeHingePanel() {
        JPanel p = new JPanel();

        JLabel xLabel = Templates.makeLabel("x", Templates.FONT12B);
        xLabel.setToolTipText("x is in the fore/aft direction (positive points towards the aft direction)");

        JLabel yLabel = Templates.makeLabel("y", Templates.FONT12B);
        yLabel.setToolTipText("y is inboard/outboard (positive points outboard)");

        JLabel zLabel = Templates.makeLabel("z", Templates.FONT12B);
        zLabel.setToolTipText("z is up/down (positive points upward)");

        JLabel hingeLabel = Templates.makeLabel("lower hinge position (mm)");
        hingeLabel.setToolTipText("Position of the lower hinge on the door. Serves as a reference point for all geometry");
        hingePositionX = Templates.makeTextField("");
        hingePositionY = Templates.makeTextField("");
        hingePositionZ = Templates.makeTextField("");

        JLabel fvLabel = Templates.makeLabel("front view hinge tip angle (deg)");
        fvLabel.setToolTipText("Tip angle from the front view of the vehicle.Ê A positive angle indicates that the top of the door tips inboard.");
        fv = Templates.makeTextField("");

        JLabel svLabel = Templates.makeLabel("side view hinge tip angle (deg)");
        svLabel.setToolTipText("Tip angle from the side view of the vehicle.Ê A positive angle indicates that the top of the door tips forward (fore)");
        sv = Templates.makeTextField("");

        JComponent[] comps = {xLabel, yLabel, zLabel,
                              hingeLabel, hingePositionX, hingePositionY, hingePositionZ,
                              fvLabel, fv,
                              svLabel, sv
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
            new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeAnglePanel() {
        JPanel p = new JPanel();

        JLabel checkLabel = Templates.makeLabel("Hinge check characteristics", Templates.FONT12B);
        JLabel checkText1 = Templates.makeLabel("The hinge check force is determined by interpolating between the points specified below.");
        JLabel checkText2 = Templates.makeLabel("This data defines the angle-torque curve for the hinge.");

        JLabel angleLabel = Templates.makeLabel("angle (degrees)");
        angleLabel.setToolTipText("An angle of zero corresponds to the door in its fully closed position");

        angle = new JTextField[NUM_ANGLES];
        for (int i=0; i<NUM_ANGLES; i++) {
            angle[i] = Templates.makeTextField("");
        }

        JLabel torqueLabel = Templates.makeLabel("torgue (ft-lbf)");
        torqueLabel.setToolTipText("Torgue at specified angle. A positive torque resists closing of the door--a negative torque assists");
        torque = new JTextField[NUM_ANGLES];
        for (int i = 0; i < NUM_ANGLES; i++) {
            torque[i] = Templates.makeTextField("");
        };

        //JLabel maxAngleLabel = Templates.makeLabel("maximum angle (degrees)", Templates.FONT11B);
        //maxAngleLabel.setToolTipText("Specifies the maximum hinge angle");
        //maxAngle = Templates.makeTextField("");

        JPanel fill = new JPanel();

        JComponent[] comps = new JComponent[NUM_ANGLES*2+6];
        comps[0]=checkLabel;
        comps[1]=checkText1;
        comps[2]=checkText2;

        comps[3]=angleLabel;
        for (int i=4; i<4+NUM_ANGLES;i++){
            comps[i]= angle[i-4];
        }

        comps[4+NUM_ANGLES]=torqueLabel;
        for (int i= 5 + NUM_ANGLES; i<(5 + NUM_ANGLES)+NUM_ANGLES;i++){
            comps[i]=torque[i- (5 + NUM_ANGLES)];
        }
        //comps[5 + NUM_ANGLES + NUM_ANGLES]= maxAngleLabel;
        //comps[6 + NUM_ANGLES + NUM_ANGLES] = maxAngle;
        comps[5 + NUM_ANGLES + NUM_ANGLES]= fill;

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = new GridBagConstraints[NUM_ANGLES * 2 + 6];
        gbcs[0] =  new GridBagConstraints(0, 0, 17, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0);
        gbcs[1] = new GridBagConstraints(0, 1, 17, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[2] = new GridBagConstraints(0, 2, 17, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);

        gbcs[3] = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0);
        for (int i = 4; i < 4 + NUM_ANGLES; i++) {
            if (i< 4 + NUM_ANGLES-1)
                gbcs[i] =  new GridBagConstraints(i-3, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0);
            else
               gbcs[i] = new GridBagConstraints(i - 3, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0);
        }

        gbcs[4 + NUM_ANGLES] = new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(2, 5, 0, 0), 0, 0);
        for (int i = 5 + NUM_ANGLES; i < (5 + NUM_ANGLES) + NUM_ANGLES; i++) {
            if (i < (5 + NUM_ANGLES) + NUM_ANGLES-1)
                gbcs[i] = new GridBagConstraints(i - (5+ NUM_ANGLES-1), 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i - (5 + NUM_ANGLES - 1), 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, 0, 5), 0, 0);
        }
        //gbcs[5 + NUM_ANGLES + NUM_ANGLES] = new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0);
        //gbcs[6 + NUM_ANGLES + NUM_ANGLES] = new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0);
        gbcs[5 + NUM_ANGLES + NUM_ANGLES] = new GridBagConstraints(0, 6, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);

        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "x_Hinge", hingePositionX);
        CustomGui.connectStringOrNumberTextField(iface, "y_Hinge", hingePositionY);
        CustomGui.connectStringOrNumberTextField(iface, "z_Hinge", hingePositionZ);

        CustomGui.connectStringOrNumberTextField(iface, "FV Hinge Tip Angle", fv);
        CustomGui.connectStringOrNumberTextField(iface, "SV Hinge Tip Angle", sv);

        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 0", angle[0]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 1", angle[1]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 2", angle[2]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 3", angle[3]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 4", angle[4]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 5", angle[5]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 6", angle[6]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 7", angle[7]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 8", angle[8]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 9", angle[9]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 10", angle[10]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 11", angle[11]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 12", angle[12]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 13", angle[13]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 14", angle[14]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Angle 15", angle[15]);

        //CustomGui.connectStringOrNumberTextField(iface, "Maximum Hinge Angle", maxAngle);

        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 0", torque[0]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 1", torque[1]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 2", torque[2]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 3", torque[3]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 4", torque[4]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 5", torque[5]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 6", torque[6]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 7", torque[7]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 8", torque[8]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 9", torque[9]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 10", torque[10]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 11", torque[11]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 12", torque[12]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 13", torque[13]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 14", torque[14]);
        CustomGui.connectStringOrNumberTextField(iface, "Hinge Torque 15", torque[15]);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Hinge card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new HingeCard());
		f.show();
	}
}
