package com.ibm.ws.jaxws;

import com.ibm.wsspi.adaptable.module.Container;

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

/**
 *
 */
public interface ImplBeanCustomizer {
    <T> T onPrepareImplBean(Class<T> cls, Container container);

}
