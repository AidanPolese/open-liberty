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
package com.ibm.ws.ejbcontainer.remote.internal;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.transport.iiop.spi.ClientORBRef;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

@Component(service = { ObjectFactory.class, ORBObjectFactory.class })
public class ORBObjectFactory implements ObjectFactory {
    private static final String REFERENCE_ORB = "orb";

    private static final AtomicServiceReference<ClientORBRef> orbRef = new AtomicServiceReference<ClientORBRef>(REFERENCE_ORB);

    @Activate
    protected void activate(ComponentContext cc) {
        orbRef.activate(cc);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        orbRef.deactivate(cc);
    }

    @Reference(name = REFERENCE_ORB, service = ClientORBRef.class, target = "(id=defaultOrb)")
    protected void addORBRef(ServiceReference<ClientORBRef> ref) {
        orbRef.setReference(ref);
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (!(obj instanceof javax.naming.Reference)) {
            return null;
        }

        return orbRef.getServiceWithException().getORB();
    }
}
