/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.config;

/**
 *
 */
public interface MetaTypeProviderConstants {
    /** MetaTypeProvider Added Event Topic */
    String METATYPE_PROVIDER_ADDED_TOPIC = "com/ibm/ws/config/xml/internal/MetaTypeRegistry/METATYPE_PROVIDER_ADDED";

    /** MetaTypeProvider Removed Event Topic */
    String METATYPE_PROVIDER_REMOVED_TOPIC = "com/ibm/ws/config/xml/internal/MetaTypeRegistry/METATYPE_PROVIDER_REMOVED";

    /** MetaTypeProvider Updated PIDS property key */
    String UPDATED_PID = "mtp_updated_pids";

}
