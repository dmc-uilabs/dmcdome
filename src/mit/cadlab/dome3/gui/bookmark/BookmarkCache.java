package mit.cadlab.dome3.gui.bookmark;

import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.swing.DialogFactory;
import mit.cadlab.dome3.DomeClientApplication;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Aug 13, 2003
 * Time: 2:47:00 PM
 * To change this template use Options | File Templates.
 */
public class BookmarkCache {

    public static String bookmarksFilename = "bookmarks.xml";
    public static final String ANALYSIS_Interface = "AnalysisTool interface";
    public static final String Project_Interface = "Project interface";
    public static final String Server = "server";
    private static DArrayList cachedbookmarksInfo = null;
    private static JDialog bookmarkmanager = null;
    public static String infoSeperator = "$$";

	public static boolean useLocalBookmarkFile = true;
    /**
     *  should be called in dome init !!!!
     */
    public static void loadBookmarks() {
        cachedbookmarksInfo = new DArrayList();
        File bookmarksFile = new File(bookmarksFilename);
        boolean existbefore = bookmarksFile.exists();
        if (!existbefore && useLocalBookmarkFile) {
            try {
                bookmarksFile.createNewFile();
                System.out.println(bookmarksFile.getName() + " created");
                Document doc = DocumentFactory.getInstance().createDocument();
                Element bksElement = DocumentHelper.createElement("/bookmarkFolders/");
                doc.add(bksElement);
                XMLUtils.writeToFile(doc, bookmarksFile.getPath());
                BookmarkCache.loadBookmarks();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            try {
                //get from bookmark info bookmark.xml
                Element xmlElement = getBookmarkXml(bookmarksFilename);
                if (xmlElement != null) {
                    List bookmarkFds = xmlElement.selectNodes("/bookmarkFolders/" + BookmarkFolder.XML_TAG);
                    for (Iterator iter = bookmarkFds.iterator(); iter.hasNext();) {
                        Element element = (Element) iter.next();
                        if (element != null) {//loadFolders;
                            BookmarkFolder bookmarkfd = new BookmarkFolder(element);
                            cachedbookmarksInfo.add(bookmarkfd);
                            if (!bookmarkfd.getFoldername().equals("root"))
                                RunMenus.BookMarksMenu.add(bookmarkfd.getMenu());
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("Error reading bookmarks file: " + bookmarksFile.getName());
            }
        }
    }

	public static Element getBookmarkXml(String bookmarkFileName) {
		if (!useLocalBookmarkFile) {
			String xmlString = null;
			try {
				InputStream in = BookmarkCache.class.getClassLoader().getResourceAsStream("/" + bookmarkFileName);
				if (in == null) {
					System.err.println("bookmarks file not found: " + bookmarkFileName);
					return null;
				}

				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				xmlString = new String(buffer);
				return XMLUtils.stringToXmlElement(xmlString);
			}
			catch (java.io.IOException e) {
				System.err.println("Error reading bookmarks file: " + e); // should direct to client
				return null;
			}
		} else {
			return XMLUtils.fileToXmlElement(bookmarkFileName);
		}
	}

    public static void saveBookmarks() {
        File bookmarksFile = new File(bookmarksFilename);
        Document doc = DocumentFactory.getInstance().createDocument();
        Element bksElement = DocumentHelper.createElement("bookmarkFolders");
        doc.add(bksElement);
        for (Iterator iter = cachedbookmarksInfo.iterator(); iter.hasNext();) {
            Object bookmarkfd = iter.next();
            if (bookmarkfd instanceof BookmarkFolder) {
                Element element = ((BookmarkFolder) bookmarkfd).toXmlElement();
                bksElement.add(element);
            }
        }
        try {
            XMLUtils.writeToFile(doc, bookmarksFile.getPath());
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }


    public static void addBookmark(String bookmarktype, String id, String aliasname, String serverUrl, String path, String loginType, String loginName) {
        if (id.indexOf(CompoundId.MAJORIDSEPARATOR) != -1)
            id = id.substring(0, id.indexOf(CompoundId.MAJORIDSEPARATOR));

        BookmarkInfo bookmark = new BookmarkInfo(bookmarktype, id, serverUrl, path, aliasname, loginType, loginName); //default folder
        getFolder(bookmark.getFolder()).addBookmark(bookmark);
    }

    public static void addBookmark(String bookmarktype, String foldername, String id, String aliasname, String serverUrl, String path, String loginType, String loginName) {
        if (id.indexOf(CompoundId.MAJORIDSEPARATOR) != -1)
            id = id.substring(0, id.indexOf(CompoundId.MAJORIDSEPARATOR));

        BookmarkInfo bookmark = new BookmarkInfo(bookmarktype, foldername, id, serverUrl, path, aliasname, loginType, loginName); //some folder
        getFolder(foldername).addBookmark(bookmark);
    }


    public static void RenameBookmark(BookmarkInfo bmk, String newname) {
        bmk.setAliasname(newname);
    }

    public static void RenameBookmarkFolder(BookmarkFolder bmkfd, String newname) {
        bmkfd.setFoldername(newname);
    }

    public static void RemoveBookmark(BookmarkInfo bmk) {
        if (!containsFolder(bmk.getFolder()))
            return;
        else
            getFolder(bmk.getFolder()).removeBookmark(bmk);
    }

    public static void RemoveBookmarkFolder(BookmarkFolder bmkfd) {
        if (bmkfd.getFoldername().equals("root")) return;//can't remove root folder
        cachedbookmarksInfo.remove(bmkfd);
        RunMenus.BookMarksMenu.remove(bmkfd.getMenu());
    }

    public static void moveToFolder(BookmarkInfo bmk, BookmarkFolder bmkfd) {
        RemoveBookmark(bmk);
        bmkfd.addBookmark(bmk);
    }

    public static BookmarkFolder creatFolder(String foldername) {
        BookmarkFolder newFolder = new BookmarkFolder(foldername);
        cachedbookmarksInfo.add(newFolder);
        RunMenus.BookMarksMenu.add(newFolder.getMenu());
        return newFolder;
    }

    /**
     * Get the list of bookmarks saved
     * @return an  list of the currently cached bookmarks
     */
    public static DArrayList getBookmarks() {
        if (cachedbookmarksInfo == null)
            loadBookmarks();
        return cachedbookmarksInfo;
    }


    public static BookmarkFolder getFolder(String foldername) {
        for (Iterator iter = cachedbookmarksInfo.iterator(); iter.hasNext();) {
            BookmarkFolder bmkfd = (BookmarkFolder) iter.next();
            if (bmkfd.getFoldername().equals(foldername)) return bmkfd;
        }
        //not found
        //create one
        BookmarkFolder newFolder = new BookmarkFolder(foldername);
        cachedbookmarksInfo.add(newFolder);
        if (!foldername.equals("root"))
            RunMenus.BookMarksMenu.add(newFolder.getMenu());
        return newFolder;
    }

    public static boolean containsFolder(String foldername) {
        for (Iterator iter = cachedbookmarksInfo.iterator(); iter.hasNext();) {
            BookmarkFolder bmkfd = (BookmarkFolder) iter.next();
            if (bmkfd.getFoldername().equals(foldername)) return true;
        }
        return false;
    }

    public static void showManager() {
        if (bookmarkmanager != null) {
            bookmarkmanager.show();
            return;
        } else {
            //create
            BookmarkManager window = new BookmarkManager();
            window.setSelectionName("");
            window.setSelectionUrl("");
            bookmarkmanager = DialogFactory.createDialog(null, "Browse for folder", window, false, true);
            bookmarkmanager.setLocation(new Point(0, DomeClientApplication.getBottomCoordinate()));
            bookmarkmanager.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            bookmarkmanager.show();
        }
    }
}
