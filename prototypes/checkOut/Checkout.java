package checkOut;

import mit.cadlab.dome.gui.deploy.components.DeployUtilities;
import mit.cadlab.dome.gui.deploy.components.ModelVersionData;
import mit.cadlab.dome.gui.deploy.components.PlayspaceVersionData;
import mit.cadlab.dome.gui.login.LoginPrompt;
import mit.cadlab.dome.gui.swing.DomeFileChooser;
import mit.cadlab.dome.gui.swing.msg.OneButton1Msg;
import mit.cadlab.dome.gui.swing.msg.TwoButton1Msg;
import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.functions.CheckOutFunctions;
import mit.cadlab.dome.util.FileUtils;

import javax.swing.JFileChooser;
import java.awt.Dimension;
import java.io.File;
import java.util.Vector;


//todo When integrated with DOME, the main build menu ...
//should have the following items: New model -> (dome model, excel model sub menus); Open model ...; separator; New playspace;
//                                 Open playspace...; separator; Checkout model; Checkout playspace;
//                                 separator; Close all; Save all; separator; Exit
// Qing can do the main build environment integration


/**
 * Created by IntelliJ IDEA.
 * User: wallace
 * Date: Mar 18, 2003
 * Time: 1:40:30 PM
 * To change this template use Options | File Templates.
 */
public class Checkout
{
	// constants for the possible checkout types
	public static final byte CHECKOUT_MODEL = 0;
	public static final byte CHECKOUT_PLAYSPACE = 1;

	private Object selectedObjectId = null;
	private File savePathFile = null;
	private ServerConnection conn = null;
	private static JFileChooser saveChooser = null;
	private Vector modelContent = null;
	private Vector playspaceContent = null;
	private static final String slash = System.getProperty("file.separator");

	/**
	 * Class used to checkout models or playspaces for a server for local editing
	 * @param checkoutType use one of the two constants Checkout.CHECKOUT_MODEL or Checkout.CHECKOUT_PLAYSPACE
	 */
	public Checkout(byte checkoutType)
	{
		//first login
		conn = LoginPrompt.showDialog(null);
		// if valid login
		if (conn == null)
			return;
		// get location of item on server to be checked out
		selectedObjectId = LocateForCheckout.showDialog(null, conn, checkoutType);

		//setup file choosed for model or playspace checkout case
		if (selectedObjectId == null)
			return;

		saveChooser = new JFileChooser();

		if (checkoutType == CHECKOUT_MODEL)
			saveChooser.setFileFilter(new DomeFileChooser.DomeModelFileFilter());
		else
			saveChooser.setFileFilter(new DomeFileChooser.DomePlayspaceFileFilter());

		this.initializeModelAndPlayspaceContent(checkoutType);
		if (!this.haveSavePermission(checkoutType))
			return;

		if (!this.saveModelPlayspaceFile(checkoutType))
			return;

		if (checkoutType == CHECKOUT_MODEL) {

			this.saveInterfaces();

			if (this.haveReDeployPermission(checkoutType))
				this.writeModelVersionFile();

		}
		else {
			if (this.haveReDeployPermission(checkoutType))
				this.writePlayspaceVersionFile();

		}

	}

	public void initializeModelAndPlayspaceContent(byte checkoutType)
	{

		if (checkoutType == CHECKOUT_MODEL)
			modelContent = CheckOutFunctions.checkoutModel(conn, selectedObjectId.toString());
		else
			playspaceContent = CheckOutFunctions.checkoutPlayspace(conn, selectedObjectId.toString());

	}

	private void saveInterfaces()
	{

		Vector modelInfo = (Vector) modelContent.get(0);              //vector with single component containing model info
		Vector interfaceInfo = (Vector) modelContent.get(1);         //vector containing other vectors, which conatain interface info

		File interfaceDir = new File(savePathFile.getParent() + slash + "interfaces-" + (savePathFile.getName()).substring(0, (savePathFile.getName().length() - 4)) + "-" + modelInfo.get(0));

		if (interfaceDir.exists()) {
			System.out.println("The directory already exists");
		}
		else {
			if (!interfaceDir.mkdir())
				System.out.println("Error creating interface directory");

		}

		int numberOfInterfaces = interfaceInfo.size();

		for (int i = 0; i < numberOfInterfaces; i++) {

			Vector intf = new Vector((Vector) interfaceInfo.get(i));
			save(interfaceDir.toString() + slash + (intf.get(0) + "-mappings"), (String) intf.get(2));
			save(interfaceDir.toString() + slash + (intf.get(0) + ".dmi"), (String) intf.get(1));
		}

	}

	private void writeModelVersionFile()
	{

		DeployUtilities.writeModelVersionFile(savePathFile.toString(), new ModelVersionData((Vector) modelContent.get(2), conn));
		return;
	}

	private void writePlayspaceVersionFile()
	{

		DeployUtilities.writePlayspaceVersionFile(savePathFile.toString(), new PlayspaceVersionData((Vector) playspaceContent.get(1), conn));
		return;
	}

	private boolean haveReDeployPermission(byte checkoutType)
	{

		int size;
		Vector element;
		if (checkoutType == CHECKOUT_MODEL) {
			size = 3;
			element = modelContent;
		}
		else {
			size = 2;
			element = playspaceContent;
		}

		if (element.size() == size)
			return true;
		else
			return false;

	}

	private boolean haveSavePermission(byte checkoutType)
	{

		Vector element;
		if (checkoutType == CHECKOUT_MODEL)

			element = modelContent;

		else
			element = playspaceContent;


		if (element.size() == 0) {
			OneButton1Msg.showError(null, "Error", "Do not have permission to check-out",
			                        "OK", new Dimension(230, 80));
			return false;
		}
		else
			return true;
	}

	public boolean saveModelPlayspaceFile(int checkoutType)
	{

		Vector checkoutElement;
		if (checkoutType == CHECKOUT_MODEL)
			checkoutElement = modelContent;
		else
			checkoutElement = playspaceContent;

		Vector ElementInfo = (Vector) checkoutElement.get(0);              //vector with single component containing model info

		String elementName;
		String elementContent;
		if (checkoutType == CHECKOUT_MODEL) {
			elementName = Checkout.removeSpaces((String) ElementInfo.get(1));
			elementContent = (String) ElementInfo.get(2);
		}
		else {
			elementName = Checkout.removeSpaces((String) ElementInfo.get(0));
			elementContent = (String) ElementInfo.get(1);
		}
		boolean notSaved = true;

		File file = null;
		String suffix;
		if (checkoutType == CHECKOUT_MODEL)
			suffix = ".dml";
		else
			suffix = ".dps";

		while (notSaved) {

			if (saveChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
				return false;

			String newFileName = saveChooser.getSelectedFile().getAbsolutePath(); // never empty

			if (!newFileName.endsWith(suffix))
				newFileName = newFileName + suffix;

			// check if file already exists
			file = new File(newFileName);

			if (file.exists()) {

				String msg = null;
				msg = "File <" + file.getName() + "> already exists. Replace it?";


				int button = TwoButton1Msg.showOption(null,
				                                      "Warning: File exists", msg, "Replace",
				                                      "Cancel", new Dimension(230, 80));
				if (button != 0) {
					Checkout.save(newFileName, elementContent);
					notSaved = false;
				}


			}
			else {
				Checkout.save(newFileName, elementContent);
				notSaved = false;
			}

		}

		savePathFile = file;

		return true;
	}

	private static String removeSpaces(String name)
	{
		int x = name.length();
		StringBuffer noSpace = new StringBuffer();

		for (int i = 0; i < x; i++) {
			if (!Character.isWhitespace(name.charAt(i))) {
				noSpace.append(name.charAt(i));
			}
		}

		return noSpace.toString();
	}

	private static void save(String fileName, String content)
	{

		FileUtils.writeStringToFile(content, fileName);

	}

	public static void main
	        (String[] args)
	{
		//new Checkout(CHECKOUT_MODEL);
		new Checkout(CHECKOUT_PLAYSPACE);
		System.exit(0);

	}
}