// RelationExecutor.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package test.jython;

import java.util.HashMap;

/**
 * The RelationExecutor interface defines methods to be used to execute different kinds of relations.
 *
 * Example use:
 * HashMap variables = new HashMap();
 * variables.put("a", new RealData(3.5));
 * variables.put("b", new IntegerData(4));
 * variables.put("c", new IntegerData(0));
 * RelationExecutor exec = new RelationExecutorImplementation();
 * exec.loadVariables(variables);
 * exec.setCode("c = a*b");
 * exec.run();
 * DomeInteger c = (DomeInteger)exec.getVariable("c");
 * // could load another set of variables and run the relation again
 */
public interface RelationExecutor {

    /**
     * Clears the variable namespace in the relation.
     */
    public void clearVariables();

    /**
     * Loads the values of the variables into the namespace
     * Values of old variables with the same name are overridden
     * The variable namespace is not cleared during this method
     *
     * IMPORTANT: The RelationExecutor guarantees that these variable values
     * are never changed by any action of this class.
     */
    public void loadVariables(HashMap variables);

    /**
     * Sets the code for this relation (optional)
     */
    public void setCode(String code);

    /**
     * Runs the relation
     */
    public void run();

    /**
     * Returns the specified variable from the relation
     * Used to retrieve output variables
     */
    public Object getVariable(String name);

}
