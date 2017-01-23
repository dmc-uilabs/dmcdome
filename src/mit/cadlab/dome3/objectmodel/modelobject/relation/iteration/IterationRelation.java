// ProceduralRelation.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.modelobject.relation.iteration;

import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;

public interface IterationRelation extends ProceduralRelation, Filterable
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Iteration Relation", "Iteration");


    public static final String WHILE_LOOP = "while loop";
    public static final String DO_WHILE_LOOP = "do while loop";
    public static final String Timestep_LOOP = "time step loop";

    public static final String CONDITION = "condition";
    public static final String INIT_CONDITION = "initial_condition";
    public static final String ITERATIONTYPE = "iteration type";
    public static final String BROADCASTINGTYPE = "broadcasting at end loop or not";
    public static final String ITERATORS = "iterators";
    public static final String ITERATOR = "iterator";

    public String getCondition();

    public void setCondition(String condition);

    public String getInitial_condition();

    public void setInitial_condition(String condition);

    public String getIterationType();

    public void setIterationType(String iterationType);

    public boolean isBroadcasting_eachloop() ;

    public void setBroadcasting_eachloop(boolean broadcasting_eachloop) ;
}
