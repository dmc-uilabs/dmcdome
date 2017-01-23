package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 20.
 */
public class RelationToolPanel extends JPanel {

    private JButton loadModel1Button;
    private JButton loadModel2Button;
    private JButton unloadModelButton;
    private JButton addRelButton;
    private JButton addInputParamButton;
    private JButton addOutputParamButton;
    private JButton removeCellButton;
    private JCheckBox cellWrapCheckBox;
    private Image img = UIUtil.createImageIcon("images/interface_separator.gif", "interface separator bar").getImage();

    final private ComponentReference compRef;

    public RelationToolPanel(ComponentReference compRef) {
        this.compRef = compRef;

//        this.setMinimumSize(new Dimension(400, 35));
//        this.setPreferredSize(new Dimension(400, 35));
//        this.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
//        setBackground(UIUtil.REL_TOOLBAR_BG_COLOR);
        //this.setLayout(new FlowLayout(FlowLayout.LEFT));
        //initComponents();
    }

//    private void initComponents() {
//        loadModel1Button = new JButton("load 1");
//        loadModel1Button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                CModel model = CoreUnitTest.doTest1();
//                compRef.getImplementationEditor().displayImplementation(model, "My Interface", "My Impl");
//            }
//        });
//
//        loadModel2Button = new JButton("load 2");
//        loadModel2Button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                CModel model = CoreUnitTest.doTest2();
//                compRef.getImplementationEditor().displayImplementation(model, "My Interface", "My Impl");
//            }
//        });
//
//        unloadModelButton = new JButton("unload");
//        unloadModelButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                if (compRef.getImplementationEditor().isImplLoaded()) {
//                    compRef.getImplementationEditor().unload();
//                }
//            }
//        });
//
//        addRelButton = new JButton("add relation after selected relation");
//        addRelButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                RelationEditor relEditor = compRef.getImplementationEditor().getRelationEditor();
//                int relCount = relEditor.getComponents().length;
//                RelationBar relBar = relEditor.addRelation("Rel " + (relCount + 1));
//            }
//        });
//        addInputParamButton = new JButton("add input to selected bar");
//        addInputParamButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                InterfaceBar itfBar = compRef.getSelectedInterfaceBar();
//                if (itfBar != null) {
//                    itfBar.addInterfaceInputCell("my itf input", "int", "C");
//                    return;
//                }
//
//                RelationBar relBar = compRef.getSelectedRelationBar();
//                if (relBar != null) {
//                    relBar.addRelationInputCell("my rel input", "real", "mm", "");
//                    return;
//                }
//            }
//        });
//
//        addOutputParamButton = new JButton("add output to selected bar");
//        addOutputParamButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                InterfaceBar itfBar = compRef.getSelectedInterfaceBar();
//                if (itfBar != null) {
//                    itfBar.addInterfaceOutputCell("my itf input", "int", "C", "");
//                    return;
//                }
//
//                RelationBar relBar = compRef.getSelectedRelationBar();
//                if (relBar != null) {
//                    relBar.addRelationOutputCell("my rel output", "int", "kg");
//                    return;
//                }
//            }
//        });
//
//        removeCellButton = new JButton("remove selected");
//        removeCellButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                RelationBar[] selectedBars = compRef.getSelectedRelationBars();
//                for (int j = 0; j < selectedBars.length; j++) {
//                    compRef.getRelationEditor().removeRelationBar(selectedBars[j].getRelAlias());
//                }
//                BaseCell[] selectedCells = compRef.getSelectedCells();
//                for (int j = 0; j < selectedCells.length; j++) {
//                    if (selectedCells[j] instanceof RelationInputCell) {
//                        RelationBar relBar = (RelationBar) selectedCells[j].getBar();
//                        relBar.removeRelationInputCell(selectedCells[j].getParamName());
//                    } else if (selectedCells[j] instanceof RelationOutputCell) {
//                        RelationBar relBar = (RelationBar) selectedCells[j].getBar();
//                        relBar.removeRelationOutputCell(selectedCells[j].getParamName());
//                    } else if (selectedCells[j] instanceof RelationDerivedCell) {
//                        RelationBar relBar = (RelationBar) selectedCells[j].getBar();
//                        relBar.removeRelationDerivedCell(selectedCells[j].getParamName());
//                    } else if (selectedCells[j] instanceof InterfaceInputCell) {
//                        InterfaceBar itfBar = (InterfaceBar) selectedCells[j].getBar();
//                        itfBar.removeInterfaceInputCell(selectedCells[j].getParamName());
//                    } else if (selectedCells[j] instanceof InterfaceOutputCell) {
//                        InterfaceBar itfBar = (InterfaceBar) selectedCells[j].getBar();
//                        itfBar.removeInterfaceOutputCell(selectedCells[j].getParamName());
//                    }
//                }
//            }
//        });
//
//        cellWrapCheckBox = new JCheckBox("cell wrap", true);
//        cellWrapCheckBox.setBackground(UIUtil.REL_TOOLBAR_BG_COLOR);
//        cellWrapCheckBox.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent event) {
//                compRef.getImplementationEditor().setCellWrapEnabled(cellWrapCheckBox.isSelected());
//                for (int i = 0; i < compRef.getRelationEditor().getComponentCount(); i++) {
//                    compRef.getRelationBar(i).updateLayoutConstraintsOfSidePanels();
//                }
//                compRef.getInterfaceBar().updateLayoutConstraintsOfSidePanels();
//
//                UIUtil.updateEditorBounds(compRef);
//            }
//        });
//
////        add(new JButton("find relation..."));
////        add(new JButton("option..."));
//
////        add(loadModel1Button);
////        add(loadModel2Button);
////        add(unloadModelButton);
////        add(addRelButton);
////        add(addInputParamButton);
////        add(addOutputParamButton);
////        add(removeCellButton);
////        add(cellWrapCheckBox);
//
//
//        Component[] components = this.getComponents();
//        for (int i = 0; i < components.length; i++) {
//            Component comp = components[i];
//            if (comp instanceof JButton) {
//                comp.setFont(UIUtil.TOOL_BUTTON_FONT);
//            }
//        }
//    }

    public ComponentReference getComponentReference() {
        return compRef;
    }

	public void paintComponent(Graphics g) {
		int x, y;
		Rectangle clip = g.getClipBounds();

		int width = img.getHeight(this);
		int height = img.getWidth(this);

		if(width > 0 && height > 0) {
			for(x = clip.x; x < (clip.x + clip.width) ; x += width) {
				for(y = clip.y; y < (clip.y + clip.height) ; y += height) {
					g.drawImage(img, x, y, this);
				}
			}
		}
	}
}