package mit.cadlab.dome3.gui.objectmodel.tools;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Apr 10, 2003
 * Time: 3:06:26 PM
 * To change this template use Options | File Templates.
 */
public class XmlViewPanel extends JPanel
{
	protected XMLSupport data;
	protected JTextArea textArea;
	protected static GridBagConstraints gbc;

	public XmlViewPanel(XMLSupport inData)
	{
		this.data = inData;
		textArea = Templates.makeDisplayTextArea(getXml());
		JButton refresh = Templates.makeButton("refresh", new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		});

		JComponent[] comps = {refresh, new JScrollPane(textArea)};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(5, 5, 0, 5), 0, 0)
		};
		Templates.layoutGridBag(this, comps, gbcs);
	}

	public void refresh()
	{
		textArea.setText(getXml());
	}

	private String getXml()
	{
		return XMLUtils.toPrettyString(data.toXmlElement()).trim();
	}
}
