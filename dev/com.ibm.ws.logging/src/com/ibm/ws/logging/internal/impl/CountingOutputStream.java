/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.impl;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {
    private final OutputStream out;
    private long count;

    public CountingOutputStream(OutputStream out) {
        this.out = out;
    }

    public long count() {
        return count;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        count++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        count += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        count += len;
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
