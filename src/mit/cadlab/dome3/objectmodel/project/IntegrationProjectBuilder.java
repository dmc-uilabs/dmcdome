// IntegrationProjectBuilder.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.project.build.ProjectBuildPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerBuild;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.util.Version;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.FormatUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class IntegrationProjectBuilder extends AbstractIntegrationProject
{
	public static final String FILE_NAME = "filename";
	protected DArrayList resources, iModels;
    protected DArrayList externalResources; // _i contains resources minus the i-models - used for GUI
	protected HashMap iModelsById;
    protected DSet removedResources;

	// variables of interest only to build mode
	protected String fileName = "";
	protected Version lastSavedVersion = new Version(0, 0, 0);  // version exists in AbstractIntegrationProject
	protected String lastSavedXml = "";                         // xmlDescription exists in AbstractDomeObject
	protected boolean loadingiModelXml;

    /*
     * flag to determine if the integration project builder object is a tool integration project builder
     * this is  not the best way to do it, but for now it is the easiest way, but I need a handle on the
     * tool model to get the model's menus
     */
    protected boolean _isToolProjectBuilder = false;
    protected AnalysisTool _toolModel = null;

	protected iModelSubscriptionChangeListener subscriptionListener = new iModelSubscriptionChangeListener();

	// todo: enforce different names for iModels

	public IntegrationProjectBuilder(Id id)
	{
		super(id);
		this.lastSavedXml = toXmlElement().asXML();
	}

	public IntegrationProjectBuilder(String fileName, Element xmlElement)
	{
		super(xmlElement);
		this.fileName = fileName;
		this.lastSavedVersion = version;
		loadIntegrationModels();
		((ModelInterfaceManagerBuilder) projectInterfaces).loadInterfaces(fileName);
		loadSubscriptions();
		this.lastSavedXml = toXmlElement().asXML();
	}

	public IntegrationProjectBuilder(Element xmlElement)
	{
		super(xmlElement);
	}

	protected void loadIntegrationModels()
	{
		if (iModels.isEmpty())
			return;
		File supportDirectory = getSupportDirectory(fileName);
		loadingiModelXml = true;
		for (int i = 0; i < iModels.size(); i++) {
			ProjectIntegrationModelInfo iModelInfo = (ProjectIntegrationModelInfo) iModels.get(i);
			File iModelFile = new File(supportDirectory, iModelInfo.getName() + "-IMODEL.dml");
			Element iModelXml = XMLUtils.fileToXmlElement(iModelFile);
			DomeModelBuilder iModel = new DomeModelBuilder(this, iModelInfo, iModelFile.getAbsolutePath(), iModelXml);
//			iModelInfo.setModel(iModel);
			iModel.setIntegrationProject(this);   //only in order to add subscription filter
			iModel.addPropertyChangeListener(DomeModel.SUBSCRIPTION_ADDED, subscriptionListener);
			iModel.addPropertyChangeListener(DomeModel.SUBSCRIPTION_DELETED, subscriptionListener);
		}
		loadingiModelXml = false;
	}

	protected void loadSubscriptions()
	{
		for (int i = 0; i < iModels.size(); i++) {
			ProjectIntegrationModelInfo iModelInfo = (ProjectIntegrationModelInfo) iModels.get(i);
			DomeModelBuilder model = (DomeModelBuilder) iModelInfo.getModel();
			List subscriptions = model.getSubscriptions();
			for (int j = 0; j < subscriptions.size(); j++) {
				Subscription subscription = (Subscription) subscriptions.get(j);
				Object info =  getResource(subscription.getResourceId());     //project resource
				if (info == null) {
					//could be iModel
					ProjectIntegrationModelInfo inf = (ProjectIntegrationModelInfo) iModelsById.get(subscription.getResourceId());
					if(inf == null)  {
						throw new IllegalArgumentException("could not synchronize subscription data: " + subscription);
					}
					else {
						inf.addSubscriber(model, subscription.getIfaceId());
					}
				}
				else {
					((BuildProjectResourceInfo)info).addSubscriber(model, subscription.getIfaceId());
				}
			}
		}
	}

	protected void initProject()
	{
		super.initProject();
		resources = new DArrayList();
		iModels = new DArrayList();
		iModelsById = new HashMap();
		removedResources = new DSet();
		mappingManager = new ConnectionMappingManagerBuild(this);
        externalResources = new DArrayList(); // _i
	}

	public ConnectionMappingManager getMappingManager()
	{
		return mappingManager;
	}

	protected void addProjectResourceInfo(ProjectResourceInfo resource)
	{
		resources.add(resource);

        if(!(resource.getType()).equals("imodel")) // _i
            externalResources.add(resource);
	}

	protected void addProjectIntegrationModelInfo(ProjectIntegrationModelInfo imodel)
	{
		iModels.add(imodel);
		iModelsById.put(imodel.getId(), imodel);

       // _i
       //automatically include imodel in resources list:
       //addResourceModel("imodel", imodel.getId(), imodel.getName(), "",null);      //null server connection since imodel resource is in the project itself
	}

	public List getIntegrationModels()
	{
		return iModels;
	}

	public List getResourceModels()
	{
		return resources;
	}

    public List getExternalResourceModels()    // _i
    {
        /*DArrayList resourceList = new DArrayList();
        resourceList = resources;
        for (Iterator i = resources.iterator(); i.hasNext();) {
             ProjectResourceInfo res = (ProjectResourceInfo) i.next();
            if(res.getType().equals("imodel")) {
                resourceList.remove(res);
            }
        }
        return  resourceList; */

        return externalResources;
    }

    public void setIsToolProjectBuilder(boolean value)
    {
        _isToolProjectBuilder = value;
    }

    public boolean getIsToolProjectBuilder()
    {
        return _isToolProjectBuilder;
    }

    public void setToolModel(AnalysisTool toolModel)
    {
        _toolModel = toolModel;
    }

    public AnalysisTool getToolModel()
    {
        return _toolModel;
    }

	public ProjectResourceInfo getResource(String id)
	{
		for (int i = 0; i < resources.size(); i++) {
			ProjectResourceInfo projectResourceInfo = (ProjectResourceInfo) resources.get(i);
			if (projectResourceInfo.getResourceUniqueId().equals(id))
				return projectResourceInfo;
			else {
				//if resource is a project
				if(projectResourceInfo.getType().equals(ProjectResourceInfo.PROJECT_RESOURCE)) {
					//and resource's contents contain the id of the resource model
					//being subscribed
					if(projectResourceInfo.getResourceContents().contains(id)) {
						return projectResourceInfo;
					}
				}
			}
		}
		return null;
	}

	public ProjectIntegrationModelInfo getIntegrationModel(String id)
	{
		for (int i = 0; i < iModels.size(); i++) {
			ProjectIntegrationModelInfo projectIntegrationModelInfo = (ProjectIntegrationModelInfo) iModels.get(i);
			if (projectIntegrationModelInfo.getId().equals(id))
				return projectIntegrationModelInfo;
		}
		return null;
	}

	protected ModelInterfaceManager createInterfacesManager()
	{
		return new ModelInterfaceManagerBuilder(this);
	}

//TODO uncomment later
//	public ModelInterfaceBuilder newInterface()
//	{
//		ModelInterfaceBuilder iface = ((ModelInterfaceManagerBuilder)projectInterfaces).newInterface();
//		return iface;
//	}

	public void addResourceModel(String type, String resourceId, String name, String description, ServerConnection svrConn)
	{
		if(svrConn != null){
		addProjectResourceInfo(new BuildProjectResourceInfo(type, resourceId, name, description, svrConn));
	}
        else {
        //for internal imodel resource _i
        addProjectResourceInfo(new BuildProjectResourceInfo(type, resourceId, name, description));
        }
	}

	public void relocateResourceModel(BuildProjectResourceInfo oldResource,
	                                  String type, String resourceId, String name,
	                                  String desc, double version, ServerConnection svrConn, HashMap ifaceDeployIdMap)
	{
		projectResourceModelsbyDeployId.remove(oldResource.getResourceDeployId());   //remove old id entry
		projectResourceModelsbyDeployId.put(resourceId, oldResource); //add new id entry
		oldResource.relocateResource(type, resourceId, name, desc, version, svrConn, ifaceDeployIdMap);

	}

	public void removeResourceModel(ProjectResourceInfo resource, boolean isSubscribed)
	{
		externalResources.remove(resource); // _i

		if (resources.remove(resource) && isSubscribed) {
			//clean up removedResources list as the use of the previously stored ids is over
			removedResources.clear();
			removedResources.add(resource.getResourceUniqueId());
			// remove all uses of resources in integration models
			((BuildProjectResourceInfo)resource).removeAllSubscriptionsandMappings();
		}
	}

	public DomeModelBuilder newIntegrationModel()
	{
		DomeModelBuilder imodel = new DomeModelBuilder(new Id(UUIDGenerator.create()), true);
		imodel.setName("iModel");
		imodel.setIntegrationProject(this);
		addProjectIntegrationModelInfo(new BuildProjectIntegrationModelInfo(imodel));
		imodel.addPropertyChangeListener(DomeModel.SUBSCRIPTION_ADDED, subscriptionListener);
		imodel.addPropertyChangeListener(DomeModel.SUBSCRIPTION_DELETED, subscriptionListener);

        // include in resource list _i
        addResourceModel("imodel", (new BuildProjectIntegrationModelInfo(imodel)).getId(), imodel.getName(), "",null);

		return imodel;
	}

	public void removeIntegrationModel(ProjectIntegrationModelInfo imodel)
	{
		if (iModels.remove(imodel)) {
			iModelsById.remove(imodel.getModel().getId().getIdString());
			imodel.removePropertyChangeListener(DomeModel.SUBSCRIPTION_ADDED, subscriptionListener);
			imodel.removePropertyChangeListener(DomeModel.SUBSCRIPTION_DELETED, subscriptionListener);
			imodel.removeAllSubscriptions();
            ((DomeModelBuilder)imodel.getModel()).broadCastiModelDeleted();
			// todo: remove all uses of model in other integration models
		}
	}

	public void copyPasteObjects(List items) {
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			if (o instanceof BuildProjectResourceInfo) {
				ProjectResourceInfo newInfo = new BuildProjectResourceInfo((BuildProjectResourceInfo) o);
				resources.add(newInfo);
                externalResources.add(newInfo); // _i
			} else if (o instanceof ProjectIntegrationModelInfo) {
				ProjectIntegrationModelInfo newInfo = new BuildProjectIntegrationModelInfo((BuildProjectIntegrationModelInfo) o);
				iModels.add(newInfo);
			}
		}
	}

	public void copyPasteObjects(List items, int childIndex)
	{
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			if(o instanceof BuildProjectResourceInfo) {
				ProjectResourceInfo newInfo = new BuildProjectResourceInfo((BuildProjectResourceInfo)o);
				resources.add(childIndex, newInfo);
                externalResources.add(childIndex, newInfo); // _i
			}
			else if(o instanceof ProjectIntegrationModelInfo) {
				ProjectIntegrationModelInfo newInfo = new BuildProjectIntegrationModelInfo((BuildProjectIntegrationModelInfo) o);
				iModels.add(childIndex, newInfo);
			}
		}
	}

	public void delete(DeletionListener notifier)
	{
		if (!isDeleted) {
			for (int i = 0; i < resources.size(); i++) {
				((BuildProjectResourceInfo) resources.get(i)).releaseServerConnection();
				super.delete(notifier); // if already deleted, no nee to super.delete()
			}
		}
	}

	// methods for save support

	public static String fixProjectFileName(String newFileName)
	{
		if (DomeFileChooser.PROJECT_FILE_EXTENSION.equals(DomeFileChooser.getExtension(newFileName)))
			return newFileName;
		else
			return newFileName + "." + DomeFileChooser.PROJECT_FILE_EXTENSION;
	}

	public String getFileName()
	{
		return fileName;
	}

	protected void setFileName(String newFileName)
	{
		String oldFileName = fileName;
		fileName = fixFileName(newFileName);
		firePropertyChange(FILE_NAME, oldFileName, fileName);
	}

	public String fixFileName(String newFileName)
	{
		return fixProjectFileName(newFileName);
	}

	public boolean isSaved()
	{
		// check if interfaces saved
		if (projectInterfaces.hasChanged())
			return false;
		// are models saved?
		for (int i = 0; i < iModels.size(); i++) {
			ProjectIntegrationModelInfo modelInfo = (ProjectIntegrationModelInfo) iModels.get(i);
			if (!((DomeModelBuilder) modelInfo.getModel()).isSaved())
				return false;
		}
		String newxml = toXmlElement().asXML();
		return lastSavedXml.equals(newxml);
	}

	public void save()
	{
		Element currentXml = toXmlElement();
		if (version.compareTo(lastSavedVersion) == 1) { // version is newer
			lastSavedVersion = version.duplicate();
		}
		else { // change version if content is different
			if (!currentXml.asXML().equals(lastSavedXml)) { // increment version
				version.revSaveVersion();
				Element versionNode = (Element) currentXml.selectSingleNode("/project/projectinfo/version");
				versionNode.setText(version.toString());
				lastSavedVersion = version.duplicate();
			}
		}
		boolean success = false;
		//if savestatus = 1 do not show any dilog since save succeeds in the first attempt
		//if savestatus = 0 show save successful dilog since save succeeds in more than one attempt
		//if savestatus = -1 show qutting without save dilog since saving is abandoned
		int savestatus = 1;
		do {
			try {
				XMLUtils.writeToFile(currentXml, fileName);
				success = true;
			}
			catch(Exception e) {
				success = false;
				String msg1 = e.getMessage();
				String msg2 = "Do you want to try to save the project again?";
				int ans = TwoButton2Msg.showError(null, "Error: Save", msg2, msg1, "OK", "Cancel", new Dimension(1, 1));
				if (ans == TwoButton1Msg.RIGHT_OPTION) {
					success = true; //to quit the while loop without saving
					savestatus = -1;
				}
				else if (ans == TwoButton1Msg.LEFT_OPTION) {
					savestatus = 0;
				}
			}
			lastSavedXml = currentXml.asXML();
			try {
				saveInterfacesAndIntegrationModels();
				success = true;
			}
			catch (Exception e) {
				success = false;
				String msg1 = e.getMessage();
				String msg2 = "Do you want to try to save the project again?";
				int ans = TwoButton2Msg.showError(null, "Error: Save", msg2, msg1, "OK", "Cancel", new Dimension(1, 1));
				if (ans == TwoButton1Msg.RIGHT_OPTION) {
					success = true; //to quit the while loop without saving
					savestatus = -1;
				}
				else if (ans == TwoButton1Msg.LEFT_OPTION) {
					savestatus = 0;
				}
			}
		}
		while (!success) ;
		if (savestatus == 0) {
			OneButton1Msg.showOption(null, "Project Save", "Project saved", "OK", new Dimension(1, 1));
		}
		else if(savestatus == -1) {
			OneButton1Msg.showOption(null, "Project Save", "Quitting without saving the project", "OK", new Dimension(1, 1));
		}
	}

	protected void saveInterfacesAndIntegrationModels() throws IOException
	{
		File supportDirectory = getSupportDirectory(fileName);
		if (supportDirectory.exists()) { // delete all contents except CVS
            File[] files = supportDirectory.listFiles();
            for(int i = 0; i < files.length; i++) {
	            if(files[i].isDirectory()) {
		            //TODO if imodel is renamed move the cvs info in the folder with old name to the
		            //TODO folder with new name and then delete the old folder
                	String dirName = files[i].getName();
		            String start = "interfaces-";
		            int index = dirName.indexOf(start);
		            if(index != -1) {
			            String dirNameSub = dirName.substring(index + start.length());
			            int ind = dirNameSub.indexOf("-");
			            String id = dirNameSub.substring(ind + 1);
			            if(iModelsById.containsKey(id)) {
							File[] subfiles = files[i].listFiles();
							for(int j = 0; j < subfiles.length; j++) {
								if(!subfiles[j].isDirectory()) //do not delete CVS folder
									subfiles[j].delete();
							}
				        }
			            else {
				            File[] subfiles = files[i].listFiles();
				            for (int j = 0; j < subfiles.length; j++) {
					            // delete CVS folder as well as other files
						            subfiles[j].delete();
				            }
				            files[i].delete();
			            }
		            }
	            }
	            else {
		            files[i].delete();  //delete imodel files, they will be saved again (see below)
	            }
            }
		} else {
			supportDirectory.mkdir();
		}
		List ifaces = getInterfaces();
		if (!ifaces.isEmpty()) {
			File ifaceDir = getInterfaceDirectory(supportDirectory, fileName, getId().getIdString());
			ifaceDir.mkdir();
			for (int i = 0; i < ifaces.size(); i++) {
				ModelInterfaceBuilder iface = (ModelInterfaceBuilder) ifaces.get(i);
				iface.save(new File(ifaceDir, iface.getId() + "." + DomeFileChooser.PROJECTINTERFACE_FILE_EXTENSION).getAbsolutePath());
			}
		}
		List imodels = getIntegrationModels();
		for (int i = 0; i < imodels.size(); i++) {
			DomeModelBuilder imodel = (DomeModelBuilder) ((ProjectIntegrationModelInfo) imodels.get(i)).getModel();
			imodel.save(new File(supportDirectory, imodel.getName() + "-IMODEL.dml").getAbsolutePath());
		}
	}

	public static File getSupportDirectory(String fileName)
	{
		File projFile = new File(fileName);
		String fName = projFile.getName();
		fName = fName.substring(0, fName.lastIndexOf("."));
		return new File(projFile.getParentFile(), fName + "-resources");
	}

	public static File getInterfaceDirectory(File supportDirectory, String fileName, String id)
	{
		File projFile = new File(fileName);
		String fName = projFile.getName();
		fName = fName.substring(0, fName.lastIndexOf("."));
		return new File(supportDirectory, "interfaces-" + fName + "-" + id);
	}

	public void saveAs(String fileName, boolean closeAfterSave)
	{
        fileName = fixFileName(fileName);
		// check if file already exists
		File file = new File(fileName);
		if (file.exists()) {
			String msg = null;
			msg = "File <" + file.getName() + "> already exists. Replace it?";
			int button = TwoButton1Msg.showOption(null,
			                                      "Warning: File exists", msg, "Replace",
			                                      "Cancel", new Dimension(230, 80));
			if (button == 0) return;
		}
        JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, file.getName(), getStatusWindowLocation());
        IntegrationProjectBuilder.saveAsModelWorker worker = new IntegrationProjectBuilder.saveAsModelWorker(this, fileName, waitWin, closeAfterSave);
        worker.start();
		//setFileName(fileName); move to saveAsModelWorker
		//save(); move to saveAsModelWorker
	}

	public Element toXmlElement()
	{
		Element xml = super.toXmlElement();
		XMLUtils.makeRootElement(xml);
		return xml;
	}

	protected ProjectResourceInfo createProjectResourceInfo(Element resourceXml)
	{
		return new BuildProjectResourceInfo(resourceXml);
	}

	protected ProjectIntegrationModelInfo createProjectIntegrationModelInfo(Element imodelXml)
	{
		return new BuildProjectIntegrationModelInfo(imodelXml);
	}

	// CausalitySupport interface

	protected CausalityManager getCausalityManager()
	{
		if(internalCausalityManager == null) {
			internalCausalityManager = new IntegrationProjectCausalityManager();
		}
		return internalCausalityManager;
	}

	class iModelSubscriptionChangeListener implements PropertyChangeListener
	{
		//TODO for nested projects subscription to nested project resource models should not throw exceptions
		public void propertyChange(PropertyChangeEvent evt)
		{
			DomeModelBuilder model = (DomeModelBuilder) evt.getSource();
			String property = evt.getPropertyName();
			Subscription subscription = (Subscription) evt.getNewValue();
			BuildProjectResourceInfo info = (BuildProjectResourceInfo) getResource(subscription.getResourceId());

			if (info == null) {
				Object imodel = iModelsById.get(subscription.getResourceId());
				if(!iModels.contains(imodel) && !removedResources.contains(subscription.getResourceId())) {
					throw new IllegalArgumentException("could not synchronize subscription data: " + subscription);
				}
				else if(iModels.contains(imodel) && property.equals(DomeModel.SUBSCRIPTION_ADDED)) {
					((ProjectIntegrationModelInfo)imodel).addSubscriber(model, subscription.getIfaceId());
				}
				else if(iModels.contains(imodel) && property.equals(DomeModel.SUBSCRIPTION_DELETED)) {
					((ProjectIntegrationModelInfo) imodel).removeSubscriber(model, subscription.getIfaceId());
				}
				else { //if resource is removed
					//check in removedResources list for the stored id
					if(removedResources.contains(subscription.getResourceId()))
						return;
				}
			}
            //TODO  'else' ? _i
		    else if (DomeModel.SUBSCRIPTION_ADDED.equals(property)) {
				info.addSubscriber(model, subscription.getIfaceId());
			} else if (DomeModel.SUBSCRIPTION_DELETED.equals(property)) {
				info.removeSubscriber(model, subscription.getIfaceId());
			}
		}
	}

	/**
	 * Returns hashtable of each interface param to system causality status
	 * @param paramMap interface param to model param
	 * @return
	 */
	public Hashtable getSystemCausality(Map paramMap)
	{
		DirectedGraph systemCausalityGraph = createSystemCausalityGraph();
		List disconnectedNodes = systemCausalityGraph.getDisconnectedNodes();
		List independents = systemCausalityGraph.getInputs();
		List intermediates = systemCausalityGraph.getIntermediates();
		List results = systemCausalityGraph.getResults();
		Hashtable paramCausality = new Hashtable();

		for (Iterator iterator = paramMap.keySet().iterator(); iterator.hasNext();) {
			Parameter ifaceParam = (Parameter) iterator.next();
			if (independents.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.INDEPENDENT.toString());
			else if (intermediates.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.INTERMEDIATE.toString());
			else if (results.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.RESULT.toString());
			else if (disconnectedNodes.contains(paramMap.get(ifaceParam)))
				paramCausality.put(ifaceParam, CausalityStatus.INDETERMINATE.toString());
			else
				throw new RuntimeException("IntegrationProjectBuilder:getSystemCausality: invalid interface parameter: " +
				                           ifaceParam.getName() + " in " + getName());
		}
		return paramCausality;
	}

	// for subscription's ssystem causality
	public Hashtable getSystemCausality(List params)
	{ //key=iface param, value = mapped param
		DirectedGraph systemCausalityGraph = createSystemCausalityGraph(); //todo: update graph instead of recalculating it everytime
		List disconnectedNodes = systemCausalityGraph.getDisconnectedNodes();
		List independents = systemCausalityGraph.getInputs();
		List intermediates = systemCausalityGraph.getIntermediates();
		List results = systemCausalityGraph.getResults();
		Hashtable paramCausality = new Hashtable();

		for (Iterator iterator = params.iterator(); iterator.hasNext();) {
			ModelObject ifaceParam = (ModelObject)iterator.next();
			if (ifaceParam instanceof Parameter) {
				if (independents.contains(ifaceParam))
					paramCausality.put(ifaceParam, CausalityStatus.INDEPENDENT.toString());
				else if (intermediates.contains(ifaceParam))
					paramCausality.put(ifaceParam, CausalityStatus.INTERMEDIATE.toString());
				else if (results.contains(ifaceParam))
					paramCausality.put(ifaceParam, CausalityStatus.RESULT.toString());
				else if (disconnectedNodes.contains(ifaceParam))
					paramCausality.put(ifaceParam, CausalityStatus.INDETERMINATE.toString());
				else
					throw new RuntimeException("IntegrationProjectBuilder.getParameterSystemCausality: invalid parameter: " + ifaceParam.getName());
			}
		}
		return paramCausality;
	}

	public DirectedGraph getGraph() {
		return createSystemCausalityGraph();
	}

    /** creates a system causality graph in build mode
     *
     */
    protected DirectedGraph createSystemCausalityGraph() {
	    DirectedGraph systemCausalityGraph = new DirectedGraph();

	    List imodelInfo = getIntegrationModels();
	    for (int i = 0; i < imodelInfo.size(); i++) {
		    ProjectIntegrationModelInfo info = (ProjectIntegrationModelInfo) imodelInfo.get(i);
		    DomeModelBase imodel = (DomeModelBase) info.getModel();
		    if(loadingiModelXml) {
			    //when we are loading xml we are creating and setting dome models
			    //for each imodel and createSystemCausalityGraph() gets called for
			    //every model when other imodels are not loaded yet
			    if(imodel == null)
				    continue;
		    }
		    systemCausalityGraph.addGraph(imodel.createModelGraph());

		    List subs = imodel.getSubscriptions();
		    // create paramIdMap (origId (iface param id) -> newId) for all subscriptions
		    for (int j = 0; j < subs.size(); j++) {
			    AbstractSubscription subscription = (AbstractSubscription) subs.get(j);
			    FormatUtils.reverseLinearMap(subscription.getParamIdMap());
		    }
	    }
/*

        List resource = getResourceModels();
        HashMap resourceModelObjMap = new HashMap(); //key=id, value=parameter (in resource)
        for (int i = 0; i < resource.size(); i++) {
            BuildProjectResourceInfo bpri = (BuildProjectResourceInfo) resource.get(i);
            if (bpri.getType().equals(ProjectResourceInfo.MODEL_RESOURCE)) {
                bpri.loadResource();
                List subscribedInterfaceIds = bpri.getSubscribedInterfaceIds();
                List viewList = bpri.getView();
                for (Iterator it = viewList.iterator(); it.hasNext();) {
                    Object o = it.next();
                    if (o instanceof BrowseInterface) {
                        BrowseInterface bi = (BrowseInterface) o;
                        if (subscribedInterfaceIds.contains(bi.getInterfaceId())) {
                            bi.loadInterface();
                            ModelInterfaceRuntimeClient iface = bi.getInterface();
                            DirectedGraph ifaceGraph = iface.getInterfaceGraph();
                            systemCausalityGraph.addGraph(ifaceGraph);
                            List nodes = ifaceGraph.getNodes();
                            for (int j = 0; j < nodes.size(); j++) {
                                Parameter p = (Parameter) nodes.get(j);
                                resourceModelObjMap.put(p.getId().getIdString(), p);
                            }
                        }
                    }
                }
            } else { //todo: support project in project
                System.out.println("AbstractIntegrationProject:creatProjectGraph - Invalid resource type - " + bpri.getType());
            }
        }*/
	    return systemCausalityGraph;
    }

    /**
     * for determine the status window location
     * @return
     */
    public static Point getStatusWindowLocation() {
        JComponent comp = BuildFocusTracker.getCurrentComponent();
        if (comp == null) { // place in top left corner
            return new Point(0, DomeClientApplication.getBottomCoordinate());
        } else { // place offset to window of component
            Window win = BuildFocusTracker.getCurrentWindow();
            if (win instanceof DomeBuildFrame && win.isShowing()) {
                Point p = win.getLocationOnScreen();
                return new Point(p.x + 25, p.y + 25);
            } else { // what is it? place in top left corner
                return new Point(0, DomeClientApplication.getBottomCoordinate());
            }
        }
    }

    static class saveAsModelWorker extends SwingWorker {
        JFrame waitWin;
        IntegrationProjectBuilder builder;
        String newName;
        boolean closeAfterSave;

        public saveAsModelWorker(IntegrationProjectBuilder builder, String newName, JFrame waitWin, boolean closeAfterSave) {
            this.builder = builder;
            this.waitWin = waitWin;
            this.newName = newName;
            this.closeAfterSave = closeAfterSave;
        }

        public Object construct() {
            builder.setFileName(newName);
            builder.save();
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);

            if (closeAfterSave) {
                ProjectBuildPanel.deleteAllConcreteParameters(builder);
            }

            waitWin.dispose();
        }
    }


}