package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

import java.util.List;
import java.util.Vector;
import java.util.Arrays;

/**
 * User: Sangmok Han
 * Date: 2005. 3. 29.
 */
public class TutorialPartFour {
    public static void main(String[] args) {
        /* open a connection and log in to DOME server */
        DomeConnection conn = new DomeConnection("root", "cadlab", "localhost:8080");

        /* browse folders, models and Interfaces */
        DomeFolder publicFolder = conn.getPublicFolder();
        DomeModel sampleModel = publicFolder.getModelByName("Type Handling Sample Model");
        DomeInterface myInterface = sampleModel.getInterfaceByName("Default Interface");

        /* instantiate Runtime Interface and retrieve the list of parameters */
        RuntimeInterface runtimeInterface = myInterface.createRuntimeInterface();

        /* set download folder for file parameters */
        runtimeInterface.setDownloadFolder("C:\\");

        List paramList = runtimeInterface.getParameters();
        System.out.println("[PARAM LIST - BEFORE] " + paramList);

        /* changes the values of integer parameters */
        RuntimeParameter integerParam = runtimeInterface.getParameterByName("MyInteger");
        integerParam.setIntegerValue(2);

        /* changes the values of real parameters */
        RuntimeParameter realParam = runtimeInterface.getParameterByName("MyReal");
        realParam.setRealValue(2.0);

        /* changes the values of boolean parameters */
        RuntimeParameter booleanParam = runtimeInterface.getParameterByName("MyBoolean");
        booleanParam.setBooleanValue(true);

        /* changes the values of matrix parameters */
        RuntimeParameter matrixParam = runtimeInterface.getParameterByName("MyMatrix");

        /* create a vector containing each row as a vector */
        Vector matrixData = new Vector();
        Vector row1 = new Vector();
        row1.add(new Integer(1)); row1.add(new Integer(2)); row1.add(new Integer(3));
        matrixData.add(row1);
        Vector row2 = new Vector();
        row2.add(new Integer(4)); row2.add(new Integer(5)); row2.add(new Integer(6));
        matrixData.add(row2);
        matrixParam.setMatrixValue(matrixData);

        /* changes the values of Vector parameters */
        RuntimeParameter vectorParam = runtimeInterface.getParameterByName("MyVector");
        Vector vectorData = new Vector();
        vectorData.add(new Double(10.0)); vectorData.add(new Double(20.0));
        vectorParam.setVectorValue(vectorData);

        /* changes the values of Enumeration parameters */
        RuntimeParameter enumParam = runtimeInterface.getParameterByName("MyEnumeration");
        enumParam.setEnumerationValue(1);

        /* changes the values of String parameters */
        RuntimeParameter stringParam = runtimeInterface.getParameterByName("MyString");
        stringParam.setStringValue("Hi");

        /* changes the values of file parameters */
        RuntimeParameter srcFileParam = runtimeInterface.getParameterByName("MySourceFile");
        RuntimeParameter tgtFileParam = runtimeInterface.getParameterByName("MyTargetFile");
        srcFileParam.setFileValue("NEW SOURCE TEXT");

        // note. for *.txt file, getFileValue() returns String of the file contents
        // note. you can see my source file still has 'SOURCE TEXT', because the local change 'NEW SOURCE TEXT' has not been submitted.
        System.out.println("MySourceFile = " + srcFileParam.getFileValue() + ", FileType = " + srcFileParam.getFileType() + ", FileName = " + srcFileParam.getFileName());
        System.out.println("MyTargetFile = " + tgtFileParam.getFileValue() + ", FileType = " + tgtFileParam.getFileType() + ", FileName = " + tgtFileParam.getFileName());

        /* submit changes and handle execution time limit exception */
        runtimeInterface.submit();

        /* retrieve the results */
        System.out.println("[PARAM LIST - AFTER] " + paramList);

        /* retrieve the results */
        int integerValue = integerParam.getIntegerValue();
        double realValue = realParam.getRealValue();
        boolean booleanValue = booleanParam.getBooleanValue();
        Vector matrixValue = matrixParam.getMatrixValue();
        Vector vectorValue = vectorParam.getVectorValue();
        String stringValue = stringParam.getStringValue();

        System.out.println("MyInteger = " + integerValue);
        System.out.println("MyReal = " + realValue);
        System.out.println("MyBoolean = " + booleanValue);
        System.out.println("MyString = " + stringValue);

        for (int i = 0; i < matrixValue.size(); i++) {
            Vector rowVector = (Vector) matrixValue.get(i);
            System.out.println("MyMatrix " + (i + 1) + " row = " + rowVector);
        }

        System.out.println("MyVector = " + vectorValue);

        Object[] enumData = enumParam.getEnumerationValue();
        int enumIndex = ((Integer) enumData [0]).intValue();
        String[] enumNames = (String[]) enumData [1];
        Object[] enumValues = (Object[]) enumData [2];

        System.out.println("MyEnumeration = " + enumNames[enumIndex] + "(" + enumValues[enumIndex] + ")");

        // note. now both files have the same 'NEW SOURCE TEXT'.
        System.out.println("MySourceFile = " + srcFileParam.getFileValue() + ", FileType = " + srcFileParam.getFileType() + ", FileName = " + srcFileParam.getFileName());
        System.out.println("MyTargetFile = " + tgtFileParam.getFileValue() + ", FileType = " + tgtFileParam.getFileType() + ", FileName = " + tgtFileParam.getFileName());

        /* log out and close connection */
        conn.close();
    }
}

class MyValueChangeListener4 implements ParameterValueChangeListener {
    public void valueChanged(ParameterValueChangeEvent event) {
        System.out.println("My value listener : Parameter value changed = " + event);
    }
}

class MyStatusChangeListener4 implements ParameterStatusChangeListener {
    public void statusChanged(ParameterStatusChangeEvent event) {
        System.out.println("My status listener : Parameter status changed = " + event);

        /* ParameterStatusChangeListener */
        if (ParameterStatusChangeEvent.SOLVING_SUCCESS_EVENT.equals(event)) {
            System.out.println("My status listener : Solving succeed");
        } else if (ParameterStatusChangeEvent.SOLVING_FAILURE_EVENT.equals(event)) {
            System.out.println("My status listener : Solving failed");
        }
    }
}

