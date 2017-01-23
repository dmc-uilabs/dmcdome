// SimpleModelDeploy.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.deploy;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.gui.deploy.components.DeployProjectData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;

import java.util.Vector;

import org.dom4j.Element;
import com.touchgraph.graphlayout.GLPanel;

public class SimpleProjectDeploy {

	public static void deployProject() {
		DomeFileChooser fc = new DomeFileChooser();
		String localFileName = fc.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);
		if (localFileName == null)
			return; // cancelled
		DeployProjectData project = new DeployProjectData(localFileName);
		if (project == null) {
			System.err.println("unable to load project at " + localFileName);
			return;
		}
		ServerConnection svrConn = LoginPrompt.showDialog(null);
		if (svrConn==null)
			return;
		// note: need to set permissions for project, project interfaces, imodels and imodel interfaces
		// in their corresponding data structures via setPermission before the next step.
		// Default is empty permissions vector.
		Vector[] preparedData = DeployUtilities.prepareDeployProjectData(new Integer(1),project);
		try {
			Vector versionInfo = DeployFilesFunctions.deployNewProject(svrConn, preparedData[0], preparedData[1], preparedData[2]);
			DeployUtilities.writeProjectVersionFile(localFileName, versionInfo, svrConn);
		}
		catch (Exception e) {
			OneButton1Msg.showError(null,"Deploy project error",e.getMessage(),"ok",OneButton1Msg.DEFAULT_SIZE);
		}
	}

	public static void redeployProject() {
		DomeFileChooser fc = new DomeFileChooser();
		String localFileName = fc.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);
		if (localFileName == null)
			return; // cancelled
		DeployProjectData project = new DeployProjectData(localFileName);
		if (project == null) {
			System.err.println("unable to load project at " + localFileName);
			return;
		}
		ServerConnection svrConn = LoginPrompt.showDialog(null);
		if (svrConn==null)
			return;
        String projectRedeployId = DeployUtilities.getValidRedeployProjectId(null, svrConn, project, localFileName, null);
		// note: need to set permissions for project, project interfaces, imodels and imodel interfaces
		// in their corresponding data structures via setPermission before the next step.
		// Default is empty permissions vector.
		Vector[] preparedData = DeployUtilities.prepareRedeployProjectData(projectRedeployId, project);
		try {
			Vector versionInfo = DeployFilesFunctions.redeployProject(svrConn, preparedData[0], preparedData[1], preparedData[2]);
			DeployUtilities.writeProjectVersionFile(localFileName, versionInfo, svrConn);
		}
		catch (Exception e) {
			OneButton1Msg.showError(null, "Redeploy project error", e.getMessage(), "ok", OneButton1Msg.DEFAULT_SIZE);
		}
	}

	public static void main(String[] args) {
		DomeInit.initializeDOME();
		//deployProject();
	    //redeployProject();

        System.out.println("here again");

        DomeFileChooser buildFileChooser = new DomeFileChooser();
        System.out.println("here");
        String localFileName = buildFileChooser.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);
        System.out.println("there");

		if (localFileName == null) return; // cancelled
		Element modelElement = XMLUtils.fileToXmlElement(localFileName);
		IntegrationProjectBuilder ipbuilder = new IntegrationProjectBuilder(localFileName, modelElement);
        GLPanel.visualizeGraph(ipbuilder);



		//System.exit(0);
	}

}
