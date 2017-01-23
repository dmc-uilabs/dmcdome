/*
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Sep 24, 2002
 * Time: 10:56:00 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */


import mit.cadlab.dome.swing.Templates;
import mit.cadlab.dome.swing.DTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public class UnitEditorBasePanel extends DataObjectPanel {

      protected AutoTextField valueTextField;
      protected JButton okButton;
      protected JButton cancelButton;
      protected JLabel expressionLabel;
      protected String[] keywords;
      public String definition;
      protected DTable table;

    public UnitEditorBasePanel() {
        this(null);
    }

    public UnitEditorBasePanel(String[] keywords){
    this(null,keywords);
    }

    public UnitEditorBasePanel(DTable parentTable, String[] initKeywords) {
        table = parentTable;
        keywords = initKeywords;
        layoutComponents(createComponents());
        configureComponents();
    }

    protected JComponent[] createComponents() {
        valueTextField = new AutoTextField(keywords);
        expressionLabel = Templates.makeLabel("Definition:");

        okButton = Templates.makeButton("OK");
        okButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
                definition = valueTextField.getText();
                System.out.println(definition);
                table.setValueAt(definition,table.getSelectedRow(),2);
                CustomUnitBuildPanel.unitEditorDialog.setVisible(false);
            }
	    });

        cancelButton = Templates.makeButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
                CustomUnitBuildPanel.unitEditorDialog.setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        return new JComponent[]{expressionLabel,valueTextField,
			        buttonPanel};
    }

    protected void layoutComponents(JComponent[] comps) {
    // gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
    GridBagConstraints[] gbcs = {
      new GridBagConstraints(0,0,1,1,0.0,0.0,gbc.NORTHWEST,gbc.NONE,new Insets(0,0,0,0),0,0), // expressionLabel
      new GridBagConstraints(0,1,2,1,1.0,0.0,gbc.NORTHWEST,gbc.HORIZONTAL,new Insets(0,0,0,0),0,0), // valueTextField
      new GridBagConstraints(1,2,1,1,0.0,1.0,gbc.NORTHEAST,gbc.NONE,new Insets(10,0,0,0),0,0) // buttonPanel
	};
    Templates.layoutGridBagB(this,comps,gbcs);
    }

    // to be overridden by subclasses
    protected void configureComponents() {}

    // connect to data model
    public void setDataObject(DataObject data) {
    }

    protected PropertyChangeListener getPropertyListener() {
        return null;
    }
}
