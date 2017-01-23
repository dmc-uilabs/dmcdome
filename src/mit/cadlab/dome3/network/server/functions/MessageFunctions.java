package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.RuntimeConstants;
import mit.cadlab.dome3.network.RuntimeUtils;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.util.DSet;
import org.apache.xmlrpc.XmlRpcClientLite;
import org.apache.xmlrpc.XmlRpcException;

import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Name: MessageFunctions
 * User: thorek
 * Date: Mar 31, 2003
 * Time: 11:58:14 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class MessageFunctions
{
	private static int counter = Integer.MIN_VALUE;

	/**
	 * Sends a message with a known structure and known arguments.
	 * @param type
	 * @param sessionId
	 * @param args
	 */
	public static void SendMessage(String type, String sessionId, List args)
	{
		Vector v = RuntimeUtils.listToVector(args);
		SendMessage(type, sessionId, v);
	}

	public static void SendMessage(String type, String sessionId, Vector args)
	{
		XmlRpcClientLite clientConn = DomeServer.getClientConnection(sessionId);
		if (clientConn != null)
			clientConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + type, args, null);
	}

	public static void sendItemStatusChangedMessageSynchronously(String sessionId, Vector args)
	{
		XmlRpcClientLite clientConn = DomeServer.getClientConnection(sessionId);
		if (clientConn != null)
			try {
				clientConn.execute(RuntimeConstants.FUNC_TYPE_RUNTIME + "."
				                        + RuntimeConstants.MESSAGE_ITEM_STATUS_CHANGED, args);
			}
			catch (XmlRpcException e) {
				System.err.println("ERROR sendItemStatusChangedMessageSynchronously for " + args + "\n\t" + e);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	}

    public static void sendIndividualToClient(String sessionId, Vector args)
    {
        XmlRpcClientLite clientConn = DomeServer.getClientConnection(sessionId);
        if (clientConn != null)
            clientConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." +
                                        RuntimeConstants.PASS_INDIVIDUAL_TO_CLIENT, args, null);
    }

    public static void preparePlotForNextGeneration(String sessionId, Vector args)
    {
        XmlRpcClientLite clientConn = DomeServer.getClientConnection(sessionId);
        if (clientConn != null)
            clientConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.PREPARE_PLOT_FOR_NEXT_GENERATION, args, null);
    }

    public static void optimizationAnalysisIsComplete(String sessionId, Vector args)
    {
        XmlRpcClientLite clientConn = DomeServer.getClientConnection(sessionId);
        if (clientConn != null)
            clientConn.executeAsync(RuntimeConstants.FUNC_TYPE_RUNTIME + "." + RuntimeConstants.OPTIMIZATION_ANALYSIS_IS_COMPLETE, args, null);
    }

	public synchronized static Integer getNextChangedIntegerId()
	{
		//todo: !!!!!!!!!!!!!! do this in another way so that the counter doesn't overflow
		return new Integer(counter++);
	}

	public static void errorMessageToClient(CompoundId modelId, String runtimeId, String objectName, String msg) {
		Thread t = new Thread(new ErrorMessageNotificationThread(modelId, runtimeId, objectName, msg));
		t.start();
	}

	static class ErrorMessageNotificationThread implements Runnable {
		CompoundId modelId;
		String runtimeId;
		String modelIdString;
		String objectName;
		String msg;

		public ErrorMessageNotificationThread(CompoundId modelId, String runtimeId, String objectName, String msg) {
			this.modelId = modelId;
			this.runtimeId = runtimeId;
			this.modelIdString = modelId.toString();
			this.objectName = (objectName==null ? "" : objectName);
			this.msg = (msg==null ? "" : msg);
		}

		public void run() {
			Collection subscriptionIdList = DomeServer.getSubscriptionInterfaces(modelId);
			if (subscriptionIdList != null)
			{
				Object[] subInfo;
				for (Iterator iterator = subscriptionIdList.iterator(); iterator.hasNext();) {
					subInfo = (Object[]) iterator.next();
					String sessionId = (String)subInfo[0];
					CompoundId ifaceId = (CompoundId)subInfo[1];
					MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_EXECUTION_ERROR,
					                             sessionId,
					                             Vectors.create(ifaceId.toString(), objectName, msg));
				}
			}
			Collection clientIdList = DomeServer.getResourceClients(runtimeId);
			if (clientIdList == null)
				return;
			if (subscriptionIdList != null)
				clientIdList = DSet.removeSet(clientIdList, subscriptionIdList);
			for (Iterator clientIter = clientIdList.iterator(); clientIter.hasNext();) {
				String clientId = (String) clientIter.next();
				MessageFunctions.SendMessage(RuntimeConstants.MESSAGE_MODEL_EXECUTION_ERROR,
				        clientId,
				        Vectors.create(modelIdString, objectName, msg));
			}
		}
	}

	public static class SendMessageThread implements Runnable {
		String messageType;
		List sessionIds;
		Vector args;

		public SendMessageThread(String messageType, List sessionIds, Vector args)
		{
			this.messageType = RuntimeConstants.FUNC_TYPE_RUNTIME + "." + messageType;
			this.sessionIds = sessionIds;
			this.args = args;
		}

		public void run()
		{
			String sessionId;
			XmlRpcClientLite clientConn;
			for (Iterator iterator = sessionIds.iterator(); iterator.hasNext();) {
				sessionId = (String) iterator.next();
				clientConn = DomeServer.getClientConnection(sessionId);
				if (clientConn != null)
					clientConn.executeAsync(messageType, args, null);
			}
		}
	}

}
