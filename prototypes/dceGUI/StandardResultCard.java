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
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

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
public class StandardResultCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    public static final int CLOSE_FORCE = 0; //to be used to access the different data arrays;
    public static final int ENERGY = 1;
    public static final int ANGLE = 2;
    public static final int VELOCITY = 3;
    public static final int LATCH_FORCE = 4;

    public static final int NUM_DATA = 5;

    JTextField[] greenNomClosed;
    JTextField[] greenNomOpen;

    JTextField[] agedNomClosed;
    JTextField[] agedNomOpen;
    JTextField[] agedInClosed;
    JTextField[] agedInOpen;
    JTextField[] agedOutClosed;
    JTextField[] agedOutOpen;

	public StandardResultCard()
	{
        JComponent[] comps = {makeTopPanel(), makeStandardPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel standardLabel = Templates.makeLabel("Standard Results", Templates.FONT12B);
        JTextArea standardText1 = Templates.makeDTextArea("For a standard analysis, the results include closing force, energy, door angle, velocity at sensor, and striker/latch force.Ê Each of these values is provided for the green seal at nominal position, and for the aged seal at nominal, inboard, and outboard conditions.");
        standardText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("dceGUI/images/standard.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("standard results nomenclature");

        JComponent[] comps = {standardLabel, standardText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JScrollPane makeStandardPanel() {
        JPanel p = new JPanel();

        JLabel greenLabel = Templates.makeLabel("Green seal", Templates.FONT12B);

        JLabel greenNomLabel = Templates.makeLabel("at Nominal condition", Templates.FONT11B);
        JLabel greenNomClosedLabel = Templates.makeLabel("windows closed");
        JLabel greenNomOpenLabel = Templates.makeLabel("windows open");

        greenNomClosed = new JTextField[NUM_DATA];
        greenNomOpen = new JTextField[NUM_DATA];
        for (int i = 0; i<NUM_DATA; i++) {
            greenNomClosed[i] = Templates.makeTextField("");
            (greenNomClosed[i]).setEditable(false);
            greenNomOpen[i] = Templates.makeTextField("");
            (greenNomOpen[i]).setEditable(false);
        }
        JLabel greenForceLabel = Templates.makeLabel("force (lbf)");
        greenForceLabel.setToolTipText("Force in the closing spring at the door open position. This is the minimum spring force required to fully close the door.");

        JLabel greenEnergyLabel = Templates.makeLabel("energy (J)");
        greenEnergyLabel.setToolTipText("The energy in the closing spring that is required to fully close the door. E=1/2*1/k*(Fspring^2-Fpreload^2).");

        JLabel greenAngleLabel = Templates.makeLabel("angle (degrees)");
        greenAngleLabel.setToolTipText("Opening angle of the door when the spring is at the minimum required closing force.");

        JLabel greenVelocityLabel = Templates.makeLabel("velocity (ft/sec)");
        greenVelocityLabel.setToolTipText("Door edge velocity when the required closing energy is applied to the door.ÊMeasured at the time when the door edge crosses the plane of the velocity sensor");

        JLabel greenLatchForceLabel = Templates.makeLabel("force on latch (lbf)");
        greenLatchForceLabel.setToolTipText("Static latching force (measured as a static force when the door is at its fully closed position).");

        JLabel agedLabel = Templates.makeLabel("Aged seal", Templates.FONT12B);

        JLabel agedNomLabel = Templates.makeLabel("at Nominal condition", Templates.FONT11B);
        JLabel agedNomClosedLabel = Templates.makeLabel("windows closed");
        JLabel agedNomOpenLabel = Templates.makeLabel("windows open");

        JLabel agedInLabel = Templates.makeLabel("at Inboard condition", Templates.FONT11B);
        JLabel agedInClosedLabel = Templates.makeLabel("windows closed");
        JLabel agedInOpenLabel = Templates.makeLabel("windows open");

        JLabel agedOutLabel = Templates.makeLabel("at Outboard condition", Templates.FONT11B);
        JLabel agedOutClosedLabel = Templates.makeLabel("windows closed");
        JLabel agedOutOpenLabel = Templates.makeLabel("windows open");


        agedNomClosed = new JTextField[NUM_DATA];
        agedNomOpen = new JTextField[NUM_DATA];
        agedInClosed = new JTextField[NUM_DATA];
        agedInOpen = new JTextField[NUM_DATA];
        agedOutClosed = new JTextField[NUM_DATA];
        agedOutOpen = new JTextField[NUM_DATA];

        for (int i = 0; i < NUM_DATA; i++) {
            agedNomClosed[i] = Templates.makeTextField("");
            (agedNomClosed[i]).setEditable(false);
            agedNomOpen[i] = Templates.makeTextField("");
            (agedNomOpen[i]).setEditable(false);
            agedInClosed[i] = Templates.makeTextField("");
            (agedInClosed[i]).setEditable(false);
            agedInOpen[i] = Templates.makeTextField("");
            (agedInOpen[i]).setEditable(false);
            agedOutClosed[i] = Templates.makeTextField("");
            (agedOutClosed[i]).setEditable(false);
            agedOutOpen[i] = Templates.makeTextField("");
            (agedOutOpen[i]).setEditable(false);
        }
        JLabel agedForceLabel = Templates.makeLabel("force (lbf)");
        agedForceLabel.setToolTipText("Force in the closing spring at the door open position. This is the minimum spring force required to fully close the door.");

        JLabel agedEnergyLabel = Templates.makeLabel("energy (J)");
        agedEnergyLabel.setToolTipText("The energy in the closing spring that is required to fully close the door. E=1/2*1/k*(Fspring^2-Fpreload^2).");

        JLabel agedAngleLabel = Templates.makeLabel("angle (degrees)");
        agedAngleLabel.setToolTipText("Opening angle of the door when the spring is at the minimum required closing force.");

        JLabel agedVelocityLabel = Templates.makeLabel("velocity (ft/sec)");
        agedVelocityLabel.setToolTipText("Door edge velocity when the required closing energy is applied to the door.ÊMeasured at the time when the door edge crosses the plane of the velocity sensor");

        JLabel agedLatchForceLabel = Templates.makeLabel("force on latch (lbf)");
        agedLatchForceLabel.setToolTipText("Static latching force (measured as a static force when the door is at its fully closed position).");

        JPanel fill = new JPanel();

        JComponent[] comps = new JComponent[25+8*NUM_DATA];

        comps[0] = greenLabel;
        comps[1] = greenNomLabel;
        comps[2] = greenNomClosedLabel;
        comps[3] = greenNomOpenLabel;

        comps[4] = greenForceLabel;
        comps[5] = greenEnergyLabel;
        comps[6] = greenAngleLabel;
        comps[7] = greenVelocityLabel;
        comps[8] = greenLatchForceLabel;

        for (int i=9; i<9+NUM_DATA; i++)
            comps[i] = greenNomClosed[i-9];
        for (int i=9+NUM_DATA; i<9+2*NUM_DATA; i++)
            comps[i] = greenNomOpen[i-(9+NUM_DATA)];

        comps[9+2*NUM_DATA] = agedLabel;
        comps[10 + 2 * NUM_DATA] = agedNomLabel;
        comps[11 + 2 * NUM_DATA] = agedNomClosedLabel;
        comps[12 + 2 * NUM_DATA] = agedNomOpenLabel;

        comps[13 + 2 * NUM_DATA] = agedInLabel;
        comps[14 + 2 * NUM_DATA] = agedInClosedLabel;
        comps[15 + 2 * NUM_DATA] = agedInOpenLabel;

        comps[16 + 2 * NUM_DATA] = agedOutLabel;
        comps[17 + 2 * NUM_DATA] = agedOutClosedLabel;
        comps[18 + 2 * NUM_DATA] = agedOutOpenLabel;

        comps[19 + 2 * NUM_DATA] = agedForceLabel;
        comps[20 + 2 * NUM_DATA] = agedEnergyLabel;
        comps[21 + 2 * NUM_DATA] = agedAngleLabel;
        comps[22 + 2 * NUM_DATA] = agedVelocityLabel;
        comps[23 + 2 * NUM_DATA] = agedLatchForceLabel;

        for (int i = 24+2*NUM_DATA; i < 24 + 3*NUM_DATA; i++)
            comps[i] = agedNomClosed[i - (24 + 2 * NUM_DATA)];
        for (int i = 24 + 3 * NUM_DATA; i < 24 + 4 * NUM_DATA; i++)
            comps[i] = agedNomOpen[i - (24 + 3 * NUM_DATA)];

        for (int i = 24 + 4 * NUM_DATA; i < 24 + 5 * NUM_DATA; i++)
            comps[i] = agedInClosed[i - (24 + 4 * NUM_DATA)];
        for (int i = 24 + 5 * NUM_DATA; i < 24 + 6 * NUM_DATA; i++)
            comps[i] = agedInOpen[i - (24 + 5 * NUM_DATA)];

        for (int i = 24 + 6 * NUM_DATA; i < 24 + 7 * NUM_DATA; i++)
            comps[i] = agedOutClosed[i - (24 + 6 * NUM_DATA)];
        for (int i = 24 + 7 * NUM_DATA; i < 24 + 8 * NUM_DATA; i++)
            comps[i] = agedOutOpen[i - (24 + 7 * NUM_DATA)];

        comps[24+8*NUM_DATA] = fill;

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = new GridBagConstraints[25 +8*NUM_DATA];
        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[1] = new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[2] = new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);
        gbcs[3] = new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);

        gbcs[4] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[5] = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[6] = new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[7] = new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[8] = new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        for (int i = 9; i < 9 + NUM_DATA; i++) {
            if (i< 9 + NUM_DATA-1)
                gbcs[i] = new GridBagConstraints(1, 2+(i-9), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(1, 2 + (i - 9), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
        }
        for (int i = 9 + NUM_DATA; i < 9 + 2 * NUM_DATA; i++) {
            if (i<9+(2*NUM_DATA-1))
                gbcs[i] = new GridBagConstraints(2, 2 + i - (9+NUM_DATA), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2, 2 + i - (9 + NUM_DATA), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
        }

        gbcs[9 + 2 * NUM_DATA] = new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(15, 5, 0, 0), 0, 0);
        gbcs[10 + 2 * NUM_DATA] = new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(15, 5, 0, 0), 0, 0);
        gbcs[11 + 2 * NUM_DATA] = new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);
        gbcs[12 + 2 * NUM_DATA] = new GridBagConstraints(2, 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);

        gbcs[13 + 2 * NUM_DATA] = new GridBagConstraints(3, 7, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(15, 5, 0, 0), 0, 0);
        gbcs[14 + 2 * NUM_DATA] = new GridBagConstraints(3, 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);
        gbcs[15 + 2 * NUM_DATA] = new GridBagConstraints(4, 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);

        gbcs[16 + 2 * NUM_DATA] = new GridBagConstraints(5, 7, 2, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(15, 5, 0, 0), 0, 0);
        gbcs[17 + 2 * NUM_DATA] = new GridBagConstraints(5, 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);
        gbcs[18 + 2 * NUM_DATA] = new GridBagConstraints(6, 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(3, 5, 0, 0), 0, 0);

        gbcs[19 + 2 * NUM_DATA] = new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[20 + 2 * NUM_DATA] = new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[21 + 2 * NUM_DATA] = new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[22 + 2 * NUM_DATA] = new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[23 + 2 * NUM_DATA] = new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        for (int i = 24 +2*NUM_DATA; i < 24 + 3 * NUM_DATA; i++) {
            if (i < 24 + 3 * NUM_DATA - 1)
                gbcs[i] = new GridBagConstraints(1, 9 + (i - (24 + 2 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(1, 9 + (i - (24 + 2 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
        }
        for (int i = 24 + 3 * NUM_DATA; i < 24 + 4 * NUM_DATA; i++) {
            if (i < 24 + 4 * NUM_DATA - 1)
                gbcs[i] = new GridBagConstraints(2, 9 + (i - (24 + 3 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2, 9 + (i - (24 + 3 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
        }
        for (int i = 24 + 4 * NUM_DATA; i < 24 + 5 * NUM_DATA; i++) {
            if (i < 24 + 5 * NUM_DATA - 1)
                gbcs[i] = new GridBagConstraints(3, 9 + (i - (24 + 4 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(3, 9 + (i - (24 + 4 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
        }
        for (int i = 24 + 5 * NUM_DATA; i < 24 + 6 * NUM_DATA; i++) {
            if (i < 24 + 6 * NUM_DATA - 1)
                gbcs[i] = new GridBagConstraints(4, 9 + (i - (24 + 5 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(4, 9 + (i - (24 + 5 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
        }
        for (int i = 24 + 6 * NUM_DATA; i < 24 + 7 * NUM_DATA; i++) {
            if (i < 24 + 7 * NUM_DATA - 1)
                gbcs[i] = new GridBagConstraints(5, 9 + (i - (24 + 6 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(5, 9 + (i - (24 + 6 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
        }
        for (int i = 24 + 7 * NUM_DATA; i < 24 + 8 * NUM_DATA; i++) {
            if (i < 24 + 8 * NUM_DATA - 1)
                gbcs[i] = new GridBagConstraints(6, 9 + (i - (24 + 7 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(6, 9 + (i - (24 + 7 * NUM_DATA)), 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }
        gbcs[24 + 8 * NUM_DATA] = new GridBagConstraints(0, 14, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);

        Templates.layoutGridBag(p, comps, gbcs);
        JScrollPane scroll = new JScrollPane(p);
        return scroll;
    }

    public void setInterface(ModelInterfaceBase iface) {
        // nominal green results
        CustomGui.connectStringOrNumberTextField(iface, "Force Green Seal Window Closed", greenNomClosed[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Green Seal Window Closed", greenNomClosed[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Green Seal Window Closed", greenNomClosed[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Green Seal Window Closed", greenNomClosed[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force at Latch Green Seal Window Closed", greenNomClosed[LATCH_FORCE]);

        CustomGui.connectStringOrNumberTextField(iface, "Force Green Seal Window Open", greenNomOpen[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Green Seal Window Open", greenNomOpen[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Green Seal Window Open", greenNomOpen[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Green Seal Window Open", greenNomOpen[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force at Latch Green Seal Window Open", greenNomOpen[LATCH_FORCE]);

        //nominal aged results
        CustomGui.connectStringOrNumberTextField(iface, "Force Aged Seal Window Closed", agedNomClosed[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Aged Seal Window Closed", agedNomClosed[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Aged Seal Window Closed", agedNomClosed[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Aged Seal Window Closed", agedNomClosed[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force on Latch Aged Seal Window Closed", agedNomClosed[LATCH_FORCE]);

        CustomGui.connectStringOrNumberTextField(iface, "Force Aged Seal Window Open", agedNomOpen[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Aged Seal Window Open", agedNomOpen[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Aged Seal Window Open", agedNomOpen[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Aged Seal Window Open", agedNomOpen[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force on Latch Aged Seal Window Open", agedNomOpen[LATCH_FORCE]);

        //inboard aged results
        CustomGui.connectStringOrNumberTextField(iface, "Force Inboard Window Closed", agedInClosed[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Inboard Window Closed", agedInClosed[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Inboard Window Closed", agedInClosed[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Inboard Window Closed", agedInClosed[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force on Latch Inboard Window Closed", agedInClosed[LATCH_FORCE]);

        CustomGui.connectStringOrNumberTextField(iface, "Force Inboard Window Open", agedInOpen[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Inboard Window Open", agedInOpen[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Inboard Window Open", agedInOpen[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Inboard Window Open", agedInOpen[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force on Latch Inboard Window Open", agedInOpen[LATCH_FORCE]);

        //outboard aged results
        CustomGui.connectStringOrNumberTextField(iface, "Force Outboard Window Closed", agedOutClosed[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Outboard Window Closed", agedOutClosed[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Outboard Window Closed", agedOutClosed[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Outboard Window Closed", agedOutClosed[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force on Latch Outboard Window Closed", agedOutClosed[LATCH_FORCE]);

        CustomGui.connectStringOrNumberTextField(iface, "Force Outboard Window Open", agedOutOpen[CLOSE_FORCE]);
        CustomGui.connectStringOrNumberTextField(iface, "Energy Outboard Window Open", agedOutOpen[ENERGY]);
        CustomGui.connectStringOrNumberTextField(iface, "Angle Outboard Window Open", agedOutOpen[ANGLE]);
        CustomGui.connectStringOrNumberTextField(iface, "Velocity at Sensor Outboard Window Open", agedOutOpen[VELOCITY]);
        CustomGui.connectStringOrNumberTextField(iface, "Force on Latch Outboard Window Open", agedOutOpen[LATCH_FORCE]);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Standard result card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new StandardResultCard());
		f.show();
	}
}
