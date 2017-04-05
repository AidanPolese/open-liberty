/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import java.lang.annotation.Annotation;

import javax.naming.spi.ObjectFactory;

/**
 * Represents the data for a registered ObjectFactory override.
 *
 * @see InjectionEngine#registerObjectFactoryInfo(ObjectFactoryInfo)
 */
public abstract class ObjectFactoryInfo
{
    /**
     * Returns the annotation class to which this object applies. For example,
     * javax.annotation.Resource.
     */
    public abstract Class<? extends Annotation> getAnnotationClass();

    /**
     * Returns the injection type to which this object applies. For example,
     * javax.transaction.UserTransaction if this ObjectFactory provides support
     * for injecting UserTransaction.
     */
    public abstract Class<?> getType();

    /**
     * Returns true if bindings can be used to override the object factory.
     */
    public abstract boolean isOverrideAllowed();

    /**
     * Returns the ObjectFactory class to use.
     */
    public abstract Class<? extends ObjectFactory> getObjectFactoryClass();

    /**
     * Returns true if the annotation attribute name should be allowed.
     *
     * @param name the attribute name
     */
    public boolean isAttributeAllowed(String name)
    {
        return true;
    }

    /**
     * Returns true if the ObjectFactory needs a processor-specified RefAddr.
     */
    public boolean isRefAddrNeeded()
    {
        return true;
    }
}
