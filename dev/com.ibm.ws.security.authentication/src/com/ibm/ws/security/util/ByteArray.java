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

/**
 * Wrapper object to allow ByteArray comparison via Object.equals(Object).
 */
public class ByteArray implements java.io.Serializable {

    private static final long serialVersionUID = 7739289083575118864L;
    private final byte[] byteArray;
    private int hash = 0;

    /**
     * <p>
     * Default constructor
     * </p>
     * Returns a ByteArray given a byte[]
     * 
     * @param the array to convert into ByteArray
     * @return the ByteArray
     */
    public ByteArray(byte[] barray) {
        byteArray = barray.clone();
    }

    /**
     * <p>
     * The <code>equals</code> method determines if the
     * array object passed in is equal to another array.
     * </p>
     * 
     * @param the array object
     * @return true if the two arrays are equal
     */
    @Override
    public boolean equals(Object arrayObj) {
        if (arrayObj instanceof ByteArray) {
            return java.util.Arrays.equals(byteArray, ((ByteArray) arrayObj).byteArray);
        }
        return false;
    }

    /**
     * <p>
     * The <code>hashCode</code> method determines the hash of an array.
     * </p>
     * 
     * @return the hash
     */
    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            int len = byteArray.length;
            for (int i = 0; i < len; i++)
                h = 31 * h + byteArray[i];
            hash = h;
        }
        return h;
    }

    /**
     * <p>
     * The <code>getArray</code> method returns a byte array
     * of type byte [].
     * </p>
     * 
     * @return the byte[]
     */
    public byte[] getArray() {
        return byteArray.clone();
    }

}
