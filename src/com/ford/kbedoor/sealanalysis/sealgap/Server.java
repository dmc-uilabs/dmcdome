package com.ford.kbedoor.sealanalysis.sealgap;

import com.ford.kbedoor.sealanalysis.oi.IdeasServer;
import com.ford.kbedoor.sealanalysis.oi.ServerEvent;
import com.ford.kbedoor.sealanalysis.oi.ServerEventListener;
import com.sdrc.openideas.OI_Bin;
import com.sdrc.openideas.OI_Part;
import com.sdrc.openideas.OI_Root;
import com.sdrc.openideas.OI_Server;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/**
 * A Customized I-DEAS Server class
 * for the glass modeller.
 * @author Ravi Boppe
 * @version 1.0
 */

public class Server extends IdeasServer {
    protected SealGapAnalyzer sga = null;

    private String homeDir = null;

    /** Constructor.
     * @param sga Instance of the SealGapAnalyzer class
     */
    public Server(SealGapAnalyzer sga) {
        this.sga = sga;
        homeDir = System.getProperty("home.dir");
    }

    /**
     *  Connect to other I-DEAS servers.
     */
    public void connect(OI_Server svr, ORB orb) throws Exception {
        try {
            super.connect(svr, orb);
            addServerExitListener(new ServerEventListener() {
                public void handleEvent(ServerEvent event) {
                    System.out.println("Server has been stopped");
                    disconnectFromCAD();
                }
            });

            addPrintErrorListener(new ServerEventListener() {
                public void handleEvent(ServerEvent event) {
                    sga.printMessage((String) event.getData());
                }
            });

            addModelSwitchListener(new ServerEventListener() {
                public void handleEvent(ServerEvent event) {
                    sga.printMessage("Server has switched model file");
                    sga.resetAll();
                }
            });

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Updates the parametric model based
     * on the best fit dimensions.
     */
/*    public void updateModel() {
        String ideasHost = null;
        if (sga.ideasHost == null)
            sga.setupCAD();

        try {
            if (!isConnected()) {
                sga.connectCAD();
            }
            //getPartReferences();
            //updateCylinders();
        } catch (Exception e) {
            System.out.println("I-DEAS Connection Error: " + e);
        }
    }*/

    /**
     * This method imports the I-DEAS parametric model
     * into the current model file. The parametric model
     * has been saved as an I-DEAS universal file.
     */
    public void importTemplate() {
        // Absolute path of the universal file.
        String unvFile = homeDir + "/unvfile/template.unv";

        // Import Side Door Glass Setup  parametric model
        // universal file. Assuming that the index of Unv
        // file is 4 in the selection index.
        // If that changes, it needs to be changed here.
        sendCommandAndWait("!!!f;i;s 4;Done;Okay;fil;" + unvFile + ";Okay;", 0);
    }

    /**
     * Gets the specified part onto the workbench
     *
     * @param partName The name of the part
     */
    public void getModelOntoWorkbench(String partName) {
        try {
            //Check if the part is on the workbench
            OI_Part[] parts = getParts(partName);
            if (parts == null || parts.length <= 0)
                return;
            if (parts.length == 1)
                load(parts[0]);

            serverSideRelease(parts[0]);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

    }

    public String cutParts(String cutterPart, String cutPart) {
        String relCmd = "/co r off";
        sendCommandAndWait(relCmd, 0);
        String command = "/co; c; lab ";
        command += cutterPart;
        command += "; ";
        command += cutPart;
        command += "; okay";

        getModelOntoWorkbench(cutPart);
        getModelOntoWorkbench(cutterPart);
        //System.out.println(command);
        String output = sendCommandAndWait(command);

        return output;
    }


    public void SendCommand(String command) {
        String currDir = System.getProperty("ideas.dir");
        File prgFile = new File(currDir + "programFile.prg");
        try {
            if (!prgFile.exists()) prgFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to create program file" + e);
        }

        try {
            FileWriter writer = new FileWriter(prgFile);
            writer.write("K : " + command);
            writer.write("\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Unable to write to programfile " + e);
        }


        String prgCmd = "$ mpos :; /F PR R ; FIL " + prgFile.getAbsolutePath() + ";okay;/";
        sendCommandAndWait(prgCmd, 0);
    }

    public int getLastRefId(String refType) {
        int refId = 0;
        IntHolder errorCode = new IntHolder(0);

        refId = (int) _cmdSvr.GetDoubleValue(refType, errorCode);
        //System.out.println("Server: Got ref id value");
        if (errorCode.value != 0) {
            if (errorCode.value == 66704) return -1;
            //System.out.println(errorCode.value);
            System.out.println("Unable to get reference point Id");
            return 0;
        }

        return refId;
    }

    public void reopenModelFile() {
        sendCommandAndWait("/f o n o", 0);
    }

    public void addParts(String aPart, String finalPart) {
        getModelOntoWorkbench(aPart);
        getModelOntoWorkbench(finalPart);
        System.out.println("Adding part" + aPart + " to " + finalPart);
        String command = "/; CO; A; label " + aPart + "; " + finalPart + "; $";
        sendCommandAndWait(command, 0);
    }

    public void turnOffForms() {
        sendCommand("/o p p 2; don; fd off; ok; ok; $");
    }

    public void turnOnForms() {
        sendCommandAndWait("/o p p 2; don; fd on; ok; ok; $", 0);
    }

    public Vector getPartNames() {
        Vector partNames = new Vector();
        IntHolder errorCode = new IntHolder(0);

        //Get all the bins from the part
        try {
            OI_Part[] parts = getParts("*");
            if (parts == null || parts.length <= 0) {
                System.out.println("No parts in model file");
                return null;
            }

            for (int i = 0; i < parts.length; i++) {
                partNames.add(parts[i].GetName(errorCode));
            }

            for (int i = 0; i < parts.length; i++)
                serverSideRelease(parts[i]);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return partNames;
    }

    public boolean disableUserInteraction() {
        int errorCode;
        // Disable user inputs and error outputs to the user
        if (_cmdSvr != null) {
            errorCode = _cmdSvr.DisableUserInput();
            if (errorCode != 0) {
                System.out.println("Cannot disable user input");
                return false;
            }
        }
        return true;
    }

    public boolean enableUserInteraction() {
        int errorCode;
        // Enable user inputs and error outputs to the user
        if (_cmdSvr != null) {
            errorCode = _cmdSvr.EnableUserInput();
            if (errorCode != 0) {
                System.out.println("Cannot enable user input");
                return false;
            }
        }
        return true;
    }

    public boolean disableUserError() {
        int errorCode;
        // Disable user inputs and error outputs to the user
        if (_cmdSvr != null) {
            errorCode = _cmdSvr.DisableErrorOutput();
            if (errorCode != 0) {
                System.out.println("Cannot disable error output");
                return false;
            }
        }
        return true;
    }

    public boolean enableUserError() {
        int errorCode;
        // Enable user inputs and error outputs to the user
        if (_cmdSvr != null) {
            errorCode = _cmdSvr.EnableErrorOutput();
            if (errorCode != 0) {
                System.out.println("Cannot enable user input");
                return false;
            }
        }
        return true;
    }

    public boolean copyPart(String oldPartName, String newPartName,
                            String newPartNo) {
        IntHolder errorCode = new IntHolder(0);
        try {
            //Check if the part is on the workbench
            OI_Part[] parts = getParts(oldPartName);
            if (parts == null || parts.length <= 0)
                return false;
            OI_Part oldPart = parts[0];

            OI_Bin oldBin = _server.GetBinById(oldPart.GetBinDetails(errorCode).binId);
            OI_Bin[] bins = _server.GetBins("Main", errorCode);
            if (errorCode.value != 0 || bins == null || bins.length <= 0) {
                System.out.println("Error retrieving bins");
                return false;
            }
            OI_Bin mainBin = bins[0];

            OI_Part newPart = oldBin.CopyPart(oldPart, mainBin,
                    newPartName, newPartNo);
            if (oldBin.GetErrorCode() != 0 || newPart == null) {
                sga.printError("Unable to copy parts in bin. Check permissions and restart", "Copy Error");
                return false;
            }
            unload(oldPart);
            serverSideRelease(oldPart);
            serverSideRelease(newPart);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            return false;
        }

        return true;
    }

    public long serverSideRelease(OI_Root rootObj) {
        long retVal = 0;
        try {
            if (rootObj != null) {
                retVal = _server.Release(rootObj);
            }
        } catch (Exception e) {
            System.out.println("Error Releasing object");
            retVal = 1;
        }
        return retVal;
    }

    public boolean checkApplTask() {
        boolean correct = false;
        IntHolder errorCode = new IntHolder(0);
        if (_server.GetCurrentApplication(errorCode) ==
                com.sdrc.openideas.OI_BaseServerPackage.Application.E_Design) {
            if (_server.GetCurrentTask(errorCode) !=
                    com.sdrc.openideas.OI_BaseServerPackage.Task.E_MasterModeler)
                sendCommandAndWait("/ta mm");

            correct = true;
        }
        return correct;
    }

    public void setXSAssociativityOff(String partName) {
        if (!disableUserInteraction()) return;
        String command = "/cr spe cr; lab " + partName + "; pt; don; ";
        String output = sendCommandAndWait(command + "ao; $; $; $");
        if (output.indexOf("turned off") == -1) {
            sendCommandAndWait(command + "ao; $; $; $", 0);
        }
        sendCommand(command + "ap; $");
        sendCommandAndWait("$; $");
        enableUserInteraction();
    }

}
