package mit.cadlab.dome3.plugin;

import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.plugin.PluginMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.AbstractCausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.ImmutableDependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.InvalidObjectsException;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DSet;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PluginModelBuilder extends DomeModelBuilder implements PluginModel {

    protected String pluginXmlType; // for loading from xml only
    protected DependencyInfo dependencyInfo;
    protected PluginConfiguration pluginConfiguration;
    protected PluginMappingManager pluginMappingManager;
    //protected PluginFilesContextBuilder pluginfiles;
    protected boolean AuxiliaryFilesChanged=false;

    public PluginModelBuilder(String id, String pluginTypeName) {
        super(new Id(id));
        if (pluginTypeName == null)
            throw new IllegalArgumentException("null pluginTypeName");
        pluginConfiguration = PluginUtils.createPluginConfiguration(pluginTypeName, this);
        pluginMappingManager = pluginConfiguration.createPluginMappingManager(this);
        getFilter(DomeModel.PARAMETERS_FILTER).addFilterListener(new ParametersListener());
        setName(pluginConfiguration.getTypeName());

    }

    public PluginModelBuilder(Id id, PluginModelBuilder model) {
        super(id, model);

        throw new UnsupportedOperationException("plugin copy constructor not supported");
    }

    protected PluginModelBuilder(Element xml) {
        super(xml);
    }


    public PluginModelBuilder(String file, Element xml) {
        super(file, xml);
        getFilter(DomeModel.PARAMETERS_FILTER).addFilterListener(new ParametersListener());
    }

    protected void parseHeaderElement(Element xmlElement) {
        super.parseHeaderElement(xmlElement);
        pluginXmlType = xmlElement.attributeValue("pluginType");
        if (pluginXmlType == null)
            throw new IllegalArgumentException(getTypeName() + " - no xml pluginType");
    }

    protected void initModel() {
        super.initModel();
        dependencyInfo = new ImmutableDependencyInfo(new DependencyInfo());
    }

	public ModelObject newModelObject(String modelObjectType) {
		Object[] oArray = null;
		Parameter o = null;
		if (pluginConfiguration.useCustomDatatype()) {
			oArray = pluginConfiguration.createParameter(this, new Id(UUIDGenerator.create()), modelObjectType);
			if (oArray != null && oArray.length > 0) {
				o = (Parameter) oArray[0];
				modelObjects.add(o);

				if (oArray.length == 2) {
					String mapString = (String) oArray[1];
					getPluginMappingManager().addMapping(o, mapString);
				}
				return o;
			}
		}
		return super.newModelObject(modelObjectType);
	}

    public String getPluginTypeName() {
        return pluginConfiguration.getTypeName();
    }

    public String getPluginXmlType() {
        return pluginConfiguration.getXmlType();
    }

    public PluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }

    protected AbstractCausalityManager createCausalityManager() {
        return new PluginModelBuilderCausalityManager();
    }

    public DependencyInfo getDependencyInfo() {
        return dependencyInfo;
    }

    public void setDependencyInfo(DependencyInfo dInfo) {
        if (dInfo == null)
            dInfo = new DependencyInfo();
        if (validateDependencyInfo(dInfo)) {
            DependencyInfo oldDInfo = dependencyInfo;
            dependencyInfo = new ImmutableDependencyInfo(dInfo);
            firePropertyChange(DEPENDENCY_INFO, oldDInfo, dependencyInfo);
        }
    }

    protected boolean validateDependencyInfo(DependencyInfo dInfo) {
        try {
            dInfo.validate();
            List nodes = dInfo.getNodes();
            Collection badNodes = DSet.removeSet(nodes, modelObjects);

            if (badNodes.isEmpty())
                return true;
            else
                throw new InvalidObjectsException(badNodes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public PluginMappingManager getPluginMappingManager() {
        return pluginMappingManager;
    }

    public int getParameterCount() {
        return getFilter(DomeModel.PARAMETERS_FILTER).getItemCount();
    }

    public List getParameters() {
        return getFilter(DomeModel.PARAMETERS_FILTER).getItems();
    }

    //gets used while reading from the xml
    public TypeInfo getTypeInfo() {
        return PluginModel.TYPE_INFO;
    }

    public String getModelExtension() {
        return getPluginXmlType();
    }

    public DirectedGraph createModelGraph() {
        modelGraph = new DirectedGraph(dependencyInfo);
        return modelGraph;
    }

    // change dependency information when parameters are added and removed
    protected class ParametersListener implements DListListener {
        public void intervalChanged(DListEvent e) {
        }

        public void intervalAdded(DListEvent e) {
            addItems(e.getItems());
        }

        public void intervalRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsReplaced(DListEvent e) {
            throw new UnsupportedOperationException("can not set objects in Procedural Relation!");
        }
    }

    protected void addItems(List items) {
        this.setDependencyInfo((new DependencyInfo(dependencyInfo, items, false)));
    }

    protected void removeItems(List items) {
        this.setDependencyInfo(new DependencyInfo(dependencyInfo, items, true));
    }

    public boolean isAuxiliaryFilesChanged(){
        return AuxiliaryFilesChanged;
    }

    public void setAuxiliaryFilesChanged(boolean changed){
        AuxiliaryFilesChanged=changed;
    }

    public Element toXmlElement() {
        //impl in abstractDomeObject
        Element xml = headerToXmlElement();
        xml.addAttribute("pluginType", getPluginXmlType());

        //imple in AbstractModelObjectScope
        addXmlContent(xml);
        if (!doc.isEmpty()) {
            xml.add(doc.toXmlElement());
        }

        //impl in AbstactDomeModel
        Element paramElement, cxtElement, mapElement;
        paramElement = DocumentHelper.createElement("parameters");
        cxtElement = DocumentHelper.createElement("contexts");
        mapElement = DocumentHelper.createElement("mappings");
        xml.add(paramElement);
        xml.add(cxtElement);
        xml.add(mapElement);

        // add parameters, relations and contexts
        for (Iterator iter = modelObjects.listIterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof Parameter) {
                Element param = ((Parameter) obj).toXmlElement();
                paramElement.add(param);
            } else if (obj instanceof Context) {
                Element cxt = ((Context) obj).toXmlElement();
                cxtElement.add(cxt);
            }
        }
        // add mappings
        Element mapSubElements = pluginMappingManager.toXmlElement(this, "modelMappings");
        if (mapSubElements != null)
            mapElement.add(mapSubElements);

        //add the dependency info
        Element depElement = dependencyInfo.toXmlElement();
        xml.add(depElement);

        //add setup related xml
        //for excel this is file name(string), Excel Version (Enumeration) and Run in Foreground(boolean)
        if (pluginConfiguration != null) {
            Element configElement = pluginConfiguration.toXmlElement();
            xml.add(configElement);
        }

        //add AuxFiles
        if (AuxFiles != null) {
            Element auxFileElement = DocumentHelper.createElement("auxfiles");
            for (Iterator itor = AuxFiles.iterator(); itor.hasNext();) {
                AbstractAuxFile f = (AbstractAuxFile) itor.next();
                auxFileElement.add(f.toXmlElement());
            }
            xml.add(auxFileElement);
        }
        return xml;
    }

    public void superSave(String filename) {
        super.superSave(filename);
    }

    public void superSaveNoGui(String filename) {
        super.superSaveNoGui(filename);
    }

    protected void loadXml(Element xmlElement) {
        // create default objects
        initModel();
        loadParameters(xmlElement);
        loadContexts(xmlElement);
        //add for auxFiles
        loadAuxFiles(xmlElement);
        // read config stuff
        pluginConfiguration = PluginUtils.createPluginConfiguration(pluginXmlType, this, getModelObjectFactory(), xmlElement);
        pluginMappingManager = pluginConfiguration.createPluginMappingManager(this);

        loadMappings(xmlElement);

        // read dependencies
        Element dependencies = (Element) xmlElement.selectSingleNode("dependencies");
        DependencyInfo dInfo = new DependencyInfo(this, dependencies);
        setDependencyInfo(dInfo);

        storeXmlAndUpdateDefaultInterface();
    }

    protected void loadMappings(Element xmlElement) {
        // read mappings
        Element mappings = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/mappings/modelMappings");
        if (mappings != null)
            pluginMappingManager.addMappings(mappings);

    }



    protected class PluginModelBuilderCausalityManager
            extends AbstractModelObjectScope.AbstractInternalCausalityManager {
        public PluginModelBuilderCausalityManager() {
            addPropertyChangeListener(PluginModel.DEPENDENCY_INFO, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    processDependencyInfoChange();
                }
            });
        }

        protected CausalityStatus getInitialCausality(Object obj) {
            return CausalityStatus.INDETERMINATE;
        }

        protected void processDependencyInfoChange() {
            Iterator parameters = PluginModelBuilder.this.getModelObjects().iterator();
            while (parameters.hasNext()) {
                Object obj = parameters.next();
                if (obj instanceof Parameter) {
                    CausalityStatus oldstat = getCausality(obj);
                    CausalityStatus newstat = dependencyInfo.getCausality(obj);
                    //in relation gui, user wants to move relation param from output
                    //filter back to input filter - bug fix
                    if (CausalityStatus.RESULT.equals(oldstat) && newstat == null) {
                        newstat = CausalityStatus.INDEPENDENT;
                    }
                    changeCausality(obj, newstat);
                }
            }
        }
    }

}
