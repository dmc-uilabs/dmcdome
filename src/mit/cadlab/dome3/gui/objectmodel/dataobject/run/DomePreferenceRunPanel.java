// DomePrefernceRunPanel.java
//   based on MatrixRunPanel of ____
//   ver 0.1

package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DomePreferenceBuildPanel;

import javax.swing.*;
import java.awt.*;

/**
 * DomePreferenceRunPanel:
 *  not editable with the structure of DomePreferenceData
 *  but size and value can be changed.
 *  ---extends buildPanel(can do this bcz it functions same as build but buttons related to structure changes are unabled...)
 */


public class DomePreferenceRunPanel extends DomePreferenceBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("PreferenceRunPanel");
	public static final String XML_TAG = "preferencerunpanel";


	public static DomePreferenceData showDialog(Component parent, DomePreferenceData data)
	{

		DomePreferenceRunPanel editor = new DomePreferenceRunPanel(data);

		JDialog d = DialogFactory.createDialog(parent, "Dome Preference Run Panel", editor, true, true);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.pack();

		d.show();

		return editor.answer;  //return it
	}


	/*
	 * Constructors
	 */

	public DomePreferenceRunPanel(DomePreferenceData v)
	{
		super(v);
		//setDataModel_GUI(v);
		convertToNotEditable();
	}


	public DomePreferenceRunPanel()
	{
		super();
		convertToNotEditable();
	}


	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("DomePreference Run Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		f.getContentPane().add(new DomePreferenceRunPanel(), BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
	}

	private void dispose()
	{

		SwingUtilities.windowForComponent(this).dispose();
	}


}
