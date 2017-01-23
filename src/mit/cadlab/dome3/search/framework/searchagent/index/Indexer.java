package mit.cadlab.dome3.search.framework.searchagent.index;

import mit.cadlab.dome3.search.framework.utils.processing.DomeXMLHandler;
import mit.cadlab.dome3.search.framework.utils.processing.InterfaceData;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;


/**
 *
 */
public class Indexer {

    public static void loadDirectory(ArrayList interfaces, File dir) {
        if(interfaces==null||!dir.exists()) {
            System.out.println("invalid arguements for Indexer.loadDirectory()");
            return;
        }
        DomeXMLHandler handler = new DomeXMLHandler();
        try {
            long start = new Date().getTime();

            File[] files = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isDirectory()) {
                    loadDirectory(interfaces, f);  // recurse
                } else if (f.getName().endsWith(".dmi") || f.getName().endsWith(".dpi")) {
                    System.out.println("loading "+f.getName()+"...");
                    InterfaceData iface11 = handler.getInstanceData(f);
                    if (iface11 != null){
                        if(interfaces==null) interfaces=new ArrayList();
                        interfaces.add(iface11);
                    }
                }
            }
            long end = new Date().getTime();

            System.out.println("loading " + interfaces.size() + " files took "
                    + (end - start) + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
