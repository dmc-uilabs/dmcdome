package mit.cadlab.dome3.objectmodel.dataobject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.*;

import mit.cadlab.dome3.network.server.Debug;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServeBigFileByHttp {

	static HttpServer server = null;
	
	ServeBigFileByHttp (File file)
	{
		this(file, 12345);
	}

	ServeBigFileByHttp (File file, int port)
	{
		Debug.trace(Debug.DETAIL, "ServeBigFileByHttp.constructor: " + "Creating listner for file:" + file.getName());
		
		if(server == null) {
			try {
				server = HttpServer.create(new InetSocketAddress(port), 0);
				Debug.trace(Debug.DETAIL, "ServeBigFileByHttp.run: " + "HTTP file transfer server created and listening port " + server.getAddress().getPort());
			} catch (IOException e) {
				Debug.trace(Debug.DETAIL, "ServeBigFileByHttp.run: " + "Unable to create server:");
				e.printStackTrace();
			}
			server.start();
		}
		MyHandler mh = new MyHandler(file);
		server.createContext("/"+file.getName(),mh);
		
	}

	
	
	class MyHandler implements HttpHandler {

		File inputFile;
		
		private boolean finished;
		
		public MyHandler(File f)
		{
			inputFile = f;
			finished = false;
		}
		boolean isFinished()
		{
			return finished;
		}
		public void handle(HttpExchange t)  {

			if (finished)
			{
				
				Debug.trace(Debug.ERROR, "ServeBigFileByHttp.MyHandler.handle: " + "Request for file " + inputFile.getName() + " after handler has finished");
				return;
			}
			try{
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				OutputStream os = null;
				// send file
				//File myFile = new File (FILE_TO_SEND);
				// Get file size
				int fileSize = (int)inputFile.length();

				int blockSize = 1024*100;
				byte [] mybytearray  = new byte [blockSize];
				//byte [] mybytearray  = new byte [69667];
				fis = new FileInputStream(inputFile);
				bis = new BufferedInputStream(fis);

				t.sendResponseHeaders(200, fileSize);
				os = t.getResponseBody();

				int realReadBytes;
				int index = 0;
				int totalSize = 0;
				while  ((realReadBytes = bis.read(mybytearray,0,blockSize)) > 0 ){
					os.write(mybytearray,0,realReadBytes);
					totalSize+=realReadBytes;
					index++;
				}

				os.close();
				bis.close();
				fis.close();
			}
			catch (Exception e)
			{
				Debug.trace(Debug.DETAIL, "ServeBigFileByHttp.MyHandler.handle: " + "Unbale to responce to requirest for file " + inputFile.getName());
				e.printStackTrace();
			}
			Debug.trace(Debug.DETAIL, "ServeBigFileByHttp.MyHandler.handle: " + "Before finished");
			finished = true;
			server.removeContext("/"+inputFile.getName());
		}
	}

}
