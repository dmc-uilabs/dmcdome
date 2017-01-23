package mit.cadlab.dome3.plugin.catalog.core;

import java.util.Calendar;
import java.lang.System;

/**
 * User: Sangmok Han
 * Date: 2006. 7. 3.
 */
public class CLog {
    public static final String PRINTLN_PREFIX = "[CLOG] ";
    public static final String LOG_PREFIX = "[CLOG|";
    public static final String DEBUG_PREFIX = "[DEBUG] ";
    public static final String INFO_PREFIX = "[INFO] ";
    public static final String SCRIPT_PREFIX = "[SCRIPT] ";
    public static final Calendar calendar = Calendar.getInstance();
    public static boolean debug = false;
    public static boolean info = true;
    public static boolean script = false;

    public static void println(String msg) {
        System.out.println(PRINTLN_PREFIX + msg);
    }

    public static void log(String msg) {
        System.out.println(LOG_PREFIX + calendar.getTime() + "] " + msg);
    }

    public static void debug(String msg) {
        if (debug) {
            System.out.println(DEBUG_PREFIX + msg);
        }
    }

    public static void script(String msg) {
        if (script) {
            System.out.println(SCRIPT_PREFIX + msg);
        }
    }

    /** this message will be shown unless */
    public static void info(String msg) {
        if (info) {
            System.out.println(INFO_PREFIX + msg);
        }
    }

    public static void setDebug(boolean debug) {
        CLog.debug = debug;
    }

    public static void setScript(boolean script) {
        CLog.script = script;
    }

    public static void setInfo(boolean info) {
        CLog.info = info;
    }
}
