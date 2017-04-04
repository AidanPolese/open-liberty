/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.processor;

import java.util.Collections;
import java.util.List;

import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;

import com.ibm.ws.javaee.dd.common.DataSource;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

public class DataSourceDefinitionProcessorProvider
                extends InjectionProcessorProvider<DataSourceDefinition, DataSourceDefinitions>
{
    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES =
                    Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(DataSource.class);

    @Override
    public Class<DataSourceDefinition> getAnnotationClass()
    {
        return DataSourceDefinition.class;
    }

    @Override
    public Class<DataSourceDefinitions> getAnnotationsClass()
    {
        return DataSourceDefinitions.class;
    }

    public List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses()
    {
        return REF_CLASSES;
    }

    @Override
    public InjectionProcessor<DataSourceDefinition, DataSourceDefinitions> createInjectionProcessor()
    {
        return new DataSourceDefinitionProcessor();
    }
}
