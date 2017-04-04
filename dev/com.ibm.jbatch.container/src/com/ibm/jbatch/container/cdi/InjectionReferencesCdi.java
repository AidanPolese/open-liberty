/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.cdi;

import java.util.List;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;

import com.ibm.jbatch.container.artifact.proxy.InjectionReferences;
import com.ibm.jbatch.container.artifact.proxy.ProxyFactory;
import com.ibm.jbatch.jsl.model.Property;

/**
 * A a bridge for BatchProducerBean.  This class's package is exported 
 * from the com.ibm.jbatch.container bundle so that BatchProducerBean, 
 * in the com.ibm.ws.jbatch.cdi bundle, can invoke the InjectionReferences,
 * which itself is located in a non-exported package.
 */
public class InjectionReferencesCdi {

    private InjectionReferences injectionReferences;
    
    public InjectionReferencesCdi(InjectionReferences injectionReferences) {
        this.injectionReferences = injectionReferences;
    }
    
    public static InjectionReferences getInjectionReferences() {
       return ProxyFactory.getInjectionReferences();
    }

    public List<Property> getProps() {
        return injectionReferences.getProps();
    }

    public JobContext getJobContext() {
        return injectionReferences.getJobContext();
    }

    public StepContext getStepContext() {
        return injectionReferences.getStepContext();
    }

}
