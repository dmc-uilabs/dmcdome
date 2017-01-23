package solarradgui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SolarRadGui extends JPanel {
    public static final GridBagConstraints gbc = null;

    private static final Dimension PREFERRED_SIZE = new Dimension(850, 500);
    private JTextField time;
    private JTextField rad;
    private ChartCard loadCard;

    public SolarRadGui(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

    /**
     * Constructor for the main Deploy playspace wizard
     */
    public SolarRadGui() {
        loadCard = new ChartCard("Solar irradiance profile", "Time", "solar irradiance", true);
        loadCard.setParamNames("time vector", "irradiance vector", "chart maximum time");
        loadCard.setYMax(1000);

        JComponent[] comps = {makeRadioPanel(), loadCard, makeDataPanel()};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 5), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0)
        };
        Templates.layoutGridBag(this, comps, gbcs);
        setPreferredSize(PREFERRED_SIZE);
    }

    private JPanel makeRadioPanel() {
        JPanel p = new JPanel();

        ImageIcon image = Templates.makeImageIcon("solarradgui/sun.jpg");
        JLabel imageLabel = new JLabel(image);

        //JPanel fill = new JPanel();
        //fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JComponent[] comps = {imageLabel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.NORTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBag(p, comps, gbcs);
        p.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        return p;
    }

    private JPanel makeDataPanel() {
        JPanel p = new JPanel();

        JLabel timeLabel = Templates.makeLabel("Time position (m): ");
        timeLabel.setToolTipText("Time position of interest");
        time = Templates.makeTextField("");

        JLabel radLabel = Templates.makeLabel("Solar irradiance (W/m^2): ");
        radLabel.setToolTipText("Amount of solar irradiance at the specified time");
        rad = Templates.makeTextField("");

        JComponent[] comps = {timeLabel, time,
                              radLabel, rad
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0),
        };
        Templates.layoutGridBag(p, comps, gbcs);
        p.setBorder(BorderFactory.createTitledBorder(null, "Solar irradiance calculation"));

        return p;
    }


    public void dispose() {
        SwingUtilities.windowForComponent(SolarRadGui.this).dispose();
    }

    public WindowAdapter getWindowAdapter() {
        return new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        };
    }

    private void setInterface(ModelInterfaceBase iface) {
        loadCard.setInterface(iface, true);
        CustomGui.connectStringOrNumberTextField(iface, "t_i", time);
        CustomGui.connectStringOrNumberTextField(iface, "irradiance_i", rad);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("PV system custom GUI");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new SolarRadGui());
        f.pack();
        f.show();
    }
}
