package mit.cadlab.dome3.objectmodel.model;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 25, 2003
 * Time: 10:15:38 AM
 * To change this template use Options | File Templates.
 */
public interface ModelRuntime extends DomeModel
{
	public static final String RUN_STATUS = "run status";
	public static final String STATUS_DONE = "done";
	public static final String STATUS_WAITING_TO_BE_EXECUTED = "waiting to be executed";
	public static final String STATUS_RUNNING = "running";
	public static final String STATUS_PAUSED = "paused";
	public static final String STATUS_WAITING_FOR_CONFIRMATION = "waiting for confirmation";
	public static final String STATUS_ABORTED = "error";
	public static final String STATUS_INCONSISTENT = "inconsistent"; // used to tell clients that they can start solving process

	// startup statuses
	public static final String STATUS_IFACE_PARENT_STARTING = "loading interface parent";
	public static final String STATUS_IFACE_STARTING = "loading interface";
	public static final String STATUS_IFACE_CREATED = "interface loaded";

	public static final String MODEL_KILLED = "model killed"; // used to tell playspace model is killed

	public CompoundId getRuntimeId();

	public Object getItem(String objectId, String methodName, List args);

	public void startModel();

	public void pauseModel();

	public void resumeModel();

	public void stopModel();

	public void startModelAndWait();

	public void setRemoteModel(String parentServerSessionId, CompoundId parentProjectId);

	public String getParentServerSessiondId();

	public CompoundId getRootProjectId();

	public boolean isRemoteModel();

	public boolean isProjectResource();

	public boolean hasAtLeastOneInterfaceLoaded();

	public String getRunStatus();

}
