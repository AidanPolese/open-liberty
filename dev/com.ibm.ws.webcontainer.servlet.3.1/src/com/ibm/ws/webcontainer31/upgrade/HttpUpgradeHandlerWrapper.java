/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer31.upgrade;

import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.WebConnection;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.webcontainer.osgi.osgi.WebContainerConstants;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;

/**
 *
 */
public class HttpUpgradeHandlerWrapper implements HttpUpgradeHandler {

    private final static TraceComponent tc = Tr.register(HttpUpgradeHandlerWrapper.class, WebContainerConstants.TR_GROUP, LoggerFactory.MESSAGES);

    HttpUpgradeHandler wrappedHandler;
    WebApp webapp;

    public HttpUpgradeHandlerWrapper(WebApp webapp, HttpUpgradeHandler handler) {
        this.webapp = webapp;
        wrappedHandler = handler;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpUpgradeHandler#destroy()
     */
    @Override
    public void destroy() {
        //call predestroy
        try {
            webapp.performPreDestroy(wrappedHandler);
        } catch (InjectionException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "destroy  injectionException during preDestroy: ", e);
            }
        }
        wrappedHandler.destroy();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpUpgradeHandler#init(javax.servlet.http.WebConnection)
     */
    @Override
    public void init(WebConnection connection) {
        wrappedHandler.init(connection);
    }

}
