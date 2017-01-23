// DocumentationBasePanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.DTextPane;
import mit.cadlab.dome3.swing.Templates;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.rtf.RTFEditorKit;

public class DocumentationBasePanel extends JPanel
{

	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
//    protected static Dimension preferredSize = new Dimension(340, 130);
	protected static Dimension preferredSize = new Dimension(450, 200);

	protected PropertyChangeListener propertyListener;
	protected DTextPane commentsEditor;
	protected DTextField urlTextField;
	protected JButton openUrlButton;
	protected Documentation dataModel;

	public DocumentationBasePanel(Documentation doc)
	{
		if (doc == null)
			throw new IllegalArgumentException("Documentation gui - null Documentation");
		dataModel = doc;
		propertyListener = getPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		layoutComponents(createComponents());
		configureComponents();
		getModelData();
		setPreferredSize(preferredSize);
	}

	protected PropertyChangeListener getPropertyListener()
	{
		return new DocumentationPropertyChangeListener();
	}

	protected JComponent[] createComponents()
	{
		JLabel commentLabel = Templates.makeLabel("comments:");
		commentsEditor = Templates.makeDTextPane();
		JScrollPane spane = new JScrollPane(commentsEditor);
		JLabel urlLabel = Templates.makeLabel("documentation URL:");
		urlTextField = Templates.makeDTextField();
		openUrlButton = Templates.makeButton("open");
		openUrlButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					Templates.openURL(urlTextField.getText());
				} catch (java.io.IOException ex) {
					System.err.println("openURL: " + ex.getMessage());
				}
			}
		});
		return new JComponent[]{commentLabel,
		                        spane,
		                        urlLabel,
		                        urlTextField,
		                        openUrlButton
		};
	}

	protected void layoutComponents(JComponent[] comps)
	{
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0), // commentLabel
			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, gbc.NORTHWEST, gbc.BOTH, new Insets(5, 0, 3, 0), 0, 0), // commentsEditor
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.NORTHWEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0), // urlLabel
			new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 4), // urlTextField
			new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, -2), // openUrlButton
		};

		Templates.layoutGridBagB(this, comps, gbcs);
	}

	// to be overridden by subclasses
	protected void configureComponents()
	{
	}

	// connect to data model
	public void setModel(Documentation model)
	{
		if (model == null)
			throw new IllegalArgumentException("Documentation gui - null Documentation");
		if (dataModel != null)
			dataModel.removePropertyChangeListener(propertyListener);
		dataModel = model;
		dataModel.addPropertyChangeListener(propertyListener);
		getModelData();
	}

	protected void getModelData()
	{
		setText(dataModel.getText());
		commentsEditor.setCurrent();
		setURL(dataModel.getURL());
		urlTextField.setCurrent();
	}

	protected void setModelData()
	{
		setModelText();
		setModelURL();
	}

	public void setModelText()
	{
		dataModel.setText(getText());
	}

	protected void setModelURL()
	{
		dataModel.setURL(getURL());
	}

	// Documentation javabean support
	public String getText()
	{
		return commentsEditor.getRtfText();
	}

	public void setText(String rtfText)
	{
		commentsEditor.setRtfText(rtfText);
	}

	public String getURL()
	{
		return urlTextField.getText();
	}

	public void setURL(String url)
	{
		urlTextField.setText(url);
	}


	protected class DocumentationPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(Documentation.TEXT)) {
				// setText((String)newValue); -- ignore
				//above line not reqd.  Actually sets repeat text in the pane
			} else if (property.equals(Documentation.URL)) {
				setURL((String) newValue);
				urlTextField.setCurrent();
			}
		}
	}

}
