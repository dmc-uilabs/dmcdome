package toolinterfaceworkspace.gui.objectmodel.toolinterface.build;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.CardLayout2;
import mit.cadlab.dome3.gui.objectmodel.modelobject.context.build.ContextTreeBuilderPanel;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.dsm.DSMPanel;
import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.objectmodel.modelobject.context.DefaultContextBuilder;
import mit.cadlab.dome3.objectmodel.model.Model;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterface;
import toolinterfaceworkspace.objectmodel.toolinterface.ToolInterfaceBuilder;
import com.touchgraph.graphlayout.GLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 27, 2003
 * Time: 11:53:14 AM
 * To change this template use Options | File Templates.
 */
public abstract class ToolInterfaceDataBuildPanel extends JPanel
{
    protected static GridBagConstraints gbc;
    protected static ImageIcon comboArrow = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrow.gif");
    protected static ImageIcon comboArrowOver = Templates.makeImageIcon("mit/cadlab/dome3/icons/arrow/comboArrowOver.gif");
    protected static Color notEditableColor = new Color(105, 105, 105);
    protected static String[] visualizations = {"list", "graph", "dsm", "xml"};

    protected CardLayout2 ifaceViewsCards;
    protected JPanel ifaceViewsPanel;

    protected ContextTreeBuilderPanel _buildViewPanel;
    protected JButton backButton;
    protected NameTextField nameField;
    protected boolean isNameFieldEditable;

    protected boolean isBackButtonEnabled = false;
    protected BasicComboPopup contextPopup;
    protected JButton rootContextsButton;
    protected int tfHeight = 0; // initialize textfield height

//    protected ToolInterfaceTreeBuilderPanel ifaceCausalityPanel;
//    protected ToolInterfaceTreeBuilderPanel sysCausalityPanel;
    protected JComboBox viewChoice,viewComboBox;
    protected DefaultComboBoxModel cbModel;

    protected GLPanel graph;
    protected DSMPanel dsm;
    protected ToolInterfaceBuilder _tInterface;

    public ToolInterfaceDataBuildPanel(ToolInterfaceBuilder tInterface)
    {
        _tInterface = tInterface;
        setBackground(Templates.DARKER_BACKGROUND_COLOR);
    }

    public void createComponents()
    {
        ifaceViewsCards = new CardLayout2();
        ifaceViewsPanel = new JPanel();
        ifaceViewsPanel.setLayout(ifaceViewsCards);

        nameField = new NameTextField();
        nameField.setForeground(Color.BLACK);
        nameField.setDisabledTextColor(notEditableColor);
        nameField.setEditable(false);
        isNameFieldEditable = false;
        nameField.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        viewChoice = Templates.makeComboBox(visualizations);
        viewChoice.setBackground(Templates.DARKER_BACKGROUND_COLOR);

    }

    protected void layoutComponents()
    {
        JComponent[] comps = {makeControlPanel(), ifaceViewsPanel};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {// 25 inset
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(2, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBagB(this, comps, gbcs);
    }

    protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();
        p.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        cbModel = new DefaultComboBoxModel(_tInterface.getViewNames().toArray());

        viewComboBox = Templates.makeComboBox(cbModel);
        viewComboBox.setForeground(Color.BLACK);
        viewComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent evt)
            {
                switchView();
                contextPopup.hide();
            }
        });

        contextPopup = new BasicComboPopup(viewComboBox);

        backButton = Templates.makeImageButton("mit/cadlab/dome3/icons/backArrow16.gif");
        backButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String currentView = ifaceViewsCards.getActiveName();
                if (currentView.equals(ToolInterface.BUILD_VIEW))
                {
                    _buildViewPanel.getContextTree().setRootContextBack();
                }
            }
        });

        backButton.setEnabled(false);
        rootContextsButton = Templates.makeImageButton(comboArrow, comboArrow, comboArrowOver, comboArrow);
        rootContextsButton.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        rootContextsButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (tfHeight == 0)
                {
                    Dimension tfSize = nameField.getSize();
                    tfHeight = tfSize.height;
                    Dimension popupSize = new Dimension(tfSize.width, tfHeight * cbModel.getSize());
                    Templates.setFixedSize(contextPopup, popupSize);
                }
                contextPopup.show(nameField, 0, tfHeight);
            }
        });

        JButton listButton = Templates.makeListArrowButton("up");
        JPanel fillerPanel = new JPanel();
        fillerPanel.setBackground(Templates.DARKER_BACKGROUND_COLOR);
        Templates.setFixedSize(fillerPanel, listButton.getPreferredSize());

		JComponent[] comps = {

            backButton,
            nameField,
            rootContextsButton,
            Templates.makeLabel("visualization"),
            viewChoice,
            fillerPanel

        };

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(3, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 15, 0, 0), 0, 0),
            new GridBagConstraints(4, 0, 1, 1, 0.0, 1.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
            new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 1, 0, 0), 0, 0)

        };

		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

    protected abstract void switchView();

    protected abstract void setMenuContext();


    protected abstract void synchronizeViewControls();

    protected String getMenuContext()
    {
//        String currentView = ifaceViewsCards.getActiveName();
//        Model m = _tInterface.getModel();
        return ModeContexts.BUILD_TOOL_INTERFACE;
    }
}
