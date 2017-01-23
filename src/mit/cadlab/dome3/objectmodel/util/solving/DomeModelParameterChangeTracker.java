// DomeModelParameterChangeTracker.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util.solving;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.Names;
import mit.cadlab.dome3.util.DSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;

/**
 * tracks changed parameters
 */
public class DomeModelParameterChangeTracker extends AbstractParameterChangeTracker
{

	// change state = 0 if unchanged
    // otherwise, change state is issued by sequential counter
    protected DSet changedRelationParams;

    protected HashMap relChanges; // changes executed by each relation
	protected List relationSequence; // trace of relations executed

	protected Object loopEntryPoint;//keep a record of loop entry point
    protected boolean exit_loop;


    public DomeModelParameterChangeTracker() {
        resetTracker();
    }

	public Collection resetTracker()
	{
		changedRelationParams = new DSet();
		relationSequence = new ArrayList();
		relChanges = new HashMap();
		return super.resetTracker();
	}

	public boolean hasChangedParametersLeftToProcess() {
		return !changedRelationParams.isEmpty();
	}

	public List getChangedRelationParameters() {
        return Collections.unmodifiableList(changedRelationParams);
    }

	// todo track states for more than just relation inputs
    public void notifyParamChange(Parameter param) {
        if (Parameters.isRelationInput(param) || Parameters.isSubscriptionInput(param)) {
            changedRelationParams.add(param);
        } else
            varSequence.add(param);
    }

	// added so if input is changed in project resource GUI, iModel solving will know about it and be able to
	// mark it as up-to-date
	public void notifyResourceParamChange(Parameter param)
	{
		varSequence.add(param);
	}

    public void notifyParamChangeProcessed(Parameter param) {
        if (Parameters.isRelationInput(param) || Parameters.isSubscriptionInput(param)) {
            changedRelationParams.remove(param);
	        varSequence.add(param);
        }
    }

    public void notifyRelationExecuted(Object rel, List relData) {
        List l = (List) relChanges.get(rel);
        if (l == null) {
            l = new ArrayList();
            relChanges.put(rel, l);
        }
        l.addAll(relData);
        for (int i = 0; i < relData.size(); i++) {
            notifyParamChangeProcessed((Parameter) relData.get(i));
        }
        relationSequence.add(rel);
    }

	public String getRelationSequence() {
        return Names.getNames(relationSequence);
    }

    //Qing added Nov 16th, to get the infor of the last executed relation
    public Object getLastExecutedRelation() {

        if (relationSequence == null || relationSequence.size() == 0) return null;
        return relationSequence.get(relationSequence.size() - 1);
    }

     public Object getLoopEntryPoint() {
        return loopEntryPoint;
    }

    public void setLoopEntryPoint(Object loopEntryPoint) {
        this.loopEntryPoint = loopEntryPoint;
    }

     public boolean isexit_loop() {
        return exit_loop;
    }

    public void setexit_loop(boolean exit_loop) {
        this.exit_loop = exit_loop;
    }
}
