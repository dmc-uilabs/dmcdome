package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 9.
 */
public class GroovyDelimiterList extends DelimiterList {
    /* keyword sets */
    public static final String[] KEYWORD_1_LIST = { "def", "abstract", "break", "byte", "boolean", "catch", "case", "class", "char", "continue", "default", "double", "do", "else", "extends", "false", "final", "float", "for", "finally", "if", "import", "implements", "int", "interface", "instanceof", "long", "length", "native", "new", "null", "package", "private", "protected", "public", "return", "switch", "synchronized", "short", "static", "super", "try", "true", "this", "throw", "throws", "threadsafe", "transient", "void", "while" };
    //public static final String[] KEYWORD_2_LIST = { "AbstractMethodError", "AccessException", "Acl", "AclEntry", "AclNotFoundException", "ActionEvent", "ActionListener", "Adjustable", "AdjustmentEvent", "AdjustmentListener", "Adler32", "AlreadyBoundException", "Applet", "AppletContext", "AppletStub", "AreaAveragingScaleFilter", "ArithmeticException", "Array", "ArrayIndexOutOfBoundsException", "ArrayStoreException", "AudioClip", "AWTError", "AWTEvent", "AWTEventMulticaster", "AWTException", "BeanDescriptor", "BeanInfo", "Beans", "BigDecimal", "BigInteger", "BindException", "BitSet", "Boolean", "BorderLayout", "BreakIterator", "BufferedInputStream", "BufferedOutputStream", "BufferedReader", "BufferedWriter", "Button", "ButtonPeer", "Byte", "ByteArrayInputStream", "ByteArrayOutputStream", "Calendar", "CallableStatement", "Canvas", "CanvasPeer", "Certificate", "Character", "CharacterIterator", "CharArrayReader", "CharArrayWriter", "CharConversionException", "Checkbox", "CheckboxGroup", "CheckboxMenuItem", "CheckboxMenuItemPeer", "CheckboxPeer", "CheckedInputStream", "CheckedOutputStream", "Checksum", "Choice", "ChoiceFormat", "ChoicePeer", "Class", "ClassCastException", "ClassCircularityError", "ClassFormatError", "ClassLoader", "ClassNotFoundException", "Clipboard", "ClipboardOwner", "Cloneable", "CloneNotSupportedException", "CollationElementIterator", "CollationKey", "Collator", "Color", "ColorModel", "Compiler", "Component", "ComponentAdapter", "ComponentEvent", "ComponentListener", "ComponentPeer", "ConnectException", "ConnectIOException", "Connection", "Constructor", "Container", "ContainerAdapter", "ContainerEvent", "ContainerListener", "ContainerPeer", "ContentHandler", "ContentHandlerFactory", "CRC32", "CropImageFilter", "Cursor", "Customizer", "CardLayout", "DatabaseMetaData", "DataFlavor", "DataFormatException", "DatagramPacket", "DatagramSocket", "DatagramSocketImpl", "DataInput", "DataInputStream", "DataOutput", "DataOutputStream", "DataTruncation", "Date", "DateFormat", "DateFormatSymbols", "DecimalFormat", "DecimalFormatSymbols", "Deflater", "DeflaterOutputStream", "DGC", "Dialog", "DialogPeer", "Dictionary", "DigestException", "DigestInputStream", "DigestOutputStream", "Dimension", "DirectColorModel", "Double", "Driver", "DriverManager", "DriverPropertyInfo", "DSAKey", "DSAKeyPairGenerator", "DSAParams", "DSAPrivateKey", "DSAPublicKey", "EmptyStackException", "Enumeration", "EOFException", "Error", "Event", "EventListener", "EventObject", "EventQueue", "EventSetDescriptor", "Exception", "ExceptionInInitializerError", "ExportException", "FeatureDescriptor", "Field", "FieldPosition", "File", "FileDescriptor", "FileDialog", "FileDialogPeer", "FileInputStream", "FilenameFilter", "FileNameMap", "FileNotFoundException", "FileOutputStream", "FileReader", "FileWriter", "FilteredImageSource", "FilterInputStream", "FilterOutputStream", "FilterReader", "FilterWriter", "Float", "FlowLayout", "FocusAdapter", "FocusEvent", "FocusListener", "Font", "FontMetrics", "FontPeer", "Format", "Frame", "FramePeer", "Graphics", "GregorianCalendar", "GridBagConstraints", "GridBagLayout", "GridLayout", "Group", "GZIPInputStream", "GZIPOutputStream", "Hashtable", "HttpURLConnection", "Identity", "IdentityScope", "IllegalAccessError", "IllegalAccessException", "IllegalArgumentException", "IllegalComponentStateException", "IllegalMonitorStateException", "IllegalStateException", "IllegalThreadStateException", "Image", "ImageConsumer", "ImageFilter", "ImageObserver", "ImageProducer", "IncompatibleClassChangeError", "IndexColorModel", "IndexedPropertyDescriptor", "IndexOutOfBoundsException", "InetAddress", "Inflater", "InflaterInputStream", "InputEvent", "InputStream", "InputStreamReader", "Insets", "InstantiationError", "InstantiationException", "Integer", "InternalError", "InterruptedException", "InterruptedIOException", "IntrospectionException", "Introspector", "InvalidClassException", "InvalidKeyException", "InvalidObjectException", "InvalidParameterException", "InvocationTargetException", "IOException", "ItemEvent", "ItemListener", "ItemSelectable", "Key", "KeyAdapter", "KeyEvent", "KeyException", "KeyListener", "KeyManagementException", "KeyPair", "KeyPairGenerator", "Label", "LabelPeer", "LastOwnerException", "LayoutManager", "LayoutManager2", "Lease", "LightweightPeer", "LineNumberInputStream", "LineNumberReader", "LinkageError", "List", "ListPeer", "ListResourceBundle", "LoaderHandler", "Locale", "LocateRegistry", "LogStream", "Long", "MalformedURLException", "MarshalException", "Math", "MediaTracker", "Member", "MemoryImageSource", "Menu", "MenuBar", "MenuBarPeer", "MenuComponent", "MenuComponentPeer", "MenuContainer", "MenuItem", "MenuItemPeer", "MenuPeer", "MenuShortcut", "MessageDigest", "MessageFormat", "Method", "MethodDescriptor", "MissingResourceException", "Modifier", "MouseAdapter", "MouseEvent", "MouseListener", "MouseMotionAdapter", "MouseMotionListener", "MulticastSocket", "Naming", "NegativeArraySizeException", "NoClassDefFoundError", "NoRouteToHostException", "NoSuchAlgorithmException", "NoSuchElementException", "NoSuchFieldError", "NoSuchFieldException", "NoSuchMethodError", "NoSuchMethodException", "NoSuchObjectException", "NoSuchProviderException", "NotActiveException", "NotBoundException", "NotOwnerException", "NotSerializableException", "NullPointerException", "Number", "NumberFormat", "NumberFormatException", "Object", "ObjectInput", "ObjectInputStream", "ObjectInputValidation", "ObjectOutput", "ObjectOutputStream", "ObjectStreamClass", "ObjectStreamException", "ObjID", "Observable", "Observer", "Operation", "OptionalDataException", "OutOfMemoryError", "OutputStream", "OutputStreamWriter", "Owner", "PaintEvent", "Panel", "PanelPeer", "ParameterDescriptor", "ParseException", "ParsePosition", "Permission", "PipedInputStream", "PipedOutputStream", "PipedReader", "PipedWriter", "PixelGrabber", "Point", "Polygon", "PopupMenu", "PopupMenuPeer", "PreparedStatement", "Principal", "PrintGraphics", "PrintJob", "PrintStream", "PrintWriter", "PrivateKey", "Process", "Properties", "PropertyChangeEvent", "PropertyChangeListener", "PropertyChangeSupport", "PropertyDescriptor", "PropertyEditor", "PropertyEditorManager", "PropertyEditorSupport", "PropertyResourceBundle", "PropertyVetoException", "ProtocolException", "Provider", "ProviderException", "PublicKey", "PushbackInputStream", "PushbackReader", "Random", "RandomAccessFile", "Reader", "Rectangle", "Registry", "RegistryHandler", "Remote", "RemoteCall", "RemoteException", "RemoteObject", "RemoteRef", "RemoteServer", "RemoteStub", "ReplicateScaleFilter", "ResourceBundle", "ResultSet", "ResultSetMetaData", "RGBImageFilter", "RMIClassLoader", "RMIFailureHandler", "RMISecurityException", "RMISecurityManager", "RMISocketFactory", "RuleBasedCollator", "Runnable", "Runtime", "RuntimeException", "Scrollbar", "ScrollbarPeer", "ScrollPane", "ScrollPanePeer", "SecureRandom", "Security", "SecurityException", "SecurityManager", "SequenceInputStream", "Serializable", "ServerCloneException", "ServerError", "ServerException", "ServerNotActiveException", "ServerRef", "ServerRuntimeException", "ServerSocket", "Shape", "Short", "Signature", "SignatureException", "Signer", "SimpleBeanInfo", "SimpleDateFormat", "SimpleTimeZone", "Skeleton", "SkeletonMismatchException", "SkeletonNotFoundException", "Socket", "SocketException", "SocketImpl", "SocketImplFactory", "SocketSecurityException", "SQLException", "SQLWarning", "Stack", "StackOverflowError", "Statement", "StreamCorruptedException", "StreamTokenizer", "String", "StringBuffer", "StringBufferInputStream", "StringCharacterIterator", "StringIndexOutOfBoundsException", "StringReader", "StringSelection", "StringTokenizer", "StringWriter", "StubNotFoundException", "SyncFailedException", "System", "SystemColor", "TextArea", "TextAreaPeer", "TextComponent", "TextComponentPeer", "TextEvent", "TextField", "TextFieldPeer", "TextListener", "Thread", "ThreadDeath", "ThreadGroup", "Throwable", "Time", "Timestamp", "TimeZone", "Toolkit", "TooManyListenersException", "Transferable", "Types", "UID", "UnexpectedException", "UnicastRemoteObject", "UnknownError", "UnknownHostException", "UnknownServiceException", "UnmarshalException", "Unreferenced", "UnsatisfiedLinkError", "UnsupportedEncodingException", "UnsupportedFlavorException", "URL", "URLConnection", "URLEncoder", "URLStreamHandler", "URLStreamHandlerFactory", "UTFDataFormatException", "Vector", "VerifyError", "VetoableChangeListener", "VetoableChangeSupport", "VirtualMachineError", "Visibility", "VMID", "Void", "Window", "WindowAdapter", "WindowEvent", "WindowListener", "WindowPeer", "WriteAbortedException", "Writer", "ZipEntry", "ZipException", "ZipFile", "ZipInputStream", "ZipOutputStream" };
    public List KEYWORD_2_LIST = new ArrayList();
    public List KEYWORD_3_LIST = new ArrayList();

    public GroovyDelimiterList(TableModel iParamModel, TableModel oParamModel) {
        super();

        if (iParamModel != null && oParamModel != null) {
            iParamModel.addTableModelListener(new TableModelListenerForKeywordList(iParamModel, KEYWORD_2_LIST, this));
            oParamModel.addTableModelListener(new TableModelListenerForKeywordList(oParamModel, KEYWORD_3_LIST, this));
        }

        //this.addDelimiter(StyleUtil.COMMENTED_STYLE, "(?:\\/\\*(?:\\s*|.*?)*\\*\\/)|(?:\\/\\/.*)");
        this.addDelimiter(StyleUtil.COMMENTED_STYLE, "(?:\\/\\*(?:.*?)*\\*\\/)|(?:\\/\\/.*)");
        this.addDelimiter(StyleUtil.NUMBER_STYLE, "\\b\\d+(?:.\\d+)?\\b");
        this.addDelimiter(StyleUtil.QUOTED_STRING_STYLE, "\"(?:\\\\.|[^\"\\\\])*\"");
        this.addDelimiter(StyleUtil.KEYWORD_BASED_STYLE, "\\b[a-zA-Z_0-9]+\\b");
    }

    public List getKeywordList1() { return Arrays.asList(KEYWORD_1_LIST); };
    public List getKeywordList2() { return KEYWORD_2_LIST; };
    public List getKeywordList3() { return KEYWORD_3_LIST; };

    public String getLangName() {
        return StyleUtil.GROOVY_LANG;
    }
}


class TableModelListenerForKeywordList implements TableModelListener {

    private TableModel paramModel;
    private List keywordList;
    private DelimiterList delimList;


    TableModelListenerForKeywordList(TableModel paramModel, List keywordList, DelimiterList delimList) {
        this.paramModel = paramModel;
        this.keywordList = keywordList;
        this.delimList = delimList;
    }

    public void tableChanged(TableModelEvent event) {
        synchronized (keywordList) {
            if (paramModel.getRowCount() == 0) {
                // when removing all parameters (for resetting)
                keywordList.clear();
            } else {
                // when adding or modiftying parameters
                if (event.getColumn() == 0) {
                    keywordList.clear();
                    for (int i = 0; i < paramModel.getRowCount(); i++) {
                        String paramName = (String) paramModel.getValueAt(i, 0);
                        if (! "".equals(paramName) && paramName != null) {
                            keywordList.add(paramName);
                        }
                    }
                    delimList.getColorizedDocument().highlightSyntax();
                }
            }
        }
    }
}