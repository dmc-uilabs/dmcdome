// ProceduralRelation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.relation.equal;

import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface EqualRelation extends ProceduralRelation, Filterable
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Equal Relation", "Equal");

}
