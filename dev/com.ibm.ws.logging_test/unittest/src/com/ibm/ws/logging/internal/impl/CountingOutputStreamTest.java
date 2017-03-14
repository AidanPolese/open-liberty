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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

public class CountingOutputStreamTest {
    @Test
    public void testWrite() throws Exception {
        CountingOutputStream out = new CountingOutputStream(new ByteArrayOutputStream());
        long n = 0;
        Assert.assertEquals(n, out.count());

        out.write(0);
        n++;
        Assert.assertEquals(n, out.count());

        out.write(Byte.MAX_VALUE);
        n++;
        Assert.assertEquals(n, out.count());

        out.write(new byte[0]);
        Assert.assertEquals(n, out.count());

        out.write(new byte[1]);
        n++;
        Assert.assertEquals(n, out.count());

        out.write(new byte[2]);
        n += 2;
        Assert.assertEquals(n, out.count());

        out.write(new byte[2], 0, 0);
        Assert.assertEquals(n, out.count());

        out.write(new byte[1], 0, 1);
        n++;
        Assert.assertEquals(n, out.count());

        out.write(new byte[2], 0, 2);
        n += 2;
        Assert.assertEquals(n, out.count());
    }

    @Test
    public void testIOException() {
        CountingOutputStream out = new CountingOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        });

        try {
            out.write(0);
            Assert.fail("expected IOException");
        } catch (IOException e) {
        }

        Assert.assertEquals(0, out.count());
    }

    @Test
    public void testIndexOutOfBoundsException() throws Exception {
        CountingOutputStream out = new CountingOutputStream(new ByteArrayOutputStream());

        try {
            out.write(new byte[0], 0, -1);
        } catch (IndexOutOfBoundsException e) {
        }

        Assert.assertEquals(0, out.count());
    }
}
