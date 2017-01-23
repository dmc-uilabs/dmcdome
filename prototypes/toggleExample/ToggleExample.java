package toggleExample;

import mit.cadlab.dome3.swing.Templates;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 20, 2003
 * Time: 11:20:40 PM
 * To change this template use Options | File Templates.
 *
 */
public class ToggleExample extends JPanel
{
	public static final Dimension DEFAULT_SIZE = new Dimension(200, 500);
	public static final GridBagConstraints gbc = null;

    private JToggleButton toggle1;
    private JToggleButton toggle2;

	{
		JPanel p = makePanel();

		JComponent[] comps = {p};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		//make the icons
        ImageIcon graphic1 = Templates.makeImageIcon("toggleExample/images/air.gif");
        ImageIcon graphic2 = Templates.makeImageIcon("toggleExample/images/seal.gif");

        //make the buttons
        toggle1 = new JToggleButton(graphic1);
        toggle1.setSelectedIcon(graphic2);
        toggle1.setToolTipText("toggle button with icon and selected icon");
        toggle1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("toggle1 action");
                System.out.println(e);
            }
        });

        toggle2 = new JToggleButton(graphic1);
        toggle2.setSelectedIcon(graphic2);
        toggle2.setToolTipText("toggle button with icon and selected icon");
        toggle2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("toggle2 action");
                System.out.println(e);
            }
        });

        //setup the button group
        ButtonGroup group = new ButtonGroup();
        group.add(toggle1);
        group.add(toggle2);

		JComponent[] comps = {toggle1, toggle2};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.CENTER, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0)
         };
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Toggle example");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ToggleExample());
		f.setSize(DEFAULT_SIZE);
		f.show();
	}
}
