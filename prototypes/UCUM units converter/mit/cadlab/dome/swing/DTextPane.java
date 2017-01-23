// DTextPane.java
package mit.cadlab.dome.swing;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

/**
 * Customized JTextPane which changes background color upon user changes
 * and supports changing background color programmatically to reflect
 * status of data in textfield.
 */
public class DTextPane extends JTextPane implements GuiConstants {
  
  protected static TextEditorPopupListener popupListener = new TextEditorPopupListener();
  protected boolean isStale = false;

  public DTextPane() {
    super();
    _configure();
  }

  private void _configure() {
    setEditorKit(new RTFEditorKit());
    addFocusListener(new FocusListener() {
	public void focusGained(FocusEvent e) {
	}
	
	public void focusLost(FocusEvent e) {
	  if (isEditable() && isStale)
	    fireActionPerformed();
	}
      });
    getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent e) {
	  setStaleIfNecessary();
	}
	public void removeUpdate(DocumentEvent e) {
	  setStaleIfNecessary();
	}
	public void changedUpdate(DocumentEvent e) {
	  setStaleIfNecessary();
	}

	protected void setStaleIfNecessary() {
	  if (isEditable() && !isStale())
	    setStale();
	}
      });
  }
 
  public void setEditable(boolean isEditable) {
    super.setEditable(isEditable);
    removeMouseListener(popupListener); // ensure it is gone
    if (isEditable())
      addMouseListener(popupListener);
  }

  public boolean isStale() {
    return isStale;
  }
  
  public void setStale() {
    isStale = true;
  }

  public void setCurrent() {
    isStale = false;
  }

  // support for easily reading/writing to Strings
  // to do: how to tell when it is empty?
  public String getRtfText() {
    try {
      ByteArrayOutputStream writer = new ByteArrayOutputStream();
      StyledDocument doc = (StyledDocument)getDocument();
      int length=doc.getLength();
      SimpleAttributeSet attr=new SimpleAttributeSet();
      StyleConstants.setForeground(attr, Color.black);
      StyleConstants.setFontSize(attr,12);
      StyleConstants.setFontFamily(attr,"Default");
      StyleConstants.setAlignment(attr,StyleConstants.ALIGN_LEFT);
      doc.setParagraphAttributes(0,length,attr,false);
      getEditorKit().write(writer,doc,0,doc.getLength());
      writer.close();
      return writer.toString();
    } catch (Exception ex) {
      System.out.println(ex);
      return "";
    }
  }

  public void setRtfText(String rtfText) {
    if (rtfText == null || rtfText.equals(""))
      return;
    StringReader sr = new StringReader(rtfText);
    try {
      getEditorKit().read(sr,getDocument(),0);
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }

  // ActionListener support
  public synchronized void addActionListener(ActionListener l) {
    listenerList.add(ActionListener.class, l);
  }
  
  public synchronized void removeActionListener(ActionListener l) {
    listenerList.remove(ActionListener.class, l);
  }
  
  protected void fireActionPerformed() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				    "");
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==ActionListener.class) {
	((ActionListener)listeners[i+1]).actionPerformed(e);
      }          
    }
  }

}
