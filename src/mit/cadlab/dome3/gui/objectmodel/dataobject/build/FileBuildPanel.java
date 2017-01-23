// FileBuildPanel.java
package mit.cadlab.dome3.gui.objectmodel.dataobject.build;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.swing.DComboBox;
import mit.cadlab.dome3.swing.DTextField;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.FileUtils;
import mit.cadlab.dome3.gui.guiutils.msg.TwoButton2Msg;
import mit.cadlab.dome3.gui.objectmodel.dataobject.DataObjectPanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class FileBuildPanel extends DataObjectPanel
{

	protected PropertyChangeListener propertyListener;
	protected DTextField valueTextField;
	protected JButton constraintsButton;
	protected DComboBox fileTypeComboBox;
	protected JCheckBox showFileCheckBox;
	protected JButton browseButton;
	protected mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile dataModel;
	public static final TypeInfo TYPE_INFO = new TypeInfo("FileBuildPanel");
	public static final String XML_TAG = "filebuildpanel";

	public FileBuildPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile f)
	{
		if (f == null)
			throw new IllegalArgumentException("DomeFile gui - null DomeFile");
		dataModel = f;
		propertyListener = createPropertyListener();
		dataModel.addPropertyChangeListener(propertyListener);
		layoutPanel();
		configComponents();
	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new FilePropertyChangeListener();
	}

	protected void layoutPanel()
	{
		fileTypeComboBox = Templates.makeDComboBox(FileData.validFileType);
		fileTypeComboBox.setSelectedObject(dataModel.getFileType());
		fileTypeComboBox.setEnabled(isFileTypeComboBoxEnabled());

		valueTextField = Templates.makeDTextField(dataModel.getFilePath());
		showFileCheckBox = Templates.makeCheckBox("show file in browser");
		browseButton = Templates.makeButton("choose...");

		JLabel label = Templates.makeLabel("file path:");
		JLabel label2 = Templates.makeLabel("file type:");
		JPanel topPanel = new JPanel();
		JComponent[] comps = {label2,
		                      fileTypeComboBox,
		                      label,
		                      valueTextField,
		                      browseButton,
		                      showFileCheckBox
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(0, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0),
			new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(0, 2, 2, 1, 0.0, 1.0, gbc.WEST, gbc.NONE, new Insets(5, 0, 0, 0), 0, 0)

		};
		Templates.layoutGridBagB(topPanel, comps, gbcs);

		//setting the combobox size
		Dimension oldDimension = fileTypeComboBox.getPreferredSize();
		Dimension expectDimension = browseButton.getPreferredSize();
		double height = oldDimension.getHeight();
		double width = expectDimension.getWidth();
		fileTypeComboBox.setPreferredSize(new Dimension((int) width, (int) height));

		JPanel middlePanel = new JPanel();
		constraintsButton = Templates.makeButton("constraints");

		JComponent[] comps2 = {topPanel,
		                       middlePanel, // filler
		                       constraintsButton,
		};

		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs2 = {
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, gbc.WEST, gbc.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0), // valueTextField
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, gbc.CENTER, gbc.BOTH, new Insets(0, 0, 0, 0), 0, 0), // fillerPanel
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, gbc.EAST, gbc.NONE, new Insets(5, 0, 0, 0), 0, -2), // constraintsButton
		};
		Templates.layoutGridBagB(this, comps2, gbcs2);


	}


	protected void configComponents()
	{
		valueTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				/*String newFilepath = getFilePath();
				String suffix = FileUtils.getDefaultSuffixForType(dataModel.getFileType());
				if(newFilepath.indexOf(".")==-1) {
					newFilepath = newFilepath + suffix;
					setFilePath(newFilepath);
				}*/
				setModelData();
			}
		});

		browseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(FileUtils.getFilterForType(getFileType()));

				if (chooser.showDialog(browseButton, "choose") != JFileChooser.APPROVE_OPTION)
					return;
				else {
					//if some file that is not that type is selected, we will assume people want other filetype
					String newFileName = chooser.getSelectedFile().getAbsolutePath(); // never empty
					String suffix = FileUtils.getDefaultSuffixForType(dataModel.getFileType());
					if (!newFileName.endsWith(suffix)) { //this means user pick another type of file or forgot to add extension
						if (newFileName.indexOf(".") == -1) {
							int answer = TwoButton2Msg.showOption(FileBuildPanel.this, "No extension", "The file name has no extension " + suffix + "!", "",
							                                      "add extension", "don't add extension", new Dimension(300, 100));
							if (answer == TwoButton2Msg.LEFT_OPTION) {
								newFileName = newFileName + suffix;
							} else {// otherwise, keep it
								setFileType(FileUtils.getTypeForFile(newFileName));
							}

						} else
							setFileType(FileUtils.getTypeForFile(newFileName));
					}
					setFileType(FileUtils.getTypeForFile(newFileName));
					setFilePath(newFileName);
					setModelData();
				}
			}
		});

		addCheckBoxListener();

		fileTypeComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				dataModel.setFileType(getFileType());
			}
		});

	}

	protected void addCheckBoxListener()
	{
		showFileCheckBox.addActionListener(createCheckBoxListener());
	}

	protected ActionListener createCheckBoxListener()
	{

		return new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (showFileCheckBox.isSelected()) {

					FileUtils.showFile(dataModel);
				}
			}
		};
	}

	private boolean isFileTypeComboBoxEnabled()
	{
		if (dataModel.getFilePath().trim().equals("")) return true;
		return false;
	}


	// connect to data model
	public void setDataObject(DataObject data)
	{
        // sangmok: added code to fix memory problem
        // setDataObject(null) is invoked during the executino of releaseDataObjectReferenceOfDataObjectPanel() in DataObjectCards class
        // when DomeMatrixData is null, codes like setDataModel_GUI() should be skipped
        // instead setDataModel_Null() should be invoked
        if (data == null) {
            setDataModel_Null();
            return;
        }
        // sangmok: added code ends

		if (data instanceof mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile)
			setModel((mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile) data);
		else
			throw new IllegalArgumentException("DomeFile gui - non-DomeFile parameter");
	}

    /**
     * sangmok : a new method to fix memory leakage problem
     * set data object reference (=dataMatrix) as null
     * also release data object reference in TableModel object
     */
	protected void setDataModel_Null()
	{
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = null;
	}
    
	public void setModel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile model)
	{
		if (model == null)
			throw new IllegalArgumentException("DomeFile gui - null DomeFile");
		if (dataModel != null) {
			dataModel.removePropertyChangeListener(propertyListener);
		}
		dataModel = model;
		dataModel.addPropertyChangeListener(propertyListener);
		getModelData();

	}

	protected void getModelData()
	{
		setFilePath(dataModel.getFilePath());
		valueTextField.setCurrent();
		setFileType(dataModel.getFileType());
	}

	/**
	 * when calling this function ,don't forget to set file type as well
	 */
	protected void setModelData()
	{
		String newFilepath = getFilePath();
		if (newFilepath.equals("")) {
			//empty string,
			dataModel.setFilePath(newFilepath);
			return;
		}

		dataModel.setFilePath(parseStr(newFilepath));

		validateFile(dataModel.getFilePath());

		//String oldType=getFileType();

		dataModel.setFileType(getFileType());
	}


	protected String parseStr(String newFilepath)
	{
		//deal with the suffix
		String suffix = FileUtils.getDefaultSuffixForType(dataModel.getFileType());
		File file = new File(newFilepath);
		if (newFilepath.endsWith(File.separator))
			return newFilepath + FileUtils.DEFAULTPREFIX + suffix;
		if (file.getName().trim().equals(""))
			return newFilepath + File.separator + FileUtils.DEFAULTPREFIX + suffix;
		/*if (!file.getName().endsWith(suffix)){
			if(file.getName().indexOf(".")==-1) {
				newFilepath = newFilepath + suffix;
			}
			//return newFilepath = newFilepath + suffix;
		}*/
		setFileType(FileUtils.getTypeForFile(newFilepath));
		return newFilepath;
	}

	protected void validateFile(String filepath)
	{
		File file;
		try {
			file = new File(filepath);
			if (file.exists()) {
				//do nothing!
			} else {
				//	OneButton1Msg.showWarning(null, "Warning:file not exist", filepath + " is not exist!", "ok", OneButton1Msg.DEFAULT_SIZE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//OneButton1Msg.showWarning(this, "File Warning", "error in the file path", "ok", OneButton1Msg.DEFAULT_SIZE);
			return;
		}

	}

	public String getFilePath()
	{
		return valueTextField.getText().trim();
	}

	public void setFilePath(String value)
	{
		valueTextField.setText(value);
	}

	public String getFileType()
	{
		return (String) fileTypeComboBox.getSelectedItem();
	}

	public void setFileType(String value)
	{
		if (FileData.isValidFileType(value))
			fileTypeComboBox.setSelectedItem(value);
		else {
			System.err.println("FileBuildPanel GUI Error--wrong file type!");
		}
	}

	protected class FilePropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile.FILEPATH)) {
				setFilePath(newValue.toString());
				valueTextField.setCurrent();
				fileTypeComboBox.setEnabled(isFileTypeComboBoxEnabled());

			}
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile.FILETYPE)) {
				setFileType(newValue.toString());
			}
		}
	}

}
