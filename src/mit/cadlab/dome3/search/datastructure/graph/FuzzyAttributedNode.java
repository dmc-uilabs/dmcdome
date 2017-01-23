package mit.cadlab.dome3.search.datastructure.graph;

import mit.cadlab.dome3.search.datastructure.FuzzySet;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Caoq
 * Date: Nov 28, 2005
 * Time: 6:03:00 PM
 * To change this template use Options | File TemplateRegistry.
 */
public class FuzzyAttributedNode extends AttributedNode implements Cloneable, XMLSupport {
    public static final String XML_TAG = "FuzzyAttributedNode";

    protected FuzzySet name;
    protected FuzzySet datatype;
    protected FuzzySet unit;
    protected FuzzySet dim;
    protected FuzzySet inputoutput;

    public FuzzyAttributedNode(FuzzySet name, FuzzySet datatype, FuzzySet unit, FuzzySet inout, FuzzySet dim) {
        this.name = name;
        this.datatype = datatype;
        this.unit = unit;
        this.dim = dim;
        this.inputoutput = inout;
    }

    public FuzzyAttributedNode(SimpleAttributedNode n) {
        name = new FuzzySet();
        name.add(n.getName());
        datatype = new FuzzySet();
        datatype.add(n.getDatatype());
        unit = new FuzzySet();
        unit.add(n.getUnit());
        dim = new FuzzySet();
        dim.add(n.getDim());
        inputoutput = new FuzzySet();
        inputoutput.add(n.getInput_output());
        weight = n.weight;
    }

    public FuzzyAttributedNode(Element xml) {
        //Element xmlElement=(Element)xml.selectSingleNode("/node/"+XML_TAG);
        Element xmlElement = xml.element(XML_TAG);
        XMLUtils.makeRootElement(xmlElement);
        name = new FuzzySet(xmlElement.element(AttributedNode.NAME).element(FuzzySet.XML_TAG));
        dim = new FuzzySet(xmlElement.element(AttributedNode.DIM).element(FuzzySet.XML_TAG));
        unit = new FuzzySet(xmlElement.element(AttributedNode.UNIT).element(FuzzySet.XML_TAG));
        datatype = new FuzzySet(xmlElement.element(AttributedNode.DATATYPE).element(FuzzySet.XML_TAG));
        inputoutput = new FuzzySet(xmlElement.element(AttributedNode.INOUT).element(FuzzySet.XML_TAG));
        weight = new Double(xmlElement.attributeValue("weight")).intValue();
    }

    public Object clone() throws CloneNotSupportedException {
        FuzzyAttributedNode cloned = new FuzzyAttributedNode((FuzzySet) name.clone(), (FuzzySet) datatype.clone(), (FuzzySet) unit.clone(), (FuzzySet) inputoutput.clone(), (FuzzySet) dim.clone());
        cloned.setWeight(weight);
        return cloned;

    }

    public Element toXmlElement() {
        Element xml = DocumentHelper.createElement(XML_TAG);
        if (name != null)
            xml.addElement(AttributedNode.NAME).add(name.toXmlElement());
        if (dim != null)
            xml.addElement(AttributedNode.DIM).add(dim.toXmlElement());
        if (unit != null)
            xml.addElement(AttributedNode.UNIT).add(unit.toXmlElement());
        if (datatype != null)
            xml.addElement(AttributedNode.DATATYPE).add(datatype.toXmlElement());
        if (inputoutput != null)
            xml.addElement(AttributedNode.INOUT).add(inputoutput.toXmlElement());
        xml.addAttribute("weight", new Double(weight).toString());
        return xml;
    }

    public void merge(SimpleAttributedNode n) {
        name.add(n.getName());
        datatype.add(n.getDatatype());
        unit.add(n.getUnit());
        dim.add(n.getDim());
        weight += n.weight;
    }

    public void mergeNode(SimpleAttributedNode n) {
        name.add(n.getName());
        datatype.add(n.getDatatype());
        unit.add(n.getUnit());
        dim.add(n.getDim());
        inputoutput.add(n.getInput_output());
    }

    public FuzzySet getName() {
        return name;
    }

    public void setName(FuzzySet name) {
        this.name = name;
    }

    public FuzzySet getDatatype() {
        return datatype;
    }

    public void setDatatype(FuzzySet datatype) {
        this.datatype = datatype;
    }

    public FuzzySet getUnit() {
        return unit;
    }

    public void setUnit(FuzzySet unit) {
        this.unit = unit;
    }

    public FuzzySet getDim() {
        return dim;
    }

    public void setDim(FuzzySet dim) {
        this.dim = dim;
    }

    public String toString() {
        return name.contentToString();
    }

    public String printDetail() {
        return "Node " + name.contentToString() + "(" + dim.contentToString() + "," + unit.contentToString() + "," + datatype.contentToString() + "," + inputoutput.contentToString() + ")" + ",weight= " + weight;
    }

    public String getXmlTag() {
        return XML_TAG;
    }

    public FuzzySet getInputoutput() {
        return inputoutput;
    }
}
