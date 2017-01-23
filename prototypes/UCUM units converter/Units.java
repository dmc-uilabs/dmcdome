/*
 * Copyright (c) 1998 The Regenstrief Institute.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * Written by Gunther Schadow.
 *
 * $Id: Units.java,v 1.1.1.1 2003/05/05 16:12:32 renu Exp $
 */

import units.ParseException;
import units.Unit;
import units.UnitTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Enumeration;

//you might want to change this code to give the user the option to
// either clobber the existing unit table or add to it.


public class Units  extends JFrame {
  JFrame win = null;
  NumberFormat numForm = NumberFormat.getInstance();

  public static void main(String argv[]) {
      new Units().show();
  }

    Input from_value;
    Input from_unit;
    Input to_value;
    Input to_unit;
    JTextField status;
    Choice dbchoice;
    JMenuBar myMenu;
    Container contentPane;
    String filename;
    int returnVal;
    String oldUrl;

  static String table_names[] = {
    "Unified Code for Units of Measure (UCUM) c/i",
  };

  static String file_names[] = {
    "ucum-ci",
  };

  int current_table = 0;

  void read() throws IOException  {
      oldUrl = new File("").getAbsolutePath() +"/"+
      file_names[current_table] +
      ".units";
      //System.out.println("old url: "+oldUrl);
            read(oldUrl);
   }

    void read(String url) throws IOException {
	UnitTab.read(url);
    	status.setText("read: `" + url + "'");
  }
    void readMore(String url) throws IOException {
	UnitTab.readMore(url);
    	status.setText("read: `" + url + "'");
  }
    void save(String url) throws IOException {
	//do something here
	UnitTab myTab = new UnitTab();
	myTab.save(url);
	status.setText("saved: `" + url + "'");
    }

  void change_table()  throws IOException  {
      int new_table = dbchoice.getSelectedIndex();

      // System.out.println("change table " + current_table + " -> " + new_table);

      if(new_table != current_table) {
	  current_table = new_table;
	  read();
      }
  }

  public Units() {
      super("Units");
      contentPane = getContentPane();
      JPanel myPane = new JPanel();
      myPane.setLayout(new GridLayout(0,2));
      setSize(420,180);
      final JFileChooser chooser = new JFileChooser();

      myMenu = new JMenuBar();
      JMenu file = new JMenu("File");
      JMenuItem load = new JMenuItem("Load Custom Units File");
      load.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
		  returnVal = chooser.showOpenDialog(contentPane);
		  if(returnVal == JFileChooser.APPROVE_OPTION) {
		      filename = new String(chooser.getSelectedFile().getName());
		      int choice = JOptionPane.showConfirmDialog(contentPane, "Do you want to replace the current units table with this set of units?", "Clobber Units Table?", JOptionPane.YES_NO_OPTION);
		      if(choice==JOptionPane.YES_OPTION){
			  try{
			      read(filename);
			  } catch(IOException x) {
			      status.setText("error :" + x.getMessage());
			  }
		      }else if(choice==JOptionPane.NO_OPTION){
			  try{
			      readMore(filename);
			  } catch(IOException x) {
			      status.setText("error :" + x.getMessage());
			  }
		      }else{ status.setText("You picked an invalid option!");}
		  }}});

      JMenuItem save = new JMenuItem("Save Custom Units File");
      save.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
		  if(filename !=null){
		      try{
			  save(filename);
		      } catch(IOException x) {
			  status.setText("error :" + x.getMessage());
		      }
		  }else{
		  returnVal = chooser.showSaveDialog(contentPane);
		  if(returnVal == JFileChooser.APPROVE_OPTION){
		      filename = chooser.getSelectedFile().getName();
		      try{
			  save(filename);
		      }catch(IOException x) {
			  status.setText("error :" + x.getMessage());
		      }
		  }}}});
      JMenuItem define = new JMenuItem("Define new unit");
      define.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
              TestCustomUnit customUnitEditor = new TestCustomUnit();
              customUnitEditor.TestCustomUnitBuild();

          }});
      JMenu help = new JMenu("help");
      JMenuItem about  = new JMenuItem("About units");
      JMenuItem primer = new JMenuItem("Units Primer");
      primer.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
		  showPrimer();
	      }});
      JMenuItem search = new JMenuItem("Search for a unit");
      help.add(about);
      help.add(primer);
      help.add(search);
      file.add(load);
      file.add(save);
      file.addSeparator();
      file.add(define);
      //    file.add(quit);
      myMenu.add(file);
      myMenu.add(help);
      //myMenu.setHelpMenu(help);
      setJMenuBar(myMenu);
      numForm.setMaximumFractionDigits(12);
      
      from_value = new Input(myPane, "From Value:", 8, true);
      from_unit  = new Input(myPane, "Unit:", 8, true);
      to_unit    = new Input(myPane, "To: Unit", 8, true);
      to_value   = new Input(myPane, "Value:", 8, false);

      JButton okbutton = new JButton("OK");
      okbutton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){
		  doit();
	      }});
      myPane.add(okbutton);

      status = new JTextField();
      status.setEditable(false);
      status.setFont(new Font("helvetica", Font.PLAIN, 12));
      //   myPane.add(status);
      status.setText("Copyright (c) 1998 Regenstrief Institute. NO WARRANTY!");
      
      dbchoice = new Choice();
      for(int i = 0; i < table_names.length; i++)
	  dbchoice.addItem(table_names[i]);
      myPane.add(dbchoice);
      dbchoice.select(current_table);
      contentPane.add(myPane, BorderLayout.CENTER);
      contentPane.add(status, BorderLayout.SOUTH);
      //      dbchoice.select(current_table);

      try {
	  read();
      }
      catch(IOException x) {
	  status.setText("error :" + x.getMessage());
    }
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  } 

    void showPrimer(){
	//System.out.println("trying to show primer");
	FileInputStream infofile;
	StringBuffer myBuffer = new StringBuffer();
	int c;
	//System.out.println("about to define dialog");
	final JDialog myDialog = new JDialog();
	try{
	    infofile = new FileInputStream(oldUrl);
	    //System.out.println("about to read: "+oldUrl);
	    c = infofile.read();
	    while(c>0){
      		myBuffer.append((char)c);
		c = infofile.read();
	    }
	    Enumeration e = UnitTab.UnitVector.elements();
	    while (e.hasMoreElements()){
		myBuffer.append(e.nextElement().toString()+"\n");
	    }
	    Container myContent = myDialog.getContentPane();
	    myContent.setLayout(new BorderLayout());
	    myDialog.setSize(500,500);
	    myDialog.setLocationRelativeTo(contentPane);
	    myDialog.setTitle("UNITS primer");
	    JTextArea textPane = new JTextArea(myBuffer.toString());
	    textPane.setEditable(false);
	    JScrollPane paneScrollPane = new JScrollPane(textPane);
	    paneScrollPane.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    paneScrollPane.setPreferredSize(new Dimension(250, 155));
	    paneScrollPane.setMinimumSize(new Dimension(10, 10));
	    myContent.add(paneScrollPane, BorderLayout.CENTER);
	    //System.out.println("added scrollpane");
	    	    JButton ok = new JButton("OK");
	    ok.addActionListener(new ActionListener(){
	    	    public void actionPerformed(ActionEvent e){
	    		myDialog.hide();
		    }});
	     myContent.add(ok, BorderLayout.SOUTH);
	    myDialog.show();
	}catch(IOException x){
	    status.setText("error :" + x.getMessage());
	}
       
    }

  void doit() {
      try {
	  // change table?
	  change_table();
	  
	  // read a value
	  double mu1 = Double.valueOf(from_value.getText()).doubleValue();
      
	  // read from unit
	  Unit u1 = new Unit(from_unit.getText());
      
	  // u1.dump();
      
	  // read to unit
	  Unit u2 = new Unit(to_unit.getText());
      
	  // u2.dump();
      
	  // convert
	  double mu2 = u1.convertTo(mu1, u2);
      
	  status.setText("" + numForm.format(mu1) + " " + u1 + " = " + numForm.format(mu2) + " " + u2);
	  to_value.setText("" + numForm.format(mu2));
      }
      catch(Exception x) {
	  if(! (x instanceof java.lang.NoSuchMethodException))
	      status.setText("" + x.getClass().getName() + ": " + x.getMessage());
      }
  }

}
// A TextField in a Panel with a Label
class Input {
    JLabel     label = null;
    JTextField text  = null;
  
    public Input(Container w, String s, int width, boolean editable) {
	label = new JLabel(s,Label.RIGHT);
	Font font = new Font("helvetica", Font.PLAIN, 10);
	label.setFont(font);
    
	text = new JTextField("",width);
	text.setEditable(editable);
	w.add(label);
	w.add(text);
    }

    public String getText() { return text.getText(); }
    public void   setText(String s) {     
	text.setText(s); 
	text.setCaretPosition(0);
    }
}
