//MIT CADLAB
//Qing Aug 13, 2002

package mit.cadlab.dome3.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.plaf.ComponentUI;

import java.io.*;

public class LineNumberedScrollPane extends JScrollPane
{
	//the line number object of this scroolpane
	private LineNumber LN;
	//the text pane object of this scroolpane
	private JTextComponent TP;

	public LineNumberedScrollPane(JTextArea ta)
	{
		this(ta, false);
	}

	public LineNumberedScrollPane(JTextPane tc)
	{
		this(tc, true);
	}

	/**
	 * Convenience constructor for Text Components
	 *   case1:  if it's JTextArea, do nothing, should directly call constructor(JTextArea);
	 *   case2: if it's JTextPane, should call constructor(JTextPane)!!! this is important !
	 *   case3: for all customized JTextPane with LineWrap disabled feature, call constructor(JTextComponent,false), !!!!
	 */

	public LineNumberedScrollPane(JTextComponent tc, boolean isLineWrapped)
	{
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

/*        if (!isLineWrapped || tc instanceof JTextArea) {
            TP = tc;
        } else {
            //only if the arg is textpane object, make sure so ur subclass features will not lose.
            TP = new JTextPane(((JTextPane) tc).getStyledDocument()) {
                //prevent word wrap
                public boolean getScrollableTracksViewportWidth() {
                    //if (!wrap)
                    //{
                    Component parent = this.getParent();
                    ComponentUI ui = this.getUI();
                    int uiWidth = ui.getPreferredSize(this).width;
                    int parentWidth = parent.getSize().width;
                    boolean bool = (parent != null)
                            ? (ui.getPreferredSize(this).width < parent.getSize().width)
                            : true;

                    return bool;
                    //}
                    // else return super.getScrollableTracksViewportWidth();
                }

                public void setBounds(int x, int y, int width, int height) {
                    //if (wrap)
                    //	super.setBounds(x, y, width, height);
                    // else
                    //{
                    Dimension size = this.getPreferredSize();
                    super.setBounds(x, y, Math.max(size.width, width), Math.max(size.height, height));
                    //	}
                }
            };
        }
 */
		TP = tc;

		TP.setMargin(new Insets(0, 5, 0, 5));

		setViewportView(TP);

		LN = new LineNumber(TP);
		LN.setPreferredSize(99);
		setRowHeaderView(LN);


		//layout components
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		panel.add(Templates.makeLabel("Row:"));
		final JLabel rowLabel = Templates.makeLabel(" ");
		panel.add(rowLabel);
		panel.add(Templates.makeLabel("Col:"));
		final JLabel colLabel = Templates.makeLabel(" ");
		panel.add(colLabel);

		setColumnHeaderView(panel);

		final JCheckBox showline = Templates.makeCheckBox();
		showline.setMnemonic(KeyEvent.VK_S);
		showline.setSelected(true);

		TP.addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent ce)
			{
				int pos = TP.getCaretPosition();
				Element map = TP.getDocument().getDefaultRootElement();
				int row = map.getElementIndex(pos);
				Element lineElem = map.getElement(row);
				int col = pos - lineElem.getStartOffset();
				rowLabel.setText("" + (row + 1));
				if (((PythonEditor) TP).isTabKeyPressed()) {
					col = col + ((PythonEditor) TP).DEFAULT_TAB_SIZE;
					colLabel.setText("" + col);
				} else {
					colLabel.setText("" + (col + 1));
				}
			}
		});

		TP.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				setLineNumber();
			}

			public void removeUpdate(DocumentEvent e)
			{
				setLineNumber();
			}

			public void changedUpdate(DocumentEvent e)
			{
				setLineNumber();
			}

			public void setLineNumber()
			{
				Element map = TP.getDocument().getDefaultRootElement();
				int maxline = map.getElementIndex(TP.getDocument().getLength());

				//if(maxline==0) return;
				LN.setEndLineNumber(maxline + 1);
			}
		});

		showline.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.DESELECTED)
				//hide line number
					LN.off();
				else
					LN.on();
			}

		});

		JPanel checkboxCorner = new JPanel();
		checkboxCorner.setLayout(new GridLayout(1, 1));
		checkboxCorner.add(showline);

		setCorner(JScrollPane.UPPER_LEFT_CORNER, checkboxCorner);

		setPreferredSize(new Dimension(100, 100));

		updateUI();
	}


	public void setLineNumberShowing(boolean is)
	{
		if (is)
			LN.on();
		else
			LN.off();
	}

	public void setLineNumberBackground(Color newBK)
	{
		LN.setBKColor(newBK);
		LN.setBackground(newBK);
	}

	public Color getLineNumberBackground()
	{
		return LN.getBackground();
	}

	public void setLineNumberForeground(Color newFR)
	{
		LN.setFRColor(newFR);
		LN.setForeground(newFR);
	}

	public Color getLineNumberForeground()
	{
		return LN.getForeground();
	}

	public void setLineNumberFont(Font f)
	{
		LN.setFont(f);
	}

	public Font setLineNumberFont()
	{
		return LN.getFont();
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("LineNumberDemo_JTextArea");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


		LineNumberedScrollPane pane = new LineNumberedScrollPane(new JTextArea());


		frame.getContentPane().add(BorderLayout.CENTER, pane);

		frame.pack();
		frame.setVisible(true);

		JFrame frame2 = new JFrame("LineNumberDemo_EmptyJTextPane");

		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTextPane p = new JTextPane();
		Dimension size = new Dimension(50, 100);
		p.setPreferredSize(size);
		p.setMaximumSize(size);
		p.setMinimumSize(size);


		LineNumberedScrollPane pane2 = new LineNumberedScrollPane(p);
		pane2.setLineNumberBackground(Color.yellow);
		pane2.setLineNumberForeground(Color.green);


		frame2.getContentPane().add(BorderLayout.CENTER, pane2);

		frame2.pack();
		frame2.setLocation(10, 10);
		frame2.setVisible(true);


		JFrame frame3 = new JFrame("LineNumberDemo_ExistJTextPane");
		frame3.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTextPane p2 = new JTextPane();
		//try load a python file
		try {
			//String path="";
			String filename = ".//src//mit//cadlab//test2//saved.py"; //---default name
			//InputStream in=new FileInputStream(path+filename);
			InputStream in = new FileInputStream(filename);

			//SyntaxDocument doc=(SyntaxDocument)getDocument();


			p2.getEditorKit().read(in, p2.getDocument(), 0);
			//p.setDocument(doc);
			in.close();


		} catch (Exception ex) {
			ex.printStackTrace();
		}

		LineNumberedScrollPane pane3 = new LineNumberedScrollPane(p2);


		frame3.getContentPane().add(BorderLayout.CENTER, pane3);

		frame3.pack();
		frame3.setLocation(30, 30);
		frame3.setVisible(true);


	}
}

