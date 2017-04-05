/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.wsat.components;

/**
 *
 */
public interface WSATConfigService {

    public boolean isSSLEnabled();

    public boolean isClientAuthEnabled();

    public String getSSLReferenceId();

    public String getWSATUrl();

}
