package hybridsystemgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

public class CostOutputCard extends JPanel
{
	public static final GridBagConstraints gbc = null;

    private final String dieselTitle = "Diesel-generator-specific costs";
    private final String commonTitle = "Common costs";
    private final String pvTitle = "PV-specific costs";

    private JTextField engCost;
    private JTextField annFuel;
    private JTextField lccFuel;
    private JTextField recEng;
    private JTextField pvAlcc;
    private JTextField dieselAlcc;

    private JTextField pvLccOm;
    private JTextField pvLcc;
    private JTextField pvElec;
    private JTextField dieselLccOm;
    private JTextField dieselLcc;
    private JTextField dieselElec;
    private JTextField pvCap;
    private JTextField pvInst;
    private JTextField dieselCap;
    private JTextField dieselInst;

    private JTextField powCon;
    private JTextField pvArray;
    private JTextField batt;
    private JTextField support;

	public CostOutputCard()
	{
        JComponent[] comps = {makeCommonPanel(), makePvPanel(), makeDieselPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };

        Templates.layoutGridBag(this, comps, gbcs);
	}

    private JPanel makeDieselPanel() {
        JPanel bigP = new JPanel();

        JLabel engCostLabel = Templates.makeLabel("Engine generator cost ($/ kW): ");
        engCostLabel.setToolTipText("Capital cost per kW of engine-generator");
        engCost = Templates.makeTextField("");

        JLabel annFuelLabel = Templates.makeLabel("Annual fuel cost ($): ");
        annFuelLabel.setToolTipText("Total cost per liter of fuel consumed in one year");
        annFuel = Templates.makeTextField("");

        JLabel lccFuelLabel = Templates.makeLabel("Life-cycle fuel cost ($): ");
        lccFuelLabel.setToolTipText("Total cost of fuel over the entire analysis");
        lccFuel = Templates.makeTextField("");

        JLabel recEngLabel = Templates.makeLabel("Recurring cost of generator ($): ");
        recEngLabel.setToolTipText("Recurring cost of the diesel generator over the entire analysis");
        recEng = Templates.makeTextField("");


        JComponent[] comps = {annFuelLabel, annFuel,
                              engCostLabel, engCost,
                              lccFuelLabel, lccFuel,
                              recEngLabel, recEng,
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 10), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(bigP, comps, gbcs);
        bigP.setBorder(BorderFactory.createTitledBorder(null, dieselTitle));

        return bigP;
    }

    private JPanel makeCommonPanel() {
        JPanel p = new JPanel();

        JLabel pvLabel = Templates.makeLabel("       PV array       ", Templates.FONT11B);
        JLabel dieselLabel = Templates.makeLabel("Diesel generator", Templates.FONT11B);

        JLabel pvLccOmLabel = Templates.makeLabel("Life-cycle O&M cost ($): ");
        pvLccOmLabel.setToolTipText("Operation and maintenance cost required to keep the system operational over " +
                "the entire analysis");
        pvLccOm = Templates.makeTextField("");
        dieselLccOm = Templates.makeTextField("");

        JLabel pvLccLabel = Templates.makeLabel("Life-cycle cost ($): ");
        pvLccLabel.setToolTipText("Sum of all the costs of the system over its life time, presented in today’s money");
        pvLcc = Templates.makeTextField("");
        dieselLcc = Templates.makeTextField("");

        JLabel pvElecLabel = Templates.makeLabel("Electricity cost ($/ kWh): ");
        pvElecLabel.setToolTipText("Cost of electricity generated by the PV array or diesel generator");
        pvElec = Templates.makeTextField("");
        dieselElec = Templates.makeTextField("");

        JLabel AlccLabel = Templates.makeLabel("Annualized life-cycle cost($): ");
        AlccLabel.setToolTipText("Cost per year of the total life-cycle cost. This value cannot be calculated by simply " +
                "dividing the life-cycle cost with the number of years, since the value of money changes due to inflation");
        pvAlcc = Templates.makeTextField("");
        dieselAlcc = Templates.makeTextField("");

        JLabel pvCapLabel = Templates.makeLabel("Capital cost ($): ");
        pvCapLabel.setToolTipText("Capital cost of the PV array or diesel generator");
        pvCap = Templates.makeTextField("");
        dieselCap = Templates.makeTextField("");

        JLabel instLabel = Templates.makeLabel("Installation cost($): ");
        instLabel.setToolTipText("Cost of system installation, engineering, and management of the PV array or diesel generator");
        pvInst = Templates.makeTextField("");
        dieselInst = Templates.makeTextField("");

        JComponent[] comps = {pvLabel, dieselLabel,
                              pvCapLabel, pvCap, dieselCap,
                              instLabel, pvInst, dieselInst,
                              pvLccLabel, pvLcc, dieselLcc,
                              pvLccOmLabel, pvLccOm, dieselLccOm,
                              pvElecLabel, pvElec, dieselElec,
                              AlccLabel, pvAlcc, dieselAlcc,
    };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(-5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(-5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(2, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };

        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, commonTitle));

        return p;
    }

    private JPanel makePvPanel() {
        JPanel p = new JPanel();

        JLabel powConLabel = Templates.makeLabel("Recurring cost of power conditioner ($): ");
        powConLabel.setToolTipText("Recurring cost of charge/discharge regulator for power conditioning over the entire analysis");
        powCon = Templates.makeTextField("");

        JLabel dieselOptPowLabel = Templates.makeLabel("Recurring cost of PV modules ($): ");
        dieselOptPowLabel.setToolTipText("Recurring cost of PV modules over the entire analysis");
        pvArray = Templates.makeTextField("");

        JLabel battLabel = Templates.makeLabel("Recurring cost of battery ($): ");
        battLabel.setToolTipText("Recurring cost of battery over the entire analysis");
        batt = Templates.makeTextField("");

        JLabel supportLabel = Templates.makeLabel("Recurring cost of PV array support ($): ");
        supportLabel.setToolTipText("Recurring cost of PV array support over the entire analysis");
        support = Templates.makeTextField("");

        JComponent[] comps = {battLabel, batt,
                              supportLabel, support,
                              dieselOptPowLabel, pvArray,
                              powConLabel, powCon,
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
        p.setBorder(BorderFactory.createTitledBorder(null, pvTitle));

        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "diesel engine-generator cost", engCost);
        CustomGui.connectStringOrNumberTextField(iface, "diesel annual fuel cost", annFuel);
        CustomGui.connectStringOrNumberTextField(iface, "diesel life-cycle fuel cost", lccFuel);
        CustomGui.connectStringOrNumberTextField(iface, "diesel recurring cost of engine-generator", recEng);

        CustomGui.connectStringOrNumberTextField(iface, "PV annualized life-cycle cost", pvAlcc);
        CustomGui.connectStringOrNumberTextField(iface, "diesel annualized life-cycle cost", dieselAlcc);
        CustomGui.connectStringOrNumberTextField(iface, "PV life-cycle O&M cost", pvLccOm);
        CustomGui.connectStringOrNumberTextField(iface, "PV life-cycle cost", pvLcc);
        CustomGui.connectStringOrNumberTextField(iface, "PV electricity cost", pvElec);
        CustomGui.connectStringOrNumberTextField(iface, "diesel life-cycle O&M cost", dieselLccOm);
        CustomGui.connectStringOrNumberTextField(iface, "diesel life-cycle fuel cost", dieselLcc);
        CustomGui.connectStringOrNumberTextField(iface, "diesel electricity cost", dieselElec);
        CustomGui.connectStringOrNumberTextField(iface, "PV capital cost", pvCap);
        CustomGui.connectStringOrNumberTextField(iface, "PV installation cost", pvInst);
        CustomGui.connectStringOrNumberTextField(iface, "diesel capital cost", dieselCap);
        CustomGui.connectStringOrNumberTextField(iface, "diesel installation cost", dieselInst);
        
        CustomGui.connectStringOrNumberTextField(iface, "PV recurring cost of battery", batt);
        CustomGui.connectStringOrNumberTextField(iface, "PV recurring cost of power conditioner", powCon);
        CustomGui.connectStringOrNumberTextField(iface, "PV recurring cost of PV array", pvArray);
        CustomGui.connectStringOrNumberTextField(iface, "PV recurring cost of PV module support", support);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Config card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new CostOutputCard());
        f.pack();
		f.show();
	}
}
