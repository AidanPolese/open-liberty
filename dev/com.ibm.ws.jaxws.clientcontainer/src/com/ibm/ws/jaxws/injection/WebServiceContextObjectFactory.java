/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.injection;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 *
 */
public class WebServiceContextObjectFactory implements ObjectFactory {

    /** {@inheritDoc} */
    @Override
    public Object getObjectInstance(Object o, Name n, Context c, @Sensitive Hashtable<?, ?> envmt) throws Exception {
        return new WebServiceContextWrapper();
    }

}
