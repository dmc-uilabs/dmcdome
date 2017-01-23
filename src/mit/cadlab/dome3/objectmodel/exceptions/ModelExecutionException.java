// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.exceptions;

import mit.cadlab.dome3.util.DomeException;

/**
 * This class is used to represent error messages from server to client for which
 * a stack trace is not useful. 
 */
public class ModelExecutionException extends DomeException {

    public ModelExecutionException(String msg) {
        super(msg);
    }

}
