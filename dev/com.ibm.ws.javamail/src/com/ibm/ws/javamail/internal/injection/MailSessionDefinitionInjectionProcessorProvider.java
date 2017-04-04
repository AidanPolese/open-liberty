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
package com.ibm.ws.javamail.internal.injection;

import java.util.Collections;
import java.util.List;

import javax.mail.MailSessionDefinition;
import javax.mail.MailSessionDefinitions;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.ws.javaee.dd.common.MailSession;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

/**
 *
 */
@Component(service = { InjectionProcessorProvider.class })
public class MailSessionDefinitionInjectionProcessorProvider
                extends InjectionProcessorProvider<MailSessionDefinition, MailSessionDefinitions> {
    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES =
                    Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(MailSession.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationClass()
     */
    @Override
    public Class<MailSessionDefinition> getAnnotationClass() {

        return MailSessionDefinition.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationsClass()
     */
    @Override
    public Class<MailSessionDefinitions> getAnnotationsClass() {

        return MailSessionDefinitions.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getJNDIEnvironmentRefClasses()
     */
    @Override
    public List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses() {

        return REF_CLASSES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#createInjectionProcessor()
     */
    @Override
    public InjectionProcessor<MailSessionDefinition, MailSessionDefinitions> createInjectionProcessor() {

        return new MailSessionDefinitionInjectionProcessor();
    }
}
