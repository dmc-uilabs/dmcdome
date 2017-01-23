package com.ford.kbedoor.sealanalysis.sealgap;

import com.sdrc.openideas.*;
import org.omg.CORBA.IntHolder;

import java.util.Vector;


/**
 * A Class to represent a OI Part. All part level functionality
 * is built into this class
 *
 * @author Parasu Narayanan
 * @since July 2000
 */

public class Part {
    protected SealGapAnalyzer sga = null;
    protected String name = null;
    Server server = null;

    public Part(SealGapAnalyzer seal, String partName, Server iServer) {
        sga = seal;
        name = partName;
        server = iServer;
    }

    public String getName() {
        return name;
    }

    public boolean groupExists(String groupName) {
        boolean exists = false;
        IntHolder errorCode = new IntHolder(0);
        try {
            OI_Part oiPart = getOIPart();
            OI_PartGrp[] oiPartGroup = oiPart.GetGroupByName(groupName, errorCode);
            if (errorCode.value != 0) {
                return exists;
            }
            if (oiPartGroup.length > 0)
                exists = true;

            for (int ii = 0; ii < oiPartGroup.length; ii++)
                server.serverSideRelease(oiPartGroup[ii]);
            server.serverSideRelease(oiPart);
        } catch (Exception e) {
            //throw e;
            System.out.println("Error: groupexists: " + e);
        }

        return exists;
    }

    public OI_Part getOIPart() {
        if (server != null) {
            try {
                //Check if the part is on the workbench
                OI_Part[] parts = server.getParts(name);
                if (parts == null || parts.length <= 0)
                    return null;
                if (parts.length == 1)
                    return parts[0];
            } catch (Exception e) {
                System.out.println("ERROR: " + e);
            }
        }
        return null;
    }

    public boolean createMaterialSide() {
        boolean retVar = false;
        if (groupExists("intfsurf")) {
            String command = "/mo spe ma; lab ";
            command += name;
            command += "; S;  ug; nn n; co ";
            command += name;
            command += "; fi; n; intfsurf; okay; gn 1; don; canc;; yes;;;;";

            server.sendCommandAndWait(command, 0);
            retVar = true;
        } else
            sga.printMessage("Group intfsurf does not exist in J Part. Aborting..");
        return retVar;
    }


    public String getRootNodeName() {
        String rootName = null;
        OI_Part oiPart = getOIPart();

        OI_FeatureNode featNode = oiPart.GetHistoryTree().GetRootNode();
        IntHolder errorCode = new IntHolder(0);

        rootName = featNode.GetName(errorCode);

        if (errorCode.value != 0) {
            System.out.println("Unable to get Feature model name");
            rootName = null;
        }
        server.serverSideRelease(featNode);
        server.serverSideRelease(oiPart);

        return rootName;
    }

    public String movePart(String featNodeName, double x, double y, double z) {
        String output = null;
        boolean rolledBack = false;

        server.disableUserError();
        server.disableUserInteraction();
        String featName = getRootNodeName();
        // Roll back history tree to the feature node
        String command = null;

        // Since we are deleting the history of the seal and the part,
        // roll back needs to happen only once. Ideas has a problem: if
        // the root node name of the door is changed and the part is
        // cut the root node name is not preserved.
        if (!featName.equals(featNodeName)) {
            command = "/cr spe ht; lab " + name + "; pt; don; SS; yes; ";
            command += "ba; re; canc";
            server.sendCommandAndWait(command, 0);
            featName = getRootNodeName();
            if (featName == null) return "Construct operation failed";
            rolledBack = true;
        }

        // We are at the right place in the history tree. Now move the part
        command = "$; $; /or; mo; lab " + name + "; pt; don;";
        command += x + "," + y + "," + z + "; $";
        server.sendCommandAndWait(command, 0);

        output = "success";

        // Once we have moved the part, do a rebuild if the part was rolled back
        if (rolledBack) {
            command = "/cr spe ht; lab " + name;
            command += "; pt; don; ff; re; ss; yes; canc";
            output = server.sendCommandAndWait(command);
        }

        server.enableUserInteraction();
        server.enableUserError();
        return output;

        /*String output = null;
        String command = null;
        command = "$; $; /or; mo; lab " + name + "; pt; don;";
        command += x + "," + y + "," + z + "; $";
        server.sendCommandAndWait(command, 0);

        output = "success";

        return output;*/
    }


    public boolean cleanWFEntities() {
        IntHolder errorCode = new IntHolder(0);
        OI_Part oiPart = getOIPart();
        OI_WFSection[] wfSections =
                oiPart.GetPartModel().GetAllWFSections(errorCode);
        boolean success = true;
        if (wfSections.length > 0) {
            for (int ii = 0; ii < wfSections.length; ii++)
                server.serverSideRelease(wfSections[ii]);

            String command = "/del; lab " + name + "; SC; ALL; DON; YES; $";
            server.sendCommandAndWait(command, 0);
        }

        OI_WFCurve[] wfCurves = oiPart.GetPartModel().GetAllWFCurves(errorCode);
        if (wfCurves.length > 0) {
            for (int ii = 0; ii < wfCurves.length; ii++)
                server.serverSideRelease(wfCurves[ii]);

            String command = "/del; lab " + name + "; C; ALL; DON; YES; $";
            server.sendCommandAndWait(command, 0);
        }
        server.serverSideRelease(oiPart);
        return success;
    }

    public void deleteHistory() {
        String command = "/mo; de; lab " + name + ";don; dh pt; ok; $";
        server.sendCommandAndWait(command, 0);
    }

    public void renameFeatureNode(String featureName) {
        OI_Part oiPart = getOIPart();
        OI_HistoryTree hist = oiPart.GetHistoryTree();
        OI_FeatureNode featNode = hist.GetRootNode();
        int errorCode = featNode.Rename(featureName);
        if (errorCode != 0) System.out.println("Error during history opn");

        // Release server side objects
        server.serverSideRelease(featNode);
        server.serverSideRelease(oiPart);
    }

    public boolean rollBackToNode(String nodeName) {
        String command = "$return; /cr spe ht; lab " + name +
                "; pt; don; sr se; fn " + nodeName +
                "; rne; re; canc";
        server.sendCommandAndWait(command, 0);
        return true;
    }


    public boolean createAllSurfaceGroup(String bulbName, String grpName) {
        IntHolder errorCode = new IntHolder(0);
        boolean retVal = true;
        OI_Part bulbPart = sga.getOIPart(bulbName);
        int outError = 0;

        //If this part already exists, delete it and create a new one.
        OI_PartGrp[] faceGrps = bulbPart.GetGroupByName(grpName, errorCode);
        if (faceGrps != null && faceGrps.length > 0)
            outError = bulbPart.DeleteGroup(faceGrps[0]);

        // Get all the user faces from the part
        OI_UserFace[] uFaces = bulbPart.GetPartModel().GetAllUserFaces(errorCode);
        if (errorCode.value != 0 || uFaces == null || uFaces.length == 0) {
            sga.printMessage("Cannot retrieve user faces from " + bulbName);
            return false;
        }

        // Stick all the faces in a group
        OI_Root[] prtGrpMember = new OI_Root[uFaces.length];
        for (int ii = 0; ii < uFaces.length; ii++)
            prtGrpMember[ii] = uFaces[ii];

        // Create the group
        OI_PartGrp newGrp = bulbPart.CreateGroup(prtGrpMember);
        outError = newGrp.ModifyName(grpName);
        if (outError != 0) {
            sga.printMessage("Cannot change group name ");
            retVal = false;
        }

        //Things must be hunky dory if we reached here.
        for (int ii = 0; ii < faceGrps.length; ii++)
            server.serverSideRelease(faceGrps[ii]);
        for (int ii = 0; ii < uFaces.length; ii++)
            server.serverSideRelease(uFaces[ii]);
        server.serverSideRelease(newGrp);
        server.serverSideRelease(bulbPart);

        return true;
    }

    public Vector getSurfacesNotInGroup(String partName, String grpName) {
        Vector surfNoGrp = new Vector();
        IntHolder errorCode = new IntHolder(0);

        //Get All the surfaces in the part
        OI_Part oiPart = sga.getOIPart(partName);
        OI_UserFace[] uFaces = oiPart.GetPartModel().GetAllUserFaces(errorCode);

        //Get all the faces in the group
        OI_PartGrp[] prtGrp = oiPart.GetGroupByName(grpName, errorCode);
        if (prtGrp == null || prtGrp.length == 0 || errorCode.value != 0) {
            System.out.println("Error getting part group from " + grpName);
            return null;
        }

        // Check which of the faces are in the group
        for (int ii = 0; ii < uFaces.length; ii++) {
            if (!prtGrp[0].IsInGroup(uFaces[ii], errorCode))
                surfNoGrp.add(new Integer(uFaces[ii].GetLabel()));
        }

        for (int ii = 0; ii < uFaces.length; ii++)
            server.serverSideRelease(uFaces[ii]);
        for (int ii = 0; ii < prtGrp.length; ii++)
            server.serverSideRelease(prtGrp[ii]);
        server.serverSideRelease(oiPart);
        return surfNoGrp;
    }

    public void addUserFaceToGroup(String partName, int surfId, String grpName) {
        //Add this surface to the group so that next time around we do not have
        // a problem.
        IntHolder errorCode = new IntHolder(0);
        OI_Part oiPart = sga.getOIPart(partName);
        OI_UserFace uFace = oiPart.GetPartModel().GetUserFace(surfId);
        OI_PartGrp[] prtGrp = oiPart.GetGroupByName(grpName, errorCode);
        prtGrp[0].AddGroupMember(uFace);

        server.serverSideRelease(uFace);
        for (int ii = 0; ii < prtGrp.length; ii++)
            server.serverSideRelease(prtGrp[ii]);
        server.serverSideRelease(oiPart);
    }

    public void removeUserFaceFromGroup(String partName, int surfId, String grpName) {
        //Add this surface to the group so that next time around we do not have
        // a problem.
        IntHolder errorCode = new IntHolder(0);
        OI_Part oiPart = sga.getOIPart(partName);
        OI_UserFace uFace = oiPart.GetPartModel().GetUserFace(surfId);
        OI_PartGrp[] prtGrp = oiPart.GetGroupByName(grpName, errorCode);
        prtGrp[0].RemoveGroupMember(uFace);

        server.serverSideRelease(uFace);
        for (int ii = 0; ii < prtGrp.length; ii++)
            server.serverSideRelease(prtGrp[ii]);
        server.serverSideRelease(oiPart);
    }

/*    protected boolean isStopRequested() {
        boolean status = false;
        if (sga.getStopStatus()) {
            int n = JOptionPane.showConfirmDialog(sga,
                    "Do you really want to stop the analysis?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION)
                status = true;
            else
                status = false;
        }
        sga.resetStopStatus();
        return status;
    }*/
}
