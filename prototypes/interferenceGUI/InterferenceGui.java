package interferenceGUI;

import mit.cadlab.dome3.gui.deploy.deployModel.DeployModelGui;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Feb 25, 2003
 * Time: 10:36:47 PM
 * To change this template use Options | File Templates.
 */

/**
 * The main class for the playspace deploy wizard
 */
public class InterferenceGui extends JPanel implements ActionListener
{
	public static final GridBagConstraints gbc = null;

    private static final String CONFIG_CARD = "config";
    private static final String INTERFERENCE_CARD = "results";

    private static final int CONFIG_INDEX = 0;
    private static final int INTERFERENCE_INDEX = 1;

    private static final int FIRST_CARD = 0;
    private static final int NUM_CARD = 2;

    private int cardIndex = 0;

	private JButton backButton;
	private JButton nextButton;

	private JRadioButton[] buttons;
    private ButtonGroup buttonGroup;

	private JRadioButton config;

    private JRadioButton interferenceResults;

	private JPanel cardPanel;

    public InterferenceGui(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

	/**
	 * Constructor for the main Deploy playspace wizard
	 */
	public InterferenceGui()
	{
		JComponent[] comps = {makeRadioPanel(), makeButtonPanel(), makeCardPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
		setPreferredSize(new Dimension(900,600));
	}

	private JPanel makeRadioPanel()
	{
		JPanel p = new JPanel();

        config = Templates.makeRadioButton("analysis configuraton", false);
        config.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        config.addActionListener(this);

        interferenceResults = Templates.makeRadioButton("interference results", false);
        interferenceResults.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        interferenceResults.addActionListener(this);

        JLabel inputLabel = Templates.makeLabel("inputs", Templates.FONT11B);
        inputLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JLabel resultLabel = Templates.makeLabel("results", Templates.FONT11B);
        resultLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        ImageIcon image = Templates.makeImageIcon("interferenceGUI/images/interferenceLogo.gif");
        JLabel imageLabel = new JLabel(image);

        JPanel fill = new JPanel();
        fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {inputLabel, config, resultLabel,
                              interferenceResults, fill, imageLabel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 13, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(config);
        buttonGroup.add(interferenceResults);

		buttons = new JRadioButton[NUM_CARD];
        buttons[CONFIG_INDEX] = config;
        buttons[INTERFERENCE_INDEX] = interferenceResults;

        //set the first radio button to be selected!
        buttons[FIRST_CARD].setSelected(true);

        return p;
	}

	private JPanel makeButtonPanel()
	{
		JPanel p = new JPanel();

		backButton = Templates.makeButton("back", this);
		nextButton = Templates.makeButton("next", this);

		JComponent[] comps = {backButton, nextButton};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);

        //set the back button disabled since will be on the first card
        backButton.setEnabled(false);

		return p;
	}

	private JPanel makeCardPanel()
	{   //cards must be added in the same order as the button group indices
		cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout2());
		cardPanel.add(CONFIG_CARD, new ConfigCard());
        cardPanel.add(INTERFERENCE_CARD, new InterferenceResultCard());

        // set the first card in the panel to match the radio buttons
        ((CardLayout2) cardPanel.getLayout()).first(cardPanel);
		return cardPanel;
	}

	private void setNextCard()
	{
        cardIndex++;
        ((CardLayout2) cardPanel.getLayout()).next(cardPanel);
        buttons[cardIndex].setSelected(true);
	}

	private void setPrevCard()
	{
        cardIndex--;
        ((CardLayout2) cardPanel.getLayout()).previous(cardPanel);
        buttons[cardIndex].setSelected(true);
	}

	public void actionPerformed(ActionEvent event)
	{

		Object object = event.getSource();
		if (object == backButton) {
			setPrevCard();
		}
        else if (object == nextButton) {
			setNextCard();
		}
        else if (object == config){
            cardIndex = CONFIG_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, CONFIG_CARD);
        }
        else if (object == interferenceResults) {
            cardIndex = INTERFERENCE_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, INTERFERENCE_CARD);
        }
        else
            System.out.println("unknow action case!");

        if (cardIndex == FIRST_CARD)
            backButton.setEnabled(false);
        else
            backButton.setEnabled(true);
        if (cardIndex == NUM_CARD-1)
            nextButton.setEnabled(false);
        else
            nextButton.setEnabled(true);
	}

	public void dispose()
	{
		SwingUtilities.windowForComponent(InterferenceGui.this).dispose();
	}

	public WindowAdapter getWindowAdapter()
	{
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		};
	}

    private void setInterface(ModelInterfaceBase iface) {
        ((ConfigCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(CONFIG_CARD)).setInterface(iface);
        ((InterferenceResultCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(INTERFERENCE_CARD)).setInterface(iface);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Interference analysis custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new InterferenceGui());
		f.setSize(DeployModelGui.DEFAULT_SIZE);
		f.show();
	}
}
