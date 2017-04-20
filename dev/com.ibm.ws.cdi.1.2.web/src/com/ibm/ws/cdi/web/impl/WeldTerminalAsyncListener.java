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
 * WeldTerminalAsyncListener is registered as the last AsyncListener
 * so that is is called after all others. It's role is to remove the
 * request context which was added by the WeldInitialAsyncListener.
 */
public class WeldTerminalAsyncListener implements AsyncListener {

    private static final TraceComponent tc = Tr.register(WeldTerminalAsyncListener.class);

    private final WeldInitialListener weldListener;
    private final ServletContext sc;

    public WeldTerminalAsyncListener(WeldInitialListener weldlistener, ServletContext sc) {
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

    }

    /** {@inheritDoc} */
    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {

        notifyWeldInitialListener((HttpServletRequest) asyncEvent.getSuppliedRequest());

    }

    private void notifyWeldInitialListener(HttpServletRequest req) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "call weldListener.requestDestroyed() req =" + req + ", sc = " + sc);
        ServletRequestEvent servletRequestEvent = new ServletRequestEvent(sc, req);
        weldListener.requestDestroyed(servletRequestEvent);
    }

}
