package mit.cadlab.dome3.network.client.objectrecord;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 31, 2003
 * Time: 7:22:43 PM
 * To change this template use Options | File Templates.
 */
public class ClientInterfaceRecord extends ClientObjectRecord {
    private ModelInterfaceRuntimeClient iface;
    private boolean ifaceOpened = false;
    private ClientPlayspaceRuntime playspaceRef = null;
    private String previousView;
    private ServerConnection svrConnection = null;
    private boolean createdOnServer = false;

    public ClientInterfaceRecord(ServerConnection ifaceServerConn, CompoundId compoundId,
                                 String name, String description,
                                 String url, ClientPlayspaceRuntime playspace) {
        super(compoundId, name, description, url);
        this.playspaceRef = playspace;
        svrConnection = ifaceServerConn;
    }

    public String getStaticId() {
        return compoundId.getInterfaceStaticId();
    }

    public CompoundId getRuntimeId() {
        return compoundId;
    }

    public void setInterface(ModelInterfaceRuntimeClient iface) {
        this.iface = iface;
    }

    public ModelInterfaceRuntimeClient getInterface() {
        createInterface();
        return iface;
    }

    public void setCurrentView(String view) {
        iface.setCurrentView(view);
    }

    public String getCurrentView() {
        if (iface == null) {
            //This is the case when listChildren has not been called and thus interface instance is
            //not yet created
            return ModelInterfaceRuntimeClient.INTERFACE_CAUSALITY_VIEW;
        }
        return iface.getCurrentView();
    }

    public void listChildren() {
        String cView = null;
        if (iface != null) {
            cView = iface.getCurrentView();
            if (cView.equals(previousView))
                return;				//don't do any work if its the same view

            //otherwise remove old view objs
            if (!content.isEmpty()) {
                Object[] objs = content.toArray();
                content.removeAll(objs);
            }
            //and add new view objs
            List icv = iface.getView(cView);
            content.addAll(icv);
            previousView = cView;
        } else {
            createInterface();
            //otherwise remove old view objs
            if (!content.isEmpty()) {
                Object[] objs = content.toArray();
                content.removeAll(objs);
            }
            //and add new view objs
            cView = ModelInterfaceRuntimeClient.INTERFACE_CAUSALITY_VIEW;
            List icv = iface.getView(cView);
            content.addAll(icv);
            previousView = cView;
        }
    }

    /*
    public void listChildren(boolean createIface) {
        if (createIface) {
            //listChildren();Qing: that's not true, notice the handle of iface in listChildren method!! should be consistent,
        } else {    //do not create interface on the server
            if (iface == null) {
                String ifaceXml = FileSystemFunctions.getInterfaceDescription(svrConnection, compoundId.getInterfaceStaticId());
                iface = new ModelInterfaceRuntimeClient(XMLUtils.stringToXmlElement(ifaceXml), true);
                ifaceOpened = false;
            }
            //otherwise remove old view objs
            if (!content.isEmpty()) {
                Object[] objs = content.toArray();
                content.removeAll(objs);
            }
            //and add new view objs
            String cView = ModelInterfaceRuntimeClient.INTERFACE_CAUSALITY_VIEW;
            List icv = iface.getView(cView);
            content.addAll(icv);
            previousView = cView;
        }
    }  */

    private void createInterface() {
        boolean isProjectResource = false;
        if (getRuntimeId().getFirstProjectStaticId() != null && getRuntimeId().getModelStaticId() != null)
            isProjectResource = true;
        if (ifaceOpened == false) {
            // try to get the interface and restore an existing interface panel
            iface = playspaceRef.getInterface(compoundId, svrConnection);
            if (iface == null) {
                iface = playspaceRef.createInterface(compoundId, svrConnection, isProjectResource);
            }
            iface.setCurrentView(ModelInterfaceRuntimeClient.INTERFACE_CAUSALITY_VIEW);
            ifaceOpened = true;
        }
    }

    /**
     * Return a list of filters which contain parameters
     * @return
     */
    public List getFilters(ModelInterfaceRuntimeClient iface) {
        if (iface == null) return Collections.EMPTY_LIST;

        List interfaceCausalityView = iface.getView(ModelInterfaceRuntimeClient.INTERFACE_CAUSALITY_VIEW);
        List systemCausalityView = iface.getView(ModelInterfaceRuntimeClient.SYSTEM_CAUSALITY_VIEW);
        List buildView = iface.getView(ModelInterfaceRuntimeClient.BUILD_VIEW);

        return interfaceCausalityView;
    }

}
