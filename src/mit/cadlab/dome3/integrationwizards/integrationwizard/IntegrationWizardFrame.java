package mit.cadlab.dome3.integrationwizards.integrationwizard;

import mit.cadlab.dome3.integrationwizards.automatedmapping.AutoMap;
import mit.cadlab.dome3.integrationwizards.modeltranslators.DomeModelTranslator;
import mit.cadlab.dome3.integrationwizards.mappingstorage.MappingMatrix;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Feb 8, 2007
 * Time: 3:01:08 PM
 * To change this template use Options | File Templates.
 */
public class IntegrationWizardFrame extends JFrame implements ActionListener{

    private AutoMap autoMap;
    private MappingMatrix acceptedMappings;
    private ArrayList userAcceptedProjects;
    private ArrayList objectiveModels;

    private boolean completeMethod;

    private int directTolerance = 1;

    public final int BUTTON_HEIGHT = 20;

    JFrame mapFrame;
    JFrame integrationFrame;

    JLabel log;

    private String iconPath = System.getProperty("user.dir") + "\\src\\mit\\cadlab\\dome3\\icons\\domeServer.gif";

    private JButton completeButton = new JButton("Complete Method");
    private JButton directButton = new JButton("Direct Method");

    private JLabel statusField1 = new JLabel("");
    private JLabel statusField2 = new JLabel("");
    private JLabel statusField3 = new JLabel("");
    private JLabel statusField4 = new JLabel("");
    private JLabel statusField5 = new JLabel("");
    private JLabel statusField6 = new JLabel("");
    private JLabel statusField7 = new JLabel("");
    private JLabel statusField8 = new JLabel("");
    private JLabel statusField9 = new JLabel("");

    private JLabel statusField10 = new JLabel("");
    private JLabel statusField11 = new JLabel("");
    private JLabel statusField12 = new JLabel("");
    private JLabel statusField13 = new JLabel("");



    public IntegrationWizardFrame(ArrayList subscriptions) {
        this.setTitle("Dome Model Integration iModelWizardFrame");
        this.objectiveModels = DomeModelTranslator.translateDefaultSubscriptions(subscriptions);

        if(objectiveModels.size()>1){
            autoMap = new AutoMap();
            autoMap.newObjectiveModels(objectiveModels);
            layoutComponents();
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
            setVisible(true);
        }
        else{
            OneButton1Msg.showWarning(null, "Warning: Automated Mapping Error" ,"iModel contains less than 2 subscripions\n"+
                    "Automated Mapping Terminated", "Ok", OneButton1Msg.DEFAULT_SIZE);
            dispose();
        }

    }

    protected void layoutComponents() {
        completeMethod = false;
        completeMethod = false;

        setResizable(false);

        log = new JLabel("Application Started.");
        log.setMaximumSize(new Dimension(200, BUTTON_HEIGHT));
        log.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel logScrollPane = new JPanel(new GridLayout(1, 1));
        logScrollPane.add(log);

        Container content = this.getContentPane();
        content.setLayout(new GridBagLayout());
        content.setBackground(Color.WHITE);
        // content.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
        p.setPreferredSize(new Dimension(640, 100));
        p.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(BevelBorder.RAISED),BorderFactory.createEmptyBorder(10,10,10,10)));
        content.add(p,new GridBagConstraints(0, 0, 1, 1, 1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        JLabel label = new JLabel("<html><b><font size=3>Dome Automated Mapping</font></b><br>" +
                "This automated mapping algorithm has two implementations as described below. Mapping will only occur between <br>" +
                "parameters which map directly together. Neither mathematical expressions nor any other variety of relations <br>" +
                "between two parameters will be derived.");
        label.setMinimumSize(new Dimension(640, 100));
        p.add(label);
        ImageIcon icon = new ImageIcon(iconPath);
        JLabel iconLabel = new JLabel(icon, JLabel.RIGHT);
        p.add(iconLabel);


        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1,2,0,5));
        content.add(p1,new GridBagConstraints(0, 1, 1, 1, 1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

        JPanel p21 = new JPanel();
        p21.setPreferredSize(new Dimension(320, 150));
        p21.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(BevelBorder.RAISED),BorderFactory.createEmptyBorder(10,10,10,10)));
        p21.setLayout(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        JLabel label2 = new JLabel("<html><b>Complete Method:</b><br>"+
                "Maps together parameters using previous models stored in a <br>" +
                "database. Models are classified using a pattern matching <br>" +
                "algorithm. An integration project is then identified and <br>" +
                "used to guide parameter mapping. Unclassified models will <br>" +
                "be mapped together using the Direct Method");
        label2.setMinimumSize(new Dimension(638,100));
        c1.gridx = 0;
        c1.gridy = 0;
        p21.add(label2,c1);
        completeButton.setPreferredSize(new Dimension(120, BUTTON_HEIGHT));
        c1.gridy= 1;
        p21.add(completeButton,c1);
        p1.add(p21);


        JPanel p41 = new JPanel();
        p41.setPreferredSize(new Dimension(318, 150));
        p41.setBorder(BorderFactory.createCompoundBorder(new BevelBorder(BevelBorder.RAISED),BorderFactory.createEmptyBorder(10,5,10,10)));
        p41.setLayout(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        JLabel label4 = new JLabel("<html><b>Direct Method:</b><br>"+
                "Maps together parameters by comparing them directly. <br>"+
                "Parameters which are most similar are matched and are <br>"+
                "intelligently mapped together. This method is most <br>"+
                "successful when model parameters to be mapped are <br>" +
                "extremely similar in name and dimension.");
        label4.setMinimumSize(new Dimension(640,100));
        c2.gridx = 0;
        c2.gridy = 0;
        p41.add(label4,c2);
        directButton.setPreferredSize(new Dimension(100, BUTTON_HEIGHT));
        c2.gridy = 1;
        p41.add(directButton,c2);
        p1.add(p41);

        JPanel p3 = new JPanel();
        p3.setLayout(new GridLayout(1,2,0,5));
        content.add(p3,new GridBagConstraints(0, 2, 1, 1, 1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));


        JPanel p5 = new JPanel();
        p5.setBackground(Color.WHITE);
        p5.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p5.setLayout(new BoxLayout(p5,BoxLayout.Y_AXIS));
        p5.setPreferredSize(new Dimension(320, 150));
        p5.add(statusField1);
        p5.add(statusField2);
        p5.add(statusField3);
        p5.add(statusField4);
        p5.add(statusField5);
        p5.add(statusField6);
        p5.add(statusField7);
        p5.add(statusField8);
        p5.add(statusField9);
        p3.add(p5);

        JPanel p6 = new JPanel();
        p6.setBackground(Color.WHITE);
        p6.setBorder(BorderFactory.createEmptyBorder(10,20,10,10));
        p6.setLayout(new BoxLayout(p6,BoxLayout.Y_AXIS));
        p6.setPreferredSize(new Dimension(315, 150));
        p6.add(statusField10);
        p6.add(statusField11);
        p6.add(statusField12);
        p6.add(statusField13);
        p3.add(p6);

        content.add(logScrollPane,new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        completeButton.addActionListener(this);
        directButton.addActionListener(this);

        this.pack();
        this.setLocation(120, 130);
    }

    public void log(String str) {
        log.setText(str);
    }


    public void actionPerformed(ActionEvent e) {
        completeButton.setEnabled(false);
        directButton.setEnabled(false);

        if (e.getSource() == completeButton) {
            completeMethod = true;
            log("Application Running");
            statusField1.setText("<html><b>Status:</b></html>");
            statusField2.setText("Starting Complete Method");
            statusField3.setText("Loading Templates...");
            new Thread() {
                public void run()
                {
                    completeMethotPartOne();
                    directButton.setEnabled(true);
                }
            }.start();
        }

        if (e.getSource() == directButton) {
            log("Application Running");
            new Thread() {
                public void run(){
                    directMethod();
                }
            }.start();
        }
    }

    private void completeMethotPartOne(){
        long beginTime = new Date().getTime();
        if(autoMap.loadTemplates()){
            statusField3.setText("Loading Templates... Templates Loaded");
            //Model Integration
            statusField4.setText("IntegratingModels...");
            ArrayList acceptedProjects = autoMap.integrationMatching();
            if(acceptedProjects!=null)
                showIntegrationResults(acceptedProjects,beginTime);
            else{
                statusField5.setText("Models not Classified");
                statusField6.setText("Switching to Direct Method");
                directMethod();
                completeProcess(0,null,beginTime);
            }
        }
        else{
            statusField4.setText("Error loading Templates");
            statusField5.setText("Switching to Direct Method");
            directMethod();
            completeProcess(0,null,beginTime);
        }
    }


    private void completeMethodPart2(long preTime){
        this.setEnabled(true);
        long beginTime = new Date().getTime();
        MappingMatrix finalMatrix;
        if(userAcceptedProjects!=null){
            finalMatrix = autoMap.integrationMapping(userAcceptedProjects);

            //Direct Method if needed
            ArrayList unmappedModels = autoMap.getUnmappedModels();
            if(unmappedModels.size()>0){
                statusField7.setText("Unclassified Models Found:  Starting Direct Method...");
                finalMatrix = autoMap.directMethod(1,unmappedModels,finalMatrix);
            }
            completeProcess(preTime,finalMatrix,beginTime);
        }
        else
            statusField7.setText("iModels Rejected, Complete Method Terminated");
    }

    private void completeProcess(long preTime,MappingMatrix finalMatrix,long beginTime){
        long finalTime = new Date().getTime();
        final long elapsed = preTime + finalTime - beginTime;
        statusField8.setText("Complete Method:  Completed!");
        statusField9.setText("Computational Time (ms): "+elapsed);
        log("Application Finished");
        showMappingResults(finalMatrix);
    }


    private void directMethod(){
        statusField10.setText("<html><b>Status:</b></html>");
        statusField11.setText("Starting Direct Method...");
        statusField12.setText("");
        statusField13.setText("");
        Object[] possibilities = {"Tight", "Default", "Loose"};
        String input = (String)JOptionPane.showInputDialog(this,"Prescribe the similarity tolerance.\n"
                            + "<html><font size=3>(tighter tolerance implies parameters must be similar)</html>\n"
                            +" ", "Similarity Tolerance",JOptionPane.WARNING_MESSAGE, null,possibilities,"Default");
        if(input!=null){
            setTolerance(input);
            long beginTime = new Date().getTime();
            final MappingMatrix finalMatrix = autoMap.directMethod(directTolerance,objectiveModels,null);
            long finalTime = new Date().getTime();
		    final long elapsed = finalTime - beginTime;
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        showMappingResults(finalMatrix);
                        statusField12.setText("Direct Method:  Completed!");
                        statusField13.setText("Computational Time (ms): "+elapsed);
                        log("Application Finished");
                    }
                }
            );
        }
        else{
            statusField12.setText("Direct Method: Terminated");
            log("Application Finished");
        }
        if(!completeMethod)
            completeButton.setEnabled(true);
        directButton.setEnabled(true);
    }


    private void showMappingResults(MappingMatrix finalMatrix){
        if(finalMatrix!=null){
            mapFrame = new MappingResultsFrame(finalMatrix,objectiveModels);
            mapFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
            this.setEnabled(false);
            mapFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent event) {
                        acceptedMappings = ((MappingResultsFrame)mapFrame).getAcceptedMappings();
                        if(acceptedMappings!=null)
                            dispose();
                        else{
                            mapFrame = null;
                            setEnabled(true);
                        }
                    }
            });
        }
    }


    private ArrayList showIntegrationResults(ArrayList acceptedProjects,long beginTime){
        long finalTime = new Date().getTime();
        final long preTime = finalTime - beginTime;
        if(acceptedProjects!=null){
            integrationFrame = new IntegrationResultsFrame(acceptedProjects,true);
            integrationFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
            integrationFrame.setVisible(true);
            this.setEnabled(false);
            integrationFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent event) {
                        userAcceptedProjects = ((IntegrationResultsFrame)integrationFrame).getAcceptedProjects();
                        integrationFrame = null;
                        completeMethodPart2(preTime);
                    }
            });
        }
        return null;
    }

    public MappingMatrix getAcceptedMappings(){
        return acceptedMappings;
    }

    public ArrayList getObjectiveModels(){
        return objectiveModels;
    }


    private void setTolerance(String input){
        if(input.equals("Tight"))
            directTolerance = 1;
        else if(input.equals("Default"))
            directTolerance = 2;
        else if(input.equals("Loose"))
            directTolerance = 3;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }

        JFrame frame = new IntegrationWizardFrame(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        frame.setVisible(true);
    }
}
