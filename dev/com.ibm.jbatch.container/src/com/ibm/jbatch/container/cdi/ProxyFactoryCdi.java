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

import com.ibm.jbatch.container.artifact.proxy.ProxyFactory;

/**
 * A a bridge for BatchProducerBean.  This class's package is exported 
 * from the com.ibm.jbatch.container bundle so that BatchProducerBean, 
 * in the com.ibm.ws.jbatch.cdi bundle, can invoke the ProxyFactory,
 * which itself is located in a non-exported package.
 */
public class ProxyFactoryCdi {

    public static InjectionReferencesCdi getInjectionReferences() {
       return new InjectionReferencesCdi( ProxyFactory.getInjectionReferences() );
    }

}
