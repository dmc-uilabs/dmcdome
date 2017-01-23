package test.plugin;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Plugin;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10Part;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10Dimension;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10MaterialProperty;
import mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport.Ideas10RealValueProperty;
import com.sdrc.openideas.util.OI_App;
import com.sdrc.openideas.util.OI_CommandWait;
import com.sdrc.openideas.OI_Server;
import com.sdrc.openideas.OI_CommandServer;
import com.sdrc.openideas.OI_Root;
import com.ford.kbedoor.sealanalysis.sealgap.SealGapAnalyzer;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import cern.jet.math.Functions;

import java.util.Vector;
import java.util.Arrays;

public class Ideas10PluginTest
{
    private OI_Server oiServer;
    private IntHolder intHolder;
	private static Ideas10Plugin plg;
    private ORB orb;

	static void print(String msg)
	{
		System.out.println(msg);
	}

    public Ideas10PluginTest() {
    }

    public void connectIdeasServer(String serverName) {
        String[] orbArgs = new String[4];
        orbArgs[0] = "-ORBname";
        orbArgs[1] = "eds.ideas10";
        orbArgs[2] = "-ORBdomain_name";
        orbArgs[3] = "MyDomain";
        Debug.trace(Debug.ALL, "initiating COBRA orb...");
        orb = org.omg.CORBA.ORB.init(orbArgs, null);
        OI_App serverConnnector = new OI_App("Ideas10Plugin", orb);
        //String serverName = "myServer";
        String host = "cadlab49";
        Debug.trace(Debug.ALL, "connecting to I-DEAS10 server...");
        try {
            oiServer = serverConnnector.ConnectServer(host, serverName);
            if (oiServer == null) {
                System.err.println(
                        "Unable to connect to the server, exiting."
                );
                System.exit(1);
            }
        } catch (org.omg.CORBA.SystemException e) {
            System.err.println(
                    "Detected unexpected CORBA exception, exiting.\n" + e
            );
            System.exit(0);
        }
        intHolder = new IntHolder();
    }

    public void testIntfAna () {
        //connectIdeasServer("myServer");
        SealGapAnalyzer sga = new SealGapAnalyzer(oiServer, orb);
        sga.setBodyMounted(false);
        sga.setABSurfacePartName("door surfaces");
        sga.setJSurfacePartName("body side surfaces");
        sga.setBulbPartName("pri seal bulb filled");
        sga.setSectionDistance(30);
        sga.initializePlanes();

        sga.setInterferenceAnalysis(true);
        sga.setCombinedAnalysis(false);
        sga.setDeflectedDoor(false);
        sga.setOffsetData(sga.makeOffsetData(0.5,0.5,0.5));
        sga.analyzeSealGap();
        Vector areas = sga.getAreaVectors();
        for (int i = 0; i < areas.size(); i++) {
            System.out.println(areas.get(i));

        }
    }

    public void testCommandWait() {
        connectIdeasServer("myServer11");
        //OI_CommandServer commandServer = oiServer.GetCommandServer();
        OI_CommandWait commandWait = new OI_CommandWait(orb, oiServer.GetCommandServer());
        String command = "/f o n PK Ford; m fil c:\\sealgap\\3part\\3partParametricSealGap.mf1 okay";
        commandWait.SendCommand(command, OI_CommandWait.E_Both);
        String command2 = "/f export b VR okay lab z_uid; 6 m fil c:\\sealgap\\3part\\export\\test11.wrl ; yes  okay";
        commandWait.SendCommand(command2, OI_CommandWait.E_Both);
        //SealGapAnalyzer sga = new SealGapAnalyzer(oiServer,orb);

	    //ExitIdeas();
	    ShutDown(0);
/*
	    connectIdeasServer("myServer12");
	    //OI_CommandServer commandServer = oiServer.GetCommandServer();
	    commandWait = new OI_CommandWait(orb, oiServer.GetCommandServer());
	    command = "/f o n PK Ford; m fil c:\\sealgap\\3part\\3partParametricSealGap.mf1 okay";
	    commandWait.SendCommand(command, OI_CommandWait.E_Both);
	    command2 = "/f export b VR okay lab z_uid; 6 m fil c:\\sealgap\\3part\\export\\test12.wrl ; yes  okay";
	    commandWait.SendCommand(command2, OI_CommandWait.E_Both);
	    //sga = new SealGapAnalyzer(oiServer, orb);

	    connectIdeasServer("myServer13");
	    //OI_CommandServer commandServer = oiServer.GetCommandServer();
	    commandWait = new OI_CommandWait(orb, oiServer.GetCommandServer());
	    command = "/f o n PK Ford; m fil c:\\sealgap\\3part\\3partParametricSealGap.mf1 okay";
	    commandWait.SendCommand(command, OI_CommandWait.E_Both);
	    command2 = "/f export b VR okay lab z_uid; 6 m fil c:\\sealgap\\3part\\export\\test13.wrl ; yes  okay";
	    commandWait.SendCommand(command2, OI_CommandWait.E_Both);
*/



/*        // disconnect
        //oiServer.Release();
        //orb.shutdown(true);
        commandWait = null;
        //commandServer = null;
        oiServer = null;
        orb = null;
        sga.disconnect();
        sga = null;
        System.gc();

        connectIdeasServer();
        //commandServer = oiServer.GetCommandServer();
        commandWait = new OI_CommandWait(orb, oiServer.GetCommandServer());
        command = "/f o n PK Ford; m fil c:\\sealgap\\3part\\3partParametricSealGap.mf1 okay";
        commandWait.SendCommand(command, OI_CommandWait.E_Both);
        command2 = "/f export b VR okay lab z_uid; 6 m fil c:\\sealgap\\3part\\export\\test2.wrl ; yes  okay";
        commandWait.SendCommand(command2, OI_CommandWait.E_Both);
        sga = new SealGapAnalyzer(oiServer, orb);
        //printCommandErrors(commandWait.GetErrors());         // Get errors
        //printCommandResults(commandWait.GetResults());         // Get results*/
    }


	private void ShutDown(int exitArg) {
		try {
			oiServer.Release(null);
			orb.shutdown(true);
			System.out.println("ORB shutdown");
			orb.destroy();
			System.out.println("ORB destroyed");
		} catch (org.omg.CORBA.SystemException se) {
			System.out.println(se);
			exitArg = 1;
		} catch (Exception e) {
			System.out.println("Non org.omg.CORBA.SystemException raised.");
			exitArg = 1;
		}

		System.exit(exitArg);
	}

	private void ExitIdeas() {
		// System.out.println("SendCommand to exit Ideas...");
		// m_commandServer.SendCommand("! $ / f e n");
		oiServer.Release(null);
		oiServer = null;

		// Give time for the Orbix services to shutdown the	ideas session.
		System.out.println("Sleep for 5 seconds...");

		try {
			Thread.sleep(5000);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Resume...");
	}
	public static void main(String[] args)
	{
		try {
            Ideas10PluginTest test = new Ideas10PluginTest();
            test.connectIdeasServer("myServer2");
			test.testIntfAna();















/*            DomeInit.initializeDOME();
            plg = new Ideas10Plugin("cadlab49", "C:\\3part\\3partParametricSealGap.mf1", "Ford",  false);
            plg.loadModel();


            System.out.println("nonexisting part");
            Ideas10Part part = plg.createPart("newBin", "newPart");
            System.out.println("name: " +part.getPartName());
            System.out.println("bin: " + part.getBinName());
            Ideas10MaterialProperty mat = part.createMaterial("newMat");
            Ideas10RealValueProperty mass = part.createRealValueProperty("mass", Ideas10RealValueProperty.MASS);
            Ideas10RealValueProperty vol = part.createRealValueProperty("volume", Ideas10RealValueProperty.VOLUME);
            Ideas10RealValueProperty surf1 = part.createRealValueProperty("surf1", Ideas10RealValueProperty.OPEN_SURFACE_AREA);
            Ideas10RealValueProperty surf2 = part.createRealValueProperty("surf2", Ideas10RealValueProperty.SOLID_SURFACE_AREA);
            System.out.println("material: " + mat.getName());
            System.out.println("mass: " + mass.getValue(true));
            System.out.println("volume: " + vol.getValue(true));
            System.out.println("open surface: " + surf1.getValue(true));
            System.out.println("solid surface: " + surf2.getValue(true));
            System.out.println("");


            System.out.println("existing part");
            Ideas10Part part2 = plg.createPart("new", "pri_seal bulb hollow");
            System.out.println("name: " + part2.getPartName());
            System.out.println("bin: " + part2.getBinName());
            Ideas10MaterialProperty mat2 = part2.createMaterial("newMat");
            Ideas10RealValueProperty mass2 = part2.createRealValueProperty("mass", Ideas10RealValueProperty.MASS, new RealData(0));
            Ideas10RealValueProperty vol2 = part2.createRealValueProperty("volume", Ideas10RealValueProperty.VOLUME, new RealData(0));
            Ideas10RealValueProperty surf12 = part2.createRealValueProperty("surf1", Ideas10RealValueProperty.OPEN_SURFACE_AREA, new RealData(0));
            Ideas10RealValueProperty surf22 = part2.createRealValueProperty("surf2", Ideas10RealValueProperty.SOLID_SURFACE_AREA, new RealData(0));
            System.out.println("material: " + mat2.getName());
            System.out.println("mass: " + mass2.getValue(true));
            System.out.println("volume: " + vol2.getValue(true));
            System.out.println("open surface: " + surf12.getValue(true));
            System.out.println("solid surface: " + surf22.getValue(true));


            System.out.println("all dimensions: ");
            String[] allDimNames = part2.getAllDimensionNames();
            for (int i = 0; i < allDimNames.length; i++) {
                System.out.println("    "+allDimNames[i]);

            }

            Ideas10Dimension dim = part2.createDimension("pri_seal_height", new RealData(15.0));
            System.out.println(dim.getDimName(true)+"'s old dimension: "+dim.getDimValue(true));
            System.out.println("setting new dimension..");
            dim.setDimValue(0.014, true);
            System.out.println("getting new dimension: "+ dim.getDimValue(true));

            int part2Label = part2.getNumberLabel();
            System.out.println("part label: " + part2Label);

            //plg.createVrmlFile("c:\\test", "primary seal");*/

           System.exit(0);
		} catch (Exception e) {
			System.out.println("ERROR: "+e);
		}
	}
}
