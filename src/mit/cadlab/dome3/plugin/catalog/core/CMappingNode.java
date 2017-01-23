package mit.cadlab.dome3.plugin.catalog.core;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 10.
 */
public class CMappingNode {
    private String mappedParameterName;
    private int status;
    private CMapping mapping;

    public CMappingNode(String mappedParameterName, CMapping mapping) {
        this.mappedParameterName = mappedParameterName;
        this.mapping = mapping;
        this.status = CConstant.UNASSIGNED_STATUS;
    }

    public boolean isInputNode() {
        /* since this node is either input or output, if this is not output node of parent mapping, it should be input */
        return ! (mapping.getOutputNode() == this);
    }

    public String getMappedParameterName() {
        return mappedParameterName;
    }

    public void setMappedParameterName(String mappedParameterName) {
        this.mappedParameterName = mappedParameterName;
    }

    public String toString() {
        return "[node:mapped=" + mappedParameterName +", isinput=" + isInputNode() +", status=" + CConstant.getStatusName(status) + "]";
    }

    public boolean isConsistent() {
        return (status != CConstant.RED_STATUS);
    }

    public void toGreenStatus() {
        this.status = CConstant.GREEN_STATUS;
    }

    public void toWhiteStatus() {
        this.status = CConstant.WHITE_STATUS;
    }

    public void toRedStatus() {
        this.status = CConstant.RED_STATUS;
    }

    public int getStatus() {
        return status;
    }

    public CMapping getMapping() {
        return mapping;
    }

    public void setStatus(int status) {
       this.status = status;
    }
}
