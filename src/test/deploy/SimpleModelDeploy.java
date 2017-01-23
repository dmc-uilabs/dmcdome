// SimpleModelDeploy.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.deploy;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.deploy.components.DeployInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployModelData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.ModelVersionData;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class SimpleModelDeploy
{
	public static void deployModel() {
		DomeFileChooser fc = new DomeFileChooser();
		String localFileName = fc.showOpenDialog(null,DomeFileChooser.DOME_MODELS_FILTER);
		if (localFileName == null)
			return; // cancelled
		DeployModelData model = DeployUtilities.loadModelForDeploy(localFileName);
		if (model==null) {
			System.err.println("unable to load model at "+localFileName);
			return;
		}
		List selectedInterfaces = Arrays.asList(DeployUtilities.selectInterfaces(model.getModelInterfaces()));
		if (selectedInterfaces.isEmpty()) {
			OneButton1Msg.showOption(null, "Deploy model aborted", "Models can not be deployed without interfaces.", "OK",
			        OneButton1Msg.DEFAULT_SIZE);
			return;
		}
		for (int i = 0; i < selectedInterfaces.size(); i++) {
			((DeployInterfaceData)selectedInterfaces.get(i)).setIsAvailable(Boolean.TRUE);
		}
		OneButton1Msg.showOption(null,"The next step","Log into the server you wish to deploy on.","OK",
		        OneButton1Msg.DEFAULT_SIZE);
		ServerConnection svrConn = LoginPrompt.showDialog(null);
		if (svrConn==null) {
			OneButton1Msg.showOption(null, "Login cancelled", "Model was not deployed on any server", "OK",
			        OneButton1Msg.DEFAULT_SIZE);
			return;
		}
		Vector modelInfo = DeployUtilities.createNewModelInfoVector(new Integer(1),"some function",
		        model.getXmlContent(),null);
		Vector interfaceInfoVector = DeployUtilities.createDeployInterfaceInfoVector(model.getModelInterfaces());
		Vector verInfo = DeployFilesFunctions.deployNewModel(svrConn,modelInfo,interfaceInfoVector);
		DeployUtilities.writeModelVersionFile(localFileName,new ModelVersionData(verInfo,svrConn));
	}

	public static void redeployModel()
	{
		String modelId;

		DomeFileChooser fc = new DomeFileChooser();
		String localFileName = fc.showOpenDialog(null, DomeFileChooser.DOME_MODELS_FILTER);
		if (localFileName == null)
			return; // cancelled
		OneButton1Msg.showOption(null, "The next step", "Log into the server you wish to redeploy on.", "OK",
		        OneButton1Msg.DEFAULT_SIZE);
		ServerConnection svrConn = LoginPrompt.showDialog(null);
		if (svrConn == null) {
			OneButton1Msg.showOption(null, "Login cancelled", "Model was not redeployed on any server", "OK",
			        OneButton1Msg.DEFAULT_SIZE);
			return;
		}
		System.out.println("logged in succesfully");
		//new
		DeployModelData model = DeployUtilities.loadModelForDeploy(localFileName);
		if (model == null) {
			return; // could not load the model file
		}
		//method below performs all redeploy validity check and returns modelId if everything is OK
		modelId = DeployUtilities.getValidRedeployModelId(null, svrConn, model, localFileName);
		if (modelId.equals(""))
			return; //could not find valid modelId for redeployment
		//end new


		Object[] interfaces = DeployUtilities.selectInterfaces(model.getModelInterfaces());
		if(interfaces == null || interfaces.length == 0)
		{
			System.out.println("No interfaces selected. \nTerminating re-deploy");
			System.exit(0);
		}
		List selectedInterfaces = Arrays.asList(interfaces);
		for (int i = 0; i < selectedInterfaces.size(); i++) {
			((DeployInterfaceData) selectedInterfaces.get(i)).setIsAvailable(Boolean.TRUE);
		}

		Vector modelInfo = DeployUtilities.createUpdateModelInfoVector(modelId, "new description",
		        model.getXmlContent(), null);
		Vector interfaceInfoVector = DeployUtilities.createDeployInterfaceInfoVector(model.getModelInterfaces());
		Vector verInfo = DeployFilesFunctions.redeployModel(svrConn, modelInfo, interfaceInfoVector);
		DeployUtilities.writeModelVersionFile(localFileName, new ModelVersionData(verInfo, svrConn));
	}

	/**
	 * if arguments exist, assume a valid model id and do redeploy
	 * otherwise, if no arguments, do a deploy
	 * @param args
	 */
	public static void main(String[] args)
	{
		DomeInit.initializeDOME();

		//redeployModel();
		deployModel();



		System.exit(0);
	}

}
