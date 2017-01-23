package mit.cadlab.dome3.api.domewc;

import mit.cadlab.dome3.api.ParameterValueChangeEvent;
import mit.cadlab.dome3.api.ParameterValueChangeListener;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeMatrix;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;

import java.io.PrintWriter;
import java.util.Vector;

/**
 * This listener is a variation of ValueChangeListenerForWebClient. The difference here is that it assumes that vectors
 * and matrices are displayed in a web page as text, e.g. "Matrix (3x3 elements)", instead of as an array of text fields
 */
public class ValueChangeListenerForWebClient2 implements ParameterValueChangeListener {
    PrintWriter writer = null;
    private boolean debug = false;
	String START_OF_SCRIPT = "<script language='JavaScript'>";
	String END_OF_SCRIPT= "</script>";

    public ValueChangeListenerForWebClient2(PrintWriter writer, boolean debug) {
        this.writer = writer;
        this.debug = debug;
    }

    public void valueChanged(ParameterValueChangeEvent event) {
        String paramName = event.getInterfaceParameterClient().getName();
        if (paramName.indexOf("'") != -1 || paramName.indexOf("\"") != -1) {
            System.out.println("Parameter Name contains ' or \". This could result in JavaScript error.");
        }

        // a matrix is displayed on the page in a <span> as, e.g., "Matrix (3x3 elements)"
        if (DomeMatrix.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            DomeMatrix matrix = (DomeMatrix) event.getInterfaceParameterClient().getCurrentDataObject();
            Vector mat = new Vector(matrix.getValues());
            int numrow, numcol;
            if (mat.size() == 0) {
                numrow = 0;
                numcol = 0;
            } else {
                Vector row = (Vector) mat.get(0);
                numrow = mat.size();
                numcol = row == null ? 0 : row.size();
            }
            String scriptStr = (START_OF_SCRIPT + "window.parent.document.getElementById('" + paramName + "').innerHTML='Matrix (" + numrow + "x" + numcol  + " elements)';" + END_OF_SCRIPT);
            writer.println(scriptStr);
            if (debug) {
                System.out.println(scriptStr);
            }
        } // a vector is displayed on the page in a <span> as, e.g., "Vector (3 elements)"
        else if (DomeVector.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            DomeVector vector = (DomeVector) event.getInterfaceParameterClient().getCurrentDataObject();
            int size = vector.getSize();
            String scriptStr =(START_OF_SCRIPT + "window.parent.document.getElementById('" + paramName + "').innerHTML='Vector (" + size + " "+(size>1?"elements":"element")+")';" + END_OF_SCRIPT);
            writer.println(scriptStr);
            if (debug) { System.out.println(scriptStr); }
        } else if (DomeEnumeration.TYPE_INFO.getTypeName().equals(event.getInterfaceParameterClient().getCurrentType())) {
            DomeEnumeration enm = (DomeEnumeration) event.getInterfaceParameterClient().getCurrentDataObject();
            String scriptStr =(START_OF_SCRIPT + "window.parent.document.getElementById('" + paramName + "').selectedIndex=" + enm.getLastSelection() + ";" + END_OF_SCRIPT);
            writer.println(scriptStr);
            if (debug) { System.out.println(scriptStr); }
        } else {
            String scriptStr = (START_OF_SCRIPT + "window.parent.document.getElementById('" + paramName + "').value='" + event.getNewValues().get(0) + "';" + END_OF_SCRIPT);
            writer.println(scriptStr);
            if (debug) { System.out.println(scriptStr); }
        }
        writer.flush();
    }
}