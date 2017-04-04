/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.utils;

import java.security.AccessController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.util.ThreadContextAccessor;

/**
 *
 */
public class JAXBUtils {

    private static final TraceComponent tc = Tr.register(JAXBUtils.class);

    public static final String RI_JAXB_CONTEXT_FACTORY = "com.sun.xml.bind.v2.ContextFactory";

    public static final String IBM_JAXB_CONTEXT_FACTORY = "com.ibm.xml.xlxp2.jaxb.JAXBContextFactory";

    private static final ThreadContextAccessor THREAD_CONTEXT_ACCESSOR =
                    AccessController.doPrivileged(ThreadContextAccessor.getPrivilegedAction());

    public static JAXBContext newInstance(Class<?>... classesToBeBound) throws JAXBException {
        ClassLoader originalContextClassLoader = THREAD_CONTEXT_ACCESSOR.getContextClassLoader(Thread.currentThread());
        try {
            THREAD_CONTEXT_ACCESSOR.setContextClassLoader(Thread.currentThread(), getJAXBContextProviderClassLoader());
            return JAXBContext.newInstance(classesToBeBound);
        } finally {
            THREAD_CONTEXT_ACCESSOR.setContextClassLoader(Thread.currentThread(), originalContextClassLoader);
        }
    }

    @FFDCIgnore(ClassNotFoundException.class)
    public static ClassLoader getJAXBContextProviderClassLoader() {
        Class<?> contextFactoryClass = null;
        try {
            contextFactoryClass = Class.forName(IBM_JAXB_CONTEXT_FACTORY);
        } catch (ClassNotFoundException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Unable to load IBM JAXB Context Factory " + IBM_JAXB_CONTEXT_FACTORY);
            }
            try {
                contextFactoryClass = Class.forName(RI_JAXB_CONTEXT_FACTORY);
            } catch (ClassNotFoundException e1) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Unable to load RI JAXB Context Factory " + RI_JAXB_CONTEXT_FACTORY);
                }
            }
        }
        return contextFactoryClass == null ? JAXBUtils.class.getClassLoader() : contextFactoryClass.getClassLoader();
    }
}
