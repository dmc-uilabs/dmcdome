package mit.cadlab.dome3.search.framework.utils.processing;

import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.search.datastructure.graph.NumberArrayList;
import mit.cadlab.dome3.search.datastructure.graph.AttributedNode;
import mit.cadlab.dome3.search.datastructure.graph.SimpleARG;
import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.UnitAnalyzer;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Nov 28, 2005
 * Time: 11:41:09 PM
 * To change this template use Options | File TemplateRegistry.
 */
public class DomeXMLHandler implements FileHandler {
    public InterfaceData getInstanceData(File f) throws FileHandlerException {
        InterfaceData data = null;
        try {
            data = loadInterface(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    public static final String FIELD_UNIT_STR = "unitStr";
    public static final String FIELD_UNIT_NUM = "unitNum";
    public static final String FIELD_UNIT_SCALE = "scaleNum";
    public static final String FIELD_LOCAION = "location";

    public static final String FIELD_INTERFACE_NAME = "ifaceName";
    public static final String FIELD_INTERFACE_ID = "ifaceId";
    public static final String FIELD_PARAM_NAME = "paramName";
    public static final String FIELD_DATATYPE = "datatype";
    public static final String FIELD_DIMENSIONLIST = "dimensionlist";

    //private InterfaceData data;

    //private HashMap param_node_map = new HashMap();
    public static final String INTERFACE = "modelinterface";
    // private HashMap modelobjectsMap = new HashMap();      //key is id, value is a node object created to hold interested values.
    // private DSet inputs=new DSet();
    //private DSet outputs=new DSet();

    public InterfaceData loadInterface(File file) throws IOException {
        // read from file : in this case, model name might not be available.
        if (!file.exists()) return null;
        String xmlString = FileUtils.readTextFileAsString(file);
        Element xmlElement = XMLUtils.stringToXmlElement(xmlString);
        InterfaceData data = loadInterface(xmlElement);
        data.setLocation(file.getAbsolutePath());
        return data;
    }


    public InterfaceData loadInterface(Element interfaceXML) throws IOException {
        InterfaceData data;
        // parse XML for information
        String interfaceName = "";
        String interfaceId = ""; //build id, to be used when locating the interface

        if (interfaceXML.getQName().getName().equals(INTERFACE)) {
            interfaceId = interfaceXML.attributeValue("id");
            if (interfaceId == null)
                throw new IllegalArgumentException(" - no xml id");
            data = new InterfaceData(interfaceId);
            interfaceName = interfaceXML.attributeValue("name");
            if (interfaceName == null || interfaceName.equalsIgnoreCase(""))
                throw new IllegalArgumentException(" - no xml name");

            data.setIfacename(interfaceName);
            //   this.version = (String) ((Element) interfaceXML.selectSingleNode("/modelinterface/interfaceinfo/version")).getData();
            if ((interfaceXML.selectSingleNode("/modelinterface/documentation") != null)) {
                data.setDescription((interfaceXML.selectSingleNode("/modelinterface/documentation/text")).getText().trim());
            }


            List paramXML = interfaceXML.selectNodes("/modelinterface/parameters/parameter");

            HashMap modelobjectsMap = loadParameters(paramXML);

            //Now figure out all input output relationship here
            //for DOMEXML files, can get input/output from the xml file
            //for general purposes, can manually create input/output list for these parameters

            DSet[] inputs_outputs = getInputOutputs(interfaceXML,modelobjectsMap);
            DSet inputs = inputs_outputs[0];
            DSet outputs = inputs_outputs[1];

            if (modelobjectsMap == null || inputs == null || outputs == null) return null;
            SimpleARG graph = createGraph(interfaceXML,modelobjectsMap, inputs, outputs);
            if (graph != null) data.setGraph(graph);
            NumberArrayList dimList = getDimensionList(modelobjectsMap);
            if (dimList != null) data.setDimensionList(dimList);
            return data;
        }
        return null;
    }

    private NumberArrayList getDimensionList(HashMap modelobjectsMap) {
        NumberArrayList dimList = new NumberArrayList();
        for (Iterator iterator = modelobjectsMap.values().iterator(); iterator.hasNext();) {
            DomeInterfaceParameter p = (DomeInterfaceParameter) iterator.next();
            if (p.getDimension() != null) dimList.insert(p.getDimension());
        }
        if (dimList.size() > 0) return dimList;
        return null;
    }

    private HashMap loadParameters(List paramXML) {
        HashMap paramHashMap = new HashMap();

        for (Iterator iterator = paramXML.iterator(); iterator.hasNext();) {
            Element paramElement = (Element) iterator.next();
            XMLUtils.makeRootElement(paramElement);
            String paramId = paramElement.attributeValue("id");
            DomeInterfaceParameter param = new DomeInterfaceParameter(paramId);

            String paramName = paramElement.attributeValue("name");
            param.setName(paramName);

            String paramType = ((Element) paramElement.selectSingleNode("/parameter/" + "currentType")).attributeValue("value");
            param.setDataType(paramType);


            if (paramType.equalsIgnoreCase("Real") || paramType.equalsIgnoreCase("Integer")) {
                Element dataobjXML = ((Element) paramElement.selectSingleNode("/parameter/" + "/data/dataobject/quantity"));
                String paramUnit = dataobjXML.attributeValue("unit");
                if (paramUnit == null || paramUnit.length() == 0)
                    paramUnit = "NO_UNIT";         //could be 'dimensionless variable' or 'not defined'
                param.setUnit(paramUnit);
                String paramMag = dataobjXML.attributeValue("magnitude");
                Double paramDim = Double.valueOf(paramMag);
                param.setDimension(paramDim);
                Double scaleN = UnitAnalyzer.scaleToBase((new Double(paramMag)).doubleValue(), paramUnit);
                if (scaleN != null) param.setMagnitude(scaleN);
                // to add to parameter list
            } else if (paramType.equalsIgnoreCase("Enumeration") || paramType.equalsIgnoreCase("String") || paramType.equalsIgnoreCase("Text") || paramType.equalsIgnoreCase("Boolean") || paramType.equalsIgnoreCase("File")) {
                //unit and dimension are all null
                // to add to parameter list
            } else if (paramType.equalsIgnoreCase("Matrix")) {
                String paramUnit = (paramElement.selectSingleNode("/parameter/" + "/data/dataobject/unit")).getText();
                if (paramUnit == null || paramUnit.length() == 0)
                    paramUnit = "NO_UNIT";

                param.setUnit(paramUnit);

                // load data
                Vector dataV = new Vector();
                Element dataElement = (Element) paramElement.selectSingleNode("/parameter/" + "/data/dataobject/data");
                if (dataElement != null) {
                    String type = dataElement.attributeValue("type");
                    String data = dataElement.getText();
                    if (data != null && data.length() > 0) {
                        // parse the rows
                        String[] rows = data.split(";");
                        // declare variables once to avoid variable creation within loops below
                        Vector newRow;
                        String[] cols;
                        String stringValue;
                        Object newValue;
                        // use hashmap to avoid recreating same object over again - debatable if it really saves time!
                        HashMap values = new HashMap(); // key is string value; value is number

                        for (int row = 0; row < rows.length; row++) {
                            // parse the columns and create the new row
                            newRow = new Vector();
                            cols = rows[row].split(",");

                            for (int i = 0; i < cols.length; i++) {
                                stringValue = cols[i];
                                newValue = values.get(stringValue);
                                if (newValue != null)
                                    newRow.addElement(newValue);
                                else {
                                    if (type.equals("real"))
                                        newValue = new Double(stringValue);
                                    else
                                        newValue = new Integer(stringValue);
                                    newRow.addElement(newValue);
                                    values.put(stringValue, newValue);
                                }
                            }
                            // add the new row to the matrix
                            dataV.add(newRow);
                        }
                    }
                }
                //todo:later think a quick algorithm, get a characteristic scale number of this matrix(could be the mean of all the scale numbers)
            } else if (paramType.equalsIgnoreCase("Vector")) {
                String paramUnit = (paramElement.selectSingleNode("/parameter/" + "/data/dataobject/unit")).getText();
                if (paramUnit == null || paramUnit.length() == 0)
                    paramUnit = "NO_UNIT";
                param.setUnit(paramUnit);

                // load data
                Vector dataV = new Vector();
                Element dataElement = (Element) paramElement.selectSingleNode("/parameter/" + "/data/dataobject/data");
                if (dataElement != null) {
                    String type = dataElement.attributeValue("type");
                    String data = dataElement.getText();
                    if (data != null && data.length() > 0) {
                        String[] values = data.split(",");
                        try {
                            for (int i = 0; i < values.length; i++) {
                                if (type.equals("real"))
                                    dataV.addElement(new Double(values[i]));
                                else
                                    dataV.addElement(new Integer(values[i]));
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //todo:later think a quick algorithm, get a characteristic scale number of this matrix(could be the mean of all the scale numbers)
            }
            paramHashMap.put(paramId, param);
        }

        return paramHashMap;
    }

    private DSet[] getInputOutputs(Element interfaceXML, HashMap modelobjectMap) {
        //define input/output
        DSet inputs = new DSet();
        DSet outputs = new DSet();
        List filterXML = interfaceXML.selectNodes("/modelinterface/views/view/filter");
        for (Iterator iterator = filterXML.iterator(); iterator.hasNext();) {
            Element filterElement = (Element) iterator.next();
            XMLUtils.makeRootElement(filterElement);
            if (filterElement.attributeValue("name").equals("Inputs")) {
                List parsXML = filterElement.selectNodes("parameters/parameter");
                for (Iterator iterator2 = parsXML.iterator(); iterator2.hasNext();) {
                    Element parElement = (Element) iterator2.next();
                    XMLUtils.makeRootElement(parElement);
                    String NodeId = parElement.attributeValue("idRef");
                    if (modelobjectMap.get(NodeId) != null) inputs.add(modelobjectMap.get(NodeId));
                }
            }
            else if (filterElement.attributeValue("name").equals("Outputs")) {
                List parsXML = filterElement.selectNodes("parameters/parameter");
                for (Iterator iterator2 = parsXML.iterator(); iterator2.hasNext();) {
                    Element parElement = (Element) iterator2.next();
                    XMLUtils.makeRootElement(parElement);
                    String NodeId = parElement.attributeValue("idRef");
                    if (modelobjectMap.get(NodeId) != null) outputs.add(modelobjectMap.get(NodeId));
                }
            }
        }
        return new DSet[]{inputs,outputs};
    }

    private SimpleARG createGraph(Element interfaceXML, HashMap modelobjectsMap, DSet inputs, DSet outputs) {
        HashMap param_node_map = new HashMap();
        //Now add nodes into mit.cadlab.dome3.search.datastructure.graph
        SimpleARG graph = new SimpleARG();
        List nodesXML = interfaceXML.selectNodes("/modelinterface/directedGraph/nodes/node");
        for (Iterator iterator = nodesXML.iterator(); iterator.hasNext();) {
            Element paramElement = (Element) iterator.next();
            XMLUtils.makeRootElement(paramElement);
            String NodeId = paramElement.attributeValue("idRef");
            if (modelobjectsMap.get(NodeId) != null) {//sometimes the interface file xml is screwed up may has bad node idref,
                DomeInterfaceParameter p = (DomeInterfaceParameter) (modelobjectsMap.get(NodeId));
                if (inputs.contains(p)) {
                    AttributedNode n = graph.addNode(p.getName(), p.getDataType(), p.getUnit(), "input", p.getDimension());
                    param_node_map.put(p, n);
                } else if (outputs.contains(p)) {
                    AttributedNode n = graph.addNode(p.getName(), p.getDataType(), p.getUnit(), "output", p.getDimension());
                    param_node_map.put(p, n);
                } else {
                    AttributedNode n = graph.addNode(p.getName(), p.getDataType(), p.getUnit(), null, p.getDimension());
                    param_node_map.put(p, n);
                }
            }
        }
        //Now add arcs into mit.cadlab.dome3.search.datastructure.graph
        List fromXML = interfaceXML.selectNodes("/modelinterface/directedGraph/arcs/from");
        for (Iterator iterator = fromXML.iterator(); iterator.hasNext();) {
            Element paramElement = (Element) iterator.next();
            XMLUtils.makeRootElement(paramElement);
            String fromNodeId = paramElement.attributeValue("idRef");
            if (modelobjectsMap.get(fromNodeId) == null) {
                System.out.println("Error in DomeXMLHandler: unknown parameter id" + fromNodeId);
                //not adding
            } else {
                List toXML = paramElement.selectNodes("/from/" + "to");
                for (Iterator iterator_tonode = toXML.iterator(); iterator_tonode.hasNext();) {
                    Element toparamElement = (Element) iterator_tonode.next();
                    XMLUtils.makeRootElement(toparamElement);
                    String toNodeId = toparamElement.attributeValue("idRef");
                    if (modelobjectsMap.get(toNodeId) == null) {
                        System.out.println("Error in DomeXMLHandler: unknown parameter id" + fromNodeId);
                        //not adding
                    } else {
                        //add arc connecting two nodes
                        graph.addArc(param_node_map.get(modelobjectsMap.get(fromNodeId)), param_node_map.get(modelobjectsMap.get(toNodeId)));
                    }
                }
            }
        }

        return graph;
    }
}



