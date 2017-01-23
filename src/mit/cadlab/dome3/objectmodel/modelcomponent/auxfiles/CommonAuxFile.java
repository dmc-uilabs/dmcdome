// CommonAuxFile.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelcomponent.auxfiles;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.Element;

import java.io.File;

/**
 *  
 */
public class CommonAuxFile extends AbstractAuxFile
{
	public CommonAuxFile(Model m, Id id, String name, File f)
	{
		super(m, id, name, f);
	}

    public CommonAuxFile(Model m,Id id, File f)
	{
		super(m, id, f.getName(), f);
	}



	public CommonAuxFile(Element xmlElement)
	{
		super(xmlElement);
	}




}
