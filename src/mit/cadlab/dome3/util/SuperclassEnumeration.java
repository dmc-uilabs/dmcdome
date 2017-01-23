// SuperclassEnumeration.java
package mit.cadlab.dome3.util;

import java.util.NoSuchElementException;

public class SuperclassEnumeration
{

	protected Class currentClass;

	public SuperclassEnumeration(Class objClass)
	{
		currentClass = objClass;
	}

	public SuperclassEnumeration(Object obj)
	{
		currentClass = obj.getClass();
	}

	public boolean hasMoreSuperclasses()
	{
		return currentClass != Object.class;
	}

	public Class nextSuperclass()
	{
		if (currentClass == Object.class)
			throw new NoSuchElementException("SuperclassEnumeration");
		currentClass = currentClass.getSuperclass();
		return currentClass;
	}

}
