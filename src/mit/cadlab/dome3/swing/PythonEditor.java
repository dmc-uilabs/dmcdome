//PythonEditor.java
//Last updated: May 27, 2002
// added "instant" highlighting and unhighlighting of removed parameters
package mit.cadlab.dome3.swing;

import org.skunk.swing.text.syntax.FileMode;
import org.skunk.swing.text.syntax.SyntaxDocument;
import org.skunk.swing.text.syntax.SyntaxEditorKit;
import org.skunk.swing.text.syntax.SyntaxStyle;
import org.skunk.util.GappedIntArray;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import mit.cadlab.dome3.gui.objectmodel.modelobject.relation.procedural.build.ProceduralRelationBuildPanel;
import mit.cadlab.dome3.gui.objectmodel.causality.CausalityInfoEditorDialog;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;

//for basicComboPopup

public class PythonEditor extends DTextPane
{

	public final static String BODY = "Body";
	protected JPopupMenu popup;
	protected StringListModel totalKeywordList;
	protected JPanel parentPanel;
	protected String oldText = "";
	protected boolean tabKeyPressed;

	Color DEFAULT_CARET_COLOR = new Color(255, 51, 51);
	int DEFAULT_TAB_SIZE = 4;

	public PythonEditor(JPanel parentPanel, String[] AddedKeyword)
	{
		super();
		this.parentPanel = parentPanel;
		if (AddedKeyword != null)
			this.totalKeywordList = new StringListModel(AddedKeyword);
		else
			this.totalKeywordList = new StringListModel();
		initialize();
	}

	public PythonEditor(JPanel parentPanel)
	{
		this(parentPanel, null);
	}

	private void initialize()
	{
		SyntaxEditorKit kit = new SyntaxEditorKit();
		this.setEditorKit(kit);
		this.setFont(SyntaxStyle.getDefaultFont());
		this.setBackground(Color.white);
		this.setForeground(Color.black);
		this.setCaretColor(Color.black);
		SyntaxDocument doc = (SyntaxDocument) this.getDocument();
		doc.putProperty("tabSize", new Integer(DEFAULT_TAB_SIZE));
		doc.setFileMode(FileMode.getMode("python"));
		this.addKeyListener(new InputHandler(this));
		this.addMouseMotionListener(new InputHandler(this));
	}

	public void setParentPanel(JPanel parentPanel)
	{
		this.parentPanel = parentPanel;
	}

	public void setText(String text)
	{
		super.setText(text);
		highlighting();
	}

	public boolean isTabKeyPressed()
	{
		return tabKeyPressed;
	}

	public JPanel getParentBuildPanel()
	{
		return parentPanel;
	}

	private String grabHint() throws Exception
	{
		//start from current caretposition
		int currentCaretPosition = getCaret().getDot();
		int startPosition = currentCaretPosition;//initialize start to be the same as end
		//check back for 'bad character', highlighting the good ones
		boolean begining = false;
		for (int offset = currentCaretPosition; offset > 0; offset--) {
			String previous = getDocument().getText(offset - 1, 1);
			char previousChar = previous.charAt(0);
			// System.out.println(previousChar);
			if (!Character.isUnicodeIdentifierPart(previousChar)) {
				startPosition = offset;
				break;
			}
			if (offset == 1) begining = true; //means the caret reach the begining of the text
		}
		//make the hint string
		//System.out.println(startPosition);
		//System.out.println(currentCaretPosition);
		if (startPosition == currentCaretPosition && begining) startPosition = 0;
		String hint = getDocument().getText(0, getDocument().getLength()).substring(startPosition, currentCaretPosition);
		//System.out.println(hint);
		//high light the hint
		getCaret().setDot(startPosition);
		getCaret().moveDot(currentCaretPosition);
		return hint;
	}

	private StringListModel getMatchedKeyword(String hint)
	{
		if (hint.equals("")) return totalKeywordList;
		//search among the keyword list
		StringListModel result = new StringListModel();
		int count = 0;
		for (int i = 0; i < totalKeywordList.size(); i++)
			if (totalKeywordList.get(i).toLowerCase().startsWith(hint.toLowerCase())) {
				result.addElement(totalKeywordList.get(i));
				count++;
			}
		if (count == 0)
			return null;
		else
			return result;
	}

	public void autoComplete()
	{
		// change location to floating with cursor!
		// must fetch now, otherwise, caret position may change!
		Point p = getCaret().getMagicCaretPosition();
		String hint = "";
		try {
			hint = grabHint();
			// System.out.println(hint);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		StringListModel Keywordlist = getMatchedKeyword(hint);
		if (Keywordlist == null) {
			int currentCaretPosition = getCaret().getDot();
			//set back caret
			getCaret().setDot(currentCaretPosition);
			//System.out.println("no such keyword could be complete");
			Toolkit.getDefaultToolkit().beep();
			return;
		} else {
			//if there is only one keyword found, replace it directly
			if (Keywordlist.size() == 1) {
				//System.out.println("keyword list contains only one keyword "+Keywordlist.get(0));
				if (Keywordlist.get(0).equalsIgnoreCase(hint)) {
					Toolkit.getDefaultToolkit().beep();//this happens when a keyword is fully typed in and no need to replace,
					return;
				}
				append(Keywordlist.get(0));
				return;
			} else {
				//get matched keyword and show there
				//new version starts ---Qing 02/10/02
				popup = new JPopupMenu();
				for (int j = 0; j < Keywordlist.size(); j++)
					popup.add(new PopupAction(Keywordlist.get(j)));

				if (p == null) p = new Point(getCaretPosition(), 0);//this is the avoid the starting point exception
				popup.show(this, (int) p.getX(), (int) p.getY() + 20); // y postion add 20 pixel offset to make view better
				popup.requestFocus();
			}
		}
	}

	public void append(String text)
	{
		replaceSelection(text);
		requestFocus();
	}

	//add function to add/remove keyword list------------
	public void addKeyword(String keyword)
	{
		//if has this word already, don't add
		if (totalKeywordList.contains(keyword)) return;
		totalKeywordList.addElement(keyword);
		highlighting();
	}

	public void removeKeyword(String keyword)
	{
		totalKeywordList.removeElement(keyword);
		unhighlight(keyword);
	}

	public void replaceKeyword(String oldKeyword, String newKeyword)
	{
		if (!totalKeywordList.contains(oldKeyword))
			return;
		else {
			totalKeywordList.removeElement(oldKeyword);
			totalKeywordList.addElement(newKeyword);
			//updated Mar 26, 2002--Qing
			replaceKeywordInContext(oldKeyword, newKeyword);
			highlighting();
		}
	}

	//updated Mar 26, 2002--Qing
	protected void replaceKeywordInContext(String keyword, String newKeyword)
	{
		//find occurance,--offset start-->end
		try {
			int contextLen;
			String contextText; //total context text string
			int fromIndex = 0; //initialize
			int offset = 0;    //initialize
			do {
				contextLen = getDocument().getLength();   //have to reget it, for the length has changed!!
				contextText = getDocument().getText(0, contextLen);
				offset = contextText.indexOf(keyword, fromIndex); //find a occurance
				if (offset == -1) return; //no occurance,
				if (checkBefore(offset) && checkAfter(offset + keyword.length())) {//check if it is a keyword word, not included in some string
					//mark
					//the following is doing repaint
					int currentDot = getCaret().getDot();
					int currentMark = getCaret().getMark();
					getCaret().setDot(offset);
					getCaret().moveDot(offset + keyword.length());
					replaceSelection(newKeyword);
					//return caret back to normal
					if (currentDot == currentMark)
						getCaret().setDot(currentDot);
					else {
						getCaret().setDot(currentMark);
						getCaret().moveDot(currentDot);
					}
				}
				fromIndex = offset + keyword.length();
			} while (fromIndex < contextLen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//end of update Mar 26 2002
	}

	protected boolean isPopupShowing()
	{
		if (popup == null) return false;
		return popup.isVisible();
	}

	class InputHandler extends KeyAdapter implements MouseMotionListener
	{
		private int prevModifier = 99; //some random no.
		private PythonEditor editor;

		public InputHandler(PythonEditor editor)
		{
			this.editor = editor;
		}

		public void keyPressed(KeyEvent event)
		{
			if (event.getKeyCode() == 9) { //tab key
				tabKeyPressed = true;
			} else {
				tabKeyPressed = false;
			}
			if (!editor.isPopupShowing()) {
				prevModifier = event.getModifiers();
			} else {
				prevModifier = 3;  //so that we could get the causality dialog up
			}

			if (event.getModifiers() == 3 // Ctrl+Shift;
			        && !Character.isISOControl(event.getKeyChar()))   // printable character
			{
				if (event.getKeyCode() == 16  // shift
				        || event.getKeyCode() == 17) // control
				{
					autoComplete();
					this.editor.requestFocus();
				}
			}
			if (prevModifier == 3 && event.getModifiers() == 0) {
				if (event.getKeyCode() == 10)  // Enter
				{
//	              System.out.println("Ctrl+Shift Enter key pressed");
					JPanel pane = editor.getParentBuildPanel();
					if (pane instanceof ProceduralRelationBuildPanel) {
						ProceduralRelationBuildPanel p = (ProceduralRelationBuildPanel) pane;
						ProceduralRelation r = (ProceduralRelation) p.getRelation();
						CausalityInfoEditorDialog.showEditor(p,
						                                     new ArrayList(r.getModelObjects()), r.getDependencyInfo());
					}
					if (!event.isConsumed()) {
						event.consume();  // so that cursor doesn't go to new line
					}
				}
			}

			//oldText = getText();
			if (!getText().equals(oldText)) {
				firePropertyChange(BODY, oldText, getText());
				oldText = getText();
			}
			highlighting();
		}

		public void keyReleased(KeyEvent e)
		{
			if (!getText().equals(oldText)) {
				firePropertyChange(BODY, oldText, getText());
				oldText = getText();
			}
			highlighting();
		}

		public void mouseMoved(MouseEvent e)
		{
			highlighting();
		}

		public void mouseDragged(MouseEvent e)
		{
		}
	}

	protected void highlighting()
	{
		int count = totalKeywordList.size();
		for (int i = 0; i < count; i++) {
			highlighting(totalKeywordList.get(i));
		}
	}

	protected void highlighting(String keyword)
	{
		//find occurance,--offset start-->end
		try {
			int contextLen = getDocument().getLength();
			String contextText = getDocument().getText(0, contextLen); //total context text string
			int fromIndex = 0; //initialize
			int offset = 0;    //initialize
			do {
				offset = contextText.indexOf(keyword, fromIndex); //find a occurance
				if (offset == -1) return; //no occurance,
				if (checkBefore(offset) && checkAfter(offset + keyword.length())) {//check if it is a keyword word, not included in some string
					applyStyle(offset, keyword.length());
				}
				fromIndex = offset + keyword.length();
			} while (fromIndex < contextLen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//get style of this offset
		//if it is style 1 or style 23, change color
	}

	protected void unhighlight(String keyword)
	{
		//find occurance,--offset start-->end
		try {
			int contextLen = getDocument().getLength();
			String contextText = getDocument().getText(0, contextLen); //total context text string
			int fromIndex = 0; //initialize
			int offset = 0;    //initialize
			SyntaxDocument doc = (SyntaxDocument) getDocument();
			GappedIntArray styleBuffer = doc.getStyleBuffer();
			do {
				offset = contextText.indexOf(keyword, fromIndex); //find a occurance
				if (offset == -1) return; //no occurance,
				if (checkBefore(offset) && checkAfter(offset + keyword.length())) {//check if it is a keyword word, not included in some string
					if (styleBuffer.get(offset) == 26) {  //apply to my style
						for (int i = 0; i < keyword.length(); i++) {
							styleBuffer.set(offset + i, 2);
						}
						//the following is doing repaint
						int currentDot = getCaret().getDot();
						int currentMark = getCaret().getMark();
						getCaret().setDot(offset);
						getCaret().moveDot(offset + keyword.length());
						//return caret back to normal
						if (currentDot == currentMark)
							getCaret().setDot(currentDot);
						else {
							getCaret().setDot(currentMark);
							getCaret().moveDot(currentDot);
						}
					}
				}
				fromIndex = offset + keyword.length();
			} while (fromIndex < contextLen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// repaint();
	}

	protected boolean checkBefore(int offset) throws Exception
	{
		if (offset == 0) return true; //already at the beginning
		String str = getDocument().getText(offset - 1, 1);
		char c = str.charAt(0);
		return !Character.isUnicodeIdentifierPart(c);
	}

	protected boolean checkAfter(int offset) throws Exception
	{
		if (offset == getDocument().getLength()) return true; //already at the end
		String str = getDocument().getText(offset, 1);
		char c = str.charAt(0);
		return !Character.isUnicodeIdentifierPart(c);
	}

	protected void applyStyle(int offset, int length)
	{
		SyntaxDocument doc = (SyntaxDocument) getDocument();
		GappedIntArray styleBuffer = doc.getStyleBuffer();
		if (styleBuffer.get(offset) == 23 || styleBuffer.get(offset) == 1) {  //apply to my style
			for (int i = 0; i < length; i++) {
				styleBuffer.set(offset + i, 26);
			}
		}
		//the following is doing repaint
		int currentDot = getCaret().getDot();
		int currentMark = getCaret().getMark();
		getCaret().setDot(offset);
		getCaret().moveDot(offset + length);
		//return caret back to normal
		if (currentDot == currentMark)
			getCaret().setDot(currentDot);
		else {
			getCaret().setDot(currentMark);
			getCaret().moveDot(currentDot);
		}
	}

	public static class AutoCompleteAction extends TextAction
	{
		public AutoCompleteAction()
		{
			super("autoCompleteAction");
		}

		public void actionPerformed(ActionEvent e)
		{
			// System.out.println("autocompleted");
			JTextComponent target = getTextComponent(e);
			if ((target != null) && (target instanceof PythonEditor)) {
				((PythonEditor) target).autoComplete();
			} else {
				//  System.out.println("some error occur in AutoCompleteAction");
			}
		}
	}

	class PopupAction extends AbstractAction
	{
		public PopupAction(String word)
		{
			super(word);
		}

		public void actionPerformed(ActionEvent e)
		{
			append((String) getValue(Action.NAME));
		}
	}

}
