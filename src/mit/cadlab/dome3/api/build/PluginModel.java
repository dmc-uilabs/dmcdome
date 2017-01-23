package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.dataobject.*;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.PluginFilesContextBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManagerBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.causality.ImmutableDependencyInfo;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.plugin.excel.ExcelConfiguration;
import mit.cadlab.dome3.plugin.matlab.MatlabConfiguration;
import mit.cadlab.dome3.util.Converters;
import mit.cadlab.dome3.util.DomeException;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.DomeClientApplication;
import org.dom4j.Element;
import org.exolab.ID.UUIDGenerator;

import java.util.*;

public class PluginModel {
    protected PluginModelBuilder modelBuilder;
    protected PluginFilesContextBuilder pluginfiles;
    protected String transientId;

    // for making a brand new plugin model
    public PluginModel(String pluginTypeName) {
        DomeClientApplication.DOME_API_BUILD_MODE = true;
        modelBuilder = new PluginModelBuilder(UUIDGenerator.create(), pluginTypeName);
        pluginfiles = new PluginFilesContextBuilder(modelBuilder);
    }

    // for openning a saved plugin model
    public PluginModel(String filename, boolean isFromSavedFile) {
        DomeClientApplication.DOME_API_BUILD_MODE = true;
        Element modelElement = XMLUtils.fileToXmlElement(filename);
        modelBuilder = new PluginModelBuilder(filename, modelElement);
        pluginfiles = new PluginFilesContextBuilder(modelBuilder);
    }

    public void save(String filename) {
        DomeClientApplication.DOME_API_BUILD_MODE = true;
        pluginfiles.addAuxFileInfos();
        modelBuilder.setAuxiliaryFilesChanged(false);
        filename = DomeModelBuilder.fixFileName(filename, modelBuilder.getModelExtension());
        modelBuilder.superSaveNoGui(filename);
    }

    public void setName(String name) {
        modelBuilder.setName(name);
    }

    public String getName() {
        return modelBuilder.getName();
    }

    public RealParameter addRealParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.REAL);
        RealParameter realp = new RealParameter(p);
        realp.setName(name);
        return realp;
    }

    protected ConcreteParameter addConcreteParameter(String type) {
        ConcreteParameter p = (ConcreteParameter) modelBuilder.newModelObject(type);
        getBuildContext().addModelObjectReference(p);
        return p;
    }

    protected DefaultContextBuilder getBuildContext() {
        return (DefaultContextBuilder) modelBuilder.getModelObjectById(DomeModel.BUILD_CONTEXT_ID);
    }

    public void setParameterMapping(Parameter p, String reference) {
        modelBuilder.getPluginMappingManager().addMapping(p.getConcreteParameter(), reference);
    }

    public String getParameterMapping(Parameter p) {
        return modelBuilder.getPluginMappingManager().getMappingObjectForParameter(p.getConcreteParameter()).toString();
    }

    public void setParametersDependency(Parameter p, Collection drivers) {
        ArrayList list = new ArrayList();
        for (Iterator iterator = drivers.iterator(); iterator.hasNext();) {
            Parameter driver = (Parameter) iterator.next();
            list.add(driver.getConcreteParameter());
        }
        DependencyInfo dInfo = getDependencyInfo();
        ConcreteParameter driven = p.getConcreteParameter();
        dInfo.clearDependency(driven);
        dInfo.setDependency(driven, list);
        try {
            dInfo.validate();
        } catch (Exception e) {
            for (int i = 0; i < list.size(); i++) {
                dInfo.removeDependency(driven, list.get(i));
            }
            throw new DomeException("The specified dependency forms one or more loops: " + e.toString());
        }
        modelBuilder.setDependencyInfo(dInfo);
    }

    public List getParametersDependency(Parameter p) {
        DependencyInfo dInfo = getDependencyInfo();
        Collection co = dInfo.getDependentsForObject(p.getConcreteParameter());
        ArrayList list = new ArrayList();
        for (Iterator iterator = co.iterator(); iterator.hasNext();) {
            list.add(Parameter.wrapParameter((ConcreteParameter) iterator.next()));
        }
        return list;
    }

    public List getDependents() {
        DependencyInfo dInfo = getDependencyInfo();
        Set keys = dInfo.getDependencyKeys();
        ArrayList list = new ArrayList();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            list.add(Parameter.wrapParameter((ConcreteParameter) iterator.next()));
        }
        return list;
    }

    public void setParametersDependency(Parameter p, Parameter driver) {
        ArrayList list = new ArrayList();
        list.add(driver);
        setParametersDependency(p, list);
    }

    public void clearParametersDependency(Parameter p) {
        DependencyInfo dInfo = getDependencyInfo();
        ConcreteParameter driven = p.getConcreteParameter();
        dInfo.clearDependency(driven);
        dInfo.validate();
        modelBuilder.setDependencyInfo(dInfo);
    }

    public DependencyInfo getDependencyInfo() {
        DependencyInfo dinfo = modelBuilder.getDependencyInfo();
        return dinfo instanceof ImmutableDependencyInfo ? ((ImmutableDependencyInfo) dinfo).getMutableDependencyInfo()
                : dinfo;
    }

    public FileParameter addSetupFile(String name, String filepath) {
        FileParameter p = new FileParameter((ConcreteParameter) pluginfiles.getFileList().addItem(DataTypeConstants.FILE));
        p.setName(name);
        p.setType(FileUtils.getTypeForFile(filepath));
        p.setPath(filepath);
        return p;
    }

    public void setAsMainSetupFile(FileParameter p) {
        pluginfiles.getMainFileEnumeration().setLastSelectionToName(p.getName());
    }

    public void setAsMainSetupFile(String fileparametername) {
        pluginfiles.getMainFileEnumeration().setLastSelectionToName(fileparametername);
    }

    public String getMainSetupFileName() {
        return pluginfiles.getMainFileEnumeration().getElementName(pluginfiles.getMainFileEnumeration().getLastSelection());
    }

    public String deleteSetupFile(FileParameter p) {
        DomeListData list = pluginfiles.getFileList();
        for (int i=0; i<list.getSize(); i++) {
            if (((ConcreteParameter) list.getElementValue(i)).getName().equals(p.getName()))
                list.removeElementAt(i);
        }
        // from here on is (an improvement?) beyond what dome build gui does
        deleteParameter(p);
        EnumerationData enm = pluginfiles.getMainFileEnumeration();
/*        for (int i = 0; i < enum.getSize(); i++) {
            if(enum.getElementName(i).equals(p.getName()))
                enum.removeElementAt(i);
        }*/
        return enm.getSize() > 0 ? enm.getElementName(enm.getLastSelection()) : "";
    }

    public String deleteSetupFile(String fileparametername) {
        return deleteSetupFile((FileParameter) getParameterByName(fileparametername));
    }

    public List getValidDataTypes() {
        String[] types = modelBuilder.getPluginConfiguration().getValidDataTypes();
        return Collections.unmodifiableList(Converters.toOrderedSet(types));
    }

    public List getSetupParameterNames() {
        List params = modelBuilder.getPluginConfiguration().getSetupParameters();
        ArrayList names = new ArrayList();
        for (int i = 0; i < params.size(); i++) {
            ConcreteParameter p = (ConcreteParameter) params.get(i);
            names.add(p.getName());
        }
        return Collections.unmodifiableList(names);
    }

    public List getSetupEnumerationParameterList(String name) {
        ConcreteParameter p = (ConcreteParameter) modelBuilder.getPluginConfiguration().getSetupParameter(name);
        if (p==null || ! (p.getCurrentDataObject() instanceof EnumerationData))
            return Collections.unmodifiableList(new ArrayList());
        else {
            List vals = p.getCurrentDataObject().getValues();
            ArrayList list = new ArrayList();
            for (int i = 0; i < vals.size(); i++) {
                EnumerationItem item = (EnumerationItem) vals.get(i);
                list.add(item.getName());
            }
            return Collections.unmodifiableList(list);
        }
    }

    public void setSetupEnumerationParameterSelection(String parameterName, String selectionName) {
        ConcreteParameter p = (ConcreteParameter) modelBuilder.getPluginConfiguration().getSetupParameter(parameterName);
        if (p != null && (p.getCurrentDataObject() instanceof EnumerationData))
            ((EnumerationData) p.getCurrentDataObject()).setLastSelectionToName(selectionName);
    }

    public boolean getSetupBooleanParameterValue(String name) {
        ConcreteParameter p = (ConcreteParameter) modelBuilder.getPluginConfiguration().getSetupParameter(name);
        if (p == null  || !(p.getCurrentDataObject() instanceof BooleanData))
            return false;
        else
            return ((BooleanData) p.getCurrentDataObject()).getValue();
    }

    public void setSetupBooleanParameterValue(String parameterName, boolean value) {
        ConcreteParameter p = (ConcreteParameter) modelBuilder.getPluginConfiguration().getSetupParameter(parameterName);
        if (p != null && (p.getCurrentDataObject() instanceof BooleanData))
            ((BooleanData) p.getCurrentDataObject()).setValue(value);
    }

    public String getSetupStringParameterValue(String name) {
        ConcreteParameter p = (ConcreteParameter) modelBuilder.getPluginConfiguration().getSetupParameter(name);
        if (p == null || !(p.getCurrentDataObject() instanceof StringData))
            return "";
        else
            return ((StringData) p.getCurrentDataObject()).getValue();
    }

    public void setSetupStringParameterValue(String parameterName, String value) {
        ConcreteParameter p = (ConcreteParameter) modelBuilder.getPluginConfiguration().getSetupParameter(parameterName);
        if (p != null && (p.getCurrentDataObject() instanceof StringData))
            ((StringData) p.getCurrentDataObject()).setValue(value);
    }

    public Interface getDefaultInterface() {
        return getInterface(ModelInterface.DEFAULT_IFACE_TAG);
    }

    public Interface getInterface(String name) {
        Collection ifaces = modelBuilder.getModelInterfacesManager().getInterfaces();
        for (Iterator iterator = ifaces.iterator(); iterator.hasNext();) {
            ModelInterfaceBuilder iface = (ModelInterfaceBuilder) iterator.next();
            if (iface.getName().equals(name))
                return new Interface(iface);
        }
        return null;
    }

    public List getInterfaceNames() {
        ArrayList names = new ArrayList();
        Collection ifaces = modelBuilder.getModelInterfacesManager().getInterfaces();
        for (Iterator iterator = ifaces.iterator(); iterator.hasNext();) {
            ModelInterfaceBuilder iface = (ModelInterfaceBuilder) iterator.next();
            names.add(iface.getName());
        }
        return names;
    }

    public Interface addInterface(String name) {
        ModelInterfaceManagerBuilder mgr = (ModelInterfaceManagerBuilder) modelBuilder.getModelInterfacesManager();
        ModelInterfaceBuilder ifaceBuilder = (ModelInterfaceBuilder) mgr.newInterface();
        return new Interface(ifaceBuilder, name);
    }

    public void close() {
        DomeModelBuilder.deleteAllConcreteParameters(modelBuilder);
        modelBuilder = null;
        pluginfiles = null;
    }

    public void deleteParameters(List paramList) {
        ConnectionMappingManager mgr = modelBuilder.getMappingManager();
        List mObjs = new ArrayList();
        for (int i = 0; i < paramList.size(); i++) {
            Parameter p = (Parameter) paramList.get(i);
            ConcreteParameter cp = p.getConcreteParameter();
            mObjs.add(cp);
            mgr.removeAllMappings(cp);
        }
        modelBuilder.deleteModelObjects(mObjs);
    }

    public void deleteParameter(Parameter p) {
        ArrayList l = new ArrayList();
        l.add(p);
        deleteParameters(l);
    }

    public Parameter getParameterByName(String name) {
        List params = modelBuilder.getParameters();
        for (int i = 0; i < params.size(); i++) {
            ConcreteParameter p = (ConcreteParameter) params.get(i);
            if (p.getName().equals(name)) {
                return Parameter.wrapParameter(p);
            }
        }
        return null;
    }

    public List getAllParameters() {
        Collection params = modelBuilder.getBuildContext().getFlattenedContentSet();
        ArrayList list = new ArrayList();
        for (Iterator iterator = params.iterator(); iterator.hasNext();) {
            ConcreteParameter p = (ConcreteParameter) iterator.next();
            list.add(Parameter.wrapParameter(p));
        }
        return list;
    }

    public MatrixParameter addMatrixParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.MATRIX);
        MatrixParameter matp = new MatrixParameter(p);
        matp.setName(name);
        return matp;
    }

    public EnumerationParameter addEnumerationParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.ENUMERATION);
        EnumerationParameter enump = new EnumerationParameter(p);
        enump.setName(name);
        return enump;
    }

    public VectorParameter addVectorParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.VECTOR);
        VectorParameter vec = new VectorParameter(p);
        vec.setName(name);
        return vec;
    }

    public IntegerParameter addIntegerParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.INTEGER);
        IntegerParameter intp = new IntegerParameter(p);
        intp.setName(name);
        return intp;
    }

    public BooleanParameter addBooleanParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.BOOLEAN);
        BooleanParameter boo = new BooleanParameter(p);
        boo.setName(name);
        return boo;
    }

    public StringParameter addStringParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.STRING);
        StringParameter str = new StringParameter(p);
        str.setName(name);
        return str;
    }

    public FileParameter addFileParameter(String name) {
        ConcreteParameter p = addConcreteParameter(DataTypeConstants.FILE);
        FileParameter file = new FileParameter(p);
        file.setName(name);
        return file;
    }

    public String[][] getPluginTypes() {
        //todo add more types
        return new String[][] {
            {ExcelConfiguration.TYPE_INFO.getTypeName(), ExcelConfiguration.TYPE_INFO.getXmlType()},
            {MatlabConfiguration.TYPE_INFO.getTypeName(), MatlabConfiguration.TYPE_INFO.getXmlType()},
        };
    }

    public String getPluginTypeName() {
        return modelBuilder.getPluginTypeName();
    }

    public List getAllSetupFileValues() {
        ArrayList vals = new ArrayList();
        List values = pluginfiles.getFileList().getValues();
        for (int i = 0; i < values.size(); i++) {
            ConcreteParameter p = (ConcreteParameter) values.get(i);
            vals.add(((FileData) p.getCurrentDataObject()).getFilePath());
        }
        return vals;
    }

    public List getAllSetupFileNames() {
        ArrayList vals = new ArrayList();
        List values = pluginfiles.getFileList().getValues();
        for (int i = 0; i < values.size(); i++) {
            ConcreteParameter p = (ConcreteParameter) values.get(i);
            vals.add(p.getName());
        }
        return vals;
    }

    public String getModelExtension() {
        return modelBuilder.getModelExtension();
    }

    public String getTransientId() {
        return transientId;
    }

    public void setTransientId(String transientId) {
        this.transientId = transientId;
    }

    public FileParameter getSetupFileParameter(String name) {
        List values = pluginfiles.getFileList().getValues();
        for (int i = 0; i < values.size(); i++) {
            ConcreteParameter p = (ConcreteParameter) values.get(i);
            if (p.getName().equals(name))
                return new FileParameter(p);
        }
        return null;
    }
}
