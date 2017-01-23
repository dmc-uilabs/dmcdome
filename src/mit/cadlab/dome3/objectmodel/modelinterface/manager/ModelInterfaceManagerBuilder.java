// InterfaceBuilderManager.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelinterface.manager;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelinterface.build.ModelInterfaceManagerBuildPanel;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBuilder;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.swing.WindowTracker;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ModelInterfaceManagerBuilder extends ModelInterfaceManager
{
	protected HashMap interfaceMappingFileMap; //key=iface id, value=iface mapping file instance
	protected ModelInterfaceBuilder defaultInterface = null;

	public ModelInterfaceManagerBuilder(Model model)
	{
		super(new Id(UUIDGenerator.create()), model);
		interfaceMappingFileMap = new HashMap();
	}

	public ModelInterface duplicateInterface(ModelInterface mi)
	{
		return duplicateInterface(mi, true);
	}

	public ModelInterface duplicateInterface(ModelInterface mi, boolean changeName)
	{
		ModelInterface dupMi = null;
		dupMi = new ModelInterfaceBuilder(model, new Id(UUIDGenerator.create()), mi, changeName);
		interfaces.add(dupMi);
		return dupMi;
	}

	public boolean deleteInterface(ModelInterface mi)
	{
		if (mi instanceof ModelInterfaceBuilder) {
			if (((ModelInterfaceBuilder) mi).isDefaultInterface()) {
				OneButton1Msg.showWarning(null, "Warning: Delete interface",
				                          "Default interface cannot be deleted.",
				                          "OK", new Dimension(200, 100));
				return false; //do not delete default interface
			}
		}
		// delete interface file
		if(modelFileName == null && model instanceof DomeModelBuilder) {
				modelFileName = ((DomeModelBuilder) model).getFileName();
		}
		else if (modelFileName == null && model instanceof IntegrationProjectBuilder) {
				modelFileName = ((IntegrationProjectBuilder) model).getFileName();
		}
		if (modelFileName != null) {
			ModelInterfaceBuilder iface = (ModelInterfaceBuilder) mi;
			if (!iface.isDefaultInterface()) {
				String ifacePath = getInterfacePath(modelFileName);
				String ifaceFileName = null;
				if(model instanceof DomeModelBuilder) {
					ifaceFileName = ifacePath + slash + iface.getId().toString() + ".dmi";
				}
				else if(model instanceof IntegrationProjectBuilder) {
					ifaceFileName = ifacePath + slash + iface.getId().toString() + ".dpi";
				}
				File ifaceFile = new File(ifaceFileName);
				if (ifaceFile != null)
					ifaceFile.delete();
				String ifaceMappingFileName = ifacePath + slash + iface.getId().toString() + "-mappings";
				File ifaceMappingFile = new File(ifaceMappingFileName);
				if (ifaceMappingFile != null)
					ifaceMappingFile.delete();
			}
		}
		if (mi instanceof ModelInterfaceBuilder) {
			((ModelInterfaceBuilder) mi).removeMappingsAndConnectionsBeforeDelete();
		}
		closeGUIonDelete(mi);
		return interfaces.remove(mi);
	}

	public ModelInterface newInterface(int index)
	{
		ModelInterface mi = new ModelInterfaceBuilder(model, new Id(UUIDGenerator.create()));
		interfaces.add(index, mi);
		return mi;
	}

	public Collection duplicateInterfaces(int[] indices)
	{
		if (indices == null || indices.length == 0) return Collections.EMPTY_LIST;
		Arrays.sort(indices);
		List duplicateInterfaces = new ArrayList();
		for (int i = 0; i < indices.length; ++i) { // should do try/catch here
			duplicateInterfaces.add(new ModelInterfaceBuilder(model, new Id(UUIDGenerator.create()),
			                                                  (ModelInterface) interfaces.get(indices[i])));
		}
		interfaces.addAll(duplicateInterfaces);
		return duplicateInterfaces;
	}

	public void deleteInterfaces(int[] indices)
	{
		//To avoid ConcurrentModificationException
		ArrayList tobeRemoved = new ArrayList();
		if (indices == null || indices.length == 0) return;
		Arrays.sort(indices);
		for (int i = 0; i < indices.length; i++) {
			ModelInterface face = (ModelInterface) interfaces.get(indices[i]);
			tobeRemoved.add(face);
		}
		for (Iterator i = tobeRemoved.iterator(); i.hasNext();) {
			ModelInterface face = (ModelInterface) i.next();
			deleteInterface(face);
		}
	}

	private void closeGUIonDelete(ModelInterface face)
	{
		WindowTracker tracker = BuildMode.getWindowTracker(face);
		List children = tracker.getChildren();
		//first collect all the frames related to an interface and then
		//close all.  Done so to avoid ConcurrentmodificationException
		List framesToClose = new ArrayList();
		for (Iterator j = children.iterator(); j.hasNext();) {
			DomeBuildFrame buildFrame = (DomeBuildFrame) j.next();
			DomeGui gui = buildFrame.getGui();
			if (gui instanceof ModelInterfaceBuildPanel) {
				Object dobj = gui.getGuiObject();
				if (dobj.equals(face)) {
					framesToClose.add(buildFrame);
				}
			} else if (!(gui instanceof ModelInterfaceManagerBuildPanel)) {
				framesToClose.add(buildFrame);
			}
		}
		Object[] frameArray = framesToClose.toArray();
		for (int i = 0; i < frameArray.length; i++) {
			DomeBuildFrame frame = (DomeBuildFrame) frameArray[i];
			frame.close();
		}
	}

	public void moveInterfacesUp(int[] indices)
	{
		interfaces.shiftLeft(indices);
	}

	public void moveInterfacesDown(int[] indices)
	{
		interfaces.shiftRight(indices);
	}

	protected String getInterfacePath(String modelFileName)
	{
		this.modelFileName = modelFileName;

		// construct interface path
		File modelFile = new File(modelFileName);
		String ifacePath = null;
		if(model instanceof DomeModel) {
			ifacePath = modelFile.getParentFile() + slash + "interfaces";
			String modelFileNameNoExtension = modelFile.getName();
			if (modelFileNameNoExtension.lastIndexOf('-') > 0)
				modelFileNameNoExtension = modelFileNameNoExtension.substring(0, modelFileNameNoExtension.lastIndexOf('-'));
			ifacePath += "-" + modelFileNameNoExtension + "-" + getModel().getId().toString();
		}
		else if(model instanceof IntegrationProject) {
			String projectFileNameNoExtension = modelFileName.substring(modelFileName.lastIndexOf(slash) + 1, modelFileName.lastIndexOf('.'));
			ifacePath = modelFile.getParentFile() + slash + projectFileNameNoExtension + "-resources" + slash + "interfaces";
			ifacePath += "-" + projectFileNameNoExtension + "-" + getModel().getId().toString();
		}
		return ifacePath;
	}

	protected String getExistingInterfacePath(String modelFileName, String idString)
	{
		this.modelFileName = modelFileName;

		// construct interface path
		File modelFile = new File(modelFileName);
		String ifacePath = modelFile.getParentFile() + slash + "interfaces";
		String modelFileNameNoExtension = modelFile.getName();
		if (modelFileNameNoExtension.lastIndexOf('-') > 0)
			modelFileNameNoExtension = modelFileNameNoExtension.substring(0, modelFileNameNoExtension.lastIndexOf('-'));
		ifacePath += "-" + modelFileNameNoExtension + "-" + idString;
		return ifacePath;
	}

	public void loadInterfaces(String modelFileName)
	{
		String ifacePath = getInterfacePath(modelFileName);
		File ifaceDir = new File(ifacePath);
		File[] ifaceFiles = ifaceDir.listFiles();
		// create the interfaces
		if (ifaceFiles != null) {
			for (int i = 0; i < ifaceFiles.length; i++) {
				if (ifaceFiles[i].isFile() &&
				        ifaceFiles[i].getName().endsWith("-mappings")) {   //mapping file
					loadMappingFileMap(ifaceFiles[i]);
				}
			}
			//then load the interfaces
			for (int i = 0; i < ifaceFiles.length; i++) {
				if (ifaceFiles[i].isFile()) {
					if(model instanceof DomeModel && ifaceFiles[i].getName().endsWith(".dmi")) {
						readInterfaceFile(ifaceFiles[i]);
					}
					else if(model instanceof IntegrationProject && ifaceFiles[i].getName().endsWith(".dpi")) {
						readInterfaceFile(ifaceFiles[i]);
					}
				}
			}
		}
	}

	private void readInterfaceFile(File file)
	{
		int endindex = 0;
		if(model instanceof DomeModel) {
			endindex = file.getName().indexOf(".dmi");
		}
		else if(model instanceof IntegrationProject) {
			endindex = file.getName().indexOf(".dpi");
		}
		int startindex = file.getName().lastIndexOf(slash) + 1;
		String ifaceid = file.getName().substring(startindex, endindex);
		File mappingFile = (File) interfaceMappingFileMap.get(ifaceid);
		SAXReader reader = new SAXReader();
		Document ifaceDoc = null;
		Document ifaceMap = null;
		try {
			ifaceDoc = reader.read(file);
			if (mappingFile != null) {
				ifaceMap = reader.read(mappingFile);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Element xmlElement = (Element) ifaceDoc.getRootElement().selectSingleNode("/modelinterface/interfaceinfo");
		boolean isdefaultIface = false;
		Element defaultIfaceElement = (Element) xmlElement.selectSingleNode(ModelInterface.DEFAULT_INTERFACE_XML_TAG);
		if (defaultIfaceElement != null)
			isdefaultIface = true;
		Element rootelement = ifaceDoc.getRootElement();
		try {
			// create the interface
			ModelInterfaceBuilder mi = new ModelInterfaceBuilder(model, rootelement,
			                                                     (ifaceMap == null) ? null : ifaceMap.getRootElement());
			if (mi.isDefaultInterface()) {
				setDefaultInterface(mi);
				((DomeModelBuilder) model).setDefaultInterface(mi); // todo model shouldn't need to know default iface!
			}
			interfaces.add(mi);
		}
		catch(Exception e) {
           if(isdefaultIface) {
	           String msg = "There was an error loading default interface for " + model.getName() +
	                   ". An empty default interface will be created.";
	           OneButton1Msg.showWarning(null, "Warning: Default Interface for " + model.getName(), msg, "OK", new Dimension(1,1));
	           ModelInterfaceBuilder mi = null;
	           //try the old id etc first
	           try {
		           mi = new ModelInterfaceBuilder(model, new Id("")); //id will be overwritten in the next statement
		           mi.parseHeader(rootelement);
		           mi.setIsDefaultInterface();
	           }
	           catch(Exception ie) { //if header cannot be loaded, assign new Id to the interface
	               mi = new ModelInterfaceBuilder(model, UUIDGenerator.create(), "Default Interface");
		           mi.setIsDefaultInterface();
	           }
	           setDefaultInterface(mi);
	           ((DomeModelBuilder) model).setDefaultInterface(mi);
	           interfaces.add(mi);
           }
		}

	}

	public void setDefaultInterface(ModelInterfaceBuilder iface)
	{
		if (defaultInterface != null) {
			throw new RuntimeException("more than one default interface found for this model-correct situation");
		} else {
			defaultInterface = iface;
		}
	}

	private void loadMappingFileMap(File mappingfile)
	{
		int endindex = mappingfile.getName().indexOf("-mappings");
		int startindex = mappingfile.getName().lastIndexOf(slash);
		if (startindex == -1) {
			startindex = 0;
		}
		String ifaceid = mappingfile.getName().substring(startindex, endindex);
		interfaceMappingFileMap.put(ifaceid, mappingfile);
	}

	public boolean defaultInterfaceExists()
	{
		return defaultInterface != null;
	}

	public ModelInterface newInterface()
	{
		ModelInterface mi = new ModelInterfaceBuilder(model, new Id(UUIDGenerator.create()));
		interfaces.add(mi);
		return mi;
	}

	public boolean addInterface(ModelInterface mi)
	{
		if (interfaces.add(mi)) {
			if (((ModelInterfaceBuilder) mi).isDefaultInterface())
				setDefaultInterface((ModelInterfaceBuilder) mi);
			return true;
		}
		return false;
	}

	public void save(String modelFileName) throws IOException
	{
        //this has been commented out so that all contents of the interface directory, including CVS info, are not deleted.
		//destroyExistingInterfaces(modelFileName);

		// create interfaces directory
		String ifacePath = getInterfacePath(modelFileName);
		File ifaceDir = new File(ifacePath);
		if (!ifaceDir.exists())
			ifaceDir.mkdir();

		// save the interfaces
		for (Iterator iter = interfaces.iterator(); iter.hasNext();) {
			String ifaceFileName = null;
			ModelInterfaceBuilder iface = (ModelInterfaceBuilder) iter.next();
			ifaceFileName = null;
			if(model instanceof DomeModel) {
				ifaceFileName = ifacePath + slash + iface.getId().toString() + ".dmi";
			}
			else if(model instanceof IntegrationProject) {
				ifaceFileName = ifacePath + slash + iface.getId().toString() + ".dpi";
			}
			iface.save(ifaceFileName);
		}
	}

	private void destroyExistingInterfaces(String modelFileName)
	{
		SAXReader reader = new SAXReader();
		Document modelDoc = null;
		File modelFile = null;
		// parse the model file
		try {
			modelFile = new File(modelFileName);
			if (!modelFile.exists())
				return;
			modelDoc = reader.read(modelFile);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// construct the existing interface path
		Element modelElement = modelDoc.getRootElement();
		String idString = modelElement.attributeValue("id");
		String ifacePath = getExistingInterfacePath(modelFileName, idString);

		// destroy interface directory
		File ifaceDir = new File(ifacePath);
		if (ifaceDir.exists()) {
			File[] files = ifaceDir.listFiles();
			deleteAllFiles(files);
			ifaceDir.delete();
		}
	}

	void deleteAllFiles(File[] files)
	{
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) { // default directory
				deleteAllFiles(files[i].listFiles());
			}
			files[i].delete();
		}
	}

	public File getInterfaceMappingFile(String ifaceid)
	{
		return (File) interfaceMappingFileMap.get(ifaceid);
	}
}
