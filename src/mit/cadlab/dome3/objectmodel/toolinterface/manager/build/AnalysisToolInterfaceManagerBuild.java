package mit.cadlab.dome3.objectmodel.toolinterface.manager.build;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.manager.AnalysisToolInterfaceManager;
import mit.cadlab.dome3.objectmodel.toolinterface.optimization.build.OptimizationInterfaceBuild;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisTool;
import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.DomeGui;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.build.ToolInterfaceManagerBuildPanel;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.swing.WindowTracker;
import mit.cadlab.dome3.objectmodel.model.tool.optimization.build.OptimizationToolBuild;
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
public class AnalysisToolInterfaceManagerBuild extends AnalysisToolInterfaceManager
{
    protected HashMap interfaceMappingFileMap; //key=iface id, value=iface mapping file instance
	protected AnalysisToolInterfaceBase toolInterface = null;

	public AnalysisToolInterfaceManagerBuild(AnalysisTool model)
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
        int button = TwoButton1Msg.showOption(null,
                "Warning: Delete Interface", "This operation cannot be undone.", "Continue",
                "Cancel", TwoButton1Msg.DEFAULT_SIZE);
        if (button == 0)
            return false;
        // delete interface file
		if (_toolFileName == null && model instanceof AnalysisToolBase)
        {
            _toolFileName = ((AnalysisToolBase) model).getFileName();
        }
		if (_toolFileName != null && !_toolFileName.equals(""))
        {
            AnalysisToolInterfaceBase iface = (AnalysisToolInterfaceBase) mi;
            String ifacePath = getInterfacePath(_toolFileName);
            String ifaceFileName = null;
            if (model instanceof AnalysisToolBase)
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
        if (mi instanceof AnalysisToolInterfaceBase)
        {
            ((AnalysisToolInterfaceBase) mi).removeMappingsAndConnectionsBeforeDelete();
        }
        closeGUIonDelete(mi);
        return _interfaces.remove(mi);
    }

	public Collection duplicateInterfaces(int[] indices)
	{
		if (indices == null || indices.length == 0) return Collections.EMPTY_LIST;
        Arrays.sort(indices);
        List duplicateInterfaces = new ArrayList();
        for (int i = 0; i < indices.length; ++i)
        {
            ToolInterface ti = new OptimizationInterfaceBuild(model, new Id(UUIDGenerator.create()),
                    (ToolInterface) _interfaces.get(indices[i]));
            _interfaces.add(ti);
        }
        return duplicateInterfaces;
	}

    public ToolInterface newInterface()
	{
        ToolInterface ti = null;
        if (model instanceof OptimizationToolBuild)
            ti = new OptimizationInterfaceBuild(model, new Id(UUIDGenerator.create()));
        _interfaces.add(ti);
		return ti;
	}

    public ToolInterface newInterface(int index)
	{
		ToolInterface mi = new OptimizationInterfaceBuild(model, new Id(UUIDGenerator.create()));
		_interfaces.add(index, mi);
		return mi;
	}

	public void deleteInterfaces(int[] indices)
	{
		//To avoid ConcurrentModificationException
		ArrayList tobeRemoved = new ArrayList();
		if (indices == null || indices.length == 0) return;
		Arrays.sort(indices);
		for (int i = 0; i < indices.length; i++) {
			ToolInterface face = (ToolInterface) _interfaces.get(indices[i]);
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
		_interfaces.shiftLeft(indices);
	}

	public void moveInterfacesDown(int[] indices)
	{
		_interfaces.shiftRight(indices);
	}

	protected String getInterfacePath(String toolFileName)
	{
		_toolFileName = toolFileName;

		// construct interface path
		File toolModelFile = new File(toolFileName);
        String toolContentPath = toolModelFile.getParentFile() + slash + toolModelFile.getName().substring(0, toolModelFile.getName().lastIndexOf("-")) + "-contents";
        File analysisToolContentFolder = new File(toolContentPath);
        if (analysisToolContentFolder.exists())
        {
            String interfacesFolderPath = analysisToolContentFolder.getAbsolutePath() + slash + "interfaces";
            File interfacesFolder = new File(interfacesFolderPath);
            if (interfacesFolder.exists())
                return interfacesFolder.getAbsolutePath();
            else
                if (interfacesFolder.mkdir())
                    return interfacesFolder.getAbsolutePath();

        }
        return "";
	}

	protected String getExistingInterfacePath(String modelFileName, String idString)
	{
		_toolFileName = modelFileName;

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
					if(model instanceof AnalysisTool && ifaceFiles[i].getName().endsWith(".dti")) {
						readInterfaceFile(ifaceFiles[i]);
					}
				}
			}
		}
	}

	private void readInterfaceFile(File file)
	{
		int endindex = file.getName().indexOf(".dti");
		int startindex = file.getName().lastIndexOf(slash) + 1;
		String ifaceid = file.getName().substring(startindex, endindex);
		File mappingFile = (File) interfaceMappingFileMap.get(ifaceid);
		SAXReader reader = new SAXReader();
		Document ifaceDoc = null;
		Document ifaceMap = null;
        try
        {
            ifaceDoc = reader.read(file);
            if (mappingFile != null)
            {
                ifaceMap = reader.read(mappingFile);
            }
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        AnalysisToolInterfaceBase ti = null;

		// create the interface
        if(model instanceof OptimizationToolBuild)
            ti = new OptimizationInterfaceBuild((OptimizationToolBuild)model, ifaceDoc.getRootElement(),
                                                        (ifaceMap == null) ? null : ifaceMap.getRootElement());
       	_interfaces.add(ti);
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

	public boolean addInterface(ToolInterface mi)
	{
		if (_interfaces.add(mi)) return true;
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

        // save the interfaces
		for (Iterator iter = _interfaces.iterator(); iter.hasNext();)
        {
            OptimizationInterfaceBuild iface = (OptimizationInterfaceBuild) iter.next();
            String ifaceFileName = ifacePath + slash + iface.getId().toString() + ".dti";
            try
            {
                iface.save(ifaceFileName);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
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
