// ProceduralRelationBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 10, 2003
 * Time: 5:31:25 PM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.run;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.NameListener;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.run.ProceduralRelationDefinitionRunPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build.IterationRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build.IterationRelationDefinitionBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoRendererTable;
import mit.cadlab.dome3.gui.objectmodel.dataobject.run.DocumentationRunPanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ConcreteProceduralRelation;
import mit.cadlab.dome3.swing.LineNumberedScrollPane;
import mit.cadlab.dome3.swing.PythonEditor;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.GuiConstants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;

public class IterationRelationRunPanel extends AbstractDomeObjectGui {

    protected static GridBagConstraints gbc;

    protected ConditionIterationRelation relation;
    protected NameTextField nameField;
    protected JTabbedPane contentTabs, definitionTabs;
    protected IterationRelationDefinitionBuildPanel defPanel;
    protected CausalityInfoRendererTable dependencyTable;
    protected JButton editCausalityButton;
    protected DocumentationRunPanel docPanel;
    protected PythonEditor pyEditor;
    protected PythonEditor pyEditor_cond,pyEditor_init_cond;

    protected String[] broadCastingChoice = {"At the end of each loop", "After condition exists"};

    public IterationRelationRunPanel(ConditionIterationRelation relation) {
        super(relation);
        if (relation == null)
            throw new IllegalArgumentException("IterationRelation gui - null ProceduralRelation");
        this.relation = relation;
        pyEditor = relation.getPythonEditor();
        pyEditor.setParentPanel(this);
        pyEditor.setEditable(false);
        pyEditor_cond = relation.getPythonEditor_cond();
        pyEditor_cond.setParentPanel(this);
        pyEditor_cond.setEditable(false);
        pyEditor_init_cond = relation.getPythonEditor_init_cond();
        pyEditor_init_cond.setParentPanel(this);
        pyEditor_init_cond.setEditable(false);
        createComponents();
    }

    protected void createComponents() {
        nameField = new NameTextField();
        nameField.setDomeObject(relation);
        nameField.setEditable(false);
        contentTabs = Templates.makeTabbedPane();
        docPanel = new DocumentationRunPanel(relation.getDocumentation());
        contentTabs.addTab("definition", makeDefinitionPanel());
        contentTabs.addTab("documentation", docPanel);
        layoutComponent();
    }

    protected void layoutComponent() {
        JComponent[] comps = {makeNamePanel(), contentTabs};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.NORTH, gbc.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.NORTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
        };
        Templates.layoutGridBagB(this, comps, gbcs);
    }

    protected JPanel makeNamePanel() {
        JPanel p = new JPanel();
        JComponent[] comps = {Templates.makeLabel("name:"), nameField};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(0, 0, 0, 5), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    protected JSplitPane makeDefinitionPanel() {
        definitionTabs = Templates.makeTabbedPaneTop();
        defPanel = new IterationRelationDefinitionBuildPanel(relation);
        definitionTabs.addTab("interface parameters", defPanel);
        definitionTabs.addTab("causality", makeCausalityPanel());

        JPanel editorPanel = makeEditorPanel();
        JSplitPane spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, definitionTabs, editorPanel);
        spane.setDividerLocation(200);
        return spane;
    }

    protected JPanel makeCausalityPanel() {
        JPanel p = new JPanel();
        dependencyTable = new CausalityInfoRendererTable(new ArrayList(relation.getModelObjects()),
                relation.getDependencyInfo());
        JComponent[] comps = {dependencyTable};
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 5, 0), 0, 0),
//			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBagB(p, comps, gbcs);
        return p;
    }

    protected JPanel makeEditorPanel() {
        if (relation.getIterationType().equals(IterationRelation.WHILE_LOOP))
            return makeWhileLoopEditorPanel();
        else if (relation.getIterationType().equals(IterationRelation.DO_WHILE_LOOP))
            return makeDoWhileLoopEditorPanel();
        else if (relation.getIterationType().equals(IterationRelation.Timestep_LOOP)) return makeTimeStepLoopEditorPanel();
        return new JPanel();
    }

    protected JPanel makeWhileLoopEditorPanel() {
        JPanel p = new JPanel();

        pyEditor.setMinimumSize(new Dimension(300, 150));

        LineNumberedScrollPane spane = new LineNumberedScrollPane(pyEditor);

        pyEditor_cond.setMinimumSize(new Dimension(300, 100));
        LineNumberedScrollPane spane_cond = new LineNumberedScrollPane(pyEditor_cond);

        pyEditor_init_cond.setMinimumSize(new Dimension(300, 100));
        LineNumberedScrollPane spane_init_cond = new LineNumberedScrollPane(pyEditor_init_cond);

        JComboBox broadcastingChangeComboBox = Templates.makeComboBox(broadCastingChoice);
        if (relation.isBroadcasting_eachloop())
            broadcastingChangeComboBox.setSelectedIndex(0);
        else
            broadcastingChangeComboBox.setSelectedIndex(1);
        broadcastingChangeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                if (comboBox.getSelectedIndex() == 0)
                    relation.setBroadcasting_eachloop(true);
                else
                    relation.setBroadcasting_eachloop(false);

            }
        });
        JComponent[] comps = {Templates.makeLabel("initial condition"),
                              spane_init_cond,
                              Templates.makeLabel("While:",GuiConstants.FONT11B),
                              spane_cond,
                              Templates.makeLabel("Do:",GuiConstants.FONT11B),
                              spane,
                              Templates.makeLabel("broadcast change "),
                              broadcastingChangeComboBox
        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
        new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 6, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(1, 6, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)     };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }

    protected JPanel makeDoWhileLoopEditorPanel() {
        JPanel p = new JPanel();

        pyEditor.setMinimumSize(new Dimension(300, 150));

        LineNumberedScrollPane spane = new LineNumberedScrollPane(pyEditor);

        pyEditor_cond.setMinimumSize(new Dimension(300, 100));
        LineNumberedScrollPane spane_cond = new LineNumberedScrollPane(pyEditor_cond);

        pyEditor_init_cond.setMinimumSize(new Dimension(300, 100));
        LineNumberedScrollPane spane_init_cond = new LineNumberedScrollPane(pyEditor_init_cond);

        JComboBox broadcastingChangeComboBox = Templates.makeComboBox(broadCastingChoice);
        if (relation.isBroadcasting_eachloop())
            broadcastingChangeComboBox.setSelectedIndex(0);
        else
            broadcastingChangeComboBox.setSelectedIndex(1);
        broadcastingChangeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                if (comboBox.getSelectedIndex() == 0)
                    relation.setBroadcasting_eachloop(true);
                else
                    relation.setBroadcasting_eachloop(false);

            }
        });
        JComponent[] comps = {Templates.makeLabel("initial condition"),
                              spane_init_cond,
                              Templates.makeLabel("do:",GuiConstants.FONT11B),
                              spane,
                              Templates.makeLabel("while:",GuiConstants.FONT11B),
                              spane_cond,
                              Templates.makeLabel("broadcast change "),
                              broadcastingChangeComboBox
        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
                new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
                new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
                new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
                new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
                new GridBagConstraints(0, 6, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 5, 0), 0, 0),
                new GridBagConstraints(1, 6, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
                };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;

    }

    protected JPanel makeTimeStepLoopEditorPanel() {
        JPanel p = new JPanel();

        pyEditor.setMinimumSize(new Dimension(300, 150));

        LineNumberedScrollPane spane = new LineNumberedScrollPane(pyEditor);
        //todo: later do this config panel
        JPanel timestep_config = new JPanel();

        JComboBox broadcastingChangeComboBox = Templates.makeComboBox(broadCastingChoice);
        if (relation.isBroadcasting_eachloop())
            broadcastingChangeComboBox.setSelectedIndex(0);
        else
            broadcastingChangeComboBox.setSelectedIndex(1);
        broadcastingChangeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                if (comboBox.getSelectedIndex() == 0)
                    relation.setBroadcasting_eachloop(true);
                else
                    relation.setBroadcasting_eachloop(false);

            }
        });
        JComponent[] comps = {Templates.makeLabel("timestep:",GuiConstants.FONT11B),
                              timestep_config,
                              Templates.makeLabel("do:",GuiConstants.FONT11B),
                              spane,
                              Templates.makeLabel("broadcast change "),
                              broadcastingChangeComboBox
        };

        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.WEST, gbc.NONE, new Insets(0, 5, 5, 0), 0, 0),
            new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(1, 4, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBag(p, comps, gbcs);
        return p;
    }


    public Relation getRelation() {
        return this.relation;
    }

    public String getTextFromPyEditor() {
        return pyEditor.getText().trim();
    }

    // DomeGui interface
    public String getTitlePrefix() {
        return "IterationRelation: ";
    }

    public String getHelpContext() {
        return null;
    }

    public void setMenuContext() {
        MenuManager.setContext(ModeContexts.RUN_MODE);
    }

    public String getMenuContext() {
        return ModeContexts.RUN_MODE;
    }

}
