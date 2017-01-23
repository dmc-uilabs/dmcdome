package mit.cadlab.dome3.objectmodel.modelinterface;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.ModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelRuntime;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ModelParameterRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectServerRuntime;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.util.ClassUtils;
import mit.cadlab.dome3.util.AggregatorMap;
import mit.cadlab.dome3.plugin.PluginModelRuntime;
import org.dom4j.Element;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 28, 2003
 * Time: 4:57:55 PM
 * To change this template use Options | File Templates.
 */
public class ModelInterfaceRuntimeServer extends AbstractModelInterfaceRuntime
{
	private int referenceCount = 0;

	public ModelInterfaceRuntimeServer(Model m, Id id)
	{
		super(m, id);
		runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
	}

	public ModelInterfaceRuntimeServer(Model m, String id)
	{
		super(m, id);
		runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
	}

	public ModelInterfaceRuntimeServer(Model m, Id id, ModelObjectScope mObjScope)
	{
		super(m, id, mObjScope);
		runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
	}

	public ModelInterfaceRuntimeServer(CompoundId parentId, Model m, Element xmlContent, Element xmlMappings)
	{
		super(m, xmlContent, xmlMappings);
		if (parentId != null)
			runtimeId = new CompoundId(parentId);
		if (runtimeId.getInterfaceRuntimeId() == null)
			runtimeId.setInterfaceRuntimeId(UUIDGenerator.create());
		populateInterfaceObjectFlatMap();
		makeInterfaceAndModelConsistent();
	}

	public CompoundId getRuntimeId()
	{
		return runtimeId;
	}

	public void capture ()
	{
		++referenceCount;
	}

	public int release ()
	{
		return --referenceCount;
	}

	public Hashtable getParameterSystemCausality()
	{
		Collection params = this.getModelObjectParameters();
		Hashtable ht = null;
		Model m = getModel();
		if (m instanceof DomeModelBase)
			ht = ((DomeModelBase) m).getSystemCausality(getInterfaceParamToModelParamMap());
		else if (m instanceof IntegrationProjectServerRuntime)
			ht = ((IntegrationProjectServerRuntime) m).getParameterSystemCausality(params);  //TODO need to reimplement this method
		else {
			ht = new Hashtable(); // xml-rpc can not return null value
			System.err.println("ModelInterfaceRuntimeServer.getParameterSystemCausality unknown model type: " + ClassUtils.getClassName(m));
		}
		return ht;
	}

	protected void makeInterfaceAndModelConsistent() {
		Boolean shouldSetInterfaceValuesInModel = shouldSetInterfaceValueInModel();
		if (shouldSetInterfaceValuesInModel == Boolean.TRUE) {
			suspendStatusPropagation();
			AggregatorMap changedInputValues = new AggregatorMap(); // key = model; value = list of changed input parameters

			// only set the inputs and read the outputs
			Iterator mObjs = getModelObjects().iterator();
			ModelObject mObj;
			InterfaceParameterRuntime ifaceParam;
			ModelParameterRuntime mdlParam;
			CausalityStatus cs;
			while (mObjs.hasNext()) {
				mObj = (ModelObject) mObjs.next();
				if (mObj instanceof InterfaceParameterRuntime) {
					ifaceParam = (InterfaceParameterRuntime)mObj;
					mdlParam = (ModelParameterRuntime)getModelParameterForInterfaceParameter(ifaceParam);
					if (mdlParam == null)
						continue;
					cs = model.getCausality(mdlParam);
					if (CausalityStatus.INDEPENDENT.equals(cs) || CausalityStatus.INDETERMINATE.equals(cs)) {
						mdlParam.setInitialValue(ifaceParam.getCurrentDataObject()); // do it this way so solver will accept value
						changedInputValues.put(mdlParam.getModel(), mdlParam);
					}
					else
						ifaceParam.setValues(mdlParam.getCurrentDataObject()); // do it this way to avoid flow back to model
					ifaceParam.setValueStatus(mdlParam.getValueStatus()); // load value status of modelparam into ifaceparam
				}
			}

			markAffectedParametersInconsistent(changedInputValues);
			doAdvancedNotificationAndResumeStatusPropagation();
		}
		else if (shouldSetInterfaceValuesInModel == Boolean.FALSE) {
			// set the model values in the interface
			Iterator mObjs = getModelObjects().iterator();
			ModelObject mObj;
			InterfaceParameterRuntime ifaceParam;
			Parameter mdlParam;
			while (mObjs.hasNext()) {
				mObj = (ModelObject) mObjs.next();
				if (mObj instanceof InterfaceParameterRuntime) {
					ifaceParam = (InterfaceParameterRuntime) mObj;
					mdlParam = getModelParameterForInterfaceParameter(ifaceParam);
					if (mdlParam == null)
						continue;
					ifaceParam.setValues(mdlParam.getCurrentDataObject()); // do it this way to avoid flow back to model
					ifaceParam.setValueStatus(mdlParam.getValueStatus()); // load value status of modelparam into ifaceparam
				}
			}
		} // otherwise, do nothing
	}

	/**
	 * Should the interface values be set in the model or should the model values
	 * be set in the interface, or neither.
	 * @return Boolean.TRUE if interface values should be set in model
	 * Boolean.FALSE if model values should be set in interface
	 * null if neither value should be set in the other
	 */
	protected Boolean shouldSetInterfaceValueInModel()
	{
		if ((model instanceof ModelRuntime) &&
		        (((ModelRuntime) model).isProjectResource() || ((ModelRuntime) model).hasAtLeastOneInterfaceLoaded()))
			return Boolean.FALSE; // load values from model to interface
        else if ((model instanceof DomeModelRuntime) &&
                (((DomeModelRuntime) model).isIntegrationModel() && ((DomeModelRuntime) model).isProjectResource())) //in case of nested i-model subscription _i
            return Boolean.FALSE;
		else if ((model instanceof IntegrationProjectServerRuntime) &&
		        ((IntegrationProjectServerRuntime) model).isProjectResource())
			return Boolean.FALSE; // load values from model to interface
		else if ((model instanceof IntegrationProjectServerRuntime) ||
		        ((model instanceof DomeModelRuntime) && ((DomeModelRuntime) model).isIntegrationModel())) {
			IntegrationProjectServerRuntime project;
			if (model instanceof IntegrationProjectServerRuntime)
				project = (IntegrationProjectServerRuntime) model;
			else
				project = (IntegrationProjectServerRuntime) ((DomeModelRuntime) model).getIntegrationProject();
			if (project.hasAtLeastOneProjectOrIModelInterfaceLoaded())
				return Boolean.FALSE; // load values from model to interface

     			return Boolean.TRUE; // load input values from interface to model
		}
		else if (model instanceof ModelRuntime) {
			return Boolean.TRUE; // load input values from interface to model
		}
		else { // other types of models, tools?
			return null;
		}
	}

	/**
	 * Set interface parameters to values sent from a client.
	 * An empty list represents no new values for this interface.
	 * @param changedValues key is parameter id string; value is value
	 */
	public void setValues(Hashtable changedValues) {
		suspendStatusPropagation();
		AggregatorMap changedInputValues = new AggregatorMap(); // key = model; value = list of changed input parameters

		Iterator changes = changedValues.entrySet().iterator();
		Map.Entry entry;
		CompoundId paramId;
		String pId;
		while (changes.hasNext()) {
			entry = (Map.Entry) changes.next();
			paramId = new CompoundId((String)entry.getKey());
			List values = (List) entry.getValue();
			if (values.isEmpty())
				break; // this interface does not have any values to set
			// get the interface parameter
			InterfaceParameterRuntime ifaceParam = null;
			ModelParameterRuntime mdlParam;
			pId = paramId.getDataObjectStaticId();
			if (pId != null) {
				ifaceParam = (InterfaceParameterRuntime) getModelObjectById(new Id(pId));
				if (ifaceParam != null) {
					// set the new value for the parameter. 
					// If the value is a small file, the file contents are in the <values> variable. If the value is a large file,
					// the <values> variable instead contains a URL indicating where to get the file. 
					Object currentDataObject = ifaceParam.getCurrentDataObject();
					
					ifaceParam.getCurrentDataObject().setValues(values);
					mdlParam = (ModelParameterRuntime) getModelParameterForInterfaceParameter(ifaceParam);
					if (mdlParam == null)
						continue;
					changedInputValues.put(mdlParam.getModel(), mdlParam);
				}
			}
		}

		markAffectedParametersInconsistent(changedInputValues);
		doAdvancedNotificationAndResumeStatusPropagation();
	}

	public void setParametersInconsistent(Vector paramIds) {
		markModelWaitingToBeExecuted();
		suspendStatusPropagation();
		AggregatorMap changedInputValues = new AggregatorMap(); // key = model; value = list of changed input parameters

		InterfaceParameterRuntime ifaceParam;
		ModelParameterRuntime mdlParam;
		CompoundId paramId;
		String pId;
		for (int i = 0; i < paramIds.size(); i++) {
			paramId = (CompoundId) paramIds.elementAt(i);
			pId = paramId.getDataObjectStaticId();
			if (pId != null) {
				ifaceParam = (InterfaceParameterRuntime) getModelObjectById(new Id(pId));
				if (ifaceParam != null) {
					ifaceParam.setValueStatus(Parameter.VALUE_STATUS_INCONSISTENT);
					mdlParam = (ModelParameterRuntime) getModelParameterForInterfaceParameter(ifaceParam);
					if (mdlParam == null)
						continue;
					changedInputValues.put(mdlParam.getModel(), mdlParam);
				}
			}
		}
		markAffectedParametersInconsistent(changedInputValues);
		doAdvancedNotificationAndResumeStatusPropagation();
	}

	private void suspendStatusPropagation() {
		Model m = getModel();
		if (m instanceof IntegrationProjectServerRuntime) {
			((IntegrationProjectServerRuntime) m).suspendStatusPropagation();
		}
		else if (m instanceof PluginModelRuntime) {
			((PluginModelRuntime) m).suspendStatusPropagation();
		}
		else if (m instanceof DomeModelRuntime) {
			((DomeModelRuntime) m).suspendStatusPropagation();
		}
	}

	private void markModelWaitingToBeExecuted()
	{
		Model m = getModel();
		if (m instanceof IntegrationProjectServerRuntime) {
			((IntegrationProjectServerRuntime) m).markModelWaitingToBeExecuted();
		}
		else if (m instanceof PluginModelRuntime) {
			((PluginModelRuntime) m).markModelWaitingToBeExecuted();
		}
		else if (m instanceof DomeModelRuntime) {
			((DomeModelRuntime) m).markModelWaitingToBeExecuted();
		}
	}

	private void markAffectedParametersInconsistent(AggregatorMap modelsAndInconsistentParameters) {
		Iterator parameterModels = modelsAndInconsistentParameters.keySet().iterator();
		DomeModelBase modelBase;
		List changedInputParameters;
		while (parameterModels.hasNext()) {
			modelBase = (DomeModelBase) parameterModels.next();
			changedInputParameters = (List) modelsAndInconsistentParameters.get(modelBase);
			modelBase.markAffectedParameters(changedInputParameters);
		}
	}

	private void doAdvancedNotificationAndResumeStatusPropagation() {
		Model m = getModel();
		if (m instanceof IntegrationProjectServerRuntime) {
			IntegrationProjectServerRuntime project = (IntegrationProjectServerRuntime) m;
			Iterator imodelInfos = project.getIntegrationModels().iterator();
			ProjectIntegrationModelInfo pimi;
			DomeModelRuntime imodel;
			while (imodelInfos.hasNext()) {
				pimi = (ProjectIntegrationModelInfo) imodelInfos.next();
				imodel = (DomeModelRuntime) pimi.getModel();
				imodel.doAdvancedNotification();
			}
			((IntegrationProjectServerRuntime) m).resumeStatusPropagation();
		}
		else if (m instanceof PluginModelRuntime) {
			((PluginModelRuntime) m).resumeStatusPropagation();
		}
		else if (m instanceof DomeModelRuntime) {
			DomeModelRuntime model = (DomeModelRuntime) m;
			if (model.isIntegrationModel())
				model.doAdvancedNotification();
			model.resumeStatusPropagation();
		}
	}
}
