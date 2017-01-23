package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.plugin.excel.ExcelConfiguration;
import mit.cadlab.dome3.plugin.ideas10.Ideas10Configuration;
import mit.cadlab.dome3.plugin.matlab.MatlabConfiguration;

public class PluginConstants {
    public static final String EXCEL = ExcelConfiguration.TYPE_INFO.getTypeName();
    public static final String MATLAB = MatlabConfiguration.TYPE_INFO.getTypeName();
    public static final String IDEAS = Ideas10Configuration.TYPE_INFO.getTypeName();

    public static final String SOFTWARE_VERSION = "software version";
    public static final String RUN_IN_FOREGROUND = "run in foreground";
}
