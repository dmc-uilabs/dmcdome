package com.ford.kbedoor.sealanalysis.sealgap;

import com.sdrc.openideas.*;
import org.omg.CORBA.DoubleHolder;
import org.omg.CORBA.IntHolder;

import javax.vecmath.Vector3d;
import java.util.Hashtable;
import java.util.Vector;


/**
 * A Class to represent a OI Part. All part level functionality
 * is built into this class
 *
 * @author Parasu Narayanan
 * @since July 2000
 */

public class WStripPathPart extends Part {
    protected Vector edgeLoopList = null;
    protected Vector uEdgeIds = null;
    protected Vector edgeInfo = null;
    protected Vector uEdgeFlags = null;
    protected Hashtable uEdgeVerts = null;
    protected Hashtable edgeRefPlanes = null;
    protected Hashtable sectionAreas = null;
    protected Vector refPlaneList = null;
    protected Hashtable refPlaneAndPoint = null;

    protected Vector sealXSSurfIds = null;

    protected int firstRefPlaneId = 0;
    protected int lastRefPlaneId = 0;

    private boolean openALine = false;

    protected int startPoint = 0;
    protected int stopPoint = 0;
    protected int rotnPoint = 0;
    protected double deflection = 2.0;

    public WStripPathPart(SealGapAnalyzer seal, String partName, Server iServer) {
        super(seal, partName, iServer);
    }

    public boolean createEdgeLoop(String groupName) {
        IntHolder errorCode = new IntHolder(0);
        uEdgeIds = new Vector();
        DoubleHolder invalidMems = new DoubleHolder(0.0);
        try {
            String grpCommand = "/li in; ug; nn n; co " + name + "; fi; n; ";
            grpCommand += groupName + "; ok; gn 1; don; canc; don; $";
            String output = server.sendCommandAndWait(grpCommand);
            if (output == null) {
                sga.printError("Unable to capture Ideas output", "Error");
                return false;
            }

            // Get the edge info from the parser
            edgeInfo = Parser.parseEdgeOutput(output);

            for (int i = 0; i < edgeInfo.size(); i++) {
                uEdgeIds.add(new Integer(((Edge) edgeInfo.get(i)).id));
            }

            if (uEdgeVerts == null) {
                // Populate the edge verts with the edge verts of all the uEdgeIds
                // Assuming that this is the first edge. So get ids for the curr edge
                // and the edges in uEdgeIds
                uEdgeVerts = new Hashtable();
                for (int i = 0; i < uEdgeIds.size(); i++) {
                    Vector vertIds = getUserEdgeVertices(((Integer) uEdgeIds.get(i)).intValue());
                    if (vertIds == null) {
                        sga.printError("Unable to get vertex ids for edges", "Error");
                        return false;
                    }

                    uEdgeVerts.put(uEdgeIds.get(i), vertIds);
                }
            }


            //Start from the first edge and keep getting the next edge in
            //the loop. First store the first edge in the vector
            edgeLoopList = new Vector();
            int currEdgeId = ((Integer) (uEdgeIds.firstElement())).intValue();
            edgeLoopList.add(new Integer(currEdgeId));
            uEdgeIds.remove(0);

            //Store the edges in order
            int numEdges = uEdgeIds.size();
            int nextEdgeId = 0;
            for (int i = 0; i < numEdges; i++) {
                nextEdgeId = getNextUserEdgeId(currEdgeId, uEdgeIds);
                if (nextEdgeId == 0 || nextEdgeId == -99) {
                    if (!openALine)
                        openALine = true;
                    else {
                        sga.printError("Problem with a-line", "Error");
                        System.out.print("There seems to be a problem with the ");
                        System.out.print(" a-line. Either the surfaces are not ");
                        System.out.print(" stitched or an edge is missing in the ");
                        System.out.print("a-line group. Please check and restart ");
                        System.out.println("the program");
                        return false;
                    }
                    // If this is the case, it is assumed that there is an open
                    // a line. What this means is that, the a-line does not form
                    // a closed loop. In this case we take the first edge a go
                    // the other way to stick it at the top of the list and
                    // hopefully we have a ordered set of edges
                    // We started from the middle and gone to one end. To complete
                    // the ordered set, start again from the first and go the other
                    // way
                    currEdgeId = ((Integer) edgeLoopList.get(0)).intValue();
                    nextEdgeId = getNextUserEdgeId(currEdgeId, uEdgeIds);
                    if (nextEdgeId == 0 || nextEdgeId == -99) {
                        sga.printError("Problem with a-line", "Error");
                        System.out.print("There seems to be a problem with the ");
                        System.out.print(" a-line. Either the surfaces are not ");
                        System.out.print(" stitched or an edge is missing in the ");
                        System.out.print("a-line group. Please check and restart ");
                        System.out.println("the program");
                        return false;
                    }
                }
                if (openALine)
                    edgeLoopList.insertElementAt(
                            new Integer(nextEdgeId), 0);
                else
                    edgeLoopList.add(new Integer(nextEdgeId));

                currEdgeId = nextEdgeId;
            }

            // There should be one single edge left in the list
            //if( uEdgeIds.size() > 0) {
            //   if( openALine) edgeLoopList.insertElementAt( uEdgeIds.get(0), 0);
            //   else edgeLoopList.add(uEdgeIds.get(0));
            //}
            uEdgeIds = null;

            if (uEdgeFlags == null) {
                uEdgeFlags = new Vector();

                //Check the direction of each of the edges and set a reversal
                // flag if it needs to be reversed
                for (int i = 0; i < edgeLoopList.size() - 1; i++) {
                    Vector currVerts = (Vector) uEdgeVerts.get(edgeLoopList.get(i));
                    Vector nextVerts = (Vector) uEdgeVerts.get(edgeLoopList.get(i + 1));
                    int currVert1 = ((Integer) (currVerts.get(0))).intValue();
                    if (currVert1 == ((Integer) (nextVerts.get(0))).intValue() ||
                            currVert1 == ((Integer) (nextVerts.get(1))).intValue()) {
                        uEdgeFlags.add(new Integer(1));
                    } else {
                        uEdgeFlags.add(new Integer(0));
                    }
                }

                // Now try the last edge.
                if (edgeLoopList.size() > 1) {
                    Vector currVerts =
                            (Vector) uEdgeVerts.get(edgeLoopList.lastElement());
                    Vector prevVerts = (Vector) uEdgeVerts.get(edgeLoopList.get(
                            edgeLoopList.size() - 2));
                    int currVert1 = ((Integer) (currVerts.get(0))).intValue();
                    if (currVert1 == ((Integer) (prevVerts.get(0))).intValue() ||
                            currVert1 == ((Integer) (prevVerts.get(1))).intValue()) {
                        uEdgeFlags.add(new Integer(0));
                    } else {
                        uEdgeFlags.add(new Integer(1));
                    }

                    // Sanity check, in some cases where there is a partial a line
                    // and the edge ids line up in order, the previous check for
                    // partial a line will not catch it. So here  we have to check
                    // the last vertex of the last edge and the first vertex of the
                    // first edge to see if they are the same. If they are not the
                    // same, it is a partial a line.
                    if (!openALine) {
                        int firstVertFirstEdge = 0;
                        int lastVertLastEdge = -1;
                        Vector firstVerts = (Vector) uEdgeVerts.
                                get(edgeLoopList.get(0));
                        Vector lastVerts = (Vector) uEdgeVerts.
                                get(edgeLoopList.lastElement());
                        if (((Integer) (uEdgeFlags.get(0))).intValue() == 0) {
                            firstVertFirstEdge = ((Integer) (firstVerts.get(0))).
                                    intValue();
                        } else {
                            firstVertFirstEdge = ((Integer) (firstVerts.get(1))).
                                    intValue();
                        }
                        if (((Integer) (uEdgeFlags.lastElement())).intValue() == 0) {
                            lastVertLastEdge = ((Integer) (lastVerts.get(1))).
                                    intValue();
                        } else {
                            lastVertLastEdge = ((Integer) (lastVerts.get(0))).
                                    intValue();
                        }
                        if (firstVertFirstEdge != lastVertLastEdge) openALine = true;
                    }

                }
                //In case we have only a single edge in the loop
                if (uEdgeFlags.size() == 0) {
                    uEdgeFlags.add(new Integer(0));
                }
            }
            // Order the edges in a clockwise manner
            orderEdgesClockwise();

            //uEdgeVerts = null;
            if (edgeLoopList.size() > 2 && !openALine)
                reorderEdgeLoop();

        } catch (Exception e) {
            //throw e;
            System.out.println("Error: createEdgeLoop : " + e);
        }
        return true;

    }

    /** Reorders the edges to start from the rocker area of the door
     */
    private void reorderEdgeLoop() {
        // Get all the start and the end vertices of the edge and find
        // out which ones are flat. First find out the edges at the  bottom of
        // the door
        Vector bottomEdges = new Vector();
        Vector newEdgeLoopList = new Vector();
        Vector newEdgeFlags = new Vector();
        int lowestEdgeId = 0;
        double lowZ = 1e5;
        double currXLen = 0;
        IntHolder errorCode = new IntHolder(0);
        for (int i = 0; i < edgeLoopList.size(); i++) {
            // Get the vertex and get the z mid point
            Vector currVerts = (Vector) uEdgeVerts.get(edgeLoopList.get(i));
            int vert1 = ((Integer) (currVerts.get(0))).intValue();
            int vert2 = ((Integer) (currVerts.get(1))).intValue();

            OI_Part oiPart = getOIPart();
            OI_Vertex oiVert1 = oiPart.GetPartModel().GetVertex(vert1);
            OI_Vertex oiVert2 = oiPart.GetPartModel().GetVertex(vert2);

            OI_3DPoint pt1 = oiVert1.GetPoint(errorCode);
            OI_3DPoint pt2 = oiVert2.GetPoint(errorCode);

            double midZ = (pt1.z + pt2.z) / 2;
            double xLen = 0;
            if (midZ < lowZ || (Math.abs(midZ - lowZ) < 1e-3)) {
                // if the average is the same, get the longer edge
                xLen = pt1.x - pt2.x;
                if ((Math.abs(midZ - lowZ) < 1e-3)) {
                    if (Math.abs(xLen) > currXLen) {
                        currXLen = Math.abs(xLen);
                        lowestEdgeId = ((Integer) edgeLoopList.get(i)).intValue();
                        lowZ = midZ;
                    }
                } else {
                    currXLen = Math.abs(xLen);
                    lowZ = midZ;
                    lowestEdgeId = ((Integer) edgeLoopList.get(i)).intValue();
                }
            }

            server.serverSideRelease(oiVert1);
            server.serverSideRelease(oiVert2);
            server.serverSideRelease((OI_Root) oiPart);
        }

        // At this point we have the edge with the lowest mid z
        // Locate this on the edge loop Id and reorder edge loop id
        // Note: the first and the last edge id would be same in the
        // new list because the list has to start from the middle of the
        // rocker and end in the middle of the rocker.
        int startIndex = edgeLoopList.indexOf(new Integer(lowestEdgeId));
        for (int i = startIndex; i < edgeLoopList.size(); i++) {
            newEdgeLoopList.add(edgeLoopList.get(i));
            newEdgeFlags.add(uEdgeFlags.get(i));
        }

        // now for the other side
        for (int i = 0; i < startIndex; i++) {
            newEdgeLoopList.add(edgeLoopList.get(i));
            newEdgeFlags.add(uEdgeFlags.get(i));
        }

        // Since the first edge id and the last edge id are the same, it might
        // create a problem when we are hashing based on edge id's. So just
        // for convinience store the edge id as a negative edge id
        newEdgeLoopList.add(new Integer(0 -
                ((Integer) (edgeLoopList.get(startIndex))).intValue()));

        edgeLoopList = newEdgeLoopList;
        newEdgeFlags.add(uEdgeFlags.get(startIndex));
        uEdgeFlags = newEdgeFlags;
        uEdgeVerts = null;
    }

    protected int getNextUserEdgeId(int currId, Vector uEdges) {
        int edgeId = 0;

        if (uEdges.size() == 0) {
            System.out.println("No Edges in list ");
            return -99;
        }

        // Get the vartex id of the curr edge and compare it to all the edges
        // in the hash table
        Vector currVerts = (Vector) uEdgeVerts.get(new Integer(currId));
        int currId1 = ((Integer) (currVerts.get(0))).intValue();
        int currId2 = ((Integer) (currVerts.get(1))).intValue();
        for (int i = 0; i < uEdges.size(); i++) {
            edgeId = ((Integer) (uEdges.get(i))).intValue();
            Vector edgeVerts = (Vector) uEdgeVerts.get(new Integer(edgeId));
            if (currId1 == ((Integer) (edgeVerts.get(0))).intValue() ||
                    currId1 == ((Integer) (edgeVerts.get(1))).intValue() ||
                    currId2 == ((Integer) (edgeVerts.get(0))).intValue() ||
                    currId2 == ((Integer) (edgeVerts.get(1))).intValue()) {
                // remove the edge from the list so that it is not compared the
                // the next time.
                uEdges.remove(i);
                return edgeId;
            }
        }

        edgeId = 0;
        return edgeId;
    }

    protected Vector getUserEdgeVertices(int userEdgeId) {
        IntHolder errorCode = new IntHolder(0);
        // get the vertices of the end ids.
        Vector vertIds = new Vector();
        OI_Part oiPart = getOIPart();
        OI_FeatureModel fModel = oiPart.GetPartModel();
        OI_UserEdge currUEdge = fModel.GetUserEdge(userEdgeId);
        OI_Edge[] edges = currUEdge.GetEdges(errorCode);
        if (errorCode.value != 0) {
            System.out.println("Unable to get edges from user edge");
            return null;
        }

        // Get the vertices of the edges. Sort them so that we are left with just
        // the two that would be of the user edge's.
        OI_Vertex[] vertices = null;
        for (int i = 0; i < edges.length; i++) {
            vertices = edges[i].GetVertices(errorCode);
            if (errorCode.value != 0) {
                System.out.println("Unable to get vertices");
                return null;
            }

            // If this is the first edge store both the vertices in the vector
            if (i == 0) {
                vertIds.add(new Integer((int) vertices[0].GetLabel()));
                vertIds.add(new Integer((int) vertices[1].GetLabel()));
            } else {
                boolean foundMatch[] = {false, false};
                int startNumb = 0;
                int endNumb = 1;
                for (int j = 0; j < vertIds.size(); j++) {
                    for (int k = startNumb; k <= endNumb; k++) {
                        if ((int) (vertices[k].GetLabel()) ==
                                ((Integer) (vertIds.get(j))).intValue()) {
                            foundMatch[k] = true;
                            if (k == startNumb)
                                startNumb = 1;
                            else
                                endNumb = 0;
                            vertIds.remove(j);
                            foundMatch[k] = true;
                            break;
                        }
                    }
                    if (foundMatch[0] && foundMatch[1]) break;
                }
                if (!foundMatch[0])
                    vertIds.add(new Integer((int) vertices[0].GetLabel()));
                if (!foundMatch[1])
                    vertIds.add(new Integer((int) vertices[1].GetLabel()));
            }
        }
        // serverSideRelease all serverside stuff before going back from here
        for (int ii = 0; ii < vertices.length; ii++)
            server.serverSideRelease(vertices[ii]);
        for (int ii = 0; ii < edges.length; ii++)
            server.serverSideRelease(edges[ii]);
        server.serverSideRelease(currUEdge);
        server.serverSideRelease(fModel);
        server.serverSideRelease(oiPart);

        // At this point the number of vertices should be 2. If not error out
        if (vertIds.size() != 2) return null;
        return vertIds;
    }

    public boolean createSectionPoints(String groupName, double distBetSections) {
        boolean success = false;

        if (!groupExists(groupName)) {
            sga.printMessage("Group " + groupName + " does not exist in the part. Aborting ......");
            return success;
        }

        // First create the edge loop out of the lines
        if (createEdgeLoop(groupName)) {
            // Create reference planes on all the edges in the edge loop.
            // First create the ref points
            int firstPointId = 0;
            int lastPointId = 0;
            double balance = 0;
            double initOffset = 0;

            //Populate the hash table with the info from the parser
            edgeRefPlanes = new Hashtable();


            for (int i = 0; i < edgeLoopList.size(); i++) {
                Thread.yield();

                // Get the edge id
                int edgeId = ((Integer) (edgeLoopList.get(i))).intValue();
                if (edgeId < 0) edgeId = Math.abs(edgeId);
                double edgeLen = 0;

                // Get the edge struct to get the length eventually
                for (int j = 0; j < edgeInfo.size(); j++) {
                    Edge edge = (Edge) edgeInfo.get(j);
                    if (edgeId == edge.id) {
                        edgeLen = edge.length;
                        break;
                    }
                }

                if (edgeLen == 0.0) {
                    return false;
                }

                // For the first and the last edges we are doing only half the
                // the edge since they are the same
                /*if( edgeLoopList.size() > 2 && !openALine )
                   if( i == 0 || i == (edgeLoopList.size() - 1 ))
                      edgeLen = edgeLen/2;*/

                // Calc the initial offset
                if (balance != 0.0)
                    initOffset = distBetSections - balance;
                else
                    initOffset = 0;


                // If the edge length is lesser than the initial offset
                // remove the edge from the group and proceed with the next
                // edge
                while (initOffset > edgeLen) {
                    balance = balance + edgeLen;
                    edgeLoopList.remove(i);
                    uEdgeFlags.remove(i);
                    boolean repeat = false;

                    //Proceed to the next edge if there is one
                    if (i >= edgeLoopList.size()) break;
                    edgeId = ((Integer) (edgeLoopList.get(i))).intValue();
                    if (edgeId < 0) {
                        repeat = true;
                        edgeId = Math.abs(edgeId);
                    }
                    edgeLen = 0;

                    // Get the edge struct to get the length eventually
                    for (int j = 0; j < edgeInfo.size(); j++) {
                        Edge edge = (Edge) edgeInfo.get(j);
                        if (edgeId == edge.id) {
                            edgeLen = edge.length;
                            break;
                        }
                    }
                    if (edgeLen == 0.0) {
                        return false;
                    }

                    //if( repeat ) edgeLen = edgeLen/2;

                    // Calc the initial offset
                    initOffset = distBetSections - balance;
                }

                //If the last skipped edge was the last in the list, get the
                //hell outta here
                if (i >= edgeLoopList.size()) break;

                double startPt = (initOffset / edgeLen) * 100;
                double endPt = 99.5;

                if (edgeLoopList.size() > 2 && !openALine) {
                    if (i == 0) startPt = 50;
                    if (i == (edgeLoopList.size() - 1)) {
                        endPt = 49.5;
                        // If we have to start after we end, remove the last edge
                        // and continue
                        if (endPt < startPt) {
                            edgeLoopList.remove(edgeLoopList.size() - 1);
                            break;
                        }
                    }
                } else {
                    if (i == 0) {
                        startPt = 15;
                        initOffset = (startPt * edgeLen) / 100;
                    }
                    if (i == (edgeLoopList.size() - 1)) endPt = 85;
                }

                String command = "/cre ref po; srs; lab " + name + "; E;";
                command += edgeId;
                command += ";done ; ";

                // Add the rest of the stuff for the command
                command += "0.0; sl " + startPt;
                command += "; el " + endPt + "; cm; ds; ds ";
                command += distBetSections + "; ";
                if (((Integer) (uEdgeFlags.get(i))).intValue() == 1) {
                    command += "FL; ";
                }
                command += "okay; done; $;";

                server.sendCommandAndWait(command, 0);

                // Get the id of the last ref point created
                //if( firstPointId == -1 )
                firstPointId = getFirstRefPoint();
                lastPointId = server.getLastRefId("Z_LAST_RPT");

                Vector refIds = new Vector();
                refIds.add(new Integer(firstPointId));
                refIds.add(new Integer(lastPointId));

                edgeRefPlanes.put(edgeLoopList.get(i), refIds);

                // Calculate the extra length remaining of the edge after all
                // the points have been created
                if (edgeLoopList.size() > 2 && !openALine && i == 0)
                    edgeLen = edgeLen / 2.0;
                balance = (edgeLen - initOffset - (0.005 * edgeLen)) % distBetSections;

                success = true;
            }

            uEdgeFlags = null;
            edgeInfo = null;

            //plotPerimeterPositions();

        }
        return success;
    }

    public boolean createSectionPlanes() {
        boolean success = false;
        refPlaneList = new Vector();
        refPlaneAndPoint = new Hashtable();

        int currId = 0;
        boolean startSecn = false;
        boolean stopSecn = false;
        int firstPointId = 0;
        int lastPointId = 0;
        for (int i = 0; i < edgeLoopList.size(); i++) {
            Thread.yield();

            Vector refIds = (Vector) edgeRefPlanes.get(edgeLoopList.get(i));


            firstPointId = ((Integer) (refIds.get(0))).intValue();
            lastPointId = ((Integer) (refIds.get(1))).intValue();

            int firstPlaneId = 0;
            int lastPlaneId = 0;
            startSecn = false;
            stopSecn = false;


            // Now that all the ref points are created, create the ref planes
            int adj = 0;
            for (int j = firstPointId; j <= lastPointId; j++) {

                if (currId < startPoint || currId > stopPoint) {
                    currId++;
                    continue;
                } else {
                    if (currId == startPoint) {
                        startSecn = true;
                        adj = j - firstPointId;
                    } else if (currId == stopPoint) stopSecn = true;
                    currId++;
                }

                String command = "/cr ref pl; cur; lab " + name + "; E; ";
                command += Math.abs(((Integer) (edgeLoopList.get(i))).intValue());
                command += "; lab; lab " + name + "; RFPT; RF" + j + "; $";

                server.sendCommandAndWait(command, 0);

                if (j == firstPointId || startSecn == true) {
                    firstPlaneId = server.getLastRefId("Z_LAST_RPL");
                    startSecn = false;
                } else if (j == lastPointId || stopSecn == true) {
                    lastPlaneId = server.getLastRefId("Z_LAST_RPL");
                    stopSecn = false;
                }

            }

            if (firstPlaneId == 0) continue;
            //In cases where there are only one point on the edge, the
            // last point id is 0. Need to set it to the first id
            if (lastPlaneId == 0) lastPlaneId = firstPlaneId;

            for (int j = firstPlaneId; j <= lastPlaneId; j++) {
                Integer planeId = new Integer(j);
                refPlaneList.add(planeId);
                refPlaneAndPoint.put(planeId,
                        new Integer(firstPointId + adj + (j - firstPlaneId)));
            }
            success = true;
        }

        // We are done with the edgeRefPlanes hashtable. So clear it.
        edgeRefPlanes.clear();
        edgeRefPlanes = null;

        return success;
    }

    protected int getFirstRefPoint() {
        // First get all the ref point series. From the last
        // series, get the reference first reference point
        int firstId = 0;
        IntHolder errorCode = new IntHolder(0);
        OI_Part oiPart = getOIPart();
        OI_RefPtSeries[] refSeries = oiPart.GetAllReferencePointSeries(errorCode);
        if (errorCode.value != 0 || refSeries.length <= 0) {
            for (int ii = 0; ii < refSeries.length; ii++)
                server.serverSideRelease(refSeries[ii]);
            server.serverSideRelease(oiPart);
            System.out.println("Cannot retrieve reference series");
            return 0;
        }

        OI_RefPtInSeries[] refPoints =
                refSeries[refSeries.length - 1].GetAllReferencePoints(errorCode);
        if (errorCode.value != 0 || refPoints.length <= 0) {
            for (int ii = 0; ii < refSeries.length; ii++)
                server.serverSideRelease(refSeries[ii]);
            for (int ii = 0; ii < refPoints.length; ii++)
                server.serverSideRelease(refPoints[ii]);
            server.serverSideRelease(oiPart);
            System.out.println("Unable to retrieve ref points");
            return 0;
        }

        firstId = refPoints[0].GetLabel();

        // Release before you leave
        for (int ii = 0; ii < refPoints.length; ii++)
            server.serverSideRelease(refPoints[ii]);
        for (int ii = 0; ii < refSeries.length; ii++)
            server.serverSideRelease(refSeries[ii]);
        server.serverSideRelease(oiPart);

        return firstId;
    }

    /*protected void plotPerimeterPositions() {
        IntHolder errorCode = new IntHolder(0);
        double firstx = 0;
        double firstz = 0;
        OI_Part oiPart = getOIPart();
        OI_RefPtSeries[] refSeries = oiPart.GetAllReferencePointSeries(errorCode);
        if (errorCode.value != 0 || refSeries.length <= 0) {
            for (int ii = 0; ii < refSeries.length; ii++)
                server.serverSideRelease(refSeries[ii]);
            server.serverSideRelease(oiPart);

            System.out.println("Cannot retrieve reference series");
            return;
        }


        for (int j = 0; j < refSeries.length; j++) {
            OI_RefPtInSeries[] refPoints =
                    refSeries[j].GetAllReferencePoints(errorCode);
            if (errorCode.value != 0 || refPoints.length <= 0) {
                for (int ii = 0; ii < refSeries.length; ii++)
                    server.serverSideRelease(refSeries[ii]);
                for (int ii = 0; ii < refPoints.length; ii++)
                    server.serverSideRelease(refPoints[ii]);
                server.serverSideRelease(oiPart);

                System.out.println("Unable to retrieve ref points");
                return;
            }

            for (int i = 0; i < refPoints.length; i++) {
                OI_3DPoint pt = refPoints[i].GetPointCoordinates(errorCode);
                if (errorCode.value != 0) return;

                if (j == 0 && i == 0) {
                    firstx = pt.x;
                    firstz = pt.z;
                }
                sga.addPerimeterDataPoint(pt.x, pt.y, pt.z);
            }
            for (int ii = 0; ii < refPoints.length; ii++)
                server.serverSideRelease(refPoints[ii]);
        }
        for (int ii = 0; ii < refSeries.length; ii++)
            server.serverSideRelease(refSeries[ii]);
        server.serverSideRelease(oiPart);
    }*/

    public boolean measureSealInterference(String bulbPartName) {
        boolean success = true;
        String output = null;
        double area = 0;
        OI_Part bulbOIPart = sga.getOIPart(bulbPartName);
        OI_Part thisPart = getOIPart();
        IntHolder errorCode = new IntHolder(0);
        DoubleHolder rptDist = new DoubleHolder();
        OI_3DPointHolder ptOnFace = new OI_3DPointHolder();

        //Get all the surfaces from the bulb part. After the boolean operation
        //only those pieces of the section surfaces that represent the
        // interference area are left. To measure these, get all the surfaces
        // from the bulb part and find the ones that lie on the corresponding
        // ref plane within a certain distance of the ref point.
        OI_UserFace[] uFaces = bulbOIPart.GetPartModel().
                GetAllUserFaces(errorCode);
        if (errorCode.value != 0) return false;

        int currSectionId = startPoint + 1;

        // Create sections and measure the area
        for (int i = 0; i < refPlaneList.size(); i++) {

            OI_RefPlane currRefPlane = thisPart.GetReferencePlane("RF" +
                    ((Integer) refPlaneList.get(i)).intValue());
            if (currRefPlane == null) {
                System.out.println("Unable to get ref plane " +
                        ((Integer) refPlaneList.get(i)).intValue());
                return false;
            }
            OI_Plane eqn = currRefPlane.GetPlaneEquation(errorCode);

            OI_RefPoint currRefPoint = thisPart.GetReferencePoint(
                    "RF" + ((Integer) refPlaneAndPoint.get(
                            refPlaneList.get(i))).intValue());
            if (currRefPoint == null) {
                System.out.println("Unable to get ref point");
            }
            OI_3DPoint rfPtLoc = currRefPoint.GetPointCoordinates(errorCode);

            for (int kk = 0; kk < uFaces.length; kk++) {
                OI_Face[] faces = uFaces[kk].GetFaces(errorCode);
                if (errorCode.value != 0) {
                    System.out.println("Could not get faces: Part:777");
                    return false;
                }

                //ASSUMPTION: Each of the UFaces has only one face
                // Get the loops->edges->vertices and the first vertex
                // out of the vertices
                OI_Loop[] loops = faces[0].GetLoops(errorCode);
                OI_Edge[] edges = loops[0].GetEdges(errorCode);
                OI_Vertex[] verts = edges[0].GetVertices(errorCode);
                OI_3DPoint vPt = verts[0].GetPoint(errorCode);

                // Calculate the distance between the point and the plane.
                // Any distance less than part tolerance will be treated as 0.
                // Keep the surfaces that lie on the plane as possible
                // candidates for calculating the area.
                // Distance between and pt x1, y1, z1 and
                // a plane Ax + By + Cz + D = 0 is calculated by:
                // Ax1 + By1 + Cz1 + D/sqrt(A**2 + B**2 + C**2)

                double dist = (eqn.a * vPt.x + eqn.b * vPt.y + eqn.c * vPt.z + eqn.d) /
                        (Math.sqrt(Math.pow(eqn.a, 2) + Math.pow(eqn.b, 2) +
                        Math.pow(eqn.c, 2)));
                // Dist is in m convert to mm
                dist *= 1000;
                if (Math.abs(dist) < 0.01) {
                    int err = faces[0].EvaluateMinDistanceFromPoint(rfPtLoc,
                            rptDist, ptOnFace);
                    //System.out.println("CANDIDATE: dist from RefPt: " +
                    //                             rptDist.value);
                    // if this surface is within a nominal distance from the
                    // ref point on the section line, add the surf
                    if (rptDist.value * 1000 < 60)
                        area += faces[0].EvaluateArea(errorCode);
                    // System.out.println(uFaces[kk].GetLabel() + " - Area: " + area);
                }
            }
            // Area is in m**2. To convert it to mm**2
            area *= 1e6;
            // Compare the area to the original area and if they are the same
            // set it to 0;
            //System.out.println("Area calculated: " + area);
            if (Math.abs(((Double) sectionAreas.get(((Integer) refPlaneList.get(i)))).doubleValue() - area) <= 0.001) {
                area = 0.0;
            }
            sga.addAreaData(area);
            currSectionId++;
            area = 0.0;
            //if (isStopRequested()) return false;
        }
        server.serverSideRelease(bulbOIPart);
        server.serverSideRelease(thisPart);
        return success;
    }

    public int getSurfaceIdAtPosition(String partName, int positionID) {
        if (sealXSSurfIds == null) {
            return 0;
        }

        com.sdrc.openideas.OI_GeometryRootPackage.TrackingId faceId = (com.sdrc.openideas.OI_GeometryRootPackage.TrackingId) sealXSSurfIds.get(positionID);
        OI_Part oiPart = sga.getOIPart(partName);
        OI_GeometryRoot geoRoot = oiPart.GetTrackingEntity(faceId);

        int retVal = geoRoot.GetLabel();
        server.serverSideRelease(geoRoot);
        server.serverSideRelease(oiPart);

        return retVal;
    }


    public boolean measureSealGap(String JPartName) {
        boolean success = true;
        int abWFCurve = 0;
        int lastJCurve = 0;
        int firstJCurve = 0;
        int prevWFCurve = 0;
        String command = null;
        String abPartCmd = "/cr spe cr; ug; nn n; co " + name;
        abPartCmd += "; fi; n; gapsurf; okay; gn; 1; don; canc;; ";
        abPartCmd += "op; ot; wf; okay; lab;" + name + "; rfpl; ";

        String jCmd = "/cr spe cr; ug; nn n; co " + JPartName;
        jCmd += "; fi; n; gapsurf; okay; gn; 1; don; canc;; ";
        jCmd += "op; ot; wf; okay; lab;" + name + "; rfpl; ";

        double gapDistance = 0;
        int currSectionId = startPoint + 1;

        // Create sections and measure the area
        for (int i = 0; i < refPlaneList.size(); i++) {
            Thread.yield();
            int currPlaneId = ((Integer) refPlaneList.get(i)).intValue();
            int currRefPoint = ((Integer) refPlaneAndPoint.get(
                    refPlaneList.get(i))).intValue();

            if (i == 0) {
                // I-DEAS returns an error if last ref id for curve is queried
                // and there is no curve in the model file. If n curves were
                // created in the model file and then deleted, the query would
                // still return a null. For the rest of the logic to work right,
                // the id of the last created wf curve is necessary. So to fake
                // I_DEAS out, create a few wf curves, get the last id, then
                // delete the curves.
                prevWFCurve = setLastWFCurveId(name, "gapsurf", currPlaneId);
                if (prevWFCurve == -1) return false;
                firstJCurve = setLastWFCurveId(JPartName, "gapsurf", currPlaneId);
                if (firstJCurve == -1) return false;
            } else {
                prevWFCurve = abWFCurve;
                firstJCurve = lastJCurve;
            }


            firstJCurve++;

            // Create curves on the AB Surface
            command = abPartCmd + "RF" + currPlaneId + "; $";

            server.sendCommandAndWait(command, 0);

            //Now that the wireframe curve was created, get the curve id
            abWFCurve = server.getLastRefId("Z_LAST_WFC");

            // The ref plane would have cut 2 sections in the interference
            // solid since the plane is infinite. To get the wf curves we
            // need, the closest curve to the ref point on which the plane
            // was created needs to be found out. Once the wf curve id is
            // got, a section can be built on it.
            int nearestWFCurve = getNearestCurve(name, currRefPoint,
                    prevWFCurve + 1, abWFCurve);

            // Now create curves on the Door inner (J Part)
            command = jCmd + "RF" + currPlaneId + "; $";
            server.sendCommandAndWait(command, 0);


            // Get the curve Id of the last curve created
            lastJCurve = server.getLastRefId("Z_LAST_WFC");

            // From the curve on AB surface, find out the distance to all
            // the curves on the j surface. The min of the distance
            // will be the seal gap distance
            gapDistance = getMinDistance(JPartName, nearestWFCurve,
                    firstJCurve, lastJCurve);

            // plot it
            //sga.addDistPlotDataPoint(currSectionId, gapDistance);
            currSectionId++;

            // Check the stop status in the analyzer. If the user requests a
            // a stop, put up a gui asking if the user wants to stay or leave
            //if (isStopRequested()) return false;
        }
        return success;
    }

    protected int getNearestCurve(String partName, int refPoint,
                                  int firstCurve, int lastCurve) {
        int nearestCurve = 0;
        double minDist = 1e5;
        int errorCode = 0;
        IntHolder ptErr = new IntHolder(0);
        DoubleHolder distance = new DoubleHolder(0.0);
        OI_3DPointHolder curPt = new OI_3DPointHolder();

        // Get the bulb part from the driver. From this we get the WF Curve and
        // evaluate the min distance from the curve to the other curve
        OI_Part oiThisPart = getOIPart();
        OI_Part oiOtherPart = null;
        if (partName.equals(name))
            oiOtherPart = oiThisPart;
        else
            oiOtherPart = sga.getOIPart(partName);

        //Get the reference Point
        String refName = "RF" + refPoint;
        OI_RefPoint refPt = oiThisPart.GetReferencePoint(refName);
        OI_3DPoint ref3D = refPt.GetPointCoordinates(ptErr);

        // Get the feature model and the curves from the feature model
        OI_FeatureModel featModel = oiOtherPart.GetPartModel();
        OI_WFCurve wfCurve = null;

        for (int i = firstCurve; i <= lastCurve; i++) {
            wfCurve = featModel.GetWFCurve(i);
            errorCode = wfCurve.EvaluateMinDistanceFromPoint(ref3D,
                    distance, curPt);
            if (errorCode == 0) {
                if (distance.value <= minDist) {
                    minDist = distance.value;
                    nearestCurve = i;
                }
            } else {
                System.out.println("Error getting min dist: " + errorCode);
            }

            server.serverSideRelease(wfCurve);
        }
        distance = null;
        curPt = null;

        // convert to mm
        minDist *= 1000;
        if (minDist > 50) nearestCurve = 0;

        server.serverSideRelease(refPt);
        server.serverSideRelease(featModel);
        server.serverSideRelease(oiThisPart);
        if (!partName.equals(name)) server.serverSideRelease(oiOtherPart);
        return nearestCurve;
    }

    protected int setLastWFCurveId(String partName, String groupName, int planeId) {
        int lastCurveId = 0;
        String cmd = "$; $; /cr spe cr; rel s; ug; nn n; co " + partName;
        cmd += "; fi; n; ";
        cmd += groupName + "; okay; gn; 1; don; canc; don; ";
        cmd += "op; ot; wf; okay; lab;";
        cmd += name + "; rfpl; ";
        cmd += "RF" + planeId + "; $";

        server.sendCommandAndWait(cmd, 0);

        //Now that the wireframe curve was created, get the curve id and
        //create section on it
        lastCurveId = server.getLastRefId("Z_LAST_WFC");
        if (lastCurveId == 0 || lastCurveId == -1)
            lastCurveId = -1;

        // Delete all the curves created here
        cmd = "/del; lab " + partName + "; C; ALL; DON; YES; $";
        server.sendCommandAndWait(cmd, 0);

        return lastCurveId;
    }

    private double getMinDistance(String partName, int abWFCurve,
                                  int firstJCurve, int lastJCurve) {
        double minDist = 1e5;
        int errorCode = 0;
        DoubleHolder distance = new DoubleHolder(0.0);
        OI_3DPointHolder curPt = new OI_3DPointHolder();
        OI_3DPointHolder objPoint = new OI_3DPointHolder();

        // Get the bulb part from the driver. From this we get the WF Curve and
        // evaluate the min distance from the curve to the other curve
        OI_Part oiThisPart = getOIPart();
        OI_Part oiOtherPart = null;
        if (partName.equals(name))
            oiOtherPart = oiThisPart;
        else
            oiOtherPart = sga.getOIPart(partName);

        OI_FeatureModel thisFeatModel = oiThisPart.GetPartModel();
        OI_FeatureModel otherFeatModel = oiOtherPart.GetPartModel();

        // Get the WF Curve for AB WF curve
        OI_WFCurve abCurve = thisFeatModel.GetWFCurve(abWFCurve);
        OI_WFCurve jCurve = null;

        for (int i = firstJCurve; i <= lastJCurve; i++) {
            // Get the WF Curve for the curve on the j part
            jCurve = otherFeatModel.GetWFCurve(i);

            // Get the distance
            errorCode = abCurve.EvaluateMinDistanceFromObject(jCurve,
                    distance, curPt, objPoint);

            // If no error or multiple solutions, use the value
            if (errorCode == 0 || errorCode == 66316) {
                if (distance.value <= minDist) {
                    minDist = distance.value;
                }
            }
            server.serverSideRelease(jCurve);
        }
        if (minDist == 1e5) minDist = 0.0;
        distance = null;
        curPt = null;
        objPoint = null;
        // We get the min dist in meters, convert it to mm
        minDist *= 1000;

        // Release everything that we have gotten here
        server.serverSideRelease(abCurve);
        server.serverSideRelease(thisFeatModel);
        server.serverSideRelease(otherFeatModel);
        server.serverSideRelease(oiThisPart);
        if (!partName.equals(name)) server.serverSideRelease(oiOtherPart);
        return minDist;
    }

    // Function creates cuts the seal bulb at the cut planes and then
    // creates surfaces on those cut planes.
    public boolean createSealCutSections(String BulbPartName) {
        boolean success = true;
        int abWFCurve = 0;
        int lastSealCurve = 0;
        int firstSealCurve = 0;
        int prevWFCurve = 0;
        String command = null;
        String sealCmd = "/cr spe cr; ug; nn n; co " + BulbPartName;
        sealCmd += "; fi; n; outersurf; okay; gn; 1; don; canc;; ";
        sealCmd += "op; ot; wf; okay; lab;" + name + "; rfpl; ";

        double gapDistance = 0;

        // Create sections and measure the area
        for (int i = 0; i < refPlaneList.size(); i++) {
            Thread.yield();
            int currPlaneId = ((Integer) refPlaneList.get(i)).intValue();
            int currPointId = ((Integer) refPlaneAndPoint.get(
                    refPlaneList.get(i))).intValue();

            if (i == 0) {
                // I-DEAS returns an error if last ref id for curve is queried
                // and there is no curve in the model file. If n curves were
                // created in the model file and then deleted, the query would
                // still return a null. For the rest of the logic to work right,
                // the id of the last created wf curve is necessary. So to fake
                // I_DEAS out, create a few wf curves, get the last id, then
                // delete the curves.
                firstSealCurve = setLastWFCurveId(BulbPartName, "outersurf",
                        currPlaneId);
                if (firstSealCurve == -1) {
                    System.out.println("Invalid firstcurve ID");
                    return false;
                }
            } else {
                firstSealCurve = lastSealCurve;
            }

            firstSealCurve++;
            // Now create curves on the Door inner (J Part)
            command = sealCmd + "RF" + currPlaneId + "; $";
            server.sendCommandAndWait(command, 0);

            // Get the curve Id of the last curve created
            lastSealCurve = server.getLastRefId("Z_LAST_WFC");

            // From the curve on AB surface, find out the distance to all
            // the curves on the sealbulb. The min of the distance
            // will be the curve that we are looking for
            if ((lastSealCurve != 0)) {

                // The ref plane would have cut 2 sections in the interference
                // solid since the plane is infinite. To get the wf curves we
                // need, the closest curve to the ref point on which the plane
                // was created needs to be found out. Once the wf curve id is
                // got, a section can be built on it.
                int nearestWFCurve = getNearestCurve(BulbPartName, currPointId,
                        firstSealCurve, lastSealCurve);

                if (nearestWFCurve != 0) {
                    // Once the last section is created, create a surface on the
                    // wf section using section by boundary
                    String Cmd = "/cr ps; lab " + BulbPartName +
                            "; C; " + nearestWFCurve + "; don; $";
                    server.sendCommandAndWait(Cmd, 0);
                } else {
                    sga.printMessage("Cannot get nearest curve");
                    return false;
                }
            }
            //sga.addProgress();
        }

        // Delete all the outer surfaces of the seal bulb at this point. Since
        // a boolean operation is going to be done at a later stage, only the
        // seal section surfaces need to be kept. Using all the surfaces will
        // make the cut operation less reliable and will take considerably more
        // time to perform the operation.
        String delCommand = "/del; lab " + BulbPartName + "; S; ug; nn n; co " +
                BulbPartName + "; fi; n; outersurf ; ok; gn 1; " +
                "don; canc; don; $";
        server.sendCommandAndWait(delCommand, 0);

        // Store the surface areas of the sections so that the trimmed surface
        // can be checked against this.
        double area = 0;
        OI_Part bulbOIPart = sga.getOIPart(BulbPartName);
        OI_Part thisPart = sga.getOIPart(name);
        IntHolder errorCode = new IntHolder(0);
        DoubleHolder rptDist = new DoubleHolder();
        OI_3DPointHolder ptOnFace = new OI_3DPointHolder();

        //Get all the surfaces from the bulb part. After the boolean operation
        //only those pieces of the section surfaces that represent the
        // interference area are left. To measure these, get all the surfaces
        // from the bulb part and find the ones that lie on the corresponding
        // ref plane within a certain distance of the ref point.
        OI_UserFace[] uFaces = bulbOIPart.GetPartModel().
                GetAllUserFaces(errorCode);
        if (errorCode.value != 0) return false;

        sectionAreas = new Hashtable();
        boolean found = false;

        // Create sections and measure the area
        for (int i = 0; i < refPlaneList.size(); i++) {
            int currPlaneId = ((Integer) refPlaneList.get(i)).intValue();
            int currPointId = ((Integer) refPlaneAndPoint.get(
                    refPlaneList.get(i))).intValue();
            OI_RefPlane currRefPlane = thisPart.GetReferencePlane("RF" +
                    currPlaneId);
            if (currRefPlane == null) {
                System.out.println("Unable to get ref plane " + currPlaneId);
                return false;
            }
            OI_Plane eqn = currRefPlane.GetPlaneEquation(errorCode);

            OI_RefPoint currRefPoint = thisPart.GetReferencePoint(
                    "RF" + currPointId);
            if (currRefPoint == null) {
                System.out.println("Unable to get ref point");
            }
            OI_3DPoint rfPtLoc = currRefPoint.GetPointCoordinates(errorCode);

            found = false;
            for (int kk = 0; kk < uFaces.length; kk++) {
                OI_Face[] faces = uFaces[kk].GetFaces(errorCode);
                if (errorCode.value != 0) {
                    System.out.println("Could not get faces: Part:777");
                    return false;
                }

                //ASSUMPTION: Each of the UFaces has only one face
                // Get the loops->edges->vertices and the first vertex
                // out of the vertices
                OI_Loop[] loops = faces[0].GetLoops(errorCode);
                OI_Edge[] edges = loops[0].GetEdges(errorCode);
                OI_Vertex[] verts = edges[0].GetVertices(errorCode);
                OI_3DPoint vPt = verts[0].GetPoint(errorCode);

                // Calculate the distance between the point and the plane.
                // Any distance less than part tolerance will be treated as 0.
                // Keep the surfaces that lie on the plane as possible
                // candidates for calculating the area.
                // Distance between and pt x1, y1, z1 and
                // a plane Ax + By + Cz + D = 0 is calculated by:
                // Ax1 + By1 + Cz1 + D/sqrt(A**2 + B**2 + C**2)

                double dist = (eqn.a * vPt.x + eqn.b * vPt.y + eqn.c * vPt.z + eqn.d) /
                        (Math.sqrt(Math.pow(eqn.a, 2) + Math.pow(eqn.b, 2) +
                        Math.pow(eqn.c, 2)));
                // Dist is in m convert to mm
                dist *= 1000;
                area = 0.0;
                if (Math.abs(dist) < 0.01) {
                    int err = faces[0].EvaluateMinDistanceFromPoint(rfPtLoc,
                            rptDist, ptOnFace);
                    // if this surface is within a nominal distance from the
                    // ref point on the section line, add the surf
                    if (rptDist.value * 1000 < 60) {
                        //area = faces[0].EvaluateArea(errorCode);
                        //if(errorCode.value != 0 )
                        //             System.out.println("Bad ErrorCode");
                        String arCmd = "/li me cha sua; lab " + BulbPartName +
                                "; " + uFaces[kk].GetLabel() +
                                "; don; don; $ $ $";
                        String arOP = server.sendCommandAndWait(arCmd);
                        String sArea = Parser.parseAreaOutput(arOP);
                        if (sArea == null) sArea = "0.0";
                        //System.out.println("Parsed area output " + sArea);
                        try {
                            area = Double.parseDouble(sArea);
                        } catch (Exception ex) {
                            System.out.println("Number Format Exception while" +
                                    " calculating area");
                            area = 0.0;
                        }
                        //System.out.println("Calculated Area = " + area);
                        found = true;
                    }
                }
                // release all that you dont want
                for (int mm = 0; mm < verts.length; mm++)
                    server.serverSideRelease(verts[mm]);
                for (int mm = 0; mm < edges.length; mm++)
                    server.serverSideRelease(edges[mm]);
                for (int mm = 0; mm < loops.length; mm++)
                    server.serverSideRelease(loops[mm]);
                for (int mm = 0; mm < faces.length; mm++)
                    server.serverSideRelease(faces[mm]);

                if (found) break;
            }
            if (!found) {
                sga.printMessage("**************** WARNING ****************");
                sga.printMessage("Section creation failed at point " + (i + 1));
                sga.printMessage("This failure possibly happened due to bad " +
                        "seal bulb geometry.");
            }
            // Area is in m**2. To convert it to mm**2
            //area *= 1e6;
            //System.out.println(currPlaneId + "Plane - Area " + area);
            sectionAreas.put(new Integer(currPlaneId), new Double(area));
            area = 0.0;
        }

        for (int kk = 0; kk < uFaces.length; kk++)
            server.serverSideRelease(uFaces[kk]);
        server.serverSideRelease(bulbOIPart);
        server.serverSideRelease(thisPart);
        return success;
    }

    public int createSurfaceOnBulbSection(String bulbName, int wfCurve,
                                          boolean createGroup) {
        int surfaceId = 0;

        //To get the id of the surface created, we have to do it at an
        //indirect way. Start by putting all the surfaces in the bulb part in
        //an entity group. Then create the new surface. The surface that is not
        //a part of the the surface group created earlier, that is new face
        // created.

        //If this is the first time around, create the surface group.
        String grpName = "AllSealBulbFacesForCheck";
        if (createGroup)
            if (!createAllSurfaceGroup(bulbName, grpName)) {
                System.out.println("Cannot create surface group");
                return 0;
            }

        // Now that the group was created, create the surface
        String Cmd = "/cr bo; op; au y; st n; pl y; g1 n; ok; lab "
                + bulbName + "; C; " + wfCurve + "; don; y";

        server.sendCommandAndWait(Cmd, 0);

        // Get the surface id
        Vector surfIds = getSurfacesNotInGroup(bulbName, grpName);

        if (surfIds == null) return 0;
        if (surfIds.size() > 1) {
            System.out.println("Invalid number of faces created at section");
            return 0;
        }

        surfaceId = ((Integer) surfIds.get(0)).intValue();
        addUserFaceToGroup(bulbName, surfaceId, grpName);

        //Get the tracking Id of the surfaces and store it as they are liable
        //to change on you
        if (sealXSSurfIds != null) {
            IntHolder errorCode = new IntHolder(0);
            OI_Part oiPart = sga.getOIPart(bulbName);
            OI_UserFace uFace = oiPart.GetPartModel().GetUserFace(surfaceId);

            com.sdrc.openideas.OI_GeometryRootPackage.TrackingId faceId =
                    uFace.TrackEntity(com.sdrc.openideas.OI_UserFacePackage.TrackingType.
                    E_OuterBoundary, errorCode);
            if (errorCode.value != 0) {
                server.serverSideRelease(uFace);
                server.serverSideRelease(oiPart);
                System.out.println("Cannot track face, aborting");
                return 0;
            }

            sealXSSurfIds.add(faceId);
            server.serverSideRelease(uFace);
            server.serverSideRelease(oiPart);
        }

        return surfaceId;
    }

    private boolean orderEdgesClockwise() {
        // Get 3 points on the edge based on the current direction.
        // Create 2 vectors from the 3 points and get a 3rd vector that is a
        // cross product of the 2 vectors. If this cross product vector has a
        // +ve y value, the edges are clockwise, do nothing. Else the edges
        // are anticlockwise and reorder the edgeloop list, the flags and the
        // vertices vectors.
        // ASSUMPTION: Clockwise is determined from inside the car, looking at
        // a passenger door.

        // Get 2 vectors;
        IntHolder errorCode = new IntHolder(0);
        Vector3d vec1 = null;
        Vector3d vec2 = null;
        OI_Part part = getOIPart();
        OI_3DPoint firstPoint = null;
        OI_3DPoint secondPoint = null;
        OI_3DPoint thirdPoint = null;
        int firstInd = 0;
        int secondInd = 1;
        if (edgeLoopList.size() <= 3) {
            if (((Integer) (uEdgeFlags.get(0))).intValue() == 1) {
                firstInd = 1;
            }
            if (((Integer) (uEdgeFlags.lastElement())).intValue() == 0)
                secondInd = 0;
            //Get the edge and evaluate the mid point for the second point
            int edgeId = ((Integer) (edgeLoopList.get(0))).intValue();
            OI_Curve curv = part.GetPartModel().GetUserEdge(edgeId).GetCurve();
            secondPoint = curv.EvaluatePointOn(50.0, errorCode);
            server.serverSideRelease(curv);
        } else {
            // Get the last vertex of the first edge
            if (((Integer) (uEdgeFlags.get(0))).intValue() == 0) {
                firstInd = 1;
            }
            if (((Integer) (uEdgeFlags.lastElement())).intValue() == 1)
                secondInd = 0;

            // For this case, the mid pt is either the start or the end
            // vertex of the middle Id somewhere.
            int midEdge = edgeLoopList.size() / 2;
            int vertId = ((Integer) ((Vector) (uEdgeVerts.get(edgeLoopList.
                    get(midEdge)))).get(firstInd)).intValue();
            OI_Vertex vert = part.GetPartModel().GetVertex(vertId);
            secondPoint = vert.GetPoint(errorCode);
            server.serverSideRelease(vert);
        }
        int vertId =
                ((Integer) ((Vector) (uEdgeVerts.get(edgeLoopList.get(0)))).
                get(firstInd)).intValue();
        OI_Vertex vert = part.GetPartModel().GetVertex(vertId);
        firstPoint = vert.GetPoint(errorCode);

        server.serverSideRelease(vert);

        vertId =
                ((Integer) ((Vector) (uEdgeVerts.get(edgeLoopList.lastElement()))).
                get(secondInd)).intValue();
        vert = part.GetPartModel().GetVertex(vertId);
        thirdPoint = vert.GetPoint(errorCode);
        server.serverSideRelease(vert);
        server.serverSideRelease(part);

        // Create the vectors
        vec1 = new Vector3d(secondPoint.x - firstPoint.x,
                secondPoint.y - firstPoint.y,
                secondPoint.z - firstPoint.z);
        vec2 = new Vector3d(thirdPoint.x - secondPoint.x,
                thirdPoint.y - secondPoint.y,
                thirdPoint.z - secondPoint.z);

        // Get the cross product vector
        Vector3d crossVec = new Vector3d();
        crossVec.cross(vec1, vec2);

        if (crossVec.y > 0) {
            return true;
        } else if (crossVec.y < 0) {
            // Flip the edges and the other vectors. The edge flags have to
            // be flipped around too.
            Vector newEdgeList = new Vector();
            Vector newEdgeFlags = new Vector();
            for (int ii = edgeLoopList.size() - 1; ii >= 0; ii--) {
                newEdgeList.add(edgeLoopList.get(ii));
                int flag = ((Integer) (uEdgeFlags.get(ii))).intValue();
                if (flag == 0)
                    flag = 1;
                else if (flag == 1) flag = 0;
                newEdgeFlags.add(new Integer(flag));
            }
            edgeLoopList = newEdgeList;
            uEdgeFlags = newEdgeFlags;
        } else // case when it is 0
        {
            //Additional processing is required to check what to do
            // not going to worry about it now.
            System.out.println("Cannot determine clockwise direction");
            return false;
        }
        return true;
    }

    public void setStartXS(int startVal) {
        startPoint = startVal - 1;
        rotnPoint = (startVal + stopPoint + 1) / 2;
    }

    public void setStopXS(int stopVal) {
        stopPoint = stopVal - 1;
        rotnPoint = (startPoint + stopVal + 1) / 2;
    }

    public int getStartXS() {
        return startPoint;
    }

    public int getStopXS() {
        return stopPoint;
    }

    public void setRotnPt(int rotn) {
        rotnPoint = rotn;
    }

    public void setDeflection(double defl) {
        deflection = defl;
    }

    public boolean deflectDoor(String JPartName, boolean isBodyMount) {
        IntHolder errorCode = new IntHolder(0);
        // Create a reference line from start point to stop point.
        OI_Part thisPart = getOIPart();
        OI_RefPoint startRefPoint = thisPart.GetReferencePoint(
                "RF" + ((Integer) refPlaneAndPoint.get(
                        refPlaneList.get(0))).intValue());
        OI_3DPoint startCoord = startRefPoint.GetPointCoordinates(errorCode);

        OI_RefPoint stopRefPoint = thisPart.GetReferencePoint(
                "RF" + ((Integer) refPlaneAndPoint.get(
                        refPlaneList.get(refPlaneList.size() - 1))).intValue());
        OI_3DPoint stopCoord = stopRefPoint.GetPointCoordinates(errorCode);

        OI_3DPoint[] endPts = new OI_3DPoint[2];
        endPts[0] = startCoord;
        endPts[1] = stopCoord;

        OI_WFCurve[] line = thisPart.CreateWFPolyline(endPts, errorCode);
        if (line == null) return false;

        // measure the distance from the line to the section point
        OI_RefPoint rotnRefPoint = thisPart.GetReferencePoint(
                "RF" + ((Integer) refPlaneAndPoint.get(
                        refPlaneList.get(rotnPoint - 1 - startPoint))).intValue());
        OI_3DPoint rotnCoord = rotnRefPoint.GetPointCoordinates(errorCode);

        OI_3DPointHolder ptOnCur = new OI_3DPointHolder();
        DoubleHolder dist = new DoubleHolder();

        int error = line[0].EvaluateMinDistanceFromPoint(rotnCoord,
                dist, ptOnCur);
        double rotnAng = Math.toDegrees(Math.atan(deflection / (dist.value * 1000)));
        if (isBodyMount) rotnAng = 0 - rotnAng;

        // Now reorient the part
        String Cmd = "/or r; lab " + JPartName + "; pt; don; lab " + name +
                "; rfpt;" + startRefPoint.GetName(errorCode) + "; V; lab " +
                name + "; C; " + line[0].GetLabel() + "; yes; " +
                rotnAng + "; don; $";

        server.sendCommandAndWait(Cmd, 0);

        // if reached this point, everything must have gone on smoothly
        return true;
    }
}
