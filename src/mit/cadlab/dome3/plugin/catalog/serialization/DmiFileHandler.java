package mit.cadlab.dome3.plugin.catalog.serialization;

import mit.cadlab.dome3.plugin.catalog.core.CConstant;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 7.
 */
public class DmiFileHandler extends DefaultHandler {
    IDContainer idContainer;

    List elementNameList;
    private String itfName;

    public DmiFileHandler(IDContainer idContainer) {
        this.idContainer = idContainer;
        elementNameList = new ArrayList();
    }

    protected void enterElement(String elementName) {
        elementNameList.add(elementName);
    }

    protected void exitElement() {
        elementNameList.remove(elementNameList.size() - 1);
    }

    public String getCurrentElementName() {
        StringBuffer sb = new StringBuffer("/");
        for (int i = 0; i < elementNameList.size(); i++) {
            String elementName = (String) elementNameList.get(i);
            sb.append(elementName).append("/");
        }
        return sb.toString();
    }

    public void endElement(String uri, String localName, String qName) {
        exitElement();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        enterElement(qName);
        String currentElementName = getCurrentElementName();

        //System.out.println("now at " + currentElementName);

        if ("/modelinterface/".equals(currentElementName)) {
            itfName = attributes.getValue("name");
            idContainer.setInterfaceID(itfName, attributes.getValue("id"));
        }

        if ("/modelinterface/parameters/parameter/".equals(currentElementName)) {
//            String nameToBeParsed = attributes.getValue("name");
//            if (nameToBeParsed.endsWith("/" + DomeSerialization.IMPL_SWITCH)) {
//                idContainer.setSwitchInterfaceParamID(itfName, attributes.getValue("id"));
//            } else {
//                int indexOfSlash = nameToBeParsed.indexOf("/");
//                if (indexOfSlash != -1) {
//                    String itfName = nameToBeParsed.substring(0, nameToBeParsed.indexOf("/"));
//                    String paramName = nameToBeParsed.substring(nameToBeParsed.indexOf("/") + 1);
//                    idContainer.setInterfaceParamID(itfName, paramName, attributes.getValue("id"));
//                } else {
//                    throw new RuntimeException("invalid param name: " + nameToBeParsed);
//                }
//            }

            String itfParamName = attributes.getValue("name");
            if (itfParamName.equals(CConstant.IMPL_SWITCH)) {
                idContainer.setSwitchInterfaceParamID(itfName, attributes.getValue("id"));
            } else {
                idContainer.setInterfaceParamID(itfName, itfParamName, attributes.getValue("id"));
            }
        }

//        if ("/modelinterface/mappingsandintermediates/mappings/interfacemappings/mappedParameter/parameter/".equals(currentElementName)) {
//            String name = attributes.getValue("name");
//            String idRef = attributes.getValue("idRef");
//            String idModelRef = attributes.getValue("idModelRef");
//        }
    }

    public IDContainer getIDContainer() {
        return idContainer;
    }
}
