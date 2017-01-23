package mit.cadlab.dome3.plugin.catalog.demo;

import edu.iupui.rg.ucum.units.UnitTab;
import mit.cadlab.dome3.plugin.catalog.core.CImplementation;
import mit.cadlab.dome3.plugin.catalog.core.CLog;
import mit.cadlab.dome3.plugin.catalog.core.CModel;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.*;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.serialization.DomeSerialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: Sangmok Han
 * Date: 2006. 7. 28.
 */
public class HowToHandleMatrixAndVector {

    public static void main(String[] args) {
        initUnitSystem();
        CLog.setDebug(true);
//        evaluateModel_1();
//        evaluateModel_2();
//        evaluateModel_3();
        evaluateModel_4();

        /* following code shows how to create CMatrix object from string. */
//        CMatrix a = new CMatrix(DataObjectUtil.createMatrix("[1 2 3; 4 5 6]|Real"), new CUnit("mm"));
//        CMatrix b = new CMatrix(DataObjectUtil.createMatrix("[3 2; 6 7; 4 5]|Integer"), new CUnit("kg"));
//        CMatrix c = (CMatrix) a.multiply(b);
//        CMatrix d = (CMatrix) a.multiply(new Double(3));
//        System.out.println(c + " / " + c.getUnit());
//        System.out.println(d + " / " + d.getUnit());

    }


    public static void evaluateModel_1() {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/dome3/models/catalog/matrixvectormodel-CATALOG.dml");
        CModel model = modelAndIDContainer.getModel();

        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        /* modify interface parameter values and evaluate the model */
        ((CMatrix) context.getInterfaceDataObject("A")).setMatrixValue("[1 3 4; 4 5 6]");
        ((CVector) context.getInterfaceDataObject("B")).setVectorValue("[1 4 7]");
        System.out.println("### first evaluation starts ###");
        context.evaluate(new String[] { "A", "B" });
        System.out.println("### first evaluation ends ###");
        System.out.println(context.getDataObject("itf.C"));
        System.out.println(context.getDataObject("itf.D"));

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CVector) context.getInterfaceDataObject("B")).setVectorValue("[5 6 7]");
        System.out.println("### second evaluation starts ###");
        context.evaluate(new String[] { "B" });
        System.out.println("### second evaluation ends ###");
        System.out.println(context.getDataObject("itf.C"));
        System.out.println(context.getDataObject("itf.D"));


        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void evaluateModel_2() {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/dome3/models/catalog/subscribe_matvec-CATALOG.dml");
        CModel model = modelAndIDContainer.getModel();

        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        /* modify interface parameter values and evaluate the model */
        ((CReal) context.getInterfaceDataObject("A")).setDoubleValue(4);
        ((CInteger) context.getInterfaceDataObject("B")).setIntegerValue(3);
        System.out.println("### first evaluation starts ###");
        context.evaluate(new String[] { "A", "B" });
        System.out.println("### first evaluation ends ###");
        System.out.println(context.getDataObject("itf.C"));
        //System.out.println(context.getDataObject("itf.D"));

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CInteger) context.getInterfaceDataObject("B")).setIntegerValue(5);
        System.out.println("### second evaluation starts ###");
        context.evaluate(new String[] { "B" });
        System.out.println("### second evaluation ends ###");
        System.out.println(context.getDataObject("itf.C"));

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void evaluateModel_3() {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/dome3/models/catalog/filemodel-CATALOG.dml");
        CModel model = modelAndIDContainer.getModel();

        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);
        context.setWorkingDirectory("C:/temp_cmodel/first_run");

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        /* modify interface parameter values and evaluate the model */
        System.out.println("### first evaluation starts ###");
        context.evaluate();
        System.out.println("### first evaluation ends ###");
        System.out.println("itf.C=" + context.getDataObject("itf.C"));
        System.out.println("itf.D=" + context.getDataObject("itf.D"));

        context.setWorkingDirectory("C:/temp_cmodel/second_run");

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CFile) context.getInterfaceDataObject("B")).setFileValue(new String("second B value").getBytes());
        System.out.println("### second evaluation starts ###");
        context.evaluate(new String[] { "B" });
        System.out.println("### second evaluation ends ###");
        System.out.println(context.getDataObject("itf.C"));
        System.out.println(context.getDataObject("itf.D"));

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void evaluateModel_4() {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/dome3/models/catalog/matrix_vector_getat_test-CATALOG.dml");
        CModel model = modelAndIDContainer.getModel();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        /* create a context used for a model evaluation */
        EvaluationContext context = new EvaluationContext(impl);

        /* add listeners to listen to evaluation events */
        context.addEvaluationListener(new SampleEvaluationListener(context));

        /* modify interface parameter values and evaluate the model */
        System.out.println("### first evaluation starts ###");
        context.evaluate();
        System.out.println("### first evaluation ends ###");
        System.out.println("itf.C=" + context.getDataObject("itf.C"));
        System.out.println("itf.D=" + context.getDataObject("itf.D"));

        /* let the context know which parameters are changed and re-evaluate the model */
        ((CMatrix) context.getInterfaceDataObject("A")).setMatrixValue("[3 4; 6 7]");
        System.out.println("### second evaluation starts ###");
        context.evaluate(new String[] { "A" });
        System.out.println("### second evaluation ends ###");
        System.out.println(context.getDataObject("itf.C"));
        System.out.println(context.getDataObject("itf.D"));

        /* closing the context will clean up DomeConnection and RuntimeInterface associated with it */
        context.close();
    }

    public static void initUnitSystem() {
        try {
            InputStream is = new ByteArrayInputStream(edu.iupui.rg.ucum.CommonUnit.DATA.getBytes());
            UnitTab.read(is);
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading unit data");
        }
    }

}
