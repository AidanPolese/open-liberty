/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.naming.spi.ObjectFactory;

import com.ibm.wsspi.injectionengine.ObjectFactoryInfo;

public class ObjectFactoryInfoImpl
                extends ObjectFactoryInfo
{
    /**
     * The annotation class.
     */
    private final Class<? extends Annotation> ivAnnotationClass;

    /**
     * The injection type handled by this object factory.
     */
    private final Class<?> ivType;

    /**
     * True if bindings can override this ObjectFactory.
     */
    private final boolean ivAllowOverride;

    /**
     * The ObjectFactory class registered with the injection engine.
     */
    private final Class<? extends ObjectFactory> ivObjectFactoryClass;

    /**
     * The set of allowed annotation attributes, or {@code null} to allow all
     * attributes.
     */
    private final Set<String> ivAllowedAttributes;

    /**
     * True if the ObjectFactory needs processor-specific RefAddr.
     */
    private final boolean ivRefAddrNeeded; // F48603

    ObjectFactoryInfoImpl(Class<? extends Annotation> annotationClass,
                          Class<?> type,
                          Class<? extends ObjectFactory> objectFactory,
                          boolean allowOverride,
                          Set<String> allowedAttributes,
                          boolean refAddrNeeded)
    {
        ivAnnotationClass = annotationClass;
        ivType = type;
        ivAllowOverride = allowOverride;
        ivObjectFactoryClass = objectFactory;
        ivAllowedAttributes = allowedAttributes;
        ivRefAddrNeeded = refAddrNeeded; // F48603
    }

    @Override
    public String toString()
    {
        return super.toString() + '[' + ivObjectFactoryClass + ", " + ivAllowedAttributes + ']';
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass()
    {
        return ivAnnotationClass;
    }

    @Override
    public Class<?> getType()
    {
        return ivType;
    }

    @Override
    public boolean isOverrideAllowed()
    {
        return ivAllowOverride;
    }

    @Override
    public Class<? extends ObjectFactory> getObjectFactoryClass()
    {
        return ivObjectFactoryClass;
    }

    @Override
    public boolean isAttributeAllowed(String name)
    {
        return ivAllowedAttributes == null || ivAllowedAttributes.contains(name);
    }

    @Override
    public boolean isRefAddrNeeded() // F48603
    {
        return ivRefAddrNeeded;
    }
}
