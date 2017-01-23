package statusWindow;

import mit.cadlab.dome3.swing.Templates;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
public class StatusWindow extends JPanel
{
	public static final GridBagConstraints gbc = null;

    public static final Dimension DEFAULT_SIZE = new Dimension(260, 100);

    public static final String OPENING_FILE = "Opening file:";
    public static final String SAVING_FILE = "Saving file:";
    public static final String CLOSING_FILE = "Closing file:";
    public static final String STARTING = "Starting:";
    public static final String DEPLOYING = "Moving to server:";

    private JLabel statusMessage;
    private JLabel objectName;


    public StatusWindow(String message, String name){
        this();
        statusMessage.setText(message);
        objectName.setText(name);
    }


	public StatusWindow()
	{
        statusMessage = Templates.makeLabel("", Templates.FONT12I);
        objectName =    Templates.makeLabel("", Templates.FONT12B);

        ImageIcon image = Templates.makeImageIcon("statusWindow/waitCursor.gif");
        JLabel imageLabel = new JLabel(image);

		JComponent[] comps = {statusMessage, objectName, imageLabel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)
        };
		Templates.layoutGridBag(this, comps, gbcs);
        setPreferredSize(DEFAULT_SIZE);
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("DOME Status");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new StatusWindow(OPENING_FILE, "filename"));
        f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
