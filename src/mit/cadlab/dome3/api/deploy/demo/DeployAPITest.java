package mit.cadlab.dome3.api.deploy.demo;

import mit.cadlab.dome3.api.deploy.ModelDeployer;
import mit.cadlab.dome3.api.deploy.DeployServerConnection;
import mit.cadlab.dome3.DomeInit;

import java.util.List;
import java.io.File;

public class DeployAPITest {

    public void testDeployModel() {
        // initialize DOME
        if (!DomeInit.isInitialized())
            DomeInit.initializeDOME();
        // preparing a new model deployment
        ModelDeployer deployer = new ModelDeployer("D:/dome3/models/test/heated gas-EXCEL.dml");
        // set the model's description
        deployer.setModelDescription("test-deployed using the deploy API");
        // see all available interfaces of the model
        List ifaces = deployer.getInterfaceNames();
        System.out.println("available interfaces: " + ifaces);
        // select to deploy only the first interface
        deployer.selectInterfaceByName((String)ifaces.get(0));
        // and set the interface description
        deployer.setInterfaceDescription((String) ifaces.get(0), "the first interface of the model");
        // make a connection to the server onto which the model will be deployed
        DeployServerConnection con = new DeployServerConnection("root", "cadlab", DeployServerConnection.USER, "localhost");
        // give all model-editing permissions to root
        deployer.giveAllModelPermissions(con, "root");
        // give all interface-using permissions to guest
        deployer.giveAllInterfacePermissions((String) ifaces.get(0), con, "guest");
        // can see if the model is already deployed to a specified path
        System.out.println("deployed? = " + deployer.isDeployed(con, "Area calculator", DeployServerConnection.SERVER, "Public/new test"));
        // ... regardless, can use deployAuto to deploy/redeploy the model
        deployer.deployAuto(con, DeployServerConnection.SERVER, "Public/new test");
        // create a new folder
        deployer.createFolder(con, con.getFolderIdForPath(DeployServerConnection.SERVER, "Public/new test".split("/")), "new folder");
        // or in another way ..
        deployer.createFolder(con, DeployServerConnection.SERVER, "Public/new test/new folder", "another folder");

        // and deploy the model there
        deployer.deployAuto(con, DeployServerConnection.SERVER, "Public/new test/new folder");

    }

    public static void main(String[] args) {
        DeployAPITest test = new DeployAPITest();
        test.testDeployCatalogModel();
        System.out.println("done!");
    }

    public void testDeployCatalogModel() {
        // initialize DOME
        if (!DomeInit.isInitialized())
            DomeInit.initializeDOME();
        // preparing a new model deployment
        ModelDeployer deployer = new ModelDeployer("D:/tomcat5-domewc/webapps/pemsweb/temp/-49972366/model/t2-CATALOG.dml");
        // set the model's description
        deployer.setModelDescription("test-deployed of catalog modelI");
        // see all available interfaces of the model
        List ifaces = deployer.getInterfaceNames();
        System.out.println("available interfaces: " + ifaces);
        // select to deploy only the first interface
        deployer.selectInterfaceByName((String) ifaces.get(0));
        // and set the interface description
        deployer.setInterfaceDescription((String) ifaces.get(0), "the first interface of the catalog model");
        // make a connection to the server onto which the model will be deployed
        DeployServerConnection con = new DeployServerConnection("root", "cadlab", DeployServerConnection.USER, "localhost");
        // give all model-editing permissions to root
        deployer.giveAllModelPermissions(con, "root");
        // give all interface-using permissions to guest
        deployer.giveAllInterfacePermissions((String) ifaces.get(0), con, "guest");
        deployer.deployAuto(con, DeployServerConnection.SERVER, "Public/new test/new folder");
    }
}
