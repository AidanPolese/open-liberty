/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.client.util;

/**
 *
 */
public class JaxRSClientUtil {

    public static String convertURItoBusId(String URI) {
        if (URI == null || "".equalsIgnoreCase(URI))
            return URI;
        URI = URI.replace(":", "-").replace("=", "@@");
        return URI;
    }
}
