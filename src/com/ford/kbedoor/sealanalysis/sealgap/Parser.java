package com.ford.kbedoor.sealanalysis.sealgap;

import javax.vecmath.Vector3d;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class to parse defaults/session files.
 * eventually this should be done in XML
 * to have a standard data storage scheme.
 */
public class Parser {
    private FileReader reader = null;
    private BufferedReader bf = null;
    private char commentChar = '#';
    private String delimiter = ",";

    public Parser(FileReader reader) {
        bf = new BufferedReader(reader);
    }

    public static Vector split(String s, String delimiter) {
        Vector v = new Vector();

        s.trim();
        StringTokenizer st = new StringTokenizer(s, delimiter);
        while (st.hasMoreTokens())
            v.addElement(st.nextToken());

        return v;
    }

    public Vector readOffsets() {
        Vector v = null;
        Vector offsets = new Vector();
        String s = null;

        try {
            // Read one line and parse it into a parameter
            while ((s = bf.readLine()) != null) {
                //if line starts with a comment char, ignore
                if (s.charAt(0) != commentChar) {
                    v = split(s, ",");
                    //String representation of a parameter
                    //Description, Symbol, Value, Units
                    if (v.size() == 3 || v.size() == 4) {
                        // Check if the value is a double or string
                        // and call the appropriate constructor.
                        try {
                            Double d = new Double((String) v.elementAt(0));
                            double xValue = d.doubleValue();
                            d = new Double((String) v.elementAt(1));
                            double yValue = d.doubleValue();
                            d = new Double((String) v.elementAt(2));
                            double zValue = d.doubleValue();
                            if (v.size() == 4) {
                                Boolean b = new Boolean(((String) v.elementAt(3)).trim());
                                boolean bValue = b.booleanValue();
                                offsets.addElement(new Offset(xValue, yValue,
                                        zValue, bValue));
                            } else
                                offsets.addElement(new Offset(xValue, yValue, zValue));
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid entry in the defaults file");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
        return offsets;

    }

    public Vector readSTLFile() {
        Vector v = null;
        String s = null;
        Enumeration e = null;
        Hashtable vertices = new Hashtable();
        Vector nodes = new Vector();
        Vector3d vertex = null;

        try {
            while ((s = bf.readLine()) != null) {
                if (s.startsWith("vertex", 7)) {
                    if (!vertices.containsKey(s)) {
                        v = split(s, " ");
                        vertex = new Vector3d(Double.parseDouble((String) v.get(1)),
                                Double.parseDouble((String) v.get(2)),
                                Double.parseDouble((String) v.get(3)));
                        vertices.put(s, vertex);
                        nodes.addElement(vertex);
                    }

                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
        return nodes;

    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String s) {
        delimiter = s;
    }

    public char getCommentChar() {
        return commentChar;
    }

    public void setCommentChar(char c) {
        commentChar = c;
    }


    public static Vector parseEdgeOutput(String output) {
        String line;
        String prevLine = null;
        String key, value;
        Vector edges = new Vector();
        Edge edge = null;

        String partName = null, type = null, color = null;
        int id = 0;
        double length = 0;
        boolean createEdge = false;

        // Split output into multiple lines
        StringTokenizer st = new StringTokenizer(output, "\n");

        // Parse Each line
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            System.out.println("Edge Line : " + line);
            if (line.length() < 5)
                continue;

            if (line.equals(prevLine)) continue;

            StringTokenizer st1 = new StringTokenizer(line, ":");
            key = (st1.nextToken()).trim();
            if (st1.hasMoreTokens()) {
                value = (st1.nextToken()).trim();
            } else
                continue;

            try {
                if (key.equals("Part Name"))
                    partName = value;
                if (key.equals("Edge Id"))
                    id = Integer.parseInt(value);
                if (key.equals("Edge Type"))
                    type = value;
                if (key.equals("Color"))
                    color = value;
                if (key.equals("Length")) {
                    length = Double.parseDouble(value);
                    createEdge = true;
                }

                if (createEdge) {
                    edge = new Edge(partName, id, type, color, length);
                    edges.add(edge);
                    createEdge = false;
                }
                prevLine = line;
            } catch (NumberFormatException e) {
                System.out.println("Error Parsing line : " + line);
            }


        }
        return edges;
    }

    public static Vector parseFaceOutput(String output) {
        String line;
        String prevLine = null;
        String key, value;
        Vector faces = new Vector();
        Face face = null;

        String partName = null, type = null, color = null, sType = null;
        int id = 0;
        boolean createFace = false;

        // Split output into multiple lines
        StringTokenizer st = new StringTokenizer(output, "\n");

        // Parse Each line
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            //System.out.println("Face Line : " + line);
            if (line.length() < 5)
                continue;
            if (line.equals(prevLine)) continue;

            StringTokenizer st1 = new StringTokenizer(line, ":");
            key = (st1.nextToken()).trim();
            if (st1.hasMoreTokens()) {
                value = (st1.nextToken()).trim();
                //System.out.println("Key: " +key + "   Value " + value);
            } else
                continue;

            try {
                if (key.equals("Part Name"))
                    partName = value;
                if (key.equals("Surface Id"))
                    id = Integer.parseInt(value);
                if (key.equals("Face Type"))
                    type = value;
                if (key.equals("Color"))
                    color = value;
                if (key.equals("Surface Type")) {
                    sType = value;
                    createFace = true;
                }

                if (createFace) {
                    face = new Face(partName, id, type, color, sType);
                    faces.add(face);
                    createFace = false;
                }
                prevLine = line;
            } catch (NumberFormatException e) {
                System.out.println("Error Parsing line : " + line);
            }


        }
        line = null;
        prevLine = null;
        return faces;
    }

    public static String parsePropertyOutput(String output) {
        String area = null;
        String line = null;
        String prevLine = null;
        String key = null;

        StringTokenizer st = new StringTokenizer(output, "\n");
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            if (line.equals(prevLine)) continue;
            StringTokenizer st1 = new StringTokenizer(line, ":");

            if (st1.countTokens() > 1) {
                key = (st1.nextToken()).trim();
                if (key.equals("Area")) {
                    area = (st1.nextToken()).trim();
                    return area;
                }
            }
            prevLine = line;
        }
        return area;
    }

    public static String parseAreaOutput(String output) {
        String area = null;
        String line = null;
        String prevLine = null;
        String key = null;

        StringTokenizer st = new StringTokenizer(output, "\n");
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            if (line.equals(prevLine)) continue;
            StringTokenizer st1 = new StringTokenizer(line, ":");

            if (st1.countTokens() > 1) {
                key = (st1.nextToken()).trim();
                if (key.equals("Total Area")) {
                    StringTokenizer st2 =
                            new StringTokenizer((st1.nextToken()).trim(), "=");
                    if ((st2.nextToken()).trim().equals("TSU1"))
                        area = (st2.nextToken()).trim();
                    return area;
                }
            }
            prevLine = line;
        }
        return area;
    }


    public static String parseRollBackOutput(String output) {
        String featName = null;
        String line = null;
        String prevLine = null;
        String key = null;

        StringTokenizer st = new StringTokenizer(output, "\n");
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            if (line.equals(prevLine)) continue;
            //System.out.println("Line : " + line );
            StringTokenizer st1 = new StringTokenizer(line, ":");
            if (st1.countTokens() > 1) {
                key = (st1.nextToken()).trim();
                //System.out.println("Key :" + key);
                if (key.equals("Rolling back to feature")) {
                    featName = (st1.nextToken()).trim();
                    //System.out.println("Parser : FeatureName : " + featName);
                    return featName;
                }
            }
            prevLine = line;
        }
        System.out.println("Cannot find feature name in output");
        return featName;
    }

    public static boolean parseConstructOutput(String output) {
        String line = null;

        // Split output into multiple lines
        StringTokenizer st = new StringTokenizer(output, "\n");

        // Parse Each line
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            if (line.equals("Construct operation failed"))
                return false;
        }
        return true;
    }

    public static String parseMeasureOutput(String output) {
        String dist = null;
        String line = null;
        String key = null;
        String prevLine = null;

        StringTokenizer st = new StringTokenizer(output, "\n");
        while (st.hasMoreTokens()) {
            line = st.nextToken();
            if (line.equals(prevLine)) continue;
            StringTokenizer st1 = new StringTokenizer(line, ":");

            if (st1.countTokens() > 1) {
                key = (st1.nextToken()).trim();
                if (key.equals("Total")) {
                    StringTokenizer st2 =
                            new StringTokenizer((st1.nextToken()).trim(), "=");
                    st2.nextToken();
                    dist = (st2.nextToken()).trim();
                    break;
                }
            }
            prevLine = line;
            st1 = null;
        }
        st = null;
        line = null;
        key = null;
        output = null;
        prevLine = null;
        return dist;
    }

    // The following functions are purely for the reading the inputs stored in
    // a file
    public String getPropertyValue(String prop) {
        Vector v = null;
        String s = null;
        String ret = null;
        try {
            // Read one line and parse it into a parameter
            while ((s = bf.readLine()) != null) {
                //if line starts with a comment char, ignore
                if (s.charAt(0) != commentChar) {
                    if (s.startsWith(prop)) {
                        // Find the last index of prop and get a substring after
                        // that
                        ret = s.substring(prop.length());
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }

        return ret;
    }

    public Vector getOffsetData() {
        Vector offs = null;
        Vector v = null;
        String s = null;
        try {
            // Read one line and parse it into a parameter
            while ((s = bf.readLine()) != null) {
                //if line starts with a comment char, ignore
                if (s.charAt(0) != commentChar) {
                    v = split(s, " ");
                    if (((String) (v.elementAt(0))).equals("Offsets")) {
                        offs = readOffsets();
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }

        return offs;
    }

}
