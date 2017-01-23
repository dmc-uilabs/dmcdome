package pvarraygui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PVArrayGui extends JPanel {
    public static final GridBagConstraints gbc = null;

    private static final Dimension PREFERRED_SIZE = new Dimension(650, 460);

    private JTextField rad;
    private JTextField ambientTemp;
    private JTextField windSpeed;
    private JTextField cellArea;
    private JTextField moduleArea;
    private JTextField terminalVoltage;
    private JTextField numParallel;
    private JTextField numSeries;
    private JTextField cellTemp;
    private JTextField pvCurr;
    private JTextField instEff;
    private JTextField cellTempChange;
    private JTextField absorption;

    public PVArrayGui(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

    public PVArrayGui() {
        JTextArea moduleText = Templates.makeDTextArea("Given a photovoltaic array's design specifications and" +
                " surrounding conditions, this interface calculates the generated current and properties of the array.");
        moduleText.setOpaque(false);
        JPanel p = new JPanel();
        JComponent[] bcomps = {moduleText, makeDataPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] bgbcs = {
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 0, 5, 5), 0, 0)
        };
        Templates.layoutGridBag(p, bcomps, bgbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, "Photovoltaic array operation"));

        JComponent[] comps = {makeRadioPanel(), p};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0),
        };
        Templates.layoutGridBag(this, comps, gbcs);
        setPreferredSize(PREFERRED_SIZE);
    }

    private JPanel makeRadioPanel() {
        JPanel p = new JPanel();

        ImageIcon image = Templates.makeImageIcon("pvarraygui/solar-panel.jpg");
        JLabel imageLabel = new JLabel(image);

        JComponent[] comps = {imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.NORTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    private JPanel makeDataPanel() {
        JPanel p = new JPanel();

        JLabel inputLabel = Templates.makeLabel("inputs", Templates.FONT11B);

        JLabel radLabel = Templates.makeLabel("Solar irradiance (W/m^2): ");
        radLabel.setToolTipText("Amount of solar irradiance at the specified time");
        rad = Templates.makeTextField("");

        JLabel absorptionLabel = Templates.makeLabel("Absorption coefficient: ");
        absorptionLabel.setToolTipText("Overall absorption coefficient of PV module");
        absorption = Templates.makeTextField("");

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

        JLabel cellTempLabel = Templates.makeLabel("Solar cells' temperature (K): ");
        cellTempLabel.setToolTipText("Temperature of the solar cells in the array");
        cellTemp = Templates.makeTextField("");

        JLabel ambientTempLabel = Templates.makeLabel("Ambient temperature (K): ");
        ambientTempLabel.setToolTipText("Temperature of the ambient air in the vicinity of the panel");
        ambientTemp = Templates.makeTextField("");

        JLabel windSpeedLabel = Templates.makeLabel("Wind speed (m/s): ");
        windSpeedLabel.setToolTipText("Speed of wind blowing past PV panel");
        windSpeed = Templates.makeTextField("");

        JLabel resultLabel = Templates.makeLabel("results", Templates.FONT11B);

        JLabel pvCurrLabel = Templates.makeLabel("PV module output current (A): ");
        pvCurrLabel.setToolTipText("Output current of PV panel");
        pvCurr = Templates.makeTextField("");
        pvCurr.setEditable(false);

        JLabel cellTempChangeLabel = Templates.makeLabel("Change in solar cells' temperature (K): ");
        cellTempChangeLabel.setToolTipText("Change in temperature of the solar cells in the array");
        cellTempChange = Templates.makeTextField("");
        cellTempChange.setEditable(false);

        JLabel instEffLabel = Templates.makeLabel("Instantaneous efficiency (%): ");
        instEffLabel.setToolTipText("Instantaneous efficiency of the module. This value drops as the cell’s temperature increases");
        instEff = Templates.makeTextField("");
        instEff.setEditable(false);

        JComponent[] comps = {inputLabel,
                              radLabel, rad,
                              absorptionLabel, absorption,
                              cellAreaLabel, cellArea,
                              moduleAreaLabel, moduleArea,
                              terminalVoltageLabel, terminalVoltage,
                              numParallelLabel, numParallel,
                              numSeriesLabel, numSeries,
                              cellTempLabel, cellTemp,
                              ambientTempLabel, ambientTemp,
                              windSpeedLabel, windSpeed,
                              resultLabel,
                              pvCurrLabel, pvCurr,
                              cellTempChangeLabel, cellTempChange,
                              instEffLabel, instEff,
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 9, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 10, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 12, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 13, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 14, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }


    public void dispose() {
        SwingUtilities.windowForComponent(PVArrayGui.this).dispose();
    }

    public WindowAdapter getWindowAdapter() {
        return new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        };
    }

    private void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "solar irradiance", rad);
        CustomGui.connectStringOrNumberTextField(iface, "module absorption coefficient", absorption);
        CustomGui.connectStringOrNumberTextField(iface, "cell area", cellArea);
        CustomGui.connectStringOrNumberTextField(iface, "module area", moduleArea);
        CustomGui.connectStringOrNumberTextField(iface, "module terminal voltage", terminalVoltage);
        CustomGui.connectStringOrNumberTextField(iface, "number of module in parallel", numParallel);
        CustomGui.connectStringOrNumberTextField(iface, "number of module in series", numSeries);
        CustomGui.connectStringOrNumberTextField(iface, "cell temperature", cellTemp);
        CustomGui.connectStringOrNumberTextField(iface, "ambient temperature", ambientTemp);
        CustomGui.connectStringOrNumberTextField(iface, "wind speed", windSpeed);
        CustomGui.connectStringOrNumberTextField(iface, "PV output current", pvCurr);
        CustomGui.connectStringOrNumberTextField(iface, "module instantaneous efficiency", instEff);
        CustomGui.connectStringOrNumberTextField(iface, "cell temperature change", cellTempChange);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("PV array custom GUI");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new PVArrayGui());
        f.pack();
        f.show();
    }
}
