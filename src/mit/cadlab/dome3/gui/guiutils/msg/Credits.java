// Credits.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;


public class Credits extends Msg
{

	private static final Dimension SIZE = new Dimension(580, 310);
	private static final String TITLE = "DOME3 Credits";
	private static final String CREDITS =
	        "Original concept: David Wallace, Nicola Senin, Francis Pahng\n" +
	        "Additional key contributors: Shaun Abrahamson, Nick Borland, Elaine Yang, Steven Kraines \n\n" +
	        "DOME3 software implementation:\n" +
	        "Qing Cao, Twiggy Chan, Charles Dumont, Ed Ferara, Renu Fondeker, Steven Kraines, Tom Ligon, Wei Mao,\n" +
	        "Nicola Senin, Prabhat Sinha, Sittha Sukkasi, Keith Thoresz, David Wallace, Jakub Wronski, Tomasz Wronski,\n" +
	        "Sane Wu, Elaine Yang, Kristie Yu\n\n" +
            "Geoff Leyland, C++ Queing Multiple Objective Optimization (QMOO) kernel, http://www.sol.co.nz/\n\n"+
	        "DOME3 uses the following open source packages:\n" +
	        "Apache xmlrpc, http://ws.apache.org/xmlrpc/; hqsldb, http://hsqldb.sourceforge.net/;\n" +
	        "Jakarta-ORO, http://jakarta.apache.org/oro/; JfreeChart, http://www.object-refinery.com/jfreechart/;\n" +
	        "Jython, http://sourceforge.net/projects/jython/; JAMA, http://math.nist.gov/javanumerics/jama/;\n" +
	        "TouchGraph, http://www.touchgraph.com/; The Regenstrief Unit Conversion Tool, \n" +
	        "http://aurora.rg.iupui.edu/~schadow/units/; SkunkDAV DAV client, http://skunkdav.sourceforge.net/;\n" +
	        "Exolab Project, http://www.exolab.org/; " +  "Colt Project, http://dsd.lbl.gov/~hoschek/colt/\n"  +
            "Ostermiller, http://ostermiller.org/utils/SignificantFigures.html\n\n" +
            "While DOME3 is a completely new implementation, prior implementations played a critical role in\n" +
	        "refining and testing the DOME concept.\n\n" +
	        "Contributors to prior implementations include:\n" +
	        "Shaun Abrahamson, Tom Almy, Francesco Bianconi, Nick Borland, JinPyung Chung, Juan Denis,\n" +
	        "Julie Eisenhard, Ed Ferara, Peter Harrigan, Paul Jackson, Jaehyun Kim, Jun-Beom Kim, Steven Kraines,\n" +
	        "Kathleen Lee, Geoff Leyland, Ben Linder, Bill Liteplo, Jeff Lyons, Shaun Meredith, Adam Molyneaux,\n" +
	        "Francis Pahng, Tina Savage, Nicola Senin, Ines Sousa, Chris Tan, Matt Wall, David Wallace,\n" +
	        "Priscilla Wang, Elaine Yang, Kristie Yu\n";

	public static void showDialog(Component parent)
	{
		JDialog d = DialogFactory.createDialog(parent, TITLE, new Credits().gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	private Credits()
	{
		gui = makeCreditsPanel();
	}

	private JPanel makeCreditsPanel()
	{
		JPanel p = new JPanel();
		JTextArea creditsTextArea = Templates.makeDisplayTextArea(CREDITS);
		JButton okButton = Templates.makeButton("OK", new CloseMsgActionListener());

		JComponent[] comps = {new JScrollPane(creditsTextArea),
		                      okButton
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 5, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBagB(p, comps, gbcs);

		// enable border to set size, then disable
		//Templates.setLineBorder(creditsTextArea);
		p.setPreferredSize(SIZE);
		return p;
	}

	public static void main(String[] args)
	{
		Credits.showDialog(null);
		System.exit(0);
	}

	public static class ShowCreditsAction extends AbstractAction
	{
		protected Component gui;

		public ShowCreditsAction(String name, Component gui)
		{
			super(name);
			this.gui = gui;
		}

		public void actionPerformed(ActionEvent event)
		{
			Credits.showDialog(gui);
		}
	}

}
