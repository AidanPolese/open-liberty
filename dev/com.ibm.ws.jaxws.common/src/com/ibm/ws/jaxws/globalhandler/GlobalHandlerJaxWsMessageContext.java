/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.globalhandler;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.webservices.handler.GlobalHandlerMessageContext;
import com.ibm.wsspi.webservices.handler.HandlerConstants;

/**
 * This class is used to convert Message to Global handler's Message Context
 * Notice: Customer need to specify the jaxws related jar file version the same as the Liberty WebServices Engine
 */
public class GlobalHandlerJaxWsMessageContext extends WrappedMessageContext implements GlobalHandlerMessageContext {
    private final Message message;
    static final TraceComponent tc = Tr.register(GlobalHandlerJaxWsMessageContext.class);

    /**
     * @param m
     */
    public GlobalHandlerJaxWsMessageContext(Message m) {
        super(m);
        message = m;
    }

    @Override
    public <T> T adapt(Class<T> clazz) {
        {
            javax.xml.ws.handler.MessageContext messageContext = null;
            if (javax.xml.ws.handler.soap.SOAPMessageContext.class.isAssignableFrom(clazz)) {
                messageContext = new GlobalhandlerSOAPMessageContextImpl(message);
                return clazz.cast(messageContext);
            }
            if (javax.xml.ws.handler.LogicalMessageContext.class.isAssignableFrom(clazz)) {
                messageContext = new GlobalHandlerLogicalMsgCtxt(message);
                return clazz.cast(messageContext);
            }

            return null;
        }
    }

    @Override
    public boolean containsProperty(String name) {
        return containsKey(name);
    }

    @Override
    public String getEngineType() {
        return HandlerConstants.ENGINE_TYPE_JAXWS;
    }

    @Override
    public String getFlowType() {

        Exchange ex = message.getExchange();
        if (message == ex.getOutMessage()) {
            return HandlerConstants.FLOW_TYPE_OUT;
        }

        if (message == ex.getInMessage()) {
            return HandlerConstants.FLOW_TYPE_IN;
        }

        return null;

    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        return request;
    }

    @Override
    public HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
        return response;
    }

    @Override
    public Object getProperty(String name) {

        return get(name);
    }

    @Override
    public Iterator<String> getPropertyNames() {
        Iterator<String> it = message.keySet().iterator();

        return it;
    }

    @Override
    public boolean isClientSide() {
        return MessageUtils.isRequestor(message);
    }

    @Override
    public boolean isServerSide() {
        return !MessageUtils.isRequestor(message);
    }

    @Override
    public void removeProperty(String name) {
        remove(name);

    }

    @Override
    public void setProperty(String name, Object value) {
        put(name, value);

    }

}
