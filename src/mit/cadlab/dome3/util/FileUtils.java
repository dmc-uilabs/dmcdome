// FileUtils.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.Debug;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static final String OSNAMEPREFIX_WIN = "Windows";
    public static final String OSNAMEPREFIX_MAC = "Mac";
    public static final String SLASH_WIN = "\\";
    public static final String SLASH_MAC = "/";
	public static final String ENDL = System.getProperty("line.separator");

    public static final String BINARY = "Binary";
    public static final String GIF = "GIF";
    public static final String IGES = "IGES";
    public static final String JPEG = "JPG";
    public static final String PDF = "Pdf";
    public static final String STEP = "STEP";
    public static final String TEXT = "Text";
    public static final String VRML = "VRML";
    public static final String EXCEL = "Excel";
    public static final String MATLAB = "Matlab";
    public static final String JAR = "Jar";
    public static final String URL = "URL";

    public static final String DEFAULTPREFIX = "domefile";
    public static final long MAXFILESIZE =  2000000; //200000;
    //public static final long MAXFILESIZE =  2000000000;  // Max file size allowed to be included in file messages
    public static final int BUFFERLENGTH = 1000 * 1024;  //for faster reading of binary files

    public static List readTextFileAsList(String fileName) throws FileNotFoundException {
        return Regex.split(Regex.endline, readTextFileAsString(fileName));
    }

    public static List readTextFileAsList(File file) throws FileNotFoundException {
        return Regex.split(Regex.endline, readTextFileAsString(file));
    }

    public static String readTextFileAsString(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        return readTextFileAsString(file);
    }

    public static long getFileSize(String fileName) {
        try {
        	File file = new File(fileName);
        	long fileSize = file.length();
        	
        	return fileSize;
        }
        catch (Exception e) {
        	// could not determine file size
        	e.printStackTrace();
        	return -1;
        }
    }
    /*
     * This method will double memory usage.  It is replaced by the implementation above which will not
     * generate memory overhead.
     */

    public static byte[] readBinaryFileAsByteArray(String file) throws FileNotFoundException, IOException {
		InputStream in = null;
        FileInputStream fin = null;
        byte[] buf = null; // output buffer
        try {
        	File f = new File(file);
        	long size = f.length();
            fin = new FileInputStream(file);
            in = new BufferedInputStream(fin);
            buf = new byte[(int)size];
            int len = in.read(buf,0,(int)size);
            System.out.println("read in bytes len = " + len);
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (fin != null) {
                fin.close();
            }
        }
        return buf;
    }

    /**
     *  Reads a file storing intermediate data into a list.
     *  @param file the file to be read
     *  @return file content
     */
    public static byte[] readBinaryFileAsByteArray1(String file) throws FileNotFoundException, IOException {
        InputStream in = null;
        FileInputStream fin = null;
        byte[] buf = null; // output buffer
        int bufLen = BUFFERLENGTH;
        try {
            fin = new FileInputStream(file);
            in = new BufferedInputStream(fin);
            buf = new byte[bufLen];
            byte[] tmp = null;
            int len = 0;
            List data = new ArrayList(10); // keeps pieces of data
            while ((len = in.read(buf, 0, bufLen)) != -1) {
                tmp = new byte[len];
                System.arraycopy(buf, 0, tmp, 0, len);
                data.add(tmp);
            }
            len = 0;
            if (data.size() == 1) return (byte[]) data.get(0);
            for (int i = 0; i < data.size(); i++) {
                len += ((byte[]) data.get(i)).length;
            }
            buf = new byte[len]; // final output buffer
            len = 0;
            for (int i = 0; i < data.size(); i++) { // fill with data
                tmp = (byte[]) data.get(i);
                System.arraycopy(tmp, 0, buf, len, tmp.length);
                len += tmp.length;
            }
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (fin != null) {
                fin.close();
            }
        }
        return buf;
    }

	/**
	 *  More efficient version than above. Above not deleted/replaced since this one needs testing.
	 *  @param file the file to be read
	 *  @return vector <file length, file content>; file content is byte[]
	 */
	public static Vector loadFileAsByteArray(File file) throws FileNotFoundException, IOException
	{
		InputStream in = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			in = new BufferedInputStream(fin);

			int totalBytes = 0;
			int bytesRead = 0;
			List data = new ArrayList(10); // keeps pieces of data
			byte[] tmp = new byte[BUFFERLENGTH];
			while ((bytesRead = in.read(tmp, 0, BUFFERLENGTH)) != -1) {
				totalBytes += bytesRead;
				data.add(tmp);
				tmp = new byte[BUFFERLENGTH];
			}
			int bytesWritten = 0;
			if (data.size() == 1) return Vectors.create(new Integer(totalBytes), (byte[]) data.get(0));
			byte[] result = new byte[totalBytes]; // final output buffer
			int bufLength = BUFFERLENGTH;
			int lastIndex = data.size()-1;
			for (int i = 0; i < data.size(); i++) { // fill with data
				tmp = (byte[]) data.get(i);
				if (i == lastIndex)
					bufLength = totalBytes - bytesWritten;
				System.arraycopy(tmp, 0, result, bytesWritten, bufLength);
				bytesWritten += bufLength;
			}
			return Vectors.create(new Integer(totalBytes), result);
		}
		finally {
			if (in != null) {
				in.close();
			}
			if (fin != null) {
				fin.close();
			}
		}
	}

	public static void writeBinaryFile(String fileName, byte[] bytes, int length) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, 0, length);
		File file = new File(fileName);
		// Set to true if the bytes should be appended to the file;
		// set to false if the bytes should replace current bytes
		// (if the file exists)
		boolean append = false;

		try {
			// Create a writable file channel
			FileChannel wChannel = new FileOutputStream(file, append).getChannel();

			// Write the ByteBuffer contents; the bytes between the ByteBuffer's
			// position and the limit is written to the file
			wChannel.write(byteBuffer);

			// Close the file
			wChannel.close();
		}
		catch (IOException e) {
			System.err.println(e);
		}

	}

    /**
     *  writes a file in binary format.
     *  @param bytearr contents of file
     *  @param filename file path
     */
    public static void writeByteArrayAsBinaryFile(byte[] bytearr, String filename)
            throws FileNotFoundException, IOException {
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
		try {
			File file = new File(filename);
			fout = new FileOutputStream(file);
			bout = new BufferedOutputStream(fout);
			if (bytearr != null) {
				bout.write(bytearr);
			}
		} finally {
			if (bout != null) {
				bout.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
    }


    public static String readTextFileAsString(File file) throws FileNotFoundException {
        if (!file.exists())
            throw new FileNotFoundException(file.getAbsolutePath());
        long length=file.length();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            // there must be a better way!
            String line = in.readLine();
            if (line == null) {
                in.close();
                return null;
            }
            StringBuffer sb = new StringBuffer(line);
            while ((line = in.readLine()) != null)
               sb.append(ENDL + line);
            in.close();
            return sb.toString();
           } catch (Exception ex) {
            System.err.println("file read error: " + file.getAbsolutePath() + "\n  " + ex);
            if (in != null)
                try {
                    in.close();
                } catch (Exception inex) {
                    System.err.println("file close error: " + file.getAbsolutePath() + "\n  " + inex);
                }
            return null;
        }
    }

	public static String readTextUrlAsString(String urlLocation) throws FileNotFoundException
	{
		URL fileUrl = ClassLoader.getSystemResource(urlLocation);
		if (fileUrl == null) {
			throw new FileNotFoundException(urlLocation);
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
			// there must be a better way!
			String line = in.readLine();
			if (line == null) {
				in.close();
				return null;
			}
			StringBuffer sb = new StringBuffer(line);
			while ((line = in.readLine()) != null)
				sb.append(ENDL + line);
			in.close();
			return sb.toString();
		}
		catch (Exception ex) {
			System.err.println("file read error: " + urlLocation + "\n  " + ex);
			if (in != null)
				try {
					in.close();
				}
				catch (Exception inex) {
					System.err.println("file close error: " + urlLocation + "\n  " + inex);
				}
			return null;
		}
	}

    public static void writeTextToFile(String text, String fileName) throws IOException {
        File file = new File(fileName);
        PrintWriter out = null;
        out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        out.print(text);
        out.close();
    }

	/**
	 * Writes file and catches exception, printing it to console.
	 * @param text text to be written to file
	 * @param fileName location of file
	 * @return true if write is successful; false if any exceptions occurred
	 */
    public static boolean writeStringToFile(String text, String fileName) {
        return writeStringToFile(text, new File(fileName));
    }

	/**
	 * Writes file and catches exception, printing it to console.
	 * @param text text to be written to file
	 * @param file file to be written
	 * @return true if write is successful; false if any exceptions occurred
	 */
    public static boolean writeStringToFile(String text, File file) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.print(text);
            out.close();
	        return true;
        } catch (Exception ex) {
            System.err.println("file write error: " + file.getAbsolutePath() + "\n  " + ex);
            if (out != null)
                out.close();
	        return false;
        }
    }

    /**
     * @param dir The directory whose contents are to be deleted
     * @param deleteDirectory whether or not the directory itself should be deleted
     */
    public static void deleteDirectoryContents(File dir, boolean deleteDirectory) {
        if (dir.isDirectory() && dir.exists()) {
            File[] contents = dir.listFiles();
            File content;
            for (int i = 0; i < contents.length; i++) {
                content = contents[i];
                if (content.isDirectory())
                    deleteDirectoryContents(content, true);
                else if (!content.delete())
                    throw new RuntimeException("unable to delete " + content.getAbsolutePath());
            }
            if (deleteDirectory)
                if (!dir.delete())
                    throw new RuntimeException("unable to delete " + dir.getAbsolutePath());
        }
    }

    //Qing add for File Data copy


    /**
     *
     * @param newObj: domefile to copy content from
     * @param oldObj: domefile to copy content to
     * @param isVerbose: show warning dialogs if true
     */
    public static void copyFile(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile newObj, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile oldObj, boolean isVerbose) {

        try {
            //newObj filepath must be valid
            if (!validateFile(newObj.getFilePath())) {
                if (isVerbose) {
                    OneButton1Msg.showWarning(null, "Warning:File does not exist", newObj.getFilePath() + " does not exist.\nFile copy has been aborted!", "ok", OneButton1Msg.DEFAULT_SIZE);
                }
                return;
            }

            File file_to_be_copied = new File(newObj.getFilePath());
            FileInputStream fin = new FileInputStream(file_to_be_copied);
            DataInputStream din = new DataInputStream(fin);
            BufferedInputStream bin = new BufferedInputStream(din);


           //oldObj filepath can be invalid, provide dummy filepath will be given into
            if (oldObj.getFilePath().trim().equals("")) {
                if (isVerbose) {
                    OneButton1Msg.showWarning(null, "Warning:Empty file path", "The file you want to copy content into\nhasn't config path yet!\nFile copy has been aborted!", "ok", OneButton1Msg.DEFAULT_SIZE);
                }
                return;
            }

            File file_to_be_write = new File(oldObj.getFilePath());


            if (file_to_be_write.exists()) {
                if (isVerbose) {
                    OneButton1Msg.showWarning(null, "Warning:File already exist", file_to_be_write + "will be overwrite!", "ok", OneButton1Msg.DEFAULT_SIZE);
                }
            }
            //binary write

            byte readFromFile[] = new byte[fin.available()];
            bin.read(readFromFile, 0, readFromFile.length);

            FileOutputStream fout = new FileOutputStream(file_to_be_write);
            DataOutputStream dout = new DataOutputStream(fout);
            BufferedOutputStream bout = new BufferedOutputStream(dout);

            bout.write(readFromFile);

            //clean up
            bin.close();
            din.close();
            fin.close();

            bout.close();
            dout.close();
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	/**
	 * copies files from source_file to destination_file
	 * @param source_file
	 * @param destination_file
	 * @throws RuntimeException if source file does not exist, is not a file, or can not be read or if
	 *         the destination file exists but is a directory or can not be overwritten or
	 *         if it does not exist and can not be written
	 */
	public static void copyFile(File source_file, File destination_file) {
		if (source_file==null || destination_file==null)
			throw new RuntimeException("copyFile: can not copy to/from null files");
		if (source_file.equals(destination_file)) // same location
			return; // do not copy
		if (!source_file.exists() || !source_file.isFile())
			throw new RuntimeException("copyFile: no such source file - "+source_file.getAbsolutePath());
		if (!source_file.canRead())
			throw new RuntimeException("copyFile: source file is unreadable - " + source_file.getAbsolutePath());
        if (destination_file.exists()) {
	        if (!destination_file.isFile())
	            throw new RuntimeException("copyFile: destination file is not a file - "+destination_file.getAbsolutePath());
			else if (!destination_file.canWrite())
				throw new RuntimeException("copyFile: destination file can not be overwritten - "+destination_file.getAbsolutePath());
        } else { // destination file does not exist
			File parent_directory = parent(destination_file);
	        if (!parent_directory.exists())
		        throw new RuntimeException("copyFile: destination directory does not exist - "+parent_directory.getAbsolutePath());
	        if (!parent_directory.canWrite())
		        throw new RuntimeException("copyFile: destination directory can not be written - "+parent_directory.getAbsolutePath());
        }

		// copy the file.
		FileInputStream source = null;
		FileOutputStream destination = null;
		byte[] buffer;
		int bytes_read;

		try {
			source = new FileInputStream(source_file);
			destination = new FileOutputStream(destination_file);
			buffer = new byte[1024];
			while (true) {
				bytes_read = source.read(buffer);
				if (bytes_read == -1) break;
				destination.write(buffer, 0, bytes_read);
			}
		}
		catch (IOException e) {
			throw new RuntimeException("copyFile: "+e);
		}
		finally {
			if (source != null)
				try {
					source.close();
				}
				catch (IOException e) {
				}
			if (destination != null)
				try {
					destination.close();
				}
				catch (IOException e) {
				}
		}
	}

   // File.getParent() can return null when the file is specified without
   // a directory or is in the root directory.
   // This method handles those cases.
	public static File parent(File f)
	{
		String dirname = f.getParent();
		if (dirname == null) {
			if (f.isAbsolute())
				return new File(File.separator);
			else
				return new File(System.getProperty("user.dir"));
		}
		return new File(dirname);
	}

    public static boolean validateFile(String filepath) {
        File file;
        try {
            file = new File(filepath);
            if (file.exists() && file.isFile()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OneButton1Msg.showWarning(null, "File Warning", "error in the file path", "ok", OneButton1Msg.DEFAULT_SIZE);
            return false;
        }

    }

    /**
     *
     * @param newObj: domefile to be compared
     * @param oldObj: target domefile
     * @return
     */
    public static boolean compareFile(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile newObj, mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile oldObj) {
        if (validateFile(newObj.getFilePath()) && validateFile(oldObj.getFilePath())) {
            try {
                File fileN = new File(newObj.getFilePath());
                FileInputStream finN = new FileInputStream(fileN);
                DataInputStream dinN = new DataInputStream(finN);
                BufferedInputStream binN = new BufferedInputStream(dinN);

                File fileO = new File(oldObj.getFilePath());
                FileInputStream finO = new FileInputStream(fileO);
                DataInputStream dinO = new DataInputStream(finO);
                BufferedInputStream binO = new BufferedInputStream(dinO);

                byte readFromFileN[] = new byte[finN.available()];
                binN.read(readFromFileN, 0, readFromFileN.length);

                byte readFromFileO[] = new byte[finO.available()];
                binO.read(readFromFileO, 0, readFromFileO.length);

                if (readFromFileN.length != readFromFileO.length)
                    return false;
                for (int i = 0; i < readFromFileN.length; i++) {
                    if (!(readFromFileN[i] == readFromFileO[i]))
                        return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     *
     * @param newObj: file to be compared
     * @param oldObj: target file
     * @return false if files can not be compared or are different
     */
    public static boolean compareFile(File newObj, File oldObj) {
        if (validateFile(newObj.getPath()) && validateFile(oldObj.getPath())) {
	        FileInputStream finN, finO;
	        DataInputStream dinN, dinO;
	        BufferedInputStream binN=null, binO=null;
            try {
                File fileN = new File(newObj.getPath());
                finN = new FileInputStream(fileN);
                dinN = new DataInputStream(finN);
                binN = new BufferedInputStream(dinN);

                File fileO = new File(oldObj.getPath());
                finO = new FileInputStream(fileO);
                dinO = new DataInputStream(finO);
                binO = new BufferedInputStream(dinO);

                byte readFromFileN[] = new byte[finN.available()];
                binN.read(readFromFileN, 0, readFromFileN.length);

                byte readFromFileO[] = new byte[finO.available()];
                binO.read(readFromFileO, 0, readFromFileO.length);

                if (readFromFileN.length != readFromFileO.length)
                    return false;
                for (int i = 0; i < readFromFileN.length; i++) {
                    if (!(readFromFileN[i] == readFromFileO[i]))
                        return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
	        finally {
	            try {
		            if (binN != null)
		                binN.close();
	            }
	            catch (IOException e) {
	            }

	            try {
		            if (binN != null)
		                binO.close();
	            }
	            catch (IOException e) {
	            }
            }
        }
        return false;
    }

    public static boolean compareContent(byte[] content1, byte[] content2) {
        if (content1.length != content2.length)
            return false;
        for (int i = 0; i < content1.length; i++) {
            if (!(content1[i] == content2[i]))
                return false;
        }
        return true;

    }


    public static boolean showFile(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile file) {
        //check if the filePath valid
        if (!validateFile(file.getFilePath())) {
            OneButton1Msg.showWarning(null, "Warning:file does not exist", file.getFilePath() + " does not exist!", "ok", OneButton1Msg.DEFAULT_SIZE);
            return false; //failure
        }
        try {
            if (System.getProperty("os.name").startsWith(OSNAMEPREFIX_WIN)) {
                if (System.getProperty("os.name").indexOf("9") != -1) {
                    //win95/98 OS:command.com
                    //for windows, you have to take care of the white space in path, think about command line
                    String filePath = getCleanFilePath(file.getFilePath());
                    Runtime.getRuntime().exec("command.com /c start " + filePath);
                } else {
                    //winNT/win2K: cmd.exe
                    //for windows, you have to take care of the white space in path, think about command line
                    String filePath = getCleanFilePath(file.getFilePath());
                    Runtime.getRuntime().exec("cmd /c start " + filePath);
                }
            } else if (System.getProperty("os.name").startsWith(OSNAMEPREFIX_MAC)) {
                java.lang.Runtime.getRuntime().exec(new String[]{"open", file.getFilePath()});
            } else {
                //unix or linux... not tested yet... Qing Apr 4th
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "open", file.getFilePath()});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; //failure
        }
        return true;  //success
    }

    /**
     * this is to avoid the command line error when a filepath has white space
     * @param filePath
     * @return
     */
    public static String getCleanFilePath(String filePath) {
        if (filePath.indexOf(" ") != 0)
            return filePath.replaceAll(" ", "\" \"");
        return filePath;
    }

    public static String getApproxFileSize(String filePath) {
        if (!validateFile(filePath)) {
            return "bad file";
        }
        try {
            File f = new File(filePath);
            long size = f.length();
            if (size < 1000) return size + " bytes";
            if (size >= 1000 && size < 1000000) return size / 1000 + "K";
            if (size >= 1000000) return size / 1000000 + "M";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "size unknown";
    }

    public static String fixPathnameSlashes(String filePath) {
        //replace any Windows-style path separators with normal (unix defined it first -- phlebbt!) separators
        
        String fixedPath = filePath.replace(SLASH_WIN, SLASH_MAC);
        return fixedPath;
    }
    
    //Qing added Sep 2nd--- for customGUi

    public static String getFileName(String filePath) {
        //filepath are in different os format
        if (filePath.indexOf(SLASH_MAC) != -1) {
            return getSubStr(filePath, SLASH_MAC);
        }
        else if(filePath.indexOf(SLASH_WIN) != -1) {
            return getSubStr(filePath, SLASH_WIN);
        }
        return filePath;
    }

    public static String getSubStr(String originalStr, String indexStr) {
        if (originalStr.indexOf(indexStr) == -1) return originalStr;
        int lastIndex = originalStr.lastIndexOf(indexStr);
        String subStr = originalStr.substring(lastIndex + 1, originalStr.length());
        return subStr;
    }

    // this method assumes the last part of the parameter's value is the filename, which will be stripped off
    public static String getFilePath(String fullfilename) {
    	String filename = getFileName(fullfilename);
    	
    	// now get everything BUT the filename
    	String filepath = fullfilename.substring(0, fullfilename.lastIndexOf(filename));
    	return filepath;
    }
    
    public static FileFilter getFilterForType(String type) {
        if (type.equals(BINARY)) {
            return null;
        } else if (type.equals(GIF)) {
            return new GIFFileFilter();
        } else if (type.equals(IGES)) {
            return new IGESFileFilter();
        } else if (type.equals(JPEG)) {
            return new JPEGFileFilter();
        } else if (type.equals(STEP)) {
            return new STEPFileFilter();
        } else if (type.equals(TEXT)) {
            return new TEXTFileFilter();
        } else if (type.equals(PDF)) {
            return new PDFFileFilter();
        } else if (type.equals(MATLAB)) {
            return new MATLABFileFilter();
        } else if (type.equals(EXCEL)) {
            return new EXCELFileFilter();
        } else if (type.equals(VRML)) {
            return new VRMLFileFilter();
        } else if (type.equals(JAR)) {
            return new JARFileFilter();
        } else
            return null;

    }

    public static String getDefaultSuffixForType(String type) {
        if (type.equals(BINARY)) {
            return "";
        } else if (type.equals(GIF)) {
            return ".gif";
        } else if (type.equals(IGES)) {
            return ".iges";
        } else if (type.equals(JPEG)) {
            return ".jpg";
        } else if (type.equals(STEP)) {
            return ".step";
        } else if (type.equals(TEXT)) {
            return ".txt";
        } else if (type.equals(PDF)) {
            return ".pdf";
        } else if (type.equals(EXCEL)) {
            return ".xls";
        } else if (type.equals(MATLAB)) {
            return ".m";
        } else if (type.equals(VRML)) {
            return ".vrml";
        } else if (type.equals(JAR)) {
            return ".jar";
        } else
            return "";

    }

    /**
     * this function is coded for file chooser, when you choose a file of different type, do a type change
     * @param absoluteFilePath
     * @return
     */
    public static String getTypeForFile(String absoluteFilePath) {
        if (absoluteFilePath.toLowerCase().endsWith(getDefaultSuffixForType(GIF))) {
            return GIF;
        } else if (absoluteFilePath.toLowerCase().endsWith(getDefaultSuffixForType(TEXT))) {
            return TEXT;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(EXCEL))) {
            return EXCEL;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(IGES))) {
            return IGES;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(MATLAB))) {
            return MATLAB;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(JPEG))) {
            return JPEG;
        } else if (absoluteFilePath.toLowerCase().endsWith(".jpeg")) {
            return JPEG;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(PDF))) {
            return PDF;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(STEP))) {
            return STEP;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(VRML))) {
            return VRML;
        } else if (absoluteFilePath.toLowerCase().endsWith(".wrl")) {
            return VRML;
        } else if (absoluteFilePath.toLowerCase().endsWith(FileUtils.getDefaultSuffixForType(JAR))) {
            return JAR;
        }
        return BINARY;
    }


    public static class GIFFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return ((f.getName().endsWith(".gif"))
                    || (f.getName().endsWith(".GIF")));
        }

        // The description of this filter
        public String getDescription() {
            return "GIF File (*.GIF)";
        }
    }

    public static class IGESFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return ((f.getName().endsWith(".iges"))
                    || (f.getName().endsWith(".IGES"))
                    || (f.getName().endsWith(".igs"))
                    || (f.getName().endsWith(".IGS")));
        }

        // The description of this filter
        public String getDescription() {
            return "IGES File (*.IGES,*.IGS)";
        }
    }


    public static class JPEGFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return ((f.getName().endsWith(".jpeg"))
                    || (f.getName().endsWith(".JPEG"))
                    || (f.getName().endsWith(".jpg"))
                    || (f.getName().endsWith(".JPG"))
                    || (f.getName().endsWith(".jpe"))
                    || (f.getName().endsWith(".JPE"))
                    );
        }

        // The description of this filter
        public String getDescription() {
            return "JPEG File (*.JPEG,*.JPG,*.JPE)";
        }
    }

    public static class STEPFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return ((f.getName().endsWith(".step"))
                    || (f.getName().endsWith(".STEP")));
        }

        // The description of this filter
        public String getDescription() {
            return "STEP File (*.step)";
        }
    }

    public static class TEXTFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return ((f.getName().endsWith(".txt"))
                    || (f.getName().endsWith(".TXT")));
        }

        // The description of this filter
        public String getDescription() {
            return "TEXT File (*.txt)";
        }
    }

    public static class PDFFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return ((f.getName().endsWith(".pdf"))
                    || (f.getName().endsWith(".PDF")));
        }

        // The description of this filter
        public String getDescription() {
            return "PDF File (*.pdf)";
        }
    }

    public static class VRMLFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return ((f.getName().endsWith(".vrml"))
                    || (f.getName().endsWith(".VRML"))
                    || (f.getName().endsWith(".wrl"))
                    || (f.getName().endsWith(".WRL")));
        }

        // The description of this filter
        public String getDescription() {
            return "VRML File (*.wrl,*vrml)";
        }
    }

    public static class MATLABFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().endsWith(".m");
        }

        // The description of this filter
        public String getDescription() {
            return "MATLAB File (*.m)";
        }
    }

    public static class EXCELFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            //MG Excel files with xlsx needs to be picked up
            boolean bRet = f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx");
            Debug.trace(Debug.ALL, "excelFiler" + bRet);
            return (f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx"));
        }

        // The description of this filter
        public String getDescription() {
        	//MG Added * at the end
            return "EXCEL File (*.xls*)";
        }
    }

    public static class JARFileFilter extends FileFilter {

        // Accept all directories and dome model files
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().endsWith(".jar");
        }

        // The description of this filter
        public String getDescription() {
            return "JAR File (*.jar)";
        }
    }
}
