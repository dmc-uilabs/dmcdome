//JPythonEditorPane.java
//Last updated: Mar 26, 2002

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
   
  
 
  protected JPopupMenu popup;
  // protected String[] AddedKeyWord={}; //temporary or permenent?
  protected StringListModel totalKeywordList;
  
 
 
 
  Color DEFAULT_CARET_COLOR=new Color(255, 51, 51);
  int DEFAULT_TAB_SIZE=4;
  
  public JPythonEditor(String[] AddedKeyword){
    
    super();
    if(AddedKeyword!=null)  this.totalKeywordList =new StringListModel(AddedKeyword);
    else this.totalKeywordList=new StringListModel();
    initialize();    
  }
  
  public JPythonEditor(){
    this(null);
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
     
     this.addKeyListener(new InputHandler());
     this.addMouseMotionListener(new InputHandler());
                
     
    
     
 
   
     
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
    {
      if(hint.equals("")) return totalKeywordList;
        
       //search among the keyword list   
      StringListModel result=new StringListModel();
      int count=0;
      for(int i=0;i<totalKeywordList.size();i++)
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
	 // System.out.println(hint);

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
	   //new version starts ---Qing 02/10/02
             popup = new JPopupMenu();
             for(int j=0;j<Keywordlist.size();j++)
		 popup.add(new PopupAction(Keywordlist.get(j)));
     
            if(p==null) p=new Point(getCaretPosition(),0);//this is the avoid the starting point exception 
	
       
	    popup.show(this,(int)p.getX(),(int)p.getY()+20); // y postion add 20 pixel offset to make view better
         
            popup.requestFocus();
            
       
	  }
       }
    }
  
    public void append(String text){
      replaceSelection(text);
      requestFocus();
    
    }
      

    //add function to add/remove keyword list------------
    public void addKeyword(String keyword){
      //if has this word already, don't add
      if(totalKeywordList.contains(keyword)) return;
      totalKeywordList.addElement(keyword);
    }    
    
    public void removeKeyword(String keyword){
      totalKeywordList.removeElement(keyword);
    }
   
    public void replaceKeyword(String oldKeyword, String newKeyword){
      if(!totalKeywordList.contains(oldKeyword)) return;
      else{
	removeKeyword(oldKeyword);
        addKeyword(newKeyword);
	//updated Mar 26, 2002--Qing
	replaceKeywordInContext(oldKeyword,newKeyword);
        
         }
    }
   //updated Mar 26, 2002--Qing
    protected void replaceKeywordInContext(String keyword, String newKeyword){
     //find occurance,--offset start-->end
      try{
      int contextLen; 
      String contextText; //total context text string
      
      int fromIndex=0; //initialize
      int offset=0;    //initialize 
      
      do{
	contextLen=getDocument().getLength();   //have to reget it, for the length has changed!!
	contextText=getDocument().getText(0,contextLen);
    
	offset=contextText.indexOf(keyword,fromIndex); //find a occurance
	
        if(offset==-1) return; //no occurance, 

        if(checkBefore(offset)&&checkAfter(offset+keyword.length())){//check if it is a keyword word, not included in some string
          //mark
	  //the following is doing repaint
	  int currentDot=getCaret().getDot();
	  int currentMark=getCaret().getMark();
	  
	  getCaret().setDot(offset);
	  getCaret().moveDot(offset+keyword.length());
	 
          replaceSelection(newKeyword);
	 
	  //return caret back to normal
	  if(currentDot==currentMark)
	    getCaret().setDot(currentDot);
	  else
	    {
	      getCaret().setDot(currentMark);
	      getCaret().moveDot(currentDot);
	    }

           }
         fromIndex=offset+keyword.length();
        
        }while(fromIndex<contextLen);
      }catch(Exception e){
	e.printStackTrace();
      }
      //end of update Mar 26 2002
    
    } 
    
    protected boolean isPopupShowing(){
      if(popup==null) return false;
      return popup.isVisible();
     }
  
 
  /**
   * 
   */

  public class InputHandler extends KeyAdapter implements MouseMotionListener{
    public void keyPressed(KeyEvent event) {
	  if (event.getModifiers()==3 // Ctrl+Shift;
	      && !Character.isISOControl(event.getKeyChar())   // printable character
	      && (event.getKeyCode()==16  // shift
		  || event.getKeyCode()==17) // control
	      )
	      autoComplete();
    }
    public void keyReleased(KeyEvent e){ 
       highlighting();	
      }
 
    public void mouseMoved(MouseEvent e){
       highlighting();	
     }
    public void mouseDragged(MouseEvent e) 
     {}
    

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
   
    //newly added starts here , Qing 02/10/02
   
    public void savePythonFile(String filename)
    {
      try { 
	             // String filename="saved.py"; ---default name
		    OutputStream os=new FileOutputStream(filename);
		   
		    SyntaxDocument doc=(SyntaxDocument)getDocument();
		    int length=doc.getLength();
		    
		    getEditorKit().write(os,doc,0,length);
		    os.close();

		    
        	    } catch (Exception ex)
			{
			ex.printStackTrace();
		    }

    }
   
    public void loadPythonFile(String filename){
      try { 
	             // String filename="saved.py"; ---default name
		    InputStream in=new FileInputStream(filename);
		   
		    SyntaxDocument doc=(SyntaxDocument)getDocument();
		   
		    
		    getEditorKit().read(in,doc,0);
		    setDocument(doc);
		    in.close();
 
		    
        	    } catch (Exception ex)
			{
			ex.printStackTrace();
		    }
    }
   
   protected JPanel keywordInputPanel() {
 
         final JTextField keywordField = new JTextField(40);
	 JButton b = new JButton("Add keyword");
	 b.setFocusPainted(false);
	 b.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent event) {
		     String keyword = keywordField.getText();
		     keyword = keyword.trim(); // strip whitespaces
		     if (keyword.equals("")) 
                         { Toolkit.getDefaultToolkit().beep();
                            return; // nothing there
			  }
                     if(keyword.indexOf(" ")!=-1) 
			 {Toolkit.getDefaultToolkit().beep();     
                          return; //illegle keyword don't add
			 }
		     addKeyword(keyword); // add work to popup menu
		     keywordField.setText(""); // clear textfield
		 }
	     });
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
	p.add(new JLabel("keyword:"));
	p.add(Box.createHorizontalStrut(5));
	p.add(keywordField);
	p.add(Box.createHorizontalGlue());
	p.add(b);
        return p;
      }
  
    protected JPanel saveloadPanel(){ 
         JButton b1 = new JButton("Save");
	 b1.setFocusPainted(false);
	 b1.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent event) {
		 savePythonFile("saved.py");}
	     });
         JButton b2 = new JButton("Load");
	 b2.setFocusPainted(false);
	 b2.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent event) {
                  loadPythonFile("saved.py");  }
	     });
	 JButton b3 = new JButton("Clear");
	 b3.setFocusPainted(false);
	 b3.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent event) {
                  setText("");  }
	     });
	 JButton b4 = new JButton("Test replace keyword");
	 b4.setFocusPainted(false);
	 b4.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent event) {
                  replaceKeyword("domeCatalog","NewdomeCatalog");  }
	     });
         JPanel p = new JPanel();
	 p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
         p.add(b2);
         p.add(b1);
	 p.add(b3);
	 p.add(b4);
         return p;

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

  class PopupAction extends AbstractAction {

    public PopupAction(String word) {
      super(word);
    }

    public void actionPerformed(ActionEvent e) {
      append((String)getValue(Action.NAME));
     }

  }   

  public static void main(String[] args){
	JFrame f = new JFrame("Testing PythonEditor");
	f.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e){
		    System.exit(0);
		}
	    });
        String[] testKeyword= {"domeBoolean","domeCatalog","GAO"};
	JPythonEditor editor=new JPythonEditor(testKeyword); 
	JScrollPane scrollpane=new JScrollPane(editor);
        scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); 
	scrollpane.setPreferredSize(new Dimension(500,500));
	f.getContentPane().add(editor.keywordInputPanel(),BorderLayout.NORTH);        
	f.getContentPane().add(scrollpane,BorderLayout.CENTER);
	f.getContentPane().add(editor.saveloadPanel(),BorderLayout.SOUTH);        
      
	f.pack();
	f.setVisible(true);
    }
  }




