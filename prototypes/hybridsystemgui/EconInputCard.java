package hybridsystemgui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import javax.swing.*;
import java.awt.*;

public class EconInputCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

    private final String pvTitle = "PV array costs";
    private final String dieselTitle = "Diesel generator costs";
    private final String econTitle = "Economic properties";

    private JTextField pvInst;
    private JTextField oilCost;
    private JTextField oilChangeCost;
    private JTextField moduleCost;
    private JTextField powConCost;
    private JTextField supportCost;
    private JTextField battCost;
    private JTextField dieselInst;
    private JTextField fuelStorage;
    private JTextField oilFilterCost;
    private JTextField fuelFilterCost;
    private JTextField sparkCost;
    private JTextField fuelCost;
    private JTextField airFilterCost;
    private JTextField pvOM;
    private JTextField inflation;
    private JTextField discount;

	public EconInputCard()
	{
        JComponent[] comps = {makeEconPanel(), makePVPanel(),  makeDieselPanel()};
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

        JLabel pvInstLabel = Templates.makeLabel("Installation factor: ");
        pvInstLabel.setToolTipText("Factor determining the installation, engineering, and management costs " +
                "as a function of the capital cost");
        pvInst = Templates.makeTextField("");

        JLabel pvOMLabel = Templates.makeLabel("Annual O&M factor: ");
        pvOMLabel.setToolTipText("Factor for determining the annual O&M cost as a function of the capital cost");
        pvOM = Templates.makeTextField("");

        JLabel moduleCostLabel = Templates.makeLabel("Module ($/ W): ");
        moduleCostLabel.setToolTipText("Cost of PV module per peak power");
        moduleCost = Templates.makeTextField("");

        JLabel powConCostLabel = Templates.makeLabel("Power conditioner ($/ W): ");
        powConCostLabel.setToolTipText("Cost of charge/discharge regulator for power conditioning per peak power");
        powConCost = Templates.makeTextField("");

        JLabel supportCostLabel = Templates.makeLabel("Array support ($/ W): ");
        supportCostLabel.setToolTipText("Cost of support of PV module support per peak power");
        supportCost = Templates.makeTextField("");

        JLabel battCostLabel = Templates.makeLabel("Battery ($/ kWh): ");
        battCostLabel.setToolTipText("Cost of battery-bank energy storage per capacity");
        battCost = Templates.makeTextField("");

        JComponent[] comps = {moduleCostLabel, moduleCost,
                              pvOMLabel, pvOM,
                              battCostLabel, battCost,
                              supportCostLabel, supportCost,
                              pvInstLabel, pvInst,
                              powConCostLabel, powConCost,
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
        bigP.setBorder(BorderFactory.createTitledBorder(null, pvTitle));

        return bigP;
    }

    private JPanel makeDieselPanel() {
        JPanel p = new JPanel();

        JLabel dieselInstLabel = Templates.makeLabel("Installation factor: ");
        dieselInstLabel.setToolTipText("Factor determining the installation, engineering, and management costs " +
                "as a function of the capital cost");
        dieselInst = Templates.makeTextField("");

        JLabel fuelStorageLabel = Templates.makeLabel("Fuel storage ($/ l): ");
        fuelStorageLabel.setToolTipText("Capital cost per liter of fuel storage");
        fuelStorage = Templates.makeTextField("");

        JLabel oilCostLabel = Templates.makeLabel("Engine oil ($/ l): ");
        oilCostLabel.setToolTipText("Price per unit volume of engine oil");
        oilCost = Templates.makeTextField("");

        JLabel oilChangeCostLabel = Templates.makeLabel("Oil change & filter cleaning ($): ");
        oilChangeCostLabel.setToolTipText("Cost of oil change & air-filter cleaning");
        oilChangeCost = Templates.makeTextField("");

        JLabel oilFilterCostLabel = Templates.makeLabel("Oil-filter replacement ($): ");
        oilFilterCostLabel.setToolTipText("Cost of oil-filter replacement");
        oilFilterCost = Templates.makeTextField("");

        JLabel airFilterCostLabel = Templates.makeLabel("Air-filter replacement ($): ");
        airFilterCostLabel.setToolTipText("Cost of air-filter replacement");
        airFilterCost = Templates.makeTextField("");

        JLabel fuelFilterCostLabel = Templates.makeLabel("Fuel-filter replacement ($): ");
        fuelFilterCostLabel.setToolTipText("Cost of fuel-filter replacement");
        fuelFilterCost = Templates.makeTextField("");

        JLabel sparkCostLabel = Templates.makeLabel("Spark plugs replacement ($): ");
        sparkCostLabel.setToolTipText("Cost of spark plugs replacement");
        sparkCost = Templates.makeTextField("");

        JLabel fuelCostLabel = Templates.makeLabel("Fuel ($/ l): ");
        fuelCostLabel.setToolTipText("Cost per liter of fuel");
        fuelCost = Templates.makeTextField("");

        JComponent[] comps = {airFilterCostLabel, airFilterCost,
                              fuelCostLabel, fuelCost,
                              oilFilterCostLabel, oilFilterCost,
                              oilCostLabel, oilCost,
                              fuelFilterCostLabel, fuelFilterCost,
                              dieselInstLabel, dieselInst,
                              sparkCostLabel, sparkCost,
                              fuelStorageLabel, fuelStorage,
                              oilChangeCostLabel, oilChangeCost,
    };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 8, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 9, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, dieselTitle));

        return p;
    }

    private JPanel makeEconPanel() {
        JPanel p = new JPanel();

        JLabel inflatLabel = Templates.makeLabel("Excess inflation: ");
        inflatLabel.setToolTipText("Rate of price increase of a component relative to general inflation." +
                " This is usually assumed to be zero");
        inflation = Templates.makeTextField("");

        JLabel discountLabel = Templates.makeLabel("Discount rate: ");
        discountLabel.setToolTipText("Rate, relative to general inflation, at which money would increase in value if invested." +
                " This value typically vary from 8% to 12%");
        discount = Templates.makeTextField("");

        JComponent[] comps = {inflatLabel, inflation,
                              discountLabel, discount
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 11, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 5, 10), 0, 0),
            new GridBagConstraints(2, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 11, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, econTitle));

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "excess inflation", inflation);
        CustomGui.connectStringOrNumberTextField(iface, "discount rate", discount);
        CustomGui.connectStringOrNumberTextField(iface, "diesel fuel storage cost", fuelStorage);
        CustomGui.connectStringOrNumberTextField(iface, "diesel installation cost factor", dieselInst);
        CustomGui.connectStringOrNumberTextField(iface, "diesel engine oil price", oilCost);
        CustomGui.connectStringOrNumberTextField(iface, "diesel oil change and air-filter cleaning cost", oilChangeCost);
        CustomGui.connectStringOrNumberTextField(iface, "diesel oil-filter replacement cost", oilFilterCost);
        CustomGui.connectStringOrNumberTextField(iface, "diesel air-filter replacement cost", airFilterCost);
        CustomGui.connectStringOrNumberTextField(iface, "diesel fuel-filter replacement cost", fuelFilterCost);
        CustomGui.connectStringOrNumberTextField(iface, "diesel spark plugs replacement cost", sparkCost);
        CustomGui.connectStringOrNumberTextField(iface, "diesel fuel cost", fuelCost);

        CustomGui.connectStringOrNumberTextField(iface, "PV installation cost factor", pvInst);
        CustomGui.connectStringOrNumberTextField(iface, "PV annual O&M cost factor", pvOM);
        CustomGui.connectStringOrNumberTextField(iface, "PV module cost", moduleCost);
        CustomGui.connectStringOrNumberTextField(iface, "PV power conditioner cost", powConCost);
        CustomGui.connectStringOrNumberTextField(iface, "PV support cost", supportCost);
        CustomGui.connectStringOrNumberTextField(iface, "PV battery cost", battCost);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new EconInputCard());
        f.pack();
		f.show();
	}
}
