package mit.cadlab.dome3.objectmodel.model.tool;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.objectmodel.*;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.tool.ToolMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.tool.AnalysisToolConfiguration;
import mit.cadlab.dome3.tool.AnalysisToolUtils;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Sep 3, 2003
 * Time: 5:53:39 PM
 * To change this template use Options | File Templates.
 */
public abstract class AnalysisToolBase extends AbstractAnalysisTool
{
    public static final String slash = System.getProperty("file.separator");

    public static final String TOOL_PROJECT_VIEW = "AnalysisTool Project View";

    protected String _toolXmlType; // for loading from xml only

    protected AnalysisToolConfiguration _toolConfiguration;
    protected ToolMappingManager _toolMappingManager;
    protected AnalysisToolDeletionListener _analysisToolDeletionListener;

    protected IntegrationProject _toolProject;

    private boolean shouldSave = true;
    protected boolean saveAsOp = false;

    public AnalysisToolBase(Id id)
	{
		super(id);
		initModel();
	}

	public AnalysisToolBase(Id id, AnalysisTool model)
	{
		super(id, model);
		initModel();
	}

	public AnalysisToolBase(String file, Element xmlElement)
	{
		super(file, xmlElement);
		loadXml(xmlElement);
	}

    public AnalysisToolBase(String id, String toolType)
    {
        this(new Id(id));
        if (toolType == null)
                    throw new IllegalArgumentException("null toolTypeName");
        _toolConfiguration = AnalysisToolUtils.createToolConfiguration(toolType, this);
        _toolMappingManager = this._toolConfiguration.createToolMappingManager(this);
        setName(this._toolConfiguration.getTypeName());
    }

	public AnalysisToolBase(Element xmlElement)
	{
		super(xmlElement);
		loadXml(xmlElement);
	}

    public List getValidModelObjectTypes()
	{
		List types = Registry.getDataObjectTypes();
		types.add("parameter");
		types.add("visualization");
		types.add(Registry.getRelationTypes());
		return types;
	}

    public Document createXmlDocument()
	{
		Document doc = DocumentFactory.getInstance().createDocument();
		Element xmlElement = toXmlElement();
		doc.add(xmlElement);
		return doc;
	}

    protected void loadContexts(Element xmlElement)
    {
        // read contexts
        Context cxt;
        Element element;
        Element BuidContextElement = null;
        Vector cxtObjects = new Vector();
        List contexts = xmlElement.selectNodes("/" + getXmlTag() + "/contexts/" + Context.XML_TAG);
        for (ListIterator iter = contexts.listIterator(contexts.size()); iter.hasPrevious();)
        {  //so that nested contexts are loaded properly
            element = (Element) iter.previous();
            String conId = element.attributeValue("id");
            if (conId.equals(BUILD_CONTEXT_ID.getIdString()))
            {  //skip build context
                BuidContextElement = element;
                continue;
            }
            cxt = (Context) getModelObjectFactory().newInstance(element, new Object[]{this, element});
            if (cxt != null)
            {
                modelObjects.add(0, cxt);                      //so that nested contexts are loaded properly
                // build a collection of all context objects
                Collection cxtObjectList = cxt.getModelObjectReferences();
                for (Iterator contextIter = cxtObjectList.iterator(); contextIter.hasNext();)
                {
                    ModelObject cxtObject = (ModelObject) contextIter.next();
                    cxtObjects.addElement(cxtObject.getId());
                }
            }
        }

        if (BuidContextElement != null)
        {
            cxt = (Context) getModelObjectFactory().newInstance(BuidContextElement, new Object[]{this, BuidContextElement});
            if (cxt != null)
            {
                //populate build context to preserve order
                Context buildContext = (Context) modelObjectsById.get(BUILD_CONTEXT_ID);
                if (buildContext != null)
                {
                    modelObjects.remove(buildContext);  //remove empty build context created initially
                }
                modelObjects.add(cxt); //add the build context with right order of modelobjects
            }
        }
        else
        {
            //TODO remove all the code that belongs to this else statement.  This is in place so that the old models
            //TODO which didn't save the build context can be loaded back
            Context buildContext = (Context) getModelObjectById(BUILD_CONTEXT_ID);
            // revisit the model objects and put them in the build context
            // only if they are not in any other context
            for (Iterator iter = modelObjects.iterator(); iter.hasNext();)
            {
                Object obj = iter.next();
                if (obj instanceof ModelObject)
                {
                    ModelObject modelObj = (ModelObject) obj;
                    if (modelObj != null && modelObj != buildContext && !cxtObjects.contains(modelObj.getId()))
                    {
                        buildContext.addModelObjectReference(modelObj);
                    }
                }
            }
        }
    }

    protected void loadMappings(Element xmlElement)
    {
        // read mappings
        Element mappings = (Element) xmlElement.selectSingleNode("/" + getXmlTag() + "/mappings/modelMappings");
        if (mappings != null)
            mappingManager.addMappings(mappings);
    }

    protected void storeXml()
	{
		// store the xml as a string
		lastSavedXml = createXmlDocument().asXML();
	}

    protected void initModel()
    {
        mappingManager = createConnectionMappingManager();
        modelObjects.add(createBuildContext());
		_interfaces = createAnalysisToolInterfacesManager();
        _analysisToolCausalityManager = createCausalityManager();
        _analysisToolDeletionListener = new AnalysisToolDeletionListener();
    }

    protected void parseHeaderElement(Element xmlElement)
	{
		super.parseHeaderElement(xmlElement);
		this._toolXmlType = xmlElement.attributeValue("toolType");
		if (this._toolXmlType == null)
			throw new IllegalArgumentException(getTypeName() + " - no xml toolType");
	}

    public ModelObject newModelObject(String modelObjectType)
	{
		Object[] oArray = null;
		Parameter o = null;
		if (this._toolConfiguration.useCustomDatatype()) {
			oArray = this._toolConfiguration.createParameter(this, new Id(UUIDGenerator.create()), modelObjectType);
			if (oArray != null && oArray.length > 0) {
				o = (Parameter) oArray[0];
				modelObjects.add(o);
				if (oArray.length == 2) {
					String mapString = (String) oArray[1];
					getToolMappingManager().addMapping(o, mapString);
				}
				return o;
			}
		}
		return super.newModelObject(modelObjectType);
	}

    public ToolMappingManager getToolMappingManager()
    {
        return _toolMappingManager;
    }

    public String getToolExtension()
    {
        return this._toolConfiguration.getXmlType();
    }

    protected static final Dimension HAPPY_MESSAGE_SIZE = new Dimension(150, 75);

    public void save(boolean closeAfterSave)
    {
        // invokes saveAs if not filename
        if (fileName.equals(""))
        {
            saveAs(closeAfterSave);
        }
        else
        {
            ((IntegrationProjectBuilder) getIntegrationProject()).save();
            File f = new File(fileName);
            JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, f.getName(), getStatusWindowLocation());
            AnalysisToolBase.saveModelWorker worker = new AnalysisToolBase.saveModelWorker(this, fileName, waitWin, closeAfterSave);
            worker.start();
            //super.save(fileName); moved to be called in saveModelWorker
        }
    }

    protected void superSave(String filename) {
        super.save(filename);
    }

    public void save(String fileName, boolean closeAfterSave)
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
                this._toolConfiguration.getTypeName(),
                new File(fileName));

        /**
         * Saving the project inside the analysis tool, inside a contents
         * folder of the analysis tool.
         * (i) create the contents folder
         * (ii) create the folder to store the project
         * (iii) create the folder that will store the interfaces
         */
        if (newFileName == null) return;

        if (newFileName.equalsIgnoreCase(fileName))
        { // same file as before
            String msg = "Can not save the new model in the original file.  Choose a different file name.";
            OneButton1Msg.showWarning(null, "Warning: Save As", msg, "OK", new Dimension(230, 80));
            return;
        }


        String projectFileName = null;

        File a = new File(newFileName);
        if (a.getParentFile().exists())
        {
            String contentFolderPath = a.getParentFile() + slash + a.getName() + "-contents";
            File contentFolder = new File(contentFolderPath);
            if (contentFolder.exists())
            {
                FileUtils.deleteDirectoryContents(contentFolder, false);
            }
            else
                if (!contentFolder.mkdir())
                    OneButton1Msg.showError(null, "build mode", "error create analysis tool contents folder", "ok", OneButton1Msg.DEFAULT_SIZE);

            String file = contentFolder.getAbsolutePath() + slash + "project";
            File projectFolder = new File(file);
            if (projectFolder.mkdir())
            {
                if (((IntegrationProjectBuilder)getIntegrationProject()).getFileName().equals(""))
                {
                    projectFileName = projectFolder.getAbsolutePath() + slash + newFileName.substring(newFileName.lastIndexOf(slash)+1) + getIntegrationProject().getName();
                }
                else
                    projectFileName = projectFolder.getAbsolutePath() + slash + convertProjectFileName(((IntegrationProjectBuilder)getIntegrationProject()).getFileName());
            }

        }
        if (projectFileName != null)
            ((IntegrationProjectBuilder) getIntegrationProject()).saveAs(projectFileName, closeAfterSave);

        newFileName = fixFileName(newFileName, getToolExtension());

        if (!newFileName.equalsIgnoreCase(fileName))
        {
			// check if file already exists
			File file = new File(newFileName);
			if (file.exists())
            {
				String msg = null;
				msg = "File <" + file.getName() + "> already exists. Replace it?";
				int button = TwoButton1Msg.showOption(null,
				                                      "Warning: File exists", msg, "Replace",
				                                      "Cancel", new Dimension(230, 80));
				if (button == 0) return;
			}
			if (fileName.equals(""))
            { // no file name before
                JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, file.getName(), getStatusWindowLocation());
                AnalysisToolBase.saveNewModelWorker worker = new AnalysisToolBase.saveNewModelWorker(this, newFileName, waitWin, closeAfterSave);
                worker.start();
				//save(newFileName); moved to be called in saveNewModelWorker
			}
            else
            {
                JFrame waitWin = StatusWindow.show(StatusWindow.SAVING_FILE, file.getName(), getStatusWindowLocation());
                AnalysisToolBase.saveAsModelWorker worker = new AnalysisToolBase.saveAsModelWorker(this, newFileName, waitWin, closeAfterSave);
                worker.start();
				//this.setShouldSave(true); moved to be called in saveAsModelWorker
				//BuildMode.duplicateModelAndSave(this, newFileName);
				//for save as just change model id kepping all other object ids same
				//BuildMode.modelSaveAs(this, newFileName); moved to be called in saveAsModelWorker
			}
        }
    }

    public void setShouldSave(boolean shouldSave)
	{
		this.shouldSave = shouldSave;
	}

    public boolean getShouldSave()
	{
		return shouldSave;
	}

    public String getFileName()
	{
		return fileName;
	}

    public Collection getAnalysisToolInterfaces()
    {
        return _interfaces.getInterfaces();
    }

    public static String fixFileName(String newFileName, String toolExtension)
    {
        // validate and fix the file name
		String properToolExt = "-" + toolExtension + ".dtl";
		String fileModelExt = DomeFileChooser.getToolExtension(newFileName);
		if (fileModelExt == null) {
			String fileExt = DomeFileChooser.getExtension(newFileName);
			if (fileExt == null)
				newFileName = newFileName + properToolExt;
			else if (fileExt.equals("dtl"))
				newFileName = newFileName.substring(0, newFileName.length() - 4) + properToolExt;
			else
				newFileName = newFileName + properToolExt;
		} else if (!fileModelExt.equalsIgnoreCase(properToolExt)) {
			newFileName = newFileName.substring(0, newFileName.length() - (fileModelExt.length() + 1)) + properToolExt;
		}
		return newFileName;
    }

    public AnalysisToolBase getSaveAsCopy()
	{
		if (saveAsOp) {
			this.changeId();
			saveAsOp = false;
			return this;
		} else
			return this;  //no changes to model for other ops
	}

    public ModelObjectFactory getModelObjectFactory()
    {
        if (moFactory == null)
            moFactory = new ModelObjectBaseFactory();
        return moFactory;
    }

    public String getToolTypeName()
    {
        return this._toolConfiguration.getTypeName();
    }

    public String getToolXmlType()
    {
        return this._toolConfiguration.getXmlType();
    }

	public TypeInfo getTypeInfo()
	{
		return AnalysisTool.TYPE_INFO;
	}

    public AnalysisToolConfiguration getToolConfiguration()
	{
		return this._toolConfiguration;
	}

    public IntegrationProject getIntegrationProject()
	{
		return _toolProject;
	}

	public void setToolProject(IntegrationProject value)
	{
		this._toolProject = value;
	}

    public DeletionListener getAnalysisToolDeletionListener()
    {
        return _analysisToolDeletionListener;
    }

    protected class ParametersListener implements DListListener
	{
		public void intervalChanged(DListEvent e)
		{
		}

		public void intervalAdded(DListEvent e)
		{
			addItems(e.getItems());
		}

		public void intervalRemoved(DListEvent e)
		{
			removeItems(e.getItems());
		}

		public void itemsRemoved(DListEvent e)
		{
			removeItems(e.getItems());
		}

		public void itemsReplaced(DListEvent e)
		{
			throw new UnsupportedOperationException("can not set objects in Procedural Relation!");
		}
	}

    private String convertProjectFileName(String fileName)
    {
        String projectFile = fileName.substring(fileName.lastIndexOf(slash)+1, fileName.lastIndexOf(".dpj"));
        return projectFile;
    }

    protected void addItems(List items)
	{
	}

	protected void removeItems(List items)
	{
	}

    protected abstract void loadXml(Element xmlElement);

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

    /**
     * This class listens to deletions of parameters in the project
     * inside the analysis tool.
     */

    class AnalysisToolDeletionListener implements DeletionListener
    {
        public AnalysisToolDeletionListener()
        {

        }

        public void objectDeleted(DeletionEvent e)
        {
            Parameter parameter = (Parameter) e.getSource();
            Collection c = mappingManager.getMappingsForParameter(parameter);
            if (c == null || c.isEmpty())
                return;
            else
                mappingManager.removeAllMappings(parameter);
        }
    }

    static class saveNewModelWorker extends SwingWorker {
        AnalysisToolBase base;
        JFrame waitWin;
        String newFileName;
        boolean closeAfterSave;

        public saveNewModelWorker(AnalysisToolBase base, String newFileName, JFrame waitWin, boolean closeAfterSave) {
            this.base = base;
            this.waitWin = waitWin;
            this.newFileName = newFileName;
            this.closeAfterSave = closeAfterSave;
        }

        public Object construct() {
            base.save(newFileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);

            if (closeAfterSave) {
                deleteAllConcreteParameters(base);
            }

            waitWin.dispose();
        }
    }

    static class saveModelWorker extends SwingWorker {
        AnalysisToolBase base;
        JFrame waitWin;
        String fileName;
        boolean closeAfterSave;

        public saveModelWorker(AnalysisToolBase base, String fileName, JFrame waitWin, boolean closeAfterSave) {
            this.base = base;
            this.waitWin = waitWin;
            this.fileName = fileName;
            this.closeAfterSave = closeAfterSave;
        }

        public Object construct() {
            base.superSave(fileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);

            if (closeAfterSave) {
                deleteAllConcreteParameters(base);
            }

            waitWin.dispose();
        }
    }

    static class saveAsModelWorker extends SwingWorker {
        AnalysisToolBase base;
        JFrame waitWin;
        String newFileName;
        boolean closeAfterSave;

        public saveAsModelWorker(AnalysisToolBase base, String newFileName, JFrame waitWin, boolean closeAfterSave) {
            this.base = base;
            this.waitWin = waitWin;
            this.newFileName = newFileName;
            this.closeAfterSave = closeAfterSave;
        }

        public Object construct() {
            base.setShouldSave(true);
            BuildMode.analysisToolSaveAs(base, newFileName);
            return new Object();
        }

        public void finished() {
            waitWin.setVisible(false);

            if (closeAfterSave) {
                deleteAllConcreteParameters(base);
            }

            waitWin.dispose();
        }
    }

    public static void deleteAllConcreteParameters(AnalysisToolBase base) {
//        /* cleanup interfaces in for the modelBuilder */
//        for (Iterator i = interfaces.iterator(); i.hasNext(); ) {
//            ModelInterface aInterface = (ModelInterface) i.next();
//            aInterface.cleanup();
//        }

        // make a list of deleted model objects (small problems with iterator forced me to delete() in 2 step. I first tried delete() as I iterate through the model objects, but the iterator didn't worked very well. It jumped when an element is deleted. )
        // memory fix it should be done before interfaces get empty

        Set deletedObjectSet = new HashSet();
        Set deletedModelObjectScopeSet = new HashSet();


        for (Iterator i = base.getModelObjects().iterator(); i.hasNext(); ) {
            ModelObject mObj = (ModelObject) i.next();
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
//                //scope.delete(null);
//            } catch (NoSuchElementException e) { System.err.println(e); }
//        }


//        // make a list of deleted model objects (small problems with iterator forced me to delete() in 2 step. I first tried delete() as I iterate through the model objects, but the iterator didn't worked very well. It jumped when an element is deleted. )
//        List modelObjectTobeDeleted = new ArrayList();
//        for (Iterator i = base.getModelObjects().iterator(); i.hasNext(); ) {
//            ModelObject modelObj = (ModelObject) i.next();
//            if (modelObj instanceof ConcreteParameter) {
//                modelObjectTobeDeleted.add(modelObj);
//            } else if (modelObj instanceof ConcreteProceduralRelation) {
//                /* procedural relation contains model objects that are not returned by modelBuilder.getModelObjects()
//                   we need to access those model objects to put them in the list of 'modelObjectTobeDeleted' */
//                ConcreteProceduralRelation relation = (ConcreteProceduralRelation) modelObj;
//                Collection modelObjectsInRelation = relation.getModelObjects();
//                for (Iterator j = modelObjectsInRelation.iterator(); j.hasNext(); ) {
//                    Object eachParam = j.next();
//                    if (eachParam instanceof ConcreteParameter) {
//                        modelObjectTobeDeleted.add(eachParam);
//                    }
//                }
//            }
//        }
//
//        // execute the deletion
//        for (Iterator i = modelObjectTobeDeleted.iterator(); i.hasNext(); ) {
//            ModelObject modelObj = (ModelObject) i.next();
//            modelObj.delete(null);
//        }
    }
}
