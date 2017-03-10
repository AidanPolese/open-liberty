/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.service.location.internal;

import com.ibm.ws.ffdc.FFDCSelfIntrospectable;
import com.ibm.wsspi.kernel.service.location.WsResource;

/**
 *
 */
interface InternalWsResource extends WsResource, FFDCSelfIntrospectable {

    String getNormalizedPath();

    String getRawRepositoryPath();

    SymbolicRootResource getSymbolicRoot();
}
