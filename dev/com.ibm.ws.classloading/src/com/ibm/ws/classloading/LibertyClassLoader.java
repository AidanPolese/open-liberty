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
package com.ibm.ws.classloading;

import java.security.SecureClassLoader;
import java.util.EnumSet;

import com.ibm.wsspi.classloading.ApiType;

/**
 * This is the supertype of classloaders created by the Liberty profile's
 * class loading service. It is provided purely for internal type safety.
 * Some method signatures will require that you provide a class loader
 * of this type simply to ensure that it is a Liberty class loader.
 * Do not create your own extension of this class as it will <strong>not
 * </strong> work predictably with the Liberty
 */
public abstract class LibertyClassLoader extends SecureClassLoader {
    protected LibertyClassLoader(ClassLoader parent) {
        super(parent);
    }

    /** @return the set of {@link ApiType}s which this class provider can access */
    public abstract EnumSet<ApiType> getApiTypeVisibility();
}
