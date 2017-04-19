/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * Drive the
 */
public class ByteArrayTest {

    /**
     * Test method for {@link com.ibm.ws.security.util.ByteArray#equals(java.lang.Object)}.
     */
    @Test
    public void equalsNull() {
        byte[] bytes = "abc".getBytes();
        ByteArray bArray = new ByteArray(bytes);
        assertFalse("bArray should not equal null", bArray.equals(null));
    }

    /**
     * Test method for {@link com.ibm.ws.security.util.ByteArray#equals(java.lang.Object)}.
     */
    @Test
    public void equalsByteArrayEquals() {
        byte[] bytes1 = "abc".getBytes();
        byte[] bytes2 = "abc".getBytes();
        ByteArray b1 = new ByteArray(bytes1);
        ByteArray b2 = new ByteArray(bytes2);
        assertEquals("bArray1 should equal bArray2", b2, b1);
        assertEquals("bArray1's hash code should equal bArray2's hash code",
                     b2.hashCode(), b1.hashCode());
    }

    /**
     * Test method for {@link com.ibm.ws.security.util.ByteArray#equals(java.lang.Object)}.
     */
    @Test
    public void equalsByteArrayNotEquals() {
        byte[] bytes1 = "abc".getBytes();
        byte[] bytes2 = "123".getBytes();
        ByteArray b1 = new ByteArray(bytes1);
        ByteArray b2 = new ByteArray(bytes2);
        assertFalse("bArray1 should not equal bArray2",
                    b2.equals(b1));
        assertFalse("bArray1's hash code should not equal bArray2's hash code",
                     b2.hashCode() == b1.hashCode());
    }

    @Test
    public void getArray() {
        byte[] bytes = "abc".getBytes();
        ByteArray bArray = new ByteArray(bytes);
        assertTrue("bArray's bytes should be equals to the ones used in the constructor",
                   Arrays.equals(bytes, bArray.getArray()));
    }

}
