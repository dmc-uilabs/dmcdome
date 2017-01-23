// DomeVectorRunPanel.java
//   based on VectorPanel of 04/11/02
//   ver 0.1
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DomeVectorBuildPanel;

import javax.swing.*;
import java.awt.*;


/**
 * DomeVectorRunPanel:
 *  not editable with the structure of DomeVectorData
 *  but size and value can be changed.
 *  ---extends buildPanel(can do this bcz it functions same as build but buttons related to structure changes are unabled...)
 */


public class DomeVectorRunPanel extends DomeVectorBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("VectorRunPanel");
	public static final String XML_TAG = "vectorrunpanel";


	public static DomeVectorData showDialog(Component parent, DomeVectorData data)
	{

		DomeVectorRunPanel editor = new DomeVectorRunPanel(data);

		JDialog d = DialogFactory.createDialog(parent, "Dome Vector Run Panel", editor, true, true);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.pack();

		d.show();

		return editor.answer;  //return it
	}


	/*
	 * Constructors
	 */
	public DomeVectorRunPanel(DomeVectorData v)
	{
		super(v);
		//setDataModel_GUI(v);
		convertToNotEditable();
	}

/* MAK: empty constructors cause problems with Constructor.newInstance()
	public DomeVectorRunPanel()
	{
		super();
		convertToNotEditable();
	}
*/

	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("DomeVector Run Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		// MAK: remove "new DomeVectorData()" to invoke empty constructor
		f.getContentPane().add(new DomeVectorRunPanel(new DomeVectorData()), BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
	}

	private void dispose()
	{

		SwingUtilities.windowForComponent(this).dispose();
	}


}
