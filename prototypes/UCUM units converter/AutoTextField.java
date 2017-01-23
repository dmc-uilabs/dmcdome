/*
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Sep 30, 2002
 * Time: 3:18:07 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import mit.cadlab.dome.gui.components.shared.StringListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AutoTextField extends JTextField {

    protected StringListModel totalKeywordList;
    protected JPopupMenu popup;

    public AutoTextField() {
        this(null);
    }

    public AutoTextField(String[] InitialKeywords) {
        super();
        if (InitialKeywords!=null) {
            this.totalKeywordList = new StringListModel(InitialKeywords);
        }
        else {
            this.totalKeywordList=new StringListModel();
        }
        initialize();
    }

    private void initialize() {

        //SyntaxEditorKit kit=new SyntaxEditorKit();
        //this.setEditorKit(kit);
        this.setBackground(Color.white);
        this.setForeground(Color.black);
        this.setCaretColor(Color.black);

        this.addKeyListener(new InputHandler());
        this.addMouseMotionListener(new InputHandler());
    }

    public class InputHandler extends KeyAdapter implements MouseMotionListener{

        public void keyPressed(KeyEvent event) {

            if (event.getModifiers()==3 // Ctrl+Shift
            && !Character.isISOControl(event.getKeyChar()) // printable character
	        && (event.getKeyCode()==16  // shift
		    || event.getKeyCode()==17) // control
	        )
	            autoComplete();
        }
        public void keyReleased(KeyEvent e){
            //highlighting();
        }

        public void mouseMoved(MouseEvent e){
            //highlighting();
        }

        public void mouseDragged(MouseEvent e) {}


    }

    public void autoComplete(){
        // change location to floating with cursor!
        // must fetch now, otherwise, caret position may change!
        Point p=getCaret().getMagicCaretPosition();
        String hint="";
        try{
            hint = grabHint();
            //System.out.println("hint is " +String.valueOf(hint));
        } catch(Exception ee) {
            ee.printStackTrace();
        }

        StringListModel Keywordlist = getMatchedKeyword(hint);

        if(Keywordlist==null) {
	        int currentCaretPosition=getCaret().getDot();
	        //set back caret
            getCaret().setDot(currentCaretPosition);
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        else{
	        //if there is only one keyword found, replace it directly
            if(Keywordlist.size()==1){
                if(Keywordlist.get(0).equalsIgnoreCase(hint)){
                    Toolkit.getDefaultToolkit().beep();
                    //this happens when a keyword is fully typed in and no need to replace,
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
                if(p==null) p=new Point(getCaretPosition(),0);
                //this is the avoid the starting point exception
            	    popup.show(this,(int)p.getX(),(int)p.getY()+20);
                    // y postion add 20 pixel offset to make view better
                    popup.requestFocus();
	        }
        }
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

	        if(!Character.isUnicodeIdentifierPart(previousChar) && previousChar!="[".charAt(0)){
	            startPosition=offset;
	            break;
	        }
            if(offset==1) begining=true; //means the caret reach the begining of the text
        }
        //make the hint string
        if(startPosition==currentCaretPosition&&begining) startPosition=0;
        String hint=getDocument().getText(0,getDocument().getLength()).substring(startPosition,currentCaretPosition);

        //highlight the hint
        getCaret().setDot(startPosition);
        getCaret().moveDot(currentCaretPosition);

        return hint;
    }

    public void append(String text){
        replaceSelection(text);
        requestFocus();
    }

    private StringListModel getMatchedKeyword(String hint) {
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

    class PopupAction extends AbstractAction {
        public PopupAction(String word) {
            super(word);
        }
        public void actionPerformed(ActionEvent e) {
            append((String)getValue(Action.NAME));
        }
    }

   /* protected void highlighting(){
        int count=totalKeywordList.size();
        for(int i=0;i<count;i++){
        highlighting(totalKeywordList.get(i));}
    }

    protected void highlighting(String keyword){
        //find occurance,--offset start-->end
        try{int contextLen=getDocument().getLength();
            String contextText=getDocument().getText(0,contextLen);
            //total context text string
            	   System.out.println("it's in this");
            int fromIndex=0; //initialize
            int offset=0;    //initialize

            do{
	        offset=contextText.indexOf(keyword,fromIndex); //find a occurance
            if(offset==-1) return; //no occurance,

            if(checkBefore(offset)&&checkAfter(offset+keyword.length())){
            //check if it is a keyword word, not included in some string
	        applyStyle(offset,keyword.length());
            }
            fromIndex=offset+keyword.length();
            }while(fromIndex<contextLen);
        }catch(Exception e){
            	   System.out.println("this is not highlighted");
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
        else {
            getCaret().setDot(currentMark);
            getCaret().moveDot(currentDot);
        }
   }*/

}
