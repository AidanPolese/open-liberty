/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.collective.member.security;

/**
 * This interface will be exported as a service to expose collective certificate
 * configuration to security bundle
 * 
 */
public interface CollectiveCertificateConfig {

    /**
     * return configured collective certificate RDN key value pair
     * 
     * @return
     */
    String getRDN();
}
