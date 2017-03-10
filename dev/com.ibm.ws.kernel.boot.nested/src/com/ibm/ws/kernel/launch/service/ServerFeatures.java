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
package com.ibm.ws.kernel.launch.service;

import com.ibm.ws.kernel.boot.internal.BootstrapConstants;

public interface ServerFeatures {

    public final static String REQUEST_SERVER_FEATURES_PROPERTY = BootstrapConstants.REQUEST_SERVER_FEATURES_PROPERTY;

    /**
     * Get an array of feature names for the features required by the server.
     */
    String[] getServerFeatureNames();
}
