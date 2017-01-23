//JPythonEditorPane.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*; // JPanel, JScrollPane,JTextPane
import javax.swing.text.*; // JTextComponent, TextAction
import java.io.*; // Writer

import org.skunk.swing.text.syntax.*; //for Python Highlighting
import org.skunk.swing.text.*; //for Python souce Editing
import org.skunk.util.GappedIntArray;
import java.util.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*; //for basicComboPopup

import mit.cadlab.dome.gui.components.shared.StringListModel;


public class JPythonEditor extends org.skunk.swing.text.TextEditorPane{
   
  
  protected BasicComboPopup popup;
  protected String[] AddedKeyWord={"DomeCatalog","DomeBoolean","GAO"};
  protected StringListModel totalKeywordList=new StringListModel(AddedKeyWord);
 
  protected JComboBox AddedKeyWordCombo;
 
  Color DEFAULT_CARET_COLOR=new Color(255, 51, 51);
  int DEFAULT_TAB_SIZE=4;
  
  public JPythonEditor(){
    super();
    initialize();    
  }
 
  private void initialize(){
      
     
         
     SyntaxEditorKit kit=new SyntaxEditorKit();	
     this.setEditorKit(kit);
     this.setFont(SyntaxStyle.getDefaultFont());
     this.setBackground(Color.white);
     this.setForeground(Color.black);
     this.setCaretColor(Color.black);
     SyntaxDocument doc = (SyntaxDocument) this.getDocument();
     doc.putProperty("tabSize", new Integer(DEFAULT_TAB_SIZE));
     doc.setFileMode(FileMode.getMode("python"));
     
     //add ctrl+shift key
     //add now ctrl+F1 for now ------------
     KeyStroke ctrl_F1=KeyStroke.getKeyStroke(KeyEvent.VK_F1,InputEvent.CTRL_MASK);
     Keymap k=this.getKeymap();
     JTextComponent.KeyBinding[] binding={new JTextComponent.KeyBinding(ctrl_F1,"autoCompleteAction")};
     this.loadKeymap(k,binding,this.getActions());
     k.addActionForKeyStroke(ctrl_F1,new AutoCompleteAction());  
        
     this.addKeyListener(new InputHandler());
                
     
    
     
 
   
     
  }
  
    private String grabHint() throws Exception{
      
      //start from current caretposition
      int currentCaretPosition=getCaret().getDot();
      int startPosition=currentCaretPosition;//initialize start to be the same as end

      //check back for 'bad character', highlighting the good ones
      boolean begining=false;
      for(int offset=currentCaretPosition;offset>0;offset--){
	  String previous=getDocument().getText(offset-1,1);
          char previousChar=previous.charAt(0);
	  // System.out.println(previousChar);   
	  if(!Character.isUnicodeIdentifierPart(previousChar)){
	    startPosition=offset;
	    break;
	  }
          if(offset==1) begining=true; //means the caret reach the begining of the text
          
        }
      //make the hint string
      //System.out.println(startPosition);
      //System.out.println(currentCaretPosition);
      if(startPosition==currentCaretPosition&&begining) startPosition=0;
      

      String hint=getDocument().getText(0,getDocument().getLength()).substring(startPosition,currentCaretPosition);
      //System.out.println(hint);
      
      //high light the hint
      getCaret().setDot(startPosition);
      getCaret().moveDot(currentCaretPosition);

      

      
   
      return hint;

     
      
      
      
    }

    private StringListModel getMatchedKeyword(String hint)
    {/**
      if(hint.equals("")) return totalKeyWordList;
        
       //search among the keyword list   
      String[] result=new String[20];
      int count=0;
      for(int i=0;i<AddedKeyWord.length;i++)
        if(AddedKeyWord[i].toLowerCase().startsWith(hint.toLowerCase()))
	  {result[count]=AddedKeyWord[i];
	   count++;}
      if(count==0) return null;
      else return result; 
     */
      if(hint.equals("")) return totalKeywordList;
        
       //search among the keyword list   
      StringListModel result=new StringListModel();
      int count=0;
      for(int i=0;i<AddedKeyWord.length;i++)
        if(totalKeywordList.get(i).toLowerCase().startsWith(hint.toLowerCase()))
	  {result.addElement(totalKeywordList.get(i));
	   count++;}
      if(count==0) return null;
      else return result; 
        
   }



    public  void autoComplete(){
      // change location to floating with cursor!
      // must fetch now, otherwise, caret position may change!
      Point p=getCaret().getMagicCaretPosition();
      
      
      String hint="";
      try{
         hint = grabHint(); 

        }catch(Exception ee)
	  {ee.printStackTrace();
	}
	StringListModel Keywordlist = getMatchedKeyword(hint);

        if(Keywordlist==null) {
	   int currentCaretPosition=getCaret().getDot();
	   //set back caret
           getCaret().setDot(currentCaretPosition);      
	   //System.out.println("no such keyword could be complete");
           Toolkit.getDefaultToolkit().beep();
          return;
          }
        else{
	  //if there is only one keyword found, replace it directly
          if(Keywordlist.size()==1){
            System.out.println("keyword list contains only one keyword "+Keywordlist.get(0));
            if(Keywordlist.get(0).equalsIgnoreCase(hint)){
                Toolkit.getDefaultToolkit().beep();//this happens when a keyword is fully typed in and no need to replace,
	        return;} 
            append(Keywordlist.get(0));
          return; 
           }
          else{
          //get matched keyword and show there
	         
	    AddedKeyWordCombo=new JComboBox(Keywordlist.toArray());
            
	    popup=new BasicComboPopup(AddedKeyWordCombo);
            
	  
           
	    if(p==null) p=new Point(getCaretPosition(),0);//this is the avoid the starting point exception 
	
       
	    popup.show(this,(int)p.getX(),(int)p.getY()+20); // y postion add 20 pixel offset to make view better
        
            System.out.println(AddedKeyWordCombo.isFocusTraversable());     
	    System.out.println(popup.isFocusTraversable());
        
	    //add in keyword text selected;--------------
	    AddedKeyWordCombo.addActionListener(new SelectionHandler());

       
	  }
       }
    }
  
    public void append(String text){
      replaceSelection(text);
    
    }
      

    //add function to add/remove keyword list------------
    public void addKeyword(String keyword){
      this.totalKeywordList.addElement(keyword);
    }    
    
    public void removeKeyword(String keyword){
      this.totalKeywordList.removeElement(keyword);
    }
    
    protected boolean isPopupShowing(){
      if(popup==null) return false;
      return popup.isVisible();
     }

    protected void movePopupSelectionDown(){
      int currentIndex=AddedKeyWordCombo.getSelectedIndex();
      if(currentIndex==(AddedKeyWordCombo.getItemCount()-1)) {//already at the end, beep and return
	Toolkit.getDefaultToolkit().beep();
	return;
          }
      else popup.getList().setSelectedIndex(currentIndex+1);
    }

    protected void remark(int start, int end)
    {     
      getCaret().setDot(start);
      getCaret().moveDot(end);

     }
    protected void movePopupSelectionUp(){
      int currentIndex=AddedKeyWordCombo.getSelectedIndex();
      if(currentIndex==0) {//already at the begining, beep and return
          Toolkit.getDefaultToolkit().beep();
          return;
          }
      else popup.getList().setSelectedIndex(currentIndex-1);
    }
  
  public class SelectionHandler implements ActionListener
    { 
       public SelectionHandler(){
            super();
        }
	public void actionPerformed(ActionEvent e){
          append(AddedKeyWordCombo.getSelectedItem().toString());
	    }
     }
  
  /**
   *  this function is not working properly yet
   */

  public class InputHandler extends KeyAdapter{
    public void keyReleased(KeyEvent e){ 
    //first handle key slection to popup
      /** not working yet ---Qing 02/05/02
       if(isPopupShowing()){
         //now means key selection begin
	 //save orginall mark position
         
	 int originalMark=((JPythonEditor)e.getComponent()).getCaret().getMark();
	
        //move and select using keyboard
	if(e.getKeyCode()==KeyEvent.VK_DOWN)
	 {System.out.println("get a arrow down");
	  remark(originalMark,((JPythonEditor)e.getComponent()).getCaret().getDot());   
	  movePopupSelectionDown();
          }
	else if(e.getKeyCode()==KeyEvent.VK_UP)
          {remark(originalMark,((JPythonEditor)e.getComponent()).getCaret().getDot());  
	  movePopupSelectionUp();}
        else if(e.getKeyCode()==KeyEvent.VK_ENTER){
          System.out.println("get a enter");
          remark(originalMark,((JPythonEditor)e.getComponent()).getCaret().getDot());  
          append(AddedKeyWordCombo.getSelectedItem().toString());
          popup.setVisible(false);
          popup=null;}
	 }  
      */    
     
     //second, highlighting Added Keyword
      //foreach string keyword in totalkeywordList
  
      
      highlighting();	
      
     }
    }  
    
    protected void highlighting(){
      int count=totalKeywordList.size();
      for(int i=0;i<count;i++){
       highlighting(totalKeywordList.get(i));}
    }
   
    protected void highlighting(String keyword){
     //find occurance,--offset start-->end
      try{
      int contextLen=getDocument().getLength(); 
      String contextText=getDocument().getText(0,contextLen); //total context text string
      
      int fromIndex=0; //initialize
      int offset=0;    //initialize 
     
      do{
	offset=contextText.indexOf(keyword,fromIndex); //find a occurance
        if(offset==-1) return; //no occurance, 

        if(checkBefore(offset)&&checkAfter(offset+keyword.length())){//check if it is a keyword word, not included in some string
          
	  applyStyle(offset,keyword.length());
           }
        fromIndex=offset+keyword.length();
      }while(fromIndex<contextLen);
      }catch(Exception e){
	e.printStackTrace();
      }
      //get style of this offset
      //if it is style 1 or style 23, change color
    
    } 
  
    protected boolean checkBefore(int offset) throws Exception{
     
      if(offset==0) return true; //already at the beginning
      String str=getDocument().getText(offset-1,1);
      char c=str.charAt(0);  
    
      return !Character.isUnicodeIdentifierPart(c); 
        
    }


    protected boolean checkAfter(int offset) throws Exception{
      if(offset==getDocument().getLength()) return true; //already at the end
      String str=getDocument().getText(offset,1);
      char c=str.charAt(0);  
      
      return !Character.isUnicodeIdentifierPart(c); 
    } 
   
   protected void applyStyle(int offset,int length){
   
     SyntaxDocument doc=(SyntaxDocument)getDocument();
     GappedIntArray styleBuffer=doc.getStyleBuffer();
     
     if(styleBuffer.get(offset)==23||styleBuffer.get(offset)==1)
       {  //apply to my style
	 for(int i=0;i<length;i++){
           styleBuffer.set(offset+i,26);
	 }
	
        }
    
     //the following is doing repaint
     int currentDot=getCaret().getDot();
     int currentMark=getCaret().getMark();
     getCaret().setDot(offset);
     getCaret().moveDot(offset+length);
     //return caret back to normal
     if(currentDot==currentMark)
     getCaret().setDot(currentDot);
     else
       {getCaret().setDot(currentMark);
        getCaret().moveDot(currentDot);
       }
   }
  
    public static class AutoCompleteAction extends TextAction{
	public AutoCompleteAction(){
            super("autoCompleteAction");
           }
     
	 
        public void actionPerformed(ActionEvent e){
            System.out.println("autocompleted");
	    JTextComponent target=getTextComponent(e);
	     if ((target !=null)&&(target instanceof JPythonEditor)){
               ((JPythonEditor)target).autoComplete();
             }
	      else{
		System.out.println("some error occur in AutoCompleteAction");
		} 
		}

  }

   

  public static void main(String[] args){
	JFrame f = new JFrame("Testing PythonEditor");
	f.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e){
		    System.exit(0);
		}
	    });
	
	JScrollPane scrollpane=new JScrollPane(new JPythonEditor());
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); 
	scrollpane.setPreferredSize(new Dimension(500,500));
     
	f.getContentPane().add(scrollpane,BorderLayout.CENTER);
      
	f.pack();
	f.setVisible(true);
    }
  }




