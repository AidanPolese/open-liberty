/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.injection;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;

/**
 * @param <T>
 * 
 */
public class LibertyClearInjectRuntimeCtxOutInterceptor<T extends Message> extends AbstractPhaseInterceptor<T> {

    /**
     * we should clear InjectionRuntimeContext after JAXRSOutInterceptor
     * The last provider should be MessageBodyWriter
     * 
     * @param phase
     */
    public LibertyClearInjectRuntimeCtxOutInterceptor(String phase) {
        super(phase);

        addAfter(JAXRSOutInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        //clear InjectionRuntimeContext
        InjectionRuntimeContextHelper.removeRuntimeContext();
    }
}
