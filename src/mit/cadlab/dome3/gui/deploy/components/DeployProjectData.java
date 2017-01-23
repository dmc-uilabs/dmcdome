// DeployProjectData.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.DomePropertyChangeSupport;
import mit.cadlab.dome3.util.xml.DomeXmlData;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.network.server.db.DbConstants;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileFilter;

import java.beans.PropertyChangeListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


public class DeployProjectData
{

	public static final String NUM_PROJECT_INT_AVAILABLE = "numProjectIntAvailable";
	protected int numProjectInterfacesAvailable = 0;
	private DomePropertyChangeSupport listeners = new DomePropertyChangeSupport(this);

	private static final String imodelExt = "imodel.dml";


	protected String fileName, buildId, name, xmlContent, description = "";
	protected List interfaces = new ArrayList(), imodels = new ArrayList();
	protected Vector editPermissions = new Vector(), contentVisibilityPermissions = new Vector();

	public DeployProjectData(String fileName)
	{
		this.fileName = fileName;
		try {
			xmlContent = FileUtils.readTextFileAsString(fileName);
			DomeXmlData xmlData = new DomeXmlData(DomeXmlData.PROJECT, xmlContent);
			buildId = xmlData.getId();
			name = xmlData.getName();
			loadInterfacesAndIntegrationModels();
			registerListeners();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	protected void loadInterfacesAndIntegrationModels() throws FileNotFoundException
	{
		File supportDirectory = IntegrationProjectBuilder.getSupportDirectory(fileName);
		File interfacesDirectory = IntegrationProjectBuilder.getInterfaceDirectory(supportDirectory, fileName, buildId);
		if (interfacesDirectory.exists()) {
			File[] ifaceFiles = interfacesDirectory.listFiles(new InterfaceFileFilter());
			for (int i = 0; i < ifaceFiles.length; i++) {
				interfaces.add(loadInterface(ifaceFiles[i]));
			}
		}
		File[] files = supportDirectory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile()) {
				if (DomeFileChooser.getModelExtension(file).equals(imodelExt)) {
					imodels.add(loadIntegrationModel(file));
				} else {
					System.out.println("unknown file: " + file.getAbsolutePath());
				}
			}
		}
	}

	protected DeployProjectInterfaceData loadInterface(File f) throws FileNotFoundException
	{
		String fileName = f.getAbsolutePath();
		String xmlContent = FileUtils.readTextFileAsString(f);
		DomeXmlData xmlData = new DomeXmlData(DomeXmlData.PROJECTINTERFACE, xmlContent);
		String mappingsFileName = fileName.substring(0, fileName.length() - 4) + "-mappings";
		String xmlMappings = FileUtils.readTextFileAsString(mappingsFileName);
		return new DeployProjectInterfaceData(fileName, xmlContent, xmlMappings, xmlData.getId(), xmlData.getName());
	}

	protected DeployModelData loadIntegrationModel(File f)
	{
		return DeployUtilities.loadModelForDeploy(f.getAbsolutePath());
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getBuildId()
	{
		return buildId;
	}

	public String getName()
	{
		return name;
	}

	public String getXmlContent()
	{
		return xmlContent;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String newDescription)
	{
		if (newDescription == null)
			description = "";
		else
			description = newDescription;
	}

	public Vector getEditPermissions()
	{
		return editPermissions;
	}

	public void setEditPermissions(Vector editPermissions)
	{
		if (editPermissions == null)
			editPermissions = DbConstants.EMPTY_VECTOR;
		else
			this.editPermissions = editPermissions;
	}

	public Vector getContentVisibilityPermissions()
	{
		return contentVisibilityPermissions;
	}

	public void setContentVisibilityPermissions(Vector contentVisibilityPermissions)
	{
		if (contentVisibilityPermissions == null)
			contentVisibilityPermissions = DbConstants.EMPTY_VECTOR;
		else
			this.contentVisibilityPermissions = contentVisibilityPermissions;
	}

	/**
	 * @return list of DeployInterfaceData structures
	 */
	public List getInterfaces()
	{
		return interfaces;
	}

	/**
	 * @return list of DeployModelData structures
	 */
	public List getIntegrationModels()
	{
		return imodels;
	}

	public String toString()
	{
		return "Project: " + name + "\t" + buildId + "\ninterfaces:\n" + interfaces + "\nintegration models:\n" + imodels;
	}

	public static class InterfaceFileFilter implements FileFilter
	{
		public boolean accept(File pathname)
		{
			return DomeFileChooser.PROJECTINTERFACE_FILE_EXTENSION.equals(DomeFileChooser.getExtension(pathname));
		}
	}

	protected void setNumAvailable()
	{
		Integer oldAvailable = new Integer(numProjectInterfacesAvailable);
		numProjectInterfacesAvailable = 0;
		ListIterator iterator = interfaces.listIterator();
		while (iterator.hasNext()) {
			if (((DeployProjectInterfaceData) iterator.next()).getIsAvailable().booleanValue()) numProjectInterfacesAvailable++;
		}
		listeners.firePropertyChange(DeployProjectData.NUM_PROJECT_INT_AVAILABLE, oldAvailable, new Integer(numProjectInterfacesAvailable));

	}

	public int getNumAvailable()
	{
		return this.numProjectInterfacesAvailable;
	}

	protected void registerListeners()
	{
		InterfaceAvailableListener l = new InterfaceAvailableListener();
		ListIterator iterator = this.interfaces.listIterator();
		while (iterator.hasNext()) {
			((DeployProjectInterfaceData) (iterator.next())).addPropertyChangeListener(DeployInterfaceData.IS_AVAILABLE, l);
		}
	}

	class InterfaceAvailableListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			DeployProjectData.this.setNumAvailable();
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(propertyName, listener);
	}

	public static void main(String[] args)
	{
		DomeInit.initializeDOME(); // load datatypes since interfaces are loaded
		DomeFileChooser fc = new DomeFileChooser();
		String projFileName = fc.showOpenDialog(null, DomeFileChooser.DOME_PROJECT_FILTER);
		if (projFileName == null)
			return;
		System.out.println(new DeployProjectData(projFileName));
		System.exit(0);
	}
}
