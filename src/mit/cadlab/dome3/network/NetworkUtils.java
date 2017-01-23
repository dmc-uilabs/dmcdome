package mit.cadlab.dome3.network;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.URL;
import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import mit.cadlab.dome3.network.server.Debug;

/**
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Apr 3, 2003
 * Time: 8:40:48 PM
 * To change this template use Options | File Templates.
 */
public class NetworkUtils {
    /**
     * Return the host's ip address.
     * @return
     */
    public static String getIpAddress() {
        String hostAddress = null;

        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostAddress = addr.getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Client does not have an ip address.");
            return null;
        }

        return hostAddress;
    }

    /**
     * Return the hostname.
     * @return
     */
    public static String getHostName() {
        String hostAddress = null;

        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostAddress = addr.getCanonicalHostName();
        } catch (UnknownHostException e) {
            System.out.println("Client does not have a hostname.");
            return null;
        }

        return hostAddress;
    }

    /**
     * parses server:port into servername, ipaddress, port
     * @param serverPort server:port
     * @return a string array of servername, ipaddress, port; servername is null if it can not be found
     * throws IllegalArgumentException if no port specified,
     * host is unknown, or port is not an integer
     */
    public static String[] parseServerPortInfo(String serverPort) {
        String name = null, addr = null, port = null;
        int colIndex = serverPort.indexOf(":");
        if (colIndex == -1)
            throw new IllegalArgumentException("invalid serverPort: " + serverPort);
        String server = serverPort.substring(0, colIndex);
        try {
            InetAddress inetAddr = InetAddress.getByName(server);
            addr = inetAddr.getHostAddress();
            name = inetAddr.getCanonicalHostName();
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("invalid server: " + server);
        }
        port = serverPort.substring(colIndex + 1);
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid port: " + serverPort);
        }
        return new String[]{name, addr, port};
    }


    /**
     * Qing: changed from package com.davidflanagan.examples.net;
     * to_file: local file location, should be valid before getting here.
     * Usage: java HttpClient <URL> [<filename>]
     * e.g. java HttpClient http://www.ibm.com/us abc
     */
    public static void httpDownload(URL _url, String _filename) {
    	NetworkUtils utilsInstance = new NetworkUtils();
    	utilsInstance.httpGetFile(_url, _filename);
    }
    
    
    /**
     * Qing: changed from package com.davidflanagan.examples.net;
     * to_file: local file location, should be valid before getting here.
     * Usage: java HttpClient <URL> [<filename>]
     * e.g. java HttpClient http://www.ibm.com/us abc
     * 
     */
    public void httpGetFile(URL _url, String _filename) {
        try {
            // Get an output stream to write the URL contents to
            OutputStream to_file = new FileOutputStream(_filename);

            // Now use the URL class to parse the user-specified URL into
            // its various parts.
            URL url = _url;
            debug(url);
            String protocol = url.getProtocol();
            if (! (protocol.equals("http") || protocol.equals("file")) ) // Check that we support the protocol
                throw new IllegalArgumentException("Must use 'http:' protocol");

            String host = url.getHost();
            int port = url.getPort();
            if (port == -1) port = 8080; // if no port, use the default HTTP port

            //to get the file on server
            String filename = url.getFile();
            if (filename !=null && filename.startsWith("/file=")) {
            	// strip off the "/file=" -- it's only there to make a valid http URL
            	filename = filename.substring(6);
            }
            
            // Open a network socket connection to the specified host and port
            Socket socket = new Socket(host, port);
            //Socket socket = new Socket("ie-collab9.mit.edu", 8080);

            // Get input and output streams for the socket
            InputStream from_server = socket.getInputStream();
            PrintWriter to_server = new PrintWriter(socket.getOutputStream());

            // Send the HTTP GET command to the Web server, specifying the file
            // This uses an old and very simple version of the HTTP protocol
            debug(filename);
            //here very tricky, bcz the xmlrpc server check keep alive to get file, so this parameter is very important
            to_server.print("GET " + filename + " HTTP/1.1\r\nConnection: Keep-Alive\r\n\r\n");
            to_server.flush(); // Send it right now!

            // Now read the server's response, and write it to the file
            byte[] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = from_server.read(buffer)) != -1)
                to_file.write(buffer, 0, bytes_read);

            // When the server closes the connection, we close our stuff
            socket.close();
            to_file.close();
        } catch (Exception e) { // Report any errors that arise
            e.printStackTrace();
        }
    }

    /**
     * This method replace httpGetRemoteFile.
     * @param serverURL
     * @param fileURL
     * @param localFilename
     */
    public void httpGetRemoteFile1(URL serverURL, URL fileURL, String localFilename) {
        try {
        	String myUrl = fileURL.toString();
        	/* TODO
        	 * if your url can contain weird characters you will want to 
             * encode it here, something like this:
             * myUrl = URLEncoder.encode(myUrl, "UTF-8");
        	 */
        	/* TODO investigate why
        	 * 1. ServerURL is not used.
        	 * 2. This method is invoked multiple times.
        	 */
            String results = doHttpUrlConnectionAction(myUrl, localFilename);       	
        } catch (Exception e) { // Report any errors that arise
            e.printStackTrace();
        }
    }
    
    /*
     * This method copy file from desiredUrl to localFileName
     * 1) If desiredUrl is a remote URL, it implies that the file is big, and the client is already open and wait for server to pull data. 
     *    In this case HTTP protoal is used to read file from the port.
     * 2) If "desiredUrl is a file URI, it implies that the file is already on local machine, implies it is a file smaller than the MaxFileSize.  
     *    In that case, just copy the file locally.
     */
	private String doHttpUrlConnectionAction(String desiredUrl,
			String localFileName) throws Exception {
		URL url = null;
		BufferedReader reader = null;
		int BLOCKSIZE = 1024 * 100;

		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			// create the HttpURLConnection
			url = new URL(desiredUrl);
			HttpURLConnection connection = null;

			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				// uncomment this if you want to write output to this url
				// connection.setDoOutput(true);
				// Give it 15 seconds to respond
				connection.setReadTimeout(15 * 1000);
				connection.connect();
				// Read the output from the server
				reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				byte[] mybytearray = new byte[BLOCKSIZE];
				InputStream is = connection.getInputStream();
				fos = new FileOutputStream(localFileName);
				bos = new BufferedOutputStream(fos);
				current = 0;
				int realByteRead;
				int index = 0;
				int totalSize = 0;
				while ((realByteRead = is.read(mybytearray, current, BLOCKSIZE)) > 0) {
					bos.write(mybytearray, current, realByteRead);
					index++;
					totalSize += realByteRead;
				}
				bos.close();
				return mybytearray.toString();
			} catch (ClassCastException e) {
				// the file is local, performing local copy
				copyLocalFile(url, url, localFileName);
				// The return value is not used.
				return "File copied...";
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			Debug.trace(Debug.ALL, "desiredUrl: " + desiredUrl + "; localFileName =" + localFileName);

			copyLocalFile(url, url, localFileName);
		}
		// The return value is not used.
		return "Exception...";
	}
		    
    /** The method copy the file in fileURL (a local file, to localFileName, that is on the same server as of the first file.
     * @param serverURL Dummy variable.  May need to be eliminated
     * @param fileURL File URL of the source file.
     * @param localFileName  File directory of the target file.
     */
    public void copyLocalFile(URL serverURL, URL fileURL, String localFileName)
    {
    	String sourceFileName = fileURL.getFile().substring(1);
    	System.out.println("sourceFileName: " + sourceFileName + "; localFileName = " + localFileName);
    	File sourceFile = new File(sourceFileName);
    	//File f2 = new File(sourceFileName, true);
    	File localFile = new File(localFileName);
    	InputStream in = null;
    	try {
    		in = new FileInputStream(sourceFile);
    	} catch (FileNotFoundException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	OutputStream out = null;
    	try {
    		out = new FileOutputStream(localFile,false);
    	} catch (FileNotFoundException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}

    	byte[] buf = new byte[1024];  
    	int len; 
    	try {
    		while ((len = in.read(buf)) > 0){

    			out.write(buf, 0, len);

    		}
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}

    	try {
    		in.close();
    		out.close();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

    /**
     * Qing: changed from package com.davidflanagan.examples.net;
     * to_file: local file location, should be valid before getting here.
     * Usage: java HttpClient <URL> [<filename>]
     * e.g. java HttpClient http://www.ibm.com/us abc
     * 
     * D Yang: after long trace, the method call landed in some package that can not support big file transport.
     * 		This method is replaced by httpGetRemoteFile1 that use http protocal to open a port in DOME client,
     * 		and to read the upload file to the port as input stream.  The DOME server side will pull from the port
     *      and read file as input stream from the port. 
     */
/*    public void httpGetRemoteFile(URL serverURL, URL fileURL, String localFilename) {
        try {
        	System.out.println("From NeteworkUtil.java, httpGetRemoteFile");
        	System.out.println("fileURL=" + fileURL);
        	System.out.println("fileURL=" + fileURL);
        	System.out.println("localFilename=" + localFilename);
        	
        	HttpURLConnection connection = (HttpURLConnection)serverURL.openConnection();
        	connection.setRequestMethod("GET");
        	connection.connect();
        	
        	int code = connection.getResponseCode();
        	
            // Get an output stream to write the URL contents to
            OutputStream to_file = new FileOutputStream(localFilename);

            // Now use the URL class to parse the user-specified URL into
            // its various parts.
            debug("serverURL=" + serverURL);
            debug("fileURL=" + fileURL);
            String protocol = serverURL.getProtocol();
            if (! protocol.equals("http") ) // Check that we support the protocol
                throw new IllegalArgumentException("Must use 'http:' protocol");

            String host = serverURL.getHost();
            int port = serverURL.getPort();
            if (port == -1) port = 8080; // if no port, use the default HTTP port

            // Open a network socket connection to the specified host and port
            Socket socket = new Socket(host, port);
            //Socket socket = new Socket("ie-collab9.mit.edu", 8080);

            // Get input and output streams for the socket
            InputStream from_server = socket.getInputStream();
            PrintWriter to_server = new PrintWriter(socket.getOutputStream());

            // Send the HTTP GET command to the Web server, specifying the file to pull
            // This uses an old and very simple version of the HTTP protocol
            
            // here very tricky, bcz the xmlrpc server check keep alive to get file, so this parameter is very important
            //to_server.print("GET " + filename + " HTTP/1.1\r\nConnection: Keep-Alive\r\n\r\n");
            to_server.print("GET " + fileURL + " HTTP/1.1\r\nConnection: Keep-Alive\r\n\r\n");
            to_server.flush(); // Send it right now!

            // Now read the server's response, in byte[] chunks at a time, and write it to localFilename
            byte[] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = from_server.read(buffer)) != -1)
                to_file.write(buffer, 0, bytes_read);

            // When the server closes the connection, we close our stuff
            socket.close();
            to_file.close();
        } catch (Exception e) { // Report any errors that arise
            e.printStackTrace();
        }
    }
*/    
    public static void debug(Object msg) {
        boolean showDebug = false;
        if (showDebug)
            System.out.println(msg);
    }

/*
    public static void main(String[] args) {
        try {
            httpDownload(new URL("http","cadlab27.mit.edu",new Integer(8080).intValue(),"/AuxFiles/d4bdc2f8-b76e-1004-8dc3-fdaf86ce8e90/customGui.jar"), "C:/temp/dometest/AuxFiles/customGui.jar");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
