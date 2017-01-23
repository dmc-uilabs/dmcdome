package mit.cadlab.dome3.plugin.catalog.ui;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sangmok Han
 * Date: 2006. 2. 6.
 */
public class GroovyScriptTextPane extends JTextPaneWithCodeCompletionPopup {
    private TableModel iParamModel;
    private TableModel oParamModel;


    public GroovyScriptTextPane(TableModel iParamModel, TableModel oParamModel) {
        //super(UIUtil.GROOVY_EDITOR_FONT, UIUtil.GROOVY_EDITOR_FONT, 120, 4, GroovyDelimiterList.DELIM_LIST);
        super(UIUtil.GROOVY_EDITOR_FONT, UIUtil.GROOVY_EDITOR_FONT, UIUtil.CODE_COMPLETION_POPUP_WIDTH, 4, new GroovyDelimiterList(iParamModel, oParamModel));
        this.iParamModel = iParamModel;
        this.oParamModel = oParamModel;
    }

    public ColorizedDocument getColorizedDocument() {
        return (ColorizedDocument) getTextPane().getDocument();
    }

    public java.util.List populateCandidates() {
        JTextPane textPane = super.getTextPane();
        Caret caret = textPane.getCaret();

        if (caret.getDot() == caret.getMark()) {
            String query = UIUtil.createAutomaticQuery(textPane);
            return createCandidateList(query);
        } else {
            try {
                return createCandidateList(textPane.getText(Math.min(caret.getMark(), caret.getDot()), Math.abs(caret.getMark() - caret.getDot())));
            } catch (BadLocationException e) {
                e.printStackTrace();
                return java.util.Collections.EMPTY_LIST;
            }
        }
    }

    public List createCandidateList(String query) {
        List candidates = new ArrayList();
        for (int i = 0; i < iParamModel.getRowCount(); i++) {
            String paramName = (String) iParamModel.getValueAt(i, 0);

            /* if there is no query, collect all parameters */
            /* if there is a query, collect parameters that start with a query, but are not equal to the query */
            if (query == null || (paramName.startsWith(query) && ! paramName.equals(query))) {
                int cropPos = ((query == null) ? 0 : query.length());
                candidates.add(new CodeCompletionCandidate(CodeCompletionCandidate.VARNAME_CC_TYPE, paramName, cropPos));
            }
        }

        for (int i = 0; i < oParamModel.getRowCount(); i++) {
            String paramName = (String) oParamModel.getValueAt(i, 0);
            if (query == null || (paramName.startsWith(query) && ! paramName.equals(query))) {
                int cropPos = ((query == null) ? 0 : query.length());
                candidates.add(new CodeCompletionCandidate(CodeCompletionCandidate.VARNAME_CC_TYPE, paramName, cropPos));
            }
        }

        return candidates;
    }
}
