package mit.cadlab.dome3.search.framework.templatemanagement;

import mit.cadlab.dome3.search.datastructure.graph.FuzzyARG;
import mit.cadlab.dome3.search.datastructure.graph.NumberArrayList;
import mit.cadlab.dome3.search.framework.utils.processing.InterfaceData;
import mit.cadlab.dome3.objectmodel.util.id.IdGenerator;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 *
 */
public class  Template implements XMLSupport {
    public static final String XML_TAG = "Template";

    protected FuzzyARG graph;
    private String description;
    private String name;
    private NumberArrayList dimList;
    private String id;

    public Template(FuzzyARG graph, String name,NumberArrayList dimList) {
        //this.id = IdGenerator.create();
        this.graph = graph;
        this.name = name;
        this.dimList=dimList;
    }

    //Added by Ligon
    public Template(FuzzyARG graph, String name, String id)
    {
        this.id = id;
        this.graph = graph;
        this.name = name;
    }

    public Template(String id,FuzzyARG graph, String name,NumberArrayList dimList) {
        this.id = id;
        this.graph = graph;
        this.graph.id = id;
        this.graph.name = name;
        this.name = name;
        this.dimList=dimList;
    }

    public Template(String name,InterfaceData data){
        this.id = data.getId();
        this.name=name;
        this.graph=new FuzzyARG(data.getGraph());
        this.graph.name = name;
        this.description="this template is created initially from "+data.getIfacename();
        if(data.getDescription()!=null)  this.description+=":"+data.getDescription();
        this.dimList=data.getDimensionList();
     }

    public Template(Element xmlElement) {
        XMLUtils.makeRootElement(xmlElement);
        id = xmlElement.elementText("id");
        name = xmlElement.elementText("name");
        description = xmlElement.elementText("description");
        String dimListString= xmlElement.elementText("dimensionlist");
        if (dimListString != null && dimListString.length() > 0) {
				String[] values = dimListString.split(" ");
                dimList=new NumberArrayList();
				try {
					for (int i = 0; i < values.length; i++) {
						dimList.insert(new Double((values[i])));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
        //add mit.cadlab.dome3.search.datastructure.graph
        Element graphXml=(Element)xmlElement.selectSingleNode(FuzzyARG.XML_TAG);
        graph=new FuzzyARG(graphXml);
        this.graph.id = id;             //added by Ligon for automated mapping
        this.graph.name = name;
    }

    public FuzzyARG getGraph() {
        return graph;
    }

    public void setGraph(FuzzyARG graph) {
        this.graph = graph;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NumberArrayList getDimList() {
        return dimList;
    }

    public void setDimList(NumberArrayList dimList) {
        this.dimList = dimList;
    }

    public String getId() {
        return id;
    }

    public Element toXmlElement() {
        Element xml = DocumentHelper.createElement(XML_TAG);
        xml.addElement("id").addText(id);
        xml.addElement("name").addText(name);
        if(description!=null)
           xml.addElement("description").addText(description);
        if(dimList!=null)
           xml.addElement("dimensionlist").addText(dimList.toString());
        xml.add(graph.toXmlElement());
        return xml;
    }

    public String getXmlTag() {
        return XML_TAG;
    }

    public void save(){
        TemplateRegistry.addTemplate(this);
     }

    public void merge(InterfaceData newdata) throws Exception{
        FuzzyARG copy=(FuzzyARG)graph.clone();
        copy.mergeGraph(newdata.getGraph());
        //mit.cadlab.dome3.search.datastructure.graph.mergeGraph(newdata.getGraph());
        graph=copy;
        this.description+="\nand merged with "+newdata.getIfacename();
        if(newdata.getDescription()!=null)  this.description+=":"+newdata.getDescription();
        for(int i=0;i<newdata.getDimensionList().size();i++){
             dimList.insert((Number)newdata.getDimensionList().get(i));
         }

    }

     public String toString() {
        return "Template: "+name;
    }


    public String printDetail() {
        String ret = "Template:"+name;
        if(description!=null)
           ret=ret+description.toString();
        return ret + graph.toString();
    }
}
