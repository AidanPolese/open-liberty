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
package com.ibm.ws.webcontainer.security.internal;

/**
 * Collection of utility methods for String to byte[] conversion.
 */
public class StringUtil {

    /**
     * Convert a byte array to a string
     * 
     * @param b
     * @return string
     */
    public static String toString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = b.length; i < len; i++) {
            sb.append((char) (b[i] & 0xff));
        }
        String str = sb.toString();
        return str;
    }

    /**
     * Convert a string to a byte array
     * 
     * @param str
     * @return byte array
     */
    public static byte[] getBytes(String str) {
        StringBuffer sb = new StringBuffer(str);
        byte[] b = new byte[sb.length()];
        for (int i = 0, len = sb.length(); i < len; i++) {
            b[i] = (byte) sb.charAt(i);
        }
        return b;
    }

    static String hash(String stringToEncrypt) {
        int hashCode = stringToEncrypt.hashCode();
        if (hashCode < 0) {
            hashCode = hashCode * -1;
            return "n" + hashCode;
        } else {
            return "p" + hashCode;
        }
    }
}
