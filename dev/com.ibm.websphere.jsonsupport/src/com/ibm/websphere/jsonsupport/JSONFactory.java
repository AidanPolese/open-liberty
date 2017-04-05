/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.jsonsupport;

import com.ibm.ws.jsonsupport.internal.JSONJacksonImpl;

/**
 *
 */
public class JSONFactory {

    private static JSON json;

    public static synchronized JSON newInstance() throws JSONMarshallException {
        if (json == null)
            json = new JSONJacksonImpl();
        return json;
    }

    public static JSON newInstance(JSONSettings settings) throws JSONMarshallException {
        return new JSONJacksonImpl(settings);
    }
}
