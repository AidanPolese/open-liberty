/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.wsat.components;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;

/**
 *
 */
public interface WSATInterceptorService {
    public AbstractPhaseInterceptor<Message> getCoorContextOutInterceptor();

    public AbstractPhaseInterceptor<SoapMessage> getCoorContextInInterceptor();

    public AbstractPhaseInterceptor<Message> getSSLServerInterceptor();
}
