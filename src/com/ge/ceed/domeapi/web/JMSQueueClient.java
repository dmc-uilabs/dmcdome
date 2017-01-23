package com.ge.ceed.domeapi.web;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;

/**
 * 
 * @author dliscomb
 *
 */
public class JMSQueueClient implements IMessageQueueClient {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JMSQueueClient.class);
	
	@Resource(mappedName = "jms/CEEDQueueConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(mappedName="jms/Queue")
	private Queue queue;

	
	private Connection connection;
	
	public JMSQueueClient() throws JMSException {
		String userName = null;
		String password = null;
		connection = connectionFactory.createConnection(userName, password);
	}

	@Override
	public void sendMessage(Object msg, String queue) {
		// TODO Auto-generated method stub
		boolean transacted = false;
		int acknowledgeMode = javax.jms.Session.AUTO_ACKNOWLEDGE;
		Session session;
		try {
			session = connection.createSession(transacted, acknowledgeMode);
			Message message = session.createMessage();
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void closeConnection() throws JMSException {
		// TODO Auto-generated method stub
		
	}

}
