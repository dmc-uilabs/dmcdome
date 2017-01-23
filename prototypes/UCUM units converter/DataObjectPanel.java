// DataObjectPanel.java

import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeListener;

public abstract class DataObjectPanel extends JPanel {

    protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
    public abstract void setDataObject(DataObject data);
    protected abstract PropertyChangeListener getPropertyListener();

}
