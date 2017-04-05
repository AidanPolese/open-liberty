package com.ibm.ws.collective.security;

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

/**
 * Credential used to represent a server within the collective infrastructure.
 */
public interface CollectiveServerCredential {

    /**
     * Get the collective DN which is represented by this credential.
     * 
     * @return the collective DN for this credential
     * @see CollectiveDNUtil
     */
    String getCollectiveDN();

    /**
     * Answers whether the credential represents a Collective Controller.
     * 
     * @return {@code true} if the server is a Collective Controller., {@code false} otherwise
     */
    boolean isCollectiveController();

    /**
     * Get the name of the server this credential represents.
     * 
     * @return the server's name
     */
    String getServerName();

    /**
     * Get the host name of the server this credential represents.
     * 
     * @return the host name of the server
     */
    String getHostName();

    /**
     * Get the url encoded user dir of the server this credential represents.
     * 
     * @return the user dir of the server
     */
    String getURLEncodedUserDir();

}
