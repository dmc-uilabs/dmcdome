// JythonTests.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package test.jython;

import org.python.util.PythonInterpreter;

import java.util.Properties;

public class JythonTests {

    private static String fp = "/Dome/code/pythonDataTypes/";

    public static void runJython() {
        Properties props = new Properties();
        props.setProperty("python.path",fp);
        SimpleData sd1 = new SimpleData("data1",5.0);
        PythonInterpreter interp = new PythonInterpreter();
        interp.initialize(System.getProperties(), props, new String[]{""});
        interp.exec("import sys");
        interp.exec("sys.path.append('/Dome/code/pythonDataTypes/')");
        interp.exec("sys.path.append('/Installers/jython/jython-20020808/dist/Lib/')");
        interp.exec("print sys.path");
        interp.execfile(fp+"PythonNumber.py");
        interp.execfile(fp+"PythonSimpleData.py");
        interp.set("data1",sd1);
        String code = "print data1\ndata2 = PythonSimpleData('dta2',12)\nprint data2";
        interp.exec(code);
//        interp.exec("print data1");
//        interp.exec("data2 = PythonSimpleData('dta2',12)");
//        interp.exec("print data2");
        interp.exec("data3 = data2+data1");
        interp.exec("print data3");
        interp.exec("data4 = data2/data1");
        interp.exec("print data4");
        SimpleData sd3 = (SimpleData)interp.get("data3",SimpleData.class);
        System.out.println(sd3);
        sd3.setValue(222.5);
        System.out.println(sd3);
        interp.exec("print data3");
    }

    public static void main(String[] args) {
        runJython();
    }
}
