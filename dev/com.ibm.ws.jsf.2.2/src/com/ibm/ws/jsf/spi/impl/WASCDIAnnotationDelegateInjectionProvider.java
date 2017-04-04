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
package com.ibm.ws.jsf.spi.impl;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContext;

import org.apache.myfaces.shared.util.ClassUtils;
import org.apache.myfaces.spi.InjectionProviderException;

import com.ibm.ws.jsf.spi.WASInjectionProvider;

/**
 * Delegation pattern to avoid direct instantiation
 */
public class WASCDIAnnotationDelegateInjectionProvider extends WASInjectionProvider
{
    private WASInjectionProvider delegate;
    private final boolean available;

    public WASCDIAnnotationDelegateInjectionProvider(ExternalContext externalContext)
    {
        try
        {
            Class clazz = ClassUtils.simpleClassForName(
                            "com.ibm.ws.jsf.cdi.WASCDIAnnotationInjectionProvider");
            delegate = (WASInjectionProvider) clazz.getConstructor(ExternalContext.class).newInstance(externalContext);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exc) {
            //Ignore
        }
        available = ((delegate != null) && delegate.isAvailable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object inject(Class Klass) throws InjectionProviderException {

        if (available) {
            return delegate.inject(Klass);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object inject(Class Klass, boolean doPostConstruct) throws InjectionProviderException {

        if (available) {
            return delegate.inject(Klass, doPostConstruct);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object inject(Class Klass, boolean doPostConstruct, ExternalContext eContext) throws InjectionProviderException
    {
        if (available)
        {
            return delegate.inject(Klass, doPostConstruct, eContext);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object inject(Object instance) throws InjectionProviderException
    {
        if (available)
        {
            return delegate.inject(instance);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object inject(Object instance, boolean doPostConstruct) throws InjectionProviderException
    {
        if (available)
        {
            return delegate.inject(instance, doPostConstruct);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object inject(Object instance, boolean doPostConstruct, ExternalContext eContext) throws InjectionProviderException
    {
        if (available)
        {
            return delegate.inject(instance, doPostConstruct, eContext);
        }
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postConstruct(Object instance, Object creationMetaData) throws InjectionProviderException
    {
        if (available)
        {
            delegate.postConstruct(instance, creationMetaData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preDestroy(Object instance, Object creationMetaData) throws InjectionProviderException
    {
        if (available)
        {
            delegate.preDestroy(instance, creationMetaData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable()
    {
        return available;
    }
}
