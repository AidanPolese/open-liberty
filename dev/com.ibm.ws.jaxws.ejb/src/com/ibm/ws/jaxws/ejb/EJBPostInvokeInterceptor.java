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
package com.ibm.ws.jaxws.ejb;

import java.rmi.RemoteException;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.ejb.internal.JaxWsEJBConstants;
import com.ibm.wsspi.ejbcontainer.WSEJBEndpointManager;

/**
 *
 */
public class EJBPostInvokeInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    private static final TraceComponent tc = Tr.register(EJBPostInvokeInterceptor.class);

    public EJBPostInvokeInterceptor() {
//        super(Phase.POST_PROTOCOL_ENDING);
        super(Phase.WRITE);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Exchange exchange = message.getExchange();

        // retrieve the ejb endpoint manager
        WSEJBEndpointManager endpointManager = (WSEJBEndpointManager) exchange.get(JaxWsEJBConstants.WS_EJB_ENDPOINT_MANAGER);

        if (endpointManager != null) {
            try {
                endpointManager.ejbPostInvoke();
            } catch (RemoteException e) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Exception occurred when attempting ejbPostInvoke: " + e.getMessage());
                }
                throw new IllegalStateException(e);
            }
        }

    }
}
