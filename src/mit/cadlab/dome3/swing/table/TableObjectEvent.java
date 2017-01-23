// TableObjectEvent.java
package mit.cadlab.dome3.swing.table;

import java.util.EventObject;

public class TableObjectEvent extends EventObject
{

	public static final int ALL_COLUMNS = -100;
	protected int column = ALL_COLUMNS;

	public TableObjectEvent(Object source)
	{
		super(source);
	}

	public TableObjectEvent(Object source, int column)
	{
		super(source);
		this.column = column;
	}

	public int getColumn()
	{
		return column;
	}

}
