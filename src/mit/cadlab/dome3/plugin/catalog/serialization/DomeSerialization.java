package mit.cadlab.dome3.plugin.catalog.serialization;

import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.DataObjectUtil;
import org.exolab.ID.UUIDGenerator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 3.
 */
public class DomeSerialization {

    public static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
    private static DecimalFormat df = new DecimalFormat("######0.0#");


    public static String createUUID() {
        return UUIDGenerator.create();
    }

    private static void saveDmlFile(CModel model, IDContainer idContainer, String filePath) throws SAXException, IOException {
        FileWriter out = new FileWriter(new File(filePath));
        //PrintWriter out = new PrintWriter(System.out);

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

        startTag("model", new String[] { "type", "id", "name", "pluginType" }, new String[] { "PluginModel", idContainer.getModelID(), model.getName(), "CATALOG" }, handler);

        startTag("modelinfo", handler);
        startTag("version", handler);
        String version = idContainer.getVersion();
        handler.characters(version.toCharArray(), 0, version.length());
        endTag("version", handler);
        endTag("modelinfo", handler);

        startTag("documentation", handler);
        startTag("text", new String[] { "format" }, new String[] { "plain" }, handler);
        String document = idContainer.getDocumentation();
        handler.characters(document.toCharArray(), 0, document.length());
        endTag("text", handler);
        endTag("documentation", handler);

        startTag("parameters", handler);
        for (Iterator i = model.getInterfaceMap().values().iterator(); i.hasNext(); ) {
            CInterface itf = (CInterface) i.next();
            List iParamList = itf.getInputParameterList();
            List oParamList = itf.getOutputParameterList();
            for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
                CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
                if (CConstant.REAL_DATA_TYPE.equals(param.getDataType()) || CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("quantity", new String[] { "magnitude", "unit" }, new String[] { formatNumber(param.getDefaultValue(), param.getDataType()), param.getUnit() }, handler);
                    endTag("quantity", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                    String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                    String dataStr = dataStrAndValueType [0];
                    String valueTypeStr = dataStrAndValueType [1];

                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("unit", handler);
                    handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                    endTag("unit", handler);
                    startTag("rowVector", handler);
                    handler.characters("true".toCharArray(), 0, "true".length());
                    endTag("rowVector", handler);
                    startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    String initValueStr = formatNumber("0", valueTypeStr);
                    handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                    endTag("initialValue", handler);
                    startTag("fixedSize", handler);
                    handler.characters("false".toCharArray(), 0, "false".length());
                    endTag("fixedSize", handler);
                    startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                    endTag("data", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                    String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                    String dataStr = dataStrAndValueType [0];
                    String valueTypeStr = dataStrAndValueType [1];

                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("unit", handler);
                    handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                    endTag("unit", handler);
                    startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    String initValueStr = formatNumber("0", valueTypeStr);
                    handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                    endTag("initialValue", handler);
                    startTag("fixedSize", handler);
                    handler.characters("false".toCharArray(), 0, "false".length());
                    endTag("fixedSize", handler);
                    startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                    endTag("data", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                    List enumList = DataObjectUtil.createEnumList(param.getDefaultValue());
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);

                    startTag("data", handler);
                    int selectedIdx = -1;
                    int idxCounter = 0;
                    for (Iterator k = enumList.iterator(); k.hasNext(); ) {
                        Object[] enumItem = (Object[]) k.next();
                        String enumName = (String) enumItem[0];
                        Object enumValue = enumItem[1];
                        String enumValueType = (enumValue instanceof Integer ? CConstant.INTEGER_DATA_TYPE : CConstant.REAL_DATA_TYPE);
                        startTag("dataobject", new String[] { "name", "type" }, new String[] { enumName, enumValueType }, handler);
                        String enumValueStr = formatNumber(enumValue.toString(), enumValueType);
                        handler.characters(enumValueStr.toCharArray(), 0, enumValueStr.length());
                        endTag("dataobject", handler);
                        if (((Boolean) enumItem[2]).booleanValue()) {
                            selectedIdx = idxCounter;
                        }
                        idxCounter++;
                    }
                    endTag("data", handler);
                    startTag("lastSelectionIndex", handler);
                    String selectedIdxStr = Integer.toString(selectedIdx);
                    handler.characters(selectedIdxStr.toCharArray(), 0, selectedIdxStr.length());
                    endTag("lastSelectionIndex", handler);

                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("filePath", new String[] { }, new String[] { }, handler);
                    handler.characters(param.getFilePath().toCharArray(), 0, param.getFilePath().length());
                    endTag("filePath", handler);
                    startTag("fileType", new String[] { }, new String[] { }, handler);
                    handler.characters(param.getFileType().toCharArray(), 0, param.getFileType().length());
                    endTag("fileType", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else {
                    throw new RuntimeException("not supported data type: " + param.getDataType());
                }
            }
            for (Iterator j = oParamList.iterator(); j.hasNext(); ) {

                CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
                if (CConstant.REAL_DATA_TYPE.equals(param.getDataType()) || CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("quantity", new String[] { "magnitude", "unit" }, new String[] { formatNumber(param.getDefaultValue(), param.getDataType()), param.getUnit() }, handler);
                    endTag("quantity", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                    String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                    String dataStr = dataStrAndValueType [0];
                    String valueTypeStr = dataStrAndValueType [1];

                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("unit", handler);
                    handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                    endTag("unit", handler);
                    startTag("rowVector", handler);
                    handler.characters("true".toCharArray(), 0, "true".length());
                    endTag("rowVector", handler);
                    startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    String initValueStr = formatNumber("0", valueTypeStr);
                    handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                    endTag("initialValue", handler);
                    startTag("fixedSize", handler);
                    handler.characters("false".toCharArray(), 0, "false".length());
                    endTag("fixedSize", handler);
                    startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                    endTag("data", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                    String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                    String dataStr = dataStrAndValueType [0];
                    String valueTypeStr = dataStrAndValueType [1];

                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("unit", handler);
                    handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                    endTag("unit", handler);
                    startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    String initValueStr = formatNumber("0", valueTypeStr);
                    handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                    endTag("initialValue", handler);
                    startTag("fixedSize", handler);
                    handler.characters("false".toCharArray(), 0, "false".length());
                    endTag("fixedSize", handler);
                    startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                    handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                    endTag("data", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                    List enumList = DataObjectUtil.createEnumList(param.getDefaultValue());
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);

                    startTag("data", handler);
                    int selectedIdx = -1;
                    int idxCounter = 0;
                    for (Iterator k = enumList.iterator(); k.hasNext(); ) {
                        Object[] enumItem = (Object[]) k.next();
                        String enumName = (String) enumItem[0];
                        Object enumValue = enumItem[1];
                        String enumValueType = (enumValue instanceof Integer ? CConstant.INTEGER_DATA_TYPE : CConstant.REAL_DATA_TYPE);
                        startTag("dataobject", new String[] { "name", "type" }, new String[] { enumName, enumValueType }, handler);
                        String enumValueStr = formatNumber(enumValue.toString(), enumValueType);
                        handler.characters(enumValueStr.toCharArray(), 0, enumValueStr.length());
                        endTag("dataobject", handler);
                        if (((Boolean) enumItem[2]).booleanValue()) {
                            selectedIdx = idxCounter;
                        }
                        idxCounter++;
                    }
                    endTag("data", handler);
                    startTag("lastSelectionIndex", handler);
                    String selectedIdxStr = Integer.toString(selectedIdx);
                    handler.characters(selectedIdxStr.toCharArray(), 0, selectedIdxStr.length());
                    endTag("lastSelectionIndex", handler);

                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                    endTag("currentType", handler);
                    startTag("data", handler);
                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                    startTag("filePath", new String[] { }, new String[] { }, handler);
                    handler.characters(param.getFilePath().toCharArray(), 0, param.getFilePath().length());
                    endTag("filePath", handler);
                    startTag("fileType", new String[] { }, new String[] { }, handler);
                    handler.characters(param.getFileType().toCharArray(), 0, param.getFileType().length());
                    endTag("fileType", handler);
                    endTag("dataobject", handler);
                    endTag("data", handler);
                    endTag("parameter", handler);
                } else {
                    throw new RuntimeException("not supported data type: " + param.getDataType());
                }

//                if (CConstant.REAL_DATA_TYPE.equals(param.getDataType()) || CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
//                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
//                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
//                    endTag("currentType", handler);
//                    startTag("data", handler);
//                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
//                    startTag("quantity", new String[] { "magnitude", "unit" }, new String[] { formatNumber(param.getDefaultValue(), param.getDataType()), param.getUnit() }, handler);
//                    endTag("quantity", handler);
//                    endTag("dataobject", handler);
//                    endTag("data", handler);
//                    endTag("parameter", handler);
//                } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
//                    startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getModelParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
//                    startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
//                    endTag("currentType", handler);
//                    startTag("data", handler);
//                    startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
//                    startTag("filePath", new String[] { }, new String[] { }, handler);
//                    handler.characters(param.getFilePath().toCharArray(), 0, param.getFilePath().length());
//                    endTag("filePath", handler);
//                    startTag("fileType", new String[] { }, new String[] { }, handler);
//                    handler.characters(param.getFileType().toCharArray(), 0, param.getFileType().length());
//                    endTag("fileType", handler);
//                    endTag("dataobject", handler);
//                    endTag("data", handler);
//                    endTag("parameter", handler);
//                } else {
//                    throw new RuntimeException("not supported data type: " + param.getDataType());
//                }
            }
        }
        for (Iterator m = model.getInterfaceMap().values().iterator(); m.hasNext(); ) {
            CInterface itf = (CInterface) m.next();
            startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getSwitchModelParamID(itf.getName()), itf.getName() + "/" + CConstant.IMPL_SWITCH }, handler);
            startTag("currentType", new String[] { "value" }, new String[] { "Enumeration" }, handler);
            endTag("currentType", handler);
            startTag("data", handler);
            startTag("dataobject", new String[] { "type" }, new String[] { "Enumeration" }, handler);
            startTag("data", handler);
            CInterface firstInterface = (CInterface) model.getInterfaceMap().values().iterator().next(); // okay to do this because all interfaces have the same implementation names
            int implIdx = 1;
            for (Iterator i = firstInterface.getImplementationMap().values().iterator(); i.hasNext(); implIdx++) {
                CImplementation impl = (CImplementation) i.next();
                startTag("dataobject", new String[] { "name", "type" }, new String[] { impl.getName(), "Integer" }, handler);
                handler.characters(Integer.toString(implIdx).toCharArray(), 0, Integer.toString(implIdx).length());
                endTag("dataobject", handler);
            }
            endTag("data", handler);
            startTag("lastSelectionIndex", handler);
            handler.characters("0".toCharArray(), 0, "0".length());
            endTag("lastSelectionIndex", handler);
            endTag("dataobject", handler);
            endTag("data", handler);
            endTag("parameter", handler);
        }
        endTag("parameters", handler);

        startTag("contexts", handler);
        startTag("context", new String[] { "id", "name" }, new String[] { "BUILD_CXT", "Build View" }, handler);
        startTag("modelobjects", handler);
        for (Iterator i = model.getInterfaceMap().values().iterator(); i.hasNext(); ) {
            CInterface itf = (CInterface) i.next();
            List iParamList = itf.getInputParameterList();
            List oParamList = itf.getOutputParameterList();
            for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
                CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
                startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getModelParamID(itf.getName(), param.getName()), idContainer.getModelID() }, handler);
                endTag("parameter", handler);
            }
            for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
                CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
                startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getModelParamID(itf.getName(), param.getName()), idContainer.getModelID() }, handler);
                endTag("parameter", handler);
            }
        }
        for (Iterator m = model.getInterfaceMap().values().iterator(); m.hasNext(); ) {
            CInterface itf = (CInterface) m.next();
            startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + CConstant.IMPL_SWITCH, idContainer.getSwitchModelParamID(itf.getName()), idContainer.getModelID() }, handler);
            endTag("parameter", handler);
        }
        endTag("modelobjects", handler);
        endTag("context", handler);
        startTag("context", new String[] { "id", "name" }, new String[] { "FILE_CXT", "Files" }, handler);
        endTag("context", handler);
        endTag("contexts", handler);

        startTag("mappings", handler);
        endTag("mappings", handler);

        startTag("dependencies", handler);

        for (Iterator i = model.getInterfaceMap().values().iterator(); i.hasNext(); ) {
            CInterface itf = (CInterface) i.next();
            List oParamList = itf.getOutputParameterList();
            for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
                CInterfaceOutputParameter oparam = (CInterfaceOutputParameter) j.next();
                Set inputParamNames = itf.getDriversOf(oparam.getName());
                if (inputParamNames.size() > 0) {
                    startTag("dependency", new String[] { "idRef" }, new String[] { idContainer.getModelParamID(itf.getName(), oparam.getName()) }, handler);
                    for (Iterator k = inputParamNames.iterator(); k.hasNext(); ) {
                        String inputParamName = (String) k.next();
                        startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + inputParamName, idContainer.getModelParamID(itf.getName(), inputParamName), idContainer.getModelID() }, handler);
                        endTag("parameter", handler);
                    }
                    startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + CConstant.IMPL_SWITCH, idContainer.getSwitchModelParamID(itf.getName()), idContainer.getModelID() }, handler);
                    endTag("parameter", handler);
                    endTag("dependency", handler);
                }
            }
        }
        endTag("dependencies", handler);

        startTag("setup", handler);
        endTag("setup", handler);

        startTag("auxfiles", handler);
        endTag("auxfiles", handler);

        startTag("catalog", new String[] { "name" }, new String[] { model.getName() }, handler);
        startTag("interfaces", handler);
        for (Iterator i = model.getInterfaceMap().values().iterator(); i.hasNext(); ) {
            CInterface itf = (CInterface) i.next();
            startTag("interface", new String[] { "name" }, new String[] { itf.getName() }, handler);

            List iParamList = itf.getInputParameterList();
            List oParamList = itf.getOutputParameterList();
            for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
                CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
                startTag("iparam", new String[] { "name" , "type", "unit", "defaultValue" }, new String[] { param.getName(), param.getDataType(), param.getUnit(), param.getDefaultValue() }, handler); //todo: remove file path and file type
                endTag("iparam", handler);
            }
            for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
                CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
                startTag("oparam", new String[] { "name" , "type", "unit", "defaultValue" }, new String[] { param.getName(), param.getDataType(), param.getUnit(), param.getDefaultValue() }, handler); //todo: remove file path and file type
                endTag("oparam", handler);
            }

            startTag("dependency", handler);
            List inputParamNames = itf.getInputParameterNames();
            for (int j = 0; j < inputParamNames.size(); j++) {
                String inputParamName = (String) inputParamNames.get(j);
                Set drivenParamNames = itf.getDrivensBy(inputParamName);
                if (drivenParamNames.size() > 0) {
                    startTag("driverparam", new String[] { "name" }, new String[] { inputParamName }, handler);
                    for (Iterator k = drivenParamNames.iterator(); k.hasNext();) {
                        String drivenParamName = (String) k.next();
                        startTag("drivenparam", new String[] { "name" }, new String[] { drivenParamName }, handler);
                        endTag("drivenparam", handler);
                    }
                    endTag("driverparam", handler);
                }
            }
            endTag("dependency", handler);

            startTag("implementations", handler);
            Collection impls = itf.getImplementationMap().values();
            for (Iterator j = impls.iterator(); j.hasNext();) {
                CImplementation impl = (CImplementation) j.next();
                CNamingService namingService = impl.getNamingService();
                startTag("implementation", new String[] { "name" }, new String[] { impl.getName() }, handler);
                startTag("interface", handler);

                List outputParamNames = impl.getOutputParameterNames();
                for (int k = 0; k < outputParamNames.size(); k++) {
                    String outputParamName = (String) outputParamNames.get(k);
                    CInterfaceOutputParameter outputParam = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + outputParamName);
                    startTag("oparam", new String[] { "name", "mappingScript" }, new String[] { outputParam.getName(), outputParam.getMapping().getMappingScript() }, handler);
                    endTag("oparam", handler);
                }
                endTag("interface", handler);

                List relAliases = impl.getRelationAliases();
                for (int k = 0; k < relAliases.size(); k++) {
                    String relAlias = (String) relAliases.get(k);
                    CRelation rel = namingService.getRelation(relAlias);

                    startTag("relation", new String[] { "type", "name", "relAlias" }, new String[] { (rel instanceof CLocalRelation) ? "local" : "remote", rel.getRelationName(), rel.getRelAlias() }, handler);

                    List iParamNames = rel.getInputParameterNames();
                    for (int l = 0; l < iParamNames.size(); l++) {
                        String iParamName = (String) iParamNames.get(l);
                        CRelationInputParameter iParam = namingService.getRelationInputParameter(relAlias + "." + iParamName);
                        startTag("iparam", new String[] { "name", "type", "unit", "defaultValue", "mappingScript" }, new String[] { iParam.getName(), iParam.getDataType(), iParam.getUnit(), iParam.getDefaultValue(), iParam.getMapping().getMappingScript() }, handler);
                        endTag("iparam", handler);
                    }
                    List oParamNames = rel.getOutputParameterNames();
                    for (int l = 0; l < oParamNames.size(); l++) {
                        String oParamName = (String) oParamNames.get(l);
                        CRelationOutputParameter oParam = namingService.getRelationOutputParameter(relAlias + "." + oParamName);
                        startTag("oparam", new String[] { "name", "type", "unit", "defaultValue" }, new String[] { oParam.getName(), oParam.getDataType(), oParam.getUnit(), oParam.getDefaultValue() }, handler);
                        endTag("oparam", handler);
                    }

                    if (rel instanceof CLocalRelation) {
                        startTag("relationScript", new String[] { "script" }, new String[] { rel.getRelationScript() }, handler);
                        endTag("relationScript", handler);
                    } else {
                        CRemoteRelation remoteRel = (CRemoteRelation) rel;
                        startTag("connection", new String[] { "server", "userid", "password", "space", "path" }, new String[] { remoteRel.getServerPort(), remoteRel.getUser(), remoteRel.getPassword(), remoteRel.getSpace(), remoteRel.getInterfacePath() }, handler);
                        endTag("connection", handler);
                    }

                    startTag("dependency", handler);
                    List relInputParamNames = rel.getInputParameterNames();
                    for (int l = 0; l < relInputParamNames.size(); l++) {
                        String inputParamName = (String) relInputParamNames.get(l);
                        Set drivenParamNames = rel.getDrivensBy(inputParamName);
                        if (drivenParamNames.size() > 0) {
                            startTag("driverparam", new String[] { "name" }, new String[] { inputParamName }, handler);
                            for (Iterator m = drivenParamNames.iterator(); m.hasNext();) {
                                String drivenParamName = (String) m.next();
                                startTag("drivenparam", new String[] { "name" }, new String[] { drivenParamName }, handler);
                                endTag("drivenparam", handler);
                            }
                            endTag("driverparam", handler);
                        }
                    }
                    endTag("dependency", handler);
                    endTag("relation", handler);
                }
                endTag("implementation", handler);
            }
            endTag("implementations", handler);
            endTag("interface", handler);
        }
        endTag("interfaces", handler);
        endTag("catalog", handler);
        endTag("model", handler);
        handler.endDocument();
    }

    private static void saveDmiFile(CModel model, IDContainer idContainer, String itfName, String filePath) throws SAXException, IOException {
        FileWriter out = new FileWriter(new File(filePath));

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

        startTag("modelinterface", new String[] { "type", "id", "name" }, new String[] { "DomeModelInterface", idContainer.getInterfaceID(itfName), itfName }, handler);

        startTag("interfaceinfo", handler);
        startTag("version", handler);
        String version = idContainer.getVersion();
        handler.characters(version.toCharArray(), 0, version.length());
        endTag("version", handler);
        endTag("interfaceinfo", handler);

        startTag("parameters", handler);
        CInterface itf = model.getInterface(itfName);
        List iParamList = itf.getInputParameterList();
        List oParamList = itf.getOutputParameterList();
        for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
            //****//startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);

            if (CConstant.REAL_DATA_TYPE.equals(param.getDataType()) || CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("quantity", new String[] { "magnitude", "unit" }, new String[] { formatNumber(param.getDefaultValue(), param.getDataType()), param.getUnit() }, handler);
                endTag("quantity", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                String dataStr = dataStrAndValueType [0];
                String valueTypeStr = dataStrAndValueType [1];

                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("unit", handler);
                handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                endTag("unit", handler);
                startTag("rowVector", handler);
                handler.characters("true".toCharArray(), 0, "true".length());
                endTag("rowVector", handler);
                startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                String initValueStr = formatNumber("0", valueTypeStr);
                handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                endTag("initialValue", handler);
                startTag("fixedSize", handler);
                handler.characters("false".toCharArray(), 0, "false".length());
                endTag("fixedSize", handler);
                startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                endTag("data", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                String dataStr = dataStrAndValueType [0];
                String valueTypeStr = dataStrAndValueType [1];

                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("unit", handler);
                handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                endTag("unit", handler);
                startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                String initValueStr = formatNumber("0", valueTypeStr);
                handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                endTag("initialValue", handler);
                startTag("fixedSize", handler);
                handler.characters("false".toCharArray(), 0, "false".length());
                endTag("fixedSize", handler);
                startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                endTag("data", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                List enumList = DataObjectUtil.createEnumList(param.getDefaultValue());
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);

                startTag("data", handler);
                int selectedIdx = -1;
                int idxCounter = 0;
                for (Iterator k = enumList.iterator(); k.hasNext(); ) {
                    Object[] enumItem = (Object[]) k.next();
                    String enumName = (String) enumItem[0];
                    Object enumValue = enumItem[1];
                    String enumValueType = (enumValue instanceof Integer ? CConstant.INTEGER_DATA_TYPE : CConstant.REAL_DATA_TYPE);

                    startTag("dataobject", new String[] { "name", "type" }, new String[] { enumName, enumValueType }, handler);
                    String enumValueStr = formatNumber(enumValue.toString(), enumValueType);
                    handler.characters(enumValueStr.toCharArray(), 0, enumValueStr.length());
                    endTag("dataobject", handler);
                    if (((Boolean) enumItem[2]).booleanValue()) {
                        selectedIdx = idxCounter;
                    }
                    idxCounter++;
                }
                endTag("data", handler);
                startTag("lastSelectionIndex", handler);
                String selectedIdxStr = Integer.toString(selectedIdx);
                handler.characters(selectedIdxStr.toCharArray(), 0, selectedIdxStr.length());
                endTag("lastSelectionIndex", handler);

                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("filePath", new String[] { }, new String[] { }, handler);
                handler.characters(param.getFilePath().toCharArray(), 0, param.getFilePath().length());
                endTag("filePath", handler);
                startTag("fileType", new String[] { }, new String[] { }, handler);
                handler.characters(param.getFileType().toCharArray(), 0, param.getFileType().length());
                endTag("fileType", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else {
                throw new RuntimeException("not supported data type: " + param.getDataType());
            }
        }
        for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
            //startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), itf.getName() + "/" + param.getName() }, handler);
            if (CConstant.REAL_DATA_TYPE.equals(param.getDataType()) || CConstant.INTEGER_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("quantity", new String[] { "magnitude", "unit" }, new String[] { formatNumber(param.getDefaultValue(), param.getDataType()), param.getUnit() }, handler);
                endTag("quantity", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.STRING_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.BOOLEAN_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                handler.characters(param.getDefaultValue().toCharArray(), 0, param.getDefaultValue().length());
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.VECTOR_DATA_TYPE.equals(param.getDataType())) {
                String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                String dataStr = dataStrAndValueType [0];
                String valueTypeStr = dataStrAndValueType [1];

                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("unit", handler);
                handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                endTag("unit", handler);
                startTag("rowVector", handler);
                handler.characters("true".toCharArray(), 0, "true".length());
                endTag("rowVector", handler);
                startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                String initValueStr = formatNumber("0", valueTypeStr);
                handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                endTag("initialValue", handler);
                startTag("fixedSize", handler);
                handler.characters("false".toCharArray(), 0, "false".length());
                endTag("fixedSize", handler);
                startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                endTag("data", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.MATRIX_DATA_TYPE.equals(param.getDataType())) {
                String[] dataStrAndValueType = DomeSerialization.getDataStringAndValueTypeFromDefaultValue(param.getDefaultValue());
                String dataStr = dataStrAndValueType [0];
                String valueTypeStr = dataStrAndValueType [1];

                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("unit", handler);
                handler.characters(param.getUnit().toCharArray(), 0, param.getUnit().length());
                endTag("unit", handler);
                startTag("initialValue", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                String initValueStr = formatNumber("0", valueTypeStr);
                handler.characters(initValueStr.toCharArray(), 0, initValueStr.length());
                endTag("initialValue", handler);
                startTag("fixedSize", handler);
                handler.characters("false".toCharArray(), 0, "false".length());
                endTag("fixedSize", handler);
                startTag("data", new String[] { "type" }, new String[] { valueTypeStr }, handler);
                handler.characters(dataStr.toCharArray(), 0, dataStr.length());
                endTag("data", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.ENUM_DATA_TYPE.equals(param.getDataType())) {
                List enumList = DataObjectUtil.createEnumList(param.getDefaultValue());
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);

                startTag("data", handler);
                int selectedIdx = -1;
                int idxCounter = 0;
                for (Iterator k = enumList.iterator(); k.hasNext(); ) {
                    Object[] enumItem = (Object[]) k.next();
                    String enumName = (String) enumItem[0];
                    Object enumValue = enumItem[1];
                    String enumValueType = (enumValue instanceof Integer ? CConstant.INTEGER_DATA_TYPE : CConstant.REAL_DATA_TYPE);
                    startTag("dataobject", new String[] { "name", "type" }, new String[] { enumName, enumValueType }, handler);
                    String enumValueStr = formatNumber(enumValue.toString(), enumValueType);
                    handler.characters(enumValueStr.toCharArray(), 0, enumValueStr.length());
                    endTag("dataobject", handler);
                    if (((Boolean) enumItem[2]).booleanValue()) {
                        selectedIdx = idxCounter;
                    }
                    idxCounter++;
                }
                endTag("data", handler);
                startTag("lastSelectionIndex", handler);
                String selectedIdxStr = Integer.toString(selectedIdx);
                handler.characters(selectedIdxStr.toCharArray(), 0, selectedIdxStr.length());
                endTag("lastSelectionIndex", handler);

                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else if (CConstant.FILE_DATA_TYPE.equals(param.getDataType())) {
                startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), param.getName() }, handler);
                startTag("currentType", new String[] { "value" }, new String[] { param.getDataType() }, handler);
                endTag("currentType", handler);
                startTag("data", handler);
                startTag("dataobject", new String[] { "type" }, new String[] { param.getDataType() }, handler);
                startTag("filePath", new String[] { }, new String[] { }, handler);
                handler.characters(param.getFilePath().toCharArray(), 0, param.getFilePath().length());
                endTag("filePath", handler);
                startTag("fileType", new String[] { }, new String[] { }, handler);
                handler.characters(param.getFileType().toCharArray(), 0, param.getFileType().length());
                endTag("fileType", handler);
                endTag("dataobject", handler);
                endTag("data", handler);
                endTag("parameter", handler);
            } else {
                throw new RuntimeException("not supported data type: " + param.getDataType());
            }
        }



        if (hasMultipleImplementations(model)) {
            startTag("parameter", new String[] { "id" , "name" }, new String[] { idContainer.getSwitchInterfaceParamID(itfName), CConstant.IMPL_SWITCH }, handler);
            startTag("currentType", new String[] { "value" }, new String[] { "Enumeration" }, handler);
            endTag("currentType", handler);
            startTag("data", handler);
            startTag("dataobject", new String[] { "type" }, new String[] { "Enumeration" }, handler);
            startTag("data", handler);
            int implIdx = 1;
            CInterface firstInterface = (CInterface) model.getInterfaceMap().values().iterator().next();
            for (Iterator i = firstInterface.getImplementationMap().values().iterator(); i.hasNext(); implIdx++) {
                CImplementation impl = (CImplementation) i.next();
                startTag("dataobject", new String[] { "name", "type" }, new String[] { impl.getName(), "Integer" }, handler);
                handler.characters(Integer.toString(implIdx).toCharArray(), 0, Integer.toString(implIdx).length());
                endTag("dataobject", handler);
            }
            endTag("data", handler);
            startTag("lastSelectionIndex", handler);
            String lastSelectionIndexStr = "0";
            handler.characters(lastSelectionIndexStr.toCharArray(), 0, lastSelectionIndexStr.length());
            endTag("lastSelectionIndex", handler);
            endTag("dataobject", handler);
            endTag("data", handler);
            endTag("parameter", handler);
        }
        endTag("parameters", handler);

        startTag("visualizations", handler);
        endTag("visualizations", handler);
        startTag("relations", handler);
        endTag("relations", handler);
        startTag("contexts", handler);
        startTag("context", new String[] { "id", "name" }, new String[] { "IFACE_MODEL_CXT", "Model View" }, handler);
        endTag("context", handler);
        endTag("contexts", handler);

        startTag("views", handler);
        startTag("view", new String[] { "name" }, new String[] { "Build View" }, handler);
        startTag("context", new String[] { "id", "name" }, new String[] { "IFACE_BUILD_CXT", "Build View" }, handler);
        startTag("modelobjects", handler);
        for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
            //****//startTag("parameter", new String[] { "name", "idRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            startTag("parameter", new String[] { "name", "idRef" }, new String[] { param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            endTag("parameter", handler);
        }
        for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
            //****//startTag("parameter", new String[] { "name", "idRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            startTag("parameter", new String[] { "name", "idRef" }, new String[] { param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            endTag("parameter", handler);
        }
        if (hasMultipleImplementations(model)) {
            startTag("parameter", new String[] { "name", "idRef" }, new String[] { CConstant.IMPL_SWITCH, idContainer.getSwitchInterfaceParamID(itfName) }, handler);
            endTag("parameter", handler);
        }
        endTag("modelobjects", handler);
        endTag("context", handler);
        endTag("view", handler);


        startTag("view", new String[] { "name" }, new String[] { "Interface Causality View" }, handler);
        startTag("filter", new String[] { "id", "name" }, new String[] { "INPUTS_FILTER", "Inputs" }, handler);
        startTag("parameters", handler);
        for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
            //****//startTag("parameter", new String[] { "name", "idRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            startTag("parameter", new String[] { "name", "idRef" }, new String[] { param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            endTag("parameter", handler);
        }
        if (hasMultipleImplementations(model)) {
            startTag("parameter", new String[] { "name", "idRef" }, new String[] { CConstant.IMPL_SWITCH, idContainer.getSwitchInterfaceParamID(itfName) }, handler);
            endTag("parameter", handler);
        }
        endTag("parameters", handler);
        endTag("filter", handler);

        startTag("filter", new String[] { "id", "name" }, new String[] { "OUTPUTS_FILTER", "Outputs" }, handler);
        startTag("parameters", handler);
        for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
            //****//startTag("parameter", new String[] { "name", "idRef", "causality" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()), "Result" }, handler);
            startTag("parameter", new String[] { "name", "idRef", "causality" }, new String[] { param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()), "Result" }, handler);
            endTag("parameter", handler);
        }
        endTag("parameters", handler);
        endTag("filter", handler);

        startTag("filter", new String[] { "id", "name" }, new String[] { "INDETERMINATES_FILTER", "Indeterminates" }, handler);
        endTag("filter", handler);

        endTag("view", handler);
        endTag("views", handler);

        startTag("directedGraph", new String[] { "id" }, new String[] { idContainer.getInterfaceID(itfName) }, handler);
        startTag("nodes", handler);

        for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
            startTag("node", new String[] { "idRef" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            endTag("node", handler);
        }
        for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
            startTag("node", new String[] { "idRef" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            endTag("node", handler);
        }
        if (hasMultipleImplementations(model)) {
            startTag("node", new String[] { "idRef" }, new String[] { idContainer.getSwitchInterfaceParamID(itfName) }, handler);
            endTag("node", handler);
        }
        endTag("nodes", handler);

        startTag("arcs", handler);

        for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
            CInterfaceInputParameter iparam = (CInterfaceInputParameter) j.next();
            Set outputParamNames = itf.getDrivensBy(iparam.getName());
            if (outputParamNames.size() > 0) {
                startTag("from", new String[] { "idRef" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), iparam.getName()) }, handler);
                for (Iterator k = outputParamNames.iterator(); k.hasNext(); ) {
                    String outputParamName = (String) k.next();
                    startTag("to", new String[] { "idRef" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), outputParamName) }, handler);
                    endTag("to", handler);
                }
                endTag("from", handler);
            }
        }

        if (hasMultipleImplementations(model)) {
            startTag("from", new String[] { "idRef" }, new String[] { idContainer.getSwitchInterfaceParamID(itf.getName()) }, handler);
            for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
                CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
                startTag("to", new String[] { "idRef" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
                endTag("to", handler);
            }
            endTag("from", handler);
        }

        endTag("arcs", handler);
        endTag("directedGraph", handler);

        startTag("interfaceToRelParamMap", handler);
        for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
            startTag("iToRpMap", new String[] { "relParamId", "ifaceId" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), idContainer.getModelParamID(itf.getName(), param.getName()) }, handler);
            endTag("iToRpMap", handler);
        }
        for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
            startTag("iToRpMap", new String[] { "relParamId", "ifaceId" }, new String[] { idContainer.getInterfaceParamID(itf.getName(), param.getName()), idContainer.getModelParamID(itf.getName(), param.getName()) }, handler);
            endTag("iToRpMap", handler);
        }
        if (hasMultipleImplementations(model)) {
            startTag("iToRpMap", new String[] { "relParamId", "ifaceId" }, new String[] { idContainer.getSwitchInterfaceParamID(itf.getName()), idContainer.getSwitchModelParamID(itf.getName()) }, handler);
            endTag("iToRpMap", handler);
        }
        endTag("interfaceToRelParamMap", handler);

        endTag("modelinterface", handler);
        handler.endDocument();
    }

    private static boolean hasMultipleImplementations(CModel model) {
        CInterface firstInterface = (CInterface) model.getInterfaceMap().values().iterator().next();
        return (firstInterface.getImplementationMap().size() > 1);
    }

    private static void saveMappingFile(CModel model, IDContainer idContainer, String itfName, String filePath) throws SAXException, IOException {
        FileWriter out = new FileWriter(new File(filePath));

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

        startTag("modelinterface", new String[] { "type", "id", "name" }, new String[] { "DomeModelInterface", idContainer.getInterfaceID(itfName), itfName }, handler);

        startTag("interfaceinfo", handler);
        startTag("version", handler);
        String version = idContainer.getVersion();
        handler.characters(version.toCharArray(), 0, version.length());
        endTag("version", handler);
        endTag("interfaceinfo", handler);

        startTag("mappingsandintermediates", handler);
        startTag("mappings", handler);
        startTag("interfacemappings", handler);
        CInterface itf = model.getInterface(itfName);
        List iParamList = itf.getInputParameterList();
        List oParamList = itf.getOutputParameterList();
        for (Iterator j = iParamList.iterator(); j.hasNext(); ) {
            CInterfaceInputParameter param = (CInterfaceInputParameter) j.next();
            //****//startTag("mappedParameter", new String[] { "name", "idRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            startTag("mappedParameter", new String[] { "name", "idRef" }, new String[] { param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getModelParamID(itf.getName(), param.getName()), idContainer.getModelID() }, handler);
            endTag("parameter", handler);
            endTag("mappedParameter", handler);
        }
        for (Iterator j = oParamList.iterator(); j.hasNext(); ) {
            CInterfaceOutputParameter param = (CInterfaceOutputParameter) j.next();
            //****//startTag("mappedParameter", new String[] { "name", "idRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            startTag("mappedParameter", new String[] { "name", "idRef" }, new String[] { param.getName(), idContainer.getInterfaceParamID(itf.getName(), param.getName()) }, handler);
            startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + param.getName(), idContainer.getModelParamID(itf.getName(), param.getName()), idContainer.getModelID() }, handler);
            endTag("parameter", handler);
            endTag("mappedParameter", handler);
        }
        //****//startTag("mappedParameter", new String[] { "name", "idRef" }, new String[] { itf.getName() + "/" + DomeSerialization.IMPL_SWITCH, idContainer.getSwitchInterfaceParamID(itf.getName()) }, handler);
        if (hasMultipleImplementations(model)) {
            startTag("mappedParameter", new String[] { "name", "idRef" }, new String[] { CConstant.IMPL_SWITCH, idContainer.getSwitchInterfaceParamID(itf.getName()) }, handler);
            startTag("parameter", new String[] { "name", "idRef", "idModelRef" }, new String[] { itf.getName() + "/" + CConstant.IMPL_SWITCH, idContainer.getSwitchModelParamID(itf.getName()), idContainer.getModelID() }, handler);
            endTag("parameter", handler);
            endTag("mappedParameter", handler);
        }

        endTag("interfacemappings", handler);
        endTag("mappings", handler);
        startTag("intermediateobjects", handler);
        endTag("intermediateobjects", handler);
        endTag("mappingsandintermediates", handler);
        endTag("modelinterface", handler);
        handler.endDocument();
    }

    public static void save(CModel model, IDContainer idContainer, String dmlFilePath) throws IOException, SAXException {
        idContainer.increaseVersion();
        /* idContainer might not have all newly added elements. assignRandomIDToNewElements() would assign random ID for newly added elements. */
        idContainer.assignRandomIDToNewElements(model);

        saveDmlFile(model, idContainer, dmlFilePath);

        File dmlFile = new File(dmlFilePath);
        String dmlFileName = dmlFile.getName();
        String parentPath = dmlFile.getParent();
        Pattern p = Pattern.compile("(.+)\\-CATALOG.dml");
        Matcher m = p.matcher(dmlFileName);
        boolean found = m.find();
        if (! found) {
            throw new RuntimeException("invalid file name: " + dmlFileName + ". expected to have a pattern of " + p.pattern());
        } else {
            String itfFolderPath = parentPath + File.separator + "interfaces-" + m.group(1) + "-" + idContainer.getModelID();
            File itfFolder = new File(itfFolderPath);
            if (! itfFolder.exists()) {
                itfFolder.mkdir();
            }

            Set itfNames = idContainer.getInterfaceNames();
            for (Iterator i = itfNames.iterator(); i.hasNext();) {
                String itfName = (String) i.next();
                String dmiFilePath = itfFolderPath + File.separator + idContainer.getInterfaceID(itfName) + ".dmi";
                String mappingFilePath = itfFolderPath + File.separator + idContainer.getInterfaceID(itfName) + "-mappings";
                saveDmiFile(model, idContainer, itfName, dmiFilePath);
                saveMappingFile(model, idContainer, itfName, mappingFilePath);
            }
        }
    }

    private static void startTag(String qName, TransformerHandler hd) throws SAXException {
        hd.startElement("", "", qName, EMPTY_ATTRIBUTES);
    }

    private static void startTag(String qName, String[] attributeNames, String[] attributeValues, TransformerHandler hd) throws SAXException {
        //System.out.println("qname: " + qName);
        AttributesImpl attributes = new AttributesImpl();
        for (int i = 0; i < attributeNames.length; i++) {
            String attributeName = attributeNames[i];
            String attributeValue = attributeValues[i];
            attributes.addAttribute("", "", attributeName, "CDATA", attributeValue);
        }
        //System.out.println("attribute: " + toAttributeString(attributes));
        hd.startElement("", "", qName, attributes);
    }

//    private static String toAttributeString(Attributes attributeValue) {
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < attributeValue.getLength(); i++) {
//            sb.append(attributeValue.getQName(i) + "=" + attributeValue.getValue(i) + ", ");
//        }
//        return sb.toString();
//    }

    private static void endTag(String qName, TransformerHandler hd) throws SAXException {
        hd.endElement("", "", qName);
    }

    private static String formatNumber(String value, String dataType) {
        if (CConstant.REAL_DATA_TYPE.equalsIgnoreCase(dataType)) {
            value = df.format(Double.parseDouble(value));
        }
        return value;
    }

    private static String formatNumber(Number value, String dataType) {
        String ret = null;
        if (CConstant.REAL_DATA_TYPE.equalsIgnoreCase(dataType)) {
            ret = df.format(value.doubleValue());
        } else {
            ret = value.toString();
        }
        return ret;
    }

    /** returns Object[] { CModel, IDContainer } */
    public static CModelAndIDContainer load(String filePath) {
        File file = new File(filePath);
        DmlFileHandler handler = new DmlFileHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(file, handler);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        CModel model = handler.getCModel();
        IDContainer idContainer = handler.getIDContainer();

        String fileName = file.getName();
        String parentPath = file.getParent();
        Pattern p = Pattern.compile("(.+)\\-CATALOG.dml");
        Matcher m = p.matcher(fileName);
        boolean found = m.find();
        if (! found) {
            throw new RuntimeException("invalid file name: " + fileName + ". expected to have a pattern of " + p.pattern());
        } else {
            String itfFolderPath = parentPath + File.separator + "interfaces-" + m.group(1) + "-" + idContainer.getModelID();
            File itfFolder = new File(itfFolderPath);
            File[] files = itfFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".dmi");
                }
            });

            for (int i = 0; i < files.length; i++) {
                File dmiFile = files[i];
                DmiFileHandler dmiFileHandler = new DmiFileHandler(idContainer);
                try {
                    SAXParser saxParser = factory.newSAXParser();
                    saxParser.parse(dmiFile, dmiFileHandler);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        /* for model with only one implementation, we need assign a new ID for each interface switch param of interfaces.
           It is because we don't have ID of interface switch param when there is only one implementation */
        if (! hasMultipleImplementations(model)) {
            for (Iterator i = model.getInterfaceMap().values().iterator(); i.hasNext(); ) {
                CInterface itf = (CInterface) i.next();
                idContainer.setSwitchInterfaceParamID(itf.getName(), DomeSerialization.createUUID());
            }
        }


        return new CModelAndIDContainer(model, idContainer);
    }


    public static class CModelAndIDContainer {
        CModel model;
        IDContainer idContainer;

        public CModelAndIDContainer(CModel model, IDContainer idContainer) {
            this.model = model;
            this.idContainer = idContainer;
        }

        public CModel getModel() {
            return model;
        }

        public IDContainer getIDContainer() {
            return idContainer;
        }
    }

    private static String[] getDataStringAndValueTypeFromDefaultValue(String defaultValue) {
        String valueTypeStr = null;
        String valueType = DataObjectUtil.getDataType(defaultValue);
        if (CConstant.REAL_DATA_TYPE.equals(valueType)) {
            valueTypeStr = "real";
        } else if (CConstant.INTEGER_DATA_TYPE.equals(valueType)) {
            valueTypeStr = "integer";
        }

        List matrixValue = DataObjectUtil.createMatrix(defaultValue);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < matrixValue.size(); i++) {
            List row = (List) matrixValue.get(i);
            for (int j = 0; j < row.size(); j++) {
                Number num = (Number) row.get(j);
                String numStr = formatNumber(num, valueType);
                if (j == 0) {
                    sb.append(numStr);
                } else {
                    sb.append(',').append(numStr);
                }
            }
            if (i != matrixValue.size() - 1) {
                sb.append(';');
            }
        }

        return new String[] { sb.toString(), valueTypeStr };
    }
}
