package mit.cadlab.dome3.plugin.ideas10;

import com.sdrc.openideas.*;
import com.sdrc.openideas.util.OI_App;
import com.sdrc.openideas.util.OI_CommandWait;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPlugin;
import mit.cadlab.dome3.plugin.PluginData;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10Assembly;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10InterferenceAnalysis;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10Part;
import mit.cadlab.dome3.util.Regex;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

public class Ideas10Plugin extends AbstractPlugin
{
    private OI_Server oiServer;
    private OI_ModelFile model;
    private OI_CommandWait commandWait;
    private OI_CommandServer commandServer;
	private OI_WorkbenchServer workbenchServer;
    private ORB orb;
    private IntHolder intHolder;
    private String host;
	private String svrName;
    private String activeSvrName = "myServer2";
    private String fileName;
    private String projectName;
    private boolean isOnDemandSvr;
	private Vector data;
	public boolean waitingToDie = false;

	static Object readFileLock = new Object();
	static List onDemandSvrs = null;
    static List orbParams = null;

    public Ideas10Plugin(String hostname, boolean isUsingOndemandSvr)
    {
        this.host = hostname;
        this.isOnDemandSvr = isUsingOndemandSvr;
        data = new Vector();
    }

    public Ideas10Plugin(String hostname, String fileName, String projectName, boolean isUsingOndemandSvr) {
        this.host = hostname;
        this.isOnDemandSvr = isUsingOndemandSvr;
        data = new Vector();
        this.fileName = fileName;
        this.projectName = projectName;
    }

    private static void loadORBParamsAndOndemandSvrList() {
	    synchronized (readFileLock) {
		    if(orbParams == null && onDemandSvrs == null) {
		        String path = System.getProperty("DOMEROOT") + File.separator + "scripts" + File.separator + "ideasOndemandSvrList.txt";
			    System.out.println("reading I-DEAS configuration file: " + path);
		        try {
			        BufferedReader in = new BufferedReader(new FileReader(path));
		            String line = in.readLine();        //read orb params
		            if (line!= null) {
		                orbParams = Regex.split(Regex.whitespace,line);
			            System.out.print("orb params:");
			            for (int i = 0; i < orbParams.size(); i++) {
				            System.out.print(" " + orbParams.get(i));
			            }
			            System.out.println();
			            line = in.readLine();       //read on demand server names
			            if (line != null) {
				            onDemandSvrs = Regex.split(Regex.whitespace, line);
				            System.out.print("on-demand servers:");
				            for (int i = 0; i < onDemandSvrs.size(); i++) {
					            System.out.print(" " + onDemandSvrs.get(i));
				            }
				            System.out.println();
			            } else {
				            System.err.println("Warning: no on-demand servers found in I-DEAS configuration file");
			            }
		            } else {
			            System.err.println("Warning: nothing read from I-DEAS configuration file");
		            }
		            in.close();
		        } catch (IOException ex) {
			        System.err.println(ex);
		            throw new RuntimeException(ex);
		        } finally {
			        // make sure orbParams and onDemandSvrs are not null
			        if (orbParams == null)
				        orbParams = Collections.EMPTY_LIST;
			        if (onDemandSvrs == null)
				        onDemandSvrs = Collections.EMPTY_LIST;
		        }
		    }
	    }
    }

	private static Object getAvailableOnDemandSvr() {
		synchronized(onDemandSvrs) {
			return onDemandSvrs.isEmpty() ? null : onDemandSvrs.remove(0);
		}
	}

	private static void releaseOnDemandSvr(String name) {
		synchronized (onDemandSvrs) {
			onDemandSvrs.add(0, name);
		}
	}

    public void connectIdeasServer() {
        if (oiServer != null)
            return;
        loadORBParamsAndOndemandSvrList();
        String[] orbArgs = (String[])orbParams.toArray(new String[]{});
        Debug.trace(Debug.ALL, "initiating COBRA orb...");
		orb = ORB.init(orbArgs, null);
	    OI_App serverConnnector = new OI_App("Ideas10Plugin", orb);
	    if (!isOnDemandSvr) {
		    svrName = activeSvrName;
	    } else {
		    Object availableSvr = getAvailableOnDemandSvr();
		    if (availableSvr == null)
			    throw new RuntimeException("no on-demand server available. \n" +
			            "please try again later");
		    else
			    svrName = (String) availableSvr;
	    }

        Debug.trace(Debug.ALL, "connecting to "+ (isOnDemandSvr ? "on-demand" : "specified") +
                               " I-DEAS10 server: " + svrName + " ...");
        try {
            oiServer = serverConnnector.ConnectServer(host, svrName);
            try {
                commandServer = oiServer.GetCommandServer();
            } catch (NullPointerException e) {
                throw new RuntimeException("I-DEAS10 Application did not start properly. \n" +
                        "The error might be related to an I-DEAS license issue. \n" +
                        "Please contact the server to make sure that an I-DEAS license is available and to kill the ondemandlaunch.exe process. \n");
            }
            if (oiServer == null) {
	            String msg = "Unable to connect to the server, exiting.";
                System.err.println();
                throw new RuntimeException(msg);
            } else {
                Debug.trace(Debug.ALL, "connected to I-DEAS10 server successfully");
            }
        } catch (org.omg.CORBA.SystemException e) {
	        e.printStackTrace();
	        throw new RuntimeException("Detected unexpected CORBA exception: " + e.toString());
        } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(e.getMessage());
        }
        intHolder = new IntHolder();
    }

    public void loadModel()
    {
	    try {
		    connectIdeasServer();
		    Debug.trace(Debug.ALL, "loading a model file...");
		    if (fileName != null && projectName != null) {
		        commandWait = new OI_CommandWait(orb, commandServer);
                // clear the workbench by openning up a new blank file
                // otherwise, might get an error if try to open a file that is already loaded
                String command0 = "/f o n ; m fil '' okay";
        		commandWait.SendCommand(command0, OI_CommandWait.E_Both);
		        printCommandErrors(commandWait.GetErrors());         // Get errors
		        printCommandResults(commandWait.GetResults());         // Get results
			    // now open the file
		        String command = "/f o n PK " + projectName + "; m fil " + fileName + " okay";
		        commandWait.SendCommand(command, OI_CommandWait.E_Both);
		        printCommandErrors(commandWait.GetErrors());         // Get errors
		        printCommandResults(commandWait.GetResults());         // Get results
			    // delete lock file
			    String tempName = fileName.substring(0, fileName.length() - 3);
			    File lockFile = new File(tempName + "lck");
			    lockFile.delete();
		    } else {
		        Debug.trace(Debug.ALL, "file name not specified");
		    }
		    model = oiServer.GetActiveModelFile();
	    } catch (SystemException e) {
		    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		    throw new RuntimeException(e.getMessage());
	    } catch (IllegalArgumentException e) {
		    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		    throw new RuntimeException(e.getMessage());
		}
	    Debug.trace(Debug.ALL, "model " + model.GetName(intHolder) + " is loaded.");
    }

    public Ideas10Part createPart(Parameter p, String binName, String partName) {
        if (binName == null || binName.equals("")) {
            Debug.trace(Debug.ALL, "no bin name specified. looking in the Main bin");
            binName = "Main";
        }
        OI_Bin[] bins = model.GetBins(binName, intHolder);
        if (bins.length == 0) {
            throw new RuntimeException("bin with name "
                    + binName + " does not exist.");
        }
        OI_Part[] parts =  bins[0].GetParts(partName, "*", "*", OID_GET_LATEST_VERSION.value, intHolder);
        Ideas10Part _part;
        if (parts.length == 0) { // no existing part with this name, creating new part, but we won't do this for now
/*            Debug.trace(Debug.ALL, "creating a brand new part...");
            _part = new Ideas10Part(model, binName, partName);*/
            throw new RuntimeException("part with name " + partName +
                    " does not exist in bin " + binName);
        }
        else { // existing part
            Debug.trace(Debug.ALL, "retrieving an existing part...");
            OI_Part part = parts[0];
            _part = new Ideas10Part(model, part, this, p);
        }
        data.addElement(_part);
        return _part;
    }

    public OI_Part[] getParts() {
        Debug.trace(Debug.ALL, "retrieving all existing part...");
        return model.GetParts("*", "*", "*", OID_GET_LATEST_VERSION.value, intHolder);
        // (partName, partNumber, revision, version, errorCode) -2 = OID_GET_LATEST_VERSION
    }

    public OI_Part getPart(String name) {
        OI_Part[] parts = model.GetParts(name, "*", "*", OID_GET_LATEST_VERSION.value, intHolder);
        if (parts.length ==0)
            return null;
        return parts[0];
        // (partName, partNumber, revision, version, errorCode) -2 = OID_GET_LATEST_VERSION
    }

	public OI_Assembly getAssembly(String name) {
		OI_Assembly[] asmbs = model.GetAssemblies(name, "*", "*", OID_GET_LATEST_VERSION.value, intHolder);
		if (asmbs.length == 0)
			return null;
		return asmbs[0];
	}

	public void movePartToOrigin(String partName) {
		OI_Part part = getPart(partName);
		OI_Edge[] edges = part.GetPartModel().GetAllEdges(intHolder);
		OI_3DPoint point = null;
		if (edges.length > 0) {
			OI_Vertex[] vertex = edges[0].GetVertices(intHolder);
			if (vertex.length > 0 ) {
				point = vertex[0].GetPoint(intHolder);
			}
		}
		double x, y, z;
		if (point!=null) {
			x = - point.x;
			y = - point.y;
			z = - point.z;
		} else {
			x = 0;
			y = 0;
			z = 0;
		}

	    double[][] coord = part.GetTransform(intHolder);
		coord[3][0] = x;
		coord[3][1] = y;
		coord[3][2] = z;
		int errorCode = part.ApplyTransform(new OI_TransformHolder(coord));
		if (errorCode != 0)
			PrintOIError(errorCode);
	}

	public int getPartNumberLabel(String name) {
		return getPart(name).GetLabel();
	}

	public int getAssemblyNumberLabel(String name) {
		return getAssembly(name).GetLabel();
	}

	public void unloadPart(String name) {
		if (workbenchServer == null)
			workbenchServer = oiServer.GetWorkbenchServer();
		OI_Part part = getPart(name);
		if (workbenchServer.IsOnWorkbench(part)) {
			int err = workbenchServer.PutAway(part);
			if (err != OI_ErrorCodes.C_NO_ERROR &&
			        err != OI_ErrorCodes.C_NOT_ON_WORKBENCH) {
				throw new RuntimeException("Unable to putaway model from work bench \n"
				        + workbenchServer.GetErrorMessage(err));
			}
		}
	}

    // we don't care about creating a brand new assembly yet
    public Ideas10Assembly createAssembly(Parameter p, String binName, String asmbName) {
        OI_Assembly[] asmbs = model.GetAssemblies(asmbName, "*", "*", OID_GET_LATEST_VERSION.value, intHolder);
        if (asmbs.length == 0)
            throw new RuntimeException("assemby with name "
                    + asmbName + " does not exist.");
        Debug.trace(Debug.ALL, "retrieving an existing part...");
        Ideas10Assembly _asmb = new Ideas10Assembly(model, asmbs[0], p, this);
        data.addElement(_asmb);
        return _asmb;
    }

    public Ideas10InterferenceAnalysis createInterferenceAnalysis(Parameter list) {
        Ideas10InterferenceAnalysis intf = new Ideas10InterferenceAnalysis(list, oiServer, orb, this);
        data.addElement(intf);
        return intf;
    }

    public OI_Server getOiServer() {
        return oiServer;
    }

    public ORB getOrb() {
        return orb;
    }

    public void printCommandErrors(int[] piErrors) {
        if (piErrors.length > 0) {
	        String error = "Command Errors:";
            for (int ii = 0; ii < piErrors.length; ii++) {
                error += oiServer.GetErrorMessage(piErrors[ii]);
	            error += "\n";
            }
	        throw new RuntimeException(error);
        }
    }

    public void printCommandResults(String[] commandResults) {
        if (commandResults.length > 0) {
            System.out.println("Results from command:");
            for (int ii = 0; ii < commandResults.length; ii++) {
                System.out.println(commandResults[ii]);
            }
        }
    }

    public void PrintOIError(int errorCode) {
        throw new RuntimeException("OI Error: "
                + oiServer.GetErrorMessage(errorCode));
    }

    public void createModel() {
        loadModel();
    }

	public void unloadModel()
	{
		//unload the current model file by opening a blank file
		String command = "/f o n ; m fil '' okay";
		try {
			commandWait.SendCommand(command, OI_CommandWait.E_Both);
		} catch (SystemException e) {
			System.out.println("Cobra system exception while unloading model " + e);
		}
		printCommandErrors(commandWait.GetErrors());         // Get errors
		printCommandResults(commandWait.GetResults());         // Get results

        //clean up
        commandWait = null;
        commandServer = null;
        //oiServer.Release(null);
		oiServer = null;
        orb = null;
        for (int i = 0; i < data.size(); i++) {
            Object o = data.get(i);
            if (o instanceof Ideas10InterferenceAnalysis) {
                ((Ideas10InterferenceAnalysis) o).disconnect();
            }
        }
		releaseOnDemandSvr(svrName);
        System.gc();
	}

	public void execute(List affectedOutputParams)
	{
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
			if (obj instanceof Ideas10Part) {
				((Ideas10Part) obj).loadNativeData();
			}
		}
		if (waitingToDie)
			return; // do not set outputs if waiting to die
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
				if (obj instanceof Ideas10Part) {
					((Ideas10Part) obj).loadJavaData(affectedOutputParams);
				}
		}

		// though this second consecutive for loop looks akward, it is actually needed to force the IdeasPart objects to
		// update before the IdeasAssembly object is updated

		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
				if (obj instanceof Ideas10Assembly) {
					((Ideas10Assembly) obj).loadJavaData(affectedOutputParams);
				}
		}
		if (waitingToDie)
			return; // do not set outputs if waiting to die
		// same here. need to update everything before doing the analysis
		for (int i = 0; i < data.size(); i++) {
			PluginData obj = (PluginData) data.get(i);
				if (obj instanceof Ideas10InterferenceAnalysis) {
					((Ideas10InterferenceAnalysis) obj).analyze();
				}
		}
	}

	public boolean isModelLoaded()
	{
        return oiServer.GetActiveModelFile().GetName(intHolder).equals(model.GetName(intHolder));
	}

	public void executeBeforeInput()
	{
	}

	public void executeAfterOutput()
	{
	}

	public void deleteModel()
	{        // don't need this in java
	}

	public void fireModelChanged()
	{
	}

	public boolean isAffectedOutputParameter(Parameter p, List affectedOutputParams) {
		return super.isAffectedOutputParameter(p, affectedOutputParams);
	}



/*        public void importNeutralFile() {
        caller.callVoidFunc(MODEL, modelPtr, IMPORT, null);
    }

	public Object createIdeasImportPart(String fileName)
	{
		Object _idImportPart = new IdeasImportPart(caller, modelPtr, fileName);
		data.addElement(_idImportPart);
		return _idImportPart;
	}

	public Object createIdeasImportAssembly(String fileName)
	{
		Object _idImportAssembly = new IdeasImportAssembly(caller, modelPtr, fileName);
		data.addElement(_idImportAssembly);
		return _idImportAssembly;
	}*/

}
