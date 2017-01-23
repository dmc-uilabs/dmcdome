package mit.cadlab.dome3.gui.deploy.components;

import mit.cadlab.dome3.gui.guiutils.SimpleChooser;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolInterfaceData;
import mit.cadlab.dome3.gui.deploy.components.tool.DeployAnalysisToolData;
import mit.cadlab.dome3.gui.deploy.components.tool.AnalysisToolVersionData;
import mit.cadlab.dome3.icons.DomeIcons;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.swing.DListDListModel;
import mit.cadlab.dome3.swing.DListModel;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.AuxiliaryFileUtils;
import mit.cadlab.dome3.util.xml.DomeXmlData;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.swing.Icon;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;


/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 10, 2003
 * Time: 9:11:39 AM
 * To change this template use Options | File Templates.
 */
public class DeployUtilities
{
	public static final String slash = System.getProperty("file.separator");

    private static final String _EMPTY_STRING = "";

	public static DeployModelData loadModelForDeploy(String modelFilePath)
	{

		File _modelFile = null;
		String id = null;
		String xmlContent = null;
		String name = null;
		try {
			_modelFile = new File(modelFilePath);
			xmlContent = FileUtils.readTextFileAsString(_modelFile);

			DomeXmlData modelParse = new DomeXmlData(DomeXmlData.MODEL, xmlContent);
			id = modelParse.getId();
			name = modelParse.getName();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		DeployModelData modelList = new DeployModelData(modelFilePath, xmlContent, id, name);
		return modelList;
	}

	public static DeployPlayspaceData loadPlayspaceForDeploy(String modelFilePath)
	{
		String id = null;
		String xmlContent = null;
		String name = null;
		try {
			xmlContent = FileUtils.readTextFileAsString(modelFilePath);
		}
		catch (FileNotFoundException e) {
			System.err.println("file not found: " + modelFilePath);
		}
		DomeXmlData xmlData = new DomeXmlData(DomeXmlData.PLAYSPACE, xmlContent);
		id = xmlData.getId();
		name = xmlData.getName();

		return new DeployPlayspaceData(modelFilePath, xmlContent, id, name);
	}

	public static List loadInterfacesForDeploy(String modelFilePath)
	{
		List interfaceList = new ArrayList();

		File _modelFile = new File(modelFilePath);
		Document model = null;
		String id = null;
		try {

			SAXReader reader = new SAXReader();
			model = reader.read(_modelFile);
		}
		catch (DocumentException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
		catch (MalformedURLException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
		Element element = model.getRootElement();
		id = element.attributeValue("id");
		String modelParent = _modelFile.getParent();
		File tempFile = _modelFile.getParentFile();
		String mdlFileName = _modelFile.getName();
		mdlFileName = mdlFileName.substring(0, mdlFileName.lastIndexOf("-"));
		String[] temp;
		while (modelParent != null) {
			temp = tempFile.list();
			modelParent = tempFile.getParent();
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].equals("interfaces-" + mdlFileName + "-" + id)) {
					// found directory containing interfaces for deploy
					File interfaceFiles = new File(tempFile.getAbsolutePath() + slash + temp[i]);

					// go into that directory and retrieve the contents of that directory
					String[] interfaces = interfaceFiles.list();

					// loop over all the files in that directory
					for (int j = 0; j < interfaces.length; j++) {

						// if model interface file found ?
						if (interfaces[j].endsWith(".dmi"))
                        {
                            try
                            {
                                String idXml = null;
                                String nameXml = null;
                                String tempXml = FileUtils.readTextFileAsString(interfaceFiles.getAbsolutePath() + slash + interfaces[j]);
                                DomeXmlData interfaceParse = new DomeXmlData(DomeXmlData.INTERFACE, tempXml);
                                idXml = interfaceParse.getId();
                                nameXml = interfaceParse.getName();
                                interfaceList.add(new DeployInterfaceData(interfaceFiles.getAbsolutePath() + slash + interfaces[j],
                                        tempXml, FileUtils.readTextFileAsString(interfaceFiles.getAbsolutePath()
                                            + slash + interfaces[j].substring(0, interfaces[j].length() - 4) + "-mappings")
                                                , idXml, nameXml));
                            }
                            catch (FileNotFoundException e)
                            {
                                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                            }
                        }
					}
				}
			}
			tempFile = tempFile.getParentFile();
		}
		return interfaceList;
	}

    public static List loadAnalysisToolInterfacesForDeploy(String fileName)
    {
        List interfaceList = new ArrayList();

        File modelFile = new File(fileName);
        Document model = null;
        String id = null;
        try
        {

            SAXReader reader = new SAXReader();
            model = reader.read(modelFile);
        }
        catch (DocumentException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        Element element = model.getRootElement();
        id = element.attributeValue("id");
        String modelParent = modelFile.getParent();
        File tempFile = modelFile.getParentFile();
        String mdlFileName = modelFile.getName();
        mdlFileName = mdlFileName.substring(0, mdlFileName.lastIndexOf("-"));
        String[] temp;
        while (modelParent != null) {
			temp = tempFile.list();
			modelParent = tempFile.getParent();
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].equals(mdlFileName + "-contents")) {
					// found directory containing interfaces for deploy
					File interfaceFiles = new File(tempFile.getAbsolutePath() + slash + temp[i] + slash + "interfaces");

					// go into that directory and retrieve the contents of that directory
					String[] interfaces = interfaceFiles.list();

					// loop over all the files in that directory
					for (int j = 0; j < interfaces.length; j++) {

						if(interfaces[j].endsWith(".dti"))
                        {
                            try
                            {
                                String tempXml = FileUtils.readTextFileAsString(interfaceFiles.getAbsolutePath() + slash + interfaces[j]);
                                DomeXmlData interfaceParse = new DomeXmlData(DomeXmlData.INTERFACE, tempXml);
                                interfaceList.add(new DeployAnalysisToolInterfaceData(interfaceFiles.getAbsolutePath() + slash + interfaces[j],
                                        tempXml, FileUtils.readTextFileAsString(interfaceFiles.getAbsolutePath()
                                            + slash + interfaces[j].substring(0, interfaces[j].length() - 4) + "-mappings"),
                                                interfaceParse.getId(), interfaceParse.getName(), interfaceParse.getModelType()));
                            }
                            catch(FileNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                        }
					}
				}
			}
			tempFile = tempFile.getParentFile();
		}
		return interfaceList;
	}





	public static Vector loadAuxFilesForDeploy(String xmlContent) throws FileNotFoundException, IOException
	{

		Vector auxFiles = null;

		DomeXmlData modelParse = new DomeXmlData(DomeXmlData.MODEL, xmlContent);
		auxFiles = modelParse.getModelAuxFiles();

		if (auxFiles == null || auxFiles.size() == 0) return null; //no auxiliary file, so return null

		Vector auxfilelist = new Vector();
		for (Iterator i = auxFiles.iterator(); i.hasNext();) {
			AbstractAuxFile af = (AbstractAuxFile) i.next();
			String id = af.getId().toString();
			String name = af.getName();
			String filepath = af.getFile().getPath();
			Boolean executeOnServer = new Boolean(af.isExecuteOnServer());
			byte[] fileContent = null;
			fileContent = FileUtils.readBinaryFileAsByteArray(filepath);
			//vector of auxiliary file information: id, name, filepath, excuteOnServer,filecontent
			auxfilelist.add(Vectors.create(id, name, filepath, executeOnServer, fileContent));
		}

		return auxfilelist;
	}

   /**
    * @param interfaceInfoVectors
    * @param option: 1:model 2: project
    * @return   vector with interface id and customGui infos
    */
    public static Vector loadCustomGuiFilesForDeploy(String modelXml, Vector interfaceInfoVectors,int option){
      Vector CustomGuiFiles = new Vector();
      //first get customGui info out of interface
       for(Iterator i=interfaceInfoVectors.iterator();i.hasNext();){
          Vector anInterfaceInfo=(Vector)i.next();
           //struture of the single item in interaceInfoVector:      third one is the xmldesciption
          String xmlDes=(String)anInterfaceInfo.get(2);
          DomeXmlData interfaceData = new DomeXmlData(DomeXmlData.INTERFACE,xmlDes);
          String interfaceId = interfaceData.getId();

          if(interfaceData.getInterfaceCustomGuiFiles()!=null){
              for(Iterator ii=interfaceData.getInterfaceCustomGuiFiles().iterator();ii.hasNext();)
              {
                //for old model customGui file type
                 CustomGuiInfo cf=(CustomGuiInfo) ii.next();
                 String filepath= cf.getJarFilePath();
                if (filepath == null) {
                    filepath = AuxiliaryFileUtils.getDefaultLocation(cf.getJarFileId().toString(), modelXml,option);
                }
                 if(filepath!=null) {
                     cf.setJarFilePath(filepath);
                 }
                 CustomGuiFiles.add(Vectors.create(interfaceId,cf));
              }
          }
       }
      return CustomGuiFiles;
    }


	public static void synchronizeModelDataWithServer(ServerConnection connection, String parentId, DeployModelData model)
	{
		model.setModelDescription((String) FileSystemFunctions.getModelInfo(connection, parentId).elementAt(2));
		Vector v = FileSystemFunctions.getAvailableInterfaces(connection, parentId);
		ListIterator list = model.getModelInterfaces().listIterator();
		while (list.hasNext()) {
			DeployInterfaceData temp = (DeployInterfaceData) list.next();
			for (int i = 0; i < v.size(); i++) {
				if (temp.getId().equals((((Vector) v.get(i)).elementAt(0)))) {
					temp.setDescription((String) (((Vector) v.get(i)).elementAt(2)));
					if (((String) (((Vector) v.get(i)).elementAt(1))).equalsIgnoreCase("AVAILABLE")) {
						temp.setIsAvailable(new Boolean(true));
					}
				}
			}
		}
	}

	public static void synchronizePlayspaceDataWithServer(ServerConnection connection, String playspaceId, DeployPlayspaceData playspaceData)
	{
		playspaceData.setPlayspaceDescription((String) FileSystemFunctions.getPlayspaceInfo(connection, playspaceId).elementAt(2));
	}

    public static void synchronizeAnalysisToolDataWithServer (ServerConnection svrConn, String analysisToolId, DeployAnalysisToolData analysisToolData)
    {
        analysisToolData.setDescription((String) FileSystemFunctions.getAnalysisToolInfo(svrConn, analysisToolId).elementAt(2));
        Vector v = FileSystemFunctions.getAvailableInterfaces(svrConn, analysisToolId);
        ListIterator list = analysisToolData.getToolInterfaces().listIterator();
        while (list.hasNext())
        {
            DeployAnalysisToolInterfaceData temp = (DeployAnalysisToolInterfaceData) list.next();
            for (int i = 0; i < v.size(); i++)
            {
                if (temp.getId().equals((((Vector) v.get(i)).elementAt(0))))
                {
                    temp.setDescription((String) (((Vector) v.get(i)).elementAt(2)));
                    if (((String) (((Vector) v.get(i)).elementAt(1))).equalsIgnoreCase("AVAILABLE"))
                    {
                        temp.setIsAvailable(new Boolean(true));
                    }
                }
            }
        }
    }

	public static void synchronizeProjectDataWithServer(ServerConnection connection, String projectId, DeployProjectData projectData)
	{
		projectData.setDescription((String) FileSystemFunctions.getProjectInfo(connection, projectId).elementAt(2));
		Vector v = FileSystemFunctions.getAvailableInterfaces(connection, projectId);
		ListIterator list = projectData.getInterfaces().listIterator();
		while (list.hasNext()) {
			DeployProjectInterfaceData temp = (DeployProjectInterfaceData) list.next();
			for (int i = 0; i < v.size(); i++) {
				if (temp.getId().equals((((Vector) v.get(i)).elementAt(0)))) {
					temp.setDescription((String) (((Vector) v.get(i)).elementAt(2)));
					if (((String) (((Vector) v.get(i)).elementAt(1))).equalsIgnoreCase("AVAILABLE")) {
						temp.setIsAvailable(new Boolean(true));
					}
				}
			}
		}
		Vector v2 = FileSystemFunctions.getAvailableProjectContents(connection, projectId, DbConstants.FILESYSTEM_BROWSE);
		ListIterator modelList = projectData.getIntegrationModels().listIterator();
		Vector models = null;

        if (v2.isEmpty())
            models = new Vector();
        else
            models = ((Vector) v2.elementAt(1));

		while (modelList.hasNext()) {
			DeployModelData temp = (DeployModelData) modelList.next();
			for (int i = 0; i < models.size(); i++) {
				Vector svrMod = ((Vector) ((Vector) models.elementAt(i)).elementAt(0));
				System.out.println("Vector is: " + svrMod);
				String modelId = ((String) svrMod.elementAt(1));
				//System.out.println(modelId);
				if (temp.getDeployId().equals(modelId)) {
					temp.setModelDescription((String) svrMod.elementAt(2));
					Vector interfaces = FileSystemFunctions.getAvailableInterfaces(connection, modelId);
					ListIterator interfaceList = temp.getModelInterfaces().listIterator();
					while (interfaceList.hasNext()) {
						DeployInterfaceData intData = (DeployInterfaceData) interfaceList.next();
						for (int j = 0; j < interfaces.size(); j++) {
							if (intData.getId().equals((((Vector) interfaces.get(j)).elementAt(0)))) {
								intData.setDescription((String) (((Vector) interfaces.get(j)).elementAt(2)));
								if (((String) (((Vector) interfaces.get(j)).elementAt(1))).equalsIgnoreCase("AVAILABLE")) {
									intData.setIsAvailable(new Boolean(true));
								}
							}
						}
					}
				}
			}
		}
	}

	public static void writeModelVersionFile(String modelFileName, ModelVersionData model)
	{
		int flag = 0;
		File f = new File(modelFileName);
		if (f == null) {
			System.out.println("ERROR: Incorrect model file path: " + modelFileName);
			System.exit(0);
		}
		for (int i = 0; i < f.getParentFile().listFiles().length; i++) {
			if (f.getParentFile().listFiles()[i].getName().equalsIgnoreCase("domeVC")) {
				flag = 1;
				File[] toBeDeleted = (new File(f.getParentFile().listFiles()[i].getAbsolutePath())).listFiles();
				for (int j = 0; j < toBeDeleted.length; j++) {
					if (toBeDeleted[j].getName().equals(f.getName().substring(0, f.getName().length() - 4) + "_" + model.getModelId() + "_" + model.getServerPort().substring(0, model.getServerPort().lastIndexOf(":")) + ".dvc"))
						if (!toBeDeleted[j].delete()) System.out.println("Could not delete file");
				}
			}
		}
		if (flag == 0)
			if (!new File(f.getParentFile() + slash + "domeVC").mkdir()) System.out.println("Error creating domeVC directory");

		File verFile = null;
		try {
			verFile = new File(f.getParent() + slash + "domeVC", f.getName().substring(0, f.getName().length() - 4) + "_" + model.getModelId() + "_" + model.getServerPort().substring(0, model.getServerPort().lastIndexOf(":")) + ".dvc");
			if (!verFile.createNewFile())
				System.out.println("Could not create Dome Version file!");
		}
		catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
		FileUtils.writeStringToFile(model.ModelVersionDataToString(), verFile);
	}

    public static void writeAnalysisToolVersionFile (String analysisToolFileName, Vector versionInfo, ServerConnection svrConn)
    {
        int flag = 0;
        File f = new File(analysisToolFileName);
        AnalysisToolVersionData analysisTool = new AnalysisToolVersionData(versionInfo, svrConn);

        if (f == null)
        {
            OneButton1Msg.showError(null, "Deploy Mode: Error", "Incorrect analysis tool file path."
                                        + "\n" + analysisToolFileName, "OK", new Dimension(150, 100));
            System.exit(0);
        }

        for (int i = 0; i < f.getParentFile().listFiles().length; i++)
        {
            if (f.getParentFile().listFiles()[i].getName().equalsIgnoreCase("domeVC"))
            {
                flag = 1;
                File[] toBeDeleted = (new File(f.getParentFile().listFiles()[i].getAbsolutePath())).listFiles();
                for (int j = 0; j < toBeDeleted.length; j++)
                {
                    if (toBeDeleted[j].getName().equals(f.getName().substring(0, f.getName().length() - 4)
                            + "_" + analysisTool.getAnalysisToolId() + "_" + analysisTool.getServerPort().substring
                                    (0, analysisTool.getServerPort().lastIndexOf(":")) + ".dvc"))
                        if (toBeDeleted[j].delete())
                            break;
                        else
                            OneButton1Msg.showError(null, "deploy mode", "ERROR: could not delete version control file",
                                        "OK", new Dimension(150, 75));
                }
            }
        }

        if (flag == 0)
            if (!new File(f.getParentFile() + slash + "domeVC").mkdir())
                OneButton1Msg.showError(null, "deploy mode", "ERROR: could not create domeVC directory", "OK", new Dimension(150, 75));

        File versionFile = null;
        try
        {
            versionFile = new File(f.getParent() + slash + "domeVC", f.getName().substring(0, f.getName().length() - 4)
                + "_" + analysisTool.getAnalysisToolId() + "_" + analysisTool.getServerPort().substring(0,
                        analysisTool.getServerPort().lastIndexOf(":")) + ".dvc");
            if (!versionFile.createNewFile())
                OneButton1Msg.showError(null, "deploy mode", "ERROR: could not create dome version control file",
                            "OK", new Dimension(150, 75));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        FileUtils.writeStringToFile(analysisTool.toString(), versionFile);
    }

	public static void writeProjectVersionFile(String projectFileName, Vector versionInfo, ServerConnection svrConn)
	{

		int flag = 0;
		File f = new File(projectFileName);
		ProjectVersionData project = new ProjectVersionData(versionInfo, svrConn);

		if (f == null) {
			System.out.println("ERROR: Incorrect project file path: " + projectFileName);
			System.exit(0);
		}

		for (int i = 0; i < f.getParentFile().listFiles().length; i++) {
			if (f.getParentFile().listFiles()[i].getName().equalsIgnoreCase("domeVC")) {
				flag = 1;
				File[] toBeDeleted = (new File(f.getParentFile().listFiles()[i].getAbsolutePath())).listFiles();
				for (int j = 0; j < toBeDeleted.length; j++) {
					if (toBeDeleted[j].getName().equals(f.getName().substring(0, f.getName().length() - 4) + "_" + project.getProjectId() + "_" + project.getServerPort().substring(0, project.getServerPort().lastIndexOf(":")) + ".dvc"))
						if (toBeDeleted[j].delete())
							break;
						else
							System.out.println("Could not delete file");
				}
			}
		}
		if (flag == 0)
			if (!new File(f.getParentFile() + slash + "domeVC").mkdir()) System.out.println("Error creating domeVC directory");

		File verFile = null;
		try {
			verFile = new File(f.getParent() + slash + "domeVC", f.getName().substring(0, f.getName().length() - 4) + "_" + project.getProjectId() + "_" + project.getServerPort().substring(0, project.getServerPort().lastIndexOf(":")) + ".dvc");
			if (!verFile.createNewFile())
				System.out.println("Could not create Dome Version file!");
		}
		catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
		FileUtils.writeStringToFile(project.toString(), verFile);
	}


	public static void writePlayspaceVersionFile(String playspaceFileName, PlayspaceVersionData playspace)
	{
		int flag = 0;
		File ps = new File(playspaceFileName);
		if (ps == null) {
			System.out.println("ERROR: Incorrect playspace file path: " + playspaceFileName);
			System.exit(0);
		}
		//System.out.println(ps.getAbsolutePath());
		for (int i = 0; i < ps.getParentFile().listFiles().length; i++) {

			if (ps.getParentFile().listFiles()[i].getName().equalsIgnoreCase("domeVC")) {
				flag = 1;
				File[] toBeDeleted = (new File(ps.getParentFile().listFiles()[i].getAbsolutePath())).listFiles();
				for (int j = 0; j < toBeDeleted.length; j++) {
					if (toBeDeleted[j].getName().equals(ps.getName().substring(0, ps.getName().length() - 4) + "_" + playspace.getDeployId() + "_" + playspace.getServerPort().substring(0, playspace.getServerPort().lastIndexOf(":")) + ".dvc"))
						if (!toBeDeleted[j].delete()) System.out.println("Could not delete file: " + toBeDeleted[j].getAbsolutePath());
				}
			}
		}
		if (flag == 0)
			if (!new File(ps.getParentFile() + slash + "domeVC").mkdir())
				System.out.println("ERROR: could not create domeVC directory in " + ps.getParent());

		File verFile = null;
		try {
			verFile = new File(ps.getParent() + slash + "domeVC", ps.getName().substring(0, ps.getName().length() - 4) + "_" + playspace.getDeployId() + "_" + playspace.getServerPort().substring(0, playspace.getServerPort().lastIndexOf(":")) + ".dvc");
			if (!verFile.createNewFile())
				System.out.println("ERROR: Could not create DOME playspace version file " + verFile.getAbsolutePath());
		}
		catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}
		FileUtils.writeStringToFile(playspace.writePlayspaceVersionDataToString(), verFile);
	}

	public static ModelVersionData readModelVersionDataFile(String modelVersionFile)
	{
		ModelVersionData modelVersion = new ModelVersionData(modelVersionFile);
		return modelVersion;
	}

	public static List findModelVersionFile(String modelFileName, ServerConnection svrConnection)
	{
		List versionFiles = new ArrayList();
		File f = new File(modelFileName);
		if (f == null) {
			System.out.println("Incorrect file path: " + modelFileName);
			System.exit(0);
		}
		File domeVC = new File(f.getParent(), "domeVC");
		if (!domeVC.exists()) {
			if (!domeVC.mkdir()) {
				System.out.println("Error - could not create domeVC directory");
				System.exit(0);
			}
		}
		if (domeVC != null) {
			for (int i = 0; i < domeVC.list().length; i++) {
				String temp = domeVC.list()[i];
				if (temp.startsWith(f.getName().substring(0, f.getName().length() - 4) + "_") && temp.endsWith(svrConnection.getServerPort().substring(0, svrConnection.getServerPort().lastIndexOf(":")) + ".dvc")) {
					versionFiles.add(domeVC.getAbsolutePath() + slash + temp);
				}
			}
			return versionFiles;
		}
		else
			return Collections.EMPTY_LIST;
	}

    public static List findAnalysisToolVersionFile(String analysisToolFileName, ServerConnection svrConnection)
    {
        List versionFiles = new ArrayList();
        File f = new File(analysisToolFileName);
        if (!f.exists())
        {
            OneButton1Msg.showError(null, "deploy mode", "incorrect file path: " + analysisToolFileName,
                    "ok", OneButton1Msg.DEFAULT_SIZE);
            System.exit(0);
        }
        File domeVC = new File(f.getParent(), "domeVC");
        if (!domeVC.exists())
            return Collections.EMPTY_LIST;
        if (domeVC != null)
        {
            for (int i = 0; i < domeVC.list().length; i++)
            {
                String temp = domeVC.list()[i];
                if (temp.toLowerCase().startsWith(f.getName().substring(0, f.getName().length() - 4).
                        toLowerCase()) && temp.toLowerCase().endsWith(svrConnection.getServerPort().substring
                                (0, svrConnection.getServerPort().lastIndexOf(":")).toLowerCase() + ".dvc"))

                    versionFiles.add(domeVC.getAbsolutePath() + slash + temp);
            }
            return versionFiles;
        }
        else
            return Collections.EMPTY_LIST;
    }

	public static List findProjectVersionFile(String projectFileName, ServerConnection svrConnection)
	{
		List versionFiles = new ArrayList();
		File f = new File(projectFileName);
		if (!f.exists()) {
			System.out.println("Incorrect file path: " + projectFileName);
			System.exit(0);
		}
		File domeVC = new File(f.getParent(), "domeVC");
		if (!domeVC.exists()) { // no version information
			return Collections.EMPTY_LIST;
		}
		if (domeVC != null) {
			for (int i = 0; i < domeVC.list().length; i++) {
				String temp = domeVC.list()[i];
				if (temp.toLowerCase().startsWith(f.getName().substring(0, f.getName().length() - 4).toLowerCase() + "_") && temp.toLowerCase().endsWith(svrConnection.getServerPort().substring(0, svrConnection.getServerPort().lastIndexOf(":")).toLowerCase()+ ".dvc")) {
					versionFiles.add(domeVC.getAbsolutePath() + slash + temp);
				}
			}
			return versionFiles;
		}
		else
			return Collections.EMPTY_LIST;
	}

	public static List findPlayspaceVersionFile(String playspaceFileName, ServerConnection svrConnection)
	{
		List versionFiles = new ArrayList();
		File ps = new File(playspaceFileName);
		if (ps == null) {
			System.out.println("ERROR: Incorrect file path " + playspaceFileName);
			System.exit(0);
		}
		File domeVC = new File(ps.getParent(), "domeVC");
		if (!domeVC.exists()) {
			if (!domeVC.mkdir()) {
				System.out.println("ERROR: Could not create domeVC directory");
				System.exit(0);
			}
		}
		if (domeVC != null) {
			for (int i = 0; i < domeVC.list().length; i++) {
				String temp = domeVC.list()[i];
				if (temp.startsWith(ps.getName().substring(0, ps.getName().length() - 4) + "_") && temp.endsWith(svrConnection.getServerPort().substring(0, svrConnection.getServerPort().lastIndexOf(":")) + ".dvc")) {
					versionFiles.add(domeVC.getAbsolutePath() + slash + temp);
				}
			}
			return versionFiles;
		}
		else
			return Collections.EMPTY_LIST;
	}

	public static boolean isOkToDeployModel(DeployModelData modelData, ModelVersionData modelVerData1, ServerConnection conn)
	{
		ModelVersionData modelVerData2 = new ModelVersionData(DeployFilesFunctions.getModelVersionData(conn, modelVerData1.getModelId()), conn);
		List modelInterfaces = modelData.getModelInterfaces();
		ListIterator iterator = modelInterfaces.listIterator();
		while (iterator.hasNext()) {
			DeployInterfaceData interfaceTemp = ((DeployInterfaceData) iterator.next());
			InterfaceVersionData interfaceVD = modelVerData1.getInterfaceVersionDataByBuildId(interfaceTemp.getId());
			if (interfaceVD != null) {
				InterfaceVersionData serverVersion = modelVerData2.getInterfaceVersionData(interfaceVD.getInterfaceId());
				if (serverVersion != null) {
					if (serverVersion.getVersion() > interfaceVD.getVersion()) {
						int option = TwoButton2Msg.showOption(null, "Versioning Deploy", "Server version " + serverVersion.getVersion() + " is more recent than local version " + interfaceVD.getVersion() + "\nDo you want to continue deploy and overwrite server version? ", interfaceTemp.getName(), "Yes", "No", TwoButton2Msg.DEFAULT_SIZE);
						if (option == TwoButton2Msg.RIGHT_OPTION) return false;
					}
				}
			}
		}
		return true;
	}


	public static boolean isOkToDeployProject(DeployProjectData projectData, ProjectVersionData projectVerData1, ServerConnection conn)
	{
		ProjectVersionData modelVerData2 = new ProjectVersionData(DeployFilesFunctions.getProjectVersionData(conn, projectVerData1.getProjectId()), conn);
		List projectInterfaces = projectData.getInterfaces();
		ListIterator iterator = projectInterfaces.listIterator();
		while (iterator.hasNext()) {
			DeployProjectInterfaceData interfaceTemp = ((DeployProjectInterfaceData) iterator.next());
			InterfaceVersionData interfaceVD = projectVerData1.getInterfaceVersionDataByBuildId(interfaceTemp.getId());
			if (interfaceVD != null) {
				InterfaceVersionData serverVersion = modelVerData2.getInterfaceVersionData(interfaceVD.getInterfaceId());
				if (serverVersion != null) {
					if (serverVersion.getVersion() > interfaceVD.getVersion()) {
						int option = TwoButton2Msg.showOption(null, "Versioning Deploy", "Server version " + serverVersion.getVersion() + " is more recent than local version " + interfaceVD.getVersion() + "\nDo you want to continue deploy and overwrite server version? ", interfaceTemp.getName(), "Yes", "No", TwoButton2Msg.DEFAULT_SIZE);
						if (option == TwoButton2Msg.RIGHT_OPTION) return false;
					}
				}
			}
		}
		return true;
	}

    public static boolean isOkToDeployAnalysisTool(DeployAnalysisToolData analysisToolData, AnalysisToolVersionData analysisToolVerData1, ServerConnection conn)
	{
		AnalysisToolVersionData modelVerData2 = new AnalysisToolVersionData(DeployFilesFunctions.getAnalysisToolVersionData(conn, analysisToolVerData1.getAnalysisToolId()), conn);
		List analysisToolInterfaces = analysisToolData.getToolInterfaces();
		ListIterator iterator = analysisToolInterfaces.listIterator();
		while (iterator.hasNext())
        {
            DeployAnalysisToolInterfaceData interfaceTemp = ((DeployAnalysisToolInterfaceData) iterator.next());
            InterfaceVersionData interfaceVD = analysisToolVerData1.getInterfaceVersionDataByBuildId(interfaceTemp.getId());
            if (interfaceVD != null)
            {
                InterfaceVersionData serverVersion = modelVerData2.getInterfaceVersionData(interfaceVD.getInterfaceId());
                if (serverVersion != null)
                {
                    if (serverVersion.getVersion() > interfaceVD.getVersion())
                    {
                        int option = TwoButton2Msg.showOption(null, "Versioning Deploy", "Server version " +
                                serverVersion.getVersion() + " is more recent than local version " + interfaceVD.getVersion()
                                        + "\nDo you want to continue deploy and overwrite server version? ", interfaceTemp.getName(),
                                                    "Yes", "No", TwoButton2Msg.DEFAULT_SIZE);
                        if (option == TwoButton2Msg.RIGHT_OPTION) return false;
                    }
                }
            }
        }
		return true;
	}


	public static boolean isOkToDeployPlayspace(DeployPlayspaceData playspaceData, PlayspaceVersionData playspaceVersionData1, ServerConnection conn)
	{
		Vector v = DeployFilesFunctions.getPlayspaceVersionData(conn, playspaceVersionData1.getDeployId());
		PlayspaceVersionData playspaceVersionData2 = new PlayspaceVersionData(v, conn);
		if (playspaceVersionData2.getVersion() > playspaceVersionData1.getVersion()) {
			int option = TwoButton2Msg.showOption(null, "Versioning Deploy", "Server version " + playspaceVersionData2.getVersion() + " is more recent than local version " + playspaceVersionData1.getVersion() + "\nDo you want to continue deploy and overwrite server version? ", playspaceData.getName(), "Yes", "No", TwoButton2Msg.DEFAULT_SIZE);
			if (option == TwoButton2Msg.RIGHT_OPTION) return false;
		}
		return true;
	}

	public static Vector createNewModelInfoVector(Integer folderId, String description, String xmlDefinition, Vector permissions)
	{
		if (folderId == null)
			throw new IllegalArgumentException("createNewModelInfoVector - null folderId");
		if (xmlDefinition == null)
			throw new IllegalArgumentException("createNewModelInfoVector - null xmlDefinition");
		return Vectors.create(folderId,
		        description == null ? "" : description,
		        xmlDefinition,
		        permissions == null ? DbConstants.EMPTY_VECTOR : permissions);
	}

    public static Vector createNewToolInfoVector(Integer folderId, String description, String xmlDefinition, Vector permissions)
	{
		if (folderId == null)
			throw new IllegalArgumentException("createNewAnalysisToolInfoVector - null folderId");
		if (xmlDefinition == null)
			throw new IllegalArgumentException("createNewAnalysisToolInfoVector - null xmlDefinition");
		return Vectors.create(folderId,
		        description == null ? "" : description,
		        xmlDefinition,
		        permissions == null ? DbConstants.EMPTY_VECTOR : permissions);
	}

	public static Vector createDeployInterfaceInfoVector(List deployInterfaceData)
	{
		Vector interfaceInfoVector = new Vector();
		Iterator it = deployInterfaceData.iterator();
		while (it.hasNext()) {
			DeployInterfaceData anInterface = (DeployInterfaceData) it.next();
			interfaceInfoVector.add(createNewInterfaceInfoVector(
			        anInterface.getIsAvailable().booleanValue(), anInterface.getDescription(),
			        anInterface.getXmlContent(), anInterface.getXmlMappingsContent(), anInterface.getPermissions()));
		}
		return interfaceInfoVector;
	}

    public static Vector createDeployToolInterfaceInfoVector(List deployToolInterfaceData)
    {
        Vector interfaceInfoVector = new Vector();
        Iterator it = deployToolInterfaceData.iterator();
        while(it.hasNext())
        {
            DeployAnalysisToolInterfaceData toolInterface = (DeployAnalysisToolInterfaceData) it.next();
            interfaceInfoVector.add(createNewInterfaceInfoVector(
                    toolInterface.getIsAvailable().booleanValue(), toolInterface.getDescription(),
                    toolInterface.getXmlContent(), toolInterface.getXmlMappingsContent(), toolInterface.getPermissions()));
        }
        return interfaceInfoVector;
    }

	public static Vector createDeployProjectInterfaceInfoVector(List deployProjectInterfaceData)
	{
		Vector interfaceInfoVector = new Vector();
		Iterator it = deployProjectInterfaceData.iterator();
		while (it.hasNext()) {
			DeployProjectInterfaceData anInterface = (DeployProjectInterfaceData) it.next();
			interfaceInfoVector.add(createNewInterfaceInfoVector(
			        anInterface.getIsAvailable().booleanValue(), anInterface.getDescription(),
			        anInterface.getXmlContent(), anInterface.getXmlMappingsContent(), anInterface.getPermissions()));
		}
		return interfaceInfoVector;
	}

	public static Vector createNewInterfaceInfoVector(boolean isAvailable, String description, String xmlDefinition, String xmlMappings, Vector permissions)
	{
		return createNewInterfaceInfoVector(isAvailable ? DbConstants.INTERFACE_STATUS_AVAILABLE : DbConstants.INTERFACE_STATUS_UNAVAILABLE,
		        description, xmlDefinition, xmlMappings, permissions);
	}

	public static Vector createUpdateModelInfoVector(String modelId, String description, String xmlDefinition, Vector permissions)
	{
		if (modelId == null)
			throw new IllegalArgumentException("createUpdateModelInfoVector - null modelId");
		if (xmlDefinition == null)
			throw new IllegalArgumentException("createUpdateModelInfoVector - null xmlDefinition");
		return Vectors.create(modelId,
		        description == null ? "" : description,
		        xmlDefinition,
		        permissions == null ? DbConstants.EMPTY_VECTOR : permissions);
	}

    public static Vector createUpdateAnalysisToolInfoVector(String analysisToolId, String description, String xmlDefinition, Vector permissions)
    {
        if (analysisToolId == null)
            throw new IllegalArgumentException("createUpdateModelInfoVector - null modelId");
        if (xmlDefinition == null)
            throw new IllegalArgumentException("createUpdateModelInfoVector - null xmlDefinition");
        return Vectors.create(analysisToolId,
                description == null ? "" : description,
                xmlDefinition,
                permissions == null ? DbConstants.EMPTY_VECTOR : permissions);
    }

	public static Vector createNewInterfaceInfoVector(String status, String description, String xmlDefinition, String xmlMappings, Vector permissions)
	{
		if (status == null)
			throw new IllegalArgumentException("createNewInterfaceInfoVector - null status");
		if (!(status.equals(DbConstants.INTERFACE_STATUS_AVAILABLE) || status.equals(DbConstants.INTERFACE_STATUS_UNAVAILABLE)))
			throw new IllegalArgumentException("createNewInterfaceInfoVector - invalid status type: " + status);
		if (xmlDefinition == null)
			throw new IllegalArgumentException("createNewModelInfoVector - null xmlDefinition");
		if (xmlMappings == null)
			throw new IllegalArgumentException("createNewModelInfoVector - null xmlMappings");
		return Vectors.create(status,
		        description == null ? "" : description,
		        xmlDefinition,
		        xmlMappings,
		        permissions == null ? DbConstants.EMPTY_VECTOR : permissions);
	}

	public static String getModelIdFromModelVersionData(ModelVersionData modelVersionData)
	{
		return modelVersionData.getModelId();
	}


	public static String getValidRedeployModelId(Component par, ServerConnection svrConn, DeployModelData model, String localFileName)
	{
		//check the local domeVC information
		List modelVersionFiles = findModelVersionFile(localFileName, svrConn);
		//if the model has not been deployed on the given server return empty string
		if (modelVersionFiles.isEmpty()) {
			OneButton1Msg.showWarning(par, "Redeploy warning", "This model is not deployed on " + svrConn.getServerPort() +
			        "\nTherefore it cannnot be redeployed", "ok", OneButton1Msg.DEFAULT_SIZE);
			return "";
		}
		//processs the modelVersionFiles information so can check if there are multiple redeploy options
		Vector modelIds = new Vector();
		HashMap modelVersionFile = new HashMap();
		for (int j = modelVersionFiles.size() - 1; j >= 0; j--) {
			ModelVersionData modelVer = new ModelVersionData((String) modelVersionFiles.get(j));
			if (modelVer.getUser().equals(svrConn.getLoginName())) {
				modelIds.addElement(modelVer.getModelId());
				modelVersionFile.put(modelVer.getModelId(), modelVer);
			}
			else {
				modelVersionFiles.remove(j);
			}
		}
		Hashtable modelPaths = DeployFilesFunctions.getPathsForModels(svrConn, modelIds);
		DArrayList modelServerFilePaths = new DArrayList(modelPaths.entrySet());

		// synchronize version files in local space with deployed models on server
		String serverHost = svrConn.getServerPort().substring(0, svrConn.getServerPort().lastIndexOf(":"));
		List keys = new ArrayList(modelPaths.keySet());
		String modelName = new File(localFileName).getName();
		List badFiles = new ArrayList();
		ListIterator iterator = modelVersionFiles.listIterator();
		while (iterator.hasNext()) {
			boolean flag = false;
			String temp = (String) iterator.next();
			for (int j = keys.size() - 1; j >= 0; j--) {
				String key = (String) keys.get(j);
				if (temp.endsWith(modelName.substring(0, modelName.length() - 4) + "_" + key + "_" + serverHost + ".dvc")) {
					flag = true;
				}
			}
			if (!flag)
				badFiles.add(temp);
		}
		if (badFiles != null) {
			ListIterator cleanUpIterator = badFiles.listIterator();
			while (cleanUpIterator.hasNext()) {
				String toBeErased = ((String) cleanUpIterator.next());
				File erase = new File(toBeErased);
				if (erase.exists()) {
					if (!erase.delete())
						System.out.println("Could not delete file: " + toBeErased);
					else
						System.out.println("Old version file exists and was deleted!");
				}
			}
		}
		//if there is more than one deployment option promp for which deployment location and set model ID info
		Object[] choice = null;
		String modelId = "";
		ModelVersionData modelVersionData = null;
		if (modelVersionFiles.size() != 1) {
			OneButton1Msg.showOption(par, "Redeploy options", "The model is deployed on " + svrConn.getServerPort() +
			        "\nin more than one location. You will be prompted to pick the location.", "ok", OneButton1Msg.DEFAULT_SIZE);
			choice = SimpleChooser.showChooser(par, "Redeploy selection", "ok", "cancel",
			        new VersionListModel(modelServerFilePaths), ListSelectionModel.SINGLE_SELECTION);
			if (choice == null || choice.length == 0) {
				return ""; // if they cancelled or did not pick anything return empty string id
			}
			modelId = (String) ((Map.Entry) choice[0]).getKey();
			modelVersionData = (ModelVersionData) modelVersionFile.get(modelId);
		}
		else { //case where the model is only deployed once on the server
			modelId = (String) modelIds.elementAt(0);
			modelVersionData = (ModelVersionData) modelVersionFile.get(modelId);
		}
		//last need to check that there are not version conflicts
		if (!isOkToDeployModel(model, modelVersionData, svrConn)) {
			return "";
		}
		//finally, check that there are interfaces in the new version of the model. If not, reject redeployment
		if ((model.getModelInterfaces()).size() == 0) {
			OneButton1Msg.show(par, "warning", "ReDeploy warning",
			        "This model has no interfaces so it cannot be redeployed", "ok", new Dimension(250, 80));
			return "";
		}
		// set interface deploy ids in DeployInterfaceData data structure
		setInterfaceDeployIds(model, modelVersionData);
		return modelId;
	}

    public static String getValidRedeployAnalysisToolId(Component par, ServerConnection svrConn, DeployAnalysisToolData analysisTool, String localFileName)
    {
        // check the local domeVC information
        List analysisToolVersionFiles = findAnalysisToolVersionFile(localFileName, svrConn);

        // if the analysis tool has not been deployed on the given server return empty string
        if (analysisToolVersionFiles.isEmpty())
        {
            OneButton1Msg.showWarning(par, "deploy mode", "this analysis tool is not deployed on " + svrConn.getServerPort() +
                    "\ntherefore it cannot be redeployed", "ok", OneButton1Msg.DEFAULT_SIZE);
            return "";
        }

        Vector analysisToolIds = new Vector();
        HashMap analysisToolVersionFileMap = new HashMap();
        for (int j = analysisToolVersionFiles.size() - 1; j >= 0; j--)
        {
            AnalysisToolVersionData analysisToolVersion = new AnalysisToolVersionData((String) analysisToolVersionFiles.get(j));
            if (analysisToolVersion.getUser().equals(svrConn.getLoginName()))
            {
                analysisToolIds.addElement(analysisToolVersion.getAnalysisToolId());
                analysisToolVersionFileMap.put(analysisToolVersion.getAnalysisToolId(), analysisToolVersion);
            }
            else
                analysisToolVersionFiles.remove(j);
        }

        Hashtable analysisToolPaths = DeployFilesFunctions.getPathsForAnalysisTools(svrConn, analysisToolIds);
        DArrayList analysisToolServerFilePaths = new DArrayList(analysisToolPaths.entrySet());

        String serverHost = svrConn.getServerPort().substring(0, svrConn.getServerPort().lastIndexOf(":"));
        List keys = new ArrayList(analysisToolPaths.keySet());
        String analysisToolName = new File(localFileName).getName();
        List badFiles = new ArrayList();
        ListIterator iterator = analysisToolVersionFiles.listIterator();
        while (iterator.hasNext())
        {
            boolean flag = false;
            String temp = (String) iterator.next();
            for (int j = keys.size() - 1; j >= 0; j--)
            {
                String key = (String) keys.get(j);
                if (temp.endsWith(analysisToolName.substring(0, analysisToolName.length() - 4)
                            + "_" + key + "_" + serverHost + ".dvc"))
                {
                    flag = true;
                }
            }
            if (!flag)
                badFiles.add(temp);
        }

        if (badFiles != null)
        {
            ListIterator cleanUpIterator = badFiles.listIterator();
            while (cleanUpIterator.hasNext())
            {
                String toBeErased = ((String) cleanUpIterator.next());
                File erase = new File(toBeErased);
                if (erase.exists())
                {
                    if (!erase.delete())
                        OneButton1Msg.showWarning(null, "deploy mode", "could not delete" +
                                "old version file", "OK", OneButton1Msg.DEFAULT_SIZE);
                }
            }
        }

        //if there is more than one deployment option promp for which deployment location and set model ID info
		Object[] choice = null;
		String analysisToolId = "";
		AnalysisToolVersionData analysisToolVersionData = null;
		if (analysisToolServerFilePaths.size() != 1)
        {
            OneButton1Msg.showOption(par, "Redeploy options", "The analysis tool is deployed on " + svrConn.getServerPort() +
                    "\nin more than one location. You will be prompted to pick the location.", "ok", OneButton1Msg.DEFAULT_SIZE);
            choice = SimpleChooser.showChooser(par, "Redeploy selection", "ok", "cancel",
                    new VersionListModel(analysisToolServerFilePaths), ListSelectionModel.SINGLE_SELECTION);
            if (choice == null || choice.length == 0)
            {
                return _EMPTY_STRING; // if they cancelled or did not pick anything return empty string id
            }
            analysisToolId = (String) ((Map.Entry) choice[0]).getKey();
            analysisToolVersionData = (AnalysisToolVersionData) analysisToolVersionFileMap.get(analysisToolId);
        }
        else
        { //case where the model is only deployed once on the server
            analysisToolId = (String) analysisToolIds.elementAt(0);
            analysisToolVersionData = (AnalysisToolVersionData) analysisToolVersionFileMap.get(analysisToolId);
        }
        //last need to check that there are not version conflicts
        if (!isOkToDeployAnalysisTool(analysisTool, analysisToolVersionData, svrConn))
        {
            return _EMPTY_STRING;
        }

		// set interface deploy ids in DeployInterfaceData data structure
		setInterfaceDeployIds(analysisTool, analysisToolVersionData);
		return analysisToolId;
    }

	public static String getValidRedeployProjectId(Component par, ServerConnection svrConn, DeployProjectData project, String localFileName, String redeployAnalysisToolId)
	{
		//check the local domeVC information
		List projectVersionFiles = findProjectVersionFile(localFileName, svrConn);
		//if the model has not been deployed on the given server return empty string
		if (projectVersionFiles.isEmpty()) {
			OneButton1Msg.showWarning(par, "Redeploy warning", "This project is not deployed on " + svrConn.getServerPort() +
			        "\nTherefore it cannnot be redeployed", "ok", OneButton1Msg.DEFAULT_SIZE);
			return "";
		}
		//processs the modelVersionFiles information so can check if there are multiple redeploy options
		Vector projectIds = new Vector();
		HashMap projectVersionFileMap = new HashMap();
		for (int j = projectVersionFiles.size() - 1; j >= 0; j--) {
			ProjectVersionData projectVer = new ProjectVersionData((String) projectVersionFiles.get(j));
			if (projectVer.getUser().equals(svrConn.getLoginName())) {
				projectIds.addElement(projectVer.getProjectId());
				projectVersionFileMap.put(projectVer.getProjectId(), projectVer);
			}
			else {
				projectVersionFiles.remove(j);
			}
		}
		Hashtable modelPaths = DeployFilesFunctions.getPathsForProjects(svrConn, projectIds);
		DArrayList modelServerFilePaths = new DArrayList(modelPaths.entrySet());

		// synchronize version files in local space with deployed models on server
		String serverHost = svrConn.getServerPort().substring(0, svrConn.getServerPort().lastIndexOf(":"));
		List keys = new ArrayList(modelPaths.keySet());
		String projectName = new File(localFileName).getName();
		List badFiles = new ArrayList();
		ListIterator iterator = projectVersionFiles.listIterator();
		while (iterator.hasNext()) {
			boolean flag = false;
			String temp = (String) iterator.next();
			for (int j = keys.size() - 1; j >= 0; j--) {
				String key = (String) keys.get(j);
				if (temp.endsWith(projectName.substring(0, projectName.length() - 4) + "_" + key + "_" + serverHost + ".dvc")) {
					flag = true;
				}
			}
			if (!flag)
				badFiles.add(temp);
		}
		if (badFiles != null) {
			ListIterator cleanUpIterator = badFiles.listIterator();
			while (cleanUpIterator.hasNext()) {
				String toBeErased = ((String) cleanUpIterator.next());
				File erase = new File(toBeErased);
				if (erase.exists()) {
					if (!erase.delete())
						System.out.println("Could not delete file: " + toBeErased);
					else
						System.out.println("Old version file exists and was deleted!");
				}
			}
		}
		//if there is more than one deployment option promp for which deployment location and set model ID info
		Object[] choice = null;
		String projectId = "";
		ProjectVersionData projectVersionData = null;
		if (modelServerFilePaths.size() != 1 && redeployAnalysisToolId == null) {
			OneButton1Msg.showOption(par, "Redeploy options", "The project is deployed on " + svrConn.getServerPort() +
			        "\nin more than one location. You will be prompted to pick the location.", "ok", OneButton1Msg.DEFAULT_SIZE);
			choice = SimpleChooser.showChooser(par, "Redeploy selection", "ok", "cancel",
			        new VersionListModel(modelServerFilePaths), ListSelectionModel.SINGLE_SELECTION);
			if (choice == null || choice.length == 0) {
				return ""; // if they cancelled or did not pick anything return empty string id
			}
			projectId = (String) ((Map.Entry) choice[0]).getKey();
			projectVersionData = (ProjectVersionData) projectVersionFileMap.get(projectId);
		}
        else if (redeployAnalysisToolId != null)
        {
            Vector v = DeployFilesFunctions.getProjectIdInsideAnalysisTool(svrConn, redeployAnalysisToolId);
            projectId = (String)((Vector)v.elementAt(0)).elementAt(0);
            projectVersionData = (ProjectVersionData) projectVersionFileMap.get(projectId);
        }
		else { //case where the model is only deployed once on the server
			projectId = (String) projectIds.elementAt(0);
			projectVersionData = (ProjectVersionData) projectVersionFileMap.get(projectId);
		}
		//last need to check that there are not version conflicts
		if (!isOkToDeployProject(project, projectVersionData, svrConn)) {
			return "";
		}

		// set interface deploy ids in DeployInterfaceData data structure
		setInterfaceDeployIds(project, projectVersionData);
		List imodels = project.getIntegrationModels();
		for (int i = 0; i < imodels.size(); i++) {
			DeployModelData deployModelData = (DeployModelData) imodels.get(i);
			IModelVersionData imvd = projectVersionData.getIModelVersionDataByBuildId(deployModelData.getId());
			if (imvd != null) {
				deployModelData.setDeployId(imvd.getModelId());//todo: get from the version information
				setInterfaceDeployIds(deployModelData, imvd);
			}
		}
		return projectId;
	}

	public static String getValidRedeployPlayspaceId(Component par, ServerConnection svrConn, DeployPlayspaceData playspace, String localFileName)
	{
		//todo need to check that the user that checked the file out is the one trying to redeploy
		//check the local domeVC information
		List playspaceVersionFiles = DeployUtilities.findPlayspaceVersionFile(localFileName, svrConn);
		//if the playspace has not been deployed on the fiver server return empty string
		if (playspaceVersionFiles.isEmpty()) {
			OneButton1Msg.showWarning(par, "Redeploy warning", "This playspace is not deployed on " + svrConn.getServerPort() +
			        "\nTherefore it cannnot be redeployed", "ok", OneButton1Msg.DEFAULT_SIZE);
			return "";
		}
		//process the version fle information so can check if there are multiple redeploy options
		Vector playspaceDeployIds = new Vector();
		HashMap playspaceVersionFile = new HashMap();
		for (int j = playspaceVersionFiles.size() - 1; j >= 0; j--) {
			PlayspaceVersionData playspaceVer = new PlayspaceVersionData((String) playspaceVersionFiles.get(j));
			if (playspaceVer.getUser().equals(svrConn.getLoginName())) {
				playspaceDeployIds.addElement(playspaceVer.getDeployId());
				playspaceVersionFile.put(playspaceVer.getDeployId(), playspaceVer);
			}
			else
				playspaceVersionFiles.remove(j);
		}
		Hashtable playspacePaths = DeployFilesFunctions.getPathsForPlayspaces(svrConn, playspaceDeployIds);
		DArrayList playspaceServerFilePaths = new DArrayList(playspacePaths.entrySet());

		// synchronize version files in local space with deployed playspaces on server
		String serverHost = svrConn.getServerPort().substring(0, svrConn.getServerPort().lastIndexOf(":"));
		List keys = new ArrayList(playspacePaths.keySet());
		String modelName = new File(localFileName).getName();
		List badFiles = new ArrayList();
		ListIterator iterator = playspaceVersionFiles.listIterator();
		while (iterator.hasNext()) {
			boolean flag = false;
			String temp = (String) iterator.next();
			for (int j = keys.size() - 1; j >= 0; j--) {
				String key = (String) keys.get(j);
				if (temp.endsWith(modelName.substring(0, modelName.length() - 4) + "_" + key + "_" + serverHost + ".dvc")) {
					flag = true;
				}
			}
			if (!flag)
				badFiles.add(temp);
		}
		if (badFiles != null) {
			ListIterator cleanUpIterator = badFiles.listIterator();
			while (cleanUpIterator.hasNext()) {
				String toBeErased = ((String) cleanUpIterator.next());
				File erase = new File(toBeErased);
				if (erase.exists()) {
					if (!erase.delete())
						System.out.println("Could not delete file: " + toBeErased);
					else
						System.out.println("Old version file exists and was deleted!");
				}
			}
		}
		// if there is more than one deployment option promp for which deployment locations and set playspace ID info
		Object[] choice = null;
		String playspaceId = "";
		PlayspaceVersionData playspaceVersionData = null;
		if (playspaceVersionFiles.size() - badFiles.size() != 1) {
			OneButton1Msg.showOption(par, "Redeploy options", "The playspace is deployed on " + svrConn.getServerPort() +
			        "\nin more than one location. You will be prompted to pick the location.", "ok", OneButton1Msg.DEFAULT_SIZE);
			choice = SimpleChooser.showChooser(par, "Redeploy selection", "ok", "cancel",
			        new VersionListModel(playspaceServerFilePaths), ListSelectionModel.SINGLE_SELECTION);
			if (choice == null || choice.length == 0) {
				return ""; // if they cancelled or did not pick anything return empty string id
			}
			playspaceId = (String) ((Map.Entry) choice[0]).getKey();
			playspaceVersionData = (PlayspaceVersionData) playspaceVersionFile.get(playspaceId);
		}
		else { //case where the playspace is only deployed once on the server
			playspaceId = (String) playspaceDeployIds.elementAt(0);
			playspaceVersionData = (PlayspaceVersionData) playspaceVersionFile.get(playspaceId);
		}
		//last need to check that there are not any version conflicts
		if (!DeployUtilities.isOkToDeployPlayspace(playspace, playspaceVersionData, svrConn)) {
			return "";
		}
		return playspaceId;
	}


	public static void setInterfaceDeployIds(DeployModelData modelData, ModelVersionData versionData)
	{
		Iterator it = modelData.getModelInterfaces().iterator();
		while (it.hasNext()) {
			DeployInterfaceData iData = (DeployInterfaceData) it.next();
			InterfaceVersionData vData = versionData.getInterfaceVersionDataByBuildId(iData.getId());
			if (vData != null)
				iData.setDeployId(vData.getInterfaceId());
		}
	}


	public static void setInterfaceDeployIds(DeployProjectData projectData, ProjectVersionData versionData)
	{
		Iterator it = projectData.getInterfaces().iterator();
		while (it.hasNext()) {
			DeployProjectInterfaceData iData = (DeployProjectInterfaceData) it.next();
			InterfaceVersionData vData = versionData.getInterfaceVersionDataByBuildId(iData.getId());
			if (vData != null)
				iData.setDeployId(vData.getInterfaceId());
		}
	}

    public static void setInterfaceDeployIds(DeployAnalysisToolData analysisToolData, AnalysisToolVersionData versionData)
	{
		Iterator it = analysisToolData.getToolInterfaces().iterator();
		while (it.hasNext()) {
			DeployAnalysisToolInterfaceData iData = (DeployAnalysisToolInterfaceData) it.next();
			InterfaceVersionData vData = versionData.getInterfaceVersionDataByBuildId(iData.getId());
			if (vData != null)
				iData.setDeployId(vData.getInterfaceId());
		}
	}

	public static void setInterfaceDeployIds(DeployModelData modelData, IModelVersionData versionData)
	{
		Iterator it = modelData.getModelInterfaces().iterator();
		while (it.hasNext()) {
			DeployInterfaceData iData = (DeployInterfaceData) it.next();
			InterfaceVersionData vData = versionData.getInterfaceVersionDataByBuildId(iData.getId());
			if (vData != null)
				iData.setDeployId(vData.getInterfaceId());
		}
	}

	public static Vector[] prepareDeployProjectData(Integer folderId, DeployProjectData data)
	{
		Vector projectInfo = Vectors.create(folderId, data.getDescription(), data.getXmlContent(),
		        data.getEditPermissions(), data.getContentVisibilityPermissions());
		Vector projectInterfaceInfo = createDeployProjectInterfaceInfoVector(data.getInterfaces());
		List iModels = data.getIntegrationModels();
		Vector allIModelsInfo = new Vector();
		Vector iModelInfo, iModelinterfaceInfo;
		for (Iterator iterator = iModels.iterator(); iterator.hasNext();) {
			DeployModelData iModel = (DeployModelData) iterator.next();
			iModelInfo = Vectors.create(iModel.getModelDescription(), iModel.getXmlContent(), iModel.getPermissions());
			iModelinterfaceInfo = createDeployInterfaceInfoVector(iModel.getModelInterfaces());
			allIModelsInfo.add(Vectors.create(iModelInfo, iModelinterfaceInfo));
		}
		return new Vector[]{projectInfo, projectInterfaceInfo, allIModelsInfo};
	}

	public static Vector[] prepareRedeployProjectData(String redeployId, DeployProjectData data)
	{
		Vector projectInfo = Vectors.create(redeployId, data.getDescription(), data.getXmlContent(),
		        data.getEditPermissions(), data.getContentVisibilityPermissions());
		Vector projectInterfaceInfo = createDeployProjectInterfaceInfoVector(data.getInterfaces());
		List iModels = data.getIntegrationModels();
		Vector allIModelsInfo = new Vector();
		Vector iModelInfo, iModelinterfaceInfo;
		for (int i = 0; i < iModels.size(); i++) {
			DeployModelData iModel = (DeployModelData) iModels.get(i);
			iModelInfo = Vectors.create(iModel.getDeployId(), iModel.getModelDescription(), iModel.getXmlContent(), iModel.getPermissions());
			iModelinterfaceInfo = createDeployInterfaceInfoVector(iModel.getModelInterfaces());
			allIModelsInfo.add(Vectors.create(iModelInfo, iModelinterfaceInfo));

		}
		return new Vector[]{projectInfo, projectInterfaceInfo, allIModelsInfo};
	}

	public static Object[] selectInterfaces(List interfaces)
	{
		return SimpleChooser.showChooser(null, "Select Interfaces to Deploy", "ok", "cancel",
		        new InterfacesListModel(new DArrayList(interfaces)), new Dimension(400, 200));
	}

   /**
    *
    * @param infoVector  :{{iface_deploy_id,version,iface_build_id}}
    * @param build_id
    * @return
    */
    public static String getIfaceDeployId(Vector infoVector,String build_id){
          for(Iterator i=infoVector.iterator();i.hasNext();){
              Vector iface=(Vector) i.next();
              String iface_build_id=(String)iface.get(2);
              if(iface_build_id.equals(build_id)) return (String)iface.get(0);
          }
         return null;
    }

	public static class InterfacesListModel extends DListDListModel implements DListModel
	{
		public InterfacesListModel(DArrayList list)
		{
			super(list);
		}

		public Icon getIcon(int index)
		{
			return DomeIcons.getIcon(DomeIcons.INTERFACE);
		}

		public String getListText(int index)
		{
			return ((DeployInterfaceData) getElementAt(index)).getName();
		}
	}

	public static class VersionListModel extends DListDListModel implements DListModel
	{
		public VersionListModel(DArrayList list)
		{
			super(list);
		}

		public Icon getIcon(int index)
		{
			return null;
		}

		public String getListText(int index)
		{
			return (String) ((Map.Entry) getElementAt(index)).getValue();
		}
	}
}
