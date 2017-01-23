package mit.cadlab.dome3.plugin.catalog.ui;

/**
 * User: Sangmok Han
 * Date: 2006. 8. 11.
 */
public interface DynamicComponent {
    public void update();
    public boolean isEnabled();
    public void setEnabled(boolean isEnabled);
}
