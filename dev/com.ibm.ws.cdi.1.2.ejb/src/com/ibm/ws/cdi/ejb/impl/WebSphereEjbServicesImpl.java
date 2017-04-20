/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.ejb.impl;

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.transaction.Transactional;

import org.jboss.weld.ejb.InternalEjbDescriptor;
import org.jboss.weld.ejb.api.SessionObjectReference;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.ejb.spi.InterceptorBindings;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.interfaces.WebSphereEjbServices;
import com.ibm.ws.ejbcontainer.EJBReference;
import com.ibm.ws.ejbcontainer.EJBReferenceFactory;

public class WebSphereEjbServicesImpl implements WebSphereEjbServices
{

    private static final TraceComponent tc = Tr.register(WebSphereEjbServicesImpl.class);

    private final EJBInterceptorRegistry interceptorRegistry = new EJBInterceptorRegistry();

    // EJBs are not allowed to be annotated with @Transactional
    // Throw an exception if an interceptor for Transactional is registered
    @Override
    public void registerInterceptors(EjbDescriptor<?> ejbDescriptor, InterceptorBindings interceptorBindings) {
        if (interceptorBindings != null) {
            final Collection<Interceptor<?>> interceptors = interceptorBindings.getAllInterceptors();

            if (interceptors != null) {
                for (Interceptor<?> interceptor : interceptors) {
                    final Set<Annotation> annotations = interceptor.getInterceptorBindings();

                    if (annotations != null) {
                        for (Annotation annotation : annotations) {
                            if (Transactional.class.equals(annotation.annotationType())) {
                                // An NPE if ejbDescriptor is null will work just fine too
                                throw new IllegalStateException(Tr.formatMessage(tc, "transactional.annotation.on.ejb.CWOWB2000E", annotation.toString(),
                                                                                 ejbDescriptor.getEjbName()));
                            }
                        }
                    }
                }

                EjbDescriptor<?> descriptor = ejbDescriptor;
                if (ejbDescriptor instanceof InternalEjbDescriptor) {
                    InternalEjbDescriptor<?> internal = (InternalEjbDescriptor<?>) ejbDescriptor;
                    descriptor = internal.delegate();
                }
                WebSphereEjbDescriptor<?> webSphereEjbDescriptor = (WebSphereEjbDescriptor<?>) descriptor;
                J2EEName ejbJ2EEName = webSphereEjbDescriptor.getEjbJ2EEName();

                interceptorRegistry.registerInterceptors(ejbJ2EEName, interceptorBindings);
            }
        }
    }

    @Override
    public SessionObjectReference resolveEjb(EjbDescriptor<?> ejbDescriptor)
    {
        EjbDescriptor<?> descriptor = ejbDescriptor;
        if (ejbDescriptor instanceof InternalEjbDescriptor) {
            InternalEjbDescriptor<?> internal = (InternalEjbDescriptor<?>) ejbDescriptor;
            descriptor = internal.delegate();
        }
        WebSphereEjbDescriptor<?> webSphereEjbDescriptor = (WebSphereEjbDescriptor<?>) descriptor;

        EJBReferenceFactory factory = webSphereEjbDescriptor.getReferenceFactory();
        EJBReference reference = factory.create(null);
        return new SessionObjectReferenceImpl(reference);
    }

    /** {@inheritDoc} */
    @Override
    public List<Interceptor<?>> getInterceptors(J2EEName ejbJ2EEName, Method method, InterceptionType interceptionType) {
        return interceptorRegistry.getInterceptors(ejbJ2EEName, method, interceptionType);
    }

    @Override
    public void cleanup()
    {
        interceptorRegistry.clearRegistry();
    }

}
