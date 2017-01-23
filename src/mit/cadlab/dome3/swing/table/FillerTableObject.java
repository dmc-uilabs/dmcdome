// FillerTableObject.java
package mit.cadlab.dome3.swing.table;

/**
 * This class is to be used for objects who do not have
 * anything to put in the columns of the table.
 */
public class FillerTableObject extends AbstractTableObject
{

	protected static final String NOTHING = "";

	public FillerTableObject()
	{
		super(null);
	}

	public FillerTableObject(Object data)
	{
		super(data);
	}

	public Object getValueAt(int column)
	{
		return NOTHING;
	}

}
