/*****************************************************************************
 *                        Web3d.org Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

import org.j3d.ui.navigation.ViewpointSelectionListener;
import org.j3d.ui.navigation.ViewpointData;
import org.web3d.vrml.renderer.common.input.LinkSelectionListener;
import org.web3d.vrml.renderer.j3d.browser.OverlayHandler;
import org.web3d.vrml.renderer.j3d.browser.VRMLUniverse;
import org.web3d.vrml.renderer.j3d.J3DSceneBuilderFactory;
import org.web3d.vrml.renderer.j3d.nodes.J3DViewpointNodeType;
import org.web3d.vrml.renderer.j3d.nodes.J3DVRMLNode;
import org.web3d.vrml.renderer.j3d.input.J3DSensorManager;
import org.web3d.vrml.renderer.j3d.input.DefaultSensorManager;
import org.web3d.vrml.nodes.VRMLClock;
import org.web3d.vrml.nodes.FrameStateManager;
import org.web3d.vrml.nodes.VRMLLinkNodeType;
import org.web3d.vrml.nodes.VRMLScene;
import org.web3d.vrml.nodes.VRMLViewpointNodeType;
import org.web3d.vrml.nodes.VRMLProtoInstance;
import org.web3d.vrml.nodes.runtime.AWTListenerEventBuffer;
import org.web3d.vrml.nodes.runtime.ScriptManager;
import org.web3d.vrml.nodes.runtime.DefaultScriptManager;
import org.web3d.vrml.nodes.runtime.GeneralisedFrameStateManager;
import org.web3d.vrml.nodes.runtime.RouteManager;
import org.web3d.vrml.nodes.runtime.DefaultRouteManager;
import org.web3d.vrml.nodes.runtime.ListsRouterFactory;
import org.web3d.vrml.nodes.runtime.EventModelEvaluator;
import org.web3d.vrml.nodes.runtime.GeneralisedEventModelEvaluator;
import org.web3d.vrml.nodes.loader.WorldLoaderManager;
import org.web3d.vrml.nodes.loader.ExternalLoadManager;
import org.web3d.vrml.nodes.loader.MemCacheLoadManager;
import org.web3d.vrml.nodes.loader.ScriptLoader;
import org.web3d.vrml.nodes.loader.DefaultScriptLoader;
import org.web3d.vrml.nodes.loader.DefaultWorldLoaderManager;
import org.web3d.vrml.nodes.loader.WorldLoader;
import org.web3d.vrml.parser.VRMLParserFactory;
import org.web3d.vrml.parser.FactoryConfigurationError;
import org.web3d.vrml.scripting.ScriptEngine;
import org.web3d.vrml.scripting.ecmascript.ECMAScriptEngine;
import org.web3d.vrml.scripting.jsai.VRML97ScriptEngine;
import org.web3d.vrml.sav.InputSource;
import org.web3d.vrml.lang.TypeConstants;
import org.web3d.browser.BrowserCore;
import org.web3d.net.protocol.JavascriptResourceFactory;
import org.web3d.net.content.VRMLContentHandlerFactory;
import org.web3d.net.content.VRMLFileNameMap;
import org.web3d.net.resolve.Web3DURNResolver;
import org.ietf.uri.URIResourceStreamFactory;
import org.ietf.uri.URI;
import org.ietf.uri.ContentHandlerFactory;
import org.ietf.uri.FileNameMap;
import org.ietf.uri.URN;

import javax.swing.*;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.View;
import javax.media.j3d.TransformGroup;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;

import mit.cadlab.dome.objectmodel.modelinterface.ModelInterfaceBase;
import mit.cadlab.dome.objectmodel.Parameter;
import mit.cadlab.dome.objectmodel.DataObject;
import mit.cadlab.dome.objectmodel.dataobject.DomeReal;
import mit.cadlab.dome.objectmodel.dataobject.DomeFile;
import mit.cadlab.dome.swing.Templates;

public class SuspensionModelCustomGui extends JPanel
    implements ViewpointSelectionListener,
		LinkSelectionListener,
		OverlayHandler,
		ActionListener {

	protected ModelInterfaceBase iface; // using the base class should allow gui to be used in both build and run
	private JTextField lengthValue;
	private JTextField thicknessValue;
	private JTextField heightValue;
	private JTextField diameterValue;
	private JTextField wallThicknessValue;
	private JTextField suspensionValue;
	private JTextField wheelMassValue;
	private JLabel suspensionUnit;
	private JLabel thicknessUnit;
	private JLabel lengthUnit;
    private JLabel heightUnit;
	private JLabel diameterUnit;
	private JLabel wallThicknessUnit;
    private JLabel wheelMassUnit;

    // Constants for the URN setup

    /** Set this to the install directory that UMEL uses */
    private static final String UMEL_INSTALL_DIR = null;

    /** Set this to the install directory that GEOVRML uses */
    private static final String GEOVRML_INSTALL_DIR = null;
        //"c:/cygwin/home/justin/Xj3D/tests/geovrml";

    /** NSS prefix used by UMEL */
    private static final String UMEL_PREFIX = "umel";

    /** NSS prefix used by GeoVRML */
    private static final String GEOVRML_PREFIX = "geovrml";

	/** The graphics config template that is best to use */
	protected GraphicsConfiguration gfxConfig;

	/** The textfield to read the values from */
	protected JTextField urlTextField;

	/** The open button on the URl panel */
	private JButton openButton;

     /** The universe to place our scene into */
    private VRMLUniverse universe;

    /** Flag to indicate we are in the setup of the scene currently */
    private boolean inSetup;

    /** Mapping of def'd Viewpoints to their real implementation */
    private HashMap viewpointDefMap;

    /** Place for error messages to go */
//    private ConsoleWindow console;

    /** Global clock */
    private VRMLClock clock;

    /** World load manager to help us load files */
    private WorldLoaderManager worldLoader;

    /** The global canvas for rendering */
    private Canvas3D canvas;

    /** The current viewpoint number */
    private int currViewpointNum;

    /** Viewpoint data */
    ViewpointData[] vdata;

    /** Should we try to use the imageLoader */
    private boolean useImageLoader;

    /**
     * Create an instance of the demo class.
     */
    public SuspensionModelCustomGui(ModelInterfaceBase base) {
		this.iface = base;
		this.setLayout(new BorderLayout());
	    GraphicsConfigTemplate3D template = null;
	    try{
		 template = new GraphicsConfigTemplate3D();
	    }
	    catch(Exception e) {
		    e.printStackTrace();
		    System.out.println(e.getMessage());
	    }
		template.setDoubleBuffer(template.REQUIRED);
		GraphicsEnvironment env =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice dev = env.getDefaultScreenDevice();

		gfxConfig = dev.getBestConfiguration(template);

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        useImageLoader = false;

        viewpointDefMap = new HashMap();

        J3DSceneBuilderFactory builder_fac =
            new J3DSceneBuilderFactory(false,
                                       true,
                                       true,
                                       true,
                                       true,
                                       true,
                                       true);

        VRMLParserFactory parser_fac = null;

        try {
            parser_fac = VRMLParserFactory.newVRMLParserFactory();
        } catch(FactoryConfigurationError fce) {
            throw new RuntimeException("Failed to load factory");
        }

        // We also need a canvas to display stuff with and a universe to set
        // the content in.
        canvas = new Canvas3D(gfxConfig);
        //System.out.println("number of texture units: " +  ((Integer)canvas.queryProperties().get("textureUnitStateMax")).intValue());
        View view = new View();
        //view.setMinimumFrameCycleTime(20);
        view.addCanvas3D(canvas);
        view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

        AWTListenerEventBuffer i_buf = new AWTListenerEventBuffer();
        canvas.addMouseListener(i_buf);
        canvas.addMouseMotionListener(i_buf);
        canvas.addKeyListener(i_buf);

        ExternalLoadManager load_manager = new MemCacheLoadManager();
        ScriptLoader script_loader = new DefaultScriptLoader();
        ScriptManager script_manager = new DefaultScriptManager();
        script_manager.setScriptLoader(script_loader);

        FrameStateManager state_manager = new GeneralisedFrameStateManager();

        J3DSensorManager sensor_manager = new DefaultSensorManager();
        sensor_manager.setInputBuffer(i_buf);

        RouteManager route_manager = new DefaultRouteManager();
        route_manager.setRouterFactory(new ListsRouterFactory());

        worldLoader = new DefaultWorldLoaderManager(state_manager);
//        worldLoader.setErrorReporter(console);
        worldLoader.registerBuilderFactory(BrowserCore.JAVA3D_RENDERER,
                                           builder_fac);
        worldLoader.registerParserFactory(BrowserCore.JAVA3D_RENDERER,
                                          parser_fac);

        EventModelEvaluator event_model = new GeneralisedEventModelEvaluator();
        event_model.initialize(script_manager,
                               route_manager,
                               sensor_manager,
                               state_manager,
                               load_manager);
//        event_model.setErrorReporter(console);

        universe = new VRMLUniverse(event_model, this);
        universe.setPrimaryView(view);
        universe.setLinkSelectionListener(this);

        clock = universe.getVRMLClock();

        ScriptEngine jsai = new VRML97ScriptEngine(universe,
                                                   route_manager,
                                                   worldLoader);
//        jsai.setErrorReporter(console);

        ScriptEngine ecma = new ECMAScriptEngine(universe,
                                                 route_manager,
                                                 worldLoader);
//        ecma.setErrorReporter(console);

        script_loader.registerScriptingEngine(jsai);
        script_loader.registerScriptingEngine(ecma);
        universe.setLinkSelectionListener(this);
        setupProperties(universe, worldLoader);

        this.add(makePanel());
    }

	private JPanel makePanel()
	{
		JPanel p = new JPanel();

		JLabel title = Templates.makeLabel("F350 Rear Truck Suspension", Templates.FONT12B);

		JLabel inputTitle1 = Templates.makeLabel("SPRING", Templates.FONT11B);

		Parameter length = getParameterByName("FrontLeafSpringLength");
		JLabel lengthLabel = Templates.makeLabel("length:");
		lengthValue = makeParameterTextField(length, true);
		lengthUnit = Templates.makeLabel(length.getCurrentDataObject().getUnit().toString());
//		lengthUnit = Templates.makeLabel("unit");

/*		Parameter thickness = getParameterByName("FrontLeafSpringThickness");
		JLabel thicknessLabel = Templates.makeLabel("thickness:");
		thicknessValue = new JTextField("0");
		thicknessUnit = Templates.makeLabel(thickness.getCurrentDataObject().getUnit().toString());
        thicknessUnit = Templates.makeLabel("unit");
*/
		Parameter height = getParameterByName("FrontLeafSpringWidth");
		JLabel heightLabel = Templates.makeLabel("width:");
		heightValue = makeParameterTextField(height, true);
		heightUnit = Templates.makeLabel(height.getCurrentDataObject().getUnit().toString());
//        heightUnit = Templates.makeLabel("unit");

		JLabel inputTitle2 = Templates.makeLabel("AXLE", Templates.FONT11B);

		Parameter diameter = getParameterByName("InnerDiameter");
		JLabel diameterLabel = Templates.makeLabel("inner diameter:");
		diameterValue = makeParameterTextField(diameter, true);
		diameterUnit = Templates.makeLabel(diameter.getCurrentDataObject().getUnit().toString());
//		diameterUnit = Templates.makeLabel("unit");

		Parameter wallThickness = getParameterByName("ShaftDiameter");
		JLabel wallThicknessLabel = Templates.makeLabel("shaft daimeter:");
		wallThicknessValue = makeParameterTextField(wallThickness, true);
		wallThicknessUnit = Templates.makeLabel(wallThickness.getCurrentDataObject().getUnit().toString());
//        wallThicknessUnit = Templates.makeLabel("unit");

		JLabel outputTitle = Templates.makeLabel("MASS PROPERTIES", Templates.FONT11B);

		Parameter suspension = getParameterByName("mass");
		JLabel suspensionLabel = Templates.makeLabel("Suspension:");
		suspensionValue = makeParameterTextField(suspension, false);
//		suspensionUnit = Templates.makeLabel(suspension.getCurrentDataObject().getUnit().toString());
		suspensionUnit = Templates.makeLabel("unit");
		suspensionValue.setEditable(false);

/*		Parameter wheelMass = getParameterByName("wheelMass");
		JLabel wheelMassLabel = Templates.makeLabel("Wheel Mass:");
		wheelMassValue = makeParameterTextField(wheelMass, false);
		wheelMassUnit = Templates.makeLabel(wheelMass.getCurrentDataObject().getUnit().toString());
		wheelMassUnit = Templates.makeLabel("unit");
		wheelMassValue.setEditable(false);
*/

		final Parameter vrmlFileParam = getParameterByName("vrml file");
		DomeFile vrmlFile =(DomeFile)vrmlFileParam.getCurrentDataObject();
		vrmlFile.addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent de) {
						if(de.getPropertyName().equals(DataObject.VALUE)) {
							DomeFile vfile = (DomeFile)vrmlFileParam.getCurrentDataObject();
							File fileObj = new File(vfile.getFilePath());
							gotoLocation(fileObj);
							canvas.repaint();
						}
					}
				});

		JPanel pane = new JPanel(new BorderLayout());
		pane.add(canvas, BorderLayout.CENTER);

		JComponent[] comps = {
							  title,
							  pane,
							  inputTitle1,
							  lengthLabel, lengthValue, lengthUnit,
//							  thicknessLabel, thicknessValue, thicknessUnit,
							  heightLabel, heightValue, heightUnit,
							  inputTitle2,
							  diameterLabel, diameterValue, diameterUnit,
							  wallThicknessLabel, wallThicknessValue, wallThicknessUnit,
							  outputTitle,
							  suspensionLabel, suspensionValue, suspensionUnit,
//							  wheelMassLabel, wheelMassValue, wheelMassUnit,
							  new JPanel()};
		// gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets(t,l,b,r), ipadx, ipady
		GridBagConstraints[] gbcs = {
			//title
			new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			//pane
			new GridBagConstraints(0, 1, 1, 11, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0),

			//inputTitle1
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),

			//lengthLabel, lengthValue, lengthUnit
			new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//thicknessLabel, thicknessValue, thicknessUnit
//			new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
//			new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
//			new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//heightLabel, heightValue, heightUnit
			new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//inputTitle2
			new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15, 5, 0, 0), 0, 0),

			//diameterLabel, diameterValue, diameterUnit
			new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//wallThicknessLabel, wallThicknessValue, wallThicknessUnit
			new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 7, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//outputTitle
			new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15, 5, 0, 0), 0, 0),

			//suspensionLabel, suspensionValue, suspensionUnit
			new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(2, 9, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
			new GridBagConstraints(3, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			//wheelMassLabel, wheelMassValue, wheelMassUnit
//			new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0),
//			new GridBagConstraints(2, 9, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0),
//			new GridBagConstraints(3, 9, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0),

			new GridBagConstraints(1, 10, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0),
		};
		Templates.layoutGridBag(p, comps, gbcs);
		return p;
	}

		/**
	 * @param paramName
	 * @return first variable found in interface with specified name
	 */
	protected Parameter getParameterByName(String paramName) {
		Iterator it = iface.getModelObjectParameters().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Parameter) {
				if (((Parameter)o).getName().equals(paramName))
					return (Parameter)o;
			}
		}
		throw new RuntimeException("unable to find parameter "+paramName);
	}

			/**
	 * Makes textfield for parameter and registers listeners between parameter and textfield.
	 * @param p
	 * @param isEditable or in other words is it an input
	 * @return
	 */
	protected JTextField makeParameterTextField(Parameter p, boolean isEditable)
	{
		JTextField tf = new JTextField(10);
		p.addPropertyChangeListener(new ParameterStatusChangeListener(p, tf));
		tf.setText(getRealValue(p));
		tf.setEditable(isEditable);
		tf.addActionListener(new SuspensionModelCustomGui.RealTextFieldActionListener(p, tf));
		p.getCurrentDataObject().addPropertyChangeListener(DataObject.VALUE, new SuspensionModelCustomGui.RealDataListener(tf));
		return tf;
	}

	/**
	 * When textfield is clicked, value is sent to parameter.
	 */
	class RealTextFieldActionListener implements ActionListener {
		JTextField txtField;
		Parameter p;

		public RealTextFieldActionListener(Parameter p, JTextField txtField)
		{
			this.p = p;
			this.txtField = txtField;
		}

		public void actionPerformed(ActionEvent e)
		{
			try {
				double newValue = Double.parseDouble(txtField.getText());
				setRealValue(p, newValue);
			}
			catch (NumberFormatException ex) {
				System.err.println("invalid real value: " + txtField.getText());
			}
		}
	}

	protected class ParameterStatusChangeListener implements PropertyChangeListener {
		Parameter p;
		JComponent comp;
		public ParameterStatusChangeListener(Parameter p, JComponent comp) {
			this.p = p;
			this.comp = comp;
		}

			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(Parameter.VALUE_STATUS)) {
					String valueStatus = p.getValueStatus();
					if (Parameter.VALUE_STATUS_STALE.equals(valueStatus))
						comp.setBackground(Templates.STALE_COLOR);
					else if (Parameter.VALUE_STATUS_INCONSISTENT.equals(valueStatus))
						comp.setBackground(Templates.INCONSISTENT_COLOR);
					else if (Parameter.VALUE_STATUS_WAITING_VALIDATION.equals(valueStatus))
						comp.setBackground(Templates.WAITING_VALIDATION_COLOR);
					else if (Parameter.VALUE_STATUS_CONSISTENT.equals(valueStatus))
						comp.setBackground(Templates.CONSISTENT_COLOR);
				}
			}
		}

	/**
	 * When value changes in parameter, value is set in textfield.
	 */
	class RealDataListener implements PropertyChangeListener {
		JTextField txtField;

		public RealDataListener(JTextField txtField)
		{
			this.txtField = txtField;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (evt.getPropertyName().equals(DataObject.VALUE)) {
				txtField.setText(evt.getNewValue().toString());
			}
		}
	}

	/**
	 * Sets value of a parameter which contains a real data object.
	 * @param p
	 * @param value
	 */
	protected void setRealValue(Parameter p, double value) {
		((DomeReal)p.getCurrentDataObject()).setValue(value);
	}

	/**
	 * Gets value of a parameter which contains a real data object.
	 * @param p
	 * @return
	 */
	protected String getRealValue(Parameter p) {
		return Double.toString(((DomeReal)p.getCurrentDataObject()).getValue());
	}



    //----------------------------------------------------------
    // Methods required by the OverlayHandler interface.
    //----------------------------------------------------------

    /**
     * Fetch the canvas that will be responsible for having the overlays
     * composited on them.
     *
     * @return The canvas instance to use
     */
    public Canvas3D getPrimaryCanvas() {
        return canvas;
    }

    //----------------------------------------------------------
    // Methods required by the LinkSelectionListener interface.
    //----------------------------------------------------------

    /**
     * Invoked when a link node has been activated. This is the node that has
     * been selected.
     *
     * @param node The selected node
     */
    public void linkSelected(VRMLLinkNodeType node) {

        String[] url_list = node.getUrl();
        boolean success = false;

        for(int i = 0; i < url_list.length; i++) {
            if(url_list[i].charAt(0) == '#') {
                // move to the viewpoint.
                String def_name = url_list[i].substring(1);
                J3DViewpointNodeType vp =
                    (J3DViewpointNodeType)viewpointDefMap.get(def_name);

                if(vp != null) {
                    vp.setBind(false, true, clock.getTime());
                    success = true;
                } else {
					System.out.println("Unknown Viewpoint " + def_name);
                }
            } else {
                // load the world.
                try {
                    URL url = new URL(url_list[i]);
                    InputSource is = new InputSource(url);
                    if(success = load(is))
                        break;

                } catch(MalformedURLException mue) {
					System.out.println("Invalid URL");
                 }
            }
        }

        if(!success)
			System.out.println("No valid URLs were found");
//            console.errorReport("No valid URLs were found", null);

    }

    //----------------------------------------------------------
    // Methods required by the ViewpointSelectionListener interface.
    //----------------------------------------------------------

    /**
     * A new viewpoint has been selected and this is it. Move to this viewpoint
     * location according to the requested means.
     *
     * @param vp The new viewpoint to use
     */
    public void viewpointSelected(ViewpointData vp) {
        if(inSetup)
            return;

        J3DViewpointNodeType vp_node = (J3DViewpointNodeType)vp.userData;
        vp_node.setBind(true, true, clock.getTime());
    }

    //----------------------------------------------------------
    // Implmentation of base class abstract methods
    //----------------------------------------------------------

    /**
     * Go to the named URL location. No checking is done other than to make
     * sure it is a valid URL.
     *
     * @param url The URL to open
     */
    public void gotoLocation(URL url) {
        InputSource is = new InputSource(url);

       urlTextField.setText(url.toString());
        load(is);
    }

    /**
     * Load the named file. The file is checked to make sure that it exists
     * before calling this method.
     *
     * @param file The file to load
     */
    public void gotoLocation(File file) {
	    try{
        InputSource is = new InputSource(file);
		load(is);
	    }
	    catch(Exception e){
		    e.printStackTrace();
	    }

    }

    protected void setWarning(String msg) {
 		System.out.println(msg);
    }

    protected void setError(String msg) {
		System.out.println(msg);
    }

    //----------------------------------------------------------
    // Local convenience methods
    //----------------------------------------------------------

    /**
     * Do all the parsing work. Convenience method for all to call internally
     *
     * @param is The inputsource for this reader
     * @return true if the world loaded correctly
     */
    private boolean load(InputSource is) {
        inSetup = true;

        boolean ret_val = false;

        WorldLoader loader = worldLoader.fetchLoader();

        VRMLScene parsed_scene = null;

        try {
            parsed_scene = loader.loadNow(universe, is);
        } catch(Exception e) {
//            console.errorReport("Failed to load ", e);
			System.out.println("Failed to load ");
            worldLoader.releaseLoader(loader);
e.printStackTrace();
            return false;
        }

        worldLoader.releaseLoader(loader);

        universe.setScene(parsed_scene);

        ret_val = true;

        // Grab the list of viewpoints and place them into the toolbar.
        ArrayList vp_list =
            parsed_scene.getByPrimaryType(TypeConstants.ViewpointNodeType);

        VRMLViewpointNodeType active_vp = universe.getViewpoint();
        ViewpointData active_data = null;
        J3DVRMLNode node;
        vdata = new ViewpointData[vp_list.size()];
        int count = 0;
        String desc;
        TransformGroup tg;
        int size = vp_list.size();

        for(int i = 0; i < size; i++) {
            node = (J3DVRMLNode)vp_list.get(i);

            if(node.getPrimaryType() == TypeConstants.ProtoInstance)
                node = (J3DVRMLNode)((VRMLProtoInstance)node).getImplementationNode();

            desc = ((VRMLViewpointNodeType)node).getDescription();

            if((desc == null) || (desc.length() == 0)) {
                desc = "Viewpoint " + count;
            }

            tg = ((J3DViewpointNodeType)node).getPlatformGroup();

            vdata[count] = new ViewpointData(desc, count, tg);
            vdata[count].userData = node;

            if(node == active_vp) {
                currViewpointNum = count;
                active_data = vdata[count];
            }

            count++;
        }

        // Finally set up the viewpoint def name list. Have to start from
        // the list of DEF names as the Viewpoint nodes don't store the DEF
        // name locally.
        viewpointDefMap.clear();
        Map def_map = parsed_scene.getDEFNodes();
        Iterator itr = def_map.keySet().iterator();

        while(itr.hasNext()) {
            String key = (String)itr.next();
            Object vp = def_map.get(key);

            if(vp instanceof VRMLViewpointNodeType)
                viewpointDefMap.put(key, vp);
        }

        inSetup = false;

        return ret_val;
    }


    /**
     * Set up the system properties needed to run the browser. This involves
     * registering all the properties needed for content and protocol
     * handlers used by the URI system. Only needs to be run once at startup.
     *
     * @param core The core representation of the browser
     * @param loader Loader manager for doing async calls
     */
    private void setupProperties(BrowserCore core, WorldLoaderManager loader) {
        // Disable font cache to fix getBounds nullPointer bug
        System.setProperty("sun.awt.font.advancecache","off");

        System.setProperty("uri.content.handler.pkgs",
                           "vlc.net.content");

        System.setProperty("uri.protocol.handler.pkgs",
                           "vlc.net.protocol");

        if (useImageLoader) {
            System.setProperty("java.content.handler.pkgs",
                               "vlc.net.content");
        }

        URIResourceStreamFactory res_fac = URI.getURIResourceStreamFactory();
        if(!(res_fac instanceof JavascriptResourceFactory)) {
            res_fac = new JavascriptResourceFactory(res_fac);
            URI.setURIResourceStreamFactory(res_fac);
        }

        ContentHandlerFactory c_fac = URI.getContentHandlerFactory();
        if(!(c_fac instanceof VRMLContentHandlerFactory)) {
            c_fac = new VRMLContentHandlerFactory(core, loader, c_fac);
            URI.setContentHandlerFactory(c_fac);
        }

        FileNameMap fn_map = URI.getFileNameMap();
        if(!(fn_map instanceof VRMLFileNameMap)) {
            fn_map = new VRMLFileNameMap(fn_map);
            URI.setFileNameMap(fn_map);
        }

        Web3DURNResolver resolver = new Web3DURNResolver();
        resolver.registerPrefixLocation(UMEL_PREFIX, UMEL_INSTALL_DIR);
        resolver.registerPrefixLocation(GEOVRML_PREFIX, GEOVRML_INSTALL_DIR);

        URN.addResolver(resolver);
    }

	/**
	 * An action has been performed. This is the Go button being pressed.
	 * Grab the URL and check with the file to see if it exists first as
	 * a local file, and then try to make a URL of it. Finally, if this all
	 * works, call the abstract gotoLocation method.
	 *
	 * @param evt The event that caused this method to be called.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object src = evt.getSource();

		File fil=null;

		if (src == openButton) {
			FileDialog fd = new FileDialog((JFrame)SwingUtilities.windowForComponent(this), "Load File");
			fd.setMode(FileDialog.LOAD);
			fd.show();

			fil = new File(fd.getDirectory() + fd.getFile());
			if (fil.exists())
				gotoLocation(fil);
		} else if(src == urlTextField) {
			String location = urlTextField.getText();

			fil = new File(location);
			// try a file first
			if(fil.exists()) {
				if(fil.isDirectory())
					setError("File is a directory");
				else {
					gotoLocation(fil);
					System.out.println("World Loaded Successfully");
				}
			} else {
				// Try a URL
				try {
					URL url = new URL(location);
					gotoLocation(url);
					System.out.println("World Loaded Successfully");
				} catch(MalformedURLException mue) {
					setError("Invalid URL: " + location);
				}
			}
		}
	}


    /**
     * Create an instance of this class and run it. The single argument, if
     * supplied is the name of the file to load initially. If not supplied it
     * will start with a blank document.
     *
     * @param args The list of arguments for this application.
     */
    public static void main(String[] args) {
		JFrame frame = new JFrame("Suspension Custom Gui");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SuspensionModelCustomGui browser = new SuspensionModelCustomGui(null);
        frame.getContentPane().add(browser);
		 frame.setSize(900, 500);
		 frame.setLocation(40, 40);
		frame.show();

        String filename = "C:\\test\\remotetest\\test.vrml";

 //       if (args.length > 0)
  //          filename = args[0];

        if (filename != null) {
            File fil = new File(filename);
            if (fil.exists()) {
                browser.gotoLocation(fil);
            } else {
                try {
                    URL url = new URL(filename);
                    browser.gotoLocation(url);
                } catch(MalformedURLException mfe) {
                   System.out.println("Malformed URL: " + filename);
                }
            }
        }

        // Display FPS, use framestate manager when updated
        while(true) {
            try {
                Thread.sleep(100);
            } catch(Exception e) {}

//            browser.displayFPS();
        }
    }
}
