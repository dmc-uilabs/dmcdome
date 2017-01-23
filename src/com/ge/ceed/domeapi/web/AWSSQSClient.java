package com.ge.ceed.domeapi.web;

import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;

import org.slf4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.ge.ceed.domeapi.DomeProxy;
import com.ge.ceed.domeapi.DomeProxyException;

/**
 * Wraps AmazonSQSClient, passing its startup properties from file "AwsCredentials.properties"
 *
 */
public class AWSSQSClient implements IMessageQueueClient {
	private static final String AWS_CREDENTIALS = "AwsCredentials.properties";
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AWSSQSClient.class);
	private AmazonSQS m_sqs = null;
	private static final String DEFAULT_QUEUE = "https://queue.amazonaws.com/338864163275/CEED_DOME";
	private String m_defQueue;
	
	public AWSSQSClient() throws IOException, DomeProxyException {
		Properties props = Configurator.getConfig();
		
		try {
			m_defQueue = props.getProperty("aws.queue", DEFAULT_QUEUE);
			logger.debug("\n\taws.queue: {}", m_defQueue);
			String proxyhost = props.getProperty("proxy.host");
			if (proxyhost != null) {
				int proxyport = Integer.parseInt(props.getProperty("proxy.port"));
				ClientConfiguration m_cfg = new ClientConfiguration();
				m_cfg.setProxyHost(proxyhost);
		    	m_cfg.setProxyPort(proxyport);				
		    	logger.debug("\n\tproxy.host: {}\n\tproxy.port: {}",proxyhost,proxyport);
		    	m_sqs = new AmazonSQSClient(new PropertiesCredentials(AWSSQSClient.class.getClassLoader().getResourceAsStream(AWS_CREDENTIALS)), m_cfg);
			} else {
				m_sqs = new AmazonSQSClient(new PropertiesCredentials(AWSSQSClient.class.getClassLoader().getResourceAsStream(AWS_CREDENTIALS)));
			}
		}
		catch (NumberFormatException ex) {
			System.out.println("Error: " + ex.toString());
		}
	}
	
	@Override
	public void sendMessage(Object msg, String queue) {
		String internalQueue = queue;
		if (internalQueue == null) {
			internalQueue = m_defQueue;	
		}
		logger.debug("\nSending a message to queue: {}", internalQueue);
		m_sqs.sendMessage(new SendMessageRequest(internalQueue, msg.toString()));
        
	}

	@Override
	public void closeConnection() throws JMSException {
		// TODO Auto-generated method stub
		
	}
	
}
