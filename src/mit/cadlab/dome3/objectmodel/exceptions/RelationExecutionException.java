// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.util.DomeException;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Jan 26, 2004
 * Time: 5:51:28 PM
 * To change this template use Options | File Templates.
 */
public class RelationExecutionException extends DomeException {

    protected String relationName;

    public RelationExecutionException(String relName, String msg, Exception ex) {
        super(msg);
        relationName = relName;
        exception = ex;
	    this.initCause(ex);
    }

    public Exception getException() {
        return exception;
    }

    public String getRelationName() {
        return relationName;
    }

	public String getMessage()
	{
		return "Relation \"" + relationName + "\"\n" + super.getMessage();
	}

	public String getMessageNoRelationName()
	{
		return super.getMessage();
	}

}
