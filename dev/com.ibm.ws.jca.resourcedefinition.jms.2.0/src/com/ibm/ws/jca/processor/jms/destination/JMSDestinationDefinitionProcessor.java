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

import java.lang.reflect.Member;
import java.util.List;

import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;

import com.ibm.ws.javaee.dd.common.JMSDestination;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionProcessor;

/**
 * This class provides processing to handle @JMSDestinationDefinition annotations defined in the target class
 */
public class JMSDestinationDefinitionProcessor extends InjectionProcessor<JMSDestinationDefinition, JMSDestinationDefinitions>
{

    public JMSDestinationDefinitionProcessor()
    {
        super(JMSDestinationDefinition.class, JMSDestinationDefinitions.class);
    }

    /**
     * @param annotationClass
     * @param annotationsClass
     */
    public JMSDestinationDefinitionProcessor(Class<JMSDestinationDefinition> annotationClass, Class<JMSDestinationDefinitions> annotationsClass) {

        super(JMSDestinationDefinition.class, JMSDestinationDefinitions.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#processXML()
     */
    @Override
    public void processXML() throws InjectionException {

        List<? extends JMSDestination> jmsDestinationDefinitions = ivNameSpaceConfig.getJNDIEnvironmentRefs(JMSDestination.class);

        if (jmsDestinationDefinitions != null) {
            for (JMSDestination jmsDestination : jmsDestinationDefinitions) {
                String jndiName = jmsDestination.getName();
                InjectionBinding<JMSDestinationDefinition> injectionBinding = ivAllAnnotationsCollection.get(jndiName);
                JMSDestinationDefinitionInjectionBinding binding;

                if (injectionBinding != null) {
                    binding = (JMSDestinationDefinitionInjectionBinding) injectionBinding;
                } else {
                    binding = new JMSDestinationDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
                    addInjectionBinding(binding);
                }

                binding.mergeXML(jmsDestination);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#createInjectionBinding(java.lang.annotation.Annotation, java.lang.Class, java.lang.reflect.Member, java.lang.String)
     */
    @Override
    public InjectionBinding<JMSDestinationDefinition> createInjectionBinding(JMSDestinationDefinition annotation, Class<?> instanceClass, Member member, String jndiName) throws InjectionException {
        InjectionBinding<JMSDestinationDefinition> injectionBinding =
                        new JMSDestinationDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
        injectionBinding.merge(annotation, instanceClass, null);
        return injectionBinding;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#resolve(com.ibm.wsspi.injectionengine.InjectionBinding)
     */
    @Override
    public void resolve(InjectionBinding<JMSDestinationDefinition> injectionBinding) throws InjectionException {

        ((JMSDestinationDefinitionInjectionBinding) injectionBinding).resolve();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#getJndiName(java.lang.annotation.Annotation)
     */
    @Override
    public String getJndiName(JMSDestinationDefinition annotation) {

        return annotation.name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#getAnnotations(java.lang.annotation.Annotation)
     */
    @Override
    public JMSDestinationDefinition[] getAnnotations(JMSDestinationDefinitions pluralAnnotation) {
        return pluralAnnotation.value();
    }
}
