package rectVolumeAndCostGUI;

import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 26, 2003
 * Time: 4:26:11 PM
 * To change this template use Options | File Templates.
 */

/**
 * Card for the login step in deployment
 */
public class VolumeAndCostGUI extends JPanel
{
	public static final GridBagConstraints gbc = null;

    private JTextField holeDiameter;
    private JTextField blockHeight;

    private JTextField blockWidth;
    private JTextField blockLength;
    private JTextField blockVolume;

    private JTextField blockCost;


    // constructor used to run the GUI in a DOME inteface
    public VolumeAndCostGUI(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

    // constructor used to run the GUI standalone
	public VolumeAndCostGUI()
	{
		JComponent[] comps = {makePanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
        };
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

        //title for the parameters section of the GUI
		JLabel parameterTitleLabel = Templates.makeLabel("Block parameters", Templates.FONT12B);
        parameterTitleLabel.setToolTipText("Dimensions of the block that can be changed by user input");

        //labels and text fields for the inputs parameters.
        JLabel diameterLabel = Templates.makeLabel("hole diameter (cm)");
        holeDiameter = Templates.makeTextField("");
        JLabel heightLabel = Templates.makeLabel("block height h (mm)");
        blockHeight = Templates.makeTextField("");

        //title for the derived variables
        JLabel dimensionsTitleLabel = Templates.makeLabel("Derived dimensions", Templates.FONT12B);
        dimensionsTitleLabel.setToolTipText("Door type: conventional front door");

        //labels and text fields for the derived geometry.
        JLabel widthLabel = Templates.makeLabel("block width w (mm)");
        blockWidth = Templates.makeTextField("");
        blockWidth.setEditable(false);
        JLabel lengthLabel = Templates.makeLabel("block length l (mm)");
        blockLength = Templates.makeTextField("");
        blockLength.setEditable(false);
        JLabel volumeLabel = Templates.makeLabel("block volume v (cubic mm)");
        blockVolume = Templates.makeTextField("");
        blockVolume.setEditable(false);

        //title for the cost variables
        JLabel costTitleLabel = Templates.makeLabel("Block cost", Templates.FONT12B);

        //labels and text fields for the cost.
        JLabel costLabel = Templates.makeLabel("block cost ($)");
        blockCost = Templates.makeTextField("");
        blockCost.setEditable(false);


        ImageIcon image = Templates.makeImageIcon("rectVolumeAndCostGUI/blockOnly.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("block nomenclature");

		JComponent[] comps = {parameterTitleLabel,
                              diameterLabel, holeDiameter,
                              heightLabel, blockHeight,

                              dimensionsTitleLabel,
                              widthLabel, blockWidth,
                              lengthLabel, blockLength,
                              volumeLabel, blockVolume,

                              costTitleLabel,
                              costLabel, blockCost,

                              imageLabel
        };

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 5, 5, 0), 0, 0),
            new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0),

            new GridBagConstraints(2, 0, 1, 9, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0)
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

    // Method to connect the custom GUI to the data in the interface
    public void setInterface(ModelInterfaceBase iface) {

        // the string is the DOME interface parameter name
        CustomGui.connectStringOrNumberTextField(iface, "diameter",holeDiameter );
        CustomGui.connectStringOrNumberTextField(iface, "h", blockHeight);
        CustomGui.connectStringOrNumberTextField(iface, "width", blockWidth);

        CustomGui.connectStringOrNumberTextField(iface, "length", blockLength);
        CustomGui.connectStringOrNumberTextField(iface, "vol", blockVolume);
        CustomGui.connectStringOrNumberTextField(iface, "cost", blockCost);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Volume and cost interface custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new VolumeAndCostGUI());
		f.show();
	}
}
