package com.ge.ceed.domeapi.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ge.ceed.domeapi.ModelParam;
import com.ge.ceed.domeapi.ModelParamDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class getModels
 */
@Deprecated
@WebServlet(description = "Get the list of models from the known DOME Servers", urlPatterns = { "/getModels" })
public class getModels extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public getModels() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Response r = new Response();
		Gson gson = new GsonBuilder().registerTypeAdapter(ModelParam.class, new ModelParamDeserializer()).create();

		try {
			//r.pkg = DomeModelUtils.instance().getModelDefs();
		} catch (Exception e) {
			r.setStatus("error");
			r.setMsg("Unable to retrieve Model Information: " + e.getMessage());		
			e.printStackTrace();
		}
	    response.setContentType("application/json");

		PrintWriter out = response.getWriter();
		out.println(gson.toJson(r));
		out.flush();
		out.close();
	}

}
