package dispatchmodelgui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 23, 2003
 * Time: 12:49:23 AM
 * To change this template use Options | File Templates.
 */

/**
 * DispatchModelGUIDomeInterface.java
 *
 * This class is the plugin to a dome native interface.
 * To construct a DispatchModelGUIDomeInterface object you must
 * pass in a ModelInterfaceBase object to its constructor.  This
 * object will then be used to create all of the necessary parameters
 * that exist inside the dome interface, which the user will have
 * a handle on inside the gui.
 *
 * An important note:
 *
 * This class extends from JPanel.  Possibly the worst example of
 * inheritence ever, but the current DOME architecture for custom
 * guis does not support a custom gui that does not inherit from
 * the JComponent class.  Therefore, extending from JPanel is only
 * a quick-fix to this problem and the class uses none of the
 * properties of its parent.  In the future DOME will able to support
 * interfaces that do not have to inherit JComponent and the JPanel
 * extension can be removed.
 *
 * DOME custom gui architecture:
 *
 * Dome object ModelInterfaceBase is passed into the constructor
 * of this class and assigned to a member variable of this class.
 * Inside the constructor all of the necessary gui components
 * are created.  First, instances of DispatchModelGUITabbedPane
 * and DispatchModelGUIDataObjectManager are created and assigned
 * to member variables of this class.  Next, a DispatchModelGUIListeners
 * object is created, which takes two arguments:
 * DispatchModelGUITabbedPane and DispatchModelGUIDataObjectManager.
 * The listener object is the glue between the classes and each class
 * passes instructions to the other via this listener object.  Finally,
 * a DispatchModelGUIDomeObjectManager is created, which takes two
 * arguments:  the listener (DispatchModelGUIListeners) and the DOME
 * native interface (DOMEModelInterfaceBase).
 *
 * The DispatchModelGUIDomeObjectManager communicates with the DOME
 * native interface by setting/getting values of its parameters.
 *
 */

public class DispatchModelGUIDomeInterface extends JPanel
{
    // grid bag constraints for GridBagLayout
    private static final GridBagConstraints gbc = null;

    // DOME native interface object
    private ModelInterfaceBase _iface;

    // listeners that listen for changes in the gui and fire property change events
    private DispatchModelGUIListeners _listeners;

    // object that holds the gui for the dispatch model gui
    private DispatchModelGUITabbedPane _pane;

    // manager of the data objects inside the gui
    private DispatchModelGUIDataObjectManager _dataMgr;

    // manager of the dome objects inside the gui
    private DispatchModelGUIDomeObjectManager _mgr;

    /**
     * The only constructor for the model dispatch gui.  This
     * constructor is to be used when user wishes to connect
     * the custom gui to a DOME native interface.  To run this
     * gui as a stand alone application (i.e. no DOME objects),
     * please use the DispatchModelGUITest class.
     * @param iface
     */
    public DispatchModelGUIDomeInterface(ModelInterfaceBase iface)
    {
        // interface is assigned to a member variable of the class
        _iface = iface;

        // members of this class are being created
        createComponents();

        // listener is instantiated
        createGUIListeners();

        // dome object manager is created
        createDomeObjectManager();

        // pane is inserted into a JPanel
        layoutComponents();
    }

    /**
     * this method instantiates two objects:
     * DispatchModelGUITabbedPane and
     * DispatchModelGUIDataObjectManager
     */
    protected void createComponents()
    {
        // gui pane
        _pane = new DispatchModelGUITabbedPane();

        // manager of gui data objects
        _dataMgr = new DispatchModelGUIDataObjectManager();
    }

    /**
     * listener between the gui components and gui
     * native data objects is instantiated in this
     * method
     */
    protected void createGUIListeners()
    {
        // gui - data object listener
        _listeners = new DispatchModelGUIListeners(_pane, _dataMgr);
    }

    /**
     * dome object manager that holds all of the pertinent
     * information to communicate with a DOME native interface
     */
    protected void createDomeObjectManager()
    {
        _mgr = new DispatchModelGUIDomeObjectManager(_listeners, _iface);
    }

    /**
     * the tabbed gui pane is inserted into a JPanel, which happens
     * to be DispatchModelGUIDomeInterface object.  I tried to explain this
     * non sense in the class description.  My apologies for bad code.
     */

    protected void layoutComponents()
    {
        JComponent[] comps = {

            _pane
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        GUIConstants.layoutGridBag(this, comps, gbcs);
    }


}
