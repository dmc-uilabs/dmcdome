// Id.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.id;

/**
 * This immutable class represents an Id object.
 */
public class Id
{
	// immutable class

	private String idString;

	public Id(String idString)
	{
		if (idString == null)
			throw new NullPointerException("Id constructor - null idString");
		this.idString = idString;
	}

	public String getIdString()
	{
		return idString;
	}

	public boolean equals(Object obj)
	{
        //change for performance
		if (obj instanceof Id) {
			//boolean res = this.getIdString().equals(((Id) obj).getIdString());
            boolean res = this.idString.equals(((Id) obj).idString);
            return res;
		} else {
			return false;
		}
	}

	public int hashCode()
	{
		return idString.hashCode();
	}

	public String toString()
	{
		return idString;
	}

}