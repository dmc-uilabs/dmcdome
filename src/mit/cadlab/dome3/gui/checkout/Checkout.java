package mit.cadlab.dome3.gui.checkout;

import edu.oswego.cs.dl.util.concurrent.misc.SwingWorker;
import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.ModelVersionData;
import mit.cadlab.dome3.gui.deploy.components.PlayspaceVersionData;
import mit.cadlab.dome3.gui.fileSystem.FileSystemObject;
import mit.cadlab.dome3.gui.guiutils.DomeFileChooser;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGuiInfo;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.CheckOutFunctions;
import mit.cadlab.dome3.network.client.functions.DeployFilesFunctions;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.util.AuxiliaryFileUtils;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.DomeXmlData;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 18, 2003
 * Time: 1:40:30 PM
 * To change this template use Options | File Templates.
 */
public class Checkout {
    // constants for the possible checkout types
    public static final byte CHECKOUT_MODEL = 0;
    public static final byte CHECKOUT_PLAYSPACE = 1;
    public static final byte CHECKOUT_PROJECT = 2;
    public static final byte CHECKOUT_ANALYSIS_TOOL = 3;

    private FileSystemObject selectedFileSystemObject = null;
    private Object selectedObjectId = null;
    private String modelType = null;
    private File savePathFile = null;
    private ServerConnection conn = null;
    private Vector modelContent = null;
    private Vector playspaceContent = null;
    private Vector projectContent = null;
    private static final String slash = System.getProperty("file.separator");
    protected Component Parent = null;


    /**
     * Class used to checkout models or playspaces for a server for local editing
     * @param checkoutType use one of the two constants Checkout.CHECKOUT_MODEL or Checkout.CHECKOUT_PLAYSPACE
     */
    public Checkout(byte checkoutType, Component parent) {
        //first login
        this.Parent = parent;
        conn = LoginPrompt.showDialog(parent);
        // if valid login
        if (conn == null)
            return;
        // get location of item on server to be checked out
        Object selectedObject = LocateForCheckout.showDialog(parent, conn, checkoutType);

        if (selectedObject == null)
            return;
        if (!(selectedObject instanceof FileSystemObject))
            return;

        selectedFileSystemObject = (FileSystemObject) selectedObject;
        selectedObjectId = selectedFileSystemObject.getId();
	    initializeModelAndPlayspaceAndProjectContent(checkoutType);
	    if (!haveSavePermission(Parent, checkoutType))
		    return;
		saveModelPlayspaceProjectFile(checkoutType);
	    JFrame waitWin = StatusWindow.show(StatusWindow.CHECKINGOUT,
	                                       selectedFileSystemObject.getName(),
	                                       BuildMode.getStatusWindowLocation());
	    CheckoutWorker ckworker = new CheckoutWorker(this, checkoutType, waitWin);
	    ckworker.start();
    }

	static class CheckoutWorker extends SwingWorker
	{
		String fn;
		JFrame waitWin;
		Checkout checkout;
		byte checkoutType;
		public CheckoutWorker(Checkout checkout, byte checkoutType, JFrame waitWin)
		{
			this.checkoutType = checkoutType;
			this.checkout = checkout;
			this.waitWin = waitWin;
		}

		public Object construct()
		{
  			if (checkoutType == CHECKOUT_MODEL) {
				checkout.saveModelInterfaces();
				if (checkout.haveReDeployPermission(checkoutType))
					checkout.writeModelVersionFile();
			} else if (checkoutType == CHECKOUT_PLAYSPACE) {
				if (checkout.haveReDeployPermission(checkoutType))
					checkout.writePlayspaceVersionFile();
			} else if (checkoutType == CHECKOUT_PROJECT) {
				checkout.saveProjectInterfaces();
				checkout.saveIModelFiles();
				checkout.saveIModelsInterfaces();
				if (checkout.haveReDeployPermission(checkoutType))
					checkout.writeProjectVersionFile();
			}
			checkout.getConnection().logout();
			return new Object();
		}

		public void finished()
		{
			waitWin.setVisible(false);
			waitWin.dispose();
			int button = TwoButton1Msg.showOption(null,
			                                      "Checkout: finished", "Checkout is done. Do you want to open it?", "Yes",
			                                      "No", new Dimension(230, 80));
			if (button != 0) {
				if (checkoutType == CHECKOUT_MODEL) {
					if (checkout.getModelType().equalsIgnoreCase("DOME"))
						BuildMode.openModel(DomeModel.TYPE_INFO.getTypeName(), checkout.getSavePathFile().getPath());
					else
						BuildMode.openModel(checkout.getModelType(), checkout.getSavePathFile().getPath());
				} else if (checkoutType == CHECKOUT_PLAYSPACE)
					BuildMode.openPlayspace(checkout.getSavePathFile().getPath());
				else if (checkoutType == CHECKOUT_PROJECT) BuildMode.openProject(checkout.getSavePathFile().getPath());
			}
		}
	}

	public Component getParent() {
		return Parent;
	}

	public ServerConnection getConnection() {
		return conn;
	}

	public String getModelType() {
		return modelType;
	}

	public File getSavePathFile() {
		return savePathFile;
	}

    public void initializeModelAndPlayspaceAndProjectContent(byte checkoutType) {

        if (checkoutType == CHECKOUT_MODEL) {
            modelContent = CheckOutFunctions.checkoutModel(conn, selectedObjectId.toString());
	        if(modelContent.isEmpty())
	            return;
            modelType = (String) ((Vector) modelContent.get(0)).get(0);
        }
        else if (checkoutType == CHECKOUT_PLAYSPACE)  {
            playspaceContent = CheckOutFunctions.checkoutPlayspace(conn, selectedObjectId.toString());
        }
        else  {
            projectContent = CheckOutFunctions.checkoutProject(conn, selectedObjectId.toString());
        }
    }

    private void saveModelInterfaces() {

        Vector modelInfo = (Vector) modelContent.get(0);              //vector with single component containing model info

        String modelXml = (String) modelInfo.get(3);

        Vector interfaceInfo = (Vector) modelContent.get(1);         //vector containing other vectors, which conatain interface info

        File interfaceDir = new File(savePathFile.getParent() + slash + "interfaces-" + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - (modelType.length() + 5))) + "-" + modelInfo.get(1));

        if (interfaceDir.exists()) {
            System.out.println("The directory already exists");
        } else {
            if (!interfaceDir.mkdir())
                System.out.println("Error creating interface directory");

        }

        int numberOfInterfaces = interfaceInfo.size();

        for (int i = 0; i < numberOfInterfaces; i++) {

            Vector intf = new Vector((Vector) interfaceInfo.get(i));
            save(interfaceDir.toString() + slash + (intf.get(0) + "-mappings"), (String) intf.get(2));

            //Qing-- check whether there are customGuis or not
            String interfaceId = (String) intf.get(0);  //but this is the build id

            String interfaceXml = (String) intf.get(1);
            String interface_deploy_id=(String)intf.get(3);

            DomeXmlData interfaceData = new DomeXmlData(DomeXmlData.INTERFACE, interfaceXml);
            Vector customGuiFiles = interfaceData.getInterfaceCustomGuiFiles();
            boolean is_old_model = false;


            if (customGuiFiles.size() > 0) {
                //first see if it's old model
                CustomGuiInfo a_info = (CustomGuiInfo) customGuiFiles.get(0);
                if (a_info.getJarFilePath() == null||a_info.getJarFilePath().equals("")) is_old_model = true;
                if (!is_old_model) {
                    interfaceXml = configCustomGuiFile(interfaceDir.toString(), interface_deploy_id, modelXml, interfaceXml);
                }
            }
            save(interfaceDir.toString() + slash + (interfaceId + ".dmi"), interfaceXml);
        }

    }

    private String configCustomGuiFile(String folderpath, String interfaceId, String modelXml, String interfaceXml) {
        boolean is_old_model = false;
        if (folderpath == null) {
            System.out.println("error getting directory to save customGui files for the interface");
            return interfaceXml;
        }

        File folder = new File(folderpath);
        if (!folder.exists() || (!folder.isDirectory())) {
            System.out.println("error getting directory to save auxiliary files");
            return interfaceXml;
        }
        File customGuifolder = new File(folderpath + slash + interfaceId);
        if (!customGuifolder.exists()) {
            customGuifolder.mkdir();
            System.out.println("folder:" + customGuifolder.getPath() + " created...");
        }


        //get customGuiInfo out of xml
        DomeXmlData interfaceData = new DomeXmlData(DomeXmlData.INTERFACE, interfaceXml);
        Vector customGuiFiles = interfaceData.getInterfaceCustomGuiFiles();
        for (Iterator i = customGuiFiles.iterator(); i.hasNext();) {
            CustomGuiInfo customGuiFile = (CustomGuiInfo) i.next();
            String filename = null;
            String id = customGuiFile.getJarFileId().toString();
            if (customGuiFile.getJarFilePath() == null) {
                //for old version models
                is_old_model = true;
                String fileLocation = AuxiliaryFileUtils.getDefaultLocation(id, modelXml,1);
                if (fileLocation != null)
                    filename = (new File(fileLocation)).getName();
            } else {
               // filename = (new File(customGuiFile.getJarFilePath())).getName();
                 filename = FileUtils.getFileName(customGuiFile.getJarFilePath());
            }
            if (filename != null) {
                if (!is_old_model) {
                    String location_on_the_server = DeployFilesFunctions.get_customGuiFile_info(conn, filename, interfaceId);
                    File localfile = new File(customGuifolder + slash + filename);

                    //download
                    try {
                        String serverInfo[] = NetworkUtils.parseServerPortInfo(conn.getServerPort());
                        URL serverUrl = new URL("http", serverInfo[0], new Integer(serverInfo[2]).intValue(), parse(location_on_the_server));
                        NetworkUtils.httpDownload(serverUrl, localfile.getPath());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    String oldFilePath = customGuiFile.getJarFilePath();
                    customGuiFile.setJarFilePath(localfile.getPath());
                    //replace the info in the xml document
                    interfaceXml = AuxiliaryFileUtils.replaceCustomGuiFileLocation(interfaceXml, oldFilePath, localfile.getPath());
                } else {
                    //old_model

                }
            }
        }
        return interfaceXml;
    }

    private String configAuxFiles(String folderpath, String modelXml) {
        if (folderpath == null) {
            System.out.println("error getting directory to save auxiliary files");
            return modelXml;
        }

        //get id out of xml
        DomeXmlData modelData = new DomeXmlData(DomeXmlData.MODEL, modelXml);
        Vector modelAuxFiles = modelData.getModelAuxFiles();
        //this is wrong this is the build id String modelId = modelData.getId();
        String modelId = selectedObjectId.toString();


        File folder = new File(folderpath);
        if (!folder.exists() || (!folder.isDirectory())) {
            System.out.println("error getting directory to save auxiliary files");
            return modelXml;
        }
        File auxTopfolder = new File(folderpath + File.separator + DomeServer.FILE_SPACE_FOLDER_NAME_ON_SERVER);

        if (auxTopfolder.exists() && auxTopfolder.isDirectory()) {
            System.out.println("find folder:" + auxTopfolder.getPath() + "...");
        } else {
            if (!auxTopfolder.mkdir())
                System.out.println("Error creating /AuxFiles directory");
            else
                System.out.println("folder:" + auxTopfolder.getPath() + " created...");

        }

        File auxfolder = new File(auxTopfolder.getPath() + File.separator + modelId);

        if (auxfolder.exists() && auxfolder.isDirectory()) {
            System.out.println("find folder:" + auxfolder.getPath() + "...");
            // FileUtils.deleteDirectoryContents(auxfolder, true);      //not clean up the folder content in case many models are downloaded to the same folder
        } else {
            if (!auxfolder.mkdir())
                System.out.println("Error creating auxFile sub directory");
            else
                System.out.println("folder:" + auxfolder.getPath() + " created...");

        }


        for (Iterator i = modelAuxFiles.iterator(); i.hasNext();) {
            AbstractAuxFile af = (AbstractAuxFile) i.next();
            Vector auxInfo = CheckOutFunctions.checkoutAuxFileForModel(conn, af.getId().toString(), modelId);
            if (auxInfo == null || auxInfo.size() == 0) {
                System.out.println("error getting auxfile information from server for auxiliary file " + af.getId());
                break;
            }

            String filename = (String) auxInfo.get(0);
            String serverLocation = (String) auxInfo.get(1);

            if (serverLocation.equals("")) {
                System.out.println("not avilable on the server");
            } else {
                //download to local address
                File localfile = new File(auxfolder.getPath() + File.separator + filename);
                try {
                    if (localfile.exists() && localfile.isFile()) {
                        System.out.println("file" + localfile.getPath() + " already exist, will be replaced");
                    } else {
                        if (!localfile.createNewFile()) {
                            System.out.println("Error creating " + filename + " in folder " + auxfolder.getPath());
                        } else {
                            System.out.println("file:" + localfile.getPath() + " created..");
                        }
                    }
                    String serverInfo[] = NetworkUtils.parseServerPortInfo(conn.getServerPort());
                    URL serverUrl = new URL("http", serverInfo[0], new Integer(serverInfo[2]).intValue(), parse(serverLocation));
                    NetworkUtils.httpDownload(serverUrl, localfile.getPath());

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                //replace location in auxFile
                af.setFile(localfile);
            }
        }
        //rewrite model xml files
        return AuxiliaryFileUtils.replaceAuxFilesLocation(modelAuxFiles, modelXml);
    }

    /**
     * this method is to put the server location in database to be handleable by url class
     *      a normal serverlocation string is d4bdc2f8-b76e-1004-8dc3-fdaf86ce8e90\customGui.jar
     * @param serverLocation
     * @return
     */
    private String parse(String serverLocation) {
        if (serverLocation.indexOf("\\") == -1) return serverLocation;
        return "/" + DomeServer.FILE_SPACE_FOLDER_NAME_ON_SERVER + "/" + AuxiliaryFileUtils.replace(serverLocation, "\\", "/");
    }

    private void writeModelVersionFile() {

        DeployUtilities.writeModelVersionFile(savePathFile.toString(), new ModelVersionData((Vector) modelContent.get(2), conn));
        return;
    }

    private void writePlayspaceVersionFile() {

        DeployUtilities.writePlayspaceVersionFile(savePathFile.toString(), new PlayspaceVersionData((Vector) playspaceContent.get(1), conn));
        return;
    }

    private void writeProjectVersionFile() {

        DeployUtilities.writeProjectVersionFile(savePathFile.toString(), (Vector) projectContent.get(4), conn);
        return;
    }

    private boolean haveReDeployPermission(byte checkoutType) {

        int size;
        Vector element;
        if (checkoutType == CHECKOUT_MODEL) {
            size = 3;
            element = modelContent;
        } else if (checkoutType == CHECKOUT_PLAYSPACE) {
            size = 2;
            element = playspaceContent;
        } else {
            size = 5;
            element = projectContent;
        }

        if (element.size() == size)
            return true;
        else
            return false;

    }

    private boolean haveSavePermission(Component parent, byte checkoutType) {

	    String type = "model";
        Vector element;
        if (checkoutType == CHECKOUT_MODEL)
            element = modelContent;
        else if (checkoutType == CHECKOUT_PLAYSPACE)  {
            element = playspaceContent;
	        type = "playspace";
        }
        else  {
            element = projectContent;
	        type = "project";
        }
        if (element.size() == 0) {
			LocateForCheckout.showMessage(parent, type);
            return false;
        } else
            return true;
    }

    public boolean saveModelPlayspaceProjectFile(int checkoutType) {

        Vector checkoutElement;
        if (checkoutType == CHECKOUT_MODEL)
            checkoutElement = modelContent;
        else if (checkoutType == CHECKOUT_PLAYSPACE)
            checkoutElement = playspaceContent;
        else
            checkoutElement = projectContent;

        Vector ElementInfo = (Vector) checkoutElement.get(0);              //vector with single component containing model info

        String elementContent;
        if (checkoutType == CHECKOUT_MODEL)
            elementContent = (String) ElementInfo.get(3);
        else if (checkoutType == CHECKOUT_PLAYSPACE)
            elementContent = (String) ElementInfo.get(1);
        else
            elementContent = (String) ElementInfo.get(2);


        boolean notSaved = true;

        String filename;

        if (checkoutType == CHECKOUT_MODEL) {
            filename = selectedFileSystemObject.getName() + "-" + modelType + ".dml";
        } else if (checkoutType == CHECKOUT_PLAYSPACE)
            filename = selectedFileSystemObject.getName() + ".dps";
        else
            filename = selectedFileSystemObject.getName() + ".dpj";

        try {
// Create a File object containing the canonical path of the
// desired file
            File f = new File(new File(filename).getCanonicalPath());
            BuildMode.buildFileChooser.setSelectedFile(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileFilterName;
        if (checkoutType == CHECKOUT_MODEL)
            fileFilterName = DomeFileChooser.DOME_MODELS_FILTER;
        else if (checkoutType == CHECKOUT_PLAYSPACE)
            fileFilterName = DomeFileChooser.DOME_PLAYSPACE_FILTER;
        else
            fileFilterName = DomeFileChooser.DOME_PROJECT_FILTER;

        File file = null;
        String suffix;
        if (checkoutType == CHECKOUT_MODEL)
            suffix = ".dml";
        else if (checkoutType == CHECKOUT_PLAYSPACE)
            suffix = ".dps";
        else
            suffix = ".dpj";

        while (notSaved) {
            String newFileName = BuildMode.buildFileChooser.showSaveDialog(null, fileFilterName);
            if (newFileName == null)
                return false;

            if (checkoutType == CHECKOUT_MODEL)
                newFileName = DomeModelBuilder.fixFileName(newFileName, modelType);
            else if (!newFileName.endsWith(suffix))
                newFileName = newFileName + suffix;

// check if file already exists
            file = new File(newFileName);

//before write to file, prompt for auxiliary files
            if (checkoutType == CHECKOUT_MODEL) {
                Vector auxFiles = AuxiliaryFileUtils.getAuxFileInfos(elementContent);
//deal with auxiliary files
                if (auxFiles.size() > 0) {
                    String msg = "has auxiliary files. You may choose to download them now or check them out later.\n" +
                            "We recommend checking all files out to avoid potential version conflicts.";
                    DomeXmlData n = new DomeXmlData(DomeXmlData.MODEL, elementContent);
                    String name = n.getName();
                    int button = TwoButton2Msg.showOption(null,
                            "Options: Model auxiliary files", msg, name, "checkout auxillary files",
                            "leave files on server", new Dimension(230, 100));
                    if (button != 0) {
                        elementContent = configAuxFiles(file.getParentFile().getPath(), elementContent);
                    }
                }

            }


            if (file.exists()) {

                String msg = null;
                msg = "File <" + file.getName() + "> already exists. Replace it?";


                int button = TwoButton1Msg.showOption(null,
                        "Warning: File exists", msg, "Replace",
                        "Cancel", new Dimension(230, 80));
                if (button != 0) {
                    Checkout.save(newFileName, elementContent);
                    notSaved = false;
                }


            } else {
                Checkout.save(newFileName, elementContent);
                notSaved = false;
            }

        }

        savePathFile = file;

        return true;
    }

    private static void save(String fileName, String content) {
        FileUtils.writeStringToFile(content, fileName);
    }

    public static void main
            (String[] args) {
        new Checkout(CHECKOUT_MODEL, null);
//new Checkout(CHECKOUT_PLAYSPACE);
        System.exit(0);

    }

    private void saveProjectInterfaces() {

        Vector projectInfo = (Vector) projectContent.get(0);              //vector with single component containing model info
        Vector interfaceInfo = (Vector) projectContent.get(1);         //vector containing other vectors, which conatain interface info

        File interfaceDir = new File(savePathFile.getParent() + slash + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - 4)) + "-resources");

        if (interfaceDir.exists()) {
            System.out.println("The directory already exists");
        } else {
            if (!interfaceDir.mkdir())
                System.out.println("Error creating project resources directory");

        }

        interfaceDir = new File(savePathFile.getParent() + slash + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - 4)) + "-resources" + slash + "interfaces-" + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - 4)) + "-" + projectInfo.get(1));
        if (interfaceDir.exists()) {
            System.out.println("The directory already exists");
        } else {
            if (!interfaceDir.mkdir())
                System.out.println("Error creating project interface directory");

        }

        int numberOfInterfaces = interfaceInfo.size();

        String projectXml = (String) projectInfo.get(2);

        for (int i = 0; i < numberOfInterfaces; i++) {

            Vector intf = new Vector((Vector) interfaceInfo.get(i));
            save(interfaceDir.toString() + slash + (intf.get(0) + "-mappings"), (String) intf.get(2));

              //Qing-- check whether there are customGuis or not
            String interfaceId = (String) intf.get(0);  //but this is the build id

            String interfaceXml = (String) intf.get(1);
            String interface_deploy_id=(String)intf.get(3);

            DomeXmlData interfaceData = new DomeXmlData(DomeXmlData.INTERFACE, interfaceXml);
            Vector customGuiFiles = interfaceData.getInterfaceCustomGuiFiles();
            boolean is_old_model = false;


            if (customGuiFiles.size() > 0) {
                //first see if it's old model
                CustomGuiInfo a_info = (CustomGuiInfo) customGuiFiles.get(0);
                if (a_info.getJarFilePath() == null||a_info.getJarFilePath().equals("")) is_old_model = true;
                if (!is_old_model) {
                    interfaceXml = configCustomGuiFile(interfaceDir.toString(), interface_deploy_id, projectXml, interfaceXml);
                }
            }

            save(interfaceDir.toString() + slash + (intf.get(0) + ".dpi"), (String) intf.get(1));
        }

        //check out project interface customGui if any


    }

    private void saveIModelFiles() {

        Vector iModelsInfo = (Vector) projectContent.get(2);         //vector containing other vectors, which conatain interface info

        File iModelsDir = new File(savePathFile.getParent() + slash + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - 4)) + "-resources");

        if (iModelsDir.exists()) {
            System.out.println("The directory already exists");
        } else {
            if (!iModelsDir.mkdir())
                System.out.println("Error creating project resources directory");

        }

        int numberOfIModels = iModelsInfo.size();

        for (int i = 0; i < numberOfIModels; i++) {

            Vector imodel = new Vector((Vector) iModelsInfo.get(i));
            save(iModelsDir.toString() + slash + (imodel.get(1) + "-IMODEL.dml"), (String) imodel.get(2));
        }

    }

    private void saveIModelsInterfaces() {

        Vector interfaceInfo = (Vector) projectContent.get(3);         //vector containing other vectors, which conatain interface info

        int numberOfInterfaces = interfaceInfo.size();

        for (int i = 0; i < numberOfInterfaces; i++) {
            Vector intf = new Vector((Vector) interfaceInfo.get(i));

            File interfaceDir = new File(savePathFile.getParent() + slash + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - 4)) + "-resources");

            if (interfaceDir.exists()) {
                System.out.println("The directory already exists");
            } else {
                if (!interfaceDir.mkdir())
                    System.out.println("Error creating project resources directory");

            }

            interfaceDir = new File(savePathFile.getParent() + slash + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - 4)) + "-resources" + slash + "interfaces-" + intf.get(0) + "-" + intf.get(1));
            if (interfaceDir.exists()) {
                System.out.println("The directory already exists");
            } else {
                if (!interfaceDir.mkdir())
                    System.out.println("Error creating project interface directory");

            }

            save(interfaceDir.toString() + slash + (intf.get(2) + "-mappings"), (String) intf.get(4));
            save(interfaceDir.toString() + slash + (intf.get(2) + ".dmi"), (String) intf.get(3));
        }

    }

}