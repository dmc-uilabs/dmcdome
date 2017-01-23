/*
 * TouchGraph LLC. Apache-Style Software License
 *
 *
 * Copyright (c) 2002 Alexander Shapiro. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        TouchGraph LLC (http://www.touchgraph.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "TouchGraph" or "TouchGraph LLC" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission.  For written permission, please contact
 *    alex@touchgraph.com
 *
 * 5. Products derived from this software may not be called "TouchGraph",
 *    nor may "TouchGraph" appear in their name, without prior written
 *    permission of alex@touchgraph.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL TOUCHGRAPH OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 */

package com.touchgraph.graphlayout;

import com.touchgraph.graphlayout.interaction.GLEditUI;
import com.touchgraph.graphlayout.interaction.GLNavigateUI;
import com.touchgraph.graphlayout.interaction.HVScroll;
import com.touchgraph.graphlayout.interaction.LocalityScroll;
import com.touchgraph.graphlayout.interaction.RotateScroll;
import com.touchgraph.graphlayout.interaction.TGUIManager;
import com.touchgraph.graphlayout.interaction.ZoomScroll;
import mit.cadlab.dome3.gui.fileSystem.browse.BrowseModelFolder;
import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBase;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.objectmodel.modelinterface.SubscriptionInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.relation.Relation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.equal.EqualRelation;
import mit.cadlab.dome3.objectmodel.modelobject.relation.procedural.ProceduralRelation;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.AbstractSubscription;
import mit.cadlab.dome3.objectmodel.modelobject.subscription.Subscription;
import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.project.BrowseInterface;
import mit.cadlab.dome3.objectmodel.project.IntegrationProjectBuilder;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityStatus;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;
import mit.cadlab.dome3.objectmodel.util.solving.DirectedGraph;
import mit.cadlab.dome3.objectmodel.util.solving.MultiItemNode;
import mit.cadlab.dome3.objectmodel.util.solving.NameIdNode;
import mit.cadlab.dome3.objectmodel.util.solving.Parameters;
import mit.cadlab.dome3.plugin.PluginModelBuilder;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DSet;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/** GLPanel contains code for adding scrollbars and interfaces to the TGPanel2
 * The "GL" prefix indicates that this class is GraphLayout specific, and
 * will probably need to be rewritten for other applications.
 *
 * @author   Alexander Shapiro
 * @version  1.20
 */
public class GLPanel extends JPanel {
	// todo: how to set initial state and change color for MultiItemNodes w/parameters
	public String zoomLabel = "Zoom"; // label for zoom menu item
	public String rotateLabel = "Rotate"; // label for rotate menu item
	public String localityLabel = "Locality"; // label for locality menu item

	public HVScroll hvScroll;
	public ZoomScroll zoomScroll;
	//public HyperScroll hyperScroll;
	public RotateScroll rotateScroll;
	public LocalityScroll localityScroll;
	public JPopupMenu glPopup;
	public Hashtable scrollBarHash; //= new Hashtable();

	protected TGPanel tgPanel;
	protected TGLensSet tgLensSet;
	protected TGUIManager tgUIManager;

	private Color defaultColor = Color.lightGray;

	public Vector edgeList = new Vector();
	public Vector nodeList = new Vector();

	boolean shouldPaint = true;

	ParamListener pl;

	// ............



	/** Default constructor.
	 */
	public GLPanel() {
		scrollBarHash = new Hashtable();
		tgLensSet = new TGLensSet();
		tgPanel = new TGPanel();
		hvScroll = new HVScroll(tgPanel, tgLensSet);
		zoomScroll = new ZoomScroll(tgPanel);
		//hyperScroll = new HyperScroll(tgPanel);
		rotateScroll = new RotateScroll(tgPanel);
		localityScroll = new LocalityScroll(tgPanel);
		initialize();
	}

	public GLPanel(DirectedGraph dg) {
		scrollBarHash = new Hashtable();
		tgLensSet = new TGLensSet();
		tgPanel = new TGPanel();
		hvScroll = new HVScroll(tgPanel, tgLensSet);
		zoomScroll = new ZoomScroll(tgPanel);
		//hyperScroll = new HyperScroll(tgPanel);
		rotateScroll = new RotateScroll(tgPanel);
		localityScroll = new LocalityScroll(tgPanel);
		initialize(dg);
	}

	public GLPanel(DomeModelBase model) {
		scrollBarHash = new Hashtable();
		tgLensSet = new TGLensSet();
		tgPanel = new TGPanel();
		hvScroll = new HVScroll(tgPanel, tgLensSet);
		zoomScroll = new ZoomScroll(tgPanel);
		//hyperScroll = new HyperScroll(tgPanel);
		rotateScroll = new RotateScroll(tgPanel);
		localityScroll = new LocalityScroll(tgPanel);
		initialize(model);
	}

	public GLPanel(IntegrationProjectBuilder project) {
		scrollBarHash = new Hashtable();
		tgLensSet = new TGLensSet();
		tgPanel = new TGPanel();
		hvScroll = new HVScroll(tgPanel, tgLensSet);
		zoomScroll = new ZoomScroll(tgPanel);
		//hyperScroll = new HyperScroll(tgPanel);
		rotateScroll = new RotateScroll(tgPanel);
		localityScroll = new LocalityScroll(tgPanel);
		initialize(project);
	}


	/** Constructor with a Color to be used for UI background.
	 */
	public GLPanel(Color color) {
		defaultColor = color;
		this.setBackground(color);
		scrollBarHash = new Hashtable();
		tgLensSet = new TGLensSet();
		tgPanel = new TGPanel();
		tgPanel.setBackground(color);
		hvScroll = new HVScroll(tgPanel, tgLensSet);
		//hvScroll.getHorizontalSB().setBackground(Color.orange);
		//hvScroll.getVerticalSB().setBackground(Color.cyan);
		zoomScroll = new ZoomScroll(tgPanel);
		//zoomScroll.getZoomSB().setBackground(Color.green);
		//hyperScroll = new HyperScroll(tgPanel);
		rotateScroll = new RotateScroll(tgPanel);
		//rotateScroll.getRotateSB().setBackground(Color.blue);
		localityScroll = new LocalityScroll(tgPanel);
		//localityScroll.getLocalitySB().setBackground(Color.red);
		initialize();
	}


	/** Initialize panel, lens, and establish a random graph as a demonstration.
	 */
	public void initialize() {
		buildPanel();
		buildLens();
		tgPanel.setLensSet(tgLensSet);
		addUIs();
		//tgPanel.addNode();  //Add a starting node.
		/*try {
		    randomGraph();
		} catch (TGException tge) {
		    System.err.println(tge.getMessage());
		    tge.printStackTrace(System.err);
		}*/
		tgPanel.setSelect(tgPanel.getGES().getFirstNode()); //Select first node, so hiding works
		setVisible(true);
	}

	public void initialize(DirectedGraph dg) {
		buildPanel();
		buildLens();
		tgPanel.setLensSet(tgLensSet);
		addUIs();
		try {
			//randomGraph();
			createGraph(dg);
		} catch (TGException tge) {
			System.err.println(tge.getMessage());
			tge.printStackTrace(System.err);
		}
		//tgPanel.setSelect(tgPanel.getGES().getFirstNode()); //Select first node, so hiding works
		setVisible(true);
	}

	public void initialize(DomeModelBase model) {
		buildPanel();
		buildLens();
		tgPanel.setLensSet(tgLensSet);
		addUIs();
		try {
			//randomGraph();
			createGraph(model);
		} catch (TGException tge) {
			System.err.println(tge.getMessage());
			tge.printStackTrace(System.err);
		}
		//tgPanel.setSelect(tgPanel.getGES().getFirstNode()); //Select first node, so hiding works
		setVisible(true);
	}

	public void initialize(IntegrationProjectBuilder project) {
		buildPanel();
		buildLens();
		tgPanel.setLensSet(tgLensSet);
		addUIs();
		try {
			//randomGraph();
			createGraph(project);
		} catch (TGException tge) {
			System.err.println(tge.getMessage());
			tge.printStackTrace(System.err);
		}
		//tgPanel.setSelect(tgPanel.getGES().getFirstNode()); //Select first node, so hiding works
		setVisible(true);
	}

	public TGPanel getTGPanel() {
		return tgPanel;
	}

	public void buildLens() {
		tgLensSet.addLens(hvScroll.getLens());
		tgLensSet.addLens(zoomScroll.getLens());
		//tgLensSet.addLens(hyperScroll.getLens());
		tgLensSet.addLens(rotateScroll.getLens());
		tgLensSet.addLens(tgPanel.getAdjustOriginLens());
	}

	public void buildPanel() {
		final JScrollBar horizontalSB = hvScroll.getHorizontalSB();
		final JScrollBar verticalSB = hvScroll.getVerticalSB();
		final JScrollBar zoomSB = zoomScroll.getZoomSB();
		final JScrollBar rotateSB = rotateScroll.getRotateSB();
		final JScrollBar localitySB = localityScroll.getLocalitySB();

		setLayout(new BorderLayout());

		JPanel scrollPanel = new JPanel();
		scrollPanel.setBackground(defaultColor);
		scrollPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();


		JPanel modeSelectPanel = new JPanel();
		modeSelectPanel.setBackground(defaultColor);
		modeSelectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		AbstractAction navigateAction = new AbstractAction("Navigate") {
			public void actionPerformed(ActionEvent e) {
				tgUIManager.activate("Navigate");
			}
		};

		AbstractAction editAction = new AbstractAction("Edit") {
			public void actionPerformed(ActionEvent e) {
				tgUIManager.activate("Edit");
			}
		};

		JRadioButton rbNavigate = new JRadioButton(navigateAction);
		rbNavigate.setBackground(defaultColor);
		rbNavigate.setSelected(true);
		rbNavigate.setFont(Templates.FONT11);
		JRadioButton rbEdit = new JRadioButton(editAction);
		rbEdit.setBackground(defaultColor);
		rbEdit.setFont(Templates.FONT11);
		rbEdit.setEnabled(false);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbNavigate);
		bg.add(rbEdit);

		modeSelectPanel.add(rbNavigate);
		modeSelectPanel.add(rbEdit);

		final JPanel topPanel = new JPanel();
		topPanel.setBackground(defaultColor);
		topPanel.setLayout(new GridBagLayout());
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		/*
		c.gridx=0;c.weightx=0;
		topPanel.add(new Label("Zoom",Label.RIGHT), c);
		c.gridx=1;c.weightx=0.5;
		topPanel.add(zoomSB,c);
		c.gridx=2;c.weightx=0;
		topPanel.add(new Label("Locality",Label.RIGHT), c);
		c.gridx=3;c.weightx=0.5;
		topPanel.add(localitySB,c);
		*/
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(0, 10, 0, 10);
		topPanel.add(modeSelectPanel, c);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 1;
		c.weightx = 1;

		scrollBarHash.put(zoomLabel, zoomSB);
		scrollBarHash.put(rotateLabel, rotateSB);
		scrollBarHash.put(localityLabel, localitySB);

		JPanel scrollselect = scrollSelectPanel(new String[]{zoomLabel, rotateLabel, localityLabel});
		scrollselect.setBackground(defaultColor);
		topPanel.add(scrollselect, c);

		add(topPanel, BorderLayout.NORTH);

		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		scrollPanel.add(tgPanel, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		scrollPanel.add(verticalSB, c);

		c.gridx = 0;
		c.gridy = 2;
		scrollPanel.add(horizontalSB, c);

		add(scrollPanel, BorderLayout.CENTER);

		glPopup = new JPopupMenu();
		glPopup.setBackground(defaultColor);

		JMenuItem menuItem = new JMenuItem("Toggle Controls");
		ActionListener toggleControlsAction = new ActionListener() {
			boolean controlsVisible = true;

			public void actionPerformed(ActionEvent e) {
				controlsVisible = !controlsVisible;
				horizontalSB.setVisible(controlsVisible);
				verticalSB.setVisible(controlsVisible);
				topPanel.setVisible(controlsVisible);
			}
		};
		menuItem.addActionListener(toggleControlsAction);
		glPopup.add(menuItem);
	}

	protected JPanel scrollSelectPanel(String[] scrollBarNames) {
		final JComboBox scrollCombo = new JComboBox(scrollBarNames);
		scrollCombo.setBackground(defaultColor);
		scrollCombo.setPreferredSize(new Dimension(80, 20));
		scrollCombo.setSelectedIndex(0);
		final JScrollBar initialSB = (JScrollBar) scrollBarHash.get(scrollBarNames[0]);
		scrollCombo.addActionListener(new ActionListener() {
			JScrollBar currentSB = initialSB;

			public void actionPerformed(ActionEvent e) {
				JScrollBar selectedSB = (JScrollBar) scrollBarHash.get(
				        scrollCombo.getSelectedItem());
				if (currentSB != null) currentSB.setVisible(false);
				if (selectedSB != null) selectedSB.setVisible(true);
				currentSB = selectedSB;
			}
		});
		scrollCombo.setFont(Templates.FONT11);

		final JPanel sbp = new JPanel(new GridBagLayout());
		sbp.setBackground(defaultColor);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		sbp.add(scrollCombo, c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.insets = new Insets(0, 10, 0, 17);
		c.fill = GridBagConstraints.HORIZONTAL;
		for (int i = 0; i < scrollBarNames.length; i++) {
			JScrollBar sb = (JScrollBar) scrollBarHash.get(scrollBarNames[i]);
			if (sb == null) continue;
			if (i != 0) sb.setVisible(false);
			//sb.setMinimumSize(new Dimension(200,17));
			sbp.add(sb, c);
		}
		return sbp;
	}

	public void addUIs() {
		tgUIManager = new TGUIManager();
		GLEditUI editUI = new GLEditUI(this);
		GLNavigateUI navigateUI = new GLNavigateUI(this);
		tgUIManager.addUI(editUI, "Edit");
		tgUIManager.addUI(navigateUI, "Navigate");
		tgUIManager.activate("Navigate");
	}

	public void randomGraph() throws TGException {
		Node n1 = tgPanel.addNode();
		n1.setType(0);
		for (int i = 0; i < 249; i++) {
			Node r = tgPanel.getGES().getRandomNode();
			Node n = tgPanel.addNode();
			n.setType(0);
			if (tgPanel.findEdge(r, n) == null) tgPanel.addEdge(r, n, Edge.DEFAULT_LENGTH);
			if (i % 2 == 0) {
				r = tgPanel.getGES().getRandomNode();
				if (tgPanel.findEdge(r, n) == null) tgPanel.addEdge(r, n, Edge.DEFAULT_LENGTH);
			}
		}
		tgPanel.setLocale(n1, 2);
	}


	private void configureModelParamNode(Node n) {
		n.setType(Node.TYPE_MODEL_PARAM);
	}

	private void configureRelationParamNode(Node n) {
		n.setType(Node.TYPE_RELATION_PARAM);
	}

	private void configureSubscriptionParamNode(Node n) {
		n.setType(Node.TYPE_SUBSCRIPTION_PARAM);
	}

	private void configureProcRelationNode(Node n) {
		n.setType(Node.TYPE_PROC_REL);
		n.setHasBorder(true);
	}

	private void configureEqualRelationNode(Node n) {
		n.setType(Node.TYPE_EQUAL_REL);
		n.setHasBorder(true);
	}

	private void configureInterfaceParamNode(Node n) {
		n.setType(Node.TYPE_INTERFACE_PARAM);
	}

	private void configureSubscribedInterfaceNode(Node n) {
		n.setType(Node.TYPE_INTERFACE_SUBSCRIB);
		n.setHasBorder(true);
	}

	private void configureModelNode(Node n) {
		n.setType(Node.TYPE_MODEL);
		n.setHasBorder(true);
	}

	private void configureProjectNode(Node n)
	{
		n.setType(Node.TYPE_PROJECT);
		n.setHasBorder(true);
	}

	private void configureNameIdNode(Node n, String nodeType)
	{
		if (NameIdNode.MODEL_NODE.equals(nodeType))
			configureModelNode(n);
		else if (NameIdNode.PROJECT_NODE.equals(nodeType))
			configureProjectNode(n);
		// else generic node representation
	}

	private void configureMultiItemNode(Node n, List items)
	{
		if (items.get(0) instanceof DomeObject)
			ConfigureNode((DomeObject) items.get(0), n);
		n.setHasBorder(true);
	}

	public void createGraph(IntegrationProjectBuilder project) throws TGException {
		HashMap wholeMap = new HashMap();
		List resource = project.getResourceModels();
		for (int i = 0; i < resource.size(); i++) {
			BuildProjectResourceInfo bpri = (BuildProjectResourceInfo) resource.get(i);
			if (bpri.getType().equals(ProjectResourceInfo.MODEL_RESOURCE)) {
				bpri.loadResource();
				List subscribedInterfaceIds = bpri.getSubscribedInterfaceIds();
				List viewList = bpri.getView();
				for (Iterator it = viewList.iterator(); it.hasNext();) {
					Object o = it.next();
					if (o instanceof BrowseInterface) {
						BrowseInterface bi = (BrowseInterface) o;
						if (subscribedInterfaceIds.contains(bi.getInterfaceId())) {
							bi.loadInterface(true); // not calculating system causality
							ModelInterfaceRuntimeClient iface = bi.getInterface();
							HashMap rpToIfaceMap = iface.getRelParamToIfaceMap();
							for (Iterator iterator = rpToIfaceMap.keySet().iterator(); iterator.hasNext();) {
								String relParam = (String) iterator.next();
								DSet ifaceParams = (DSet) wholeMap.get(relParam);
								if (ifaceParams == null) {
									ifaceParams = new DSet();
								}
								ifaceParams.add(rpToIfaceMap.get(relParam));
                                wholeMap.put(relParam, ifaceParams);
                            }
							createGraph(iface.getInterfaceGraph(),iface.getId().getIdString());
						}
					}
				}
			} else {
				bpri.loadResource();
				List viewList = bpri.getView();
				for (Iterator it = viewList.iterator(); it.hasNext();) {
					Object o = it.next();
					if (o instanceof BrowseModelFolder) {
						BrowseModelFolder folder = (BrowseModelFolder) o;
						if ("interfaces".equals(folder.getName())) {
							List interfaces = folder.getContent();
							for (int j = 0; j < interfaces.size(); j++) {
								if (interfaces.get(j) instanceof BrowseInterface) {
									BrowseInterface bi = (BrowseInterface) interfaces.get(j);
									bi.loadInterface(true); // not calculating system causality
									ModelInterfaceRuntimeClient iface = bi.getInterface();
									HashMap rpToIfaceMap = iface.getRelParamToIfaceMap();
									for (Iterator iterator = rpToIfaceMap.keySet().iterator(); iterator.hasNext();) {
										String relParam = (String) iterator.next();
										DSet ifaceParams = (DSet) wholeMap.get(relParam);
										if (ifaceParams == null) {
											ifaceParams = new DSet();
										}
										ifaceParams.add(rpToIfaceMap.get(relParam));
										wholeMap.put(relParam, ifaceParams);
									}
									createGraph(iface.getInterfaceGraph(), iface.getId().getIdString());
								}
							}
						}
					}
				}
			}
		}

		// go through the whole map and look for relation parameters that are mapped to more than one interface
		Iterator iter = wholeMap.keySet().iterator();
		while (iter.hasNext()) {
			String relParam = (String) iter.next();
			DSet ifaces = (DSet) wholeMap.get(relParam);
			if (ifaces.size()>1) {
				Node relNode = new Node(relParam,"");
				configureModelParamNode(relNode);
				tgPanel.addNode(relNode);
				for (int i = 0; i < ifaces.size(); i++) {
					String ifaceParam = (String) ifaces.get(i);
					Node ifaceParamNode = tgPanel.findNode(ifaceParam);
					if (ifaceParamNode !=null)
						tgPanel.addEdge(relNode, ifaceParamNode, Edge.DEFAULT_LENGTH);
				}
			}
		}

		Iterator it = project.getIntegrationModels().iterator();
		while (it.hasNext()) {
			ProjectIntegrationModelInfo aIModel = (ProjectIntegrationModelInfo) it.next();
			DomeModel dm = aIModel.getModel();
			ConnectionMappingManager mgr = dm.getMappingManager();
			List subs = dm.getSubscriptions();
			HashMap paramIdMap = new HashMap();

            // create graph for relations within the imodel
            Collection mObjs =  dm.getModelObjects();
            for (Iterator iterator = mObjs.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                if (o instanceof ProceduralRelation) {
                    ProceduralRelation rel = (ProceduralRelation)o;
                    DirectedGraph dg = new DirectedGraph(rel.getDependencyInfo());
                    createGraph(dg);
                }
            }

            // add model parameters
            List modelParams = dm.getFilter(DomeModel.PARAMETERS_FILTER).getItems();
            for (int i = 0; i < modelParams.size(); i++) {
                Parameter mp = (Parameter) modelParams.get(i);
                Node mpNode = tgPanel.addNode(getGraphId(mp), mp.getName());
                ConfigureNode(mp, mpNode);
                Collection map = mgr.getMappingsForParameter(mp);
                for (Iterator iterator = map.iterator(); iterator.hasNext();) {
                    Parameter p = (Parameter) iterator.next();
                    if (Parameters.isRelationInput(p))
                        tgPanel.addEdge(mpNode, tgPanel.findNode(getGraphId(p)), Edge.DEFAULT_LENGTH);
                    else if (Parameters.isRelationOutput(p))
                        tgPanel.addEdge(tgPanel.findNode(getGraphId(p)), mpNode, Edge.DEFAULT_LENGTH);
                    // else = p is a subscription param -> edge will be drawn later
                }
            }

            // create paramIdMap (newId - > origId (iface param id)) for all subscriptions
            for (int i = 0; i < subs.size(); i++) {
                AbstractSubscription subscription = (AbstractSubscription) subs.get(i);
                paramIdMap.putAll(subscription.getParamIdMap());
            }

            // link graphs between subscriptions
			for (int i = 0; i < subs.size(); i++) {
				AbstractSubscription sub = (AbstractSubscription) subs.get(i);
				Collection params = sub.getModelObjects();
				for (Iterator iterator = params.iterator(); iterator.hasNext();) {
					Object subObj = iterator.next();
					if (subObj instanceof Parameter) {
						Parameter param = (Parameter) subObj;
						Collection map = mgr.getMappingsForParameter(param);
						if (!map.isEmpty()) { // mapped to parameter in imodel
							String paramId = param.getId().getIdString();
							Node ifaceNode = tgPanel.findNode(sub.getResourceId()+"."+paramIdMap.get(paramId));
							for (Iterator iterator2 = map.iterator(); iterator2.hasNext();) {
	                            Parameter imodelParam = (Parameter) iterator2.next();
	                            Object mappedIfaceId = (paramIdMap.get(imodelParam.getId().getIdString()));
	                            Node mappedIfaceNode = null;
	                            if (mappedIfaceId==null) // must be in a relation within imodel
	                                mappedIfaceNode = tgPanel.findNode(getGraphId(imodelParam));
	                            else // in a subscription
	                                mappedIfaceNode = tgPanel.findNode((String) mappedIfaceId);
	                            if (Parameters.isSubscriptionInput(param))
	                                tgPanel.addEdge(mappedIfaceNode, ifaceNode, Edge.DEFAULT_LENGTH);
	                            else
	                                tgPanel.addEdge(ifaceNode, mappedIfaceNode, Edge.DEFAULT_LENGTH);
							}
						}
					}
				}
			}
		}
        tgPanel.repaintAfterMove();
	}

	// use for creating imodel part of the project graph in build mode
	public void createIModelGraph(DomeModelBase model) throws TGException {
		pl = new ParamListener();

		//get model parameters
		List modelParams = new ArrayList(model.getFilter(DomeModel.PARAMETERS_FILTER).getItems());
		for (int i = 0; i < modelParams.size(); i++) {
			Parameter mp = (Parameter) modelParams.get(i);

			// add and configure node for each model parameter
			Node mpNode = tgPanel.findNode(getGraphId(mp));
			if (mpNode == null) {
				mp.addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
				mpNode = tgPanel.addNode(getGraphId(mp), mp.getName());
				ConfigureNode(mp, mpNode);
			}
		}

		// get all relations
		Iterator it = model.getFilter(DomeModel.RELATIONS_FILTER).getItems().iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof ProceduralRelation) {
				// add and configure node for a relation
				ProceduralRelation rel = (ProceduralRelation) obj;
				Node relNode = tgPanel.addNode(getGraphId(rel), rel.getName());
				ConfigureNode((DomeObject) obj, relNode);


				// add and configure nodes for all relation parameters within the relation
				Collection relParams = rel.getModelObjects();
				for (Iterator iterator = relParams.iterator(); iterator.hasNext();) {
					Parameter relParam = (Parameter) iterator.next();
					Node node = tgPanel.findNode(getGraphId(relParam));
					if (node == null) {
						relParam.addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
						node = tgPanel.addNode(getGraphId(relParam), relParam.getName());
						ConfigureNode(relParam, node);
					}
				}

				List triggers = rel.getDependencyInfo().getTriggers();

				List inputs = rel.getItems(CausalityStatus.INDEPENDENT);
				for (Iterator iterator2 = inputs.iterator(); iterator2.hasNext();) {
					Parameter in = (Parameter) iterator2.next();
					if (triggers.contains(in)) {
						Node inNode = tgPanel.findNode(getGraphId(in));
						tgPanel.addEdge(inNode, relNode, Edge.DEFAULT_LENGTH + 20);
					}
				}
				List outputs = new ArrayList(rel.getItems(CausalityStatus.INTERMEDIATE));
				outputs.addAll(rel.getItems(CausalityStatus.RESULT));
				for (Iterator iterator4 = outputs.iterator(); iterator4.hasNext();) {
					Parameter out = (Parameter) iterator4.next();
					Node outNode = tgPanel.findNode(getGraphId(out));
					tgPanel.addEdge(relNode, outNode, Edge.DEFAULT_LENGTH + 20);
				}
			}
		}
	}

	public void createPluginModelGraph(DomeModelBase model) throws TGException {
		DependencyInfo dInfo = ((PluginModelBuilder) model).getDependencyInfo();
		DirectedGraph dg = new DirectedGraph(dInfo);
		createGraph(dg);
	}

	public void createGraph(DomeModelBase model) throws TGException {
		String modelType = model.getTypeName();
		if ("Plugin Model".equals(modelType)) {
			createPluginModelGraph(model);
		} else {
			pl = new ParamListener();
			ConnectionMappingManager mgr = model.getMappingManager();

			//get model parameters
			List modelParams = new ArrayList(model.getFilter(DomeModel.PARAMETERS_FILTER).getItems());
			for (int i = 0; i < modelParams.size(); i++) {
				Parameter mp = (Parameter) modelParams.get(i);

				// add and configure node for each model parameter
				Node mpNode = tgPanel.findNode(getGraphId(mp));
				if (mpNode == null) {
					mp.addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
					mpNode = tgPanel.addNode(getGraphId(mp), mp.getName());
					ConfigureNode(mp, mpNode);
				}
			}

			// get all relations
			Iterator it = model.getFilter(DomeModel.RELATIONS_FILTER).getItems().iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof ProceduralRelation) {
					// add and configure node for a relation
					ProceduralRelation rel = (ProceduralRelation) obj;
					Node relNode = tgPanel.addNode(getGraphId(rel), rel.getName());
					ConfigureNode((DomeObject) obj, relNode);

					// add and configure nodes for all relation parameters within the relation
					Collection relParams = rel.getModelObjects();
					for (Iterator iterator = relParams.iterator(); iterator.hasNext();) {
						Parameter relParam = (Parameter) iterator.next();
						Node node = tgPanel.findNode(getGraphId(relParam));
						if (node == null) {
							relParam.addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
							node = tgPanel.addNode(getGraphId(relParam), relParam.getName());
							ConfigureNode(relParam, node);
						}
					}

					List triggers = rel.getDependencyInfo().getTriggers();

					List inputs = rel.getItems(CausalityStatus.INDEPENDENT);
					for (Iterator iterator2 = inputs.iterator(); iterator2.hasNext();) {
						Parameter in = (Parameter) iterator2.next();
						if (triggers.contains(in)) {
							Node inNode = tgPanel.findNode(getGraphId(in));
							tgPanel.addEdge(inNode, relNode, Edge.DEFAULT_LENGTH + 20);

							Collection mapping = mgr.getMappingsForParameter(in);
							for (Iterator iterator3 = mapping.iterator(); iterator3.hasNext();) {
								Parameter mp = (Parameter) iterator3.next();
								Node mpNode = tgPanel.findNode(getGraphId(mp));
								if (mpNode == null) {
									mp.addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
									mpNode = tgPanel.addNode(getGraphId(mp), mp.getName());
									ConfigureNode(mp, mpNode);
								}
								if (tgPanel.findEdge(mpNode, inNode) == null) {
									tgPanel.addEdge(mpNode, inNode, Edge.DEFAULT_LENGTH + 40);
								}
							}
						}
					}
					List outputs = new ArrayList(rel.getItems(CausalityStatus.INTERMEDIATE));
					outputs.addAll(rel.getItems(CausalityStatus.RESULT));
					for (Iterator iterator4 = outputs.iterator(); iterator4.hasNext();) {
						Parameter out = (Parameter) iterator4.next();
						Node outNode = tgPanel.findNode(getGraphId(out));
						tgPanel.addEdge(relNode, outNode, Edge.DEFAULT_LENGTH + 20);
						Collection mapping = mgr.getMappingsForParameter(out);
						for (Iterator iterator3 = mapping.iterator(); iterator3.hasNext();) {
							Parameter mp = (Parameter) iterator3.next();
							Node mpNode = tgPanel.findNode(getGraphId(mp));
							if (mpNode == null) {
								mp.addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
								mpNode = tgPanel.addNode(getGraphId(mp), mp.getName());
								ConfigureNode(mp, mpNode);
							}
							if (tgPanel.findEdge(outNode, mpNode) == null) {
								tgPanel.addEdge(outNode, mpNode, Edge.DEFAULT_LENGTH + 40);
							}
						}
					}
				}
			}


			if (model.isIntegrationModel()) {
                for (Iterator iterator = model.getSubscriptions().iterator(); iterator.hasNext();) {
                    Subscription sub = (Subscription) iterator.next();
					Node subNode = tgPanel.findNode(sub.getId().getIdString());
					if (subNode == null) {
						subNode = tgPanel.addNode(sub.getId().getIdString(), sub.getName());
						configureSubscribedInterfaceNode(subNode);
					}
					createGraph(subNode, sub.getGraph());
                }

					// mapping between subscription parameters
                for (Iterator iterator = model.getSubscriptions().iterator(); iterator.hasNext();) {
	                Subscription sub = (Subscription) iterator.next();
	                for (Iterator it3 = sub.getModelObjects().iterator(); it3.hasNext();) {
		                Object subObj = it3.next();
		                if (subObj instanceof Parameter) {
			                Parameter subParam = (Parameter) subObj;
			                Collection subMap = mgr.getMappingsForParameter(subParam);
			                if (!subMap.isEmpty()) {
				                for (Iterator it4 = subMap.iterator(); it4.hasNext();) {
					                Object o = it4.next();
					                if (o instanceof Parameter) { // might be model param, or subscription param
						                Parameter mappedSubParam = (Parameter) o;
						                Node subParamNode = tgPanel.findNode(getGraphId(subParam));
						                Node mappedSubParamNode = tgPanel.findNode(getGraphId(mappedSubParam));
						                if (subParamNode != null && mappedSubParamNode != null) {
							                if (Parameters.isSubscriptionInput(subParam)) {
								                if (tgPanel.findEdge(mappedSubParamNode, subParamNode) == null) {
									                tgPanel.addEdge(mappedSubParamNode, subParamNode, Edge.DEFAULT_LENGTH);
								                }
							                }
							                else if (Parameters.isSubscriptionOutput(subParam)) {
								                if (tgPanel.findEdge(subParamNode, mappedSubParamNode) == null) {
									                tgPanel.addEdge(subParamNode, mappedSubParamNode, Edge.DEFAULT_LENGTH);
								                }
							                }
						                }
					                }
				                }
			                }
		                }
	                }
                }
			}
		}
	}

	public void createGraph(Node centerNode, DirectedGraph dg) throws TGException {
		pl = new ParamListener(); // used in createNodes
		Node n;

		List disconnectedNodes = dg.getDisconnectedNodes();
		List disconnectedGraphNodes = createNodes(disconnectedNodes);
		for (int i = 0; i < disconnectedGraphNodes.size(); i++) {
			n = (Node) disconnectedGraphNodes.get(i);
			if (tgPanel.findEdge(centerNode,n) == null)
				tgPanel.addEdge(Edge.createDisconnectedEdge(centerNode,n));
		}
		List inputs = dg.getInputs();
		List inputNodes = createNodes(inputs);
		for (int i = 0; i < inputNodes.size(); i++) {
			n = (Node) inputNodes.get(i);
			if (tgPanel.findEdge(n, centerNode) == null)
				tgPanel.addEdge(n, centerNode, Edge.DEFAULT_LENGTH);
		}
		List outputs = new DArrayList(dg.getOutputs());
		List outputNodes = createNodes(outputs);
		for (int i = 0; i < outputNodes.size(); i++) {
			n = (Node) outputNodes.get(i);
			if (tgPanel.findEdge(centerNode, n) == null)
				tgPanel.addEdge(centerNode, n, Edge.DEFAULT_LENGTH);
		}
	}

	protected List createNodes(List graphNodes) throws TGException {
		List nodes = new ArrayList();
		Node n;
		Object obj;
		for (int i = 0; i < graphNodes.size(); i++) {
			obj = graphNodes.get(i);
			String name, id;
			if (obj instanceof DomeObject) {
				DomeObject dObj = (DomeObject) obj;
				id = getGraphId(dObj);
				name = dObj.getName();
				n = tgPanel.findNode(id);
				if (n == null) {
					n = tgPanel.addNode(id, name);
					ConfigureNode(dObj, n);
					if (dObj instanceof Parameter)
						dObj.addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
				}
			}
			else if (obj instanceof MultiItemNode) {
				MultiItemNode min = (MultiItemNode) obj;
				n = tgPanel.findNode(min.getId());
				if (n == null) {
					n = tgPanel.addNode(min.getId(), min.getNameNoBrackets());
					List items = min.getItems();
					configureMultiItemNode(n, items);
					for (int j = 0; j < items.size(); j++) {
						Object dObj = items.get(j);
						if (dObj instanceof Parameter)
							((Parameter) dObj).addPropertyChangeListener(Parameter.VALUE_STATUS, pl);
					}
				}
			}
			else if (obj instanceof NameIdNode) {
				NameIdNode node = (NameIdNode) obj;
				n = tgPanel.findNode(node.getId());
				if (n == null) {
					n = tgPanel.addNode(node.getId(), node.getName());
					configureNameIdNode(n, node.getType());
				}
			}
			else {
				name = obj.toString();

				n = tgPanel.findNode(name);
				if (n == null) {
					n = tgPanel.addNode(name, name);
					n.setType(0);
				}
			}
			n.setVisible(true);
			tgPanel.expandNode(n);
			nodes.add(n);
		}
		return nodes;
	}

	public void createGraph(DirectedGraph dg) throws TGException {
		createGraph(dg, null);
	}

	public void createGraph(DirectedGraph dg, String graphId) throws TGException {
		pl = new ParamListener();
		createNodes(dg.getNodes());

		Map arcs = dg.getArcs();
		for (Iterator iterator = arcs.keySet().iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			String fromId;
			if (o instanceof DomeObject) {
				fromId = getGraphId((DomeObject) o);
			} else if (o instanceof NameIdNode) {
				fromId = ((NameIdNode) o).getId();
			} else if (o instanceof MultiItemNode) {
				fromId = ((MultiItemNode) o).getId();
			} else {
				fromId = o.toString();
			}
			List toNodes = (List) arcs.get(o);
			for (Iterator iterator2 = toNodes.iterator(); iterator2.hasNext();) {
				Object o2 = iterator2.next();
				String toId;
				if (o2 instanceof DomeObject) {
					toId = getGraphId((DomeObject) o2);
				} else if (o2 instanceof NameIdNode) {
					toId = ((NameIdNode) o2).getId();
				} else if (o2 instanceof MultiItemNode) {
					toId = ((MultiItemNode) o2).getId();
				} else {
					toId = o2.toString();
				}
				Node fromNode = tgPanel.findNode(fromId);
				Node toNode = tgPanel.findNode(toId);
				if (tgPanel.findEdge(fromNode, toNode) == null)
					tgPanel.addEdge(fromNode, toNode, Edge.DEFAULT_LENGTH);
			}
		}
	}

	private String getGraphId(DomeObject dObj) {
		if (dObj instanceof ModelObject) {
			ModelObjectScope scope = ((ModelObject)dObj).getScope();
			while (scope instanceof ModelObject)
				scope = ((ModelObject)scope).getScope();
			if (scope instanceof SubscriptionInterface)
				return ((SubscriptionInterface)scope).getSubscription().getResourceId() + "." + dObj.getId().getIdString();
		}
		return dObj.getId().getIdString();
	}

	private void ConfigureNode(DomeObject obj, Node n) {

		if (obj instanceof Parameter) {
			if (Parameters.isModelParameter((Parameter) obj))
				configureModelParamNode(n);
			else if (Parameters.isRelationParameter((Parameter) obj))
				configureRelationParamNode(n);
			else if (Parameters.isSubscriptionParameter((Parameter) obj))
				configureSubscriptionParamNode(n);
			else //if (Parameters.isInterfaceParameter((Parameter) obj))
				configureInterfaceParamNode(n);
			n.setColorToState((Parameter)obj);
		} else if (obj instanceof Relation) {
			if (obj instanceof EqualRelation)
				configureEqualRelationNode(n);
			else
				configureProcRelationNode(n);
		} else if (obj instanceof DomeModel) {
			configureModelNode(n);
		}
	}


	public static void visualizeGraph(String frameName, DirectedGraph dg) {
		JFrame frame;
		frame = new JFrame(frameName);
		GLPanel glPanel = new GLPanel(dg);
		frame.addWindowListener(new GraphWindowListener(glPanel, dg));
		frame.getContentPane().add(glPanel,BorderLayout.CENTER);
		frame.setSize(500, 500);
		frame.show();
	}

	public static class GraphWindowListener extends WindowAdapter {
		private GLPanel glPanel;
		private DirectedGraph dg;

		public GraphWindowListener(GLPanel glPanel, DirectedGraph dg)
		{
			this.glPanel = glPanel;
			this.dg = dg;
		}

		public void windowClosing(WindowEvent e)
		{
			glPanel.disconnectGraphListeners(dg);
			((Window)e.getSource()).dispose();
		}
	}

	private void disconnectGraphListeners(DirectedGraph dg) {
		if (pl==null)
			return;
		Iterator nodes = dg.getNodes().iterator();
		Object obj;
		while (nodes.hasNext()) {
			obj = nodes.next();
			if (obj instanceof Parameter)
				((Parameter) obj).removePropertyChangeListener(Parameter.VALUE_STATUS, pl);
			else if (obj instanceof MultiItemNode) {
				List items = ((MultiItemNode)obj).getItems();
				for (int j = 0; j < items.size(); j++) {
					Object dObj = items.get(j);
					if (dObj instanceof Parameter)
						((Parameter) dObj).removePropertyChangeListener(Parameter.VALUE_STATUS, pl);
				}
			}
		}
	}

	public static void visualizeGraph(DomeModelBase model) {
		JFrame frame;
		frame = new JFrame("Graph Layout");
		GLPanel glPanel = new GLPanel(model);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().add(glPanel);
		frame.setSize(500, 500);
		frame.show();
	}

	public static void visualizeGraph(IntegrationProjectBuilder project) {
		JFrame frame;
		frame = new JFrame("Graph Layout");
		GLPanel glPanel = new GLPanel(project);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().add(glPanel);
		frame.setSize(500, 500);
		frame.show();
	}

	public static void main(String[] args) {
		JFrame frame;
		frame = new JFrame("Graph Layout");
		GLPanel glPanel = new GLPanel();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.getContentPane().add("Center", glPanel);
		frame.setSize(500, 500);
		frame.setVisible(true);

	}

	public void updateGraph(DomeModelBase model) {
		tgPanel.clearAll();
		try {
			createGraph(model);
		} catch (TGException tge) {
			System.err.println(tge.getMessage());
			tge.printStackTrace(System.err);
		}
	}

	public void updateGraph(IntegrationProjectBuilder project) {
		tgPanel.clearAll();
		try {
			createGraph(project);
		} catch (TGException tge) {
			System.err.println(tge.getMessage());
			tge.printStackTrace(System.err);
		}
	}

	//to be called by interface only
	public void updateGraph(DirectedGraph dg) {
		tgPanel.clearAll();
		try {
			createGraph(dg);
		} catch (TGException tge) {
			System.err.println(tge.getMessage());
			tge.printStackTrace(System.err);
		}
	}

	public void shouldPaint(boolean value) {
		shouldPaint = value;
	}


	class ParamListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent e) {
			Parameter p = (Parameter) e.getSource();
			Node n = tgPanel.findNode(getGraphId(p));
			n.setColorToState(p);
			//n.paint(GLPanel.this.getGraphics(), tgPanel);
			if (shouldPaint)
				tgPanel.paint(GLPanel.this.tgPanel.getGraphics());

			/* int ulx, uly;
			ulx = (int)(n.drawx-(n.getWidth()/2+2));
			uly = (int)(n.drawy-(n.getHeight()/2+2));
			//GLPanel.this.repaint(ulx, uly, n.getWidth()+4, n.getHeight()+4);
			Graphics g = GLPanel.this.tgPanel.getGraphics();
			tgPanel.paint(g);
			g.setColor(Color.black);
			g.drawRect(ulx, uly, n.getWidth()+4, n.getHeight()+4);
			//*/
		}
	}

} // end com.touchgraph.graphlayout.GLPanel
