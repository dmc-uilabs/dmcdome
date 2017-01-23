//Plugin.java
package mit.cadlab.dome3.plugin;

import java.util.List;

public interface Plugin
{

	public void createModel();

	public void deleteModel();

	public boolean isModelLoaded();

	public void loadModel();

	public void executeBeforeInput();

	/**
	 * This method only exists for test programs which call execute directly.
	 * Real execution of models should use the form of execute which
	 * gets the list of affected output parameters.
	 */
	public void execute();

	public void execute(List affectedOutputParams);

	public void executeAfterOutput();

	public void unloadModel();

}
