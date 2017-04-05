/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.runtime.metadata;

/**
 * Internal. This class is used for internal communication between the metadata service and {@link MetaDataImpl}, which is extended by containers.
 */
public class MetaDataSecrets {
    /**
     * Initializes the metadata id.
     */
    public static void setID(MetaDataImpl metaData, int id) {
        metaData.id = id;
    }

    /**
     * The id set by {@link #setID}, or -1 if never called.
     */
    public static int getID(MetaDataImpl metaData) {
        return metaData.id;
    }
}