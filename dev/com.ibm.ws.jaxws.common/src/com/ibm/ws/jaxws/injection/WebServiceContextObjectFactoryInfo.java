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
package com.ibm.ws.jaxws.injection;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;
import javax.naming.spi.ObjectFactory;
import javax.xml.ws.WebServiceContext;

import com.ibm.wsspi.injectionengine.ObjectFactoryInfo;

/**
 *
 */
public class WebServiceContextObjectFactoryInfo extends ObjectFactoryInfo {

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return Resource.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends ObjectFactory> getObjectFactoryClass() {
        return WebServiceContextObjectFactory.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getType() {
        return WebServiceContext.class;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOverrideAllowed() {
        return false;
    }

}
