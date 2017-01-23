// DomeModelBuilder.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.model.dome;

import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.model.ModelBuilder;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.*;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManagerBuild;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerRuntime;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Latch;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.AbstractProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.DefaultSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.InfoInterface;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.DomeJavaBean;
import mit.cadlab.dome3.DomeClientApplication;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;

/**
 * Version of DomeModel for building DomeModel.
 */
public class DomeModelBuilder extends DomeModelBase implements ModelBuilder
{

//	protected String fileName = "";
	protected boolean shouldSave = true;
	protected Relation relationToTest = null;
	protected boolean saveAsOp = false;
    protected HashMap subscriptionMap; //key - iface id, value - DefaultSubscription id
    protected DSet resourceSet;
	protected DeletionListener subscriptionDeletionListener = new SubscriptionDeletionListener();
	protected PropertyChangeListener resourceRemovalListener = new ResourceRemovalListener();
    protected boolean loopChecking=true;



	// todo: add deletion listener
	public DomeModelBuilder(Id id)
	{
		super(id);
		createDefaultModelInterface();
	}

	//for iModels
	public DomeModelBuilder(Id id, boolean isIModel)
	{
		super(id);
		isIntegrationModel = isIModel;
		if (isIModel) {
			subscriptionMap = new HashMap();
			resourceSet = new DSet();
		}
        createDefaultModelInterface();
	}

	public DomeModelBuilder(Id id, DomeModel model)
	{
		super(id, model);
	}

	//used to copy project integration models
	public DomeModelBuilder(Id id, DomeModel model, boolean copyObjects)
	{
		this(id, model);
		createDefaultModelInterface();
		if (model.isIntegrationModel()) {
			isIntegrationModel = true;
			setIntegrationProject(model.getIntegrationProject());
		}
		HashMap idMap = null;
		if(copyObjects) {
			idMap = copyModelObjects(model.getModelObjects(), false);
        }
		if (model.isIntegrationModel()) {
			subscriptionMap = new HashMap();
			Map smap = ((DomeModelBuilder) model).getSubscriptionMap();
			for (Iterator i = smap.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				Object c = subscriptionMap.get(e.getKey());
				if(c == null) {
					c = new ArrayList();
				}
				((Collection)c).add(idMap.get(e.getValue()));
				subscriptionMap.put(e.getKey(), c);
			}
			resourceSet = new DSet();
			Collection rset = ((DomeModelBuilder) model).getResourcesSubscribed();
			for (Iterator iterator = rset.iterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				resourceSet.add(obj);
				if(obj instanceof BuildProjectResourceInfo) {
					BuildProjectResourceInfo pri = ((BuildProjectResourceInfo) obj);
					Collection ifaceIds = pri.getSubscribedInterfaceIds(model);
					pri.addSubscriber(this, ifaceIds);
				}
				if (obj instanceof BuildProjectIntegrationModelInfo) {
					BuildProjectIntegrationModelInfo pii = ((BuildProjectIntegrationModelInfo) obj);
					Collection ifaceIds = pii.getSubscribedInterfaceIds(model);
					pii.addSubscriber(this, ifaceIds);
				}
			}
		}
	}

	public DomeModelBuilder(Element xmlElement)
	{
		super(xmlElement);
		if (interfaces instanceof ModelInterfaceManagerBuilder)
			if (!((ModelInterfaceManagerBuilder) interfaces).defaultInterfaceExists()) {
				createDefaultModelInterface();
			}
	}

	public DomeModelBuilder(String file, Element xmlElement)
	{
		super(file, xmlElement);
		loadInterfaces(file);
		if (interfaces instanceof ModelInterfaceManagerBuilder)
			if (!((ModelInterfaceManagerBuilder) interfaces).defaultInterfaceExists()) {
				createDefaultModelInterface();
			}
		if(isIntegrationModel) {
			resourceSet = new DSet();
			subscriptionMap = new HashMap();
			populateSubscriptionMap();
		}
        //qing : change here Dec 7th, do not calcuate graph while loading,
        calculateModelGraph();
        updateInterfaceGraph_and_updateLastSavedXml();
  	}

	//used by iProject to load iModel
	public DomeModelBuilder(IntegrationProject project, ProjectIntegrationModelInfo info, String file, Element xmlElement)
	{
		super(project, info, file, xmlElement);
		loadInterfaces(file);
		if (interfaces instanceof ModelInterfaceManagerBuilder)
			if (!((ModelInterfaceManagerBuilder) interfaces).defaultInterfaceExists()) {
				createDefaultModelInterface();
			}
		if (isIntegrationModel) {
			resourceSet = new DSet();
			subscriptionMap = new HashMap();
			populateSubscriptionMap();
		}
        //qing : change here Dec 7th, do not calcuate graph while loading,
        calculateModelGraph();
        updateInterfaceGraph_and_updateLastSavedXml();
	}


	private void populateSubscriptionMap() {
		for (Iterator i = modelObjects.iterator(); i.hasNext();) {
			Object obj = i.next();
			if (obj instanceof DefaultSubscription) {
				DefaultSubscription ds = (DefaultSubscription) obj;
				Object c = subscriptionMap.get(ds.getIfaceId());
				if(c == null) {
					c = new ArrayList();
				}
				((Collection)c).add(ds.getId());
				subscriptionMap.put(ds.getIfaceId(), c);
			}
		}
	}



	public DomeModelBuilder(String id, String name)
	{
		super(id, name);
	}

	protected ConnectionMappingManager createConnectionMappingManager()
	{
		return new ConnectionMappingManagerBuild(this);
	}

	protected void createDefaultModelInterface()
	{
		defaultIface = new ModelInterfaceBuilder(this, UUIDGenerator.create(),
		                                         ModelInterface.DEFAULT_IFACE_TAG);
		((ModelInterfaceBuilder) defaultIface).setIsDefaultInterface();
		((ModelInterfaceManagerBuilder) interfaces).addInterface(defaultIface);
	}

	protected ModelInterfaceManager createInterfacesManager()
	{
		return new ModelInterfaceManagerBuilder(this);
	}

	public void loadInterfaces(String modelFileName)
	{
		((ModelInterfaceManagerBuilder) interfaces).loadInterfaces(modelFileName);
	}

	public void setIntegrationProject(IntegrationProject project)
	{
		this.iProject = project;
		addSubscriptionFilter(); // to Model Object View
	}

	public Relation getRelationToTest()
	{
		return relationToTest;
	}

	public void setRelationToTest(Relation relationToTest)
	{
		this.relationToTest = relationToTest;
	}

	public ModelObjectFactory getModelObjectFactory()
    {
        if (moFactory == null)
            moFactory = new ModelObjectBaseFactory();
        return moFactory;
    }

	public ModelObject newModelObject(String modelObjectType)
	{
		if (modelObjectType.equals("Latch")) {
			DefaultContextBuilder latchContext = (DefaultContextBuilder)super.newModelObject(Context.TYPE_INFO.getTypeName());
			latchContext.setName(Latch.NAME);
			DefaultContextBuilder clockContext = (DefaultContextBuilder) super.newModelObject(Context.TYPE_INFO.getTypeName());
			clockContext.setName(Latch.CLOCK);
			latchContext.addModelObjectReference(clockContext);
			DefaultContextBuilder inContext = (DefaultContextBuilder) super.newModelObject(Context.TYPE_INFO.getTypeName());
			inContext.setName(Latch.INPUT);
			latchContext.addModelObjectReference(inContext);
			DefaultContextBuilder outContext = (DefaultContextBuilder) super.newModelObject(Context.TYPE_INFO.getTypeName());
			outContext.setName(Latch.OUTPUT);
			latchContext.addModelObjectReference(outContext);
			return latchContext;
		} else {
			return super.newModelObject(modelObjectType);
		}
	}

	public void subscribe(ServerConnection svrConn, ModelInterface iface, String ifaceDeployId, int ifaceVersion, String resourceId)
	{
			Id id = new Id(UUIDGenerator.create());
			ModelObject mObj = new DefaultSubscription(this, id, iface, ifaceDeployId, ifaceVersion, resourceId,
			                                           svrConn, ((ModelInterfaceBase)iface).getInterfaceGraph());
			modelObjects.add(mObj);
			mObj.addDeletionListener(subscriptionDeletionListener);
			getBuildContext().addModelObjectReference(mObj);
			Object c = subscriptionMap.get(ifaceDeployId);
			if(c == null)  {
				c = new ArrayList();
			}
			((Collection)c).add(id);
			subscriptionMap.put(ifaceDeployId, c);
			firePropertyChange(SUBSCRIPTION_ADDED, null, mObj);
	}

	public void relocateSubscription(Subscription s, ServerConnection svrConn, String ifaceDeployId,
	                                 int ifaceVersion, String oldIfaceId)
	{
		((DefaultSubscription)s).relocate(ifaceDeployId, ifaceVersion, svrConn);

		Object c = subscriptionMap.get(oldIfaceId);
		subscriptionMap.remove(oldIfaceId);
		subscriptionMap.put(ifaceDeployId, c);
	}

	public List getSubScriptions(String resourceid) {
		List subs = new ArrayList();
		List subscriptions = getSubscriptions();
		for (Iterator iterator = subscriptions.iterator(); iterator.hasNext();) {
			Subscription o = (Subscription) iterator.next();
			String resid = o.getResourceId();
			if (resid.equals(resourceid)) {
				subs.add(o);
			}
		}
		return subs;
	}

	public Collection getSubscriptionModelObjectIds(String ifaceId) {
		return (Collection)subscriptionMap.get(ifaceId);
	}

	public Map getSubscriptionMap()
	{
		return Collections.unmodifiableMap(subscriptionMap);
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setShouldSave(boolean shouldSave)
	{
		this.shouldSave = shouldSave;
	}

	public boolean getShouldSave()
	{
		return shouldSave;
	}

    	protected static Dimension SAVE_ERROR_SIZE = new Dimension(250, 100);

	public void save(boolean closeAfterSave)
	{
		// invokes saveAs if not filename
			if (fileName.equals("")) {
				saveAs(closeAfterSave);
			} else {
                File f = new File(fileName);
                JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, f.getName(), getStatusWindowLocation());
                DomeModelBuilder.saveModelWorker worker = new DomeModelBuilder.saveModelWorker(this, fileName, waitWin, closeAfterSave);
                worker.start();
				//super.save(fileName); moved to be called in saveModelWorker
			}
	}

    protected void superSave(String filename) {
        super.save(filename);
    }

    protected void superSaveNoGui(String filename) {
        super.saveNoGui(filename);
    }

	public void save(String fileName)
	{
		String oldFileName = this.fileName;
		this.fileName = fileName;
		super.save(fileName);
		firePropertyChange(FILENAME, oldFileName, fileName);
	}

	public void saveAs(boolean closeAfterSave)
	{
		saveAsOp = true;
        String newFileName = BuildMode.buildFileChooser.showSaveDialog(BuildMode.getCurrentModelFrame(),
		                                                               (this instanceof PluginModelBuilder) ? ((PluginModelBuilder) this).getPluginTypeName() : getTypeName(),
		                                                               new File(fileName));
		if (newFileName == null)
			return;

		newFileName = fixFileName(newFileName, getModelExtension());

		if (newFileName.equalsIgnoreCase(fileName)) { // same file as before
			String msg = "Can not save the new model in the original file.  Choose a different file name.";
			OneButton1Msg.showWarning(null, "Warning: Save As", msg, "OK", new Dimension(230, 80));
		} else {
			// check if file already exists
			File file = new File(newFileName);
			if (file.exists()) {
				String msg = null;
				msg = "File <" + file.getName() + "> already exists. Replace it?";
				int button = TwoButton1Msg.showOption(null,
				                                      "Warning: File exists", msg, "Replace",
				                                      "Cancel", new Dimension(230, 80));
				if (button == 0) return;
			}
			if (fileName.equals("")) { // no file name before
                JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, file.getName(), getStatusWindowLocation());
                DomeModelBuilder.saveNewModelWorker worker = new DomeModelBuilder.saveNewModelWorker(this, newFileName, waitWin, closeAfterSave);
                worker.start();
				//save(newFileName); moved to be called in saveNewModelWorker
			} else {
                JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, file.getName(), getStatusWindowLocation());
                DomeModelBuilder.saveAsModelWorker worker = new DomeModelBuilder.saveAsModelWorker(this, newFileName, waitWin, closeAfterSave);
                worker.start();
				//this.setShouldSave(true); moved to be called in saveAsModelWorker
				//BuildMode.duplicateModelAndSave(this, newFileName);
				//for save as just change model id kepping all other object ids same
				//BuildMode.modelSaveAs(this, newFileName); moved to be called in saveAsModelWorker
			}
		}
	}

	public static String fixFileName(String fileName, String modelExt)
	{
		// validate and fix the file name
		String properModelExt = "-" + modelExt + ".dml";
		String fileModelExt = DomeFileChooser.getModelExtension(fileName);
		if (fileModelExt == null) {
			String fileExt = DomeFileChooser.getExtension(fileName);
			if (fileExt == null)
				fileName = fileName + properModelExt;
			else if (fileExt.equals("dml"))
				fileName = fileName.substring(0, fileName.length() - 4) + properModelExt;
			else
				fileName = fileName + properModelExt;
		} else if (!fileModelExt.equalsIgnoreCase(properModelExt)) {
			fileName = fileName.substring(0, fileName.length() - (fileModelExt.length() + 1)) + properModelExt;
		}
		return fileName;
	}

	public String getModelExtension()
	{
		return "DOME";
	}

	public void setDefaultInterface(DomeModelInterface iface)
	{
		this.defaultIface = iface;
	}

	public DomeModelBuilder getSaveAsCopy()
	{
		if (saveAsOp) {
			this.changeId();
			saveAsOp = false;
			return this;
		} else
			return this;  //no changes to model for other ops
	}

/*
	protected void save(Document xmlDoc, String fileName)
	{
		try {
			// save interfaces
			((ModelInterfaceManagerBuilder)interfaces).save(fileName);
			// save the model
			XMLUtils.writeToFile(xmlDoc, fileName);
			lastSavedXml = xmlDoc.asXML();
		}
		catch (Exception ex) {
			System.err.println(ex);
		}
	}
*/

	class SubscriptionDeletionListener implements DeletionListener
	{
		public void objectDeleted(DeletionEvent e)
		{
			DefaultSubscription source = (DefaultSubscription)e.getSource();
			String id = source.getIfaceId();
			Collection subids = (Collection)subscriptionMap.get(id);
			subids.remove(source.getId());
			firePropertyChange(SUBSCRIPTION_DELETED, null, e.getSource());
		}
	}

	public void subscribeResource(Object resource)
	{
		resourceSet.add(resource);
		((DomeJavaBean)resource).addPropertyChangeListener(resourceRemovalListener);
        firePropertyChange(RESOURCE_SUBSCRIBED, null, resource);
	}

	public void unsubscribeResource(Object resource)
	{
		resourceSet.remove(resource);
		((DomeJavaBean) resource).removePropertyChangeListener(resourceRemovalListener);
		firePropertyChange(RESOURCE_UNSUBSCRIBED, null, resource);
	}

	public Collection getResourcesSubscribed() {
		return Collections.unmodifiableCollection(resourceSet);
	}

	public void addResourceNameChangeListener(PropertyChangeListener pl) {
		for(Iterator i = resourceSet.iterator(); i.hasNext(); ) {
			//could be remote resource or iModel in the same project
			DomeJavaBean info = (DomeJavaBean) i.next();
			info.addPropertyChangeListener(pl);
		}
	}

	class ResourceRemovalListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if(e.getPropertyName().equals(InfoInterface.RESOURCE_REMOVED)) {
				unsubscribeResource(e.getOldValue());
			}
		}
	}

	public void broadCastiModelDeleted()
	{
		firePropertyChange(IMODEL_DELETED, this, null);
	}

	protected void updateInterfaceGraph_and_updateLastSavedXml(){
       int interfacesize=interfaces.countInterfaces();
         for(int i=0;i<interfacesize;i++){
             ModelInterface interf=interfaces.getInterface(i);
             if(interf instanceof AbstractDomeModelInterface)
             {
	             ((AbstractDomeModelInterface) interf).updateSavedStatus();
             }
         }
     }

	protected DirectedGraph getSystemCausalityGraph()
	{
		return createModelGraph();
	}

     public boolean isLoopChecking() {
        return loopChecking;
    }

    public void setLoopChecking(boolean loopChecking) {
        if(this.loopChecking != loopChecking)
        {
            this.loopChecking = loopChecking;
        }
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

    static class saveNewModelWorker extends SwingWorker {
        DomeModelBuilder builder;
        JFrame waitWin;
        String newFileName;
        boolean closeAfterSave;

        public saveNewModelWorker(DomeModelBuilder builder, String newFileName,JFrame waitWin, boolean closeAfterSave) {
            this.builder = builder;
            this.waitWin = waitWin;
            this.newFileName = newFileName;
            this.closeAfterSave = closeAfterSave;
        }

        public Object construct() {
            builder.save(newFileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);

            if (closeAfterSave) {
                deleteAllConcreteParameters(builder);
            }

            waitWin.dispose();
        }
    }

    static class saveModelWorker extends SwingWorker {
        DomeModelBuilder builder;
        JFrame waitWin;
        String fileName;
        boolean closeAfterSave;

        public saveModelWorker(DomeModelBuilder builder, String fileName, JFrame waitWin, boolean closeAfterSave) {
            this.builder = builder;
            this.waitWin = waitWin;
            this.fileName = fileName;
            this.closeAfterSave = closeAfterSave;
        }

        public Object construct() {
            builder.superSave(fileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);

            if (closeAfterSave) {
                deleteAllConcreteParameters(builder);
            }

            waitWin.dispose();
        }
    }

    static class saveAsModelWorker extends SwingWorker {
        DomeModelBuilder builder;
        JFrame waitWin;
        String newFileName;
        boolean closeAfterSave;

        public saveAsModelWorker(DomeModelBuilder builder, String newFileName, JFrame waitWin, boolean closeAfterSave) {
            this.builder = builder;
            this.waitWin = waitWin;
            this.newFileName = newFileName;
            this.closeAfterSave = closeAfterSave;
        }

        public Object construct() {
            builder.setShouldSave(true);
            BuildMode.modelSaveAs(builder, newFileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);

            if (closeAfterSave) {
                deleteAllConcreteParameters(builder);
            }

            waitWin.dispose();
        }
    }

    /***
     * sangmok: memeory problem fix
     * There was a problem of not releasing data objects when a user closes window.
     * call delete() for every ConcreteParameter instance contained in this panel.
     * This will make each instance release reference to DataObject
     */
    public static void deleteAllConcreteParameters(DomeModelBuilder modelBuilder) {
        /* cleanup interfaces in for the modelBuilder */
        modelBuilder.interfaces.cleanup();

        // make a list of deleted model objects (small problems with iterator forced me to delete() in 2 step. I first tried delete() as I iterate through the model objects, but the iterator didn't worked very well. It jumped when an element is deleted. )
        // memory fix it should be done before interfaces get empty

        Set deletedObjectSet = new HashSet();
        Set deletedModelObjectScopeSet = new HashSet();


        for (int i = 0; i < modelBuilder.modelObjects.size(); i++) {
            ModelObject mObj = (ModelObject) modelBuilder.modelObjects.get(i);
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
//	            //scope.delete(null);
//            } catch (NoSuchElementException e) { System.err.println(e); }
//        }
    }
}
