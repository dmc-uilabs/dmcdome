package mit.cadlab.dome3.integrationwizards.integrationwizard;

import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ModelMapping;
import mit.cadlab.dome3.integrationwizards.mappingstorage.ParameterPair;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 20, 2007
 * Time: 3:47:30 PM
 * To change this template use Options | File Templates.
 */
public class MappingResultsFrame extends JFrame implements ActionListener{
    private MappingMatrix finalMappingMatrix;
    private MappingMatrix acceptedMappingMatrix;
    private ArrayList objectiveModels;
    private Container content;
    private boolean acceptedPairs;

    private int numModels;

    private JButton acceptButton;
    private JButton rejectButton;
    private final int BUTTON_HEIGHT = 20;

    public MappingResultsFrame(MappingMatrix mappingMatrix, ArrayList objectiveModels){
        this.setTitle("Automated Mapping Results");
        this.objectiveModels = objectiveModels;
        this.numModels = objectiveModels.size();
        this.finalMappingMatrix = mappingMatrix;
        this.acceptedMappingMatrix = new MappingMatrix(numModels);
        this.acceptedPairs = false;
        layoutComponents();
    }

    private void layoutComponents(){
        setResizable(false);
        content = this.getContentPane();
        content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JPanel p1 = new JPanel();
        p1.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(BevelBorder.RAISED),BorderFactory.createEmptyBorder(5,5,5,5)));
        p1.setLayout(new GridBagLayout());
        JLabel label1 = new JLabel("<html><b><font size=3>Mappings</font></b>");
        p1.add(label1,new GridBagConstraints(3, 0, 1, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        content.add(p1);

        JPanel p = new JPanel();
        JScrollPane pane = new JScrollPane(p);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        boolean mappingsExist = false;
        for (int columnIndex=0;columnIndex<numModels;columnIndex++)
        {
        	for (int rowIndex=(columnIndex+1);rowIndex<numModels;rowIndex++){
                Object matrixEntry = finalMappingMatrix.getEntry(columnIndex,rowIndex);
                if(matrixEntry instanceof ModelMapping){
                    p.add(layoutModelMappings((ModelMapping)matrixEntry,columnIndex,rowIndex));
                    mappingsExist = true;
                }
            }
        }

        if(!mappingsExist){
            OneButton1Msg.show(null, "error", "Integration Wizard Status:",
                            "Zero mappings were identified", "ok", new Dimension(180, 80));
            acceptedMappingMatrix = null;
            dispose();
        }
        else{
            if(p.getPreferredSize().getHeight()>500)
                pane.setPreferredSize(new Dimension(525,500));
            content.add(pane);

            JPanel p2 = new JPanel();
            acceptButton = new JButton("Accept Checked Mappings");
            acceptButton.setPreferredSize(new Dimension(170, BUTTON_HEIGHT));
            rejectButton = new JButton("Reject All Mappings");
            rejectButton.setPreferredSize(new Dimension(170, BUTTON_HEIGHT));
            p2.add(acceptButton);
            p2.add(rejectButton);
            content.add(p2);

            acceptButton.addActionListener(this);
            rejectButton.addActionListener(this);
            this.pack();
            this.setLocation(200, 200);
            this.setVisible(true);
        }
    }

    private JPanel layoutModelMappings(ModelMapping modelMappings,int columnIndex,int rowIndex){
        String modelAName = ((FuzzyARG)objectiveModels.get(columnIndex)).name;
        String modelBName = ((FuzzyARG)objectiveModels.get(rowIndex)).name;

        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(BevelBorder.RAISED),BorderFactory.createEmptyBorder(10,5,10,10)));

        JPanel p2 = new JPanel();
        p2.setBackground(Color.WHITE);
        p2.setLayout(new GridBagLayout());
        JLabel label2 = new JLabel("<html><b>"+modelAName+"</b></html>");
        label2.setPreferredSize(new Dimension(225,40));
        JLabel label3 = new JLabel("<html><b>"+modelBName+"</b></html>");
        label3.setPreferredSize(new Dimension(220,40));
        JLabel label6 = new JLabel();
        label6.setPreferredSize(new Dimension(25,20));
        p2.add(label2,new GridBagConstraints(1, 0, 1, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p2.add(label3,new GridBagConstraints(2, 0, 1, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p2.add(label6,new GridBagConstraints(3, 0, 1, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p.add(p2);
        ArrayList outputMappings = modelMappings.getOutputParameterMapping();
        ArrayList inputMappings = modelMappings.getInputParameterMapping();
        if(outputMappings.size()>0)
            p.add(layoutMappings(outputMappings,columnIndex,rowIndex,"output"));
        if(inputMappings.size()>0)
            p.add(layoutMappings(inputMappings,columnIndex,rowIndex,"input"));
        return p;
    }

    private JPanel layoutMappings(ArrayList mappings,int columnIndex,int rowIndex,String inout){
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

        for(int mapIndex=0;mapIndex<mappings.size();mapIndex++)
            p.add(layoutMapping((ParameterPair)mappings.get(mapIndex),columnIndex,rowIndex,inout));
        return p;
    }

    private JPanel layoutMapping(ParameterPair pair,int col,int row,String inout)
    {
        String parameterAName = (String)pair.getColumnNode().getName().getSampleSet().iterator().next();
        String parameterBName = (String)pair.getRowNode().getName().getSampleSet().iterator().next();
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(480,25));
        p.setBackground(Color.WHITE);
        p.setLayout(new GridBagLayout());
        JLabel label4 = new JLabel(parameterAName);
        label4.setPreferredSize(new Dimension(220,25));
        JLabel label5 = new JLabel(parameterBName);
        label5.setPreferredSize(new Dimension(220,25));
        JCheckBox box = new JCheckBox();
        box.setSelected(true);
        box.setBackground(Color.WHITE);
        p.add(label4,new GridBagConstraints(1, 0, 1, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p.add(label5,new GridBagConstraints(2, 0, 1, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        p.add(box,new GridBagConstraints(3, 0, 1, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        pair.addCheckBox(box);
        acceptedMappingMatrix.addParameterPair(col,row,pair,inout);
        return p;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == acceptButton){
            processAcceptedMappings();
            if(!acceptedPairs)
                acceptedMappingMatrix = null;
            dispose();
        }
        if (e.getSource() == rejectButton){
            acceptedMappingMatrix = null;
            dispose();
        }
    }

    private void processAcceptedMappings()
    {
        for (int columnIndex=0;columnIndex<numModels;columnIndex++)
        {
        	for (int rowIndex=(columnIndex+1);rowIndex<numModels;rowIndex++){
                Object entry = acceptedMappingMatrix.getEntry(columnIndex,rowIndex);
                if(entry instanceof ModelMapping){
                    ModelMapping map = (ModelMapping)entry;
                    map.setOutputParameterMapping(processMappings(map.getOutputParameterMapping()));
                    map.setInputParameterMapping(processMappings(map.getInputParameterMapping()));
                    acceptedMappingMatrix.setEntry(columnIndex,rowIndex,map);
                }
            }
        }
    }

    private ArrayList processMappings(ArrayList mappings)
    {
        ArrayList acceptedMappings = new ArrayList();
        for(int mapIndex=0;mapIndex<mappings.size();mapIndex++)
        {
            ParameterPair pair = (ParameterPair)mappings.get(mapIndex);
            if(pair.checkBoxStatus()){
                acceptedMappings.add(pair);
                acceptedPairs = true;
            }
        }
        return acceptedMappings;
    }

    public MappingMatrix getAcceptedMappings(){
        return acceptedMappingMatrix;
    }
}
