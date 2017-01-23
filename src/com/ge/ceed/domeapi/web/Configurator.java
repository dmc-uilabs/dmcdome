package com.ge.ceed.domeapi.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

import com.ge.ceed.domeapi.DomeProxy;

/**
 * Provides minimal defaults for common properties
 * @author dliscomb
 *
 */
public final class Configurator {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Configurator.class);

	private static final String RESULTS_DIR = "results.dir";
	private static final String WEBAPP_DIR = "webapp.dir";
	
	private Configurator() {
		// no need to instantiate it
	}
	
	/**
	 * Get configuration properties, and specify the default for "webapp.dir"
	 * @param webAppDir the default for "webapp.dir"
	 * @return Properties object with key/value pairs as defined in "config/config.properties"
	 * @throws IOException
	 */
	public static Properties getConfig(File webAppDir) throws IOException{
		if (webAppDir==null) {
			throw new IllegalArgumentException("webAppDir cannot be null");
		}
		
		Properties sessionProperties = new Properties();
		InputStream configProperties = DomeProxy.class.getClassLoader().getResourceAsStream("config/config.properties");
		if (configProperties!=null) {
			sessionProperties.load(configProperties);
			configProperties.close();
		}
		String webappDirectory = (String) sessionProperties.get(WEBAPP_DIR);
		if (null==webappDirectory || webappDirectory.length() == 0) {
			sessionProperties.put(WEBAPP_DIR, webAppDir.getAbsolutePath());
		} else {
			logger.info("'{}' was overridden with: '{}'", WEBAPP_DIR, webappDirectory);
			
		}
		String resultsDirectoryName = (String) sessionProperties.get(RESULTS_DIR);
		if (null==resultsDirectoryName) {
			sessionProperties.put(RESULTS_DIR, "results");
		} else {
			logger.info("'{}' was overridden with: '{}'", RESULTS_DIR, resultsDirectoryName);
		}
		return sessionProperties;
	}
	
	/**
	 * Get configuration properties, and specify the value of "java.io.tmpdir" as the default for "webapp.dir"
	 * @return Properties object with key/value pairs as defined in "config/config.properties"
	 * @throws IOException
	 */
	public static Properties getConfig() throws IOException {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		return getConfig(tempDir);
	}
}

