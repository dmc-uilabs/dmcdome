package com.ford.kbedoor.sealanalysis.oi;

/** Interface for handling Server Events.
 *  Look in OI_EventType for the different
 *  types of events.
 *
 * @author Yao Ge
 *         (Modified by Ravi Boppe)
 */
public interface ServerEventListener {
    public void handleEvent(ServerEvent event);
}
