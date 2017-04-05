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
package com.ibm.ws.transaction.services;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;
import javax.naming.spi.ObjectFactory;
import javax.transaction.TransactionSynchronizationRegistry;

import org.osgi.service.component.annotations.Component;

import com.ibm.wsspi.injectionengine.ObjectFactoryInfo;

/**
 *
 */
@Component(service = { ObjectFactoryInfo.class })
public class TransactionSynchronizationRegistryObjectFactoryInfo extends ObjectFactoryInfo {

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return Resource.class;
    }

    @Override
    public Class<?> getType() {
        return TransactionSynchronizationRegistry.class;
    }

    @Override
    public boolean isOverrideAllowed() {
        return false;
    }

    @Override
    public Class<? extends ObjectFactory> getObjectFactoryClass() {
        return TransactionSynchronizationRegistryObjectFactory.class;
    }
}