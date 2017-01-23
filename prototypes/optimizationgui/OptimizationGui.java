package optimizationgui;

import mit.cadlab.dome3.objectmodel.model.tool.AnalysisToolBase;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.toolinterface.optimizationinterface.run.OptimizationInterfaceRuntimeClient;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimizationinterface.OptimizationInterfaceResultsPanel;
import mit.cadlab.dome3.gui.objectmodel.toolinterface.run.optimizationinterface.optimizationinterfaceresultspanelcomponents.ParetoFrontPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 28, 2004
 * Time: 11:11:41 AM
 * To change this template use Options | File Templates.
 */
public class OptimizationGui extends JPanel
{
    public static final GridBagConstraints gbc = null;

    public static final String _EMPTY_STRING = "";

    public static final String X_PARAM = "x";
    public static final String Y_PARAM = "y";
    public static final String Z_PARAM = "z";

    private AnalysisToolInterfaceBase _tface;
    private ModelInterfaceBase _mface;

    private JTextField _xField, _yField, _zField;

    public OptimizationGui(AnalysisToolInterfaceBase tface)
    {
        this();
        _tface = tface;
        layoutComponents();
    }

    public OptimizationGui(ModelInterfaceBase mface)
    {
        this();
        _mface = mface;
    }

    public OptimizationGui()
    {
        createComponents();

    }

    protected void createComponents()
    {
    }

    protected JPanel createVariablesPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, "  variables:  ", 0, 0, Templates.FONT11));

        _xField = Templates.makeTextField(_EMPTY_STRING);

        JComponent[] comps = {

            Templates.makeLabel("x: "), _xField
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    protected JPanel createObjectivePanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, "  objectives:  ", 0, 0, Templates.FONT11));

        _yField = Templates.makeTextField(_EMPTY_STRING);
        _zField = Templates.makeTextField(_EMPTY_STRING);

        JComponent[] comps = {

            Templates.makeLabel("y: "), _yField,
            Templates.makeLabel("z: "), _zField,
            new JPanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    protected JPanel createParetoPanel()
    {
        if (_tface instanceof OptimizationInterfaceRuntimeClient)
            return new ParetoFrontPanel();
        else
            return new JPanel();
    }

    protected void layoutComponents()
    {
        JComponent[] comps = {

            createVariablesPanel(), createObjectivePanel(), createParetoPanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(5, 5, 5, 5), 0, 0),

            new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.getContentPane().add(new OptimizationGui());
        f.pack();
        f.show();
    }
}
