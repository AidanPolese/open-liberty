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
package com.ibm.ws.jca.metadata;

import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 * Metadata interface for rar modules.
 */
public interface ConnectorModuleMetaData extends ModuleMetaData {
    /**
     * Returns the unique identifier for the RAR module
     * 
     * @return the unique identifier for the RAR module
     */
    public String getIdentifier();

    /**
     * Returns the JCA specification version with which the resource adapter claims compliance.
     * 
     * @return the JCA specification version with which the resource adapter claims compliance.
     */
    public String getSpecVersion();

    /**
     * Indicates whether or not the RAR module is embedded in an application.
     * 
     * @return true if the RAR module is embedded in an application. Otherwise false.
     */
    public boolean isEmbedded();
}
