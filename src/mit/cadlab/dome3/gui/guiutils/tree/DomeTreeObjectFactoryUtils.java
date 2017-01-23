// DomeTreeObjectFactoryUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.tree;


public class DomeTreeObjectFactoryUtils
{

	public static void registerListTreeObjects(DomeTreeObjectFactory f)
	{
		f.registerTreeObjectInfo("java.util.AbstractList", "mit.cadlab.dome3.gui.guiutils.tree.ListTreeObject",
		                         "java.util.List");
		f.registerTreeObjectKeyLink("java.util.ArrayList", "java.util.AbstractList");
		f.registerTreeObjectKeyLink("mit.cadlab.dome3.util.DArrayList", "java.util.AbstractList");
		f.registerTreeObjectKeyLink("java.util.Collections$EmptyList", "java.util.AbstractList");
		f.registerTreeObjectKeyLink("java.util.Collections$UnmodifiableList", "java.util.AbstractList");
		f.registerTreeObjectKeyLink("java.util.Collections$UnmodifiableRandomAccessList", "java.util.AbstractList");
	}

}
