package mit.cadlab.dome3.plugin.catalog.pcc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2006. 1. 2.
 */
public class CodeBase {

    public static final int MINIMUM_CODELINE_SIGNATURE_LENGTH = 3;
    public static final String DEFAULT_CODE_BASE = "default";
    private static Map instances = new HashMap();

    private String codeBaseName;

    /* code token signature to starting row index of signature instances. (ex) SN=SN*N+SN*N;\nSN=SN*N+SN*N; -> [3, 6, 10]*/
    private Map signatureToCodeLinesMap;

    static {
        addInstance(DEFAULT_CODE_BASE);
    }

    private CodeBase(String codeBaseName) {
        this.codeBaseName = codeBaseName;
        signatureToCodeLinesMap = new HashMap();
    }

    /**
     * possibly there can be separated multiple code bases
     * they are retrieved by name of code base
     */
    public static CodeBase getInstance(String codeBaseName) {
        return (CodeBase) instances.get(codeBaseName);
    }

    /** get the default code base */
    public static CodeBase getInstance() {
        return getInstance(DEFAULT_CODE_BASE);
    }

    public String getCodeBaseName() {
        return codeBaseName;
    }

    /** this method is not going to be implemented very soon, but shows how code base could be extended */
    public static void addInstance(String codeBaseName) {
        instances.put(codeBaseName, new CodeBase(codeBaseName));
    }

    /** returns a array of code token signatures starting with given cueStr's code token signatures.
     * a Set of code token signatures is returned. it returns an empty set if not found */
    public Set findSignatures(String cueStr) {
        String cueSignature = TokenMatrix.createCodeTokenSignature(cueStr);
        Set signatureSet = new TreeSet();
        for (Iterator i = signatureToCodeLinesMap.keySet().iterator(); i.hasNext(); ) {
            String aSignature = (String) i.next();
            if (aSignature.startsWith(cueSignature)) {
                signatureSet.add(aSignature);
            }
        }
        return signatureSet;
    }

    /** return TokenMatrix corresponding to aSignature. returns null if not found */
    public TokenMatrix getTokenMatrix(String aSignature) {
        TokenMatrix matrix = new TokenMatrix();
        Set codeLineSet = (Set) signatureToCodeLinesMap.get(aSignature);
        for (Iterator i = codeLineSet.iterator(); i.hasNext(); ) {
            CodeLine codeLine = (CodeLine) i.next();
            matrix.addCode(codeLine.getCodeString(), codeLine.getCodeReference());
        }

        if (matrix.getRowSize() > 0) {
            return matrix;
        } else {
            return null;
        }
    }

    /** remove all index */
    public void clearIndex() {
        signatureToCodeLinesMap.clear();
    }

    /**
     * if removeIndexesThatStartWith is true, entries that start with srcId is removed
     * (ex) to remove all indexes of implementation "implA" removeIndex("implA/", true);
     * (ex) to remove all indexes of implementation "implA" and relation "relB" removeIndex("implA/relB.", true);
     * (ex) to remove all indexes of implementation "implA" and relation "relB" removeIndex("implA/relB.width", false);
     */
    public void removeIndex(String srcId, boolean removeIndexesThatStartWith) {
        Set entrySet = signatureToCodeLinesMap.entrySet();
        for (Iterator i = entrySet.iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            Set rowSet = (Set) entry.getValue();
            for (Iterator j = rowSet.iterator(); j.hasNext(); ) {
                CodeLine codeLine = (CodeLine) j.next();
                if (removeIndexesThatStartWith) {
                    if (codeLine.getCodeReference().getSourceId().startsWith(srcId)) {
                        j.remove();
                    }
                } else {
                    if (codeLine.getCodeReference().getSourceId().equals(srcId)) {
                        j.remove();
                    }
                }
            }
            /* a signature has an empty rowSet, such a signature no more exists */
            if (rowSet.isEmpty()) {
                i.remove();
            }
        }
    }

    /** indexing source is supplied from a String instance */
    public void index(String srcId, String srcText) {
        String codeLineStr = srcText.trim();
        String aSignature = TokenMatrix.createCodeTokenSignature(codeLineStr);
        CodeLine codeLine = new CodeLine(codeLineStr, new CodeReference(srcId, 0));

        /* find & remove the previous CodeLine instance in signatureToCodeLinesMap */
        for (Iterator i = signatureToCodeLinesMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            Set rowSet = (Set) entry.getValue();
            if (rowSet.contains(codeLine)) {
                rowSet.remove(codeLine);
                if (rowSet.isEmpty()) {
                    i.remove();
                }
                /* we know there is only one match. break iteration */
                break;
            }
        }

        /* if a signature is too short, we don't want to index it */
        if (aSignature.length() < MINIMUM_CODELINE_SIGNATURE_LENGTH) {
            return;
        }

        Set rowSet = (Set) signatureToCodeLinesMap.get(aSignature);
        if (rowSet == null) {
            rowSet = new TreeSet();
            signatureToCodeLinesMap.put(aSignature, rowSet);
        }
        rowSet.add(codeLine);
    }

    /** index source is supplied from the given reader, srcId is used to indentify the source of the indexed contents, which is usually a file name.  */
    public void index(String srcId, Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String codeLine = br.readLine();
        int lineNo = 0;
        while (codeLine != null) {
            codeLine = codeLine.trim();
            String aSignature = TokenMatrix.createCodeTokenSignature(codeLine);

            /* if a signature is too short, we don't want to index it */
            if (aSignature.length() < MINIMUM_CODELINE_SIGNATURE_LENGTH) {
                lineNo++;
                codeLine = br.readLine();
                continue;
            }

            Set rowSet = (Set) signatureToCodeLinesMap.get(aSignature);
            if (rowSet == null) {
                rowSet = new TreeSet();
                signatureToCodeLinesMap.put(aSignature, rowSet);
            }
            rowSet.add(new CodeLine(codeLine, new CodeReference(srcId, lineNo)));

            lineNo++;
            codeLine = br.readLine();
        }
    }

    public String toString() {
        return "[CodeBase: name=" + codeBaseName + ", signatureToCodelineMap=" + signatureToCodeLinesMap + "]";
    }
}