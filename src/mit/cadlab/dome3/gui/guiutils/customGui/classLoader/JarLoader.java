// JarLoader.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.customGui.classLoader;

import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

// Adapted from
// http://skaiste.elekta.lt/Books/O'Reilly/Bookshelfs/books/javaenterprise/security/ch03_05.htm

/**
 * This classloader loads classes from a jar (byte[]).
 * Image resources can be retrieved via the findIcon class.
 * Find resource does not work since it requires a URL return type and there is no physical location
 * for the image files.
 * Other types of resources (audio, etc) are not supported.
 */
public class JarLoader extends ClassLoader
{

	private HashMap classBytes = new HashMap(); // className - classBytes
	private HashMap resourceBytes = new HashMap(); // imageName - imageBytes

	public JarLoader(byte[] jarBytes, int jarLength, ClassLoader parent) {
		super(parent);
		readJarFile(jarBytes, jarLength);
	}

	public ImageIcon getIcon(String name) {
		Object resourceEntry = resourceBytes.get(name);
		if (resourceEntry instanceof byte[]) {
			ImageIcon icon = new ImageIcon((byte[])resourceEntry);
			resourceBytes.put(name, icon); // cache for future retrievals
			return icon;
		}
		else if (resourceEntry instanceof ImageIcon)
			return ((ImageIcon)resourceEntry);
		return null;
	}

	protected URL findResource(String name)
	{
		Object resourceEntry = resourceBytes.get(name);
		if (resourceEntry != null) {
			System.out.println("DOME custom jar loader: use JarLoader.getIcon [returns ImageIcon] to get "+name);
			System.out.println("\nor use send -noCCL flag to DomeClientApplication to use default jar loader");
		}
		return null;
	}

	protected Class findClass(String name) {
		String urlName = name.replace('.', '/');
		byte[] buf = (byte[]) classBytes.get(urlName);
		if (buf != null) {
			return defineClass(name, buf, 0, buf.length, null);
		}
		return null;
	}

	protected void readJarFile(byte[] jarBytes, int jarLength) {
		try {
			JarInputStream jis = new JarInputStream(new ByteArrayInputStream(jarBytes, 0, jarLength));
			try {
				JarEntry je;
				while ((je = jis.getNextJarEntry()) != null) {
					String jarEntryName = je.getName();
					if (jarEntryName.endsWith(".class"))
						loadClassBytes(jis, jarEntryName);
					else
						loadResourceBytes(jis, jarEntryName);
					jis.closeEntry();
				}
			}
			catch (IOException ioe) {
				System.out.println("Badly formatted jar file");
			}

		}
		catch (IOException e) {
			System.err.println("JarLoader.readJarFile: "+e);
		}
	}

	private void loadClassBytes(JarInputStream jis, String jarEntryName)
	{
		BufferedInputStream jarBuf = new BufferedInputStream(jis);
		ByteArrayOutputStream jarOut = new ByteArrayOutputStream();
		int b;
		try {
			while ((b = jarBuf.read()) != -1)
				jarOut.write(b);
			classBytes.put(jarEntryName.substring(0, jarEntryName.length() - 6),
			                jarOut.toByteArray());
		}
		catch (IOException ioe) {
			System.out.println("Error reading entry " + jarEntryName);
		}
	}

	private void loadResourceBytes(JarInputStream jis, String jarEntryName)
	{
		BufferedInputStream jarBuf = new BufferedInputStream(jis);
		ByteArrayOutputStream jarOut = new ByteArrayOutputStream();
		int b;
		try {
			while ((b = jarBuf.read()) != -1)
				jarOut.write(b);
			resourceBytes.put(jarEntryName,
			               jarOut.toByteArray());
		}
		catch (IOException ioe) {
			System.out.println("Error reading entry " + jarEntryName);
		}
	}

}
