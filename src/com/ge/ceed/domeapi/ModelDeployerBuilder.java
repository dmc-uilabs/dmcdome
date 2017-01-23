package com.ge.ceed.domeapi;

import java.io.File;

import mit.cadlab.dome3.api.deploy.ModelDeployer;
import pemsweb.IntegrationContext;

// no longer needed, as IntegrationContext is no longer the vehicle for getting an integration model
@Deprecated
public class ModelDeployerBuilder {
	
	private File workingdir;

	ModelDeployerBuilder(File workingDirectory) {
		this.workingdir = workingDirectory;
	}
	
	public ModelDeployer build(String sessionID, String modelName, IntegrationContext context) {
		if (context==null) {
			throw new IllegalArgumentException("Parameter 'context' cannot be null.");
		}
		String modelFilePath = null;
		{
			// save the document to a file
			File moddir = new File(workingdir, sessionID + "/model");
			moddir.mkdirs();
			File modelPathName = new File(moddir, modelName);
			modelFilePath = context.saveAsCatalogModel(modelPathName.getAbsolutePath(), modelName);
		}
		ModelDeployer deployer = new ModelDeployer(modelFilePath);
		return deployer;
	}

}
