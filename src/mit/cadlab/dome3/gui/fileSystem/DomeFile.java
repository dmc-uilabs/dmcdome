// File.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.gui.fileSystem.browse.BrowseDomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseInterfaceDomeFile;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceBuild;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class DomeFile extends FileSystemObject
{
	public static final String MODEL_TYPE = "Model";
	public static final String PLAYSPACE_TYPE = "Playspace";
	public static final String INTERFACE_TYPE = "Interface";
	public static final String PROJECT_TYPE = "Project";
    public static final String ANALYSIS_TOOL_TYPE = "AnalysisTool";

   	protected String fileType;
	protected String description;
	protected String modified;
	protected int version;
	protected String url = null;
	protected ProjectResourceInfo pri = null;


	public DomeFile(String fileType, String id, String name, String desc, String url, String modified, int version)
	{
		super(id, name);
		this.fileType = fileType;
		this.description = desc;
		this.modified = modified;
		this.version = version;
		this.url = url;
	}


	public DomeFile(String fileType, String id, String name, String desc, String modified, int version)
	{
		super(id, name);
		this.fileType = fileType;
		this.description = desc;
		this.modified = modified;
		this.version = version;
	}

	public DomeFile(String Id, String name, String url, ProjectResourceInfo pri)
	{
		this.id = pri.getResourceUniqueId();
		this.name = pri.getName();
		this.url = url;
		this.pri = pri;
	}


	public DomeFile(String fileType, String id, String name)
	{
		super(id, name);
		this.fileType = fileType;
	}

	public String getFileType()
	{
		return fileType;
	}

	public String getDescription()
	{
		return description;
	}

	public String getModified()
	{
		return modified;
	}

	public double getVersion()
	{
		return version;
	}

	public DArrayList getContent()
	{
		return content;
	}

	public String getStatus()
	{
		return "toBeSet";
	}

	public String getUrl ()
	{
		return url;
	}

	public void addContent(Collection objs)
	{
		content.addAll(objs);
	}

    public void listChildren(ServerConnection svrConn)
    {
        if (this.lookAtChildren)
        {
            if (this.fileType.equalsIgnoreCase("model"))
            {
                addAvailableInterfaces(svrConn);
            }
            else if (this.fileType.equalsIgnoreCase("project"))
            {
                addProjectAvailableContent(svrConn);
            }
            else if (this.fileType.equalsIgnoreCase("playspace"))
            {
                addPlayspaceDescription(svrConn);
            }
            else if (this.fileType.equalsIgnoreCase(DomeFile.ANALYSIS_TOOL_TYPE))
            {
                addAnalysisToolAvailableContent(svrConn);
            }
        }
        this.lookAtChildren = false;
    }

	private void addAvailableInterfaces(ServerConnection svrConn)
	{


		String dbId, name, description, url, lastModified;
		Integer version;
		Vector anInterface;
		Vector v = FileSystemFunctions.getAvailableInterfacesInfo(svrConn, this.getId().toString(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
		//add to the file
		for (int i = 0; i < v.size(); i++) {
			anInterface = (Vector) v.get(i);
			name = (String) anInterface.get(0);
			dbId = (String) anInterface.get(1);
			description = (String) anInterface.get(2);
			version = (Integer) anInterface.get(3);
			lastModified = anInterface.get(4).toString();
			this.content.add(new BrowseInterfaceDomeFile(dbId, name, description, svrConn.getServerPort(), lastModified, version.intValue()));
		}
	}


	private void addPlayspaceDescription(ServerConnection svrConn)
	{
		String description = FileSystemFunctions.getPlayspaceDescription(svrConn, this.getId().toString());
		//DomeXmlData dxd = new DomeXmlData()
		Element XMLDescription = XMLUtils.stringToXmlElement(description);

		ClientPlayspaceBuild ps = new ClientPlayspaceBuild(svrConn, XMLDescription);
		Collection models = ps.getModelRecords();
		Iterator it = models.iterator();
		Vector mVec = new Vector(0);
		while (it.hasNext()) {
			// get the next model record
			ClientModelRecord modelRecord = (ClientModelRecord) it.next();
			// get the server connection for this model
			ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, modelRecord.getUrl());

			// means failed to connect or used clicked cancel in Login dialog
			//then don't proceed so that we can process the remaining models
			if (newSvrConn == null) {
				String msg = "Could not connect to " + modelRecord.getUrl();
				OneButton1Msg.showWarning(null, "Connection failed", msg, "ok", new Dimension(1, 1));
			} else {
				// get the model's last modified time and version number
				mVec = FileSystemFunctions.getModelLastModified(newSvrConn, modelRecord.getStaticId());
			}
			if (mVec.size() > 0) {
				String lastModified = mVec.get(0).toString();
				int version = ((Integer) mVec.get(1)).intValue();

				// create a file object for the model and add it to the content

				BrowseDomeFile modelFile;
				modelFile = new BrowseDomeFile(MODEL_TYPE, modelRecord.getStaticId(),
				                               modelRecord.getName(), modelRecord.getDescription(),
				                               modelRecord.getUrl(), lastModified, version);
				this.content.add(modelFile);
			}
		}

		Collection projects = ps.getProjectRecords();
		Iterator it2 = projects.iterator();
		Vector mVec2 = new Vector(0);
		while (it2.hasNext()) {
			// get the next project record
			ClientProjectRecord projectRecord = (ClientProjectRecord) it2.next();
			// get the server connection for this model
			ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, projectRecord.getUrl());
			// means failed to connect or used clicked cancel in Login dialog
			//then don't proceed so that we can process the remaining projects
			if (newSvrConn == null) {
				String msg = "Could not connect to " + projectRecord.getUrl();
				OneButton1Msg.showWarning(null, "Connection failed", msg, "ok", new Dimension(1, 1));
			} else {
				// get the project's last modified time and version number
				mVec2 = FileSystemFunctions.getProjectLastModified(newSvrConn,
				                                                   projectRecord.getStaticId());
			}
			if (mVec2.size() > 0) {
				String lastModified = mVec2.get(0).toString();
				int version = ((Integer) mVec2.get(1)).intValue();
				// create a file object for the project and add it to the content
				BrowseDomeFile projectFile;
				projectFile = new BrowseDomeFile(PROJECT_TYPE, projectRecord.getStaticId(),
				                                 projectRecord.getName(), projectRecord.getDescription(),
				                                 projectRecord.getUrl(), lastModified, version);
				this.content.add(projectFile);
			}
		}

        Collection tools = ps.getToolRecords();
		Iterator it3 = tools.iterator();
		Vector mVec3 = new Vector(0);
		while (it3.hasNext()) {
			// get the next project record
			ClientAnalysisToolRecord toolRecord = (ClientAnalysisToolRecord) it3.next();
			// get the server connection for this model
			ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, toolRecord.getUrl());
			// means failed to connect or used clicked cancel in Login dialog
			//then don't proceed so that we can process the remaining projects
			if (newSvrConn == null) {
				String msg = "Could not connect to " + toolRecord.getUrl();
				OneButton1Msg.showWarning(null, "Connection failed", msg, "ok", new Dimension(1, 1));
			} else {
				// get the project's last modified time and version number
				mVec3 = FileSystemFunctions.getAnalysisToolLastModified(newSvrConn,toolRecord.getStaticId());
			}
			if (mVec3.size() > 0) {
				String lastModified = mVec3.get(0).toString();
				int version = ((Integer) mVec3.get(1)).intValue();
				// create a file object for the project and add it to the content
				BrowseDomeFile projectFile;
				projectFile = new BrowseDomeFile(ANALYSIS_TOOL_TYPE, toolRecord.getStaticId(),
				                                 toolRecord.getName(), toolRecord.getDescription(),
				                                 toolRecord.getUrl(), lastModified, version);
				this.content.add(projectFile);
			}
		}
	}


	private void addProjectAvailableContent(ServerConnection svrConn)
	{

		DArrayList cc = this.content;


		String dbId, name, description, lastModified;
		Integer version;
		Vector anInterface, aIModelWithInterface, anIModel, allInterfaces;
		ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, url);

		Vector v = FileSystemFunctions.getAvailableInterfacesInfo(newSvrConn, this.getId().toString(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
		Vector v2 = FileSystemFunctions.getAvailableProjectContents(svrConn, this.getId().toString(), DbConstants.FILESYSTEM_BROWSE);

		BrowseModelFolder iFolder, imFolder, rFolder;
		if (!v2.isEmpty()) {

			iFolder = new BrowseModelFolder("", new Integer(0), "Interfaces");
			this.content.add(iFolder);

			Vector iModels = (Vector) v2.get(1);
			if (!iModels.isEmpty()) {
				imFolder = new BrowseModelFolder("", new Integer(0), "iModels");
				this.content.add(imFolder);
				cc = imFolder.content;

				for (int i = 0; i < iModels.size(); i++) {
					aIModelWithInterface = (Vector) iModels.get(i);
					anIModel = (Vector) aIModelWithInterface.get(0);
					name = (String) anIModel.get(0);
					dbId = (String) anIModel.get(1);
					description = (String) anIModel.get(2);
					version = (Integer) anIModel.get(3);
					lastModified = anIModel.get(4).toString();
					DomeFile df = new DomeFile(MODEL_TYPE, dbId, name, description, lastModified, version.intValue());
					cc.add(df);

					/*allInterfaces = (Vector)aIModelWithInterface.get(1);
					//anInterface = (Vector) v.get(i);
					name = (String) anInterface.get(0);
					dbId = (String) anInterface.get(1);
					description = (String) anInterface.get(2);
					version = (Integer) anInterface.get(3);
					lastModified = anInterface.get(4).toString();
					cc.add(new BrowseInterfaceDomeFile(dbId, name, description, url, lastModified, version.intValue()));*/


				}

			}


			cc = iFolder.content;
		}

		//add to the file
		for (int i = 0; i < v.size(); i++) {
			anInterface = (Vector) v.get(i);
			name = (String) anInterface.get(0);
			dbId = (String) anInterface.get(1);
			description = (String) anInterface.get(2);
			version = (Integer) anInterface.get(3);
			lastModified = anInterface.get(4).toString();
			cc.add(new BrowseInterfaceDomeFile(dbId, name, description, url, lastModified, version.intValue()));
		}
	}

    private void addAnalysisToolAvailableContent(ServerConnection svrConn)
    {

        DArrayList cc = this.content;

        Vector v1 = FileSystemFunctions.getAvailableInterfacesInfo(svrConn, getId().toString(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
        Vector v2 = FileSystemFunctions.getProjectInsideAnalysisTool(svrConn, getId().toString());

        Vector analysisToolInterface = null;
        String dbId, name, description, url, lastModified,
                idProject, projectName, projectDescription,
                lastModifiedProject = null;
        Integer version = null;
        int projectVersion;
        Vector projectModifiedVector = null;

        BrowseModelFolder iFolder, pFolder;
        if (!v1.isEmpty())
        {
            iFolder = new BrowseModelFolder(Folder.ANALYSIS_TOOL_CONTENT_FOLDER, new Integer(0), "Interfaces");
            this.content.add(iFolder);
            cc = iFolder.getContent();


            for (int i = 0; i < v1.size(); i++)
            {
                analysisToolInterface = (Vector) v1.get(i);
                name = (String) analysisToolInterface.get(0);
                dbId = (String) analysisToolInterface.get(1);
                description = (String) analysisToolInterface.get(2);
                version = (Integer) analysisToolInterface.get(3);
                lastModified = analysisToolInterface.get(4).toString();
                cc.add(new BrowseInterfaceDomeFile(dbId, name, description, svrConn.getServerPort(), lastModified, version.intValue()));
            }
        }

        if (!v2.isEmpty())
        {
            pFolder = new BrowseModelFolder(Folder.ANALYSIS_TOOL_CONTENT_FOLDER, new Integer(0), "Project");
            this.content.add(pFolder);
            cc = pFolder.getContent();

            idProject = (String) v2.get(0);
            projectName = (String) v2.get(1);
            projectDescription = (String) v2.get(2);
            projectModifiedVector = FileSystemFunctions.getProjectLastModified(svrConn, idProject);
            lastModifiedProject = projectModifiedVector.get(0).toString();
            projectVersion = ((Integer) projectModifiedVector.get(1)).intValue();
            cc.add(new DomeFile(DomeFile.PROJECT_TYPE, idProject, projectName, projectDescription,
                    svrConn.getServerPort(), lastModifiedProject, projectVersion));
        }
    }

	public String getType()
	{
		return fileType;
	}


}
