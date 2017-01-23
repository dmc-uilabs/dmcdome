package mit.cadlab.dome3.api.domewc;

import mit.cadlab.dome3.api.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.*;
import java.io.PrintWriter;

/**
 * User: Sangmok Han
 * Date: Apr 14, 2005
 */
public class DomeSession implements HttpSessionBindingListener {
    public static final String DEFAULT_CONN_NAME = "DEFAULT_CONN_NAME";
    public static final String DOME_SESSION_BINDING_NAME = "DOME_SESSION";

    Map domeConnMap = new HashMap(); // String "demo1_conn" -> DomeConnection domeConn
    Map userIdMap = new HashMap(); // String "demo1_conn" -> String "guest"
    Map passwordMap = new HashMap(); // String "demo1_conn" -> String ""
    Map serverMap = new HashMap(); // String "demo1_conn" -> String "localhost:8080"

    Map runtimeItfMap = new HashMap(); // String "demo1_itf" -> RuntimeInterface runtimeItf
    Map paramMapMap = new HashMap(); // String "demo1_itf" -> Map paramMap

    public DomeSession() {
    }

    public void valueBound(HttpSessionBindingEvent event) {

    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println("DomeSession invalidated");
        close();
    }

    public void close() {
        Collection domeConnSet = domeConnMap.values();
        for (Iterator i = domeConnSet.iterator(); i.hasNext();) {
            DomeConnection domeConn = (DomeConnection) i.next();
            if (domeConn != null && domeConn.isConnected()) {
                System.out.println("DomeConnection is open. Close it!");
                domeConn.close();
            }
        }

        domeConnMap.clear();
        runtimeItfMap.clear();
        
        Collection paramMapSet = paramMapMap.values();
        for (Iterator i = paramMapSet.iterator(); i.hasNext();) {
            Map paramMap = (Map) i.next();
            if (paramMap != null) {
                paramMap.clear();
            }
        }

    }

    public DomeConnection getDomeConnection(String connName) {
        return (DomeConnection) domeConnMap.get(connName);
    }

    public DomeConnection getDomeConnection() {
        return (DomeConnection) domeConnMap.get(DEFAULT_CONN_NAME);
    }

    public void setDomeConnection(DomeConnection domeConn) {
        domeConnMap.put(DEFAULT_CONN_NAME, domeConn);
    }

    public void setDomeConnection(String connName, DomeConnection domeConn) {
        domeConnMap.put(connName, domeConn);
    }

    public RuntimeInterface getRuntimeInterface(String runtimeItfName) {
        return (RuntimeInterface) runtimeItfMap.get(runtimeItfName);
    }

    public void setRuntimeInterface(String runtimeItfName, RuntimeInterface runtimeItf) {
        runtimeItfMap.put(runtimeItfName, runtimeItf);
    }

    public Map getParameterMap(String runtimeItfName) {
        Map paramMap = (Map) paramMapMap.get(runtimeItfName);
        if (paramMap == null) {
            paramMap = new HashMap();
            paramMapMap.put(runtimeItfName, paramMap);
        }
        return paramMap;
    }

    public String getUserId(String connName) {
        return (String) userIdMap.get(connName);
    }

    public String getUserId() {
        return (String) userIdMap.get(DEFAULT_CONN_NAME);
    }

    public void setUserId(String connName, String userId) {
        userIdMap.put(connName, userId);
    }

    public void setUserId(String userId) {
        userIdMap.put(DEFAULT_CONN_NAME, userId);
    }

    public String getServer(String connName) {
        return (String) serverMap.get(connName);
    }

    public String getServer() {
        return (String) serverMap.get(DEFAULT_CONN_NAME);
    }

    public void setServer(String connName, String server) {
        serverMap.put(connName, server);
    }

    public void setServer(String server) {
        serverMap.put(DEFAULT_CONN_NAME, server);
    }

    public String getPassword(String connName) {
        return (String) passwordMap.get(connName);
    }

    public String getPassword() {
        return (String) passwordMap.get(DEFAULT_CONN_NAME);
    }

    public void setPassword(String connName, String password) {
        passwordMap.put(connName, password);
    }

    public void setPassword(String password) {
        passwordMap.put(DEFAULT_CONN_NAME, password);
    }

    public boolean isConnected() {
        if (getDomeConnection() != null && getDomeConnection().isConnected()) {
            return true;
        }
        return false;
    }

    public boolean isConnected(String connName) {
        if (getDomeConnection(connName) != null && getDomeConnection(connName).isConnected()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        List ret = new ArrayList();
        StringTokenizer st = new StringTokenizer("MD1111|MD2222|PJ1111", "|", false);
        while (st.hasMoreElements()) {
            ret.add(st.nextToken());
        }
        System.out.println(ret);

        System.out.println(toString(ret, "?"));

        DomeConnection conn = new DomeConnection("tutorialUser", "123", "localhost:8080");
        Map interfaceMap = new HashMap();
        String treeScript = getTreeScript(conn.getServerPublicFolder(), conn.getServerPrivateFolder(), "sv", "MD1111|MD2222|PJ1111", null, interfaceMap);
        System.out.println(interfaceMap);
        System.out.println(treeScript);
        conn.close();
    }

    /**
     * returns String representing a javascript showing the tree structures of public folder and private folder
     * Warning! interfaceMap is not an input argument. it is an output argument. we provide an empty map and the map is populated during invocation.
     * @param publicFolder
     * @param privateFolder
     * @param folderType
     * @param openedModelStr
     * @param togglingModel
     * @param interfaceMap [output field] An empty map instance should be given to this argument. The empty map is populated so that it would contain all interfaceId -> DomeInterface mapping that show up in tree script.
     * @return
     */
    public static String getTreeScript(DomeFolder publicFolder, DomeFolder privateFolder, String folderType, String openedModelStr, String togglingModel, Map interfaceMap) {
        /* update openedModelStr with togglingModel start */
        List openedModels = DomeSession.toList(openedModelStr, "|");
        if (togglingModel == null) {
            /* does nothing. keep current openedModelStr */
        } else if (openedModels.contains(togglingModel)) {
            openedModels.remove(togglingModel);
            openedModelStr = DomeSession.toString(openedModels, "|");
        } else {
            openedModels.add(togglingModel);
            openedModelStr = DomeSession.toString(openedModels, "|");
        }
        togglingModel = "";
        /* update openedModelStr with togglingModel end */

        StringBuffer sb = new StringBuffer();
        String rootLabel = "Root";
        if ("my".equals(folderType)) {
            rootLabel = "My Root";
        } else if ("sv".equals(folderType)) {
            rootLabel = "Server Root";
        } else if (folderType.startsWith("gr")) {
			String groupName = folderType.substring(3);
            rootLabel = "Group " + groupName + " Root";
		} else if (folderType.startsWith("us")) {
			String userName = folderType.substring(3);
			rootLabel = "User " + userName + " Root";
		}

        sb.append("foldersTree = gFld('<img src=\"images/dome/folder-icon.jpg\" border=0> " + rootLabel + "', '#')\n");
        TreeScriptRenderer publicRenderer = new TreeScriptRenderer(publicFolder, 0, folderType, openedModelStr, interfaceMap);
        sb.append(publicRenderer.toTreeScript());
        TreeScriptRenderer privateRenderer = new TreeScriptRenderer(privateFolder, 0, folderType, openedModelStr, interfaceMap);
        sb.append(privateRenderer.toTreeScript());
        sb.append("initializeDocument()\n");
        return sb.toString();
    }

    public static List toList(String input, String delimiter) {
        List ret = new ArrayList();

        if (input == null) {
            return ret;
        }

        StringTokenizer st = new StringTokenizer(input, delimiter, false);
        while (st.hasMoreElements()) {
            ret.add(st.nextToken());
        }
        return ret;
    }

    public static String toString(Collection input, String delimiter) {
        StringBuffer sb = new StringBuffer();

        if (input == null) {
            return "";
        }

        for (Iterator i = input.iterator(); i.hasNext(); ) {
            sb.append(i.next().toString());
            if (i.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public String toString() {
        return "[DomeSession: " + domeConnMap.toString() + " / " + runtimeItfMap.toString() + "]";
    }
}

class TreeScriptRenderer {
    StringBuffer sb = new StringBuffer();
    DomeFolder domeFolder;
    int parentFolderId;
    String folderType;
    String openedModelStr;
    List openedModelList;
    Map interfaceMap;

    public TreeScriptRenderer(DomeFolder domeFolder, int parentFolderId, String folderType, String openedModelStr, Map interfaceMap) {
        this.domeFolder = domeFolder;
        this.parentFolderId = parentFolderId;
        this.folderType = folderType;
        this.openedModelStr = openedModelStr;
        this.openedModelList = DomeSession.toList(openedModelStr, "|");
        this.interfaceMap = interfaceMap;
    }

    public String toTreeScript() {
        /* no need to generate a script for null DomeFolder */
        if (domeFolder == null) {
            return "";
        }

        if (parentFolderId == 0) {
            sb.append("folder" + domeFolder.getFolderId() + " = insFld(foldersTree" + ", gFld('<img src=\"images/dome/folder-icon.jpg\" border=0> " + domeFolder.getFolderName() + "', '#'))\n");
        } else {
            sb.append("folder" + domeFolder.getFolderId() + " = insFld(folder" + parentFolderId + ", gFld('<img src=\"images/dome/folder-icon.jpg\" border=0> " + domeFolder.getFolderName() + "', '#'))\n");
        }

        List folders = domeFolder.getFolders();
        for (Iterator i = folders.iterator(); i.hasNext(); ) {
            TreeScriptRenderer renderer = new TreeScriptRenderer((DomeFolder) i.next(), domeFolder.getFolderId(), folderType, openedModelStr, interfaceMap);
            sb.append(renderer.toTreeScript());
        }

        List models = domeFolder.getModels();
        for (Iterator i = models.iterator(); i.hasNext(); ) {
            DomeModel model = (DomeModel) i.next();
            if (! openedModelList.contains(model.getModelId())) {
                /* This is the case when this model is not opened. 'insDoc' make this model as a leaf tree node */
                sb.append("insDoc(folder" + domeFolder.getFolderId() + ", gLnk(0, '<img src=\"images/dome/model-icon.jpg\" border=0> " + model.getModelName() + "', 'browse_page.jsp?folderType=" + folderType + "&openedModelStr=" + ((openedModelStr == null) ? "" : openedModelStr) + "&togglingModel=" + model.getModelId() + "'))\n");
            } else {
                /* This is the case when this model is opened. 'insFld' make this model as a non-leaf tree node. interfaces in this model become a leaf tree node. */
                sb.append("model" + model.getModelId().replace('-', '_') + " = insFld(folder" + domeFolder.getFolderId() + ", gFld('<img src=\"images/dome/model-icon.jpg\" border=0> " + model.getModelName() + "', 'browse_page.jsp?folderType=" + folderType + "&openedModelStr=" + ((openedModelStr == null) ? "" : openedModelStr)+ "&togglingModel=" + model.getModelId() + "'))\n");
                List interfaceList = model.getInterfaces();
                for (Iterator j = interfaceList.iterator(); j.hasNext(); ) {
                    DomeInterface ifc = (DomeInterface) j.next();
                    sb.append("insDoc(model" + model.getModelId().replace('-', '_') + ", gLnk(0, '<img src=\"images/dome/interface-icon.jpg\" border=0> " + ifc.getInterfaceName() + "', 'interface_page.jsp?folderType=" + folderType + "&openedModelStr=" + ((openedModelStr == null) ? "" : openedModelStr) + "&interfaceId=" + ifc.getInterfaceId() + "'))\n");
                    interfaceMap.put(ifc.getInterfaceId(), ifc);
                }
            }
        }

        List projects = domeFolder.getProjects();
        for (Iterator i = projects.iterator(); i.hasNext(); ) {
            DomeProject project = (DomeProject) i.next();
            //sb.append("insDoc(folder" + domeFolder.getFolderId() + ", gLnk(0, '<img src=\"images/dome/project-icon.jpg\" border=0> " + project.getProjectName() + "', 'browse_page.jsp?openedModelStr=PJ" + project.getProjectId() + "'))\n");

            if (! openedModelList.contains(project.getProjectId())) {
                /* This is the case when this model is not opened. 'insDoc' make this model as a leaf tree node */
                sb.append("insDoc(folder" + domeFolder.getFolderId() + ", gLnk(0, '<img src=\"images/dome/project-icon.jpg\" border=0> " + project.getProjectName() + "', 'browse_page.jsp?folderType=" + folderType + "&openedModelStr=" + ((openedModelStr == null) ? "" : openedModelStr) + "&togglingModel=" + project.getProjectId() + "'))\n");
            } else {
                /* This is the case when this model is opened. 'insFld' make this model as a non-leaf tree node. interfaces in this model become a leaf tree node. */
                sb.append("project" + project.getProjectId().replace('-', '_') + " = insFld(folder" + domeFolder.getFolderId() + ", gFld('<img src=\"images/dome/project-icon.jpg\" border=0> " + project.getProjectName() + "', 'browse_page.jsp?folderType=" + folderType + "&openedModelStr=" + ((openedModelStr == null) ? "" : openedModelStr)+ "&togglingModel=" + project.getProjectId() + "'))\n");
                List interfaceList = project.getInterfaces();
                for (Iterator j = interfaceList.iterator(); j.hasNext(); ) {
                    DomeInterface ifc = (DomeInterface) j.next();
                    sb.append("insDoc(project" + project.getProjectId().replace('-', '_') + ", gLnk(0, '<img src=\"images/dome/interface-icon.jpg\" border=0> " + ifc.getInterfaceName() + "', 'interface_page.jsp?folderType=" + folderType + "&openedModelStr=" + ((openedModelStr == null) ? "" : openedModelStr) + "&interfaceId=" + ifc.getInterfaceId() + "'))\n");
                    interfaceMap.put(ifc.getInterfaceId(), ifc);
                }
            }
        }

        return sb.toString();
    }
}