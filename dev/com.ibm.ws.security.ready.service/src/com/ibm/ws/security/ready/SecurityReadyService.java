/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.ready;

/**
 *
 */
public interface SecurityReadyService {
    /**
     * Answers if the security service as a whole is ready to process requests.
     * 
     * @return boolean indiciating if the security service is ready to process
     *         requests.
     */
    public boolean isSecurityReady();

}
