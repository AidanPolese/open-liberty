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
package com.ibm.ws.injection;

import java.io.PrintWriter;

import com.ibm.ws.injectionengine.osgi.internal.DeferredReferenceData;
import com.ibm.wsspi.injectionengine.InjectionException;

public class TestDeferredReferenceData implements DeferredReferenceData {
    private final Boolean returnValue;
    boolean called;

    public TestDeferredReferenceData(Boolean returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public boolean processDeferredReferenceData() throws InjectionException {
        called = true;
        if (returnValue == null) {
            throw new InjectionException("test exception");
        }
        return returnValue;
    }

    @Override
    public void introspectDeferredReferenceData(PrintWriter writer, String indent) {}
}
