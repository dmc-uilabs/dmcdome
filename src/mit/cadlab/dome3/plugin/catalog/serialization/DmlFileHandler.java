package mit.cadlab.dome3.plugin.catalog.serialization;

import mit.cadlab.dome3.plugin.catalog.core.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 4.
 */
public class DmlFileHandler extends DefaultHandler {

    IDContainer idContainer;
    CModel model;
    CInterface itf;
    CImplementation impl;
    CRelation rel;
    CParameter param;

    String driverParamName;
    String relAlias;

    List elementNameList;
    private StringBuffer documentationSb;

    public DmlFileHandler() {
        idContainer = new IDContainer();
        elementNameList = new ArrayList();
        documentationSb = new StringBuffer();
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
        String currentElementName = getCurrentElementName();

        if ("/model/documentation/text/".equals(currentElementName)) {
            idContainer.setDocumentation(documentationSb.toString());
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/".equals(currentElementName)) {
            /* once all parameters are set into namingspace, we call updateInputParamNamesOfAllCMappings() to re-extract input parameters from CMapping script */
            impl.getNamingService().updateInputParamNamesOfAllCMappings();
        }
        exitElement();
    }

    public void characters(char[] ch, int start, int length) {
        String currentElementName = getCurrentElementName();

        if ("/model/modelinfo/version/".equals(currentElementName)) {
            idContainer.setVersion(new String(ch, start, length));
        }

        if ("/model/documentation/text/".equals(currentElementName)) {
            documentationSb.append(new String(ch, start, length));
        }

//        if ("/model/catalog/interfaces/interface/iparam/filePath/".equals(currentElementName) || "/model/catalog/interfaces/interface/oparam/filePath".equals(currentElementName)) {
//            param.setFilePath(new String(ch, start, length));
//        }
//
//        if ("/model/catalog/interfaces/interface/iparam/fileType/".equals(currentElementName) || "/model/catalog/interfaces/interface/oparam/fileType".equals(currentElementName)) {
//            param.setFileType(new String(ch, start, length));
//        }
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        enterElement(qName);
        String currentElementName = getCurrentElementName();

        //System.out.println("now at " + currentElementName);

        if ("/model/".equals(currentElementName)) {
            idContainer.setModelID(attributes.getValue("id"));
        }

        if ("/model/catalog/".equals(currentElementName)) {
            model = new CModel(attributes.getValue("name"));
        }

        if ("/model/parameters/parameter/".equals(currentElementName)) {
            String nameToBeParsed = attributes.getValue("name");
            if (nameToBeParsed.endsWith("/" + CConstant.IMPL_SWITCH)) {
                int indexOfSlash = nameToBeParsed.indexOf("/");
                if (indexOfSlash != -1) {
                    String itfName = nameToBeParsed.substring(0, nameToBeParsed.indexOf("/"));
                    idContainer.setSwitchModelParamID(itfName, attributes.getValue("id"));
                } else {
                    throw new RuntimeException("invalid param name: " + nameToBeParsed);
                }

            } else {
                int indexOfSlash = nameToBeParsed.indexOf("/");
                if (indexOfSlash != -1) {
                    String itfName = nameToBeParsed.substring(0, nameToBeParsed.indexOf("/"));
                    String paramName = nameToBeParsed.substring(nameToBeParsed.indexOf("/") + 1);
                    idContainer.setModelParamID(itfName, paramName, attributes.getValue("id"));
                } else {
                    throw new RuntimeException("invalid param name: " + nameToBeParsed);
                }
            }
        }

        if ("/model/catalog/interfaces/interface/".equals(currentElementName)) {
            itf = model.addInterface(attributes.getValue("name"));
        }

        if ("/model/catalog/interfaces/interface/iparam/".equals(currentElementName)) {
            CInterfaceInputParameter param = itf.addInputParameter(attributes.getValue("name"));
            param.setDataType(attributes.getValue("type"));
            param.setUnit(attributes.getValue("unit"));
            param.setDefaultValue(attributes.getValue("defaultValue"));
        }

        if ("/model/catalog/interfaces/interface/oparam/".equals(currentElementName)) {
            CInterfaceOutputParameter param = itf.addOutputParameter(attributes.getValue("name"));
            param.setDataType(attributes.getValue("type"));
            param.setUnit(attributes.getValue("unit"));
            param.setDefaultValue(attributes.getValue("defaultValue"));
        }

        if ("/model/catalog/interfaces/interface/dependency/driverparam/".equals(currentElementName)) {
            this.driverParamName = attributes.getValue("name");
        }
        if ("/model/catalog/interfaces/interface/dependency/driverparam/drivenparam/".equals(currentElementName)) {
            itf.setDependency(driverParamName, attributes.getValue("name"));
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/".equals(currentElementName)) {
            impl = itf.addImplementation(attributes.getValue("name"));
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/interface/oparam/".equals(currentElementName)) {
            CNamingService namingService = impl.getNamingService();
            CInterfaceOutputParameter param = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + attributes.getValue("name"));
            param.getMapping().setMappingScript(attributes.getValue("mappingScript"));
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/relation/".equals(currentElementName)) {
            String type = attributes.getValue("type");
            String relName = attributes.getValue("name");
            String relAlias = attributes.getValue("relAlias");
            if ("local".equalsIgnoreCase(type)) {
                rel = impl.addLocalRelation(relName, relAlias);
            } else {
                rel = impl.addRemoteRelation(relName, relAlias);
            }
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/relation/iparam/".equals(currentElementName)) {
            /* CRelationInputParameter */
            param = rel.addInputParameter(attributes.getValue("name"));
            param.setDataType(attributes.getValue("type"));
            param.setUnit(attributes.getValue("unit"));
            param.setDefaultValue(attributes.getValue("defaultValue"));
            //System.out.println("@@@@@@@@ : " + attributes.getValue("defaultValue") + " ===> " + param);
            ((CRelationInputParameter) param).getMapping().setMappingScript(attributes.getValue("mappingScript"));
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/relation/oparam/".equals(currentElementName)) {
            /* CRelationOutputParameter */
            param = rel.addOutputParameter(attributes.getValue("name"));
            param.setDataType(attributes.getValue("type"));
            param.setUnit(attributes.getValue("unit"));
            param.setDefaultValue(attributes.getValue("defaultValue"));
            //System.out.println("@@@@@@@@ : " + attributes.getValue("defaultValue") + " ===> " + param);
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/relation/relationScript/".equals(currentElementName)) {
            ((CLocalRelation) rel).setRelationScript(attributes.getValue("script"));
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/relation/connection/".equals(currentElementName)) {
            CRemoteRelation remoteRel = (CRemoteRelation) rel;
            remoteRel.setServerPort(attributes.getValue("server"));
            remoteRel.setUser(attributes.getValue("userid"));
            remoteRel.setPassword(attributes.getValue("password"));
            remoteRel.setSpace(attributes.getValue("space"));
            remoteRel.setInterfacePath(attributes.getValue("path"));
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/relation/dependency/driverparam/".equals(currentElementName)) {
            this.driverParamName = attributes.getValue("name");
        }

        if ("/model/catalog/interfaces/interface/implementations/implementation/relation/dependency/driverparam/drivenparam/".equals(currentElementName)) {
            rel.setDependency(driverParamName, attributes.getValue("name"));
        }
    }



    public CModel getCModel() {
        return model;
    }

    public IDContainer getIDContainer() {
        return idContainer;
    }
}