package mit.cadlab.dome3.api;

import java.util.Date;

/**
 * User: Sangmok Han
 * Date: 2005. 1. 23.
 */
public class DomePlayspace {
    private String playspaceId;
    private String playspaceName;
    private String description;
    private Date lastModified;
    private int version;
    private DomeConnection domeConn;

    public DomePlayspace(String playspaceId, String playspaceName, String description, Date lastModified, int version, DomeConnection domeConn) {
        this.playspaceId = playspaceId;
        this.playspaceName = playspaceName;
        this.description = description;
        this.lastModified = lastModified;
        this.version = version;
        this.domeConn = domeConn;
    }

    public String getPlayspaceId() {
        return playspaceId;
    }

    public String getPlayspaceName() {
        return playspaceName;
    }

    public String getDescription() {
        return description;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public int getVersion() {
        return version;
    }

    public String toString() {
        return "[DOME PLAYSPACE: '" + playspaceName + "' (ID = " + playspaceId + "), description = " + description + ", last modified at = " + lastModified + ", version = " + version + "]";
    }

    public RuntimePlayspace createRuntimePlayspace() {
        return new RuntimePlayspace(this, domeConn);
    }
}
