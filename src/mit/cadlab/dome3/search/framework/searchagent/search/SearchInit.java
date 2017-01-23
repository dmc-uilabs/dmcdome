package mit.cadlab.dome3.search.framework.searchagent.search;

import edu.iupui.rg.ucum.units.UnitTab;
import mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes.CommonUnit;
import mit.cadlab.dome3.search.framework.templatemanagement.TemplateRegistry;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Qing
 * Date: Aug 17, 2005
 * Time: 4:48:38 PM
 * To change this template use Options | File TemplateRegistry.
 */
public class SearchInit {
    private static boolean initialized = false;
    public static String userdir = System.getProperty("user.dir");
    public static String template_folder="templates";

	//eligble for local files
    private static String[] eligble_suffix={".dmi",".dpi"};

    public static void initialize() {
        if (initialized) {
            System.err.println("mit.cadlab.mit.cadlab.dome3.search.SearchInit.initialize should not be called multiple times!");
            return;
        }
         // register units
        loadUnits();
 //       loadTemplates();
        initialized = true;

    }

    private static void loadTemplates() {
        //in TemplateRegistry, load template
        TemplateRegistry.loadTemplates();
    }


    public static void loadUnits() {
        try {
            InputStream is = new ByteArrayInputStream(CommonUnit.DATA.getBytes());
            UnitTab.read(is);
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading unit data");
        }

    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        SearchInit.initialized = initialized;
    }

    public static String[] getEligble_suffix() {
        return eligble_suffix;
    }

    public static void setEligble_suffix(String[] eligble_suffix) {
        SearchInit.eligble_suffix = eligble_suffix;
    }
}