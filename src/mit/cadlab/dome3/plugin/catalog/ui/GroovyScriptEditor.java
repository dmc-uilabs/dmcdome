package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 3.
 */
public class GroovyScriptEditor extends JPanel {
    private JTextPaneWithCodeCompletionPopup scriptEditorPane;
    private JScrollPane scriptEditorScrollPane;

    public GroovyScriptEditor(RelationDialog relDialog) {
        super();
        scriptEditorPane = new GroovyScriptTextPane(relDialog.getInputParameterTable().getModel(), relDialog.getOutputParameterTable().getModel());
        scriptEditorScrollPane = new JScrollPane(scriptEditorPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scriptEditorScrollPane, BorderLayout.CENTER);
    }

    public void setPreferredSize(Dimension size) {
        super.setPreferredSize(size);
        int width = size.width;
        int height = size.height - 6;
        scriptEditorScrollPane.setPreferredSize(new Dimension(width, height));
        scriptEditorPane.setLayerSize(100, 100);
    }

//    public JTextPane getTextPane() {
//        return scriptEditorPane.getTextPane();
//    }

    public String getText() {
        return scriptEditorPane.getTextPane().getText();
    }

    public void setText(String text) {
        scriptEditorPane.getTextPane().setText(text);
        scriptEditorPane.forceLayerResizing();
    }
}

