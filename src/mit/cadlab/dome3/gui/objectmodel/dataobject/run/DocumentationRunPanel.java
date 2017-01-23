// DocumentationRunPanel.java
/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: Apr 10, 2003
 * Time: 11:46:56 AM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.swing.DTextPane;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DocumentationBuildPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class DocumentationRunPanel extends DocumentationBuildPanel
{

	public DocumentationRunPanel(Documentation doc)
	{
		super(doc);
		convertToNotEditable();
	}

	protected void convertToNotEditable()
	{
		commentsEditor.setEnabled(false);
		urlTextField.setEnabled(false);
		openUrlButton.setEnabled(false);

	}


}