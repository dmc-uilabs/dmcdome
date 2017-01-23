package mit.cadlab.dome3.plugin.catalog.core;

import edu.iupui.rg.ucum.units.UnitTab;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: Sangmok Han
 * Date: 2005. 11. 11.
 */
public class CoreUnitTest {
    public static void main(String[] args) {
//        initUnitSystem();
//        doUnitTest();

        initUnitSystem();
//        doTest1();
//        doTest2();
        doTest3();

//        System.out.println(paramB.getDriversOf(true));
//        System.out.println(paramB.getDriversOf(false));
//
//        System.out.println(paramC.getDriversOf(true));
//        System.out.println(paramC.getDriversOf(false));
//
//        System.out.println(paramF.getDriversOf(true));
//        System.out.println(paramF.getDriversOf(false));
//
//        System.out.println(paramFD.getDriversOf(true));
//        System.out.println(paramFD.getDriversOf(false));
//
//        System.out.println(paramY.getDriversOf(true));
//        System.out.println(paramY.getDriversOf(false));
//
//        System.out.println(paramX.getDrivensBy(true));
//        System.out.println(paramX.getDrivensBy(false));
//
//        System.out.println(paramB.getDrivensBy(true));
//        System.out.println(paramB.getDrivensBy(false));
//
//        System.out.println(paramC.getDrivensBy(true));
//        System.out.println(paramC.getDrivensBy(false));
//
//        System.out.println(paramFD.getDrivensOf(true));
//        System.out.println(paramFD.getDrivensOf(false));

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

    public static void doUnitTest() {
        CParameter testParam = new CRelationInputParameter("test", "A", null);
        System.out.println(testParam);
        //testParam.setValue(new Double(4.4));
        System.out.println(testParam);
        testParam.setUnit("m");
        System.out.println(testParam);
        testParam.setUnit("mm");
        System.out.println(testParam);
        testParam.setUnit("cm");
        System.out.println(testParam);
        testParam.setUnit("km");
        System.out.println(testParam);
        testParam.setUnit(CConstant.NEWTON_UNIT);
        System.out.println(testParam);

    }

    public static CModel doTest3() {

        CModel model = new CModel("Sample Model");
        CInterface myInterface = model.addInterface("My Interface");
        myInterface.addInputParameter("X");
        myInterface.addOutputParameter("Y");
        myInterface.addOutputParameter("Z");
        myInterface.setDependency("X", "Y");
        myInterface.setDependency("X", "Z");

        CImplementation implementation = myInterface.addImplementation("Default Implementation");
        CInterfaceInputParameter paramX = implementation.getNamingService().getInterfaceInputParameter("itf.X");
        CInterfaceOutputParameter paramY = implementation.getNamingService().getInterfaceOutputParameter("itf.Y");
        paramY.getMapping().setMappingScript("relA.D", Arrays.asList(new String[] { "relA.D" }));
        CInterfaceOutputParameter paramZ = implementation.getNamingService().getInterfaceOutputParameter("itf.Z");
        paramZ.getMapping().setMappingScript("relB.E", Arrays.asList(new String[] { "relB.E" }));

        CRemoteRelation rel1 = implementation.addRemoteRelation("relation A", "relA");
        rel1.setServerPort("cadlab27.mit.edu:8080");
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
        rel2.setRelationScript("");
        CRelationInputParameter paramE = rel2.addInputParameter("E");
        paramE.getMapping().setMappingScript("relA.C", Arrays.asList(new String[] { "relA.C" }));
        CRelationInputParameter paramF = rel2.addInputParameter("F");
        paramF.getMapping().setMappingScript("relA.A", Arrays.asList(new String[] { "relA.A" }));

        CRelationOutputParameter paramG = rel2.addOutputParameter("G");
        CDerivedParameter paramGD = paramG.addDerivedParameter("V2");

        rel2.setDependency("E", "G");
        rel2.setDependency("F", "G");

        System.out.println(model);
        System.out.println(implementation.getNamingService());
        Set changedItfParamNames = new HashSet();
        changedItfParamNames.add("X");
        System.out.println(implementation.getExecutionSequence(changedItfParamNames));
        System.out.println(implementation.getNamingService().findAllMappingNodes());

        Map modifiedParamMap = new HashMap();
        //paramX.setDoubleValue(10.0);
        modifiedParamMap.put("X", paramX);
        //System.out.println(implementation.generateScript(modifiedParamMap));

        return model;
    }

    public static CModel doTest1() {

        CModel model = new CModel("My Test");
        CInterface myInterface = model.addInterface("My Interface");
        myInterface.addInputParameter("X");
        myInterface.addOutputParameter("Y");
        CImplementation implementation = myInterface.addImplementation("My Impl");
        CInterfaceInputParameter paramX = implementation.getNamingService().getInterfaceInputParameter("itf.X");
        CInterfaceOutputParameter paramY = implementation.getNamingService().getInterfaceOutputParameter("itf.Y");
        paramY.getMapping().setMappingScript("Rel 2.F + 1", Arrays.asList(new String[] { "Rel 2.F" }));

        CLocalRelation rel1 = implementation.addLocalRelation("Rel 1");
        //System.out.println("********** REL1: " + rel1 + "/" + rel1.getNamespace() + "/" + rel1.getQualifiedName());
        CRelationInputParameter paramA = rel1.addInputParameter("A");
        paramA.getMapping().setMappingScript("itf.X", Arrays.asList(new String[] { "itf.X" }));
        CRelationInputParameter paramB = rel1.addInputParameter("B");
        paramB.getMapping().setMappingScript("Rel 1.A * 2", Arrays.asList(new String[] { "Rel 1.A" }));
        CRelationOutputParameter paramC = rel1.addOutputParameter("C");
        rel1.setDependency("A", "C");
        rel1.setDependency("B", "C");
        rel1.setRelationScript("Rel 1.C = Rel 1.A * Rel 1.B");

//        paramA.toGreenStatus();
//        paramB.toGreenStatus();
//        System.out.println(rel1.getDeterminableOutputParametersNames());

        CLocalRelation rel2 = implementation.addLocalRelation("Rel 2");
        CRelationInputParameter paramD = rel2.addInputParameter("D");
        paramD.getMapping().setMappingScript("Rel 1.C * 2", Arrays.asList(new String[] { "Rel 1.C" }));
        CRelationInputParameter paramE = rel2.addInputParameter("E");
        CRelationOutputParameter paramF = rel2.addOutputParameter("F");
        CDerivedParameter paramFD = paramF.addDerivedParameter("V2");
        rel2.setDependency("D", "F");
        rel2.setDependency("E", "F");
        rel2.setRelationScript("Rel 2.F = Rel 2.D + Rel 2.E");

        System.out.println(model);
        System.out.println(implementation.getNamingService());
        Set changedItfParamNames = new HashSet();
        changedItfParamNames.add("X");
        System.out.println(implementation.getExecutionSequence(changedItfParamNames));
        System.out.println(implementation.getNamingService().findAllMappingNodes());

        Map modifiedParamMap = new HashMap();
        //paramX.setDoubleValue(10.0);
        modifiedParamMap.put("X", paramX);
        System.out.println(implementation.generateScript(modifiedParamMap));

        return model;
    }

    public static CModel doTest2() {
        CModel model = new CModel("My Test");
        CInterface myInterface = model.addInterface("My Interface");
        myInterface.addInputParameter("X");
        myInterface.addOutputParameter("Y");
        myInterface.addOutputParameter("Z");
        CImplementation implementation = myInterface.addImplementation("My Impl");
        CInterfaceInputParameter paramX = implementation.getNamingService().getInterfaceInputParameter("itf.X");
        CInterfaceOutputParameter paramY = implementation.getNamingService().getInterfaceOutputParameter("itf.Y");
        paramY.getMapping().setMappingScript("Rel 1.D", Arrays.asList(new String[] { "Rel 1.D" }));
        CInterfaceOutputParameter paramZ = implementation.getNamingService().getInterfaceOutputParameter("itf.Z");
        paramZ.getMapping().setMappingScript("Rel 2.E", Arrays.asList(new String[] { "Rel 2.E" }));

        CRemoteRelation rel1 = implementation.addRemoteRelation("Rel 1");
        rel1.setServerPort("cadlab27.mit.edu:8080");
        rel1.setUser("guest");
        rel1.setPassword("");
        rel1.setSpace("Group");
        //rel1.setInterfacePath("Public/Tutorial examples/Polymer curing model/polymer curing interface");
        rel1.setInterfacePath("administrators/Public/Tutorial examples/Tutorial resources/Pin  model/pin interface");

        CRelationInputParameter paramA = rel1.addInputParameter("A");
        paramA.getMapping().setMappingScript("itf.X", Arrays.asList(new String[] { "itf.X" }));
        CRelationInputParameter paramB = rel1.addInputParameter("B");
        paramB.getMapping().setMappingScript("Rel 2.G", Arrays.asList(new String[] { "Rel 2.G" }));
        CRelationOutputParameter paramC = rel1.addOutputParameter("C");
        CRelationOutputParameter paramD = rel1.addOutputParameter("D");

        rel1.setDependency("A", "C");
        rel1.setDependency("A", "D");
        rel1.setDependency("B", "D");

        CLocalRelation rel2 = implementation.addLocalRelation("Rel 2");
        CRelationInputParameter paramE = rel2.addInputParameter("E");
        paramE.getMapping().setMappingScript("Rel 1.C", Arrays.asList(new String[] { "Rel 1.C" }));
        CRelationInputParameter paramF = rel2.addInputParameter("F");
        paramF.getMapping().setMappingScript("Rel 1.A", Arrays.asList(new String[] { "Rel 1.A" }));

        CRelationOutputParameter paramG = rel2.addOutputParameter("G");
        CDerivedParameter paramGD = paramG.addDerivedParameter("V2");

        rel2.setDependency("E", "G");
        rel2.setDependency("F", "G");

        System.out.println(model);
        System.out.println(implementation.getNamingService());
        Set changedItfParamNames = new HashSet();
        changedItfParamNames.add("X");
        System.out.println(implementation.getExecutionSequence(changedItfParamNames));
        System.out.println(implementation.getNamingService().findAllMappingNodes());

        Map modifiedParamMap = new HashMap();
        //paramX.setDoubleValue(10.0);
        modifiedParamMap.put("X", paramX);
        System.out.println(implementation.generateScript(modifiedParamMap));

        return model;
    }
}
