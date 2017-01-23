package com.ge.dome.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.data.xy.XYSeriesCollection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A servlet for plotting charts for the Wind Model Use Case
 * <p/>
 * Gets the data from the param "OutParamsData" in a file.
 * The file contains Dome model results as a JSON object
 * Specific Json objects - "Grid Data" and "Turbine Data" is extracted 
 * to get the XY data for the plots
 * JFreeCharts used for plotting
 * <p/>
 */

public class TwoColumnCharts extends HttpServlet {
	private static final long serialVersionUID = 5L;
    private static final int BUFSIZE = 4096;
    private String strFilePath;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TwoColumnCharts() {
        super();

        // TODO Auto-generated constructor stub
    }


	private static String readFile(String fileName) throws IOException {
	    BufferedReader bufrdr = new BufferedReader(new FileReader(fileName));
	    StringBuilder sb = new StringBuilder();

	    try {
		        String line = bufrdr.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = bufrdr.readLine();
	        }

	    }
	    catch(IOException ex){
	        System.out.println (ex.toString());
	        System.out.println("Could not find file " + fileName);
	    } finally {
	        bufrdr.close();
	    }

	    return sb.toString();
	}
	/*
	 * json as input contains string with interface, inparams, outparams ...
	 */
	private static  List<ArrayList> deserialize(String json, String strParam){
		  if (json == null || json.length() < 1) {
		    return null;
		  }
		  JsonParser parser = new JsonParser();
		  JsonObject jsonObject=(JsonObject)parser.parse(json);
		  if (jsonObject.isJsonObject() == false)
		  {
			  System.out.println("We are expecting a Json Object as input with information for interfaces, inparmas, outParams");
			  return null;

		  }

		  /*
		   * Interested in outParams only
		   * This will have JsonObjects and values can be arrays
		   */
		  JsonElement outParams = (JsonElement)jsonObject.get("outParams");
		  if (outParams != null) {
		        System.out.println(outParams);
 				JsonParser parserA = new JsonParser();
				JsonObject jsonObjectA=(JsonObject)parserA.parse(outParams.toString());
				System.out.println("Object in Outparams=" + jsonObjectA.toString());

				/*
				 * Get the params of interest here
				*/
				JsonObject gD= (JsonObject)jsonObjectA.get("Grid Data");
				JsonObject tD= (JsonObject)jsonObjectA.get("Turbine Data ");
				JsonArray jarrayGD = gD.get("value").getAsJsonArray();
			    JsonArray jarrayTD = tD.get("value").getAsJsonArray();


			    Gson gson = new Gson();
		        System.out.println("Done");
		        if ( strParam.equalsIgnoreCase("gd")) {
		        	/* this is for Grid Data*/
					List<ArrayList> mListGD = gson.fromJson(jarrayGD.toString(), ArrayList.class);
					System.out.println(mListGD.size() + "," + mListGD.get(1).size());
		        	return mListGD;
		        } else {
					List<ArrayList> mListTD = gson.fromJson(jarrayTD.toString(), ArrayList.class);
					System.out.println(mListTD.size() + "," + mListTD.get(1).size());
		        	return mListTD;
		        }
		  }
		  return null;
	}

	public File createFileWithXYdata(String name, List<ArrayList> pData,int colIndex, String sTitle, String xLabel,String yLabel)
	{
		/* Keep only 2 chars from title*/
		String sT = sTitle.substring(0,2);
		String sT1 = sT+"_"+ yLabel;
		sT1 = sT1.replace('.', '_');


		File fn = new File(sT1+".png");
		String filePath = this.strFilePath  + fn;
		File file1 = new File(filePath);
	    System.out.println(file1);

	    try {
		   	org.jfree.data.xy.XYSeries xy_data = new org.jfree.data.xy.XYSeries(name);

			for (ListIterator<ArrayList> iter = pData.listIterator(); iter.hasNext(); ) {
				ArrayList<Double> aList = iter.next();
				/* extract the y component from the array, x is the last column*/
				double xVal = aList.get(8); // index is 0 based, we need col 9
				double yVal = aList.get(colIndex-1);
				xy_data.add(xVal, yVal);
			}
	        /*
	         * Add all XYSeries to XYSeriesCollection. We have one series in each graph
	         */
	        XYSeriesCollection series= new XYSeriesCollection();
	        series.addSeries(xy_data);
	        /*
	         * Prepare file with xy data
	         */
	        boolean showLegend = false;
	        boolean showTooltips = true;
	        int width=400;  // Width of the image
	        int height=240; // Height of the image
/* Meena Temp
	        JFreeChart XYLineChart=ChartFactory.createXYLineChart(sT1,xLabel,yLabel,series,PlotOrientation.VERTICAL,showLegend,showTooltips,false);
	        ChartUtilities.saveChartAsPNG(file1, XYLineChart, width, height);// Write the data to the output stream
*/
	        
	    } catch (Exception e) {
                System.err.println(e.toString()); /* Throw exceptions to log files */
        }



        return file1;

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			             HttpServletResponse response)
	               throws IOException {

     }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			/*
			 * Get the servlet context
			 * the file contains Dome model results as a JSON object
			 * File is under web application folder
			 * File Name is passed as a request parameter
			 */

	        OutputStream out = response.getOutputStream(); /* Get the output stream from the response object */
        	ServletContext context  = getServletConfig().getServletContext();
        	String mimetype = context.getMimeType(strFilePath);
        	strFilePath = context.getRealPath("")  + File.separator;


	        try {
	            HttpSession session = request.getSession(true);

	        	String strFile = (String) request.getParameter("OutParamsData");
	           	strFile = strFilePath+ strFile ;
	           	/*
	        	 * Read the file which has ModelParams in Json Format
	        	 */
				String json_text = readFile(strFile);

	        	/*
	        	 * Deseralize the Json object and get the chart data
	        	 * gD and tD: This Matrix has  number of columns as 9. Column 9 contains x-axis data for plots
	        	 *     We are ploting 8 xy plots
	        	 */

				List<ArrayList> mListGD = deserialize(json_text,"gd");
				List<ArrayList> mListTD = deserialize(json_text,"td");

	           /*
	            * Charts for Grid Data and Turbine Data
	            * yLabels for the plots are outputs from the Dome Model
				* But for now we are hard coding
	           */
				File gFile1 = createFileWithXYdata("Grid1", mListGD,1, "Grid Data", "Time pu","Vabc_B120(pu)") ;
				File gFile2 = createFileWithXYdata("Grid2", mListGD,2, "Grid Data", "Time pu","Vabc_B25(pu)") ;
				File gFile3 = createFileWithXYdata("Grid3", mListGD,3, "Grid Data", "Time pu","Vabc_B575(pu)") ;
				File gFile4 = createFileWithXYdata("Grid4", mListGD,4, "Grid Data", "Time pu","P_B25(MW)") ;
				File gFile5 = createFileWithXYdata("Grid5", mListGD,5, "Grid Data", "Time pu","Q_B25(Mvar)") ;
				File gFile6 = createFileWithXYdata("Grid6", mListGD,6, "Grid Data", "Time pu","V_Plant 2.3kV pos. seq(pu)") ;
				File gFile7 = createFileWithXYdata("Grid7", mListGD,7, "Grid Data", "Time pu","I Plant pos. seq (pu div 2 MVA)") ;
				File gFile8 = createFileWithXYdata("Grid8", mListGD,8, "Grid Data", "Time pu","Motor speed (pu)") ;

				File tFile1 = createFileWithXYdata("Turbine1", mListTD,1, "Turbine Data", "Time pu","Pos. seq V1_B575(pu)") ;
				File tFile2 = createFileWithXYdata("Turbine2", mListTD,2, "Turbine Data", "Time pu","Pos. seq I1_B575(pu)") ;
				File tFile3 = createFileWithXYdata("Turbine3", mListTD,3, "Turbine Data", "Time pu","Generated P (MW)") ;
				File tFile4 = createFileWithXYdata("Turbine4", mListTD,4, "Turbine Data", "Time pu","Generated Q(Mvar)") ;
				File tFile5 = createFileWithXYdata("Turbine5", mListTD,5, "Turbine Data", "Time pu","Vdc(V)") ;
				File tFile6 = createFileWithXYdata("Turbine6", mListTD,6, "Turbine Data", "Time pu","Speed(pu)") ;
				File tFile7 = createFileWithXYdata("Turbine7", mListTD,7, "Turbine Data", "Time pu","Wind Speed (m per sec)") ;
				File tFile8 = createFileWithXYdata("Turbine8", mListTD,8, "Turbine Data", "Time pu","pitch angle (deg)") ;

	            String myData[] = new String[44];

	            myData[0] = "<HTML>";
	            myData[1] = "<head>" + "<title>" +"Wind DFIG example" + "</title>" + "</head>";
	            myData[2] = "<BODY>";
	            myData[3] = "<CENTER>";
	            myData[4] ="<table BORDER=1 CELLPADDING=0 CELLSPACING=0 WIDTH=50% >";
	            myData[5] = "<tr>";
	            myData[6] = "<th>"+ "Grid"+ "</th>";
	            myData[7] = "<th>"+ "WindTurbine"+ "</th>";
	            myData[8] = "</tr>";

	            myData[9] = "<tr>";
	            myData[10] = "<td><img src=" + '"' + gFile1.getName() +'"' + "></img>" + "</td>";
	            myData[11] = "<td><img src=" + '"' + tFile1.getName() +'"' + "></img>" + "</td>";
	            myData[12] = "</tr>";

	            myData[13] = "<tr>";
	            myData[14] = "<td><img src=" + '"' + gFile2.getName() +'"' + "></img>" + "</td>";
	            myData[15] = "<td><img src=" + '"' + tFile2.getName() +'"' + "></img>" + "</td>";
	            myData[16] = "</tr>";

	            myData[17] = "<tr>";
	            myData[18] = "<td><img src=" + '"' + gFile3.getName() +'"' + "></img>" + "</td>";
	            myData[19] = "<td><img src=" + '"' + tFile3.getName() +'"' + "></img>" + "</td>";
	            myData[20] = "</tr>";

	            myData[21] = "<tr>";
	            myData[22] = "<td><img src=" + '"' + gFile4.getName() +'"' + "></img>" + "</td>";
	            myData[23] = "<td><img src=" + '"' + tFile4.getName() +'"' + "></img>" + "</td>";
	            myData[24] = "</tr>";

	            myData[25] = "<tr>";
	            myData[26] = "<td><img src=" + '"' + gFile5.getName() +'"' + "></img>" + "</td>";
	            myData[27] = "<td><img src=" + '"' + tFile5.getName() +'"' + "></img>" + "</td>";
	            myData[28] = "</tr>";

	            myData[29] = "<tr>";
	            myData[30] = "<td><img src=" + '"' + gFile6.getName() +'"' + "></img>" + "</td>";
	            myData[31] = "<td><img src=" + '"' + tFile6.getName() +'"' + "></img>" + "</td>";
	            myData[32] = "</tr>";

	            myData[33] = "<tr>";
	            myData[34] = "<td><img src=" + '"' + gFile7.getName() +'"' + "></img>" + "</td>";
	            myData[35] = "<td><img src=" + '"' + tFile7.getName() +'"' + "></img>" + "</td>";
	            myData[36] = "</tr>";

	            myData[37] = "<tr>";
	            myData[38] = "<td><img src=" + '"' + gFile8.getName() +'"' + "></img>" + "</td>";
	            myData[39] = "<td><img src=" + '"' + tFile8.getName() +'"' + "></img>" + "</td>";
	            myData[40] = "</tr>";

	            myData[41] = "</table>";
	            myData[42] = "</CENTER";
	            myData[43] = "</BODY></HTML>";

	            for (int i=0; i<myData.length;i++) {
	            	String str1 = myData[i];
	                 out.write(str1.getBytes(Charset.forName("UTF-8")));
	            }

	            response.setContentType("text/html"); // Set the HTTP Response Type
	        }
	        catch (Exception e) {
	                System.err.println(e.toString()); /* Throw exceptions to log files */
	        }
	        finally {
	                out.close();/* Close the output stream */
	        }

		//End
	}

}

