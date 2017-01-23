package mit.cadlab.dome3.plugin.javajar;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jmekler
 * Date: 10/3/11
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface JarModelPluginInterface {
    public static String EXECUTE_METHOD = "execute";
    public static String ISLOADED_METHOD = "isLoaded";

    public void execute();

    public boolean isLoaded();
}
