package fullWindNoiseToolGUI;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.guiutils.customGui.CustomGui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
public class DoorGeometryCard extends JPanel
{

	public static final GridBagConstraints gbc = null;

    private JTextField aX;
    private JTextField aY;
    private JTextField aZ;

    private JTextField bX;
    private JTextField bY;
    private JTextField bZ;

    private JTextField cX;
    private JTextField cY;
    private JTextField cZ;

    private JTextField dX;
    private JTextField dY;
    private JTextField dZ;

    private JTextField eX;
    private JTextField eY;
    private JTextField eZ;

    private JTextField fX;
    private JTextField fY;
    private JTextField fZ;

    private JTextField cgX;
    private JTextField cgY;
    private JTextField cgZ;

    private JTextField mass;
    //private JTextField dropOff;
    private JTextField iZZ;
    private JTextField cheat;

	public DoorGeometryCard()
	{
		JComponent[] comps = {makeTopPanel(), makeBottomPanel(), makePointPanel(), };
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 0), 0, 0)
        };
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makeTopPanel()
	{
		JPanel p = new JPanel();
		JLabel programLabel = Templates.makeLabel("Conventional Front Door geometry", Templates.FONT12B);
        programLabel.setToolTipText("Front-hinged doors with one latch. The front door closes and latches on the rear door.");

        JTextArea programText = Templates.makeDTextArea("This panel is used to input overall dimensions of the door and some of its properties, "+
                                 "such as moment-of-inertia and mass. These data affect the door closing effort tool that drives ADAMS standard and teardown effort simulations\n\n"+
                                 "In a production tool, these data would drive a parametric CAD model of the door, and then door properties would be derived from the actual geometry. Such a model was not available for the pilot project.");
        programText.setOpaque(false);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/geometry.gif");
        JLabel imageLabel = new JLabel(image);
        imageLabel.setToolTipText("door geometry nomenclature");

		JComponent[] comps = {programLabel, programText, imageLabel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			//new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.BOTH, new Insets(10, 5, 0, 0), 0, 0),
            //new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 4, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(5, 5, 5, 5), 0, 0),
		};

		Templates.layoutGridBag(p, comps, gbcs);

		return p;
	}

    private JScrollPane makePointPanel()
    {
        JPanel p = new JPanel();

        JLabel geometryPointsLabel = Templates.makeLabel("Geometry points are used to define the seals door area.", Templates.FONT12B);
        JLabel geometryPointsLabel2 = Templates.makeLabel("Choose geometry points to approximate door seal perimeter.", Templates.FONT12B);

        JLabel xLabel = Templates.makeLabel("x",Templates.FONT12B);
        xLabel.setToolTipText("x is in the fore/aft direction (positive points towards the aft direction)");

        JLabel yLabel = Templates.makeLabel("y", Templates.FONT12B);
        yLabel.setToolTipText("y is inboard/outboard (positive points outboard)");

        JLabel zLabel = Templates.makeLabel("z", Templates.FONT12B);
        zLabel.setToolTipText("z is up/down (positive points upward)");

        JLabel aPointLabel = Templates.makeLabel("A point (mm)");
        aPointLabel.setToolTipText("geometry point required to define the edge of the door");
        aX = Templates.makeTextField("");
        aY = Templates.makeTextField("");
        aZ = Templates.makeTextField("");

        JLabel bPointLabel = Templates.makeLabel("B point (mm)");
        bPointLabel.setToolTipText("geometry point required to define the edge of the door");
        bX = Templates.makeTextField("");
        bY = Templates.makeTextField("");
        bZ = Templates.makeTextField("");

        JLabel cPointLabel = Templates.makeLabel("C point (mm)");
        cPointLabel.setToolTipText("geometry point required to define the edge of the door");
        cX = Templates.makeTextField("");
        cY = Templates.makeTextField("");
        cZ = Templates.makeTextField("");

        JLabel dPointLabel = Templates.makeLabel("D point (mm)");
        dPointLabel.setToolTipText("geometry point required to define the edge of the door");
        dX = Templates.makeTextField("");
        dY = Templates.makeTextField("");
        dZ = Templates.makeTextField("");

        JLabel ePointLabel = Templates.makeLabel("E point (mm)");
        ePointLabel.setToolTipText("geometry point required to define the edge of the door");
        eX = Templates.makeTextField("");
        eY = Templates.makeTextField("");
        eZ = Templates.makeTextField("");

        JLabel fPointLabel = Templates.makeLabel("F point (mm)");
        fPointLabel.setToolTipText("geometry point required to define the edge of the door");
        fX = Templates.makeTextField("");
        fY = Templates.makeTextField("");
        fZ = Templates.makeTextField("");

        JLabel cgLabel = Templates.makeLabel("center of gravity (mm)");
        cgLabel.setToolTipText("Door center of gravity. Include the door sheet metal and all parts directly attached to the door");
        cgX = Templates.makeTextField("");
        cgY = Templates.makeTextField("");
        cgZ = Templates.makeTextField("");

        JPanel fill = new JPanel();

        JComponent[] comps = {geometryPointsLabel, geometryPointsLabel2, xLabel, yLabel, zLabel,
                              aPointLabel, aX, aY, aZ,
                              bPointLabel, bX, bY, bZ,
                              cPointLabel, cX, cY, cZ,
                              dPointLabel, dX, dY, dZ,
                              ePointLabel, eX, eY, eZ,
                              fPointLabel, fX, fY, fZ,
                              cgLabel, cgX, cgY, cgZ,
                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 4, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 4, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 5, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 6, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 7, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 8, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 8, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 9, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 9, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(3, 9, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 10, 1, 1, 0.0, 1.0, gbc.CENTER, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);
        JScrollPane scroll = new JScrollPane(p);
        return scroll;

    }

    private JPanel makeBottomPanel() {
        JPanel p = new JPanel();

        JLabel massLabel = Templates.makeLabel("weight (lbf)");
        massLabel.setToolTipText("Door weight. Include the door sheet metal and all parts directly attached to the door");
        mass = Templates.makeTextField("");

        JLabel inertiaLabel = Templates.makeLabel("inertia Izz (kg-mm^2) ");
        inertiaLabel.setToolTipText("Door mass moment of inertia. Include the door sheet metal and all parts directly attached to the door");
        iZZ = Templates.makeTextField("");

        // dropoff not used
        //JLabel dropOffLabel = Templates.makeLabel("drop off (mm)");
        //dropOffLabel.setToolTipText("Amount of drop (sag) at the latch relative to the nominal striker position");
       // dropOff = Templates.makeTextField("");

        JLabel cheatLabel = Templates.makeLabel("cheat (mm)");
        cheatLabel.setToolTipText("Amount of displacement that the top of the door edgeline goes through at closing due to frame bending");
        cheat = Templates.makeTextField("");

        JPanel fill = new JPanel();

        JComponent[] comps = {massLabel, mass,
                              inertiaLabel, iZZ,
                              //dropOffLabel, dropOff,
                              cheatLabel, cheat,
                              fill
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            //new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            //new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    public void setInterface(ModelInterfaceBase iface) {
        CustomGui.connectStringOrNumberTextField(iface, "x_A", aX);
        CustomGui.connectStringOrNumberTextField(iface, "y_A", aY);
        CustomGui.connectStringOrNumberTextField(iface, "z_A", aZ);

        CustomGui.connectStringOrNumberTextField(iface, "x_B", bX);
        CustomGui.connectStringOrNumberTextField(iface, "y_B", bY);
        CustomGui.connectStringOrNumberTextField(iface, "z_B", bZ);

        CustomGui.connectStringOrNumberTextField(iface, "x_C", cX);
        CustomGui.connectStringOrNumberTextField(iface, "y_C", cY);
        CustomGui.connectStringOrNumberTextField(iface, "z_C", cZ);

        CustomGui.connectStringOrNumberTextField(iface, "x_D", dX);
        CustomGui.connectStringOrNumberTextField(iface, "y_D", dY);
        CustomGui.connectStringOrNumberTextField(iface, "z_D", dZ);

        CustomGui.connectStringOrNumberTextField(iface, "x_E", eX);
        CustomGui.connectStringOrNumberTextField(iface, "y_E", eY);
        CustomGui.connectStringOrNumberTextField(iface, "z_E", eZ);

        CustomGui.connectStringOrNumberTextField(iface, "x_F", fX);
        CustomGui.connectStringOrNumberTextField(iface, "y_F", fY);
        CustomGui.connectStringOrNumberTextField(iface, "z_F", fZ);

        CustomGui.connectStringOrNumberTextField(iface, "x_CG", cgX);
        CustomGui.connectStringOrNumberTextField(iface, "y_CG", cgY);
        CustomGui.connectStringOrNumberTextField(iface, "z_CG", cgZ);

        CustomGui.connectStringOrNumberTextField(iface, "Door Mass", mass);
        CustomGui.connectStringOrNumberTextField(iface, "Izz", iZZ);
        CustomGui.connectStringOrNumberTextField(iface, "Door Cheat", cheat);

        //dropOff;     drop off not used
        //dropOff.setEnabled(false);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Program and geometry card");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DoorGeometryCard());
		f.show();
	}
}
