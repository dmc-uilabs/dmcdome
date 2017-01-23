package mit.cadlab.dome3.gui.objectmodel.toolinterface.run;

import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;

import javax.swing.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 28, 2003
 * Time: 2:16:46 PM
 * To change this template use Options | File Templates.
 */
public abstract class AnalysisToolInterfaceRunPanel extends AbstractDomeObjectGui
{
    protected ToolInterface _ti;

    public AnalysisToolInterfaceRunPanel(ToolInterface ti)
    {
        super(ti);

        _ti = ti;
    }

}
