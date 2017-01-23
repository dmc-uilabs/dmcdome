package mit.cadlab.dome3.integrationwizards.imodelwizard;

import mit.cadlab.dome3.integrationwizards.automatedmapping.AutoMap;
import mit.cadlab.dome3.integrationwizards.patternmatching.integration.ProjectMatching;
import mit.cadlab.dome3.integrationwizards.integrationwizard.IntegrationResultsFrame;
import mit.cadlab.dome3.integrationwizards.integrationwizard.MappingResultsFrame;
import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.waitcursor.StatusWindow;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.util.ArrayList;
import java.util.Collection;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Mar 6, 2007
 * Time: 2:51:23 PM
 * To change this template use Options | File Templates.
 */
public class iModelWizardFrame extends JFrame implements ActionListener{

    private ArrayList resources;
    private AutoMap autoMap;
    private Container content;
    private ArrayList integrationProjects;
    private MappingMatrix acceptedMappings;

    private JFrame integrationFrame;
    private ProjectMatching acceptedProject;
    private JFrame mapFrame;

    private JButton startButton = new JButton("start");
    JPanel p3;
    JPanel cards;

    JLabel pLabel;

    private int BUTTON_HEIGHT = 20;

    public iModelWizardFrame(ArrayList resources){
        this.autoMap = new AutoMap();
        this.resources = resources;
        this.integrationProjects = new ArrayList();
        this.setTitle("iModel Wizard");
        layoutComponents();
    }

    private void layoutComponents(){
        setResizable(false);
        content = this.getContentPane();
        //content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        content.setLayout(new GridBagLayout());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(BevelBorder.RAISED),BorderFactory.createEmptyBorder(5,5,5,5)));
        JLabel label = new JLabel("<html><b>iModel Wizard Description:</b></html>");
        JLabel label2 = new JLabel("<html>This wizard searches previously built iModels<br>"+
                "and compares them with the resources in this integration project.<br>"+
                "It will identify and display possible resource combinations. If a<br>"+
                "combination is accepted, it will create the iModel and open it.<br>"+
                "The option is also available to implement the mappings which <br>" +
                "existed in the previous iModel. Once the iModel has accepted, <br>"+
                "these possible mappings will be displayed and can be accepted <br>"+
                "or rejected.</html>");
        p.setPreferredSize(new Dimension(330,160));
        p.setMinimumSize(new Dimension(330,160));
        p.add(label);
        p.add(label2);
        content.add(p,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        JPanel p2 = new JPanel();
        p2.setLayout(new GridBagLayout());
        startButton.setPreferredSize(new Dimension(100, BUTTON_HEIGHT));
        startButton.addActionListener(this);
        p2.add(startButton);
        p2.setPreferredSize(new Dimension(330,30));
        content.add(p2,new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        this.pack();
        this.setLocation(200, 200);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton){
            setEnabled(false);
            new Thread() {
                public void run(){
                    begin();
                    setEnabled(true);
                }
            }.start();
        }
    }

    private void begin(){
        //First all of the interfaces in each resource are classified
        Point point = this.getLocationOnScreen();
        JFrame wait = StatusWindow.show("iModel Wizard Status:","Classifying Resource Interfaces",new Point((int)point.getX()-20,(int)point.getY()+50));
        if(autoMap.loadTemplates()){
            boolean error = false;
            try{
                addObjectiveModelsAndRun(new ArrayList(),0,resources.size());
            }
            catch(Exception e){
                error = true;
                OneButton1Msg.show(null, "error", "Integration Classification Error:",
                    "Unknown Error Occured. Wizard Terminated", "ok", new Dimension(180, 80));
            }
            wait.dispose();
            if(!error)
                if(integrationProjects.size()>0){
                    showResults();
                }
                else{
                    OneButton1Msg.showError(null,"iModel Wizard Status:",
                        "Zero similar projects were found", "ok", new Dimension(180, 80));
                    this.dispose();
               }
            else
                this.dispose();
        }
        else{
            OneButton1Msg.showError(null,"iModel Wizard Status:",
                    "Template Loading Error. Wizard Terminated", "ok", new Dimension(180, 80));
            this.dispose();
        }
    }

    //recusive algorithm to run through all of the resource interface possibilities
    private void addObjectiveModelsAndRun(ArrayList objectiveModels,int recIndex,int numResources){
        SkeletonResource resource = (SkeletonResource)resources.get(recIndex);
        ArrayList graphs = resource.getGraphs();
        FuzzyARG graph;
        for(int graphIndex=0;graphIndex<graphs.size();graphIndex++){
            graph = (FuzzyARG)graphs.get(graphIndex);
            //if this is the second matched interface graph, you must reset the entry in the two Lists
            if((recIndex+1) <= objectiveModels.size())
                objectiveModels.set(recIndex,graph);
            else
                objectiveModels.add(graph);
            //If this is the last resource model, run integrationMatching method to find possible iModels
            if((recIndex+1) == numResources){
                autoMap.newObjectiveModels(objectiveModels);
                ArrayList projects = autoMap.integrationMatching();
                if(projects!=null)
                    integrationProjects.addAll((Collection)projects.clone());
            }
            //otherwise go to the next resource model to add the next interface graph
            else
                addObjectiveModelsAndRun(objectiveModels,(recIndex+1),numResources);
        }
    }

    private void showResults(){
        integrationFrame = new IntegrationResultsFrame(integrationProjects,false);
        integrationFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
        integrationFrame.setVisible(true);
        this.setEnabled(false);
        integrationFrame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) {
                integrationProjects=null;
                ArrayList proj = ((IntegrationResultsFrame)integrationFrame).getAcceptedProjects();
                if(proj!=null){
                    acceptedProject = (ProjectMatching)proj.get(0);
                    mappings();
                }
                integrationFrame = null;
            }
        });
    }

    private void mappings(){
        int n = JOptionPane.showConfirmDialog(this,"View Corresponding iModel Mappings?","Model Mapping",
                JOptionPane.YES_NO_OPTION);
        if(n==0){
            Point point = this.getLocationOnScreen();
        JFrame wait = StatusWindow.show("iModel Wizard Status:","Loading Integration Mappings",new Point((int)point.getX()-20,(int)point.getY()+50));
            ArrayList projects = new ArrayList();
            projects.add(acceptedProject);
            boolean error = false;
            MappingMatrix mappingMatrix = null;
            try{
                mappingMatrix = autoMap.integrationMapping(projects);
            }
            catch(Exception e){
                error = true;
                OneButton1Msg.showError(null, "Integration Mapping Error" ,"Unknown Error. Mappings Will not be Implemented."
                    , "Ok", OneButton1Msg.DEFAULT_SIZE);
            }
            wait.dispose();
            if(error)
                this.dispose();
            else
                showMappingResults(mappingMatrix,acceptedProject.getObjectiveModels());
        }
        else
            dispose();
    }

    private void showMappingResults(MappingMatrix mappingMatrix,ArrayList objectiveModels){
        if(mappingMatrix!=null){
            mapFrame = new MappingResultsFrame(mappingMatrix,objectiveModels);
            mapFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
            this.setEnabled(false);
            mapFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent event) {
                        acceptedMappings = ((MappingResultsFrame)mapFrame).getAcceptedMappings();
                        if(acceptedMappings!=null)
                            dispose();
                        else{
                            mapFrame = null;
                            dispose();
                        }
                    }
            });
        }
    }

    public ProjectMatching getAcceptedProject(){
        return acceptedProject;
    }

    public MappingMatrix getAcceptedMappings(){
        return acceptedMappings;
    }

    public ArrayList getResources(){
        return resources;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }

        JFrame frame = new iModelWizardFrame(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        frame.setVisible(true);
    }
}
