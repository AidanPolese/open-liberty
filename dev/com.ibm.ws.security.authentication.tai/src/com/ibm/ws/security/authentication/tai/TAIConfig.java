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
package com.ibm.ws.security.authentication.tai;

/**
 * Represents security configurable options for Trust Association.
 */
public interface TAIConfig {

    public static final String KEY_INVOKE_BEFORE_SSO = "invokeBeforeSSO";

    public static final String KEY_INVOKE_AFTER_SSO = "invokeAfterSSO";

    public boolean isFailOverToAppAuthType();

    public boolean isInvokeForUnprotectedURI();

    public boolean isInvokeForFormLogin();
}
