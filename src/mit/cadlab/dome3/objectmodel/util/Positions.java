/*
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Oct 28, 2002
 * Time: 1:59:24 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.objectmodel.util;


public class Positions
{

	public static final String ADD = "add";
	public static final String DEL = "remove";
	public static final String UPDATE = "update";

	protected String change = "";
	protected int start = -1;
	protected int howmany = -1;


	public Positions(String changetype, int start, int howmany)
	{
		this.start = start;
		this.howmany = howmany;


		change = changetype;
		int end = getEndIndex();
		if (start >= end)
			throw new IllegalArgumentException("Positions - Bad index parameters");
	}


	public Positions(String changetype, int howmany)
	{//for multiple indiecs, start will remain -1
		this.howmany = howmany;
		change = changetype;
	}


	public int getStartIndex()
	{
		return this.start;
	}


	public int getEndIndex()
	{//note endindex is exclusive
		if ((start == -1) && (howmany == -1)) return -1;
		return this.start + this.howmany;
	}


	public String getChangeType()
	{
		return this.change;
	}
}