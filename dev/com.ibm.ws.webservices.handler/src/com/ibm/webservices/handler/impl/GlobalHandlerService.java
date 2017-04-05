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
package com.ibm.webservices.handler.impl;

import java.util.List;

import com.ibm.wsspi.webservices.handler.Handler;

/**
 *
 */
public interface GlobalHandlerService {

    public List<Handler> getJAXWSServerSideInFlowGlobalHandlers();

    public List<Handler> getJAXWSServerSideOutFlowGlobalHandlers();

    public List<Handler> getJAXWSClientSideInFlowGlobalHandlers();

    public List<Handler> getJAXWSClientSideOutFlowGlobalHandlers();

    public List<Handler> getJAXRSServerSideInFlowGlobalHandlers();

    public List<Handler> getJAXRSServerSideOutFlowGlobalHandlers();

    public List<Handler> getJAXRSClientSideInFlowGlobalHandlers();

    public List<Handler> getJAXRSClientSideOutFlowGlobalHandlers();

    public boolean getSaajFlag();

}
