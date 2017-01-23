// Copyright.java
package mit.cadlab.dome3.gui.guiutils.msg;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.LayeredCenterLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class Copyright extends Msg
{

	public static final String VERSION = "DOME3 beta 1"; // should be in constants
	public static final int DECLINE_OPTION = 0;
	public static final int ACCEPT_OPTION = 1;

	private static final Dimension SIZE = new Dimension(650, 435);
	private static final String TITLE = "DOME3 Copyright";

	private static final String COPYRIGHT =
	        "Copyright (c) 2012, David Wallace and Elaine Yang \n" +
	        "All rights reserved.";

	private static final String USE_POLICY =
	        "Redistributions of source code must retain the above copyright notice,\n" + 
	        "this list of conditions and the following disclaimer.\n" +
	        "Redistributions in binary form must reproduce the above copyright notice,\n" + 
	        "this list of conditions and the following disclaimer\n" +
	        "in the documentation and/or other materials provided with the distribution.\n\n\n" +
	        "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"\n" +
	        "AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n" +
	        "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. \n" + 
	        "IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,\n" +
	        "INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,\n" +
	        "BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\n" +
	        "LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED\n" +
	        "AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, \n" + 
	        "OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF\n" +
	        "THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
	 
	public static int showAskAcceptanceDialog(Component parent)
	{
		Copyright c = new Copyright(true);
		JDialog d = DialogFactory.createDialog(parent, TITLE, c.gui, true, false);
		d.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		d.show();
		return c.answer;
	}

	public static void showInformationDialog(Component parent)
	{
		JDialog d = DialogFactory.createDialog(parent, TITLE, new Copyright(false).gui, true, false);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
	}

	private Copyright(boolean askAcceptance)
	{
		gui = makeCopyrightPanel(askAcceptance);
		answer = DECLINE_OPTION; // default
	}

	private JLayeredPane makeCopyrightPanel(boolean askAcceptance)
	{
		JPanel t = new JPanel();
		JLabel versionLabel = Templates.makeLabel(VERSION, Templates.FONT11B);
		JLabel termsLabel = Templates.makeLabel("Terms of use", Templates.FONT11B);
		JTextArea copyrightTextArea = Templates.makeDisplayTextArea(COPYRIGHT);
		JTextArea useTextArea = Templates.makeDisplayTextArea(USE_POLICY);
		JPanel buttonPanel = askAcceptance ? createAskAcceptancePanel(t) : createOkPanel(t);

		//
		JPanel i = new JPanel();
		ImageIcon icon = Templates.makeImageIcon("mit/cadlab/dome3/icons/copyrightBackground.gif");
		JLabel iconLabel = new JLabel(icon, SwingConstants.RIGHT);
		JComponent[] bcomps = {iconLabel};
		GridBagConstraints[] bgbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0)};
		Templates.layoutGridBag(i, bcomps, bgbcs);
		//

		JComponent[] comps = {versionLabel,
		                      termsLabel,
		                      copyrightTextArea,
		                      useTextArea,
		                      buttonPanel
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.NONE, new Insets(10, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.HORIZONTAL, new Insets(10, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBagB(t, comps, gbcs);

		// enable border to set size, then disable
		//copyrightTextArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
		//useTextArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
		t.setPreferredSize(SIZE);
		t.setOpaque(false);

		JLayeredPane p = new JLayeredPane(); //here add the two panels i and t (t must be transparent)
		p.setLayout(new LayeredCenterLayout());
		p.add(t);
		p.add(i);

		return p;
	}

	private JPanel createAskAcceptancePanel(JPanel mainPanel)
	{
		JPanel p = new JPanel();
		JButton creditsButton = Templates.makeButton(new Credits.ShowCreditsAction("credits", mainPanel));
		JButton acceptButton = Templates.makeButton("I accept",
		                                            new MsgOptionActionListener(ACCEPT_OPTION));
		JButton declineButton = Templates.makeButton("I decline",
		                                             new MsgOptionActionListener(DECLINE_OPTION));

		JComponent[] comps = {creditsButton,
		                      acceptButton,
		                      declineButton
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 20), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setOpaque(false);
		return p;
	}

	private JPanel createOkPanel(JPanel mainPanel)
	{
		JPanel p = new JPanel();
		JButton creditsButton = Templates.makeButton(new Credits.ShowCreditsAction("credits", mainPanel));
		JButton okButton = Templates.makeButton("OK",
		                                        new CloseMsgActionListener());

		JComponent[] comps = {creditsButton,
		                      okButton
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 20), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setOpaque(false);
		return p;
	}

	public static void main(String[] args)
	{
		switch (Copyright.showAskAcceptanceDialog(null)) {
			case Copyright.ACCEPT_OPTION:
				System.out.println("I accept");
				break;
			default:
				System.out.println("I decline");
		}
		;
		Copyright.showInformationDialog(null);
		System.exit(0);
	}

	public static class ShowCopyrightAction extends AbstractAction
	{
		protected Component gui;

		public ShowCopyrightAction(String name, Component gui)
		{
			super(name);
			this.gui = gui;
		}

		public void actionPerformed(ActionEvent event)
		{
			Copyright.showInformationDialog(gui);
		}
	}

}
