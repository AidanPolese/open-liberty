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
package com.ibm.ws.jaxrs20.injection.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * InjectionRuntimeContextImpl helps to store objects for injection
 * ParamInjectionMetadata object is used for parameter injection
 */
public class InjectionRuntimeContext {

    private final Map<String, Object> map = new HashMap<String, Object>();

    public void setRuntimeCtxObject(String type, Object object) {

        if (null == type || null == object) {
            return;
        }

        map.put(type, object);
    }

    public Object getRuntimeCtxObject(String type) {

        if (map.containsKey(type)) {
            return map.get(type);
        }

        return null;
    }
}
