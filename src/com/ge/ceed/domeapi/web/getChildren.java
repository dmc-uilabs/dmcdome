package com.ge.ceed.domeapi.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.ge.ceed.domeapi.DomeEntity;
import com.ge.ceed.domeapi.DomeEntitySerializer;
import com.ge.ceed.domeapi.DomeProxy;
import com.ge.ceed.domeapi.DomeProxyException;
import com.ge.ceed.domeapi.IDomeEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class getChildren
 */

@WebServlet(
		description = "Retrieve all child Dome Entities from the server", 
		urlPatterns = { "/getChildren" } )
public class getChildren extends HttpServlet {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(getChildren.class);
	
	private static final long serialVersionUID = 1L;
	private Gson gson = new GsonBuilder().registerTypeAdapter(DomeEntity.class, new DomeEntitySerializer()).create();

	private DomeProxy domeProxy;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Response r = new Response();
		r.setStatus(Response.ERROR);
		
	    try {
			String json = request.getParameter("data");
			logger.debug("received data: {}", json);
			IDomeEntity entity = gson.fromJson(json, DomeEntity.class);
			r.setPkg(domeProxy.getChildren(entity));
	    	r.setStatus(Response.SUCCESS);
	    	r.setMsg(null);
	    } catch (Throwable e) {
	    	r.setMsg("Invalid request: " + e.getMessage());
	    	logger.error("Invalid request",e);
	    }
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    String jsonOutput = gson.toJson(r);
	    logger.debug("returning: {}", jsonOutput);
    	out.println(jsonOutput);
    	out.flush();
    	out.close();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		File defaultTempDir = (File) this.getServletContext().getAttribute(ServletContext.TEMPDIR);
		try {
			Properties domeAPIServicesProperties = Configurator.getConfig(defaultTempDir);
			domeProxy = new DomeProxy(domeAPIServicesProperties);
		} catch (IOException e) {
			logger.error("Unable to initialize {}: \n{}", this.getClass(), e);
			throw new ServletException("Unable to initialize", e);
		} catch (DomeProxyException e) {
			throw new ServletException("Unable to initialize", e);
		}
		
		if (domeProxy!=null) {
			logger.info("getChildren endpoint initialized.");
		} else {
			logger.error("getChildren did not get a DomeProxy!");
		}

	}

}
