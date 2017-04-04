/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.injectionengine.InjectionBinding;

@Trivial
public class NonCompBinding {
    private static final TraceComponent tc = Tr.register(NonCompBinding.class);

    final OSGiInjectionScopeData scopeData;
    final InjectionBinding<?> binding;
    private int refs;

    NonCompBinding(OSGiInjectionScopeData scopeData, InjectionBinding<?> binding) {
        this.scopeData = scopeData;
        this.binding = binding;
        refs = 1;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "<init>", this);
        }
    }

    @Override
    public String toString() {
        return super.toString() +
               "[binding=" + binding +
               ", scope=" + scopeData +
               ", refs=" + refs +
               ']';
    }

    public void ref() {
        refs++;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "ref", this);
        }
    }

    public void unref() {
        refs--;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "unref", this);
        }

        if (refs == 0) {
            scopeData.removeNonCompBinding(binding);
        }
    }
}
