package mit.cadlab.dome3.gui.bookmark;

import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.util.xml.XMLSupport;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Aug 13, 2003
 * Time: 3:19:11 PM
 * To change this template use Options | File Templates.
 */
public class BookmarkInfo implements XMLSupport {
    public static final String XML_TAG = "bookmark_info";

    String id = "";
    String folder = "root";
    String serverURL = "";
    String aliasname = "";
    String loginType = "";
    String loginName = "";
    String path = "";
	String comment = "";
    String type = DomeFile.INTERFACE_TYPE;    //default is interface
    protected JMenuItem menu;

    public BookmarkInfo(String _type, String _id, String _serverUrl, String _path, String _aliasname, String _loginType, String _loginName) {
        this.type = _type;
        this.id = _id;
        this.serverURL = _serverUrl;
        this.path = _path;
        this.aliasname = _aliasname;
        this.loginType = _loginType;
        this.loginName = _loginName;
        menu = MenuUtils.makeMenuItem(aliasname);
        if (type.equals(DomeFile.PROJECT_TYPE)) {
            menu.setIcon(BookmarkManager.ProjectIcon);
        } else if (type.equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
            menu.setIcon(BookmarkManager.AnaylsisIcon);
        } else if (type.equals(DomeFile.PLAYSPACE_TYPE)) {
            menu.setIcon(BookmarkManager.PlayspaceIcon);
        } else if (type.equals(BookmarkCache.ANALYSIS_Interface)) {
            menu.setIcon(BookmarkManager.InterfaceIcon);
        } else if (type.equals(DomeFile.INTERFACE_TYPE) || type.equals(BookmarkCache.Project_Interface)) {
            menu.setIcon(BookmarkManager.InterfaceIcon);
        } else if (type.equals(BookmarkCache.Server)) {
            menu.setIcon(BookmarkManager.ServerIcon);
        }

        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBookmark();
            }
        });
    }

    public BookmarkInfo(String _id, String _serverUrl, String _path, String _aliasname, String _loginType, String _loginName) {
        this.id = _id;
        this.serverURL = _serverUrl;
        this.path = _path;
        this.aliasname = _aliasname;
        this.loginType = _loginType;
        this.loginName = _loginName;
        menu = MenuUtils.makeMenuItem(aliasname);
        if (type.equals(DomeFile.PROJECT_TYPE)) {
            menu.setIcon(BookmarkManager.ProjectIcon);
        } else if (type.equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
            menu.setIcon(BookmarkManager.AnaylsisIcon);
        } else if (type.equals(DomeFile.PLAYSPACE_TYPE)) {
            menu.setIcon(BookmarkManager.PlayspaceIcon);
        } else if (type.equals(BookmarkCache.ANALYSIS_Interface)) {
            menu.setIcon(BookmarkManager.InterfaceIcon);
        } else if (type.equals(DomeFile.INTERFACE_TYPE) || type.equals(BookmarkCache.Project_Interface)) {
            menu.setIcon(BookmarkManager.InterfaceIcon);
        } else if (type.equals(BookmarkCache.Server)) {
            menu.setIcon(BookmarkManager.ServerIcon);
        }
        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBookmark();
            }
        });
    }


    public BookmarkInfo(String _type, String _folder, String _id, String _serverUrl, String _path, String _aliasname, String _loginType, String _loginName) {
        this.type = _type;
        this.folder = _folder;
        this.id = _id;
        this.serverURL = _serverUrl;
        this.path = _path;
        this.aliasname = _aliasname;
        this.loginType = _loginType;
        this.loginName = _loginName;
        menu = MenuUtils.makeMenuItem(aliasname);
        if (type.equals(DomeFile.PROJECT_TYPE)) {
            menu.setIcon(BookmarkManager.ProjectIcon);
        } else if (type.equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
            menu.setIcon(BookmarkManager.AnaylsisIcon);
        } else if (type.equals(DomeFile.PLAYSPACE_TYPE)) {
            menu.setIcon(BookmarkManager.PlayspaceIcon);
        } else if (type.equals(BookmarkCache.ANALYSIS_Interface)) {
            menu.setIcon(BookmarkManager.InterfaceIcon);
        } else if (type.equals(DomeFile.INTERFACE_TYPE) || type.equals(BookmarkCache.Project_Interface)) {
            menu.setIcon(BookmarkManager.InterfaceIcon);
        } else if (type.equals(BookmarkCache.Server)) {
            menu.setIcon(BookmarkManager.ServerIcon);
        }
        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBookmark();
            }
        });
    }

	public BookmarkInfo(Element xmlElement)
	{
		this(xmlElement, true);
	}

	public BookmarkInfo(Element xmlElement, boolean makeGuiElements)
	{
		XMLUtils.makeRootElement(xmlElement);
		type = xmlElement.elementText("type");
		folder = xmlElement.elementText("folder");
		id = xmlElement.elementText("id");
		serverURL = xmlElement.elementText("serverUrl");
		path = xmlElement.elementText("path");
		aliasname = xmlElement.elementText("aliasName");
		setComment(xmlElement.elementText("comment"));
		loginType = xmlElement.elementText("loginType");
		loginName = xmlElement.elementText("loginName");
		if (makeGuiElements) {
			menu = MenuUtils.makeMenuItem(aliasname);
			if (type.equals(DomeFile.PROJECT_TYPE)) {
				menu.setIcon(BookmarkManager.ProjectIcon);
			}
			else if (type.equals(DomeFile.ANALYSIS_TOOL_TYPE)) {
				menu.setIcon(BookmarkManager.AnaylsisIcon);
			}
			else if (type.equals(DomeFile.PLAYSPACE_TYPE)) {
				menu.setIcon(BookmarkManager.PlayspaceIcon);
			}
			else if (type.equals(BookmarkCache.ANALYSIS_Interface)) {
				menu.setIcon(BookmarkManager.InterfaceIcon);
			}
			else if (type.equals(DomeFile.INTERFACE_TYPE) || type.equals(BookmarkCache.Project_Interface)) {
				menu.setIcon(BookmarkManager.InterfaceIcon);
			}
			else if (type.equals(BookmarkCache.Server)) {
				menu.setIcon(BookmarkManager.ServerIcon);
			}
			menu.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					goBookmark();
				}
			});
		}
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Element toXmlElement() {
        Element xml = DocumentHelper.createElement(XML_TAG);
        xml.addElement("type").addText(type);
        xml.addElement("folder").addText(folder);
        xml.addElement("id").addText(id);
        xml.addElement("serverUrl").addText(serverURL);
        xml.addElement("path").addText(path);
        xml.addElement("aliasName").addText(aliasname);
	    xml.addElement("comment").addText(comment);
        xml.addElement("loginType").addText(loginType);
        xml.addElement("loginName").addText(loginName);
        return xml;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAliasname() {
        return aliasname;
    }

    public void setAliasname(String aliasname) {
        this.aliasname = aliasname;
        menu.setText(aliasname);
    }

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		if (comment == null)
			return;
		this.comment = comment;
	}

    public String getXmlTag() {
        return XML_TAG;
    }

    public String getInfo() {
        if (type.equals(BookmarkCache.Server))
            return serverURL + " login in as " + loginName;
        return serverURL + path;
    }

    public void goBookmark() {
        RunMode.goBookmark(type, id, serverURL, loginType, loginName, aliasname);
    }


    public String toString() {
        return aliasname;
    }

    public JMenuItem getMenuItem() {
        return menu;
    }


}
