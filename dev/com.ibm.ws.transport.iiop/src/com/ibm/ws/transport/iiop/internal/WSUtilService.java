/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.internal;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.transport.iiop.spi.RemoteObjectReplacer;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceSet;

/**
 * Internal component to allow WSUtilImpl to interact with OSGi services.
 */
@Component
public class WSUtilService {
    private static volatile WSUtilService instance;

    @Trivial
    private static void setInstance(WSUtilService c) {
        instance = c;
    }

    @Trivial
    static WSUtilService getInstance() {
        return instance;
    }

    private static final String REFERENCE_REMOTE_OBJECT_REPLACERS = "remoteObjectReplacers";

    private final ConcurrentServiceReferenceSet<RemoteObjectReplacer> remoteObjectReplacers =
                    new ConcurrentServiceReferenceSet<RemoteObjectReplacer>(REFERENCE_REMOTE_OBJECT_REPLACERS);

    @Activate
    protected void activate(ComponentContext context) {
        setInstance(this);
        remoteObjectReplacers.activate(context);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        remoteObjectReplacers.deactivate(context);
    }

    @Reference(name = REFERENCE_REMOTE_OBJECT_REPLACERS,
                    service = RemoteObjectReplacer.class,
                    cardinality = ReferenceCardinality.MULTIPLE,
                    policy = ReferencePolicy.DYNAMIC)
    protected void addRemoteObjectReplacer(ServiceReference<RemoteObjectReplacer> ref) {
        remoteObjectReplacers.addReference(ref);
    }

    protected void removeRemoteObjectReplacer(ServiceReference<RemoteObjectReplacer> ref) {
        remoteObjectReplacers.removeReference(ref);
    }

    @Sensitive
    public Object replaceObject(@Sensitive Object obj) {
        for (RemoteObjectReplacer replacer : remoteObjectReplacers.services()) {
            Object result = replacer.replaceRemoteObject(obj);
            if (result != null) {
                return result;
            }
        }
        return obj;
    }
}
