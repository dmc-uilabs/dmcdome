package mit.cadlab.dome3.objectmodel.dataobject;

import java.net.URL;

public class URLwithDestFileName{

	/**
	 * @param args
	 */
	private URL fileURL;
	public URL getFileURL() {
		return fileURL;
	}
	public void setFileURL(URL fileURL) {
		this.fileURL = fileURL;
	}
	public String getDestFileName() {
		return destFileName;
	}
	public void setDestFileName(String destFileName) {
		this.destFileName = destFileName;
	}
	private String destFileName;
}
