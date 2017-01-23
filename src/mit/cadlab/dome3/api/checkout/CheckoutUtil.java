package mit.cadlab.dome3.api.checkout;

import mit.cadlab.dome3.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome3.gui.deploy.components.ModelVersionData;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.functions.CheckOutFunctions;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles.AbstractAuxFile;
import mit.cadlab.dome3.util.AuxiliaryFileUtils;
import mit.cadlab.dome3.util.DomeError;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.DomeXmlData;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

public class CheckoutUtil {

    private static final String slash = System.getProperty("file.separator");

    /**
     * currently ignoring the interfeces' custom GUIs
     */
    public void checkoutModel(CheckoutServerConnection con, String space, String path, String modelName,
                                     String saveToFile, boolean writeVersion) {
        String modelId = con.getModelId(space, path.split("/"), modelName);
        if (modelId==null)
            throw new DomeError("Could not find model '" + modelName + "' in from the path '" + path + "' in the " + space+ " space");
        Vector modelContent = CheckOutFunctions.checkoutModel(con.getServerConnection(), modelId);
        if (modelContent.isEmpty())
            throw new DomeError("Could not retrieve the content of model '" + modelName + "'");
        Vector modelInfo = (Vector) modelContent.get(0);
        String modelType = (String) modelInfo.get(0);

        String modelXml = (String) modelInfo.get(3);
        String newFileName = DomeModelBuilder.fixFileName(saveToFile, modelType);
        File file = new File(newFileName);
        Vector auxFiles = AuxiliaryFileUtils.getAuxFileInfos(modelXml);
        if (auxFiles.size() > 0) {
            modelXml = configAuxFiles(file.getParentFile().getPath(), modelXml, modelId, con);
        }
        FileUtils.writeStringToFile(modelXml, newFileName);

        Vector interfaceInfo = (Vector) modelContent.get(1);
        File interfaceDir = new File(file.getParent() + slash + "interfaces-" + (file.getName()).substring(0, (file.getName().length() - (modelType.length() + 5))) + "-" + modelInfo.get(1));

        if (!interfaceDir.exists()) {
            if (!interfaceDir.mkdir())
                throw new DomeError("Could not create a folder to store the model's interface(s)");
        }
        for (int i = 0; i < interfaceInfo.size(); i++) {
            Vector intf = new Vector((Vector) interfaceInfo.get(i));
            FileUtils.writeStringToFile((String) intf.get(2), interfaceDir.toString() + slash + (intf.get(0) + "-mappings"));
            String interfaceId = (String) intf.get(0);
            String interfaceXml = (String) intf.get(1);
            FileUtils.writeStringToFile(interfaceXml, interfaceDir.toString() + slash + (interfaceId + ".dmi"));
        }
        if (writeVersion)
            DeployUtilities.writeModelVersionFile(file.toString(), new ModelVersionData((Vector) modelContent.get(2), con.getServerConnection()));
    }

    private String configAuxFiles(String folderpath, String modelXml, String modelId, CheckoutServerConnection con) {
        //get id out of xml
        DomeXmlData modelData = new DomeXmlData(DomeXmlData.MODEL, modelXml);
        Vector modelAuxFiles = modelData.getModelAuxFiles();
        File folder = new File(folderpath);
        if (!folder.exists() || (!folder.isDirectory())) {
            throw new DomeError("The given location to save to is invalid");
        }
        File auxTopfolder = new File(folderpath + File.separator + DomeServer.FILE_SPACE_FOLDER_NAME_ON_SERVER);
        if (!auxTopfolder.exists() || !auxTopfolder.isDirectory()) {
            if (!auxTopfolder.mkdir())
                throw new DomeError("Error creating a folder to store auxilary files.");
        }
        File auxfolder = new File(auxTopfolder.getPath() + File.separator + modelId);
        if (!auxfolder.exists() || !auxfolder.isDirectory()) {
            if (!auxfolder.mkdir())
                throw new DomeError("Error creating a folder to store auxilary files of the model.");
        }
        for (Iterator i = modelAuxFiles.iterator(); i.hasNext();) {
            AbstractAuxFile af = (AbstractAuxFile) i.next();
            Vector auxInfo = CheckOutFunctions.checkoutAuxFileForModel(con.getServerConnection(), af.getId().toString(), modelId);
            if (auxInfo == null || auxInfo.size() == 0) {
                throw new DomeError("error getting auxfile information from server for auxiliary file " + af.getId());
            }
            String filename = (String) auxInfo.get(0);
            String serverLocation = (String) auxInfo.get(1);
            if (serverLocation.equals("")) {
                System.out.println("not available on the server");
            } else {
                //download to local address
                File localfile = new File(auxfolder.getPath() + File.separator + filename);
                try {
                    if (localfile.exists() && localfile.isFile()) {
                        System.out.println("file" + localfile.getPath() + " already exist, will be replaced");
                    } else {
                        if (!localfile.createNewFile())
                            throw new DomeError("Error creating " + filename + " in folder " + auxfolder.getPath());
                    }
                    String serverInfo[] = NetworkUtils.parseServerPortInfo(con.getServerConnection().getServerPort());
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

    private String parse(String serverLocation) {
        if (serverLocation.indexOf("\\") != -1)
            serverLocation = "/" + DomeServer.FILE_SPACE_FOLDER_NAME_ON_SERVER + "/" + AuxiliaryFileUtils.replace(serverLocation, "\\", "/");
        return serverLocation;
    }
}
