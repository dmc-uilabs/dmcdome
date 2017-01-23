package mit.cadlab.dome3.gui.objectmodel.toolinterface.build;

import mit.cadlab.dome3.gui.guiutils.customGui.*;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.DomeBuildFrame;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.objectmodel.toolinterface.AbstractAnalysisToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.AnalysisToolInterfaceBase;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import com.sun.java.CardLayout2;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jacobwronski
 * Date: Aug 21, 2003
 * Time: 11:23:51 AM
 * To change this template use Options | File Templates.
 */
public abstract class ToolInterfaceBuildPanel extends AbstractDomeObjectGui
{
    public static final GridBagConstraints gbc = null;

    public static final TypeInfo TYPE_INFO = new TypeInfo("ToolInterfaceBuildPanel");
    public static final String XML_TAG = "toolinterfacebuildpanel";
    public static final String TOOLNAME = ", for ";
    public static final String DEFINITION = "definition";
    public static final String CONFIGURATION = "configuration";
    public static final String DOCUMENTATION = "documentation";

    protected AnalysisToolInterfaceBase _tInterface;
    protected NameTextField nameField;
    protected DefaultComboBoxModel cbModel;
    protected JTabbedPane contentTabs;
    protected DomeBuildFrame modelviewDialog;
    protected boolean isModelviewDialogOPen;
    protected DocumentationBuildPanel docPanel;
    protected JComboBox _guiSelComboBox;
    protected CardLayout2 _definitionPanelCards;
    protected JPanel _definitionPanel;
    protected HashMap _guiToComboBoxMap = new HashMap();//key: filedata of comboboxmodel, value, gui card. except DEFAULT

    public ToolInterfaceBuildPanel(AnalysisToolInterfaceBase ti)
    {
        super(ti);
        _tInterface = ti;

    }

    protected void createComponents()
    {
        nameField = new NameTextField();
		nameField.setDomeObject(_tInterface);
		NameListener ifaceNameListener = new NameListener()
		{
			public void nameChanged(String newName)
			{
				Container cont = getTopLevelAncestor();
				((DomeBuildFrame) cont).setTitle(getTitlePrefix() + newName +
				                                 TOOLNAME + _tInterface.getModel().getName());
			}
		};

        _tInterface.addPropertyChangeListener(NameListener.NAME, ifaceNameListener);

		NameListener modelNameListener = new NameListener()
		{
			public void nameChanged(String newName)
			{
				Container cont = getTopLevelAncestor();
				((DomeBuildFrame) cont).setTitle(getTitlePrefix() + _tInterface.getName() +
				                                 TOOLNAME + newName);
			}
		};

		_tInterface.getModel().addPropertyChangeListener(NameListener.NAME, modelNameListener);

        //get custom gui put them into combobox
        cbModel = new CustomGUIComboBoxModel(_tInterface, true);
        _guiSelComboBox = Templates.makeDComboBox(cbModel);
        _guiSelComboBox.addItemListener(
                new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {

                        if (_guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.CHANGE_ADD))
                        {
                            CustomGuiChooser.showDialog(ToolInterfaceBuildPanel.this, _tInterface);
                            _guiSelComboBox.setModel(new CustomGUIComboBoxModel(_tInterface, true));
                        }
                        else if (_guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.CHANGE_EDIT))
                        {
                            CustomGuiPicker.showDialog(ToolInterfaceBuildPanel.this, _tInterface);
                            _guiSelComboBox.setModel(new CustomGUIComboBoxModel(_tInterface, true));
                        }
                        else if (_guiSelComboBox.getSelectedItem().toString().equals(CustomGUIComboBoxModel.DEFAULT))
                        {
                            _definitionPanelCards.show(_definitionPanel, CustomGUIComboBoxModel.DEFAULT);
                        }
                        else
                        {
                            CustomGuiInfo item = (CustomGuiInfo) _guiSelComboBox.getSelectedItem();
                            _definitionPanelCards.show(_definitionPanel, item.toString());
                        }
                    }
                }
        );

        _tInterface.addPropertyChangeListener(AbstractAnalysisToolInterface.CUSTOMGUICHANGE, new CustomGuiListener());

        contentTabs = Templates.makeTabbedPane();

        _definitionPanelCards = new CardLayout2();
        _definitionPanel = new JPanel();
        _definitionPanel.setLayout(_definitionPanelCards);
        docPanel = new DocumentationBuildPanel(_tInterface.getDocumentation());
	}

    public abstract void setMenuContext();

    public String getHelpContext()
    {
        return null;
    }

     // AnalysisTool interface
	public String getTitlePrefix()
	{
		return _tInterface.getTypeInfo().getTypeName() + ": ";
	}

	public String getTitle()
	{
		return getTitlePrefix() + getDomeObject().getName() +", for " + _tInterface.getModel().getName();
	}

    protected void layoutComponents()
    {
		JComponent[] comps = {makeControlPanel(), contentTabs};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

    protected JPanel makeControlPanel()
	{
		JPanel p = new JPanel();

        JComponent[] comps = {

            Templates.makeLabel("name:"),
            nameField,
            Templates.makeLabel("graphical interface:"), _guiSelComboBox
        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {

            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 10), 0, 0),
            new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0, gbc.EAST, gbc.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0)
        };

        Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

    public void addAllCustomGUIs()
	{
		ArrayList files = _tInterface.getCustomGUIList();
		for (int i = 0; i < files.size(); i++) {
			addCustomGUIPanel((CustomGuiInfo) files.get(i));
		}
	}

	public void addCustomGUIPanel(CustomGuiInfo file)
	{
		JComponent customGui = CustomGuiUtils.createCustomGui(file, _tInterface);
		_definitionPanel.add(file.toString(), customGui);
		_guiToComboBoxMap.put(file, customGui);
	}

	protected class CustomGuiListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			Object newValue = evt.getNewValue();
			Object oldValue = evt.getOldValue();
			if (oldValue == null)//add
			{
				CustomGuiInfo file = (CustomGuiInfo) newValue;
				addCustomGUIPanel(file);
			} else if (newValue == null)//remove
			{
				CustomGuiInfo file = (CustomGuiInfo) oldValue;
				JComponent gui = (JComponent) _guiToComboBoxMap.get(file);
				_definitionPanel.remove(gui);
				_guiToComboBoxMap.remove(file);
			}
            else{//edit
                CustomGuiInfo file = (CustomGuiInfo) oldValue;
                JComponent gui = (JComponent) _guiToComboBoxMap.get(file);
				_definitionPanel.remove(gui);
				_guiToComboBoxMap.remove(file);
                addCustomGUIPanel(file);
            }
		}
	}
}
