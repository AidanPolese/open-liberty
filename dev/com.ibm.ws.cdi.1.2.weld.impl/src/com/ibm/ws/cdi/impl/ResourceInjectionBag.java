/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.impl;

import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.resource.ResourceRefConfigList;
import com.ibm.wsspi.injectionengine.JNDIEnvironmentRefBindingHelper;
import com.ibm.wsspi.injectionengine.JNDIEnvironmentRefType;

public class ResourceInjectionBag {
    public final Map<JNDIEnvironmentRefType, Map<String, String>> allBindings = JNDIEnvironmentRefBindingHelper.createAllBindingsMap();
    public final Map<String, String> envEntryValues = new HashMap<String, String>();
    public final ResourceRefConfigList resourceRefConfigList;

    public ResourceInjectionBag(ResourceRefConfigList resourceRefConfigList) {
        this.resourceRefConfigList = resourceRefConfigList;
    }
}
