package mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles;

import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeList;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.util.FileUtils;
import org.exolab.ID.UUIDGenerator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Jul 1, 2003
 * Time: 10:19:02 AM
 * To change this template use Options | File Templates.
 */
public class PluginFilesContextBuilder extends DefaultContextBuilder {
    protected PluginModelBuilder pluginModelBuilder;
    protected EnumerationData indexEnum;
    protected DomeListData fileList;
    protected BooleanData moved_files_can_be_executed;
    protected PropertyChangeListener listener = new fileListListener();

    protected Hashtable fileParam_AuxFile = new Hashtable();//id to search back auxfileId

    protected Hashtable param_file = new Hashtable();
    protected ArrayList listenedParams = new ArrayList();


    public PluginFilesContextBuilder(PluginModelBuilder m) {
        super(m, new Id(UUIDGenerator.create()));
        pluginModelBuilder = m;
        initContext();
    }

    public DomeListData getFileList() {
        return fileList;
    }

    public EnumerationData getMainFileEnumeration() {
        return indexEnum;
    }

    protected void setShouldSave(){
      pluginModelBuilder.setAuxiliaryFilesChanged(true);
    }

    protected void initContext() {
        pluginModelBuilder.addPropertyChangeListener(listener);
        //filelist
        Parameter fileListParam = new ConcreteParameter(pluginModelBuilder,
                                                        new Id(UUIDGenerator.create()),
                                                        DomeList.TYPE_INFO.getTypeName());
        fileList = (DomeListData) fileListParam.getCurrentDataObject();
        fileList.setItemType(FileData.TYPE_INFO.getTypeName());
        fileListParam.setName("model files");

        //index enumeration
        Parameter mainFileEnumParam = new ConcreteParameter(pluginModelBuilder,
                                                            new Id(UUIDGenerator.create()),
                                                            DomeEnumeration.TYPE_INFO.getTypeName());
        mainFileEnumParam.setName("main model file");
        indexEnum = (EnumerationData) mainFileEnumParam.getCurrentDataObject();
        fileList.addPropertyChangeListener(listener);


        Parameter moved_files_can_be_executedParam = new ConcreteParameter(pluginModelBuilder,
                                                                           new Id(UUIDGenerator.create()),
                                                                           DomeBoolean.TYPE_INFO.getTypeName());
        moved_files_can_be_executedParam.setName("moved files can be executed");
        moved_files_can_be_executed = (BooleanData) moved_files_can_be_executedParam.getCurrentDataObject();
	    //For Ideas model do not transfer aux file to server as it is generally too big and causes deployment failure
        if(pluginModelBuilder.getPluginConfiguration().getTypeName().equals("Ideas8 Model")
            || pluginModelBuilder.getPluginConfiguration().getTypeName().equals("Ideas Model"))
	        moved_files_can_be_executed.setValue(false);    //default setting for Ideas
	    else
	        moved_files_can_be_executed.setValue(true);
        loadAuxFileInfos();

        this.setName("files");
        this.addModelObjectReference(fileListParam);
        this.addModelObjectReference(mainFileEnumParam);
        this.addModelObjectReference(moved_files_can_be_executedParam);

    }

    public void loadAuxFileInfos() {
      //  for (int i = 0; i < fileList.getSize(); i++)
      //      pluginModelBuilder.deleteModelObject((Parameter) fileList.getElementValue(i));
        fileList.removeAll();

        ArrayList AuxFiles = pluginModelBuilder.getAuxFiles();
        int mainIndex = 0;
        for (int i = 0; i < AuxFiles.size(); i++) {
            AbstractAuxFile auxFile = (AbstractAuxFile) AuxFiles.get(i);
            Parameter fileParam = new ConcreteParameter(pluginModelBuilder, new Id(UUIDGenerator.create()), DomeFile.TYPE_INFO.getTypeName());
            //Parameter fileParam = fileList.addItem(DomeFile.TYPE_INFO.getTypeName());
            fileList.addItemReference(fileParam);
            fileParam.addPropertyChangeListener(listener);
            listenedParams.add(fileParam);
            FileData file = (FileData) fileParam.getCurrentDataObject();
            file.setFilePath(auxFile.getFile().getPath());
            file.setFileType(FileUtils.getTypeForFile(auxFile.getFile().getPath()));
            fileParam.setName(auxFile.getName());

			moved_files_can_be_executed.setValue(auxFile.isExecuteOnServer());

	        if (auxFile.isMainModelFile()) {
                mainIndex = i;
            }

            fileParam_AuxFile.put(fileParam, auxFile);

            //      indexEnum.addElement(auxFile.getName(),new Integer(i));
        }
        if (indexEnum.getSize() > 0) indexEnum.setLastSelection(mainIndex);
    }

    public boolean getMoved_files_can_be_executed() {
        return moved_files_can_be_executed.getBooleanValue().booleanValue();
    }

    public void setMoved_files_can_be_executed(boolean trueOrfalse) {
        this.moved_files_can_be_executed.setValue(trueOrfalse);
    }

    public void cleanListenedParamStack() {
        for (int i = 0; i < listenedParams.size(); i++) {
            Parameter p = (Parameter) listenedParams.get(i);
            removeDeepListener(p, listener);
        }
        listenedParams.clear();
        param_file.clear();
    }

    public void addDeepListener(Parameter p, PropertyChangeListener l) {

        p.addPropertyChangeListener(l);
        if (p.getCurrentDataObject() instanceof FileData) {
            (p.getCurrentDataObject()).addPropertyChangeListener(l);
            //make a pair here
            param_file.put(p.getCurrentDataObject(), p);
        }

    }

    public void removeDeepListener(Parameter p, PropertyChangeListener l) {
        p.addPropertyChangeListener(l);
        if (p.getCurrentDataObject() instanceof FileData)
            (p.getCurrentDataObject()).removePropertyChangeListener(l);

    }

    public AbstractAuxFile getOriginalAuxFile(Parameter p) {
        for (Enumeration E = fileParam_AuxFile.keys(); E.hasMoreElements();) {
            Parameter param = (Parameter) E.nextElement();
            if (param.getId().toString().equals(p.getId().toString())) return (AbstractAuxFile) fileParam_AuxFile.get(p);
        }
        return null;
    }

    public boolean inStack(AbstractAuxFile f) {
        boolean inStack = false;
        for (Enumeration E = fileParam_AuxFile.keys(); E.hasMoreElements();) {
            Parameter param = (Parameter) E.nextElement();
            if (((AbstractAuxFile) fileParam_AuxFile.get(param)).getId().toString().equals(f.getId().toString())) {
                inStack = true;
                break;
            }
        }
        return inStack;
    }

    public void addAuxFileInfos() {
        //clear old info
        pluginModelBuilder.clearAuxFiles();
        int mainfileindex = indexEnum.getLastSelection();
        for (int i = 0; i < fileList.getSize(); i++) {
            Parameter fileParam = (Parameter) fileList.getElementValue(i);
            addAuxFileInfo(fileParam, (i == mainfileindex) ? true : false);
        }


    }

    public void addAuxFileInfo(Parameter fParam, boolean isMain) {
        Parameter fileParam = fParam;
        FileData file = (FileData) fileParam.getCurrentDataObject();

        if (getOriginalAuxFile(fileParam) == null) {
            AbstractAuxFile auxF = new CommonAuxFile(pluginModelBuilder, new Id(UUIDGenerator.create()), fileParam.getName(), new File(file.getFilePath()));
            Id newAdded = pluginModelBuilder.addAuxFile(auxF);
            auxF.setMainModelFile(false);
            if (isMain) {
                auxF.setMainModelFile(true);
            }
	        auxF.setExecuteOnServer(moved_files_can_be_executed.getBooleanValue().booleanValue());
            fileParam_AuxFile.put(fileParam, auxF);
        } else {
            AbstractAuxFile auxF = getOriginalAuxFile(fileParam);
            auxF.setName(fileParam.getName());
            auxF.setFile(new File(file.getFilePath()));
            auxF.setMainModelFile(false);
            if (isMain) {
                auxF.setMainModelFile(true);
            }
	        auxF.setExecuteOnServer(moved_files_can_be_executed.getBooleanValue().booleanValue());
            pluginModelBuilder.addAuxFile(auxF);
        }
    }

    /**
     * listen to filelist change and change crespondingly
     */
    class fileListListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            //System.out.println(e.getPropertyName());
            if (e.getPropertyName().equals(DomeList.LIST) || e.getPropertyName().equals(DomeList.SIZE)) {
                cleanListenedParamStack();
                int oldMainIndex = indexEnum.getLastSelection();
                indexEnum.clear();
                for (int i = 0; i < fileList.getSize(); i++) {
                    Parameter fileParam = (Parameter) fileList.getElementValue(i);
                    addDeepListener(fileParam, listener);
                    listenedParams.add(fileParam);
                    FileData file = (FileData) fileParam.getCurrentDataObject();
                    File f = new File(file.getFilePath());
                    indexEnum.addElement(fileParam.getName(), f.getPath());
                }
                if (indexEnum.getSize() > 0) {
                    if (oldMainIndex < indexEnum.getSize() && oldMainIndex > -1)
                        indexEnum.setLastSelection(oldMainIndex);
                    else
                        indexEnum.setLastSelection(0);
                }
                setShouldSave();

            } else if (e.getPropertyName().equals(ModelObject.NAME)) {
                if (e.getSource() instanceof Parameter) {
                    Parameter fileParam = (Parameter) e.getSource();
                    int index = fileList.getParameterIndex(fileParam);
                    FileData file = (FileData) fileParam.getCurrentDataObject();
                    File f = new File(file.getFilePath());
                    indexEnum.setElementName(index, fileParam.getName());
                    indexEnum.setElementValue(index, f.getPath());

                }
                setShouldSave();
            } else if (e.getPropertyName().equals(FileData.VALUE) || e.getPropertyName().equals(FileData.FILEPATH) || e.getPropertyName().equals(FileData.FILETYPE)) {
                if (e.getSource() instanceof FileData) {

                    FileData file = (FileData) e.getSource();
                    if (param_file.get(file) == null) return;

                    Parameter fileParam = (Parameter) param_file.get(file);
                    int index = fileList.getParameterIndex(fileParam);
                    File f = new File(file.getFilePath());
                    indexEnum.setElementName(index, fileParam.getName());
                    indexEnum.setElementValue(index, f.getPath());
                }
                setShouldSave();
            } else if (e.getPropertyName().equals(DomeModel.AUXFILES_MODIFIED)) {
                // System.out.println("auxfile changed");
              //  cleanListenedParamStack();
               // loadAuxFileInfos();
            }
        }
    }
}


