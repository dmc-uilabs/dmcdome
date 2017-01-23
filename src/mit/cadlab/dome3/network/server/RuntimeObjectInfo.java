package mit.cadlab.dome3.network.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 12, 2003
 * Time: 3:51:15 PM
 * To change this template use Options | File Templates.
 */
public class RuntimeObjectInfo
{
	private ArrayList staticIds = new ArrayList();         // list of all static ids
	private HashMap objectStaticMap = new HashMap();       // maps object static ids to objects
	private HashMap objectIdMap = new HashMap();           // maps runtime ids to static ids
	private HashMap objectNameMap = new HashMap();         // maps object static ids to names
	private HashMap objectDescriptionMap = new HashMap();  // maps object static ids to descriptions
	private HashMap objectUrlMap = new HashMap();          // maps object static ids to locations
	private HashMap objectStatusMap = new HashMap();       // maps object static ids to status

	private Object resourceLock = new Object();

	public void addObject(String staticId, Object obj)
	{
		synchronized (resourceLock) {
			if (staticId != null && obj != null) {
				if (!staticIds.contains(staticId))
					staticIds.add(staticId);
				objectStaticMap.put(staticId, obj);
			}
		}
	}

	public void addObject(String staticId, String runtimeId, Object obj)
	{
		synchronized (resourceLock) {
			if (obj != null && staticId != null) {
				if (!staticIds.contains(staticId))
					staticIds.add(staticId);
				objectStaticMap.put(staticId, obj);
				if (runtimeId != null) {
					objectIdMap.put(runtimeId, staticId);
					objectIdMap.put(staticId, runtimeId);
				}
			}
		}
	}

	public void addStaticInfo(String staticId, String name, String descr)
	{
		synchronized (resourceLock) {
			addStaticInfo(staticId, name, descr, null);
		}
	}

	public void addStaticInfo(String staticId, String name, String descr, String url)
	{
		synchronized (resourceLock) {
			if (staticId != null) {
				if (!staticIds.contains(staticId))
					staticIds.add(staticId);
				if (name != null)
					objectNameMap.put(staticId, name);
				if (url != null)
					objectUrlMap.put(staticId, url);
				if (descr != null)
					objectDescriptionMap.put(staticId, descr);
			}
		}
	}

	public void removeUsingStaticId(String staticId)
	{
		synchronized (resourceLock) {
			if (staticId != null) {
				staticIds.remove(staticId);
				objectStaticMap.remove(staticId);
				objectNameMap.remove(staticId);
				objectUrlMap.remove(staticId);
				objectDescriptionMap.remove(staticId);
				objectStatusMap.remove(staticId);
				String runtimeId = (String) objectIdMap.get(staticId);
				if (runtimeId != null)
					objectIdMap.remove(runtimeId);
				objectIdMap.remove(staticId);
			}
		}
	}

	public void removeUsingRuntimeId(String runtimeId)
	{
		synchronized (resourceLock) {
			if (runtimeId != null) {
				String staticId = (String) objectIdMap.get(runtimeId);
				removeUsingStaticId(staticId);
			}
		}
	}


	public void setStatus(String staticId, String status)
	{
		synchronized (resourceLock) {
			if (staticId != null && status != null && staticIds.contains(staticId)) {
				objectStatusMap.put(staticId, status);
			}
		}
	}

	public String getStatus(String staticId)
	{
		synchronized (resourceLock) {
			if (staticId != null)
				return (String) objectStatusMap.get(staticId);
			return null;
		}
	}

	public String getName(String staticId)
	{
		synchronized (resourceLock) {
			String name = null;
			if (staticId != null)
				name = (String) objectNameMap.get(staticId);
			return name;
		}
	}

	public String getUrl(String staticId)
	{
		synchronized (resourceLock) {
			String url = null;
			if (staticId != null)
				url = (String) objectUrlMap.get(staticId);
			return url;
		}
	}

	public String getDescription(String staticId)
	{
		synchronized (resourceLock) {
			String descr = null;
			if (staticId != null)
				descr = (String) objectDescriptionMap.get(staticId);
			return descr;
		}
	}

	public void setDescription(String staticId, String description)
	{
		synchronized (resourceLock) {
			if (staticId != null && description != null)
				objectDescriptionMap.put(staticId, description);
		}
	}

	public Object getObjectFromRuntimeId(String runtimeId)
	{
		synchronized (resourceLock) {
			Object obj = null;
			if (runtimeId != null) {
				String staticId = (String) objectIdMap.get(runtimeId);
				obj = objectStaticMap.get(staticId);
			}
			return obj;
		}
	}

	public Object getObjectFromStaticId(String staticId)
	{
		synchronized (resourceLock) {
			Object obj = null;
			if (staticId != null)
				obj = objectStaticMap.get(staticId);
			return obj;
		}
	}

	public Collection getAllStaticIds()
	{
		synchronized (resourceLock) {
			return staticIds;
		}
	}

	public Collection getAllObjects()
	{
		synchronized (resourceLock) {
			return objectStaticMap.values();
		}
	}

	public int size()
	{
		synchronized (resourceLock) {
			return objectStaticMap.size();
		}
	}

	/*
	public Collection getAllObjects ()
	{
		Collection objs = objectStaticMap.values();
		if (objs.size() == 0)
			objs = objectRuntimeMap.values();
		return objs;
	}
	*/
}
