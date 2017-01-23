package mit.cadlab.dome3.plugin.catalog.demo;

import edu.iupui.rg.ucum.units.UnitTab;
import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.core.dataobject.CReal;
import mit.cadlab.dome3.plugin.catalog.runtime.EvaluationContext;
import mit.cadlab.dome3.plugin.catalog.serialization.DomeSerialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * User: Sangmok Han
 * Date: 2006. 7. 3.
 */
public class TutorialModel1 {

    /**
     * !!!!! NOTE !!!!!
     * Please use HowToCreateCatalogModel and HowToEvaluateCatalogModel instead of this class. It has better code comments.
     */
    public static void main(String[] args) {
        initUnitSystem();
        CLog.setDebug(true);
        test1();
        test2();
        test3();
    }

    /** shows how to load a Catalog model and how evaluate the implementation of an interface */
    public static void test1() {
        CModel model = loadModel();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        EvaluationContext context = new EvaluationContext(impl);
        ((CReal) context.getInterfaceDataObject("width")).setDoubleValue(50);
        ((CReal) context.getInterfaceDataObject("height")).setDoubleValue(80);
        //context.setValidationMode(false); // its default value is false
        context.setValidationMode(false);
        context.addEvaluationListener(new SampleEvaluationListener(context));
        context.evaluate(new String[] { "width", "height" }); // modified param names
    }

    /** shows how to create a Catalog model and how validation mode works */
    public static void test2() {
        CModel model = createModel();
        CImplementation impl = model.getInterface("My Interface").getImplementation("Default Implementation");

        EvaluationContext context = new EvaluationContext(impl);
        ((CReal) context.getInterfaceDataObject("X")).setDoubleValue(50);
        context.setValidationMode(true);
        context.addEvaluationListener(new SampleEvaluationListener(context));
        context.evaluate(new String[] { "X" }); // modified param names
    }

    /** shows how to load a Catalog model and how evaluate the implementation of an interface */
    public static void test3() {
        CModel model = loadModel3();
        CImplementation impl = model.getInterface("default interface").getImplementation("default implementation");

        EvaluationContext context = new EvaluationContext(impl);
        ((CReal) context.getInterfaceDataObject("len")).setDoubleValue(50);
        ((CReal) context.getInterfaceDataObject("dia")).setDoubleValue(80);
        //context.setValidationMode(false); // its default value is false
        context.setValidationMode(false);
        context.addEvaluationListener(new SampleEvaluationListener(context));
        context.evaluate(new String[] { "len", "dia" }); // modified param names
        context.close();
    }

    public static CModel loadModel() {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/dome3/models/catalog/quickeval1-CATALOG.dml");
        return modelAndIDContainer.getModel();
    }

    public static CModel loadModel3() {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("C:/dome3/models/catalog/quickeval3-CATALOG.dml");
        return modelAndIDContainer.getModel();
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

    public static CModel createModel() {

        CModel model = new CModel("Sample Model");
        CInterface myInterface = model.addInterface("My Interface");
        myInterface.addInputParameter("X");
        myInterface.addOutputParameter("Y");
        myInterface.addOutputParameter("Z");
        myInterface.setDependency("X", "Y");
        myInterface.setDependency("X", "Z");

        CParameter xITF = myInterface.getInputParameter("X");
        xITF.setDataType(CConstant.REAL_DATA_TYPE);
        xITF.setDefaultValue("30");
        xITF.setUnit("cm");

        CImplementation implementation = myInterface.addImplementation("Default Implementation");

        implementation.synchronizeInterfaceParameters();

        CInterfaceInputParameter paramX = implementation.getNamingService().getInterfaceInputParameter("itf.X");
        CInterfaceOutputParameter paramY = implementation.getNamingService().getInterfaceOutputParameter("itf.Y");
        paramY.getMapping().setMappingScript("relA.D", Arrays.asList(new String[] { "relA.D" }));
        CInterfaceOutputParameter paramZ = implementation.getNamingService().getInterfaceOutputParameter("itf.Z");
        paramZ.getMapping().setMappingScript("relB.E", Arrays.asList(new String[] { "relB.E" }));

        CRemoteRelation rel1 = implementation.addRemoteRelation("relation A", "relA");
        rel1.setServerPort("localhost:8080");
        rel1.setUser("guest");
        rel1.setPassword("");
        rel1.setSpace("Group");
        rel1.setInterfacePath("administrators/Public/Tutorial examples/Tutorial resources/Pin  model/pin interface");

        CRelationInputParameter paramA = rel1.addInputParameter("A");
        paramA.getMapping().setMappingScript("itf.X", Arrays.asList(new String[] { "itf.X" }));
        CRelationInputParameter paramB = rel1.addInputParameter("B");
        paramB.getMapping().setMappingScript("relB.G", Arrays.asList(new String[] { "relB.G" }));
        CRelationOutputParameter paramC = rel1.addOutputParameter("C");
        CRelationOutputParameter paramD = rel1.addOutputParameter("D");

        rel1.setDependency("A", "C");
        rel1.setDependency("A", "D");
        rel1.setDependency("B", "D");

        CLocalRelation rel2 = implementation.addLocalRelation("relation B", "relB");
        rel2.setRelationScript("G << E + F");

        CRelationInputParameter paramE = rel2.addInputParameter("E");
        paramE.getMapping().setMappingScript("relA.C", Arrays.asList(new String[] { "relA.C" }));
        CRelationInputParameter paramF = rel2.addInputParameter("F");
        paramF.getMapping().setMappingScript("relA.A", Arrays.asList(new String[] { "relA.A" }));

        CRelationOutputParameter paramG = rel2.addOutputParameter("G");

        rel2.setDependency("E", "G");
        rel2.setDependency("F", "G");

        return model;
    }
}
