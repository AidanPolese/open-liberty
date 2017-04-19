/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer31.osgi.srt.factory;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContextPool;
import com.ibm.ws.webcontainer31.osgi.srt.SRTConnectionContext31;

/**
 * A simple pool for SRTConnectionContext31 objects.
 */
@Component(property = { "service.vendor=IBM", "service.ranking:Integer=31", "servlet.version=3.1" })
public class SRTConnectionContextPool31Impl implements SRTConnectionContextPool {
    private final ThreadLocal<com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContext> head = new ThreadLocal<com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContext>();

    @Override
    public final com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContext get() {
        com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContext context = null;
        com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContext headContext = head.get();
        if (headContext != null) {
            context = headContext;
            head.set(context.nextContext);
        }

        if (context == null) {
            context = new SRTConnectionContext31();
        }

        context.nextContext = null;

        return context;
    }

    @Override
    public final void put(com.ibm.ws.webcontainer.osgi.srt.SRTConnectionContext context) {
        context.nextContext = head.get();
        head.set(context);
    }
}
