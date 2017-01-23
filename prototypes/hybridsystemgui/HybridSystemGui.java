package hybridsystemgui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HybridSystemGui extends JPanel implements ActionListener
{
	public static final GridBagConstraints gbc = null;

    private static final String DESIGN_CARD = "Design inputs";
    private static final String PROP_CARD = "Property Inputs";
    private static final String ECON_CARD = "Economic inputs";
    private static final String OBJECTIVE_CARD = "Objective outputs";
    private static final String PROPOUT_CARD = "Property Outputs";
    private static final String COST_CARD = "Cost outputs";

    private static final int DESIGN_INDEX = 0;
    private static final int PROP_INDEX = 1;
    private static final int ECON_INDEX = 2;
    private static final int OBJECTIVE_INDEX = 3;
    private static final int PROPOUT_INDEX = 4;
    private static final int COST_INDEX = 5;

    private static final int FIRST_CARD = 0;
    private static final int NUM_CARD = 6;

    private static final Dimension PREFERRED_SIZE = new Dimension(800,400);

    private int cardIndex = 0;

	private JButton backButton;
	private JButton nextButton;

	private JRadioButton[] buttons;
    private ButtonGroup buttonGroup;

    private JRadioButton designInput;
    private JRadioButton propInput;
	private JRadioButton econInput;
    private JRadioButton objectiveOutput;
    private JRadioButton propOutput;
    private JRadioButton costOutput;
    private JPanel cardPanel;

    public HybridSystemGui(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

	/**
	 * Constructor for the main Deploy playspace wizard
	 */
	public HybridSystemGui()
	{
		JComponent[] comps = {makeRadioPanel(), makeButtonPanel(), makeCardPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 5), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 5, 5), 0, 0),
			new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 0, 5, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
		setPreferredSize(PREFERRED_SIZE);
	}

	private JPanel makeRadioPanel()
	{
		JPanel p = new JPanel();

        designInput = Templates.makeRadioButton("Design parameters, energy, & CO2 emission", false);
        designInput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        designInput.addActionListener(this);

        propInput = Templates.makeRadioButton("Properties of load, diesel generator, & PV", false);
        propInput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        propInput.addActionListener(this);

        econInput = Templates.makeRadioButton("Costs & economics", false);
        econInput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        econInput.addActionListener(this);

        objectiveOutput = Templates.makeRadioButton("Design objectives, energy, & CO2 emission", false);
        objectiveOutput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        objectiveOutput.addActionListener(this);

        propOutput = Templates.makeRadioButton("Properties of load, diesel generator, & PV", false);
        propOutput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        propOutput.addActionListener(this);

        costOutput = Templates.makeRadioButton("Costs & economics", false);
        costOutput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        costOutput.addActionListener(this);

        JLabel inputLabel = Templates.makeLabel("inputs", Templates.FONT11B);
        inputLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JLabel resultLabel = Templates.makeLabel("results", Templates.FONT11B);
        resultLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JPanel fill = new JPanel();
        fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {inputLabel, designInput, propInput, econInput, resultLabel,
                              objectiveOutput, propOutput, costOutput, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 12, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(designInput);
        buttonGroup.add(propInput);
        buttonGroup.add(econInput);
        buttonGroup.add(objectiveOutput);
        buttonGroup.add(propOutput);
        buttonGroup.add(costOutput);

		buttons = new JRadioButton[NUM_CARD];
        buttons[DESIGN_INDEX] = designInput;
        buttons[PROP_INDEX] = propInput;
        buttons[ECON_INDEX] = econInput;
        buttons[OBJECTIVE_INDEX] = objectiveOutput;
        buttons[PROPOUT_INDEX] = propOutput;
        buttons[COST_INDEX] = costOutput;

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

		cardPanel.add(DESIGN_CARD, new DesignInputCard());
        cardPanel.add(PROP_CARD, new PropInputCard());
        cardPanel.add(ECON_CARD, new EconInputCard());
        cardPanel.add(OBJECTIVE_CARD, new ObjectiveOutputCard());
        cardPanel.add(PROPOUT_CARD, new PropOutputCard());
        cardPanel.add(COST_CARD, new CostOutputCard());

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
        else if (object == designInput) {
            cardIndex = DESIGN_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, DESIGN_CARD);
        }
        else if (object == propInput) {
            cardIndex = PROP_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, PROP_CARD);
        }
        else if (object == econInput){
            cardIndex = ECON_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, ECON_CARD);
        }
        else if (object == objectiveOutput) {
            cardIndex = OBJECTIVE_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, OBJECTIVE_CARD);
        }
        else if (object == propOutput) {
            cardIndex = PROPOUT_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, PROPOUT_CARD);
        }
        else if (object == costOutput) {
            cardIndex = COST_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, COST_CARD);
        }
        else
            System.out.println("unknown action case!");

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
		SwingUtilities.windowForComponent(HybridSystemGui.this).dispose();
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
        ((DesignInputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(DESIGN_CARD)).setInterface(iface);
        ((PropInputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(PROP_CARD)).setInterface(iface);
        ((EconInputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(ECON_CARD)).setInterface(iface);
        ((ObjectiveOutputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(OBJECTIVE_CARD)).setInterface(iface);
        ((CostOutputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(COST_CARD)).setInterface(iface);
        ((PropOutputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(PROPOUT_CARD)).setInterface(iface);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Hybrid system custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new HybridSystemGui());
        f.pack();
		f.show();
	}
}
