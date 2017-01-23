// AbstractParameterChangeTracker.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.Names;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;

/**
 * tracks changed parameters
 */
public abstract class AbstractParameterChangeTracker
{

    protected List varSequence; // trace of variable changes
	int nextChangeId;

	/**
	 * @return list of changed parameters
	 */
	public Collection resetTracker() {
		List changesProcessed = varSequence;
	    varSequence = new ArrayList();
	    nextChangeId = 1;
		return changesProcessed;
	}

	public abstract boolean hasChangedParametersLeftToProcess();

	public List getChangedParameters() {
	    return Collections.unmodifiableList(varSequence);
	}

	public abstract void notifyParamChange(Parameter param);

	public String getChangeSequence() {
        return Names.getNames(varSequence);
    }

	public int getNumberChangedParameters() {
		return varSequence.size();
	}
}
