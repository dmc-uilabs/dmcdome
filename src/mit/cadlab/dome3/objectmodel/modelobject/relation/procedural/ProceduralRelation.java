// ProceduralRelation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.relation.procedural;

import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ProceduralRelation extends Relation, Filterable
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Procedural Relation", "Procedural");
	public static final String INPUT_OUTPUT_VIEW = "Relation Causality View";
	public static final String MODEL_CAUSALITY_VIEW = "Model Causality View";
	public static final List viewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{INPUT_OUTPUT_VIEW, MODEL_CAUSALITY_VIEW}));

	public static final String BODY = "body";
	public static final String DEPENDENCY_INFO = "dependencyInfo";

	public String getBody();

	public void setBody(String body);

	public DependencyInfo getDependencyInfo();

	public void setDependencyInfo(DependencyInfo dInfo);

}
