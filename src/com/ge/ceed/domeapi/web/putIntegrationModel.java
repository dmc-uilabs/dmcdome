package com.ge.ceed.domeapi.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mit.cadlab.dome3.api.DomeFolder;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;

import com.ge.ceed.domeapi.ConnectorDeSerializer;
import com.ge.ceed.domeapi.DomeEntity;
import com.ge.ceed.domeapi.DomeEntitySerializer;
import com.ge.ceed.domeapi.DomeFolderEntity;
import com.ge.ceed.domeapi.DomeInterfaceEntity;
import com.ge.ceed.domeapi.DomeProjectEntity;
import com.ge.ceed.domeapi.DomeProxy;
import com.ge.ceed.domeapi.DomeProxyException;
import com.ge.ceed.domeapi.IConnector;
import com.ge.ceed.domeapi.IModel;
import com.ge.ceed.domeapi.IModelDeSerializer;
import com.ge.ceed.domeapi.IntegrationModel;
import com.ge.ceed.domeapi.util.MultipartDownloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Servlet implementation class getChildren
 */

@WebServlet(
		description = "Save an IntegrationModel", 
		urlPatterns = { "/putIntegrationModel" } )
public class putIntegrationModel extends HttpServlet {
	
	public static final Logger logger = org.slf4j.LoggerFactory.getLogger(putIntegrationModel.class);

	private static final long serialVersionUID = 1L;

	// need to deserialize DomeInterfaceEntity
	private static Gson gson;
	private DomeProxy domeProxy;	    
	private File resultsDir = null;
	
	static {
		GsonBuilder bldr = new GsonBuilder();
		bldr.registerTypeAdapter(DomeEntity.class, new DomeEntitySerializer());
		bldr.registerTypeAdapter(IConnector.class, new ConnectorDeSerializer());
		bldr.registerTypeAdapter(IModel.class, new IModelDeSerializer());
		gson = bldr.create();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		File defaultTempDir = (File) this.getServletContext().getAttribute(ServletContext.TEMPDIR);
		resultsDir = new File(this.getServletContext().getRealPath("results"));
		resultsDir.mkdirs(); // when running in Eclipse, the results folder doesn't get copied
		try {
			Properties domeAPIServicesProperties = Configurator.getConfig(defaultTempDir);
			domeProxy = new DomeProxy(domeAPIServicesProperties);
		} catch (IOException e) {
			logger.error("Unable to initialize {}: \n{}", this.getClass(), e);
			throw new ServletException("Unable to initialize 'putIntegrationModel'", e);
		} catch (DomeProxyException e) {
			throw new ServletException("Unable to initialize", e);
		}

		if (domeProxy!=null) {
			logger.info("putIntegrationModel endpoint initialized.");
		} else {
			logger.error("putIntegrationModel did not get a DomeProxy!");
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Response r = new Response();
		logger.debug("\nputIntegrationModel...");

	    try {
			String jsonData = request.getParameter("data");
			Map<String, String> field2FileItems = MultipartDownloader.handleFileDownload(request, resultsDir);
			if (null == jsonData) {
				String fileItem = field2FileItems.get("data");
				if (null == fileItem) {
					throw new IllegalArgumentException("Missing parameter 'data'; cannot run.");
				}
				jsonData = fileItem;
			}

			logger.debug("\njson data: \n{}", jsonData);
			
			IntegrationModel integrationModelEntity = gson.fromJson(jsonData, IntegrationModel.class);
			if (null==integrationModelEntity) {
				throw new JsonSyntaxException("Integration model was missing.  Cannot deploy anything.");
			}
			integrationModelEntity.setFolder(new DomeFolderEntity(new Integer[] {},new DomeFolder("CEEDIntegrated",32, null)));
			logger.debug("\ngot integration model: {}", integrationModelEntity);
			IModel model = domeProxy.putIntegrationModel(integrationModelEntity, field2FileItems);
			r.setPkg(model);
			r.setStatus(Response.SUCCESS);
			r.setMsg(null);
			
	    } catch (DomeProxyException  e) {
	    	r.setStatus(Response.ERROR);
	    	r.setMsg("Error sharing integration model: " + e.getMessage());	  
	    	e.printStackTrace();
	    } catch (JsonSyntaxException  e) {
	    	r.setStatus(Response.ERROR);
	    	r.setMsg("Invalid request: " + e.getMessage());	  
	    	e.printStackTrace();
	    } catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.apache.tomcat.util.http.fileupload.FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    String jsonOut = gson.toJson(r);
	    logger.debug("\nreturning: {}", jsonOut);
    	out.println(jsonOut);
    	out.flush();
    	out.close();
	    
	}

}
