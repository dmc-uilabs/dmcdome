package toolinterfaceworkspace.objectmodel.toolinterface.manager;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterfaceBuilder;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;
import toolinterfaceworkspace.gui.objectmodel.toolinterface.build.ToolInterfaceBuildPanel;
import toolinterfaceworkspace.gui.objectmodel.toolinterface.build.ToolInterfaceManagerBuildPanel;

import java.util.*;
import java.io.File;
import java.net.MalformedURLException;

import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.util.id.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.tool.ToolModel;
import mit.cadlab.dome3.tool.ToolModelBuilder;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.swing.WindowTracker;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 11:07:10 AM
 * To change this template use Options | File Templates.
 */
public class ToolInterfaceManagerBuilder extends ToolInterfaceManager
{
    protected HashMap interfaceMappingFileMap; //key=iface id, value=iface mapping file instance
	protected ToolInterfaceBuilder toolInterface = null;

	public ToolInterfaceManagerBuilder(ToolModel model)
	{
		super(new Id(UUIDGenerator.create()), model);
		interfaceMappingFileMap = new HashMap();
	}

	public ToolInterface duplicateInterface(ToolInterface mi)
	{
		return duplicateInterface(mi, true);
	}

	public ToolInterface duplicateInterface(ToolInterface mi, boolean changeName)
	{
		ToolInterface dupMi = null;
        return null;
	}

	public boolean deleteInterface(ToolInterface mi)
	{
		// delete interface file
		if (toolFileName == null && model instanceof ToolModelBuilder)
        {
            toolFileName = ((ToolModelBuilder) model).getFileName();
        }
		if (toolFileName != null)
        {
            ToolInterfaceBuilder iface = (ToolInterfaceBuilder) mi;
            String ifacePath = getInterfacePath(toolFileName);
            String ifaceFileName = null;
            if (model instanceof ToolModelBuilder)
            {
                ifaceFileName = ifacePath + slash + iface.getId().toString() + ".dti";
            }
            else if (model instanceof IntegrationProjectBuilder)
            {
                ifaceFileName = ifacePath + slash + iface.getId().toString() + ".dti";
            }
            File ifaceFile = new File(ifaceFileName);
            if (ifaceFile != null)
                ifaceFile.delete();
            String ifaceMappingFileName = ifacePath + slash + iface.getId().toString() + "-mappings";
            File ifaceMappingFile = new File(ifaceMappingFileName);
            if (ifaceMappingFile != null)
                ifaceMappingFile.delete();
        }
        if (mi instanceof ToolInterfaceBuilder)
        {
            ((ToolInterfaceBuilder) mi).removeMappingsAndConnectionsBeforeDelete();
        }
        closeGUIonDelete(mi);
        return interfaces.remove(mi);
    }

	public ToolInterface newInterface(int index)
	{
        return null;
	}

	public Collection duplicateInterfaces(int[] indices)
	{
		if (indices == null || indices.length == 0) return Collections.EMPTY_LIST;
		Arrays.sort(indices);
		List duplicateInterfaces = new ArrayList();
		for (int i = 0; i < indices.length; ++i) { // should do try/catch here
//			duplicateInterfaces.add(new ModelInterfaceBuilder(model, new Id(UUIDGenerator.create()),
//			                                                  (ModelInterface) interfaces.get(indices[i])));
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
			ToolInterface face = (ToolInterface) interfaces.get(indices[i]);
			tobeRemoved.add(face);
		}
		for (Iterator i = tobeRemoved.iterator(); i.hasNext();) {
			ToolInterface face = (ToolInterface) i.next();
			deleteInterface(face);
		}
	}

	private void closeGUIonDelete(ToolInterface face)
	{
		WindowTracker tracker = BuildMode.getWindowTracker(face);
		List children = tracker.getChildren();
		//first collect all the frames related to an interface and then
		//close all.  Done so to avoid ConcurrentmodificationException
		List framesToClose = new ArrayList();
		for (Iterator j = children.iterator(); j.hasNext();) {
			DomeBuildFrame buildFrame = (DomeBuildFrame) j.next();
			DomeGui gui = buildFrame.getGui();
			if (gui instanceof ToolInterfaceBuildPanel) {
				Object dobj = gui.getGuiObject();
				if (dobj.equals(face)) {
					framesToClose.add(buildFrame);
				}
			} else if (!(gui instanceof ToolInterfaceManagerBuildPanel)) {
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
		this.toolFileName = modelFileName;

		// construct interface path
		File modelFile = new File(modelFileName);
		String ifacePath = null;
		if(model instanceof ToolModel) {
			ifacePath = modelFile.getParentFile() + slash + "interfaces";
			String modelFileNameNoExtension = modelFile.getName();
			if (modelFileNameNoExtension.lastIndexOf('-') > 0)
				modelFileNameNoExtension = modelFileNameNoExtension.substring(0, modelFileNameNoExtension.lastIndexOf('-'));
			ifacePath += "-" + modelFileNameNoExtension + "-" + getModel().getId().toString();
		}
		return ifacePath;
	}

	protected String getExistingInterfacePath(String modelFileName, String idString)
	{
		this.toolFileName = modelFileName;

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
					if(model instanceof ToolModel && ifaceFiles[i].getName().endsWith(".dmi")) {
						readInterfaceFile(ifaceFiles[i]);
					}
				}
			}
		}
	}

	private void readInterfaceFile(File file)
	{
		int endindex = 0;
		if(model instanceof ToolModel) {
			endindex = file.getName().indexOf(".dti");
		}
		int startindex = file.getName().lastIndexOf(slash) + 1;
		String ifaceid = file.getName().substring(startindex, endindex);
		File mappingFile = (File) interfaceMappingFileMap.get(ifaceid);
		SAXReader reader = new SAXReader();
		Document ifaceDoc = null;
		Document ifaceMap = null;

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

	public ToolInterface newInterface()
	{
		ToolInterface mi = new ToolInterfaceBuilder(model, new Id(UUIDGenerator.create()));
        interfaces.add(mi);
		return mi;
	}

	public boolean addInterface(ToolInterface mi)
	{
		return false;
	}

	public void save(String modelFileName)
	{
		destroyExistingInterfaces(modelFileName);

		// create interfaces directory
		String ifacePath = getInterfacePath(modelFileName);
		File ifaceDir = new File(ifacePath);
		if (!ifaceDir.exists())
			ifaceDir.mkdir();

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
