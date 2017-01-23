// MessageArea.java
// Copyright (c) 2002 Massachsetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * An uneditable area for messages.
 * Can set an initial set of message styles.
 */
public class MessageArea extends JTextPane
{

	protected static GridBagConstraints gbc;
	protected static final String NEWLINE = "\n";
	protected List messages;
	protected boolean isCaretFrozen = false;

	public MessageArea()
	{
		super.setEditable(false);
		messages = new ArrayList();
	}

	public MessageArea(List messageFormats)
	{
		this();
		for (int i = 0; i < messageFormats.size(); i++) {
			Object obj = messageFormats.get(i);
			if (obj instanceof MessageFormat) {
				addMessageFormat((MessageFormat) obj);
			} else {
				System.err.println("MessageArea constructor - unknown message format: " + obj);
			}
		}
	}

	public void addMessageFormat(MessageFormat format)
	{
		addMessageFormat(format.name, format.color,
		                 format.fontFamily, format.style, format.size);
	}

	public void addMessageFormat(String name, Color color, Font f)
	{
		addMessageFormat(name, color, f.getName(), f.getStyle(), f.getSize());
	}

	public void addMessageFormat(String formatName, Color color, String fontFamily, int style, int size)
	{
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style s = addStyle(formatName, def);
		StyleConstants.setForeground(s, color);
		StyleConstants.setFontFamily(s, fontFamily);
		switch (style) {
			case Font.BOLD:
				StyleConstants.setBold(s, true);
				break;
			case Font.ITALIC:
				StyleConstants.setItalic(s, true);
				break;
			case Font.BOLD + Font.ITALIC:
				StyleConstants.setBold(s, true);
				StyleConstants.setItalic(s, true);
		}
		StyleConstants.setFontSize(s, size);
	}

	public void addMessage(String messageType, String messageText)
	{
        /* delete first row */
		if (messages.size() > 100) {
            messages.remove(0);
        }

        Document doc = getDocument();
		messageText += NEWLINE;
		try {
			int dot = getCaret().getDot();
			doc.insertString(doc.getEndPosition().getOffset() - 1, messageText,
			                 getStyle(messageType));
			if (isCaretFrozen)
				getCaret().setDot(dot);
		} catch (BadLocationException ex) {
			System.err.println(ex);
		}
		messages.add(new Msg(doc.getEndPosition().getOffset() - 2, messageType, messageText));
	}

	public void clearAllMessages()
	{
		messages.clear();
		Document doc = getDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException ex) {
			System.err.println(ex);
		}
	}

	public void clearMessagesBeforeSelection()
	{
		int dot = getCaret().getDot();
		int mark = getCaret().getMark();
		if (dot == mark) return; // nothing selected
		int caretPosition = Math.min(dot, mark);
		int i = messages.size() - 1;
		for (; i >= 0; --i) {
			Msg msg = (Msg) messages.get(i);
			if (msg.endPosition < caretPosition)
				break;
		}
		if (i < 0) return;
		while (i >= 0) {
			messages.remove(i);
			i--;
		}
		reloadMessages();
	}

	private void reloadMessages()
	{
		Document doc = getDocument();
		Msg msg;
		try {
			doc.remove(0, doc.getLength()); // clear it
			for (int i = 0; i < messages.size(); i++) {
				msg = (Msg) messages.get(i);
				doc.insertString(doc.getEndPosition().getOffset() - 1, msg.text,
				                 getStyle(msg.type));
				msg.endPosition = doc.getEndPosition().getOffset() - 2;
			}
		} catch (BadLocationException ex) {
			System.err.println(ex);
		}
	}

	public void freezeCaret(boolean isCaretFrozen)
	{
		this.isCaretFrozen = isCaretFrozen;
	}

	protected static class Msg
	{
		public int endPosition; // in document
		public String type;
		public String text;

		public Msg(int endPosition, String type, String text)
		{
			this.endPosition = endPosition;
			this.type = type;
			this.text = text;
		}
	}

	public static class MessageFormat
	{
		public String name;
		public Color color;
		public String fontFamily;
		public int style;
		public int size;

		public MessageFormat(String name, Color color,
		                     String fontFamily, int style, int size)
		{
			this.name = name;
			this.color = color;
			this.fontFamily = fontFamily;
			this.style = style;
			this.size = size;
		}

		public MessageFormat(String name, Color color, Font font)
		{
			this.name = name;
			this.color = color;
			this.fontFamily = font.getName();
			this.style = font.getStyle();
			this.size = font.getSize();
		}
	}

}

