/**
 *  You probably don't *need* to "inherit* from this interface
 *  If you're getting called from c++ directly, it'll find your
 *  evaluate method whether or not your object implements this method
 *  This is so that Evaluators written in Java can work.
 */

package org.goof.qmoo;

import org.goof.rts.AnyMap;

public interface Objective
{
    public void start(AnyMap obj, AnyMap space);

    public boolean evaluate(Individual i);
}
