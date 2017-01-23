// IterationRelationBuildPanel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.build;

import mit.cadlab.dome3.gui.menu.MenuManager;
import mit.cadlab.dome3.gui.mode.ModeContexts;
import mit.cadlab.dome3.gui.mode.build.BuildFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.AbstractDomeObjectGui;
import mit.cadlab.dome3.gui.objectmodel.NameTextField;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationDefinitionBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.equal.build.EqualRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.IteratorsChooser;
import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.iteration.IterationListCellRenderer;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoEditorDialog;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoRendererTable;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.IterationRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.iteration.ConditionIterationRelation;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.ConcreteEqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.swing.PythonEditor;
import mit.cadlab.dome3.swing.LineNumberedScrollPane;
import mit.cadlab.dome3.swing.GuiConstants;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IterationRelationBuildPanel extends AbstractDomeObjectGui {

    protected static GridBagConstraints gbc;

    protected ConditionIterationRelation relation;
    protected PropertyChangeListener propertyListener;
    protected ModelObjectNameListener nameListener;
    protected NameTextField nameField;
    protected JTabbedPane contentTabs, definitionTabs;
    protected IterationRelationDefinitionBuildPanel defPanel;
    protected CausalityInfoRendererTable dependencyTable;
    protected JButton editCausalityButton;
    protected JList IteratorsList;
    protected JButton editIteratorsButton;
    protected DocumentationBuildPanel docPanel;
    protected PythonEditor pyEditor;
    protected PythonEditor pyEditor_cond,pyEditor_init_cond;

    protected String[] broadCastingChoice = {"At the end of each loop", "After condition exits"};
    protected JComboBox broadcastingChangeComboBox;

    public IterationRelationBuildPanel(ConditionIterationRelation relation) {
        super(relation);
        if (relation == null)
            throw new IllegalArgumentException("IterationRelation gui - null EqualRelation");
        this.relation = relation;
        pyEditor = relation.getPythonEditor();
        pyEditor.setParentPanel(this);
        pyEditor_cond = relation.getPythonEditor_cond();
        pyEditor_cond.setParentPanel(this);
        pyEditor_init_cond = relation.getPythonEditor_init_cond();
        pyEditor_init_cond.setParentPanel(this);


        createComponents();

        propertyListener = getPropertyListener();
        this.relation.addPropertyChangeListener(propertyListener);
        this.relation.addModelObjectsListener(new RelationListener());
        nameListener = new ModelObjectNameListener();

        Iterator rObjs = relation.getModelObjects().iterator();
        while (rObjs.hasNext()) {
            Object obj = rObjs.next();
            if (obj instanceof Parameter) {
                Parameter rp = (Parameter) obj;
                rp.addPropertyChangeListener(ModelObject.NAME, nameListener);
            }
        }
    }

    protected PropertyChangeListener getPropertyListener() {
        return new IterationRelationPropertyChangeListener();
    }

    class IterationRelationPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            Object newValue = e.getNewValue();
            if (property.equals(ProceduralRelation.DEPENDENCY_INFO)) {
                dependencyTable.setDependencyInfo(new ArrayList(relation.getModelObjects()), (DependencyInfo) newValue);
                IteratorsList.repaint();
            } else if (property.equals(ProceduralRelation.BODY)) {
                pyEditor.setText((String) newValue);
            } else if (property.equals(IterationRelation.CONDITION)) {
                pyEditor_cond.setText((String) newValue);
            } else if (property.equals(IterationRelation.INIT_CONDITION)) {
                pyEditor_init_cond.setText((String) newValue);
            }
            else if (property.equals(IterationRelation.ITERATIONTYPE)) {
                //do nothing for now
            } else if (property.equals(IterationRelation.BROADCASTINGTYPE)) {
                Boolean isBroadcastingeachloop = (Boolean) newValue;
                if (isBroadcastingeachloop.booleanValue())
                    broadcastingChangeComboBox.setSelectedIndex(0);
                else
                    broadcastingChangeComboBox.setSelectedIndex(1);
            }
         }
    }

    class ModelObjectNameListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            if (property.equals(ModelObject.NAME)) {
                // update causality table
                Object obj = e.getSource();
                DependencyInfo dInfo = relation.getDependencyInfo();
                if (dInfo.getCausality(obj) == CausalityStatus.INDEPENDENT ||
                        dInfo.getCausality(obj) == CausalityStatus.RESULT) {
                    dependencyTable.setDependencyInfo(new ArrayList(relation.getModelObjects()), dInfo);
                }
            }
        }
    }

    protected void createComponents() {
        nameField = new NameTextField();
        nameField.setDomeObject(relation);
        contentTabs = Templates.makeTabbedPane();
        docPanel = new DocumentationBuildPanel(relation.getDocumentation());
        contentTabs.addTab("definition", makeDefinitionPanel());
        contentTabs.addTab("documentation", docPanel);
        contentTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setMenuContext();
            }
        });
        layoutComponent();
    }

    protected void layoutComponent() {
        JPanel p = new JPanel();
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
        JComponent[] comps = {Templates.makeLabel("name:"),
                              nameField
        };
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
        definitionTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setMenuContext();
            }
        });
        JPanel editorPanel = makeEditorPanel();
        JSplitPane spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, definitionTabs, editorPanel);
        spane.setDividerLocation(200);
        return spane;
    }

    protected JPanel makeCausalityPanel() {
        JPanel p = new JPanel();
        dependencyTable = new CausalityInfoRendererTable(new ArrayList(relation.getModelObjects()),
                relation.getDependencyInfo());
        editCausalityButton = Templates.makeButton("Edit causality information",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        DependencyInfo newInfo = CausalityInfoEditorDialog.showEditor(IterationRelationBuildPanel.this,
                                new ArrayList(relation.getModelObjects()), relation.getDependencyInfo());
                        if (newInfo != null)
                            relation.setDependencyInfo(newInfo);
                    }
                });
        editIteratorsButton = Templates.makeButton("Edit iterators",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        List newIterators = IteratorsChooser.showDialog(IterationRelationBuildPanel.this,
                                relation.getItems(CausalityStatus.INDEPENDENT), relation.getIteratorItems());
                        if (newIterators != null)                                    //the above is okay , since the chooser won't change the above directly
                            relation.setIteratorItems(newIterators);
                    }
                });


        JPanel iteratorsPanel = new JPanel();

        DefaultListModel listmodel = new DefaultListModel();

        for (Iterator i = relation.getIteratorItems().iterator(); i.hasNext();) {
            listmodel.addElement(i.next());
        }
        IteratorsList = Templates.makeList(listmodel);
        IteratorsList.setCellRenderer(new IterationListCellRenderer());
        IteratorsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        IteratorsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        IteratorsList.setVisibleRowCount(-1);
        IteratorsList.setBackground(Templates.DARKER_BACKGROUND_COLOR);

        relation.addPropertyChangeListener(IterationRelation.ITERATORS,new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
               ((DefaultListModel)IteratorsList.getModel()).clear();
               for(Iterator i=relation.getIteratorItems().iterator();i.hasNext();){
                   ((DefaultListModel)IteratorsList.getModel()).addElement(i.next());
               }
            }
        });
        JScrollPane listScroller = new JScrollPane(IteratorsList);
        listScroller.setPreferredSize(new Dimension(250, 80));

        JComponent[] comps_in_iteratorsPanel = {listScroller,
                                                editIteratorsButton
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs1 = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
        Templates.layoutGridBagB(iteratorsPanel, comps_in_iteratorsPanel, gbcs1);

        if (relation.getParameterCount() < 2) {
            editCausalityButton.setEnabled(false);
            IteratorsList.setEnabled(false);
            editIteratorsButton.setEnabled(false);
        }


        JComponent[] comps = {dependencyTable,
                              editCausalityButton,
                              iteratorsPanel
        };
        // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
        GridBagConstraints[] gbcs = {
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(0, 0, 5, 0), 0, 0),
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, gbc.SOUTH, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0)};
        Templates.layoutGridBagB(p, comps, gbcs);
        return p;
    }

    protected JPanel makeEditorPanel() {
        broadcastingChangeComboBox = Templates.makeComboBox(broadCastingChoice);
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
            new GridBagConstraints(1, 6, 1, 1, 0.0, 1.0, gbc.SOUTHEAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0)
        };
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


        JComponent[] comps = {
                              Templates.makeLabel("initial condition"),
                              spane_init_cond,
                              Templates.makeLabel("Do:",GuiConstants.FONT11B),
                              spane,
                              Templates.makeLabel("While:",GuiConstants.FONT11B),
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

    public void addNotify() {
        super.addNotify();
        SwingUtilities.windowForComponent(this).addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                docPanel.setModelText(); // hack since it doesn't seem to update properly
            }
        });
    }

    class RelationListener implements DListListener {
        public void intervalChanged(DListEvent e) {
            // do nothing
        }

        public void intervalAdded(DListEvent e) {
            addItems(e.getItems());
        }

        public void intervalRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsRemoved(DListEvent e) {
            removeItems(e.getItems());
        }

        public void itemsReplaced(DListEvent e) {
            // not supported
        }

        protected void addItems(List items) {
            Iterator rObjs = items.iterator();
            while (rObjs.hasNext()) {
                Object obj = rObjs.next();
                if (obj instanceof Parameter) {
                    Parameter rp = (Parameter) obj;
                    rp.addPropertyChangeListener(ModelObject.NAME, nameListener);
                }
            }
            if (relation.getModelObjects().size() > 1 && !editCausalityButton.isEnabled()) {
                editCausalityButton.setEnabled(true);
                IteratorsList.setEnabled(true);
                editIteratorsButton.setEnabled(true);
            }
        }

        protected void removeItems(List items) {
            Iterator rObjs = items.iterator();
            while (rObjs.hasNext()) {
                Object obj = rObjs.next();
                if (obj instanceof Parameter) {
                    Parameter rp = (Parameter) obj;
                    rp.removePropertyChangeListener(ModelObject.NAME, nameListener);
                }
            }
            if (relation.getModelObjects().size() < 2 && editCausalityButton.isEnabled()) {
                editCausalityButton.setEnabled(false);
                IteratorsList.setEnabled(false);
                editIteratorsButton.setEnabled(false);
            }
        }
    }

    // DomeObjectGui interface
    public String getTitlePrefix() {
        return "IterationRelation: ";
    }

    public String getHelpContext() {
        return null;
    }

    public void setMenuContext() {
        switch (contentTabs.getSelectedIndex()) {
            case 0: // definition
                switch (definitionTabs.getSelectedIndex()) {
                    case 0: // parameters
                        defPanel.setMenuContext();
                        return;
                    default: // causality
                        if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
                            MenuManager.setContext(ModeContexts.BUILD_PROJECT);
                        } else if (relation.getModel() instanceof PluginModel)
                            MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
                        else
                            MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
                }
                break;
            case 1: // documentation
                if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
                    MenuManager.setContext(ModeContexts.BUILD_PROJECT_DOCUMENTATION);
                } else if (relation.getModel() instanceof PluginModel)
                    MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL_DOCUMENTATION);
                else
                    MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL_DOCUMENTATION);
                break;
            default: // default for other tabs
                if (((DomeModel) relation.getModel()).getIntegrationProject() != null) {
                    MenuManager.setContext(ModeContexts.BUILD_PROJECT);
                } else if (relation.getModel() instanceof PluginModel)
                    MenuManager.setContext(ModeContexts.BUILD_PLUGINMODEL);
                else
                    MenuManager.setContext(ModeContexts.BUILD_DOMEMODEL);
        }
        BuildFocusTracker.notifyInFocus(this, relation.getModel());
    }

    public void test() {
        System.out.println(relation.getName() + ":test2");
    }

}
