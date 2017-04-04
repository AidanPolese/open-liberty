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
package com.ibm.ws.ejbcontainer.remote.client.internal.injection;

import javax.naming.Reference;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.wsspi.injectionengine.factory.EJBLinkReferenceFactory;

@Component(service = EJBLinkReferenceFactory.class)
public class EJBLinkClientReferenceFactoryImpl implements EJBLinkReferenceFactory {

    private static final String FACTORY_CLASS_NAME = EJBLinkClientObjectFactoryImpl.class.getName();

    @org.osgi.service.component.annotations.Reference(service = LibertyProcess.class, target = "(wlp.process.type=client)")
    protected void setLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    protected void unsetLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    @Override
    public Reference createEJBLinkReference(String refName,
                                            String application, String module, String component,
                                            String beanName,
                                            String beanInterface, String homeInterface,
                                            boolean localRef, boolean remoteRef) {
        EJBLinkClientInfo info = new EJBLinkClientInfo(
                        refName,
                        application, module, component,
                        beanName,
                        beanInterface, homeInterface,
                        localRef, remoteRef);

        EJBLinkClientInfoRefAddr refAddr = new EJBLinkClientInfoRefAddr(info);
        Reference ref = new Reference(beanInterface, refAddr, FACTORY_CLASS_NAME, null);
        return ref;
    }
}