// IdGenerator.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.id;

import mit.cadlab.dome3.util.xml.XMLSupport;

/**
 * The IdGenerator interface defines methods that IdGenerators should implement.
 */
public interface IdGenerator extends XMLSupport
{

	public Id nextId();

	public Id nextId(String info);


}
