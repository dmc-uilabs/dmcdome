package com.ford.kbedoor.sealanalysis.oi;

/**
 * ServerEvent class.
 * Refer OI_EventType for details.
 * @author Yao Ge
 *         (Modified by Ravi Boppe)
 */
public class ServerEvent {

    public final static int ServerExit = 0;
    public final static int ModelSwitch = 1;
    public final static int PrintMessage = 2;
    public final static int PrintError = 3;
    public final static int SendCommand = 4;
    public final static int CommandComplete = 5;
    public final static int TypeCount = 6;

    protected int _type;
    protected Object _data;

    public ServerEvent(int type, Object data) {
        _type = type;
        _data = data;
    }

    public int getType() {
        return _type;
    }

    public Object getData() {
        return _data;
    }
}
