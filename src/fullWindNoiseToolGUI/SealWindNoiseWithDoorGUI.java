package fullWindNoiseToolGUI;

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
public class SealWindNoiseWithDoorGUI extends JPanel implements ActionListener
{
	public static final GridBagConstraints gbc = null;

    private static final String SEAL_CARD = "sealGeometry";
    private static final String DOOR_CARD = "doorGeometry";
    private static final String INTERFERENCE_SETUP_CARD = "interferenceSetup";

    private static final String FEA_CARD = "feaResults";
    private static final String INTERFERENCE_RESULT_CARD = "interferenceResults";
    private static final String STANDARD_CARD = "standardResults";
    private static final String TEARDOWN_CARD = "tearDownResults";

    private static final int SEAL_INDEX = 0;
    private static final int DOOR_INDEX = 1;
    private static final int INTERFERENCE_SETUP_INDEX = 2;
    private static final int INTERFERENCE_RESULT_INDEX = 3;
    private static final int FEA_INDEX = 4;
    private static final int STANDARD_INDEX = 5;
    private static final int TEARDOWN_INDEX = 6;

    private static final int FIRST_CARD = 0;
    private static final int NUM_CARD = 7;

    private int cardIndex = 0;

	private JButton backButton;
	private JButton nextButton;

	private JRadioButton[] buttons;
    private ButtonGroup buttonGroup;

	private JRadioButton sealGeometry;
	private JRadioButton doorGeometry;
    private JRadioButton interferenceSetup;

    private JRadioButton feaResults;
    private JRadioButton interferenceResults;

    private JRadioButton standardResults;
    private JRadioButton teardownResults;

	private JPanel cardPanel;

    public SealWindNoiseWithDoorGUI(ModelInterfaceBase iface){
        this();
        setInterface(iface);
    }

	/**
	 * Constructor for the main to run the GUI standalone
	 */
	public SealWindNoiseWithDoorGUI()
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

		sealGeometry = Templates.makeRadioButton("Body/Seal geometry", false);
		sealGeometry.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        sealGeometry.addActionListener(this);

        doorGeometry = Templates.makeRadioButton("Door geometry", false);
        doorGeometry.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        doorGeometry.addActionListener(this);

        interferenceSetup = Templates.makeRadioButton("Interference setup", false);
        interferenceSetup.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        interferenceSetup.addActionListener(this);

        interferenceResults = Templates.makeRadioButton("Seal interference results", false);
        interferenceResults.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        interferenceResults.addActionListener(this);

        feaResults = Templates.makeRadioButton("Seal CLD results", false);
        feaResults.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        feaResults.addActionListener(this);

        standardResults = Templates.makeRadioButton("Standard effort results", false);
        standardResults.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        standardResults.addActionListener(this);

        teardownResults = Templates.makeRadioButton("Teardown effort results", false);
        teardownResults.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        teardownResults.addActionListener(this);

        JLabel inputLabel = Templates.makeLabel("setup", Templates.FONT11B);
        inputLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JLabel resultLabel = Templates.makeLabel("results", Templates.FONT11B);
        resultLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        ImageIcon image = Templates.makeImageIcon("fullWindNoiseToolGUI/images/logo.gif");
        JLabel imageLabel = new JLabel(image);

        JPanel fill = new JPanel();
        fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {inputLabel, sealGeometry, doorGeometry,
                              //latch, air,closingSpring,
                              interferenceSetup,
                              resultLabel,
                              interferenceResults, feaResults, standardResults, teardownResults, fill, imageLabel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			//new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			//new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			//new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 12, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(sealGeometry);
        buttonGroup.add(doorGeometry);
        buttonGroup.add(interferenceSetup);
        buttonGroup.add(interferenceResults);
        buttonGroup.add(feaResults);
        buttonGroup.add(standardResults);
        buttonGroup.add(teardownResults);

		buttons = new JRadioButton[NUM_CARD];
        buttons[SEAL_INDEX] = sealGeometry;
        buttons[DOOR_INDEX] = doorGeometry;
        buttons[INTERFERENCE_SETUP_INDEX] = interferenceSetup;
        buttons[FEA_INDEX] = feaResults;
        buttons[INTERFERENCE_RESULT_INDEX] = interferenceResults;
        buttons[STANDARD_INDEX] = standardResults;
        buttons[TEARDOWN_INDEX] = teardownResults;

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
		cardPanel.add(SEAL_CARD, new SealGeometryCard());
        cardPanel.add(DOOR_CARD, new DoorGeometryCard());
        cardPanel.add(INTERFERENCE_SETUP_CARD, new InterferenceSetupCard());
        cardPanel.add(INTERFERENCE_RESULT_CARD, new InterferenceResultCard());
        cardPanel.add(FEA_CARD, new FeaResultCard());
        cardPanel.add(STANDARD_CARD, new StandardResultCard());
        cardPanel.add(TEARDOWN_CARD, new TeardownResultCard());

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
        else if (object == sealGeometry){
            cardIndex = SEAL_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, SEAL_CARD);
        }
        else if (object == doorGeometry) {
            cardIndex = DOOR_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, DOOR_CARD);
        }
        else if (object == interferenceSetup) {
            cardIndex = INTERFERENCE_SETUP_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, INTERFERENCE_SETUP_CARD);
        }
        else if (object == feaResults) {
            cardIndex = FEA_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, FEA_CARD);
        }
        else if (object == interferenceResults) {
            cardIndex = INTERFERENCE_RESULT_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, INTERFERENCE_RESULT_CARD);
        }
        else if (object == standardResults) {
            cardIndex = STANDARD_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, STANDARD_CARD);
        }
        else if (object == teardownResults) {
            cardIndex = TEARDOWN_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, TEARDOWN_CARD);
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
		SwingUtilities.windowForComponent(SealWindNoiseWithDoorGUI.this).dispose();
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

    private void setInterface(ModelInterfaceBase iface){
       ((SealGeometryCard)((CardLayout2)(cardPanel.getLayout())).getComponent(SEAL_CARD)).setInterface(iface);
       ((DoorGeometryCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(DOOR_CARD)).setInterface(iface);
       ((InterferenceSetupCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(INTERFERENCE_SETUP_CARD)).setInterface(iface);
       ((FeaResultCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(FEA_CARD)).setInterface(iface);
       ((InterferenceResultCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(INTERFERENCE_RESULT_CARD)).setInterface(iface);
       ((StandardResultCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(STANDARD_CARD)).setInterface(iface);
       ((TeardownResultCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(TEARDOWN_CARD)).setInterface(iface);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Integrated tool custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new SealWindNoiseWithDoorGUI());
		f.setSize(DeployModelGui.DEFAULT_SIZE);
		f.show();
	}
}
