package dceGUI;

import mit.cadlab.dome3.gui.deploy.deployModel.DeployModelGui;
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

import com.sun.java.CardLayout2;


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
public class DCEgui extends JPanel implements ActionListener
{
	public static final GridBagConstraints gbc = null;

    private static final String PROG_CARD = "programGeometry";
    private static final String HINGE_CARD = "hinge";
    private static final String LATCH_CARD = "latch";
    private static final String AIR_CARD = "air";
    private static final String OVER_SLAM_CARD = "overSlam";
    private static final String CLOSING_SPRING_CARD = "closingSpring";
    private static final String DOOR_SEAL_CARD = "doorSeal";
    private static final String BODY_SEAL_CARD = "bodySeal";
    private static final String CUT_LINE_CARD = "cutLineSeal";
    private static final String STANDARD_CARD = "standardResults";
    private static final String TEARDOWN_CARD = "tearDownResults";

    private static final int PROG_INDEX = 0;
    private static final int HINGE_INDEX = 1;
    private static final int LATCH_INDEX = 2;
    private static final int AIR_INDEX = 3;
    private static final int OVER_SLAM_INDEX = 4;
    private static final int CLOSING_SPRING_INDEX = 5;
    private static final int DOOR_SEAL_INDEX = 6;
    private static final int BODY_SEAL_INDEX = 7;
    private static final int CUT_LINE_INDEX = 8;
    private static final int STANDARD_INDEX = 9;
    private static final int TEARDOWN_INDEX = 10;

    private static final int FIRST_CARD = 0;
    private static final int NUM_CARD = 11;

    private int cardIndex = 0;

	private JButton backButton;
	private JButton nextButton;

	private JRadioButton[] buttons;
    private ButtonGroup buttonGroup;

	private JRadioButton programGeometry;
	private JRadioButton hinge;
	private JRadioButton latch;
	private JRadioButton air;
	private JRadioButton overSlam;
	private JRadioButton closingSpring;
    private JRadioButton doorSeal;
    private JRadioButton bodySeal;
    private JRadioButton cutLineSeal;

    private JRadioButton standardResults;
    private JRadioButton teardownResults;

	private JPanel cardPanel;

    public DCEgui(ModelInterfaceBase iface){
        this();
        setInterface(iface);
    }

	/**
	 * Constructor for the main to run the GUI standalone
	 */
	public DCEgui()
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

		programGeometry = Templates.makeRadioButton("Program and geometry", false);
		programGeometry.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        programGeometry.addActionListener(this);

        hinge = Templates.makeRadioButton("Hinge", false);
        hinge.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        hinge.addActionListener(this);

		latch = Templates.makeRadioButton("Latch", false);
		latch.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        latch.addActionListener(this);

		air = Templates.makeRadioButton("Air", false);
		air.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        air.addActionListener(this);

		overSlam = Templates.makeRadioButton("Over slam", false);
		overSlam.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        overSlam.addActionListener(this);

		closingSpring = Templates.makeRadioButton("Closing spring", false);
		closingSpring.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        closingSpring.addActionListener(this);

        doorSeal = Templates.makeRadioButton("Door seal", false);
        doorSeal.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        doorSeal.addActionListener(this);

        bodySeal = Templates.makeRadioButton("Body seal", false);
        bodySeal.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        bodySeal.addActionListener(this);

        cutLineSeal = Templates.makeRadioButton("Cut line seal", false);
        cutLineSeal.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        cutLineSeal.addActionListener(this);

        standardResults = Templates.makeRadioButton("Standard results", false);
        standardResults.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        standardResults.addActionListener(this);

        teardownResults = Templates.makeRadioButton("Teardown results", false);
        teardownResults.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        teardownResults.addActionListener(this);

        JLabel inputLabel = Templates.makeLabel("inputs", Templates.FONT11B);
        inputLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JLabel resultLabel = Templates.makeLabel("results", Templates.FONT11B);
        resultLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        ImageIcon image = Templates.makeImageIcon("dceGUI/images/dceLogo.gif");
        JLabel imageLabel = new JLabel(image);

        JPanel fill = new JPanel();
        fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {inputLabel, programGeometry, hinge, latch, air, overSlam,
		                      closingSpring, doorSeal, bodySeal, cutLineSeal, resultLabel,
                              standardResults, teardownResults, fill, imageLabel};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),

            new GridBagConstraints(0, 13, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0, gbc.SOUTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(programGeometry);
        buttonGroup.add(hinge);
        buttonGroup.add(latch);
        buttonGroup.add(air);
        buttonGroup.add(overSlam);
        buttonGroup.add(closingSpring);
        buttonGroup.add(doorSeal);
        buttonGroup.add(bodySeal);
        buttonGroup.add(cutLineSeal);
        buttonGroup.add(standardResults);
        buttonGroup.add(teardownResults);

		buttons = new JRadioButton[NUM_CARD];
        buttons[PROG_INDEX] = programGeometry;
        buttons[HINGE_INDEX] = hinge;
        buttons[LATCH_INDEX] = latch;
        buttons[AIR_INDEX] = air;
        buttons[OVER_SLAM_INDEX] = overSlam;
        buttons[CLOSING_SPRING_INDEX] = closingSpring;
        buttons[DOOR_SEAL_INDEX] = doorSeal;
        buttons[BODY_SEAL_INDEX] = bodySeal;
        buttons[CUT_LINE_INDEX] = cutLineSeal;
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
		cardPanel.setLayout(new com.sun.java.CardLayout2());
		cardPanel.add(PROG_CARD, new ProgramGeometryCard());
        cardPanel.add(HINGE_CARD, new HingeCard());
		cardPanel.add(LATCH_CARD, new LatchCard());
		cardPanel.add(AIR_CARD, new AirCard());
		cardPanel.add(OVER_SLAM_CARD, new OverSlamCard());
		cardPanel.add(CLOSING_SPRING_CARD, new ClosingCard());
        cardPanel.add(DOOR_SEAL_CARD, new DoorSealCard());
        cardPanel.add(BODY_SEAL_CARD, new BodySealCard());
        cardPanel.add(CUT_LINE_CARD, new CutLineSealCard());
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
        else if (object == programGeometry){
            cardIndex = PROG_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, PROG_CARD);
        }
        else if (object == hinge) {
            cardIndex = HINGE_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, HINGE_CARD);
        }
        else if (object == latch) {
            cardIndex = LATCH_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, LATCH_CARD);
        }
        else if (object == air) {
            cardIndex = AIR_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, AIR_CARD);
        }
        else if (object == overSlam) {
            cardIndex = OVER_SLAM_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, OVER_SLAM_CARD);
        }
        else if (object == closingSpring) {
            cardIndex = CLOSING_SPRING_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, CLOSING_SPRING_CARD);
        }
        else if (object == doorSeal) {
            cardIndex = DOOR_SEAL_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, DOOR_SEAL_CARD);
        }
        else if (object == bodySeal) {
            cardIndex = BODY_SEAL_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, BODY_SEAL_CARD);
        }
        else if (object == cutLineSeal) {
            cardIndex = CUT_LINE_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, CUT_LINE_CARD);
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
		SwingUtilities.windowForComponent(DCEgui.this).dispose();
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
       ((ProgramGeometryCard)((CardLayout2)(cardPanel.getLayout())).getComponent(PROG_CARD)).setInterface(iface);
       ((HingeCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(HINGE_CARD)).setInterface(iface);
       ((LatchCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(LATCH_CARD)).setInterface(iface);
       ((AirCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(AIR_CARD)).setInterface(iface);
       ((OverSlamCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(OVER_SLAM_CARD)).setInterface(iface);
       ((ClosingCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(CLOSING_SPRING_CARD)).setInterface(iface);
       ((DoorSealCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(DOOR_SEAL_CARD)).setInterface(iface);
       ((BodySealCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(BODY_SEAL_CARD)).setInterface(iface);
       ((CutLineSealCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(CUT_LINE_CARD)).setInterface(iface);
       ((StandardResultCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(STANDARD_CARD)).setInterface(iface);
       ((TeardownResultCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(TEARDOWN_CARD)).setInterface(iface);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("DCE project custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new DCEgui());
		f.setSize(DeployModelGui.DEFAULT_SIZE);
		f.show();
	}
}
