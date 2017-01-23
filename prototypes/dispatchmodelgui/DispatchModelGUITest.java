package dispatchmodelgui;

import javax.swing.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 18, 2003
 * Time: 11:23:48 PM
 * To change this template use Options | File Templates.
 */

/**
 * This class should be used to test the dispatch model custom
 * gui as a stand alone application. The following procedure is
 * adopted:
 *
 * 1. DispatchModelGUITabbedPane object is created.
 *
 * Class DispatchModelGUITabbedPane creates all of the gui components.
 * The gui is only a shell and has no data objects that are stored
 * inside it.  Furthermore, the gui has no knowledge about any listeners
 * that are listening for property changes inside the gui.
 *
 * 2. DispatchModelGUIDataObjectManager object is created.
 *
 * Class DispatchModelGUIDataObjectManager creates all of the required data
 * objects that communicate with the dispatch model gui, when it runs as
 * a stand - along application.  DispatchModelGUIDataObjectManager manages
 * all of the lists that store the required gui data objects: DayDataObject,
 * FuelDataObject, PowerSourceDataObject.
 *
 * 3. DispatchModelGUIListeners object is created.
 *
 * The DispatchModelGUIListeners object takes a DispatchModelGUITabbedPane and
 * a DispatchModelGUIDataObjectManager object as an argument to its constructor.
 * It then listens to this object, which fires property changes.  Once a property
 * change is fired inside the gui, the DispatchModelGUIListeners object will take
 * action consistent with the desired behaviour of the gui.
 *
 * Architecture:
 * The chosen architecture is such that the gui components and gui data objects
 * have no knowledge about each other's existance.  The communication between
 * them is done via the DispatchModelGUIListeners class.
 *
 * For more information on each class, please see the descriptions included
 * inside the class files.
 *
 * Note: To run the dispatch model gui from within DOME, please use
 * DispatchModelGUIDomeInterface.java
 *
 * Developed at the MIT - CADlab, 2003.
 */

public class DispatchModelGUITest
{
    /**
     * Main method for stand alone application
     * @param args
     */
    public static void main(String[] args)
    {

        // JFrame is initally created.  This operation is not
        // necessary when running the gui from within DOME.
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // creating gui components
        DispatchModelGUITabbedPane pane = new DispatchModelGUITabbedPane();

        // adding gui components to the content pane of the frame
        f.getContentPane().add(pane);

        // creating the gui data object manager that manages all of the data objects
        DispatchModelGUIDataObjectManager mgr = new DispatchModelGUIDataObjectManager();

        // creating the listeners that listen for changes inside the gui and communicate
        // those changes to the data object manager.  The data object will then take
        // appropriate action.
        DispatchModelGUIListeners l = new DispatchModelGUIListeners(pane, mgr);

        // standard show
        f.show();

        // standard pack
        f.pack();
    }
}
