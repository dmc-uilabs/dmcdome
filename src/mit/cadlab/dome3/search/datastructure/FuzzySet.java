package mit.cadlab.dome3.search.datastructure;

import mit.cadlab.dome3.util.xml.XMLSupport;
import mit.cadlab.dome3.util.xml.XMLUtils;
import mit.cadlab.dome3.search.datastructure.graph.AttributedNode;

import java.util.*;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 * A (discrete) Random variable is a function from a finite or
 * countable infinite sample space S to the real numbers.
 * It associates a real number with each possible outcome of an experiment (probability distribution) on
 * the result set of numbers.
 */
public class FuzzySet implements Cloneable, XMLSupport {
    public static final String XML_TAG = "FuzzySet";

    protected HashMap Probability;    //key:sample; value: frequency
    private int Denominator;

    /**
     * constructor
     */
    public FuzzySet() {
        Probability = new HashMap();
        Denominator = 0;
    }

    public FuzzySet(Element xmlElement) {

        Denominator = new Double(xmlElement.attributeValue("Denominator")).intValue();
        List membersXml = xmlElement.selectNodes("members/member");
        Probability = new HashMap();
        for (Iterator iter = membersXml.iterator(); iter.hasNext();) {
            Element memberXml = (Element) iter.next();
            String value = memberXml.attributeValue("value");
            if(value.equals("___NULL____"))  value=null;
            String frequency = memberXml.attributeValue("frequency");
            Probability.put(value, frequency);
        }
    }

    public void add(Object o) {
        if (Probability.keySet().contains(o)) {
            int freq = ((Integer) Probability.get(o)).intValue();
            Probability.put(o, new Integer(freq + 1)); //increase freqency by one
        } else {
            Probability.put(o, new Integer(1));
        }
        Denominator += 1;
    }

    public Object clone() throws CloneNotSupportedException {
        FuzzySet cloned = new FuzzySet();

        cloned.Probability = new HashMap(Probability);
        cloned.Denominator = Denominator;
        return cloned;
    }

    public double getProbabilty(Object o) {
        if (!Probability.keySet().contains(o)) {
            return 0.0;
        }
        if (Probability.get(o) instanceof Integer) {
            int freq = ((Integer) (Probability.get(o))).intValue();
            return (double) freq / Denominator;
        }
        else if (Probability.get(o) instanceof String) {
            int freq = new Integer(((String) (Probability.get(o)))).intValue();
            return (double) freq / Denominator;
        }
        return 0.0;
    }

    public String toString() {
        String s = "[";
        for (Iterator i = Probability.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            s += "(" + entry.getKey() + "," + entry.getValue() + ")";
        }
        s += "]";
        return s;
    }

    public String contentToString() {
        String s = "[";
        for (Iterator i = Probability.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            s += entry.getKey() + ",";
        }
        s += "]";
        return s;
    }

    public Element toXmlElement() {
        Element xml = DocumentHelper.createElement(XML_TAG);
        //add hashmap
        Element membersXml = xml.addElement("members");
        for (Iterator i = Probability.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Element xmlElement = DocumentHelper.createElement("member");
            if(entry.getKey()==null)
                xmlElement.addAttribute("value", "___NULL____");
            else
                xmlElement.addAttribute("value", entry.getKey().toString());
            xmlElement.addAttribute("frequency", entry.getValue().toString());
            membersXml.add(xmlElement);
        }
        //add overall denominator
        xml.addAttribute("Denominator", new Double(Denominator).toString());
        return xml;
    }

    public int getSampleSize() {
        return Probability.size();
    }

    public Set getSampleSet() {
        return Probability.keySet();
    }

    public String getXmlTag() {
        return XML_TAG;
    }
}
