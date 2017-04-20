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
package com.ibm.ws.jaxws.web;

import org.apache.cxf.jaxws.JAXWSMethodInvoker;

/**
 *
 */
public class POJOJAXWSMethodInvoker extends JAXWSMethodInvoker {

    private final Object serviceObject;

    /**
     * @return the serviceObject
     */
    public Object getServiceObject() {
        return serviceObject;
    }

    /**
     * @param bean
     */
    public POJOJAXWSMethodInvoker(Object bean) {
        super(bean);
        this.serviceObject = bean;
    }
}
