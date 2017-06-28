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
package com.ibm.ws.jaxrs20.cache;

import java.lang.ref.SoftReference;
import java.util.WeakHashMap;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.model.OperationResourceInfo;

/**
 *
 */
public class LibertyJaxRsResourceMethodCache {

    final public class ResourceMethodCache {

        OperationResourceInfo ori;
        MultivaluedMap<String, String> values;
        String responseMediaType;

        public ResourceMethodCache(OperationResourceInfo ori, MultivaluedMap<String, String> values, String responseMediaType) {
            this.ori = ori;
            this.values = values;
            this.responseMediaType = responseMediaType;
        }

        public OperationResourceInfo getOperationResourceInfo() {
            return this.ori;
        }

        public MultivaluedMap<String, String> getValues() {
            return this.values;
        }

        public String getMediaType() {
            return this.responseMediaType;
        }
    }

    private final WeakHashMap<String, SoftReference<ResourceMethodCache>> cache = new WeakHashMap<String, SoftReference<ResourceMethodCache>>();

    public ResourceMethodCache get(String uriString) {

        if (uriString == null || "".equals(uriString)) {
            return null;
        }

        SoftReference<ResourceMethodCache> ref = cache.get(uriString);

        if (ref == null || ref.get() == null) {
            return null;
        }

        return ref.get();
    }

    public void put(String uriString, OperationResourceInfo ori, MultivaluedMap<String, String> values, String mt) {

        if (uriString == null || "".equals(uriString) || ori == null || values == null) {
            return;
        }
        synchronized (cache) {
            cache.put(uriString, new SoftReference<ResourceMethodCache>(new ResourceMethodCache(ori, values, mt)));
        }
    }

    public void destroy() {

        cache.clear();
    }
}
