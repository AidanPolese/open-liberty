/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.threading;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class LibertyJaxRsWorkQueueThreadContext {

    private final Map<String, Object> map = new HashMap<String, Object>();

    public void put(Class<?> cls, Object o) {
        if (cls == null || o == null)
            return;

        map.put(cls.getName(), o);
    }

    public Object get(Class<?> cls) {

        if (cls == null)
            return null;

        return map.get(cls.getName());
    }

    public void remove(Class<?> cls) {
        if (cls == null)
            return;

        map.remove(cls.getName());
    }

}
