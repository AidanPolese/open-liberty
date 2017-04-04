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

import java.lang.reflect.Member;
import java.util.List;

import javax.resource.ConnectionFactoryDefinition;
import javax.resource.ConnectionFactoryDefinitions;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ws.javaee.dd.common.ConnectionFactory;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionConfigConstants;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionProcessor;

/**
 *
 */
public class ConnectionFactoryDefinitionProcessor
                extends InjectionProcessor<ConnectionFactoryDefinition, ConnectionFactoryDefinitions>
{
    private final static TraceComponent tc = Tr.register(ConnectionFactoryDefinitionProcessor.class,
                                                         InjectionConfigConstants.traceString,
                                                         InjectionConfigConstants.messageFile);

    public ConnectionFactoryDefinitionProcessor()
    {
        super(ConnectionFactoryDefinition.class, ConnectionFactoryDefinitions.class);
    }

    /**
     * @param annotationClass
     * @param annotationsClass
     */
    public ConnectionFactoryDefinitionProcessor(Class<ConnectionFactoryDefinition> annotationClass, Class<ConnectionFactoryDefinitions> annotationsClass) {

        super(ConnectionFactoryDefinition.class, ConnectionFactoryDefinitions.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#processXML()
     */
    @Override
    public void processXML() throws InjectionException {

        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "processXML : " + this);

        List<? extends ConnectionFactory> connectionFactoryDefinitions = ivNameSpaceConfig.getJNDIEnvironmentRefs(ConnectionFactory.class);

        if (connectionFactoryDefinitions != null)
        {
            for (ConnectionFactory connectionFactory : connectionFactoryDefinitions)
            {
                String jndiName = connectionFactory.getName();
                InjectionBinding<ConnectionFactoryDefinition> injectionBinding = ivAllAnnotationsCollection.get(jndiName);
                ConnectionFactoryDefinitionInjectionBinding binding;

                if (injectionBinding != null)
                {
                    binding = (ConnectionFactoryDefinitionInjectionBinding) injectionBinding;
                }
                else
                {
                    binding = new ConnectionFactoryDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
                    addInjectionBinding(binding);
                }

                binding.mergeXML(connectionFactory);
            }
        }

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "processXML : " + this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#createInjectionBinding(java.lang.annotation.Annotation, java.lang.Class, java.lang.reflect.Member, java.lang.String)
     */
    @Override
    public InjectionBinding<ConnectionFactoryDefinition> createInjectionBinding(ConnectionFactoryDefinition annotation, Class<?> instanceClass, Member member, String jndiName) throws InjectionException {
        InjectionBinding<ConnectionFactoryDefinition> injectionBinding =
                        new ConnectionFactoryDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
        injectionBinding.merge(annotation, instanceClass, null);
        return injectionBinding;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#resolve(com.ibm.wsspi.injectionengine.InjectionBinding)
     */
    @Override
    public void resolve(InjectionBinding<ConnectionFactoryDefinition> injectionBinding) throws InjectionException {

        ((ConnectionFactoryDefinitionInjectionBinding) injectionBinding).resolve();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#getJndiName(java.lang.annotation.Annotation)
     */
    @Override
    public String getJndiName(ConnectionFactoryDefinition annotation) {

        return annotation.name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#getAnnotations(java.lang.annotation.Annotation)
     */
    @Override
    public ConnectionFactoryDefinition[] getAnnotations(ConnectionFactoryDefinitions pluralAnnotation) {
        return pluralAnnotation.value();
    }

}
