package com.ge.ceed.domeapi.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

/**
 * Handles downloading file attachments when given an HttpServletRequest.
 * 
 * @author dliscomb
 *
 */
public class FileDownloader {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FileDownloader.class);

	private static final int MAX_MEM_SIZE = 1024 * 1024; // maximum size that will be stored in memory
	private static final long MAX_FILE_SIZE = -1; // maximum file size; -1 == 'no limit'

	private static ServletFileUpload upload;
	
	private FileDownloader() {
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

	public static Map<String, FileItem> handleFileDownload(HttpServletRequest request) throws FileUploadException {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		logger.debug("\nIsMultiPart: {}", isMultipart);
		Map<String, FileItem> theMap = Collections.emptyMap();
		if (isMultipart) {
			// Parse the request to get file items.
			@SuppressWarnings("unchecked")
			List<FileItem> fileItems = (List<FileItem>) upload.parseRequest(request);
			logger.debug("\nServletFileUpload found {} 'fileItems' ", fileItems.size());
			theMap = new HashMap<String, FileItem>(fileItems.size());

			// make a map from the list, keyed on 'Field Name'
			Map<String, FileItem> field2FileItem = new HashMap<String, FileItem>();
			for (FileItem fileItem : fileItems) {
				field2FileItem.put(fileItem.getFieldName(), fileItem);
			}
			theMap = field2FileItem;
		}

		return theMap;

	}

}
