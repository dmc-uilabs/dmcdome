package mit.cadlab.dome3.network.client.objectrecord;

import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBrowse;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectClientRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfoRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfoRuntime;
import mit.cadlab.dome3.util.xml.XMLUtils;

import javax.swing.JFrame;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.awt.Dimension;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Mar 31, 2003
 * Time: 7:22:16 PM
 * To change this template use Options | File Templates.
 */
public class ClientProjectRecord extends ClientObjectRecord
{
	private ClientPlayspaceRuntime playspaceRef = null;
	private boolean contentAdded;
	private IntegrationProjectClientRuntime project;
	private ClientAnalysisToolRecord parentTool = null; // if project in tool
    private ServerConnection projectConn;
	private Object contentLoadingLock = new Object();

    // for build
    public ClientProjectRecord(CompoundId id, String name, String description,
	                           String url)
	{
		super(id, name, description, url);
	}

    // for run
    public ClientProjectRecord(CompoundId id, String name, String description,
                               String url, IntegrationProjectClientRuntime project,
                               ClientPlayspaceRuntime playspace)
    {
        super(id, name, description, url);
        this.project = project;
        this.playspaceRef = playspace;
    }


    // for run
    public ClientProjectRecord(CompoundId id, String name, String description,
                               String url,ClientPlayspaceRuntime playspace)
    {
        super(id, name, description, url);
        this.playspaceRef = playspace;
    }

    // for project in optimization in run mode
	public ClientProjectRecord(CompoundId id, String name, String description,
	                           String url, ClientPlayspaceRuntime playspace, ClientAnalysisToolRecord parentTool)
	{
		this(id, name, description, url, null, playspace);
		this.parentTool = parentTool;
	}

	public IntegrationProjectClientRuntime getProject()
	{
		return project;
	}

	public ClientPlayspaceRuntime getPlayspace()
	{
		return playspaceRef;
	}


	public String getStaticId ()
	{
		return compoundId.getCurrentProjectStaticId();
	}

	public void listChildren()
	{
		synchronized(contentLoadingLock) { // don't try this again while stuff is loading!
			if (contentAdded)
				return;
		}
		if (!contentAdded) {
			projectConn = LoginUtils.compareServersAndGetConnection(playspaceRef.getServerConnection(), url);
			if (projectConn == null)
				return;
			JFrame waitWin = StatusWindow.show(StatusWindow.STARTING,
			                                   getName(), RunMode.getWindowLocation());
			OpenWorker worker = new OpenWorker(waitWin);
			worker.start();
		}
	}

	private void addProjectAvailableContent()
	{
		Vector aIModelWithInterface, anIModel;

        // todo: getProject XML first!
		if (project == null) {
			if (parentTool != null) {
				CompoundId toolId = parentTool.compoundId;
				Vector projInfo = RuntimeFunctionsClient.getToolProjectInfo(projectConn, toolId.toString());
				if (projInfo.isEmpty()) {
					System.err.println("addProjectAvailableContent - unable to load project for parent tool: " +
					                   parentTool.getName());
					return;
				}
				this.project = new IntegrationProjectClientRuntime(this.compoundId, projectConn,
				                                                   XMLUtils.stringToXmlElement((String)projInfo.get(0)),
				                                                   (Hashtable)projInfo.get(1),
				                                                   this.playspaceRef);
			} else { //parentTool==null,then check if it's a normal project
                if(playspaceRef!=null)  {
	                project=playspaceRef.getProject(compoundId);
	                if (project == null)
		                project = playspaceRef.createProject(compoundId, projectConn);
                }
              else
				 System.err.println("addProjectAvailableContent - unable to load project "+
				                   getName());
			}

		}

		Vector v = FileSystemFunctions.getAvailableInterfacesInfo(projectConn,
		                                                          compoundId.getCurrentProjectStaticId(),
		                                                          PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
		Vector v2 = FileSystemFunctions.getAvailableProjectContents(projectConn,
		                                                            compoundId.getCurrentProjectStaticId(),
		                                                            DbConstants.FILESYSTEM_BROWSE);

		BrowseModelFolder iFolder, imFolder, rFolder;

		if(v != null && !v.isEmpty())  {
			iFolder = new BrowseModelFolder(Folder.PROJECT_CONTENT_FOLDER, new Integer(0), "Interfaces");
			for(int i = 0; i < v.size(); i++) {
				Vector projectIfaceVector = (Vector) v.get(i);
				String ifaceName = (String) projectIfaceVector.get(0);
				String ifaceid = (String) projectIfaceVector.get(1);
				String ifaceDesc = (String) projectIfaceVector.get(2);
				CompoundId cid =  new CompoundId(compoundId);
				cid.setInterfaceStaticId(ifaceid);
				ClientInterfaceRecord iface = new ClientInterfaceRecord(projectConn, cid, ifaceName,
				                                                        ifaceDesc, url, playspaceRef);
				iFolder.getContent().add(iface);
			}
			this.content.add(iFolder);
		}
		if (!v2.isEmpty()) {
			Vector iModels = (Vector) v2.get(1);
			if (!iModels.isEmpty()) {
				imFolder = new BrowseModelFolder(Folder.PROJECT_CONTENT_FOLDER, new Integer(0), "iModels");
				this.content.add(imFolder);
				for (int i = 0; i < iModels.size(); i++) {
					aIModelWithInterface = (Vector) iModels.get(i);  //browse representation
  					anIModel = (Vector) aIModelWithInterface.get(0);
					String name = (String) anIModel.get(0);
					String dbId = (String) anIModel.get(1); // deploy id
					String description = (String) anIModel.get(2);
					CompoundId modelId = new CompoundId (project.getResourceRuntimeId(dbId));

					ClientModelRecord cmr = new ClientModelRecord(modelId, name, description, url, playspaceRef);
					ProjectIntegrationModelInfo iModelInfo = project.getIntegrationModel(dbId);
					if(iModelInfo instanceof ProjectIntegrationModelInfoRuntime) {
						((ProjectIntegrationModelInfoRuntime)iModelInfo).setObject(cmr.getModel());
					}
					imFolder.addContent(cmr);
				}
			}

			String projectXml = (String) v2.get(0);
			IntegrationProjectBrowse ip = new IntegrationProjectBrowse(XMLUtils.stringToXmlElement(projectXml));
			List resources = ip.getResourceModels();
			if (!resources.isEmpty()) {
				rFolder = new BrowseModelFolder(Folder.PROJECT_CONTENT_FOLDER, new Integer(0), "Resources");
				this.content.add(rFolder);
				for (int i = 0; i < resources.size(); i++) {
					ProjectResourceInfo priBrowse = (ProjectResourceInfo) resources.get(i);  //browse representation
					if (priBrowse.getSubscribedInterfaceIds().isEmpty()) // skip unused resources
						continue;
					ProjectResourceInfo pri = project.getResource(priBrowse.getResourceUniqueId());  //actual resource in the project
					{
						if (pri.getType().equals(ProjectResourceInfo.MODEL_RESOURCE)) {
							CompoundId modelId = new CompoundId (project.getResourceRuntimeId(pri.getResourceUniqueId()));
							ClientModelRecord mod = new ClientModelRecord(modelId,
							                      pri.getName(),
							                      pri.getResourceDescription(),
							                      pri.getResourceHostName() + ":" + pri.getResourcePort(),
							                      playspaceRef);
                           if(pri instanceof ProjectResourceInfoRuntime) {
	                           ((ProjectResourceInfoRuntime)pri).setObject(mod.getModel());
                           }
							rFolder.addContent(mod);
						}
						else {  //PROJECT_RESOURCE   //TODO test this part
							CompoundId newProjectId = new CompoundId (project.getResourceRuntimeId(pri.getResourceUniqueId()));
							IntegrationProjectClientRuntime subproject = project.getProject(newProjectId,
							                                                                playspaceRef.getServerConnection());
							newProjectId.getNextProjectRuntimeId(); //to advance the current id pointer
							ClientProjectRecord proj = new ClientProjectRecord(newProjectId,
							                        pri.getName(),
							                        pri.getResourceDescription(),
							                        pri.getResourceHostName() + ":" + pri.getResourcePort(),
							                        subproject,
							                        playspaceRef);
							if (pri instanceof ProjectResourceInfoRuntime) {
								((ProjectResourceInfoRuntime) pri).setObject(subproject);
							}
							rFolder.addContent(proj);
						}
					}
				}
			}
		} 
	}

	class OpenWorker extends SwingWorker
	{
		JFrame waitWin;

		public OpenWorker(JFrame waitWin)
		{
			this.waitWin = waitWin;
		}

		public Object construct()
		{
			try {
				synchronized (contentLoadingLock) {
					addProjectAvailableContent();
					contentAdded = true;
				}
			}
			catch (Exception e) {
				OneButton1Msg.showError(null, "Error starting " + name,
				                        e.getMessage(),
				                        "ok", new Dimension(200, 1));
			}
			return new Object();
		}

		public void finished()
		{
			waitWin.setVisible(false);
			waitWin.dispose();
		}

	}

}
