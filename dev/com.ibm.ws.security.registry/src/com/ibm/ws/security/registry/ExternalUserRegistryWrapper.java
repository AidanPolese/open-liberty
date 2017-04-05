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
package com.ibm.ws.security.registry;

/**
 *
 */
public interface ExternalUserRegistryWrapper {

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.security.registry.UserRegistry#getExternalUserRegistry()
     */
    com.ibm.websphere.security.UserRegistry getExternalUserRegistry();

}
