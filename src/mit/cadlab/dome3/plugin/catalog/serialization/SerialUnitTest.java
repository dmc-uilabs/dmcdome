package mit.cadlab.dome3.plugin.catalog.serialization;

import mit.cadlab.dome3.plugin.catalog.core.CNamingService;

/**
 * User: Sangmok Han
 * Date: 2006. 3. 5.
 */
public class SerialUnitTest {
    public static void main(String[] args) throws Exception {
        DomeSerialization.CModelAndIDContainer modelAndIDContainer = DomeSerialization.load("c:/test_model/RefModel-CATALOG.dml");
        System.out.println(modelAndIDContainer.model);
        System.out.println(modelAndIDContainer.idContainer);
        CNamingService namingService = modelAndIDContainer.model.getInterface("itfA").getImplementation("impl 1").getNamingService();
        System.out.println(namingService.getMap());

        modelAndIDContainer.idContainer = new IDContainer();
        DomeSerialization.save(modelAndIDContainer.model, modelAndIDContainer.idContainer, "c:/test_model/test-CATALOG.dml");
        DomeSerialization.CModelAndIDContainer modelAndIDContainer2 = DomeSerialization.load("c:/test_model/test-CATALOG.dml");
        System.out.println(modelAndIDContainer2.model);
        System.out.println(modelAndIDContainer2.idContainer);

        System.out.println("equal1:" + modelAndIDContainer2.model.equals(modelAndIDContainer.model));
        System.out.println("equal2:" + modelAndIDContainer2.idContainer.equals(modelAndIDContainer.idContainer));

        modelAndIDContainer2.idContainer.assignRandomIDToAllElements(modelAndIDContainer2.model);
        DomeSerialization.save(modelAndIDContainer2.model, modelAndIDContainer2.idContainer, "c:/test_model/savedas-CATALOG.dml");
        DomeSerialization.CModelAndIDContainer modelAndIDContainer3 = DomeSerialization.load("c:/test_model/savedas-CATALOG.dml");
        System.out.println(modelAndIDContainer3.model);
        System.out.println(modelAndIDContainer3.idContainer);

        System.out.println("equal3:" + modelAndIDContainer3.model.equals(modelAndIDContainer2.model));
        System.out.println("equal4:" + modelAndIDContainer3.idContainer.equals(modelAndIDContainer2.idContainer));
    }
}
