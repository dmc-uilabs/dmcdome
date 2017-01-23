package com.ford.kbedoor.sealanalysis.sealgap;

import com.sdrc.openideas.OI_Part;
import com.sdrc.openideas.OI_Server;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.omg.CORBA.ORB;
import mit.cadlab.dome3.network.client.functions.Vectors;

/**
 *
 * @author Parasuram Narayanan
 * @version 1.0
 * @since June 2000
 */
public class SealGapAnalyzer {

    protected Server server = null;
    protected String ideasHost = null;
    protected File sessionFile = null;

    // Part Names
    protected String ABSurfacePartName = null;
    protected String JSurfacePartName = null;
    protected String BulbPartName = null;
    protected String ABOriginalName = null;
    protected String JOriginalName = null;
    protected String BulbOriginalName = null;

    protected WStripPathPart ABPart;
    protected Part JPart;
    protected Part bulbPart;

    protected boolean deflectionStatus = false;

    // For Seal Gap Distance Offsets
    protected double xIntfTotal = 0.0;
    protected double yIntfTotal = 0.0;
    protected double zIntfTotal = 0.0;

    protected double xGapTotal = 0.0;
    protected double yGapTotal = 0.0;
    protected double zGapTotal = 0.0;

    protected double xMin = 1e7;
    protected double xMax = -1e7;
    protected double yMin = 1e7;
    protected double yMax = -1e7;
    protected double zMin = 1e7;
    protected double zMax = -1e7;

    private String nodeName = null;
    private double distBetSections = 90;
    int maxSections = 50;

    protected Vector areaVectors = null;
    protected Vector gapVectors = null;
    protected Vector areaOffsetStrings = null;
    protected Vector gapOffsetStrings = null;
    protected Vector currAreaData = null;
    protected Vector currGapData = null;

    public boolean sealInterferenceArea = true;
    private boolean partsCopied = false;
    private boolean stopRequested = false;
    public boolean deflectedDoor = false;
    public boolean combined = false;
    private boolean isBodyMounted;

    //To create the surfaces on the seal bulb
    private boolean firstIntfPlot = true;
    private boolean planesGenerated = false;

    // Data for all the offsets
    Vector offsetData = null;

    /** Constructor */
    public SealGapAnalyzer(OI_Server svr, ORB orb) {
        // passing on a handle on OI_Server object
        server = new Server(this);
        server.turnOffAutoSave();
        try {
            server.connect(svr, orb);
        } catch (Exception e) {
            System.err.println("SealGapAnalyzer: " + e);
        }
        server.printMessage("Connected from Seal Gap Analyzer ");
        ///server.turnOffForms();
    }

    public void disconnect() {
        server.disconnectFromCAD();
    }

    protected boolean initAll() {
        areaVectors = null;
        gapVectors = null;
        currAreaData = null;
        currGapData = null;
        areaOffsetStrings = null;
        gapOffsetStrings = null;
        System.gc();

        // Check the existence of parts and named groups
        if (!server.checkApplTask()) {
            System.err.println("Please change I-DEAS Application to Design \n" +
                    "and task to Master Modeler before starting \n" +
                    "the kbedoor analyzer");
            return false;
        }

        if (!checkAllParts())
            return false;
        if (!makeCopiesOfParts())
            return false;

        areaVectors = new Vector();
        gapVectors = new Vector();
        areaOffsetStrings = new Vector();
        gapOffsetStrings = new Vector();

        ABPart = new WStripPathPart(this, ABSurfacePartName, server);
        if (!ABPart.groupExists("sectionline")) {
            System.err.println("Group sectionline does not exist in " + ABSurfacePartName);
            return false;
        }
        if (!ABPart.groupExists("gapsurf")) {
            System.err.println("Group gapsurf does not exist in " + ABSurfacePartName);
            return false;
        }

        JPart = new Part(this, JSurfacePartName, server);
        if (!JPart.groupExists("intfsurf")) {
            printError("Group intfsurf does not exist in " +
                    JSurfacePartName, "Named Group");
            return false;
        }

        if (!JPart.groupExists("gapsurf")) {
            printError("Group gapsurf does not exist in " +
                    JSurfacePartName, "Named Group");
            return false;
        }


        bulbPart = new Part(this, BulbPartName, server);
        if (!bulbPart.groupExists("outersurf")) {
            printError("Group outersurf does not exist in " +
                    BulbPartName, "Named Group");
            return false;
        }

        server.getModelOntoWorkbench(JSurfacePartName);
        JPart.deleteHistory();
        JPart.renameFeatureNode("JSurfRootNode");

        // Get the history tree node name for the JPart
        nodeName = JPart.getRootNodeName();

        server.getModelOntoWorkbench(ABSurfacePartName);
        if (!ABPart.createSectionPoints("sectionline", distBetSections))
            return false;

        server.getModelOntoWorkbench(BulbPartName);
        server.setXSAssociativityOff(JSurfacePartName);

        return true;
    }

    public void setupPlanes() {
        try {
            Thread t = new Thread() {
                public void run() {
                    initializePlanes();
                }
            };
            t.start();
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
    }

    public void initializePlanes() {
        boolean success = initAll();
        ABPart.setStopXS(maxSections - 1);
        ABPart.setRotnPt(maxSections / 2);
        System.out.println("Section Points Generation Complete!");
    }

    public void analysis() {
        try {
            Thread t = new Thread() {
                public void run() {
                    if (combined) sealInterferenceArea = true;
                    analyzeSealGap();
                    if (combined) {
                        sealInterferenceArea = false;
                        analyzeSealGap();
                    }
                }
            };
            t.start();
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
    }

    public void analyzeSealGap() {
        double xPrevOffset;
        double yPrevOffset;
        double zPrevOffset;

        double xOffset;
        double yOffset;
        double zOffset;

        // Generate the planes if they havent been generated
        if (!planesGenerated) {
            if (!ABPart.createSectionPlanes()) return;
            planesGenerated = true;
        }

        int validOffs = 0;
        for (int loopId = 0; loopId < offsetData.size(); loopId++) {
            Offset off = (Offset) (offsetData.get(loopId));
            // If this item is not checked off in the form, dont use it
            if (!off.isInputValue())
                continue;
            else
                validOffs++;
        }

        if (sealInterferenceArea) {
            server.getModelOntoWorkbench(BulbPartName);
            if (firstIntfPlot)
                server.getModelOntoWorkbench(JSurfacePartName);
            else
                server.unload(JPart.getOIPart());
        } else {
            server.getModelOntoWorkbench(JSurfacePartName);
            server.unload(bulbPart.getOIPart());
        }

        if (deflectedDoor && !deflectionStatus) {
            if (!ABPart.deflectDoor(JSurfacePartName, isBodyMounted)) {
                System.out.println("Deflection failed");
                return;
            }
            deflectionStatus = true;
        }

        //server.disableGraphics();
        for (int loopId = 0; loopId < offsetData.size(); loopId++) {
            Offset off = (Offset) (offsetData.get(loopId));
            // If this item is not checked off in the form, dont use it
            if (!off.isInputValue()) continue;


            // Create a new vector to store data
            currAreaData = new Vector();
            currGapData = new Vector();

            if (sealInterferenceArea) {
                // Calculate the offsets
                xPrevOffset = xIntfTotal;
                yPrevOffset = yIntfTotal;
                zPrevOffset = zIntfTotal;
                if (isBodyMounted) {
                    xIntfTotal = off.getXValue();
                    yIntfTotal = off.getYValue();
                    zIntfTotal = off.getZValue();
                } else {
                    // Move the body panel the other way so that we get the same
                    // relative movement between the panel and the body
                    xIntfTotal = 0 - off.getXValue();
                    yIntfTotal = 0 - off.getYValue();
                    zIntfTotal = 0 - off.getZValue();
                }
                xOffset = xIntfTotal - xPrevOffset;
                yOffset = yIntfTotal - yPrevOffset;
                zOffset = zIntfTotal - zPrevOffset;

                if (firstIntfPlot) {
                    printMessage("Creating cross sections in bulb part");
                    if (!ABPart.createSealCutSections(BulbPartName)) {
                        printMessage("Unable to create plane cut section on seal");
                        return;
                    }
                    // Delete the history from the bulb part so that the
                    // operations are fast
                    bulbPart.cleanWFEntities();
                    bulbPart.deleteHistory();

                    // Perform a boolean cut between the sealbulb and the j part. The
                    // resultant will be the bulb part
                    // If the JPart was moved to measure kbedoor distance, return it
                    // to the nominal position before the parts are cut
                    if (xGapTotal != 0.0 || yGapTotal != 0.0 || zGapTotal != 0.0) {
                        JPart.movePart(nodeName, (0.0 - xGapTotal),
                                (0.0 - yGapTotal), (0.0 - zGapTotal));
                        xGapTotal = 0.0;
                        yGapTotal = 0.0;
                        zGapTotal = 0.0;
                    }

                    String output = server.cutParts(JSurfacePartName, BulbPartName);
                    if (!Parser.parseConstructOutput(output)) {
                        printMessage("I-DEAS cut operation did not succeed!");
                        return;
                    }
                    firstIntfPlot = false;
                }

                // Move only if necessary
                if (xOffset != 0.0 || yOffset != 0.0 || zOffset != 0.0) {
                    //Check to see if the move and the cut succeeded.
                    String output = bulbPart.movePart(nodeName, xOffset,
                            yOffset, zOffset);
                    xOffset = 0.0;
                    yOffset = 0.0;
                    zOffset = 0.0;
                    if (!Parser.parseConstructOutput(output)) {
                        printMessage("Cut operation after move failed");
                        printMessage("Continuing with the next offset");
                        continue;
                    }
                }
                // Create the interference sections and get the area
                printMessage("Measuring seal interference");
                if (!ABPart.measureSealInterference(BulbPartName)) {
                    break;
                }

                // Add the vector the the vector of vectors
                areaVectors.add(currAreaData);

            } else {
                printMessage("Measuring seal gap");

                // Calculate the offsets
                xPrevOffset = xGapTotal;
                yPrevOffset = yGapTotal;
                zPrevOffset = zGapTotal;
                if (isBodyMounted) {
                    xGapTotal = off.getXValue();
                    yGapTotal = off.getYValue();
                    zGapTotal = off.getZValue();
                } else {
                    // Move the body panel the other way so that we get the same
                    // relative movement between the panel and the body
                    xGapTotal = 0 - off.getXValue();
                    yGapTotal = 0 - off.getYValue();
                    zGapTotal = 0 - off.getZValue();
                }
                xOffset = xGapTotal - xPrevOffset;
                yOffset = yGapTotal - yPrevOffset;
                zOffset = zGapTotal - zPrevOffset;

                // Move the JPart to the new positions
                if (xOffset != 0.0 || yOffset != 0.0 || zOffset != 0.0) {
                    String output = JPart.movePart(nodeName, xOffset,
                            yOffset, zOffset);
                    xOffset = 0.0;
                    yOffset = 0.0;
                    zOffset = 0.0;
                }
                //Create curves and measure kbedoor
                if (!ABPart.measureSealGap(JSurfacePartName)) {
                    break;
                }
                ;

                // Add the vectors and the string
                gapVectors.add(currGapData);

                JPart.cleanWFEntities();
                ABPart.cleanWFEntities();
            }
        }
        //server.turnOnForms();
    }

    // or door
    public void setABSurfacePartName(String partName) {
        ABSurfacePartName = partName;
    }

    // or bodyside
    public void setJSurfacePartName(String partName) {
        JSurfacePartName = partName;
    }

    public void setBulbPartName(String partName) {
        BulbPartName = partName;
    }

    public String getBulbName() {
        return BulbPartName;
    }

    public void resetAll() {
        deflectionStatus = false;
        deflectedDoor = false;
        combined = false;
    }

    public void reopenModelFile() {
        server.reopenModelFile();
    }

    public void setSectionDistance(double dist) {
        if (dist > 0.0)
            distBetSections = dist;
        else {
            printMessage("Cannot set distance between sections to a negative value");
            printMessage("Defaulting back to 90 mm");
            distBetSections = 90;
        }

        // Inform the user of the setting.
        printMessage("Section Pitch " + distBetSections + " mm");
    }

    public double getSectionDistance() {
        return distBetSections;
    }

    public OI_Part getOIPart(String partName) {
        if (partName.equals(ABSurfacePartName))
            return ABPart.getOIPart();
        else if (partName.equals(JSurfacePartName))
            return JPart.getOIPart();
        else if (partName.equals(BulbPartName))
            return bulbPart.getOIPart();
        else
            System.out.println("Unknown part name");
        return null;
    }

    public Vector getPartNames() {
        return server.getPartNames();
    }

    public Part getSealPart() {
        return bulbPart;
    }

    public Part getClosurePart() {
        return JPart;
    }

    public Vector makeOffsetData(double x, double y, double z) {
        Offset offset = new Offset(x, y, z);
        return Vectors.create(offset);
    }

    // Set and get the offset data vector
    public void setOffsetData(Vector data) {
        offsetData = data;
    }

    public Vector getOffsetData() {
        return offsetData;
    }

    public void addAreaData(double area) {
        currAreaData.add(new Double(area));
    }

    private void setDefaultValues() {
        ABSurfacePartName = null;
        JSurfacePartName = null;
        BulbPartName = null;
        ABOriginalName = null;
        JOriginalName = null;
        BulbOriginalName = null;

        ABPart = null;
        JPart = null;
        bulbPart = null;

        deflectionStatus = false;
        deflectedDoor = false;
        combined = false;

        // For Seal Gap Distance Offsets
        xIntfTotal = 0.0;
        yIntfTotal = 0.0;
        zIntfTotal = 0.0;

        xGapTotal = 0.0;
        yGapTotal = 0.0;
        zGapTotal = 0.0;

        xMin = 1e7;
        xMax = -1e7;
        zMin = 1e7;
        zMax = -1e7;

        nodeName = null;
        distBetSections = 90;

        areaVectors = null;
        gapVectors = null;
        areaOffsetStrings = null;
        gapOffsetStrings = null;
        currAreaData = null;
        currGapData = null;

        sealInterferenceArea = true;
        partsCopied = false;
        maxSections = 50;

        //To create the surfaces on the seal bulb
        firstIntfPlot = true;
        planesGenerated = false;

        // Data for all the offsets
        offsetData = null;
        stopRequested = false;
    }

    private boolean checkAllParts() {
        if (ABSurfacePartName == null || JSurfacePartName == null ||
                BulbPartName == null) {
            System.err.println("Enter valid part names");
            return false;
        }
        if (server != null) {
            try {
                //Check if the part is on the workbench
                OI_Part[] parts = server.getParts(ABSurfacePartName);
                if (parts == null || parts.length <= 0) {
                    System.err.println(ABSurfacePartName + " does not exist");
                    return false;
                }
                server.serverSideRelease(parts[0]);

                parts = server.getParts(JSurfacePartName);
                if (parts == null || parts.length <= 0) {
                    System.err.println(JSurfacePartName + " does not exist");
                    return false;
                }
                server.serverSideRelease(parts[0]);

                parts = server.getParts(BulbPartName);
                if (parts == null || parts.length <= 0) {
                    System.err.println(BulbPartName + " does not exist");
                    return false;
                }
                server.serverSideRelease(parts[0]);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        } else
            return false;
        return true;
    }

    private boolean makeCopiesOfParts() {
        // Check if the parts have been copied before
        if (partsCopied) return true;

        String user = System.getProperty("user.name");
        Date currDate = (Calendar.getInstance()).getTime();
        String dateTime = (DateFormat.getInstance()).format(currDate);
        String partNo = user;
        dateTime = dateTime.replace(':', '-');
        dateTime = dateTime.replace(';', '-');
        dateTime = dateTime.replace(',', '-');
        dateTime = dateTime.replace('"', '-');
        dateTime = dateTime.replace(' ', '-');
        //Make copy of the AB Part
        if (server != null) {
            ABOriginalName = ABSurfacePartName;
            ABSurfacePartName = new String(ABOriginalName + "-" + dateTime);
            if (!server.copyPart(ABOriginalName, ABSurfacePartName, partNo)) {
                // restore the names back
                ABSurfacePartName = ABOriginalName;
                ABOriginalName = null;
                return false;
            }

            // Make copy of the JPart
            JOriginalName = JSurfacePartName;
            JSurfacePartName = new String(JOriginalName + "-" + dateTime);
            if (!server.copyPart(JOriginalName, JSurfacePartName, partNo)) {
                // restore the names back
                JSurfacePartName = JOriginalName;
                JOriginalName = null;
                return false;
            }

            //Make copy of bulb part
            BulbOriginalName = BulbPartName;
            BulbPartName = new String(BulbOriginalName + "-" + dateTime);
            if (!server.copyPart(BulbOriginalName, BulbPartName, partNo)) {
                // restore the names back
                BulbPartName = BulbOriginalName;
                BulbOriginalName = null;
                return false;
            }
        }
        // remove everything from the workbench
        server.unload(null);
        partsCopied = true;
        return true;
    }

    protected void breakProcess() {
        stopRequested = true;
    }

    public boolean getStopStatus() {
        return stopRequested;
    }

    public void resetStopStatus() {
        stopRequested = false;
    }

    public int getMaxSections() {
        return maxSections;
    }

    public void setStartXS(int startVal) {
        ABPart.setStartXS(startVal);
    }

    public void setStopXS(int stopVal) {
        ABPart.setStopXS(stopVal);
    }

    public void setRotnPt(int rotn) {
        ABPart.setRotnPt(rotn);
    }

    public void setDeflection(double defl) {
        ABPart.setDeflection(defl);
    }

    // just so that the previous printError method can be used
    public void printError(String msg, String dummy) {
        System.err.println(msg);
    }

    public void printMessage(String msg) {
        System.out.println(msg);
    }

    public void setBodyMounted(boolean bodyMounted) {
        isBodyMounted = bodyMounted;
    }

    public void setCombinedAnalysis(boolean combined) {
        this.combined = combined;
    }

    public void setInterferenceAnalysis(boolean sealInterferenceArea) {
        this.sealInterferenceArea = sealInterferenceArea;
    }

    public void setDeflectedDoor(boolean deflectedDoor) {
        this.deflectedDoor = deflectedDoor;
    }

    public Vector getAreaVectors() {
        return areaVectors;
    }
}
