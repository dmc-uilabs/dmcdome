/*
 *  Copyright (c) 2001, Jacob Smullyan.
 *
 *  This is part of SkunkDAV, a WebDAV client.  See http://skunkdav.sourceforge.net/ 
 *  for the latest version.
 * 
 *  SkunkDAV is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 * 
 *  SkunkDAV is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with SkunkDAV; see the file COPYING.  If not, write to the Free
 *  Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
*/

package org.skunk.swing.text.syntax;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.skunk.trace.Debug;

public class FileMode implements Serializable
{
    static final long serialVersionUID = 8155220627611660623L;

    private String modeName;
    private String[] extensions;
    private static ModeMapContainer modeMapContainer;
    private boolean shouldHighlight=true;
    private boolean canHighlight=false;

    static
    {
	modeMapContainer=new ModeMapContainer();
	initDefaultModes(modeMapContainer);
    }

    public static void initDefaultModes(ModeMapContainer container)
    {
      /*InputStream in=FileMode.class.getResourceAsStream("default.modes");
	BufferedReader breader=new BufferedReader(new InputStreamReader(in));
	try
	{
	    container.readConfig(breader);
	}
	catch (IOException oyVeh)
	{
	    if (Debug.DEBUG)
	    {
		Debug.trace(FileMode.class, Debug.DP2, oyVeh);
	    }
	    }*/
// 	container.clear();
// 	FileMode javaMode=container.getMode("java", true);
// 	javaMode.addExtension(".java");
// 	javaMode.canHighlight=true;
// 	FileMode plainMode=container.getMode("plain", true);
// 	plainMode.setShouldHighlight(false);
// 	plainMode.addExtension(".txt");
// 	plainMode.canHighlight=false;
 	FileMode pythonMode=container.getMode("python", true);
 	pythonMode.setShouldHighlight(true);
 	pythonMode.addExtension(".py");
 	pythonMode.canHighlight=true;
// 	FileMode HtmlMode=container.getMode("HTML", true);
// 	HtmlMode.setShouldHighlight(true);
// 	HtmlMode.canHighlight=true;
// 	HtmlMode.addExtensions(new String[] {".html", ".htm"});
// 	FileMode StmlMode=container.getMode("STML", true);
// 	StmlMode.canHighlight=true;
// 	StmlMode.setShouldHighlight(true);
// 	StmlMode.addExtensions(new String[] {".comp", ".dcmp"});
//         FileMode javascriptMode=container.getMode("javascript", true);
//         javascriptMode.addExtension(".js");
//         javascriptMode.canHighlight=true;
//         javascriptMode.setShouldHighlight(true);
    }

    public static ModeMapContainer getModeMapContainer()
    {
	return modeMapContainer;
    }

    public static FileMode getMode(String modeName, boolean create)
    {
	return FileMode.modeMapContainer.getMode(modeName, create);
    }

    public static FileMode getMode(String modeName)
    {
	return FileMode.modeMapContainer.getMode(modeName, true);
    }

    public static FileMode getModeForFilename(String filename)
    {
	return FileMode.modeMapContainer.getModeForFilename(filename);
    }

    public final static Iterator modes()
    {
	return FileMode.modeMapContainer.modes();
    }

    public final static Iterator modeNames()
    {
	return FileMode.modeMapContainer.modeNames();
    }

    private FileMode(String modeName, String[] extensions)
    {
	this.modeName=modeName;
	this.extensions=extensions;
    }

    public final String getName()
    {
	return modeName;
    }

    public final String[] getExtensions()
    {
	return this.extensions;
    }

    public final boolean getShouldHighlight()
    {
	return this.shouldHighlight;
    }

    public final void setShouldHighlight(boolean shouldHighlight)
    {
	this.shouldHighlight=shouldHighlight;
    }

    public final boolean getCanHighlight()
    {
	return this.canHighlight;
    }

    public final void addExtension(String extension)
    {
	if (extensions==null)
	{
	    extensions=new String[] {extension};
	}
	else 
	{
	    String[] tmp=extensions;
	    int oldLen=tmp.length;
	    extensions=new String[oldLen+1];
	    System.arraycopy(tmp, 0, extensions, 0, oldLen);
	    extensions[oldLen]=extension;
	}
    }

    public final void addExtensions(String[] extensionArray)
    {
	int newLen=extensionArray.length;
	if (extensions==null)
	{
	    extensions=new String[newLen];
	    System.arraycopy(extensionArray, 0, extensions, 0, newLen);
	}
	else
	{
	    String[] tmp=extensions;
	    int oldLen=tmp.length;
	    extensions=new String[oldLen+extensionArray.length];
	    System.arraycopy(tmp, 0, extensions, 0, oldLen);
	    System.arraycopy(extensionArray, 0, extensions, oldLen, newLen);
	}
    }

    public final void removeExtension(String extension)
    {
	if (extensions!=null)
	{
	    int extIsReal=-1;
	    for (int i=0;i<extensions.length;i++)
	    {
		if (extensions[i].equals(extension))
		{
		    extIsReal=i;
		    break;
		}
	    }
	    if (extIsReal<0)
		return;
	    String[] tmp=new String[extensions.length-1];
	    System.arraycopy(extensions, 0, tmp, 0, extIsReal);
	    System.arraycopy(extensions, extIsReal+1, tmp, extIsReal, tmp.length-extIsReal);
	    extensions=tmp;
	}
    }
		

    public final String toString()
    {
	StringBuffer sb = new StringBuffer("FileMode ")
	    .append(modeName)
	    .append(" [");
	if (extensions!=null)
	{
	    for (int i=0;i<extensions.length;i++)
	    {
		if (i>0) sb.append(", ");
		sb.append("\"");
		sb.append(extensions[i]);
		sb.append("\"");
	    }
	}
	sb.append("]");
	return sb.toString();
    }

    public final int hashCode()
    {
	return modeName.hashCode();
    }



    /**
     * wrapper for static member, for configuration package
     */
    public static final class ModeMapContainer implements Serializable
    {
	static final long serialVersionUID = 507341428772651975L;
	public static final String MODE_MAP_PROPERTY="modeMap";
	public static final String CONFIG_DATA_PROPERTY="configData";
	private static final String CONFIG_BOILERPLATE=
	    "###############################################################"
	    + "\n" +
	    "# MODE           SHOULD_HIGHLIGHT CAN_HIGHLIGHT    EXTENSIONS #"
	    + "\n" +
	    "###############################################################"
	    + "\n";
	private static final int CONFIG_TAB_SIZE=17; 

	private HashMap modeMap;

	public ModeMapContainer(HashMap modeMap) 
	{ 
	    setModeMap(modeMap); 
	}

	public ModeMapContainer()
	{
	    this(new HashMap());
	}

	public final HashMap getModeMap() 
	{ 
	    return modeMap; 
	}

	public final void setModeMap(HashMap modeMap) 
	{ 
	    this.modeMap=modeMap; 
	}

	public final FileMode getMode(String modeName, boolean create)
	{
	    if (this.modeMap.containsKey(modeName))
		return (FileMode) this.modeMap.get(modeName);
	    else if (create)
	    {
		FileMode fm=new FileMode(modeName, null);
		this.modeMap.put(modeName, fm);
		return fm;
	    }
	    else
		return null;
	}

	public final FileMode getMode(String modeName)
	{
	    return this.getMode(modeName, true);
	}

	public final FileMode getModeForFilename(String filename)
	{
	    for (Iterator it=modeMap.keySet().iterator();it.hasNext();)
	    {
		Object fmkey=it.next();
		FileMode fm=(FileMode)modeMap.get(fmkey);
		String[] exts=fm.getExtensions();
		if (exts==null)
		    continue;
		for (int i=0;i<exts.length;i++)
		{
		    if (filename.endsWith(exts[i]))
			return fm;
		}
	    }
	    return null;
	}
	
	public final Iterator modes()
	{
	    return this.modeMap.values().iterator();
	}
	
	public final Iterator modeNames()
	{
	    return modeMap.keySet().iterator();
	}

	protected final void clear()
	{
	    this.modeMap.clear();
	}

	public final String getConfigData()
	{
	    StringWriter sw=new StringWriter();
	    try
	    {
		writeConfig(new BufferedWriter(sw));
	    }
	    catch (IOException oyVeh) 
	    {
		return null;
	    }
	    return sw.getBuffer().toString();
	}

	public final void setConfigData(String configData, boolean clearOut)
	{
	    if (clearOut) clear();
	    setConfigData(configData);
	}

	public final void setConfigData(String configData)
	{
	    StringReader sr=new StringReader(configData);
	    try
	    {
		readConfig(new BufferedReader(sr));
	    }
	    catch (IOException oyVeh)
	    {
		//will already be logged
	    }
	}

	/**
	 * writes the state of the file mode set in a config file format to the stream.
	 * @param brighter the stream to which to write
	 */
	public void writeConfig(BufferedWriter brighter)
	    throws IOException
	{
	    StringBuffer sb=new StringBuffer(CONFIG_BOILERPLATE);
	    for (Iterator it=modeMap.keySet().iterator();it.hasNext();)
	    {
		FileMode fm=(FileMode) modeMap.get(it.next());
		sb.append(pad(fm.getName(), CONFIG_TAB_SIZE));
		sb.append(pad(new Boolean(fm.getShouldHighlight()).toString(), CONFIG_TAB_SIZE));
		sb.append(pad(new Boolean(fm.getCanHighlight()).toString(), CONFIG_TAB_SIZE));
		String[] ext=fm.getExtensions();
		for (int i=0;i<ext.length;i++)
		{
		    if (i>0) sb.append(" ");
		    sb.append(ext[i]);
		}
		sb.append("\n");
	    }
	    try
	    {
		brighter.write(sb.toString(), 0, sb.length());
		brighter.flush();
		brighter.close();
	    }
	    catch (IOException oyVeh)
	    {
		if (Debug.DEBUG) Debug.trace(this, Debug.DP2, oyVeh);
		throw oyVeh;
	    }
	}

	private String pad(String s, int length)
	{
	    StringBuffer sb=new StringBuffer(s);
	    while (sb.length()<length)
		sb.append(" ");
	    return sb.toString();
	}

	/**
	 * sets the mode set according the config information in the stream,
	 * in the same format generated by writeConfig (and in "default.modes", the
	 * default config file).
	 * @param breader the stream from which to read the config information.
	 */
	public void readConfig(BufferedReader breader)
	    throws IOException
	{
	    clear();
	    try
	    {
		String line;
		while ((line=breader.readLine())!=null)
		{
		    if (line.trim().startsWith("#")) 
			continue;
		    StringTokenizer st=new StringTokenizer(line);
		    String name=st.nextToken();
		    boolean should=new Boolean(st.nextToken()).booleanValue();	
		    boolean can=new Boolean(st.nextToken()).booleanValue();	
		    String[] exts=new String[st.countTokens()];
		    for (int i=0;i<exts.length;i++)
		    {
			exts[i]=st.nextToken();
		    }
		    FileMode tmp=getMode(name, true);
		    tmp.setShouldHighlight(should);
		    tmp.canHighlight=can;
		    tmp.addExtensions(exts);
		}
	    }
	    catch (Exception oyVeh)
	    {
		if (Debug.DEBUG) Debug.trace(ModeMapContainer.class, Debug.DP2, oyVeh);
		throw new IOException(oyVeh.getMessage());
	    }
	}
    }   
}

/* $Log: FileMode.java,v $
/* Revision 1.1.1.1  2003/05/05 16:12:32  renu
/* Reformatted and restructered source tree
/*
/* Revision 1.1.1.1  2002/09/18 19:52:17  caoq
/* initial import
/*
/* Revision 1.9  2001/02/16 18:15:10  smulloni
/* many fixes to TextEditorCustomizer.  FileMode and SyntaxStyle now have a
/* configData property (they will probably be made into sibling classes).
/*
/* Revision 1.8  2001/02/14 21:41:19  smulloni
/* added javascript mode.
/*
/* Revision 1.7  2001/02/13 22:53:50  smulloni
/* adding a syntax highlighting mode for STML (Skunk Template Markup Language);
/* fixed a bug in SyntaxStyle in reading default.styles.
/*
/* Revision 1.6  2001/02/09 21:02:37  smulloni
/* added very primitive html mode.
/*
/* Revision 1.5  2001/02/08 20:23:43  smulloni
/* added a python mode
/*
/* Revision 1.4  2001/02/06 22:13:41  smulloni
/* first more-or-less working version of syntax highlighting, with customization.
/*
/* Revision 1.3  2001/02/06 00:11:18  smulloni
/* struggle, perhaps futile, with the TextEditorCustomizer and other implicated
/* classes
/*
/* Revision 1.2  2001/02/02 23:30:33  smulloni
/* adding customization features to the text editor.
/*
/* Revision 1.1  2001/01/30 23:05:09  smulloni
/* beginning of integration of syntax highlighting package into SimpleTextEditor.
/* */
