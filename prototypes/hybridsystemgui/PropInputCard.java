package hybridsystemgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class PropInputCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

    private final String mfcSpecTitle = "PV array properties";
    private final String sysDesignTitle = "Diesel generator properties";
    private final String comTitle = "System load";

    private JTextField pvLifetime;
    private JTextField powConLifetime;
    private JTextField supportLifetime;
    private JTextField battLifetime;
    private JTextField dischargeEff;
    private JTextField chargeEff;

    private JTextField fuelStorage;
    private JTextField airClean;
    private JTextField oilFilter;
    private JTextField airFilter;
    private JTextField fuelFilter;
    private JTextField spark;
    private JComboBox engineType;

    private JTextField dailyLoad;
    private JTextField dayHours;
    private JTextField loadFrac;
    private JTextField analysis;

	public PropInputCard()
	{
        JComponent[] comps = {makeLoadPanel(), makePVPanel(),  makeDieselPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makePVPanel() {
        JPanel bigP = new JPanel();

        JLabel pvLifetimeLabel = Templates.makeLabel("PV modules' lifetime (y): ");
        pvLifetimeLabel.setToolTipText("Lifetime of the PV modules");
        pvLifetime = Templates.makeTextField("");

        JLabel powConLifetimeLabel = Templates.makeLabel("Power conditioner's lifetime (y): ");
        powConLifetimeLabel.setToolTipText("Lifetime of charge/discharge regulator for power conditioning before it needs to be replaced");
        powConLifetime = Templates.makeTextField("");

        JLabel supportLifetimeLabel = Templates.makeLabel("Array support's lifetime (y): ");
        supportLifetimeLabel.setToolTipText("Lifetime of PV module support before it needs to be replaced. " +
                "Usually, the support does not need to be replaced for the entire system’s lifetime");
        supportLifetime = Templates.makeTextField("");

        JLabel battLifetimeLabel = Templates.makeLabel("Battery's lifetime (y): ");
        battLifetimeLabel.setToolTipText("Lifetime of battery-bank energy storage before it needs to be replaced");
        battLifetime = Templates.makeTextField("");

        JLabel dischargeEffLabel = Templates.makeLabel("Battery's discharging efficiency (%): ");
        dischargeEffLabel.setToolTipText("Efficiency of battery discharging");
        dischargeEff = Templates.makeTextField("");

        JLabel chargeEffLabel = Templates.makeLabel("Battery's charging efficiency (%): ");
        chargeEffLabel.setToolTipText("Efficiency of battery charging");
        chargeEff = Templates.makeTextField("");

        //JPanel fill = new JPanel();

        JComponent[] comps = {battLifetimeLabel, battLifetime,
                              powConLifetimeLabel, powConLifetime,
                              pvLifetimeLabel, pvLifetime,
                              chargeEffLabel, chargeEff,
                              supportLifetimeLabel, supportLifetime,
                              dischargeEffLabel, dischargeEff,
                              //fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(bigP, comps, gbcs);
        bigP.setBorder(BorderFactory.createTitledBorder(null, mfcSpecTitle));
        return bigP;
    }

    private JPanel makeDieselPanel() {
        JPanel p = new JPanel();

        JLabel fuelStorageLabel = Templates.makeLabel("Fuel storage's size (l): ");
        fuelStorageLabel.setToolTipText("Size of fuel storage");
        fuelStorage = Templates.makeTextField("");

        JLabel airCleanLabel = Templates.makeLabel("Air-filter cleaning period: ");
        airCleanLabel.setToolTipText("Number of oil changes before air-filter cleaning is required");
        airClean = Templates.makeTextField("");

        JLabel oilFilterLabel = Templates.makeLabel("Oil-filter replacement period: ");
        oilFilterLabel.setToolTipText("Number of oil changes before oil-filter replacement is required");
        oilFilter = Templates.makeTextField("");

        JLabel airFilterLabel = Templates.makeLabel("Air-filter replacement period: ");
        airFilterLabel.setToolTipText("Number of oil changes before air-filter replacement is required");
        airFilter = Templates.makeTextField("");

        JLabel fuelFilterLabel = Templates.makeLabel("Fuel-filter replacement period: ");
        fuelFilterLabel.setToolTipText("Number of oil changes before fuel-filter replacement is required");
        fuelFilter = Templates.makeTextField("");

        JLabel sparkLabel = Templates.makeLabel("Spark plugs replacement period: ");
        sparkLabel.setToolTipText("Number of oil changes before spark plugs replacement is required");
        spark = Templates.makeTextField("");

        JLabel engineTypeLabel = Templates.makeLabel("Engine generator type: ");
        engineTypeLabel.setToolTipText("Type of generator");
        engineType = Templates.makeDComboBox();

        JComponent[] comps = {fuelStorageLabel, fuelStorage,
                              airCleanLabel, airClean,
                              airFilterLabel, airFilter,
                              oilFilterLabel, oilFilter,
                              fuelFilterLabel, fuelFilter,
                              sparkLabel, spark,
                              engineTypeLabel, engineType,
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 10), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(4, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, sysDesignTitle));

        return p;
    }

    private JPanel makeLoadPanel() {
        JPanel p = new JPanel();

        JLabel dailyLoadLabel = Templates.makeLabel("Net daily load (Wh): ");
        dailyLoadLabel.setToolTipText("Total load demand of the system in a day");
        dailyLoad = Templates.makeTextField("");

        JLabel dayHoursLabel = Templates.makeLabel("Day hours (h): ");
        dayHoursLabel.setToolTipText("Number of hours with sunlight in a day");
        dayHours = Templates.makeTextField("");

        JLabel loadFracLabel = Templates.makeLabel("Day load fraction: ");
        loadFracLabel.setToolTipText("Fraction of the total daily load that has to be delivered during the day");
        loadFrac = Templates.makeTextField("");

        JLabel analysisLabel = Templates.makeLabel("Length of analysis (y): ");
        analysisLabel.setToolTipText("Number of years covered by the analysis");
        analysis = Templates.makeTextField("");

        JComponent[] comps = {dayHoursLabel, dayHours,
                              dailyLoadLabel, dailyLoad,
                              loadFracLabel, loadFrac,
                              analysisLabel, analysis
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
        p.setBorder(BorderFactory.createTitledBorder(null, comTitle));

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectEnumerationComboBox(iface, "engine type", engineType);

        CustomGui.connectStringOrNumberTextField(iface, "net daily load", dailyLoad);
        CustomGui.connectStringOrNumberTextField(iface, "day hours", dayHours);
        CustomGui.connectStringOrNumberTextField(iface, "day load fraction", loadFrac);
        CustomGui.connectStringOrNumberTextField(iface, "analysis period", analysis);
        CustomGui.connectStringOrNumberTextField(iface, "diesel fuel storage size", fuelStorage);
        CustomGui.connectStringOrNumberTextField(iface, "diesel air-filter cleaning period", airClean);
        CustomGui.connectStringOrNumberTextField(iface, "diesel oil-filter replacement period", oilFilter);
        CustomGui.connectStringOrNumberTextField(iface, "diesel air-filter replacement period", airFilter);
        CustomGui.connectStringOrNumberTextField(iface, "diesel fuel-filter replacement period", fuelFilter);
        CustomGui.connectStringOrNumberTextField(iface, "diesel spark plugs replacement period", spark);

        CustomGui.connectStringOrNumberTextField(iface, "PV array lifetime", pvLifetime);
        CustomGui.connectStringOrNumberTextField(iface, "PV power conditioner lifetime", powConLifetime);
        CustomGui.connectStringOrNumberTextField(iface, "PV support lifetime", supportLifetime);
        CustomGui.connectStringOrNumberTextField(iface, "PV battery lifetime", battLifetime);
        CustomGui.connectStringOrNumberTextField(iface, "battery discharge efficiency", dischargeEff);
        CustomGui.connectStringOrNumberTextField(iface, "battery charge efficiency", chargeEff);        
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new PropInputCard());
        f.pack();
		f.show();
	}
}
