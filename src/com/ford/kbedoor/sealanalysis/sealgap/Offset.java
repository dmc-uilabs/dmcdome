package com.ford.kbedoor.sealanalysis.sealgap;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Offset.java
 * @author Ravi Boppe
 * @version 1.0
 * @since Jan 2000
 */

public class Offset {
    protected Double xValue;
    protected Double yValue;
    protected Double zValue;
    protected Boolean inputValue = new Boolean(true);
    protected boolean validated = true;

    public Offset() {
        xValue = new Double(0);
        yValue = new Double(0);
        zValue = new Double(0);
    }

    public Offset(double x, double y, double z) {
        xValue = new Double(x);
        yValue = new Double(y);
        zValue = new Double(z);
    }

    public Offset(double x, double y, double z, boolean input) {
        xValue = new Double(x);
        yValue = new Double(y);
        zValue = new Double(z);
        inputValue = new Boolean(input);
    }

    public double getXValue() {
        return ((Double) xValue).doubleValue();
    }

    public double getYValue() {
        return ((Double) yValue).doubleValue();
    }

    public double getZValue() {
        return ((Double) zValue).doubleValue();
    }

    public boolean isValid() {
        return validated;
    }

    public boolean isInputValue() {
        return inputValue.booleanValue();
    }

    public void setXValue(double d) {
        xValue = new Double(d);
    }

    public void setYValue(double d) {
        yValue = new Double(d);
    }

    public void setZValue(double d) {
        zValue = new Double(d);
    }

    public void setValid(boolean b) {
        validated = b;
    }

    public void setInputValue(boolean b) {
        inputValue = new Boolean(b);
    }


    public void copy(Offset p) {
        if (p != null) {
            xValue = p.xValue;
            yValue = p.yValue;
            zValue = p.zValue;
            inputValue = p.inputValue;
        }

    }

    public void print() {
        if (xValue instanceof Double)
            System.out.println("X Value = " + ((Double) xValue).doubleValue());
        if (yValue instanceof Double)
            System.out.println("Y Value = " + ((Double) yValue).doubleValue());
        if (zValue instanceof Double)
            System.out.println("Z Value = " + ((Double) zValue).doubleValue());
        System.out.println("\n");
    }

    public void write(FileWriter fw) {
        String s = (xValue + ", " + yValue + ", " + zValue + ", "
                + inputValue + "\n");
        try {
            fw.write(s);
        } catch (IOException e) {
            System.out.println("Error writing parameter\n" + e);
        }
    }

}
