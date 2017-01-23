// FileRunPanel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject.run;

import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.objectmodel.modelobject.parameter.run.ParameterRunPanel;
import mit.cadlab.dome3.gui.objectmodel.dataobject.build.FileBuildPanel;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.RuntimeFunctionsClient;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ParameterRuntime;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.util.FileUtils;

import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 *  
 */
public class FileRunPanel extends FileBuildPanel
{
	public static final TypeInfo TYPE_INFO = new TypeInfo("FileRunPanel");
	public static final String XML_TAG = "filerunpanel";
	protected boolean isFileViewedOnce = false;

	public FileRunPanel(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile f)
	{
		super(f);
		showFileCheckBox.setSelected(((FileData) dataModel).isIfChecked());
		convertToNotEditable();
	}

	public void convertToNotEditable()
	{
		valueTextField.setEnabled(true);   //so that file path can be changed on client
		browseButton.setEnabled(true);     //so that file path can be changed on client
		fileTypeComboBox.setEnabled(false);
	}

	//so that the client can get the file from the server
	protected ActionListener createCheckBoxListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				((FileData) dataModel).setIfChecked(showFileCheckBox.isSelected());

				if (showFileCheckBox.isSelected()) {
					((FileData) dataModel).ensureGoodFileName(FileRunPanel.this);
					boolean hasCurrentChanges = ((FileData) dataModel).getHasCurrentChanges();
					if (!hasCurrentChanges && !isFileViewedOnce) { //file does not exist for sure
						getContents();
						isFileViewedOnce = FileUtils.showFile(dataModel);
					} else {
						File file = new File(dataModel.getFilePath());
						if (!file.exists()) { //file does not exist
							getContents();
						}
						isFileViewedOnce = FileUtils.showFile(dataModel);
					}
				}
			}

			private void getContents()
			{
				//go to the server to get the changes
				JComponent comp = RunFocusTracker.getCurrentComponent();
				if (comp instanceof ParameterRunPanel) {
					ParameterRuntime p = (ParameterRuntime) ((ParameterRunPanel) comp).getDomeObject();
					ModelObjectScope scope = p.getScope();
					if (scope instanceof ModelInterfaceRuntimeClient) {
						ServerConnection con = ((ModelInterfaceRuntimeClient) scope).getServerConnection();
						CompoundId cId = ((ModelInterfaceRuntimeClient) scope).getRuntimeId();
						cId.setDataObjectStaticId(p.getId());
						Object fileCon = RuntimeFunctionsClient.getFileContent(con, cId);
						((FileData) dataModel).setFileValue(FileRunPanel.this, fileCon);
					}
				}
			}
		};
	}

	protected PropertyChangeListener createPropertyListener()
	{
		return new RunPropertyChangeListener();
	}

	protected class RunPropertyChangeListener implements PropertyChangeListener
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
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile.VALUE) && ((FileData) dataModel).isIfChecked()) {
				FileUtils.showFile(dataModel);
			}

		}
	}

	private boolean isFileTypeComboBoxEnabled()
	{
		if (dataModel.getFilePath().trim().equals("")) return true;
		return false;
	}

}
