package mit.cadlab.dome3.search.framework.utils.processing;

import mit.cadlab.dome3.search.datastructure.graph.SimpleAttributedNode;
import mit.cadlab.dome3.search.datastructure.graph.SimpleARG;
import mit.cadlab.dome3.search.datastructure.graph.NumberArrayList;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Nov 28, 2005
 * Time: 11:26:00 PM
 * To change this template use Options | File TemplateRegistry.
 */
public class InterfaceData {
    protected SimpleARG graph;
    protected String modelname;    //modelName
    protected String ifacename;
    protected String description;
    protected String location;
    protected NumberArrayList dimensionList;
    protected String id;

    public InterfaceData(String _id){
        this.id=_id;
    }

    public String getId() {
        return id;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SimpleARG getGraph() {
        return graph;
    }

    public String getModelname() {
        return modelname;
    }

    public String getIfacename() {
        return ifacename;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public NumberArrayList getDimensionList() {
        return dimensionList;
    }

    public void setGraph(SimpleARG graph) {
        this.graph = graph;
    }

    public void setIfacename(String ifacename) {
        this.ifacename = ifacename;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDimensionList(NumberArrayList dimensionList) {
        this.dimensionList = dimensionList;
    }

    public String printDetail(){
        String ret = "Interface:"+ifacename;
        if(modelname!=null)
           ret=ret+modelname;
        if(description!=null)
           ret=ret+description.toString();
        return ret + graph.toString();
    }

     protected String getDimensionListString() {
        String dl = "[";
        for (int i = 0; i < dimensionList.size(); i++) {
            dl = dl + " " + dimensionList.get(i) + " ";
        }
        dl = dl + "]";
        return dl;
    }


}
