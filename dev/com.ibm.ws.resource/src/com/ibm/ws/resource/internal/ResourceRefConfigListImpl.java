/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.resource.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.ws.resource.ResourceRefConfig;
import com.ibm.ws.resource.ResourceRefConfigList;

public class ResourceRefConfigListImpl implements ResourceRefConfigList {

    private final List<ResourceRefConfig> ivList = new ArrayList<ResourceRefConfig>();
    private final Map<String, ResourceRefConfig> ivMap = new HashMap<String, ResourceRefConfig>();

    @Override
    public String toString() {
        return super.toString() + ivList;
    }

    @Override
    public int size() {
        return ivList.size();
    }

    @Override
    public ResourceRefConfig getResourceRefConfig(int i) {
        return ivList.get(i);
    }

    @Override
    public ResourceRefConfig findByName(String name) {
        return ivMap.get(name);
    }

    @Override
    public ResourceRefConfig findOrAddByName(String name) {
        ResourceRefConfig resRef = ivMap.get(name);
        if (resRef == null) {
            resRef = new ResourceRefConfigImpl(name, null);
            ivList.add(resRef);
            ivMap.put(name, resRef);
        }
        return resRef;
    }

}
