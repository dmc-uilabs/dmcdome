package mit.cadlab.dome3.plugin.catalog;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.*;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.CDataObject;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2005. 9. 6.
 */
public class CatalogModelRuntime extends PluginModelRuntime {
    protected CatalogPlugin plg;
    private CModel catalogModel;

    public CatalogModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource) {
        super(parentId, xml, isProjectResource);
        loadNativeModel(xml);
    }

    protected void executeNativePlugin(List affectedOutputParams) {
        if (!plg.isModelLoaded()) {
            plg.loadModel();
        }
        plg.execute(affectedOutputParams);
    }

    /**
     * Halt the native model.
     */
    public void stopModel() {
        plg.unloadModel();
    }

    public void deleteModel() {
        if (solver.isSolving()) {
            solver.stopSolving();
            waitingToDie = Boolean.TRUE;
            return;
        }
        plg.deleteModel();
        super.deleteModel();
    }

    protected void loadNativeModel(Element xml) {
//        String fileName = getMainModelFileName();
//        if (fileName == null) {
//            throw new UnsupportedOperationException("can not start catalog model - no filename");
//        }
        catalogModel = this.createCatalogMode(xml);

        plg = new CatalogPlugin(this);

        /* create catalog objects that are mapped to dome object */
        Iterator it = getModelObjects().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Parameter) {
                Parameter p = (Parameter) o;
                createCatalogDataObject(p);
            }
        }
    }

    public CModel getCatalogModel() {
        return catalogModel;
    }

    private CModel createCatalogMode(Element xml) {
        Element catalogElement = (Element) xml.selectSingleNode("/model/catalog");
		//XMLUtils.makeRootElement(modelElement);
        String catModelName = catalogElement.attributeValue("name");
        CModel model = new CModel(catModelName);

        List interfaceElements = xml.selectNodes("/model/catalog/interfaces/interface");
        for (Iterator i = interfaceElements.iterator(); i.hasNext();) {
            Element itfElement = (Element) i.next();
            CInterface itf = model.addInterface(itfElement.attributeValue("name"));
            List iparamList = itfElement.elements("iparam");
            for (int j = 0; j < iparamList.size(); j++) {
                Element paramElement = (Element) iparamList.get(j);
                CInterfaceInputParameter param = itf.addInputParameter(paramElement.attributeValue("name"));
                param.setDataType(paramElement.attributeValue("type"));
                param.setUnit(paramElement.attributeValue("unit"));
                param.setDefaultValue(paramElement.attributeValue("defaultValue"));
            }

            List oparamList = itfElement.elements("oparam");
            for (int j = 0; j < oparamList.size(); j++) {
                Element paramElement = (Element) oparamList.get(j);
                CInterfaceOutputParameter param = itf.addOutputParameter(paramElement.attributeValue("name"));
                param.setDataType(paramElement.attributeValue("type"));
                param.setUnit(paramElement.attributeValue("unit"));
                param.setDefaultValue(paramElement.attributeValue("defaultValue"));
            }

            List driverElemList = itfElement.element("dependency").elements("driverparam");
            for (int j = 0; j < driverElemList.size(); j++) {
                Element driverElement = (Element) driverElemList.get(j);
                String driverParamName = driverElement.attributeValue("name");
                List drivenParamElemList = driverElement.elements("drivenparam");
                for (int k = 0; k < drivenParamElemList.size(); k++) {
                    Element drivenElement = (Element) drivenParamElemList.get(k);
                    String drivenParamName = drivenElement.attributeValue("name");
                    itf.setDependency(driverParamName, drivenParamName);
                }
            }

            List implElemList = itfElement.element("implementations").elements("implementation");
            for (int j = 0; j < implElemList.size(); j++) {
                Element implElement = (Element) implElemList.get(j);
                CImplementation impl = itf.addImplementation(implElement.attributeValue("name"));
                //speedup Clog.debug("implementation added : " + impl);
                List oparamElemList = implElement.element("interface").elements("oparam");
                for (int k = 0; k < oparamElemList.size(); k++) {
                    Element oparamElement = (Element) oparamElemList.get(k);
                    CNamingService namingService = impl.getNamingService();
                    //System.out.println("get you you : " + CConstant.ITF_ALIAS + "." + oparamElement.attributeValue("name"));
                    CInterfaceOutputParameter param = namingService.getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + oparamElement.attributeValue("name"));
                    param.getMapping().setMappingScript(oparamElement.attributeValue("mappingScript"));
                }

                List relElemList = implElement.elements("relation");
                for (int k = 0; k < relElemList.size(); k++) {
                    CRelation rel;
                    Element relElement = (Element) relElemList.get(k);
                    String relType = relElement.attributeValue("type");
                    String relName = relElement.attributeValue("name");
                    String relAlias = relElement.attributeValue("relAlias");
                    if ("local".equalsIgnoreCase(relType)) {
                        rel = impl.addLocalRelation(relName, relAlias);
                    } else {
                        rel = impl.addRemoteRelation(relName, relAlias);
                    }

                    List relIparamList = relElement.elements("iparam");
                    for (int l = 0; l < relIparamList.size(); l++) {
                        Element relIparamElement = (Element) relIparamList.get(l);
                        CRelationInputParameter param = rel.addInputParameter(relIparamElement.attributeValue("name"));
                        param.setDataType(relIparamElement.attributeValue("type"));
                        param.setUnit(relIparamElement.attributeValue("unit"));
                        param.setDefaultValue(relIparamElement.attributeValue("defaultValue"));
                        param.getMapping().setMappingScript(relIparamElement.attributeValue("mappingScript"));
                    }

                    List relOparamList = relElement.elements("oparam");
                    for (int l = 0; l < relOparamList.size(); l++) {
                        Element relOparamElement = (Element) relOparamList.get(l);
                        CRelationOutputParameter param = rel.addOutputParameter(relOparamElement.attributeValue("name"));
                        param.setDataType(relOparamElement.attributeValue("type"));
                        param.setUnit(relOparamElement.attributeValue("unit"));
                        param.setDefaultValue(relOparamElement.attributeValue("defaultValue"));
                    }

                    if ("local".equalsIgnoreCase(relType)) {
                        ((CLocalRelation) rel).setRelationScript(relElement.element("relationScript").attributeValue("script"));
                    } else {
                        CRemoteRelation remoteRel = (CRemoteRelation) rel;
                        Element connElement = relElement.element("connection");
                        remoteRel.setServerPort(connElement.attributeValue("server"));
                        remoteRel.setUser(connElement.attributeValue("userid"));
                        remoteRel.setPassword(connElement.attributeValue("password"));
                        remoteRel.setSpace(connElement.attributeValue("space"));
                        remoteRel.setInterfacePath(connElement.attributeValue("path"));
                    }

                    List relDriverElemList = relElement.element("dependency").elements("driverparam");
                    for (int l = 0; l < relDriverElemList.size(); l++) {
                        Element relDriverElement = (Element) relDriverElemList.get(l);
                        String relDriverParamName = relDriverElement.attributeValue("name");
                        List relDrivenParamElemList = relDriverElement.elements("drivenparam");
                        for (int m = 0; m < relDrivenParamElemList.size(); m++) {
                            Element relDrivenElement = (Element) relDrivenParamElemList.get(m);
                            String relDrivenParamName = relDrivenElement.attributeValue("name");
                            rel.setDependency(relDriverParamName, relDrivenParamName);
                        }
                    }
                }
                impl.getNamingService().updateInputParamNamesOfAllCMappings();
                //System.out.println("param impl naming: " + impl.getNamingService().getParameters());
            }
        }
        Debug.trace(Debug.ALL, "[CATALOG PLUGIN] Created model \"" + model.getName() + "\"");
        return model;
    }

//    /** returns a script variable name mapped to a given parameter. it is invoked in CatalogPlugin.execute(). CDataObject is binded to the returned variable name */
//    public String getMappedScriptVariableName(Parameter p) {
//        return (String) getPluginMappingManager().getMappingObjectForParameter(p);
//    }

    /**
     * create CDataObject which has a reference to a given parameter
     * the reference is used to data exchange between a plugin data object and a java data object
     * when creating catalog data object, its reference is added to a vector called data in CatalogPlugin
     */
    protected CDataObject createCatalogDataObject(Parameter p) {
        CDataObject catDataObj = null;

        if (p.getCurrentType().equals(DomeReal.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createReal(p);
        } else if (p.getCurrentType().equals(DomeInteger.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createInteger(p);
        } else if (p.getCurrentType().equals(DomeString.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createString(p);
        } else if (p.getCurrentType().equals(DomeBoolean.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createBoolean(p);
        } else if (p.getCurrentType().equals(DomeEnumeration.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createEnumeration(p);
        } else if (p.getCurrentType().equals(DomeFile.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createFile(p);
        } else if (p.getCurrentType().equals(DomeMatrix.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createMatrix(p);
        } else if (p.getCurrentType().equals(DomeVector.TYPE_INFO.getTypeName())) {
            catDataObj = (plg).createVector(p);
        }

        boolean isInput = getCausality(p).equals(CausalityStatus.INDEPENDENT);
        if (isInput) {
            catDataObj.setIsInput(true);
        } else {
            catDataObj.setIsInput(false);
        }

        return catDataObj;
    }
}
