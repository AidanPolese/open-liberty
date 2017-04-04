/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authentication.internal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import com.ibm.ws.security.authentication.AuthenticationData;

/**
 *
 */
public interface JAASService {

    /**
     * Performs a JAAS login.
     * 
     * @param jaasEntryName
     * @param authenticationData
     * @param partialSubject
     * @return the authenticated subject.
     * @throws javax.security.auth.login.LoginException
     */
    public abstract Subject performLogin(String jaasEntryName, AuthenticationData authenticationData, Subject partialSubject) throws LoginException;

    /**
     * Performs a JAAS login.
     * 
     * @param jaasEntryName
     * @param callbackHandler
     * @param partialSubject
     * @return the authenticated subject.
     * @throws javax.security.auth.login.LoginException
     */
    public abstract Subject performLogin(String jaasEntryName, CallbackHandler callbackHandler, Subject partialSubject) throws LoginException;

}