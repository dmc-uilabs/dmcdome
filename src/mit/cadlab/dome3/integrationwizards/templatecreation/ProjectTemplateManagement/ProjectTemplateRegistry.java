package mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement;

import mit.cadlab.dome3.integrationwizards.templatecreation.DomeiModelXMLHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import mit.cadlab.dome3.util.xml.XMLUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Jan 10, 2007
 * Time: 2:34:02 PM
 * To change this template use Options | File Templates.
 */
public class ProjectTemplateRegistry {

    public static boolean templatesLoaded = false;
    public static String userdir = System.getProperty("user.dir");
    public static String templateDir = userdir + System.getProperty("file.separator") + "ProjectTemplates";
    private static ArrayList templates = new ArrayList();

    public ArrayList getTemplates()
    {
        return templates;
    }

    //Load Project Templates from the template directory
    public static void loadTemplates() {
        //read from file system
        //recursive all the files under the folder
        File templateDirectory=new File(templateDir);
        if (!templateDirectory.exists()||!templateDirectory.isDirectory()) {
            System.out.println("TemplateRegistry: Error in loading templates, can't find templates files");
            return;
        }
        loadTemplates(templateDirectory);
        templatesLoaded = true;
    }

    private static void loadTemplates(File dir)
    {
       File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory())
				loadTemplates(f);  // recurse
            else
            {
                TemplateXMLReader reader = new TemplateXMLReader();
                try
                {
				    ProjectTemplate template = reader.getTemplateData(f);
                    registerTemplate(template);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
			}
		}
    }

    //Register the template in the template ArrayList
    private static void registerTemplate(ProjectTemplate temp)
    {
        templates.add(temp);
    }

    //Create a template from an integration project
    public static ProjectTemplate createTemplate(String xmlContent,String projectName)
    {
        ProjectTemplate project = null;
        DomeiModelXMLHandler handler = new DomeiModelXMLHandler();
        try
        {
            project = handler.getProjectData(xmlContent,projectName);
            if(project!=null)
            {
                registerTemplate(project);
                saveTemplate(project);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return project;
    }

    //Save the template to the project template directory
    private static String saveTemplate(ProjectTemplate data)
    {
        String fileLocation = templateDir + System.getProperty("file.separator") + data.getID();
        if(data!=null)
        {
            try
            {
                File dir=new File(templateDir);
                if(!dir.exists()) dir.mkdir();
                File f=new File(fileLocation);
                if(!f.exists()) f.createNewFile();
                XMLUtils.writeToFile(data.toXmlElement(), f);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return fileLocation;
         }
         return null;
    }

    public static boolean templatesLoaded(){
        return templatesLoaded;
    }

   /* public static void createTemplates(String dir)
    {
        File directory=new File(dir);
        if (!directory.exists()||!directory.isDirectory()) {
            System.out.println("Template Creation Error: Can't find integration project files");
            return;
        }
        File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory());
            else
            {
                System.out.println(f.getName());
                createTemplate(f.getAbsolutePath());
			}
		}

    }   */
}
