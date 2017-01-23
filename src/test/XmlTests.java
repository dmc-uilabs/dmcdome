// XmlTests.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package test;

import mit.cadlab.dome3.DomeInit;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.config.Registry;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.Documentation;
import mit.cadlab.dome3.objectmodel.dataobject.DocumentationData;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.dataobject.BooleanData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText;
import mit.cadlab.dome3.objectmodel.dataobject.FileData;
import mit.cadlab.dome3.objectmodel.dataobject.IntegerData;
import mit.cadlab.dome3.objectmodel.dataobject.RealData;
import mit.cadlab.dome3.objectmodel.dataobject.StringData;
import mit.cadlab.dome3.objectmodel.dataobject.TextData;
import mit.cadlab.dome3.objectmodel.dataobject.DomeListData;
import mit.cadlab.dome3.util.units.Quantity;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.Element;

public class XmlTests {

    public static void testQuantity() {
        Quantity q = new Quantity("2.5 in");
        debug(q);
        Element e = q.toXmlElement();
        XMLUtils.print(e);
        Quantity q2 = new Quantity(e);
        debug(q2);
    }

    public static void testReal() {
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal r1 = new RealData(2.5);
        r1.setUnit("in");
        debug(r1);
        Element e = r1.toXmlElement();
        XMLUtils.print(e);
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeReal r2 = new RealData(e);
        debug(r2);
    }

    public static void testInteger() {
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger i1 = new IntegerData(2);
        i1.setUnit("ft");
        debug(i1);
        Element e = i1.toXmlElement();
        XMLUtils.print(e);
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger i2 = new IntegerData(e);
        debug(i2);
    }

    public static void testBoolean() {
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean b1 = new BooleanData(true);
        debug(b1);
        Element e = b1.toXmlElement();
        XMLUtils.print(e);
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean b2 = new BooleanData(e);
        debug(b2);
    }

    public static void testString() {
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString s1 = new StringData("the quick brown fox jumped over the lazy dog");
        debug(s1);
        Element e = s1.toXmlElement();
        XMLUtils.print(e);
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeString s2 = new StringData(e);
        debug(s2);
    }

    public static void testText() {
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText t1 = new TextData("\tone day I'll see the moon\n<bark>");
        debug(t1);
        Element e = t1.toXmlElement();
        XMLUtils.print(e);
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeText t2 = new TextData(e);
        debug(t2);
    }

    public static void testFile() {
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile f1 = new FileData("/usr/local/files/somefile.txt");
        debug(f1);
        Element e = f1.toXmlElement();
        XMLUtils.print(e);
        mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile f2 = new FileData(e);
        debug(f2);
    }

	public static void testFileParameter(){
	   ConcreteParameter  p=new	 ConcreteParameter(null,new Id("000"),"File");
	   debug(p);

	   mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile f=(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile)p.getCurrentDataObject();
	   f.setFilePath("c:/");

		Element e = p.toXmlElement();
        XMLUtils.print(e);


	  ConcreteParameter  p2=new	 ConcreteParameter(null,e);
		debug(p2);

	}

    public static void testDocumentation() {
        Documentation d1 = new DocumentationData("this is \"real\" complicated\nmulti-line text!\n\tsome things > others",
                                                  "www.doc.com/somedoc.html");
        debug(d1);
        Element e = d1.toXmlElement();
        XMLUtils.print(e);
        Documentation d2 = new DocumentationData(e);
        debug(d2);
    }

	public static void testNewDataType() {
		DomeInit.initializeDOME();
		mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger i1 = new IntegerData(5,"in");
		System.out.println(i1);
		try {
			mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger i2 = (mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeInteger) Registry.getConstructor(i1, Registry.BASE_CLS).newInstance(new Object[]{i1});
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}

	public static void testData(){

		DomeModel myModel = new DomeModelBuilder(new Id("one"));
		DomeListData d1 = new DomeListData();
		d1.setScope(myModel);
		DomeListData d2 = new DomeListData();
		d2.setScope(myModel);
		d1.addPropertyChangeListener(d2.getValueShadowListener());
		d1.addItem("Real");
		XMLUtils.print(d1.toXmlElement());
		XMLUtils.print(d2.toXmlElement());

	}

    protected static void debug(String prefix, Object obj) {
        System.out.println("\n"+prefix+": "+obj);
    }

    protected static void debug(Object obj) {
        System.out.println("\n"+obj);
    }

    public static void main(String[] args) {
	    DomeInit.initializeDOME();
	    //testNewDataType();
        /*DummyUnits.registerUnits(); // initialize dummy units database
        testQuantity();
        testReal();
        testInteger();
        testBoolean();
        testString();
        testText();
        testFile();
        testDocumentation();*/
	    testData();
	    //testFileParameter();
    }

}
