package mit.cadlab.dome3.tool;

/**
 * Created by Jacob Wronski.
 * Date: Jun 6, 2003
 * Time: 1:52:06 PM
 * To change this template use Options | File Templates.
 */

/*
 * This will be the interface, which all dome tools should
 * implement.  The functionality of dome tools very unique for each
 * case and more methods may be added to this interface if needed.
 */

public interface AnalysisTool
{
    public void createModel();

    public void loadModel();

    public void unloadModel();

    public void deleteModel();

    public void execute();
}
