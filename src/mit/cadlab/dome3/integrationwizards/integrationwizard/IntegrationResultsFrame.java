package mit.cadlab.dome3.integrationwizards.integrationwizard;

import mit.cadlab.dome3.integrationwizards.patternmatching.integration.ProjectMatching;
import mit.cadlab.dome3.integrationwizards.patternmatching.integration.MatchedModelPair;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.ProjectTemplate;
import mit.cadlab.dome3.integrationwizards.templatecreation.ProjectTemplateManagement.MappedModels;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Mar 5, 2007
 * Time: 12:56:20 PM
 * To change this template use Options | File Templates.
 */
public class IntegrationResultsFrame extends JFrame implements ActionListener{

    private ArrayList acceptedProjects;
    private ArrayList userAcceptedProjects;
    private Container content;
    private boolean useCheckBox;
    private ButtonGroup group = new ButtonGroup();

    private JButton acceptButton;
    private JButton rejectButton;
    private final int BUTTON_HEIGHT = 20;

    public IntegrationResultsFrame(ArrayList acceptedProjects,boolean useCheckBox){
        this.setTitle("iModel Classification Results");
        this.acceptedProjects = acceptedProjects;
        this.userAcceptedProjects = new ArrayList();
        this.useCheckBox = useCheckBox;
        layoutComponents();
    }

    private void layoutComponents(){
        setResizable(false);
        content = this.getContentPane();
        content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JPanel p = new JPanel();
        JScrollPane pane = new JScrollPane(p);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        for(int projectIndex=0;projectIndex<acceptedProjects.size();projectIndex++){
            ProjectMatching project = (ProjectMatching)acceptedProjects.get(projectIndex);
            p.add(layoutProject(project));
        }
        if(p.getPreferredSize().getHeight()>500)
            pane.setPreferredSize(new Dimension(550,500));
        content.add(pane);

        JPanel p2 = new JPanel();
        if(useCheckBox)
            acceptButton = new JButton("Accept Checked iModel(s)");
        else
            acceptButton = new JButton("Add Selected iModel");
        acceptButton.setPreferredSize(new Dimension(170, BUTTON_HEIGHT));
        rejectButton = new JButton("Reject All Possible iModels");
        rejectButton.setPreferredSize(new Dimension(170, BUTTON_HEIGHT));
        p2.add(acceptButton);
        p2.add(rejectButton);
        content.add(p2);

        acceptButton.addActionListener(this);
        rejectButton.addActionListener(this);
        this.pack();
        this.setLocation(200, 200);
    }

    private JPanel layoutProject(ProjectMatching project){
        ProjectTemplate template = project.getTemplate();
        ArrayList matchedModels = project.getMatchedModelPairs();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(BevelBorder.RAISED),BorderFactory.createEmptyBorder(5,5,5,5)));

        JPanel topPanel = new JPanel();
        JPanel namePanel = new JPanel();
        JLabel name = new JLabel("<html><b>Classified iModel Name:  " +template.name +"</b></html>");
        namePanel.add(name);
        namePanel.setPreferredSize(new Dimension(470,20));
        topPanel.add(namePanel);
        JToggleButton box;
        if(useCheckBox)
            box = new JCheckBox();
        else{
            box = new JRadioButton();
            group.add(box);
        }
        project.addCheckBox(box);
        box.setSelected(false);
        topPanel.add(box);
        p.add(topPanel);

        JPanel p1 = new JPanel();
        p1.setBackground(Color.WHITE);
        p1.setLayout(new GridBagLayout());
        JPanel p2 = new JPanel();
        p2.setBackground(Color.WHITE);
        JLabel label2 = new JLabel("<html><b>Objective Model Name</b></html>");
        //p2.setPreferredSize(new Dimension(240,25));
        p2.add(label2);
        JPanel p4 = new JPanel();
        p4.setBackground(Color.WHITE);
        JLabel label4 = new JLabel("<html><b>Matched Template Name</b></html>");
        //p4.setPreferredSize(new Dimension(240,25));
        p4.add(label4);
        p1.add(p2,new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        p1.add(p4,new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        p.add(p1);
        p.add(layoutSubscriptions(template,matchedModels));
        return p;
    }

    private JPanel layoutSubscriptions(ProjectTemplate project,ArrayList modelPairings)
    {
        JPanel p = new JPanel();p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

        ArrayList finalMappedModels = project.getFinalMappedModelPairs();
        for(int matchIndex=0;matchIndex<modelPairings.size();matchIndex++){
            MatchedModelPair matchedModels = ((MatchedModelPair)modelPairings.get(matchIndex));
            String templateID = matchedModels.getTemplateModel().id;
            //Find the matched objective/template model pair which corresponds to each of the
            //mapped subscription models in the current mapped pair of subscription models
            for (int mapIndex=0;mapIndex<finalMappedModels.size();mapIndex++){
                MappedModels mappedModels = (MappedModels)finalMappedModels.get(mapIndex);
                if(templateID.equals(mappedModels.modelOneID)){
                    p.add(layoutModelPanel(matchedModels.getObjectiveModel().name,matchedModels.getTemplateModel().name));
                    break;
                }
                else if(templateID.equals(mappedModels.modelTwoID)){
                    p.add(layoutModelPanel(matchedModels.getObjectiveModel().name,matchedModels.getTemplateModel().name));
                    break;
                }
            }
        }

        return p;
    }

    private JPanel layoutModelPanel(String objectiveName, String templateName){
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(0,10,5,10));
        p.setBackground(Color.WHITE);
        p.setLayout(new GridBagLayout());
        JLabel label1 = new JLabel("<html>" + objectiveName + "</html>");
        label1.setPreferredSize(new Dimension(240,25));
        JLabel label2 = new JLabel("<html>" +templateName + "</html>");
        label2.setPreferredSize(new Dimension(240,25));
        p.add(label1,new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        p.add(label2,new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        return p;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == acceptButton){
            for(int projectIndex=0;projectIndex<acceptedProjects.size();projectIndex++){
                ProjectMatching project = (ProjectMatching)acceptedProjects.get(projectIndex);
                if(project.checkBoxStatus())
                    userAcceptedProjects.add(project);
            }
            if(userAcceptedProjects.size()==0)
                userAcceptedProjects = null;
            dispose();
        }
        if (e.getSource() == rejectButton){
            userAcceptedProjects = null;
            dispose();
        }
    }

    public ArrayList getAcceptedProjects(){
        return userAcceptedProjects;
    }

}
