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

import java.lang.reflect.Member;
import java.util.List;

import javax.jms.JMSConnectionFactoryDefinition;
import javax.jms.JMSConnectionFactoryDefinitions;

import com.ibm.ws.javaee.dd.common.JMSConnectionFactory;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionProcessor;

/**
 * This class provides processing to handle @JMSConnectionFactoryDefinition annotations defined in the target class
 */
public class JMSConnectionFactoryDefinitionProcessor extends InjectionProcessor<JMSConnectionFactoryDefinition, JMSConnectionFactoryDefinitions> {

    public JMSConnectionFactoryDefinitionProcessor() {
        super(JMSConnectionFactoryDefinition.class, JMSConnectionFactoryDefinitions.class);
    }

    /**
     * @param annotationClass
     * @param annotationsClass
     */
    public JMSConnectionFactoryDefinitionProcessor(Class<JMSConnectionFactoryDefinition> annotationClass, Class<JMSConnectionFactoryDefinitions> annotationsClass) {

        super(JMSConnectionFactoryDefinition.class, JMSConnectionFactoryDefinitions.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#processXML()
     */
    @Override
    public void processXML() throws InjectionException {

        List<? extends JMSConnectionFactory> jmsConnectionFactoryDefinitions = ivNameSpaceConfig.getJNDIEnvironmentRefs(JMSConnectionFactory.class);

        if (jmsConnectionFactoryDefinitions != null) {
            for (JMSConnectionFactory jmsConnectionFactory : jmsConnectionFactoryDefinitions) {
                String jndiName = jmsConnectionFactory.getName();
                InjectionBinding<JMSConnectionFactoryDefinition> injectionBinding = ivAllAnnotationsCollection.get(jndiName);
                JMSConnectionFactoryDefinitionInjectionBinding binding;

                if (injectionBinding != null) {
                    binding = (JMSConnectionFactoryDefinitionInjectionBinding) injectionBinding;
                } else {
                    binding = new JMSConnectionFactoryDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
                    addInjectionBinding(binding);
                }

                binding.mergeXML(jmsConnectionFactory);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#createInjectionBinding(java.lang.annotation.Annotation, java.lang.Class, java.lang.reflect.Member, java.lang.String)
     */
    @Override
    public InjectionBinding<JMSConnectionFactoryDefinition> createInjectionBinding(JMSConnectionFactoryDefinition annotation, Class<?> instanceClass, Member member, String jndiName) throws InjectionException {
        InjectionBinding<JMSConnectionFactoryDefinition> injectionBinding =
                        new JMSConnectionFactoryDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
        injectionBinding.merge(annotation, instanceClass, null);
        return injectionBinding;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#resolve(com.ibm.wsspi.injectionengine.InjectionBinding)
     */
    @Override
    public void resolve(InjectionBinding<JMSConnectionFactoryDefinition> injectionBinding) throws InjectionException {

        ((JMSConnectionFactoryDefinitionInjectionBinding) injectionBinding).resolve();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#getJndiName(java.lang.annotation.Annotation)
     */
    @Override
    public String getJndiName(JMSConnectionFactoryDefinition annotation) {

        return annotation.name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#getAnnotations(java.lang.annotation.Annotation)
     */
    @Override
    public JMSConnectionFactoryDefinition[] getAnnotations(JMSConnectionFactoryDefinitions pluralAnnotation) {
        return pluralAnnotation.value();
    }

}
