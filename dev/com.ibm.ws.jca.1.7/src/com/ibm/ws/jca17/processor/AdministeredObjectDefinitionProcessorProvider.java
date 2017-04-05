/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca17.processor;

import java.util.Collections;
import java.util.List;

import javax.resource.AdministeredObjectDefinition;
import javax.resource.AdministeredObjectDefinitions;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.javaee.dd.common.AdministeredObject;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

/**
 *
 */
@Component(service = { InjectionProcessorProvider.class })
public class AdministeredObjectDefinitionProcessorProvider
                extends InjectionProcessorProvider<AdministeredObjectDefinition, AdministeredObjectDefinitions> {

    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES = Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(AdministeredObject.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationClass()
     */
    @Override
    public Class<AdministeredObjectDefinition> getAnnotationClass() {

        return AdministeredObjectDefinition.class;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationsClass()
     */
    @Override
    public Class<AdministeredObjectDefinitions> getAnnotationsClass() {

        return AdministeredObjectDefinitions.class;
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
    public InjectionProcessor<AdministeredObjectDefinition, AdministeredObjectDefinitions> createInjectionProcessor() {

        return new AdministeredObjectDefinitionProcessor();
    }

}
