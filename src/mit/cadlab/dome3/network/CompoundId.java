package mit.cadlab.dome3.network;

import mit.cadlab.dome3.objectmodel.util.id.Id;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Feb 26, 2003
 * Time: 5:36:19 PM
 *
 * CompoundId provides a mechanism for passing around a hierarchical
 * collections of object identifiers. The top level in the hierarchy
 * is the playspace. Any number of project identifiers can follow. Then
 * come the interface id, the model id, and finally the object id.
 */
public class CompoundId
{
	String idString;

	// static ids
	String playspaceStaticId = null;
	List projectStaticIds = new ArrayList();
	String interfaceStaticId = null;
	String modelStaticId = null;
	String objectStaticId = null;

	// runtime ids
	String playspaceRuntimeId = null;
	List projectRuntimeIds = new ArrayList();
	String interfaceRuntimeId = null;
	String modelRuntimeId = null;
	String objectRuntimeId = null;

	int currentProjectId = -1;    // index of the most recently accessed project id

	public static final String MAJORIDSEPARATOR = "\n";    // separates static and runtime ids
	private static final String IDSEPARATOR = ".";          // separates playspace/project/interface, etc.

	private static final String COMPOUND_PREFIX = "CID" + IDSEPARATOR;
	private static final String STATIC_PREFIX = "STAT" + IDSEPARATOR;
	private static final String RUNTIME_PREFIX = "RUN" + IDSEPARATOR;
	private static final String PLAYSPACE_PREFIX = "PS";
	private static final String PROJECT_PREFIX = "PJ";
	private static final String INTERFACE_PREFIX = "IF";
	private static final String MODEL_PREFIX = "ML";
	private static final String DATAOBJECT_PREFIX = "DO";


	public CompoundId()
	{
		idString = COMPOUND_PREFIX;
	}

	public CompoundId(CompoundId id)
	{
		this(id.toString());
		this.currentProjectId = id.currentProjectId;
	}


	// parse an id string and extract the compound id structure
	public CompoundId(String idString)
	{
		if (idString == null)
			return;

		this.idString = new String(idString);

		// discard the leading identifier
		if (!idString.startsWith(COMPOUND_PREFIX))
			return;
		idString = idString.substring(COMPOUND_PREFIX.length());

		// break into static/runtime ids
		StringTokenizer tokenizer = new StringTokenizer(idString, MAJORIDSEPARATOR);
		String nextMajorId = null;

		// get static id
		if (tokenizer.hasMoreElements())
			nextMajorId = tokenizer.nextToken();
		if (nextMajorId != null && nextMajorId.startsWith(STATIC_PREFIX)) {
			parseStaticCompoundIdString(nextMajorId);
			if (tokenizer.hasMoreElements())
				nextMajorId = tokenizer.nextToken();
		}

		// get runtime id
		if (nextMajorId != null && nextMajorId.startsWith(RUNTIME_PREFIX))
			parseRuntimeCompoundIdString(nextMajorId);
	}

	private void parseStaticCompoundIdString(String idString)
	{
		parseCompoundIdString(idString, false);
	}

	private void parseRuntimeCompoundIdString(String idString)
	{
		parseCompoundIdString(idString, true);
	}

	private void parseCompoundIdString(String idString, boolean isRuntime)
	{
		StringTokenizer tokenizer = new StringTokenizer(idString, IDSEPARATOR);

		// discard the prefix
		tokenizer.nextToken();

		// get the playspace Id
		String nextId = null;
		if (tokenizer.hasMoreElements() &&
		        (nextId = tokenizer.nextToken()).startsWith(PLAYSPACE_PREFIX)) {
			if (isRuntime)
				playspaceRuntimeId = nextId.substring(PLAYSPACE_PREFIX.length());
			else
				playspaceStaticId = nextId.substring(PLAYSPACE_PREFIX.length());
			if (tokenizer.hasMoreElements())
				nextId = tokenizer.nextToken();
		}

		// get project ids
		if (nextId != null && nextId.startsWith(PROJECT_PREFIX)) {
			do {
				if (isRuntime)
					projectRuntimeIds.add(nextId.substring(PROJECT_PREFIX.length()));
				else
					projectStaticIds.add(nextId.substring(PROJECT_PREFIX.length()));
			} while (tokenizer.hasMoreElements() &&
			        (nextId = tokenizer.nextToken()).startsWith(PROJECT_PREFIX));
		}

		// interface id
		if (nextId != null && nextId.startsWith(INTERFACE_PREFIX)) {
			if (isRuntime)
				interfaceRuntimeId = nextId.substring(INTERFACE_PREFIX.length());
			else
				interfaceStaticId = nextId.substring(INTERFACE_PREFIX.length());
			if (tokenizer.hasMoreElements())
				nextId = tokenizer.nextToken();
		}

		// model id
		if (nextId != null && nextId.startsWith(MODEL_PREFIX)) {
			if (isRuntime)
				modelRuntimeId = nextId.substring(MODEL_PREFIX.length());
			else
				modelStaticId = nextId.substring(MODEL_PREFIX.length());
			if (tokenizer.hasMoreElements())
				nextId = tokenizer.nextToken();
		}

		// object id
		if (nextId != null && nextId.startsWith(DATAOBJECT_PREFIX)) {
			if (isRuntime)
				objectRuntimeId = nextId.substring(DATAOBJECT_PREFIX.length());
			else
				objectStaticId = nextId.substring(DATAOBJECT_PREFIX.length());
		}
	}

	// static id set methods
	public void setPlayspaceStaticId(String playspaceId)
	{
		this.playspaceStaticId = (playspaceId != null ? new String(playspaceId) : null);
	}

	public void addParentProjectStaticId(String projectId)
	{
		projectStaticIds.add(0, (projectId != null ? new String(projectId) : null));
		currentProjectId = 0;
	}

	public void addProjectStaticId(String projectId)
	{
		projectStaticIds.add(projectId != null ? new String(projectId) : null);
//		currentProjectId = 0;
	}

	public void setInterfaceStaticId(String interfaceId)
	{
		interfaceRuntimeId = null;
		this.interfaceStaticId = (interfaceId != null ? new String(interfaceId) : null);
	}

	public void setModelStaticId(String modelId)
	{
		modelRuntimeId = null;
		this.modelStaticId = (modelId != null ? new String(modelId) : null);
	}

	public void setDataObjectStaticId(String objectId)
	{
		objectRuntimeId = null;
		this.objectStaticId = (objectId != null ? new String(objectId) : null);
	}

	public void setDataObjectStaticId(Id objectId)
	{
		objectRuntimeId = null;
		this.objectStaticId = (objectId != null ? objectId.getIdString() : null);
	}

	// runtime id set methods
	public void setPlayspaceRuntimeId(String playspaceId)
	{
		this.playspaceRuntimeId = (playspaceId != null ? new String(playspaceId) : null);
	}

	public void addParentProjectRuntimeId(String projectId)
	{
		projectRuntimeIds.add(0, (projectId != null ? new String(projectId) : null));
		currentProjectId = 0;
	}

	public void addProjectRuntimeId(String projectId)
	{
		projectRuntimeIds.add((projectId != null ? new String(projectId) : null));
//		currentProjectId = 0;
	}

	public void setFirstProjectRuntimeId(String projectRuntimeId)
	{
		if (projectRuntimeIds.size() == 0)
			projectRuntimeIds.add(new String());
		projectRuntimeIds.set(0, (projectRuntimeId != null ? new String(projectRuntimeId) : null));
		currentProjectId = 0;
	}

	public void resetFirstProjectStaticId ()
	{
		if (projectStaticIds.size() > 0) {
			projectStaticIds.remove(0);
			currentProjectId--;
		}
	}

	public void setInterfaceRuntimeId(String interfaceId)
	{
		this.interfaceRuntimeId = (interfaceId != null ? new String(interfaceId) : null);
	}

	public void setModelRuntimeId(String modelId)
	{
		this.modelRuntimeId = (modelId != null ? new String(modelId) : null);
	}

	public void setDataObjectRuntimeId(String objectId)
	{
		this.objectRuntimeId = (objectId != null ? new String(objectId) : null);
	}

	public void resetDataObjectRuntimeId()
	{
		this.objectRuntimeId = null;
	}


	public String toString()
	{
		idString = COMPOUND_PREFIX + STATIC_PREFIX;

		if (playspaceStaticId != null)
			idString += IDSEPARATOR + PLAYSPACE_PREFIX + playspaceStaticId;
		for (int i = 0; i < projectStaticIds.size(); i++)
			idString += IDSEPARATOR + PROJECT_PREFIX + (String) projectStaticIds.get(i);
		if (interfaceStaticId != null)
			idString += IDSEPARATOR + INTERFACE_PREFIX + interfaceStaticId;
		if (modelStaticId != null)
			idString += IDSEPARATOR + MODEL_PREFIX + modelStaticId;
		if (objectStaticId != null)
			idString += IDSEPARATOR + DATAOBJECT_PREFIX + objectStaticId;

		idString += MAJORIDSEPARATOR + RUNTIME_PREFIX;

		if (playspaceRuntimeId != null)
			idString += IDSEPARATOR + PLAYSPACE_PREFIX + playspaceRuntimeId;
		for (int i = 0; i < projectRuntimeIds.size(); i++)
			idString += IDSEPARATOR + PROJECT_PREFIX + (String) projectRuntimeIds.get(i);
		if (interfaceRuntimeId != null)
			idString += IDSEPARATOR + INTERFACE_PREFIX + interfaceRuntimeId;
		if (modelRuntimeId != null)
			idString += IDSEPARATOR + MODEL_PREFIX + modelRuntimeId;
		if (objectRuntimeId != null)
			idString += IDSEPARATOR + DATAOBJECT_PREFIX + objectRuntimeId;

		return new String(idString);
	}

	// static id get methods
	public String getPlayspaceStaticId()
	{
		if (playspaceStaticId == null) return null;
		return new String(playspaceStaticId);
	}

	public String getFirstProjectStaticId()
	{
		if (projectStaticIds.size() == 0)
			return null;
		currentProjectId = 0;
		if ((String) projectStaticIds.get(currentProjectId) == null) return null;
		return new String((String) projectStaticIds.get(currentProjectId));
	}

	public String queryFirstProjectStaticId()
	{
		if (projectStaticIds.size() == 0)
			return null;
		if ((String) projectStaticIds.get(0) == null) return null;
		return new String((String) projectStaticIds.get(0));
	}

	public String getCurrentProjectStaticId()
	{
		if (projectStaticIds.isEmpty())
			return null;

		if (currentProjectId == projectStaticIds.size()) {
			currentProjectId = currentProjectId = -1;
			return null;
		}

		if (currentProjectId == -1)
			currentProjectId = currentProjectId = 0;

		if (currentProjectId < projectStaticIds.size())
			return (String) projectStaticIds.get(currentProjectId);
		return null;
	}

	public String getNextProjectStaticId()
	{
		if (projectStaticIds.size() == 0)
			return null;

		if (currentProjectId < 0)
			currentProjectId = 0;

		if (++currentProjectId == projectStaticIds.size()) {
			currentProjectId = -1;
			return null;
		}

		if ((String) projectStaticIds.get(currentProjectId) == null) return null;
		return new String((String) projectStaticIds.get(currentProjectId));
	}

	public String getLastProjectStaticId()
	{
		if (projectStaticIds.size() == 0)
			return null;
		currentProjectId = projectStaticIds.size() - 1;
		if ((String) projectStaticIds.get(currentProjectId) == null) return null;
		return new String((String) projectStaticIds.get(currentProjectId));
	}

	public String getInterfaceStaticId()
	{
		if (interfaceStaticId == null) return null;
		return new String(interfaceStaticId);
	}

	public String getModelStaticId()
	{
		if (modelStaticId == null) return null;
		return new String(modelStaticId);
	}

	public String getDataObjectStaticId()
	{
		if (objectStaticId == null) return null;
		return new String(objectStaticId);
	}

	// runtime id get methods
	public String getPlayspaceRuntimeId()
	{
		if (playspaceRuntimeId == null) return null;
		return new String(playspaceRuntimeId);
	}

	public String getFirstProjectRuntimeId()
	{
		if (projectRuntimeIds.size() == 0)
			return null;
		currentProjectId = 0;
		if ((String) projectRuntimeIds.get(currentProjectId) == null) return null;
		return new String((String) projectRuntimeIds.get(currentProjectId));
	}

	public String getCurrentProjectRuntimeId()
	{
		if (projectRuntimeIds.isEmpty())
			return null;

		if (currentProjectId == projectRuntimeIds.size()) {
			currentProjectId = -1;
			return null;
		}

		if (currentProjectId == -1)
			currentProjectId = currentProjectId = 0;

		if (currentProjectId < projectRuntimeIds.size())
			return (String)projectRuntimeIds.get(currentProjectId);
		return null;
	}

	public String getNextProjectRuntimeId()
	{
		if (projectRuntimeIds.size() == 0)
			return null;

		if (currentProjectId < 0)
			currentProjectId = 0;

		if (++currentProjectId == projectRuntimeIds.size()) {
			currentProjectId = -1;
			return null;
		}

		if ((String) projectRuntimeIds.get(currentProjectId) == null) return null;
		return new String((String) projectRuntimeIds.get(currentProjectId));
	}

	public String getLastProjectRuntimeId()
	{
		if (projectRuntimeIds.size() == 0)
			return null;
		currentProjectId = projectRuntimeIds.size() - 1;
		if ((String) projectRuntimeIds.get(currentProjectId) == null) return null;
		return new String((String) projectRuntimeIds.get(currentProjectId));
	}

	public String getInterfaceRuntimeId()
	{
		if (interfaceRuntimeId == null) return null;
		return new String(interfaceRuntimeId);
	}

	public String getModelRuntimeId()
	{
		if (modelRuntimeId == null) return null;
		return new String(modelRuntimeId);
	}

	public String getDataObjectRuntimeId()
	{
		if (objectRuntimeId == null) return null;
		return new String(objectRuntimeId);
	}


	public void resetStaticInfo()
	{
		playspaceStaticId = null;
		projectStaticIds = new ArrayList();
		interfaceStaticId = null;
		modelStaticId = null;
		objectStaticId = null;
	}

	public void resetRuntimeInfo()
	{
		playspaceRuntimeId = null;
		projectRuntimeIds = new ArrayList();
		interfaceRuntimeId = null;
		modelRuntimeId = null;
		objectRuntimeId = null;
	}

	public boolean equals(Object obj)
	{
		return toString().equals(obj.toString());
	}

	public int hashCode()
	{
		return toString().hashCode();
	}
}
