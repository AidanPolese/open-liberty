/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import java.util.EnumSet;

import com.ibm.wsspi.classloading.ApiType;

/**
 * An interface for anything that has access to a restricted set of APIs.
 */
public interface DeclaredApiAccess {

    EnumSet<ApiType> getApiTypeVisibility();

}
