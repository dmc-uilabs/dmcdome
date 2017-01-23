// RtfTester.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.util.Vector;

public class RtfTester extends JFrame {

    private RtfPanel panel1, panel2, panel3;
  protected String special_text="$DOC$";

    public RtfTester() {
        super("RTF Tester");
        JMenuBar mb = new JMenuBar();
        mb.add(RtfPanel.createStyleMenu());
        setJMenuBar(mb);

        panel1 = new RtfPanel();
        panel2 = new RtfPanel();
        panel3 = new RtfPanel();
        panel3.setEditable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1,3));
        contentPanel.add(makeOriginalPanel());
        contentPanel.add(makeTargetPanel());
        contentPanel.add(makeResultPanel());
        contentPanel.setMinimumSize(new Dimension(800,300));
        contentPanel.setPreferredSize(new Dimension(600,150));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(contentPanel,BorderLayout.CENTER);
        mainPanel.add(makeButtonPanel(),BorderLayout.SOUTH);

        getRootPane().setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    private RtfPanel makeRtfPanel() {
        RtfPanel p = new RtfPanel();
        return p;
    }

    private JPanel makeOriginalPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        p.setLayout(new BorderLayout());
        p.add(new JLabel("original"), BorderLayout.NORTH);
        p.add(new JScrollPane(panel1), BorderLayout.CENTER);
        return p;
    }

    private JPanel makeTargetPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        p.setLayout(new BorderLayout());
        p.add(new JLabel("include original ("+special_text+")"), BorderLayout.NORTH);
        p.add(new JScrollPane(panel2), BorderLayout.CENTER);
        return p;
    }

    private JPanel makeResultPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        p.setLayout(new BorderLayout());
        p.add(new JLabel("result"), BorderLayout.NORTH);
        p.add(new JScrollPane(panel3), BorderLayout.CENTER);
        return p;
    }

    private JPanel makeButtonPanel() {
        JPanel p = new JPanel();
        JButton showMergeButton = new JButton("show merge");
        showMergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mergeContent();
            }
        });
        p.add(showMergeButton);
        return p;
    }

    private void mergeContent() {
        System.out.println("merge content");
        // find out where to put panel 1 in panel 2
        // show result in panel 3
	
	//first take a look of the special text in panel 2
	
	
	
	String rtfContent1 = panel1.getRtfText();
	String rtfContent2 =panel2.getRtfText();
	
	//get position: assuming the special text only has one
	
        if(rtfContent2.indexOf(special_text)<0) 
	  {
	    //means nothing, then just append content in panel1 to panel2 at the end
	    panel3.setRtfText(rtfContent1+rtfContent2);
	  }
	else 
	  {//then insert the content in panel 1 to panel 2
	    StringBuffer newString=new StringBuffer(rtfContent2);
	    
	    int pos=rtfContent2.indexOf(special_text);
	    //replace 
	    newString.replace(pos,pos+special_text.length(),rtfContent1);
	    
	    panel3.setRtfText(newString.toString());
	    System.out.println(newString.toString());
	  }
        
    }

    public static void main(String[] args) {
        new RtfTester().show();
    }
}
