// BuildMappingValuePropagationException.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;

import java.beans.PropertyChangeEvent;

/**
 * Thrown when error occurs in making mappings live in build mode.
 */
public class BuildMappingValuePropagationException extends RuntimeException
{

	public BuildMappingValuePropagationException(Parameter fromParam, Parameter toParam, PropertyChangeEvent event, Throwable cause)
	{
		super(makeMessage(fromParam, toParam, event, cause), cause);
	}

	public static String makeMessage(Parameter fromParam, Parameter toParam, PropertyChangeEvent event, Throwable cause) {
		return "From " + fromParam.getName() + " (" + getScopeName(fromParam) + ")" +
		        " to " + toParam.getName() + " (" + getScopeName(toParam) + ")\n" +
		        "New value of "+event.getPropertyName() + ": " + event.getNewValue() +"\n" +
		        cause.getMessage();
	}

	public static String getScopeName(Parameter p) {
		ModelObjectScope scope = p.getScope();
		String scopeName = p.getScope().getName();
		while (scope instanceof ModelObject) { // not yet at top level
			scope = ((ModelObject)scope).getScope();
			scopeName = scope.getName() + "->" + scopeName;
		}
		return scopeName;
	}

	public String toString() {
		return "BuildMappingValuePropagationException:\n" + getMessage();
	}

}
