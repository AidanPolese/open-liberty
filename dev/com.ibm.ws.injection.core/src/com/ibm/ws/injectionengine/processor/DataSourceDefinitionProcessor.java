/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.processor;

import java.lang.reflect.Member;
import java.util.List;

import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ws.javaee.dd.common.DataSource;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionConfigConstants;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionProcessor;

public class DataSourceDefinitionProcessor
                extends InjectionProcessor<DataSourceDefinition, DataSourceDefinitions>
{
    private final static String CLASS_NAME = DataSourceDefinitionProcessor.class.getName();

    private final static TraceComponent tc = Tr.register(CLASS_NAME,
                                                         InjectionConfigConstants.traceString,
                                                         InjectionConfigConstants.messageFile);

    public DataSourceDefinitionProcessor()
    {
        super(DataSourceDefinition.class, DataSourceDefinitions.class);
    }

    @Override
    public void processXML()
                    throws InjectionException
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "processXML : " + this);

        List<? extends DataSource> dsds = ivNameSpaceConfig.getDataSourceDefinitions();

        if (dsds != null)
        {
            for (DataSource dsd : dsds)
            {
                String jndiName = dsd.getName();
                InjectionBinding<DataSourceDefinition> injectionBinding = ivAllAnnotationsCollection.get(jndiName);
                DataSourceDefinitionInjectionBinding binding;

                if (injectionBinding != null)
                {
                    binding = (DataSourceDefinitionInjectionBinding) injectionBinding;
                }
                else
                {
                    binding = new DataSourceDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
                    addInjectionBinding(binding);
                }

                binding.mergeXML(dsd);
            }
        }

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "processXML : " + this);
    }

    @Override
    public void resolve(InjectionBinding<DataSourceDefinition> binding)
                    throws InjectionException
    {
        ((DataSourceDefinitionInjectionBinding) binding).resolve();
    }

    @Override
    public InjectionBinding<DataSourceDefinition> createInjectionBinding(DataSourceDefinition annotation,
                                                                         Class<?> instanceClass,
                                                                         Member member,
                                                                         String jndiName) // F743-33811
    throws InjectionException
    {
        InjectionBinding<DataSourceDefinition> injectionBinding =
                        new DataSourceDefinitionInjectionBinding(jndiName, ivNameSpaceConfig);
        injectionBinding.merge(annotation, instanceClass, null);
        return injectionBinding;
    }

    @Override
    public String getJndiName(DataSourceDefinition annotation)
    {
        return annotation.name();
    }

    @Override
    public DataSourceDefinition[] getAnnotations(DataSourceDefinitions annotation)
    {
        return annotation.value();
    }
}
