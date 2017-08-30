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
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.JsonWebToken;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
public class ClaimBean<T> implements Bean<T>, PassivationCapable {

    private static final TraceComponent tc = Tr.register(ClaimBean.class);

    private final Class<T> beanClass;
    private Type beanType;
    private Claim claim;
    private Set<Type> types;
    private Set<Annotation> qualifiers;
    private String name;
    private String id;
    private Class<? extends Annotation> scope;

    public ClaimBean(BeanManager beanManager, Class<T> beanClass, Claim claim) {
        this(beanManager, beanClass, beanClass, claim);
    }

    public ClaimBean(BeanManager beanManager, Type beanType, Class<T> beanClass, Claim claim) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "<init>", beanManager, beanType, beanClass, claim);
        }
        this.beanType = beanType;
        this.beanClass = beanClass;
        this.claim = claim;
        this.types = new HashSet<Type>();
        types.add(beanType);
        this.qualifiers = new HashSet<Annotation>();
        this.qualifiers.add(claim);
        this.name = this.getClass().getName() + "[" + claim + "," + beanType + "]";
        this.id = beanManager.hashCode() + "#" + this.name;
        setScope(beanType);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "<init>", this);
        }
    }

    /**
     * @param beanType
     */
    private void setScope(Type beanType) {
        Class<?> beanTypeClass = null;
        if (beanType instanceof ParameterizedType) {
            beanTypeClass = (Class<?>) ((ParameterizedType) beanType).getRawType();
        } else if (beanType instanceof Class) {
            beanTypeClass = (Class<?>) beanType;
        }
        if (ClaimValue.class.isAssignableFrom(beanTypeClass)) {
            scope = RequestScoped.class;
        } else {
            scope = Dependent.class;
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public T create(CreationalContext<T> creationalContext) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "create", creationalContext);
        }

        T instance = null;
        CDI<Object> cdi = CDI.current();
        BeanManager beanManager = cdi.getBeanManager();

        if (beanType instanceof ParameterizedType) {
            instance = createClaimValueForParameterizedType(creationalContext, beanManager);
        } else if (beanType instanceof Class) {
            instance = createClaimValueForClassType();
        } else {
            throw new IllegalArgumentException(Tr.formatMessage(tc, "MPJWT_CDI_INVALID_INJECTION_TYPE", beanType));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "create", instance);
        }
        return instance;
    }

    /**
     * @param creationalContext
     * @param beanManager
     * @return
     */
    private T createClaimValueForParameterizedType(CreationalContext<T> creationalContext, BeanManager beanManager) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "createClaimValueForParameterizedType", creationalContext, beanManager);
        }

        T instance = null;
        ParameterizedType parameterizedType = (ParameterizedType) beanType;
        Type actualReturnType = parameterizedType.getActualTypeArguments()[0];
        if (actualReturnType instanceof ParameterizedType) {
            actualReturnType = ((ParameterizedType) actualReturnType).getRawType();
        }

        boolean isOptional = Optional.class.isAssignableFrom((Class<?>) actualReturnType);
        Class<?> returnClass = (Class<?>) actualReturnType;
        instance = getClaimValue(returnClass, isOptional);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "createClaimValueForParameterizedType", instance);
        }
        return instance;
    }

    /**
     * @param <U>
     * @param injectionPoint
     * @param returnClass
     * @param aType
     * @return
     */
    private <U> T getClaimValue(Class<U> returnClass, boolean isOptional) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getClaimValue", returnClass, isOptional);
        }

        T instance = null;
        final String claimName = getClaimName();
        instance = (T) new ClaimValue() {

            @Override
            public String getName() {
                return claimName;
            }

            @Override
            public U getValue() {
                U value = null;
                JsonWebToken jsonWebToken = null;
                Instance<JsonWebToken> jsonWebTokenInstance = CDI.current().select(JsonWebToken.class);

                if (jsonWebTokenInstance != null && jsonWebTokenInstance.isAmbiguous() == false && jsonWebTokenInstance.isUnsatisfied() == false) {
                    jsonWebToken = jsonWebTokenInstance.get();

                    if (isOptional) {
                        value = (U) jsonWebToken.claim(claimName);
                    } else {
                        value = (U) jsonWebToken.getClaim(claimName);
                    }
                }

                return value;
            }
        };

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getClaimValue", instance);
        }
        return instance;
    }

    /**
     * @param instance
     * @return
     */
    private T createClaimValueForClassType() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "createClaimValueForClassType");
        }

        T instance = null;
        Class<T> ipClass = (Class<T>) beanType;
        String claimName = getClaimName();
        if (ClaimValue.class.equals(ipClass)) {
            instance = (T) new ClaimValue() {

                @Override
                public String getName() {
                    return claimName;
                }

                @Override
                public Object getValue() {
                    Object value = null;
                    JsonWebToken jsonWebToken = null;
                    Instance<JsonWebToken> jsonWebTokenInstance = CDI.current().select(JsonWebToken.class);

                    if (jsonWebTokenInstance != null && jsonWebTokenInstance.isAmbiguous() == false && jsonWebTokenInstance.isUnsatisfied() == false) {
                        jsonWebToken = jsonWebTokenInstance.get();
                        value = jsonWebToken.getClaim(claimName);
                    }

                    return value;
                }
            };
        } else {
            // Provider path
            JsonWebToken jsonWebToken = null;
            Instance<JsonWebToken> jsonWebTokenInstance = CDI.current().select(JsonWebToken.class);

            if (jsonWebTokenInstance != null && jsonWebTokenInstance.isAmbiguous() == false && jsonWebTokenInstance.isUnsatisfied() == false) {
                jsonWebToken = jsonWebTokenInstance.get();
                instance = jsonWebToken.getClaim(claimName);
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "createClaimValueForClassType", instance);
        }
        return instance;
    }

    private String getClaimName() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getClaimName");
        }

        String claimName = claim.value();
        if (claimName == null || claimName.trim().isEmpty()) {
            claimName = claim.standard().name();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getClaimName", claimName);
        }
        return claimName;
    }

    /** {@inheritDoc} */
    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "destroy", instance, creationalContext);
        }

        creationalContext.release();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "destroy");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getBeanClass() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getBeanClass");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getBeanClass", beanClass);
        }
        return beanClass;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNullable() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "isNullable");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "isNullable", true);
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> getScope() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getScope");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getScope", scope);
        }
        return scope; //RequestScoped.class; TODO:Switch to dependent when type is for Provider<T>
    }

    /** {@inheritDoc} */
    @Override
    public Set<Type> getTypes() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getTypes");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getTypes", types);
        }
        return types;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Annotation> getQualifiers() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getQualifiers");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getQualifiers", qualifiers);
        }
        return qualifiers;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getName");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getName", name);
        }
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getStereotypes");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getStereotypes", Collections.emptySet());
        }
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAlternative() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "isAlternative");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "isAlternative", false);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getInjectionPoints");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getInjectionPoints", Collections.emptySet());
        }
        return Collections.emptySet();
    }

    @Override
    public String getId() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getId");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getId", id);
        }
        return id;
    }

    @Override
    public String toString() {
        return "Bean for " + this.types + " with Qualifiers " + this.qualifiers;
    }

}
