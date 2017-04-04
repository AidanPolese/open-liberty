/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import javax.naming.Reference;

import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.factory.IndirectJndiLookupReferenceFactory;

public class IndirectJndiLookupReferenceFactoryImpl implements IndirectJndiLookupReferenceFactory {
    private final ResourceBindingListenerManager resourceBindingListenerManager;

    public IndirectJndiLookupReferenceFactoryImpl(ResourceBindingListenerManager resourceBindingListenerManager) {
        this.resourceBindingListenerManager = resourceBindingListenerManager;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + resourceBindingListenerManager + ']';
    }

    @Override
    public Reference createIndirectJndiLookup(String refName, String bindingName, String type) throws InjectionException {
        if (resourceBindingListenerManager != null && type != null) {
            ResourceBindingImpl binding = resourceBindingListenerManager.binding(refName, bindingName, type, null);
            if (binding != null) {
                return new IndirectReference(refName, binding.getBindingName(), type, null, binding.getBindingListenerName(), false);
            }
        }

        return new IndirectReference(refName, bindingName, type, null, null, false);
    }

    @Override
    public Reference createIndirectJndiLookupInConsumerContext(String refName, String bindingName, String type) throws InjectionException {
        if (resourceBindingListenerManager != null && type != null) {
            ResourceBindingImpl binding = resourceBindingListenerManager.binding(refName, bindingName, type, null);
            if (binding != null) {
                return new IndirectReference(refName, binding.getBindingName(), type, null, binding.getBindingListenerName(), false);
            }
        }

        // Note that defaultBinding = true to allow auto-link where supported
        return new IndirectReference(refName, bindingName, type, null, null, true);
    }
}
