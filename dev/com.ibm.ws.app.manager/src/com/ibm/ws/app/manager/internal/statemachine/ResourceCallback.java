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
package com.ibm.ws.app.manager.internal.statemachine;

import java.io.File;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.kernel.service.location.WsResource;

interface ResourceCallback {
    /**
     * An indication that an attempt was made to access the resource, and it
     * is not currently available, but it will be monitored for availability.
     * This method may be called multiple times.
     */
    void pending();

    void successfulCompletion(Container c, WsResource r);

    void failedCompletion(Throwable t);

    Container setupContainer(String _servicePid, File downloadedFile);
}
