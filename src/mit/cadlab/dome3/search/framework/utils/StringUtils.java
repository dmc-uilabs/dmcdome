package mit.cadlab.dome3.search.framework.utils;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Jan 21, 2006
 * Time: 12:23:59 AM
 * To change this template use Options | File Templates.
 */
public class StringUtils {
     public static String getfileName(String absolutePath) {
        try {
            File ifaceFile = new File(absolutePath);
            String fileName = ifaceFile.getCanonicalPath();
            int separatorindex=fileName.lastIndexOf(System.getProperty("path.separator"));
            int dotindex = fileName.lastIndexOf(".");
            if(separatorindex==-1||dotindex==-1) return absolutePath;
            return fileName.substring(separatorindex, dotindex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String rip_suffix(String originalStr, String toRip) {
        if (originalStr.indexOf(toRip) == -1)
            return originalStr;
        else {
            int lastIndex = originalStr.lastIndexOf(toRip);
            return originalStr.substring(0, lastIndex);
        }
    }
}
