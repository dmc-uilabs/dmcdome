// DocumentationBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DocumentationBasePanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.text.StyledEditorKit;

public class DocumentationBuildPanel extends DocumentationBasePanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("DocumentationBuildPanel");
	public static final String XML_TAG = "documentationbuildpanel";

	public DocumentationBuildPanel(Documentation doc)
	{
		super(doc);
	}

	protected void configureComponents()
	{
		commentsEditor.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setModelText();
			}
		});
		urlTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setModelURL();
			}
		});
	}

	public static final JMenu menu = createStyleMenu();

	protected static JMenu createStyleMenu()
	{
		JMenu menu = MenuUtils.makeBoldMenu("Format documentation");

		JMenu styleMenu = MenuUtils.makeMenu("Font style");
		styleMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.BoldAction(), "Bold"));
		styleMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ItalicAction(), "Italic"));
		styleMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.UnderlineAction(), "Underline"));
		menu.add(styleMenu);
		menu.addSeparator();

		JMenu sizeMenu = MenuUtils.makeMenu("Font size");
		sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("12", 12)));
		sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("14", 14)));
		sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("16", 16)));
		sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("18", 18)));
		sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("24", 24)));
		sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("36", 36)));
		sizeMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontSizeAction("48", 48)));
		menu.add(sizeMenu);
		menu.addSeparator();

		JMenu familyMenu = MenuUtils.makeMenu("Font family");
		familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Sans Serif", "SansSerif")));
		familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Serif", "Serif")));
		familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Dialog", "Dialog")));
		familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Dialog Input", "DialogInput")));
		familyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.FontFamilyAction("Monospaced", "Monospaced")));
		menu.add(familyMenu);
		menu.addSeparator();

		JMenu colorMenu = MenuUtils.makeMenu("Font color");
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Black", Color.black)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Blue", Color.blue)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Cyan", Color.cyan)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Green", Color.green)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Magenta", Color.magenta)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Orange", Color.orange)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Pink", Color.pink)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Red", Color.red)));
		colorMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.ForegroundAction("Yellow", Color.yellow)));
		menu.add(colorMenu);
		/*menu.addSeparator();

		JMenu justifyMenu = MenuUtils.makeMenu("Justification");
		justifyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.AlignmentAction("Left justify", StyleConstants.ALIGN_LEFT)));
		justifyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.AlignmentAction("Center justify", StyleConstants.ALIGN_CENTER)));
		justifyMenu.add(MenuUtils.makeMenuItem(new StyledEditorKit.AlignmentAction("Right justify", StyleConstants.ALIGN_RIGHT)));
		menu.add(justifyMenu);*/

		return menu;
	}

}
