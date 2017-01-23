package pvsystemgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class ModuleInputCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

    private final String mfcSpecTitle = "PV module's specifications";
    private final String sysDesignTitle = "System design";
    private final String comTitle = "Computation settings";

    private JTextField cellBandGap;
    private JTextField matConst;
    private JTextField absorption;
    private JTextField diodeSeries;
    private JTextField diodeShunt;
    private JTextField diodeIdeal;
    private JTextField tempEff;
    private JTextField therCap;
    private JTextField emissivity;
    private JTextField refEff;
    private JTextField shortCoeff1;
    private JTextField shortCoeff2;
    private JTextField shortCoeff3;

    private JTextField cellArea;
    private JTextField moduleArea;
    private JTextField terminalVoltage;
    private JTextField numParallel;
    private JTextField numSeries;
    private JTextField groundView;
    private JTextField skyView;
    private JTextField tilt;

    private JTextField timeInt;
    private JTextField intStep;
    private JTextField timePos;

	public ModuleInputCard()
	{
        JComponent[] comps = {makeTopPanel(),  makeSysDesignPanel(), makeComPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeTopPanel() {
        JPanel bigP = new JPanel();

        JTextArea moduleSpecText = Templates.makeDTextArea("These values are characteristics of the PV module. They can" +
                " be directly obtained or derived from the manufacturer's data sheet or I-V curves.");

        moduleSpecText.setOpaque(false);
        JPanel p = new JPanel();

        JLabel cellBandGapLabel = Templates.makeLabel("Cell band gap energy (eV): ");
        cellBandGapLabel.setToolTipText("Band gap energy at 0 K. This value is 1.16 eV for silicon");
        cellBandGap = Templates.makeTextField("");

        JLabel matConstLabel = Templates.makeLabel("Material constant (A/K^3): ");
        matConstLabel.setToolTipText("Material-specific constant used in calculation of reverse saturation current, " +
                "derived from the manufacturer’s data sheet and I-V curve characteristic of PV module");
        matConst = Templates.makeTextField("");

        JLabel absorptionLabel = Templates.makeLabel("Absorption coefficient: ");
        absorptionLabel.setToolTipText("Overall absorption coefficient of PV module");
        absorption = Templates.makeTextField("");

        JLabel diodeSeriesLabel = Templates.makeLabel("Diode series resistance (ohm): ");
        diodeSeriesLabel.setToolTipText("Diode series resistance per module");
        diodeSeries = Templates.makeTextField("");

        JLabel diodeShuntLabel = Templates.makeLabel("Diode shunt resistance (ohm): ");
        diodeShuntLabel.setToolTipText("Diode shunt resistance per module");
        diodeShunt = Templates.makeTextField("");

        JLabel diodeIdealLabel = Templates.makeLabel("Diode ideality factor (ohm): ");
        diodeIdealLabel.setToolTipText("Ideality factor for the module");
        diodeIdeal = Templates.makeTextField("");

        JLabel tempEffLabel = Templates.makeLabel("Temperature efficiency (K^-1): ");
        tempEffLabel.setToolTipText("Temperature coefficient for PV module conversion efficiency");
        tempEff = Templates.makeTextField("");

        JLabel therCapLabel = Templates.makeLabel("Thermal capacity (J/K): ");
        therCapLabel.setToolTipText("Effective thermal capacity of the module at specified temperature");
        therCap = Templates.makeTextField("");

        JLabel emissivityLabel = Templates.makeLabel("Panel emissivity: ");
        emissivityLabel.setToolTipText("Average emissivity of PV panel");
        emissivity = Templates.makeTextField("");

        JLabel refEffLabel = Templates.makeLabel("Reference efficiency (%): ");
        refEffLabel.setToolTipText("Efficiency of PV module at reference temperature");
        refEff = Templates.makeTextField("");

        JLabel shortCoeff1Label = Templates.makeLabel("Short-circuit constant coefficient 1 (m^2/V): ");
        shortCoeff1Label.setToolTipText("Module-specific coefficient used in calculation of light-generated current");
        shortCoeff1 = Templates.makeTextField("");

        JLabel shortCoeff2Label = Templates.makeLabel("Short-circuit constant coefficient 2 (m^2/W): ");
        shortCoeff2Label.setToolTipText("Module-specific coefficient used in calculation of light-generated current");
        shortCoeff2 = Templates.makeTextField("");

        JLabel shortCoeff3Label = Templates.makeLabel("Short-circuit constant coefficient 3 (K^-1): ");
        shortCoeff3Label.setToolTipText("Module-specific coefficient used in calculation of light-generated current");
        shortCoeff3 = Templates.makeTextField("");

        //JPanel fill = new JPanel();

        JComponent[] comps = {cellBandGapLabel, cellBandGap,
                              matConstLabel, matConst,
                              diodeSeriesLabel, diodeSeries,
                              absorptionLabel, absorption,
                              diodeShuntLabel, diodeShunt,
                              diodeIdealLabel, diodeIdeal,
                              tempEffLabel, tempEff,
                              therCapLabel, therCap,
                              shortCoeff1Label, shortCoeff1,
                              refEffLabel, refEff,
                              shortCoeff2Label, shortCoeff2,
                              emissivityLabel, emissivity,
                              shortCoeff3Label, shortCoeff3,
                              //fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 20), 0, 0),

            //new GridBagConstraints(0, 8, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(5, 5, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);
        bigP.setBorder(BorderFactory.createTitledBorder(null, mfcSpecTitle));

        JComponent[] bigComps = {moduleSpecText, p};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] bigGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(bigP, bigComps, bigGbcs);
        return bigP;
    }

    private JPanel makeSysDesignPanel() {
        JPanel p = new JPanel();

        JLabel cellAreaLabel = Templates.makeLabel("Cell area (m^2): ");
        cellAreaLabel.setToolTipText("Total surface area of PV cells that receive solar radiation");
        cellArea = Templates.makeTextField("");

        JLabel moduleAreaLabel = Templates.makeLabel("Module area (m^2): ");
        moduleAreaLabel.setToolTipText("Total area of PV module");
        moduleArea = Templates.makeTextField("");

        JLabel terminalVoltageLabel = Templates.makeLabel("Terminal voltage (V): ");
        terminalVoltageLabel.setToolTipText("Terminal voltage of module. " +
                "This value is usually equal to 12 V for a 36-celled module");
        terminalVoltage = Templates.makeTextField("");

        JLabel numParallelLabel = Templates.makeLabel("Number of modules in parallel: ");
        numParallelLabel.setToolTipText("Number of modules connected in parallel in the array");
        numParallel = Templates.makeTextField("");

        JLabel numSeriesLabel = Templates.makeLabel("Number of modules in series: ");
        numSeriesLabel.setToolTipText("Number of modules connected in series in the array");
        numSeries = Templates.makeTextField("");

        JLabel groundViewLabel = Templates.makeLabel("Panel-ground view factor: ");
        groundViewLabel.setToolTipText("View factor from panel to ground. This value can be considered 0 " +
                "when the panel’s tilt angle is small, e.g. 15º");
        groundView = Templates.makeTextField("");

        JLabel skyViewLabel = Templates.makeLabel("Panel-sky view factor: ");
        skyViewLabel.setToolTipText("View factor from panel to sky. This value can be considered 1 " +
                "when the panel’s tilt angle is small, e.g. 15º");
        skyView = Templates.makeTextField("");

        JLabel tiltLabel = Templates.makeLabel("Tilt angle (deg): ");
        tiltLabel.setToolTipText("Tilt angle of PV panel, as measured from the horizontal level");
        tilt = Templates.makeTextField("");

        JComponent[] comps = {numParallelLabel, numParallel,
                              moduleAreaLabel, moduleArea,
                              numSeriesLabel, numSeries,
                              terminalVoltageLabel, terminalVoltage,
                              groundViewLabel, groundView,
                              cellAreaLabel, cellArea,
                              skyViewLabel, skyView,
                              tiltLabel, tilt
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 20), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(4, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, sysDesignTitle));

        return p;
    }

    private JPanel makeComPanel() {
        JPanel p = new JPanel();

        JLabel timeIntLabel = Templates.makeLabel("Time interval (min): ");
        timeIntLabel.setToolTipText("Time interval between steps of evaluation");
        timeInt = Templates.makeTextField("");

        JLabel intStepLabel = Templates.makeLabel("Integration time step size (s): ");
        intStepLabel.setToolTipText("Size of time-step used in internal integration to compute SOC");
        intStep = Templates.makeTextField("");

        JLabel timePosLabel = Templates.makeLabel("Starting time position (h): ");
        timePosLabel.setToolTipText("Time position to start the simulation");
        timePos = Templates.makeTextField("");

        JComponent[] comps = {timeIntLabel, timeInt,
                              intStepLabel, intStep,
                              timePosLabel, timePos,
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 20), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, comTitle));

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "cell band gap energy", cellBandGap);
        CustomGui.connectStringOrNumberTextField(iface, "material constant", matConst);
        CustomGui.connectStringOrNumberTextField(iface, "module absorption coefficient", absorption);
        CustomGui.connectStringOrNumberTextField(iface, "module diode series resistance", diodeSeries);
        CustomGui.connectStringOrNumberTextField(iface, "module diode shunt resistance", diodeShunt);
        CustomGui.connectStringOrNumberTextField(iface, "module diode ideality factor", diodeIdeal);
        CustomGui.connectStringOrNumberTextField(iface, "module temperature efficiency", tempEff);
        CustomGui.connectStringOrNumberTextField(iface, "module thermal capacity", therCap);
        CustomGui.connectStringOrNumberTextField(iface, "panel emissivity", emissivity);
        CustomGui.connectStringOrNumberTextField(iface, "reference module efficiency", refEff);
        CustomGui.connectStringOrNumberTextField(iface, "short-circuit constant coefficients 1", shortCoeff1);
        CustomGui.connectStringOrNumberTextField(iface, "short-circuit constant coefficients 2", shortCoeff2);
        CustomGui.connectStringOrNumberTextField(iface, "short-circuit constant coefficients 3", shortCoeff3);
        CustomGui.connectStringOrNumberTextField(iface, "cell area", cellArea);
        CustomGui.connectStringOrNumberTextField(iface, "module area", moduleArea);
        CustomGui.connectStringOrNumberTextField(iface, "module terminal voltage", terminalVoltage);
        CustomGui.connectStringOrNumberTextField(iface, "number of module in parallel", numParallel);
        CustomGui.connectStringOrNumberTextField(iface, "number of module in series", numSeries);
        CustomGui.connectStringOrNumberTextField(iface, "panel-ground view factor", groundView);
        CustomGui.connectStringOrNumberTextField(iface, "panel-sky view factor", skyView);
        CustomGui.connectStringOrNumberTextField(iface, "tilt angle", tilt);

        CustomGui.connectStringOrNumberTextField(iface, "starting time position", timePos);
        CustomGui.connectStringOrNumberTextField(iface, "time interval", timeInt);
        CustomGui.connectStringOrNumberTextField(iface, "integration time step", intStep);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ModuleInputCard());
        f.pack();
		f.show();
	}
}
