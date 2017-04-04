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
package com.ibm.ws.beanvalidation;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;
import javax.naming.spi.ObjectFactory;
import javax.validation.ValidatorFactory;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.injectionengine.ObjectFactoryInfo;

/**
 * Provides the data to register an ObjectFactory override for
 * the ValidatorFactory data type. <p>
 * 
 * This supports injection and lookup of an instance of ValidatorFactory
 * using the Resource annotation or resource-env xml stanza. <p>
 */
@Component(service = ObjectFactoryInfo.class)
@Trivial
public class ValidatorFactoryObjectFactoryInfo extends ObjectFactoryInfo {

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return Resource.class;
    }

    @Override
    public Class<?> getType() {
        return ValidatorFactory.class;
    }

    @Override
    public boolean isOverrideAllowed() {
        return false;
    }

    @Override
    public Class<? extends ObjectFactory> getObjectFactoryClass() {
        return ValidatorFactoryObjectFactory.class;
    }

}
