package pvsystemgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

/**
 * Card for the login step in deployment
 */
public class ResultsCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private final String moduleTitle = "Effects on PV module";
    private final String controllerTitle = "Controller's results";
    private final String systemTitle = "System's characteristics";

    private JTextField absSolar;
    private JTextField convCoef;
    private JTextField convLoss;
    private JTextField instEff;
    private JTextField lightCurr;
    private JTextField revCurr;
    private JTextField prodPower;
    private JTextField radLoss;

    private JTextField bcm;
    private JTextField fcm;
    private JTextField lvd;

    private JTextField presLoad;
    private JTextField presIrrad;
    private JTextField pvCurr;
    private JTextField invCurr;
    private JTextField invPow;
    private JTextField batCurr;
    private JTextField batVol;
    private JTextField soc;

	public ResultsCard()
	{
        JComponent[] comps = {makeModulePanel(), makeControllerPanel(), makeSystemPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeModulePanel() {
        JPanel p = new JPanel();

        JLabel absSolarLabel = Templates.makeLabel("Absorbed solar power (W): ");
        absSolarLabel.setToolTipText("Rate of solar energy absorbed by PV module");
        absSolar = Templates.makeTextField("");
        absSolar.setEditable(false);

        JLabel convCoefLabel = Templates.makeLabel("Convective heat transfer coefficiennt: ");
        convCoefLabel.setToolTipText("Rate of convective heat loss from PV module");
        convCoef = Templates.makeTextField("");
        convCoef.setEditable(false);

        JLabel convLossLabel = Templates.makeLabel("Convective loss (W): ");
        convLossLabel.setToolTipText("Rate of convective heat loss from PV module");
        convLoss = Templates.makeTextField("");
        convLoss.setEditable(false);

        JLabel instEffLabel = Templates.makeLabel("Instantaneous efficiency (%): ");
        instEffLabel.setToolTipText("Instantaneous efficiency of the module. This value drops as the cell’s temperature increases");
        instEff = Templates.makeTextField("");
        instEff.setEditable(false);

        JLabel lightCurrLabel = Templates.makeLabel("Light-generated current (A): ");
        lightCurrLabel.setToolTipText("Light-generated current per module");
        lightCurr = Templates.makeTextField("");
        lightCurr.setEditable(false);

        JLabel revCurrLabel = Templates.makeLabel("Reverse saturation current (A): ");
        revCurrLabel.setToolTipText("Reverse saturation current per module." +
                "This current is a small current that flows back to the PV arrays all the time");
        revCurr = Templates.makeTextField("");
        revCurr.setEditable(false);

        JLabel prodPowerLabel = Templates.makeLabel("Produced power (W): ");
        prodPowerLabel.setToolTipText("Rate of electrical power produced by PV module");
        prodPower = Templates.makeTextField("");
        prodPower.setEditable(false);

        JLabel radLossLabel = Templates.makeLabel("Radiative loss (W): ");
        radLossLabel.setToolTipText("Rate of radiative heat loss from PV module, as a combined result of radiative heat" +
                " exchanges between the panel and the sky, and the panel and the ground");
        radLoss = Templates.makeTextField("");
        radLoss.setEditable(false);

        JComponent[] comps = {revCurrLabel, revCurr,
                              prodPowerLabel, prodPower,
                              convLossLabel, convLoss,
                              instEffLabel, instEff,
                              radLossLabel, radLoss,
                              absSolarLabel, absSolar,
                              convCoefLabel, convCoef,
                              lightCurrLabel, lightCurr,
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

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, moduleTitle));

        return p;
    }

    private JPanel makeControllerPanel() {
        JPanel p = new JPanel();

        JLabel bcmLabel = Templates.makeLabel("Boost-charge mode: ");
        bcmLabel.setToolTipText("Mode that controller boost-charges battery");
        bcm = Templates.makeTextField("");
        bcm.setEditable(false);

        JLabel fcmLabel = Templates.makeLabel("Float-charge mode: ");
        fcmLabel.setToolTipText("Mode that controller float-charges battery");
        fcm = Templates.makeTextField("");
        fcm.setEditable(false);

        JLabel lvdLabel = Templates.makeLabel("Low-voltage disconnect: ");
        lvdLabel.setToolTipText("Mode that controller prevent aggressive discharging of the battery");
        lvd = Templates.makeTextField("");
        lvd.setEditable(false);

        JComponent[] comps = {bcmLabel, bcm,
                              fcmLabel, fcm,
                              lvdLabel, lvd
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
        p.setBorder(BorderFactory.createTitledBorder(null, controllerTitle));

        return p;
    }

    private JPanel makeSystemPanel() {
        JPanel p = new JPanel();

        JLabel presLoadLabel = Templates.makeLabel("Present load power (W): ");
        presLoadLabel.setToolTipText("Load power at the present");
        presLoad = Templates.makeTextField("");
        presLoad.setEditable(false);

        JLabel presIrradLabel = Templates.makeLabel("Present solar irradiance (W): ");
        presIrradLabel.setToolTipText("Solar irradiance at the present");
        presIrrad = Templates.makeTextField("");
        presIrrad.setEditable(false);

        JLabel pvCurrLabel = Templates.makeLabel("PV module output current (A): ");
        pvCurrLabel.setToolTipText("Output current of PV panel");
        pvCurr = Templates.makeTextField("");
        pvCurr.setEditable(false);

        JLabel invCurrLabel = Templates.makeLabel("Inverter input current (A): ");
        invCurrLabel.setToolTipText("Input current of inverter");
        invCurr = Templates.makeTextField("");
        invCurr.setEditable(false);

        JLabel invPowLabel = Templates.makeLabel("Inverter input power (W): ");
        invPowLabel.setToolTipText("Total power input of inverter");
        invPow = Templates.makeTextField("");
        invPow.setEditable(false);

        JLabel batCurrLabel = Templates.makeLabel("Battery current (A): ");
        batCurrLabel.setToolTipText("Current input to battery");
        batCurr = Templates.makeTextField("");
        batCurr.setEditable(false);

        JLabel batVolLabel = Templates.makeLabel("Battery output voltage (V): ");
        batVolLabel.setToolTipText("Output voltage of battery");
        batVol = Templates.makeTextField("");
        batVol.setEditable(false);

        JLabel socLabel = Templates.makeLabel("Normalized state of charge: ");
        socLabel.setToolTipText("Ratio of state of charge of battery divided by maximum capacity");
        soc = Templates.makeTextField("");
        soc.setEditable(false);

        JComponent[] comps = {presLoadLabel, presLoad,
                              presIrradLabel, presIrrad,
                              pvCurrLabel, pvCurr,
                              invCurrLabel, invCurr,
                              invPowLabel, invPow,
                              batVolLabel, batVol,
                              batCurrLabel, batCurr,
                              socLabel, soc
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

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 20), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 20), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, systemTitle));

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "absorbed solar power", absSolar);
        CustomGui.connectStringOrNumberTextField(iface, "convective heat transfer coefficient", convCoef);
        CustomGui.connectStringOrNumberTextField(iface, "convective loss", convLoss);
        CustomGui.connectStringOrNumberTextField(iface, "module instantaneous efficiency", instEff);
        CustomGui.connectStringOrNumberTextField(iface, "module light-generated current", lightCurr);
        CustomGui.connectStringOrNumberTextField(iface, "module reverse saturation current", revCurr);
        CustomGui.connectStringOrNumberTextField(iface, "produced power", prodPower);
        CustomGui.connectStringOrNumberTextField(iface, "radiative loss", radLoss);

        CustomGui.connectStringOrNumberTextField(iface, "BCM status", bcm);
        CustomGui.connectStringOrNumberTextField(iface, "FCM status", fcm);
        CustomGui.connectStringOrNumberTextField(iface, "LVD status", lvd);

        CustomGui.connectStringOrNumberTextField(iface, "present load", presLoad);
        CustomGui.connectStringOrNumberTextField(iface, "present irradiance", presIrrad);
        CustomGui.connectStringOrNumberTextField(iface, "PV output current", pvCurr);
        CustomGui.connectStringOrNumberTextField(iface, "input current", invCurr);
        CustomGui.connectStringOrNumberTextField(iface, "input power", invPow);
        CustomGui.connectStringOrNumberTextField(iface, "battery current", batCurr);
        CustomGui.connectStringOrNumberTextField(iface, "battery output voltage", batVol);
        CustomGui.connectStringOrNumberTextField(iface, "normalized state of charge", soc);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Interference result card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ResultsCard());
		f.show();
	}
}
