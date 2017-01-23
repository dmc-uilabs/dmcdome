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
public class TeardownResultCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    public static final int EFFORT = 0;
    public static final int CLOSING = 1;
    public static final int ENERGY_CONT = 2;
    public static final int PERCENT_CONT= 3;

    public static final int NUM_BASE_DATA = 2;
    public static final int NUM_TEAR_DATA = 4;


    JTextField[] base;

    JTextField[] oppositeWindow;
    JTextField[] extractor;
    JTextField[] striker;
    JTextField[] doorCheck;
    JTextField[] primarySeal;
    JTextField[] secondarySeal;
    JTextField[] shedlipSeal;
    JTextField[] others;

    JTextField totalSum;


	public TeardownResultCard()
	{
        JComponent[] comps = {makeTopPanel(), makeTeardownPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel p = new JPanel();
        JLabel standardLabel = Templates.makeLabel("Teardown closing effort results", Templates.FONT12B);
        JTextArea standardText1 = Templates.makeDTextArea("These results are determined by an ADAMS simulation which is part of the door closing effort tool.\n\nThe teardown analysis is designed to determine the amount of energy that each modeled component contributes to the overall closing effort." +
        "The total energy required for a fully assembled door is used as a baseline reference.");
        standardText1.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/teardown.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("teardown results nomenclature");

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

    private JScrollPane makeTeardownPanel() {
        JPanel p = new JPanel();

        JLabel baseLabel = Templates.makeLabel("fully assembled baseline");
        baseLabel.setToolTipText("Fully assembled door closing efforts.");

        base = new JTextField[NUM_BASE_DATA];
        for (int i =0; i<NUM_BASE_DATA; i++) {
            base[i] = Templates.makeTextField("");
            base[i].setEditable(false);
        }

        JLabel effortLabel = Templates.makeLabel("measure effort (lbf)", Templates.FONT11B);
        effortLabel.setToolTipText("Minimum closing spring force required to close the door.");

        JLabel closingLabel = Templates.makeLabel("closing energy (J)", Templates.FONT11B);
        closingLabel.setToolTipText("Minimum energy (calculated from minimum closing spring force) required to close the door.");

        JLabel contEnergyLabel = Templates.makeLabel("energy contribution (J)", Templates.FONT11B);
        contEnergyLabel.setToolTipText("Amount of energy specified component contributes to the overal closing effort. A negative value indicates component aids in closing the door.");

        JLabel contPercentLabel = Templates.makeLabel("percent contribution (%)", Templates.FONT11B);
        contPercentLabel.setToolTipText("Percent contribution specified component has in the overall closing efforts. A negative value indicates component aids in closing the door.");

        JLabel oppositeDoorLabel = Templates.makeLabel("opposite window open");
        oppositeDoorLabel.setToolTipText("Closing efforts for fully assembled door without air pressure effects");
        oppositeWindow = new JTextField[NUM_TEAR_DATA];
        for (int i=0; i<NUM_TEAR_DATA; i++) {
            oppositeWindow[i]=Templates.makeTextField("");
            oppositeWindow[i].setEditable(false);
        }

        JLabel extractorLabel = Templates.makeLabel("extractor");
        extractorLabel.setToolTipText("Effect of the air extractor given fully-assembled door with windows closed and air extractors taped");
        extractor = new JTextField[NUM_TEAR_DATA];
        for (int i = 0; i < NUM_TEAR_DATA; i++) {
            extractor[i] = Templates.makeTextField("");
            extractor[i].setEditable(false);
        }

        JLabel strikerLabel = Templates.makeLabel("striker offset eliminated");
        strikerLabel.setToolTipText("Closing efforts with windows open, striker adjusted so that there is no offset.");
        striker = new JTextField[NUM_TEAR_DATA];
        for (int i = 0; i < NUM_TEAR_DATA; i++) {
            striker[i] = Templates.makeTextField("");
            striker[i].setEditable(false);
        }

        JLabel doorCheckLabel = Templates.makeLabel("hinge check removed");
        doorCheckLabel.setToolTipText("Closing efforts with windows open, striker neutralized, and hinge check removed.");
        doorCheck = new JTextField[NUM_TEAR_DATA];
        for (int i = 0; i < NUM_TEAR_DATA; i++) {
            doorCheck[i] = Templates.makeTextField("");
            doorCheck[i].setEditable(false);
        }

        JLabel primarySealLabel = Templates.makeLabel("primary seal removed");
        primarySealLabel.setToolTipText("Closing efforts with windows open, striker neutralized, hinge check removed, and primary door seal removed.");
        primarySeal = new JTextField[NUM_TEAR_DATA];
        for (int i = 0; i < NUM_TEAR_DATA; i++) {
            primarySeal[i] = Templates.makeTextField("");
            primarySeal[i].setEditable(false);
        }

        JLabel secondarySealLabel = Templates.makeLabel("secondary seal removed");
        secondarySealLabel.setToolTipText("Closing efforts with windows open and striker neutralized; hinge check, primary door seal, and secondary body seal removed.");
        secondarySeal = new JTextField[NUM_TEAR_DATA];
        for (int i = 0; i < NUM_TEAR_DATA; i++) {
            secondarySeal[i] = Templates.makeTextField("");
            secondarySeal[i].setEditable(false);
        }

        JLabel shedlipSealLabel = Templates.makeLabel("shedlip seal removed");
        shedlipSealLabel.setToolTipText("Closing efforts with windows open, striker neutralized, hinge check removed, and all seals removed.");
        shedlipSeal = new JTextField[NUM_TEAR_DATA];
        for (int i = 0; i < NUM_TEAR_DATA; i++) {
            shedlipSeal[i] = Templates.makeTextField("");
            shedlipSeal[i].setEditable(false);
        }

        JLabel othersLabel = Templates.makeLabel("other contributors");
        othersLabel.setToolTipText("Contribution energy from additional components not removed during the model simulations (such as friction and hinge angle).");
        others = new JTextField[NUM_TEAR_DATA];
        for (int i = 0; i < NUM_TEAR_DATA; i++) {
            others[i] = Templates.makeTextField("");
            others[i].setEditable(false);
        }

        JLabel totalSumLabel = Templates.makeLabel("total percent contribution", Templates.FONT11B);
        totalSumLabel.setToolTipText("Sum of percent contributions (excluding those which aid in door closing efforts).  Should equal 100%.");
        totalSum = Templates.makeTextField("");
        totalSum.setFont(Templates.FONT11B);
        totalSum.setEditable(false);

        JPanel fill = new JPanel();

        JComponent[] comps = new JComponent[16 + NUM_BASE_DATA + 8*NUM_TEAR_DATA];

        comps[0] = baseLabel;
        for (int i=1; i<1+NUM_BASE_DATA;i++)
            comps[i] = base[i-1];

        comps[1 + NUM_BASE_DATA] = effortLabel;
        comps[2 + NUM_BASE_DATA] = closingLabel;
        comps[3 + NUM_BASE_DATA] = contEnergyLabel;
        comps[4 + NUM_BASE_DATA] = contPercentLabel;

        comps[5 + NUM_BASE_DATA] =  oppositeDoorLabel;
        for (int i = 6+ NUM_BASE_DATA;i < 6+ NUM_BASE_DATA + NUM_TEAR_DATA;i++)
            comps[i]   = oppositeWindow[i - (6 + NUM_BASE_DATA)];

        comps[6 + NUM_BASE_DATA + NUM_TEAR_DATA] = extractorLabel;
        for (int i = 7 + NUM_BASE_DATA + NUM_TEAR_DATA; i < 7 + NUM_BASE_DATA + 2*NUM_TEAR_DATA; i++)
            comps[i] = extractor[i-(7 + NUM_BASE_DATA + NUM_TEAR_DATA)];

        comps[7 + NUM_BASE_DATA + 2 * NUM_TEAR_DATA] = strikerLabel;
        for (int i = 8 + NUM_BASE_DATA + 2*NUM_TEAR_DATA; i < 8 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA; i++)
            comps[i] = striker[i - (8 + NUM_BASE_DATA + 2*NUM_TEAR_DATA)];

        comps[8 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA] = doorCheckLabel;
        for (int i = 9 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA; i < 9 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA; i++)
            comps[i] = doorCheck[i - (9 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA)];

        comps[9 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA] = primarySealLabel;
        for (int i = 10 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA; i < 10 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA; i++)
            comps[i] = primarySeal[i - (10 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA)];

        comps[10 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA] = secondarySealLabel;
        for (int i = 11 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA; i < 11 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA; i++)
            comps[i] = secondarySeal[i - (11 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA)];

        comps[11 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA] = shedlipSealLabel;
        for (int i = 12 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA; i < 12 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA; i++)
            comps[i] = shedlipSeal[i - (12 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA)];

        comps[12 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA] = othersLabel;
        for (int i = 13 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA; i < 13 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA; i++)
            comps[i] = others[i - (13 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA)];

        comps[13 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA] = totalSumLabel;
        comps[14 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA] = totalSum;

        comps[15 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA] = fill;

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = new GridBagConstraints[16 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA];

        gbcs[0] = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 1; i < 1 + NUM_BASE_DATA; i++)
            gbcs[i] = new GridBagConstraints(i, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);

        gbcs[1 + NUM_BASE_DATA] = new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[2 + NUM_BASE_DATA] = new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[3 + NUM_BASE_DATA] = new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[4 + NUM_BASE_DATA] = new GridBagConstraints(4, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0);

        gbcs[5 + NUM_BASE_DATA] = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 6 + NUM_BASE_DATA; i < 6 + NUM_BASE_DATA + NUM_TEAR_DATA; i++){
            if (i< 6 + NUM_BASE_DATA + NUM_TEAR_DATA-1)
                gbcs[i] = new GridBagConstraints(i + 1-(6 + NUM_BASE_DATA), 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (6 + NUM_BASE_DATA), 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[6 + NUM_BASE_DATA + NUM_TEAR_DATA] = new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 7 + NUM_BASE_DATA + NUM_TEAR_DATA; i < 7 + NUM_BASE_DATA + 2*NUM_TEAR_DATA; i++) {
            if (i < 7 + NUM_BASE_DATA + 2*NUM_TEAR_DATA - 1)
                gbcs[i] = new GridBagConstraints(i + 1 - (7 + NUM_BASE_DATA + NUM_TEAR_DATA), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (7 + NUM_BASE_DATA + NUM_TEAR_DATA), 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[7 + NUM_BASE_DATA + 2 * NUM_TEAR_DATA] = new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 8 + NUM_BASE_DATA + 2 * NUM_TEAR_DATA; i < 8 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA; i++) {
            if (i < 8 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA - 1)
                gbcs[i] = new GridBagConstraints(i + 1 - (8 + NUM_BASE_DATA + 2*NUM_TEAR_DATA), 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (8 + NUM_BASE_DATA + 2*NUM_TEAR_DATA), 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[8 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA] = new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 9 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA; i < 9 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA; i++) {
            if (i < 9 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA - 1)
                gbcs[i] = new GridBagConstraints(i + 1 - (9 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA), 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (9 + NUM_BASE_DATA + 3 * NUM_TEAR_DATA), 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[9 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA] = new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 10 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA; i < 10 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA; i++) {
            if (i < 10 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA - 1)
                gbcs[i] = new GridBagConstraints(i + 1 - (10 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA), 6, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (10 + NUM_BASE_DATA + 4 * NUM_TEAR_DATA), 6, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[10 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA] = new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 11 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA; i < 11 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA; i++) {
            if (i < 11 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA - 1)
                gbcs[i] = new GridBagConstraints(i + 1 - (11 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA), 7, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (11 + NUM_BASE_DATA + 5 * NUM_TEAR_DATA), 7, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[11 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA] = new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 12 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA; i < 12 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA; i++) {
            if (i < 12 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA - 1)
                gbcs[i] = new GridBagConstraints(i + 1 - (12 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA), 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (12 + NUM_BASE_DATA + 6 * NUM_TEAR_DATA), 8, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[12 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA] = new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        for (int i = 13 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA; i < 13 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA; i++) {
            if (i < 13 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA - 1)
                gbcs[i] = new GridBagConstraints(i + 1 - (13 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA), 9, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
            else
                gbcs[i] = new GridBagConstraints(i + 1 - (13 + NUM_BASE_DATA + 7 * NUM_TEAR_DATA), 9, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
        }

        gbcs[13 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA] = new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0);
        gbcs[14 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA] = new GridBagConstraints(4, 10, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);

        gbcs[15 + NUM_BASE_DATA + 8 * NUM_TEAR_DATA] = new GridBagConstraints(0, 11, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);

        Templates.layoutGridBag(p, comps, gbcs);
        JScrollPane scroll = new JScrollPane(p);
        return scroll;
    }

    public void setInterface(ModelInterfaceBase iface) {
        //connect the effort variables
        CustomGui.connectStringOrNumberTextField(iface, "Base Line Measure Effort", base[EFFORT]);
        CustomGui.connectStringOrNumberTextField(iface, "Opposite Door Window Open Measure Effort", oppositeWindow[EFFORT]);
        CustomGui.connectStringOrNumberTextField(iface, "Extractor Measure Effort", extractor[EFFORT]);
        CustomGui.connectStringOrNumberTextField(iface, "Striker Offset Neutralized Measure Effort", striker[EFFORT]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Separate Check Removed Measure Effort", doorCheck[EFFORT]);
        CustomGui.connectStringOrNumberTextField(iface, "Primary Door Seal Removed Measure Effort", primarySeal[EFFORT]);
        CustomGui.connectStringOrNumberTextField(iface, "Secondary Door Seal Removed Measure Effort", secondarySeal[EFFORT]);
        CustomGui.connectStringOrNumberTextField(iface, "Shedlip Seal Removed Measure Effort", shedlipSeal[EFFORT]);

        //connect the energy variables
        CustomGui.connectStringOrNumberTextField(iface, "Base Line Closing Energy", base[CLOSING]);
        CustomGui.connectStringOrNumberTextField(iface, "Opposite Door Window Open Closing Energy", oppositeWindow[CLOSING]);
        CustomGui.connectStringOrNumberTextField(iface, "Extractor Closing Energy", extractor[CLOSING]);
        CustomGui.connectStringOrNumberTextField(iface, "Striker Offset Neutralized Closing Energy", striker[CLOSING]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Separate Check Removed Closing Energy", doorCheck[CLOSING]);
        CustomGui.connectStringOrNumberTextField(iface, "Primary Door Seal Removed Closing Energy", primarySeal[CLOSING]);
        CustomGui.connectStringOrNumberTextField(iface, "Secondary Door Seal Removed Closing Energy", secondarySeal[CLOSING]);
        CustomGui.connectStringOrNumberTextField(iface, "Shedlip Seal Removed Closing Energy", shedlipSeal[CLOSING]);

        //connect the contribution energy variables
        CustomGui.connectStringOrNumberTextField(iface, "Opposite Door Window Contribution Energy", oppositeWindow[ENERGY_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Extractor Contribution Energy", extractor[ENERGY_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Striker Offset Neutralized Contribution Energy", striker[ENERGY_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Separate Check Removed Contribution Energy", doorCheck[ENERGY_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Primary Door Seal Removed Contribution Energy", primarySeal[ENERGY_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Secondary Door Seal Removed Contribution Energy", secondarySeal[ENERGY_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Shedlip Seal Removed Contribution Energy", shedlipSeal[ENERGY_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Others Contributors Contribution Energy", others[ENERGY_CONT]);

        //connect the component contribution variables
        CustomGui.connectStringOrNumberTextField(iface, "Opposite Door Window Component Contribution", oppositeWindow[PERCENT_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Extractor Component Contribution", extractor[PERCENT_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Striker Offset Neutralized Component Contribution", striker[PERCENT_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Door Separate Check Removed Component Contribution", doorCheck[PERCENT_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Primary Door Seal Removed Component Contribution", primarySeal[PERCENT_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Secondary Door Seal Removed Component Contribution", secondarySeal[PERCENT_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Shedlip Seal Removed Component Contribution", shedlipSeal[PERCENT_CONT]);
        CustomGui.connectStringOrNumberTextField(iface, "Others Contributors Component Contribution", others[PERCENT_CONT]);

        CustomGui.connectStringOrNumberTextField(iface, "Total Contribution Sum", totalSum);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Teardown result card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new TeardownResultCard());
		f.show();
	}
}
