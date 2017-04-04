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
package com.ibm.ws.jca.rar;

import com.ibm.wsspi.classloading.ClassLoaderIdentity;

/**
 * Provides coordination between a resource adapter and the configured
 * bundle service associated with that resource adapter.
 */
public interface ResourceAdapterBundleService {
    /**
     * Sets the classloader identity for the resource adapter on the
     * associated resource adapter bundle service.
     * 
     * This method will be called after the resource adapter bundle
     * service has been activated, but before the resource adapter
     * has been installed.
     */
    void setClassLoaderID(ClassLoaderIdentity classloaderId);
}
