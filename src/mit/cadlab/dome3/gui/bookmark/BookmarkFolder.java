package mit.cadlab.dome3.gui.bookmark;

import mit.cadlab.dome3.gui.mode.run.RunMenus;
import mit.cadlab.dome3.swing.MenuUtils;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;
import mit.cadlab.dome3.util.DomePropertyChangeSupport;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Feb 3, 2004
 * Time: 6:34:13 PM
 * To change this template use Options | File Templates.
 */
public class BookmarkFolder implements XMLSupport {
    public static final String XML_TAG = "bookmark_folder";
    String foldername = "root"; //default
    protected DArrayList bookmarks = new DArrayList();
    protected JMenu menu;
    private DomePropertyChangeSupport listeners = new DomePropertyChangeSupport(this);

    public BookmarkFolder(String foldername) {
        this.foldername = foldername;
        menu = MenuUtils.makeMenu(foldername.equals("root") ? "Bookmarks" : foldername);
        menu.setIcon(BookmarkManager.open_Icon);
        bookmarks.addDListListener(new DListListener() {
            //DListListener Interface
            public void intervalChanged(DListEvent e) {
             };

            public void intervalAdded(DListEvent e) {
                listeners.firePropertyChange("bookmarks added",e.getIndices(),e.getItems());
            };

            public void intervalRemoved(DListEvent e) {
                 listeners.firePropertyChange("bookmarks removed",e.getIndices(),e.getItems());
            };

            public void itemsRemoved(DListEvent e) {
            };

            public void itemsReplaced(DListEvent e) {
            }
        });
    }

    public BookmarkFolder(Element xmlElement) {
	    this(xmlElement, true);
    }

	public BookmarkFolder(Element xmlElement, boolean makeGuiElements) {
         XMLUtils.makeRootElement(xmlElement);
        foldername = xmlElement.elementText("foldername");
		if (makeGuiElements) {
			menu = MenuUtils.makeMenu(foldername.equals("root") ? "Bookmarks" : foldername);
			menu.setIcon(BookmarkManager.open_Icon);
		}
        List bmksXml = xmlElement.selectNodes("/" + getXmlTag() + "/bookmarks/" + BookmarkInfo.XML_TAG);
        BookmarkInfo bmk;
        Element element;
        for (Iterator iter = bmksXml.iterator(); iter.hasNext();) {
            element = (Element) iter.next();
            bmk = new BookmarkInfo(element, makeGuiElements);
            bookmarks.add(bmk);
	        if (makeGuiElements) {
		        if (foldername.equals("root"))
			        RunMenus.BookMarksMenu.add(bmk.getMenuItem());
		        else
			        menu.add(bmk.getMenuItem());
	        }
        }
         bookmarks.addDListListener(new DListListener() {
            //DListListener Interface
            public void intervalChanged(DListEvent e) {
                //update tree
            };

            public void intervalAdded(DListEvent e) {
               listeners.firePropertyChange("bookmarks added",e.getIndices(),e.getItems());
            };

            public void intervalRemoved(DListEvent e) {
               listeners.firePropertyChange("bookmarks removed",e.getIndices(),e.getItems());
            };

            public void itemsRemoved(DListEvent e) {
            };

            public void itemsReplaced(DListEvent e) {
            }
        });
    }

    public Element toXmlElement() {
        Element xml = DocumentHelper.createElement(XML_TAG);
        xml.addElement("foldername").addText(foldername);
        XMLUtils.addCollection(xml, "bookmarks", bookmarks);
        ;
        return xml;
    }

    public void addDListListener(DListListener l) {
        bookmarks.addDListListener(l);
    }

    public DArrayList getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(DArrayList bookmarks) {
        this.bookmarks = bookmarks;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
        for (Iterator iter = bookmarks.iterator(); iter.hasNext();) {
            BookmarkInfo bmk = (BookmarkInfo) (iter.next());
            bmk.setFolder(foldername);
        }
        menu.setText(foldername);
    }

    public void addBookmark(BookmarkInfo bmk) {
        bookmarks.add(bmk);
        bmk.setFolder(this.getFoldername());
        if (foldername.equals("root"))
            RunMenus.BookMarksMenu.add(bmk.getMenuItem());
        else
            menu.add(bmk.getMenuItem());
    }

    public void removeBookmark(BookmarkInfo bmk) {
        bookmarks.remove(bmk);
        if (foldername.equals("root"))
            RunMenus.BookMarksMenu.remove(bmk.getMenuItem());
        else
            menu.remove(bmk.getMenuItem());
    }

    public String toString() {
        return foldername;
    }

    public String getXmlTag() {
        return XML_TAG;
    }

    public JMenu getMenu() {
        return menu;
    }

     public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}

     public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(listener);
	}
}
