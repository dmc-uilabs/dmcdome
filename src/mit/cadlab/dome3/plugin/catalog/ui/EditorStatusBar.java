package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 31.
 */
public class EditorStatusBar extends JPanel {
    private JLabel messageLabel;
    private JLabel locationLabel;
    private JLabel memoryLabel;

    public EditorStatusBar() {
        initComponents();
    }

    public void initComponents() {
        messageLabel = new JLabel();
        locationLabel = new JLabel();
        memoryLabel = new JLabel();
        messageLabel.setBorder(UIUtil.STATUS_LABEL_BORDER);
        locationLabel.setBorder(UIUtil.STATUS_LABEL_BORDER);
        memoryLabel.setBorder(UIUtil.STATUS_LABEL_BORDER);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        messageLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, UIUtil.STATUS_BAR_HEIGHT));
        messageLabel.setFont(UIUtil.STATUS_BAR_FONT);
        locationLabel.setPreferredSize(new Dimension(UIUtil.LOCATION_LABEL_WIDTH, UIUtil.STATUS_BAR_HEIGHT));
        locationLabel.setFont(UIUtil.STATUS_BAR_FONT);
        locationLabel.setMinimumSize(locationLabel.getPreferredSize());
        locationLabel.setMaximumSize(locationLabel.getPreferredSize());
        memoryLabel.setPreferredSize(new Dimension(UIUtil.MEMORY_LABEL_WIDTH, UIUtil.STATUS_BAR_HEIGHT));
        memoryLabel.setFont(UIUtil.STATUS_BAR_FONT);
        memoryLabel.setMinimumSize(memoryLabel.getPreferredSize());
        memoryLabel.setMaximumSize(memoryLabel.getPreferredSize());
        this.add(messageLabel);
        this.add(Box.createHorizontalStrut(2));
        this.add(locationLabel);
        this.add(Box.createHorizontalStrut(2));
        this.add(memoryLabel);

        this.setMinimumSize(new Dimension(UIUtil.LOCATION_LABEL_WIDTH, UIUtil.STATUS_BAR_HEIGHT));
        this.setPreferredSize(new Dimension(UIUtil.LOCATION_LABEL_WIDTH, UIUtil.STATUS_BAR_HEIGHT));
        this.setBorder(UIUtil.STATUS_BAR_BORDER);
        setMessage("ready");
    }

    public void setMessage(String msg) {
        messageLabel.setText(msg);
    }

    public void setLocation(String loc) {
        locationLabel.setText(loc);
    }
}
