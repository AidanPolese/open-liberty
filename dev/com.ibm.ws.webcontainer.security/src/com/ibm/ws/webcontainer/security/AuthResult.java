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
package com.ibm.ws.webcontainer.security;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * AuthenticationResult enumeration of response codes.
 * 
 * A separate class because the compiler conflicts between ant and
 * eclipse are very annoying.
 */
@Trivial
public enum AuthResult {
    UNKNOWN, SUCCESS, FAILURE, SEND_401, REDIRECT, TAI_CHALLENGE, CONTINUE, REDIRECT_TO_PROVIDER, RETURN, OAUTH_CHALLENGE
}