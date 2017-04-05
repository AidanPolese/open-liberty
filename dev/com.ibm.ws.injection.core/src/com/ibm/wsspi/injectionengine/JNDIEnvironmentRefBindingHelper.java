/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.resource.ResourceRefConfigList;

public class JNDIEnvironmentRefBindingHelper {
    /**
     * Create a new map for holding all JNDIEnvironmentRef bindings.
     */
    public static Map<JNDIEnvironmentRefType, Map<String, String>> createAllBindingsMap() {
        Map<JNDIEnvironmentRefType, Map<String, String>> allBindings = new EnumMap<JNDIEnvironmentRefType, Map<String, String>>(JNDIEnvironmentRefType.class);
        for (JNDIEnvironmentRefType refType : JNDIEnvironmentRefType.VALUES) {
            if (refType.getBindingElementName() != null) {
                allBindings.put(refType, new HashMap<String, String>());
            }
        }
        return allBindings;
    }

    /**
     * Update a ComponentNameSpaceConfiguration object with processed binding
     * and extension metadata.
     *
     * @param compNSConfig the configuration to update
     * @param allBindings the map of all bindings
     * @param envEntryValues the env-entry value bindings map
     * @param resRefList the resource-ref binding and extension list
     */
    public static void setAllBndAndExt(ComponentNameSpaceConfiguration compNSConfig,
                                       Map<JNDIEnvironmentRefType, Map<String, String>> allBindings,
                                       Map<String, String> envEntryValues,
                                       ResourceRefConfigList resRefList) {
        for (JNDIEnvironmentRefType refType : JNDIEnvironmentRefType.VALUES) {
            if (refType.getBindingElementName() != null) {
                compNSConfig.setJNDIEnvironmentRefBindings(refType.getType(), allBindings.get(refType));
            }
        }

        compNSConfig.setEnvEntryValues(envEntryValues);
        compNSConfig.setResourceRefConfigList(resRefList);
    }
}
