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
package com.ibm.ws.zos.core;

/**
 * Marker interface that represents the angel process, if we're connected to it.
 */
public interface Angel {
    /**
     * Service property that indicates the version of the DRM when this service
     * was registered.
     */
    public final static String ANGEL_DRM_VERSION = "angel.drm.version";

    /**
     * Get the version of the DRM the angel was using when this service was registered.
     */
    public int getDRM_Version();
}
