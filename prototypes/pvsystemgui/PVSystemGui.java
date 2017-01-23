package pvsystemgui;

import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main class for the playspace deploy wizard
 */
public class PVSystemGui extends JPanel implements ActionListener
{
	public static final GridBagConstraints gbc = null;

    private static final String LOAD_CARD = "Load profile";
    private static final String SOLAR_CARD = "Solar irradiance profile";
    private static final String MODULE_CARD = "PV module's inputs";
    private static final String OTHER_CARD = "Other components' inputs";
    private static final String RESULT_CARD = "Results";
    private static final String BATTERY_VOLTAGE_CARD = "Battery voltage";
    private static final String BATTERY_CURRENT_CARD = "Battery current";
    private static final String CELL_TEMP_CARD = "Cell temperature";
    private static final String PV_CURRENT_CARD = "PV generated current";
    private static final String DELIVERED_LOAD_CARD = "Delivered load profile";

    private static final int LOAD_INDEX = 0;
    private static final int SOLAR_INDEX = 1;
    private static final int MODULE_INDEX = 2;
    private static final int OTHER_INDEX = 3;
    private static final int RESULTS_INDEX = 4;
    private static final int BATTERY_VOLTAGE_INDEX = 5;
    private static final int BATTERY_CURRENT_INDEX = 6;
    private static final int CELL_TEMP_INDEX = 7;
    private static final int PV_CURRENT_INDEX = 8;
    private static final int DELIVERED_LOAD_INDEX = 9;

    private static final int FIRST_CARD = 0;
    private static final int NUM_CARD = 10;

    private static final Dimension PREFERRED_SIZE = new Dimension(850,500);

    private int cardIndex = 0;

	private JButton backButton;
	private JButton nextButton;

	private JRadioButton[] buttons;
    private ButtonGroup buttonGroup;

    private JRadioButton loadInput;
    private JRadioButton solarInput;
	private JRadioButton moduleInput;
    private JRadioButton otherCompInput;
    private JRadioButton systemBehaviors;
    private JRadioButton batVoltBehaviors;
    private JRadioButton batCurrBehaviors;
    private JRadioButton cellTempBehaviors;
    private JRadioButton pvCurrBehaviors;
    private JRadioButton deliverLoadBehaviors;
	private JPanel cardPanel;

    public PVSystemGui(ModelInterfaceBase iface) {
        this();
        setInterface(iface);
    }

	/**
	 * Constructor for the main Deploy playspace wizard
	 */
	public PVSystemGui()
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

        loadInput = Templates.makeRadioButton("Load profile", false);
        loadInput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        loadInput.addActionListener(this);

        solarInput = Templates.makeRadioButton("Solar irradiance profile", false);
        solarInput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        solarInput.addActionListener(this);

        moduleInput = Templates.makeRadioButton("PV module, system, & computation", false);
        moduleInput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        moduleInput.addActionListener(this);

        otherCompInput = Templates.makeRadioButton("surrounding, battery, controller, & inverter", false);
        otherCompInput.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        otherCompInput.addActionListener(this);

        systemBehaviors = Templates.makeRadioButton("System behaviors", false);
        systemBehaviors.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        systemBehaviors.addActionListener(this);

        batVoltBehaviors = Templates.makeRadioButton("Battery voltage profile", false);
        batVoltBehaviors.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        batVoltBehaviors.addActionListener(this);

        batCurrBehaviors = Templates.makeRadioButton("Battery current profile", false);
        batCurrBehaviors.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        batCurrBehaviors.addActionListener(this);

        cellTempBehaviors = Templates.makeRadioButton("Cell temperature profile", false);
        cellTempBehaviors.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        cellTempBehaviors.addActionListener(this);

        pvCurrBehaviors = Templates.makeRadioButton("PV generated current profile", false);
        pvCurrBehaviors.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        pvCurrBehaviors.addActionListener(this);

        deliverLoadBehaviors = Templates.makeRadioButton("Actual delivered load profile", false);
        deliverLoadBehaviors.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        deliverLoadBehaviors.addActionListener(this);

        JLabel inputLabel = Templates.makeLabel("inputs", Templates.FONT11B);
        inputLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JLabel resultLabel = Templates.makeLabel("results", Templates.FONT11B);
        resultLabel.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        JPanel fill = new JPanel();
        fill.setBackground(Templates.DARKER_BACKGROUND_COLOR);

		JComponent[] comps = {inputLabel, loadInput, solarInput, moduleInput, otherCompInput, resultLabel,
                              systemBehaviors, batVoltBehaviors, batCurrBehaviors, cellTempBehaviors, pvCurrBehaviors,
                              deliverLoadBehaviors, fill};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(10, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(0, 12, 1, 1, 0.0, 1.0, gbc.WEST, gbc.VERTICAL, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(loadInput);
        buttonGroup.add(solarInput);
        buttonGroup.add(moduleInput);
        buttonGroup.add(otherCompInput);
        buttonGroup.add(systemBehaviors);
        buttonGroup.add(batVoltBehaviors);
        buttonGroup.add(batCurrBehaviors);
        buttonGroup.add(cellTempBehaviors);
        buttonGroup.add(pvCurrBehaviors);
        buttonGroup.add(deliverLoadBehaviors);

		buttons = new JRadioButton[NUM_CARD];
        buttons[LOAD_INDEX] = loadInput;
        buttons[SOLAR_INDEX] = solarInput;
        buttons[MODULE_INDEX] = moduleInput;
        buttons[OTHER_INDEX] = otherCompInput;
        buttons[RESULTS_INDEX] = systemBehaviors;
        buttons[BATTERY_VOLTAGE_INDEX] = batVoltBehaviors;
        buttons[BATTERY_CURRENT_INDEX] = batCurrBehaviors;
        buttons[CELL_TEMP_INDEX] = cellTempBehaviors;
        buttons[PV_CURRENT_INDEX] = pvCurrBehaviors;
        buttons[DELIVERED_LOAD_INDEX] = deliverLoadBehaviors;

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

        ChartCard loadCard = new ChartCard("Load power profile", "Time", "Load power", true);
        loadCard.setParamNames("load time vector", "load vector", "chart maximum time");
        loadCard.setYMax(300);
        cardPanel.add(LOAD_CARD, loadCard);

        ChartCard solarCard = new ChartCard("Solar irradiance profile", "Time", "Solar irradiance", true);
        solarCard.setParamNames("irradiance time vector", "irradiance vector", "chart maximum time");
        solarCard.setYMax(1000);
        cardPanel.add(SOLAR_CARD, solarCard);

		cardPanel.add(MODULE_CARD, new ModuleInputCard());
        cardPanel.add(OTHER_CARD, new OtherCompInputCard());
        cardPanel.add(RESULT_CARD, new ResultsCard());

        ChartCard vBatCard = new ChartCard("Battery voltage profile", "Time", "Battery voltage", false);
        vBatCard.setParamNames("Vbat time vector", "battery voltage data", "chart maximum time");
        vBatCard.setYMin(23.5);
        vBatCard.setYMax(24.5);
        cardPanel.add(BATTERY_VOLTAGE_CARD, vBatCard);

        ChartCard iBatCard = new ChartCard("Battery current profile", "Time", "Battery current", false);
        iBatCard.setParamNames("Ibat time vector", "battery current data", "chart maximum time");
        iBatCard.setYMin(0);
        iBatCard.setYMax(6);
        cardPanel.add(BATTERY_CURRENT_CARD, iBatCard);

        ChartCard cellTempCard = new ChartCard("Cell temperature profile", "Time", "Cell temperature", false);
        cellTempCard.setParamNames("Tp time vector", "cell temperature data", "chart maximum time");
        cellTempCard.setYMin(300);
        cellTempCard.setYMax(320);
        cardPanel.add(CELL_TEMP_CARD, cellTempCard);

        ChartCard iPVCard = new ChartCard("PV generated current profile", "Time", "PV generated current", false);
        iPVCard.setParamNames("Ipv time vector", "PV current data", "chart maximum time");
        iPVCard.setYMin(0);
        iPVCard.setYMax(5);
        cardPanel.add(PV_CURRENT_CARD, iPVCard);

        ChartCard delivLoadCard = new ChartCard("Actual delivered load profile", "Time", "Delivered load", false);
        delivLoadCard.setParamNames("SatLoad time vector", "delivered load data", "chart maximum time");
        delivLoadCard.setYMin(0);
        delivLoadCard.setYMax(300);
        cardPanel.add(DELIVERED_LOAD_CARD, delivLoadCard);

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
        else if (object == loadInput) {
            cardIndex = LOAD_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, LOAD_CARD);
        }
        else if (object == solarInput) {
            cardIndex = SOLAR_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, SOLAR_CARD);
        }
        else if (object == moduleInput){
            cardIndex = MODULE_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, MODULE_CARD);
        }
        else if (object == otherCompInput) {
            cardIndex = OTHER_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, OTHER_CARD);
        }
        else if (object == systemBehaviors) {
            cardIndex = RESULTS_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, RESULT_CARD);
        }
        else if (object == batVoltBehaviors) {
            cardIndex = BATTERY_VOLTAGE_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, BATTERY_VOLTAGE_CARD);
        }
        else if (object == batCurrBehaviors) {
            cardIndex = BATTERY_CURRENT_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, BATTERY_CURRENT_CARD);
        }
        else if (object == cellTempBehaviors) {
            cardIndex = CELL_TEMP_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, CELL_TEMP_CARD);
        }
        else if (object == pvCurrBehaviors) {
            cardIndex = PV_CURRENT_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, PV_CURRENT_CARD);
        }
        else if (object == deliverLoadBehaviors) {
            cardIndex = DELIVERED_LOAD_INDEX;
            ((CardLayout2) cardPanel.getLayout()).show(cardPanel, DELIVERED_LOAD_CARD);
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
		SwingUtilities.windowForComponent(PVSystemGui.this).dispose();
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
        ((ChartCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(LOAD_CARD)).setInterface(iface, true);
        ((ChartCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(SOLAR_CARD)).setInterface(iface, true);
        ((ModuleInputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(MODULE_CARD)).setInterface(iface);
        ((OtherCompInputCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(OTHER_CARD)).setInterface(iface);
        ((ResultsCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(RESULT_CARD)).setInterface(iface);
        ((ChartCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(BATTERY_VOLTAGE_CARD)).setInterface(iface, false);
        ((ChartCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(BATTERY_CURRENT_CARD)).setInterface(iface, false);
        ((ChartCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(CELL_TEMP_CARD)).setInterface(iface, false);
        ((ChartCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(PV_CURRENT_CARD)).setInterface(iface, false);
        ((ChartCard) ((CardLayout2) (cardPanel.getLayout())).getComponent(DELIVERED_LOAD_CARD)).setInterface(iface, false);
    }

	public static void main(String[] args)
	{
		JFrame f = new JFrame("PV system custom GUI");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new PVSystemGui());
        f.pack();
		f.show();
	}
}
