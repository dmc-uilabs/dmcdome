// DomeFileChooser.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils;

import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

/**
 * File chooser for DOME with custom view and sets of file filters.
 * The list of file filters can be retrieved via getFileFilterNames.
 * General file filters for the different Dome file types can be accessed via the constants defined below.
 * File filters for specific models types are available via the type name of the model.
 * If it is a plugin model, it is available via the plugin type name (or PluginConfiguration.TYPE_INFO.getTypeName()).
 */
public class DomeFileChooser extends JFileChooser
{

	public static final String MODEL_FILE_EXTENSION = "dml";
    public static final String ANALYSIS_TOOL_FILE_EXTENSION = "dtl";
    public static final String ANALYSIS_TOOL_INTERFACE_RESULTS_EXTENSION = "rti";
	public static final String PLAYSPACE_FILE_EXTENSION = "dps";
	public static final String PROJECT_FILE_EXTENSION = "dpj";
	public static final String INTERFACE_FILE_EXTENSION = "dmi";
	public static final String PROJECTINTERFACE_FILE_EXTENSION = "dpi";
	public static final List DOME_FILE_EXTENSIONS = Arrays.asList(new String[]{MODEL_FILE_EXTENSION,
	                                                                           ANALYSIS_TOOL_FILE_EXTENSION,
	                                                                           PLAYSPACE_FILE_EXTENSION,
	                                                                           PROJECT_FILE_EXTENSION,
	                                                                           INTERFACE_FILE_EXTENSION,
                                                                               ANALYSIS_TOOL_INTERFACE_RESULTS_EXTENSION});

	// constants for accessing Dome file type filters
	public static final String DOME_FILES_FILTER = "All Dome Files";
	public static final String DOME_MODELS_FILTER = "All Dome Models";
    public static final String DOME_ANALYSIS_TOOL_FILTER = "dome analysis tools";
    public static final String DOME_ANALYSIS_TOOL_INTERFACE_RESULTS_FILTER = "dome analysis tool interface result files";
	public static final String DOME_PLAYSPACE_FILTER = "Dome Playspaces";
	public static final String DOME_PROJECT_FILTER = "Dome Projects";
	public static final String DOME_INTERFACE_FILTER = "Dome Interfaces";

	private static final HashMap domeFileFilters = createStandardDomeFileFilters();

	private static HashMap createStandardDomeFileFilters()
	{
		HashMap filters = new HashMap();
		filters.put(DOME_FILES_FILTER, new DefaultDomeFileFilter());
		filters.put(DOME_MODELS_FILTER, new SpecificDomeFileFilter("DOME Model File", MODEL_FILE_EXTENSION));
        filters.put(DOME_ANALYSIS_TOOL_FILTER, new SpecificDomeFileFilter("DOME Model File", ANALYSIS_TOOL_FILE_EXTENSION));
		filters.put(DOME_PLAYSPACE_FILTER, new SpecificDomeFileFilter("DOME Playspace File", PLAYSPACE_FILE_EXTENSION));
		filters.put(DOME_PROJECT_FILTER, new SpecificDomeFileFilter("DOME Project File", PROJECT_FILE_EXTENSION));
		filters.put(DOME_INTERFACE_FILTER, new SpecificDomeFileFilter2("DOME Interface File", INTERFACE_FILE_EXTENSION));
        filters.put(DOME_ANALYSIS_TOOL_INTERFACE_RESULTS_FILTER, new SpecificDomeFileFilter(
                "analysis tool interface results file", ANALYSIS_TOOL_INTERFACE_RESULTS_EXTENSION));
		return filters;
	}

	public static void registerPluginFileFilter(String name, String pluginExt)
	{
		registerPluginFileFilter(name, name, pluginExt);
	}

	public static void registerPluginFileFilter(String keyName, String name, String pluginExt)
	{
		if (domeFileFilters.containsKey(name)) {
			System.err.println("duplicate plugin file filter registration ignored: " + name + "\t" + pluginExt);
			return;
		} else {
			domeFileFilters.put(keyName, new PluginModelFileFilter(name, pluginExt));
		}
	}

    public static void registerToolFileFilter(String name, String toolExt)
    {
        DomeFileChooser.registerToolFileFilter(name, name, toolExt);
    }

    public static void registerToolFileFilter(String keyName, String name, String toolExt)
    {
        if(domeFileFilters.containsKey(name))
        {
            System.err.println("duplicate tool file filter registration ignored: " + name + "\t" + toolExt);
            return;
        }
        else
        {
            domeFileFilters.put(keyName, new ToolModelFileFilter(name, toolExt));
        }
    }

    public static void registerAnalysisToolInterfaceResultsFileFilter(String name, String resExt)
    {
        DomeFileChooser.registerAnalysisToolInterfaceResultsFileFilter(name, name, resExt);
    }

    public static void registerAnalysisToolInterfaceResultsFileFilter(String keyName, String name, String resExt)
    {
        if (domeFileFilters.containsKey(name))
        {
            System.err.println("duplicate analysis tool interface results file filter registration ignored: " + name + "\t" + resExt);
            return;
        }
        else
            domeFileFilters.put(keyName, new AnalysisToolInterfaceResultsFileFilter(name, resExt));
    }

	public static Collection getFileFilterNames()
	{
		return Collections.unmodifiableSet(domeFileFilters.keySet());
	}

	public static FileFilter getDomeFileFilter(String name)
	{
		if (domeFileFilters.containsKey(name))
			return (FileFilter) domeFileFilters.get(name);
		else
			throw new IllegalArgumentException("getDomeFileFilter: invalid filter name - " + name);
	}

	public DomeFileChooser()
	{
		customize();
	}

	public DomeFileChooser(String currentDirectoryPath)
	{
		super(currentDirectoryPath);
		customize();
	}

	public DomeFileChooser(File currentDirectory)
	{
		super(currentDirectory);
		customize();
	}

	public DomeFileChooser(FileSystemView fsv)
	{
		super(fsv);
		customize();
	}

	public DomeFileChooser(File currentDirectory, FileSystemView fsv)
	{
		super(currentDirectory, fsv);
		customize();
	}

	public DomeFileChooser(String currentDirectoryPath, FileSystemView fsv)
	{
		super(currentDirectoryPath, fsv);
		customize();
	}

	private void customize()
	{
		setFileView(new DomeFileView());
	}


	public String showOpenDialog(Component parent, String fileFilterName, File currentDirectory)
	{
		if (fileFilterName != null)
			setFilter(fileFilterName);
		if (currentDirectory != null)
			setCurrentDirectory(currentDirectory);
		int returnVal = showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return getSelectedFile().getPath();
		} else {
			return null;
		}
	}

	public String showOpenDialog(Component parent, String fileFilterName)
	{
		return showOpenDialog(parent, fileFilterName, null);
	}

	public String showSaveDialog(Component parent, String fileFilterName, File selectedFile)
	{
		return showSaveDialog(parent, getDomeFileFilter(fileFilterName), selectedFile);
	}

	public String showSaveDialog(Component parent, FileFilter fileFilter, File selectedFile)
	{
		if (fileFilter != null)
			setFilter(fileFilter);
		setSelectedFile(selectedFile);
		int returnVal = showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return getSelectedFile().getPath();
		} else {
			return null;
		}
	}

	public String showSaveDialog(Component parent, String fileFilterName)
	{
		return showSaveDialog(parent, fileFilterName, null);
	}

	public void setFilter(String filterType)
	{
		FileFilter filter = getDomeFileFilter(filterType);
		if (filter != null) {
			this.resetChoosableFileFilters();
			this.addChoosableFileFilter(filter);
			this.setFileFilter(filter);
		}
	}

	public void setFilter(FileFilter filter)
	{
		if (filter != null)
        {
            this.resetChoosableFileFilters();
            this.addChoosableFileFilter(filter);
            this.setFileFilter(filter);
        }
	}

    public void resetChoosableFileFilters()
    {
        /*
         * overwriting method because it does not work on a mac
         * if there is more than one file filter and the default
         * file filter is not the one selected the mac Java crashes
         * when attempting to remove that file filter
         */

        setFileFilter(getAcceptAllFileFilter()); // sets the default file filter to be the selected one
        super.resetChoosableFileFilters();
    }

	/**
	 * Excludes Dome specific directories and shows Dome specific files
	 */
	public static class DefaultDomeFileFilter extends FileFilter
	{
		public boolean accept(File f)
		{
			if (f.isDirectory()) {
				return isNonDomeDirectory(f);
			}
			return isDomeFile(f);
		}

		public String getDescription()
		{
			return "Dome Files (*.dml, *.dtl, *.dps, *.dpj, *.dmi, *.rti)";
		}
	}

	/**
	 * Excludes Dome specific directories and shows Dome files with a specific file extension
	 */
	public static class SpecificDomeFileFilter extends FileFilter
	{
		protected String description;
		protected String extension;

		public SpecificDomeFileFilter(String description, String extension)
		{
			this.description = description + " (*." + extension + ")";
			this.extension = extension;
		}

		public boolean accept(File f)
		{
			if (f.isDirectory()) {
				return isNonDomeDirectory(f);
			}
			return isSpecificDomeFile(f, extension);
		}

		public String getDescription()
		{
			return description;
		}
	}

	/**
	 * Excludes Dome VC directory
	 * and shows Dome files with a specific file extension
	 */
	public static class SpecificDomeFileFilter2 extends SpecificDomeFileFilter
	{
		public SpecificDomeFileFilter2(String description, String extension)
		{
			super(description, extension);
		}

		public boolean accept(File f)
		{
			if (f.isDirectory()) {
				return !f.getName().equals("domeVC");
			}
			return isSpecificDomeFile(f, extension);
		}
	}

	public static class PluginModelFileFilter extends FileFilter
	{
		protected String description;
		protected String extension;

		/**
		 *
		 * @param name is used in description as "NAME File"
		 * @param pluginExt is used in file extension as "-PLUGINEXT.dml"
		 */
		public PluginModelFileFilter(String name, String pluginExt)
		{
			pluginExt += ".dml";
			this.description = name + " File (*-" + pluginExt + ")";
			this.extension = pluginExt.toLowerCase();
		}

		public boolean accept(File f)
		{
			if (f.isDirectory()) {
				return isNonDomeDirectory(f);
			}
			String fileModelExt = getModelExtension(f);
			return fileModelExt != null && fileModelExt.equals(extension);
		}

		public String getDescription()
		{
			return description;
		}
	}

    public static class ToolModelFileFilter
                                    extends FileFilter
    {
        protected String _description;
        protected String _extension;

        public ToolModelFileFilter(String name, String toolExt)
        {
	        toolExt += ".dtl";
            this._description = name + " File (*-" + toolExt + ")";
            this._extension = toolExt.toLowerCase();
        }

        public boolean accept(File f)
        {
            if(f.isDirectory())
            {
                return DomeFileChooser.isNonDomeDirectory(f);
            }
            String fileModelExt = getToolExtension(f);
            return fileModelExt != null && fileModelExt.equals(this._extension);
        }

        public String getDescription()
        {
            return this._description;
        }
    }

    public static class AnalysisToolInterfaceResultsFileFilter extends FileFilter
    {
        protected String _description;
        protected String _extension;

        public AnalysisToolInterfaceResultsFileFilter(String name, String resExt)
        {
            resExt += ".rti";
            _description = name + " File (*-" + resExt + ")";
            _extension = resExt.toLowerCase();
        }

        public boolean accept(File f)
        {
            if (f.isDirectory())
            {
                return DomeFileChooser.isNonDomeDirectory(f);
            }
            String fileModelExt = getAnalysisToolInterfaceResultsExtension(f);
            return fileModelExt != null && fileModelExt.equals(_extension);
        }

        public String getDescription()
        {
            return _description;
        }
    }

	/**
	 * returns true unless it is a domeVC or interfaces- or -resources directory
	 * @param f
	 * @return
	 */
	public static boolean isNonDomeDirectory(File f)
	{
		return !(f.getName().equals("domeVC") || f.getName().startsWith("interfaces-") ||
		        f.getName().endsWith("-resources") || f.getName().endsWith("-contents"));
	}

	/**
	 * returns true if extension ends in dml, dps, dpj, rti or dmi
	 * @param f
	 * @return
	 */
	public static boolean isDomeFile(File f)
	{
		String extension = getExtension(f);
		return extension != null && DOME_FILE_EXTENSIONS.contains(extension);
	}

	/**
	 * returns true if extension ends in specified extension
	 * @param f
	 * @param ext extension in lower case
	 * @return
	 */
	public static boolean isSpecificDomeFile(File f, String ext)
	{
		String extension = getExtension(f);
		return extension != null && extension.equals(ext);
	}


	public static class DomeFileView extends FileView
	{
		public Icon getIcon(File f)
		{
			String extension = getExtension(f);
			if (extension == null)
				return null;
			else if (extension.equals(MODEL_FILE_EXTENSION))
				return DomeIcons.getIcon(DomeIcons.MODEL);
            else if (extension.equals(DomeFileChooser.ANALYSIS_TOOL_FILE_EXTENSION))
                return DomeIcons.getIcon(DomeIcons.ANALYSIS_TOOL);
            else if (extension.equals(DomeFileChooser.ANALYSIS_TOOL_INTERFACE_RESULTS_EXTENSION))
                return DomeIcons.getIcon(DomeIcons.CONTEXT);
			else if (extension.equals(PLAYSPACE_FILE_EXTENSION))
				return DomeIcons.getIcon(DomeIcons.PLAYSPACE);
			else if (extension.equals(PROJECT_FILE_EXTENSION))
				return DomeIcons.getIcon(DomeIcons.PROJECT);
			else if (extension.equals(INTERFACE_FILE_EXTENSION))
				return DomeIcons.getIcon(DomeIcons.INTERFACE);
			return null;
		}

		public String getName(File f)
		{
			String modelExtension = getModelExtension(f);
			if (modelExtension == null)
			{ // not a Dome Model file
				modelExtension = getToolExtension(f);
				if (modelExtension == null)
                {
                     modelExtension = getAnalysisToolInterfaceResultsExtension(f);
                }
                if (modelExtension != null)
				{
					return f.getName().substring(0,f.getName().lastIndexOf("-"));
				}
				if (DOME_FILE_EXTENSIONS.contains(getExtension(f)))
					return f.getName().substring(0, f.getName().lastIndexOf("."));
			}
			else
			{
				return f.getName().substring(0, f.getName().lastIndexOf("-"));
			}
			return super.getName(f);
		}
	}

	/**
	 * returns lower case of extension of file
	 * @param f
	 * @return
	 */
	public static String getExtension(File f)
	{
		return getExtension(f.getName());
	}

	/**
	 * returns lower case of extension of file
	 * @param s
	 * @return
	 */
	public static String getExtension(String s)
	{
		String ext = null;
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * returns lower case of model extension of file
	 * @param f
	 * @return
	 */
	public static String getModelExtension(File f)
	{
		return getModelExtension(f.getName());
	}

	/**
	 * returns lower case of model extension of file
	 * @param s
	 * @return
	 */
	public static String getModelExtension(String s)
	{
		String ext = getExtension(s);
		if (ext != null && ext.equals(MODEL_FILE_EXTENSION)) {
			String modelExt = null;
			int i = s.lastIndexOf('-');
			if (i > 0 && i < s.length() - 1) {
				modelExt = s.substring(i + 1).toLowerCase();
			}
			return modelExt;
		} else
			return null;
	}

    public static String getToolExtension(String s)
    {
        String ext = getExtension(s);
        if(ext != null && ext.equals(ANALYSIS_TOOL_FILE_EXTENSION))
        {
            String toolExt = null;
            int i = s.lastIndexOf('-');
            if(i > 0 && i < s.length() - 1)
            {
                toolExt = s.substring(i+1).toLowerCase();
            }
            return toolExt;
        }
        else
            return null;
    }

	public static String getToolExtension(File f)
	{
		return getToolExtension(f.getName());
	}

    public static String getAnalysisToolInterfaceResultsExtension(String s)
    {
        String ext = getExtension(s);
        if (ext != null && ext.equals(ANALYSIS_TOOL_INTERFACE_RESULTS_EXTENSION))
        {
            String resultsExt = null;
            int i = s.lastIndexOf('-');
            if (i > 0 && i < s.length() - 1)
                resultsExt = s.substring(i+1).toLowerCase();
            return resultsExt;
        }
        else
            return null;
    }

    public static String getAnalysisToolInterfaceResultsExtension(File f)
    {
        return getAnalysisToolInterfaceResultsExtension(f.getName());
    }
}
