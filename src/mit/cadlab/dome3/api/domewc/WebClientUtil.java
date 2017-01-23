package mit.cadlab.dome3.api.domewc;

import mit.cadlab.dome3.api.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;
import mit.cadlab.dome3.plugin.catalog.core.CConstant;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.text.NumberFormat;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.jfree.data.XYDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.PlotOrientation;

/**
 * User: Sangmok Han
 * Date: 2006. 4. 15.
 */
public class WebClientUtil {

    private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();

    public static void main(String[] args) {
        createGraphImage("c:/test.jpg", 500, 300, null, "year", "persons", new double[] { 1900, 1950, 2000 }, new double[] { 50000000, 70000000, 120000000 });
    }

    public static DomeConnection createDomeConnection(String userId, String passwd, String server) {
        DomeConnection domeConn;
        log("creating a connection to " + server + " as " + userId);
        domeConn = new DomeConnection(userId, passwd, server);
        log("got the connection");
        return domeConn;
    }

    public static RuntimeInterface createRuntimeInterface(DomeConnection domeConn, String space, String path, String downloadFolder) {
        log("getting an interface from " + space + " : " + path);
        DomeInterface domeItf = null;
        try {
            domeItf = domeConn.getInterfaceByPath(space, path);
            log("got the interface");
        } catch (NullPointerException e) {
            throw new RuntimeException("Error getting the specified interface. Make sure that the path " + space + ": " + path + " is valid", e);
        }
        log("creating the runtime interface");
        RuntimeInterface runtimeItf = domeItf.createRuntimeInterface();
        runtimeItf.setDownloadFolder(downloadFolder);
        log("got the runtime interface");
        return runtimeItf;
    }

    private static void log(String msg) {
        System.out.println("[WebClientUtil] " + msg);
    }

    /**
     * Note. how to use idMap to add notification IDs to the status listener
     * Map idMap = new HashMap();
     * idMap.put("param name A", new String[] { "notified id 1", "notified id 2", "notified id 3" });
     * idMap.put("param name B", new String[] { "notified id 4" });
     */
    public static void setValueAndStatusListener(RuntimeInterface runtimeItf, AfterValueChangedTask[] tasks, Map idMap, PrintWriter writer, Map paramMap, int maxFactionDigits, boolean debugJavascript) {
        log("setting value and status listener");
        runtimeItf.clearValueAndStatusChangeListener();
        ValueChangeListenerForWebClient valueChgListener = new ValueChangeListenerForWebClient(writer, paramMap, debugJavascript);
        if (tasks != null) {
            for (int i = 0; i < tasks.length; i++) {
                AfterValueChangedTask task = tasks[i];
                valueChgListener.addAfterValueChangedTask(task);
            }
        }
        valueChgListener.setMaximumFractionDigits(maxFactionDigits);
        runtimeItf.addParameterValueChangeListener(valueChgListener);

        StatusChangeListenerForWebClient statusChgListener = new StatusChangeListenerForWebClient(writer, debugJavascript);
        if (idMap != null) {
            for (Iterator i = idMap.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry entry = (Map.Entry) i.next();
                String paramName = (String) entry.getKey();
                String[] ids = (String []) entry.getValue();
                for (int j = 0; j < ids.length; j++) {
                    String id = ids[j];
                    statusChgListener.addStatusNotification(paramName, id);
                }
            }
        }
        runtimeItf.addParameterStatusChangeListener(statusChgListener);
    }

    private static Object getValueFromParamMap(String paramName, Map paramMap) {
        if (paramMap.get(paramName) == null) {
            throw new RuntimeException("param name not exists in paramMap: " + paramName);
        }
        return paramMap.get(paramName);
    }

    /**
     * how each data type is stored in ParamMap
     *
     * Integer, Double: "200" or "10.0"
     * String: "John"
     * Enumeration: "0" // last selected index
     * Vector: "[20 10 30]";
     * Matrix: "[[20 10 30] [30 40 50] [20 10 10]]";
     * File: "c:/section.wrl" // file path as string
     *
     */
    public static int getInteger(String paramName, Map paramMap) {
        return ((Integer) getValueFromParamMap(paramName, paramMap)).intValue();
    }

    public static double getReal(String paramName, Map paramMap) {
        return ((Double) getValueFromParamMap(paramName, paramMap)).doubleValue();
    }

    /** for enumeration type, last selected index as Integer is stored in param map */
    public static int getEnumeration(String paramName, Map paramMap) {
        return ((Integer) getValueFromParamMap(paramName, paramMap)).intValue();
    }

    public static String getString(String paramName, Map paramMap) {
        return (String) getValueFromParamMap(paramName, paramMap);
    }

    /** returns [Double, Double, Double] */
    public static List getVector(String paramName, Map paramMap) {
        //return DataObjectUtil.createVector((String) getValueFromParamMap(paramName, paramMap), CConstant.REAL_DATA_TYPE);
        return (List) getValueFromParamMap(paramName, paramMap);
    }

    /** returns [[Double, Double, Double], [Double, Double, Double], [Double, Double, Double]]  */
    public static List getMatrix(String paramName, Map paramMap) {
        //return DataObjectUtil.createMatrix((String) getValueFromParamMap(paramName, paramMap), CConstant.REAL_DATA_TYPE);
        return (List) getValueFromParamMap(paramName, paramMap);
    }

    /** Note. filepath can be found using RuntimeInterface.getDownloadFolder() + "/" + getFileName() */
    public static String getFileName(String paramName, Map paramMap) {
        return (String) getValueFromParamMap(paramName, paramMap);
    }

    public static boolean getBoolean(String paramName, Map paramMap) {
        return ((Boolean) getValueFromParamMap(paramName, paramMap)).booleanValue();
    }

    public static String displayValue(String paramName, String defaultValue, int maxFractionDigits, Map paramMap) {
        Object value = paramMap.get(paramName);
        if (value == null) {
            return defaultValue;
        } else {
            String dispValue = null;
            if (value instanceof Number) {
                System.out.println("formatting " + paramName + " : value = " + value);
                try {
                    NumberFormat numFormat = NumberFormat.getInstance();
                    numFormat.setGroupingUsed(false);
                    numFormat.setMaximumFractionDigits(maxFractionDigits);
                    dispValue = numFormat.format(((Number) value).doubleValue());
                } catch (NumberFormatException e) {
                    System.out.println("number format exception: " + e.getMessage());
                    dispValue = value.toString();
                }
            } else if (value instanceof List) {
                if (((List) value).size() > 0) {
                    if (((List) value).get(0) instanceof List) {
                        /* matrix display */
                        dispValue = "[" + ((List) value).size() + " x " + ((List) ((List) value).get(0)).size() + "]";
                    } else {
                        /* vector display */
                        dispValue = "[" + ((List) value).size() + " elements]";
                    }
                } else {
                    dispValue = "[0 element]"; // or dispValue = "[0 x 0]"
                }
            } else {
                dispValue = value.toString();
            }
            return dispValue;
        }
    }

    public static String displayValue(String paramName, String defaultValue, Map paramMap) {
        return displayValue(paramName, defaultValue, 2, paramMap);
    }

    /** this is called just before calling submit(); */
    public static void setInputParamMap(String[] inputParamNames, String[] inputParamDataTypes, Map paramMap, HttpServletRequest request) {
        for (int i = 0; i < inputParamNames.length; i++) {
            String paramName = inputParamNames[i];
            String dataType = inputParamDataTypes[i];
            String valueStr = request.getParameter(paramName);

            if (valueStr == null) {
                log("Warning! input parameter " + paramName + " is not set.");
                continue;
            } else {
                log("Setting " + paramName + " using value: " + valueStr);
            }

            Object valueObj = getValueObjectForParamMapFromValueString(valueStr, dataType);
            paramMap.put(paramName, valueObj);
        }
    }

    /** this is called after submit(); finishes */
    public static void setOutputParamMap(String[] outputParamNames, String[] outputParamDataTypes, Map paramMap, RuntimeInterface runtimeItf) {
        for (int i = 0; i < outputParamNames.length; i++) {
            String paramName = outputParamNames[i];
            String dataType = outputParamDataTypes[i];
            RuntimeParameter param = runtimeItf.getParameterByName(paramName);
            if (param == null) {
                throw new RuntimeException("invalid param name. no such parameter as " + paramName);
            }
            Object valueObj = getValueObjectForParamMapFromRuntimeParameter(param);

            if (param == null) {
                System.out.println("skipping to set output param map for " + paramName);
                continue;
            }

            paramMap.put(paramName, valueObj);
        }
    }

    private static Object getValueObjectForParamMapFromValueString(String valueStr, String dataType) {
        if ("Real".equalsIgnoreCase(dataType)) {
            valueStr = valueStr.replaceAll(",", "");
            return new Double(valueStr); // value is assumed to be String "10,000.00"
        } else if ("Integer".equalsIgnoreCase(dataType)) {
            valueStr = valueStr.replaceAll(",", "");
            return new Integer(valueStr); // value is assumed to be String "40,000"
        } else if ("String".equalsIgnoreCase(dataType)) {
            return valueStr; // value is assumed to be String
        } else if ("Vector".equalsIgnoreCase(dataType)) {
            return DataObjectUtil.createVector(valueStr); // value is assumed to be String "[1 3 4]";
        } else if ("Matrix".equalsIgnoreCase(dataType)) {
            return DataObjectUtil.createMatrix(valueStr); // value is assumed to be String "[3 4; 4 5]";
        } else if ("Enumeration".equalsIgnoreCase(dataType)) {
            return new Integer(valueStr); // value is assumed to be Integer indicating selectedIdx;
        } else if ("Boolean".equalsIgnoreCase(dataType)) {
            return new Boolean(valueStr.equalsIgnoreCase("true")); // value is assumed to be String "true" or "false"
        } else {
            throw new RuntimeException("invalid data type: " + dataType);
        }
    }

    /** retrieve a value object to be stored in paramMap from a runtime parameter
     * the value object is created by calling the appropriate getValue() method of the runtime parameter
     * and manipulating the return value based on its data type. */
    private static Object getValueObjectForParamMapFromRuntimeParameter(RuntimeParameter param) {
        String dataType = param.getDataType();
        if ("Real".equalsIgnoreCase(dataType)) {
            return new Double(param.getRealValue());
        } else if ("Integer".equalsIgnoreCase(dataType)) {
            return new Integer(param.getIntegerValue());
        } else if ("String".equalsIgnoreCase(dataType)) {
            return param.getStringValue();
        } else if ("Vector".equalsIgnoreCase(dataType)) {
            return param.getVectorValue();
        } else if ("Matrix".equalsIgnoreCase(dataType)) {
            return param.getMatrixValue();
        } else if ("Enumeration".equalsIgnoreCase(dataType)) {
            int selectedIdx = DataObjectUtil.getSelectedIndex(param.getEnumerationList());
            return new Integer(selectedIdx);
        } else if ("Boolean".equalsIgnoreCase(dataType)) {
            return new Boolean(param.getBooleanValue());
        } else if ("File".equalsIgnoreCase(dataType)) {
            return param.getFileName();
        } else {
            throw new RuntimeException("invalid data type: " + dataType);
        }
    }

    public static void initParamMapFromRuntimeInterface(RuntimeInterface runtimeItf, Map paramMap) {
        List paramList = runtimeItf.getParameters();
        for (int i = 0; i < paramList.size(); i++) {
            RuntimeParameter param = (RuntimeParameter) paramList.get(i);
            String paramName = param.getParamName();
            Object valueObj = getValueObjectForParamMapFromRuntimeParameter(param);
            paramMap.put(paramName, valueObj);
        }
    }

    public static void setRuntimeParameters(String[] inputParamNames, String[] inputParamDataTypes, Map paramMap, RuntimeInterface runtimeInterface) {
        for (int i = 0; i < inputParamNames.length; i++) {
            String paramName = inputParamNames[i];
            String dataType = inputParamDataTypes[i];
            RuntimeParameter param = (RuntimeParameter) runtimeInterface.getParameterByName(paramName);

            System.out.println("paramName: " + paramName + ", dataType: " + dataType + ", value: " + param.getRawValue() + ", in the map: " + paramMap.get(paramName));
            try {
                /* set runtime param to submitted value */
                if ("Real".equals(dataType)) {
                    if (param.getRealValue() != WebClientUtil.getReal(paramName, paramMap)) {
                        System.out.println("real has been changed!: " + paramName + " = " + paramMap.get(paramName));
                        param.setRealValue(WebClientUtil.getReal(paramName, paramMap));
                    }
                } else if ("Integer".equals(dataType)) {
                    if (param.getIntegerValue() != WebClientUtil.getInteger(paramName, paramMap)) {
                        System.out.println("integer has been changed!");
                        param.setIntegerValue(WebClientUtil.getInteger(paramName, paramMap));
                    }
                } else if ("String".equals(dataType)) {
                    if (param.getStringValue() != WebClientUtil.getString(paramName, paramMap)) {
                        param.setStringValue(WebClientUtil.getString(paramName, paramMap));
                    }
                } else if ("Vector".equals(dataType)) {
                    if (! param.getVectorValue().equals(new Vector(WebClientUtil.getVector(paramName, paramMap)))) {
                        System.out.println("vector has been changed!");
                        param.setVectorValue(new Vector(WebClientUtil.getVector(paramName, paramMap)));
                    }
                } else if ("Matrix".equals(dataType)) {
                    if (! isSame(param.getMatrixValue(), WebClientUtil.getMatrix(paramName, paramMap))) {
                        System.out.println("matrix has been changed!");
                        param.setMatrixValue(new Vector(WebClientUtil.getMatrix(paramName, paramMap)));
                    }
                } else if ("Enumeration".equals(dataType)) {
                    param.setEnumerationValue(WebClientUtil.getEnumeration(paramName, paramMap));
                } else if ("Boolean".equals(dataType)) {
                    param.setBooleanValue(WebClientUtil.getBoolean(paramName, paramMap));
                } else if ("File".equals(dataType)) {
                    throw new RuntimeException("WARNING! a file-type input parameter cannot be set in the web client");
                }
            } catch (RuntimeException e) {
                System.out.println("error occurred when setting " + paramName + " using param value = " + param.getRawValue() + "of type " + dataType + " (if it is ClassCastException, make sure DOME type is the same as " + dataType + ")");
                throw e;
            }
        }
    }

    /** return if the given two Vector or Matrix are the same */
    private static boolean isSame(List v1, List v2) {
        if (v1 == v2) {
            return true;
        } else {
            if (v1 != null && v2 != null && v1.size() == v2.size()) {
                for (int i = 0; i < v1.size(); i++) {
                    Object v1Element = v1.get(i);
                    Object v2Element = v2.get(i);
                    if (v1Element instanceof List && v1Element instanceof List) {
                        return isSame((List) v1Element, (List) v2Element);
                    } else {
                        if (! v1Element.equals(v2Element)) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * StringWriter writer = new StringWriter();
     * TransformerHandler handler = startDocument(writer);
     * startTag("parameters", handler);
     * startTag("parameter", new String[] { "name" }, new String[] { "width" }, handler);
     * //insertBody("body text", handler);
     * endTag("parameter", handler);
     * endTag("parameters", handler);
     * System.out.println(writer.toString());
     */
    public static TransformerHandler startDocument(Writer out) throws SAXException {

        StreamResult streamResult = new StreamResult(out);
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler handler = null;
        try {
            handler = factory.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        Transformer serializer = handler.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.VERSION, "1.0");

        handler.setResult(streamResult);
        handler.startDocument();
        return handler;
    }

    /**
     * equivalent to the following three lines of code:
     * startTag(qName, hd);
     * insertBody(bodyText, hd)
     * endTag(qName);
     */
    public static void insertTag(String qName, String bodyText, TransformerHandler hd) throws SAXException {
        hd.startElement("", "", qName, EMPTY_ATTRIBUTES);
        hd.characters(bodyText.toCharArray(), 0, bodyText.length());
        hd.endElement("", "", qName);
    }

    public static void startTag(String qName, TransformerHandler hd) throws SAXException {
        hd.startElement("", "", qName, EMPTY_ATTRIBUTES);
    }

    public static void startTag(String qName, String[] attributeNames, String[] attributeValues, TransformerHandler hd) throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        for (int i = 0; i < attributeNames.length; i++) {
            String attributeName = attributeNames[i];
            String attributeValue = attributeValues[i];
            attributes.addAttribute("", "", attributeName, "CDATA", attributeValue);
        }
        hd.startElement("", "", qName, attributes);
    }

    public static void insertBody(String text, TransformerHandler handler) throws SAXException {
        handler.characters(text.toCharArray(), 0, text.length());
    }

    public static void endTag(String qName, TransformerHandler hd) throws SAXException {
        hd.endElement("", "", qName);
    }

    public static void createGraphImage(String filePath, int width, int height, String title, String xLabel, String yLabel, double[] xValues, double[] yValues) {
        org.jfree.data.XYSeries series = new org.jfree.data.XYSeries("Average Size");
        for (int i = 0; i < xValues.length; i++) {
            double xValue = xValues[i];
            double yValue = yValues[i];
            series.add(xValue, yValue);
        }
        XYDataset xyDataset = new org.jfree.data.XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, xyDataset, PlotOrientation.VERTICAL, false, false, false);

        try {
            ChartUtilities.saveChartAsPNG(new File(filePath), chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
