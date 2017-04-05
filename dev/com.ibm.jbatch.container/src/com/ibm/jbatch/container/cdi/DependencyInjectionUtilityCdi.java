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

import com.ibm.jbatch.container.util.DependencyInjectionUtility;
import com.ibm.jbatch.jsl.model.Property;

/**
 * A a bridge for BatchProducerBean.  This class's package is exported 
 * from the com.ibm.jbatch.container bundle so that BatchProducerBean, 
 * in the com.ibm.ws.jbatch.cdi bundle, can invoke the DependencyInjectionUtility,
 * which itself is located in a non-exported package.
 */
public class DependencyInjectionUtilityCdi {

    public static String getPropertyValue(List<Property> propList, String batchPropName) {
        return DependencyInjectionUtility.getPropertyValue(propList, batchPropName);
    }

}
