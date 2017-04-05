/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import javax.naming.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.injectionengine.InjectionConfigurationException;
import com.ibm.wsspi.injectionengine.factory.EJBLinkReferenceFactory;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

public class EJBLinkReferenceFactoryImpl implements EJBLinkReferenceFactory {
    private static final TraceComponent tc = Tr.register(EJBLinkReferenceFactoryImpl.class);

    private final AtomicServiceReference<EJBLinkReferenceFactory> ejbLinkReferenceFactorySRRef;

    EJBLinkReferenceFactoryImpl(AtomicServiceReference<EJBLinkReferenceFactory> ejbLinkReferenceFactory) {
        ejbLinkReferenceFactorySRRef = ejbLinkReferenceFactory;
    }

    @Override
    public Reference createEJBLinkReference(String refName, String application, String module, String component, String beanName, String beanInterface, String homeInterface,
                                            boolean localRef, boolean remoteRef) throws InjectionConfigurationException {

        EJBLinkReferenceFactory factory = ejbLinkReferenceFactorySRRef.getService();

        if (factory != null) {
            return factory.createEJBLinkReference(refName, application, module, component, beanName, beanInterface, homeInterface, localRef, remoteRef);
        }

        String componentString = component != null ? component : module;
        String message = Tr.formatMessage(tc, "EJB_REF_NOT_SUPPORTED_CWNEN1007E",
                                          refName, componentString, module, application);
        throw new InjectionConfigurationException(message);
    }
}
