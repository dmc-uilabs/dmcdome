package hybridsystemgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class DesignInputCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

    private final String designTitle = "Design parameters";
    private final String energyTitle = "Energy inputs";
    private final String co2Title = "CO2 emission rates";

    private JTextField pvLoadFrac;
    private JTextField dieselRatedPowFac;
    private JTextField hourDieselOpt;
    private JTextField minimumSOC;

    private JTextField pvOmEnergy;
    private JTextField pvInstEnergy;
    private JTextField pvElmEnergy;
    private JTextField pvDistEnergy;
    private JTextField dieselHeatVal;
    private JTextField dieselInstEnergy;
    private JTextField dieselElmEnergy;
    private JTextField dieselDistEnergy;

    private JTextField pvMfc;
    private JTextField dieselMfc;
    private JTextField battMfc;
    private JTextField fuel;

	public DesignInputCard()
	{
        JComponent[] comps = {makeDesignPanel(),  makeEnergyPanel(), makeCO2Panel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeDesignPanel() {
        JPanel bigP = new JPanel();

        JTextArea moduleSpecText = Templates.makeDTextArea("These values are critical parameters that should be designed " +
                "to achieve the desired net values of electricity cost, capital cost, life-cycle cost, CO2 emission, and " +
                "electricity production efficiency");

        moduleSpecText.setOpaque(false);
        JPanel p = new JPanel();

        JLabel pvLoadFracLabel = Templates.makeLabel("PV load fraction: ");
        pvLoadFracLabel.setToolTipText("Fraction of the total, daily load that is delivered by PV and battery");
        pvLoadFrac = Templates.makeTextField("");

        JLabel dieselRatedPowFacLabel = Templates.makeLabel("Diesel rated-power factor (>1): ");
        dieselRatedPowFacLabel.setToolTipText("Ratio between the diesel generator's rated-power and actual, required, operating power");
        dieselRatedPowFac = Templates.makeTextField("");

        JLabel hourDieselOptLabel = Templates.makeLabel("Diesel's daily operating hours (h): ");
        hourDieselOptLabel.setToolTipText("Number of operation of diesel generator in a day");
        hourDieselOpt = Templates.makeTextField("");


        JLabel minimumSOCtLabel = Templates.makeLabel("Battery's minimum SOC (<1): ");
        minimumSOCtLabel.setToolTipText("Minimum state of charge of battery");
        minimumSOC = Templates.makeTextField("");

        JComponent[] comps = {pvLoadFracLabel, pvLoadFrac,
                              hourDieselOptLabel, hourDieselOpt,
                              dieselRatedPowFacLabel, dieselRatedPowFac,
                              minimumSOCtLabel, minimumSOC,
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 10), 0, 0),

            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),

            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        bigP.setBorder(BorderFactory.createTitledBorder(null, designTitle));

        JComponent[] bigComps = {moduleSpecText, p};

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] bigGbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(bigP, bigComps, bigGbcs);
        return bigP;
    }

    private JPanel makeEnergyPanel() {
        JPanel p = new JPanel();

        JLabel pvLabel = Templates.makeLabel("       PV array       ", Templates.FONT11B);
        JLabel dieselLabel = Templates.makeLabel("Diesel generator", Templates.FONT11B);

        JLabel pvOmEnergyLabel = Templates.makeLabel("Annual O&M energy (kWh/ y): ");
        pvOmEnergyLabel.setToolTipText("Energy required for operation and maintenance of PV");
        pvOmEnergy = Templates.makeTextField("");

        JLabel pvInstEnergyLabel = Templates.makeLabel("Installation energy (kWh): ");
        pvInstEnergyLabel.setToolTipText("Energy required for installation of PV array or diesel generator");
        pvInstEnergy = Templates.makeTextField("");
        dieselInstEnergy = Templates.makeTextField("");

        JLabel pvElmEnergyLabel = Templates.makeLabel("End-of-life management energy (Wh): ");
        pvElmEnergyLabel.setToolTipText("Energy required to manage PV array or diesel generator after its lifetime");
        pvElmEnergy = Templates.makeTextField("");
        dieselElmEnergy = Templates.makeTextField("");

        JLabel pvDistEnergyLabel = Templates.makeLabel("Distributuion energy (kWh): ");
        pvDistEnergyLabel.setToolTipText("Energy required to transport product materials between analysis stages: " +
                "material production, manufacturing, use, and end-of-life");
        pvDistEnergy = Templates.makeTextField("");
        dieselDistEnergy = Templates.makeTextField("");

        JLabel dieselHeatValLabel = Templates.makeLabel("Fuel heating value (kWh/ l): ");
        dieselHeatValLabel.setToolTipText("Heating value of diesel fuel");
        dieselHeatVal = Templates.makeTextField("");

        JComponent[] comps = {pvLabel, dieselLabel,
                              pvDistEnergyLabel, pvDistEnergy, dieselDistEnergy,
                              pvInstEnergyLabel, pvInstEnergy, dieselInstEnergy,
                              pvElmEnergyLabel, pvElmEnergy, dieselElmEnergy,
                              pvOmEnergyLabel, pvOmEnergy,
        dieselHeatValLabel, dieselHeatVal,
    };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(-5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(-5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, energyTitle));

        return p;
    }

    private JPanel makeCO2Panel() {
        JPanel p = new JPanel();

        JLabel pvMfcLabel = Templates.makeLabel("PV mfg. (t/ kW-y): ");
        pvMfcLabel.setToolTipText("CO2 emission rate from PV manufacturing");
        pvMfc = Templates.makeTextField("");

        JLabel dieselMfcLabel = Templates.makeLabel("Diesel generator mfg. (t/ kW-y): ");
        dieselMfcLabel.setToolTipText("CO2 emission rate from diesel generator manufacturing");
        dieselMfc = Templates.makeTextField("");

        JLabel battMfcLabel = Templates.makeLabel("Battery mfg. (t/ kW-y): ");
        battMfcLabel.setToolTipText("CO2 emission rate from battery manufacturing");
        battMfc = Templates.makeTextField("");

        JLabel fuelLabel = Templates.makeLabel("Fuel use (kg/ l): ");
        fuelLabel.setToolTipText("CO2 emission rate from fuel usage");
        fuel = Templates.makeTextField("");

        JComponent[] comps = {fuelLabel, fuel,
                              battMfcLabel, battMfc,
                              pvMfcLabel, pvMfc,
                              dieselMfcLabel, dieselMfc,
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, co2Title));

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "PV load fraction", pvLoadFrac);
        CustomGui.connectStringOrNumberTextField(iface, "diesel rated power factor", dieselRatedPowFac);
        CustomGui.connectStringOrNumberTextField(iface, "daily hours of diesel operation", hourDieselOpt);
        CustomGui.connectStringOrNumberTextField(iface, "minimum SOC", minimumSOC);

        CustomGui.connectStringOrNumberTextField(iface, "PV annual operation and maintenance energy", pvOmEnergy);
        CustomGui.connectStringOrNumberTextField(iface, "PV installation energy", pvInstEnergy);
        CustomGui.connectStringOrNumberTextField(iface, "PV end-of-life management energy", pvElmEnergy);
        CustomGui.connectStringOrNumberTextField(iface, "PV distribution energy", pvDistEnergy);
        CustomGui.connectStringOrNumberTextField(iface, "diesel heating value", dieselHeatVal);
        CustomGui.connectStringOrNumberTextField(iface, "diesel installation energy", dieselInstEnergy);
        CustomGui.connectStringOrNumberTextField(iface, "diesel end-of-life management energy", dieselElmEnergy);
        CustomGui.connectStringOrNumberTextField(iface, "diesel distribution energy", dieselDistEnergy);

        CustomGui.connectStringOrNumberTextField(iface, "battery manufacturing CO2 emission rate", battMfc);
        CustomGui.connectStringOrNumberTextField(iface, "PV manufacturing CO2 emission rate", pvMfc);
        CustomGui.connectStringOrNumberTextField(iface, "diesel generator manufacturing CO2 emission rate", dieselMfc);
        CustomGui.connectStringOrNumberTextField(iface, "fuel CO2 emission rate", fuel);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DesignInputCard());
        f.pack();
		f.show();
	}
}
