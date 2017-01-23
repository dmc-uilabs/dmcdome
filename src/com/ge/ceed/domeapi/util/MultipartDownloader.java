package com.ge.ceed.domeapi.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;


import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

/**
 * Handles downloading file attachments when given an HttpServletRequest.
 * 
 * @author dliscomb
 *
 */
public class MultipartDownloader {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MultipartDownloader.class);

	private static final int MAX_MEM_SIZE = 1024 * 1024; // maximum size that will be stored in memory
	private static final long MAX_FILE_SIZE = -1; // maximum file size; -1 == 'no limit'

	private static ServletFileUpload upload;
	
	private MultipartDownloader() {
		// all static
	}

	static {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(MAX_MEM_SIZE);
		// Location to save data that is larger than maxMemSize.
		// factory.setRepository(new File("c:\\temp"));

		upload = new ServletFileUpload(factory);
		// maximum file size to be uploaded.
		upload.setSizeMax(MAX_FILE_SIZE);
	}
	
	public static boolean isMultipart(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}

	public static Map<String, String> handleFileDownload(HttpServletRequest request, File resultsDir) throws FileUploadException, IOException, IllegalStateException, ServletException {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		logger.debug("\nIsMultiPart: {}", isMultipart);
		Map<String, String> theMap = Collections.emptyMap();
		if (isMultipart) {
			// Parse the request to get file items.
			//@SuppressWarnings("unchecked")
			//List<FileItem> fileItems = (List<FileItem>) upload.parseRequest(request);
			//logger.debug("\nServletFileUpload found {} 'fileItems' ", fileItems.size());
			theMap = new HashMap<String, String>();

			// make a map from the list, keyed on 'Field Name'
			for (Part part : request.getParts()) {
			      String fName = getFileName(part);
			      if (fName != null) {
			    	  File newFile = new File(resultsDir, UUID.randomUUID().toString());
			    	  String fname = part.getName();
			    	  theMap.put(fname, newFile.getAbsolutePath());
			    	  part.write(newFile.getAbsolutePath());
			    	  logger.debug("Found file [{}]: {}", part.getName(), newFile.getAbsolutePath());
			      }
			}
		}
		
		return theMap;
	}
	
	private static String getFileName(Part part) {
		
		String fName = null;
		
		String partHeader = part.getHeader("content-disposition");
		logger.info("Part Header = " + partHeader);
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				fName = cd.substring(cd.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}

		return fName;
	}
}
