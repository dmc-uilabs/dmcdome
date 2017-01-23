// OptionListDialog.java
package mit.cadlab.dome.gui.components.shared;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class OptionListDialog extends JDialog implements ActionListener {

    DefaultListModel optionsModel, optionsPickedModel;
    JList options, optionsPicked;
    JButton rightButton, leftButton, upButton, downButton, commitButton, cancelButton;
    OptionListListener listener;
    GridBagConstraints gbc;
    Object[] originalChoices;
    Dimension listPrefSize, buttonsPrefSize;

    public static Object[] showOptionListDialog(JFrame parent,
						String title,
						Object[] choices,
						Object[] choicesPicked){
	OptionListListener l = new OptionListListener();
	OptionListDialog d = new OptionListDialog(parent,l,title,choices,choicesPicked);
	d.show();
	return l.getValues();
    }

    public static Object[] showOptionListDialog(JDialog parent,
						String title,
						Object[] choices,
						Object[] choicesPicked){
	OptionListListener l = new OptionListListener();
	OptionListDialog d = new OptionListDialog(parent,l,title,choices,choicesPicked);
	d.show();
	return l.getValues();
    }

    public OptionListDialog(JFrame parent, OptionListListener listener, String title,
			    Object[] choices, Object[] choicesPicked){
	super(parent,title,true); // modal
	initializeDialog(listener,choices,choicesPicked);
    }

    public OptionListDialog(JDialog parent, OptionListListener listener, String title,
			    Object[] choices, Object[] choicesPicked){
	super(parent,title,true); // modal
	initializeDialog(listener,choices,choicesPicked);
    }

    protected void initializeDialog(OptionListListener listener,
				    Object[] choices, Object[] choicesPicked){
	this.listener = listener;
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	createGuiComponents(choices,choicesPicked);
	getContentPane().add(makeLayout(),BorderLayout.CENTER);
	setSize(getPreferredSize());
	setResizable(false);
    }

    protected void createGuiComponents(Object[] choices, Object[] choicesPicked){
	// create Buttons
	rightButton = Templates.makeListChooserButton("right",this);
	leftButton = Templates.makeListChooserButton("left",this);
	upButton = Templates.makeListChooserButton("up",this);
	downButton = Templates.makeListChooserButton("down",this);
	commitButton = Templates.makeButton("OK",this);
	cancelButton = Templates.makeButton("cancel",this);

	// create optionsModel
	optionsModel = new DefaultListModel();
	for (int i=0; i<choices.length; ++i)
	    optionsModel.addElement(choices[i]);
	options = Templates.makeList(optionsModel);
	listPrefSize = options.getPreferredSize();

	// create optionsPickedModel
	optionsPickedModel = new DefaultListModel();
	for (int i=0; i<choicesPicked.length; ++i){
	    Object choice = choicesPicked[i];
	    if (optionsModel.contains(choice)){
		if (optionsPickedModel.contains(choice)){
		    System.out.println("error! can not add choice twice: "+choice);
		} else {
		    optionsModel.removeElement(choice);
		    optionsPickedModel.addElement(choice);
		}
	    } else {
		System.out.println("error! can not add type "+choice);
	    }
	}
	originalChoices = optionsPickedModel.toArray();
	optionsPicked = Templates.makeList(optionsPickedModel);
    }

    protected JPanel makeLayout(){
	JPanel p = new JPanel();
	
	// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
	GridBagConstraints[] gbcs = {
	    new GridBagConstraints(0,0,1,1,1.0,0.0,gbc.SOUTHWEST,gbc.NONE,new Insets(0,0,3,0),0,0), // options
	    new GridBagConstraints(2,0,1,1,1.0,0.0,gbc.SOUTHWEST,gbc.NONE,new Insets(0,0,3,0),0,0), // selections
	    new GridBagConstraints(0,1,1,2,1.0,1.0,gbc.NORTHWEST,gbc.NONE,new Insets(0,0,0,0),0,0), // optionList
	    new GridBagConstraints(2,1,1,2,1.0,1.0,gbc.NORTHWEST,gbc.NONE,new Insets(0,0,0,0),0,0), //selectionList
	    new GridBagConstraints(1,1,1,1,0.0,0.0,gbc.NORTH,gbc.NONE,new Insets(0,5,0,5),0,0), // rightButton
	    new GridBagConstraints(3,1,1,1,0.0,0.0,gbc.NORTH,gbc.NONE,new Insets(0,5,0,0),0,0), // upButton
	    new GridBagConstraints(1,2,1,1,0.0,0.0,gbc.NORTH,gbc.NONE,new Insets(0,5,0,5),0,0), // leftButton
	    new GridBagConstraints(3,2,1,1,0.0,0.0,gbc.NORTH,gbc.NONE,new Insets(0,5,0,0),0,0), // downButton
	    new GridBagConstraints(2,3,1,1,0.0,0.0,gbc.NORTHEAST,gbc.NONE,new Insets(5,0,0,0),0,-6) // buttonPanel
		};

	JComponent[] comps = {Templates.makeLabel("options:"),
			     Templates.makeLabel("selections:"),
			     new JScrollPane(options),
			     new JScrollPane(optionsPicked),
			     rightButton,
			     upButton,
			     leftButton,
			     downButton,
			     makeButtonPanel()};

	// set size of scrollpanes to be max of size of list and button panel
	Dimension prefSize = new Dimension(Math.max(listPrefSize.width+40,buttonsPrefSize.width+2),listPrefSize.height+40);
	comps[2].setPreferredSize(prefSize);
	comps[3].setPreferredSize(prefSize);
	comps[2].setMinimumSize(prefSize);
	comps[3].setMinimumSize(prefSize);
	comps[2].setMaximumSize(prefSize);
	comps[3].setMaximumSize(prefSize);

	// set GridBagLayout
	GridBagLayout gridbag = new GridBagLayout();
	p.setLayout(gridbag);
	for (int i=0; i<gbcs.length; ++i){
	    gridbag.setConstraints(comps[i],gbcs[i]);
	    p.add(comps[i]);
	}

	p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	return p;
    }

    protected JPanel makeLayout2(){
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
	Dimension scrollPaneSize = new Dimension(listPrefSize.width+40,listPrefSize.height+40);
	JScrollPane optionsPane = new JScrollPane(options);
	optionsPane.setPreferredSize(scrollPaneSize);
	p.add(optionsPane);
	p.add(makeButtonPanel1());
	JScrollPane optionsPickedPane = new JScrollPane(optionsPicked);
	optionsPickedPane.setPreferredSize(scrollPaneSize);
	p.add(optionsPickedPane);
	p.add(makeButtonPanel2());
	return p;
    }

    protected JPanel makeButtonPanel(){
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
	p.add(commitButton);
	p.add(Box.createHorizontalStrut(5));
	p.add(cancelButton);
	buttonsPrefSize = p.getPreferredSize();
	return p;
    }

    public void actionPerformed(ActionEvent e){
	Object source = e.getSource();
	if (source == rightButton){
	    rightAction();
	} else if (source == leftButton){
	    leftAction();
	} else if (source == upButton){
	    upAction();
	} else if (source == downButton){
	    downAction();
	} else if (source == commitButton){
	    commitAction();
	} else if (source == cancelButton){
	    cancelAction();
	} else {
	    System.out.println("don't know how to deal with "+e);
	}
    }

    protected void rightAction(){
	Object[] choices = options.getSelectedValues();
	for (int i=0; i<choices.length; ++i){
	    optionsModel.removeElement(choices[i]);
	    optionsPickedModel.addElement(choices[i]);
	}
    }

    protected void leftAction(){
	Object[] choices = optionsPicked.getSelectedValues();
	for (int i=0; i<choices.length; ++i){
	    optionsPickedModel.removeElement(choices[i]);
	    optionsModel.addElement(choices[i]);
	}
    }

    protected void upAction(){
	int[] choices = optionsPicked.getSelectedIndices();
	if (choices.length!=0 && choices[0]==0) return; // at beginning
	for (int i=0; i<choices.length; ++i){
	    int currentIndex = choices[i];
	    Object choice = optionsPickedModel.remove(currentIndex);
	    optionsPickedModel.insertElementAt(choice,currentIndex-1);
	    choices[i] = choices[i]-1;
	}
	optionsPicked.setSelectedIndices(choices);
    }

    protected void downAction(){
	int[] choices = optionsPicked.getSelectedIndices();
	int nChoices = choices.length;
	if (nChoices!=0 && choices[nChoices-1]==(optionsPickedModel.size()-1)) return; // at end
	for (int i=choices.length-1; i>=0; --i){
	    int currentIndex = choices[i];
	    Object choice = optionsPickedModel.remove(currentIndex);
	    optionsPickedModel.insertElementAt(choice,currentIndex+1);
	    choices[i] = choices[i]+1;
	}
	optionsPicked.setSelectedIndices(choices);
    }

    protected void commitAction(){
	listener.setValues(optionsPickedModel.toArray());
	dispose();
    }

    protected void cancelAction(){
	dispose();
    }

    protected JPanel makeButtonPanel1(){
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
	p.add(Box.createVerticalGlue());
	p.add(rightButton);
	p.add(Box.createVerticalStrut(10));
	p.add(leftButton);
	p.add(Box.createVerticalGlue());
	return p;
    }

    protected JPanel makeButtonPanel2(){
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
	p.add(Box.createVerticalGlue());
	p.add(upButton);
	p.add(Box.createVerticalStrut(10));
	p.add(downButton);
	p.add(Box.createVerticalGlue());
	p.add(commitButton);
	p.add(cancelButton);
	return p;
    }

    public static void main(String[] args){
	String[] choices = {"Number","Boolean","String","Vector","Matrix",
	"List","Table","Dictionary","File"};
	String[] choicesPicked = {"Number","Vector"};
	JFrame parent = null;
	Object[] answer = showOptionListDialog(parent,
					       "OptionListDialog",
					       choices,
					       choicesPicked);
	if (answer==null)
	    System.out.println("cancelled");
	else {
	    System.out.print("picked: ");
	    for (int i=0; i<answer.length; ++i){
		System.out.print(answer[i]+" ");
	    }
	    System.out.println();
	}
	System.exit(0);
    }

}

class OptionListListener {
    Object[] values=null;
    
    public void setValues(Object[] values){
	    this.values = values;
    }
    
    public Object[] getValues(){
	return values;
    }
}
