package mit.cadlab.dome3.api.build;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;

public class FileParameter extends Parameter{
    public FileParameter(ConcreteParameter concreteParam) {
        super(concreteParam);
    }

    public void setPath(String filepath) {
        ((FileData) getDataObject()).setFilePath(filepath);
    }

    public void setType(String filetype) {
        ((FileData) getDataObject()).setFileType(filetype);
    }

    public String getPath() {
        return  ((FileData) getDataObject()).getFilePath();
    }

    public String getType() {
        return ((FileData) getDataObject()).getFileType();
    }

    public String getDataType() {
        return DataTypeConstants.FILE;
    }
}
