package toolinterfaceworkspace.gui.objectmodel.toolinterface.build.qmoointerface;

import mit.cadlab.dome3.tool.ToolModelBuilder;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterfaceBuilder;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 28, 2003
 * Time: 2:54:49 PM
 * To change this template use Options | File Templates.
 */
public class QMOOInterfaceDataSplitPanel extends JSplitPane
{
    private static final GridBagConstraints gbc = null;

    private ToolInterfaceBuilder _toolInterfaceBuilder;

    public QMOOInterfaceDataSplitPanel(ToolInterfaceBuilder toolInterfaceBuilder)
    {
        super(JSplitPane.VERTICAL_SPLIT);
		_toolInterfaceBuilder = toolInterfaceBuilder;
		setTopComponent(makeVariablesPanel());
		setBottomComponent(makeObjectivePanel());
		setDividerLocation(150);
    }

    protected JPanel makeVariablesPanel()
    {
        JPanel p = new JPanel();

        JComponent[] comps = {

            Templates.makeLabel("design variables:")

		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
    }

    protected JPanel makeObjectivePanel()
    {
        JPanel p = new JPanel();

        JComponent[] comps = {

            Templates.makeLabel("design objectives:")

		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

		};

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
    }
}
