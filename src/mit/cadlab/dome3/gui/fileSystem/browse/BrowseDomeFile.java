package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.gui.fileSystem.Folder;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBrowse;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * Name: BrowseDomeFile
 * User: thorek
 * Date: Mar 26, 2003
 * Time: 6:00:11 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */

public class BrowseDomeFile extends DomeFile
{


	public BrowseDomeFile(String fileType, String id, String name, String desc, String url, String modified, int version)
	{
		super(fileType, id, name, desc, url, modified, version);
	}

	public BrowseDomeFile(String fileType, String id, String name, String desc, String modified, int version)
	{
		super(fileType, id, name, desc, modified, version);
	}

	public BrowseDomeFile(ProjectResourceInfo pri, ServerConnection svrConn)
	{
		super(pri.getResourceUniqueId(), pri.getName(), svrConn.getServerPort(), pri);
		if (pri.getType().equals(ProjectResourceInfo.MODEL_RESOURCE))
			this.fileType = MODEL_TYPE;
		else
			this.fileType = PROJECT_TYPE;

	}


	public void listChildren(ServerConnection svrConn)
	{
        if (this.lookAtChildren)
        {
            if (pri == null)
            {
                if (this.fileType.equalsIgnoreCase("model"))
                {
                    addAvailableInterfaces(svrConn);
                }
                else if(fileType.equalsIgnoreCase(DomeFile.ANALYSIS_TOOL_TYPE))
                {
                    addAnalysisToolAvailableContent(svrConn);
                }
                else if (this.fileType.equalsIgnoreCase("project"))
                {
                    addProjectAvailableContent(svrConn);
                }
            }
            else
            {
                if (this.fileType.equalsIgnoreCase("model"))
                {
                    addResourceModelInterfaces(svrConn);
                }
                else if (this.fileType.equalsIgnoreCase("project"))
                {
                    addResourceProjectContent(svrConn);
                }
            }
        }
        this.lookAtChildren = false;
	}


	public String getUrl()
	{
		return url;
	}


	private void addAvailableInterfaces(ServerConnection svrConn)
	{
		String dbId, name, description, lastModified;
		Integer version;
		Vector anInterface;
		ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, url);
		Vector v = FileSystemFunctions.getAvailableInterfacesInfo(newSvrConn, this.getId().toString(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
		//add to the file
		for (int i = 0; i < v.size(); i++) {
			anInterface = (Vector) v.get(i);
			name = (String) anInterface.get(0);
			dbId = (String) anInterface.get(1);
			description = (String) anInterface.get(2);
			version = (Integer) anInterface.get(3);
			lastModified = anInterface.get(4).toString();
			this.content.add(new BrowseInterfaceDomeFile(dbId, name, description, url, lastModified, version.intValue()));
		}
	}

    private void addProjectInsideAnalysisTool(ServerConnection svrConn)
    {
        String dbId, name, description, lastModified;
        Integer version;

        ServerConnection newServerConnection = LoginUtils.compareServersAndGetConnection(svrConn, url);
        Vector project = FileSystemFunctions.getProjectInsideAnalysisTool(newServerConnection, (String) getId());
        if (!project.isEmpty())
        {
            name = (String) project.get(1);
            dbId = (String) project.get(0);
            description = (String) project.get(2);
            version = (Integer) project.get(3);
            lastModified = project.get(4).toString();
            content.add(new BrowseDomeFile(DomeFile.PROJECT_TYPE, dbId, name, description, svrConn.getServerPort(), lastModified, version.intValue()));
        }
    }

	private void addProjectAvailableContent(ServerConnection svrConn)
	{

		DArrayList cc = this.content;


		String dbId, name, description, lastModified;
		Integer version;
		Vector anInterface, aIModelWithInterface, anIModel;
		ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, url);

		Vector v = FileSystemFunctions.getAvailableInterfacesInfo(newSvrConn, this.getId().toString(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
		Vector v2 = FileSystemFunctions.getAvailableProjectContents(newSvrConn, this.getId().toString(), DbConstants.FILESYSTEM_BROWSE);

		BrowseModelFolder iFolder, imFolder, rFolder;
		if (!v2.isEmpty()) {

			iFolder = new BrowseModelFolder(Folder.PROJECT_CONTENT_FOLDER, new Integer(0), "Interfaces");
			this.content.add(iFolder);
			imFolder = new BrowseModelFolder(Folder.PROJECT_CONTENT_FOLDER, new Integer(0), "iModels");
			this.content.add(imFolder);
			cc = imFolder.getContent();
			Vector iModels = (Vector) v2.get(1);
			if (!iModels.isEmpty()) {


				for (int i = 0; i < iModels.size(); i++) {
					aIModelWithInterface = (Vector) iModels.get(i);
					anIModel = (Vector) aIModelWithInterface.get(0);
					name = (String) anIModel.get(0);
					dbId = (String) anIModel.get(1);
					description = (String) anIModel.get(2);
					version = (Integer) anIModel.get(3);
					lastModified = anIModel.get(4).toString();
					BrowseDomeFile df = new BrowseDomeFile(MODEL_TYPE, dbId, name, description, newSvrConn.getServerPort(), lastModified, version.intValue());
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
			rFolder = new BrowseModelFolder(Folder.PROJECT_CONTENT_FOLDER, new Integer(0), "Resources");
			this.content.add(rFolder);
			cc = rFolder.getContent();

			String projectXml = (String) v2.get(0);
			IntegrationProjectBrowse ip = new IntegrationProjectBrowse(XMLUtils.stringToXmlElement(projectXml));
			List resource = ip.getResourceModels();
			for (int i = 0; i < resource.size(); i++) {
				ProjectResourceInfo pri = (ProjectResourceInfo) resource.get(i);
				if (pri.getSubscribedInterfaceIds().isEmpty()) // skip unused resources
					continue;
                Vector a = FileSystemFunctions.getAvailableInterfacesInfo(newSvrConn, pri.getResourceDeployId(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
				if (!a.isEmpty())
                    cc.add(new BrowseDomeFile(pri, newSvrConn));          // added this if statement so that users who have no permissions to view
                                                                          // resource interfaces, cannot see the resource as well.
			}

			cc = iFolder.getContent();
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

        String dbId, name, description, lastModified;
        Integer version;
        Vector anInterface;
        ServerConnection newSvrConn = LoginUtils.compareServersAndGetConnection(svrConn, url);
        Vector v = FileSystemFunctions.getAvailableInterfacesInfo(newSvrConn, this.getId().toString(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
        Vector project = FileSystemFunctions.getProjectInsideAnalysisTool(newSvrConn, (String) getId());

		BrowseModelFolder iFolder, pFolder;
		if (!v.isEmpty())
        {
            iFolder = new BrowseModelFolder(Folder.ANALYSIS_TOOL_CONTENT_FOLDER, new Integer(0), "Interfaces");
            this.content.add(iFolder);
            cc = iFolder.getContent();

            for (int i = 0; i < v.size(); i++)
            {
                anInterface = (Vector) v.get(i);
                name = (String) anInterface.get(0);
                dbId = (String) anInterface.get(1);
                description = (String) anInterface.get(2);
                version = (Integer) anInterface.get(3);
                lastModified = anInterface.get(4).toString();
                cc.add(new BrowseInterfaceDomeFile(dbId, name, description, url, lastModified, version.intValue()));
            }
        }

        if (!project.isEmpty())
        {
            pFolder = new BrowseModelFolder(Folder.ANALYSIS_TOOL_CONTENT_FOLDER, new Integer(0), "Project");
			this.content.add(pFolder);
            cc = pFolder.getContent();

            name = (String) project.get(1);
            dbId = (String) project.get(0);
            description = (String) project.get(2);
            version = (Integer) project.get(3);
            lastModified = project.get(4).toString();
            cc.add(new BrowseDomeFile(DomeFile.PROJECT_TYPE, dbId, name, description, svrConn.getServerPort(), lastModified, version.intValue()));
        }
	}

	private void addResourceModelInterfaces(ServerConnection svrConn)
	{
		ServerConnection con = LoginUtils.compareServersAndGetConnection(svrConn, pri.getResourceHostName() + ":" + pri.getResourcePort());
		Vector v = FileSystemFunctions.getModelInfo(con, pri.getResourceDeployId());

		if (!v.isEmpty()) {
			//set information except id
			this.name = (String) v.get(1);
			this.description = (String) v.get(2);
			this.version = ((Integer) v.get(3)).intValue();
			this.modified = (v.get(4)).toString();

			String dbId, name, description, lastModified;
			Integer version;
			Vector anInterface;
			v = FileSystemFunctions.getAvailableInterfacesInfo(con, pri.getResourceDeployId(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
			//add to the file
			for (int i = 0; i < v.size(); i++) {
				anInterface = (Vector) v.get(i);
				name = (String) anInterface.get(0);
				dbId = (String) anInterface.get(1);
				description = (String) anInterface.get(2);
				version = (Integer) anInterface.get(3);
				lastModified = anInterface.get(4).toString();
				this.content.add(new BrowseInterfaceDomeFile(dbId, name, description, con.getServerPort(), lastModified, version.intValue(), pri));
			}
		}
	}


	private void addResourceProjectContent(ServerConnection svrConn)
	{

		ServerConnection con = LoginUtils.compareServersAndGetConnection(svrConn, pri.getResourceHostName() + ":" + pri.getResourcePort());
		Vector v = FileSystemFunctions.getProjectInfo(con, pri.getResourceDeployId());
		//set information except id
		this.name = (String) v.get(1);
		this.description = (String) v.get(2);
		this.version = ((Integer) v.get(3)).intValue();
		this.modified = v.get(4).toString();   //Stringified Date

		String dbId, name, description, lastModified;
		Integer version;
		Vector anInterface, aIModelWithInterface, anIModel;

		v = FileSystemFunctions.getAvailableInterfacesInfo(con, pri.getResourceDeployId(), PermissionUtils.PERMISSION_TO_VIEW_INTERFACE);
		Vector v2 = FileSystemFunctions.getAvailableProjectContents(con, pri.getResourceDeployId(), DbConstants.FILESYSTEM_BROWSE);

		BrowseModelFolder iFolder = new BrowseModelFolder("", new Integer(0), "Interfaces");
		this.content.add(iFolder);

		BrowseModelFolder imFolder = new BrowseModelFolder("", new Integer(0), "iModels");
		this.content.add(imFolder);

		BrowseModelFolder rFolder = new BrowseModelFolder("", new Integer(0), "Resources");
		this.content.add(rFolder);

		if (!v2.isEmpty()) {
			Vector iModels = (Vector) v2.get(1);
			if (!iModels.isEmpty()) {
				for (int i = 0; i < iModels.size(); i++) {
					aIModelWithInterface = (Vector) iModels.get(i);
					anIModel = (Vector) aIModelWithInterface.get(0);
					name = (String) anIModel.get(0);
					dbId = (String) anIModel.get(1);
					description = (String) anIModel.get(2);
					version = (Integer) anIModel.get(3);
					lastModified = anIModel.get(4).toString();
					BrowseDomeFile df = new BrowseDomeFile(MODEL_TYPE, dbId, name, description, con.getServerPort(), lastModified, version.intValue());
					imFolder.getContent().add(df);

					Vector allInterfaces = (Vector)aIModelWithInterface.get(1);
					for(int j = 0; j < allInterfaces.size(); j++) {
						anInterface = (Vector) allInterfaces.get(j);
						name = (String) anInterface.get(0);
						dbId = (String) anInterface.get(1);
						description = (String) anInterface.get(2);
						version = (Integer) anInterface.get(3);
						lastModified = anInterface.get(4).toString();
						df.getContent().add(new BrowseInterfaceDomeFile(dbId, name, description, url, lastModified, version.intValue()));
					}
				}
			}

			String projectXml = (String) v2.get(0);
			IntegrationProjectBrowse ip = new IntegrationProjectBrowse(XMLUtils.stringToXmlElement(projectXml));
			List resource = ip.getResourceModels();
			for (int i = 0; i < resource.size(); i++) {
				ProjectResourceInfo pri = (ProjectResourceInfo) resource.get(i);
				if (pri.getSubscribedInterfaceIds().isEmpty()) // skip unused resources
					continue;
				rFolder.getContent().add(new BrowseDomeFile(pri, con));
			}
		}

		//add project interfaces
		for (int i = 0; i < v.size(); i++) {
			anInterface = (Vector) v.get(i);
			name = (String) anInterface.get(0);
			dbId = (String) anInterface.get(1);
			description = (String) anInterface.get(2);
			version = (Integer) anInterface.get(3);
			lastModified = anInterface.get(4).toString();
			iFolder.getContent().add(new BrowseInterfaceDomeFile(dbId, name, description, con.getServerPort(), lastModified, version.intValue()));
		}


	}

	public void relocate(String fileType, String id, String name, String desc, String url, String modified, int version)
	{
		this.id = id;
		this.name = name;
		this.fileType = fileType;
		this.description = desc;
		this.modified = modified;
		this.version = version;
		this.url = url;
	}
}