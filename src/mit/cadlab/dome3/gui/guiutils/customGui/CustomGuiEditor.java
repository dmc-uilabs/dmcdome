// CustomGuiEditor.java
package mit.cadlab.dome3.gui.guiutils.customGui;

import mit.cadlab.dome3.gui.guiutils.customGui.classLoader.JarResources;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton1Msg;
import mit.cadlab.dome3.gui.guiutils.msg.OneButton2Msg;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterface;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.AbstractDomeModelInterface;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.exolab.ID.UUIDGenerator;
import mit.cadlab.dome3.objectmodel.toolinterface.ToolInterface;
import mit.cadlab.dome3.objectmodel.toolinterface.AbstractAnalysisToolInterface;
import mit.cadlab.dome3.swing.Templates;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;

/**
 * Used for selecting customGui.
 * version 2 of customGui chooser, can show jar content and picking the main class name
 */
public class CustomGuiEditor extends JDialog
{

	protected static Dimension DEFAULT_SIZE = new Dimension(500, 300);


	public static void showDialog(JComponent comp, ModelInterface mInterface, CustomGuiInfo info_to_be_edited)
	{
		CustomGuiEditor chooser = new CustomGuiEditor(comp, mInterface, info_to_be_edited);
		chooser.show();
	}

    public static void showDialog(JComponent comp, ToolInterface tInterface, CustomGuiInfo infoToBeEdited)
    {
        CustomGuiEditor chooser = new CustomGuiEditor(comp, tInterface, infoToBeEdited);
        chooser.show();
    }

	// instance variables
	protected static GridBagConstraints gbc; // used as abbreviation for GridBagConstraints class
	protected static String title = "Custom GUI Chooser";
	JButton selectButton,okButton,cancelButton;
	JButton pickButton,testButton;
	JList list;//list to show the content of the jar file
	JTextField fileTextField,nameTextField,jarLocTextField;
	ModelInterface mInterface = null;
    ToolInterface _tInterface = null;
	CustomGuiInfo customGui;
	// static String[] answer = null;

	protected CustomGuiEditor(Component comp, ModelInterface mInterface, CustomGuiInfo info_to_be_edited)
	{
		super(JOptionPane.getFrameForComponent(comp), title + " for interface:" + mInterface.getName(), true); // interface
		this.mInterface = mInterface;
		this.customGui = info_to_be_edited;
		//super(JOptionPane.getFrameForComponent(comp), title, true); // interface
		createComponents(comp);
	}

    protected CustomGuiEditor(Component comp, ToolInterface tInterface, CustomGuiInfo infoToBeEdited)
    {
        super(JOptionPane.getFrameForComponent(comp), title + " for interface " + tInterface.getName(), true);
        _tInterface = tInterface;
        customGui = infoToBeEdited;
        createComponents(comp);
    }

    protected void createComponents(Component comp)
    {
        Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(makeChooser(), BorderLayout.CENTER);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(comp);
    }

	protected JPanel makeChooser()
	{
		JPanel p = new JPanel();

		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				if (source == selectButton) {
					chooseAction();
				}
				else if (source == okButton) {
					commitAction();
				}
				else if (source == cancelButton) {
					cancelAction();
				}
				else if (source == pickButton) {
					pickAction();
				}
				else if (source == testButton) {
					testAction();
				}
			}
		};
		// create Buttons
		okButton = Templates.makeButton("ok", actionListener);
		cancelButton = Templates.makeButton("cancel", actionListener);
		okButton.setPreferredSize(cancelButton.getPreferredSize());
		selectButton = Templates.makeButton("select", actionListener);
		pickButton = Templates.makeButton("set", actionListener);
		pickButton.setEnabled(false);
		testButton = Templates.makeButton("test", actionListener);
		testButton.setEnabled(false);

		// setup TextFields
		fileTextField = Templates.makeTextField("");
		fileTextField.setEditable(false);
		nameTextField = Templates.makeTextField("");
		jarLocTextField = Templates.makeTextField("");
		jarLocTextField.setEditable(false);

		// setup Labels
		JLabel jarContentsLabel = Templates.makeLabel("select main GUI class (eg., package.classname):");
		JLabel fileLabel = Templates.makeLabel("main class selected:");
		JLabel nameLabel = Templates.makeLabel("name to appear in interface combobox: ");
		JLabel jarLocLabel = Templates.makeLabel("jar file for custom GUI: ");

		//setup list
		list = new JList(new jarContentlistmodel());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		//setVisibleRowCount(-1) makes the list display the maximum number of items possible in the available space onscreen.
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting() == false) {

					if (list.getSelectedIndex() == -1) {
						//No selection, disable fire button.
						testButton.setEnabled(false);
						pickButton.setEnabled(false);
					}
					else {
						testButton.setEnabled(true);
						pickButton.setEnabled(true);
					}
				}
			}
		});
		list.setFont(Templates.FONT11);
		JScrollPane listScroller = new JScrollPane(list);

		//load content
		String jarPath = customGui.getJarFilePath();
		jarLocTextField.setText(jarPath);
		fileTextField.setText(customGui.getClassName());
		nameTextField.setText(customGui.getShortName());

		list.setModel(new jarContentlistmodel(new File(jarPath)));

		JComponent[] comps = {
			nameLabel,
			nameTextField,
			jarLocLabel,
			jarLocTextField,
			selectButton,
			jarContentsLabel,
			listScroller,
			pickButton,
			testButton,
			fileLabel,
			fileTextField,
			okButton,
			cancelButton
		};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 5, 2, 2, 1.0, 1.0, gbc.WEST, gbc.BOTH, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.HORIZONTAL, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, gbc.NORTHEAST, gbc.HORIZONTAL, new Insets(2, 5, 0, 5), 0, 0),
			new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(2, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 9, 1, 1, 1.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 0), 0, 0),
			new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(10, 5, 5, 0), 0, 0)
		};

		Templates.layoutGridBagB(p, comps, gbcs);
		p.setPreferredSize(DEFAULT_SIZE);
		return p;
	}

	class jarContentlistmodel extends DefaultListModel
	{
		File jarFile;

		public jarContentlistmodel()
		{
		}

		public jarContentlistmodel(File _jarfile)
		{
			this.jarFile = _jarfile;
			loadContent();
		}

		protected void loadContent()
		{
			JarResources jarR = new JarResources(jarFile.getAbsolutePath());
			Enumeration e = jarR.getContents();
			while (e.hasMoreElements()) {
				addElement(e.nextElement());
			}
		}
	}


	protected void commitAction()
	{


		String filepath = jarLocTextField.getText();
		String classname = fileTextField.getText();
		String name = nameTextField.getText();

		if (classname.equals("")) {
			OneButton1Msg.showError(this, "Error: Config customGui Info", "Main class not selected yet!",
			                        "ok", new Dimension(230, 70));
			return;
		}

		if (name.equals("")) {
			OneButton1Msg.showError(this, "Error: Config customGui Info", "CustomGui name hasn't been specified yet!",
			                        "ok", new Dimension(230, 70));
			return;
		}

        if (mInterface != null)
		    ((AbstractDomeModelInterface) mInterface).editCustomGui(customGui, new CustomGuiInfo(classname, name, filepath));
        if (_tInterface != null)
            ((AbstractAnalysisToolInterface)_tInterface).editCustomGui(customGui, new CustomGuiInfo(classname, name, filepath));

		customGui.setClassName(classname);
		customGui.setJarFilePath(filepath);
		customGui.setShortName(name);
		dispose();
	}

	protected void cancelAction()
	{
		dispose();
	}

	protected Id getNextId()
	{
		return new Id(UUIDGenerator.create());
	}

	protected void pickAction()
	{
		String originalStr = list.getSelectedValue().toString();
		if (originalStr.endsWith(".class") || originalStr.endsWith(".Class")) {
			//first remove .class suffix;
			String classname = CustomGuiUtils.parse(originalStr.substring(0, originalStr.length() - 6));
			fileTextField.setText(classname);
		}
		else {
			fileTextField.setText("");
			OneButton2Msg.showError(this, "Error: set GUI main class", "is not a Java class file", originalStr,
			                        "ok", new Dimension(230, 70));
		}
	}

	protected void testAction()
	{
		String filepath = jarLocTextField.getText();
		String originalStr = list.getSelectedValue().toString();

		if (originalStr.endsWith(".class") || originalStr.endsWith(".Class")) {
			//first remove .class suffix;
			String classname = CustomGuiUtils.parse(originalStr.substring(0, originalStr.length() - 6));
			if (!CustomGuiUtils.isCustomGuiJcomponent(filepath, classname)) {
				OneButton2Msg.showError(this, "Error: set GUI main class", "is not a JComponent GUI class", originalStr,
				                        "ok", new Dimension(230, 70));
			}
			else {
				OneButton2Msg.showOption(this, "Option: successful test", "is a valid JComponent GUI class", originalStr,
				                         "ok", new Dimension(230, 70));
			}
		}
		else {
			OneButton2Msg.showError(this, "Error: set GUI main class", "is not a Java class file", originalStr,
			                        "ok", new Dimension(230, 70));
		}
	}

	protected void chooseAction()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new classFileFilter());
		if (chooser.showDialog(this, "choose") != JFileChooser.APPROVE_OPTION)
			return;
		else {
			//if some file that is not that type is selected, we will assume people want other filetype
			String newFileName = chooser.getSelectedFile().getAbsolutePath(); // never empty
			jarLocTextField.setText(newFileName);
			list.setModel(new jarContentlistmodel(chooser.getSelectedFile()));
		}
	}


	public static void main(String[] args)
	{

		//  customGuiEditor.showDialog(null, null);

	}

	class classFileFilter extends FileFilter
	{
		public boolean accept(File f)
		{
			if (f.isDirectory()) {
				return true;
			}
			return ((f.getName().endsWith(".jar"))
			        || (f.getName().endsWith(".JAR")));
		}

		public String getDescription()
		{
			return "jar files (*.jar)";
		}
	}
}
