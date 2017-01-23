package com.ge.ceed.domeapi;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class extends from OutputStream to redirect output to a StringBuffer
 * This is used to redirect stdout and std err messages when we run a Dome Model
 */
public class CustomOutputStream extends OutputStream {
    private StringBuffer strBuffer;

    public CustomOutputStream(StringBuffer strBuffer) {
        this.strBuffer = strBuffer;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        strBuffer.append(String.valueOf((char)b));
    }
}