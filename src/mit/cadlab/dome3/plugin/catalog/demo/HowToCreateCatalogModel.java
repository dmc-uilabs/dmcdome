package mit.cadlab.dome3.plugin.catalog.demo;

import mit.cadlab.dome3.plugin.catalog.core.*;
import mit.cadlab.dome3.plugin.catalog.serialization.DomeSerialization;
import mit.cadlab.dome3.plugin.catalog.serialization.IDContainer;

import java.util.Iterator;
import java.util.Set;

/**
 * This demo class creates two catalog models and save them to C:/dome3/models/catalog/
 * It shows how to create CModel, CInterface, CImplementation, CRelation, and CParameter.
 * To see how those created models look like, you may open the generated DML files in the Catalog Model Builder.
 *
 * User: Sangmok Han
 * Date: 2006. 7. 17.
 */
public class HowToCreateCatalogModel {

    public static void main(String[] args) {
        CLog.setDebug(false);

        /* create & save the first model */
        CModel model_1 = createModel_1();
        System.out.println("object map of model 1: " + model_1.getInterface("default interface").getImplementation("default implementation").getNamingService().getMap());
        IDContainer idContainer_1 = new IDContainer();
        String filePath_1 = "C:/dome3/models/catalog/tutorial_model_1-CATALOG.dml";
        try {
            DomeSerialization.save(model_1, idContainer_1, filePath_1);
        } catch (Exception e) {
            System.out.println("fail to save CModel as " + filePath_1 + " : exception = " + e.toString());
        }

        /* create & save the second model */
        CModel model_2 = createModel_2();
        System.out.println("object map of model 2: " + model_2.getInterface("default interface").getImplementation("default implementation").getNamingService().getMap());
        IDContainer idContainer_2 = new IDContainer();
        String filePath_2 = "C:/dome3/models/catalog/tutorial_model_2-CATALOG.dml";
        try {
            DomeSerialization.save(model_2, idContainer_2, filePath_2);
        } catch (Exception e) {
            System.out.println("fail to save CModel as " + filePath_2 + " : exception = " + e.toString());
        }
    }

    /** my diameter ad */
    public static CModel createModel_1() {
        /* create a model called 'My Demo Model,' having an interface called 'default interface' */
        CModel model = new CModel("My Demo Model");
        CInterface itf = model.addInterface("default interface");

        /* three real parameter X, Y, and Z, whose default values are zero and unit information is not specified, are created */
        CInterfaceInputParameter diameterItfParam = itf.addInputParameter("hole diameter");
        CInterfaceInputParameter depthItfParam = itf.addInputParameter("hole depth");
        CInterfaceOutputParameter densityItfParam = itf.addOutputParameter("cost density");
        CInterfaceOutputParameter pinVolItfParam = itf.addOutputParameter("pin volume");
        itf.setDependency("hole diameter", "cost density");
        itf.setDependency("hole depth", "cost density");
        itf.setDependency("hole diameter", "pin volume");
        itf.setDependency("hole depth", "pin volume");

        /* data type, default value, and unit information can be modified in the following way */
        diameterItfParam.setDataType(CConstant.REAL_DATA_TYPE);
        diameterItfParam.setDefaultValue("0.5");
        diameterItfParam.setUnit("[in_us]"); // inch

        depthItfParam.setDataType(CConstant.REAL_DATA_TYPE);
        depthItfParam.setDefaultValue("1");
        depthItfParam.setUnit("[in_us]"); // inch

        densityItfParam.setDataType(CConstant.REAL_DATA_TYPE);
        densityItfParam.setDefaultValue("0");
        densityItfParam.setUnit("[$_p_cm3]"); // dollar per cubic centimeter

        pinVolItfParam.setDataType(CConstant.REAL_DATA_TYPE);
        pinVolItfParam.setDefaultValue("0");
        pinVolItfParam.setUnit("[c_mm]"); // cubic millimeter

        /* create an implementation called 'default implementation' for 'default interface' */
        CImplementation impl = itf.addImplementation("default implementation");

        /* CNamingService instance is used to locate resources such as CParameter, CRelation, and CMapping in the associated CImplementation */
        /* Note that a unique, qualified name should be used to locate those resources.
         * for example, "width" in the interface of an implementation can be accessed using its qualified name "itf.width", by invoking namingService.getInterfaceInputParameter("itf.width");. */
        CNamingService namingService = impl.getNamingService();

        /* below method invocation is optional in this sample code.
           it copies the parameter definition of the interface to the implementation.
           it is used to apply any modification made to the definition of an interface to its implementations,
           which might have been added before the modification is made. */
        // implementation.synchronizeInterfaceParameters();

        /* following code shows how to add remote relation: relA */
        CRemoteRelation relA = impl.addRemoteRelation("Pin Model/pin interface", "relA");
        relA.setServerPort("localhost:8080");
        relA.setUser("guest");
        relA.setPassword("");
        relA.setSpace("Group");
        relA.setInterfacePath("administrators/Public/Tutorial examples/Tutorial resources/Pin  model/pin interface");
        /* following code shows how to configure remote relation automatically: it automatically adds input/output parameters and sets dependency for the relation. */
        relA.configureRemoteRelationUsingRuntimeInterfaceInformation();

        /* following code shows how to add remote relation: relB */
        CRemoteRelation relB = impl.addRemoteRelation("Pin Model/pin interface", "relB");
        relB.setServerPort("localhost:8080");
        relB.setUser("guest");
        relB.setPassword("");
        relB.setSpace("Group");
        relB.setInterfacePath("administrators/Public/Tutorial examples/Tutorial resources/Pin  model/pin interface");
        /* following code shows how to configure remote relation automatically: it automatically adds input/output parameters and sets dependency for the relation. */
        relB.configureRemoteRelationUsingRuntimeInterfaceInformation();

        /* following code shows how to add local relation: relC */
        CLocalRelation relC = impl.addLocalRelation("density calc relation", "relC");
        CRelationInputParameter volOfLocalRel = relC.addInputParameter("volume");
        volOfLocalRel.setUnit("[c_cm]"); // cubic centimeter
        CRelationInputParameter costOfLocalRel = relC.addInputParameter("cost");
        costOfLocalRel.setUnit("$");
        CRelationOutputParameter densityOfLocalRel = relC.addOutputParameter("density");
        densityOfLocalRel.setUnit("[$_p_cm3]"); // compatible unit conversion
        /* uncomment the below line to see how IllegalArgumentException("Unit conversion exception") thown when it meets an erroneous unit conversion
         * and how it is handled to be collected as a RelationExecutionFailure instance, which can be accessed using EvaluationContext.getRelationExecutionFailures() */
        //densityOfLocalRel.setUnit("kg"); // incompatible unit conversion
        relC.setRelationScript("density << cost / volume");
        relC.setDependency("cost", "density");
        relC.setDependency("volume", "density");

        /* following code is optional. it shows alternative way of getting references to CRelationInputParameter or CRelationOutputParameter instances */
        CRelationInputParameter diameterOfRelA = namingService.getRelationInputParameter("relA.diameter");
        CRelationOutputParameter headHeightOfRelA = namingService.getRelationOutputParameter("relA.head height");
        CRelationInputParameter volumeOfRelC = namingService.getRelationInputParameter("relC.volume");
        CRelationOutputParameter densityOfRelC = namingService.getRelationOutputParameter("relC.density");
        CInterfaceOutputParameter costDensityOfItf = namingService.getInterfaceOutputParameter("itf.cost density");
        CInterfaceInputParameter holeDepthOfItf = namingService.getInterfaceInputParameter("itf.hole depth");

        /* following code shows how to setup mappings */
        relA.getInputParameter("length").getMapping().setMappingScript("itf.hole depth * 2");
        relA.getInputParameter("diameter").getMapping().setMappingScript("itf.hole diameter * 2");

        relB.getInputParameter("length").getMapping().setMappingScript("itf.hole depth * 3");
        relB.getInputParameter("diameter").getMapping().setMappingScript("itf.hole diameter * 3");

        relC.getInputParameter("volume").getMapping().setMappingScript("relA.volume + relB.volume");
        relC.getInputParameter("cost").getMapping().setMappingScript("relA.cost + relB.cost");

        impl.getOutputParameter("cost density").getMapping().setMappingScript("relC.density");
        impl.getOutputParameter("pin volume").getMapping().setMappingScript("relC.volume");

        /* following code shows how to find mapping scripts referencing a parameter given as an argument such as "itf.hole diameter" */
        System.out.println("find parameters whose mapping script references itf.hole diameter: " + namingService.findParametersWhoseMappingScriptReferences("itf.hole diameter"));

        /* When one wants to remove a parameter from a relation or an interface,
         * first call clearMappingScriptsReferencing() or clearAllMappingsBetweenInterfaceAndRelations() method
         * to make the parameter no longer referenced in the mapping scripts. */
//        impl.getNamingService().clearMappingScriptsReferencing("itf.hole depth");
//        impl.getNamingService().clearAllMappingsBetweenInterfaceAndRelations();

        /* one can query causality defined inside an implementation to find independent/intermediate/result parameters.
         * The result can be used to create an auto-generated interface, often called "default interface" */
        System.out.println("independent parameters: " + namingService.queryIndependentParameters());
        System.out.println("intermediate parameters: " + namingService.queryIntermediateParameters());
        System.out.println("result parameters: " + namingService.queryResultParameters());
        System.out.println("indeterminate parameters: " + namingService.queryIndeterminateParameters());
        System.out.println("input parameters: " + namingService.queryInputParameters());
        System.out.println("output parameters: " + namingService.queryOutputParameters());

        return model;
    }

    public static CModel createModel_2() {
        /* create a model called 'My Demo Model,' having an interface called 'default interface' */
        CModel model = new CModel("My Demo Model");
        CInterface myInterface = model.addInterface("default interface");

        /* create an implementation called 'default implementation' for 'default interface' */
        CImplementation impl = myInterface.addImplementation("default implementation");

        /* following code shows how to add remote relation. */
        CRemoteRelation rel1 = impl.addRemoteRelation("solar radiation on tilted surface", "A");
        rel1.setServerPort("18.80.1.101:8080");
        rel1.setUser("guest");
        rel1.setPassword("");
        rel1.setSpace("Server");
        rel1.setInterfacePath("Public/solar radiation/solar radiation on tilted surface/solar radiation on tilted surface - complete Interface");
        rel1.configureRemoteRelationUsingRuntimeInterfaceInformation();

        /* following code shows how to add remote relation. */
        CRemoteRelation rel2 = impl.addRemoteRelation("extraterrestrial radiation", "B");
        rel2.setServerPort("18.80.1.101:8080");
        rel2.setUser("guest");
        rel2.setPassword("");
        rel2.setSpace("Server");
        rel2.setInterfacePath("Public/solar radiation/extraterrestrial radiation/extraterrestrial radiation estimates Interface");
        rel2.configureRemoteRelationUsingRuntimeInterfaceInformation();

        /* set up mappings among relation parameters */
        CRelationInputParameter A_extraterrestrial_radiation = impl.getNamingService().getRelationInputParameter("A.extraterrestrial radiation");
        A_extraterrestrial_radiation.getMapping().setMappingScript("B.extraterrestrial radiation I");

        /* create default interface parameters */
        Set queriedInputParams = impl.getNamingService().queryInputParameters();
        for (Iterator i = queriedInputParams.iterator(); i.hasNext();) {
            CRelationInputParameter relParam = (CRelationInputParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceInputParameter itfParam = myInterface.addInputParameter(paramNameInInterface);
            itfParam.setDataType(relParam.getDataType());
            itfParam.setDefaultValue(relParam.getDefaultValue());
            itfParam.setUnit(relParam.getUnit());
        }

        Set queriedOutputParams = impl.getNamingService().queryOutputParameters();
        for (Iterator i = queriedOutputParams.iterator(); i.hasNext();) {
            CParameter relParam = (CParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceOutputParameter itfParam = myInterface.addOutputParameter(paramNameInInterface);
            itfParam.setDataType(relParam.getDataType());
            itfParam.setDefaultValue(relParam.getDefaultValue());
            itfParam.setUnit(relParam.getUnit());
        }

        impl.synchronizeInterfaceParameters();

        /* set up mappings between relation parameters and interface parameters */
        for (Iterator i = queriedInputParams.iterator(); i.hasNext();) {
            CRelationInputParameter relParam = (CRelationInputParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceInputParameter itfParam = impl.getNamingService().getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + paramNameInInterface);
            relParam.getMapping().setMappingScript(itfParam.getQualifiedName());
        }

        for (Iterator i = queriedOutputParams.iterator(); i.hasNext();) {
            CParameter relParam = (CParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceOutputParameter itfParam = impl.getNamingService().getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + paramNameInInterface);
            itfParam.getMapping().setMappingScript(relParam.getQualifiedName());
        }

        /* set up myInterface's dependency by summing up the dependency of each implementation */
        myInterface.setDependenciesFromImplementations();

        /* the code commented below has the same effect as the one-line code above */
//        for (Iterator i = impl.getInputParameterList().iterator(); i.hasNext(); ) {
//            CInterfaceInputParameter iParam = (CInterfaceInputParameter) i.next();
//            for (Iterator j = impl.getOutputParameterList().iterator(); j.hasNext(); ) {
//                CInterfaceOutputParameter oParam = (CInterfaceOutputParameter) j.next();
//                if (oParam.getDriversOf(true).contains(iParam.getQualifiedName())) {
//                    myInterface.setDependency(iParam.getName(), oParam.getName());
//                }
//            }
//        }

        return model;
    }

    public static CModel createModel_3() {
        /* create a model called 'My Demo Model,' having an interface called 'default interface' */
        CModel model = new CModel("My Demo Model");
        CInterface myInterface = model.addInterface("default interface");

        /* create an implementation called 'default implementation' for 'default interface' */
        CImplementation impl = myInterface.addImplementation("default implementation");

        /* following code shows how to add remote relation. */
        CRemoteRelation rel1 = impl.addRemoteRelation("Operation parameters of PV with MPP tracker Interface", "A");
        rel1.setServerPort("18.80.1.101:8080");
        rel1.setUser("guest");
        rel1.setPassword("");
        rel1.setSpace("Server");
        rel1.setInterfacePath("Public/photovoltaic/PV module operational characteristics/Operation parameters of PV with MPP tracker Interface");
        rel1.configureRemoteRelationUsingRuntimeInterfaceInformation();

        /* create default interface parameters */
        Set queriedInputParams = impl.getNamingService().queryInputParameters();
        for (Iterator i = queriedInputParams.iterator(); i.hasNext();) {
            CRelationInputParameter relParam = (CRelationInputParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceInputParameter itfParam = myInterface.addInputParameter(paramNameInInterface);
            itfParam.setDataType(relParam.getDataType());
            itfParam.setDefaultValue(relParam.getDefaultValue());
            itfParam.setUnit(relParam.getUnit());
        }

        Set queriedOutputParams = impl.getNamingService().queryOutputParameters();
        for (Iterator i = queriedOutputParams.iterator(); i.hasNext();) {
            CParameter relParam = (CParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceOutputParameter itfParam = myInterface.addOutputParameter(paramNameInInterface);
            itfParam.setDataType(relParam.getDataType());
            itfParam.setDefaultValue(relParam.getDefaultValue());
            itfParam.setUnit(relParam.getUnit());
        }

        impl.synchronizeInterfaceParameters();

        /* set up mappings between relation parameters and interface parameters */
        for (Iterator i = queriedInputParams.iterator(); i.hasNext();) {
            CRelationInputParameter relParam = (CRelationInputParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceInputParameter itfParam = impl.getNamingService().getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + paramNameInInterface);
            relParam.getMapping().setMappingScript(itfParam.getQualifiedName());
        }

        for (Iterator i = queriedOutputParams.iterator(); i.hasNext();) {
            CParameter relParam = (CParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceOutputParameter itfParam = impl.getNamingService().getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + paramNameInInterface);
            itfParam.getMapping().setMappingScript(relParam.getQualifiedName());
        }

        /* set up myInterface's dependency by summing up the dependency of each implementation */
        myInterface.setDependenciesFromImplementations();

        return model;
    }

    public static CModel createModel_4() {
        /* create a model called 'My Demo Model,' having an interface called 'default interface' */
        CModel model = new CModel("My Demo Model");
        CInterface myInterface = model.addInterface("default interface");

        /* create an implementation called 'default implementation' for 'default interface' */
        CImplementation impl = myInterface.addImplementation("default implementation");

        /* following code shows how to add remote relation. */
        CRemoteRelation rel1 = impl.addRemoteRelation("solar radiation on tilted surface", "A");
        rel1.setServerPort("18.80.1.101:8080");
        rel1.setUser("guest");
        rel1.setPassword("");
        rel1.setSpace("Server");
        rel1.setInterfacePath("Public/solar radiation/solar radiation on tilted surface/solar radiation on tilted surface - complete Interface");
        rel1.configureRemoteRelationUsingRuntimeInterfaceInformation();

        /* create default interface parameters */
        Set queriedInputParams = impl.getNamingService().queryInputParameters();
        for (Iterator i = queriedInputParams.iterator(); i.hasNext();) {
            CRelationInputParameter relParam = (CRelationInputParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceInputParameter itfParam = myInterface.addInputParameter(paramNameInInterface);
            itfParam.setDataType(relParam.getDataType());
            itfParam.setDefaultValue(relParam.getDefaultValue());
            itfParam.setUnit(relParam.getUnit());
        }

        Set queriedOutputParams = impl.getNamingService().queryOutputParameters();
        for (Iterator i = queriedOutputParams.iterator(); i.hasNext();) {
            CParameter relParam = (CParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceOutputParameter itfParam = myInterface.addOutputParameter(paramNameInInterface);
            itfParam.setDataType(relParam.getDataType());
            itfParam.setDefaultValue(relParam.getDefaultValue());
            itfParam.setUnit(relParam.getUnit());
        }

        impl.synchronizeInterfaceParameters();

        /* set up mappings between relation parameters and interface parameters */
        for (Iterator i = queriedInputParams.iterator(); i.hasNext();) {
            CRelationInputParameter relParam = (CRelationInputParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceInputParameter itfParam = impl.getNamingService().getInterfaceInputParameter(CConstant.ITF_ALIAS + "." + paramNameInInterface);
            relParam.getMapping().setMappingScript(itfParam.getQualifiedName());
        }

        for (Iterator i = queriedOutputParams.iterator(); i.hasNext();) {
            CParameter relParam = (CParameter) i.next();
            String paramNameInInterface = relParam.getQualifiedName().replace('.', '_');
            CInterfaceOutputParameter itfParam = impl.getNamingService().getInterfaceOutputParameter(CConstant.ITF_ALIAS + "." + paramNameInInterface);
            itfParam.getMapping().setMappingScript(relParam.getQualifiedName());
        }

        /* set up myInterface's dependency by summing up the dependency of each implementation */
        myInterface.setDependenciesFromImplementations();

        return model;
    }
}
