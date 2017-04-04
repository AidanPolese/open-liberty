/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import java.io.PrintWriter;

import com.ibm.ws.injectionengine.ReferenceContextImpl;
import com.ibm.wsspi.injectionengine.InjectionException;

public class OSGiReferenceContextImpl extends ReferenceContextImpl implements DeferredReferenceData {
    private final OSGiInjectionScopeData scopeData;

    OSGiReferenceContextImpl(OSGiInjectionEngineImpl injectionEngine, OSGiInjectionScopeData scopeData) {
        super(injectionEngine);
        this.scopeData = scopeData;
        scopeData.addDeferredReferenceData(this);
    }

    @Override
    public synchronized void process() throws InjectionException {
        scopeData.removeDeferredReferenceData(this);
        super.process();
    }

    @Override
    public boolean processDeferredReferenceData() throws InjectionException {
        process();
        return true;
    }

    @Override
    public void introspectDeferredReferenceData(PrintWriter writer, String indent) {
        writer.println(indent + toString());
    }
}
