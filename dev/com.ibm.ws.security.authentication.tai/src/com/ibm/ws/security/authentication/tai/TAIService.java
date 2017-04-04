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

import java.util.Map;

import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

public interface TAIService {

    public boolean isInvokeForUnprotectedURI();

    public boolean isFailOverToAppAuthType();

    public boolean isInvokeForFormLogin();

    public Map<String, TrustAssociationInterceptor> getTais(boolean invokeBeforeSSO);
}
