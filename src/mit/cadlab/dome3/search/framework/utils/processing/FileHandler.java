package mit.cadlab.dome3.search.framework.utils.processing;

import mit.cadlab.dome3.integrationwizards.mappingstorage.IModelData;

import java.io.File;

/**
 *
 */
public interface FileHandler {
  public InterfaceData getInstanceData(File f) throws FileHandlerException;
}