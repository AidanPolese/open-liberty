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
package com.ibm.ws.jca.processor.jms.connectionfactory;

import java.util.Collections;
import java.util.List;

import javax.jms.JMSConnectionFactoryDefinition;
import javax.jms.JMSConnectionFactoryDefinitions;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.javaee.dd.common.JMSConnectionFactory;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

/**
 * A provider class for injection processors of JMS Connection Factory.
 */
@Component(service = { InjectionProcessorProvider.class })
public class JMSConnectionFactoryDefinitionProcessorProvider extends InjectionProcessorProvider<JMSConnectionFactoryDefinition, JMSConnectionFactoryDefinitions> {
    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES = Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(JMSConnectionFactory.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationClass()
     */
    @Override
    public Class<JMSConnectionFactoryDefinition> getAnnotationClass() {

        return JMSConnectionFactoryDefinition.class;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessorProvider#getAnnotationsClass()
     */
    @Override
    public Class<JMSConnectionFactoryDefinitions> getAnnotationsClass() {

        return JMSConnectionFactoryDefinitions.class;
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
    public InjectionProcessor<JMSConnectionFactoryDefinition, JMSConnectionFactoryDefinitions> createInjectionProcessor() {

        return new JMSConnectionFactoryDefinitionProcessor();
    }

}
