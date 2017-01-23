package mit.cadlab.dome3.search.framework.utils.processing;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.framework.searchagent.index.Indexer;
import mit.cadlab.dome3.search.framework.templatemanagement.Template;
import mit.cadlab.dome3.search.framework.templatemanagement.TemplateRegistry;
import mit.cadlab.dome3.search.framework.utils.StringUtils;
import mit.cadlab.dome3.search.framework.utils.processing.InterfaceData;
import mit.cadlab.dome3.search.framework.utils.processing.DomeXMLHandler;
import mit.cadlab.dome3.search.framework.searchagent.search.SearchInit;
import mit.cadlab.dome3.util.xml.XMLUtils;

import java.io.File;
import java.util.ArrayList;

import org.dom4j.Element;

/**
 *    call this class to create templates from test suit
 *    Change by Ligon: tempalte id and filename now corresponds to the interfaces id
 */
public class CreateTemplates {
    ArrayList TestSuite;
    String userdir = System.getProperty("user.dir");
    boolean interfaceLoad = false;

    public CreateTemplates() {
        TestSuite = new ArrayList();
        LoadDir();
    }

    public void LoadDir() {

        String file1 = userdir + "\\interfaces";
        System.out.println(file1);
        File dir = new File(file1);
        Indexer.loadDirectory(TestSuite, dir);
        interfaceLoad = true;
    }

    public Template createTemplateFromInterface(InterfaceData data) {
        //create a template
        try {
            File interfaceFile = new File(data.getLocation());
            String fileName = StringUtils.rip_suffix(interfaceFile.getName(), ".dmi");
            Template t = new Template(data.getId(), new FuzzyARG(data.getGraph()),data.getIfacename(), data.getDimensionList());
            TemplateRegistry.addTemplate(t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Template createTemplateFromInterface(String fileLoc) {

        InterfaceData iface12 = null;

        try {
            DomeXMLHandler handler = new DomeXMLHandler();
            iface12 = handler.getInstanceData((new File(fileLoc)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (iface12 != null) {
            File interfaceFile = new File(fileLoc);
            String fileName = StringUtils.rip_suffix(interfaceFile.getName(), ".dmi");
            Template t = new Template(iface12.getId(), new FuzzyARG(iface12.getGraph()), "template created from \" "+iface12.getIfacename() +"\"", iface12.getDimensionList());
            TemplateRegistry.addTemplate(t);
            System.out.println(t.getId() + "created");
            TemplateRegistry.saveTemplate(t);

        }
        return null;

    }

    public static boolean createTemplateFromXML(Element xmlElement){
        InterfaceData iface12 = null;
        try {
            DomeXMLHandler handler = new DomeXMLHandler();
            iface12 = handler.loadInterface(xmlElement);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (iface12 != null) {
            Template t = new Template(iface12.getId(), new FuzzyARG(iface12.getGraph()),iface12.getIfacename(), iface12.getDimensionList());
            TemplateRegistry.addTemplate(t);
            TemplateRegistry.saveTemplate(t);
            return true;
        }
        return false;
    }

    public void createTemplates() {
        if (!interfaceLoad) LoadDir();

        for (int i = 0; i < TestSuite.size(); i++) {
            Template t = createTemplateFromInterface((InterfaceData) TestSuite.get(i));
            System.out.println("Template "+t.getId() + " created");
            TemplateRegistry.saveTemplate(t);
        }

    }

    public static void main(String[] args) {
       CreateTemplates category = new CreateTemplates();
       category.createTemplates();
       String file1 = SearchInit.userdir + "\\interfaces\\57.dmi";
       //category.createTemplateFromInterface(file1);
    }
}
