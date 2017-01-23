package com.ge.ceed.domeapi.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.ge.ceed.domeapi.DomeInterfaceEntity;
import com.ge.ceed.domeapi.DomeProxy;
import com.ge.ceed.domeapi.DomeProxyException;
import com.ge.ceed.domeapi.IModel;
import com.ge.ceed.domeapi.ModelParam;
import com.ge.ceed.domeapi.ModelParamDeserializer;
import com.ge.ceed.domeapi.ObjectSubclassSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class getModel
 */
@WebServlet(
		description = "Get the model information including inputs and outputs", 
		urlPatterns = { "/getModel" })
public class getModel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(getModel.class); 
	private DomeProxy domeProxy;
	private Gson gson; 
       
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

	    try {
			String json = request.getParameter("data");
			logger.debug("received data: {}", json);
			DomeInterfaceEntity interfaceEntity = gson.fromJson(json, DomeInterfaceEntity.class);
			IModel m = domeProxy.getParams(interfaceEntity);
	    	r.setPkg(m);
	    	r.setStatus(Response.SUCCESS);
	    	r.setMsg(null);
	    } catch (Exception e) {
	    	r.setStatus(Response.ERROR);
	    	r.setMsg("Unable to retrieve Model Information: " + e.getMessage());	  
	    	e.printStackTrace();
	    }
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    String jsonString = gson.toJson(r);
		logger.debug("returning: {}", jsonString);
    	out.println(jsonString);
    	out.flush();
    	out.close();
	    
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ModelParam.class, new ModelParamDeserializer());
		builder.registerTypeAdapter(Object.class, new ObjectSubclassSerializer());
		gson = builder.create();
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
			logger.info("getModel endpoint initialized.");
		} else {
			logger.error("getModel did not get a DomeProxy!");
		}

	}

}
