package mit.cadlab.dome3.search.datastructure.graph;

import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.search.graphmatching.GreedyBipartiteMatching;
import mit.cadlab.dome3.search.graphmatching.GMAlgorithm;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

/**
 * Definition:
 * A random mit.cadlab.dome3.search.datastructure.graph representation of pattern = a union attributed mit.cadlab.dome3.search.datastructure.graph (vertex/arc attributes assume a set of values) + associated probability distribution.
 */
public class FuzzyARG extends AttributedGraph implements Cloneable, XMLSupport {
    public static final String XML_TAG = "FuzzyARG";

    public FuzzyARG() {

    }

    public FuzzyARG(Element xmlElement) {
        XMLUtils.makeRootElement(xmlElement);
        loadXml(xmlElement);
    }

    public FuzzyARG(SimpleARG iAG) {
        HashMap temp = new HashMap();//key is orgNode, value is RandomNode

        DSet iAGnodes = (DSet) iAG.getNodes();
        //change all node into randomgrpah node
        Iterator iter = iAGnodes.iterator();
        while (iter.hasNext()) {
            SimpleAttributedNode n = (SimpleAttributedNode) iter.next();
            FuzzyAttributedNode rn = new FuzzyAttributedNode(n);
            addNode(rn);  //copy all nodes
            temp.put(n, rn);
        }

        //copy all arcs
        Map arcMap = iAG.getArcs();

        arcs = new HashMap();
        Iterator entries = arcMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();     //key-value pair
            Object orgN = entry.getKey();
            List orgN_links = (List) entry.getValue();
            for (Iterator it = orgN_links.iterator(); it.hasNext();) {
                Object orgN_to = it.next();
                if (temp.get(orgN) != null && temp.get(orgN_to) != null)
                    addArc(temp.get(orgN), temp.get(orgN_to));
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        FuzzyARG clonedGraph = new FuzzyARG();
        HashMap temp = new HashMap();
        Iterator iter = nodes.iterator();
        while (iter.hasNext()) {
            FuzzyAttributedNode rn = (FuzzyAttributedNode) iter.next();
            //clone one
            FuzzyAttributedNode newrn = (FuzzyAttributedNode) rn.clone();
            clonedGraph.addNode(newrn);  //copy all nodes
            temp.put(rn, newrn);
        }
        //clone arc should copy the weight too
        clonedGraph.arcs = new HashMap();
        Iterator entries = ArcWeights.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();     //key-value pair
            Object orgN = entry.getKey();
            List orgN_links = (List) entry.getValue();//arcs
            for (Iterator it = orgN_links.iterator(); it.hasNext();) {
                Arc a = (Arc) it.next();
                if (temp.get(orgN) != null && temp.get(a.getToN()) != null)
                    clonedGraph.addArc(temp.get(orgN), temp.get(a.getToN()), a.getWeight());
            }
        }

        return clonedGraph;
    }

    public AttributedNode addNode(String name, String datatype, String unit, Double dim, String input, int weight) {
        SimpleAttributedNode n = new SimpleAttributedNode(name, datatype, unit, input, dim);
        n.setWeight(weight);
        FuzzyAttributedNode rn = new FuzzyAttributedNode(n);
        if (super.addNode(rn)) return rn;
        return null;
    }

    public AttributedNode addNode(SimpleAttributedNode n, int weight) {
        n.setWeight(weight);
        FuzzyAttributedNode rn = new FuzzyAttributedNode(n);
        if (super.addNode(rn)) return rn;
        return null;
    }

    public String getXmlTag() {
        return XML_TAG;
    }

    public Element toXmlElement() {
        Element xml = DocumentHelper.createElement(XML_TAG);

        Element nodeXml = xml.addElement("nodes");
        Element arcsXml = xml.addElement("arcs");
        List nodeList = new ArrayList(nodes);
        for (Iterator iterator = nodeList.iterator(); iterator.hasNext();) {
            nodeXml.add(toXmlRef("node", iterator.next()));
        }

        List arcKeys = new ArrayList(ArcWeights.keySet());

        for (Iterator iterator = arcKeys.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            List arcs = (ArrayList) ArcWeights.get(o);
            for (Iterator iterator2 = arcs.iterator(); iterator2.hasNext();) {
                Arc a = (Arc) iterator2.next();
                Element arcXml = DocumentHelper.createElement("arc");
                arcsXml.add(arcXml);
                arcXml.addAttribute("from", new Integer(nodes.indexOf(o)).toString());
                arcXml.addAttribute("to", new Integer(nodes.indexOf(a.getToN())).toString());
                arcXml.addAttribute("weight", new Integer(a.getWeight()).toString());
            }

        }
        return xml;
    }

    private Element toXmlRef(String tag, Object obj) {
        Element xmlElement = DocumentHelper.createElement(tag);
        if (obj instanceof FuzzyAttributedNode) {
            xmlElement.add(((FuzzyAttributedNode) obj).toXmlElement());
        }
        return xmlElement;
    }

    protected void loadXml(Element xmlElement) {
        List nodeXml = xmlElement.selectNodes("/" + XML_TAG + "/nodes/node");

        for (Iterator iterator = nodeXml.iterator(); iterator.hasNext();) {
            Element n = (Element) iterator.next();
            addNode(parseXmlRef(n));
        }

        List arcXml = xmlElement.selectNodes("/" + XML_TAG + "/arcs/arc");

        for (int i = 0; i < arcXml.size(); i++) {
            Element arc = (Element) arcXml.get(i);
            int index = new Integer(arc.attributeValue("from")).intValue();
            Object fromNode = getNode(index);
            int toindex = new Integer(arc.attributeValue("to")).intValue();
            Object toNode = getNode(toindex);
            int weight = new Integer(arc.attributeValue("weight")).intValue();
            if (fromNode != null && toNode != null)
                addArc(fromNode, toNode, weight);
        }
    }


    public Object parseXmlRef(Element xmlElement) {
        return new FuzzyAttributedNode(xmlElement);
    }

    /**
     * to merge an interface mit.cadlab.dome3.search.datastructure.graph in
     * 1. union
     * 2. merge aligned node
     * 3. merge aligned arc
     * 4. update weight
     *
     */
    public void mergeGraph(SimpleARG interfacegraph, HashMap revertnodeMapping) {
        if (interfacegraph.getNodes() == null) {
            System.out.println("Error in merging graphs---- invalid interface mit.cadlab.dome3.search.datastructure.graph");
            return;
        }

        DSet ig_nodes = interfacegraph.getNodes();
        HashMap ig_arcs = interfacegraph.getArcs();

        if (revertnodeMapping == null || revertnodeMapping.size() == 0) //no merging, simple union
        {
            //add all new interface nodes
            HashMap map = new HashMap();//keep a map for original node and the converted randomgraph node
            for (Iterator i = ig_nodes.iterator(); i.hasNext();) {
                SimpleAttributedNode node = (SimpleAttributedNode) i.next();
                map.put(node, addNode(node, 1));  //converted into a randomgraphnode
            }
            //add  all new interface arcs

            for (Iterator i = ig_arcs.keySet().iterator(); i.hasNext();) {
                SimpleAttributedNode fromNode = (SimpleAttributedNode) i.next();
                List toNodes = (DSet) ig_arcs.get(fromNode);
                for (Iterator it = toNodes.iterator(); it.hasNext();) {
                    SimpleAttributedNode toNode = (SimpleAttributedNode) it.next();
                    addArc(map.get(fromNode), map.get(toNode));
                }

            }

        } else {
            // merge node pair, merge arc, increase weight of merged nodes/arcs
            //merge node pair: key: inode, value tnode
            //add all nodes first
            //add all new interface nodes
            HashMap map = new HashMap();//keep a map for original node and the converted randomgraph node
            for (Iterator i = ig_nodes.iterator(); i.hasNext();) {
                SimpleAttributedNode node = (SimpleAttributedNode) i.next();
                if (revertnodeMapping.get(node) != null) //has aligned template node
                {
                    FuzzyAttributedNode rnode = (FuzzyAttributedNode) revertnodeMapping.get(node);
                    //merge node
                    rnode.mergeNode(node);
                    //update weight
                    rnode.setWeight(rnode.weight + 1);
                    map.put(node, revertnodeMapping.get(node));
                } else
                    map.put(node, addNode(node, 1));  //converted into a randomgraphnode
            }

            for (Iterator i = ig_arcs.keySet().iterator(); i.hasNext();) {
                SimpleAttributedNode fromNode = (SimpleAttributedNode) i.next();
                List toNodes = (DSet) ig_arcs.get(fromNode);
                for (Iterator it = toNodes.iterator(); it.hasNext();) {
                    SimpleAttributedNode toNode = (SimpleAttributedNode) it.next();
                    addArc(map.get(fromNode), map.get(toNode));
                }

            }
        }
    }

    public void mergeGraph(SimpleARG ifacegraph) {
        GMAlgorithm algorithm = new GMAlgorithm(this, ifacegraph);
        algorithm.matching();
        HashMap nodemapping = algorithm.getNodewise_alignment();//key: template node, value: iface node
        if (nodemapping == null || nodemapping.size() == 0)
            System.out.println("Warning: Graph Merging: no alignment has been found, might be a bad decision to merge! ");
        mergeGraph(ifacegraph, revert(nodemapping));
    }

    private HashMap revert(HashMap m) {
        HashMap result = new HashMap(m.size());
        for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            result.put(entry.getValue(), entry.getKey());
        }
        return result;
    }
}
