/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.processor.jms.destination;

import java.util.Collections;
import java.util.List;

import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.javaee.dd.common.JMSDestination;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

/**
 *
 */
@Component(service = { InjectionProcessorProvider.class })
public class JMSDestinationDefinitionProcessorProvider extends InjectionProcessorProvider<JMSDestinationDefinition, JMSDestinationDefinitions> {

    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES = Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(JMSDestination.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationClass()
     */
    @Override
    public Class<JMSDestinationDefinition> getAnnotationClass() {

        return JMSDestinationDefinition.class;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationsClass()
     */
    @Override
    public Class<JMSDestinationDefinitions> getAnnotationsClass() {

        return JMSDestinationDefinitions.class;
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
    public InjectionProcessor<JMSDestinationDefinition, JMSDestinationDefinitions> createInjectionProcessor() {

        return new JMSDestinationDefinitionProcessor();
    }

}
