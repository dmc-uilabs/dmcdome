package com.ge.ceed.domeapi.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mit.cadlab.dome3.api.RuntimeParameter;
import mit.cadlab.dome3.api.SolverStateTracker;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import com.ge.ceed.domeapi.DomeProxy;
import com.ge.ceed.domeapi.IModel;
import com.ge.ceed.domeapi.Model;
import com.ge.ceed.domeapi.ModelParam;
import com.ge.ceed.domeapi.ModelParamDeserializer;
import com.ge.ceed.domeapi.util.MultipartDownloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class runModel
 */
@WebServlet(description = "Run the specified model and return the new values", 
urlPatterns = { "/runModel" })
@MultipartConfig
public class runModel extends HttpServlet {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(runModel.class);
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(ModelParam.class, new ModelParamDeserializer()).create();

	private SolverStateTracker tracker = null;
	private File resultsDir = null;
	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Response r = new Response();
		logger.debug("\nrunModel...");
		DomeProxy proxy = null; 
		try {
			String queue = request.getParameter("queue");
			String jsonData = request.getParameter("data");
			Object inf = request.getParameter("infile");

			Map<String, String> field2FileItems = MultipartDownloader.handleFileDownload(request, resultsDir);
			if (null == queue) {
				logger.debug("\n\t Missing 'queue'; checking FileItems...");
				String fileItem = field2FileItems.get("queue");
				if (null == fileItem) {
					throw new IllegalArgumentException("Missing parameter 'queue'; cannot run.");
				}
				queue = fileItem;
			}
			if (null == jsonData) {
				logger.debug("\n\t Missing 'data'; checking FileItems...");
				String fileItem = field2FileItems.get("data");
				if (null == fileItem) {
					throw new IllegalArgumentException("Missing parameter 'data'; cannot run.");
				}
				jsonData = fileItem;
			}
			logger.debug("queue: {}", queue);
			logger.debug("json input: {}", jsonData);
			IModel model = gson.fromJson(jsonData, Model.class);
			// Look for file output params and set their output to the local disk store.
			for (ModelParam modelParam : model.getOutParams()) {
				logger.debug("\nLooking at output parameter: {}", modelParam);
				if (modelParam != null && modelParam.getValue() != null) {
					// Change the path to the local storage and
					if (ModelParam.FILE_TYPE.equals(modelParam.getType())) {
						File newFile = new File(resultsDir, UUID.randomUUID().toString());
						String newFilePath = newFile.getAbsolutePath(); // DOME checks that file.getPath() matches file.getAbsolutePath()  
						modelParam.setValue(newFilePath);
						logger.debug("new modelParam'{}'", modelParam);
					}
				}
			}

			proxy = new DomeProxy(queue);
			
			model = proxy.runModel(model, tracker, queue, field2FileItems);
			
			if (proxy != null && proxy.getAccumulatedMessages() != null ) {
				r.setMsg(proxy.getAccumulatedMessages().toString());
				logger.debug("\nrunModel(): ****START******************************");
				logger.debug("\nrunModel(): picked up these stdouts '{}' " ,proxy.getAccumulatedMessages() );
				logger.debug("\nrunModel(): ****END*********************************");
			} else {
				r.setMsg(null);
				
			}
			//Meena: This is the misleading part.This is set to success even when 
			//Dome Client is not always  successful. Client GUI has to check for 
			// r.setMsg
			r.setStatus(Response.SUCCESS); 
			r.setPkg(model);
			logger.debug("success!");
			//proxy.closeConnections();

		} catch (Exception e) {
			r.setStatus(Response.ERROR);
			logger.error("Run failed: ", e);
			if (proxy != null && proxy.getAccumulatedMessages() != null ) {
				r.setMsg("runModel(): Run failed: " + e.getMessage() + proxy.getAccumulatedMessages().toString());
				logger.debug("runModel(): ****START******************************");
				logger.debug("runModel():  picked up these stdouts '{}' " ,proxy.getAccumulatedMessages() );
				logger.debug("runModel(): ****END*********************************");

			} else {
				r.setMsg("runModel(): Run failed: " + e.getMessage());
			}		
			
			
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(gson.toJson(r));
		out.flush();
		out.close();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		resultsDir = new File(this.getServletContext().getRealPath("results"));
		resultsDir.mkdirs(); // when running in Eclipse, the results folder doesn't get copied
		logger.info("endpoint 'runModel' initialized.");
	}

}
