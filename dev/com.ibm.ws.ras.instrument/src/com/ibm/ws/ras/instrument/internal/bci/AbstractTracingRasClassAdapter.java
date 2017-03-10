/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2013
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.ras.instrument.internal.bci;

import org.objectweb.asm.ClassVisitor;

import com.ibm.ws.ras.instrument.internal.model.ClassInfo;
import com.ibm.ws.ras.instrument.internal.model.TraceOptionsData;

public abstract class AbstractTracingRasClassAdapter extends AbstractRasClassAdapter {

    public AbstractTracingRasClassAdapter(ClassVisitor visitor, ClassInfo classInfo) {
        super(visitor, classInfo);
    }

    public boolean isTraceExceptionOnThrow() {
        TraceOptionsData data = getTraceOptionsData();
        if (data != null) {
            return getTraceOptionsData().isTraceExceptionThrow();
        }
        return false;
    }

    public boolean isTraceExceptionOnHandling() {
        TraceOptionsData data = getTraceOptionsData();
        if (data != null) {
            return data.isTraceExceptionHandling();
        }
        return false;
    }
}
