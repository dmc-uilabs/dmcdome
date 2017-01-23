// MessageLogDialogLogHandler.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.util.log.LogHandler;
import mit.cadlab.dome3.util.log.LogLevel;
import mit.cadlab.dome3.util.log.LogRecord;

public class MessageLogDialogLogHandler extends LogHandler
{

	MessageLogDialog msgLog;

	public MessageLogDialogLogHandler(Model m, MessageLogDialog mLog)
	{
		super("Log_" + m.getId());
		if (mLog == null)
			throw new NullPointerException("MessageLogDialogLogHandler - null MessageLogDialog");
		this.msgLog = mLog;
	}

	public MessageLogDialogLogHandler(ModelInterface mi, MessageLogDialog mLog)
	{
		super("Log_" + mi.getId());
		if (mLog == null)
			throw new NullPointerException("MessageLogDialogLogHandler - null MessageLogDialog");
		this.msgLog = mLog;
	}

    /**
     * Added this method to support message logs in analysis tool interfaces.
     * @param ti
     * @param mLog
     */
    public MessageLogDialogLogHandler(ToolInterface ti, MessageLogDialog mLog)
    {
        super("Log_" + ti.getId());
        if (mLog == null)
            throw new NullPointerException("MessageLogDialogLogHandler - null MessageLogDialog");
        msgLog = mLog;
    }

	protected void log(LogRecord record)
	{
		LogLevel level = record.getLevel();
		if (level.equals(LogLevel.ERROR)) {
			msgLog.addError(record.getString());
		} else if (level.equals(LogLevel.WARNING)) {
			msgLog.addWarning(record.getString());
		} else if (level.equals(LogLevel.INFO)) {
			msgLog.addInfo(record.getString());
		} else if (level.equals(LogLevel.DEBUG)) {
			msgLog.addTestInfo(record.getString());
		}
	}

}
