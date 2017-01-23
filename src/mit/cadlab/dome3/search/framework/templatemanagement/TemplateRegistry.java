package mit.cadlab.dome3.search.framework.templatemanagement;

import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.search.framework.searchagent.search.SearchInit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;


/**
 * a class as a container to hold multiple templates
 */
public class TemplateRegistry  {
    private static boolean templatesLoaded = false;
    private static HashMap registered_templates = new HashMap();
    public static String templateDir = SearchInit.userdir + System.getProperty("file.separator") + SearchInit.template_folder;
    private static PropertyChangeSupport listener=new PropertyChangeSupport(registered_templates);

    public static void addTemplate(Template templatetoadd) {
        registered_templates.put(templatetoadd.getId(), templatetoadd);
        listener.firePropertyChange("Template Added",null,templatetoadd);
    }

    public static void addListenerToTemplateRegistry(PropertyChangeListener lis){
        listener.addPropertyChangeListener(lis);
    }

    //write into file system
    public static void save() {
        File templateDirectory=new File(templateDir);
        if (!templateDirectory.exists()) {
            templateDirectory.mkdir();
        }
        for (Iterator iter = registered_templates.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            Template t = (Template) entry.getValue();
            saveTemplate(t);
        }
    }

    public static void dumpAll() {
        System.out.println("dumping templates....");
        registered_templates.clear();
        //FILE FOLDER CLEAR

    }

    public static void loadTemplates() {
        //read from file system
        //recursive all the files under the folder
        File templateDirectory=new File(templateDir);
        if (!templateDirectory.exists()||!templateDirectory.isDirectory()) {
            System.out.println("TemplateRegistry: Error in loading templates, can't find templates files");
            return;
        }
        loadTemplatesFromDirectory(templateDirectory);
        templatesLoaded = true;
    }

    public static void SaveTemplates(){
       for(Iterator i=registered_templates.values().iterator();i.hasNext();)
       {
           Template t=(Template)i.next();
           String filelocation=saveTemplate(t);
           System.out.println(t.getName()+" saved to disk "+filelocation);
       }
    }

    public static void loadTemplatesFromDirectory(File dir){
        File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				loadTemplatesFromDirectory(f);  // recurse
			} else
            {
				Template t=loadTemplate(f.getPath());
                addTemplate(t);
			}
		}
    }

    public static String saveTemplate(Template t) {
        String fileLocation = templateDir + System.getProperty("file.separator") + t.getId();
        try {
            File dir=new File(templateDir);
            if(!dir.exists()) dir.mkdir();
            File f=new File(fileLocation);
            if(!f.exists()) f.createNewFile();
            XMLUtils.writeToFile(t.toXmlElement(), f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLocation;
    }


    public static Template loadTemplate(String fileLocation) {
        try {
            String xmlString = FileUtils.readTextFileAsString(fileLocation);
            Template t=new Template(XMLUtils.stringToXmlElement(xmlString));
            addTemplate(t);
            return t;
        } catch (java.io.IOException e) {
            System.err.println("Error reading template file: " + fileLocation);
            return null;
        }
    }

    public static Template getTemplate(String templateID){
        return (Template)registered_templates.get(templateID);
    }

    public static ArrayList getAllTemplates(){
        return new ArrayList(registered_templates.values());
    }

    public static boolean templatesLoaded(){
        return templatesLoaded;
    }
}
