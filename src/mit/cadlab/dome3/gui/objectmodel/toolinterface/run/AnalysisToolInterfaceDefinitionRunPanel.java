package mit.cadlab.dome3.gui.objectmodel.toolinterface.run;

import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Nov 28, 2003
 * Time: 2:18:33 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolInterfaceDefinitionRunPanel extends JPanel
{
    protected static GridBagConstraints gbc;
    protected static ImageIcon comboArrow = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrow.gif");
    protected static ImageIcon comboArrowOver = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrowOver.gif");
    protected static Color notEditableColor = new Color(105, 105, 105);
    protected static String[] visualizations = {"list", "graph", "dsm"};

}
