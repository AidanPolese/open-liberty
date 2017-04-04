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

import com.ibm.ws.resource.ResourceRefConfig;
import com.ibm.ws.resource.ResourceRefConfigFactory;
import com.ibm.ws.resource.ResourceRefConfigList;
import com.ibm.wsspi.resource.ResourceConfig;
import com.ibm.wsspi.resource.ResourceConfigFactory;

public class ResourceRefConfigFactoryImpl implements ResourceConfigFactory, ResourceRefConfigFactory {

    @Override
    public ResourceConfig createResourceConfig(String type) {
        return createResourceRefConfig(type);
    }

    @Override
    public ResourceRefConfig createResourceRefConfig(String type) {
        return new ResourceRefConfigImpl(null, type);
    }

    @Override
    public ResourceRefConfigList createResourceRefConfigList() {
        return new ResourceRefConfigListImpl();
    }

}
