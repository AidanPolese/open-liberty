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

import javax.resource.ConnectionFactoryDefinition;
import javax.resource.ConnectionFactoryDefinitions;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.javaee.dd.common.ConnectionFactory;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

/**
 *
 */
@Component(service = { InjectionProcessorProvider.class })
public class ConnectionFactoryDefinitionProcessorProvider
                extends InjectionProcessorProvider<ConnectionFactoryDefinition, ConnectionFactoryDefinitions> {
    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES =
                    Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(ConnectionFactory.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationClass()
     */
    @Override
    public Class<ConnectionFactoryDefinition> getAnnotationClass() {

        return ConnectionFactoryDefinition.class;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationsClass()
     */
    @Override
    public Class<ConnectionFactoryDefinitions> getAnnotationsClass() {

        return ConnectionFactoryDefinitions.class;
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
    public InjectionProcessor<ConnectionFactoryDefinition, ConnectionFactoryDefinitions> createInjectionProcessor() {

        return new ConnectionFactoryDefinitionProcessor();
    }

}
