package com.ge.ceed.domeapi.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mit.cadlab.dome3.DomeClientApplication;
import mit.cadlab.dome3.api.DomeConnection;
import mit.cadlab.dome3.api.DomeFolder;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;

import com.ge.ceed.domeapi.Server;
import com.ge.ceed.domeapi.util.StreamGobbler;

/**
 * Web enpoint to allow managing the underlying DOME server.
 * @author citrit
 *
 */
@WebServlet(description = "Check the status of the services and internal DOME Server", urlPatterns = { "/getCEEDStatus" }, loadOnStartup=1)
public class getCEEDStatus extends HttpServlet {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(getCEEDStatus.class);
	private static final String START_INTERNAL_DOME = "startInternalDOME";
	private static final long serialVersionUID = -5791832784979361205L;
	private Properties props = null;
	private static boolean hasDomeStarted = false;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public getCEEDStatus() {
		super();
		
	}

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			DomeClientApplication.DOME_SERVER_MODE = true;
			ServletContext sc = getServletContext();
			props = Configurator.getConfig(new File(sc.getRealPath("/WEB-INF")));
			String msg = spawnDome();
			logger.info(msg);
			this.log(msg);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		this.log(closeDome());
	}

	private String closeDome() {
		boolean startInternalDOME = BooleanUtils.toBoolean((props.getProperty(START_INTERNAL_DOME, "true")));
		String msg = "Internal DOME server startup is disabled.";
		if (!startInternalDOME) {
			logger.info(msg);
		} else {
			String serverName = props.getProperty("dome.server.name", "localhost");
			String serverPort = serverName + ":" + props.getProperty("dome.server.port");
			ServerConnection conn = LoginUtils.login(LoginUtils.ADMIN, "root", serverPort, LoginUtils.encryptPassword("cadlab"));
			conn.shutdown();
			msg = "DOME server successfully shut down.";
			hasDomeStarted = false;
		}
		return msg;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String cmd = req.getParameter("cmd");
		if (cmd.equalsIgnoreCase("isAlive")) {
			if (isDomeAlive()) {
				res.setStatus(HttpServletResponse.SC_OK);
				res.getOutputStream().print("Server is fine, thank you very much.");
			} else {
				res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "DOME is not responding");
				this.log("DOME server is not responding.");
			}
		}
		else if (cmd.equalsIgnoreCase("shutdown")) {
			if (isDomeAlive()) {
				String msg = closeDome();
				res.getOutputStream().print(msg);
				this.log(msg);
			} else {
				res.getOutputStream().print("DOME server is NOT started.");
			}
		}
		else if (cmd.equalsIgnoreCase("startup")) {
			if (!isDomeAlive()) {
				String msg = spawnDome();
				res.getOutputStream().print(msg);
				this.log(msg);
			} else {
				res.getOutputStream().print("DOME server is already started.");
			}
		}
	}

	private boolean isDomeAlive() throws IOException {
		
		boolean ret = false;	
		DomeConnection conn = null;
		
		try {
			Server server = getConnection();
			conn = server.openConnection();
			
			DomeFolder folder = conn.getMyPublicFolder();
			if (folder != null) {
				ret = true;
			}
		}
		catch (RuntimeException ex) {
			logger.info("\n\tisDomeAlive results: \n",ex);
		}
		finally {
			if (conn != null) 
				conn.close();
		}
		
		return ret;
	}

	private Server getConnection() throws IOException {
		String serverName = props.getProperty("dome.server.name");
		Server server = new Server(serverName, 
					props.getProperty("dome.server.port"),
					props.getProperty("dome.server.user"),
					props.getProperty("dome.server.pw"));
		return server;
	}
	
	/**
	 * Spawns the DOME Service based on the values in the properties
	 * @throws IOException
	 */
	private synchronized String spawnDome() throws IOException {
		String msg;
		boolean startInternalDOME = BooleanUtils.toBoolean((props.getProperty("startInternalDOME", "true")));
		msg = "Internal DOME server startup is disabled.";
		if (!startInternalDOME) {
			logger.info(msg);
		} else if (!hasDomeStarted) {
			logger.debug("Starting DOME services...");
			String domeDir = this.getServletContext().getRealPath("/WEB-INF");
			domeDir = props.getProperty("dome.directory", domeDir);
			File fDomeDir = new File(domeDir);
			logger.debug("Finding {}: {}", fDomeDir.getPath(), fDomeDir.exists());
			
			String classpath = getClassPath(domeDir); // + 
			String sysClasspath = System.getProperty("java.class.path");
			if (sysClasspath != null) classpath += File.pathSeparator + sysClasspath;
			
			int dbPort = Integer.parseInt(props.getProperty("dome.server.db.port"));
			int domePort = Integer.parseInt(props.getProperty("dome.server.port"));
			int javaXms = Integer.parseInt(props.getProperty("dome.server.Xms"));
			int javaXmx = Integer.parseInt(props.getProperty("dome.server.Xmx"));
			String domeDbg = props.getProperty("dome.server.debug");
			
			//props.getProperty("java.home") + sep + "bin" + sep + 
			String cmd = "java -Xms" + javaXms + "M -Xmx" + javaXmx + "M" +
					" -DDOMEROOT=" + domeDir + 
					" -Djava.library.path=" + domeDir + "/dlls" + 
					" -cp " + classpath + 
					" mit.cadlab.dome3.network.server.DomeServer " + domePort + " " + dbPort;
			
			logger.debug("Server CMD: \n{}", cmd);
			List <String> cmdArgs = Arrays.asList(cmd.split(" "));
			ProcessBuilder rt = new ProcessBuilder(cmdArgs);
			rt.directory(new File(fDomeDir,  "db"));
			rt.redirectErrorStream(true);
			File logDir = new File(fDomeDir,"logs");
			boolean logDirExists = logDir.exists();
			if (!logDirExists) {
				logDirExists = logDir.mkdirs();
			}
			File logFile=null;
			if (logDirExists) {
				logFile = new File(logDir, "server.log");
				boolean fileCreated = logFile.createNewFile();
				logger.debug("file {} created: {}", logFile.getName(), fileCreated);
			} else {
				logger.warn("Unable to create log dir {}", logDir);
			}
			
			Process p = rt.start();          
			
			// any output?
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), new FileOutputStream(logFile));
			
			outputGobbler.start();
			//errorGobbler.start();
			msg = "Internal DOME Server started.";
			hasDomeStarted = true;
		}
		return msg;
	}

	private static String getClassPath(String domeDir) {
		char pSep = File.pathSeparatorChar;
		StringBuilder classpath = new StringBuilder();
		File folder = new File(domeDir, "lib");
		FilenameFilter filter = FileFilterUtils.suffixFileFilter(".jar", IOCase.INSENSITIVE);
		File[] listOfFiles = folder.listFiles(filter); 

		for (File jarFile : listOfFiles) 
		{
			if (jarFile.isFile()) 
			{
				classpath.append(jarFile.getAbsolutePath()).append(pSep);
			}
		}

		return classpath.toString();
	}
}
