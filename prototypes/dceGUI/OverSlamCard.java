package dceGUI;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
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
public class OverSlamCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JCheckBox atC;
    private JCheckBox atD;
    private JCheckBox atE;

    private JTextField stiffC;
    private JTextField gapC;

    private JTextField stiffD;
    private JTextField gapD;

    private JTextField stiffE;
    private JTextField gapE;

	public OverSlamCard()
	{
        JComponent[] comps = {makeTopPanel(), makeSlamPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel slamLabel = Templates.makeLabel("Over slam bumpers", Templates.FONT12B);
        JTextArea slamText1 = Templates.makeDTextArea("Over slam bumpers are modeled as a simple linear spring that is activated" +
                                               "when the door contacts bumpers.");
        slamText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("dceGUI/images/overSlam.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("over slam bumber nomenclature");

        JComponent[] comps = {slamLabel, slamText1, imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeSlamPanel() {
        JPanel p = new JPanel();

        JLabel stiffnessLabel = Templates.makeLabel("Bumper stiffness (N/mm)", Templates.FONT12B);
        stiffnessLabel.setToolTipText("Stiffness of overslam bumper.Ê Value is the slope of a linear fit through the bumper force versus deflection curve.");

        JLabel gapLabel = Templates.makeLabel("Nominal bumper gap (mm)", Templates.FONT12B);
        gapLabel.setToolTipText("Distance from bumper to door in nominal closed position. Positive values indicate non-contact");

        atC = Templates.makeCheckBox("bumper at location C", false);
        atC.setToolTipText("Indicates whether there is a bumper at this location");
        stiffC = Templates.makeTextField("");
        gapC = Templates.makeTextField("");

        atC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (atC.isSelected()) {
                    stiffC.setEditable(true);
                    gapC.setEditable(true);
                }
                else {
                    stiffC.setEditable(false);
                    gapC.setEditable(false);
                }
            }
        });
        stiffC.setEditable(false);
        gapC.setEditable(false);

        atD = Templates.makeCheckBox("bumper at location D", false);
        atD.setToolTipText("Indicates whether there is a bumper at this location");
        stiffD = Templates.makeTextField("");
        gapD = Templates.makeTextField("");

        atD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (atD.isSelected()) {
                    stiffD.setEditable(true);
                    gapD.setEditable(true);
                } else {
                    stiffD.setEditable(false);
                    gapD.setEditable(false);
                }
            }
        });
        stiffD.setEditable(false);
        gapD.setEditable(false);

        atE = Templates.makeCheckBox("bumper at location E", false);
        atE.setToolTipText("Indicates whether there is a bumper at this location");
        stiffE = Templates.makeTextField("");
        gapE = Templates.makeTextField("");

        atE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (atE.isSelected()) {
                    stiffE.setEditable(true);
                    gapE.setEditable(true);
                } else {
                    stiffE.setEditable(false);
                    gapE.setEditable(false);
                }
            }
        });
        stiffE.setEditable(false);
        gapE.setEditable(false);


        JPanel fill = new JPanel();

        JComponent[] comps = {stiffnessLabel, gapLabel,
                              atC, stiffC, gapC,
                              atD, stiffD, gapD,
                              atE, stiffE, gapE,
                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {

        atC.setSelected(false);
        //atC.setEnabled(false);
        stiffC.setEditable(false);
        gapC.setEditable(false);

        atD.setSelected(false);
        //atD.setEnabled(false);
        stiffD.setEditable(false);
        gapD.setEditable(false);

        atE.setSelected(false);
        //atE.setEnabled(false);
        stiffE.setEditable(false);
        gapE.setEditable(false);

        CustomGui.connectBooleanCheckBox(iface, "Bumper C", atC);
        CustomGui.connectStringOrNumberTextField(iface, "Overslam Bumper C Stiffness", stiffC);
        CustomGui.connectStringOrNumberTextField(iface, "Overslam Bumper C Nominal Gap", gapC);

        CustomGui.connectBooleanCheckBox(iface, "Bumper D", atD);
        CustomGui.connectStringOrNumberTextField(iface, "Overslam Bumper D Stiffness", stiffD);
        CustomGui.connectStringOrNumberTextField(iface, "Overslam Bumper D Nominal Gap", gapD);

        CustomGui.connectBooleanCheckBox(iface, "Bumper E", atE);
        CustomGui.connectStringOrNumberTextField(iface, "Overslam Bumper E stiffness", stiffE);
        CustomGui.connectStringOrNumberTextField(iface, "Overslam Bumper E Nominal Gap", gapE);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Over slam card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new OverSlamCard());
		f.show();
	}
}
