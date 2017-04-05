/**
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
package com.ibm.websphere.management.j2ee;


/**
 * Identifies a JCA resource. A JCAResource object manages one or more connection
 * factories. For each JCA resource provided on a server, there must be one
 * JCAResource OBJECT_NAME in the servers resources list that identifies it.
 */
public interface JCAResourceMBean extends J2EEResourceMBean {

    /**
     * A list of the connection factories available on the corresponding JCAResource
     * object. For each connection factory available to this JCAResource there must be one
     * JCAConnectionFactory OBJECT_NAME in the connectionFactories list that
     * identifies it.
     */
    String[] getconnectionFactories();

}
