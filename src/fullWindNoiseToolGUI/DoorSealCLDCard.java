package fullWindNoiseToolGUI;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
public class DoorSealCLDCard extends JPanel
{

	public static final GridBagConstraints gbc = null;
    private static final int OB_MINUS = 0; // these ar here for when you want to extract the data for def and load from the arrays
    private static final int OB = 1;
    private static final int NOMINAL = 2;
    private static final int IB = 3;
    private static final int IB_PLUS = 4;
    private static final int NUM_POS = 5;


    private JTextField age;
    private JTextField damping;

    private JTextField ab_offset;
    private JTextField[] ab_deflect;
    private JTextField[] ab_load;
    private JCheckBox ab_include;

    private JTextField bc_offset;
    private JTextField[] bc_deflect;
    private JTextField[] bc_load;
    private JCheckBox bc_include;

    private JTextField cd_offset;
    private JTextField[] cd_deflect;
    private JTextField[] cd_load;
    private JCheckBox cd_include;

    private JTextField de_offset;
    private JTextField[] de_deflect;
    private JTextField[] de_load;
    private JCheckBox de_include;

    private JTextField ef_offset;
    private JTextField[] ef_deflect;
    private JTextField[] ef_load;
    private JCheckBox ef_include;

    private JTextField fa_offset;
    private JTextField[] fa_deflect;
    private JTextField[] fa_load;
    private JCheckBox fa_include;

	public DoorSealCLDCard()
	{
        JComponent[] comps = {makeTopPanel(), makePropertyPanel(), makeDataPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 5, 5, 5), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel doorLabel = Templates.makeLabel("Door seal CLD characteristics", Templates.FONT12B);
        JTextArea doorText1 = Templates.makeDTextArea("The door seal is defined by three sets of data:Ê geometry, stiffness, and damping.\n\n" +
                "The seal stiffness characteristics are determined from force deflection values input on this page. " +
                "For each seal section, enter the deflection (in mm) and the corresponding seal" +
                "Compression Load Deflection(CLD)for the outboard (OB), nominal, inboard(IB)" +
                "conditions, plus locations slightly farther inboard(IB + X) and outboard(OB Ð X). These values are input to the ADAMS closing effort simulations.");
        doorText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/doorSeal.gif");
        JLabel imageLabel = new JLabel(image);

        JComponent[] comps = {doorLabel, doorText1,imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, gbc.SOUTHEAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makePropertyPanel() {
        JPanel p = new JPanel();

        JLabel ageLabel = Templates.makeLabel("Seal age (0-100%))");
        ageLabel.setToolTipText("Age of seal as percent of vehicle lifetime");
        age = Templates.makeTextField("");

        JLabel dampingLabel = Templates.makeLabel("Seal damping coefficient (0.0-1.0 N-s/100mm)");
        dampingLabel.setToolTipText("Seal damping coefficient");
        damping = Templates.makeTextField("");

        JPanel fill = new JPanel();

        JComponent[] comps = {ageLabel, age,
                              dampingLabel, damping,
                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

	private JScrollPane makeDataPanel()
	{
		JPanel p = new JPanel();

        JPanel ab = makeABPanel();
        JPanel bc = makeBCPanel();
        JPanel cd = makeCDPanel();
        JPanel de = makeDEPanel();
        JPanel ef = makeEFPanel();
        JPanel fa = makeFAPanel();

		JPanel fill = new JPanel();

		JComponent[] comps = {ab, bc, cd, de, ef, fa, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

			new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);
        JScrollPane scroll = new JScrollPane(p);
        //scroll.add(p);
        //scroll.setPreferredSize(new Dimension(700, 600));
		return scroll;
	}

    private JPanel makeZoneLabel(JCheckBox c, JLabel l, boolean makeWhite) {
        JPanel p = new JPanel();
        if (makeWhite) {
            p.setBackground(Color.WHITE);
            c.setBackground(Color.WHITE);
        }
        JComponent[] comps = {c, l};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeABPanel() {
        JPanel p = new JPanel();

        JLabel zoneLabel = Templates.makeLabel("Zone A-B", Templates.FONT12B);
        zoneLabel.setToolTipText("Seal region between letters on seal nomenclature figure above");
        ab_include = Templates.makeCheckBox();
        ab_include.setToolTipText("check box to include this seal section in the analysis");

        JLabel offsetLabel = Templates.makeLabel("offset (mm)");
        offsetLabel.setToolTipText("Distance seal is offset perpendicular to the door edgeline. Positive is towards door center");

        JLabel ob_MinusLabel = Templates.makeLabel("outboard -");
        ob_MinusLabel.setToolTipText("Slightly farther than outboard position.");

        JLabel ob_Label = Templates.makeLabel("outboard");
        ob_Label.setToolTipText("Outboard position.");

        JLabel nominal_Label = Templates.makeLabel("nominal");
        nominal_Label.setToolTipText("Nominal closed postion.");

        JLabel ib_Label = Templates.makeLabel("inboard");
        ib_Label.setToolTipText("Inboard position.");

        JLabel ib_PlusLabel = Templates.makeLabel("inboard +");
        ib_PlusLabel.setToolTipText("Slightly farther than inboard position.");

        JLabel deflectionLabel = Templates.makeLabel("Seal deflection (mm)");
        deflectionLabel.setToolTipText("Seal deflection at specified location");

        JLabel forceLabel = Templates.makeLabel("Seal stiffness (g/100mm)");
        forceLabel.setToolTipText("Stiffness corresonding to specified position and deflection");

        ab_offset = Templates.makeTextField("");

        ab_deflect = new JTextField[NUM_POS];
        ab_load = new JTextField[NUM_POS];
        for (int i=0;i<NUM_POS;i++) {
            ab_deflect[i] = Templates.makeTextField("");
            ab_load[i] = Templates.makeTextField("");
        }

        JComponent[] comps = new JComponent[NUM_POS*2 + 10];
        comps[0] = makeZoneLabel(ab_include, zoneLabel, false);
        comps[1] = offsetLabel;
        comps[2] = ob_MinusLabel;
        comps[3] = ob_Label;
        comps[4] = nominal_Label;
        comps[5] = ib_Label;
        comps[6] = ib_PlusLabel;
        comps[7] = ab_offset;
        comps[8] = deflectionLabel;
        for (int i=9; i<9+NUM_POS; i++)
            comps[i] = ab_deflect[i-9];
        comps[9+NUM_POS]= forceLabel;
        for (int i=10+NUM_POS;i<10+NUM_POS*2;i++)
            comps[i] = ab_load[i-(10+NUM_POS)];

        GridBagConstraints[] gbcs = new GridBagConstraints[NUM_POS * 2 + 10];
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        gbcs[0] =  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[1] = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[2] = new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[3] = new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[4] = new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[5] = new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[6] = new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[7] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 100, 0);
        gbcs[8] = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i=9; i<9+NUM_POS;i++)
        {
            if (i<(9+NUM_POS-1))
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[9 + NUM_POS] = new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0);
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++) {
            if (i < (10 + NUM_POS * 2 - 1))
                gbcs[i] = new GridBagConstraints(2 + i- (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS) , 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeBCPanel() {
        JPanel p = new JPanel();

        JLabel zoneLabel = Templates.makeLabel("Zone B-C", Templates.FONT12B);
        zoneLabel.setToolTipText("Seal region between letters on seal nomenclature figure above");
        bc_include = Templates.makeCheckBox();
        bc_include.setToolTipText("check box to include this seal section in the analysis");

        JLabel offsetLabel = Templates.makeLabel("offset (mm)");
        offsetLabel.setToolTipText("Distance seal is offset perpendicular to the door edgeline. Positive is towards door center");

        JLabel ob_MinusLabel = Templates.makeLabel("outboard -");
        ob_MinusLabel.setToolTipText("Slightly farther than outboard position.");

        JLabel ob_Label = Templates.makeLabel("outboard");
        ob_Label.setToolTipText("Outboard position.");

        JLabel nominal_Label = Templates.makeLabel("nominal");
        nominal_Label.setToolTipText("Nominal closed postion.");

        JLabel ib_Label = Templates.makeLabel("inboard");
        ib_Label.setToolTipText("Inboard position.");

        JLabel ib_PlusLabel = Templates.makeLabel("inboard +");
        ib_PlusLabel.setToolTipText("Slightly farther than inboard position.");

        JLabel deflectionLabel = Templates.makeLabel("Seal deflection (mm)");
        deflectionLabel.setToolTipText("Seal deflection at specified location");

        JLabel forceLabel = Templates.makeLabel("Seal stiffness (g/100mm)");
        forceLabel.setToolTipText("Stiffness corresonding to specified position and deflection");

        bc_offset = Templates.makeTextField("");

        bc_deflect = new JTextField[NUM_POS];
        bc_load = new JTextField[NUM_POS];
        for (int i = 0; i < NUM_POS; i++) {
            bc_deflect[i] = Templates.makeTextField("");
            bc_load[i] = Templates.makeTextField("");
        }

        JComponent[] comps = new JComponent[NUM_POS * 2 + 10];
        comps[0] = makeZoneLabel(bc_include, zoneLabel, true);
        comps[1] = offsetLabel;
        comps[2] = ob_MinusLabel;
        comps[3] = ob_Label;
        comps[4] = nominal_Label;
        comps[5] = ib_Label;
        comps[6] = ib_PlusLabel;
        comps[7] = bc_offset;
        comps[8] = deflectionLabel;
        for (int i = 9; i < 9 + NUM_POS; i++)
            comps[i] = bc_deflect[i - 9];
        comps[9 + NUM_POS] = forceLabel;
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++)
            comps[i] = bc_load[i - (10 + NUM_POS)];

        GridBagConstraints[] gbcs = new GridBagConstraints[NUM_POS * 2 + 10];
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[1] = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[2] = new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[3] = new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[4] = new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[5] = new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[6] = new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[7] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 100, 0);
        gbcs[8] = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 9; i < 9 + NUM_POS; i++) {
            if (i < (9 + NUM_POS - 1))
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[9 + NUM_POS] = new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0);
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++) {
            if (i < (10 + NUM_POS * 2 - 1))
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBackground(Color.white);
        return p;
    }

    private JPanel makeCDPanel() {
        JPanel p = new JPanel();

        JLabel zoneLabel = Templates.makeLabel("Zone C-D", Templates.FONT12B);
        zoneLabel.setToolTipText("Seal region between letters on seal nomenclature figure above");
        cd_include = Templates.makeCheckBox();
        cd_include.setToolTipText("check box to include this seal section in the analysis");

        JLabel offsetLabel = Templates.makeLabel("offset (mm)");
        offsetLabel.setToolTipText("Distance seal is offset perpendicular to the door edgeline. Positive is towards door center");

        JLabel ob_MinusLabel = Templates.makeLabel("outboard -");
        ob_MinusLabel.setToolTipText("Slightly farther than outboard position.");

        JLabel ob_Label = Templates.makeLabel("outboard");
        ob_Label.setToolTipText("Outboard position.");

        JLabel nominal_Label = Templates.makeLabel("nominal");
        nominal_Label.setToolTipText("Nominal closed postion.");

        JLabel ib_Label = Templates.makeLabel("inboard");
        ib_Label.setToolTipText("Inboard position.");

        JLabel ib_PlusLabel = Templates.makeLabel("inboard +");
        ib_PlusLabel.setToolTipText("Slightly farther than inboard position.");

        JLabel deflectionLabel = Templates.makeLabel("Seal deflection (mm)");
        deflectionLabel.setToolTipText("Seal deflection at specified location");

        JLabel forceLabel = Templates.makeLabel("Seal stiffness (g/100mm)");
        forceLabel.setToolTipText("Stiffness corresonding to specified position and deflection");

        cd_offset = Templates.makeTextField("");

        cd_deflect = new JTextField[NUM_POS];
        cd_load = new JTextField[NUM_POS];
        for (int i = 0; i < NUM_POS; i++) {
            cd_deflect[i] = Templates.makeTextField("");
            cd_load[i] = Templates.makeTextField("");
        }

        JComponent[] comps = new JComponent[NUM_POS * 2 + 10];
        comps[0] = makeZoneLabel(cd_include, zoneLabel, false);
        comps[1] = offsetLabel;
        comps[2] = ob_MinusLabel;
        comps[3] = ob_Label;
        comps[4] = nominal_Label;
        comps[5] = ib_Label;
        comps[6] = ib_PlusLabel;
        comps[7] = cd_offset;
        comps[8] = deflectionLabel;
        for (int i = 9; i < 9 + NUM_POS; i++)
            comps[i] = cd_deflect[i - 9];
        comps[9 + NUM_POS] = forceLabel;
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++)
            comps[i] = cd_load[i - (10 + NUM_POS)];

        GridBagConstraints[] gbcs = new GridBagConstraints[NUM_POS * 2 + 10];
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[1] = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[2] = new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[3] = new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[4] = new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[5] = new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[6] = new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[7] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 100, 0);
        gbcs[8] = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 9; i < 9 + NUM_POS; i++) {
            if (i < (9 + NUM_POS - 1))
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[9 + NUM_POS] = new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0);
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++) {
            if (i < (10 + NUM_POS * 2 - 1))
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeDEPanel() {
        JPanel p = new JPanel();

        JLabel zoneLabel = Templates.makeLabel("Zone D-E", Templates.FONT12B);
        zoneLabel.setToolTipText("Seal region between letters on seal nomenclature figure above");
        de_include = Templates.makeCheckBox();
        de_include.setToolTipText("check box to include this seal section in the analysis");

        JLabel offsetLabel = Templates.makeLabel("offset (mm)");
        offsetLabel.setToolTipText("Distance seal is offset perpendicular to the door edgeline. Positive is towards door center");

        JLabel ob_MinusLabel = Templates.makeLabel("outboard -");
        ob_MinusLabel.setToolTipText("Slightly farther than outboard position.");

        JLabel ob_Label = Templates.makeLabel("outboard");
        ob_Label.setToolTipText("Outboard position.");

        JLabel nominal_Label = Templates.makeLabel("nominal");
        nominal_Label.setToolTipText("Nominal closed postion.");

        JLabel ib_Label = Templates.makeLabel("inboard");
        ib_Label.setToolTipText("Inboard position.");

        JLabel ib_PlusLabel = Templates.makeLabel("inboard +");
        ib_PlusLabel.setToolTipText("Slightly farther than inboard position.");

        JLabel deflectionLabel = Templates.makeLabel("Seal deflection (mm)");
        deflectionLabel.setToolTipText("Seal deflection at specified location");

        JLabel forceLabel = Templates.makeLabel("Seal stiffness (g/100mm)");
        forceLabel.setToolTipText("Stiffness corresonding to specified position and deflection");

        de_offset = Templates.makeTextField("");

        de_deflect = new JTextField[NUM_POS];
        de_load = new JTextField[NUM_POS];
        for (int i = 0; i < NUM_POS; i++) {
            de_deflect[i] = Templates.makeTextField("");
            de_load[i] = Templates.makeTextField("");
        }

        JComponent[] comps = new JComponent[NUM_POS * 2 + 10];
        comps[0] = makeZoneLabel(de_include, zoneLabel, true);
        comps[1] = offsetLabel;
        comps[2] = ob_MinusLabel;
        comps[3] = ob_Label;
        comps[4] = nominal_Label;
        comps[5] = ib_Label;
        comps[6] = ib_PlusLabel;
        comps[7] = de_offset;
        comps[8] = deflectionLabel;
        for (int i = 9; i < 9 + NUM_POS; i++)
            comps[i] = de_deflect[i - 9];
        comps[9 + NUM_POS] = forceLabel;
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++)
            comps[i] = de_load[i - (10 + NUM_POS)];

        GridBagConstraints[] gbcs = new GridBagConstraints[NUM_POS * 2 + 10];
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[1] = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[2] = new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[3] = new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[4] = new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[5] = new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[6] = new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[7] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 100, 0);
        gbcs[8] = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 9; i < 9 + NUM_POS; i++) {
            if (i < (9 + NUM_POS - 1))
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[9 + NUM_POS] = new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0);
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++) {
            if (i < (10 + NUM_POS * 2 - 1))
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBackground(Color.white);
        return p;
    }

    private JPanel makeEFPanel() {
        JPanel p = new JPanel();

        JLabel zoneLabel = Templates.makeLabel("Zone E-F", Templates.FONT12B);
        zoneLabel.setToolTipText("Seal region between letters on seal nomenclature figure above");
        ef_include = Templates.makeCheckBox();
        ef_include.setToolTipText("check box to include this seal section in the analysis");

        JLabel offsetLabel = Templates.makeLabel("offset (mm)");
        offsetLabel.setToolTipText("Distance seal is offset perpendicular to the door edgeline. Positive is towards door center");

        JLabel ob_MinusLabel = Templates.makeLabel("outboard -");
        ob_MinusLabel.setToolTipText("Slightly farther than outboard position.");

        JLabel ob_Label = Templates.makeLabel("outboard");
        ob_Label.setToolTipText("Outboard position.");

        JLabel nominal_Label = Templates.makeLabel("nominal");
        nominal_Label.setToolTipText("Nominal closed postion.");

        JLabel ib_Label = Templates.makeLabel("inboard");
        ib_Label.setToolTipText("Inboard position.");

        JLabel ib_PlusLabel = Templates.makeLabel("inboard +");
        ib_PlusLabel.setToolTipText("Slightly farther than inboard position.");

        JLabel deflectionLabel = Templates.makeLabel("Seal deflection (mm)");
        deflectionLabel.setToolTipText("Seal deflection at specified location");

        JLabel forceLabel = Templates.makeLabel("Seal stiffness (g/100mm)");
        forceLabel.setToolTipText("Stiffness corresonding to specified position and deflection");

        ef_offset = Templates.makeTextField("");

        ef_deflect = new JTextField[NUM_POS];
        ef_load = new JTextField[NUM_POS];
        for (int i = 0; i < NUM_POS; i++) {
            ef_deflect[i] = Templates.makeTextField("");
            ef_load[i] = Templates.makeTextField("");
        }

        JComponent[] comps = new JComponent[NUM_POS * 2 + 10];
        comps[0] = makeZoneLabel(ef_include, zoneLabel, false);
        comps[1] = offsetLabel;
        comps[2] = ob_MinusLabel;
        comps[3] = ob_Label;
        comps[4] = nominal_Label;
        comps[5] = ib_Label;
        comps[6] = ib_PlusLabel;
        comps[7] = ef_offset;
        comps[8] = deflectionLabel;
        for (int i = 9; i < 9 + NUM_POS; i++)
            comps[i] = ef_deflect[i - 9];
        comps[9 + NUM_POS] = forceLabel;
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++)
            comps[i] = ef_load[i - (10 + NUM_POS)];

        GridBagConstraints[] gbcs = new GridBagConstraints[NUM_POS * 2 + 10];
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[1] = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[2] = new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[3] = new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[4] = new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[5] = new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[6] = new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[7] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 100, 0);
        gbcs[8] = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 9; i < 9 + NUM_POS; i++) {
            if (i < (9 + NUM_POS - 1))
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[9 + NUM_POS] = new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0);
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++) {
            if (i < (10 + NUM_POS * 2 - 1))
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeFAPanel() {
        JPanel p = new JPanel();

        JLabel zoneLabel = Templates.makeLabel("Zone F-A", Templates.FONT12B);
        zoneLabel.setToolTipText("Seal region between letters on seal nomenclature figure above");
        fa_include = Templates.makeCheckBox();
        fa_include.setToolTipText("check box to include this seal section in the analysis");

        JLabel offsetLabel = Templates.makeLabel("offset (mm)");
        offsetLabel.setToolTipText("Distance seal is offset perpendicular to the door edgeline. Positive is towards door center");

        JLabel ob_MinusLabel = Templates.makeLabel("outboard -");
        ob_MinusLabel.setToolTipText("Slightly farther than outboard position.");

        JLabel ob_Label = Templates.makeLabel("outboard");
        ob_Label.setToolTipText("Outboard position.");

        JLabel nominal_Label = Templates.makeLabel("nominal");
        nominal_Label.setToolTipText("Nominal closed postion.");

        JLabel ib_Label = Templates.makeLabel("inboard");
        ib_Label.setToolTipText("Inboard position.");

        JLabel ib_PlusLabel = Templates.makeLabel("inboard +");
        ib_PlusLabel.setToolTipText("Slightly farther than inboard position.");

        JLabel deflectionLabel = Templates.makeLabel("Seal deflection (mm)");
        deflectionLabel.setToolTipText("Seal deflection at specified location");

        JLabel forceLabel = Templates.makeLabel("Seal stiffness (g/100mm)");
        forceLabel.setToolTipText("Stiffness corresonding to specified position and deflection");

        fa_offset = Templates.makeTextField("");

        fa_deflect = new JTextField[NUM_POS];
        fa_load = new JTextField[NUM_POS];
        for (int i = 0; i < NUM_POS; i++) {
            fa_deflect[i] = Templates.makeTextField("");
            fa_load[i] = Templates.makeTextField("");
        }

        JComponent[] comps = new JComponent[NUM_POS * 2 + 10];
        comps[0] = makeZoneLabel(fa_include, zoneLabel, true);
        comps[1] = offsetLabel;
        comps[2] = ob_MinusLabel;
        comps[3] = ob_Label;
        comps[4] = nominal_Label;
        comps[5] = ib_Label;
        comps[6] = ib_PlusLabel;
        comps[7] = fa_offset;
        comps[8] = deflectionLabel;
        for (int i = 9; i < 9 + NUM_POS; i++)
            comps[i] = fa_deflect[i - 9];
        comps[9 + NUM_POS] = forceLabel;
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++)
            comps[i] = fa_load[i - (10 + NUM_POS)];

        GridBagConstraints[] gbcs = new GridBagConstraints[NUM_POS * 2 + 10];
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[1] = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[2] = new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[3] = new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[4] = new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[5] = new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[6] = new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[7] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 100, 0);
        gbcs[8] = new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 9; i < 9 + NUM_POS; i++) {
            if (i < (9 + NUM_POS - 1))
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - 9, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[9 + NUM_POS] = new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 0, 0), 0, 0);
        for (int i = 10 + NUM_POS; i < 10 + NUM_POS * 2; i++) {
            if (i < (10 + NUM_POS * 2 - 1))
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(2 + i - (10 + NUM_POS), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBackground(Color.white);
        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {

        CustomGui.connectStringOrNumberTextField(iface,"Percentage Door Seal Aging", age);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Damping Coefficient", damping);

        //ab section
        CustomGui.connectBooleanCheckBox(iface, "Door Seal Include Zone AB", ab_include);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Offset", ab_offset);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Deflection O/B-(x)", ab_deflect[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Deflection O/B", ab_deflect[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Deflection Nominal", ab_deflect[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Deflection I/B", ab_deflect[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Deflection I/B+(x)", ab_deflect[IB_PLUS]);

        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Load O/B-(x)", ab_load[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Load O/B", ab_load[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Load Nominal", ab_load[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Load I/B", ab_load[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone AB Load I/B+(x)", ab_load[IB_PLUS]);

        //bc section
        CustomGui.connectBooleanCheckBox(iface, "Door Seal Include Zone BC", bc_include);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Offset", bc_offset);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Deflection O/B-(x)", bc_deflect[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Deflection O/B", bc_deflect[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Deflection Nominal", bc_deflect[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Deflection I/B", bc_deflect[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Deflection I/B+(x)", bc_deflect[IB_PLUS]);

        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Load O/B-(x)", bc_load[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Load O/B", bc_load[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Load Nominal", bc_load[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Load I/B", bc_load[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone BC Load I/B+(x)", bc_load[IB_PLUS]);

        //cd section
        CustomGui.connectBooleanCheckBox(iface, "Door Seal Include Zone CD", cd_include);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Offset", cd_offset);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Deflection O/B-(x)", cd_deflect[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Deflection O/B", cd_deflect[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Deflection Nominal", cd_deflect[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Deflection I/B", cd_deflect[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Deflection I/B+(x)", cd_deflect[IB_PLUS]);

        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Load O/B-(x)", cd_load[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Load O/B", cd_load[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Load Nominal", cd_load[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Load I/B", cd_load[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone CD Load I/B+(x)", cd_load[IB_PLUS]);

        //de section
        CustomGui.connectBooleanCheckBox(iface, "Door Seal Include Zone DE", de_include);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Offset", de_offset);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Deflection O/B-(x)", de_deflect[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Deflection O/B", de_deflect[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Deflection Nominal", de_deflect[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Deflection I/B", de_deflect[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Deflection I/B+(x)", de_deflect[IB_PLUS]);

        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Load O/B-(x)", de_load[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Load O/B", de_load[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Load Nominal", de_load[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Load I/B", de_load[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone DE Load I/B+(x)", de_load[IB_PLUS]);

        //ef section
        CustomGui.connectBooleanCheckBox(iface, "Door Seal Include Zone EF", ef_include);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Offset", ef_offset);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Deflection O/B-(x)", ef_deflect[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Deflection O/B", ef_deflect[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Deflection Nominal", ef_deflect[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Deflection I/B", ef_deflect[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Deflection I/B+(x)", ef_deflect[IB_PLUS]);

        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Load O/B-(x)", ef_load[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Load O/B", ef_load[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Load Nominal", ef_load[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Load I/B", ef_load[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone EF Load I/B+(x)", ef_load[IB_PLUS]);

        //fa section
        CustomGui.connectBooleanCheckBox(iface, "Door Seal Include Zone FA", fa_include);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Offset", fa_offset);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Deflection O/B-(x)", fa_deflect[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Deflection O/B", fa_deflect[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Deflection Nominal", fa_deflect[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Deflection I/B", fa_deflect[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Deflection I/B+(x)", fa_deflect[IB_PLUS]);

        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Load O/B-(x)", fa_load[OB_MINUS]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Load O/B", fa_load[OB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Load Nominal", fa_load[NOMINAL]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Load I/B", fa_load[IB]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Seal Zone FA Load I/B+(x)", fa_load[IB_PLUS]);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Door seal card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DoorSealCLDCard());
		f.show();
	}
}
