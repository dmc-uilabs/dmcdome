package jacob.tubecostmodelgui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jul 6, 2004
 * Time: 2:33:36 PM
 * To change this template use Options | File Templates.
 */
public class TubeCostModelGUI extends JPanel
{
    private static final GridBagConstraints gbc = null;

    public static final String _EMPTY_STRING = "";
    public static final String INNER_RADIUS = "Inner Radius: ";
    public static final String MM_1 = "mm";
    public static final String MM_2 = "mm";
    public static final String M_1 = "m";
    public static final String OUTER_RADIUS = "Outer Radius: ";
    public static final String TITLE = "Material and Manufacturing Cost Model for Heat Exchanger Tubing Components";
    public static final String GEOMETRY_CONFIGURATION = "Geometry Configuration: ";
    public static final String MODEL_INSTRUCTION = "\nThis interface is used to calculate the material and manufacturing cost of tubing" +
                                " for a tube bundle heat exchanger.  Users of this interface are able to modify geometry inputs as well as the tuby material type. " +
                                " The third party application behind this interface is an EXCEL spreadsheet, which calculates the total cost of fabricating a tube" +
                                " with the specified configuration. \n\nNote: thin walled tubes are difficult " +
                                "to extrude and while they perform well in heat transfer applications, they are expensive to fabricate.";
    public static final String TUBE_LENGTH = "Length of Tube: ";
    public static final String MATERIAL_SELECTION = "Material Selection: ";
    public static final String MATERIAL_TYPE_1 = "SS 316L Ultra-High Polished";
    public static final String SELECT_MATERIAL_TYPE = "Select material type: ";
    public static final String MATERIAL_PRICE = "Material Price: ";
    public static final String TOTAL_COST_OF_TUBING = "Total cost of tubing: ";
    public static final String DOLLAR_PER_CC = "$/cc";
    public static final String COST_OF_TUBING_UNIT = "/ 1000 tubes";

    public static final String[] MATERIAL_TYPES = {

        MATERIAL_TYPE_1
    };

    public static final JLabel titleLabel = Templates.makeLabel(TITLE, Templates.FONT12B);
    public static final JLabel innerRadiusLabel = Templates.makeLabel(INNER_RADIUS);
    public static final JLabel outerRadiusLabel = Templates.makeLabel(OUTER_RADIUS);
    public static final JLabel geometryConfigurationLabel = Templates.makeLabel(GEOMETRY_CONFIGURATION);
    public static final JLabel materialSelectionLabel = Templates.makeLabel(MATERIAL_SELECTION);
    public static final JLabel tubeLengthLabel = Templates.makeLabel(TUBE_LENGTH);
    public static final JLabel selectMaterialTypeLabel = Templates.makeLabel(SELECT_MATERIAL_TYPE);
    public static final JLabel materialPriceLabel = Templates.makeLabel(MATERIAL_PRICE);
    public static final JLabel totalCostOfTubingLabel = Templates.makeLabel(TOTAL_COST_OF_TUBING);
    public static final JLabel mm1Label = Templates.makeLabel(MM_1);
    public static final JLabel mm2Label = Templates.makeLabel(MM_2);
    public static final JLabel m1Label = Templates.makeLabel(M_1);
    public static final JLabel dPerCC = Templates.makeLabel(DOLLAR_PER_CC);
    public static final JLabel cTUnit = Templates.makeLabel(COST_OF_TUBING_UNIT);
    public static final JLabel emptyLabel = Templates.makeLabel(_EMPTY_STRING);

    private JLabel _tubeImageLabel;
    private JTextArea _instructionLabel;

    private JTextField _innerRadiusTextField, _outerRadiusTextField, _tubeLengthTextField,
                            _materialPriceTextField, _totalCostOfTubingTextField;

    private JComboBox _materialTypes;

    private ModelInterfaceBase _iface = null;

    public TubeCostModelGUI (ModelInterfaceBase iface)
    {
        _iface = iface;

        createComponents();

        layoutComponents();

        setInterface(iface);
    }

    public TubeCostModelGUI ()
    {
        createComponents();

        layoutComponents();
    }

    protected void createComponents()
    {
        _instructionLabel = Templates.makeDTextArea(MODEL_INSTRUCTION);

        _instructionLabel.setOpaque(false);
        _instructionLabel.setEditable(false);

        ImageIcon tubeImage = Templates.makeImageIcon("jacob/tubecostmodelgui/images/CostModelTube.gif");
        _tubeImageLabel = new JLabel(tubeImage);

        _innerRadiusTextField = Templates.makeTextField(_EMPTY_STRING);
        _outerRadiusTextField = Templates.makeTextField(_EMPTY_STRING);
        _tubeLengthTextField = Templates.makeTextField(_EMPTY_STRING);
        _materialPriceTextField = Templates.makeTextField(_EMPTY_STRING);
        _totalCostOfTubingTextField = Templates.makeTextField(_EMPTY_STRING);

        _materialTypes = Templates.makeComboBox(MATERIAL_TYPES);
    }

    protected void layoutComponents()
    {

        JComponent[] comps = {

            titleLabel,

            _instructionLabel, _tubeImageLabel,

            makeInputsPanel(),

            makeOutputPanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(5, 10, 10, 10), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(15, 10, 0, 10), 0, 0),

            new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),

            new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(this, comps, gbcs);
    }

    private JPanel makeInputsPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, " Cost Model Inputs ", 0, 0, Templates.FONT11));

        JComponent[] comps = {

            geometryConfigurationLabel,

            innerRadiusLabel, _innerRadiusTextField, mm1Label,

            outerRadiusLabel, _outerRadiusTextField, mm2Label,

            tubeLengthLabel, _tubeLengthTextField, m1Label,

            materialSelectionLabel,

            selectMaterialTypeLabel, _materialTypes, emptyLabel,

            materialPriceLabel, _materialPriceTextField, dPerCC
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(10, 10, 5, 10), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0),

            new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0),

            new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(15, 10, 5, 10), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 0), 0, 0),
            new GridBagConstraints(2, 5, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0),

            new GridBagConstraints(0, 6, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(5, 10, 15, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 15, 0), 0, 0),
            new GridBagConstraints(2, 6, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 10, 15, 10), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    private JPanel makeOutputPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(null, " Cost Model Outputs ", 0, 0, Templates.FONT11));

        JComponent[] comps = {

            totalCostOfTubingLabel, _totalCostOfTubingTextField, cTUnit,

            new JPanel()
        };

        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTH, gbc.NONE, new Insets(10, 10, 5, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(10, 15, 5, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(10, 10, 5, 10), 0, 0),

            new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);

        return p;
    }

    public void setInterface(ModelInterfaceBase iface)
    {

        CustomGui.connectStringOrNumberTextField(iface, "Inner Radius", _innerRadiusTextField);
        CustomGui.connectStringOrNumberTextField(iface, "Outer Radius", _outerRadiusTextField);
        CustomGui.connectStringOrNumberTextField(iface, "Length", _tubeLengthTextField);
        CustomGui.connectStringOrNumberTextField(iface, "Price Per Cubic Centimeter", _materialPriceTextField);
        CustomGui.connectStringOrNumberTextField(iface, "Material Cost", _totalCostOfTubingTextField);
    }

    public static void main (String[] args)
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new TubeCostModelGUI());
        f.pack();
        f.show();
    }
}
