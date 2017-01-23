package pvsystemgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class OtherCompInputCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

    private final String surroundTitle = "Surrounding conditions";
    private final String controllerTitle = "Controller's settings";
    private final String inverterTitle = "Inverter's characteristics";
    private final String batteryTitle = "Battery's characteristics";

    private JTextField groundEmis;
    private JTextField groundTemp;
    private JTextField skyEmis;
    private JTextField ambientTemp;
    private JTextField windSpeed;

    private JTextField chargeEff;
    private JTextField selfRate;
    private JTextField maxSOC;
    private JTextField num2V;

    private JTextField boost;
    private JTextField maxFloat;
    private JTextField minfloat;
    private JTextField recon;
    private JTextField discon;
    private JTextField conLoss;

    private JTextField inputVoltage;
    private JTextField varLoss;
    private JTextField fixedLoss;

	public OtherCompInputCard()
	{
        JComponent[] comps = {makeSurroundPanel(), makeBatteryPanel(), makeControllerPanel(), makeInverterPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeBatteryPanel() {
        JPanel p = new JPanel();

        JLabel chargeEffLabel = Templates.makeLabel("Charge/discharge efficiency: ");
        chargeEffLabel.setToolTipText("Efficiency in charging or discharging of battery");
        chargeEff = Templates.makeTextField("");

        JLabel selfRateLabel = Templates.makeLabel("Self-discharge rate (h^-1): ");
        selfRateLabel.setToolTipText("Rate that battery discharges itself. The magnitude of this value is typically small");
        selfRate = Templates.makeTextField("");

        JLabel maxSOCLabel = Templates.makeLabel("Maximum state of charge (Wh): ");
        maxSOCLabel.setToolTipText("Maximum energy that can be stored by battery. " +
                "This value is equivalent to the capacity of the battery in units of energy");
        maxSOC = Templates.makeTextField("");

        JLabel num2VLabel = Templates.makeLabel("Number of 2-V cells: ");
        num2VLabel.setToolTipText("Number of 2-V cells composing the battery");
        num2V = Templates.makeTextField("");

        JComponent[] comps = {chargeEffLabel, chargeEff,
                              selfRateLabel, selfRate,
                              maxSOCLabel, maxSOC,
                              num2VLabel, num2V
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 20), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, batteryTitle));

        return p;
    }

    private JPanel makeSurroundPanel() {
        JPanel p = new JPanel();

        JLabel groundEmisLabel = Templates.makeLabel("Ground emissivity: ");
        groundEmisLabel.setToolTipText("Average emissivity of the ground");
        groundEmis = Templates.makeTextField("");

        JLabel groundTempLabel = Templates.makeLabel("Ground temperature (K): ");
        groundTempLabel.setToolTipText("Temperature of the ground");
        groundTemp = Templates.makeTextField("");

        JLabel skyEmisLabel = Templates.makeLabel("Sky emissivity: ");
        skyEmisLabel.setToolTipText("Average emissivity of the sky. This value is usually estimated to be 1");
        skyEmis = Templates.makeTextField("");

        JLabel ambientTempLabel = Templates.makeLabel("Ambient temperature (K): ");
        ambientTempLabel.setToolTipText("Temperature of the ambient air in the vicinity of the panel");
        ambientTemp = Templates.makeTextField("");

        JLabel windSpeedLabel = Templates.makeLabel("Wind speed (m/s): ");
        windSpeedLabel.setToolTipText("Speed of wind blowing past PV panel");
        windSpeed = Templates.makeTextField("");

        //JPanel fill = new JPanel();

        JComponent[] comps = {groundEmisLabel, groundEmis,
                              groundTempLabel, groundTemp,
                              skyEmisLabel, skyEmis,
                              ambientTempLabel, ambientTemp,
                              windSpeedLabel, windSpeed
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 20), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, surroundTitle));

        return p;
    }

    private JPanel makeControllerPanel() {
        JPanel p = new JPanel();

        JLabel boostLabel = Templates.makeLabel("Boost voltage (V): ");
        boostLabel.setToolTipText("Voltage level at which controller disconnects PV array to prevent overcharging of battery in boost-charge mode");
        boost = Templates.makeTextField("");

        JLabel maxFloatLabel = Templates.makeLabel("Maximum float voltage (V): ");
        maxFloatLabel.setToolTipText("Voltage level at which controller disconnects PV array to prevent overcharging of battery in float-charge mode");
        maxFloat = Templates.makeTextField("");

        JLabel minFloatLabel = Templates.makeLabel("Minimum float voltage (V): ");
        minFloatLabel.setToolTipText("Voltage level at which controller switches from float-charge to boost-charge mode during discharging of battery");
        minfloat = Templates.makeTextField("");

        JLabel reconLabel = Templates.makeLabel("Reconnect Voltage (V): ");
        reconLabel.setToolTipText("Voltage level at which controller reconnects load after it was in low-voltage-disconnect mode");
        recon = Templates.makeTextField("");

        JLabel disconLabel = Templates.makeLabel("Disconnect Voltage (V): ");
        disconLabel.setToolTipText("Voltage level at which controller disconnects load to prevent aggressive discharging of battery");
        discon = Templates.makeTextField("");

        JLabel conLossLabel = Templates.makeLabel("Controller loss corrent (A): ");
        conLossLabel.setToolTipText("Current loss while passing controller");
        conLoss = Templates.makeTextField("");

        JComponent[] comps = {boostLabel, boost,
                              minFloatLabel, minfloat,
                              reconLabel, recon,
                              maxFloatLabel, maxFloat,
                              disconLabel, discon,
                              conLossLabel, conLoss
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 20), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, controllerTitle));

        return p;
    }

    private JPanel makeInverterPanel() {
        JPanel p = new JPanel();

        JLabel inputVoltageLabel = Templates.makeLabel("Input voltage (V): ");
        inputVoltageLabel.setToolTipText("Input voltage of inverter. This voltage is equal to the voltage of battery");
        inputVoltage = Templates.makeTextField("");

        JLabel varLossLabel = Templates.makeLabel("Variable loss coefficient: ");
        varLossLabel.setToolTipText("Variable loss coefficient, accounts for the loss due to the resistances of the transformer’s copper and the load");
        varLoss = Templates.makeTextField("");

        JLabel fixedLossLabel = Templates.makeLabel("Fixed loss coefficient (W): ");
        fixedLossLabel.setToolTipText("Fixed loss coefficient, representing the combined losses from switching circuit and transformer core");
        fixedLoss = Templates.makeTextField("");

        JComponent[] comps = {varLossLabel, varLoss,
                              inputVoltageLabel, inputVoltage,
                              fixedLossLabel, fixedLoss
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
        p.setBorder(BorderFactory.createTitledBorder(null, inverterTitle));

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "minimum float voltage", minfloat);
        CustomGui.connectStringOrNumberTextField(iface, "maximum float voltage", maxFloat);
        CustomGui.connectStringOrNumberTextField(iface, "boost voltage", boost);
        CustomGui.connectStringOrNumberTextField(iface, "reconnect voltage", recon);
        CustomGui.connectStringOrNumberTextField(iface, "disconnect voltage", discon);
        CustomGui.connectStringOrNumberTextField(iface, "controller loss current", conLoss);

        CustomGui.connectStringOrNumberTextField(iface, "charge/discharge efficiency", chargeEff);
        CustomGui.connectStringOrNumberTextField(iface, "self-discharge rate", selfRate);
        CustomGui.connectStringOrNumberTextField(iface, "maximum state of charge", maxSOC);
        CustomGui.connectStringOrNumberTextField(iface, "number of 2-V cells", num2V);

        CustomGui.connectStringOrNumberTextField(iface, "fixed loss coefficient", fixedLoss);
        CustomGui.connectStringOrNumberTextField(iface, "variable loss coefficient", varLoss);
        CustomGui.connectStringOrNumberTextField(iface, "input voltage", inputVoltage);

        CustomGui.connectStringOrNumberTextField(iface, "ambient temperature", ambientTemp);
        CustomGui.connectStringOrNumberTextField(iface, "ground emissivity", groundEmis);
        CustomGui.connectStringOrNumberTextField(iface, "ground temperature", groundTemp);
        CustomGui.connectStringOrNumberTextField(iface, "sky emissivity", skyEmis);
        CustomGui.connectStringOrNumberTextField(iface, "wind speed", windSpeed);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new OtherCompInputCard());
        f.pack();
		f.show();
	}
}
