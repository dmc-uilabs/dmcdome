package com.ford.kbedoor.sealanalysis.oi;

import com.sdrc.openideas.*;
import com.sdrc.openideas.util.OI_App;
import com.sdrc.openideas.util.OI_CommandWait;
import com.sdrc.openideas.util.OI_EventObserverLink;
import com.sdrc.openideas.util.OI_ItemSelector;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ORB;

import java.util.Properties;
import java.util.Vector;

/**
 * IdeasServer is a class which encapsulates
 * OI classes and functionality for convenience.
 * We use IIOP for connecting to the server and
 * hence you must have an environment variable
 * which represents the stringified orb. This is
 * automatically set if you start a window from
 * within I-DEAS. This ensures a more secure way
 * of connecting to the server so that the client does
 * not connect to anyone else's I-DEAS session
 * running on the same machine.
 *
 * originally written by Yao Ge.
 * modified by Ravi Boppe.
 * June 2000
 * Modified of Ideas 10.1 port by Parasu Narayanan.
 * Aug 2003
 */
public class IdeasServer extends OI_App implements ServerEventListener {
    protected Properties props = null;
    public static IntHolder errorCode = new IntHolder();
    protected String _hostName = "localhost";

    // OI Objects
    protected OI_Server _server = null;
    protected OI_DataInstallation _pdm = null;
    protected OI_WorkbenchServer _wbServer = null;
    protected OI_CommandServer _cmdSvr = null;
    protected OI_GUIServer _guiServer = null;
    protected OI_ListRegion _listRegion = null;
    protected OI_CommandWait _commandWait = null;
    protected OI_ItemSelector _itemSelector = null;
    protected OI_GraphicsRegion _graphicsRegion = null;
    protected OI_SelectionServer _selectionServer = null;
    protected OI_AccessControlServer _accessControlServer = null;
    protected OI_EventObserverLink _eventObservers[] = null;

    protected String oiErrorMessage = null;

    protected Vector _listenerList[] =
            new Vector[ServerEvent.TypeCount];

    protected OI_EventInfo _oiEvents[] =
            {
                new OI_EventInfo(OI_EventType.OIE_ServerExiting, null),
                new OI_EventInfo(OI_EventType.OIE_TaskSwitch, null),
                new OI_EventInfo(OI_EventType.OIE_ApplicationSwitch, null),
                new OI_EventInfo(OI_EventType.OIE_ModelFileSwitch, null),
                new OI_EventInfo(OI_EventType.OIE_SendList, null),
                new OI_EventInfo(OI_EventType.OIE_SendError, null),
                new OI_EventInfo(OI_EventType.OIE_SendCommand, null),
                new OI_EventInfo(OI_EventType.OIE_CommandCompleted, null)
            };

    // local caches
    protected OI_Project _currentProject;

    public IdeasServer() {
        super(" ", null);
    }

    /**
     * Connect to other Ideas servers
     */
    public void connect(OI_Server oiSvr, ORB orb)
            throws Exception {
        _server = oiSvr;
        errorCode = new IntHolder();
        m_orb = orb;
            try {
                if (_server == null) {
                    throw new Exception("OI Server is null");
                }
                // Get Access Control Server
                if ((_accessControlServer = _server.GetAccessControlServer()) == null) {
                    oiErrorMessage = _server.GetCurrentErrorMessage();
                    throw new Exception(oiErrorMessage);
                }
/*                // Lock the server so that no one else connects to it
                if ((_accessControlServer.LockServer()) != OI_ErrorCodes.C_NO_ERROR) {
                    oiErrorMessage = _server.GetCurrentErrorMessage();
                    throw new Exception(oiErrorMessage);
                }*/
                // Get Selection Server
                if ((_selectionServer = _server.GetSelectionServer()) == null) {
                    oiErrorMessage = _server.GetCurrentErrorMessage();
                    throw new Exception(oiErrorMessage);
                }
                // Get Item Selector
                if ((_itemSelector = new OI_ItemSelector(_selectionServer)) == null) {
                    throw new Exception("Could Not Create OI_SelectItems");
                }
                // Get Command Server
                if ((_cmdSvr = _server.GetCommandServer()) == null) {
                    oiErrorMessage = _server.GetCurrentErrorMessage();
                    throw new Exception(oiErrorMessage);
                }
            } catch (SystemException e) {
                throw e;
            }
            turnOffAutoSave();
    }

    /**
     * Check if connected to I-DEAS.
     */
    public boolean isConnected() {
        return (_server != null);
    }

    /**
     * disconnect from I-DEAS.
     */
    public void disconnectFromCAD() {
/*        if (isConnected()) {
            try {
                m_orb.shutdown(true);
                System.out.println("shutdown");
                m_orb.destroy();
                System.out.println("destroy");
            } catch (org.omg.CORBA.SystemException se) {
                System.out.println(se);
                System.exit(1);
            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(1);
            }
        }*/
//        System.out.println("disconnected");
        // Null everything out.
        // to remove any stale object references.
        //_accessControlServer.UnlockServer();
        removeAllListeners();
        //turnOnAutoSave();
        m_orb = null;
        //System.out.println("Orb is null");
        _server = null;
        _pdm = null;
        _wbServer = null;
        _cmdSvr = null;
        _guiServer = null;
        _listRegion = null;
        _commandWait = null;
        _itemSelector = null;
        _graphicsRegion = null;
        _selectionServer = null;
        _accessControlServer = null;
    }

    /**
     * get I-DEAS server (OI_Server) instance.
     */
    public OI_Server getOIServer() {
        return _server;
    }

    /**
     * Turns off I-DEAS autosave prompt feature
     * this prevents locking of the server.
     */
    public void turnOffAutoSave() {
        sendCommandAndWait("$ mpos :; /O P P 8 D SR OFF AS OFF OK OK $ return;", 0);
    }

    /**
     * Turns on I-DEAS autosave prompt feature.
     * If you turn off autosave feature for any
     * reason, turn it back on while disconnecting
     * from the OI program.
     */
    public void turnOnAutoSave() {
        sendCommand("$ mpos :; /O P P 8 D SR ON AS ON OK OK $ return;");
    }

    /**
     *  Prints a message to the I-DEAS list region.
     *
     *  @param msg Message to be printed to the list region.
     */
    public void printMessage(String msg) {
        if (isConnected()) {
            if (_guiServer == null)
                _guiServer = _server.GetGUIServer();
            if (_listRegion == null)
                _listRegion = _guiServer.GetListRegion();
            if(_listRegion != null)
                _listRegion.PutToList(msg);
        }
    }

    /**
     *  send a command to I-DEAS using commandserver.
     *
     *  @param cmdStr Command String.
     */
    public void sendCommand(String cmdStr) {
        if (isConnected()) {
            if (_cmdSvr == null)
                _cmdSvr = _server.GetCommandServer();
            _cmdSvr.SendCommand(cmdStr);
            //System.out.println(_cmdSvr.GetCurrentErrorMessage());
        }
    }

    /**
     *  send a command to I-DEAS using commandserver.
     *  and waits till it is completed. Returns the
     *  results in a String object.
     *
     *  @param cmdStr Command String.
     */
    public String sendCommandAndWait(String cmdStr) {
        String results = null;
        if (isConnected()) {
            _commandWait = new OI_CommandWait(m_orb, _cmdSvr);
            if (_commandWait.SendCommand(cmdStr, OI_CommandWait.E_Both)) {

                results = _commandWait.GetResultsString();
                //printCommandErrors(_commandWait.GetErrors());
            }
        }
        return results;
    }

    public void printCommandErrors(int[] piErrors) {
        if (piErrors.length > 0) {
            System.out.println("Command Errors:");
            for (int ii = 0; ii < piErrors.length; ii++) {
                System.out.println(_server.GetErrorMessage(piErrors[ii]));
                System.out.println("");
            }
        }
    }

    public String sendCommandAndWait(String cmdStr, int awaitResults) {
        String results = null;
        if (isConnected()) {
            if (awaitResults != 0) {
                results = sendCommandAndWait(cmdStr);
            } else {
                _commandWait = new OI_CommandWait(m_orb, _cmdSvr);
                _commandWait.SendCommand(cmdStr, OI_CommandWait.E_Both);
                _commandWait = null;
            }
        }
        return results;
    }

    /**
     *  Get Parts on the I-DEAS workbench.
     *
     */
    public OI_Part[] getWorkbenchParts()
            throws Exception {
        if (!isConnected())
            throw new Exception("Not connected to CAD Server");

        if (_wbServer == null)
            _wbServer = _server.GetWorkbenchServer();

        OI_Part[] parts = getParts("*");

        Vector list = new Vector();

        for (int i = 0; i < parts.length; i++)
            if (_wbServer.IsOnWorkbench(parts[i]))
                list.addElement(parts[i]);

        if (list.size() == 0)
            throw new Exception("No part on workbench");

        OI_Part[] wbParts = new OI_Part[list.size()];
        list.copyInto(wbParts);
        return wbParts;
    }


    /**
     *  Get Parts with the specified qualifier.
     *
     * @param qualifier
     */
    public OI_Part[] getParts(String qualifier)
            throws Exception {
        if (!isConnected())
            throw new Exception("Not connected to CAD Server");

        OI_ModelFile currentFile = _server.GetActiveModelFile();

        IntHolder err = new IntHolder(_server.GetErrorCode());
        if (err.value != OI_ErrorCodes.C_NO_ERROR)
            throw new Exception("Could not retrieve active model file "
                    + " error code: " +
                    Integer.toString(err.value));

        OI_Part[] oiParts = currentFile.GetParts(qualifier, "*", "*",
                OID_GET_LATEST_VERSION.value, err);

        return (oiParts);
    }

    /**
     *  Get Parts with the specified qualifier
     *  from the specified folder.
     *
     * @param folder Folder name.
     * @param qualifer qualifier.
     *
     */
    public OI_Part[] getParts(String folder, String qualifer)
            throws Exception {
        if (!isConnected())
            throw new Exception("Not connected to CAD Server");
        OI_Bin[] bins;
        bins = _server.GetBins(folder, errorCode);

        if (bins.length < 1)
            throw new Exception("Folder " + folder + " does not exist");
        OI_Part[] oiParts = bins[0].GetParts(qualifer, "*", "*",
                OID_GET_LATEST_VERSION.value,
                errorCode);
        return (oiParts);
    }

    /**
     *  Get workbench assembly.
     *
     */
    public OI_Assembly getAssembly()
            throws Exception {
        if (!isConnected())
            throw new Exception("No connection to CAD Server");

        if (_wbServer == null)
            _wbServer = _server.GetWorkbenchServer();

        OI_Assembly asm = _wbServer.GetAssembly();

        if (asm == null)
            throw new Exception("No assembly on the workbench");

        return (asm);
    }

    /**
     * Get the assemblies in the current model file.
     *
     * @param qualifier qualifier.
     *
     */
    public OI_Assembly[] getAssemblies(String qualifier)
            throws Exception {
        if (!isConnected())
            throw new Exception("Not connected to CAD Server");

        OI_ModelFile currentFile = _server.GetActiveModelFile();

        IntHolder err = new IntHolder(_server.GetErrorCode());

        if (err.value != OI_ErrorCodes.C_NO_ERROR ||
                currentFile == null)
            throw new Exception("Could not retrieve active model file "
                    + " error code: " + Integer.toString(err.value));

        OI_Assembly[] oiAsm = currentFile.GetAssemblies(qualifier, "*", "*",
                OID_GET_LATEST_VERSION.value, err);

        if (oiAsm.length < 1)
            throw new Exception("No Assemblies in Model File");

        if (err.value != OI_ErrorCodes.C_NO_ERROR)
            throw new Exception("Unable to get assembly as " + qualifier);

        return (oiAsm);
    }

    /**
     * Get the assemblies in the current model file,
     * with the specified qualifier.
     *
     * @param folder Folder name.
     * @param qualifer qualifier.
     *
     */
    public OI_Assembly[] getAssemblies(String folder, String qualifer)
            throws Exception {
        if (!isConnected())
            throw new Exception("No connection to CAD Server");

        OI_Bin[] bins;
        bins = _server.GetBins(folder, errorCode);

        if (bins.length < 1)
            throw new Exception("Folder " + folder + " does not exist");

        OI_Assembly[] oiAssms = bins[0].GetAssemblies(qualifer, "*", "*",
                OID_GET_LATEST_VERSION.value,
                errorCode);

        if (oiAssms.length < 1)
            throw new Exception("No assembly match " + qualifer);

        return oiAssms;
    }

    /**
     *  Get Projects.
     *
     * @param qualifier qualifier.
     */
    public OI_Project[] getProjects(String qualifier)
            throws Exception {

        if (!isConnected())
            return new OI_Project[0];
        if (_pdm == null)
            _pdm = _server.GetDataInstallation();

        IntHolder err = new IntHolder();

        OI_Project[] projects = _pdm.GetProjects(qualifier, err);
        if (err.value != OI_ErrorCodes.C_NO_ERROR)
            throw new Exception("Unable to get projects" + qualifier);

        return (projects);
    }

    /**
     *  Get Current project.
     */
    public OI_Project getCurrentProject() {
        return _currentProject;
    }

    /**
     *  Set Current project.
     *
     * @param project OI_Project.
     *
     */
    public void setCurrentProject(OI_Project project) {
        _currentProject = project;
    }

    /**
     *  Load a part or an assembly onto the workbench.
     *
     * @param model an instance of OI_Part or OI_Assembly.
     *
     */
    public void load(OI_Root model) {
        if (!isConnected())
            return;

        if (_wbServer == null)
            _wbServer = _server.GetWorkbenchServer();

        OI_Root modelItem = model;

        if (!(model instanceof OI_Part) && (!(model instanceof OI_Assembly))) {
            System.err.println("Invalid instance of model passed in: " +
                    "it has to be either Part or Assembly");
            return;
        }

        if (!_wbServer.IsOnWorkbench((OI_ModelingItem) modelItem)) {
            int err = _wbServer.GetOnto((OI_ModelingItem) modelItem);
            if (err != OI_ErrorCodes.C_NO_ERROR) {
                System.err.println("Unable to bring model onto work bench");
                System.err.println(_wbServer.GetErrorMessage(err));
            }
        }
    }

    /**
     *  putaway a part or an assembly from the workbench.
     *
     * @param model an instance of OI_Part or OI_Assembly.
     *
     */
    public void unload(OI_Root model) {
        if (!isConnected())
            return;

        if (_wbServer == null)
            _wbServer = _server.GetWorkbenchServer();

        IntHolder errorCode = new IntHolder(0);
        if (model == null) {
            OI_Part[] parts = _wbServer.GetParts("*", "*", "*", 0, errorCode);
            for (int ii = 0; ii < parts.length; ii++)
                if (parts[ii] != null) unload(parts[ii]);
            return;
        }

        OI_Root modelItem = model;

        if (!(model instanceof OI_Part) && (!(model instanceof OI_Assembly))) {
            System.err.println("Invalid instance of model passed in: " +
                    "it has to be either Part or Assembly");
            return;
        }

        if (_wbServer.IsOnWorkbench((OI_ModelingItem) modelItem)) {
            int err = _wbServer.PutAway((OI_ModelingItem) modelItem);
            if (err != OI_ErrorCodes.C_NO_ERROR &&
                    err != OI_ErrorCodes.C_NOT_ON_WORKBENCH) {
                System.err.println("Unable to putaway model from work bench");
                System.err.println(_wbServer.GetErrorMessage(err));
            }
        }
    }
    /**
     * Returns the part instances (Children) associated
     * with the given assembly.
     *
     * @param assembly Assembly for which parts are to be queried.
     * @return Children (as OI_InstanceSequence)
     * @throws Exception (in case parts are not found)
     */
    /*public OI_Part[] getAssemblyParts(OI_Assembly assembly)
       throws Exception
    {

       IntHolder errorCode = new IntHolder();

       OI_Instance currInstance = null;
       OI_AssemblyInstance assemblyInstance = null;
       OI_InstanceSequenceHolder partInstances= null;

       // Get the Root Instance.
       currInstance = com.sdrc.openideas.OI_AssemblyInstanceHelper.narrow( assembly.GetRootInstance());
       assemblyInstance = com.sdrc.openideas.OI_AssemblyInstanceHelper.narrow(curInstance);

       // Get the descendant parts.
       // For now, assume that the assembly does not
       // contain instances of other assemblies.
       // will change this later.

       partInstances= assemblyInstance.GetDescendantParts(errorCode);

       // Handle error condition
       if (errorCode.value != OI_ErrorCodes.C_NO_ERROR)
          throw new Exception("error code: " + errorCode.toString());

       // From the Instance sequence, get the actual part reference
       // and return in a list.

       OI_Part[] parts = new OI_Part[partInstances.length];
       for(int i = 0; i < partInstances.length; i++)
       {
          parts[i] = partInstances[i];
       }
       return parts;
    }*/

    /**
     * Returns the Instance aware parts associated
     * with the given assembly.
     *
     * @param assembly Assembly for which parts are to be queried.
     * @return Children (as OI_Parts)
     * @throws Exception (in case parts are not found)
     */
    /*public OI_Part[] getAssemblyInstanceAwareParts(OI_Assembly assembly)
       throws Exception
    {

       IntHolder errorCode = new IntHolder();

       OI_AssemblyInstance assemblyInstance = null;
       OI_InstanceSequence partInstances= null;

       // Get the Root Instance.
       assemblyInstance = (OI_AssemblyInstance)assembly.GetRootInstance();

       // Get the descendant parts.
       // For now, assume that the assembly does not
       // contain instances of other assemblies.
       // will change this later.

       partInstances= assemblyInstance.GetDescendantParts(errorCode);

       // Handle error condition
       /*if (errorCode.value != OI_ErrorCodes.C_NO_ERROR)
          throw new Exception("error code: " + errorCode.toString());

       // From the Instance sequence, get the actual part reference
       // and return in a list.

       OI_Part[] parts = new OI_Part[partInstances.length];
       for(int i = 0; i < partInstances.length; i++)
       {
          parts[i] = (partInstances[i].GetInstanceAwarePart());
       }
       return partInstances;
    }*/

    /**
     * Add listeners to the ServerExitEvent.
     *
     * @param l ServerEventListener .
     */
    public void addServerExitListener(ServerEventListener l) {
        if (_listenerList[ServerEvent.ServerExit] == null)
            _listenerList[ServerEvent.ServerExit] = new Vector();
        _listenerList[ServerEvent.ServerExit].addElement(l);
    }

    public void addModelSwitchListener(ServerEventListener l) {
        if (_listenerList[ServerEvent.ModelSwitch] == null)
            _listenerList[ServerEvent.ModelSwitch] = new Vector();
        _listenerList[ServerEvent.ModelSwitch].addElement(l);
    }

    public void addPrintMessageListener(ServerEventListener l) {
        if (_listenerList[ServerEvent.PrintMessage] == null)
            _listenerList[ServerEvent.PrintMessage] = new Vector();

        _listenerList[ServerEvent.PrintMessage].addElement(l);
    }

    public void addPrintErrorListener(ServerEventListener l) {
        if (_listenerList[ServerEvent.PrintError] == null)
            _listenerList[ServerEvent.PrintError] = new Vector();

        _listenerList[ServerEvent.PrintError].addElement(l);
    }

    public void addSendCommandListener(ServerEventListener l) {
        if (_listenerList[ServerEvent.SendCommand] == null)
            _listenerList[ServerEvent.SendCommand] = new Vector();

        _listenerList[ServerEvent.SendCommand].addElement(l);
    }

    public void addCommandCompleteListener(ServerEventListener l) {

        if (_listenerList[ServerEvent.CommandComplete] == null)
            _listenerList[ServerEvent.CommandComplete] = new Vector();

        _listenerList[ServerEvent.CommandComplete].addElement(l);


    }

    private void removeAllListeners() {
        for (int i = 0; i < _listenerList.length; i++) {
            if (_listenerList[i] != null)
                _listenerList[i].removeAllElements();
        }
    }

    public void handleEvent(ServerEvent event) {
        int type = event.getType();

        if (_listenerList[type] != null) {
            for (int i = 0; i < _listenerList[type].size(); i++)
                ((ServerEventListener) _listenerList[type].elementAt(i)).handleEvent(event);
        } else
            System.out.println("No event listener registered");
    }

    /** disables the updates to the graphics region */
    public void disableGraphics() {
        if (_guiServer == null)
            _guiServer = _server.GetGUIServer();
        if (_graphicsRegion == null)
            _graphicsRegion = _guiServer.GetGraphicsRegion();

        if (_graphicsRegion.SetVisibility(false) != 0)
            System.err.println("Cannot disable graphics region");

    }

    /** enables the updates to the graphics region */
    public void enableGraphics() {
        if (_guiServer == null)
            _guiServer = _server.GetGUIServer();
        if (_graphicsRegion == null)
            _graphicsRegion = _guiServer.GetGraphicsRegion();

        if (_graphicsRegion.SetVisibility(true) != 0)
            System.err.println("Cannot enable graphics region");
        _graphicsRegion.Redisplay();

    }

    /**
     *  A class which implements the interfaces for server callback
     *  events. If we need to monitory any server event (OI_EventType),
     *  try doing it here.
     */
    class ServerCallbacks implements OI_CallbackInterfaceOperations {
        public void CallbackMessage(OI_EventData eventData) {
            synchronized (eventData) {
                String msg;
                switch (eventData.event.value()) {
                    case OI_EventType._OIE_ServerExiting:
                        handleEvent(new ServerEvent(ServerEvent.ServerExit, null));
                        break;

                    case OI_EventType._OIE_ModelFileSwitch:
                        handleEvent(new ServerEvent(ServerEvent.ModelSwitch, null));
                        break;

                    case OI_EventType._OIE_SendList:
                        msg = new String(eventData.anyData.extract_string());
                        handleEvent(new ServerEvent(ServerEvent.PrintMessage, msg));
                        break;

                    case OI_EventType._OIE_SendError:
                        msg = new String(eventData.anyData.extract_string());
                        handleEvent(new ServerEvent(ServerEvent.PrintError, msg));
                        break;

                    case OI_EventType._OIE_SendCommand:
                        handleEvent(new ServerEvent(ServerEvent.SendCommand, null));
                        break;

                    case OI_EventType._OIE_CommandCompleted:
                        handleEvent(new ServerEvent(ServerEvent.CommandComplete, null));
                        break;

                    default:
                        System.out.println("Unspported Server Event Type: " +
                                eventData.event.value());
                }
            }
        }
    }

}
