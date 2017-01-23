package com.ge.ceed.domeapi.web;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import com.ge.ceed.domeapi.DomeProxy;
import com.ge.ceed.domeapi.DomeProxyException;

/**
 * Create an ActiveMQ message producer and implement a method
 * that sends a message to the queue.
 *
 */
public class ActiveMQClient implements IMessageQueueClient {
	
	private Destination destination;
	private Connection connection;
	private Session session;
	private MessageProducer m_activemq;
	
	private String user = ActiveMQConnection.DEFAULT_USER;
    private String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String URL_DEFAULT = "tcp://forge2.research.ge.com:61616";
    private String url = "tcp://forge2.research.ge.com:61616";
    private boolean transacted = false;
    
    public ActiveMQClient(String queue) throws IOException, DomeProxyException {
    	Properties props = Configurator.getConfig();
	    try {
	    	url = props.getProperty("queue", URL_DEFAULT);
	    	// Create the connection.
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			connection = connectionFactory.createConnection();
			connection.start();
	       
			// Create the session and the queue.
		    session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
		    destination = session.createQueue(queue);
		   
		    // Create the ActiveMQ client that produces the messages.
		    m_activemq = session.createProducer(destination);
		    
		} catch (JMSException e) {
			e.printStackTrace();
		}
    }
	
	@Override
	public synchronized void sendMessage(Object msg, String queue) {
	
		try {
				/*if (!destination.equals(session.createQueue(queue)))
				{
					destination = session.createQueue("queue");
					m_activemq = session.createProducer(destination);
				}
				*/
				TextMessage message = session.createTextMessage(msg.toString());
				m_activemq.send(destination, message);
				//session.commit();
				
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
    }

	@Override
	public void closeConnection() throws JMSException {
		session.close();
		connection.close();
	}
}
