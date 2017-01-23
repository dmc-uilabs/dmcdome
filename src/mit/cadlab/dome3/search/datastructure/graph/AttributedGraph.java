package mit.cadlab.dome3.search.datastructure.graph;


import mit.cadlab.dome3.util.DSet;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

import java.util.*;

/**
 * AttributedGraph is a attributed, weighted, directed mit.cadlab.dome3.search.datastructure.graph
 *
 **/

public abstract class AttributedGraph extends Digraph {

    protected HashMap ArcWeights;
    public String name;
    public String id;   //Added by Ligon for automated mapping

    public boolean addNode(Object node) {
        if (!(node instanceof AttributedNode)) {
            new Exception("AttributedGraph error -- invalid Node: " + node);
            return false;
        }
        return super.addNode(node);
    }

    public abstract AttributedNode addNode(String name, String datatype, String unit, Double dim, String inputoutput, int weight);

    public boolean addNodes() {
        //not supported at this time
        new Exception("AttributedGraph error -- addNodes() not supported yet ");
        return false;
    }

    public boolean addArcs() {
        //not supported at this time
        new Exception("AttributedGraph error -- addArcs() not supported yet");
        return false;
    }

    public boolean addArc(Object fromNode, Object toNode, int weight) {
        if (!(fromNode instanceof AttributedNode) || !(toNode instanceof AttributedNode))
            return false;
        boolean issuccessful = super.addArc(fromNode, toNode);
        if (!issuccessful) return issuccessful;
        Arc a = new Arc((AttributedNode) fromNode, (AttributedNode) toNode, weight);
        if (ArcWeights == null) ArcWeights = new HashMap();
        if (ArcWeights.get(fromNode) == null) {
            List arcs = new ArrayList();
            ArcWeights.put(fromNode, arcs);
        }
        ((ArrayList) ArcWeights.get(fromNode)).add(a);
        return true;
    }

    public boolean addArc(Object fromNode, Object toNode) {
         if (!(fromNode instanceof AttributedNode) || !(toNode instanceof AttributedNode))
            return false;
        Arc a = getArc((AttributedNode)fromNode, (AttributedNode)toNode);
        if (a != null) {// this arc already exist
            a.setWeight(a.getWeight() + 1);
	        return true;
        } else
        {
            return addArc(fromNode, toNode, 1);
        }
    }

    public boolean updateNodeWeight(int NodeIndex, int weight) {
        if (NodeIndex < 0 || NodeIndex > nodes.size()) return false;
        ((AttributedNode) nodes.get(NodeIndex)).setWeight(weight);
        return true;
    }

    public AttributedNode getNode(int NodeIndex) {
        return ((AttributedNode) nodes.get(NodeIndex));
    }

    public boolean updateArcWeight(Object fromNode, Object toNode, int weight) {
        if (!containsNode(fromNode) || !containsNode(toNode)) return false;
        if (ArcWeights.get(fromNode) == null) return false;
        ArrayList arcs = (ArrayList) ArcWeights.get(fromNode);
        for (int i = 0; i < arcs.size(); i++) {
            Arc a = (Arc) arcs.get(i);
            if (a.getToN().equals(toNode)) {
                a.setWeight(weight);
                return true;
            }
        }
        return false;
    }

    public boolean updateArcWeight(int fromNodeIndex, int toNodeIndex, int weight) {
        if (fromNodeIndex < 0 || toNodeIndex < 0 || toNodeIndex > nodes.size() || fromNodeIndex > nodes.size()) return false;
        if (ArcWeights.get(nodes.get(fromNodeIndex)) == null) return false;
        ArrayList arcs = (ArrayList) ArcWeights.get(nodes.get(fromNodeIndex));
        for (int i = 0; i < arcs.size(); i++) {
            Arc a = (Arc) arcs.get(i);
            if (a.getToN().equals(nodes.get(toNodeIndex))) {
                a.setWeight(weight);
                return true;
            }
        }
        return false;
    }

    /**
     * this function will get a DSet of dummy arcs, for all those indirect (reacheable but not adjcent) nodes
     * @param nodes
     * @return
     */
    public DSet getIndirectArcs(DSet nodes) {
        if (nodes == null || nodes.size() <= 1) return null;
        DSet result = new DSet();
        for (int i=0;i <  nodes.size(); i++) {
           AttributedNode fromnode = (AttributedNode) nodes.get(i);
            for(int j=0;j<nodes.size();j++){
              AttributedNode tonode = (AttributedNode) nodes.get(j);
              if(areReachableNodes(fromnode,tonode)&&!areAdjacentNodes(fromnode,tonode))
              {
                  Arc a=new Arc(fromnode,tonode);
                   result.add(a);
              }
            }
        }
        if (result.size() != 0) return result;
        return null;

    }
    public DSet getArcs(DSet nodes) {
        if (nodes == null || nodes.size() <= 1) return null;
        DSet result = new DSet();
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            AttributedNode node = (AttributedNode) it.next();
            //get adjecent nodes
            if (ArcWeights.get(node) != null) {
                ArrayList arcs = (ArrayList) ArcWeights.get(node);
                for (int i = 0; i < arcs.size(); i++) {
                    Arc a = (Arc) arcs.get(i);
                    if (nodes.contains(a.getToN())) {
                        result.add(a);
                    }
                }
            }
        }
        if (result.size() != 0) return result;
        return null;
    }

    public Arc getArc(AttributedNode fromNode, AttributedNode toNode) {
        if (!containsNode(fromNode) || !containsNode(toNode)) return null;
        if (!areAdjacentNodes(fromNode, toNode)) return null;
        ArrayList arcs = (ArrayList) ArcWeights.get(fromNode);
        for (int i = 0; i < arcs.size(); i++) {
            Arc a = (Arc) arcs.get(i);
            if (a.getToN().equals(toNode)) {
                return a;
            }
        }
        return null;
    }

    public String toString() {
        String ret = "\n"+"GRAPH:";
        String ret2="";
        String nodearcStr="";
        if (nodes != null) {
            ret2 = ret2 + nodes.size() + " nodes ";
            nodearcStr=nodearcStr + "\n Nodes:";
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                AttributedNode n = (AttributedNode) i.next();
                nodearcStr = nodearcStr + "\n\t" + n.toString();
            }
        }
        if (ArcWeights != null) {
            nodearcStr = nodearcStr + "\n Arcs:";
            int arcCounts = 0;
            for (Iterator entries = ArcWeights.entrySet().iterator(); entries.hasNext();) {
                Map.Entry entry = (Map.Entry) entries.next();
                List arcs = (List) entry.getValue();
                for (Iterator it = arcs.iterator(); it.hasNext();) {
                    Arc a = (Arc) it.next();
                    nodearcStr = nodearcStr + "\n\t" + a.toString();
                    arcCounts++;
                }
            }
            ret2 = ret2 + arcCounts + " arcs";
        }
        return ret + ret2+nodearcStr+"\n}";
    }

    public int getArcWeight(AttributedNode fromNode, AttributedNode toNode) {
        if (!containsNode(fromNode) || !containsNode(toNode)) return -1;
        if (ArcWeights.get(fromNode) == null) return -1;
        ArrayList arcs = (ArrayList) ArcWeights.get(fromNode);
        for (int i = 0; i < arcs.size(); i++) {
            Arc a = (Arc) arcs.get(i);
            if (a.getToN().equals(toNode)) {
                return a.getWeight();
            }
        }
        return -1;
    }

    public int getNodeIndex(AttributedNode node) {
        if (!containsNode(node)) return -1;
        return (nodes.indexOf(node));
    }

    //Added by Ligon
    public Parameter getCorrespondingParameter(AttributedNode node){
        if(nodes.contains(node)){
            Object entry = parameters.get(getNodeIndex(node));
            if(entry instanceof Parameter)
                return (Parameter)entry;
        }
        return null;
    }

    public class Arc {
        protected AttributedNode fromN,toN;
        protected int weight;

        public Arc(AttributedNode fromN, AttributedNode toN) {
            this.fromN = fromN;
            this.toN = toN;
            this.weight = 1;
        }

        public Arc(AttributedNode fromN, AttributedNode toN, int weight) {
            this.fromN = fromN;
            this.toN = toN;
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public AttributedNode getFromN() {
            return fromN;
        }

        public AttributedNode getToN() {
            return toN;
        }

        public String toString() {
            return fromN + "--" + weight + "--" + toN;
        }
    }
}

