package loadgui;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoadGui extends JPanel {
    public static final GridBagConstraints gbc = null;

    private static final Dimension PREFERRED_SIZE = new Dimension(850, 500);
    private JTextField time;
    private JTextField rad;
    private ChartCard loadCard;

    public LoadGui(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

    public LoadGui() {
        loadCard = new ChartCard("Load demand profile", "Time", "load", true);
        loadCard.setParamNames("time vector", "load vector", "chart maximum time");
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

        ImageIcon image = Templates.makeImageIcon("loadgui/houses.jpg");
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

        JLabel radLabel = Templates.makeLabel("Load demand (W): ");
        radLabel.setToolTipText("Amount of load demand at the specified time");
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
        p.setBorder(BorderFactory.createTitledBorder(null, "Load demand calculation"));

        return p;
    }


    public void dispose() {
        SwingUtilities.windowForComponent(LoadGui.this).dispose();
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
        CustomGui.connectStringOrNumberTextField(iface, "time position", time);
        CustomGui.connectStringOrNumberTextField(iface, "present load", rad);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Load demand GUI");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new LoadGui());
        f.pack();
        f.show();
    }
}
