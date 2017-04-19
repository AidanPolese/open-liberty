/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.web.impl;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.jboss.weld.servlet.WeldInitialListener;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * WeldInitiallAsyncListener is registered as the first AsyncListener
 * so that is is called before all others. It's role is to add the request
 * context to the thread for use by other AsyncListenets. The request
 * request context will be removed by the WeldTerminallAsyncListener.
 */
public class WeldInitialAsyncListener implements AsyncListener {

    private static final TraceComponent tc = Tr.register(WeldInitialAsyncListener.class);

    private final WeldInitialListener weldListener;
    private final ServletContext sc;

    public WeldInitialAsyncListener(WeldInitialListener weldlistener, ServletContext sc) {
        this.weldListener = weldlistener;
        this.sc = sc;
    }

    /** {@inheritDoc} */
    @Override
    public void onComplete(AsyncEvent asyncEvent) {

        notifyWeldInitialListener((HttpServletRequest) asyncEvent.getSuppliedRequest());

    }

    /** {@inheritDoc} */
    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {

        notifyWeldInitialListener((HttpServletRequest) asyncEvent.getSuppliedRequest());

    }

    /** {@inheritDoc} */
    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

        // re-register the listener because starting async removes listeners.
        asyncEvent.getAsyncContext().addListener(this, asyncEvent.getSuppliedRequest(), asyncEvent.getSuppliedResponse());

    }

    /** {@inheritDoc} */
    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {

        notifyWeldInitialListener((HttpServletRequest) asyncEvent.getSuppliedRequest());

    }

    private void notifyWeldInitialListener(HttpServletRequest req) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, " call weldListener.requestInitialized() req =" + req + ", sc = " + sc);
        ServletRequestEvent servletRequestEvent = new ServletRequestEvent(sc, req);
        weldListener.requestInitialized(servletRequestEvent);
    }

}
