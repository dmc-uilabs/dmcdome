package mit.cadlab.dome3.objectmodel.util.solving;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: May 19, 2003
 * Time: 12:48:27 PM
 * To change this template use Options | File Templates.
 */
public class NameIdNode {

	// if more types are added, add to setType list as well
	public static final String GENERIC_NODE = "GENERIC";
	public static final String MODEL_NODE = "MODEL";
	public static final String PROJECT_NODE = "PROJECT";

	protected String name;
	protected String id;
	protected String type;

	public NameIdNode(String id, String name)
	{
		this(id, name, GENERIC_NODE);
	}

	public NameIdNode(String id, String name, String nodeType) {
		this.id = id;
		this.name = name;
		setType(nodeType);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		if (MODEL_NODE.equals(type) || PROJECT_NODE.equals(type))
			this.type = type;
		else
			this.type = GENERIC_NODE;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof NameIdNode)
			return id.equals(((NameIdNode)obj).getId());
		else
			return false;
	}

	public String toString()
	{
		return getName()+"("+getId()+" "+getType()+")";
	}

}
