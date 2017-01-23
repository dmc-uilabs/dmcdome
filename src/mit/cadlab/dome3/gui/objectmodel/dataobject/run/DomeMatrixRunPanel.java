// DomeMatrixRunPanel.java
//   based on MatrixPanel of 04/11/02
//   ver 0.1

package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.objectmodel.dataobject.DomeMatrixData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.DomeMatrixBuildPanel;

import javax.swing.*;
import java.awt.*;

/**
 * DomeMatrixRunPanel:
 *  not editable with the structure of DomeMatrixData
 *  but size and value can be changed.
 *  ---extends buildPanel(can do this bcz it functions same as build but buttons related to structure changes are unabled...)
 */


public class DomeMatrixRunPanel extends DomeMatrixBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("MatrixRunPanel");
	public static final String XML_TAG = "matrixrunpanel";


	public static DomeMatrixData showDialog(Component parent, DomeMatrixData data)
	{

		DomeMatrixRunPanel editor = new DomeMatrixRunPanel(data);

		JDialog d = DialogFactory.createDialog(parent, "Dome Matrix Run Panel", editor, true, true);

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.pack();

		d.show();

		return editor.answer;  //return it
	}


	/*
	 * Constructors
	 */

	public DomeMatrixRunPanel(DomeMatrixData v)
	{
		super(v);
		//setDataModel_GUI(v);
		convertToNotEditable();
	}


	/* MAK: remove empty constructor -- confuses panel constructor finder mechanism and throws constructor error
	public DomeMatrixRunPanel()
	{
		//MAK: remove "new DomeMatrixDat()" below
		super(new DomeMatrixData());
		convertToNotEditable();
	}
	*/

	public static void main(String[] args)
	{
		JFrame f = Templates.makeTestFrame("DomeMatrix Run Panel");

		f.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		f.getContentPane().add(new DomeMatrixRunPanel(new DomeMatrixData()), BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
	}

	private void dispose()
	{

		SwingUtilities.windowForComponent(this).dispose();
	}


}
