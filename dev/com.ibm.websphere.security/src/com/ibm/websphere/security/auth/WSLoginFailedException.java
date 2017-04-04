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
package com.ibm.websphere.security.auth;

import javax.security.auth.login.LoginException;

/**
 * This exception is thrown whenever authentication fails.
 * 
 * @author IBM
 * @version 1.0
 * @ibm-api
 */
public class WSLoginFailedException extends LoginException {

    /**
     * <p>
     * A constructor that accepts an error message. The error message can be retrieved
     * using the getMessage() API.
     * </p>
     * 
     * @param errorMessage An error message.
     */
    public WSLoginFailedException(String errorMessage) {
        super(errorMessage);
    }

}
