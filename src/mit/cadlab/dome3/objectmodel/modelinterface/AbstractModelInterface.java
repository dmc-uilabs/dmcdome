// AbstractModelInterface.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.AbstractModelObjectScope;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityChangeListener;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.ImmutableCausalityManager;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.log.Log;
import mit.cadlab.dome3.util.log.LogHandler;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;

public abstract class AbstractModelInterface extends AbstractModelObjectScope
        implements ModelInterface
{
	protected Model model;
	protected Version modelVersion = null;
	protected Version version = new Version(0, 0, 1);
	protected Version lastSavedVersion = new Version(0, 0, 0);
	protected String lastSavedXml = "";
	protected boolean isValidated = false;
	protected boolean isdefaultIface = false;
	protected CausalityManager internalCausalityManager;
	protected LogHandler logHandler = Log.getDefaultLog(); // default, no logging

	public AbstractModelInterface(Model m, Id id)
	{
		super(id);
		setModel(m);
		internalCausalityManager = createInternalCausalityManager();
	}

	public AbstractModelInterface(Model m, Id id, ModelObjectScope mObjScope)
	{
		this(m, id, mObjScope, true);
	}

	public AbstractModelInterface(Model m, Id id, ModelObjectScope mObjScope, boolean copyObjects)
	{
		super(id, mObjScope, copyObjects);
		setModel(m);
		// objects and mappings copied if same model
		// objects only if different model (currently not allowed by gui)
		internalCausalityManager = createInternalCausalityManager();
	}

	public AbstractModelInterface(Model m, Element xmlElement)
	{
		super(xmlElement);
		setModel(m);
		// create causality manager
		Element causalityElement = (Element) xmlElement.selectSingleNode("causality");
		if (causalityElement != null)
			internalCausalityManager = createInternalCausalityManager(xmlElement);
		else
			internalCausalityManager = createInternalCausalityManager();

		// init
		parseInterfaceInfoElement((Element) xmlElement.selectSingleNode("/modelinterface/interfaceinfo"));
	}

    public void cleanup() {
        Set deletedObjectSet = new HashSet();
        Set deletedModelObjectScopeSet = new HashSet();

        for (int i = 0; i < modelObjects.size(); i++) {
            ModelObject mObj = (ModelObject) modelObjects.get(i);

            if (mObj instanceof Parameter) {
                deletedObjectSet.add(mObj);
            } else if (mObj instanceof Subscription) {
                deletedModelObjectScopeSet.add(mObj);
            } else if (mObj instanceof Relation) {
                deletedModelObjectScopeSet.add(mObj);
            } else if (mObj instanceof DefaultContextBuilder) {
                List mObjReferences = ((DefaultContextBuilder) mObj).getModelObjectReferences();
                for (int j = 0; j < mObjReferences.size(); j++) {
                    ModelObject mObjInBuilder = (ModelObject) mObjReferences.get(j);
                    if (mObjInBuilder instanceof Parameter) {
                        deletedObjectSet.add(mObjInBuilder);
                    } else if (mObjInBuilder instanceof Subscription) {
                        deletedModelObjectScopeSet.add(mObjInBuilder);
                    } else if (mObjInBuilder instanceof Relation) {
                        deletedModelObjectScopeSet.add(mObjInBuilder);
                        if (mObjInBuilder instanceof AbstractProceduralRelation) {
                            ((AbstractProceduralRelation) mObjInBuilder).clearRelationExecutor();
                        }
                    }
                }
            }
        }

        for (Iterator j = deletedObjectSet.iterator(); j.hasNext(); ) {
            try {
                ((ModelObject) j.next()).delete(null);
            } catch (NoSuchElementException e) { System.err.println(e); }
        }

//        for (Iterator j = deletedModelObjectScopeSet.iterator(); j.hasNext(); ) {
//            try {
//                ModelObjectScope scope = (ModelObjectScope) j.next();
//                scope.deleteAllModelObjects();
//                //scope.delete(null);
//            } catch (NoSuchElementException e) { System.err.println(e); }
//        }
    }

	protected abstract CausalityManager createInternalCausalityManager();

	protected abstract CausalityManager createInternalCausalityManager(Element xmlElement);

	public CausalityManager getCausalityManager()
	{
		return new ImmutableCausalityManager(internalCausalityManager);
	}

	// ModelInterface interface
	public LogHandler getLogHandler()
	{
		return logHandler;
	}

	public void setLogHandler(LogHandler log)
	{
		if (log == null)
			throw new AbstractDomeObject.DomeObjectException("AbstractModelInterface: setLogHandler", "null log handler");
		this.logHandler = log;
	}

	// CausalitySupport interface

	public List getItems(CausalityStatus causality)
	{
		return getCausalityManager().getItems(causality);
	}

	public boolean isItemOfCausality(Object obj, CausalityStatus causality)
	{
		return getCausalityManager().isItemOfCausality(obj, causality);
	}

	public CausalityStatus getCausality(Object obj)
	{
		return getCausalityManager().getCausality(obj);
	}

	public void addCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(l);
	}

	public void removeCausalityChangeListener(CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(l);
	}

	public void addCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().addCausalityChangeListener(obj, l);
	}

	public void removeCausalityChangeListener(Object obj, CausalityChangeListener l)
	{
		getCausalityManager().removeCausalityChangeListener(obj, l);
	}

	public boolean isValidModelObjectType(String modelObjectType)
	{
		if (model != null) {
			return model.isValidModelObjectType(modelObjectType);
		} else {
			return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter")
			        || modelObjectType.equals("Context") || modelObjectType.equals("Procedural Relation");
		}
	}

	public boolean isValidModelObjectType(ModelObject modelObject)
	{
		if (model != null) {
			return model.isValidModelObjectType(modelObject);
		} else {
			String modelObjectType = modelObject.getTypeName();
			return Registry.isValidDataObjectType(modelObjectType) || modelObjectType.equals("Parameter")
			        || modelObjectType.equals("Context") || modelObjectType.equals("Procedural Relation");
		}
	}

	// ModelObject interface

	public Model getModel()
	{
		return model;
	}

	private void setModel(Model model)
	{
		if (this.model != null)
			throw new AbstractDomeObject.DomeObjectException("setModel", "can not change model of ModelObject!");
		this.model = model;
	}

	public TypeInfo getTypeInfo()
	{
		return DomeModelInterface.TYPE_INFO;
	}

	public Version getModelVersion()
	{
		return modelVersion;
	}

	public Version getVersion()
	{
		return version;
	}

	public void validate()
	{
		// TODO: implement this
	}

	public boolean isValidated()
	{
		return isValidated;
	}

	public boolean isDefaultInterface()
	{
		return isdefaultIface;
	}

	protected boolean hasChanged(Document xmlDoc)
	{
		String newXml = xmlDoc.asXML();
		if (lastSavedXml.equals(newXml))
			return false;
		return true;
	}

	public String getXmlTag()
	{
		return ModelInterface.XML_TAG;
	}

	protected abstract Document createXmlDocument();

	protected Element createInterfaceInfoElement()
	{
		Element xml = DocumentHelper.createElement("interfaceinfo");
		xml.add(version.toXmlElement());
		if (isDefaultInterface())
			xml.add(DocumentHelper.createElement(DEFAULT_INTERFACE_XML_TAG));
		return xml;
	}

	protected void parseInterfaceInfoElement(Element xmlElement)
	{
		if (xmlElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml interface info");
		Element versionElement = (Element) xmlElement.selectSingleNode("version");
		if (versionElement == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml version: " + xmlElement.asXML());
		version = new Version(versionElement);
		Element defaultIfaceElement = (Element) xmlElement.selectSingleNode(DEFAULT_INTERFACE_XML_TAG);
		if (defaultIfaceElement != null)
			isdefaultIface = true;
	}

	protected void addXmlContent(Element xmlElement)
	{
		xmlElement.add(createInterfaceInfoElement());
	}

	protected String contentToString()
	{
		return "  version: " + version.toString();
	}

}
