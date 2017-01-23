package mit.cadlab.dome3.plugin.ideas10.dataobject.dataobjectsupport;

import com.sdrc.openideas.OI_Server;
import com.sdrc.openideas.util.OI_CommandWait;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Plugin;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10Assembly;
import mit.cadlab.dome3.plugin.ideas10.dataobject.Ideas10Part;
import org.omg.CORBA.ORB;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Ideas10VrmlFile extends AbstractPluginData
{
	// todo: move this to be an external environment variable
	private static boolean compilingForFord = false; // set to true if compiling for Ford
	// general VRML export command makes sure only geometry is exported in VRML
	// Ford-specific version of VRML export command does not set geometry only option
	// because their version of I-DEAS already fixes the VRML export to export geometry only --
	// that option is fixed and disabled, therefore trying to set it creates an error condition

	public static final String CLASS = "IdeasVrmlFile";

    private boolean isResult;
    private Ideas10Plugin plg;
    private Ideas10Part part;
    private Ideas10Assembly asmb;
    private boolean isPart;
    private FileData f;

	public Ideas10VrmlFile(Ideas10Plugin plg, Ideas10Part part, Parameter p, DomeFile file)
	{
		this.parameter = p;
        this.part = part;
        this.plg = plg;
		if (p == null)
			this.f = new FileData();
		else
			this.f = (FileData) file;
		isResult = true;
        isPart = true;
	}

    public Ideas10VrmlFile(Ideas10Plugin plg, Ideas10Assembly asmb, Parameter p, DomeFile file) {
	    this.parameter = p;
        this.asmb = asmb;
        this.plg = plg;
	    if (p == null)
		    this.f = new FileData();
	    else
		    this.f = (FileData) file;
        isResult = true;
        isPart = false;
    }

    public Ideas10VrmlFile(Ideas10Plugin plg, Parameter p, DomeFile file) {
	    this.parameter = p;
        this.plg = plg;
	    if(p ==null)
	        this.f = new FileData();
	    else
            this.f = (FileData) file;
        isResult = true;
        isPart = true;
    }

    public void loadJavaData() {
	    int partLabel;
        // don't need to copy and move part anymore, since setting the vrml output options is enough to get the focus on the part
        /*String copyPartName;
	    if (isPart) {
		    copyPartName = copyPart();
		    plg.movePartToOrigin(copyPartName);
		    partLabel = plg.getPartNumberLabel(copyPartName);
	    } else {
		    // not moving assmebly for now
		    copyPartName = asmb.getAssemblyName();
		    partLabel = plg.getAssemblyNumberLabel(copyPartName);
	    }*/
        partLabel = isPart ? part.getNumberLabel() : asmb.getNumberLabel();
	    String command = "/f export b VR okay lab z_uid; " + partLabel + " VT FN IO REF OF Okay F '" + f.getFilePath() + "' ; yes  okay";
	    if (compilingForFord)
	        command = "/f export b VR okay lab z_uid; " + partLabel + " m fil '" + f.getFilePath() + "' ; yes  okay";
	    OI_Server server = plg.getOiServer();
        ORB orb = plg.getOrb();
        OI_CommandWait commandWait = new OI_CommandWait(orb, server.GetCommandServer());
        commandWait.SendCommand(command, OI_CommandWait.E_Both);
	    //plg.unloadPart(copyPartName);
        plg.printCommandErrors(commandWait.GetErrors());         // Get errors
        plg.printCommandResults(commandWait.GetResults());         // Get results
        f.notifyFileChanged();
    }

	private String copyPart() {
		Date currDate = (Calendar.getInstance()).getTime();
		String dateTime = (DateFormat.getInstance()).format(currDate);
		String partNum = System.getProperty("user.name");
		dateTime = dateTime.replace(':', '-');
		dateTime = dateTime.replace(';', '-');
		dateTime = dateTime.replace(',', '-');
		dateTime = dateTime.replace('"', '-');
		dateTime = dateTime.replace(' ', '-');
		String newPartName = part.getPartName()+"-vrml-"+dateTime;
		return part.copyPart(newPartName, partNum);
	}

	private String copyAssembly() {
		Date currDate = (Calendar.getInstance()).getTime();
		String dateTime = (DateFormat.getInstance()).format(currDate);
		String asmbNum = System.getProperty("user.name");
		dateTime = dateTime.replace(':', '-');
		dateTime = dateTime.replace(';', '-');
		dateTime = dateTime.replace(',', '-');
		dateTime = dateTime.replace('"', '-');
		dateTime = dateTime.replace(' ', '-');
		String newAsmbName = asmb.getAssemblyName() + "-vrml-" + dateTime;
		return asmb.copyAssembly(newAsmbName, asmbNum);
	}

    public void loadData(String name) {
        // don't need to copy and move part anymore, since setting the vrml output options is enough to get the focus on the part        
	    /*String copyPartName = copyPart();
	    plg.movePartToOrigin(copyPartName);
	    int partLabel = plg.getPartNumberLabel(copyPartName);*/
        int partLabel = plg.getPartNumberLabel(name);
	    String command = "/f export b VR okay lab z_uid; " + partLabel + " VT FN IO REF OF Okay F '" + f.getFilePath() + "' ; yes  okay";
	    if (compilingForFord)
	        command = "/f export b VR okay lab z_uid; " + partLabel + " m fil '" + f.getFilePath() + "' ; yes  okay";

        OI_Server server = plg.getOiServer();
        ORB orb = plg.getOrb();

        OI_CommandWait commandWait = new OI_CommandWait(orb, server.GetCommandServer());
        commandWait.SendCommand(command, OI_CommandWait.E_Both);
	    //plg.unloadPart(copyPartName);
        plg.printCommandErrors(commandWait.GetErrors());         // Get errors
        plg.printCommandResults(commandWait.GetResults());         // Get results
        f.notifyFileChanged();
    }

	public void destroy()
	{
	}

	public void resetObjectPointer() {
	}

	public boolean getIsResult()
	{
		return isResult;
	}
}
