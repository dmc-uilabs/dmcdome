// SimplePlayspaceDeploy.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.deploy;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.gui.deploy.components.DeployPlayspaceData;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.PlayspaceVersionData;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;

import javax.swing.JOptionPane;
import java.util.Vector;

public class SimplePlayspaceDeploy
{
	public static void deployPlayspace() {
		DomeFileChooser fc = new DomeFileChooser();
		String localFileName = fc.showOpenDialog(null, DomeFileChooser.DOME_PLAYSPACE_FILTER);
		if (localFileName == null)
			return; // cancelled
		DeployPlayspaceData playspaceData = DeployUtilities.loadPlayspaceForDeploy(localFileName);
		//DeployPlayspaceData playspaceData = new DeployPlayspaceData(localFileName);
		if (playspaceData ==null) {
			System.err.println("unable to load playspace at "+localFileName);
			return;
		}

        String des = JOptionPane.showInputDialog("Playspace description: ");
        playspaceData.setPlayspaceDescription(des);

		OneButton1Msg.showOption(null,"The next step","Log into the server you wish to deploy on.","OK",
		        OneButton1Msg.DEFAULT_SIZE);
		ServerConnection svrConn = LoginPrompt.showDialog(null);
		if (svrConn==null) {
			OneButton1Msg.showOption(null, "Login cancelled", "Model was not deployed on any server", "OK",
			        OneButton1Msg.DEFAULT_SIZE);
			return;
		}

        // dummy folder id = 1
        Vector verInfo = DeployFilesFunctions.deployNewPlayspace(svrConn, 2,
                playspaceData.getPlayspaceDescription(),
                playspaceData.getXmlContent(), new Vector(), new Vector());

		PlayspaceVersionData psVersionData = new PlayspaceVersionData(verInfo, svrConn);
		DeployUtilities.writePlayspaceVersionFile(localFileName,psVersionData);

	}

    public static void redeployPlayspace()
    {
	    DomeFileChooser fc = new DomeFileChooser();
	    String localFileName = fc.showOpenDialog(null, DomeFileChooser.DOME_PLAYSPACE_FILTER);
	    if (localFileName == null)
            return; // cancelled
        //DeployPlayspaceData playspaceData = new DeployPlayspaceData(localFileName);
	    DeployPlayspaceData playspaceData = DeployUtilities.loadPlayspaceForDeploy(localFileName);
	    if (playspaceData == null)
	    {
            System.err.println("unable to load playspace at " + localFileName);
            return;
        }

	    OneButton1Msg.showOption(null, "The next step", "Log into the server you wish to deploy on.", "OK",
	            OneButton1Msg.DEFAULT_SIZE);
	    ServerConnection svrConn = LoginPrompt.showDialog(null);
	    if (svrConn == null)
	    {
		    OneButton1Msg.showOption(null, "Login cancelled", "Model was not deployed on any server", "OK",
		            OneButton1Msg.DEFAULT_SIZE);
		    return;
	    }

	    String playspaceId = DeployUtilities.getValidRedeployPlayspaceId(null, svrConn, playspaceData, localFileName);
	    if (!playspaceId.equals("")) {
	        playspaceData.setDeployId(playspaceId);
            String des = JOptionPane.showInputDialog("Playspace new description: ");
            playspaceData.setPlayspaceDescription(des);
            Vector verInfo = DeployFilesFunctions.redeployPlayspace(svrConn, playspaceId,
                  playspaceData.getPlayspaceDescription(),
                  playspaceData.getXmlContent(), new Vector(), new Vector());
            System.out.println("version info:" + verInfo);
	        PlayspaceVersionData psVersionData = new PlayspaceVersionData(verInfo, svrConn);
	        DeployUtilities.writePlayspaceVersionFile(localFileName, psVersionData);
	    }
    }

	/**
	 * if arguments exist, assume a valid playspace id and do redeploy
	 * otherwise, if no arguments, do a deploy
	 * @param args
	 */
	public static void main(String[] args)
	{
		DomeInit.initializeDOME();
        redeployPlayspace();
        //mit.cadlab.dome3.gui.deploy.deployPlayspace();
		System.exit(0);
	}

}
