/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.mp.jwt.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.inject.Provider;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.extension.WebSphereCDIExtension;

/**
 *
 */
@Component(service = WebSphereCDIExtension.class, property = { "api.classes=org.eclipse.microprofile.jwt.Claim;org.eclipse.microprofile.jwt.Claims;org.eclipse.microprofile.jwt.ClaimValue;org.eclipse.microprofile.jwt.JsonWebToken" }, immediate = true)
public class JwtCDIExtension implements Extension, WebSphereCDIExtension {

    private static final TraceComponent tc = Tr.register(JwtCDIExtension.class);

    private final Map<Claim, Set<Type>> injectionTypes = new HashMap<Claim, Set<Type>>();
    private boolean addJsonWebTokenBean = false;

    public void processInjectionTarget(@Observes ProcessInjectionTarget<?> pit) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "processInjectionTarget", pit);
        }

        Class<?> targetClass = pit.getAnnotatedType().getJavaClass();
        ClassLoader classLoader = targetClass.getClassLoader();

        for (InjectionPoint injectionPoint : pit.getInjectionTarget().getInjectionPoints()) {
            Claim claim = getClaimAnnotation(injectionPoint);
            if (claim != null) {
                Type type = injectionPoint.getType();
                Throwable configException = null;
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    configException = processParameterizedType(injectionPoint, pType, classLoader, claim);
                } else {
                    configException = validateInjectionPoint(injectionPoint, type, type, classLoader, false, claim);
                }
                if (configException != null) {
                    Tr.error(tc, "MPJWT_CDI_CANNOT_RESOLVE_INJECTION_POINT", injectionPoint, configException);
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "processInjectionTarget");
        }
    }

    private Claim getClaimAnnotation(InjectionPoint injectionPoint) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getClaimAnnotation", injectionPoint);
        }
        Claim claim = null;

        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        if (qualifiers != null) {
            //find the qualifier
            for (Annotation qualifier : qualifiers) {
                if (qualifier.annotationType().equals(Claim.class)) {
                    claim = (Claim) qualifier;
                    break;
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getClaimAnnotation", claim);
        }
        return claim;
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "afterBeanDiscovery", abd, beanManager);
        }

        for (Entry<Claim, Set<Type>> entrySet : injectionTypes.entrySet()) {
            Claim claim = entrySet.getKey();
            for (Type type : entrySet.getValue()) {
                try {
                    if (type instanceof TypeVariable) {
                        TypeVariable<?> typeVar = (TypeVariable<?>) type;
                        Type[] bounds = typeVar.getBounds();
                        for (Type bound : bounds) {
                            addClaimBean(abd, beanManager, bound, claim);
                        }
                    } else {
                        addClaimBean(abd, beanManager, type, claim);
                    }
                } catch (ClaimTypeException e) {
                    abd.addDefinitionError(e);
                }
            }
        }

        if (addJsonWebTokenBean || injectionTypes.isEmpty() == false) {
            abd.addBean(new JsonWebTokenBean(beanManager));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "afterBeanDiscovery");
        }
    }

    private Throwable processParameterizedType(InjectionPoint injectionPoint, ParameterizedType injectionType, ClassLoader classLoader, Claim claim) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "processParameterizedType", injectionPoint, injectionType, classLoader, claim);
        }

        Throwable configException = null;
        Type rawType = injectionType.getRawType();
        Type returnType = injectionType.getActualTypeArguments()[0];

        if (Provider.class.isAssignableFrom((Class<?>) rawType)) {
            configException = validateInjectionPoint(injectionPoint, returnType, returnType, classLoader, false, claim);
        } else if (Optional.class.isAssignableFrom((Class<?>) rawType)) {
            Type[] aTypes = injectionType.getActualTypeArguments();
            returnType = aTypes[0];
            configException = validateInjectionPoint(injectionPoint, returnType, injectionType, classLoader, true, claim);
        } else {
            if (returnType instanceof ParameterizedType) {
                returnType = ((ParameterizedType) returnType).getRawType();
            }
            configException = validateInjectionPoint(injectionPoint, returnType, injectionType, classLoader, false, claim);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "processParameterizedType", configException);
        }
        return configException;
    }

    private Throwable validateInjectionPoint(InjectionPoint injectionPoint, Type conversionType, Type injectionType, ClassLoader classLoader, boolean optional, Claim claim) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "validateInjectionPoint", conversionType, injectionType, classLoader, optional, claim);
        }

        Throwable configException = null;
        Type rawInjectionType = injectionType;

        if (injectionType instanceof ParameterizedType) {
            rawInjectionType = ((ParameterizedType) injectionType).getRawType();
        }

        // TODO: Validate type
//        if (ClaimValue.class.isAssignableFrom((Class<?>) rawInjectionType) || Provider.class.isAssignableFrom((Class<?>) rawInjectionType)) {
        try {
            Set<Type> injectionTypesForQualifier = injectionTypes.get(claim);
            if (injectionTypesForQualifier == null) {
                injectionTypesForQualifier = new HashSet<Type>();
                injectionTypes.put(claim, injectionTypesForQualifier);
            }
            injectionTypesForQualifier.add(injectionType);
        } catch (Throwable e) {
            configException = e;
        }
//        } else {
//            configException = new ClaimTypeException(Tr.formatMessage(tc, "MPJWT_CDI_INVALID_INJECTION_TYPE", injectionType));
//        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "validateInjectionPoint", configException);
        }
        return configException;
    }

    private void addClaimBean(AfterBeanDiscovery abd, BeanManager beanManager, Type type, Claim claim) throws ClaimTypeException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "addClaimBean", abd, beanManager, type, claim);
        }

        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (!clazz.isPrimitive()) {
                addClaimBean(abd, beanManager, type, clazz, claim);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            addClaimBean(abd, beanManager, pType, claim);
        } else {
            throw new ClaimTypeException(Tr.formatMessage(tc, "MPJWT_CDI_INVALID_INJECTION_TYPE", type));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "addClaimBean");
        }
    }

    private void addClaimBean(AfterBeanDiscovery abd, BeanManager beanManager, ParameterizedType type, Claim claim) throws ClaimTypeException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "addClaimBean", abd, beanManager, type, claim);
        }

        Type rawInjectionType = type.getRawType();
        if (rawInjectionType instanceof Class) {
            Class<?> clazz = (Class<?>) rawInjectionType;
            addClaimBean(abd, beanManager, type, clazz, claim);
        } else {
            throw new ClaimTypeException(Tr.formatMessage(tc, "MPJWT_CDI_INVALID_INJECTION_TYPE", type));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "addClaimBean");
        }
    }

    private <T> void addClaimBean(AfterBeanDiscovery abd, BeanManager beanManager, Type beanType, Class<T> clazz, Claim claim) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "addClaimBean", abd, beanManager, beanType, clazz, claim);
        }

        ClaimBean<T> converterBean = new ClaimBean<T>(beanManager, beanType, clazz, claim);
        abd.addBean(converterBean);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "addClaimBean");
        }
    }

    public void processInjectionPoint(@Observes ProcessInjectionPoint<?, JsonWebToken> pip, BeanManager beanManager) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "processInjectionPoint", pip, beanManager);
        }

        addJsonWebTokenBean = true;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "processInjectionPoint");
        }
    }
}
