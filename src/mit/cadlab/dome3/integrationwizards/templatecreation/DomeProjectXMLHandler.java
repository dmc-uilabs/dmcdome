package mit.cadlab.dome3.integrationwizards.templatecreation;

import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Ligon
 * Date: Mar 2, 2007
 * Time: 12:24:00 PM
 * To change this template use Options | File Templates.
 */
public class DomeProjectXMLHandler {

    public static final String projectModel = "project";

    public static HashMap loadProjectModel(Element projectXML)
        {
            // parse XML for information
            HashMap resourceIds = new HashMap();
            String projectId = ""; //build id, to be used when locating the project
            String name;
            try  {name = projectXML.getQName().getName();}
            catch (Exception e)
            {return null;}

            if (name.equals(projectModel)) {
                projectId = projectXML.attributeValue("id");
                if (projectId == null)
                    throw new IllegalArgumentException(" - no xml id");


                //Record the basic data for each subscribed model
                List subXML = projectXML.selectNodes("/project/resources/resource");
                String modelID;
                for (Iterator iterator=subXML.iterator();iterator.hasNext();){
                    Element subscriptionElement = (Element) iterator.next();
                    XMLUtils.makeRootElement(subscriptionElement);
                    modelID = subscriptionElement.attributeValue("id");
                    if (projectId == null)
                        throw new IllegalArgumentException(" - no xml id");
                    List idXML = subscriptionElement.selectNodes("/resource/subscribedInterfaceIds");
                    String interfaceID;
                    for (Iterator idIterator = idXML.iterator();idIterator.hasNext();){
                        Element idElement = (Element) idIterator.next();
                        interfaceID = idElement.elementText("ifaceId");
                        if(interfaceID!=null)
                            resourceIds.put(modelID,interfaceID);
                    }
                }

                return resourceIds;
            }
            return null;
        }

}
