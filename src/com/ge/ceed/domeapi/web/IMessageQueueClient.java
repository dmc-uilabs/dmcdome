package com.ge.ceed.domeapi.web;

import javax.jms.JMSException;

/**
 * 
 * @author 502066264
 *
 */
public interface IMessageQueueClient {

	public void sendMessage(Object msg, String queue);
	public void closeConnection() throws JMSException;

}
